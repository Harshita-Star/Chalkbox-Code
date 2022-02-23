package Json;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.primefaces.shaded.json.JSONArray;
import org.primefaces.shaded.json.JSONObject;

import schooldata.ClassInfo;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;

@ManagedBean(name="unmarkedAttendanceJson")
@ViewScoped

public class UnmarkedAttendanceJsonBean implements Serializable
{
	String json="";
	ArrayList<ClassInfo> classList,allClassList;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	DatabaseMethods1 DBM=new DatabaseMethods1();


	public UnmarkedAttendanceJsonBean()
	{
		
		Connection conn=DataBaseConnection.javaConnection();
		try {
			classList = new ArrayList<>();
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();
			String schid=params.get("schoolid");

			boolean checkAtt,check;

			JSONArray arr=new JSONArray();

			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				String strDate=new SimpleDateFormat("yyyy-MM-dd").format(new Date());
				allClassList=DBJ.schoolSectionList(schid,conn);
				if(allClassList.size()>0)
				{
					for(ClassInfo cc : allClassList)
					{
						checkAtt = DBM.checkWhetherAttendanceMarked(cc.getSectionId(),strDate,schid,conn);
						if(checkAtt==false)
						{
							check = DBJ.checkSectionHolidayForAttendanceJSON(strDate, conn, schid, cc.getSectionId());
							if(check==false)
							{
								classList.add(cc);
							}
						}
					}
				}

				for(ClassInfo cc : classList)
				{
					JSONObject obj = new JSONObject();

					obj.put("class", cc.getClassName());
					obj.put("section", cc.getSectionName());

					arr.put(obj);
				}
			}

			json=arr.toString();
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
}
