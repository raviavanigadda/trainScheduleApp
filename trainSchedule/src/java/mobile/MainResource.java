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
    JSONObject initialObject = new JSONObject();
    
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

    //Query 1 by - 1894475 - P SRUTHI
    //http://localhost:8080/trainSchedule/webresources/main/viewTrainSchedule&100
    //Train Schedule at different stations
    @GET
    @Path("viewTrainSchedule&{id}")
    @Produces("application/json")
    public String getText1(@PathParam("id") int id) {

        try {
       
            stm = con.createStatement();
            
            String sql = "select s.STATIONNAME,d.ARRIVALTIME,d.DEPARTURETIME,t.TRAINNAME,r.ROUTENAME from details d,station s,train t,route r where d.routeID = r.ID and s.ID = d.STATIONID and r.TRAINID = t.ID and t.ID ="+id;
            
            rs = stm.executeQuery(sql);
           
            String stationName, arrivalTime, departureTime, trainName;
            int flag = 0;
            if(rs.next()) {
               if(flag!=1)
                {
              
                initialObject.accumulate("Status: ","OK");
                initialObject.accumulate("Timestamp: ",now);
                
                }
               do {
           
                trainName = rs.getString("trainname");
                stationName = rs.getString("stationname");
                arrivalTime = rs.getString("arrivaltime");
                departureTime = rs.getString("departuretime");
             
                flag = 1;
                
                mainObject.accumulate("Station Name", stationName);
                mainObject.accumulate("Arrival Time", arrivalTime);
                mainObject.accumulate("Departure Time", departureTime);
                mainObject.accumulate("Train Name", trainName);
                               
                mainArray.add(mainObject);
                mainObject.clear();
            }while (rs.next());
          
               initialObject.accumulate("Train Schedule: ", mainArray);
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
        
        if(initialObject.isEmpty())
        {
            mainObject.accumulate("Status","FAILED");
            mainObject.accumulate("Timestamp",now); 
            mainObject.accumulate("TrainID",id);
            mainObject.accumulate("Message","Train Details are not found. Please try again");
            
            return mainObject.toString();
        }
          return initialObject.toString();
    }
    
    //Query 2 by - 1895212 - Sai Ravi Teja A
    //http://localhost:8080/trainSchedule/webresources/main/viewTrainRoutes&100
    //get the list routes taken by a train
    @GET
    @Path("viewTrainRoutes&{id}")
    @Produces("application/json")
    public String getText2(@PathParam("id") int id) {

        try {
       
            stm = con.createStatement();
            String sql = "select r.routename, r.ROUTELENGTH  from route r, train t where r.TRAINID = t.ID and t.ID = "+id;
            rs = stm.executeQuery(sql);
           
            String routeName, routeLength;
            int flag = 0;
            if(rs.next()) {
                if(flag!=1)
                {
                initialObject.accumulate("Status","OK");
                initialObject.accumulate("Timestamp",now);
                }
                do {
                routeName = rs.getString("routename");
                routeLength = rs.getString("routelength");
                
                mainObject.accumulate("Route Name", routeName);
                mainObject.accumulate("Route Length", routeLength);
                
                mainArray.add(mainObject);
                mainObject.clear();
            }while (rs.next());
                initialObject.accumulate("Route Information", mainArray);
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
        
         if(initialObject.isEmpty())
        {
            mainObject.accumulate("Status","FAILED");
            mainObject.accumulate("Timestamp",now); 
            mainObject.accumulate("TrainID",id);
            mainObject.accumulate("Message","Train Routes are not found. Please try again");
            
            return mainObject.toString();
        }
          return initialObject.toString();
    }
    
    //Query 3 by - 1895212 - Sai Ravi Teja A
    //http://localhost:8080/trainSchedule/webresources/main/showBookingHistory&1115
    //Retrive Booking History of a specific user
    @GET
    @Path("showBookingHistory&{id}")
    @Produces("application/json")
    public String getText3(@PathParam("id") int id) {

        try {

            stm = con.createStatement();
            String sql = "select b.BOOKINGDATE,u.FIRSTNAME,u.LASTNAME,b.NOOFPLACES,s.STATIONNAME from users u,booking b,station s,route r,details d where b.USERID=u.ID and b.ROUTEID=r.ID and d.STATIONID=s.ID and d.ROUTEID=r.ID and u.ID= "+id;
            rs = stm.executeQuery(sql);
           
            String firstName, lastName, bookDate, stationName;
            int noPlaces, flag=0; 
 
            if(rs.next())
            {
                if(flag!=1)
                {
                initialObject.accumulate("Status: ","OK");
                initialObject.accumulate("Timestamp: ",now);
                
                firstName = rs.getString("firstname");
                lastName = rs.getString("lastname");
                
                initialObject.accumulate("First Name", firstName);
                initialObject.accumulate("Last Name", lastName);
                }
                do {
                
                stationName = rs.getString("stationname");
             
                bookDate = rs.getString("bookingdate");
                noPlaces = rs.getInt("noofplaces");
                
                mainObject.accumulate("Booking Date", bookDate);
                
                mainObject.accumulate("No of places", noPlaces);
                mainObject.accumulate("Station Name", stationName);
               
                mainArray.add(mainObject);
                mainObject.clear();
            }while (rs.next());
            
            initialObject.accumulate("Booking History: ", mainArray);
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
        
         if(initialObject.isEmpty())
        {
            mainObject.accumulate("Status","FAILED");
            mainObject.accumulate("Timestamp",now); 
            mainObject.accumulate("TrainID",id);
            mainObject.accumulate("Message","Booking details are not found. Please try again.");
            
            return mainObject.toString();
        }
          return initialObject.toString();
    } 
    
    
    //Query 4 by - 1895212 - Sai Ravi Teja A
    //http://localhost:8080/trainSchedule/webresources/main/viewStationSchedule&2010
    //get the list of trains coming to the station
    @GET
    @Path("viewStationSchedule&{id}")
    @Produces("application/json")
    public String getText4(@PathParam("id") int id) {

        try {
       
            stm = con.createStatement();
            String sql = "select t.TRAINNAME,d.ARRIVALTIME,d.DEPARTURETIME,r.ROUTENAME from details d,station s,train t ,route r where d.routeID = r.ID and d.STATIONID=s.ID and r.TRAINID=t.ID and s.ID ="+id;
            rs = stm.executeQuery(sql);
           
            int trainID,stationID;
            String trainName,arrivalTime,departureTime,routeName;
            
            int flag = 0;
            if(rs.next()) {
                if(flag!=1)
                {
                initialObject.accumulate("Status","OK");
                initialObject.accumulate("Timestamp",now);
                initialObject.accumulate("StationID", id);
                }
                do {
                    
                trainName = rs.getString("trainname");
                arrivalTime = rs.getString("arrivaltime");
                departureTime = rs.getString("departuretime");
                routeName = rs.getString("routename");
                
                mainObject.accumulate("Train Name", trainName);
                mainObject.accumulate("Arrival Time", arrivalTime);
                mainObject.accumulate("Departure Time", departureTime);
                mainObject.accumulate("Route Name: ", routeName);
            
                mainArray.add(mainObject);
                mainObject.clear();
            }
                while (rs.next());
                initialObject.accumulate("Station Schedule: ", mainArray);
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
        
         if(initialObject.isEmpty())
        {
            mainObject.accumulate("Status","FAILED");
            mainObject.accumulate("Timestamp",now); 
            mainObject.accumulate("TrainID",id);
            mainObject.accumulate("Message","Station schedule is not ready. Please try again later");
            
            return mainObject.toString();
        }
          return initialObject.toString();
    }
    
    //Query 5 by - 1895212 - Shriya
    //localhost:8080/trainSchedule/webresources/main/createAccount&1210&bhari&rathy&anypw&b.ava@gmail.com&4388752231
    //sign up for app. 
    @GET
    @Path("createAccount&{id}&{firstname}&{lastname}&{password}&{email}&{phonenumber}")
    @Produces("application/json")
    public String getText5(@PathParam("id") int id,@PathParam("firstname") String firstName,@PathParam("lastname") String lastName,@PathParam("password") String password,@PathParam("email") String email,@PathParam("phonenumber") String phoneNumber) {

        try {
            String sql = "insert into users values(?,?,?,?,?,?)";
           
            ps = con.prepareStatement(sql);
            ps.setInt(1,id);
            ps.setString(2,firstName);
            ps.setString(3,lastName);
            ps.setString(4,password);
            ps.setString(5,email);
            ps.setString(6,phoneNumber);
                 
            int flag = ps.executeUpdate();
                
            if(flag == 1) {
            mainObject.accumulate("Status","OK");
            mainObject.accumulate("Timestamp",now);
            mainObject.accumulate("Message","Account created successfully. Please login now.");
            }
            else
            {
            mainObject.accumulate("Status","FAILED");
            mainObject.accumulate("Timestamp",now);
            mainObject.accumulate("Message","Wrong data entered. Please try again.");
            }
        } catch (SQLException ex) {
            
                mainObject.accumulate("Status","ERROR_DB");
                mainObject.accumulate("Timestamp",now);
                mainObject.accumulate("Message","Account already exists. Please try again.");
                
            Logger.getLogger(MainResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            closeDBConnection(rs, stm, con);
            }
        
          return mainObject.toString();
    }
    
    //should modify
    //Query 6 by - 1895212 - Shriya
    //localhost:8080/trainSchedule/webresources/main/createAccount&1210&bhari&rathy&anypw&b.ava@gmail.com&4388752231
    //sign up for app. 
    @GET
    @Path("updateProfile&{id}&{email}&{password}&{phonenumber}")
    @Produces("application/json")
    public String getText6(@PathParam("id") int id,@PathParam("email") String email,@PathParam("password") String password,@PathParam("phonenumber") String phonenumber) {

        try {
            
            String sql = "insert into users values(?,?,?,?,?,?)";
           
            ps = con.prepareStatement(sql);
            ps.setInt(1,id);
            ps.setString(2,firstName);
            ps.setString(3,lastName);
            ps.setString(4,password);
            ps.setString(5,email);
            ps.setString(6,phoneNumber);
                 
            int flag = ps.executeUpdate();
                
            if(flag == 1) {
            mainObject.accumulate("Status","OK");
            mainObject.accumulate("Timestamp",now);
            mainObject.accumulate("Message","Account created successfully. Please login now.");
            }
            else
            {
            mainObject.accumulate("Status","FAILED");
            mainObject.accumulate("Timestamp",now);
            mainObject.accumulate("Message","Wrong data entered. Please try again.");
            }
        } catch (SQLException ex) {
            
                mainObject.accumulate("Status","ERROR_DB");
                mainObject.accumulate("Timestamp",now);
                mainObject.accumulate("Message","Account already exists. Please try again.");
                
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