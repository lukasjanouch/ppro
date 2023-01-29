package cz.uhk.ppro.album;

import cz.uhk.ppro.album.category.Category;
import cz.uhk.ppro.user.User;
import cz.uhk.ppro.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AlbumService {
    private AlbumRepository albumRepository;
    private UserService userService;

    public void saveAlbum(Album album) {
        albumRepository.save(album);
    }

    public List<Album> getAllActiveAlbums() {
        return albumRepository.findAlbumsByOrderByName();
    }

    public List<Album> getAlbumsByUserId(Long id) {
        return albumRepository.findAlbumsByUser_IdOrderByName(id);
    }
    public Optional<Album> getAlbumById(Long id) {
        return albumRepository.findById(id);
    }

    public void deleteAlbumById(Long id){
        albumRepository.deleteAlbumById(id);
    }

    public List<Album> getByKeyword(String keyword){
        return albumRepository.findByKeyword(keyword);
    }

    public List<Album> getByCategory(Category category){return albumRepository.findByCategory(category);}
}
