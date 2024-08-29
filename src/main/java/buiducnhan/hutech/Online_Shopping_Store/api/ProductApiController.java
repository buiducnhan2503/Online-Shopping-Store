package buiducnhan.hutech.Online_Shopping_Store.api;

import buiducnhan.hutech.Online_Shopping_Store.entities.Category;
import buiducnhan.hutech.Online_Shopping_Store.entities.Product;
import buiducnhan.hutech.Online_Shopping_Store.entities.Supplier;
import buiducnhan.hutech.Online_Shopping_Store.service.CategoryService;
import buiducnhan.hutech.Online_Shopping_Store.service.ProductService;
import buiducnhan.hutech.Online_Shopping_Store.service.SupplierService;
import buiducnhan.hutech.Online_Shopping_Store.viewmodels.product.ProductGetVm;
import buiducnhan.hutech.Online_Shopping_Store.viewmodels.product.ProductPostVm;
import buiducnhan.hutech.Online_Shopping_Store.viewmodels.product.ProductPutVm;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/api/products")
public class ProductApiController {
    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SupplierService supplierService;

    @GetMapping()
    public Map<String, Object> getAllProducts(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size,
            @RequestParam(value = "sortField", defaultValue = "id") String sortField,
            @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir) {

        Page<Product> productPage = productService.findPaginated(page, size, sortField, sortDir);
        List<Product> products = productPage.getContent();

        List<ProductGetVm> productGetVms = products.stream()
                .map(ProductGetVm::from)
                .toList();

        Map<String, Object> response = new HashMap<>();
        response.put("currentPage", page);
        response.put("totalPages", productPage.getTotalPages());
        response.put("totalItems", productPage.getTotalElements());
        response.put("products", productGetVms);
        response.put("sortField", sortField);
        response.put("sortDir", sortDir);
        response.put("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        return response;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductGetVm> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new RuntimeException("Product not found on :: " + id));
        ProductGetVm productGetVm = ProductGetVm.from(product);
        return ResponseEntity.ok().body(productGetVm);
    }

    @PostMapping
    public ResponseEntity<ProductPostVm> createProduct(@RequestPart("product") @Valid ProductPostVm productPostVm,
                                                       @RequestParam(name = "file", required = false) MultipartFile file,
                                                       @RequestParam(name = "files", required = false) MultipartFile[] files) throws IOException {
        // Tạo đối tượng Product từ các tham số
        Product product = new Product();
        product.setName(productPostVm.name());
        product.setPrice(productPostVm.price());
        product.setDescription(productPostVm.description());
        product.setAmount(productPostVm.amount());

        // Xử lý Category
        Category category = categoryService.getCategoryById(productPostVm.category_id())
                .orElseThrow(() -> new RuntimeException("Category not found on :: " + productPostVm.category_id()));
        product.setCategory(category);

        // Xử lý Supplier
        Supplier supplier = supplierService.getSupplierById(productPostVm.supplier_id())
                .orElseThrow(() -> new RuntimeException("Supplier not found on :: " + productPostVm.supplier_id()));
        product.setSupplier(supplier);

        // Tạo sản phẩm và lưu vào cơ sở dữ liệu
        Product savedProduct = productService.createProduct(product, file, files);

        ProductPostVm responseProduct = ProductPostVm.from(savedProduct);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseProduct);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductPutVm> updateProduct(@PathVariable Long id,
                                                      @RequestPart("product") @Valid ProductPutVm productPutVm,
                                                      @RequestParam(name = "file", required = false) MultipartFile file,
                                                      @RequestParam(name = "files", required = false) MultipartFile[] files) throws IOException {

        Product existingProduct = productService.getProductById(id)
                .orElseThrow(() -> new RuntimeException("Product not found on :: " + id));

        // Cập nhật các thuộc tính sản phẩm
        existingProduct.setName(productPutVm.name());
        existingProduct.setPrice(productPutVm.price());
        existingProduct.setDescription(productPutVm.description());
        existingProduct.setAmount(productPutVm.amount());

        if (productPutVm.category_id() != null) {
            Category category = categoryService.getCategoryById(productPutVm.category_id())
                    .orElseThrow(() -> new RuntimeException("Category not found on :: " + productPutVm.category_id()));
            existingProduct.setCategory(category);
        }

        if (productPutVm.supplier_id() != null) {
            Supplier supplier = supplierService.getSupplierById(productPutVm.supplier_id())
                    .orElseThrow(() -> new RuntimeException("Supplier not found on :: " + productPutVm.supplier_id()));
            existingProduct.setSupplier(supplier);
        }

        // Cập nhật sản phẩm và lưu vào cơ sở dữ liệu
        Product updatedProduct = productService.updateProduct(existingProduct, file, files);

        // Chuyển đổi sản phẩm đã cập nhật thành ProductPutVm và trả về phản hồi
        ProductPutVm responseProductPutVm = ProductPutVm.from(updatedProduct);
        return ResponseEntity.status(HttpStatus.OK).body(responseProductPutVm);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProductById(id);
        return ResponseEntity.ok().build();
    }
}
