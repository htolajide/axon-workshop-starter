package io.axoniq.workshop.rental.command;

import io.axoniq.workshop.shared.BikeRegisteredEvent;
import io.axoniq.workshop.shared.RegisterBikeCommand;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

public class Bike {

}

