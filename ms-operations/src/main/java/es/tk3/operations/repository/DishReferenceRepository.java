package es.tk3.operations.repository;

import es.tk3.operations.model.Dish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DishReferenceRepository extends JpaRepository<Dish, UUID> {
    Optional<Dish> findByKitchenDishId(Long kitchenDishId);
}
