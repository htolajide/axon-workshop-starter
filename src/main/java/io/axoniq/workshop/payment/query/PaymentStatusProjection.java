package io.axoniq.workshop.payment.query;

import io.axoniq.workshop.shared.PaymentConfirmedEvent;
import io.axoniq.workshop.shared.PaymentPreparedEvent;
import io.axoniq.workshop.shared.PaymentRejectedEvent;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

@Component
public class PaymentStatusProjection {

    private final PaymentStatusRepository paymentStatusRepository;

    public PaymentStatusProjection(PaymentStatusRepository paymentStatusRepository) {
        this.paymentStatusRepository = paymentStatusRepository;
    }
    @EventHandler
    public void on(PaymentPreparedEvent ppe) {
        var paymentStatus = new PaymentStatus(ppe.getPaymentId(), ppe.getUserId());
        this.paymentStatusRepository.save(paymentStatus);
    }
    @EventHandler
    public void on(PaymentConfirmedEvent pce) {
        this.paymentStatusRepository.findById(pce.getPaymentId()).ifPresent(
            payment -> payment.setStatus(PaymentStatus.Status.APPROVED));
    }

    @EventHandler
    public void on(PaymentRejectedEvent evt) {
        this.paymentStatusRepository.findById(evt.getPaymentId()).ifPresent(payment -> payment.setStatus(PaymentStatus.Status.REJECTED));
    }
    @QueryHandler(queryName = "getPayments")
    public Iterable<PaymentStatus> findAll() {
        return this.paymentStatusRepository.findAll();
    }
}
