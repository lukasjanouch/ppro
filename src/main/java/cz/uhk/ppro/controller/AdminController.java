package cz.uhk.ppro.controller;

import cz.uhk.ppro.album.image.Image;
import cz.uhk.ppro.registration.token.ConfirmationTokenService;
import cz.uhk.ppro.user.User;
import cz.uhk.ppro.user.UserRepository;
import cz.uhk.ppro.user.UserRole;
import cz.uhk.ppro.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class AdminController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConfirmationTokenService confirmationTokenService;

    @GetMapping("/uzivatele")
    String getUsersView(Model model){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userName = ((UserDetails) auth.getPrincipal()).getUsername();

        List<User> users = userRepository.findAllByOrderByUsername();
        for (int i = 0; i < users.size(); i++){
            if (userName.equals(users.get(i).getUsername()) ){
                users.remove(i);
            }
        }
        model.addAttribute("usersList", users);
        return "delete-user";
    }
    @GetMapping("/delete-user-by-id")
    public String deleteUser(@RequestParam Long id){
        confirmationTokenService.deleteConfirmationToken(id);
        userRepository.deleteById(id);
        return "redirect:/uzivatele";
    }
}
