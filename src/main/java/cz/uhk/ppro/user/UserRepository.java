package cz.uhk.ppro.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional(readOnly = true)
public interface UserRepository
        extends JpaRepository<User, Long> {//typ id usera

    Optional<User> findByEmail(String email);//není potřeba JPQL (Java Persistence...) dotaz

    @Transactional
    @Modifying
    @Query("UPDATE User a " +
            "SET a.enabled = TRUE WHERE a.email = ?1")
    void enableUser(String email);//bylo int

    Optional<User> getUserByEmail(String email);


}
