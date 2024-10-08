package com.example.PMS.views.forms;

import com.example.PMS.entity.City;
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

import java.util.List;

public class CityForm extends FormLayout {
    ComboBox<State> states = new ComboBox<>("State");
    TextField name = new TextField("City");
    TextField id = new TextField("ID");

    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button close = new Button("Cancel");

    private City city;

    Binder<City> binder = new BeanValidationBinder<>(City.class);

    private PmsService pmsService;

    public CityForm(PmsService pmsService) {
        addClassName("city-form");
        this.pmsService = pmsService;

        List<State> statesList = pmsService.findAllStates("");
        states.setItems(statesList);
        states.setItemLabelGenerator(State::getName);

        binder.bindInstanceFields(this);
        binder.forField(states).asRequired("State is required").bind(City::getState, City::setState);

        add(states, name, id, createButtonsLayout());
    }

    private Component createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(event -> validateAndSave());
        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, city)));
        close.addClickListener(event -> fireEvent(new CloseEvent(this)));

        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));
        return new HorizontalLayout(save, delete, close);
    }

    private void validateAndSave() {
        if (city == null) {
            System.out.println("City must be set before saving.");
            Notification.show("City must be set before saving.").addThemeVariants(NotificationVariant.LUMO_ERROR);
            city = new City();
            return;
        }

        State selectedState = states.getValue();
        if (selectedState == null) {
            System.out.println("State must be selected before saving.");
            Notification.show("State must be selected before saving.").addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        try {
            binder.writeBean(city);
            city.setState(selectedState);

            if (city.getId() == null || city.getId().trim().isEmpty()) {
                System.out.println("ID must be manually assigned before saving.");
                Notification.show("ID must be manually assigned before saving.").addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            if (pmsService.cityExistsById(city.getId())){
                Notification.show("ID already exists").addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            if (pmsService.cityExistsByName(city.getName())){
                Notification.show("State already exists").addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            fireEvent(new SaveEvent(this, city));
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }


    public void setCity(City city) {
        this.city = city;
        binder.readBean(city);
    }

    public static abstract class CityFormEvent extends ComponentEvent<CityForm> {
        private City city;

        protected CityFormEvent(CityForm source, City city) {
            super(source, false);
            this.city =  city;
        }

        public City getCity() {
            return city;
        }
    }

    public static class SaveEvent extends CityFormEvent {
        SaveEvent(CityForm source, City city) {
            super(source, city);
        }
    }

    public static class DeleteEvent extends CityFormEvent {
        DeleteEvent(CityForm source, City city) {
            super(source, city);
        }
    }

    public static class CloseEvent extends CityForm.CityFormEvent {
        CloseEvent(CityForm source) {
            super(source, null);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}
