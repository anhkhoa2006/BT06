package vn.hcmute.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import vn.hcmute.entity.User;
import vn.hcmute.dao.UserDao;

@WebServlet(urlPatterns = {"/verify-otp"})
public class VerifyOTPController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/views/verify-otp.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String enteredOtp = req.getParameter("otp");

        // Validation
        Map<String, String> errors = new HashMap<>();
        if (enteredOtp == null || enteredOtp.trim().isEmpty()) {
            errors.put("otp", "Mã OTP không được để trống!");
        } else if (enteredOtp.trim().length() != 6) {
            errors.put("otp", "Mã OTP phải có đúng 6 ký tự!");
        }

        if (!errors.isEmpty()) {
            req.setAttribute("errors", errors);
            req.getRequestDispatcher("/views/verify-otp.jsp").forward(req, resp);
            return;
        }

        HttpSession session = req.getSession();
        String sessionOtp = (String) session.getAttribute("otp");

        if (sessionOtp != null && sessionOtp.equals(enteredOtp)) {
            String email = (String) session.getAttribute("tempEmail");
            String username = (String) session.getAttribute("tempUsername");
            String password = (String) session.getAttribute("tempPassword");

            User newUser = new User();
            newUser.setEmail(email);
            newUser.setUsername(username);
            newUser.setPassword(password);
            newUser.setStatus(1);
            UserDao userDao = new UserDao();
            userDao.insert(newUser);

            session.removeAttribute("otp");
            session.removeAttribute("tempEmail");
            session.removeAttribute("tempUsername");
            session.removeAttribute("tempPassword");

            req.setAttribute("message", "Kích hoạt thành công! Vui lòng đăng nhập.");
            req.getRequestDispatcher("/views/login.jsp").forward(req, resp);
        } else {
            req.setAttribute("error", "Mã OTP không chính xác hoặc đã hết hạn!");
            req.getRequestDispatcher("/views/verify-otp.jsp").forward(req, resp);
        }
    }
}