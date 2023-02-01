package io.axoniq.workshop.payment.query;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class PaymentStatus {
    @Id
    private String id;

    private Status status;
    private String userId;

    public PaymentStatus() {
    }

    public PaymentStatus(String id, String userId) {
        this.id = id;
        this.userId = userId;
        this.status = Status.PENDING;
    }

    public String getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public enum Status {

        PENDING, APPROVED, REJECTED
    }
}
