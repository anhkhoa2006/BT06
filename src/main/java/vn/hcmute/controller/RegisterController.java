package vn.hcmute.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import vn.hcmute.dao.UserDao;
import vn.hcmute.entity.User;
import vn.hcmute.utils.EmailUtils;

@WebServlet(urlPatterns = {"/register"})
public class RegisterController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/views/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        // Validation
        Map<String, String> errors = new HashMap<>();

        if (username == null || username.trim().isEmpty()) {
            errors.put("username", "Tên đăng nhập không được để trống!");
        } else if (username.trim().length() < 3 || username.trim().length() > 50) {
            errors.put("username", "Tên đăng nhập phải từ 3 đến 50 ký tự!");
        }

        if (email == null || email.trim().isEmpty()) {
            errors.put("email", "Email không được để trống!");
        } else if (!email.trim().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            errors.put("email", "Email không đúng định dạng!");
        }

        if (password == null || password.trim().isEmpty()) {
            errors.put("password", "Mật khẩu không được để trống!");
        } else if (password.length() < 6) {
            errors.put("password", "Mật khẩu phải có ít nhất 6 ký tự!");
        }

        // Check existing username/email
        if (errors.isEmpty()) {
            UserDao userDao = new UserDao();
            if (userDao.findByUsername(username) != null) {
                errors.put("username", "Tên đăng nhập đã tồn tại!");
            }
            if (userDao.findByEmail(email) != null) {
                errors.put("email", "Email đã được sử dụng!");
            }
        }

        if (!errors.isEmpty()) {
            req.setAttribute("errors", errors);
            req.getRequestDispatcher("/views/register.jsp").forward(req, resp);
            return;
        }

        String otp = EmailUtils.generateOTP();
        String subject = "Mã xác thực đăng ký tài khoản";
        String content = "Xin chào " + username + ",\n\nMã OTP kích hoạt tài khoản của bạn là: " + otp + "\n\nVui lòng không chia sẻ mã này cho ai.";

        boolean isSent = EmailUtils.sendEmail(email, subject, content);

        if (isSent) {
            HttpSession session = req.getSession();
            session.setAttribute("otp", otp);
            session.setAttribute("tempEmail", email);
            session.setAttribute("tempUsername", username);
            session.setAttribute("tempPassword", password);

            resp.sendRedirect(req.getContextPath() + "/verify-otp");
        } else {
            req.setAttribute("error", "Lỗi gửi email. Vui lòng kiểm tra lại địa chỉ email!");
            req.getRequestDispatcher("/views/register.jsp").forward(req, resp);
        }
    }
}