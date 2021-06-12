package edu.ifrs.demo.client;

import java.io.IOException;

import javax.annotation.Resource;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/go")
public class HelloServlet extends HttpServlet {

    @Resource(lookup = "java:app/demo/HelloStateless")
    private HelloStateless ejb;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ejb.send();
        ejb.test();
    }

}
