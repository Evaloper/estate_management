package com.example.PMS.views.location.nav;

import com.example.PMS.entity.City;
import com.example.PMS.entity.Phase;
import com.example.PMS.entity.State;
import com.example.PMS.service.PmsService;
import com.example.PMS.views.forms.CityForm;
import com.example.PMS.views.forms.PhaseForm;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.dom.Style;

import java.util.List;

public class PhaseView extends VerticalLayout {
    Grid<Phase> phaseGrid = new Grid<>(Phase.class);
    TextField filterText = new TextField();
    PhaseForm phaseForm;
    PmsService pmsService;

    Binder<Phase> binder = new BeanValidationBinder<>(Phase.class);

    public PhaseView(PmsService pmsService){
        addClassName("phase-grid");
        this.pmsService = pmsService;
        setSizeFull();

        phaseForm = new PhaseForm(pmsService);
        configureForm();
        configureGrid();

        add(getToolbar(), getContent());
        updateList();
        closeEditor();

    }

    private void configureForm() {
        phaseForm.setWidth("25em");
        phaseForm.addListener(PhaseForm.SaveEvent.class, this::savePhase);
        phaseForm.addListener(PhaseForm.DeleteEvent.class, this::deletePhase);
        phaseForm.addListener(PhaseForm.CloseEvent.class, e -> closeEditor());
    }

    private void configureGrid() {
        phaseGrid.addClassNames("city-grid");
        phaseGrid.setSizeFull();
        phaseGrid.setColumns("state", "city", "name", "id");


        phaseGrid.getColumns().forEach(col -> col.setAutoWidth(true));

        phaseGrid.asSingleSelect().addValueChangeListener(event ->
                editPhase(event.getValue()));
    }

    private HorizontalLayout getToolbar() {
        filterText.setPlaceholder("Search");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());

        Icon searchIcon = new Icon(VaadinIcon.SEARCH);
        searchIcon.addClickListener(click -> updateList());
        searchIcon.addClassName("search-button");

        filterText.setWidth("300px");
        searchIcon.getStyle().setColor("grey");
        searchIcon.getStyle().setPosition(Style.Position.RELATIVE);
        searchIcon.getStyle().setRight("35px");
        searchIcon.getStyle().setTop("12px");
        searchIcon.setSize("18px");

        Button addButton = new Button("Add Phase", new Icon(VaadinIcon.PLUS));
        addButton.addClickListener(click -> openStateFormDialog());
        addButton.getStyle().setBackgroundColor("Green");
        addButton.getStyle().setColor("white");

        HorizontalLayout searchFieldWithButton = new HorizontalLayout(filterText, searchIcon);
        searchFieldWithButton.setSpacing(false);
        searchFieldWithButton.addClassName("search-field");

        ComboBox<State> states = new ComboBox<>("State");
        List<State> statesList = pmsService.findAllStates("");
        states.setItems(statesList);
        states.setItemLabelGenerator(State::getName);

        ComboBox<City> cities = new ComboBox<>("City");
        List<City> cityList = pmsService.findAllCities("");
        cities.setItems(cityList);
        cities.setItemLabelGenerator(City::getName);

        states.addValueChangeListener(event -> {
            if (event.getValue() != null) {
                updateListByFilters(event.getValue(), cities.getValue());
            }
        });

        cities.addValueChangeListener(event -> {
            if (event.getValue() != null) {
                updateListByFilters(states.getValue(), event.getValue());
            }
        });

        binder.forField(states).bind(phase -> phase.getState(), (phase, state) -> phase.setState(state));
        binder.forField(cities).bind(phase -> phase.getCity(), (phase, city) -> phase.setCity(city));

        Button resetButton = new Button("Reset Filter");
        resetButton.addClickListener(click -> {
            states.clear();
            cities.clear();
            updateList();
        });
        resetButton.getStyle().setBackgroundColor("Green");
        resetButton.getStyle().setColor("white");

        HorizontalLayout toolbar = new HorizontalLayout(addButton, searchFieldWithButton, states, cities, resetButton);
        toolbar.getStyle().set("align-items", "baseline");
        toolbar.setDefaultVerticalComponentAlignment(Alignment.BASELINE);
        toolbar.addClassName("toolbar");
        return toolbar;
    }


    private HorizontalLayout getContent() {
        HorizontalLayout content = new HorizontalLayout(phaseGrid, phaseForm);
        content.setFlexGrow(2, phaseGrid);
        content.addClassNames("content");
        content.setSizeFull();
        content.getStyle().setPadding("0");
        return content;
    }

    public void editPhase(Phase phase) {
        if (phase == null) {
            closeEditor();
        } else {
            PhaseForm phaseForm1 = new PhaseForm(pmsService);

            phaseForm1.setPhase(phase);
            phaseForm1.setVisible(true);
            addClassName("editing");

            Dialog dialog = new Dialog();
            phaseForm1.setWidth("25em");
            dialog.add(phaseForm1);
            dialog.setCloseOnOutsideClick(true);
            dialog.open();

            phaseForm1.addListener(PhaseForm.SaveEvent.class, event -> {
                savePhase(event);
                dialog.close();
            });
            phaseForm1.addListener(PhaseForm.DeleteEvent.class, e ->{
                deletePhase(e);
                closeEditor();
                dialog.close();
            });
            phaseForm1.addListener(PhaseForm.CloseEvent.class, e -> {
                closeEditor();
                dialog.close();
            });
        }
    }

    private void updateList() {
        phaseGrid.setItems(pmsService.findAllPhases(filterText.getValue()));
    }

    private void closeEditor() {
        phaseForm.setPhase(null);
        phaseForm.setVisible(false);
        removeClassName("editing");
    }


    private void savePhase(PhaseForm.SaveEvent event) {
        pmsService.savePhase(event.getPhase());
        updateList();
        closeEditor();
    }

    private void deletePhase(PhaseForm.DeleteEvent event) {
        pmsService.deletePhase(event.getPhase());
        updateList();
        closeEditor();

    }

    private void openStateFormDialog() {
        Phase newPhase = new Phase();
        PhaseForm phaseForm = new PhaseForm(pmsService);
        phaseForm.setPhase(newPhase);
        phaseForm.setWidth("25em");
        Dialog dialog = new Dialog();
        dialog.add(phaseForm);
        dialog.setCloseOnOutsideClick(true);
        dialog.open();


        phaseForm.addListener(PhaseForm.SaveEvent.class, event -> {
            savePhase(event);
            dialog.close();

        });
        phaseForm.addListener(PhaseForm.DeleteEvent.class, e -> {
            deletePhase(e);
            closeEditor();
            dialog.close();
        });
        phaseForm.addListener(PhaseForm.CloseEvent.class, e -> {
            closeEditor();
            dialog.close();
        });
    }

    private void updateListByFilters(State state, City city) {
        if (state != null && city != null) {
            phaseGrid.setItems(pmsService.findPhasesByStateAndCity(state, city));
        } else if (state != null) {
            phaseGrid.setItems(pmsService.findPhasesByState(state));
        } else if (city != null) {
            phaseGrid.setItems(pmsService.findPhasesByCity(city));
        } else {
            updateList();
        }
    }

}
