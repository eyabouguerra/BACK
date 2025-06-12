package backAgil.example.back.servicesImpl;


import org.springframework.security.core.context.SecurityContextHolder;
import backAgil.example.back.models.Cart;
import backAgil.example.back.models.Produit;
import backAgil.example.back.models.User;
import backAgil.example.back.repositories.CartRepository;

import backAgil.example.back.repositories.ProduitRepository;
import backAgil.example.back.repositories.UserRepository;
import backAgil.example.back.services.CartService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cRepo;

    @Autowired
    private ProduitRepository pRepo;

    @Autowired
    private UserRepository uRepo;

    // ✅ Méthode utilitaire pour récupérer l'utilisateur connecté
    public User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return uRepo.findById(username).orElse(null);
    }

    @Override
    public Cart addToCart(Long id) {
        Produit produit = pRepo.findById(id).orElse(null);
        User user = getCurrentUser();

        if (produit != null && user != null) {
            Set<Produit> produits = new HashSet<>();
            produits.add(produit); // ✅ on l'ajoute dans le Set
            Cart cart = new Cart(produits, user); // ✅ maintenant compatible
            return cRepo.save(cart);
        }
        return null;
    }
    @Override
    public void cleanCart() {
        User user = getCurrentUser();
        if (user != null) {
            List<Cart> cartItems = cRepo.findByUser(user);
            cRepo.deleteAll(cartItems);
        }
    }


    @Override
    public List<Cart> getCartDetails() {
        User user = getCurrentUser();
        return (user != null) ? cRepo.findByUser(user) : List.of();
    }

    @Override
    public boolean removeFromCart(Long id) {
        User user = getCurrentUser();
        List<Cart> cartItems = cRepo.findByUser(user); // Supprimer uniquement les items du user courant

        for (Cart cartItem : cartItems) {
            for (Produit p : cartItem.getProduct()) { // getProducts(), pas getProduct()
                if (p.getId().equals(id)) {
                    cRepo.delete(cartItem); // supprime toute la ligne de panier
                    return true;
                }
            }
        }
        return false;
    }

}
