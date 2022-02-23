package Json;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.imageio.ImageIO;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.StudentInfo;

@ManagedBean(name="schoolfeedbackios")
@ViewScoped
public class SchoolFeedbackIosJsonBean implements Serializable {

	String json;
	String imagePath;
	ArrayList<StudentInfo> list;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();

	public SchoolFeedbackIosJsonBean() {

		Connection conn=DataBaseConnection.javaConnection();

 
		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String studentid = params.get("studentid");
			//  String rating = params.get("rating");
			String review = params.get("review");
			String schoolid=params.get("Schoolid");
			imagePath = params.get("image");


			//   Date newDate=null;
			/*  try {
			//newDate= new SimpleDateFormat("dd/MM/yyyy").parse(date);
			} catch (ParseException e) {
				
				e.printStackTrace();
			}
			 */

			//	list=new DatabaseMethods1().parentListForApp(studentid);
			String status="";
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			if(checkRequest)
			{
				if(studentid==null || studentid.equals(""))
				{
					status="not updated";
				}
				else
				{
					int rendomNumber=0;
					String name="";
					if(!imagePath.equals(""))
					{
						imagePath=imagePath.replaceAll(" ", "+");
						new DatabaseMethods1(schoolid).fileCreation(imagePath,conn);

						byte[] imageByte = javax.xml.bind.DatatypeConverter.parseBase64Binary(imagePath);
						try {
							BufferedImage img = ImageIO.read(new ByteArrayInputStream(imageByte));
							rendomNumber=(int)(Math.random()*9000)+1000;
							name="concernfeedback"+String.valueOf(rendomNumber)+".jpg";
							DBJ.makeScanPathIOS11(img,name,schoolid,conn);

						}
						catch (IOException e) {

							e.printStackTrace();
						}

					}

					int i=DBJ.Schoolfeedback(studentid, review,schoolid,conn,name);

					if(i>0)
					{
						StudentInfo ln=DBJ.studentDetailslistByAddNo(studentid,schoolid,conn);
						DBJ.adminnotification("Concern/Feedback",ln.getFname()+" Added A Concern/Feedback",
								"admin-"+schoolid,schoolid,"Concern"+studentid,conn);
						status="updated";
					}
					else
					{
						status="not updated";
					}
				}
			}
			else
			{
				status="not updated";
			}

			JSONObject mainobj = new JSONObject();
			JSONArray arr=new JSONArray();

			JSONObject obj = new JSONObject();
			obj.put("status",status);

			arr.add(obj);

			mainobj.put("SchoolJson", arr);

			json=mainobj.toJSONString();
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
