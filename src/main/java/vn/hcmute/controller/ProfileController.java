package vn.hcmute.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import vn.hcmute.dao.UserDao;
import vn.hcmute.entity.User;

@WebServlet(urlPatterns = {"/profile"})
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,      // 1 MB
    maxFileSize = 1024 * 1024 * 5,         // 5 MB
    maxRequestSize = 1024 * 1024 * 10      // 10 MB
)
public class ProfileController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        User account = (User) session.getAttribute("account");
        if (account == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        // Reload fresh data from DB
        UserDao userDao = new UserDao();
        User user = userDao.findById(account.getId());
        if (user != null) {
            session.setAttribute("account", user);
        }

        req.setAttribute("user", user);
        req.getRequestDispatcher("/views/profile.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession();
        User account = (User) session.getAttribute("account");
        if (account == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String fullname = req.getParameter("fullname");
        String phone = req.getParameter("phone");

        // Validation
        Map<String, String> errors = new HashMap<>();

        if (fullname == null || fullname.trim().isEmpty()) {
            errors.put("fullname", "Họ tên không được để trống!");
        } else if (fullname.trim().length() < 2 || fullname.trim().length() > 100) {
            errors.put("fullname", "Họ tên phải từ 2 đến 100 ký tự!");
        }

        if (phone != null && !phone.trim().isEmpty()) {
            if (!phone.trim().matches("^0\\d{9}$")) {
                errors.put("phone", "Số điện thoại không hợp lệ! (VD: 0912345678)");
            }
        }

        // Handle file upload
        Part filePart = req.getPart("images");
        String fileName = null;
        if (filePart != null && filePart.getSize() > 0) {
            String submittedFileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
            String fileExtension = "";
            int dotIndex = submittedFileName.lastIndexOf('.');
            if (dotIndex > 0) {
                fileExtension = submittedFileName.substring(dotIndex + 1).toLowerCase();
            }

            // Validate file type
            if (!fileExtension.equals("jpg") && !fileExtension.equals("jpeg") 
                && !fileExtension.equals("png") && !fileExtension.equals("gif")) {
                errors.put("images", "Chỉ chấp nhận file ảnh (jpg, jpeg, png, gif)!");
            }

            // Validate file size (5MB)
            if (filePart.getSize() > 5 * 1024 * 1024) {
                errors.put("images", "Dung lượng ảnh không được vượt quá 5MB!");
            }

            if (!errors.containsKey("images")) {
                fileName = System.currentTimeMillis() + "_" + submittedFileName;
            }
        }

        if (!errors.isEmpty()) {
            req.setAttribute("errors", errors);
            UserDao userDao = new UserDao();
            req.setAttribute("user", userDao.findById(account.getId()));
            req.getRequestDispatcher("/views/profile.jsp").forward(req, resp);
            return;
        }

        // Save file if uploaded
        if (fileName != null && filePart != null) {
            String uploadPath = getServletContext().getRealPath("/") + "uploads";
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            filePart.write(uploadPath + File.separator + fileName);
        }

        // Update user
        UserDao userDao = new UserDao();
        User user = userDao.findById(account.getId());
        user.setFullname(fullname.trim());
        if (phone != null && !phone.trim().isEmpty()) {
            user.setPhone(phone.trim());
        }
        if (fileName != null) {
            user.setImages("uploads/" + fileName);
        }

        userDao.update(user);

        // Update session
        session.setAttribute("account", user);
        session.setAttribute("username", user.getUsername());

        req.setAttribute("user", user);
        req.setAttribute("message", "Cập nhật hồ sơ thành công!");
        req.getRequestDispatcher("/views/profile.jsp").forward(req, resp);
    }
}
