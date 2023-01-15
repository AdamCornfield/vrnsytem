/* Program By Adam Cornfield */

/*
Simple page will open if an Admin logs in to allow them to choose if they enter in Admin Mode or User Mode, in user mode they will not be able to edit anything or make any changes
*/

package com.vrnsystem;

import javafx.event.*;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.*;
import java.util.Scanner;

public class accessSelector {
    public static String data;

    @FXML private Button adminBtn;
    @FXML private Button userBtn;

    public void initialize() {
        adminBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent event) {
                try {
                    VRNView.accessLevel = "ADMIN";
                    App.setRoot("VRNView");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        userBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent event) {
                try {
                    VRNView.accessLevel = "USER";
                    App.setRoot("VRNView");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
