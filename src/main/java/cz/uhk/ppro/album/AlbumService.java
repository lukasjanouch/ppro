package cz.uhk.ppro.album;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AlbumService {
    private AlbumRepository albumRepository;

    public void saveAlbum(Album album) {
        albumRepository.save(album);
    }

    public List<Album> getAllActiveAlbums() {
        return albumRepository.findAll();
    }

    public Optional<Album> getImageById(Long id) {
        return albumRepository.findById(id);
    }
}
