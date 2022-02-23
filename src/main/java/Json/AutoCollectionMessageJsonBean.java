package Json;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.SchoolInfo;
import schooldata.SchoolInfoList;

@ManagedBean(name="autoCollectionMessageJsonBean")
@ViewScoped
public class AutoCollectionMessageJsonBean implements Serializable {

	String json="";
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	DatabaseMethods1 DBM=new DatabaseMethods1();
	public AutoCollectionMessageJsonBean() 
	{

		Connection conn=DataBaseConnection.javaConnection();
		try {
			String strDate = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
			ArrayList<SchoolInfo> dataList=new ArrayList<>();
			dataList=DBM.allSchoolListForCollection(conn);
			if(dataList.size()>0)
			{
				String collection = "0";
				String message = "";
				for(SchoolInfo ss : dataList)
				{
					collection = "0";
					SchoolInfoList list=DBM.fullSchoolInfo(ss.getId(), conn);
					collection = DBM.todaysCollection(ss.getId(),"collectionTrigger", conn);
					message = "Hello Sir, today's("+strDate+") fee collection in "+ss.getSchoolName()+" is Rs."+collection;
					DBJ.messageurlStaff(ss.getAutoCollectionNo(), message, "Admin", conn, ss.getId());
					////// // System.out.println(message);
				}
				
				json="done";
			}
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
