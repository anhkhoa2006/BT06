package vn.hcmute.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import vn.hcmute.dao.ProductDao;
import vn.hcmute.entity.Product;
import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/product"})
public class ProductController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ProductDao dao = new ProductDao();
        
        int page = 1;
        String pageParam = req.getParameter("page");
        if (pageParam != null && !pageParam.isEmpty()) {
            try {
                page = Integer.parseInt(pageParam);
            } catch (NumberFormatException e) {
                page = 1;
            }
        }
        
        int limit = 6;
        int offset = (page - 1) * limit;
        
        List<Product> productList = dao.findAll(offset, limit);
        long totalProducts = dao.countAll();
        int totalPages = (int) Math.ceil((double) totalProducts / limit);
        
        req.setAttribute("productList", productList);
        req.setAttribute("currentPage", page);
        req.setAttribute("totalPages", totalPages);
        
        req.getRequestDispatcher("/views/product-list.jsp").forward(req, resp);
    }
}