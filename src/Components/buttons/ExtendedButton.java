package Components.buttons;

import javafx.scene.control.Button;

public class ExtendedButton extends Button {
    public ExtendedButton() {
        super();
        styleProperty().set("-fx-background-color: red;");
    }
}
