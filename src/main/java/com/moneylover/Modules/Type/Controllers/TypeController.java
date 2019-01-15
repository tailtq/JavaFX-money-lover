package com.moneylover.Modules.Type.Controllers;

import com.moneylover.Infrastructure.Exceptions.NotFoundException;
import com.moneylover.Modules.Type.Entities.Type;
import com.moneylover.Modules.Type.Services.TypeService;

import java.sql.SQLException;
import java.util.ArrayList;

public class TypeController {
    private TypeService service;

    public TypeController() throws SQLException, ClassNotFoundException {
        service = new TypeService();
    }

    public ArrayList<Type> list() throws SQLException {
        ArrayList<Type> types = this.service.list();

        return types;
    }

    public Type getDetail(int id) throws SQLException, NotFoundException {
        Type type = this.service.getDetail(id);

        return type;
    }

    public Type create(Type type) throws SQLException, NotFoundException {
        Type newType = this.service.create(type);

        return newType;
    }

    public boolean create(ArrayList<Type> types) throws SQLException, NotFoundException {
        this.service.create(types);

        return true;
    }

    public Type update(Type type, int id) throws SQLException, NotFoundException {
        Type updatedType = this.service.update(type, id);

        return updatedType;
    }

    public boolean delete(int id) throws SQLException {
        return this.service.deleteById(id);
    }
}
