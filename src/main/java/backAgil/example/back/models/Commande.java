package backAgil.example.back.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    private String codeCommande;


    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date dateCommande;

    @Enumerated(EnumType.STRING)
    private Commande.StatutCommande statut;

    public enum StatutCommande {
        PLANNIFIER, LIVRE, EN_ATTENTE, EN_COURS
    }

    private Float price;

    private Float totalPrice;

    @ManyToOne(fetch = FetchType.EAGER) // Changé de LAZY à EAGER pour charger le client
    @JoinColumn(name = "client_id")
    private Client client;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<CommandeProduit> commandeProduits;

    // Constructors
    public Commande() {
    }

    public Commande(String codeCommande,Float price, Date dateCommande, Float totalPrice, Client client, List<CommandeProduit> commandeProduits) {
        this.codeCommande = codeCommande;

        this.price = price;
        this.dateCommande = dateCommande;
        this.totalPrice = totalPrice;
        this.client = client;
        this.commandeProduits = commandeProduits;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodeCommande() {
        return codeCommande;
    }

    public void setCodeCommande(String codeCommande) {
        this.codeCommande = codeCommande;
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

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public List<CommandeProduit> getCommandeProduits() {
        return commandeProduits;
    }

    public void setCommandeProduits(List<CommandeProduit> commandeProduits) {
        this.commandeProduits = commandeProduits;
    }

    public StatutCommande getStatut() {
        return statut;
    }

    public void setStatut(StatutCommande statut) {
        this.statut = statut;
    }

    @Override
    public String toString() {
        return "Commande{" +
                "id=" + id +
                ", codeCommande='" + codeCommande + '\'' +

                ", dateCommande=" + dateCommande +
                ", price=" + price +
                ", totalPrice=" + totalPrice +
                ", client=" + client +
                ", commandeProduits=" + commandeProduits +
                '}';
    }
}