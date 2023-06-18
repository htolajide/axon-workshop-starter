package io.axoniq.workshop.rental.api;

import io.axoniq.workshop.rental.query.BikeStatus;
import io.axoniq.workshop.shared.RegisterBikeCommand;
import io.axoniq.workshop.shared.RequestBikeCommand;
import io.axoniq.workshop.shared.ReturnBikeCommand;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping("/rental")
public class BikeController {

    private static final List<String> RENTERS = Arrays.asList(
            "Allard", "Steven", "Josh", "David", "Marc", "Sara", "Milan", "Jeroen", "Marina", "Jeannot"
    );

    private final CommandGateway commandGateway;

    private final QueryGateway queryGateway;

    public BikeController(CommandGateway commandGateway, QueryGateway queryGateway) {
        this.commandGateway = commandGateway;
        this.queryGateway = queryGateway;
    }

    @PostMapping
    public CompletableFuture<Void> generateBikes(@RequestParam("bikes") int bikeCount) {
        CompletableFuture<Void> all = CompletableFuture.completedFuture(null);
        for (int i = 0; i < bikeCount; i++) {
            all = CompletableFuture.allOf(all,
                    commandGateway.send(
                            new RegisterBikeCommand(UUID.randomUUID().toString())
                    ));
        }
        return all;
    }

    @GetMapping("/bikes")
    public CompletableFuture<List<BikeStatus>> findAll() {
        return queryGateway.query("findAll", null, ResponseTypes.multipleInstancesOf(BikeStatus.class));
    }

    @GetMapping("/bikes/{bikeId}")
    public CompletableFuture<BikeStatus> findStatus(@PathVariable("bikeId") String bikeId) {
       return  queryGateway.query("findOne", bikeId, BikeStatus.class);
    }

    @PostMapping("/requestBike")
    public CompletableFuture<String> requestBike(@RequestParam("bikeId") String bikeId) {
        String renter = randomRenter();
        commandGateway.send(new RequestBikeCommand(bikeId, renter));
        return CompletableFuture.completedFuture(String.format("Bike %s request successful by %s", bikeId, renter));
    }

    @PostMapping("/returnBike")
    public CompletableFuture<String> returnBike(@RequestParam("bikeId") String bikeId) {
        commandGateway.send(new ReturnBikeCommand(bikeId));
        return CompletableFuture.completedFuture(String.format("Bike %s returned successful", bikeId));
    }
    // ...
    private String randomRenter() {
        return RENTERS.get(ThreadLocalRandom.current().nextInt(RENTERS.size()));
    }
}
