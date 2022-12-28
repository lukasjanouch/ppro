package cz.uhk.ppro.controller;

import cz.uhk.ppro.album.*;
import cz.uhk.ppro.album.image.Image;
import cz.uhk.ppro.album.image.ImageDto;
import cz.uhk.ppro.album.image.ImageService;
import cz.uhk.ppro.user.User;
import cz.uhk.ppro.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class AlbumController {


    @Autowired
    private UserService userService;

    @Autowired
    private AlbumService albumService;

    private final Logger log = LoggerFactory.getLogger(this.getClass());



    @GetMapping("/nove-album")
    public String getNewAlbumView(Model model) {
        model.addAttribute("album", new AlbumDto());
        return "add-album";//stejný název jako v templates
    }

    @PostMapping("/addAlbum")
    public String addAlbum(AlbumDto albumDto, Model model, HttpServletRequest request) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String userName = ((UserDetails) auth.getPrincipal()).getUsername();
        //System.out.println("user.getEmail()");
        User user = (User) userService.loadUserByUsername(userName);

        try {
            Album album = new Album();
            List<Image> imageData = new ArrayList<>();
            //String uploadDirectory = System.getProperty("user.dir") + uploadFolder;
            /*String uploadDirectory = request.getServletContext().getRealPath(uploadFolder);
            for (int i = 0; i < albumDto.getImages().size(); i++) {
                //String fileName = albumDto.getImage().getOriginalFilename();
                String fileName = albumDto.getImages().get(i).getImage().getOriginalFilename();
                String filePath = Paths.get(uploadDirectory, fileName).toString();
                log.info("FileName: " + albumDto.getImages().get(i).getImage().getOriginalFilename());
                if (fileName == null || fileName.contains("..")) {
                    model.addAttribute("message", "Sorry! Filename contains invalid path sequence \" + fileName");
                    return "/new-album";
                }
                try {
                    File dir = new File(uploadDirectory);
                    if (!dir.exists()) {
                        log.info("Folder Created");
                        dir.mkdirs();
                    }
                    // Save the file locally
                    BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(filePath)));
                    System.out.println(filePath);
                    stream.write(albumDto.getImages().get(i).getImage().getBytes());
                    stream.close();
                } catch (Exception e) {
                    log.info("in catch");
                    e.printStackTrace();
                }
                Image image = new Image();
                image.setAlbum(album);
                image.setImage(albumDto.getImages().get(i).getImage().getBytes());
                imageData.add(image);
            }
            *///byte[] imageData = albumDto.getImage().getBytes();
            String[] names = albumDto.getName().split(",");

            album.setName(names[0]);
            //album.setImage(imageData);
            //album.setImages(imageData);
            album.setAuthor(albumDto.getAuthor());
            album.setPublisher(albumDto.getPublisher());
            album.setScale(albumDto.getScale());
            album.setLikes(0);
            user.getAlbums().add(album);

            album.setUser(user);
            albumService.saveAlbum(album);
            log.info("HttpStatus===" + new ResponseEntity<>(HttpStatus.OK));
            model.addAttribute("album", albumDto);
            //return "redirect:/";
            return "redirect:/moje-galerie";
        } catch (Exception e) {
            e.printStackTrace();
            log.info("Exception: " + e);
            return "/error";
        }
    }



    @GetMapping("moje-galerie")//albumy přihlášeného uživatele
    public String showUsersAlbums(Model model) {
        model.addAttribute("albumList", albumService.getAllLogedInUserAlbums());
        return "/my-gallery";
    }
    @GetMapping("/")//všechny albumy
    public String showAllAlbums(Model model) {
        model.addAttribute("albumList", albumService.getAllActiveAlbums());
        System.out.println(albumService.getAlbumById(3L));
        return "/index";
    }

    @GetMapping("/album/{id}")
    public String viewAlbumDetail(Model model, @PathVariable Long id){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userName = ((UserDetails) auth.getPrincipal()).getUsername();
        User user = (User) userService.loadUserByUsername(userName);
        Long loggedUsId = user.getId();
        model.addAttribute("album", albumService.getAlbumById(id).get());
        model.addAttribute("loggedUsId", loggedUsId);
        return "/album-detail";
    }
}
