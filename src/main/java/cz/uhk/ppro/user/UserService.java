package cz.uhk.ppro.user;

import cz.uhk.ppro.exception.UserAlreadyExistsException;
import cz.uhk.ppro.registration.token.ConfirmationToken;
import cz.uhk.ppro.registration.token.ConfirmationTokenRepository;
import cz.uhk.ppro.registration.token.ConfirmationTokenService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Service
@AllArgsConstructor //generates a constructor with 1 parameter for each field in your class. Fields marked with @NonNull result in null checks on those parameters.
public class UserService implements UserDetailsService {

    private final static String USER_NOT_FOUND_MSG = "uživatel s emailem %s nenalezen";
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ConfirmationTokenService confirmationTokenService;

    private final ConfirmationTokenRepository confirmationTokenRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email).orElseThrow(()->
                new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG, email)));
    }

    public String signUpUser(User user) throws UserAlreadyExistsException{
        boolean userExists = userRepository
                .findByEmail(user.getEmail())
                .isPresent();

        if(userExists){
            //check of attributes are the same and, jestli je to stejný uživatel
            //if email not confirmed send confirmation email again, jinak vyjímka
            /*if(!user.getEnabled()){
                confirmationTokenRepository.deleteConfirmationTokenByUser(user);
                userRepository.delete(user);

                String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());
                user.setPassword(encodedPassword);
                userRepository.save(user);
                String token = UUID.randomUUID().toString();
                ConfirmationToken confirmationToken = new ConfirmationToken(token,
                        LocalDateTime.now(), LocalDateTime.now().plusMinutes(15), user);
                confirmationTokenService.saveConfirmationToken(confirmationToken);

                return token;
            }*/
            throw new UserAlreadyExistsException("Uživatel s daným emailem už existuje.");
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
    public void enableUser(String email) {
        userRepository.enableUser(email);
    }//bylo int
}
