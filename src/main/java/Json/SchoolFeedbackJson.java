package Json;

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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import schooldata.DataBaseConnection;
import schooldata.StudentInfo;
import sun.misc.BASE64Decoder;
@ManagedBean(name="schoolfeedback")
@ViewScoped
public class SchoolFeedbackJson implements Serializable {

	String json;
	String imagePath;
	ArrayList<StudentInfo> list;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();

	public SchoolFeedbackJson() {

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
						byte[] imageByte = null;
						BASE64Decoder decoder = new BASE64Decoder();
						try {
							imageByte = decoder.decodeBuffer(imagePath);
						} catch (IOException e) {
							e.printStackTrace();
						}
						ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
						/*image = ImageIO.read(bis);
			    			bis.close();
						 */
						// write the image to a file
						/*File outputfile = new File("image.png");
			    			ImageIO.write(image, "png", outputfile);
						 */
						//InputStream stream = new ByteArrayInputStream(imagePath.getBytes(StandardCharsets.UTF_8));
						rendomNumber=(int)(Math.random()*9000)+1000;
						name="concernfeedback"+String.valueOf(rendomNumber)+".jpg";
						DBJ.makeScanPath11(bis,name,schoolid,conn);

					}

					int i=DBJ.Schoolfeedback(studentid, review,schoolid,conn,name);

					if(i>0)
					{
						StudentInfo ln=DBJ.studentDetailslistByAddNo(studentid,schoolid,conn);
						DBJ.adminnotification("Concern/Feedback",ln.getFname()+" Added A Concern/Feedback",
								"admin-"+schoolid,schoolid,"Concern-"+studentid,conn);
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
