package buiducnhan.hutech.Online_Shopping_Store.service;

import buiducnhan.hutech.Online_Shopping_Store.entities.Product;
import buiducnhan.hutech.Online_Shopping_Store.entities.ProductImage;
import buiducnhan.hutech.Online_Shopping_Store.repository.ProductImageRepository;
import buiducnhan.hutech.Online_Shopping_Store.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;

    @Value("${upload.path}")
    private String uploadPath;

    public Page<Product> findPaginated(int pageNo, int pageSize, String sortBy, String sortDir) {
        if (pageNo < 0) {
            pageNo = 0;
        }
        Sort sort = Sort.by(sortBy);
        if (sortDir.equalsIgnoreCase("desc")) {
            sort = sort.descending();
        }
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        return productRepository.findAll(pageable);
    }

    public List<Product> getAllProducts(Integer pageNo, Integer pageSize, String sortBy) {
        return productRepository.findAllProducts(pageNo, pageSize, sortBy);
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public Product createProduct(Product product, MultipartFile file, MultipartFile[] files) throws IOException {
        String fileName = null;

        // Xử lý tệp chính nếu có
        if (file != null && !file.isEmpty()) {
            fileName = saveImage(file);
        }

        Set<ProductImage> productImages = new HashSet<>();
        if (files != null) {
            for (MultipartFile item : files) {
                String fileNames = saveImage(item);
                ProductImage productImage = new ProductImage();
                productImage.setImage_url(fileNames);
                productImage.setProduct(product);
                productImages.add(productImage);
            }
        }

        product.setImages(productImages);
        product.setImage(fileName);
        return productRepository.save(product);
    }

    private String saveImage(MultipartFile file) throws IOException {
        String uploadDir = "src/main/resources/static/images/";
        // Tạo thư mục nếu không tồn tại
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String originalFileName = file.getOriginalFilename();
        String uniqueFileName = UUID.randomUUID().toString() + "_" + originalFileName;
        Path path = Paths.get(uploadDir + uniqueFileName);

        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        return uniqueFileName;
    }

    public Product updateProduct(Product product, MultipartFile file, MultipartFile[] files) throws IOException {
        Product existingProduct = productRepository.findById(product.getId())
                .orElseThrow(() -> new IllegalStateException("Product with ID " + product.getId() + " does not exist."));

        // Update product information
        existingProduct.setName(product.getName());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setCategory(product.getCategory());

        // Update main image if provided
        if (file != null && !file.isEmpty()) {
            String fileName = saveImage(file);
            existingProduct.setImage(fileName);
        }

        // Update additional images
        if (files != null && files.length > 0) {
            Set<ProductImage> newImages = new HashSet<>();
            for (MultipartFile image : files) {
                if (!image.isEmpty()) {
                    String fileName = saveImage(image);
                    ProductImage productImage = new ProductImage();
                    productImage.setImage_url(fileName);
                    productImage.setProduct(existingProduct);
                    newImages.add(productImage);
                }
            }
            // Clear existing images
            existingProduct.getImages().clear();
            // Add new images
            existingProduct.getImages().addAll(newImages);
        }
        return productRepository.save(existingProduct);
    }

    public void deleteProductById(Long id) {
        if (!productRepository.existsById(id)) {
            throw new IllegalStateException("Product with ID " + id + " does not exist.");
        }
        productRepository.deleteById(id);
    }

    public List<Product> searchProduct(String keyword) {
        return productRepository.searchProduct(keyword);
    }
}
