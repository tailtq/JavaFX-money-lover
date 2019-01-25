package com.moneylover.Modules.SubCategory.Controllers;

import com.moneylover.Infrastructure.Exceptions.NotFoundException;
import com.moneylover.Modules.SubCategory.Entities.SubCategory;
import com.moneylover.Modules.SubCategory.Services.SubCategoryService;

import java.sql.SQLException;
import java.util.ArrayList;

public class SubCategoryController {
    private SubCategoryService service;

    public SubCategoryController() throws SQLException, ClassNotFoundException {
        service = new SubCategoryService();
    }

    public ArrayList<SubCategory> list() throws SQLException {
        ArrayList<SubCategory> subCategories = this.service.list();

        return subCategories;
    }

    public ArrayList<SubCategory> list(int typeId) throws SQLException {
        ArrayList<SubCategory> subCategories = this.service.list(typeId);

        return subCategories;
    }

    public SubCategory getDetail(int id) throws SQLException, NotFoundException {
        SubCategory subCategory = this.service.getDetail(id);

        return subCategory;
    }

    public SubCategory create(SubCategory subCategory) throws SQLException, NotFoundException {
        SubCategory newSubCategory = this.service.create(subCategory);

        return newSubCategory;
    }

    public boolean create(ArrayList<SubCategory> subCategories) throws SQLException, NotFoundException {
        this.service.create(subCategories);

        return true;
    }

    public SubCategory update(SubCategory subCategory, int id) throws SQLException, NotFoundException {
        SubCategory updatedSubCategory = this.service.update(subCategory, id);

        return updatedSubCategory;
    }

    public boolean delete(int id) throws SQLException {
        return this.service.deleteById(id);
    }
}
