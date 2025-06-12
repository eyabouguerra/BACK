package backAgil.example.back.servicesImpl;

import backAgil.example.back.models.*;
import backAgil.example.back.repositories.CartRepository;
import backAgil.example.back.repositories.ClientRepository;
import backAgil.example.back.repositories.OrderDetailRepository;
import backAgil.example.back.repositories.ProduitRepository;
import backAgil.example.back.services.OrderDetailService;
import backAgil.example.back.services.ProduitService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderDetailServiceImpl implements OrderDetailService {


    private static final Logger logger = LoggerFactory.getLogger(OrderDetailServiceImpl.class);
    private static final String ORDER_PLACED = "Placed";

    @Autowired
    private OrderDetailRepository orderRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProduitService produitService;

    @Transactional
    @Override
    public OrderDetail placeOrder(OrderInput orderInput) {
        logger.info("Placing order for: {}", orderInput.getFullName());

        // Validate input
        if (orderInput.getFullName() == null || orderInput.getFullAddress() == null || orderInput.getContactNumber() == null) {
            logger.warn("Invalid order input: missing required fields");
            throw new IllegalArgumentException("Full name, address, and contact number are required");
        }

        // Check if client already exists
        Client client = clientRepository.findByFullName(orderInput.getFullName())
                .orElseGet(() -> {
                    // Create new client if not exists
                    Client newClient = new Client();
                    newClient.setFullName(orderInput.getFullName());
                    newClient.setFullAddress(orderInput.getFullAddress());
                    newClient.setContactNumber(orderInput.getContactNumber());
                    logger.info("Creating new client: {}", newClient.getFullName());
                    return clientRepository.save(newClient);
                });

        // If client exists but details are different, update them
        if (!client.getFullAddress().equals(orderInput.getFullAddress()) ||
                !client.getContactNumber().equals(orderInput.getContactNumber())) {
            client.setFullAddress(orderInput.getFullAddress());
            client.setContactNumber(orderInput.getContactNumber());
            client = clientRepository.save(client);
            logger.info("Updated client details for: {}", client.getFullName());
        }

        List<OrderDetail> orders = new ArrayList<>();
        float totalAmount = 0;

        // Process multiple products
        for (OrderProductQuantity productQuantity : orderInput.getOrderProductQuantityList()) {
            if (productQuantity.getQuantity() <= 0) {
                logger.warn("Invalid quantity for product ID {}: {}", productQuantity.getId(), productQuantity.getQuantity());
                continue;
            }

            Produit product = produitService.getProductById(productQuantity.getId());
            if (product == null) {
                logger.warn("Product not found: {}", productQuantity.getId());
                continue;
            }

            OrderDetail order = new OrderDetail();
            order.setOrderFullName(orderInput.getFullName());
            order.setOrderFullOrder(orderInput.getFullAddress());
            order.setOrderContactNumber(orderInput.getContactNumber());
            order.setOrderStatuts(ORDER_PLACED);
            order.setClient(client);

            float productTotal = product.getPrix() * productQuantity.getQuantity();
            order.setOrderAmount(productTotal);
            totalAmount += productTotal;

            orders.add(orderRepository.save(order));
            logger.info("Order created for product ID {} with quantity {}, amount: {}",
                    product.getId(), productQuantity.getQuantity(), productTotal);
        }

        // Clear the cart
        cartRepository.deleteAll();
        logger.info("Cart cleared after placing order for client: {}", client.getFullName());

        return orders.isEmpty() ? null : orders.get(0);
    }

    @Override
    public List<OrderDetail> getAllOrdersWithProducts() {
        return orderRepository.findAll();
    }

    @Override
    public Optional<OrderDetail> getOrderWithProducts(Long orderId) {
        return orderRepository.findById(orderId);
    }

}
