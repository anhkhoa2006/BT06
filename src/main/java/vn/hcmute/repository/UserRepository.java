package vn.hcmute.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.hcmute.entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :kw, '%')) " +
           "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :kw, '%')) " +
           "OR LOWER(u.fullname) LIKE LOWER(CONCAT('%', :kw, '%')) " +
           "OR u.phone LIKE CONCAT('%', :kw, '%')")
    Page<User> searchUsers(@Param("kw") String keyword, Pageable pageable);
}
