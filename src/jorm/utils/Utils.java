package jorm.utils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

@SuppressWarnings("unused")
public class Utils {

    public boolean ValidateTable(Connection connection)
            throws SQLException {
        DatabaseMetaData databaseMetaData = connection.getMetaData();
        String[] types = {"TABLE"};

        ResultSet tablesList = databaseMetaData.getTables(null, null, "%", types);
        while (tablesList.next()) {
            System.out.println(tablesList.getString("TABLE_NAME"));
        }

        return true;
    }

    public boolean GetAllPrimaryKeys(Connection connection)
            throws SQLException {
        DatabaseMetaData databaseMetaData = connection.getMetaData();
        String[] types = {"TABLE"};

        ResultSet tablesList = databaseMetaData.getTables(null, null, "%", types);
        while (tablesList.next()) {
            String tableName = tablesList.getString("TABLE_NAME");
            System.out.println("Table: " + tableName);
            try (ResultSet primaryKeys = databaseMetaData.getPrimaryKeys(null, null, tableName)) {
                while (primaryKeys.next()) {
                    System.out.println("Primary key: " + primaryKeys.getString("COLUMN_NAME"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    public boolean GetAllForeignKeys(Connection connection)
            throws SQLException {
        DatabaseMetaData databaseMetaData = connection.getMetaData();
        String[] types = {"TABLE"};

        ResultSet tablesList = databaseMetaData.getTables(null, null, "%", types);
        while (tablesList.next()) {
            String tableName = tablesList.getString("TABLE_NAME");
            try (ResultSet foreignKeys = databaseMetaData.getExportedKeys(null, null, tableName)) {
                while (foreignKeys.next()) {
                    System.out.println("Foreign key of Table: " + foreignKeys.getString("FKTABLE_NAME"));
                    System.out.println("Foreign key column: " + foreignKeys.getString("FKCOLUMN_NAME"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return true;
    }
}
