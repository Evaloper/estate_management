package com.example.PMS.views.location;

import com.example.PMS.service.PmsService;
import com.example.PMS.views.location.nav.CityView;
import com.example.PMS.views.location.nav.PhaseView;
import com.example.PMS.views.location.nav.StateView;
import com.example.PMS.views.MainLayout;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "location", layout = MainLayout.class)
public class LocationView extends VerticalLayout {

    private final VerticalLayout content = new VerticalLayout();
    private final PmsService pmsService;

    @Autowired
    public LocationView(PmsService pmsService) {
        this.pmsService = pmsService;

        Tab stateTab = new Tab("State");
        Tab cityTab = new Tab("City");
        Tab phaseTab = new Tab("Phase");

        Tabs tabs = new Tabs(stateTab, cityTab, phaseTab);
        tabs.setWidth("100%");
        tabs.getStyle().setBackgroundColor("#bfbfbf");
        tabs.getStyle().set("color", "white");

        content.setSizeFull();
        content.setPadding(false);
        setPadding(false);

        addClassName("location-view");
        setSizeFull();

        add(tabs, content);

        tabs.setSelectedTab(stateTab);
        setContent(stateTab);
        setTabStyles(stateTab, stateTab, cityTab, phaseTab);


        tabs.addSelectedChangeListener(event -> {
            Tab selectedTab = event.getSelectedTab();
            setTabStyles(selectedTab, stateTab, cityTab, phaseTab);
            setContent(selectedTab);
        });
    }

    private void setTabStyles(Tab selectedTab, Tab... tabs) {
        for (Tab tab : tabs) {
            if (tab == selectedTab) {
                tab.getStyle().set("color", "#ff7201");
            } else {
                tab.getStyle().remove("color");
            }
        }
    }

    private void setContent(Tab selectedTab) {
        content.removeAll();

        if ("State".equals(selectedTab.getLabel())) {
            content.add(new StateView(pmsService));
        } else if ("City".equals(selectedTab.getLabel())) {
            content.add(new CityView(pmsService));
        } else if ("Phase".equals(selectedTab.getLabel())) {
            content.add(new PhaseView(pmsService));
        }
    }
}
