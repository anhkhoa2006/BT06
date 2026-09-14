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

@WebServlet(urlPatterns = {"/forgot-password"})
public class ForgotPasswordController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/views/forgot-password.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");

        // Validation
        Map<String, String> errors = new HashMap<>();
        if (email == null || email.trim().isEmpty()) {
            errors.put("email", "Email không được để trống!");
        } else if (!email.trim().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            errors.put("email", "Email không đúng định dạng!");
        }

        if (!errors.isEmpty()) {
            req.setAttribute("errors", errors);
            req.getRequestDispatcher("/views/forgot-password.jsp").forward(req, resp);
            return;
        }

        UserDao userDao = new UserDao();
        User user = userDao.findByEmail(email);

        if (user != null) {
            String otp = EmailUtils.generateOTP();
            String subject = "Mã OTP khôi phục mật khẩu";
            String content = "Mã OTP khôi phục mật khẩu của bạn là: " + otp;

            boolean isSent = EmailUtils.sendEmail(email, subject, content);

            if (isSent) {
                HttpSession session = req.getSession();
                session.setAttribute("resetOtp", otp);
                session.setAttribute("resetEmail", email);

                resp.sendRedirect(req.getContextPath() + "/verify-reset-otp");
            } else {
                req.setAttribute("error", "Lỗi hệ thống khi gửi email!");
                req.getRequestDispatcher("/views/forgot-password.jsp").forward(req, resp);
            }
        } else {
            req.setAttribute("error", "Email không tồn tại trong hệ thống!");
            req.getRequestDispatcher("/views/forgot-password.jsp").forward(req, resp);
        }
    }
}