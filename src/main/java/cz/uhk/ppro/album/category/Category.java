package cz.uhk.ppro.album.category;

import cz.uhk.ppro.album.Album;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor//will generate a constructor with no parameters
@Entity
public class Category {
    @SequenceGenerator(
            name = "category_sequence",
            sequenceName = "category_sequence",
            allocationSize = 1
    )
    @Id //z javax.persistence
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "category_sequence"
    )
    //atributy, které není potřeba ukládat do db označujeme @Transient
    //např. věk, protože lze vypočítat z data narození
    private Long id;

    private String name;
    @OneToMany(mappedBy = "category", cascade = CascadeType.REMOVE)
    private List<Album> albums = new ArrayList<>();
}
