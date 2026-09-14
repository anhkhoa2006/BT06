package vn.hcmute.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import vn.hcmute.dao.UserDao;

@WebServlet(urlPatterns = {"/verify-reset-otp"})
public class ResetPasswordController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/views/reset-password.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String enteredOtp = req.getParameter("otp");
        String newPassword = req.getParameter("newPassword");

        // Validation
        Map<String, String> errors = new HashMap<>();
        if (enteredOtp == null || enteredOtp.trim().isEmpty()) {
            errors.put("otp", "Mã OTP không được để trống!");
        } else if (enteredOtp.trim().length() != 6) {
            errors.put("otp", "Mã OTP phải có đúng 6 ký tự!");
        }

        if (newPassword == null || newPassword.trim().isEmpty()) {
            errors.put("newPassword", "Mật khẩu mới không được để trống!");
        } else if (newPassword.length() < 6) {
            errors.put("newPassword", "Mật khẩu mới phải có ít nhất 6 ký tự!");
        }

        if (!errors.isEmpty()) {
            req.setAttribute("errors", errors);
            req.getRequestDispatcher("/views/reset-password.jsp").forward(req, resp);
            return;
        }

        HttpSession session = req.getSession();
        String sessionOtp = (String) session.getAttribute("resetOtp");
        String email = (String) session.getAttribute("resetEmail");

        if (sessionOtp != null && sessionOtp.equals(enteredOtp)) {
            UserDao userDao = new UserDao();
            userDao.updatePassword(email, newPassword);

            session.removeAttribute("resetOtp");
            session.removeAttribute("resetEmail");

            req.setAttribute("message", "Đổi mật khẩu thành công. Vui lòng đăng nhập lại!");
            req.getRequestDispatcher("/views/login.jsp").forward(req, resp);
        } else {
            req.setAttribute("error", "Mã OTP không chính xác!");
            req.getRequestDispatcher("/views/reset-password.jsp").forward(req, resp);
        }
    }
}