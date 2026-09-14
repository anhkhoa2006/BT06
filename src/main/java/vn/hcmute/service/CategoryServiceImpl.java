package vn.hcmute.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.hcmute.dao.CategoryDao;
import vn.hcmute.dao.ICategoryDao;
import vn.hcmute.entity.Category;
import vn.hcmute.repository.CategoryRepository;

import java.util.List;

@Service
public class CategoryServiceImpl implements ICategoryService {

    @Autowired(required = false)
    private CategoryRepository categoryRepository;

    public ICategoryDao cateDao = new CategoryDao();

    public CategoryServiceImpl() {
    }

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Category> findAll() {
        if (categoryRepository != null) {
            return categoryRepository.findAll();
        }
        return cateDao.findAll();
    }

    @Override
    public Category findById(int id) {
        if (categoryRepository != null) {
            return categoryRepository.findById(id).orElse(null);
        }
        return cateDao.findById(id);
    }

    @Override
    public List<Category> searchByName(String keyword) {
        if (categoryRepository != null) {
            return categoryRepository.findByCategorynameContainingIgnoreCase(keyword, Pageable.unpaged()).getContent();
        }
        return cateDao.searchByName(keyword);
    }

    @Override
    public void insert(Category category) {
        if (categoryRepository != null) {
            categoryRepository.save(category);
            return;
        }
        Category cate = this.findByCategoryname(category.getCategoryname());
        if (cate == null) {
            cateDao.insert(category);
        }
    }

    @Override
    public void update(Category category) {
        if (categoryRepository != null) {
            categoryRepository.save(category);
            return;
        }
        Category cate = this.findById(category.getCategoryid());
        if (cate != null) {
            cateDao.update(category);
        }
    }

    @Override
    public void delete(int id) {
        try {
            if (categoryRepository != null) {
                categoryRepository.deleteById(id);
                return;
            }
            cateDao.delete(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int count() {
        if (categoryRepository != null) {
            return (int) categoryRepository.count();
        }
        return cateDao.count();
    }

    @Override
    public List<Category> findAll(int page, int pagesize) {
        if (categoryRepository != null) {
            return categoryRepository.findAll(PageRequest.of(page, pagesize)).getContent();
        }
        return cateDao.findAll(page, pagesize);
    }

    @Override
    public List<Category> searchByName(String catname, int page, int pagesize) {
        if (categoryRepository != null) {
            return categoryRepository.findByCategorynameContainingIgnoreCase(catname, PageRequest.of(page, pagesize)).getContent();
        }
        return cateDao.searchByName(catname, page, pagesize);
    }

    @Override
    public int countByKeyword(String catname) {
        if (categoryRepository != null) {
            return (int) categoryRepository.findByCategorynameContainingIgnoreCase(catname, Pageable.unpaged()).getTotalElements();
        }
        return cateDao.countByKeyword(catname);
    }

    @Override
    public Category findByCategoryname(String name) {
        if (categoryRepository != null) {
            return categoryRepository.findByCategoryname(name).orElse(null);
        }
        try {
            return cateDao.findByCategoryname(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Page<Category> findAll(Pageable pageable) {
        if (categoryRepository != null) {
            return categoryRepository.findAll(pageable);
        }
        int page = pageable.getPageNumber();
        int size = pageable.getPageSize();
        List<Category> list = cateDao.findAll(page, size);
        return new org.springframework.data.domain.PageImpl<>(list, pageable, cateDao.count());
    }

    @Override
    public Page<Category> searchByName(String catname, Pageable pageable) {
        if (categoryRepository != null) {
            return categoryRepository.findByCategorynameContainingIgnoreCase(catname, pageable);
        }
        int page = pageable.getPageNumber();
        int size = pageable.getPageSize();
        List<Category> list = cateDao.searchByName(catname, page, size);
        return new org.springframework.data.domain.PageImpl<>(list, pageable, cateDao.countByKeyword(catname));
    }

    @Override
    public Category save(Category category) {
        if (categoryRepository != null) {
            return categoryRepository.save(category);
        }
        if (category.getCategoryid() > 0) {
            cateDao.update(category);
        } else {
            cateDao.insert(category);
        }
        return category;
    }
}