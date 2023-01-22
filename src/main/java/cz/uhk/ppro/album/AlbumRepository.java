package cz.uhk.ppro.album;

import cz.uhk.ppro.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(readOnly = true)
public interface AlbumRepository extends JpaRepository<Album, Long> {
    List<Album> findAlbumsByOrderByName();

    List<Album> findAlbumsByUser_IdOrderByName(Long id);

    @Query(value = "select * from album a where a.name like %:keyword%", nativeQuery = true)
    List<Album> findByKeyword(@Param("keyword") String keyword);

    void deleteAlbumById(Long id);
}
