package Json;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.imageio.ImageIO;

import org.json.simple.JSONObject;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import sun.misc.BASE64Decoder;
@ManagedBean(name="addTicketRaisingJson")
@ViewScoped
public class AddTicketRaisingJson implements Serializable
{
	String json="";int i=0;
	SimpleDateFormat ss=new SimpleDateFormat("dd/MM/yyyy");
	String schoolName="",contactNo="";
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	DatabaseMethods1 DBM=new DatabaseMethods1();


	public AddTicketRaisingJson()
	{
		Connection conn=DataBaseConnection.javaConnection();
		
		try 
		{
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();

			String userid = params.get("userid");
			String schid = params.get("schid");
			String type = params.get("type");
			String desc = params.get("desc");
			String userType = params.get("usertype");
			// String ticketDate=params.get("date");
			String imageList = params.get("imageList");
			String AppType = params.get("AppType");
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			
			String ticketid="";
			
			if(checkRequest)
			{
				int rendomNumber=0;
				String name="";
				if(AppType.equalsIgnoreCase("android"))
				{
					if(imageList==null || imageList.equals("") )
					{
						name="";

					}
					else
					{
						byte[] imageByte = null;
						BASE64Decoder decoder = new BASE64Decoder();
						try {
							imageByte = decoder.decodeBuffer(imageList);
						} catch (IOException e) {
							
							e.printStackTrace();
						}
						ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
						rendomNumber=(int)(Math.random()*9000)+1000;
						name="scrnshot"+String.valueOf(rendomNumber)+".jpg";
						DBJ.makeScanPath11(bis,name,schid,conn);

					}
				}
				else
				{
					if(!imageList.equals(""))
					{


						imageList=imageList.replaceAll(" ", "+");
						new DatabaseMethods1(schid).fileCreation(imageList,conn);

						byte[] imageByte = javax.xml.bind.DatatypeConverter.parseBase64Binary(imageList);
						try {
							BufferedImage img = ImageIO.read(new ByteArrayInputStream(imageByte));
							rendomNumber=(int)(Math.random()*9000)+1000;
							name="Homewrok"+String.valueOf(rendomNumber)+".jpg";
							DBJ.makeScanPathIOS11(img,name,schid,conn);

						}
						catch (IOException e) {

							e.printStackTrace();
						}




					}
				}

				/*String filePath="";
		        if(imageList!=null)
		        {
		        	String image[]=imageList.split("###");
		            for(int i=0;i<image.length;i++)
		            {
		    	        	Date tdy=new Date();
		    	    		String tdt = new SimpleDateFormat("yMdhms").format(tdy);
		    	    		int randomPIN = (int)(Math.random()*9000)+1000;
		    	    		String fileName = "scrnshot"+tdt+"_"+String.valueOf(randomPIN)+".jpg";
		    	    		if(!image[i].equals(""))
		    	    		{
		    	    			BufferedImage imagee = null;
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

		    		    		DBJ.makeScanPath11(bis,fileName,schid,conn);

		    		    		if(filePath.equals(""))
		    		    		{
		    		    			filePath=fileName;

		    		    		}
		    		    		else
		    		    		{
		    		    			filePath=filePath+","+fileName;

		    		    		}
		    	    		}
		            }
		        }*/

				Date dt=new Date();
				/*try {
					dt=ss.parse(ticketDate);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				 */
				int n = (int) (100000 + Math.random() * 900000);
				ticketid="CB"+String.valueOf(n);
				i=DBJ.addTicketRaising(userid,schid,type,desc,dt,userType,name,ticketid,conn);
				contactNo=DBM.contactNo("Developers",conn);
			}

			
			JSONObject obj = new JSONObject();
			String status="";
			if(i>0)
			{
				status="Ticket Added Successfully.Your Ticket Id No "+ticketid;
				schoolName=DBM.getSMSSchoolName(schid, conn);

			}
			else
			{
				status="";
			}
			obj.put("status",status);
			json=obj.toJSONString();
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		finally
		{
			try
			{
				conn.close();
			} catch (Exception e) {
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
