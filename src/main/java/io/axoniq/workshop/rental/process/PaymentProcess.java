package io.axoniq.workshop.rental.process;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

@Saga
public class PaymentProcess {

    @Autowired
    private transient CommandGateway commandGateway;
}
