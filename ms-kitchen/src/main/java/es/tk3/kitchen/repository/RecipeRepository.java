package es.tk3.kitchen.repository;

import es.tk3.kitchen.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    @Query("SELECT DISTINCT r FROM Recipe r JOIN FETCH r.ingredients i WHERE i.subRecipe.id = :subRecipeId")
    List<Recipe> findRecipesByIngredientSubRecipeId(@Param("subRecipeId") Long subRecipeId);

    @Query("SELECT DISTINCT r FROM Recipe r JOIN FETCH r.ingredients i WHERE i.rawMaterial.id = :rawMaterialId")
    List<Recipe> findRecipesByIngredientRawMaterialId(@Param("rawMaterialId") Long rawMaterialId);

}