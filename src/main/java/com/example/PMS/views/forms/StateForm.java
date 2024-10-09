package com.example.PMS.views.forms;

import com.example.PMS.entity.State;
import com.example.PMS.service.PmsService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;

public class StateForm extends FormLayout {

    TextField name = new TextField("Name of State");
    TextField state_id = new TextField("ID");
    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button close = new Button("Cancel");

    private State state;

    Binder<State> binder = new BeanValidationBinder<>(State.class);

    private PmsService pmsService;

    public StateForm(PmsService pmsService) {
        this.pmsService = pmsService;
        addClassName("state-form");
        binder.bindInstanceFields(this);
        add(name, state_id, createButtonsLayout());
    }

    private Component createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(event -> validateAndSave());
        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, state)));
        close.addClickListener(event -> fireEvent(new CloseEvent(this)));

        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));
        return new HorizontalLayout(save, delete, close);
    }

    private void validateAndSave() {
        if (state == null) {
            Notification.show("State object is not set. Please try again.").addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        try {
            binder.writeBean(state);

            if (state.getStateId() == null || state.getStateId().trim().isEmpty()) {
                Notification.show("ID must be manually set").addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            if (pmsService.stateExistsById(state.getStateId())) {
                Notification.show("ID already exists").addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            if (pmsService.stateExistsByName(state.getName())) {
                Notification.show("State already exists").addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            fireEvent(new SaveEvent(this, state));
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }

    public void setState(State state) {
        this.state = state;
        binder.readBean(state);
    }

    public static abstract class StateFormEvent extends ComponentEvent<StateForm> {
        private State state;

        protected StateFormEvent(StateForm source, State state) {
            super(source, false);
            this.state = state;
        }

        public State getState() {
            return state;
        }
    }

    public static class SaveEvent extends StateFormEvent {
        SaveEvent(StateForm source, State state) {
            super(source, state);
        }
    }

    public static class DeleteEvent extends StateFormEvent {
        DeleteEvent(StateForm source, State state) {
            super(source, state);
        }
    }

    public static class CloseEvent extends StateFormEvent {
        CloseEvent(StateForm source) {
            super(source, null);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}
