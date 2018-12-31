package Demo;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class Icon extends Application {
    private Parent root;

    private void loadFontAwesome() {
//        String css = getClass().getResource("../public/fonts/css/font-awesome.min.css").toExternalForm();
//        root.getStylesheets().add(css);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        this.loadFontAwesome();

        HBox buttonBox1 = new HBox();
        ToggleButton toggle1 = new ToggleButton();
        FontAwesomeIconView iconView = new FontAwesomeIconView(FontAwesomeIcon.ANGELLIST);
        toggle1.setGraphic(iconView);
        toggle1.setText("Hello World");
        buttonBox1.getChildren().add(toggle1);
        Button button = new Button();

        FlowPane iconsPane = new FlowPane(3, 3);
        iconsPane.getChildren().add(buttonBox1);
        Scene scene = new Scene(new ScrollPane(iconsPane), 500, 500);


//        root = FXMLLoader.load(getClass().getResource("../main/resources/views/sidebar.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
