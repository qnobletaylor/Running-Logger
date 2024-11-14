package edu.bhcc;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Hello, World Servlet.
 */
public class HelloServlet extends HttpServlet {

    /**
     * Process an HTTP Request.
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);

        //  Output a Hello Message
        PrintWriter writer = response.getWriter();
        

        try (Scanner sc = new Scanner(new File("src/main/java/edu/bhcc/index.html"))){
            while (sc.hasNextLine()) {
                writer.println(sc.nextLine());
            }
        }
    }
}
