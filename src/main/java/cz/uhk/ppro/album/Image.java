package cz.uhk.ppro.album;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
@Getter
@Setter
@NoArgsConstructor//will generate a constructor with no parameters
@Entity
public class Image {
    @SequenceGenerator(
            name = "image_sequence",
            sequenceName = "image_sequence",
            allocationSize = 1
    )
    @Id //z javax.persistence
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "image_sequence"
    )
    //atributy, které není potřeba ukládat do db označujeme @Transient
    //např. věk, protože lze vypočítat z data narození
    private Long id;

    @Column(length = Integer.MAX_VALUE)
    @Lob
    private byte[] image;
    @ManyToOne
    private Album album;
}
