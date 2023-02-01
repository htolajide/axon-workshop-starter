package io.axoniq.workshop;

import io.axoniq.workshop.rental.command.Bike;
import io.axoniq.workshop.shared.BikeRegisteredEvent;
import io.axoniq.workshop.shared.RegisterBikeCommand;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class RentalTests {

    private static final String bikeId = UUID.randomUUID().toString();

    private static final String renter = "myuser";

    private FixtureConfiguration<Bike> fixture;

    @BeforeEach
    void setup() {
        fixture = new AggregateTestFixture<>(Bike.class);
    }

    @Test
    void shouldRegisterBike() {
        fixture.givenNoPriorActivity()
                .when(new RegisterBikeCommand(bikeId))
                .expectEvents(new BikeRegisteredEvent(bikeId));
    }

    // @Test
    // void shouldRequestBike() {
    // 	fixture.given(new BikeRegisteredEvent(bikeId))
    // 			.when(new RequestBikeCommand(bikeId, renter))
    // 			.expectEvents(new BikeInUseEvent(bikeId, renter));
    // }

    // @Test
    // void shouldNotRequestUnavailableBike() {
    // 	fixture.given(new BikeRegisteredEvent(bikeId), new BikeInUseEvent(bikeId, renter))
    // 			.when(new RequestBikeCommand(bikeId, renter))
    // 			.expectException(IllegalStateException.class);
    // }

    // @Test
    // void shouldReturnBike() {
    // 	fixture.given(new BikeRegisteredEvent(bikeId), new BikeInUseEvent(bikeId, renter))
    // 			.when(new ReturnBikeCommand(bikeId))
    // 			.expectEvents(new BikeReturnedEvent(bikeId));
    // }

    // @Test
    // void shouldNotReturnAvailableBike() {
    // 	fixture.given(
    // 					new BikeRegisteredEvent(bikeId),
    // 					new BikeInUseEvent(bikeId, renter),
    // 					new BikeReturnedEvent(bikeId)
    // 			)
    // 			.when(new ReturnBikeCommand(bikeId))
    // 			.expectException(IllegalStateException.class);
    // }
}
