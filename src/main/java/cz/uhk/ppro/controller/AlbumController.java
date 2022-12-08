package cz.uhk.ppro.controller;

import cz.uhk.ppro.album.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Paths;

@Controller
public class AlbumController {

    @Value("")
    private String uploadFolder;

    @Autowired
    private AlbumService albumService;

    private final Logger log = LoggerFactory.getLogger(this.getClass());


    @GetMapping("/nove-album")
    public String getNewAlbumView(Model model){
        model.addAttribute("album", new AlbumDto());
        return "/new-album";//stejný název jako v templates
    }

    @PostMapping("/addAlbum")
    public String addAlbum(AlbumDto albumDto, Model model, HttpServletRequest request){
        try {
            //String uploadDirectory = System.getProperty("user.dir") + uploadFolder;
            String uploadDirectory = request.getServletContext().getRealPath(uploadFolder);
            String fileName = albumDto.getImage().getOriginalFilename();
            String filePath = Paths.get(uploadDirectory, fileName).toString();
            log.info("FileName: " + albumDto.getImage().getOriginalFilename());
            if (fileName == null || fileName.contains("..")) {
                model.addAttribute("message", "Sorry! Filename contains invalid path sequence \" + fileName");
                return "/new-album";
            }
            String[] names = albumDto.getName().split(",");
            try {
                File dir = new File(uploadDirectory);
                if (!dir.exists()) {
                    log.info("Folder Created");
                    dir.mkdirs();
                }
                // Save the file locally

                BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(filePath)));
                stream.write(albumDto.getImage().getBytes());
                stream.close();
            } catch (Exception e) {
                log.info("in catch");
                e.printStackTrace();
            }
            byte[] imageData = albumDto.getImage().getBytes();
            Album album = new Album();
            album.setName(names[0]);
            album.setImage(imageData);
            album.setAuthor(albumDto.getAuthor());
            album.setPublisher(albumDto.getPublisher());
            album.setScale(albumDto.getScale());
            albumService.saveAlbum(album);
            log.info("HttpStatus===" + new ResponseEntity<>(HttpStatus.OK));
            model.addAttribute("album", albumDto);
            return "redirect:/";
        } catch (Exception e) {
            e.printStackTrace();
            log.info("Exception: " + e);
            return "/error";
        }
    }

    @GetMapping("/")
    public String showAllAlbums(Model model){
        model.addAttribute("albumList", albumService.getAllActiveAlbums());
        return "index";
    }
}
