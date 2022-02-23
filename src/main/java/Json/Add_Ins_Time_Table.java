package Json;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.primefaces.model.file.UploadedFile;

import schooldata.DataBaseConnection;
import schooldata.SchoolInfoList;
import sun.misc.BASE64Decoder;

@ManagedBean(name="addinstimetable")
@ViewScoped
public class Add_Ins_Time_Table implements Serializable
{
	String studentjson;
	String data;
	String json;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();

	public Add_Ins_Time_Table()
	{
		Connection conn=DataBaseConnection.javaConnection();
		
		try {
			
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();
			String classid = params.get("classid");
			String sectionid = params.get("sectionid");
			params.get("subjectid");
			String schid = params.get("Schoolid");
			String imagePath = params.get("image");
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			JSONObject mainobj = new JSONObject();
			JSONArray arr=new JSONArray();
			JSONObject obj = new JSONObject();
			
			String status="not updated";
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
					name="timetable"+String.valueOf(rendomNumber)+".jpg";

					DBJ.makeScanPath11(bis,name,schid,conn);

				}
				new Date();
				new Date();
				int i=DBJ.submitTimeTable(classid,sectionid,name,schid,conn);
				
				
				if(i>0)
				{
					SchoolInfoList ls =  DBJ.fullSchoolInfo(schid, conn);
					String imageurl = ls.getDownloadpath()+name;
					DBJ.notification("Time Table","New Time Table Updated",sectionid+"-"+classid+"-"+schid,schid,imageurl,conn);
					status="updated";
				}
				else
				{
					status="not updated";
				}
			}
			
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


	/*public StreamedContent getImage() throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();

        if (context.getRenderResponse()) {
            // So, we're rendering the view. Return a stub StreamedContent so that it will generate right URL.
            return new DefaultStreamedContent();
        }
        else {
            // So, browser is requesting the image. Get ID value from actual request param.
            String id = context.getExternalContext().getRequestParameterMap().get("id");
            Image image = service.find(Long.valueOf(id));
            return new DefaultStreamedContent(new ByteArrayInputStream(image.getBytes()));
        }
    }
	 */

	public void makeProfile(UploadedFile file)
	{
		ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
		String realPath = ctx.getRealPath("/");
		String savePath = realPath;
		String fileName = "new_file";
		try
		{
			InputStream in = file.getInputStream();
			OutputStream out = new FileOutputStream(new File(savePath + fileName));


			int read = 0;
			byte[] bytes = new byte[1024];
			while ((read = in.read(bytes)) != -1)
			{
				out.write(bytes, 0, read);
			}
			in.close();
			out.flush();
			out.close();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}


	public String getStudentjson() {
		return studentjson;
	}


	public void setStudentjson(String studentjson) {
		this.studentjson = studentjson;
	}




}
