package cz.uhk.ppro.album.like;

import cz.uhk.ppro.album.Album;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class LikeService {

    private LikeRepository likeRepository;
    public void saveLike(LikeEntity likeEntity) {
        likeRepository.save(likeEntity);
    }
    public Optional<List<LikeEntity>> getAllAlbumsLikes(Album album) {
        return likeRepository.getAllByAlbum(album);
    }
    public void deleteLike(Long id){likeRepository.deleteById(id);}
}
