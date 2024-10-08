package com.example.PMS.views;

import com.example.PMS.views.location.LocationView;
import com.example.PMS.views.dashboard.Dashboard;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.theme.lumo.LumoUtility;

/**
 * The main view is a top-level placeholder for other views.
 */
@CssImport("/themes/PMS/styles.css")
public class MainLayout extends AppLayout {

    private H1 viewTitle;

    public MainLayout() {
        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");

        viewTitle = new H1();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        addToNavbar(toggle, viewTitle);
    }

    private void addDrawerContent() {
        Span appName = new Span("Property Management Service");
        appName.addClassNames(LumoUtility.FontWeight.BOLD, LumoUtility.FontSize.LARGE);
        Icon logoImg = VaadinIcon.GLOBE_WIRE.create();
        logoImg.getStyle().setWidth("40%");
        logoImg.getStyle().setHeight("40%");
        appName.getStyle().setFontWeight(Style.FontWeight.BOLD);

        appName.getStyle().set("color", "white");
        logoImg.getStyle().set("color", "white");

        HorizontalLayout logo = new HorizontalLayout(logoImg, appName);
        Header header = new Header(logo);
        header.getStyle().set("background-color", "#005fdb");

        Scroller scroller = new Scroller(createNavigation());
        scroller.getStyle().set("background-color", "#005fdb");

        logo.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        logo.getStyle().setPadding("20px");
        header.addClassNames("py-m", "px-m");

        addToDrawer(header, scroller);
    }

    private SideNav createNavigation() {
        SideNav nav = new SideNav();

        Icon dashboardIcon = VaadinIcon.DASHBOARD.create();
        dashboardIcon.getStyle().set("color", "white");

        Icon locationIcon = VaadinIcon.LOCATION_ARROW.create();
        locationIcon.getStyle().set("color", "white");
        nav.getStyle().setPadding("20px");



        SideNavItem dashboardItem = new SideNavItem("Dashboard", Dashboard.class, dashboardIcon);
        SideNavItem locationItem = new SideNavItem("Location", LocationView.class, locationIcon);

        dashboardItem.getStyle().set("color", "white");
        locationItem.getStyle().set("color", "white");



        dashboardItem.addClassName("side-nav-item");
        dashboardItem.addClassName("locationSideNav");

        locationItem.addClassName("side-nav-item");

        nav.addItem(dashboardItem, locationItem);

        return nav;

    }

    @Override
    protected void afterNavigation() {
//        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }


}
