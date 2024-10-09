package com.example.PMS.views.location.nav;

import com.example.PMS.entity.State;
import com.example.PMS.service.PmsService;
import com.example.PMS.views.forms.StateForm;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.dom.Style;

public class StateView extends VerticalLayout {

    private final Grid<State> stateGrid = new Grid<>(State.class);
    private final TextField filterText = new TextField();
    private final StateForm stateForm;
    private final PmsService pmsService;

    public StateView(PmsService pmsService) {
        this.pmsService = pmsService;

        addClassName("state-view");
        setSizeFull();

        stateForm = new StateForm(pmsService);
        configureForm();
        configureGrid();

        add(getToolbar(), getContent());
        updateList();
        closeEditor();
    }

    private HorizontalLayout getContent() {
        HorizontalLayout content = new HorizontalLayout(stateGrid, stateForm);
        content.setFlexGrow(2, stateGrid);
        content.addClassNames("content");
        content.setSizeFull();
        content.getStyle().setPadding("0");
        return content;
    }

    private void configureForm() {
        stateForm.setWidth("25em");
        stateForm.addListener(StateForm.SaveEvent.class, this::saveState);
        stateForm.addListener(StateForm.DeleteEvent.class, this::deleteState);
        stateForm.addListener(StateForm.CloseEvent.class, e -> closeEditor());
    }

    private void configureGrid() {
        stateGrid.addClassNames("state-grid");
        stateGrid.setSizeFull();
        stateGrid.setColumns("name", "stateId");
        stateGrid.getColumns().forEach(col -> col.setAutoWidth(true));
        stateGrid.asSingleSelect().addValueChangeListener(event -> editState(event.getValue()));
    }

    private HorizontalLayout getToolbar() {
        filterText.setPlaceholder("Search");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());

        Icon searchIcon = new Icon(VaadinIcon.SEARCH);
        searchIcon.addClickListener(click -> updateList());
        searchIcon.addClassName("search-button");
        searchIcon.getStyle().setColor("grey");
        searchIcon.getStyle().setPosition(Style.Position.RELATIVE);
        searchIcon.getStyle().setRight("35px");
        searchIcon.getStyle().setTop("12px");
        searchIcon.setSize("18px");

        HorizontalLayout searchFieldWithButton = new HorizontalLayout(filterText, searchIcon);
        searchFieldWithButton.setSpacing(false);
        searchFieldWithButton.addClassName("search-field");

        Button addStateButton = new Button("Add State");
        addStateButton.addClickListener(click -> openStateFormDialog());
        addStateButton.getStyle().setBackgroundColor("Green");
        addStateButton.getStyle().setColor("white");

        HorizontalLayout toolbar = new HorizontalLayout(searchFieldWithButton, addStateButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private void openStateFormDialog() {
        stateForm.setState(new State()); // Initializes a new State
        stateForm.setVisible(true);
        Dialog dialog = createStateFormDialog();
        dialog.open();
    }

    public void editState(State state) {
        if (state == null) {
            closeEditor();
        } else {
            stateForm.setState(state); // Set the existing state
            stateForm.setVisible(true);
            Dialog dialog = createStateFormDialog();
            dialog.open();
        }
    }

    private Dialog createStateFormDialog() {
        Dialog dialog = new Dialog();
        dialog.add(stateForm);
        dialog.setCloseOnOutsideClick(true);
        stateForm.addListener(StateForm.SaveEvent.class, event -> {
            saveState(event);
            dialog.close();
        });
        stateForm.addListener(StateForm.DeleteEvent.class, e -> {
            deleteState(e);
            closeEditor();
            dialog.close();
        });
        stateForm.addListener(StateForm.CloseEvent.class, e -> {
            closeEditor();
            dialog.close();
        });
        return dialog;
    }

    private void closeEditor() {
        stateForm.setState(null);
        stateForm.setVisible(false);
        removeClassName("editing");
    }

    private void updateList() {
        stateGrid.setItems(pmsService.findAllStates(filterText.getValue()));
    }

    private void saveState(StateForm.SaveEvent event) {
        pmsService.saveState(event.getState());
        updateList();
        closeEditor();
    }

    private void deleteState(StateForm.DeleteEvent event) {
        pmsService.deleteState(event.getState());
        updateList();
        closeEditor();
    }
}
