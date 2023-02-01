package io.axoniq.workshop.rental.query;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class BikeStatus {

    @Id
    private String bikeId;
    private String renter;
    private RentalStatus status;

    public BikeStatus() {
    }

    public BikeStatus(String bikeId) {
        this.bikeId = bikeId;
        this.status = RentalStatus.AVAILABLE;
    }

    public String getBikeId() {
        return bikeId;
    }
    public String getRenter() {
        return renter;
    }

    public void requestedBy(String renter) {
        this.renter = renter;
        this.status = RentalStatus.REQUESTED;
    }

    public void rented() {
        status = RentalStatus.RENTED;
    }

    public void returned() {
        status = RentalStatus.AVAILABLE;
        renter = null;
    }

    public RentalStatus getStatus() {
        return status;
    }

    public String description() {
        switch (status) {
            case REQUESTED:
                return String.format("Bike %s was requested for rental by %s", bikeId, renter);
            case RENTED:
                return String.format("Bike %s was rented by %s", bikeId, renter);
            case AVAILABLE:
                return String.format("Bike %s is available for rental", bikeId);
           default:
                return "Status unknown";
        }
    }
}
