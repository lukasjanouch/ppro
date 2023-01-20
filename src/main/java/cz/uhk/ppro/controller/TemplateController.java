package cz.uhk.ppro.controller;

import cz.uhk.ppro.user.User;
import cz.uhk.ppro.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller//jako @Component; Dependency injection
@RequestMapping("/")
public class TemplateController {
    @Autowired
    UserService userService;

    @GetMapping("login")
    public String getLoginView(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "login";//stejný název jako v templates
        }
        return "redirect:/";
    }



    /*@GetMapping("api/v1/registration")
    public String getRegistrationView(){
        return "registration";//stejný název jako v templates
    }*/
    /*@GetMapping("moje-galerie")
    public String getGalleryView(){
        return "my-gallery";//stejný název jako v templates
    }*/
}
