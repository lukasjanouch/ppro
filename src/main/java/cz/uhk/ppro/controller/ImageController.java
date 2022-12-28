package cz.uhk.ppro.controller;

import cz.uhk.ppro.album.Album;
import cz.uhk.ppro.album.AlbumDto;
import cz.uhk.ppro.album.AlbumService;
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
public class ImageController {

    @Value("")
    private String uploadFolder;

    @Autowired
    private AlbumService albumService;

    @Autowired
    private ImageService imageService;

    private final Logger log = LoggerFactory.getLogger(this.getClass());




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
        return "add-images";
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
                return "add-album";
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
            if(albumDto.getImages().get(i).getImage().getBytes().length > 0) {
                imageData.add(image);
            }
        }
        for (Image image : imageData) {
            album.getImages().add(image);
        }
        System.out.println(album.getImages().size());

        for (int i = 0; i < album.getImages().size(); i++) {
            imageService.saveImage(album.getImages().get(i));
        }
        //System.out.println(album.getImages().get(1).getId());
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

}
