package backAgil.example.back.models;

import jakarta.persistence.*;

@Entity
@Table(name = "clients")
public class Client {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long clientId;

    @Column
    private String fullName;
    @Column
    private String fullAddress;
    @Column
    private String AlternateContactNumber;
    @Column
    private String contactNumber;
    @Column
    private Double latitude;

    @Column
    private Double longitude;


    public String getAlternateContactNumber() {
        return AlternateContactNumber;
    }

    public Client() {

    }

    public void setAlternateContactNumber(String alternateContactNumber) {
        AlternateContactNumber = alternateContactNumber;
    }

    // Getters and Setters
    public Long getClientId() { return clientId; }
    public void setClientId(Long clientId) { this.clientId = clientId; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getFullAddress() { return fullAddress; }
    public void setFullAddress(String fullAddress) { this.fullAddress = fullAddress; }
    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Client(Long clientId, String fullName, String fullAddress, String contactNumber, Double latitude, Double longitude) {
        this.clientId = clientId;
        this.fullName = fullName;
        this.fullAddress = fullAddress;
        this.contactNumber = contactNumber;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Client(Long clientId, String fullName, String fullAddress, String contactNumber) {
        this.clientId = clientId;
        this.fullName = fullName;
        this.fullAddress = fullAddress;
        this.contactNumber = contactNumber;
    }

    @Override
    public String toString() {
        return "Client{" +
                "clientId=" + clientId +
                ", fullName='" + fullName + '\'' +
                ", fullAddress='" + fullAddress + '\'' +
                ", contactNumber='" + contactNumber + '\'' +
                '}';
    }
}