package com.example.PMS.repository;

import com.example.PMS.entity.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StateRepository extends JpaRepository<State, Long> {
    @Query("select c from State c " +
            "where lower(c.name) like lower(concat('%', :searchTerm, '%')) " +
            "or lower(c.stateId) like lower(concat('%', :searchTerm, '%'))")
    List<State> search(@Param("searchTerm") String searchTerm);

    Optional<State> findByName(String name);
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN TRUE ELSE FALSE END FROM State s WHERE s.stateId = :stateId")
    boolean existsByStateId(@Param("stateId")String stateId);
}
