package com.example.car_rental_sys.controllers;

import com.example.car_rental_sys.concurrency.AsyncExecutor;
import com.example.car_rental_sys.model.FuelType;
import com.example.car_rental_sys.model.TransmissionType;
import com.example.car_rental_sys.model.Vehicle;
import com.example.car_rental_sys.model.VehicleStatus;
import com.example.car_rental_sys.service.ServiceFactory;
import com.example.car_rental_sys.service.VehicleService;
import com.example.car_rental_sys.util.AlertHelper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;

/**
 * Controller for adding and editing vehicles in the fleet catalog.
 */
public class EditVehiclePageController extends Controller {

    private static boolean editMode = false;
    private static Vehicle targetVehicle;

    @FXML
    private TextField txtVehicleName;

    @FXML
    private ComboBox<String> cmbCategory;

    @FXML
    private ComboBox<Integer> cmbSeatNumber;

    @FXML
    private ComboBox<String> cmbCarBrand;

    @FXML
    private Pane imgPane;

    @FXML
    private ImageView imgVehicle;

    @FXML
    private Pane dragPane;

    @FXML
    private TextField txtPrice;

    @FXML
    private TextField txtManufacturing;

    @FXML
    private TextField txtChassisNumber;

    @FXML
    private TextField txtPlateNumber;

    @FXML
    private Button btnOperation;

    @FXML
    private Button btnSaveOption;

    @FXML
    private Label lblClearImage;

    @FXML
    private Label btnDeleteVehicle;

    @FXML
    private Pane colorOne, colorTwo, colorThree, colorFour, colorFive, colorSix, colorSeven, colorEight;

    private String selectedImagePath = "/com/example/car_rental_sys/image/cars/Huracan_EVI.png";

    public static void setEditMode(boolean isEdit, Vehicle vehicle) {
        editMode = isEdit;
        targetVehicle = vehicle;
    }

    @FXML
    public void initialize() {
        populateDropdowns();
        setupColorPanes();

        if (editMode && targetVehicle != null) {
            loadVehicleData(targetVehicle);
        } else {
            if (btnDeleteVehicle != null) btnDeleteVehicle.setVisible(false);
            if (btnSaveOption != null) btnSaveOption.setVisible(false);
            if (btnOperation != null) btnOperation.setVisible(true);
        }
    }

    private void populateDropdowns() {
        if (cmbCategory != null) {
            cmbCategory.getItems().setAll("Sedan", "SUV", "Coupe", "Supercar", "Luxury", "Electric");
            cmbCategory.getSelectionModel().selectFirst();
        }
        if (cmbSeatNumber != null) {
            cmbSeatNumber.getItems().setAll(2, 4, 5, 7, 8);
            cmbSeatNumber.getSelectionModel().select(1); // 4 seats
        }
        if (cmbCarBrand != null) {
            cmbCarBrand.getItems().setAll(
                    "Lamborghini", "Ferrari", "Porsche", "McLaren",
                    "Mercedes-Benz", "BMW", "Audi", "Tesla", "Rolls-Royce", "Bentley"
            );
            cmbCarBrand.getSelectionModel().selectFirst();
        }
    }

    private void setupColorPanes() {
        String[] colors = {"#165DFF", "#00B42A", "#F7BA1E", "#F53F3F", "#722ED1", "#D91AD9", "#3491FA", "#1D2129"};
        Pane[] panes = {colorOne, colorTwo, colorThree, colorFour, colorFive, colorSix, colorSeven, colorEight};

        for (int i = 0; i < panes.length && i < colors.length; i++) {
            if (panes[i] != null) {
                final String hex = colors[i];
                panes[i].setStyle("-fx-background-color: " + hex + "; -fx-background-radius: 18px; -fx-cursor: hand;");
            }
        }
    }

    private void loadVehicleData(Vehicle v) {
        if (txtVehicleName != null) txtVehicleName.setText(v.getModel());
        if (txtPlateNumber != null) txtPlateNumber.setText(v.getLicensePlate());
        if (txtChassisNumber != null) txtChassisNumber.setText(v.getLicensePlate() != null ? v.getLicensePlate() : "");
        if (txtPrice != null && v.getDailyRate() != null) txtPrice.setText(v.getDailyRate().toPlainString());
        if (txtManufacturing != null && v.getYear() != null) txtManufacturing.setText(String.valueOf(v.getYear()));

        if (cmbCarBrand != null && v.getBrand() != null) cmbCarBrand.setValue(v.getBrand());
        if (cmbSeatNumber != null && v.getSeats() != null) cmbSeatNumber.setValue(v.getSeats());

        if (btnDeleteVehicle != null) btnDeleteVehicle.setVisible(true);
        if (btnSaveOption != null) btnSaveOption.setVisible(true);
        if (btnOperation != null) btnOperation.setVisible(false);
    }

