package vn.hcmute.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.hcmute.entity.Category;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    Page<Category> findByCategorynameContainingIgnoreCase(String name, Pageable pageable);
    Optional<Category> findByCategoryname(String name);
}
