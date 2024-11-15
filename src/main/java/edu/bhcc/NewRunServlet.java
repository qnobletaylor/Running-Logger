package edu.bhcc;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;


public class NewRunServlet extends HttpServlet {


  private record Run(String date, double distance, double time, double speed, String gps){}
  private ArrayList<Run> runs;
  private Connection database;


  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{

    try {
      database = DriverManager.getConnection("jdbc:sqlite:src/main/webapp/WEB-INF/running.db");
      insertRun(database, request);
      runs = getAllRuns(database);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }

    sendHtml(response, runs);
  }

  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{

    try {
      database = DriverManager.getConnection("jdbc:sqlite:src/main/webapp/WEB-INF/running.db");
      runs = getAllRuns(database);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }

    sendHtml(response, runs);
  }


  private void sendHtml(HttpServletResponse response, ArrayList<Run> runs) throws IOException {
    response.setContentType("text/html");
    response.setStatus(HttpServletResponse.SC_OK);

    PrintWriter out = response.getWriter();
    out.println("<!DOCTYPE html>");
    out.println("<html lang='en'>");
    out.println("<body>");
    out.println("<div style=height:450px;width:350px;overflow:auto>");
    for (Run run : runs) {
      out.println("<h1>" + run.date + "</h1>");
      out.println("<p>" + run.distance + "</p>");
      out.println("<p>" + run.time + "</p>");
      out.println("<p>" + run.speed + "</p>");
      out.println("<p>" + run.gps + "</p>");
    }
    out.println("</div>");
    out.println("<form action='/' method='post'>");
    out.println("<input type='submit' value='New Run'>");
    out.println("</form>");
    out.println("</body>");
  }


  private void insertRun(Connection connection, HttpServletRequest request) throws SQLException{
    String date = request.getParameter("date");
    double distance = Double.parseDouble(request.getParameter("distance"));
    int timeHr = validateInt(request.getParameter("timeHr"));
    int timeMin = validateInt(request.getParameter("timeMin"));
    int timeSec = validateInt(request.getParameter("timeSec"));
    double time = timeHr * 3600 + timeMin * 60 + timeSec; // time stored in seconds
    String gps = request.getParameter("gps");


    PreparedStatement insert = connection.prepareStatement("INSERT INTO RUNS(Date, Distance, Time, GPS) VALUES(?, ?, ?, ?)");
    insert.setString(1, date);
    insert.setDouble(2, distance);
    insert.setDouble(3, time);
    insert.setString(4, gps);

    insert.executeUpdate();
  }

  private ArrayList<Run> getAllRuns(Connection connection) throws SQLException {
    ResultSet query = connection.createStatement().executeQuery("SELECT * FROM RUNS");
    ArrayList<Run> runs = new ArrayList<Run>();

    if(query.next()) {
      do {
        String queryDate = query.getString("Date");
        double queryDistance = query.getDouble("Distance");
        double queryTime = query.getDouble("Time");
        double querySpeed = query.getDouble("Speed");
        String queryGps = query.getString("GPS");

        runs.add(new Run(queryDate, queryDistance, queryTime, querySpeed, queryGps));
      } while(query.next());
    }
    return runs;
  }

  private int validateInt(String input) {
    try {
      return Integer.parseInt(input);
    } catch (NumberFormatException e) {
      System.out.println("Exception Thrown");
      return 0;
    }
  }
}
