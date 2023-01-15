/* Program By Adam Cornfield */

package com.vrnsystem;

import com.vrnsystem.vehicle;
import com.vrnsystem.fileSystem;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.fxml.Initializable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.*;
import java.util.*;
import javafx.event.*;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.nio.charset.StandardCharsets;

public class VRNView implements Initializable {
    //Loads all of the data from the two CSV data files into a 2D array list which will be used as a temporary database until it is saved again at the end
    private ArrayList<List<String>> VRNInfo = fileSystem.parseCSV("src/main/resources/VRNInfo.csv");
    private ObservableList<vehicle> data = FXCollections.observableArrayList();
    private ArrayList<List<String>> dailyInfo = fileSystem.parseCSV("src/main/resources/VRNDaily.csv");
    private ObservableList<vrnDaily> cameraData = FXCollections.observableArrayList();

    //Sets up before and after lists to enable reversion of changed data at the end.
    private ArrayList<List<String>> beforeData = new ArrayList<List<String>>();
    private ArrayList<List<String>> afterData = new ArrayList<List<String>>();

    //Defines some basic variables to be used throughout the program
    public static String accessLevel;
    private String currentEditedVRN;
    private int editsMade = 0;

    //The Below Section will add all of the on screen elements as editable variables that the program can access in this class from the FXML file which defines the visual layout of the document.
    @FXML private TableView<vehicle> VehicleInfoTable;
    @FXML private TableColumn<vehicle, String> registration;
    @FXML private TableColumn<vehicle, String> make;
    @FXML private TableColumn<vehicle, String> model;
    @FXML private TableColumn<vehicle, String> year;
    @FXML private TableColumn<vehicle, String> offences;

    @FXML private TableView<vrnDaily> dailyVRNTable;
    @FXML private TableColumn<vrnDaily, String> cameraReg;
    @FXML private TableColumn<vrnDaily, String> cameraSpeed;

    @FXML private VBox vBoxBtn;

    @FXML private Button btnRemoveEntry;
    @FXML private Button btnEndShift;

    @FXML private Button btnEdit;
    @FXML private TextField inpEditMake;
    @FXML private TextField inpEditModel;
    @FXML private TextField inpEditYear;
    @FXML private TextField inpEditOffences;
    @FXML private Button btnEditSaveChanges;

    @FXML private Label labelEditMake;
    @FXML private Label labelEditModel;
    @FXML private Label labelEditYear;

    @FXML private Button btnAddEntry;
    @FXML private TextField inpAddRegistration;
    @FXML private TextField inpAddMake;
    @FXML private TextField inpAddModel;
    @FXML private TextField inpAddYear;
    @FXML private TextField inpAddOffences;
    @FXML private Button btnAddSave;

    @FXML private Label labelAddRegistration;
    @FXML private Label labelAddMake;
    @FXML private Label labelAddModel;
    @FXML private Label labelAddYear;
    
    @FXML private TextField inpVRNRegistration;
    @FXML private TextField inpVRNMake;
    @FXML private TextField inpVRNModel;
    @FXML private TextField inpVRNYear;
    @FXML private TextField inpVRNOffences;
    @FXML private Button btnAddOffenceToRecords;

    @FXML private HBox editBox;
    @FXML private HBox addBox;
    @FXML private VBox cameraVbox;

    @FXML private Text editsMadeText;

    //Simple method which will simple increase a counter of edits made and update an onscreen label
    private void addEdit () {
        editsMade++;
        editsMadeText.setText("Number of Edits Made: " + editsMade);
    }

