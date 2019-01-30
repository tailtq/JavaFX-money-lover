package com.moneylover.app.Category;

import com.moneylover.Infrastructure.Constants.CommonConstants;
import com.moneylover.Modules.Category.Entities.Category;
import com.moneylover.Modules.SubCategory.Entities.SubCategory;
import com.moneylover.Modules.Type.Entities.Type;
import com.moneylover.app.BaseViewPresenter;
import com.moneylover.Infrastructure.Contracts.DialogInterface;
import javafx.beans.property.IntegerProperty;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class CategoryPresenter extends BaseViewPresenter implements DialogInterface {
    private boolean onlyExpenseCategories = false;

    private IntegerProperty selectedType;

    private IntegerProperty selectedCategory;

    private IntegerProperty selectedSubCategory;

    private static ArrayList<Type> types;

    private static ArrayList<Category> categories;

    private static ArrayList<SubCategory> subCategories;

    private static ArrayList<Pair<Type, ArrayList<Pair<Category, ArrayList<SubCategory>>>>> combinedTypes;

    public CategoryPresenter(IntegerProperty selectedType, IntegerProperty selectedCategory, IntegerProperty selectedSubCategory) {
        this.selectedType = selectedType;
        this.selectedCategory = selectedCategory;
        this.selectedSubCategory = selectedSubCategory;
    }

    public CategoryPresenter(IntegerProperty selectedCategory, IntegerProperty selectedSubCategory) {
        this.selectedCategory = selectedCategory;
        this.selectedSubCategory = selectedSubCategory;
    }

    public void setOnlyExpenseCategories(boolean onlyExpenseCategories) {
        this.onlyExpenseCategories = onlyExpenseCategories;
    }

    public static void setTypes(ArrayList<Type> types) {
        CategoryPresenter.types = types;
    }

    public static void setCategories(ArrayList<Category> categories) {
        CategoryPresenter.categories = categories;
    }

    public static void setSubCategories(ArrayList<SubCategory> subCategories) {
        CategoryPresenter.subCategories = subCategories;
    }

    public static void combineCategories() {
        CategoryPresenter.combinedTypes = new ArrayList<>();
        ArrayList<SubCategory> subCategories = (ArrayList) CategoryPresenter.subCategories.clone();
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

            CategoryPresenter.combinedTypes.add(new Pair<>(type, combineType));
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

        this.createScreen(parent, "Choose Budget", 330, 500);
    }

    private void loadTreeViewCategories() throws IOException {
        int i = 0;

        for (Pair<Type, ArrayList<Pair<Category, ArrayList<SubCategory>>>> type: this.combinedTypes) {
            FXMLLoader tabLoader = new FXMLLoader(getClass().getResource("/com/moneylover/components/buttons/tag-button.fxml"));
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
                String moneyType = categoryDetail.getMoneyType();

                if (this.onlyExpenseCategories
                        && !moneyType.equals(CommonConstants.EXPENSE)
                        && !moneyType.equals(CommonConstants.LOAN)
                        && !moneyType.equals(CommonConstants.LOAN_REPAYMENT)) {
                    continue;
                }

                VBox vBoxCategory = new VBox();
                VBox vBoxSubCategories = new VBox();
                FXMLLoader categoryLoader = new FXMLLoader(getClass().getResource("/com/moneylover/components/buttons/normal-button.fxml"));
                Button buttonCategory = categoryLoader.load();
                buttonCategory.setText(categoryDetail.getName());
                buttonCategory.getStyleClass().addAll("image-button", categoryDetail.getIcon());
                buttonCategory.setAlignment(Pos.TOP_LEFT);
                buttonCategory.setUserData(categoryDetail.getId());
                buttonCategory.setOnAction(actionEvent -> {
                    if (this.selectedType != null) {
                        this.selectedType.set(categoryDetail.getTypeId());
                    }

                    this.selectedCategory.set(categoryDetail.getId());
                    this.selectedSubCategory.set(0);
                    this.closeScene(actionEvent);
                });

                vBoxCategory.getChildren().add(buttonCategory);
                vBoxCategory.getStyleClass().add("category");
                vBoxSubCategories.getStyleClass().add("subcategories");

                for (SubCategory subCategory: category.getValue()) {
                    FXMLLoader subCategoryLoader = new FXMLLoader(getClass().getResource("/com/moneylover/components/buttons/normal-button.fxml"));
                    Button buttonSubCategory = subCategoryLoader.load();
                    buttonSubCategory.setText(subCategory.getName());
                    buttonSubCategory.getStyleClass().addAll("image-button", subCategory.getIcon());
                    buttonSubCategory.setAlignment(Pos.TOP_LEFT);
                    buttonSubCategory.setUserData(subCategory.getId());
                    buttonSubCategory.setOnAction(actionEvent -> {
                        if (this.selectedType != null) {
                            this.selectedType.set(subCategory.getTypeId());
                        }

                        this.selectedCategory.set(subCategory.getCategoryId());
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

    public void handleSelectedCategoryId(IntegerProperty selectedButton, Button selectCategory, String type) {
        selectedButton.addListener((observableValue, oldValue, newValue) -> {
            ObservableList<String> classes = selectCategory.getStyleClass();

            if (newValue.intValue() == 0) {
                return;
            }

            int i = 0;

            for (String element: classes) {
                if (element.contains("i_")) {
                    classes.remove(i);
                    break;
                }
                i++;
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
    public void closeScene(Event e) {
        DialogInterface.closeScene(e);
    }
}
