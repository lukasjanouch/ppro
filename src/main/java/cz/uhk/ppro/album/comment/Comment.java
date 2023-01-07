package cz.uhk.ppro.album.comment;

import cz.uhk.ppro.album.Album;
import cz.uhk.ppro.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor//will generate a constructor with no parameters
@Entity
public class Comment {
    @SequenceGenerator(
            name = "comment_sequence",
            sequenceName = "comment_sequence",
            allocationSize = 1
    )
    @Id //z javax.persistence
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "comment_sequence")
    private Long id;
    @ManyToOne
    private User user;
    @ManyToOne
    private Album album;
    private String text;
}
