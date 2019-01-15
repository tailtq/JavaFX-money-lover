package com.moneylover.Modules.Time.Controllers;

import com.moneylover.Infrastructure.Exceptions.NotFoundException;
import com.moneylover.Modules.Time.Entities.Time;
import com.moneylover.Modules.Time.Services.TimeService;

import java.sql.SQLException;
import java.util.ArrayList;

public class TimeController {
    private TimeService service;

    public TimeController() throws SQLException, ClassNotFoundException {
        service = new TimeService();
    }

    public ArrayList<Time> list() throws SQLException {
        ArrayList<Time> times = this.service.list();

        return times;
    }

    public Time getDetail(int id) throws SQLException, NotFoundException {
        Time time = this.service.getDetail(id);

        return time;
    }

    public Time getDetail(int month, int year) throws SQLException, NotFoundException {
        Time time = this.service.getDetail(month, year);

        return time;
    }

    public Time create(Time time) throws SQLException, NotFoundException {
        Time newTime = this.service.create(time);

        return newTime;
    }

    public boolean create(ArrayList<Time> times) throws SQLException, NotFoundException {
        this.service.create(times);

        return true;
    }

    public Time update(Time time, int id) throws SQLException, NotFoundException {
        Time updatedTime = this.service.update(time, id);

        return updatedTime;
    }

    public boolean delete(int id) throws SQLException {
        return this.service.deleteById(id);
    }
}
