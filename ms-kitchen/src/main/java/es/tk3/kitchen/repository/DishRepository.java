package es.tk3.kitchen.repository;

import es.tk3.kitchen.model.Dish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DishRepository extends JpaRepository<Dish, Long> {

    @Query("SELECT d FROM Dish d JOIN d.preparations p WHERE p.recipe.id = :recipeId")
    List<Dish> findDishesByPreparationRecipeId(@Param("recipeId") Long recipeId);
}
