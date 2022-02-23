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
@ManagedBean(name="addStudentFeeDetails")
@ViewScoped
public class AddStudentFeeDetailsJsonBean  implements Serializable{


	String json;

	ArrayList<StudentInfo> list;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();

	public AddStudentFeeDetailsJsonBean() {

		Connection conn=DataBaseConnection.javaConnection();

		try 
		{
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();
			String studentid = params.get("studentid");
			String schoolid = params.get("Schoolid");
			String feeAmount=params.get("amount");
			String paidby=params.get("paid");
			String chequeno=params.get("ctnno");
			String review = params.get("month");
			String imagePath = params.get("image");
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			String status = "not updated";
			if(checkRequest)
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
					name="fee"+String.valueOf(rendomNumber)+".jpg";
					DBJ.makeScanPath11(bis,name,schoolid,conn);

				}



				int i=DBJ.SchoolfeeDeatilsForJson(studentid, review,name,feeAmount,paidby,chequeno,schoolid,conn);

				//  ArrayList<NotificationInfo> info=new DatabaseMethods1().allMessageforapp(studentid);
				if(i>0)
				{
					DBJ.adminnotification("Fees Details","New Fee Details Added", "admin-"+schoolid,schoolid,"FeeDetail-"+studentid,conn);
					status="updated";
				}
				else
				{
					status="not updated";
				}
			}
			
			JSONObject mainobj = new JSONObject();
			JSONArray arr=new JSONArray();
			JSONObject obj = new JSONObject();
			obj.put("status",status);

			arr.add(obj);

			mainobj.put("SchoolJson", arr);

			json=mainobj.toJSONString();
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		finally 
		{
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
