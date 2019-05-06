package com.example;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Scanner;

public class SQLDate {

  private static final String DB_CONNECTION = "jdbc:postgresql://localhost:5432/SQLDateExample";
  private static final String DB_USER = "postgres";
  private static final String DB_PASSWORD = "postgres";

  private static Scanner scanner = new Scanner(System.in);

  public static void main(String[] args) throws SQLException, ParseException {
    try (Connection connection = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD)) {
      createDatabase(connection);
      Date date = getDate();
      insertWithPreparedStatement(connection, date);
      print(connection);
      date = getDate();
      insertWithStatement(connection, date);
      print(connection);
    }
  }

  private static void createDatabase(Connection connection) throws SQLException {
    try (Statement st = connection.createStatement()) {
      st.execute("DROP TABLE IF EXISTS Dates");
      st.execute("CREATE TABLE Dates(" +
          "id SERIAL, " +
          "field DATE NOT NULL )");
    }
  }

  private static Date getDate() throws ParseException {
    System.out.print("Input date(dd.MM.yyyy): ");
    String line = scanner.nextLine();
    java.util.Date parsedDate = new SimpleDateFormat("dd.MM.yyyy").parse(line);
    Date sqlDate = new Date(parsedDate.getTime());

    return sqlDate;
  }

  private static void insertWithStatement(Connection connection, Date date) throws SQLException {
    String formattedDate = "'" + date.toString() + "'";
    System.out.println("formattedDate = " + formattedDate);
    String sql = "INSERT INTO Dates(field) VALUES ( " + formattedDate + " )";
    try (Statement st = connection.createStatement()) {
      st.execute(sql);
    }
  }

  private static void insertWithPreparedStatement(Connection connection, Date date) throws SQLException {
    String sql = "INSERT INTO Dates(field) VALUES ( ? )";
    try (PreparedStatement st = connection.prepareStatement(sql)) {
      st.setDate(1, date);
      st.execute();
    }
  }

  private static void print(Connection connection) throws SQLException {
    try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM Dates")) {
      try (ResultSet rs = ps.executeQuery()) {
        printResultSet(rs);
      }
    }
  }

  private static void printResultSet(ResultSet rs) throws SQLException {
    ResultSetMetaData md = rs.getMetaData();

    for (int i = 1; i <= md.getColumnCount(); i++) {
      System.out.print("\t" + md.getColumnName(i) + "\t\t");
    }
    System.out.println();
    while (rs.next()) {
      for (int i = 1; i <= md.getColumnCount(); i++) {
        System.out.print("\t" + rs.getString(i) + "\t\t");
      }
      System.out.println();
    }
  }
}