    @FXML
    private void addVehicleBtnClicked(MouseEvent event) {
        Vehicle vehicle = buildVehicleFromForm();
        if (vehicle == null) return;

        btnOperation.setDisable(true);
        AsyncExecutor.getInstance().execute(() -> {
            try {
                VehicleService vehicleService = ServiceFactory.getInstance().getVehicleService();
                vehicleService.addVehicle(vehicle);

                Platform.runLater(() -> {
                    btnOperation.setDisable(false);
                    AlertHelper.showSuccess("Vehicle Added", "Vehicle " + vehicle.getBrand() + " " + vehicle.getModel() + " added to fleet.");
                    clearForm();
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    btnOperation.setDisable(false);
                    AlertHelper.showError("Failed to Add", e.getMessage());
                });
            }
        });
    }

    @FXML
    private void saveVehicleBtnClicked(MouseEvent event) {
        if (targetVehicle == null) return;
        Vehicle vehicle = buildVehicleFromForm();
        if (vehicle == null) return;
        vehicle.setVehicleId(targetVehicle.getVehicleId());

        btnSaveOption.setDisable(true);
        AsyncExecutor.getInstance().execute(() -> {
            try {
                VehicleService vehicleService = ServiceFactory.getInstance().getVehicleService();
                vehicleService.updateVehicle(vehicle);

                Platform.runLater(() -> {
                    btnSaveOption.setDisable(false);
                    AlertHelper.showSuccess("Vehicle Updated", "Fleet record updated successfully.");
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    btnSaveOption.setDisable(false);
                    AlertHelper.showError("Failed to Save", e.getMessage());
                });
            }
        });
    }

    @FXML
    private void btnDeleteVehicleClicked(MouseEvent event) {
        if (targetVehicle == null) return;
        boolean confirmed = AlertHelper.showConfirmation("Delete Vehicle", "Are you sure you want to remove this vehicle from the fleet catalog?");
        if (!confirmed) return;

        AsyncExecutor.getInstance().execute(() -> {
            try {
                VehicleService vehicleService = ServiceFactory.getInstance().getVehicleService();
                vehicleService.deleteVehicle(targetVehicle.getVehicleId());

                Platform.runLater(() -> {
                    AlertHelper.showSuccess("Vehicle Deleted", "Vehicle removed from fleet.");
                    clearForm();
                });
            } catch (Exception e) {
                Platform.runLater(() -> AlertHelper.showError("Delete Failed", e.getMessage()));
            }
        });
    }

    @FXML
    private void btnBrowseClicked(MouseEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select Vehicle Image");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File file = chooser.showOpenDialog(null);
        if (file != null) {
            selectedImagePath = file.toURI().toString();
            try {
                imgVehicle.setImage(new Image(new FileInputStream(file)));
                imgPane.setVisible(true);
                dragPane.setVisible(false);
            } catch (Exception ignored) {
            }
        }
    }

    @FXML
    private void imgVehicleDrop(DragEvent event) {
        Dragboard db = event.getDragboard();
        if (db.hasFiles() && !db.getFiles().isEmpty()) {
            File file = db.getFiles().get(0);
            try {
                imgVehicle.setImage(new Image(new FileInputStream(file)));
                selectedImagePath = file.toURI().toString();
                imgPane.setVisible(true);
                dragPane.setVisible(false);
            } catch (Exception ignored) {
            }
        }
    }

    @FXML
    private void clearBtnClicked(MouseEvent event) {
        imgVehicle.setImage(null);
        imgPane.setVisible(false);
        dragPane.setVisible(true);
        selectedImagePath = null;
    }

    private Vehicle buildVehicleFromForm() {
        String name = txtVehicleName != null ? txtVehicleName.getText().trim() : "";
        String brand = cmbCarBrand != null ? cmbCarBrand.getValue() : "Lamborghini";
        String plate = txtPlateNumber != null ? txtPlateNumber.getText().trim() : "";
        String priceStr = txtPrice != null ? txtPrice.getText().trim() : "150";

        if (name.isEmpty() || plate.isEmpty()) {
            AlertHelper.showWarning("Required Fields", "Please enter vehicle name and license plate number.");
            return null;
        }

        BigDecimal price;
        try {
            price = new BigDecimal(priceStr);
        } catch (Exception e) {
            price = new BigDecimal("150.00");
        }

        Vehicle v = new Vehicle();
        v.setBrand(brand);
        v.setModel(name);
        v.setLicensePlate(plate);
        v.setDailyRate(price);
        v.setSeats(cmbSeatNumber != null ? cmbSeatNumber.getValue() : 4);
        v.setFuelType(FuelType.PETROL);
        v.setTransmission(TransmissionType.AUTOMATIC);
        v.setStatus(VehicleStatus.AVAILABLE);
        v.setImagePath(selectedImagePath);

        try {
            if (txtManufacturing != null && !txtManufacturing.getText().isBlank()) {
                v.setYear(Integer.parseInt(txtManufacturing.getText().trim()));
            } else {
                v.setYear(2023);
            }
        } catch (Exception ignored) {
            v.setYear(2023);
        }

        return v;
    }

    private void clearForm() {
        if (txtVehicleName != null) txtVehicleName.clear();
        if (txtPlateNumber != null) txtPlateNumber.clear();
        if (txtChassisNumber != null) txtChassisNumber.clear();
        if (txtPrice != null) txtPrice.clear();
        if (txtManufacturing != null) txtManufacturing.clear();
        clearBtnClicked(null);
    }
}

