package io.axoniq.workshop.payment.query;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentStatusRepository extends CrudRepository<PaymentStatus, String> {}
