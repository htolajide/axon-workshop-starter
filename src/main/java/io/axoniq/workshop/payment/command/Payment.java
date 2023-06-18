package io.axoniq.workshop.payment.command;

import io.axoniq.workshop.shared.*;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;
import org.axonframework.modelling.command.AggregateLifecycle;

@Aggregate
public class Payment {
    @AggregateIdentifier
    private String paymentId;
    private String userId;

    private boolean completed;
    public Payment() {
    }

    @CommandHandler
    public Payment(PreparePaymentCommand ppc) {
        AggregateLifecycle.apply(new PaymentPreparedEvent(ppc.getPaymentId(), ppc.getUserId()));
    }

    @EventSourcingHandler
    public void on(PaymentPreparedEvent ppe) {
        this.paymentId = ppe.getPaymentId();
        this.userId = ppe.getUserId();
    }

    @CommandHandler
    public void handle(ConfirmPaymentCommand cpc) {
        AggregateLifecycle.apply(new PaymentConfirmedEvent(cpc.getPaymentId()));
    }

    @EventSourcingHandler
    public void on(PaymentConfirmedEvent pce) {
        completed = true;
    }

    @CommandHandler
    public void handle(RejectPaymentCommand cmd){
        AggregateLifecycle.apply(new PaymentRejectedEvent(cmd.getPaymentId()));
    }

    @EventSourcingHandler
    public void on(PaymentRejectedEvent evt) {
        completed = false;
    }
}
