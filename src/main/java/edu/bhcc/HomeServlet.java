package edu.bhcc;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


public class HomeServlet extends HttpServlet {


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
