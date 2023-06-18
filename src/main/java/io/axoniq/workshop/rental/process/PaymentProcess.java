package io.axoniq.workshop.rental.process;

import io.axoniq.workshop.shared.*;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.deadline.DeadlineManager;
import org.axonframework.deadline.annotation.DeadlineHandler;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.SagaLifecycle;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Saga
public class PaymentProcess {
    private String bikeId;
    private String deadlineId;
    private String paymentId;
    @Autowired
    private transient CommandGateway commandGateway;
    @Autowired
    private transient DeadlineManager deadlineManager;

    @StartSaga
    @SagaEventHandler(associationProperty = "bikeId")
    public void on(BikeRequestedEvent bre) {
        this.bikeId = bre.getBikeId();
        String paymentId = UUID.randomUUID().toString();
        this.paymentId = paymentId;
        // deadlineId is a member field.
        deadlineId = deadlineManager.schedule(Duration.of(30, ChronoUnit.SECONDS), "paymentDeadline");
        SagaLifecycle.associateWith("paymentId", paymentId);
        commandGateway.send(new PreparePaymentCommand(paymentId, bre.getRenter()));
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "paymentId")
    public void on(PaymentConfirmedEvent pce) {
        deadlineManager.cancelSchedule(deadlineId, "paymentDeadline");
        commandGateway.send(new ApproveBikeRequestCommand(this.bikeId));
    }

    @DeadlineHandler(deadlineName = "paymentDeadline")
    public void handle() {
        // send RejectBikeRequestCommand.
        commandGateway.send(new RejectBikeRequestCommand(bikeId));
        commandGateway.send(new RejectPaymentCommand(paymentId));
        SagaLifecycle.end();
    }
}
