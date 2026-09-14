package vn.hcmute.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.hcmute.dao.IUserDao;
import vn.hcmute.dao.UserDao;
import vn.hcmute.entity.User;
import vn.hcmute.repository.UserRepository;

import java.util.List;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired(required = false)
    private UserRepository userRepository;

    private IUserDao userDao = new UserDao();

    public UserServiceImpl() {
    }

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void insert(User user) {
        if (userRepository != null) {
            userRepository.save(user);
            return;
        }
        userDao.insert(user);
    }

    @Override
    public void update(User user) {
        if (userRepository != null) {
            userRepository.save(user);
            return;
        }
        userDao.update(user);
    }

    @Override
    public void delete(int id) throws Exception {
        if (userRepository != null) {
            userRepository.deleteById(id);
            return;
        }
        userDao.delete(id);
    }

    @Override
    public User findById(int id) {
        if (userRepository != null) {
            return userRepository.findById(id).orElse(null);
        }
        return userDao.findById(id);
    }

    @Override
    public User findByUsername(String username) {
        if (userRepository != null) {
            return userRepository.findByUsername(username).orElse(null);
        }
        return userDao.findByUsername(username);
    }

    @Override
    public User findByEmail(String email) {
        if (userRepository != null) {
            return userRepository.findByEmail(email).orElse(null);
        }
        return userDao.findByEmail(email);
    }

    @Override
    public void updatePassword(String email, String newPassword) {
        if (userRepository != null) {
            User u = userRepository.findByEmail(email).orElse(null);
            if (u != null) {
                u.setPassword(newPassword);
                userRepository.save(u);
            }
            return;
        }
        userDao.updatePassword(email, newPassword);
    }

    @Override
    public List<User> findAll() {
        if (userRepository != null) {
            return userRepository.findAll();
        }
        return userDao.findAll();
    }

    @Override
    public List<User> findAll(int page, int pagesize) {
        if (userRepository != null) {
            return userRepository.findAll(PageRequest.of(page, pagesize)).getContent();
        }
        return userDao.findAll(page, pagesize);
    }

    @Override
    public List<User> search(String keyword, int page, int pagesize) {
        if (userRepository != null) {
            return userRepository.searchUsers(keyword, PageRequest.of(page, pagesize)).getContent();
        }
        return userDao.search(keyword, page, pagesize);
    }

    @Override
    public int count() {
        if (userRepository != null) {
            return (int) userRepository.count();
        }
        return userDao.count();
    }

    @Override
    public int countByKeyword(String keyword) {
        if (userRepository != null) {
            return (int) userRepository.searchUsers(keyword, Pageable.unpaged()).getTotalElements();
        }
        return userDao.countByKeyword(keyword);
    }

    @Override
    public boolean checkExistUsername(String username) {
        if (userRepository != null) {
            return userRepository.existsByUsername(username);
        }
        return userDao.findByUsername(username) != null;
    }

    @Override
    public boolean checkExistEmail(String email) {
        if (userRepository != null) {
            return userRepository.existsByEmail(email);
        }
        return userDao.findByEmail(email) != null;
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        if (userRepository != null) {
            return userRepository.findAll(pageable);
        }
        int page = pageable.getPageNumber();
        int size = pageable.getPageSize();
        List<User> list = userDao.findAll(page, size);
        return new org.springframework.data.domain.PageImpl<>(list, pageable, userDao.count());
    }

    @Override
    public Page<User> search(String keyword, Pageable pageable) {
        if (userRepository != null) {
            return userRepository.searchUsers(keyword, pageable);
        }
        int page = pageable.getPageNumber();
        int size = pageable.getPageSize();
        List<User> list = userDao.search(keyword, page, size);
        return new org.springframework.data.domain.PageImpl<>(list, pageable, userDao.countByKeyword(keyword));
    }

    @Override
    public User save(User user) {
        if (userRepository != null) {
            return userRepository.save(user);
        }
        if (user.getId() > 0) {
            userDao.update(user);
        } else {
            userDao.insert(user);
        }
        return user;
    }
}
