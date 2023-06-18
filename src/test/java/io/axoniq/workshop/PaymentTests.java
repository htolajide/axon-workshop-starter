package io.axoniq.workshop;

import io.axoniq.workshop.payment.command.Payment;
import io.axoniq.workshop.shared.ConfirmPaymentCommand;
import io.axoniq.workshop.shared.PaymentConfirmedEvent;
import io.axoniq.workshop.shared.PaymentPreparedEvent;
import io.axoniq.workshop.shared.PreparePaymentCommand;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class PaymentTests {

    private static final String paymentId = UUID.randomUUID().toString();

    private static final String userId = "payment-user";

    private FixtureConfiguration<Payment> fixture;

    @BeforeEach
    void setup() {
        fixture = new AggregateTestFixture<>(Payment.class);
    }

     @Test
     void shouldPreparePayment() {
         fixture.givenNoPriorActivity()
                 .when(new PreparePaymentCommand(paymentId, userId))
                 .expectEvents(new PaymentPreparedEvent(paymentId, userId));
     }

     @Test
     void shouldConfirmPayment() {
         fixture.given(new PaymentPreparedEvent(paymentId, userId))
                 .when(new ConfirmPaymentCommand(paymentId))
                 .expectEvents(new PaymentConfirmedEvent(paymentId));
     }
}