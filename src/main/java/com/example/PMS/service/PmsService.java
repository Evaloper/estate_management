package com.example.PMS.service;

import com.example.PMS.entity.City;
import com.example.PMS.entity.Phase;
import com.example.PMS.entity.State;
import com.example.PMS.repository.CityRepository;
import com.example.PMS.repository.PhaseRepository;
import com.example.PMS.repository.StateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PmsService {
    private final StateRepository stateRepository;
    private final CityRepository cityRepository;
    private final PhaseRepository phaseRepository;

    public PmsService(StateRepository stateRepository,
                      CityRepository cityRepository,
                      PhaseRepository phaseRepository) {
        this.stateRepository = stateRepository;
        this.cityRepository = cityRepository;
        this.phaseRepository = phaseRepository;
    }

    public List<State> findAllStates(String stringFilter) {
        if (stringFilter == null || stringFilter.isEmpty()) {
            return stateRepository.findAll();
        } else {
            return stateRepository.search(stringFilter);
        }
    }


    public List<City> findAllCities(String stringFilter) {
        if (stringFilter == null || stringFilter.isEmpty()) {
            return cityRepository.findAll();
        } else {
            return cityRepository.search(stringFilter);
        }
    }

    public List<Phase> findAllPhases(String stringFilter) {
        if (stringFilter == null || stringFilter.isEmpty()) {
            return phaseRepository.findAll();
        } else {
            return phaseRepository.search(stringFilter);
        }
    }

    public long countStates() {
        return stateRepository.count();
    }

    public void deleteState(State state) {
        stateRepository.delete(state);
    }

    @Transactional
    public void saveState(State state) {
        if (state == null || state.getStateId() == null) {
            System.err.println("State or State ID is null. Ensure the form is connected and ID is assigned.");
            return;
        }
        stateRepository.save(state);
    }


    private String generateUniqueId() {
        // You can use UUID, a random string, or any logic you prefer
        return String.valueOf(System.currentTimeMillis());
    }


    public long countCity() {
        return cityRepository.count();
    }

    public void deleteCity(City city) {
        cityRepository.delete(city);
    }

    @Transactional
    public void saveCity(City city) {
        if (city == null || city.getName() == null || city.getState() == null) {
            throw new IllegalArgumentException("City or mandatory fields (name, state) are null.");
        }
        if (city.getId() == null || city.getId().trim().isEmpty()) {
            city.setId(generateUniqueId());
        }
        cityRepository.save(city);
    }



    public long countPhase() {
        return phaseRepository.count();
    }

    public void deletePhase(Phase phase) {
        phaseRepository.delete(phase);
    }

    public void savePhase(Phase phase) {
        if (phase == null) {
            System.err.println("Contact is null. Are you sure you have connected your form to the application?");
            return;
        }
        phaseRepository.save(phase);
    }

    public List<Phase> findAllPhase() {
        return phaseRepository.findAll();
    }

    public List<City> findAllCities(){
        return cityRepository.findAll();
    }

    public boolean stateExistsById(String stateId) {
        return stateRepository.existsByStateId(stateId);
    }
    public boolean stateExistsByName(String name) {
        return stateRepository.findByName(name).isPresent();
    }

    public boolean cityExistsById(String phaseId) {
        return cityRepository.existsById(phaseId);
    }

    public boolean cityExistsByName(String name) {
        return cityRepository.findByName(name).isPresent();
    }
    public boolean phaseExistsById(String phaseId) {
        return phaseRepository.existsById(phaseId);
    }


    public List<Phase> findPhasesByStateAndCity(State state, City city) {
        return phaseRepository.findByStateAndCity(state, city);
    }

    public List<Phase> findPhasesByState(State state) {
        return phaseRepository.findByState(state);
    }

    public List<Phase> findPhasesByCity(City city) {
        return phaseRepository.findByCity(city);
    }

    public List<City> findCitiesByState(Long city) {
        State state = findStateById(city);
        return cityRepository.findByState(state);
    }

    private State findStateById(Long stateId){
        Optional<State> stateOptional = stateRepository.findById(stateId);
        if (stateOptional.isPresent()) {
            return stateOptional.get();
        } else {
            throw new IllegalArgumentException("State with ID " + stateId + " not found");
        }
    }
}
