package jorm.utils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    public static Date ParseDate(String date)
            throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd").parse(date);
    }

    public static Date ParseTime(String time)
            throws ParseException {
        return new SimpleDateFormat("HH:mm:ss").parse(time);
    }

    public static Date ParseDateTime(String datetime)
            throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").parse(datetime);
    }
}
