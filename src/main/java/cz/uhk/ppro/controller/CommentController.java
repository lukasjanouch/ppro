package cz.uhk.ppro.controller;

import cz.uhk.ppro.album.Album;
import cz.uhk.ppro.album.AlbumRepository;
import cz.uhk.ppro.album.comment.Comment;
import cz.uhk.ppro.album.comment.CommentDto;
import cz.uhk.ppro.album.comment.CommentRepository;
import cz.uhk.ppro.album.comment.CommentService;
import cz.uhk.ppro.user.User;
import cz.uhk.ppro.user.UserRepository;
import cz.uhk.ppro.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
public class CommentController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CommentService commentService;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private AlbumRepository albumRepository;

    @PostMapping("/comment-album/{id}")
    public String addComment(@PathVariable Long id, @ModelAttribute CommentDto commentDto, @ModelAttribute Album album,
                             Model model) {
        Comment comment = new Comment();
        comment.setAlbum(album);
        comment.setText(commentDto.getText());
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userName = ((UserDetails) auth.getPrincipal()).getUsername();
        User loggedUser = (User) userService.loadUserByUsername(userName);
        comment.setUser(loggedUser);
        userRepository.findById(loggedUser.getId()).map(
                user -> {
                    user.getComments().add(comment);
                    return userRepository.save(user);
                }
        );
        albumRepository.findById(id).map(
                album1 -> {
                    if (album1.getComments() == null) {
                        List<Comment> comments = new ArrayList<>();
                        comments.add(comment);
                        album1.setComments(comments);
                    } else {
                        album1.getComments().add(comment);
                    }
                    return albumRepository.save(album1);
                }
        );
        commentService.saveComment(comment);
        return "redirect:/album/{id}";
    }
    @GetMapping("/delete-comment")
    public String deleteAlbum(@RequestParam Long id) {
        Comment comment = commentService.getCommentById(id).get();
        Long albumId = comment.getAlbum().getId();
        commentService.deleteComment(id);
        return "redirect:/album/" + albumId;
    }
}
