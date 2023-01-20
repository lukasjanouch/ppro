package cz.uhk.ppro.album.image;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ImageService {
    private ImageRepository imageRepository;

    public void saveImage(Image image) {
        imageRepository.save(image);
    }

    public List<Image> getAllActiveImages() {
        return imageRepository.findAll();
    }

    public Optional<Image> getImageById(Long id) {
        return imageRepository.findById(id);
    }

    public void deleteImageById(Long id){
        imageRepository.deleteById(id);
    }
}
