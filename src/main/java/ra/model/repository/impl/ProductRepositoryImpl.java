package ra.model.repository.impl;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ra.model.entity.Product;
import ra.model.repository.IProductRepository;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
@Repository
public class ProductRepositoryImpl implements IProductRepository {
    @Autowired
    private SessionFactory sessionFactory;
    @Override
    public List<Product> getAllProducts() {
        Session session = sessionFactory.openSession();
        try {
            // Sử dụng JPQL với JOIN FETCH để nạp danh sách hình ảnh phụ
            String hql = "SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.imageList";
            List<Product> productList = session.createQuery(hql).list();
            return productList;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
        return null;
    }


    @Override
    public Product getProductById(int id) {
        Session session = sessionFactory.openSession();
        try {
            // Sử dụng JPQL với JOIN FETCH để nạp danh sách hình ảnh phụ
            String hql = "SELECT p FROM Product p LEFT JOIN FETCH p.imageList WHERE p.productId = :productId";
            Product product = (Product) session.createQuery(hql)
                    .setParameter("productId", id)
                    .uniqueResult();
            return product;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
        return null;
    }


    @Override
    public Boolean addProduct(Product product) {
       Session session = sessionFactory.openSession();
       try {
           session.beginTransaction();
           session.save(product);
           session.getTransaction().commit();
           return true;
       }catch (Exception e) {
           e.printStackTrace();
           session.getTransaction().rollback();
       }finally {
           session.close();
       }
        return false;
    }

    @Override
    public Boolean updateProduct(Product product) {
       Session session = sessionFactory.openSession();
       try {
           session.beginTransaction();
           session.update(product);
           session.getTransaction().commit();
           return true;
       }catch (Exception e) {
           e.printStackTrace();
       }finally {
           session.close();
       }
       return false;
    }

    @Override
    public Boolean deleteProduct(int id) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.delete(getProductById(id));
            session.getTransaction().commit();
            return true;
        }catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
        }
        return false;
    }
}
