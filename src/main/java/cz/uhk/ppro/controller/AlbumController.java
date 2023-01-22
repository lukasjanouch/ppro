package cz.uhk.ppro.controller;

import cz.uhk.ppro.album.*;
import cz.uhk.ppro.album.comment.Comment;
import cz.uhk.ppro.album.comment.CommentDto;
import cz.uhk.ppro.album.comment.CommentRepository;
import cz.uhk.ppro.album.comment.CommentService;
import cz.uhk.ppro.album.image.Image;
import cz.uhk.ppro.album.like.LikeEntity;
import cz.uhk.ppro.album.like.LikeService;
import cz.uhk.ppro.user.User;
import cz.uhk.ppro.user.UserRepository;
import cz.uhk.ppro.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Controller
public class AlbumController {


    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AlbumService albumService;


    @Autowired
    private LikeService likeService;

    private final Logger log = LoggerFactory.getLogger(this.getClass());


    @GetMapping(path = {"/","/search-album", "/select-team"})//všechny albumy
    public String showAlbums(Model model, @Param("keyword") String keyword, String team) {
        List<Album> list;
        if(keyword == null && "".equals(team)) {
            list = albumService.getAllActiveAlbums();
        } else if (keyword != null) {
            list = albumService.getByKeyword(keyword);
        } else if (team != null) {
            list = albumService.getByKeyword(team);
        } else {
            list = albumService.getAllActiveAlbums();
        }
        model.addAttribute("albumList", list);
        model.addAttribute("keyword", keyword);
        model.addAttribute("team", team);
        System.out.println(albumService.getAlbumById(3L));
        return "/index";
    }
/*
    @PostMapping("/select-team")
    public String showAlbumsByTeam(String team, Model model){
        List<Album> list;
        if(!"VsechnyTymy".equals(team)) {
            list = albumService.getByKeyword(team);
            for (Album album: list) {
                System.out.println(album.getName());
            }
            System.out.println(team);
        }else {
            list = albumService.getAllActiveAlbums();
        }
        model.addAttribute("albumList", list);
        model.addAttribute("keyword", "");
        model.addAttribute("team", team);
        return "/index";
    }
*/
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
            album.setComments(new ArrayList<>());
            album.setLikeEntities(new ArrayList<>());
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
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.isAuthenticated()) {
            //String userName = ((UserDetails) auth.getPrincipal()).getUsername();
            String userName = auth.getName();
            User loggedUser = (User) userService.loadUserByUsername(userName);
            model.addAttribute("loggedUser", loggedUser);
        }
        model.addAttribute("albumList", albumService.getAllLogedInUserAlbums());
        return "/my-gallery";
    }



    @GetMapping("/album/{id}")
    public String viewAlbumDetail(Model model, @PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userName = auth.getName();
        User loggedUser = (User) userService.loadUserByUsername(userName);
        Album album = albumService.getAlbumById(id).get();
        boolean containsLike = false;
        for (LikeEntity like1 : album.getLikeEntities()) {
            if (Objects.equals(like1.getUser().getId(), loggedUser.getId())) {
                containsLike = true;
                break;
            }
        }
        model.addAttribute("album", album);
        model.addAttribute("loggedUser", loggedUser);
        model.addAttribute("containsLike", containsLike);

        CommentDto commentDto = new CommentDto();
        model.addAttribute("comment", commentDto);
        return "/album-detail";
    }

    @GetMapping("/album-nahled/{id}")
    public String viewAlbumFromIndex(Model model, @PathVariable Long id) {

        Album album = albumService.getAlbumById(id).get();
        model.addAttribute("album", album);
        CommentDto commentDto = new CommentDto();
        model.addAttribute("comment", commentDto);
        return "/album-from-index";
    }



    @GetMapping("/delete-album")
    public String deleteAlbum(@RequestParam Long id) {
        albumService.deleteAlbumById(id);
        return "redirect:/moje-galerie";
    }

    @PostMapping("like-album/{id}")
    public String likeAlbum(@PathVariable Long id, Model model) {

        Album album = albumService.getAlbumById(id).get();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userName = ((UserDetails) auth.getPrincipal()).getUsername();
        User loggedUser = (User) userService.loadUserByUsername(userName);
        LikeEntity likeEntity = new LikeEntity();
        likeEntity.setUser(loggedUser);
        likeEntity.setAlbum(album);
        List<LikeEntity> likes = new ArrayList<>();
        if (likeService.getAllAlbumsLikes(album).isPresent()) {
            likes = likeService.getAllAlbumsLikes(album).get();
        }
        boolean containsLike = false;
        LikeEntity like = new LikeEntity();
        for (LikeEntity like1 : likes) {
            if (Objects.equals(like1.getUser().getId(), loggedUser.getId())) {
                containsLike = true;
                like = like1;
                break;
            }
        }
        if (album.getLikeEntities().size() == 0) {
            likeService.saveLike(likeEntity);
        } else if (!containsLike) {
            likeService.saveLike(likeEntity);
        } else {
            likeService.deleteLike(like.getId());
        }
        return "redirect:/album/{id}";
    }
}
