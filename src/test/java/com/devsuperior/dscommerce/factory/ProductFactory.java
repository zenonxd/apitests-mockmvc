package com.devsuperior.dscommerce.factory;

import com.devsuperior.dscommerce.dto.ProductDTO;
import com.devsuperior.dscommerce.entities.Category;
import com.devsuperior.dscommerce.entities.Product;

public class ProductFactory {

    public static Product createProduct() {
        Product product = new Product(3L, "Macbook", "Descrição", 1.250, "https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/3-big.jpg");
        product.getCategories().add(new Category(3L, "Computadores"));
        return product;
    }

    public static ProductDTO createProductDto() {
        ProductDTO dto = new ProductDTO(createProduct());
        return dto;
    }
}
