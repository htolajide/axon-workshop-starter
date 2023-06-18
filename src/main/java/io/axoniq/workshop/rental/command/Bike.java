package io.axoniq.workshop.rental.command;

import io.axoniq.workshop.shared.*;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate
public class Bike {
    @AggregateIdentifier
    private String bikeId;

    private String renter;
    private boolean available;

    public Bike(){}
    @CommandHandler
    protected Bike(RegisterBikeCommand cmd){
        apply(new BikeRegisteredEvent(cmd.getBikeId()));
    }

    @CommandHandler
    public void handle(RequestBikeCommand cmd){
        if (renter != null) {
            throw new IllegalStateException("Bike has already been requested by another user");
        }

        if (!available) {
            throw new IllegalStateException("Bike already in use");
        }
        apply(new BikeRequestedEvent(cmd.getBikeId(), cmd.getRenter()));
    }

    @EventSourcingHandler
    public void on(BikeRequestedEvent bre) {
        this.renter = bre.getRenter();
    }

    @CommandHandler
    public void handle(ReturnBikeCommand rbc) {
        if (this.available) {
            throw new IllegalStateException("This bike is already returned");
        }
        apply(new BikeReturnedEvent(rbc.getBikeId()));
    }

    @EventSourcingHandler
    public void on(BikeInUseEvent bie) {
        this.available = false;
    }

    @EventSourcingHandler
    public void on(BikeRegisteredEvent bre){

        this.bikeId = bre.getBikeId();
        this.available = true;
    }

    @EventSourcingHandler
    public void on(BikeReturnedEvent bre) {
        this.renter = null;
        this.available = true;
    }

    @CommandHandler
    public void handle(ApproveBikeRequestCommand abc) {
        apply(new BikeInUseEvent(abc.getBikeId(), this.renter));
    }

    @CommandHandler
    public void handle(RejectBikeRequestCommand cmd) {
        apply(new BikeRequestRejectedEvent(cmd.getBikeId()));
    }

    @EventSourcingHandler
    public void on(BikeRequestRejectedEvent evt){
        this.bikeId = evt.getBikeId();
    }
}

