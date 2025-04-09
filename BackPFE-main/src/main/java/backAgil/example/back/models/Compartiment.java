package backAgil.example.back.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import org.antlr.v4.runtime.misc.NotNull;
@Entity
@Table(name = "compartiments")
public class Compartiment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Compartiment_ID")
    private Long id;

    @Column(name = "Capacite_Max", nullable = false)
    private double capaciteMax;

    @Column(name = "Reference", nullable = false, unique = true)
    private String reference;

    @Enumerated(EnumType.STRING)
    @Column(name = "Statut", nullable = false)
    private Statut statut; // Statut du compartiment (plein, en cours, vide)



    @ManyToOne
    @JoinColumn(name = "citerne_id")
    @JsonBackReference
    private Citerne citerne;





    public Compartiment() {}

    public enum Statut {
        PLEIN,
        EN_COURS,
        VIDE;
    }

    public Compartiment(Long id, double capaciteMax, String reference, Statut statut) {
        this.id = id;
        this.capaciteMax = capaciteMax;
        this.reference = reference;
        this.statut = statut;
    }

    public double getCapaciteMax() {
        return capaciteMax;
    }

    public void setCapaciteMax(double capaciteMax) {
        this.capaciteMax = capaciteMax;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Statut getStatut() {
        return statut;
    }

    public void setStatut(Statut statut) {
        this.statut = statut;
    }

    public Citerne getCiterne() {
        return citerne;
    }

    public void setCiterne(Citerne citerne) {
        this.citerne = citerne;
    }

    public Compartiment(Long id, double capaciteMax, String reference, Statut statut, Citerne citerne) {
        this.id = id;
        this.capaciteMax = capaciteMax;
        this.reference = reference;
        this.statut = statut;
        this.citerne = citerne;
    }

    @Override
    public String toString() {
        return "Compartiment{" +
                "id=" + id +
                ", capaciteMax=" + capaciteMax +
                ", reference='" + reference + '\'' +
                ", statut=" + statut +
                ", citerne=" + citerne +
                '}';
    }
}
