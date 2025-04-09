package backAgil.example.back.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "citernes")
public class Citerne {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Citerne_ID")
    private Long id;
    private String reference;

    private double capacite;


    @OneToMany(mappedBy = "citerne")
    @JsonManagedReference
    private List<Compartiment> compartiments;



    @OneToOne(mappedBy = "citerne")
    private Camion camion;



    public Citerne() {}

    public Citerne(Long id, String reference, List<Compartiment> compartiments, double capacite, Camion camion) {
        this.id = id;
        this.reference = reference;
        this.compartiments = compartiments;
        this.capacite = capacite;
        this.camion = camion;
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

    public double getCapacite() {
        return capacite;
    }

    public void setCapacite(double capacite) {
        this.capacite = capacite;
    }

    public List<Compartiment> getCompartiments() {
        return compartiments;
    }

    public void setCompartiments(List<Compartiment> compartiments) {
        this.compartiments = compartiments;
    }

    @JsonIgnore
    public Camion getCamion() {
        return camion;
    }

    public void setCamion(Camion camion) {
        this.camion = camion;
    }


    @Override
    public String toString() {
        return "Citerne{" +
                "id=" + id +
                ", reference='" + reference + '\'' +
                ", capacite=" + capacite +
                ", compartiments=" + compartiments.size() + " compartiments" +
                '}';
    }

}
