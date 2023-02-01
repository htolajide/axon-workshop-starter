package io.axoniq.workshop.shared

import org.axonframework.modelling.command.TargetAggregateIdentifier

// ...
// Bounded Context: Rental.
data class RegisterBikeCommand(@TargetAggregateIdentifier val bikeId: String)

data class BikeRegisteredEvent(val bikeId: String)

// ...
// Bounded Context: Payment.