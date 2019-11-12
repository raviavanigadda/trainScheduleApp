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

    /**
     * Retrieves representation of an instance of mobile.MainResource
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getJson() {
        //TODO return proper representation object
        throw new UnsupportedOperationException();
    }
    
    //http://192.168.2.164:8080/trainSchedule/webresources/main/listUsers
    @GET
    @Path("listUsers")
    @Produces("application/json")
    public String getText() {
       
        mainObject.accumulate("Status", "Error");
        mainObject.accumulate("Message", "User doesn't exists");        
       
        try {
       
            stm = con.createStatement();
            String sql = "select * from users";
            rs = stm.executeQuery(sql);

            int id;
            String fname, lname, email, password, phone;
            

            while (rs.next()) {
                mainObject.clear();
                id = rs.getInt("id");
                password = rs.getString("password");
                fname = rs.getString("firstname");
                lname = rs.getString("lastname");
                email = rs.getString("email");
                phone = rs.getString("phonenumber");
               

                mainObject.accumulate("id", id);
                mainObject.accumulate("password", password);
                mainObject.accumulate("fname", fname);
                mainObject.accumulate("lname", lname);
                mainObject.accumulate("email", email);
                mainObject.accumulate("phone", phone);
              
                mainArray.add(mainObject);
               
                mainObject.clear();
                
            }

        } catch (SQLException ex) {
            Logger.getLogger(MainResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            closeDBConnection(rs, stm, con);
            }
        return mainArray.toString();
    }

    //http://192.168.2.164:8080/trainSchedule/webresources/main/singleUser&1105
    @GET
    @Path("singleUser&{id}")
    @Produces("application/json")
    public String getText1(@PathParam("id") int id) {
       
        mainObject.accumulate("Status", "Error");
        mainObject.accumulate("Message", "User doesn't exists");        
       
        try {
       
            stm = con.createStatement();
            String sql = "select * from users where id =" + id;
            rs = stm.executeQuery(sql);


            String fname, lname, email, password, phone;
            

            while (rs.next()) {
                mainObject.clear();
                id = rs.getInt("id");
                password = rs.getString("password");
                fname = rs.getString("firstname");
                lname = rs.getString("lastname");
                email = rs.getString("email");
                phone = rs.getString("phonenumber");
               

                mainObject.accumulate("id", id);
                mainObject.accumulate("password", password);
                mainObject.accumulate("fname", fname);
                mainObject.accumulate("lname", lname);
                mainObject.accumulate("email", email);
                mainObject.accumulate("phone", phone);
              
                
                
            }

        } catch (SQLException ex) {
            Logger.getLogger(MainResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            closeDBConnection(rs, stm, con);
            }
        return mainObject.toString();
    }
   
    //Query 4 by - 1895212 - Sai Ravi Teja A
    //http://192.168.2.164:8080/trainSchedule/webresources/main/viewTrainRoutes&100
    //gets list of stations where each train will stop 
    @GET
    @Path("viewTrainRoutes&{id}")
    @Produces("application/json")
    public String getText2(@PathParam("id") int id) {

        try {
       
            stm = con.createStatement();
            String sql = "select distinct(r.routename), s.stationname from route r, train t, station s, details d where r.TRAINID = "+id+" and d.STATIONID = s.ID" ;
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
   
     //Query by - 1895212 - Sai Ravi Teja A
    //http://192.168.2.164:8080/trainSchedule/webresources/main/viewTrainRoutes&100
    //gets list of stations where each train will stop 
    @GET
    @Path("viewTrainRoutes&{id}")
    @Produces("application/json")
    public String getText3(@PathParam("id") int id) {

        try {
       
            stm = con.createStatement();
            String sql = "select distinct(r.routename), s.stationname from route r, train t, station s, details d where r.TRAINID = "+id+" and d.STATIONID = s.ID" ;
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