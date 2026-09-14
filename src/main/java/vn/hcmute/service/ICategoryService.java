package vn.hcmute.service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.hcmute.entity.Category;

public interface ICategoryService {
    void insert(Category category);
    int count();
    List<Category> findAll(int page, int pagesize);
    List<Category> searchByName(String catname);
    List<Category> searchByName(String catname, int page, int pagesize);
    int countByKeyword(String catname);
    List<Category> findAll();
    Category findById(int cateid);
    void delete(int cateid) throws Exception;
    void update(Category category);
    Category findByCategoryname(String name);

    // Spring Data methods
    Page<Category> findAll(Pageable pageable);
    Page<Category> searchByName(String catname, Pageable pageable);
    Category save(Category category);
}