package ra.controller;

import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerMapping;
import ra.model.entity.Product;
import ra.model.entity.ProductImage;
import ra.model.service.IProductImageService;
import ra.model.service.IProductService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.List;

@RequestMapping
@Controller

public class ProductController {
  @Autowired
  private IProductService productService;
  @Autowired
  private IProductImageService productImageService;

  @RequestMapping(value = {"/", "/loadProducts"})
  public String loadProduct(Model model) {
    List<Product> productList = productService.getAllProducts();
    model.addAttribute("productList", productList);
    return "productList";
  }

  @RequestMapping(value = "/initInsertProduct")
  public String initInsertProduct(Model model) {
    Product productNew = new Product();
    model.addAttribute("product", productNew);
    return "insertProduct";
  }

  @RequestMapping(value = "/insertProduct")
  public String insertProduct(@Validated @ModelAttribute("product") Product product,
                              BindingResult result,
                              @RequestParam("filePrimary") MultipartFile filePrimary,
                              @RequestParam("secondaryImages") MultipartFile[] listSecondImages,
                              Model model) {
    String imageUrl = "/Users/viennguyenthi/Desktop/model3/UploadFile/src/main/resources/static/images/";
      try {
        String primaryImageName = filePrimary.getOriginalFilename();
        if (!filePrimary.isEmpty()) {
          // Upload ảnh chính
          File primaryImage = new File(imageUrl + primaryImageName);
          Files.copy(filePrimary.getInputStream(), primaryImage.toPath(), StandardCopyOption.REPLACE_EXISTING);
          product.setImageUrl(primaryImageName);
        }

        boolean blSave = productService.addProduct(product);
        if (blSave) {
          if (listSecondImages != null && listSecondImages.length > 0) {
            for (MultipartFile secondImage : listSecondImages) {
              String imageName = secondImage.getOriginalFilename();
              File secondImageFile = new File(imageUrl + imageName);
              Files.copy(secondImage.getInputStream(), secondImageFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

              ProductImage pimage = new ProductImage();
              pimage.setProduct(product);
              pimage.setImageName(imageName);
              productImageService.save(pimage);
            }
          }
          return "redirect:/loadProducts";  // Chuyển hướng đến danh sách sản phẩm
        } else {
          model.addAttribute("error", "Insert failed!");
          model.addAttribute("productNew", product);
          return "insertProduct";
        }
      } catch (IOException e) {
        e.printStackTrace(); // Ghi log lỗi
        model.addAttribute("error", "An error occurred while processing files.");
        model.addAttribute("productNew", product);
        return "insertProduct";
      }
    }




  @RequestMapping(value = "/initUpdateProduct/{id}")
  public String initUpdateProduct(@PathVariable Integer id, Model model) {
    Product product = productService.getProductById(id);
    if (product != null) {
      model.addAttribute("productUpdate", product);
      return "updateProduct";
    } else {
      model.addAttribute("error", "Product not found.");
      return "redirect:/loadProducts";
    }
  }

  @RequestMapping(value = "/updateProduct")
  public String updateProduct(@ModelAttribute("productUpdate") Product product,
                              @RequestParam(value = "filePrimary", required = false) MultipartFile filePrimary,
                              @RequestParam(value = "secondaryImages", required = false) MultipartFile[] listSecondImages,
                              Model model) {

    String imageUrl = "/Users/viennguyenthi/Desktop/model3/UploadFile/src/main/resources/static/images/";

    try {
      // Tìm sản phẩm hiện có
      Product existingProduct = productService.getProductById(product.getProductId());
      if (existingProduct == null) {
        model.addAttribute("error", "Product not found.");
        return "updateProduct";
      }

      // Cập nhật thông tin sản phẩm
      existingProduct.setProductName(product.getProductName());
      existingProduct.setPrice(product.getPrice());
      existingProduct.setStock(product.getStock());
      existingProduct.setColor(product.getColor());
      existingProduct.setStatus(product.getStatus());

      // Xử lý hình ảnh chính
      if (filePrimary != null && !filePrimary.isEmpty()) {
        // Xóa hình ảnh cũ nếu có
        if (existingProduct.getImageUrl() != null && !existingProduct.getImageUrl().isEmpty()) {
          File oldPrimaryImageFile = new File(imageUrl + existingProduct.getImageUrl());
          if (oldPrimaryImageFile.exists()) {
            oldPrimaryImageFile.delete();
          }
        }

        // Tải lên hình ảnh mới
        String primaryImageName = filePrimary.getOriginalFilename();
        File primaryImage = new File(imageUrl + primaryImageName);
        Files.copy(filePrimary.getInputStream(), primaryImage.toPath(), StandardCopyOption.REPLACE_EXISTING);
        existingProduct.setImageUrl(primaryImageName);
      }

      // Xử lý hình ảnh phụ
      if (listSecondImages != null) {
        // Xóa tất cả hình ảnh phụ cũ
        productImageService.deleteByProId(existingProduct.getProductId());

        // Tải lên hình ảnh phụ mới
        for (MultipartFile secondImage : listSecondImages) {
          String imageName = secondImage.getOriginalFilename();
          File secondImageFile = new File(imageUrl + imageName);
          Files.copy(secondImage.getInputStream(), secondImageFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

          ProductImage pimage = new ProductImage();
          pimage.setProduct(existingProduct);
          pimage.setImageName(imageName);
          productImageService.save(pimage);
        }
      }

      // Cập nhật sản phẩm
      productService.updateProduct(existingProduct);

      return "redirect:/loadProducts"; // Chuyển hướng đến danh sách sản phẩm
    } catch (IOException e) {
      e.printStackTrace(); // Ghi log lỗi
      model.addAttribute("error", "An error occurred while processing files.");
      model.addAttribute("productUpdate", product);
      return "updateProduct";
    }
  }


  @RequestMapping(value = "/deleteProduct/{id}")
  public String deleteProduct(@PathVariable Integer id, Model model) {
    String imageUrl = "/Users/viennguyenthi/Desktop/model3/UploadFile/src/main/resources/static/images/";
    try {
      // Lấy sản phẩm theo id
      Product product = productService.getProductById(id);
      if (product == null) {
        model.addAttribute("error", "Product not found.");
        return "redirect:/loadProducts";
      }
      // Xóa hình ảnh chính của sản phẩm nếu có
      if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
        File primaryImageFile = new File(imageUrl + product.getImageUrl());
        if (primaryImageFile.exists()) {
          primaryImageFile.delete();
        }
      }
      // Xóa tất cả hình ảnh phụ của sản phẩm
      List<ProductImage> productImages = productImageService.findByProductId(id);
      for (ProductImage image : productImages) {
        File secondaryImageFile = new File(imageUrl + image.getImageName());
        if (secondaryImageFile.exists()) {
          secondaryImageFile.delete();
        }
      }
      productImageService.deleteByProId(id);
      // Xóa sản phẩm
      productService.deleteProduct(id);
      return "redirect:/loadProducts"; // Chuyển hướng đến danh sách sản phẩm
    } catch (Exception e) {
      e.printStackTrace(); // Ghi log lỗi
      model.addAttribute("error", "An error occurred while deleting the product.");
      return "redirect:/loadProducts";
    }
  }

  @RequestMapping(value = "/detailProduct/{id}")
  public String detailProduct(@PathVariable Integer id, Model model) {
    try {
      // Lấy thông tin sản phẩm
      Product product = productService.getProductById(id);
      if (product != null) {
        // Lấy danh sách hình ảnh phụ của sản phẩm
        List<ProductImage> productImages = productImageService.findByProductId(id);

        // Thêm thông tin sản phẩm và hình ảnh phụ vào model
        model.addAttribute("product", product);
        model.addAttribute("productImages", productImages);

        return "detailProduct"; // Trả về trang detailProduct.html
      } else {
        model.addAttribute("error", "Product not found.");
        return "redirect:/loadProducts";
      }
    } catch (Exception e) {
      e.printStackTrace();
      model.addAttribute("error", "An error occurred while retrieving product details.");
      return "redirect:/loadProducts";
    }
  }

}
