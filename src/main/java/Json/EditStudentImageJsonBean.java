package Json;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.json.simple.JSONObject;

import schooldata.DataBaseConnection;
import sun.misc.BASE64Decoder;

@ManagedBean(name="editStudentImageJson")
@ViewScoped

public class EditStudentImageJsonBean implements Serializable
{
	String json;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();

	public EditStudentImageJsonBean()
	{
		Connection conn=DataBaseConnection.javaConnection();

		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String studentid = params.get("studentid");
			String schid = params.get("Schoolid");
			String imagePath = params.get("image");
	   
			int i=0 ;
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				String fileName="";
				if(!imagePath.equals(""))
				{
					Date tdy=new Date();

					String tdt = new SimpleDateFormat("yMdhms").format(tdy);

					int randomPIN = (int)(Math.random()*9000)+1000;
					fileName = "new_student_profile_"+tdt+"_"+String.valueOf(randomPIN)+".jpg";

					byte[] imageByte = null;
					BASE64Decoder decoder = new BASE64Decoder();
					try {
						imageByte = decoder.decodeBuffer(imagePath);
					} catch (IOException e) {
						e.printStackTrace();
					}

					ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);

					DBJ.makeScanPath11(bis,fileName,schid,conn);

				}

				i=DBJ.updateStudentProfileImage(studentid,fileName,schid,conn);
			}

			JSONObject obj = new JSONObject();

			String status="";
			if(i>0)
			{
				status="updated";
			}
			else
			{
				status="not updated";
			}

			obj.put("status",status);

			//arr.add(obj);


			//mainobj.put("SchoolJson", arr);

			json=obj.toJSONString();
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
