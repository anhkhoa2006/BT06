package vn.hcmute.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import vn.hcmute.config.JPAConfig;
import vn.hcmute.entity.Product;
import java.util.List;

public class ProductDao {

	public List<Product> getTop10Newest() {
		EntityManager em = JPAConfig.getEntityManager();
		try {
			String jpql = "SELECT p FROM Product p ORDER BY p.createdDate DESC";
			TypedQuery<Product> query = em.createQuery(jpql, Product.class);
			query.setMaxResults(10);
			return query.getResultList();
		} finally {
			em.close();
		}
	}

	public List<Product> findAll(int offset, int limit) {
		EntityManager em = JPAConfig.getEntityManager();
		try {
			String jpql = "SELECT p FROM Product p";
			TypedQuery<Product> query = em.createQuery(jpql, Product.class);
			query.setFirstResult(offset);
			query.setMaxResults(limit);
			return query.getResultList();
		} finally {
			em.close();
		}
	}

	public long countAll() {
		EntityManager em = JPAConfig.getEntityManager();
		try {
			String jpql = "SELECT COUNT(p) FROM Product p";
			return em.createQuery(jpql, Long.class).getSingleResult();
		} finally {
			em.close();
		}
	}

	public Product findById(int id) {
		EntityManager em = JPAConfig.getEntityManager();
		try {
			return em.find(Product.class, id);
		} finally {
			em.close();
		}
	}

	public void insert(Product product) {
		EntityManager enma = JPAConfig.getEntityManager();
		EntityTransaction trans = enma.getTransaction();
		try {
			trans.begin();
			enma.persist(product); 
			trans.commit();
		} catch (Exception e) {
			e.printStackTrace();
			trans.rollback();
			throw e;
		} finally {
			enma.close();
		}
	}
	
		public List<Product> findAll() {
			EntityManager em = JPAConfig.getEntityManager();
			try {
				String jpql = "SELECT p FROM Product p";
				TypedQuery<Product> query = em.createQuery(jpql, Product.class);
				return query.getResultList();
			} finally {
				em.close();
			}
		}

		public void update(Product product) {
			EntityManager enma = JPAConfig.getEntityManager();
			EntityTransaction trans = enma.getTransaction();
			try {
				trans.begin();
				enma.merge(product);
				trans.commit();
			} catch (Exception e) {
				e.printStackTrace();
				trans.rollback();
				throw e;
			} finally {
				enma.close();
			}
		}

		public void delete(int id) {
			EntityManager enma = JPAConfig.getEntityManager();
			EntityTransaction trans = enma.getTransaction();
			try {
				trans.begin();
				Product product = enma.find(Product.class, id);
				if (product != null) {
					enma.remove(product);
				}
				trans.commit();
			} catch (Exception e) {
				e.printStackTrace();
				trans.rollback();
				throw e;
			} finally {
				enma.close();
			}
		}
}