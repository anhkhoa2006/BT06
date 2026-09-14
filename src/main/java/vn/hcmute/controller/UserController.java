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
import vn.hcmute.entity.User;
import vn.hcmute.service.IUserService;
import vn.hcmute.utils.Constants;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/admin")
public class UserController {

    @Autowired
    private IUserService userService;

    @GetMapping("/users")
    public String listUsers(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            Model model) {

        if (page < 1) page = 1;
        if (size < 1) size = 5;

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending());
        Page<User> userPage;

        if (keyword != null && !keyword.trim().isEmpty()) {
            keyword = keyword.trim();
            userPage = userService.search(keyword, pageable);
        } else {
            keyword = "";
            userPage = userService.findAll(pageable);
        }

        int totalPages = userPage.getTotalPages();
        if (totalPages == 0) totalPages = 1;

        model.addAttribute("listUser", userPage.getContent());
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", userPage.getTotalElements());
        model.addAttribute("pageSize", size);

        return "admin/user-list";
    }

    @GetMapping("/user/add")
    public String addUser(Model model) {
        model.addAttribute("user", new User());
        return "admin/user-add";
    }

    @PostMapping("/user/insert")
    public String insertUser(
            @ModelAttribute User user,
            @RequestParam(name = "images1", required = false) MultipartFile file,
            @RequestParam(name = "images", required = false) String imagesUrl,
            Model model) {

        if (userService.checkExistUsername(user.getUsername())) {
            model.addAttribute("error", "Tên đăng nhập đã tồn tại!");
            return "admin/user-add";
        }
        if (userService.checkExistEmail(user.getEmail())) {
            model.addAttribute("error", "Email đã tồn tại trong hệ thống!");
            return "admin/user-add";
        }

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
                user.setImages(fname);
            } else if (imagesUrl != null && !imagesUrl.trim().isEmpty()) {
                user.setImages(imagesUrl.trim());
            } else {
                user.setImages("avatar.png");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        userService.insert(user);
        return "redirect:/admin/users";
    }

    @GetMapping("/user/edit")
    public String editUser(@RequestParam("id") int id, Model model) {
        User user = userService.findById(id);
        model.addAttribute("user", user);
        return "admin/user-edit";
    }

    @PostMapping("/user/update")
    public String updateUser(
            @ModelAttribute User user,
            @RequestParam(name = "images1", required = false) MultipartFile file,
            @RequestParam(name = "images", required = false) String imagesUrl) {

        User old = userService.findById(user.getId());
        if (old == null) {
            return "redirect:/admin/users";
        }

        String oldImage = old.getImages();

        // Giữ mật khẩu cũ nếu người dùng không nhập mật khẩu mới
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            user.setPassword(old.getPassword());
        }

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
                user.setImages(fname);
            } else if (imagesUrl != null && !imagesUrl.trim().isEmpty()) {
                user.setImages(imagesUrl.trim());
            } else {
                user.setImages(oldImage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        userService.update(user);
        return "redirect:/admin/users";
    }

    @GetMapping("/user/delete")
    public String deleteUser(@RequestParam("id") int id) {
        try {
            userService.delete(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/admin/users";
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
