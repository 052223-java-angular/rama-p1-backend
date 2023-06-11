package com.revature.cookbook.repositories;

import java.util.Optional;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.revature.cookbook.entities.Recipe;
import com.revature.cookbook.entities.Review;

/**
 * The UserRepository interface provides database operations for User entities.
 */
@Repository
public interface RecipeRepository extends JpaRepository<Recipe, String> {

    /**
     * Finds a user by username.
     *
     * @param username the username to search for
     * @return an Optional containing the User object if found, or an empty Optional
     *         otherwise
     */
    Optional<Recipe> findById(String id);

    List<Recipe> findAll();

    List<Recipe> findByCusine(String cusine);

    @Query(value = "select * from recipes r where r.calories >= :lowerRange and r.calories <= :upperRange", nativeQuery=true)
    List<Recipe>findByRecipes( int lowerRange, int upperRange);
}
