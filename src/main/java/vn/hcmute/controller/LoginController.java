package vn.hcmute.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import vn.hcmute.dao.UserDao;
import vn.hcmute.entity.User;

@WebServlet(urlPatterns = {"/login"})
public class LoginController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/views/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        // Validation
        Map<String, String> errors = new HashMap<>();
        if (username == null || username.trim().isEmpty()) {
            errors.put("username", "Tên đăng nhập không được để trống!");
        }
        if (password == null || password.trim().isEmpty()) {
            errors.put("password", "Mật khẩu không được để trống!");
        }

        if (!errors.isEmpty()) {
            req.setAttribute("errors", errors);
            req.getRequestDispatcher("/views/login.jsp").forward(req, resp);
            return;
        }

        UserDao userDao = new UserDao();
        User user = userDao.findByUsername(username);

        if (user != null && user.getPassword().equals(password)) {
            if (user.getStatus() == 1) {
                HttpSession session = req.getSession();
                session.setAttribute("account", user);
                session.setAttribute("username", user.getUsername());

                resp.sendRedirect(req.getContextPath() + "/home");
            } else {
                req.setAttribute("error", "Tài khoản chưa được kích hoạt OTP!");
                req.getRequestDispatcher("/views/login.jsp").forward(req, resp);
            }
        } else {
            req.setAttribute("error", "Sai tên đăng nhập hoặc mật khẩu!");
            req.getRequestDispatcher("/views/login.jsp").forward(req, resp);
        }
    }
}