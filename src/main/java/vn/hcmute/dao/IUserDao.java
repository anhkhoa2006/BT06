package vn.hcmute.dao;

import java.util.List;
import vn.hcmute.entity.User;

public interface IUserDao {
    void insert(User user);
    void update(User user);
    void delete(int id) throws Exception;
    User findById(int id);
    User findByUsername(String username);
    User findByEmail(String email);
    void updatePassword(String email, String newPassword);
    List<User> findAll();
    List<User> findAll(int page, int pagesize);
    List<User> search(String keyword, int page, int pagesize);
    int count();
    int countByKeyword(String keyword);
}
