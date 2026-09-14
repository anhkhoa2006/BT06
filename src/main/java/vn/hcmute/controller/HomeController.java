package vn.hcmute.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import vn.hcmute.dao.ProductDao;
import java.util.List;
import vn.hcmute.entity.Product;
import vn.hcmute.dao.ProductDao;

@WebServlet(urlPatterns = { "/home" })
public class HomeController extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession();
		if (session.getAttribute("account") == null) {
			resp.sendRedirect(req.getContextPath() + "/login");
			return;
		}

		ProductDao productDao = new ProductDao();
		List<Product> top10Products = productDao.getTop10Newest();

		req.setAttribute("top10Products", top10Products);
		req.getRequestDispatcher("/views/home.jsp").forward(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}
}