package ra.model.repository;

import ra.model.entity.ProductImage;

import java.util.List;

public interface IProductImageRepository {
    List<ProductImage> findAll();

    Boolean save(ProductImage productImage);

    boolean delete(Integer imageId);

    boolean deleteByProId(Integer proId);

    List<ProductImage> findByProductId(Integer proId);
    Boolean update(ProductImage productImage);
}
