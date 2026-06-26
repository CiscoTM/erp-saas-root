package es.tk3.operations.repository;

import es.tk3.operations.model.FunctionSheet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FunctionSheetRepository extends JpaRepository<FunctionSheet, UUID> {

    Optional<FunctionSheet>findByBookingId(Long bookingId);

    @Query("SELECT fs FROM FunctionSheet fs JOIN fs.details d WHERE d.dish.id = :dishId AND fs.status = 'ACTIVE'")
    List<FunctionSheet> findAllActiveSheetsByDishId(@Param("dishId") UUID dishId);
}
