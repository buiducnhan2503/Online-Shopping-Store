package buiducnhan.hutech.Online_Shopping_Store.controllers;

import buiducnhan.hutech.Online_Shopping_Store.entities.Product;
import buiducnhan.hutech.Online_Shopping_Store.service.CategoryService;
import buiducnhan.hutech.Online_Shopping_Store.service.ProductService;
import buiducnhan.hutech.Online_Shopping_Store.service.SupplierService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {
    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SupplierService supplierService;

    @GetMapping()
    public String getAllProducts(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size,
            @RequestParam(value = "sortField", defaultValue = "id") String sortField,
            @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir,
            Model model) {

        Page<Product> productPage = productService.findPaginated(page, size, sortField, sortDir);
        List<Product> products = productPage.getContent();

        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("totalItems", productPage.getTotalElements());
        model.addAttribute("products", products);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        return "products/product-list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("suppliers", supplierService.getAllSuppliers());
        return "products/add-product";
    }

    @PostMapping("/add")
    public String addProduct(@Valid Product product,
                             BindingResult result,
                             Model model,
                             @RequestParam("imageFile") MultipartFile file,
                             @RequestParam("imageFiles") MultipartFile[] imagelist) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("suppliers", supplierService.getAllSuppliers());
            return "products/add-product";
        }
        try {
            productService.createProduct(product, file, imagelist);
        } catch (IOException e) {
            e.printStackTrace();
            result.rejectValue("image", "error.product", "Error uploading image");
            return "products/add-product";
        }
        return "redirect:/products";
    }

    @GetMapping("/details/{id}")
    public String showProduct(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("suppliers", supplierService.getAllSuppliers());
        return "products/details-product";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("suppliers", supplierService.getAllSuppliers());
        return "products/update-product";
    }

    @PostMapping("/update/{id}")
    public String updateProduct(@PathVariable Long id, @Valid Product product, BindingResult result,
                                @RequestParam("imageFile") MultipartFile file,
                                @RequestParam("imageFiles") MultipartFile[] files) {
        if (result.hasErrors()) {
            product.setId(id);
            return "products/update-product";
        }
        try {
            productService.updateProduct(product, file, files);
        } catch (IOException e) {
            e.printStackTrace();
            result.rejectValue("image", "error.product", "Error uploading image");
            return "products/update-product";
        }
        return "redirect:/products";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProductById(id);
        return "redirect:/products";
    }

    @GetMapping("/search")
    public String searchProduct(
            @NotNull Model model,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(defaultValue = "id") String sortBy) {
        model.addAttribute("products", productService.searchProduct(keyword));
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages",
                productService
                        .getAllProducts(pageNo, pageSize, sortBy)
                        .size() / pageSize);
        model.addAttribute("categories",
                categoryService.getAllCategories());
        return "products/product-list";
    }
}
