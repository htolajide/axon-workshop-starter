package io.axoniq.workshop.rental.query;

import io.axoniq.workshop.shared.*;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BikeStatusProjection {
    private final BikeStatusRepository bikeStatusRepository;

    @Autowired
    public BikeStatusProjection(BikeStatusRepository bikeStatusRepository) {
        this.bikeStatusRepository = bikeStatusRepository;
    }

    @EventHandler
    public void on(BikeRegisteredEvent bre) {
        BikeStatus bikeStatus = new BikeStatus(bre.getBikeId());
        this.bikeStatusRepository.save(bikeStatus);
    }

    @EventHandler
    public void on(BikeRequestedEvent evt) {
        this.bikeStatusRepository.findById(evt.getBikeId()).ifPresent(bike -> {
            bike.requestedBy(evt.getRenter());
                    //return bike;
        });
    }

    @EventHandler
    public void on(BikeInUseEvent bue) {
//        Optional<BikeStatus> bks = this.bikeStatusRepository.findById(bue.getBikeId());
//        if (bks.isPresent()) {
//            var bikeStatus = bks.get();
//            bikeStatus.requestedBy(bue.getRenter());
//            bikeStatus.rented();
//            return bikeStatus;
//        } else {
//            throw new IllegalStateException("Bike " + bue.getBikeId() + " not available");
//        }
        this.bikeStatusRepository.findById(bue.getBikeId())
                .map(bs -> {
                    bs.requestedBy(bue.getRenter());
                    bs.rented();
                    return bs;
                });
    }

    @EventHandler
    public void on(BikeReturnedEvent bre) {
//        Optional<BikeStatus> bks = this.bikeStatusRepository.findById(bre.getBikeId());
//        if (bks.isPresent()) {
//            var bikeStatus = bks.get();
//            bikeStatus.returned();
//        } else {
//            throw new IllegalStateException("Bike " + bre.getBikeId() + " not available");
//        }
        // update bikeStatus..
        bikeStatusRepository.findById(bre.getBikeId())
                .map(bs -> {
                    bs.returned();
                    return bs;
                });
    }

    @EventHandler
    public void on(BikeRequestRejectedEvent evt) {
        this.bikeStatusRepository.findById(evt.getBikeId()).ifPresent(BikeStatus::rejected);
    }

    @QueryHandler(queryName = "findAll")
    public List<BikeStatus> findAll() {
        return this.bikeStatusRepository.findAll();
    }

    @QueryHandler(queryName = "findOne")
    public BikeStatus findOne(String bikeId) {
        var bikeStatus = this.bikeStatusRepository.findById(bikeId);
        return bikeStatus.orElseGet(BikeStatus::new);
    }
}
