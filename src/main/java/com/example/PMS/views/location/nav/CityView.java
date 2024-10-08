package com.example.PMS.views.location.nav;

import com.example.PMS.entity.City;
import com.example.PMS.entity.State;
import com.example.PMS.service.PmsService;
import com.example.PMS.views.forms.CityForm;
import com.example.PMS.views.forms.StateForm;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.dom.Style;

public class CityView extends VerticalLayout {

    Grid<City> cityGrid = new Grid<>(City.class);
    TextField filterText = new TextField();
    CityForm cityForm;
    PmsService pmsService;

    public CityView(PmsService pmsService){
        this.pmsService = pmsService;

        addClassName("city-view");
        setSizeFull();

        cityForm = new CityForm(pmsService);
        configureForm();
        configureGrid();

        add(getToolbar(), getContent());
        updateList();
        closeEditor();
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


        HorizontalLayout searchFieldWithButton = new HorizontalLayout(filterText, searchIcon);
        searchFieldWithButton.setSpacing(false);
        searchFieldWithButton.addClassName("search-field");

        com.vaadin.flow.component.button.Button addContactButton = new Button("Add City");
        addContactButton.addClickListener(click -> openStateFormDialog());
        addContactButton.getStyle().setBackgroundColor("Green");
        addContactButton.getStyle().setColor("white");

        HorizontalLayout toolbar = new HorizontalLayout(searchFieldWithButton, addContactButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }



    private HorizontalLayout getContent() {
        HorizontalLayout content = new HorizontalLayout(cityGrid, cityForm);
        content.setFlexGrow(2, cityGrid);
        content.addClassNames("content");
        content.setSizeFull();
        content.getStyle().setPadding("0");
        return content;
    }

    private void configureForm() {
        cityForm.setWidth("25em");
        cityForm.addListener(CityForm.SaveEvent.class, this::saveCity);
        cityForm.addListener(CityForm.DeleteEvent.class, this::deleteCity);
        cityForm.addListener(CityForm.CloseEvent.class, e -> closeEditor());
    }


    private void configureGrid() {
        cityGrid.addClassNames("city-grid");
        cityGrid.setSizeFull();
        cityGrid.setColumns("state", "name", "id");

        cityGrid.getColumns().forEach(col -> col.setAutoWidth(true));

        cityGrid.asSingleSelect().addValueChangeListener(event ->
                editCity(event.getValue()));
    }


    public void editCity(City city) {
        if (city == null) {
            closeEditor();
        } else {
            CityForm cityForm1 = new CityForm(pmsService);

            cityForm1.setCity(city);
            cityForm1.setVisible(true);
            addClassName("editing");


            Dialog dialog = new Dialog();
            cityForm1.setWidth("25em");
            dialog.add(cityForm1);
            dialog.setCloseOnOutsideClick(true);
            dialog.open();

            cityForm1.addListener(CityForm.SaveEvent.class, event -> {
                saveCity(event);
                dialog.close();
            });
            cityForm1.addListener(CityForm.DeleteEvent.class, e ->{
                deleteCity(e);
                closeEditor();
                dialog.close();
            });
            cityForm1.addListener(CityForm.CloseEvent.class, e -> {
                closeEditor();
                dialog.close();
            });
        }
    }

    private void updateList() {
        cityGrid.setItems(pmsService.findAllCities(filterText.getValue()));
    }

    private void closeEditor() {
        cityForm.setCity(null);
        cityForm.setVisible(false);
        removeClassName("editing");
    }

    private void saveCity(CityForm.SaveEvent event) {
        pmsService.saveCity(event.getCity());
        updateList();
        closeEditor();
    }

    private void deleteCity(CityForm.DeleteEvent event) {
        pmsService.deleteCity(event.getCity());
        updateList();
        closeEditor();

    }

    private void openStateFormDialog() {
        City newCity = new City();
        CityForm cityForm = new CityForm(pmsService);
        cityForm.setCity(newCity);
        cityForm.setWidth("25em");
        Dialog dialog = new Dialog();
        dialog.add(cityForm);
        dialog.setCloseOnOutsideClick(true);
        dialog.open();


        cityForm.addListener(CityForm.SaveEvent.class, event -> {
            saveCity(event);
            dialog.close();

        });
        cityForm.addListener(CityForm.DeleteEvent.class, e -> {
            deleteCity(e);
            closeEditor();
            dialog.close();
        });
        cityForm.addListener(CityForm.CloseEvent.class, e -> {
            closeEditor();
            dialog.close();
        });
    }

}
