package cz.uhk.ppro.album;

import cz.uhk.ppro.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(readOnly = true)
public interface AlbumRepository extends JpaRepository<Album, Long> {
    List<Album> getAlbumsByUser_Id(Long id);
}
