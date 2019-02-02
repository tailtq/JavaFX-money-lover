package com.moneylover.app.User;

import com.moneylover.Infrastructure.Exceptions.NotFoundException;
import com.moneylover.Modules.User.Controllers.UserController;
import com.moneylover.Modules.User.Entities.User;
import com.moneylover.app.BaseViewPresenter;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.io.IOException;
import java.sql.SQLException;

public class AuthenticationPresenter extends BaseViewPresenter {
    private StringProperty changeMainScene;

    @FXML
    private TextField name;

    @FXML
    private TextField email;

    @FXML
    private PasswordField password;

    @FXML
    private PasswordField passwordConfirmation;

    private com.moneylover.Modules.User.Controllers.UserController userController;

    public AuthenticationPresenter(StringProperty changeMainScene) throws SQLException, ClassNotFoundException {
        this.changeMainScene = changeMainScene;
        this.userController = new UserController();
    }

    public Scene loadSignInForm() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/moneylover/pages/authentication/signin.fxml"));
        fxmlLoader.setController(this);
        VBox parent = fxmlLoader.load();

        return new Scene(parent);
    }

    @FXML
    private void changeScene() {
        if (this.changeMainScene.get().equals("signin")) {
            this.changeMainScene.set("signup");
        } else {
            this.changeMainScene.set("signin");
        }
    }

    @FXML
    private void login() throws SQLException {
        String email = this.email.getText().trim();
        String password = this.password.getText().trim();
        if (email.isEmpty() || password.isEmpty()) {
            this.setFieldsNull(this.password);
            this.showErrorDialog("Email or password is not valid");
            return;
        }

        User user = new User(email, password);
        try {
            UserPresenter.setUser(this.userController.login(user));
            this.changeMainScene.set("transaction");
        } catch (NotFoundException e) {
            this.setFieldsNull(this.password);
            this.showErrorDialog("Email or password is not valid");
        }
    }

    public Scene loadSignUpForm() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/moneylover/pages/authentication/signup.fxml"));
        fxmlLoader.setController(this);
        VBox parent = fxmlLoader.load();

        return new Scene(parent);
    }

    @FXML
    public void signUp() throws SQLException, NotFoundException {
        String name = this.name.getText().trim();
        String email = this.email.getText().trim();
        String password = this.password.getText().trim();
        String passwordConfirmation = this.passwordConfirmation.getText().trim();
        if (email.isEmpty() || name.isEmpty() || password.isEmpty() || passwordConfirmation.isEmpty()) {
            this.showErrorDialog("Please input all information!");
            return;
        }

        if (name.length() > 80) {
            this.showErrorDialog("Name is not valid");
            return;
        }
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException ex) {
            this.showErrorDialog("Email is not valid");
            return;
        }
        if (!password.equals(passwordConfirmation)) {
            this.setFieldsNull(this.password);
            this.showErrorDialog("Password Confirmation is invalid!");
            return;
        }

        User user = new User(name, email, password);

        try {
            this.userController.getUserByEmail(user.getEmail());
            this.setFieldsNull(this.password, this.passwordConfirmation);
            this.showErrorDialog("Email is used by another user!");
            return;
        } catch (NotFoundException e) {
            this.userController.create(user);
        }

        this.changeMainScene.set("signin");
    }
}
