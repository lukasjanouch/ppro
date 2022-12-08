package cz.uhk.ppro.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller//jako @Component; Dependency injection
@RequestMapping("/")
public class TemplateController {

    /*@GetMapping("api/v1/registration")
    public String getRegistrationView(){
        return "registration";//stejný název jako v templates
    }*/



    @GetMapping("login")
    public String getLoginView(){
        return "login";//stejný název jako v templates
    }

    @GetMapping("moje-galerie")
    public String getGalleryView(){
        return "my-gallery";//stejný název jako v templates
    }

    @GetMapping("muj-profil")
    public String getProfileView(){
        return "my-profile";//stejný název jako v templates
    }


}
