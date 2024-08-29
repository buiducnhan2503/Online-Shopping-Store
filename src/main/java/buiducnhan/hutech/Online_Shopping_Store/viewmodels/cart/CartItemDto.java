package buiducnhan.hutech.Online_Shopping_Store.viewmodels.cart;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class CartItemDto {
    private final Long productId;
    private final String productName;
    private final int quantity;
    private final double price;
}
