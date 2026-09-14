package vn.hcmute.service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.hcmute.entity.User;

public interface IUserService {
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
    boolean checkExistUsername(String username);
    boolean checkExistEmail(String email);

    // Spring Data methods
    Page<User> findAll(Pageable pageable);
    Page<User> search(String keyword, Pageable pageable);
    User save(User user);
}
