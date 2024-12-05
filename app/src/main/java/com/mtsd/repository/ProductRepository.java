package com.mtsd.repository;

import com.mtsd.model.Product;
import com.mtsd.model.ReportLowStock;
import com.mtsd.model.ReportStockSummary;

import java.util.List;

public interface ProductRepository {
    boolean insert(Product product);
    boolean updateProduct(Product product);
    boolean deleteProductById(int productId);
    Product getProductById(int productId);
    List<Product> getAllProducts();
    Product getProductByName(String productName);
    int getQuantitySumOfAllProducts();
    int getLowStockCount(int threshold);
    int getProductQuantityById(int productId);
    boolean updateProductQuantity(int productId, int newQuantity);
    List<ReportLowStock> getLowStockProducts(int threshold);
    double getTotalInventoryValue();

    List<ReportStockSummary> getStockSummary();

}
