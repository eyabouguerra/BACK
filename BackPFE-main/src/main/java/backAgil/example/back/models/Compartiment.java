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

    @Column(name = "Reference", nullable = false, unique = true)  // Ajout de la colonne Reference
    private String reference;

    @Enumerated(EnumType.STRING)
    @Column(name = "Statut", nullable = false)
    private Statut statut; // Statut du compartiment (plein, en cours, vide)



    @ManyToOne
    @JsonBackReference // Utilisation de JsonBackReference pour éviter une boucle infinie de sérialisation
    @JoinColumn(name = "citerne_id", nullable = false) // Citerne est obligatoire pour un compartiment
    private Citerne citerne;



    public Compartiment() {}


    public enum Statut {
        PLEIN,
        EN_COURS,
        VIDE;
    }

    public Compartiment(Long id, double capaciteMax,  String reference, Statut statut, Citerne citerne) {
        this.id = id;
        this.capaciteMax = capaciteMax;
        this.statut = statut;
        this.citerne = citerne;
        this.reference = reference;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getCapaciteMax() {
        return capaciteMax;
    }

    public void setCapaciteMax(double capaciteMax) {
        this.capaciteMax = capaciteMax;
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

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }


    @Override
    public String toString() {
        return "Compartiment{" +
                "id=" + id +
                ", capaciteMax=" + capaciteMax +
                ", statut=" + statut +
                ", citerne=" + citerne +
                ", reference='" + reference +
                '}';
    }
}