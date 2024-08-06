package ra.model.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ra.model.entity.Product;
import ra.model.repository.IProductRepository;
import ra.model.service.IProductService;

import java.util.List;
@Service
public class ProductServiceImpl implements IProductService {

    @Autowired
    private IProductRepository productRepository;
    @Override
    public List<Product> getAllProducts() {
        return productRepository.getAllProducts();
    }

    @Override
    public Product getProductById(int id) {
        return productRepository.getProductById(id);
    }

    @Override
    public Boolean addProduct(Product product) {
        return productRepository.addProduct(product);
    }

    @Override
    public Boolean updateProduct(Product product) {
        return productRepository.updateProduct(product);
    }

    @Override
    public Boolean deleteProduct(int id) {
        return productRepository.deleteProduct(id);
    }
}
