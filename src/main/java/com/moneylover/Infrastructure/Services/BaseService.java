package com.moneylover.Infrastructure.Services;

import java.sql.*;

abstract public class BaseService {
    protected Connection connection;

    protected Statement statement;

    public BaseService() throws SQLException, ClassNotFoundException {
        connection = connectToDB();
    }

    abstract protected String getTable();

    private Connection connectToDB() throws ClassNotFoundException, SQLException {
        String username = "sa";
        String password = "Abcd@1234";
        String url = "jdbc:sqlserver://localhost:1433;databaseName=moneylover";
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        Connection connection = DriverManager.getConnection(url, username, password);

        return connection;
    }

    protected Statement getStatement() throws SQLException {
        return connection.createStatement();
    }

    protected PreparedStatement getPreparedStatement(String statementString) throws SQLException {
        return connection.prepareStatement(statementString);
    }

    protected void closeConnection() throws SQLException {
        statement.close();
        connection.close();
    }

    protected ResultSet get(String... args) throws SQLException {
        String condition = "";

        if (args.length > 0) {
            condition = "WHERE ";

            for (String arg: args) {
                condition += arg + " ";
            }
        }

        String query = "SELECT * FROM " + getTable() + " " + condition + " ORDER BY created_at DESC";
        statement = getStatement();
        ResultSet resultSet = statement.executeQuery(query);

        return resultSet;
    }

    protected ResultSet _getById(int id) throws SQLException {
        String query = "SELECT * FROM " + getTable() + " WHERE id = " + id;
        statement = getStatement();
        ResultSet resultSet = statement.executeQuery(query);

        return resultSet;
    }

    public boolean deleteById(int id) throws SQLException {
        String statementString = "DELETE FROM " + getTable() + " WHERE id = " + id;
        statement = getStatement();
        boolean result = statement.execute(statementString);

        closeConnection();

        return result;
    }
}
