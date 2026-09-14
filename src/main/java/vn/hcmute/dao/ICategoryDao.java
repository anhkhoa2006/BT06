package vn.hcmute.dao;

import java.util.List;
import vn.hcmute.entity.Category;

public interface ICategoryDao {
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
    Category findByCategoryname(String name) throws Exception;
}