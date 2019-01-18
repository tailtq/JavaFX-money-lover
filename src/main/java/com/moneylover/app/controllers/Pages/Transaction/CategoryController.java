package com.moneylover.app.controllers.Pages.Transaction;

import com.moneylover.Modules.Category.Entities.Category;
import com.moneylover.Modules.SubCategory.Controllers.SubCategoryController;
import com.moneylover.Modules.SubCategory.Entities.SubCategory;
import com.moneylover.Modules.Type.Controllers.TypeController;
import com.moneylover.Modules.Type.Entities.Type;
import com.moneylover.app.controllers.BaseViewController;
import com.moneylover.app.controllers.Contracts.DialogInterface;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

public class CategoryController extends BaseViewController implements DialogInterface {
    private IntegerProperty selectedType;

    private IntegerProperty selectedCategory;

    private IntegerProperty selectedSubCategory;

    private TypeController typeController;

    private com.moneylover.Modules.Category.Controllers.CategoryController categoryController;

    private SubCategoryController subCategoryController;

    private ArrayList<Type> types;

    private ArrayList<Category> categories;

    private ArrayList<SubCategory> subCategories;

    private ArrayList<Pair<Type, ArrayList<Pair<Category, ArrayList<SubCategory>>>>> combinedTypes;


    public CategoryController(IntegerProperty selectedType, IntegerProperty selectedCategory, IntegerProperty selectedSubCategory) throws SQLException, ClassNotFoundException {
        this.selectedType = selectedType;
        this.selectedCategory = selectedCategory;
        this.selectedSubCategory = selectedSubCategory;
        this.types = this.loadTypes();
        this.categories = this.loadCategories();
        this.subCategories = this.loadSubCategories();
        this.combineCategories(types, categories, (ArrayList<SubCategory>) subCategories.clone());
    }

    public CategoryController(StringProperty selectedCategory, ArrayList<Type> types, ArrayList<Category> categories, ArrayList<SubCategory> subCategories) {
//        this.selectedCategory = selectedCategory;

    }

    private ArrayList<Type> loadTypes() throws SQLException, ClassNotFoundException {
        this.typeController = new TypeController();

        return this.typeController.list();
    }

    private ArrayList<Category> loadCategories() throws SQLException, ClassNotFoundException {
        this.categoryController = new com.moneylover.Modules.Category.Controllers.CategoryController();

        return this.categoryController.list();
    }

    private ArrayList<SubCategory> loadSubCategories() throws SQLException, ClassNotFoundException {
        this.subCategoryController = new SubCategoryController();

        return this.subCategoryController.list();
    }

    private void combineCategories(ArrayList<Type> types, ArrayList<Category> categories, ArrayList<SubCategory> subCategories) {
        this.combinedTypes = new ArrayList<>();
        ArrayList<Pair<Category, ArrayList<SubCategory>>> combinedCategories = new ArrayList<>();
        ArrayList<SubCategory> combinedSubCategories;

        for (Category category: categories) {
            combinedSubCategories = new ArrayList<>();

            for (Iterator<SubCategory> it = subCategories.iterator(); it.hasNext();) {
                SubCategory subCategory = it.next();
                if (subCategory.getCategoryId() == category.getId()) {
                    combinedSubCategories.add(subCategory);
                    it.remove();
                }
            }

            combinedCategories.add(new Pair<>(category, combinedSubCategories));
        }

        for (Type type: types) {
            ArrayList<Pair<Category, ArrayList<SubCategory>>> combineType = new ArrayList<>();

            for (Iterator<Pair<Category, ArrayList<SubCategory>>> it = combinedCategories.iterator(); it.hasNext();) {
                Pair<Category, ArrayList<SubCategory>> combinedCategory = it.next();

                if (combinedCategory.getKey().getTypeId() == type.getId()) {
                    combineType.add(combinedCategory);
                    it.remove();
                }
            }

            this.combinedTypes.add(new Pair<>(type, combineType));
        }
    }

    /*========================== Draw ==========================*/
    @FXML
    private HBox buttonGroupTabs;

    @FXML
    private TabPane tabPaneTypes;

    @FXML
    private TreeView categoriesView;

    @FXML
    public void showCategoryDialog() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/moneylover/components/dialogs/choose-category.fxml"));
        fxmlLoader.setController(this);
        Parent parent = fxmlLoader.load();
        this.loadTreeViewCategories();

