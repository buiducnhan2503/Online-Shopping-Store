package buiducnhan.hutech.Online_Shopping_Store.viewmodels.product;

import buiducnhan.hutech.Online_Shopping_Store.entities.Product;
import buiducnhan.hutech.Online_Shopping_Store.entities.ProductImage;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.Set;
import java.util.stream.Collectors;

@Builder
public record ProductPutVm(Long id, String name, double price, String description, int amount, String image,
                           Long category_id, Long supplier_id, Set<String> image_list) {
    public static ProductPutVm from(@NotNull Product product) {

        Set<String> imageList = product.getImages().stream()
                .map(ProductImage::getImage_url)
                .collect(Collectors.toSet());

        return ProductPutVm.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .description(product.getDescription())
                .amount(product.getAmount())
                .image(product.getImage())
                .category_id(product.getCategory().getId())
                .supplier_id(product.getSupplier().getId())
                .image_list(imageList)
                .build();
    }
}
