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
        if (state == null) {
            throw new IllegalArgumentException("State is null. Ensure the form is connected and State object is properly initialized.");
        }

        // If the state has an ID, we update the existing entity.
        // If the state does not have an ID, it means we're creating a new one.
        if (state.getId() != null) {
            Optional<State> existingState = stateRepository.findById(state.getId());
            if (existingState.isPresent()) {
                // Update existing state entity
                State stateToUpdate = existingState.get();
                stateToUpdate.setName(state.getName());
                stateToUpdate.setStateId(state.getStateId());
                stateToUpdate.setCities(state.getCities());
                stateToUpdate.setPhases(state.getPhases());
                stateRepository.save(stateToUpdate);
            } else {
                throw new IllegalArgumentException("State with ID " + state.getId() + " does not exist in the database.");
            }
        } else {
            // Create a new state entity
            stateRepository.save(state);
        }
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
        if (city.getCityId() == null || city.getCityId().trim().isEmpty()) {
            city.setCityId(generateUniqueId());
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

    public boolean cityExistsById(String cityId) {
        return cityRepository.existsByCityId(cityId);
    }

    public boolean cityExistsByName(String name) {
        return cityRepository.findByName(name).isPresent();
    }
    public boolean phaseExistsById(String phaseId) {
        return phaseRepository.existsByPhaseId(phaseId);
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
