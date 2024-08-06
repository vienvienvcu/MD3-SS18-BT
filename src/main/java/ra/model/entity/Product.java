package ra.model.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Table(name = "products")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Integer productId;
    @NotBlank(message = "Product name is empty!")
    @Column(name = "product_name")
    private String productName;
    @NotBlank(message = "Product name is empty!")
    @Column(name = "image_name")
    private String imageUrl;
    @NotNull(message = "price is empty")
    @Column(name = "price")
    private Double price;
    @NotNull(message = "stock is empty")
    @Column(name = "stock")
    private Integer stock;
    @NotBlank(message = "Color is empty!")
    @Column(name = "color")
    private String color;
    @Column(name = "status")
    private Boolean status;


    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> imageList;



}
