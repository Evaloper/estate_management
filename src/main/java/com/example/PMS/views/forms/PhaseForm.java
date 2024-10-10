package com.example.PMS.views.forms;

import com.example.PMS.entity.City;
import com.example.PMS.entity.Phase;
import com.example.PMS.entity.State;
import com.example.PMS.service.PmsService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;

import java.util.Collections;
import java.util.List;

public class PhaseForm extends FormLayout {
    ComboBox<State> states = new ComboBox<>("State");
    ComboBox<City> cities = new ComboBox<>("City");
    TextField name = new TextField("Phase");
    TextField phaseId = new TextField("ID");

    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button close = new Button("Cancel");

    private Phase phase;

    Binder<Phase> binder = new BeanValidationBinder<>(Phase.class);
    private PmsService pmsService;

    public PhaseForm(PmsService pmsService) {
        addClassName("phase-form");
        this.pmsService = pmsService;

        List<State> statesList = pmsService.findAllStates("");
        states.setItems(statesList);
        states.setItemLabelGenerator(State::getName);

        cities.setItems(Collections.emptyList());
//        cities.setItemLabelGenerator(City::getName);

        states.addValueChangeListener(event -> {
            State selectedState = event.getValue();
            if (selectedState != null) {
                List<City> filteredCities = pmsService.findCitiesByState(selectedState.getId());
                cities.setItems(filteredCities);
            } else {
                cities.clear();
            }
        });

        cities.setItemLabelGenerator(City::getName);

        binder.bindInstanceFields(this);
        binder.forField(states).asRequired("State is required").bind(Phase::getState, Phase::setState);
        binder.forField(cities).asRequired("City is required").bind(Phase::getCity, Phase::setCity);

        add(states, cities, name, phaseId, createButtonsLayout());
    }


    private Component createButtonsLayout() {
            save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
            close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

            save.addClickShortcut(Key.ENTER);
            close.addClickShortcut(Key.ESCAPE);

            save.addClickListener(event -> validateAndSave());
            delete.addClickListener(event -> fireEvent(new DeleteEvent(this, phase)));
            close.addClickListener(event -> fireEvent(new CloseEvent(this)));

            binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));
            return new HorizontalLayout(save, delete, close);
        }

        private void validateAndSave() {
            if (phase == null) {
                System.out.println("Phase must be set before saving.");
//                Notification.show("Phase must be set before saving.").addThemeVariants(NotificationVariant.LUMO_ERROR);
                phase = new Phase();
                return;
            }

            State selectedState = states.getValue();
            City selectedCity = cities.getValue();
            if (selectedState == null) {
                System.out.println("State must be selected before saving.");
                Notification.show("State must be selected before saving.").addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            if (selectedCity == null) {
                System.out.println("City must be selected before saving.");
                Notification.show("City must be selected before saving.").addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            try {
                binder.writeBean(phase);
                phase.setState(selectedState);
                phase.setCity(selectedCity);

                if (phase.getPhaseId() == null || phase.getPhaseId().trim().isEmpty()) {
                    System.out.println("ID must be manually assigned before saving.");
                    Notification.show("ID must be manually assigned before saving.").addThemeVariants(NotificationVariant.LUMO_ERROR);
                    return;
                }

                if (pmsService.phaseExistsById(phase.getPhaseId())){
                    Notification.show("ID already exists").addThemeVariants(NotificationVariant.LUMO_ERROR);
                    return;
                }


                fireEvent(new SaveEvent(this, phase));
            } catch (ValidationException e) {
                e.printStackTrace();
            }
        }

    public void setPhase(Phase phase) {
        this.phase = phase;
        binder.readBean(phase);
    }

        public static abstract class PhaseFormEvent extends ComponentEvent<PhaseForm> {
            private Phase phase;

            protected PhaseFormEvent(PhaseForm source, Phase phase) {
                super(source, false);
                this.phase =  phase;
            }

            public Phase getPhase() {
                return phase;
            }
        }

        public static class SaveEvent extends PhaseFormEvent {
            SaveEvent(PhaseForm source, Phase phase) {
                super(source, phase);
            }
        }

        public static class DeleteEvent extends PhaseFormEvent {
            DeleteEvent(PhaseForm source, Phase phase) {
                super(source, phase);
            }
        }

        public static class CloseEvent extends PhaseFormEvent {
            CloseEvent(PhaseForm source) {
                super(source, null);
            }
        }

        public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                ComponentEventListener<T> listener) {
            return getEventBus().addListener(eventType, listener);
        }

}
