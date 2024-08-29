package buiducnhan.hutech.Online_Shopping_Store.viewmodels.order;

import buiducnhan.hutech.Online_Shopping_Store.entities.Order;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;
import java.util.stream.Collectors;

@Builder
public record OrderGetVm(Long id, String customerName, String phoneNumber, String address, List<OrderDetailDto> orderDetail) {
    public static OrderGetVm from (@NotNull Order order){
        List<OrderDetailDto> response = order.getOrderDetails().stream()
                .map(detail -> new OrderDetailDto(detail.getQuantity(), detail.getProduct().getId()))
                .collect(Collectors.toList());

        return new OrderGetVm(
                order.getId(),
                order.getCustomerName(),
                order.getPhoneNumber(),
                order.getAddress(),
                response
        );
    }
}
