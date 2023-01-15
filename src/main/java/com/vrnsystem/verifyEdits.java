/* Program By Adam Cornfield */

package com.vrnsystem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class verifyEdits {
    //Public variables allowing the calling method to pass in data for this page to work with
    public static ArrayList<List<String>> VRNInfo = new ArrayList<List<String>>();
    public static ArrayList<List<String>> beforeData = new ArrayList<List<String>>();
    public static ArrayList<List<String>> afterData = new ArrayList<List<String>>();

    //Value to track the progress of checked changes
    private int totalChanges = beforeData.size();

    //References from the FXML file allowing them to be edited in software
    @FXML private TextField inpRegBefore;
    @FXML private TextField inpMakeBefore;
    @FXML private TextField inpModelBefore;
    @FXML private TextField inpYearBefore;
    @FXML private TextField inpOffencesBefore;
    
    @FXML private TextField inpRegAfter;
    @FXML private TextField inpMakeAfter;
    @FXML private TextField inpModelAfter;
    @FXML private TextField inpYearAfter;
    @FXML private TextField inpOffencesAfter;

    @FXML private Button btnRevertChange;
    @FXML private Button btnAcceptChange;
    @FXML private Button btnFinish;

    @FXML private VBox beforeBox;
    @FXML private VBox afterBox;

    @FXML private Label labelBefore;
    @FXML private Label labelAfter;
    @FXML private Label labelEdit;
    
    //This will check both the before and after lists and determine based on that data whether the data was edited, added new, or removed from the records and then pass back that info as a string
    private String detectChange () {
        boolean beforeExists = true;
        boolean afterExists = true;

        try {
            beforeData.get(beforeData.size() - 1).get(1);
        } catch (Exception e) {
            beforeExists = false;
        }

        try {
            afterData.get(afterData.size() - 1).get(1);
        } catch (Exception e) {
            afterExists = false;
        }

        if (beforeExists && afterExists) {
            //There has been an edit

            return "EDIT";
        } else if (beforeExists && !afterExists) {
            //There has been a removal
            
            return "REMOVE";
        } else if (!beforeExists && afterExists) {
            //There has been an addition
            
            return "ADD";
        } else {
            return null;
        }
    }

    //Will hide either the before or after box to remove confusion around what data is being changed
    private void changeBoxVisibility () {
        String change = detectChange();
        beforeBox.setVisible(true);
        afterBox.setVisible(true);

        labelBefore.setText("Before");
        labelAfter.setText("After");

        if (change.equals("EDIT")) {
        } else if (change.equals("REMOVE")) {
            labelBefore.setText("Remove Record?");
            afterBox.setVisible(false);
        } else if (change.equals("ADD")) {
            labelAfter.setText("Add Record?");
            beforeBox.setVisible(false);
        }
    }

    //Will simple remove the checked value from the before and after databases and progresses the list onto the next value to be checked
    private void acceptChange () {
        beforeData.remove(beforeData.size() - 1);
        afterData.remove(afterData.size() - 1);

        if (beforeData.size() == 0) {
            btnFinish.setDisable(false);
            btnAcceptChange.setDisable(true);
            btnRevertChange.setDisable(true);
        } else {
            displayData();
        }
    }

    //Depending on what type of change was made this method will get the original data and restore it to that original state so no data was edited.
    private void revertChange () {
        String change = detectChange();

        if (change.equals("EDIT")) {
            //Checks all VRN Records for a matching registration number
            for (int i = 0; i < VRNInfo.size(); i++) {
                if (beforeData.get(beforeData.size() - 1).get(0).equals(VRNInfo.get(i).get(0))) {
                    VRNInfo.set(i, beforeData.get(beforeData.size() - 1));
                }
            }
        } else if (change.equals("REMOVE")) {
            //If data was removed it will be added back into the 2D Array List
            VRNInfo.add(beforeData.get(beforeData.size() - 1));
        } else if (change.equals("ADD")) {
            //Checks all VRN Records for a matching registration number
            for (int i = 0; i < VRNInfo.size(); i++) {
                if (afterData.get(afterData.size() - 1).get(0).equals(VRNInfo.get(i).get(0))) {
                    VRNInfo.remove(i);
                }
            }
        }
        
        //Progresses the verification onto the next change.
        beforeData.remove(beforeData.size() - 1);
        afterData.remove(afterData.size() - 1);

        if (beforeData.size() == 0) {
            btnFinish.setDisable(false);
            btnAcceptChange.setDisable(true);
            btnRevertChange.setDisable(true);
        } else {
            displayData();
        }
    }

    //Will get the next values in the before and after lists 
    private void displayData () {
        changeBoxVisibility();

        //Updates Edit numbeer at the top of the page
        int currentChange = totalChanges - (beforeData.size() - 1);
        labelEdit.setText("Edit: " + currentChange + "/" + totalChanges);

        //Sets before data to the input boxes
        try {
            inpRegBefore.setText(beforeData.get(beforeData.size() - 1).get(0));
            inpMakeBefore.setText(beforeData.get(beforeData.size() - 1).get(1));
            inpModelBefore.setText(beforeData.get(beforeData.size() - 1).get(2));
            inpYearBefore.setText(VRNInfo.get(beforeData.size() - 1).get(3));
            try {
                inpOffencesBefore.setText(beforeData.get(beforeData.size() - 1).get(4));
            } catch (Exception e) {
                inpOffencesBefore.setText("");
            }
        } catch (Exception e) {
            inpRegBefore.setText("");
            inpMakeBefore.setText("");
            inpModelBefore.setText("");
            inpYearBefore.setText("");
            inpOffencesBefore.setText("");
        }

        //Sets after data to the input boxes
        try {
            inpRegAfter.setText(afterData.get(afterData.size() - 1).get(0));
            inpMakeAfter.setText(afterData.get(afterData.size() - 1).get(1));
            inpModelAfter.setText(afterData.get(afterData.size() - 1).get(2));
            inpYearAfter.setText(afterData.get(afterData.size() - 1).get(3));
            try {
                inpOffencesAfter.setText(afterData.get(afterData.size() - 1).get(4));
            } catch (Exception e) {
                inpOffencesAfter.setText("");
            }
        } catch (Exception e) {
            inpRegAfter.setText("");
            inpMakeAfter.setText("");
            inpModelAfter.setText("");
            inpYearAfter.setText("");
            inpOffencesAfter.setText("");
        }
    }

    //Main entry point into the program
    public void initialize() {
        //Disables the finish button, only to be enabled once all checks are complete.
        btnFinish.setDisable(true);

        //Event handlers for the buttons
        btnRevertChange.setOnMouseClicked((EventHandler<? super MouseEvent>) new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent event) {
                revertChange();
            }
        });
        
        btnAcceptChange.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent event) {
                acceptChange();
            }
        });
        
        //When the program is finished it will save all data to the CSV file from the 2D Array List and then redirect to the main page/log the user out
        btnFinish.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent event) {
                fileSystem.listToCSV(VRNInfo, "src/main/resources/VRNInfo.csv");

                try {
                    App.setRoot("MainPage");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        

        displayData();
    }
}
