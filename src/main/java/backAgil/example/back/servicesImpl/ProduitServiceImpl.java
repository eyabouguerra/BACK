package backAgil.example.back.servicesImpl;

import backAgil.example.back.models.Cart;
import backAgil.example.back.models.Produit;
import backAgil.example.back.models.User;
import backAgil.example.back.repositories.CartRepository;
import backAgil.example.back.repositories.ProduitRepository;
import backAgil.example.back.services.CartService;
import backAgil.example.back.services.ProduitService; // Importez l'interface ProduitService
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProduitServiceImpl implements ProduitService { // Implémentez l'interface ProduitService
    @Autowired
    private ProduitRepository pRepo;
    @Autowired
    private CartService cartService;
    /*@Autowired
    private UserRepository uRepo;
     */
    @Autowired
    private CartRepository cRepo;

    @Override
    public List<Produit> getAllProduits() {
        return pRepo.findAll();
    }

    @Override
    public Produit getProduitById(Long id) {
        return pRepo.findById(id).orElse(null);
    }

    @Override
    public void deleteProduitById(Long id) {
        pRepo.deleteById(id);
    }

    @Override
    public Produit addProduit(Produit p) {
        try {
            return pRepo.save(p);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'ajout du produit: " + e.getMessage(), e);
        }
    }


    @Override
    public Produit editProduit(Produit p) {
        if (p.getId() == null) {
            throw new IllegalArgumentException("L'ID du produit ne doit pas être null.");
        }

        Produit existingProduit = pRepo.findById(p.getId())
                .orElseThrow(() -> new RuntimeException("Produit avec ID " + p.getId() + " introuvable"));

        // Mise à jour des champs sans affecter le typeProduit
        existingProduit.setCodeProduit(p.getCodeProduit());
        existingProduit.setNomProduit(p.getNomProduit());
        existingProduit.setLibelle(p.getLibelle());
        existingProduit.setPrix(p.getPrix());
        existingProduit.setDate(p.getDate());
        existingProduit.setDescription(p.getDescription());

        // Ne modifie pas typeProduit si le champ est nul
        if (p.getTypeProduit() != null) {
            existingProduit.setTypeProduit(p.getTypeProduit());
        }

        return pRepo.save(existingProduit);
    }

    public List<Produit> getProduitsByType(Long typeId) {

        return pRepo.findByTypeProduit_Id(typeId);
    }

    /*public List<Produit> getProductDetails(boolean isSingleProductCheckout,Long id){
        if(isSingleProductCheckout) {
            List<Produit> list = new ArrayList<>();
            Produit product = pRepo.findById(id).get();
            list.add(product);
            return list;
        }else {
        String username = JWTRequestFilter.CURRENT_USER.
        User user = uRepo.findById(username).get();

        List<Cart> carts = cRepo.findByUser(user);
            return carts.stream().map(x -> x.getProduit().collect(Collectors.toList()));
            List<Produit> produits = cRepo.findAll()  // Get all Cart objects
                    .stream()
                    .map(cart -> cart.getProduct())  // Map to the Produit of each Cart
                    .collect(Collectors.toList());  // Collect the results into a list
            return produits;


        }
    }*/

    public List<Produit> getProductDetails(boolean isSingleProductCheckout, Long id) {
        if (isSingleProductCheckout) {
            Produit product = pRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Produit non trouvé"));
            return List.of(product);
        } else {
            User user = cartService.getCurrentUser();  // récupérer user via CartService
            if (user == null) {
                return List.of();  // ou gérer l'erreur
            }
            List<Cart> carts = cRepo.findByUser(user);
            return carts.stream()
                    .flatMap(cart -> cart.getProduct().stream())
                    .collect(Collectors.toList());
        }
    }



@Override
    public Produit getProductById(Long id) {
        return pRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + id));
    }

}
