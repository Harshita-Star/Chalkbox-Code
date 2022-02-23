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
import javax.faces.model.SelectItem;
import javax.imageio.ImageIO;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import schooldata.ClassInfo;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.SchoolInfoList;
import schooldata.StudentInfo;

@ManagedBean(name="addnewsiosJson")
@ViewScoped
public class AddNewsJsonIos implements Serializable {


	String json,concern;
	String selectedCLassSection,selectedSection,subject, type, addNo,schoolid,classes;
	ArrayList<SelectItem> classSection,sectionList, subjectList;
	ArrayList<ClassInfo> selectedClassList;
	StudentInfo selectedStudent, selectedAss;
	SchoolInfoList ls = new SchoolInfoList();
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();

	public AddNewsJsonIos(){

		Connection conn=DataBaseConnection.javaConnection();

		try 
		{
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();
			concern = params.get("news");
			schoolid=params.get("Schoolid");
			String imagePath = params.get("image");
			type=params.get("type");
			classes=params.get("classes");

			//		type="all";
			//		classes="";
			
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
					/*
				BufferedImage image = null;
				byte[] imageByte = null;
				BASE64Decoder decoder = new BASE64Decoder();
				try {
					imageByte = decoder.decodeBuffer(imagePath);
				} catch (IOException e) {
					
				    DBJ.fileCreation(e.getMessage(), schoolid,conn);
					e.printStackTrace();

				}
				ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
				image = ImageIO.read(bis);
				bis.close();



				// write the image to a file
				File outputfile = new File("image.png");
				ImageIO.write(image, "png", outputfile);


				//InputStream stream = new ByteArrayInputStream(imagePath.getBytes(StandardCharsets.UTF_8));
			    rendomNumber=(int)(Math.random()*9000)+1000;
			    name="news"+String.valueOf(rendomNumber)+".jpg";
			    DBJ.makeScanPath11(bis,name,schoolid,conn);
					 */

					imagePath=imagePath.replaceAll(" ", "+");
					new DatabaseMethods1(schoolid).fileCreation(imagePath,conn);

					byte[] imageByte = javax.xml.bind.DatatypeConverter.parseBase64Binary(imagePath);
					try {
						BufferedImage img = ImageIO.read(new ByteArrayInputStream(imageByte));
						rendomNumber=(int)(Math.random()*9000)+1000;
						name="news"+String.valueOf(rendomNumber)+".jpg";
						DBJ.makeScanPathIOS11(img,name,schoolid,conn);

					}
					catch (IOException e) {

						e.printStackTrace();
					}
				}

				selectedClassList = new ArrayList<>();

				if(type.equalsIgnoreCase("classwise"))
				{
					String clsSecArr[] = classes.split(",");
					String cls = "";
					String sec = "";
					for(int x=0;x<clsSecArr.length;x++)
					{
						String tempArr[] = clsSecArr[x].split("-");
						cls = tempArr[0];
						sec = tempArr[1];
						ClassInfo cc = new ClassInfo();
						cc.setClassid(cls);
						cc.setSectionId(sec);
						selectedClassList.add(cc);
					}

				}


				int i=DBJ.news(concern,schoolid,name,conn,type,selectedClassList);
				////// // System.out.println(i);
				
				if(i>0)
				{
					Runnable r = new Runnable()
					{
						public void run()
						{
							ls=DBJ.fullSchoolInfo(schoolid,conn);
							concern = "Dear Parent,\n"+concern+"\nRegards,"+ls.getSmsSchoolName();

							if(type.equals("all"))
							{
								DBJ.notification("News",concern,schoolid,schoolid,"",conn);
							}
							else
							{
								for(ClassInfo cc : selectedClassList)
								{
									DBJ.notification("News",concern,cc.getSectionId()+"-"+cc.getClassid()+"-"+schoolid,schoolid,"",conn);
								}
							}
							//DBJ.notification("News",concern,schoolid,schoolid,conn);
							
						}
					};

					new Thread(r).start();

					status="updated";
				}
				else
				{
					status="not updated";
				}
			}
			else
			{
				status = "not updated";
			
			}

			

			obj.put("status",status);

			arr.add(obj);


			mainobj.put("SchoolJson", arr);

			json=mainobj.toJSONString();
		} 
		catch (Exception e) {
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
