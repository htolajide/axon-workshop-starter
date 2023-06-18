package io.axoniq.workshop.shared

import io.axoniq.workshop.payment.command.Payment
import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.lang.Exception
import java.sql.Timestamp
import java.time.ZonedDateTime

// ...
// Bounded Context: Rental.
data class RegisterBikeCommand(@TargetAggregateIdentifier val bikeId: String)

data class BikeRegisteredEvent(val bikeId: String)

data class RequestBikeCommand(@TargetAggregateIdentifier val bikeId: String, val renter: String)

data class BikeRequestedEvent(val bikeId: String, val renter: String)

data class BikeInUseEvent(val bikeId: String, val renter: String)

data class ReturnBikeCommand(@TargetAggregateIdentifier val bikeId: String)

data class BikeReturnedEvent(val bikeId: String)
data class ApproveBikeRequestCommand(@TargetAggregateIdentifier val bikeId: String)

data class BikeRequestApprovedEvent(val bikeId: String)

data class RejectBikeRequestCommand(@TargetAggregateIdentifier val bikeId: String)

data class BikeRequestRejectedEvent(val bikeId: String)


// ...
// Bounded Context: Payment.

data class PreparePaymentCommand(@TargetAggregateIdentifier val paymentId: String, val userId: String)

data class PaymentPreparedEvent(val paymentId: String, val userId: String)

data class ConfirmPaymentCommand(@TargetAggregateIdentifier val paymentId: String)
data class PaymentConfirmedEvent(val paymentId: String)

data class RejectPaymentCommand(@TargetAggregateIdentifier val paymentId: String)

data class PaymentRejectedEvent(val paymentId: String)
 data class ErrorMessage(val statusCode : Number, val exception: String, val message: String, val timestamp: ZonedDateTime)
