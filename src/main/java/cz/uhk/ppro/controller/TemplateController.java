package cz.uhk.ppro.controller;

import cz.uhk.ppro.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller//jako @Component; Dependency injection
@RequiredArgsConstructor
//@RequestMapping("/")
public class TemplateController {

    final UserService userService;

    @GetMapping("login")
    public String getLoginView(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "login";//stejný název jako v templates
        }
        return "redirect:";
    }

}
