package cz.uhk.ppro.album;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.Mapping;

import javax.persistence.*;
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
    private String name;
    private String publisher;
    private String author;
    private String scale;
    //@ElementCollection//defines a mapping for a non-Entity class
    @OneToMany(mappedBy = "album")
    private List<Image> images;
    //private byte[] image;


}