    //Main Method to start the program, this is the entry point into the whole file
    public void initialize(URL location, ResourceBundle resources) {
        //Sets edit and remove buttons to disabled by default until a user clicks on an entry in the table
        btnEdit.setDisable(true);
        btnRemoveEntry.setDisable(true);
        
        //When this page was opened the login page passed whether the user was an administrator or not, if they are not an admin then they are not able to edit anything and these elements are hidden.
        if (accessLevel.equals("USER")) {
            vBoxBtn.setVisible(false);
            cameraVbox.setVisible(false);
        }
        
        //Sets the cell factory which enables data to be added to both of the tables
        registration.setCellValueFactory(new PropertyValueFactory<vehicle, String>("Registration"));
        make.setCellValueFactory(new PropertyValueFactory<vehicle, String>("Make"));
        model.setCellValueFactory(new PropertyValueFactory<vehicle, String>("Model"));
        year.setCellValueFactory(new PropertyValueFactory<vehicle, String>("Year"));
        offences.setCellValueFactory(new PropertyValueFactory<vehicle, String>("Offences"));

        cameraReg.setCellValueFactory(new PropertyValueFactory<vrnDaily, String>("Registration"));
        cameraSpeed.setCellValueFactory(new PropertyValueFactory<vrnDaily, String>("Speed"));
        
        //Calls 2 functions, each of them will refresh the table data and synchronize it with the 2D Array list
        refreshTable();
        refreshDailyTable();
        
        //Will open the edit box below the table by making it visible and giving it height.
        btnEdit.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent event) {
                closeAllTabs();
                editBox.setVisible(true);
                editBox.setPrefHeight(100);

                //Resets all of the error labels to invisible
                labelEditMake.setVisible(false);
                labelEditModel.setVisible(false);
                labelEditYear.setVisible(false);
            }
        });

        //Will take all of the values from the text boxes in the edit pane and submit those changes to the 2D ArrayList
        btnEditSaveChanges.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent event) {
                labelEditMake.setVisible(false);
                labelEditModel.setVisible(false);
                labelEditYear.setVisible(false);

                //Will check each of the text boxes to make sure they are not empty, if they are it will be marked as a non pass and an error will appear below the box.
                boolean pass = true;

                if (inpEditMake.getText().isEmpty()) {
                    labelEditMake.setVisible(true);
                    pass = false;
                }

                if (inpEditModel.getText().isEmpty()) {
                    labelEditModel.setVisible(true);
                    pass = false;
                }

                if (inpEditYear.getText().isEmpty()) {
                    labelEditYear.setVisible(true);
                    pass = false;
                }

                //If it passed all checks the program will continue
                if (pass == true) {
                    //Creates an alert popup on screen to make sure they want to apply the changes they made
                    Alert alert = new Alert(AlertType.CONFIRMATION, "Are you sure you want to update vehicle " + currentEditedVRN + " to set:\nYear = " + inpEditYear.getText() + "\nMake = " + inpEditMake.getText() + "\nModel = " + inpEditModel.getText() + "\nOffences = " + inpEditOffences.getText(), ButtonType.NO, ButtonType.YES);
                    alert.showAndWait();
    
                    if (alert.getResult() == ButtonType.YES) {
                        //Will add the unedited data to before list for later review
                        beforeData.add(getVRNData(currentEditedVRN));

                        //Adds data to the new list for later review
                        List<String> newVRNListData = new ArrayList<String>();
                        newVRNListData.add(currentEditedVRN);
                        newVRNListData.add(inpEditMake.getText());
                        newVRNListData.add(inpEditModel.getText());
                        newVRNListData.add(inpEditYear.getText());
                        newVRNListData.add(inpEditOffences.getText());

                        afterData.add(newVRNListData);
    
                        //Saves data to the 2D Array by using a standard method
                        VRNInfo = fileSystem.editListrow(VRNInfo, currentEditedVRN, inpEditMake.getText(), inpEditModel.getText(), inpEditYear.getText(), inpEditOffences.getText());
                        
                        //Resets labels for next time
                        labelEditMake.setVisible(false);
                        labelEditModel.setVisible(false);
                        labelEditYear.setVisible(false);
    
                        //Increase counter and refresh the page for more edits
                        addEdit();
                        closeAllTabs();
                        refreshTable();
                    }
                }
            }
        });

        //Handles when a user opens the add box
        btnAddEntry.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent event) {
                //Makes the box visible
                closeAllTabs();
                addBox.setVisible(true);
                addBox.setPrefHeight(100);

                //Resets all of the error labels to invisible
                labelAddRegistration.setVisible(false);
                labelAddMake.setVisible(false);
                labelAddModel.setVisible(false);
                labelAddYear.setVisible(false);

                //Clears all previous old data
                inpAddRegistration.clear();
                inpAddMake.clear();
                inpAddModel.clear();
                inpAddYear.clear();
                inpAddOffences.clear();
            }
        });

        btnAddSave.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent event) {
                labelAddRegistration.setVisible(false);
                labelAddMake.setVisible(false);
                labelAddModel.setVisible(false);
                labelAddYear.setVisible(false);

                //Checks all data to make sure it conforms to expectations
                boolean pass = true;

                if (inpAddRegistration.getText().isEmpty() || inpAddRegistration.getText().length() != 7) {
                    labelAddRegistration.setVisible(true);
                    pass = false;
                }

                if (inpAddMake.getText().isEmpty()) {
                    labelAddMake.setVisible(true);
                    pass = false;
                }

                if (inpAddModel.getText().isEmpty()) {
                    labelAddModel.setVisible(true);
                    pass = false;
                }

                if (inpAddYear.getText().isEmpty()) {
                    labelAddYear.setVisible(true);
                    pass = false;
                }

                //Once the data is checked the program continues
                if (pass == true) {
                    //Provides error to confirm changes
                    Alert alert = new Alert(AlertType.CONFIRMATION, "Are you sure you want to add a " + inpAddYear.getText() + " " + inpAddMake.getText() + " " + inpAddModel.getText() + " Registration No: " + inpAddRegistration.getText() + " the database?", ButtonType.NO, ButtonType.YES);
                    alert.showAndWait();
    
                    if (alert.getResult() == ButtonType.YES) {
                        //Adds data to before list
                        beforeData.add(new ArrayList<String>());
    
                        //Adds data to a new List to then put into the after list
                        List<String> newVRNListData = new ArrayList<String>();
                        newVRNListData.add(inpAddRegistration.getText());
                        newVRNListData.add(inpAddMake.getText());
                        newVRNListData.add(inpAddModel.getText());
                        newVRNListData.add(inpAddYear.getText());
                        newVRNListData.add(inpAddOffences.getText());
    
                        afterData.add(newVRNListData);
    
                        //Saves data to 2D ArrayList
                        VRNInfo = fileSystem.addToList(VRNInfo, inpAddRegistration.getText(), inpAddMake.getText(), inpAddModel.getText(), inpAddYear.getText(), inpAddOffences.getText());
    
                        inpAddRegistration.clear();
                        inpAddMake.clear();
                        inpAddModel.clear();
                        inpAddYear.clear();
                        inpAddOffences.clear();

                        addEdit();
                        closeAllTabs();
                        refreshTable();
                    } 
                }
            }
        });

        //Handles the removal of data from the maint able
        btnRemoveEntry.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent event) {
                try {
                    //Gets data from table for a confirmation alert
                    vehicle selectedVehicle = VehicleInfoTable.getSelectionModel().getSelectedItem();
                    String registration = selectedVehicle.getRegistration();
                    String make = selectedVehicle.getMake();
                    String model = selectedVehicle.getModel();
                    String year = selectedVehicle.getYear();

                    Alert alert = new Alert(AlertType.CONFIRMATION, "Are you sure you want to delete a " + year + " " + make + " " + model + " Registration No: " + registration + " from the database?", ButtonType.NO, ButtonType.YES);
                    alert.showAndWait();

                    if (alert.getResult() == ButtonType.YES) {
                        //Adds data to before and after
                        beforeData.add(getVRNData(registration));
                        afterData.add(new ArrayList<String>());

                        //removes entry from list
                        VRNInfo = fileSystem.removeFromList(VRNInfo, registration);
    
                        addEdit();
                        closeAllTabs();
                        refreshTable();
                    } 
                } catch (Exception e) { }
            }
        });

        //Handles the end of shift scenario, will log out if the user has not made any edits or redirect to the confirmation page if they have
        btnEndShift.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent event) {
                if (editsMade > 0) {
                    Alert alert = new Alert(AlertType.CONFIRMATION, "Are you sure you want to save " + editsMade + " change(s) and log out?", ButtonType.NO, ButtonType.YES);
                    alert.showAndWait();
    
                    if (alert.getResult() == ButtonType.YES) {
                        try {
                            //Saves data to the public variables in target file so that the data can be carried on
                            verifyEdits.VRNInfo = VRNInfo;
                            verifyEdits.beforeData = beforeData;
                            verifyEdits.afterData = afterData;
                            App.setRoot("verifyEdits");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    //Logs the user out
                    try {
                        App.setRoot("MainPage");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        //Creates an event handler which activates every time the user clicks on the table, this is used to enable the edit and remove buttons
        VehicleInfoTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent event) {
                try {
                    //Gets value from the table and sets it to the input boxes for editing
                    vehicle selectedVehicle = VehicleInfoTable.getSelectionModel().getSelectedItem();
                    String registration = selectedVehicle.getRegistration();
                    String make = selectedVehicle.getMake();
                    String model = selectedVehicle.getModel();
                    String year = selectedVehicle.getYear();
                    String offences = selectedVehicle.getOffences();
    
                    currentEditedVRN = registration;
                    inpEditMake.setText(make);
                    inpEditModel.setText(model);
                    inpEditYear.setText(year);
                    inpEditOffences.setText(offences);

                    //Enables the buttons
                    btnEdit.setDisable(false);
                    btnRemoveEntry.setDisable(false);
                } catch (Exception e) { }
            }
        });

        //Handles the second table with the camera records
        dailyVRNTable.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent event) {
                inpVRNRegistration.setText("");
                inpVRNMake.setText("");
                inpVRNModel.setText("");
                inpVRNYear.setText("");
                inpVRNOffences.setText("");

                try {
                    //Will check the main table's values to find any matching registration number, if it is found it will then display that data to the user
                    vrnDaily selectedReg = dailyVRNTable.getSelectionModel().getSelectedItem();
                    String regNo = selectedReg.getRegistration();
                    for (int i = 0; i < VRNInfo.size(); i++) {
                        if (regNo.equals(VRNInfo.get(i).get(0))) {
                            inpVRNRegistration.setText(VRNInfo.get(i).get(0));
                            inpVRNMake.setText(VRNInfo.get(i).get(1));
                            inpVRNModel.setText(VRNInfo.get(i).get(2));
                            inpVRNYear.setText(VRNInfo.get(i).get(3));

                            //This section is in a try since sometimes there is not data in the 4th entry so this prevents the whole program crashing as a result of that
                            try {
                                inpVRNOffences.setText(VRNInfo.get(i).get(4));
                            } catch (Exception e) {
                                inpVRNOffences.setText("");
                            }
                        } else {

                        }
                    }
                } catch (Exception e) {
                }
            }
        });

        //Will add a speeding offence to the selected vehicle
        btnAddOffenceToRecords.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent event) {
                Alert alert = new Alert(AlertType.CONFIRMATION, "Are you sure you want to add a speeding offence to " + inpVRNRegistration.getText() + "?", ButtonType.NO, ButtonType.YES);
                alert.showAndWait();

                if (alert.getResult() == ButtonType.YES) {
                    for (int i = 0; i < VRNInfo.size(); i++) {
                        if (inpVRNRegistration.getText().equals(VRNInfo.get(i).get(0))) {
                            //Saves the before values to the before list
                            beforeData.add(getVRNData(inpVRNRegistration.getText()));

                            //This creates a new list which the edited data will get saved to
                            List<String> newVRNListData = getVRNData(inpVRNRegistration.getText());

                            //Will try to append to a pre-existing value, if that fails it will create a new value
                            try {
                                String currentOffences = VRNInfo.get(i).get(4);
                                currentOffences = currentOffences + ", Speeding";
                                
                                VRNInfo.get(i).set(4, currentOffences);
                                newVRNListData.set(4, VRNInfo.get(i).get(4));
                            } catch (Exception e) {
                                String currentOffences = "";
                                currentOffences = currentOffences + "Speeding";

                                VRNInfo.get(i).add(currentOffences);
                                newVRNListData.add(VRNInfo.get(i).get(4));
                            }

                            afterData.add(newVRNListData);

                            addEdit();
                        }
                    }
                    
                    refreshTable();
                } 
            }
        });
    }

    //Simple method which will close all of the tabs at the bottom of the screen
    private void closeAllTabs () {
        editBox.setVisible(false);
        editBox.setPrefHeight(0);
        addBox.setVisible(false);
        addBox.setPrefHeight(0);
    }

    //The method will clear the table of all it's data, disable the buttons and then add the data back to the table using the data from the VRNInfo list stored in memory.
    private void refreshTable () {
        data.clear();
        btnEdit.setDisable(true);
        btnRemoveEntry.setDisable(true);

        for (int i = 0; i < VRNInfo.size(); i++) {
            List<String> row = VRNInfo.get(i);
            try {
                data.add(new vehicle(row.get(0), row.get(1), row.get(2), row.get(3), row.get(4)));
            } catch (Exception e) {
                data.add(new vehicle(row.get(0), row.get(1), row.get(2), row.get(3), ""));
            }
        }

        VehicleInfoTable.setItems(data);
    }

    //Method takes a registration number and will get the entry of a vehicle with that registration number
    private List<String> getVRNData (String regNo) {
        List<String> newDataRow = new ArrayList<String>();
        for (int i = 0; i < VRNInfo.size(); i++) {
            if (regNo.equals(VRNInfo.get(i).get(0))) {
                newDataRow = VRNInfo.get(i);
            }
        }
        return new ArrayList<String>(newDataRow);
    }

    //Refreshes the secondary camera records table with new data from the memory values
    private void refreshDailyTable () {
        cameraData.clear();

        for (int i = 0; i < dailyInfo.size(); i++) {
            List<String> row = dailyInfo.get(i);
            try {
                cameraData.add(new vrnDaily(row.get(0), row.get(1)));
            } catch (Exception e) {
                cameraData.add(new vrnDaily(row.get(0), ""));
            }
        }

        dailyVRNTable.setItems(cameraData);
    }
}
