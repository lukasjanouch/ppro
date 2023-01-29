package cz.uhk.ppro.controller;

import cz.uhk.ppro.album.*;
import cz.uhk.ppro.album.category.Category;
import cz.uhk.ppro.album.category.CategoryService;
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
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class AlbumController {
    private final UserService userService;

    private final CategoryService categoryService;

    private final AlbumService albumService;

    private final LikeService likeService;

    private final Logger log = LoggerFactory.getLogger(this.getClass());


    @GetMapping(path = {"/","/search-album", "/select-team"})//všechny albumy
    public String showAlbums(Model model, @Param("keyword") String keyword, String team) {
        List<Category> categories = categoryService.getAllCategories();
        List<Album> list;
        if(keyword == null && "".equals(team)) {
            list = albumService.getAllActiveAlbums();
        } else if (keyword != null) {
            list = albumService.getByKeyword(keyword);
        } else if (team != null) {
            Category category = categoryService.getCategoryByName(team).get();
            list = albumService.getByCategory(category);
        } else {
            list = albumService.getAllActiveAlbums();
        }
        model.addAttribute("albumList", list);
        model.addAttribute("keyword", keyword);
        model.addAttribute("team", team);
        model.addAttribute("categoryList", categories);
        System.out.println(albumService.getAlbumById(3L));
        return "/index";
    }

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
            String[] names = albumDto.getName().split(",");
            boolean categoryExists = false;
            if(!"".equals(albumDto.getCategoryDto().getName())) {
                List<Category> categories = categoryService.getAllCategories();
                for (Category category: categories){
                    if (category.getName().equals(albumDto.getCategoryDto().getName())) {
                        categoryExists = true;
                        break;
                    }
                }
                Category category;
                if (!categoryExists) {
                    category = new Category();
                    category.getAlbums().add(album);
                    category.setName(albumDto.getCategoryDto().getName());
                    categoryService.saveCategory(category);
                }else {
                    category = categoryService.getCategoryByName(albumDto.getCategoryDto().getName()).get();
                }
                album.setCategory(category);
            }else {
                model.addAttribute("album", albumDto);
                model.addAttribute("message", "Pro účel filtrování musí být vybrán tým.");
                return "/add-album";
            }
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
            model.addAttribute("albumList", albumService.getAlbumsByUserId(loggedUser.getId()));
        }
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
        Category category = albumService.getAlbumById(id).get().getCategory();
        Long categoryId = category.getId();
        albumService.deleteAlbumById(id);
        if (category.getAlbums().size() == 0){
            categoryService.deleteCategoryById(categoryId);
        }
        return "redirect:/moje-galerie";
    }

    @PostMapping("like-album/{id}")
    public String likeAlbum(@PathVariable Long id) {
        Album album = albumService.getAlbumById(id).get();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userName = ((UserDetails) auth.getPrincipal()).getUsername();
        User loggedUser = (User) userService.loadUserByUsername(userName);

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
        if (album.getLikeEntities().size() == 0 || !containsLike) {
            LikeEntity likeEntity = new LikeEntity();
            likeEntity.setUser(loggedUser);
            likeEntity.setAlbum(album);
            likeService.saveLike(likeEntity);
        }  else {
            likeService.deleteLike(like.getId());
        }
        return "redirect:/album/{id}";
    }
}
