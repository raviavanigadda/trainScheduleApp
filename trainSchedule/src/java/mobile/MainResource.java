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

    //Query 1
    //http://localhost:8080/trainSchedule/webresources/main/viewTrainSchedule&100
    //Train Schedule at different stations. Inputs: TrainID
    @GET
    @Path("viewTrainSchedule&{id}")
    @Produces("application/json")
    public String getText1(@PathParam("id") int id) {

        try {
       
            stm = con.createStatement();
            
            String sql = "select s.id as staID,s.STATIONNAME,d.ARRIVALTIME,d.DEPARTURETIME,t.TRAINNAME,r.ROUTENAME from details d,station s,train t,route r where d.routeID = r.ID and s.ID = d.STATIONID and r.TRAINID = t.ID and t.ID ="+id;
            
            rs = stm.executeQuery(sql);
           
            String stationName, arrivalTime, departureTime, trainName;
            int flag = 0,stationID;
            if(rs.next()) {
               if(flag!=1)
                {
              
                initialObject.accumulate("Status","OK");
                initialObject.accumulate("Timestamp",now);
                initialObject.accumulate("Train ID",id);
                
                trainName = rs.getString("trainname");
                initialObject.accumulate("Train Name", trainName);
                
                }
               do {
                stationID = rs.getInt("staID");
                stationName = rs.getString("stationname");
                arrivalTime = rs.getString("arrivaltime");
                departureTime = rs.getString("departuretime");
             
                flag = 1;
                mainObject.accumulate("Station ID", stationID);
                mainObject.accumulate("Station Name", stationName);
                mainObject.accumulate("Arrival Time", arrivalTime);
                mainObject.accumulate("Departure Time", departureTime);
                    
                mainArray.add(mainObject);
                mainObject.clear();
                
            }while (rs.next());
          
               initialObject.accumulate("Train Schedule", mainArray);
            }
           
        } catch (SQLException ex) {
            mainObject.accumulate("Status","ERROR_DB");
            mainObject.accumulate("Timestamp",now);
            mainObject.accumulate("Message","Server Issues. Please try again.");
       
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
            mainObject.accumulate("Message","Train Details are incorrect. Please try again.");
            
            return mainObject.toString();
        }
          return initialObject.toString();
    }
    
    //Query 2
    //http://localhost:8080/trainSchedule/webresources/main/viewTrainRoutes&100
    //get the list of routes taken by a train. Inputs: TrainID
    @GET
    @Path("viewTrainRoutes&{id}")
    @Produces("application/json")
    public String getText2(@PathParam("id") int id) {

        try {
       
            stm = con.createStatement();
            String sql = "select t.id as trainID,r.routename, r.ROUTELENGTH  from route r, train t where r.TRAINID = t.ID and t.ID = "+id;
            rs = stm.executeQuery(sql);
           
            String routeName, routeLength;
            int flag = 0,trainID;
            if(rs.next()) {
                if(flag!=1)
                {
                    
                initialObject.accumulate("Status","OK");
                initialObject.accumulate("Timestamp",now);
                trainID = rs.getInt("trainID");
                initialObject.accumulate("Train ID", trainID);
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
                mainObject.accumulate("Message","Server Issues. Please try again later.");

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
            mainObject.accumulate("Message","Train Routes are not found. Please try again.");
            
            return mainObject.toString();
        }
          return initialObject.toString();
    }
    
    //Query 3
    //http://localhost:8080/trainSchedule/webresources/main/showBookingHistory&1115
    //Retrive Booking History of a specific user. Inputs: userID
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
                initialObject.accumulate("User ID", id);
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
            
            initialObject.accumulate("Booking History", mainArray);
           }
    
        } catch (SQLException ex) {
            mainObject.accumulate("Status","ERROR_DB");
            mainObject.accumulate("Timestamp",now);
            mainObject.accumulate("Message","Server Issues. Please try again later.");
       
            Logger.getLogger(MainResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            closeDBConnection(rs, stm, con);
            }
        
         if(initialObject.isEmpty())
        {
            mainObject.accumulate("Status","FAILED");
            mainObject.accumulate("Timestamp",now); 
            mainObject.accumulate("User ID", id);
            mainObject.accumulate("Message","Booking details are not found. Please try again.");
            
            return mainObject.toString();
        }
          return initialObject.toString();
    } 
    
    
    //Query 4
    //http://localhost:8080/trainSchedule/webresources/main/viewStationSchedule&2010
    //get the list of trains coming to the station
    @GET
    @Path("viewStationSchedule&{id}")
    @Produces("application/json")
    public String getText4(@PathParam("id") int id) {

        try {
       
            stm = con.createStatement();
            String sql = "select s.stationname,t.id as trainID,t.TRAINNAME,d.ARRIVALTIME,d.DEPARTURETIME,r.ROUTENAME from details d,station s,train t ,route r where d.routeID = r.ID and d.STATIONID=s.ID and r.TRAINID=t.ID and s.ID ="+id;
            rs = stm.executeQuery(sql);
           
            int trainID,stationID;
            String trainName,arrivalTime,departureTime,routeName,stationName;
            
            int flag = 0;
            if(rs.next()) {
                if(flag!=1)
                {
                stationName = rs.getString("stationname");
                initialObject.accumulate("Status","OK");
                initialObject.accumulate("Timestamp",now);
                initialObject.accumulate("Station ID", id);
                initialObject.accumulate("Station Name", stationName);
                }
                do {
                
                trainID = rs.getInt("trainID");
                trainName = rs.getString("trainname");
                arrivalTime = rs.getString("arrivaltime");
                departureTime = rs.getString("departuretime");
                routeName = rs.getString("routename");
                
                mainObject.accumulate("Train ID", trainID);
                mainObject.accumulate("Train Name", trainName);
                mainObject.accumulate("Arrival Time", arrivalTime);
                mainObject.accumulate("Departure Time", departureTime);
                mainObject.accumulate("Route Name", routeName);
            
                mainArray.add(mainObject);
                mainObject.clear();
            }
                while (rs.next());
                initialObject.accumulate("Station Schedule", mainArray);
            }
            
        } catch (SQLException ex) {
                mainObject.accumulate("Status","ERROR_DB");
                mainObject.accumulate("Timestamp",now);
               mainObject.accumulate("Message","Server Issues. Please try again later.");

            Logger.getLogger(MainResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            closeDBConnection(rs, stm, con);
            }
        
         if(initialObject.isEmpty())
        {
            mainObject.accumulate("Status","FAILED");
            mainObject.accumulate("Timestamp",now); 
            mainObject.accumulate("Station ID",id);
            mainObject.accumulate("Message","Station schedule is not yet ready. Please try again later.");
            
            return mainObject.toString();
        }
          return initialObject.toString();
    }
    
    //Query 5
    //http://localhost:8080/trainSchedule/webresources/main/signUp&1210&bhari&rathy&anypw&b.ava@gmail.com&4388752231
    //sign up for app. 
    @GET
    @Path("signUp&{id}&{firstname}&{lastname}&{password}&{email}&{phonenumber}")
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
            mainObject.accumulate("Message","Account created successfully. Please login now with following credentials.");
            mainObject.accumulate("User ID",id);
            mainObject.accumulate("Password",password);
           
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
               mainObject.accumulate("Message","Server Issues. Please try again later.");
                
            Logger.getLogger(MainResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            closeDBConnection(rs, stm, con);
            }
        
          return mainObject.toString();
    }
    
    //Query 6
    //http://localhost:8080/trainSchedule/webresources/main/forgotPassword&1160&anyp1234
    //ChangePassword 
    @GET
    @Path("forgotPassword&{id}&{password}")
    @Produces("application/json")
    public String getText6(@PathParam("id") int id,@PathParam("password") String password) {

        try {
            
            String sql = "update users set password=? where id=?";
           
            ps = con.prepareStatement(sql);
            ps.setString(1,password);
            ps.setInt(2, id);
                 
            int flag = ps.executeUpdate();
                
            if(flag == 1) {
            mainObject.accumulate("Status","OK");
            mainObject.accumulate("Timestamp",now);
            mainObject.accumulate("User ID",id);
            mainObject.accumulate("Message","Password changed sucessfully. Please login with new password");
            }
            else
            {
            mainObject.accumulate("Status","FAILED");
            mainObject.accumulate("Timestamp",now);
            mainObject.accumulate("User ID",id);
            mainObject.accumulate("Message","Entered details are incorrect. Please try again.");
            }
        } catch (SQLException ex) {
                mainObject.accumulate("Status","ERROR_DB");
                mainObject.accumulate("Timestamp",now);
                mainObject.accumulate("Message","Server Issues. Please try again later.");
            Logger.getLogger(MainResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            closeDBConnection(rs, stm, con);
            }
        
          return mainObject.toString();
    }
    
    //Query 7
    //http://localhost:8080/trainSchedule/webresources/main/trainReservationChart&100
    //Train Reservation Chart inputs:trainID
    @GET
    @Path("trainReservationChart&{trainID}")
    @Produces("application/json")
    public String getText7(@PathParam("trainID") int trainID) {

        try {
       
            stm = con.createStatement();
            String sql = "select distinct u.FIRSTNAME,u.LASTNAME,u.EMAIL,u.PHONENUMBER,b.NOOFPLACES from users u, booking b, station s, route r, details d, train t where b.USERID = u.ID and b.ROUTEID=r.ID and d.STATIONID = s.ID and d.ROUTEID = r.ID and t.ID = r.TRAINID and t.ID ="+trainID;
            rs = stm.executeQuery(sql);
           
           
            String firstName,lastName,email,phoneno;
            int noofplaces;
            
            int flag = 0;
            if(rs.next()) {
                if(flag!=1)
                {
                initialObject.accumulate("Status","OK");
                initialObject.accumulate("Timestamp",now);
                initialObject.accumulate("TrainID", trainID);
                }
                do {
                    
                firstName = rs.getString("firstname");
                lastName = rs.getString("lastname");
                email = rs.getString("email");
                phoneno = rs.getString("phonenumber");
                noofplaces = rs.getInt("noofplaces");
                
                mainObject.accumulate("First Name", firstName);
                mainObject.accumulate("Last Name", lastName);
                mainObject.accumulate("Email", email);
                mainObject.accumulate("Phone number", phoneno);
                mainObject.accumulate("No of seats", noofplaces);
                
                mainArray.add(mainObject);
                mainObject.clear();
            }
                while (rs.next());
                initialObject.accumulate("Train Reservation Chart", mainArray);
            }
            
        } catch (SQLException ex) {
                mainObject.accumulate("Status","ERROR_DB");
                mainObject.accumulate("Timestamp",now);
                mainObject.accumulate("Message","Server Issues. Please try again later.");
 
            Logger.getLogger(MainResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            closeDBConnection(rs, stm, con);
            }
        
         if(initialObject.isEmpty())
        {
            mainObject.accumulate("Status","FAILED");
            mainObject.accumulate("Timestamp",now); 
            mainObject.accumulate("TrainID",trainID);
            mainObject.accumulate("Message","No bookings are made. Book now!");
            
            return mainObject.toString();
        }
          return initialObject.toString();
    }
    
    //Query 8
    //http://localhost:8080/trainSchedule/webresources/main/bookTicket&5230&4&1-NOV-2029&1110&24
    //Book Tickets
    @GET
    @Path("bookTicket&{id}&{noofplaces}&{bookingdate}&{userid}&{routeid}")
    @Produces("application/json")
    public String getText8(@PathParam("id") int id,@PathParam("noofplaces") int noofplaces,@PathParam("bookingdate") String bookingDate,@PathParam("userid") int userID,@PathParam("routeid") int routeID) {

        try {
            String sql = "insert into booking values(?,?,?,?,?)";
           
            ps = con.prepareStatement(sql);
           
            ps.setInt(1,id);
            ps.setInt(2,noofplaces);
            ps.setString(3,bookingDate);
            ps.setInt(4,userID);
            ps.setInt(5,routeID);
                           
            int flag = ps.executeUpdate();
                
            if(flag == 1) {
            mainObject.accumulate("Status","OK");
            mainObject.accumulate("Timestamp",now);
             mainObject.accumulate("Booking ID",id);
            mainObject.accumulate("Message","Ticket booked Successfully. Check booking history for more details.");
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
                mainObject.accumulate("Message","Server Issues. Please try again later.");
                
            Logger.getLogger(MainResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            closeDBConnection(rs, stm, con);
            }
        
          return mainObject.toString();
    }
    
    //Query 9
    //http://localhost:8080/trainSchedule/webresources/main/stationsBetweenRoutes&100
    //Search stations between routes
    @GET
    @Path("stationsBetweenRoutes&{route1}&{route2}")
    @Produces("application/json")
    public String getText9(@PathParam("route1") int r1,@PathParam("route2") int r2) {

        try {
       
            stm = con.createStatement();
            String sql = "select distinct s.STATIONNUMBER,s.STATIONNAME from route r, station s, details d where r.ID=d.ROUTEID and s.ID=d.STATIONID and r.ID between "+r1+" and "+r2;
            rs = stm.executeQuery(sql);
           
           
            String stationName;
            int stationNumber;
            
            int flag = 0;
            if(rs.next()) {
                if(flag!=1)
                {
                initialObject.accumulate("Status","OK");
                initialObject.accumulate("Timestamp",now);
                initialObject.accumulate("Initial Route", r1);
                initialObject.accumulate("End Route", r2);
                }
                do {
                    
                stationName = rs.getString("stationname");
                stationNumber = rs.getInt("stationnumber");
                            
                mainObject.accumulate("Station Number", stationNumber);
                mainObject.accumulate("Station Name", stationName);
               
                mainArray.add(mainObject);
                mainObject.clear();
                
            }
                while (rs.next());
                initialObject.accumulate("Station Information between routes", mainArray);
            }
            
        } catch (SQLException ex) {
            
                mainObject.accumulate("Status","ERROR_DB");
                mainObject.accumulate("Timestamp",now);
                mainObject.accumulate("Message","Server Issues. Please try again later.");

            Logger.getLogger(MainResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            closeDBConnection(rs, stm, con);
            }
        
         if(initialObject.isEmpty())
        {
            mainObject.accumulate("Status","FAILED");
            mainObject.accumulate("Timestamp",now); 
            mainObject.accumulate("Route1",r1);
            mainObject.accumulate("Route2",r2);
            mainObject.accumulate("Message","No such routes exist. Please enter correct routes.");
            
            return mainObject.toString();
        }
          return initialObject.toString();
    }
    
     //Query 10
    //http://localhost:8080/trainSchedule/webresources/main/seatsAvailability&2010&100 
    //Search availability of seats in particular train and station
    @GET
    @Path("seatsAvailability&{stationID}&{trainID}")
    @Produces("application/json")
    public String getText11(@PathParam("stationID") int stationID,@PathParam("trainID") int trainID) {

        try {
       
            stm = con.createStatement();
            String sql = "select distinct t.CAPACITY, t.COACHES from train t, route r, details d, station s where t.ID=r.TRAINID and d.ROUTEID=r.ID and d.STATIONID=s.ID and t.ID="+trainID+" and s.ID="+stationID;
            rs = stm.executeQuery(sql);
           
           
            
            int trainSeats,trainCoaches;
            int flag = 0;
            if(rs.next()) {
                if(flag!=1)
                {
                initialObject.accumulate("Status","Available");
                initialObject.accumulate("Timestamp",now);
                initialObject.accumulate("stationID", stationID);
                initialObject.accumulate("trainID", trainID);
                }
                do {
                    
                trainSeats = rs.getInt("capacity");
                trainCoaches = rs.getInt("coaches");
                            
                mainObject.accumulate("No of seats available", trainSeats);
                mainObject.accumulate("No of coaches", trainCoaches);
               
            }
                while (rs.next());
                initialObject.accumulate("Availablity of seats", mainObject);
            }
            
        } catch (SQLException ex) {
            
                mainObject.accumulate("Status","ERROR_DB");
                mainObject.accumulate("Timestamp",now);
                mainObject.accumulate("Message","Server Issues. Please try again later.");

            Logger.getLogger(MainResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            closeDBConnection(rs, stm, con);
            }
        
         if(initialObject.isEmpty())
        {
            mainObject.accumulate("Status","Not Available");
            mainObject.accumulate("Timestamp",now); 
            mainObject.accumulate("stationID",stationID);
            mainObject.accumulate("trainID",trainID);
            mainObject.accumulate("Message","No seats are available at the moment. Please try again.");
            
            return mainObject.toString();
        }
          return initialObject.toString();
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
