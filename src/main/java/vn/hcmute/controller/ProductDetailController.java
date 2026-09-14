package vn.hcmute.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import vn.hcmute.dao.ProductDao;
import vn.hcmute.entity.Product;
import java.io.IOException;

@WebServlet(urlPatterns = {"/product-detail"})
public class ProductDetailController extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idParam = req.getParameter("id");
        
        if (idParam != null && !idParam.isEmpty()) {
            try {
                int productId = Integer.parseInt(idParam);
                ProductDao dao = new ProductDao();
                Product product = dao.findById(productId);
                
                if (product != null) {
                    req.setAttribute("product", product);
                    req.getRequestDispatcher("/views/product-detail.jsp").forward(req, resp);
                    return;
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        
        resp.sendRedirect(req.getContextPath() + "/home");
    }
}