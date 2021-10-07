package cz.uhk.ppro.user;

import cz.uhk.ppro.registration.token.ConfirmationToken;
import cz.uhk.ppro.registration.token.ConfirmationTokenService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor //generates a constructor with 1 parameter for each field in your class. Fields marked with @NonNull result in null checks on those parameters.
public class UserService implements UserDetailsService {

    private final static String USER_NOT_FOUND_MSG = "uživatel s emailem %s nenalezen";
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ConfirmationTokenService confirmationTokenService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email).orElseThrow(()->
                new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG, email)));
    }

    public String signUpUser(User user){
        boolean userExists = userRepository.findByEmail(user.getEmail()).isPresent();
        if(userExists){
            //TODO: check of attributes are the same and, jestli je to stejný uživatel
            //TODO: if email not confirmed send confirmation email again, jinak:
            throw new IllegalStateException("uživatel s daným emailem už existuje");
        }
        String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        userRepository.save(user);

        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken(token,
                LocalDateTime.now(), LocalDateTime.now().plusMinutes(15), user);
        confirmationTokenService.saveConfirmationToken(confirmationToken);

        //TODO: send email

        return token;
    }
    public int enableUser(String email) {
        return userRepository.enableUser(email);
    }
}
