package buiducnhan.hutech.Online_Shopping_Store.viewmodels.order;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class OrderDetailDto {
    private final int quantity;
    private final Long productId;
}
