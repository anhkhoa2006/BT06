package vn.hcmute.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import vn.hcmute.config.JPAConfig;
import vn.hcmute.entity.User;
import java.util.List;

public class UserDao implements IUserDao {

	@Override
	public void insert(User user) {
		EntityManager em = JPAConfig.getEntityManager();
		EntityTransaction trans = em.getTransaction();
		try {
			trans.begin();
			em.persist(user);
			trans.commit();
		} catch (Exception e) {
			e.printStackTrace();
			trans.rollback();
			throw e;
		} finally {
			em.close();
		}
	}

	@Override
	public void update(User user) {
		EntityManager em = JPAConfig.getEntityManager();
		EntityTransaction trans = em.getTransaction();
		try {
			trans.begin();
			em.merge(user);
			trans.commit();
		} catch (Exception e) {
			e.printStackTrace();
			trans.rollback();
			throw e;
		} finally {
			em.close();
		}
	}

	@Override
	public void delete(int id) throws Exception {
		EntityManager em = JPAConfig.getEntityManager();
		EntityTransaction trans = em.getTransaction();
		try {
			trans.begin();
			User user = em.find(User.class, id);
			if (user != null) {
				em.remove(user);
			} else {
				throw new Exception("Không tìm thấy người dùng có ID: " + id);
			}
			trans.commit();
		} catch (Exception e) {
			e.printStackTrace();
			trans.rollback();
			throw e;
		} finally {
			em.close();
		}
	}

	@Override
	public User findById(int id) {
		EntityManager em = JPAConfig.getEntityManager();
		try {
			return em.find(User.class, id);
		} catch (Exception e) {
			return null;
		} finally {
			em.close();
		}
	}

	@Override
	public User findByUsername(String username) {
		EntityManager em = JPAConfig.getEntityManager();
		try {
			String jpql = "SELECT u FROM User u WHERE u.username = :username";
			TypedQuery<User> query = em.createQuery(jpql, User.class);
			query.setParameter("username", username);
			return query.getSingleResult();
		} catch (Exception e) {
			return null;
		} finally {
			em.close();
		}
	}

	@Override
	public User findByEmail(String email) {
		EntityManager em = JPAConfig.getEntityManager();
		try {
			String jpql = "SELECT u FROM User u WHERE u.email = :email";
			TypedQuery<User> query = em.createQuery(jpql, User.class);
			query.setParameter("email", email);
			return query.getSingleResult();
		} catch (Exception e) {
			return null;
		} finally {
			em.close();
		}
	}

	@Override
	public void updatePassword(String email, String newPassword) {
		EntityManager em = JPAConfig.getEntityManager();
		EntityTransaction trans = em.getTransaction();
		try {
			trans.begin();
			User user = findByEmail(email);
			if (user != null) {
				user.setPassword(newPassword);
				em.merge(user);
			}
			trans.commit();
		} catch (Exception e) {
			e.printStackTrace();
			trans.rollback();
		} finally {
			em.close();
		}
	}

	@Override
	public List<User> findAll() {
		EntityManager em = JPAConfig.getEntityManager();
		try {
			String jpql = "SELECT u FROM User u ORDER BY u.id DESC";
			TypedQuery<User> query = em.createQuery(jpql, User.class);
			return query.getResultList();
		} finally {
			em.close();
		}
	}

	@Override
	public List<User> findAll(int page, int pagesize) {
		EntityManager em = JPAConfig.getEntityManager();
		try {
			String jpql = "SELECT u FROM User u ORDER BY u.id DESC";
			TypedQuery<User> query = em.createQuery(jpql, User.class);
			query.setFirstResult(page * pagesize);
			query.setMaxResults(pagesize);
			return query.getResultList();
		} finally {
			em.close();
		}
	}

	@Override
	public List<User> search(String keyword, int page, int pagesize) {
		EntityManager em = JPAConfig.getEntityManager();
		try {
			String jpql = "SELECT u FROM User u WHERE LOWER(u.username) LIKE :kw OR LOWER(u.email) LIKE :kw OR LOWER(u.fullname) LIKE :kw OR u.phone LIKE :kw ORDER BY u.id DESC";
			TypedQuery<User> query = em.createQuery(jpql, User.class);
			query.setParameter("kw", "%" + keyword.toLowerCase() + "%");
			query.setFirstResult(page * pagesize);
			query.setMaxResults(pagesize);
			return query.getResultList();
		} finally {
			em.close();
		}
	}

	@Override
	public int count() {
		EntityManager em = JPAConfig.getEntityManager();
		try {
			String jpql = "SELECT count(u) FROM User u";
			Query query = em.createQuery(jpql);
			return ((Long) query.getSingleResult()).intValue();
		} finally {
			em.close();
		}
	}

	@Override
	public int countByKeyword(String keyword) {
		EntityManager em = JPAConfig.getEntityManager();
		try {
			String jpql = "SELECT count(u) FROM User u WHERE LOWER(u.username) LIKE :kw OR LOWER(u.email) LIKE :kw OR LOWER(u.fullname) LIKE :kw OR u.phone LIKE :kw";
			Query query = em.createQuery(jpql);
			query.setParameter("kw", "%" + keyword.toLowerCase() + "%");
			return ((Long) query.getSingleResult()).intValue();
		} finally {
			em.close();
		}
	}
}