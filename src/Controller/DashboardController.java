package Controller;

import View.DashboardScene;
import View.StartupScene;

public class DashboardController {

    private DashboardScene dashboardScene;
    private ApplicationController applicationController;

    public DashboardController(ApplicationController applicationController) {
        this.applicationController = applicationController;
        showDashboard();
    }

    public void showDashboard() {
        applicationController.setStageProperties();
        ApplicationController.stage.setScene(new StartupScene(new AccountController()));
        ApplicationController.stage.show();
    }
}
