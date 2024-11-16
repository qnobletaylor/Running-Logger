package edu.bhcc;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;

/**
 * This class handles both post and get requests for localhost:8080/Run. On post, it receives data from an html
 * form to input a Run record into the sqlite database. For both requests it will also read all data from the
 * database and send it to the browser to be displayed.
 */
public class RunServlet extends HttpServlet {

  /**
   * record class datatype to hold records from the sqlite database when being read from.
   * @param date of an activity
   * @param distance traveled during the activity
   * @param time spent during activity
   * @param speed at which the activity was performed
   * @param stravaID activity id for strava
   */
  private record Run(String date, double distance, double time, double speed, String stravaID){}
  private ArrayList<Run> runs; // holds the runs from database before being sent through the servlet response
  private Connection database; // connection to sqlite database


  /**
   * Reply to a post request, establishes connection to the database and inserts a new record from the html form sent
   * with the request. Next will respond by sending the updated list of runs to be viewed in the browser.
   */
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{

    try {
      database = DriverManager.getConnection("jdbc:sqlite:src/main/webapp/WEB-INF/running.db");

      insertRun(database, request); // create new database record
      runs = parseRuns(database); // initialize arrayList with all database records
      sendHtml(response, runs); // respond to client

      database.close(); // close sqlite connection
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }

  }

  /**
   * Handles a get request. So that if you enter localhost:8080/Run you'll be greeted with every activity that has
   * already by submitted into the database.
   */
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{

    try {
      database = DriverManager.getConnection("jdbc:sqlite:src/main/webapp/WEB-INF/running.db");

      runs = parseRuns(database); // initialize arrayList with all database records
      sendHtml(response, runs); //  respond to client

      database.close(); // close sqlite connection
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }


  }

  /**
   * Takes a servlet response as well as the runs ArrayList and sends the html line by line back to the client. Will
   * loop through the ArrayList so that all runs are added to the page to be displayed.
   */
  private void sendHtml(HttpServletResponse response, ArrayList<Run> runs) throws IOException {
    response.setContentType("text/html");
    response.setStatus(HttpServletResponse.SC_OK);

    PrintWriter out = response.getWriter();

    out.println("<!DOCTYPE html>");
    out.println("<html lang='en'>");
    out.println("<body>");
    out.println("<div style=height:500px;width:450px;overflow:auto>");

    for (Run run : runs) { // insert each run into the page
      out.println("<h3>" + run.date + "</h3>");
      out.println("<p>Distance : " + String.format("%.2f", run.distance) + " miles</p>");
      out.println("<p>Duration : " + String.format("%d : %d : %d", ((int) run.time / 3600),
          ((int) run.time % 3600) / 60, ((int) run.time % 60)) + "</p>");
      out.println("<p>Speed    : " + String.format("%.2f", run.speed) + " min/mile</p>");

      if (run.stravaID != null) { // Checks if there is a strava id present
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


  /**
   * Inserts a record into the sqlite database, takes care of parsing the html form data so that it will fit
   * within the database. The only variable which is checked for whether it's empty or not is stravaID, all others are
   * left unchecked because the html input itself already implements checking for valid data.
   */
  private void insertRun(Connection connection, HttpServletRequest request) throws SQLException{
    String date = request.getParameter("date");
    double distance = Double.parseDouble(request.getParameter("distance"));
    int timeHr = validateInt(request.getParameter("timeHr"));
    int timeMin = validateInt(request.getParameter("timeMin"));
    int timeSec = validateInt(request.getParameter("timeSec"));
    double time = timeHr * 3600 + timeMin * 60 + timeSec; // time stored in seconds
    String stravaID = request.getParameter("stravaID").isEmpty() ? null : request.getParameter("stravaID");

    // prepared statement for record insertion
    PreparedStatement insert = connection.prepareStatement("INSERT INTO RUNS(Date, Distance, Time, stravaID) VALUES" +
        "(?, ?, ?, ?)");
    insert.setString(1, date);
    insert.setDouble(2, distance);
    insert.setDouble(3, time);
    insert.setString(4, stravaID);

    insert.executeUpdate();
  }

  /**
   * Queries the database for all records in the Runs column and inserts it into the ArrayList of Run. The speed
   * attribute is derived by the database itself when a record is inserted.
   *
   * @return the ArrayList containing all records from database
   */
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

  /**
   * Somewhat of a failsafe to ensure that form data for time is actually an int (if the form was left blank it would
   * throw NumberFormatException, in which case we want to set that field to 0
   *
   * @return either the parsed int from string or zero if no int can be parsed
   */
  private int validateInt(String input) {
    try {
      return Integer.parseInt(input);
    } catch (NumberFormatException e) {
      return 0;
    }
  }
}
