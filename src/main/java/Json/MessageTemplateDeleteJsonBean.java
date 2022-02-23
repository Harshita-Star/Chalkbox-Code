package Json;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import schooldata.DataBaseConnection;

@ManagedBean(name="deletetemplate")
@ViewScoped
public class MessageTemplateDeleteJsonBean implements Serializable {


	String json;
	String sectionid,message;
	DataBaseMeathodJson DBJ = new DataBaseMeathodJson();

	public MessageTemplateDeleteJsonBean()
	{

		Connection conn=DataBaseConnection.javaConnection();

		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String SchoolId=params.get("Schoolid");
			String id=params.get("id");
			
			String status="";
			int i =0;
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest =DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				i=DBJ.allMessagedelete(id,SchoolId,conn);
			}
			
			if(i>0)
			{
				status="Template delete Successfully";
			}
			else
			{
				status="Template not delete ";
			}

			json=status;
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


	public void renderJson() throws IOException {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		externalContext.setResponseContentType("application/json");
		externalContext.setResponseCharacterEncoding("UTF-8");
		externalContext.getResponseOutputWriter().write(json);
		facesContext.responseComplete();


	}

}