        this.createScreen(parent, "Choose Category", 330, 500);
    }

    private void loadTreeViewCategories() throws IOException {
        int i = 0;

        for (Pair<Type, ArrayList<Pair<Category, ArrayList<SubCategory>>>> type: this.combinedTypes) {
            FXMLLoader tabLoader = new FXMLLoader(getClass().getResource("/com/moneylover/components/tag-button.fxml"));
            tabLoader.setController(this);
            Button buttonTab = tabLoader.load();

            Tab newTab = new Tab();
            buttonTab.setUserData(i++);
            buttonTab.setOnAction(actionEvent -> {
                this.changeTab(actionEvent);
            });
            buttonTab.setText(type.getKey().getName());

            if (i == 1) {
                buttonTab.getStyleClass().add("active");
            }

            VBox container = new VBox();
            ArrayList<Pair<Category, ArrayList<SubCategory>>> categories = type.getValue();

            for (Pair<Category, ArrayList<SubCategory>> category: categories) {
                Category categoryDetail = category.getKey();
                VBox vBoxCategory = new VBox();
                VBox vBoxSubCategories = new VBox();
                FXMLLoader categoryLoader = new FXMLLoader(getClass().getResource("/com/moneylover/components/normal-button.fxml"));
                Button buttonCategory = categoryLoader.load();
                buttonCategory.setText(categoryDetail.getName());
                buttonCategory.getStyleClass().addAll("image-button", categoryDetail.getIcon());
                buttonCategory.setAlignment(Pos.TOP_LEFT);
                buttonCategory.setUserData(categoryDetail.getId());
                buttonCategory.setOnAction(actionEvent -> {
                    this.selectedType.set(categoryDetail.getTypeId());
                    this.selectedCategory.set(categoryDetail.getId());
                    this.closeScene(actionEvent);
                });

                vBoxCategory.getChildren().add(buttonCategory);
                vBoxCategory.getStyleClass().add("category");
                vBoxSubCategories.getStyleClass().add("subcategories");

                for (SubCategory subCategory: category.getValue()) {
                    FXMLLoader subCategoryLoader = new FXMLLoader(getClass().getResource("/com/moneylover/components/normal-button.fxml"));
                    Button buttonSubCategory = subCategoryLoader.load();
                    buttonSubCategory.setText(subCategory.getName());
                    buttonSubCategory.getStyleClass().addAll("image-button", subCategory.getIcon());
                    buttonSubCategory.setAlignment(Pos.TOP_LEFT);
                    buttonSubCategory.setUserData(subCategory.getId());
                    buttonSubCategory.setOnAction(actionEvent -> {
                        this.selectedType.set(subCategory.getTypeId());
                        this.selectedCategory.set(subCategory.getId());
                        this.selectedSubCategory.set(subCategory.getId());
                        this.closeScene(actionEvent);
                    });

                    vBoxSubCategories.getChildren().add(buttonSubCategory);
                }

                vBoxCategory.getChildren().add(vBoxSubCategories);
                container.getChildren().add(vBoxCategory);
            }

            ScrollPane scrollPane = new ScrollPane(container);
            scrollPane.setFitToWidth(true);
            newTab.setContent(scrollPane);
            this.tabPaneTypes.getTabs().add(newTab);
            this.buttonGroupTabs.getChildren().add(buttonTab);
        }
    }

    void handleSelectedCategoryId(IntegerProperty selectedButton, Button selectCategory, String type) {
        selectedButton.addListener((observableValue, oldValue, newValue) -> {
            ObservableList<String> classes = selectCategory.getStyleClass();
            int i = 0;
            System.out.println(type);
            for (String element: classes) {
                if (element.contains("i_")) {
                    classes.remove(i);
                    break;
                }
                i++;
            }

            if (newValue.intValue() == 0) {
                selectCategory.setText("");
                return;
            }

            if (type.equals("category")) {
                for (Category category : this.categories) {
                    if (category.getId() != newValue.intValue()) {
                        continue;
                    }
                    selectCategory.setText(category.getName());
                    classes.add(category.getIcon());

                    return;
                }
            } else {
                for (SubCategory subCategory : this.subCategories) {
                    if (subCategory.getId() != newValue.intValue()) {
                        continue;
                    }
                    selectCategory.setText(subCategory.getName());
                    classes.add(subCategory.getIcon());

                    return;
                }
            }
        });
    }

    @FXML
    private void changeTab(Event e) {
        this.activeTab(e, this.tabPaneTypes);
    }

    @FXML
    private void closeScene(Event e) {
        Node node = (Button) e.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        stage.close();
    }
}
