package edu.bhcc;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;


public class NewRunServlet extends HttpServlet {


  private record Run(String date, double distance, double time, double speed, String stravaID){}
  private ArrayList<Run> runs;
  private Connection database;



  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{

    try {
      database = DriverManager.getConnection("jdbc:sqlite:src/main/webapp/WEB-INF/running.db");
      insertRun(database, request);
      runs = parseRuns(database);
      sendHtml(response, runs);
      database.close();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }

  }

  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{

    try {
      database = DriverManager.getConnection("jdbc:sqlite:src/main/webapp/WEB-INF/running.db");
      runs = parseRuns(database);
      sendHtml(response, runs);
      database.close();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }


  }


  private void sendHtml(HttpServletResponse response, ArrayList<Run> runs) throws IOException {
    response.setContentType("text/html");
    response.setStatus(HttpServletResponse.SC_OK);

    PrintWriter out = response.getWriter();
    out.println("<!DOCTYPE html>");
    out.println("<html lang='en'>");
    out.println("<body>");
    out.println("<div style=height:500px;width:450px;overflow:auto>");
    for (Run run : runs) {
      out.println("<h3>" + run.date + "</h3>");
      out.println("<p>Distance : " + String.format("%.2f", run.distance) + " miles</p>");
      out.println("<p>Duration : " + String.format("%d : %d : %d", ((int) run.time / 3600),
          ((int) run.time % 3600) / 60, ((int) run.time % 60)) + "</p>");
      out.println("<p>Speed    : " + String.format("%.2f", run.speed) + " min/mile</p>");
      if (run.stravaID != null) {
        out.println("<div class='strava-embed-placeholder' data-embed-type='activity' data-embed-id='"+run.stravaID+"' " +
            "data-style='standard'></div><script src='https://strava-embeds.com/embed.js'></script>\n");
      }
      out.println("<h2>--------------------------------------------------------</h2>");
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
    String stravaID = request.getParameter("stravaID").isEmpty() ? null : request.getParameter("stravaID");


    PreparedStatement insert = connection.prepareStatement("INSERT INTO RUNS(Date, Distance, Time, stravaID) VALUES" +
        "(?, ?, ?, ?)");
    insert.setString(1, date);
    insert.setDouble(2, distance);
    insert.setDouble(3, time);
    insert.setString(4, stravaID);

    insert.executeUpdate();
  }

  private ArrayList<Run> parseRuns(Connection connection) throws SQLException {
    ResultSet query = connection.createStatement().executeQuery("SELECT * FROM RUNS");
    ArrayList<Run> runs = new ArrayList<>();

    if(query.next()) {
      do {
        String queryDate = query.getString("Date");
        double queryDistance = query.getDouble("Distance");
        double queryTime = query.getDouble("Time");
        double querySpeed = query.getDouble("Speed");
        String queryID = query.getString("stravaID");

        runs.add(new Run(queryDate, queryDistance, queryTime, querySpeed, queryID));
      } while(query.next());
    }
    return runs;
  }

  private int validateInt(String input) {
    try {
      return Integer.parseInt(input);
    } catch (NumberFormatException e) {
      return 0;
    }
  }
}
