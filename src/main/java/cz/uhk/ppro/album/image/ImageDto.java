package cz.uhk.ppro.album.image;

import cz.uhk.ppro.album.AlbumDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.ManyToOne;

@Setter
@Getter
@NoArgsConstructor
public class ImageDto {
    private MultipartFile image;
    private AlbumDto albumDto;

}
