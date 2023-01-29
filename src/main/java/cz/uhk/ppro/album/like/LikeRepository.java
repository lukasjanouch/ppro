package cz.uhk.ppro.album.like;

import cz.uhk.ppro.album.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface LikeRepository extends JpaRepository<LikeEntity, Long> {
    @Override
    void deleteById(Long id);

    Optional<List<LikeEntity>> getAllByAlbum(Album album);

}
