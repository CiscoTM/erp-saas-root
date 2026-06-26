package es.tk3.operations.repository;

import es.tk3.operations.model.CommercialMenuRecipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommercialMenuRecipeRepository extends JpaRepository<CommercialMenuRecipe, Long> {
    List<CommercialMenuRecipe> findByCommercialMenuId(String commercialMenuId);
    List<CommercialMenuRecipe> findByDishId(UUID dishId);
}
