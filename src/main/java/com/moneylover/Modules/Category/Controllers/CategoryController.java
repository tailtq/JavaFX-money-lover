package com.moneylover.Modules.Category.Controllers;

import com.moneylover.Infrastructure.Exceptions.NotFoundException;
import com.moneylover.Modules.Category.Entities.Category;
import com.moneylover.Modules.Category.Services.CategoryService;

import java.sql.SQLException;
import java.util.ArrayList;

public class CategoryController {
    private CategoryService service;

    public CategoryController() throws SQLException, ClassNotFoundException {
        service = new CategoryService();
    }

    public ArrayList<Category> list() throws SQLException {
        ArrayList<Category> categories = this.service.list();

        return categories;
    }

    public ArrayList<Category> list(int typeId) throws SQLException {
        ArrayList<Category> categories = this.service.list(typeId);

        return categories;
    }

    public Category getDetail(int id) throws SQLException, NotFoundException {
        Category category = this.service.getDetail(id);

        return category;
    }

    public Category getDetail(String name) throws SQLException, NotFoundException {
        Category category = this.service.getDetail(name);

        return category;
    }

    public Category create(Category category) throws SQLException, NotFoundException {
        Category newCategory = this.service.create(category);

        return newCategory;
    }

    public boolean create(ArrayList<Category> categories) throws SQLException, NotFoundException {
        this.service.create(categories);

        return true;
    }

    public Category update(Category category, int id) throws SQLException, NotFoundException {
        Category updatedCategory = this.service.update(category, id);

        return updatedCategory;
    }

    public boolean delete(int id) throws SQLException {
        return this.service.deleteById(id);
    }
}
