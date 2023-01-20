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
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Service
@AllArgsConstructor //generates a constructor with 1 parameter for each field in your class. Fields marked with @NonNull result in null checks on those parameters.
public class UserService implements UserDetailsService {

    private final static String USER_NOT_FOUND_MSG = "uživatel s uživatelským jménem %s nenalezen";
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ConfirmationTokenService confirmationTokenService;

    private final ConfirmationTokenRepository confirmationTokenRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(()->
                new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG, username)));
    }

    public String signUpUser(User user) throws UserAlreadyExistsException{
        boolean userByUsernameExists = userRepository
                .findByUsername(user.getUsername())
                .isPresent();
        boolean userByEmailExists = userRepository
                .findByEmail(user.getEmail())
                .isPresent();
        if(userByEmailExists){
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
        if(userByUsernameExists){
            throw new UserAlreadyExistsException("Uživatel s daným uživatelským jménem už existuje.");
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

    public void changePassword(Long id, String password){
        String encodedPassword = bCryptPasswordEncoder.encode(password);
        userRepository.findById(id)
                .map(user1 -> {
                    user1.setPassword(encodedPassword);
                    return userRepository.save(user1);
                });
    }
    //pro obnovu zapomenutého hesla:
    public void updateResetPasswordToken(String token, String email) {
        User user = userRepository.findByEmail(email).get();
        user.setResetPasswordToken(token);
        userRepository.save(user);

    }
    public User getByResetPasswordToken(String token) {
        return userRepository.findByResetPasswordToken(token).get();
    }
    public void updatePassword(User user, String newPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);

        user.setResetPasswordToken(null);
        userRepository.save(user);
    }
}
