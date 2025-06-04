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

    private Float price;

    private Float totalPrice;

    @Enumerated(EnumType.STRING)
    private Commande.StatutCommande statut;

    public enum StatutCommande {
        PLANNIFIER, LIVRE, EN_ATTENTE
    }
    @ManyToOne(fetch = FetchType.EAGER) // Changé de LAZY à EAGER pour charger le client
    @JoinColumn(name = "client_id")
    // Suppression de @JsonBackReference pour permettre la sérialisation du client
    private Client client;

    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<CommandeProduit> commandeProduits;

    // Constructors
    public Commande() {
    }

    public Commande(Long id, String codeCommande, Date dateCommande, Float price, Float totalPrice, StatutCommande statut, Client client, List<CommandeProduit> commandeProduits) {
        this.id = id;
        this.codeCommande = codeCommande;

        this.dateCommande = dateCommande;
        this.price = price;
        this.totalPrice = totalPrice;
        this.statut = statut;
        this.client = client;
        this.commandeProduits = commandeProduits;
    }

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



    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Date getDateCommande() {
        return dateCommande;
    }

    public void setDateCommande(Date dateCommande) {
        this.dateCommande = dateCommande;
    }

    public Float getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Float totalPrice) {
        this.totalPrice = totalPrice;
    }

    public StatutCommande getStatut() {
        return statut;
    }

    public void setStatut(StatutCommande statut) {
        this.statut = statut;
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

    @Override
    public String toString() {
        return "Commande{" +
                "id=" + id +
                ", codeCommande='" + codeCommande + '\'' +

                ", dateCommande=" + dateCommande +
                ", price=" + price +
                ", totalPrice=" + totalPrice +
                ", statut=" + statut +
                ", client=" + client +
                ", commandeProduits=" + commandeProduits +
                '}';
    }
}