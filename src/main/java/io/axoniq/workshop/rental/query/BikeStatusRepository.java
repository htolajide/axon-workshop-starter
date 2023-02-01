package io.axoniq.workshop.rental.query;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BikeStatusRepository extends JpaRepository<BikeStatus, String> {
}