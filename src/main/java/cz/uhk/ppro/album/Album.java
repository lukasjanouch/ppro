package cz.uhk.ppro.album;

import cz.uhk.ppro.album.comment.Comment;
import cz.uhk.ppro.album.image.Image;
import cz.uhk.ppro.album.like.LikeEntity;
import cz.uhk.ppro.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor//will generate a constructor with no parameters
@Entity
public class Album {
    @SequenceGenerator(
            name = "album_sequence",
            sequenceName = "album_sequence",
            allocationSize = 1
    )
    @Id //z javax.persistence
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "album_sequence"
    )
    //atributy, které není potřeba ukládat do db označujeme @Transient
    //např. věk, protože lze vypočítat z data narození
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @NotNull
    @NotEmpty(message = "Toto pole musí být vyplněno.")
    private String name;
    private String publisher;
    private String author;
    private String scale;
    //@ElementCollection//defines a mapping for a non-Entity class
    @OneToMany(mappedBy = "album", cascade = CascadeType.REMOVE)
    private List<Image> images;
    //private byte[] image;
    @OneToMany(mappedBy = "album", cascade = CascadeType.REMOVE)
    private List<Comment> comments;
    @OneToMany(mappedBy = "album", cascade = CascadeType.REMOVE)
    private List<LikeEntity> likeEntities;


}

