package Json;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import schooldata.DataBaseConnection;
import timetable_module.DataBaseMethodsTimeTable;

@ManagedBean(name="autoTimeTableJsonBean")
@ViewScoped
public class AutoTimeTableJsonBean implements Serializable
{
	
	String json="";
     public AutoTimeTableJsonBean() {

    		Connection conn=DataBaseConnection.javaConnection();
    		try {
    			Timestamp currentDate=new Timestamp(new Date().getTime());currentDate.setHours(0);currentDate.setMinutes(0);currentDate.setSeconds(0);currentDate.setNanos(0);
        		new DataBaseMethodsTimeTable().replaceTimeTable(currentDate,conn);

        		
        		
        		json="done";
			} catch (Exception e) {
				// TODO: handle exception
			}
    		finally {
    			try {
        			conn.close();
        		} catch (SQLException e) {
        			e.printStackTrace();
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
