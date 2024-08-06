package ra.model.service;

import ra.model.entity.Product;

import java.util.List;

public interface IProductService {
    List<Product> getAllProducts();
    Product getProductById(int id);
    Boolean addProduct(Product product);
    Boolean updateProduct(Product product);
    Boolean deleteProduct(int id);
}
