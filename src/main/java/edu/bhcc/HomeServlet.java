package edu.bhcc;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet for displaying data at the home address of localhost:8080. Handles both get and post requests. In both
 * cases the same html will be sent so there is a file located at "src/main/webapp/WEB-INF/index.html" which contains
 * the page source. I felt like writing a file and then reading the file with a Scanner was a much cleaner way to
 * send the response. I probably would have done a html source file for the RunServlet as well but this was before
 * our class on 11/15 learning about java templating, so I decided to stick with what we'd learned previously.
 */
public class HomeServlet extends HttpServlet {


    /**
     * Replies to the client with html on get request.
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);

        PrintWriter writer = response.getWriter();

        try (Scanner sc = new Scanner(new File("src/main/webapp/WEB-INF/index.html"))){
            while (sc.hasNextLine()) {
                writer.println(sc.nextLine());
            }
        }
    }

    /**
     * Replies to client with html on post request. I realize this could have been not included, but I made the button
     * on localhost:8080/Run have a post method.
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws IOException {

        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);

        PrintWriter writer = response.getWriter();

        try (Scanner sc = new Scanner(new File("src/main/webapp/WEB-INF/index.html"))){
            while (sc.hasNextLine()) {
                writer.println(sc.nextLine());
            }
        }
    }
}
