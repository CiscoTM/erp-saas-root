package es.tk3.operations.repository;

import es.tk3.operations.entities.FunctionSheet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FunctionSheetRepository extends JpaRepository<FunctionSheet, Long> {
    Optional<FunctionSheet>findByBookingId(Long bookingId);
}
