package backAgil.example.back.services;

import backAgil.example.back.models.OrderDetail;
import backAgil.example.back.models.OrderInput;


import java.util.List;
import java.util.Optional;

public interface OrderDetailService {
    OrderDetail placeOrder(OrderInput orderInput);

    List<OrderDetail> getAllOrdersWithProducts();

    Optional<OrderDetail> getOrderWithProducts(Long orderId);
}
