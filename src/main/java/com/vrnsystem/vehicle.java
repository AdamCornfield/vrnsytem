/* Program By Adam Cornfield */

/*
Creates the public class vehicle which is used in interacting with and adding data to the TableViews
*/

package com.vrnsystem;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class vehicle {
    private SimpleStringProperty registration;
    private SimpleStringProperty make;
    private SimpleStringProperty model;
    private SimpleStringProperty year;
    private SimpleStringProperty offences;

    public vehicle(String registration, String make, String model, String year, String offences) {
        this.registration = new SimpleStringProperty(registration);
        this.make = new SimpleStringProperty(make);
        this.model = new SimpleStringProperty(model);
        this.year = new SimpleStringProperty(year);
        this.offences = new SimpleStringProperty(offences);
    }

    public String getRegistration() {
        return registration.get();
    }

    public String getMake() {
        return make.get();
    }

    public String getModel() {
        return model.get();
    }

    public String getYear() {
        return year.get();
    }

    public String getOffences() {
        return offences.get();
    }

    public void setRegistration(String registration) {
        this.registration = new SimpleStringProperty();
    }

    public void setMake(String make) {
        this.make = new SimpleStringProperty();
    }

    public void setModel(String model) {
        this.model = new SimpleStringProperty();
    }

    public void setYear(String year) {
        this.year = new SimpleStringProperty();
    }

    public void setOffences(String offences) {
        this.offences = new SimpleStringProperty();
    }
}
