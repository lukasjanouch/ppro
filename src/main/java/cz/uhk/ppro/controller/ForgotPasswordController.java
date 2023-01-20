package cz.uhk.ppro.controller;

import cz.uhk.ppro.Utility;
import cz.uhk.ppro.album.Album;
import cz.uhk.ppro.email.EmailService;
import cz.uhk.ppro.user.*;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;

@Controller
public class ForgotPasswordController {
    @Autowired
    private EmailService emailService;

    @Autowired
    private UserService userService;

    @GetMapping("/forgot_password")
    public String showForgotPasswordForm(Model model) {
        model.addAttribute("email", new ChangeEmailDto());
        return "forgot_password";
    }

    @PostMapping("/forgot_password")
    public String processForgotPassword(HttpServletRequest request,
                                        @ModelAttribute("email") @Valid ChangeEmailDto emailDto,
                                        BindingResult bindingResult, RedirectAttributes redirAttrs,
                                        Model model) {

        if (bindingResult.hasErrors()) {
            return "/forgot_password";
        }
        String email = emailDto.getEmail();
        // request.getParameter("email");
        String token = RandomString.make(30);
        try {
            userService.updateResetPasswordToken(token, email);
        }catch (Exception e){
            model.addAttribute("message", "Neexistuje žádný uživatel se zadaným emailem.");
            return "/forgot_password";
        }
        String resetPasswordLink = Utility.getSiteURL(request) + "/reset_password?token=" + token;
        emailService.sendEmailToChangePassword(email, resetPasswordLink);
        redirAttrs.addFlashAttribute("message", "Byl vám zaslán email pro obnovu hesla.");
        return "redirect:/forgot_password";
    }


    @GetMapping("/reset_password")
    public String showResetPasswordForm(@Param(value = "token") String token, Model model) {
        User user = userService.getByResetPasswordToken(token);
        model.addAttribute("token", token);
        if (user == null) {
            model.addAttribute("message", "Token není validní.");
        }
        model.addAttribute("userPassword", new ChangePasswordDto());
        return "reset_password";
    }

    @PostMapping("/reset_password")
    public String processResetPassword(HttpServletRequest request, @ModelAttribute("userPassword") @Valid ChangePasswordDto passwordDto,
                                       BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "/reset_password";
        }
        String token = request.getParameter("token");
        String password = passwordDto.getPassword();

        User user = userService.getByResetPasswordToken(token);

        if (user == null) {
            model.addAttribute("title", "Obnova hesla");
            model.addAttribute("message", "Token není validní.");
            return "reset_password2";
        }
        userService.updatePassword(user, password);
        return "redirect:/reset_password2";
    }

    @GetMapping("/reset_password2")
    public String showResetPasswordForm2(Model model) {
        model.addAttribute("title", "Obnova hesla");
        model.addAttribute("message", "Své heslo jste úspěšně obnovil/a, můžete se přihlásit.");
        return "reset_password2";
    }
}
