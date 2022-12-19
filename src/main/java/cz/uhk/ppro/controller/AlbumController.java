package cz.uhk.ppro.controller;

import cz.uhk.ppro.album.*;
import cz.uhk.ppro.user.User;
import cz.uhk.ppro.user.UserService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @Value("")
    private String uploadFolder;

    private UserService userService;

    @Autowired
    private AlbumService albumService;

    @Autowired
    private ImageService imageService;

    private final Logger log = LoggerFactory.getLogger(this.getClass());



    @GetMapping("/nove-album")
    public String getNewAlbumView(Model model) {
        model.addAttribute("album", new AlbumDto());
        return "/new-album";//stejný název jako v templates
    }

    @PostMapping("/addAlbum")
    public String addAlbum(AlbumDto albumDto, Model model, HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userName = auth.getPrincipal().toString();
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
            //album.setUser(user);
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
    @GetMapping("pridat-obrazky/{id}")
    String getAddImagesView(@PathVariable Long id, Model model){
        Optional<Album> album = albumService.getAlbumById(id);
        AlbumDto albumDto = new AlbumDto();
        albumDto.setId(album.get().getId());
        albumDto.setAuthor(album.get().getAuthor());
        albumDto.setName(album.get().getName());
        albumDto.setPublisher(album.get().getPublisher());
        albumDto.setScale(album.get().getScale());
        for(int i = 0; i < 3; i++){
            ImageDto image = new ImageDto();
            albumDto.getImages().add(image);
        }
        System.out.println(albumDto.getImages().size());
        model.addAttribute("album", albumDto);
        return "/addImages";
    }

    @PostMapping("addImages/{id}")//{id}
    String addImages(@PathVariable Long id,
                     @ModelAttribute AlbumDto albumDto, Model model, HttpServletRequest request) throws IOException {
        Optional<Album> albumO = albumService.getAlbumById(id);
        Album album = albumO.get();
        List<Image> imageData = new ArrayList<>();
        String uploadDirectory = request.getServletContext().getRealPath(uploadFolder);
        for(int i = 0; i < albumDto.getImages().size(); i++){
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
        album.setImages(imageData);
        System.out.println(album.getImages().size());

        for (int i = 0; i < album.getImages().size(); i++) {
            imageService.saveImage(album.getImages().get(i));
        }
        System.out.println(album.getImages().get(1).getId());
        //albumService.saveAlbum(album);
        model.addAttribute("album", albumDto);
        return "redirect:/moje-galerie";
    }

    @GetMapping("/image/display/{id}")
    @ResponseBody
    void showImage(@PathVariable("id") Long id, HttpServletResponse response, Optional<Image> image)//Optional<Album> album
            throws ServletException, IOException {
        log.info("Id :: " + id);
        /*album = albumService.getAlbumById(id);
        response.setContentType("image/jpeg; image/jpg; image/png; image/gif");
        for(int i = 0; i < album.get().getImages().size(); i++) {
            response.getOutputStream().write(album.get().getImages().get(i).getImage());
        }
        response.getOutputStream().close();*/
        image = imageService.getImageById(id);
        if(image.isPresent()) {
            response.setContentType("image/jpeg; image/jpg; image/png; image/gif");
            response.getOutputStream().write(image.get().getImage());
            response.getOutputStream().close();
        }
    }

    @GetMapping("moje-galerie")//albumy přihlášeného uživatele
    public String showUsersAlbums(Model model) {
        model.addAttribute("albumList", albumService.getAllActiveAlbums());
        return "/my-gallery";
    }
    @GetMapping("/")//všechny albumy
    public String showAllAlbums(Model model) {
        model.addAttribute("albumList", albumService.getAllActiveAlbums());
        System.out.println(albumService.getAlbumById(3L));
        return "/index";
    }
}
