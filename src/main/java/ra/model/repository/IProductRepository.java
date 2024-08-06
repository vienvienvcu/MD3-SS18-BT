package ra.model.repository;

import ra.model.entity.Product;

import java.util.List;

public interface IProductRepository {
    List<Product> getAllProducts();
    Product getProductById(int id);
    Boolean addProduct(Product product);
    Boolean updateProduct(Product product);
    Boolean deleteProduct(int id);
}
