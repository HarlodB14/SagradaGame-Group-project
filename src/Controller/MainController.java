package Controller;

import DAL.DatabaseConnector;

import java.sql.SQLException;

public class MainController {
    private DashboardController dashboardController;
    private ApplicationController applicationController;

    public MainController() throws SQLException {
        this.applicationController = new ApplicationController();
        this.dashboardController = new DashboardController(applicationController);
        DatabaseConnector.getConnection();
    }

    public void startApplication() {
        dashboardController.showDashboard();
    }
}
