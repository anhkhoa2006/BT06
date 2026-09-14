package vn.hcmute.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import vn.hcmute.dao.CategoryDao;
import vn.hcmute.dao.ProductDao;
import vn.hcmute.entity.Category;
import vn.hcmute.entity.Product;

import java.io.IOException;
import java.sql.Date;
import java.util.List;

@WebServlet(urlPatterns = {"/admin/product"})
public class AdminProductController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null) action = "list"; 
        
        ProductDao productDao = new ProductDao();
        
        try {
            switch (action) {
                case "delete":
                    int deleteId = Integer.parseInt(req.getParameter("id"));
                    productDao.delete(deleteId);
                    resp.sendRedirect(req.getContextPath() + "/admin/product?action=list");
                    break;
                    
                case "edit":
                    int editId = Integer.parseInt(req.getParameter("id"));
                    Product product = productDao.findById(editId);
                    req.setAttribute("product", product);
                    req.setAttribute("categoryList", new CategoryDao().findAll());
                    req.getRequestDispatcher("/views/admin/product-form.jsp").forward(req, resp);
                    break;
                    
                case "add":
                    req.setAttribute("categoryList", new CategoryDao().findAll());
                    req.getRequestDispatcher("/views/admin/product-form.jsp").forward(req, resp);
                    break;
                    
                case "list":
                default:
                    List<Product> productList = productDao.findAll();
                    req.setAttribute("productList", productList);
                    req.getRequestDispatcher("/views/admin/product-list.jsp").forward(req, resp);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        
        String action = req.getParameter("action");
        ProductDao productDao = new ProductDao();
        
        try {
            Product product = new Product();
            if ("update".equals(action)) {
                product.setProductId(Integer.parseInt(req.getParameter("productId")));
            }
            
            product.setProductName(req.getParameter("productName"));
            product.setPrice(Integer.parseInt(req.getParameter("price")));
            product.setImages(req.getParameter("images"));
            product.setDescription(req.getParameter("description"));
            product.setCreatedDate(new Date(System.currentTimeMillis()));
            
            Category category = new Category();
            category.setCategoryid(Integer.parseInt(req.getParameter("categoryId")));
            product.setCategory(category);
            
            if ("update".equals(action)) {
                productDao.update(product);
            } else if ("insert".equals(action)) {
                productDao.insert(product);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        resp.sendRedirect(req.getContextPath() + "/admin/product?action=list");
    }
}