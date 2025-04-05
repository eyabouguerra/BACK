package backAgil.example.back.models;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "commandes")
public class Commande {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Commande_ID")
    private Long id;

    private int numero;
    private Float quantite;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date dateCommande;

    private Float price;

    private Float totalPrice;


    @ManyToMany
    @JoinTable(name = "commande_produits",
            joinColumns = @JoinColumn(name = "commande_id"),
            inverseJoinColumns = @JoinColumn(name = "produit_id"))
    private List<Produit> produits;


    public Commande() {
    }

    public Commande(Long id, Float quantite, int numero, Date dateCommande, Float price, Float totalPrice, List<Produit> produits) {
        this.id = id;
        this.quantite = quantite;
        this.numero = numero;
        this.dateCommande = dateCommande;
        this.price = price;
        this.totalPrice = totalPrice;
        this.produits = produits;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public Float getQuantite() {
        return quantite;
    }

    public void setQuantite(Float quantite) {
        this.quantite = quantite;
    }

    public Date getDateCommande() {
        return dateCommande;
    }

    public void setDateCommande(Date dateCommande) {
        this.dateCommande = dateCommande;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Float getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Float totalPrice) {
        this.totalPrice = totalPrice;
    }

    public List<Produit> getProduits() {
        return produits;
    }

    public void setProduits(List<Produit> produits) {
        this.produits = produits;
    }

    @Override
    public String toString() {
        return "Commande{" +
                "id=" + id +
                ", numero=" + numero +
                ", quantite=" + quantite +
                ", dateCommande=" + dateCommande +
                ", price=" + price +
                ", totalPrice=" + totalPrice +
                ", produits=" + produits +
                '}';
    }
}