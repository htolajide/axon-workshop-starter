package io.axoniq.workshop.rental.query;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BikeStatusProjection {
    private final BikeStatusRepository bikeStatusRepository;

    public BikeStatusProjection(BikeStatusRepository bikeStatusRepository) {
        this.bikeStatusRepository = bikeStatusRepository;
    }
}
