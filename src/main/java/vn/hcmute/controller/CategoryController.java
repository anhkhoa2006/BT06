package vn.hcmute.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.hcmute.entity.Category;
import vn.hcmute.service.ICategoryService;
import vn.hcmute.utils.Constants;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/admin")
public class CategoryController {

    @Autowired
    private ICategoryService cateService;

    @GetMapping("/categories")
    public String listCategories(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            Model model) {

        if (page < 1) page = 1;
        if (size < 1) size = 5;

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("categoryid").descending());
        Page<Category> categoryPage;

        if (keyword != null && !keyword.trim().isEmpty()) {
            keyword = keyword.trim();
            categoryPage = cateService.searchByName(keyword, pageable);
        } else {
            keyword = "";
            categoryPage = cateService.findAll(pageable);
        }

        int totalPages = categoryPage.getTotalPages();
        if (totalPages == 0) totalPages = 1;

        model.addAttribute("listcate", categoryPage.getContent());
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", categoryPage.getTotalElements());
        model.addAttribute("pageSize", size);

        return "admin/category-list";
    }

    @GetMapping("/category/add")
    public String addCategory(Model model) {
        model.addAttribute("category", new Category());
        return "admin/category-add";
    }

    @PostMapping("/category/insert")
    public String insertCategory(
            @ModelAttribute Category category,
            @RequestParam(name = "images1", required = false) MultipartFile file,
            @RequestParam(name = "images", required = false) String imagesUrl) {

        String uploadPath = Constants.DIR;
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) uploadDir.mkdirs();

        try {
            if (file != null && !file.isEmpty()) {
                String originalFilename = Paths.get(file.getOriginalFilename()).getFileName().toString();
                int dotIndex = originalFilename.lastIndexOf(".");
                String ext = (dotIndex > 0) ? originalFilename.substring(dotIndex + 1) : "png";
                String fname = System.currentTimeMillis() + "." + ext;
                file.transferTo(new File(uploadPath, fname));
                category.setImages(fname);
            } else if (imagesUrl != null && !imagesUrl.trim().isEmpty()) {
                category.setImages(imagesUrl.trim());
            } else {
                category.setImages("avatar.png");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        cateService.insert(category);
        return "redirect:/admin/categories";
    }

    @GetMapping("/category/edit")
    public String editCategory(@RequestParam("id") int id, Model model) {
        Category category = cateService.findById(id);
        model.addAttribute("cate", category);
        return "admin/category-edit";
    }

    @PostMapping("/category/update")
    public String updateCategory(
            @ModelAttribute Category category,
            @RequestParam(name = "images1", required = false) MultipartFile file,
            @RequestParam(name = "images", required = false) String imagesUrl) {

        Category old = cateService.findById(category.getCategoryid());
        String oldImage = (old != null) ? old.getImages() : null;

        String uploadPath = Constants.DIR;
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) uploadDir.mkdirs();

        try {
            if (file != null && !file.isEmpty()) {
                if (oldImage != null && !oldImage.startsWith("http")) {
                    deleteFile(uploadPath + File.separator + oldImage);
                }
                String originalFilename = Paths.get(file.getOriginalFilename()).getFileName().toString();
                int dotIndex = originalFilename.lastIndexOf(".");
                String ext = (dotIndex > 0) ? originalFilename.substring(dotIndex + 1) : "png";
                String fname = System.currentTimeMillis() + "." + ext;
                file.transferTo(new File(uploadPath, fname));
                category.setImages(fname);
            } else if (imagesUrl != null && !imagesUrl.trim().isEmpty()) {
                category.setImages(imagesUrl.trim());
            } else {
                category.setImages(oldImage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        cateService.update(category);
        return "redirect:/admin/categories";
    }

    @GetMapping("/category/delete")
    public String deleteCategory(@RequestParam("id") int id) {
        try {
            cateService.delete(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/admin/categories";
    }

    private void deleteFile(String filePath) {
        try {
            Path path = Paths.get(filePath);
            if (Files.exists(path)) {
                Files.delete(path);
            }
        } catch (Exception ignored) {}
    }
}