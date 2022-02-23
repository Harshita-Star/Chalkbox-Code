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
import javax.faces.model.SelectItem;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import schooldata.ClassInfo;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.SchoolInfoList;
import schooldata.StudentInfo;
import sun.misc.BASE64Decoder;
@ManagedBean(name="newsjsonbean")
@ViewScoped
public class addNewsJsonBean implements Serializable
{
	String json;
	String selectedCLassSection,selectedSection,subject, type, addNo,concern,schoolid,classes;
	ArrayList<SelectItem> classSection,sectionList, subjectList;
	ArrayList<ClassInfo> selectedClassList;
	StudentInfo selectedStudent, selectedAss;
	Connection conn=DataBaseConnection.javaConnection();
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	DatabaseMethods1 obj=new DatabaseMethods1();
	SchoolInfoList ls = new SchoolInfoList();
	public addNewsJsonBean()
	{
          
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
			boolean checkRequest =DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
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
						DBJ.fileCreation(e.getMessage(), schoolid,conn);
						e.printStackTrace();

					}
					ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
					rendomNumber=(int)(Math.random()*9000)+1000;
					name="news"+String.valueOf(rendomNumber)+".jpg";
					DBJ.makeScanPath11(bis,name,schoolid,conn);
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
							
							try {
								conn.close();
							} catch (SQLException e) {
								e.printStackTrace();
							}
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
		} catch (Exception e) {
			// TODO: handle exception
		}
		finally
		{

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
