package ra.model.repository.impl;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ra.model.entity.ProductImage;
import ra.model.repository.IProductImageRepository;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
@Repository
public class ProductImageRepositoryImpl implements IProductImageRepository {
    @Autowired
    private SessionFactory sessionFactory;


    @Override
    public List<ProductImage> findAll() {
        Session session = sessionFactory.openSession();
        try {
            List productImageList = session.createQuery("from ProductImage").list();
            return productImageList;
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            session.close();
        }
        return null;
    }

    @Override
    public Boolean save(ProductImage productImage) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.save(productImage);
            session.getTransaction().commit();
            return true;
        }catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
        }finally {
            session.close();
        }

        return null;
    }

// Xóa một ảnh sản phẩm dựa trên imageId của nó.
    @Override
    public boolean delete(Integer imageId) {
        Session session = sessionFactory.openSession();
        try{
            session.beginTransaction();
            int result = session.createQuery("delete from ProductImage where imageId = :imageId")
                    .setParameter("imageId", imageId)
                    .executeUpdate();
            session.getTransaction().commit();
            if(result>0)
                return true;
        }catch (Exception ex){
            ex.printStackTrace();
            session.getTransaction().rollback();
        }finally {
            session.close();
        }
        return false;
    }
//Xóa tất cả ảnh sản phẩm liên kết với một sản phẩm cụ thể dựa trên proId của
    @Override
    public boolean deleteByProId(Integer proId) {
        Session session = sessionFactory.openSession();
        try{
            session.beginTransaction();
            int result = session.createQuery("delete from ProductImage where product.productId = :proId")
                    .setParameter("proId", proId)
                    .executeUpdate();
            session.getTransaction().commit();
            if(result>0)
                return true;
        }catch (Exception ex){
            ex.printStackTrace();
            session.getTransaction().rollback();
        }finally {
            session.close();
        }
        return false;
    }

    @Override
    public List<ProductImage> findByProductId(Integer proId) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            List<ProductImage> productImages = session.createQuery("from ProductImage where product.productId = :proId", ProductImage.class)
                    .setParameter("proId", proId)
                    .list();
            session.getTransaction().commit();
            return productImages;
        } catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
        } finally {
            session.close();
        }
        return new ArrayList<>();
    }


    @Override
    public Boolean update(ProductImage productImage) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.update(productImage);
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
}
