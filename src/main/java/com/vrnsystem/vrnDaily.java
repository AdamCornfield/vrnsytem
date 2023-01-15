/* Program By Adam Cornfield */

/*
Creates the public class vrnDaily which is used in interacting with and adding data to the TableViews from the camera records
*/

package com.vrnsystem;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class vrnDaily {
    private SimpleStringProperty registration;
    private SimpleStringProperty speed;

    public vrnDaily(String registration, String speed) {
        this.registration = new SimpleStringProperty(registration);
        this.speed = new SimpleStringProperty(speed);
    }

    public String getRegistration() {
        return registration.get();
    }

    public String getSpeed() {
        return speed.get();
    }

    public void setRegistration(String registration) {
        this.registration = new SimpleStringProperty();
    }

    public void setSpeed(String speed) {
        this.speed = new SimpleStringProperty();
    }
}