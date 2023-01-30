package cz.uhk.ppro.controller;

import cz.uhk.ppro.album.Album;
import cz.uhk.ppro.email.EmailSender;
import cz.uhk.ppro.exception.UserAlreadyExistsException;
import cz.uhk.ppro.registration.RegistrationService;
import cz.uhk.ppro.registration.token.ConfirmationToken;
import cz.uhk.ppro.registration.token.ConfirmationTokenService;
import cz.uhk.ppro.user.*;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class MyProfileController {

    private final UserService userService;

    private final UserRepository userRepository;

    private final ConfirmationTokenService confirmationTokenService;

    private final EmailSender emailSender;

    private final RegistrationService registrationService;

    @GetMapping("muj-profil")
    public String getProfileView(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userName = ((UserDetails) auth.getPrincipal()).getUsername();
        User loggedUser = (User) userService.loadUserByUsername(userName);

        model.addAttribute("loggedUser", loggedUser);
        int imgsCount = 0;
        for (Album album : loggedUser.getAlbums()) {
            imgsCount = imgsCount + album.getImages().size();
        }
        model.addAttribute("imgsCount", imgsCount);
        model.addAttribute("userPassword", new ChangePasswordDto());
        model.addAttribute("userUsername", new ChangeUsernameDto());
        model.addAttribute("userEmail", new ChangeEmailDto());
        return "my-profile";//stejný název jako v templates
    }

    @PutMapping("change-username/{id}")
    public String changeUsername(@PathVariable Long id, @ModelAttribute("userUsername") @Valid ChangeUsernameDto user,
                                 BindingResult bindingResult, Model model, RedirectAttributes redirAttrs
                                  ) throws Exception {
        User loggedUser = userRepository.findById(id).get();
        if (bindingResult.hasErrors()) {
            model.addAttribute("loggedUser", loggedUser);
            model.addAttribute("userPassword", new ChangePasswordDto());
            model.addAttribute("userEmail", new ChangeEmailDto());
            int imgsCount = 0;
            for (Album album : loggedUser.getAlbums()) {
                imgsCount = imgsCount + album.getImages().size();
            }
            model.addAttribute("imgsCount", imgsCount);
            return "my-profile";
        }
        try {
            boolean userByUsernameExists = userRepository
                    .findByUsername(user.getUsername())
                    .isPresent();
            if (userByUsernameExists) {
                throw new UserAlreadyExistsException("Uživatel s daným uživatelským jménem už existuje.");
            }
        } catch (UserAlreadyExistsException exception) {
            model.addAttribute("message", exception.getMessage());
            model.addAttribute("loggedUser", loggedUser);
            model.addAttribute("userPassword", new ChangePasswordDto());
            model.addAttribute("userUsername", new ChangeUsernameDto());
            model.addAttribute("userEmail", new ChangeEmailDto());
            int imgsCount = 0;
            for (Album album : loggedUser.getAlbums()) {
                imgsCount = imgsCount + album.getImages().size();
            }
            model.addAttribute("imgsCount", imgsCount);
            return "my-profile";
        }
        userRepository.findById(id)
                .map(user1 -> {
                    user1.setUsername(user.getUsername());
                    return userRepository.save(user1);
                });
        loggedUser.setUsername(user.getUsername());
        Authentication auth = new UsernamePasswordAuthenticationToken(loggedUser, null, loggedUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        redirAttrs.addFlashAttribute("usernameMess", "Uživatelské jméno bylo úspěšně změněno.");
        return "redirect:/muj-profil";
    }

    @PutMapping("change-email/{id}")
    public String changeEmail(@PathVariable Long id,
                              @ModelAttribute("userEmail") @Valid ChangeEmailDto user, BindingResult bindingResult,
                              RedirectAttributes redirAttrs, Model model ) {
        User loggedUser = userRepository.findById(id).get();
        if (bindingResult.hasErrors()) {
            model.addAttribute("loggedUser", loggedUser);
            model.addAttribute("userPassword", new ChangePasswordDto());
            model.addAttribute("userUsername", new ChangeUsernameDto());
            int imgsCount = 0;
            for (Album album : loggedUser.getAlbums()) {
                imgsCount = imgsCount + album.getImages().size();
            }
            model.addAttribute("imgsCount", imgsCount);
            return "my-profile";
        }
        try {
            boolean userByUsernameExists = userRepository
                    .findByEmail(user.getEmail())
                    .isPresent();
            if (userByUsernameExists) {
                throw new UserAlreadyExistsException("Uživatel s daným emailem už existuje.");
            }
        } catch (UserAlreadyExistsException exception) {
            model.addAttribute("message", exception.getMessage());
            model.addAttribute("loggedUser", loggedUser);
            model.addAttribute("userPassword", new ChangePasswordDto());
            model.addAttribute("userUsername", new ChangeUsernameDto());
            model.addAttribute("userEmail", new ChangeEmailDto());
            int imgsCount = 0;
            for (Album album : loggedUser.getAlbums()) {
                imgsCount = imgsCount + album.getImages().size();
            }
            model.addAttribute("imgsCount", imgsCount);
            return "my-profile";
        }
        loggedUser.setEmail(user.getEmail());
        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken(token,
                LocalDateTime.now(), LocalDateTime.now().plusMinutes(15), loggedUser);
        confirmationTokenService.saveConfirmationToken(confirmationToken);

        String link = "http://localhost:8080/muj-profil/confirm?token=" + token + "&email=" + user.getEmail();
        emailSender.send(
                user.getEmail(),
                registrationService.buildEmailToChangeIt(loggedUser.getUsername(), link));
        redirAttrs.addFlashAttribute("confirmMessage", "Potvrzovací email byl odeslán.");
        return "redirect:/muj-profil";
    }

    @GetMapping("/muj-profil/confirm")
    public String confirm(@RequestParam("token") String token, @RequestParam("email") String email,
                          RedirectAttributes redirAttrs, Model model) {
        ConfirmationToken confirmationToken = confirmationTokenService
                .getToken(token)
                .orElseThrow(() ->
                        new IllegalStateException("token not found"));

        if (confirmationToken.getConfirmedAt() != null) {
            throw new IllegalStateException("email already confirmed");
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("token expired");
        }

        confirmationTokenService.setConfirmedAt(token);

        userRepository.findById(confirmationToken.getUser().getId())
                .map(user1 -> {
                    user1.setEmail(email);
                    return userRepository.save(user1);
                });
        User loggedUser = confirmationToken.getUser();

        model.addAttribute("loggedUser", loggedUser);
        model.addAttribute("userPassword", new ChangePasswordDto());
        model.addAttribute("userUsername", new ChangeUsernameDto());
        model.addAttribute("userEmail", new ChangeEmailDto());
        int imgsCount = 0;
        for (Album album : loggedUser.getAlbums()) {
            imgsCount = imgsCount + album.getImages().size();
        }
        model.addAttribute("imgsCount", imgsCount);
        model.addAttribute("emailMess", "Email byl úspěšně změněn.");
        return "my-profile";
    }

    @PutMapping("change-password/{id}")
    public String changePassword(@PathVariable Long id, @ModelAttribute("userPassword") @Valid ChangePasswordDto user,
                                 BindingResult bindingResult, RedirectAttributes redirAttrs, Model model) {
        User loggedUser = userRepository.findById(id).get();
        if (bindingResult.hasErrors()) {
            model.addAttribute("loggedUser", loggedUser);
            model.addAttribute("userEmail", new ChangeEmailDto());
            model.addAttribute("userUsername", new ChangeUsernameDto());
            int imgsCount = 0;
            for (Album album : loggedUser.getAlbums()) {
                imgsCount = imgsCount + album.getImages().size();
            }
            model.addAttribute("imgsCount", imgsCount);
            return "my-profile";
        }
        userService.changePassword(id, user.getPassword());
        redirAttrs.addFlashAttribute("passwordMess", "Heslo bylo úspěšně změněno.");
        return "redirect:/muj-profil";
    }
}
