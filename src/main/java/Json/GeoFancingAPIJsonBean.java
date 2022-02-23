package Json;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.StudentInfo;

@ManagedBean(name="geoFancingApiJson")
@ViewScoped
public class GeoFancingAPIJsonBean implements Serializable
{
	String json="";
	DatabaseMethods1 dbm = new DatabaseMethods1();
	DataBaseMeathodJson dbj = new DataBaseMeathodJson();

     public GeoFancingAPIJsonBean() 
     {
    	  Connection conn=DataBaseConnection.javaConnection();
    	  try {
    		   Map<String, String> params =FacesContext.getCurrentInstance().
    	   				getExternalContext().getRequestParameterMap();
    	   		String imeiNo = params.get("source");
    	   		String sourcename=params.get("sourcename");
    	   		String alert=params.get("alerttype");
    	   		String geo_name=params.get("geo");
    	   		//// // System.out.println();
    			String message="Dear parent\n" + 
    	   				"Bus is about to reach to your Ward's stop.\n" + 
    	   				"Regards \n" + 
    	   				"BLM academy";  
    		      new java.util.Timer().schedule(
    		    	        new java.util.TimerTask() {
    		    	            @Override
    		    	            public void run() {
    		    	                     
    		    	            	  
    		    	    	        	if(alert.equalsIgnoreCase("geo"))
    		    	    	    		{
    		    	    	        		
    		    	    	        		String number="";
    		    	    	        		String name="";
    		    	    	        		
    		    	    	        		
    		    	    	        		String tIntv = dbm.contactNo("geo feance", conn);
    		    	    	    			
    		    	    	    			long transIntv = Long.valueOf(tIntv.equalsIgnoreCase("") ? "0" : tIntv);
    		    	    	    			
    		    	    	        		
    		    	    	        		String  i=dbj.lastGeoFenceCheck(imeiNo,geo_name,sourcename,conn);
    		    	    	        		
    		    	    	        		
    		    	    	        		boolean check=true;
    		    	    	        		
    		    	    	        		
    		    	    	        		if(i.equals(""))
    		    	    	        		{
    		    	    	        			check=true;
    		    	    	        		}
    		    	    	        		else
    		    	    	        		{
    		    	    	        			
    		    	    	        			
    		    	    	        			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    		    	    	        			Date d1 =new Date();
    		    	    	        			String dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(d1);
    		    	    	    				
    		    	    	        			Date prevDt = null;
    		    	    						Date currDt = null;
    		    	    	        			
    		    	    	        			
    		    	    						try {
    		    	    							
    		    	    							
    		    	    							prevDt = sdf.parse(i);
    		    	    							currDt = sdf.parse(dateTime);
    		    	    							long min = TimeUnit.MILLISECONDS.toMinutes(currDt.getTime() - prevDt.getTime());
    		    	    							if(min >= transIntv)
    		    	    							{
    		    	    								check=true;
    		    	    							}
    		    	    							else
    		    	    							{
    		    	    								check=false;
    		    	    							}
    		    	    							
    		    	    							
    		    	    						
    		    	    							
    		    	    						} catch (Exception e) {
    		    	    							e.printStackTrace();
    		    	    						}
    		    	    					}
    		    	    	        		
    		    	    	        		if(check==true)
    		    	    	       		    {
    		    	    	        			dbj.savegeoFence(imeiNo,geo_name,sourcename,conn,"yes");
    		    	    	        			String schid = dbj.schidByGeofence(conn, geo_name);
    		    	    	            		ArrayList<StudentInfo>list= dbj.allStudentListforgeo(conn, geo_name, schid);
    		    	    	            	    
    		    	    	            		
    		    	    	            		for(StudentInfo ls:list) 
    		    	    	            		{
    		    	    	            			if(number.equals(""))
    		    	    	            			{
    		    	    	            				number=String.valueOf(ls.getFathersPhone());
    		    	    	            				name=ls.getAddNumber();
    		    	    	            			}
    		    	    	            			else
    		    	    	            			{
    		    	    	            				number=number+","+String.valueOf(ls.getFathersPhone());
    		    	    	            				name=name+","+ls.getAddNumber();
    		    	    	            			}
    		    	    	            		}
    		    	    	            		
    		    	    	            		if(!number.equals(""))
    		    	    	            		{
    		    	    	            			dbj.messageurl1(number, message, name, list.get(0).getSchid(), conn);
    		    	    	            		}
    		    	    	            		
    		    	    	       		     }
    		    	    	        		else
    		    	    	        		{
    		    	    	        			dbj.savegeoFence(imeiNo,geo_name,sourcename,conn,"No");
    		    	    	            		
    		    	    	        		}
    		    	    	    			
    		    	    	    		}
    		    	    	    		else if(alert.equalsIgnoreCase("speed"))
    		    	    	    		{
    		    	    	    			String speed=params.get("speed");
    		    	    	    	        		
    		    	    	    		}
    		    	    	    		else if(alert.equalsIgnoreCase("ignition_on"))
    		    	    	    		{
    		    	    	    			
    		    	    	    		}
    		    	    					 
    		    	    				
    		    	    	    		
    		    	            	
    		    	            }
    		    	        },
    		    	        2000
    		    	);
    		      
    	            
    	      	     json="done";
		} catch (Exception e) {
			// TODO: handle exception
		}
    	 finally {
			try {
				conn.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
    	 
    
    	     
    	     
    	 
     }
     
     public void renderJson() throws IOException
 	{
 		FacesContext facesContext = FacesContext.getCurrentInstance();
 		ExternalContext externalContext = facesContext.getExternalContext();
 		externalContext.setResponseContentType("application/json");
 		externalContext.setResponseCharacterEncoding("UTF-8");
 		externalContext.getResponseOutputWriter().write(json);
 		facesContext.responseComplete();

 	}
 	public String getJson() {
 		return json;
 	}
 	public void setJson(String json) {
 		this.json = json;
 	}
}
