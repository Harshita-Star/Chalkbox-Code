package schooldata;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.primefaces.model.file.UploadedFile;

@ManagedBean(name="studentDetails")
@ViewScoped
public class studentDetailsForJson implements Serializable
{
	String studentjson;
	String data;
	String json;
	ArrayList<StudentInfo> list;
	public studentDetailsForJson()
	{
		Connection conn=DataBaseConnection.javaConnection();
		Map<String, String> params =FacesContext.getCurrentInstance().
				getExternalContext().getRequestParameterMap();

		String sectionid = params.get("sectionid");
		String schid = params.get("schid");

		list=new DatabaseMethods1().getAllStudentStrentgthforpp(schid,sectionid,conn);

		JSONObject mainobj = new JSONObject();
		JSONArray arr=new JSONArray();

		for(StudentInfo ss:list)
		{
			JSONObject obj = new JSONObject();
			obj.put("Studentname", ss.getFullName());
			obj.put("AddNumber",ss.getAddNumber());
			arr.add(obj);
		}

		mainobj.put("SchoolJson", arr);

		json=mainobj.toJSONString();

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
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
