package main.java.com.tailtq.Components;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Button;

public class ExtendedButton extends Button {
    public ExtendedButton() {
        super();
        styleProperty().set("-fx-background-color: red;");


    }

}
