/* Program By Adam Cornfield */

/*
Main menu/Login Page
*/

package com.vrnsystem;

import javafx.event.*;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.*;
import java.util.Scanner;

public class MainPage {
    @FXML private TextField userName;
    @FXML private TextField passwordField;
    @FXML private Button loginButton;
    @FXML private Button registerButton;
    @FXML private Label labelIncorrectDetails;
    
    private File file = new File("src\\main\\resources\\logins.txt");

    public void initialize() {        
        loginButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override public void handle(ActionEvent event) {
                String user = userName.getText();
                String passw = passwordField.getText();

                try {
                    //Opens login information file
                    Scanner myReader = new Scanner(file);
                    
                    //Will repeat for every line in the file
                    while (myReader.hasNextLine()) {
                        //Saves data to local variables
                        String data = myReader.nextLine();
                        String storedName = data.split("\\~")[0];
                        String storedPassw = ceaser.ceaserDecrypt(data.split("\\~")[1], 5);
                        String perms = data.split("\\~")[2];

                        //If the input username and password matches both entries in the login file it will authorise the user
                        if (storedName.equals(user) && storedPassw.equals(passw)) {
                            //Determines where the user should go based on their access level.
                            if (perms.equals("ADMIN")) {
                                try {
                                    App.setRoot("accessSelector");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                try {
                                    VRNView.accessLevel = "USER";
                                    App.setRoot("VRNView");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            //Error respose for incorrect details
                            labelIncorrectDetails.setVisible(true);
                        }
                    }
                    myReader.close();
                } catch (FileNotFoundException e) {
                    System.out.println("An error has occured");
                    e.printStackTrace();
                }
            }
        });  

        //Opens register menu
        registerButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent event) {
                try {
                    switchToRegister();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void switchToRegister() throws IOException {
        App.setRoot("registerPage");
    }
}
