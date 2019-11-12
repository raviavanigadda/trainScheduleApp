/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mobile;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * REST Web Service
 *
 * @author Ravilion
 */

@Path("main")
public class MainResource {

    Connection con= null;
    Statement stm= null;
    PreparedStatement ps = null;
    ResultSet rs= null;
    JSONObject mainObject = new JSONObject();
    JSONArray mainArray = new JSONArray();
    long now = Instant.now().toEpochMilli()/ 1000L;
    
    @Context
    private UriInfo context;

    /**
     * Creates a new instance of MainResource
     * @throws java.sql.SQLException
     */
    public MainResource() throws SQLException {
        try {
            Class.forName("oracle.jdbc.OracleDriver");
            con = DriverManager.getConnection("jdbc:oracle:thin:@144.217.163.57:1521:XE", "mad312team11", "anypw");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MainResource.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

     
    //Query 4 by - 1895212 - Sai Ravi Teja A
    //http://192.168.2.164:8080/trainSchedule/webresources/main/viewTrainRoutes&100
    //gets list of stations where each train will stop 
    @GET
    @Path("viewTrainRoutes&{id}")
    @Produces("application/json")
    public String getText4(@PathParam("id") int id) {

        try {
       
            stm = con.createStatement();
            String sql = "select distinct(r.routename), s.stationname from route r, train t, station s, details d where r.TRAINID = "+id+" and d.STATIONID = s.ID";
            rs = stm.executeQuery(sql);
           
            String routeName, stationName;
 
            if(rs.next()) {
                do {
                mainObject.clear();
                routeName = rs.getString("routename");
                stationName = rs.getString("stationname");
                mainObject.accumulate("Status","OK");
                mainObject.accumulate("Timestamp",now);
                mainObject.accumulate("Route Name: ", routeName);
                mainObject.accumulate("Station Name", stationName);
                mainArray.add(mainObject);
            }while (rs.next());
            return mainArray.toString();
            }
            else
                {
            mainObject.accumulate("Status","FAILED");
            mainObject.accumulate("Timestamp",now); 
            mainObject.accumulate("TrainID",id);
            mainObject.accumulate("Message","Train details are not found. Please try again");
            }
    
        } catch (SQLException ex) {
            mainObject.accumulate("Status","ERROR_DB");
            mainObject.accumulate("Timestamp",now);
            mainObject.accumulate("Message","Database Issues");
       
            Logger.getLogger(MainResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            closeDBConnection(rs, stm, con);
            }
          return mainObject.toString();
    }
    
   //Query 6 by - 1895212 - Sai Ravi Teja A
    //http://192.168.2.164:8080/trainSchedule/webresources/main/showBookingHistory&1115
    //gets Booking history of a particular user
    @GET
    @Path("showBookingHistory&{id}")
    @Produces("application/json")
    public String getText6(@PathParam("id") int id) {

        try {

            stm = con.createStatement();
            String sql = "select u.firstname, u.lastname, b.noofplaces, b.BOOKINGDATE, r.routename, s.stationname from users u inner join booking b on b.USERID =u.ID inner join route r on r.ID = b.ROUTEID inner join details d on d.ROUTEID = r.ID inner join station s on s.ID = d.STATIONID where u.ID ="+id;
            rs = stm.executeQuery(sql);
           
            String firstName, lastName, bookDate, routeName, stationName;
            int noPlaces; 
 
            if(rs.next()) {
                do {
                mainObject.clear();
                
                routeName = rs.getString("routename");
                stationName = rs.getString("stationname");
                firstName = rs.getString("firstname");
                lastName = rs.getString("lastname");
                bookDate = rs.getString("bookdate");
                noPlaces = rs.getInt("noofplaces");
                
                mainObject.accumulate("Status","OK");
                mainObject.accumulate("Timestamp",now);
                mainObject.accumulate("First Name", firstName);
                mainObject.accumulate("Last Name", lastName);
                mainObject.accumulate("No of places", noPlaces);
                mainObject.accumulate("Booking Date", bookDate);
                mainObject.accumulate("Route Name", routeName);
                mainObject.accumulate("Station Name", stationName);
               
                mainArray.add(mainObject);
                
            }while (rs.next());
            return mainArray.toString();
            }
            else
            {
            mainObject.accumulate("Status","FAILED");
            mainObject.accumulate("Timestamp",now); 
            mainObject.accumulate("TrainID",id);
            mainObject.accumulate("Message","Booking details are not found. Please try again");
            }
    
        } catch (SQLException ex) {
            mainObject.accumulate("Status","ERROR_DB");
            mainObject.accumulate("Timestamp",now);
            mainObject.accumulate("Message","Database Issues");
       
            Logger.getLogger(MainResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            closeDBConnection(rs, stm, con);
            }
          return mainObject.toString();
    } 
    
    
    
     private void closeDBConnection(ResultSet rs, Statement stm, Connection con) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
            }
        }
        if (stm != null) {
            try {
                stm.close();
            } catch (SQLException e) {
                
            }
        }
        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                
            }
        }
    }
     

}

/*select u.firstname, u.lastname, b.noofplaces, b.BOOKINGDATE, r.routename, s.stationname
from users u inner join booking b on b.USERID =u.ID
inner join route r on r.ID = b.ROUTEID
inner join details d on d.ROUTEID = r.ID
inner join station s on s.ID = d.STATIONID
where u.ID = 1125;*/