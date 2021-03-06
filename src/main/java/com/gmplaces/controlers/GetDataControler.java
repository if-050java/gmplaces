package com.gmplaces.controlers;

import com.gmplaces.models.Address;
import com.gmplaces.models.IDataService;
import com.google.gson.Gson;
import org.apache.log4j.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import static com.gmplaces.controlers.Utils.parseExceptions;


public class GetDataControler extends HttpServlet {

    final Logger logger = Logger.getLogger(GetDataControler.class);

    @Override
    public void doGet(HttpServletRequest request,HttpServletResponse response)
            throws IOException, ServletException
    {
        ServletContext ctx = request.getServletContext();
        IDataService dataService = (IDataService)ctx.getAttribute("dataservice");
        try{
            List<Address> list = dataService.getData();

            logger.debug("list size: "+String.valueOf(list.size()));
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(new Gson().toJson(list));
       } catch (Exception exp) {
            logger.error(parseExceptions(exp));
        }
    }


    @Override
    protected void doPost(HttpServletRequest request,HttpServletResponse response)
            throws ServletException, IOException
    {

        doGet(request, response);
    }



}
