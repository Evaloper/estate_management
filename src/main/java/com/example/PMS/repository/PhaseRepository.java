package com.example.PMS.repository;

import com.example.PMS.entity.City;
import com.example.PMS.entity.Phase;
import com.example.PMS.entity.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PhaseRepository extends JpaRepository<Phase, String> {
    @Query("select c from Phase c " +
            "where lower(c.name) like lower(concat('%', :searchTerm, '%')) " +
            "or lower(c.id) like lower(concat('%', :searchTerm, '%')) " +
            "or lower(c.state) like lower(concat('%', :searchTerm, '%')) " +
            "or lower(c.city) like lower(concat('%', :searchTerm, '%')) ")
    List<Phase> search(@Param("searchTerm") String searchTerm);


    List<Phase> findByStateAndCity(State state, City city);
    List<Phase> findByState(State state);
    List<Phase> findByCity(City city);
}
