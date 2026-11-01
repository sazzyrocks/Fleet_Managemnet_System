package com.example.car_rental_sys.controllers;

import com.example.car_rental_sys.model.Role;
import com.example.car_rental_sys.model.User;
import com.example.car_rental_sys.service.ServiceFactory;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.net.URL;

/**
 * Controller for the Settings navigation Middle Bar component.
 */
public class MiddleBarController {

    @FXML
    private Pane userMiddleBar;

    @FXML
    private Button btnSysSetting;

    @FXML
    private Button btnAccSecurity;

    @FXML
    private Button btnAccPwd;

    public static MiddleBarController instance;

    public MiddleBarController() {
        instance = this;
    }

    @FXML
    private void initialize() {
        instance = this;
        initButtonStyle();
        initTheme();
    }

    private void initButtonStyle() {
        if (btnSysSetting != null) {
            btnSysSetting.getStyleClass().add("btnMiddleFocusStyle");
            btnSysSetting.getStyleClass().remove("defaultMiddleBtnStyle");
        }
        if (btnAccSecurity != null) {
            btnAccSecurity.getStyleClass().add("defaultMiddleBtnStyle");
        }
        if (btnAccPwd != null) {
            btnAccPwd.getStyleClass().add("defaultMiddleBtnStyle");
        }
    }

    private void initTheme() {
        User user = ServiceFactory.getInstance().getAuthService().getCurrentUser();
        if (user != null && user.getRole() == Role.CUSTOMER && userMiddleBar != null) {
            URL darkCss = getClass().getResource("/com/example/car_rental_sys/style/middleBarDark.css");
            if (darkCss != null) {
                userMiddleBar.getStylesheets().add(darkCss.toExternalForm());
            }
        }
    }

    @FXML
    void btnSysSettingClicked(MouseEvent event) {
        barButtonClickEvent(event);
        User user = ServiceFactory.getInstance().getAuthService().getCurrentUser();
        if (user != null && user.getRole() == Role.CUSTOMER) {
            if (CustomerServiceController.instance != null) {
                CustomerServiceController.instance.showSettingPage();
            }
        } else if (user != null && user.getRole() == Role.ADMIN) {
            if (AdminServiceController.instance != null) {
                AdminServiceController.instance.showSettingPage();
            }
        } else {
            if (DriverMainPageController.driverMainPageInstance != null) {
                DriverMainPageController.driverMainPageInstance.showSettingPage();
            }
        }
    }

    @FXML
    void btnAccSettingClicked(MouseEvent event) {
        barButtonClickEvent(event);
        User user = ServiceFactory.getInstance().getAuthService().getCurrentUser();
        if (user != null && user.getRole() == Role.CUSTOMER) {
            if (CustomerServiceController.instance != null) {
                CustomerServiceController.instance.showAccSecurity();
            }
        } else if (user != null && user.getRole() == Role.ADMIN) {
            if (AdminServiceController.instance != null) {
                AdminServiceController.instance.showAccSecurity();
            }
        } else {
            if (DriverMainPageController.driverMainPageInstance != null) {
                DriverMainPageController.driverMainPageInstance.showAccSecurity();
            }
        }
    }

    @FXML
    void btnAccPasswordClicked(MouseEvent event) {
        barButtonClickEvent(event);
        User user = ServiceFactory.getInstance().getAuthService().getCurrentUser();
        if (user != null && user.getRole() == Role.CUSTOMER) {
            if (CustomerServiceController.instance != null) {
                CustomerServiceController.instance.showPasswordPage();
            }
        } else if (user != null && user.getRole() == Role.ADMIN) {
            if (AdminServiceController.instance != null) {
                AdminServiceController.instance.showPasswordPage();
            }
        } else {
            if (DriverMainPageController.driverMainPageInstance != null) {
                DriverMainPageController.driverMainPageInstance.showPasswordPage();
            }
        }
    }

    @FXML
    private void barButtonClickEvent(MouseEvent e) {
        if (!(e.getSource() instanceof Button)) {
            return;
        }
        Button btn = (Button) e.getSource();
        clearFocusStyle();
        btn.getStyleClass().add("btnMiddleFocusStyle");
        btn.getStyleClass().remove("defaultMiddleBtnStyle");
    }

    private void clearFocusStyle() {
        Button[] btns = {btnSysSetting, btnAccSecurity, btnAccPwd};
        for (Button btn : btns) {
            if (btn != null) {
                btn.getStyleClass().remove("btnMiddleFocusStyle");
                btn.getStyleClass().add("defaultMiddleBtnStyle");
            }
        }
    }
}

