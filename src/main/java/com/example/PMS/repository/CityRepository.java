package com.example.PMS.repository;

import com.example.PMS.entity.City;
import com.example.PMS.entity.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CityRepository extends JpaRepository<City, String> {
    @Query("select c from City c " +
            "where lower(c.name) like lower(concat('%', :searchTerm, '%')) " +
            "or lower(c.id) like lower(concat('%', :searchTerm, '%')) " +
            "or lower(c.name) like lower(concat('%', :searchTerm, '%')) ")
    List<City> search(@Param("searchTerm") String searchTerm);

    Optional<City> findByName(String name);

    List<City> findByState(State state);
}
