package cz.uhk.ppro.registration;

import cz.uhk.ppro.exception.UserAlreadyExistsException;
import cz.uhk.ppro.user.User;
import cz.uhk.ppro.user.UserDto;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Objects;

//@RestController
//@RequestMapping(path = "api/v1/registration")
@Controller
@AllArgsConstructor //generates a constructor with 1 parameter for each field in your class. Fields marked with @NonNull result in null checks on those parameters.
public class RegistrationController {

    private final RegistrationService registrationService;
/*
    @PostMapping
    public String register(@RequestBody RegistrationRequest request){
        return registrationService.register(request);
    }*/

    @GetMapping("/registrace")
    public String showRegistrationForm(Model model) {
        UserDto userDto = new UserDto();
        model.addAttribute("user", userDto);
        System.out.println("Provedl se požadavek GET.");
        return "/registration";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("user") @Valid UserDto userDto, BindingResult bindingResult, Model model){
    //public String register(UserDto userDto){
        if (bindingResult.hasErrors()){
            model.addAttribute("registrationForm", userDto);
            System.out.println("ošetření textových polí");
            return "/registration";
        }
        try {
            registrationService.register(userDto);
        }catch(UserAlreadyExistsException exception) {
                model.addAttribute("message", exception.getMessage());
            System.out.println(exception.getMessage());
            return "/registration";
        }
        registrationService.register(userDto);
        System.out.println("Provedl se požadavek POST.");
        return "redirect:/";
    }

    @GetMapping("/registrace/confirm")
    public String confirm(@RequestParam("token") String token) {
        registrationService.confirmToken(token);
        return "/login";
    }
}

