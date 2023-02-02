# Building event-driven applications using DDD, CQRS and Event Sourcing, with Axon Framework
Welcome to this workshop! You will build a "bike rental" sample application with [Axon Framework and Axon Server](https://developer.axoniq.io/).

## Installation
The following software must be installed in your local environment:

*  JDK version 11 (or higher).

*  [Axon Server](https://developer.axoniq.io/download), available to download as a standalone JAR file.

   In the Axon Server directory, create a file called `axonserver.properties` and add the following line:

         axoniq.axonserver.devmode.enabled=true
         
   Run Axon Server (@ `localhost:8024`):

      java -jar axonserver.jar
        
*  Axon Server is also as a Docker image; you can use Docker to run Axon Server by using the following command:

         docker run -d --name axonserver -p 8024:8024 -p 8124:8124 -e AXONIQ_AXONSERVER_DEVMODE_ENABLED=true axoniq/axonserver
      
   You can then start and stop Axon Server respectively by running:

         docker start axonserver
         docker stop axonserver

In addition, an IDE such as [Jetbrains IDEA](https://www.jetbrains.com/idea/) is recommended.

## Exercises
The workshop consists of a number of exercises to be completed in sequence, thus building the `rental` and 
`payment` boundary contexts.

### Exercise 1: Rental
In this exercise, the command model of the `rental` boundary context will be implemented. 

An event-sourced aggregate `Bike` (representing the domain entity "bike") accepts and validates incoming commands, 
publishing corresponding events that are persisted in the Axon Server event store.

Follow the steps outlined below.

#### Aggregate 
The (failing) `shouldRegisterBike` test in `RentalTests` attempts to send a `RegisterBikeCommand` to (a newly created) `Bike` aggregate, expecting 
a `BikeRegisteredEvent` to be published as a result. 
   
   * Annotate the `Bike` class as an aggregate via `@Aggregate`.
   
   * The aggregate needs a unique identifier; use the `@AggregateIdentifier` annotation on a `String` member field called `bikeId`.
   
   * Create a _command handling constructor_, annotated with `CommandHandler`, that accepts `RegisterBikeCommand`; also add a no-arg constructor (required).
   
   * Publish a `BikeRegisteredEvent` in the command handling constructor via the statically imported `apply` function.
   
   * Create an _event sourcing handler_ that accepts `BikeRegisteredEvent` and performs the actual state change; in this case, setting the aggregate's identifier.

> For more information on implementing aggregates, see [here](https://docs.axoniq.io/reference-guide/v/4.6/axon-framework/axon-framework-commands/modeling/aggregate).

#### Commands and Events
The remaining unit tests cover the other commands and events related to `Bike`.

Each test (uncomment to run) describes the expected commands parameters and event results. Use the Kotlin data classes `BikeRegisterCommand` and `BikeRegisteredEvent` as templates for implementing the other commands and events in the `rental` context.

Command handlers typically perform business logic to ensure that the aggregate, in a certain state, is able to process the command in question.

A command handler is commonly paired with an event sourcing handler, where actual state updates are made; these handlers are also invoked by Axon upon _sourcing events from the event store when loading an aggregate.

#### Command and Query Gateways
Commands and queries are sent from the `BikeController` using a `CommandGateway` and `QueryGateway` respectively. 

Implement the (POST) `/requestBike` and `/returnBike` endpoints by sending the corresponding commands; see the `generateBikes` method for how to use `CommandGateway`. 

Implement the (GET) `/bikes` and `/bikes/{bikeId}` endpoints by sending the corresponding queries:

*  GET `/bikes`:

   `queryGateway.send("findAll", null, ResponseTypes.multipleInstancesOf(BikeStatus.class));`

*  GET `/bikes/{bikeId}`:

   `queryGateway.send("findOne", bikeId, BikeStatus.class);`

> View the [JavaDoc](https://apidocs.axoniq.io/latest/org/axonframework/queryhandling/QueryGateway.html#query-java.lang.String-Q-org.axonframework.messaging.responsetypes.ResponseType-) for sending queries.

#### Projections
Queries are handled by a query model (a.k.a. _projections_). A projection is updated when events are published, and it implements so-called _query handlers_ that receive and respond to queries (sent via the `QueryGateway`).

A `BikeProjection` class exists that needs the following:

*  Event handlers for each of the events currently supported; an event handler is annotated with `@EventHandler` and updates bike statuses in an embedded database.

   > Don't confuse `@EventHandler` with `@EventSourcingHandler`! The latter only exists on the command-side.

   `BikeStatusRepository` offers methods for persisting (`save`) and retrieving instances (`findById`, `findAll`) of `BikeStatus`. 

   > `BikeStatus` contains methods to set relevant fields; you can find and update a particular bike status via 
   >
   >       bikeStatusRepository.findById(someBikeId).ifPresent(
   >        bikeStatus -> { 
   >           // update bikeStatus...
   >        }
   >       )`

*  Query handlers are annotated with `@QueryHandler(queryName = "someQuery")` and use `BikeStatusRepository` to retrieve and return results (that conform to what is expected in `BikeController`). 

#### Running the Application with Axon Server
Open your web browser and navigate to `localhost:8024` for the Administrator UI.

Start the Bike application; you will see the application connected to Axon Server in the _Overview_ section. 

Perform the following tasks and inspect the _Search_ (for events), _Commands_ and _Queries_ sections:

*  Register a number of bikes.

*  Get all registered bikes.

*  Request a specific bike.

*  Get the status of the requested bike.

*  Return the bike.

Close the Bike application; reset the event store in the _Settings_ section.

### Exercise 2: Payment
In this exercise, the command model of the `payment` boundary context will be implemented. 

An event-sourced aggregate `Payment` (representing the domain entity "payment") accepts and validates incoming commands, 
publishing corresponding events that are persisted in the Axon Server event store.

In addition, you will implement a _process_ which will coordinate a transaction involving both the `rental` and `payment` contexts.

Follow the steps outlined below.

#### Aggregate
Ensure that the tests in `PaymentTests` pass by:

*  Adding the required commands and events. 

*  Adding the corresponding command and event sourcing handlers in the `Payment` aggregate.

> Note: The `Payment` aggregate should have a state indicating it's "completed". 

#### Commands and Query Gateways
Inject a `CommandGateway` and `QueryGateway` in the `PaymentController`, then implement the POST `/{paymentId}/confirm` and GET `/` endpoints.

> You may be wondering why there isn't a `/{paymentId}/prepare` endpoint; theis will be covered in the _Process_ section below.

#### Projections
A `PaymentProjection` class exists that needs to be completed with event handlers for the `PaymentPreparedEvent` and `PaymentConfirmedEvent` events, as well as a query handler for the `getPayments` query.

#### Process
In order to successfully rent a bike, a payment must be made before the bike request is approved. Since this is a transaction that spans both `rental` and `payment` contexts, a _process_ (also called _saga_) must be initiated. 

Since requesting a bike now involves payment approval, the following event and command must be added:

*  `BikeRequestedEvent`

   A bike is now _not_ immediately available upon request, but is subject to payment approval. This event is published upon handling the `RequestBikeCommand`.

   > Only the `renter` state should be updated upon `BikeRequestedEvent` (the `available` state should remain `true`).

*  `ApproveBikeRequestCommand`

   If payment is approved, this new command is sent to the `Bike` aggregate. The `BikeInUseEvent` will then be published.

   > The `available` state will now be `false`.

Update existing tests to reflect the new sequence of events; ensure all tests pass (by fixing the `Bike` aggregate) before continuing.

> _Optionally_ add any other tests you deem necessary!

The payment process is implemented in `PaymentProcess`; it starts and ends upon receiving a `BikeRequestedEvent` and `PaymentConfirmedEvent` respectively.

Implement the process as follows:

*  Annotate a member function that accepts a `BikeRequestedEvent` with 

         @StartSaga
         @SagaEventHandler(associationId = "bikeId")

*  In this event handler:

   Store the bike ID as process state (simply a member field, which will be used later).

   Generate a new payment ID and associated it with the process via:

         SagaLifecycle.associatedWith("paymentId", paymentId)

   Send a `PreparePaymentCommand`. 

*  Annotate a member function that accepts a `PaymentConfirmedEvent` with

         @EndSaga
         @SagaEventHandler(associationId = "paymentId")

   Send a `ApproveBikeRequestCommand`.

> Why can't the `@EndSaga` event handler be associated with `bikeId`?

> Why didn't we need a "prepare payment" endpoint in `PaymentController`?

#### Running the Application with Axon Server
> If Axon Server is not currently running, start it again. Make sure to reset the event store.

Start the Bike application anew.

Perform the following tasks and inspect the _Search_ (for events), _Commands_ and _Queries_ sections:

*  Register a number of bikes.

*  Get all registered bikes.

*  Request a specific bike.

*  Get all payments (currently only one).

*  Confirm the payment for the bike.

Verify that the payment has been approved and the bike is now in use.

Close the Bike application; reset the event store in _Settings_.

### Exercise 3: Payment Deadline.
A payment deadline can be added to ensure that if the user doesn't confirm the payment within a specified timeframe, the bike request is rejected.

Add the following commands and events:

*  `RejectBikeRequestCommand`
*  `BikeRequestRejectedEvent`

Update the `Bike` aggregate with a command handler and an event sourcing handler (for the command and event respectively).

In `PaymentProcess`, add the following:

*  Inject the configured deadline manager:

         @AutoWired
         private transient DeadlineManager deadlineManager;

*  When the process starts, schedule a deadline:

         // deadlineId is a member field.
         deadlineId = deadlineManager.schedule(Duration.of(30, ChronoUnit.SECONDS), "paymentDeadline");

*  When the process ends (successfully), cancel the deadline:

         deadlineManager.cancelSchedule(deadlineId, "paymentDeadline");

*  If the deadline is reached, the bike request is rejected:

         @DeadlineHandler(deadlineName = "paymentDeadline")
         public void handle() {
            // send RejectBikeRequestCommand.  
            SagaLifecycle.end();
         }

Finally, configure a new deadline manager in `BikeApplication`:

      @Bean
      public DeadlineManager deadlineManager(Configuration configuration) {
         return SimpleDeadlineManager.builder()
               .scopeAwareProvider(new ConfigurationScopeAwareProvider(configuration))
               .build();
      }

#### Running the Application with Axon Server
> If Axon Server is not currently running, start it again. Make sure to reset the event store.

Start the Bike application anew.

Perform the following tasks and inspect the _Search_ (for events), _Commands_ and _Queries_ sections:

*  Register a number of bikes.

*  Get all registered bikes.

*  Request a specific bike.

*  Let the timeframe elapse (default 30s).

*  Confirm that the bike request has been rejected.

Also ensure that a bike request with a confirmed payment within the timeframe does indeed succeed.

Close the Bike application; reset the event store in _Settings_.
