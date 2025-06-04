package backAgil.example.back.models;


import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "cart")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cartId")
    private Long id;
    @ManyToMany
    @JoinTable(
            name = "cart_products",
            joinColumns = @JoinColumn(name = "cart_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private Set<Produit> product = new HashSet<>();
    @ManyToOne
    @JoinColumn(name = "user_name") // FK vers User.userName
    private User user;

    //Quand je fait user rejenerer tout******///////


    public Cart() {

    }

    public Cart(Set<Produit> product, User user) {
        this.product = product;
        this.user = user;
    }

    public Set<Produit> getProduct() {
        return product;
    }

    public void setProduct(Set<Produit> product) {
        this.product = product;
    }

    public Cart(Set<Produit> product) {
        this.product = product;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Cart{" +
                "id=" + id +
                ", product=" + product +
                '}';
    }
}
