package buiducnhan.hutech.Online_Shopping_Store.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CartItem {
    private Product product;
    private int quantity;
}
