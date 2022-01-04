package jorm.utils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Utils {

    public boolean validateTable(Connection connection) throws SQLException {

        DatabaseMetaData databaseMetaData = connection.getMetaData();
        String[] types = {"TABLE"};

        ResultSet tablesList = databaseMetaData.getTables(null, null, "%", types);
        while (tablesList.next()) {
            System.out.println(tablesList.getString("TABLE_NAME"));
        }

        return true;
    }

    public boolean getAllPrimaryKeys(Connection connection) throws SQLException {

        DatabaseMetaData databaseMetaData = connection.getMetaData();
        String[] types = {"TABLE"};

        ResultSet tablesList = databaseMetaData.getTables(null, null, "%", types);
        while (tablesList.next()) {
            String tableName = tablesList.getString("TABLE_NAME");
            System.out.println("Table: " + tableName);
            try (ResultSet primarykeys = databaseMetaData.getPrimaryKeys(null, null, tableName)) {
                while (primarykeys.next()) {
                    System.out.println("Primary key: " + primarykeys.getString("COLUMN_NAME"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    public boolean getAllForeignKeys(Connection connection) throws SQLException {

        DatabaseMetaData databaseMetaData = connection.getMetaData();
        String[] types = {"TABLE"};

        ResultSet tablesList = databaseMetaData.getTables(null, null, "%", types);
        while (tablesList.next()) {
            String tableName = tablesList.getString("TABLE_NAME");
            try (ResultSet foreignkeys = databaseMetaData.getExportedKeys(null, null, tableName)) {
                while (foreignkeys.next()) {
                    System.out.println("Foreign key of Table: " + foreignkeys.getString("FKTABLE_NAME"));
                    System.out.println("Foreign key column: " + foreignkeys.getString("FKCOLUMN_NAME"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return true;
    }
}
