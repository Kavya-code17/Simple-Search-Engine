package com.Accio;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

//we are making com.Accio.Search class our servlet by this annotation
@WebServlet("/Search") //  "/search"   is the path or route of the servlet
//we are inheriting the property of http Servlet

public class Search extends HttpServlet {
    //we will create request object  and response object using the doGet function
    protected void doGet (HttpServletRequest request , HttpServletResponse response)throws IOException {
        //we are getting the request in the form of text
        // we are creating a search keyword for further actions like response
        // Getting keyword from frontend
        String keyword = request.getParameter("keyword");
        // Get the results from database
        // Setting up connection to database
        Connection connection = DatabaseConnection.getConnection();
        //Use the connection instance to run the query
        try {
            // Store the query of user
            PreparedStatement preparedStatement = connection.prepareStatement("Insert into history values(?, ?);");
            preparedStatement.setString(1, keyword);
            preparedStatement.setString(2, "http://localhost:8080/SearchEngine/Search?keyword="+keyword);
            preparedStatement.executeUpdate();

            // Getting results after running the ranking query
            ResultSet resultSet = connection.createStatement().executeQuery("select pageTitle, pageLink, (length(lower(pageText))-length(replace(lower(pageText), '" + keyword.toLowerCase() + "', '')))/length('" + keyword.toLowerCase() + "') as countoccurence from pages order by countoccurence desc limit 30");

            // Store results in array
            ArrayList<SearchResult> results = new ArrayList<SearchResult>();
            // Iterate results over resultset
            // Transferring values from resultset to results arraylist
            while (resultSet.next()) {
                SearchResult searchResult = new SearchResult();
                searchResult.setTitle(resultSet.getString("pageTitle"));
                searchResult.setLink(resultSet.getString("pageLink"));
                results.add(searchResult);
            }

            // Displaying results arraylist in console
            for (SearchResult result : results) {
                System.out.println(result.getTitle() + "\n" + result.getLink() + "\n");
            }
            // Set parameter of request
            request.setAttribute("results", results);
            // The request is forwarded to the front end : search.jsp file
            request.getRequestDispatcher("search.jsp").forward(request, response);

            //we are generating a html response instead of printing it in the console
            //for that first we have to set  the content to  html
            response.setContentType("text/html");

            //it will generate the response using search keyword here
            PrintWriter out = response.getWriter();
        } catch (SQLException | ServletException sqlException) {
            sqlException.printStackTrace();
        }
    }


        //using this we are able to print the required search keyword:
        //this is an example of interaction between frontend and backend
        //out.println("<h3> This is the keyword you have entered : " +keyword+"</h3>");
    }
