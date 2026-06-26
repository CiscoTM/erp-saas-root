package es.tk3.sales.repository;

import es.tk3.sales.model.DishOperationRef;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DishOperationRefRepository extends JpaRepository<DishOperationRef, UUID> {
}
