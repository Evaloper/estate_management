package com.example.PMS.repository;

import com.example.PMS.entity.City;
import com.example.PMS.entity.Phase;
import com.example.PMS.entity.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PhaseRepository extends JpaRepository<Phase, Long> {
    @Query("select c from Phase c " +
            "where lower(c.name) like lower(concat('%', :searchTerm, '%')) " +
            "or lower(c.phaseId) like lower(concat('%', :searchTerm, '%')) ")
    List<Phase> search(@Param("searchTerm") String searchTerm);


    List<Phase> findByStateAndCity(State state, City city);
    List<Phase> findByState(State state);
    List<Phase> findByCity(City city);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN TRUE ELSE FALSE END FROM Phase p WHERE p.phaseId = :phaseId")
    boolean existsByPhaseId(@Param("phaseId") String phaseId);
}
