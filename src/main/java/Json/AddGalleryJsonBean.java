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
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.json.simple.JSONObject;
import org.primefaces.model.file.UploadedFile;

import schooldata.DataBaseConnection;
import sun.misc.BASE64Decoder;

@ManagedBean(name="addGalleryJson")
@ViewScoped

public class AddGalleryJsonBean implements Serializable
{
	String json;
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();


	public AddGalleryJsonBean()
	{
		Connection conn=DataBaseConnection.javaConnection();

		try 
		{
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String tag = params.get("name");
			String imageList = params.get("imageList");
			String schoolid = params.get("Schoolid");
			String type = params.get("type");
			String tagId = params.get("tagid");
			String cls = params.get("classid");
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			int x=0;
			if(checkRequest)
			{
				String image[]=imageList.split("###");
				////// // System.out.println(image.length);
				String filePath="";
				for(int i=0;i<image.length;i++)
				{
					Date tdy=new Date();

					//Timestamp tdt=new Timestamp(tdy.getTime());

					String tdt = new SimpleDateFormat("yMdhms").format(tdy);

					int randomPIN = (int)(Math.random()*9000)+1000;
					String fileName = "new_file_gallery_"+tdt+"_"+String.valueOf(randomPIN)+".jpg";

					if(!image[i].equals(""))
					{

						byte[] imageByte = null;
						BASE64Decoder decoder = new BASE64Decoder();
						try
						{
							imageByte = decoder.decodeBuffer(image[i]);
						}
						catch (IOException e)
						{
							e.printStackTrace();
						}
						ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);

						DBJ.makeScanPath11(bis,fileName,schoolid,conn);

						if(filePath.equals(""))
						{
							filePath=fileName;

						}
						else
						{
							filePath=filePath+","+fileName;

						}
					}
					////// // System.out.println("image "+i+" : "+image[i]);
				}
				
				if(type.equalsIgnoreCase("add"))
				{
					x=DBJ.addGalleryImages(filePath,tag,schoolid,conn,cls,"yes");
				}
				else if(type.equalsIgnoreCase("edit"))
				{
					x=DBJ.updateGalleryImages(filePath,tag,schoolid,conn,tagId,cls,"yes");
				}
			}

			

			JSONObject obj = new JSONObject();
			String status="";
			if(x>0)
			{
				status="Gallery Updated Successfully.";
			}
			else
			{
				status="not updated";
			}

			////// // System.out.println(status);
			obj.put("status",status);

			//arr.add(obj);

			json=obj.toJSONString();
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

	public void makeProfile(UploadedFile file)
	{
		Date tdy=new Date();

		Timestamp tdt=new Timestamp(tdy.getTime());
		int randomPIN = (int)(Math.random()*9000)+1000;

		ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
		String realPath = ctx.getRealPath("/");
		String savePath = realPath;
		String fileName = "new_file_gallery_"+tdt+"_"+String.valueOf(randomPIN)+".jpg";
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
}
