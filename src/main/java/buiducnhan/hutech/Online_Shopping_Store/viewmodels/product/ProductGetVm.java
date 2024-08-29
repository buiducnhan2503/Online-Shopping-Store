package buiducnhan.hutech.Online_Shopping_Store.viewmodels.product;

import buiducnhan.hutech.Online_Shopping_Store.entities.Product;
import buiducnhan.hutech.Online_Shopping_Store.entities.ProductImage;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.Set;
import java.util.stream.Collectors;

@Builder
public record ProductGetVm(Long id, String name, double price, String description, String image, int amount,
                           Long category_id, Long supplier_id, Set<String> image_list) {
    public static ProductGetVm from(@NotNull Product product) {
        Set<String> imageList = product.getImages().stream()
                .map(ProductImage::getImage_url)
                .collect(Collectors.toSet());

        return ProductGetVm.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .description(product.getDescription())
                .image(product.getImage())
                .amount(product.getAmount())
                .category_id(product.getCategory().getId())
                .supplier_id(product.getSupplier().getId())
                .image_list(imageList)
                .build();
    }
}

