package cz.uhk.ppro.album;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.ElementCollection;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class AlbumDto {
    private Long id;
    @NotNull
    @NotEmpty(message = "Toto pole musí být vyplněno.")
    private String name;
    private String publisher;
    private String author;
    private String scale;

    private MultipartFile image;
}
