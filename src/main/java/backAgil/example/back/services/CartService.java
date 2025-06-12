package backAgil.example.back.services;

import backAgil.example.back.models.Cart;
import backAgil.example.back.models.Produit;
import backAgil.example.back.models.User;

import java.util.List;

public interface CartService {
    Cart addToCart(Long id);

    List<Cart> getCartDetails();

    boolean removeFromCart(Long id);
    User getCurrentUser();
    void cleanCart();

}
