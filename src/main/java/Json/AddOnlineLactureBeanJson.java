package Json;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import schooldata.DataBaseConnection;
import schooldata.DataBaseMethodStudent;
import schooldata.DatabaseMethods1;
import schooldata.EmployeeInfo;
import schooldata.StudentInfo;
import session_work.QueryConstants;
import sun.misc.BASE64Decoder;
@ManagedBean(name="addOnlineLactureBean")
@ViewScoped
public class AddOnlineLactureBeanJson implements Serializable{

	 

	String studentjson;
	String data;
	String json,sms;
	ArrayList<StudentInfo> studentList=new ArrayList<>();

	String classid,sectionid,subjectid,desc,schid,imagePath,subjectType,userid;
	String status="Updated";
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	DatabaseMethods1 DBM= new DatabaseMethods1();

	
	public AddOnlineLactureBeanJson() 
	{
		java.sql.Connection conn=DataBaseConnection.javaConnection();	
		
		
		try
		{
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();
			userid=params.get("userid");
			classid = params.get("classid");
			sectionid = params.get("sectionid");
			subjectid = params.get("subjectid");
			desc = params.get("desc");
			schid = params.get("Schoolid");
			imagePath = params.get("image");
			String  title = params.get("title");
			String  link = params.get("youtubelink");
			
		   JSONObject mainobj = new JSONObject();
			JSONArray arr=new JSONArray();
			JSONObject obj = new JSONObject();


			
			DataBaseMethodStudent objStd=new DataBaseMethodStudent();
			ArrayList<String> stdColumnList=objStd.attendanceFieldList();
			String session=DatabaseMethods1.selectedSessionDetails(schid, conn);
			
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
				name="onlineLacture"+String.valueOf(rendomNumber)+".jpg";
				DBJ.makeScanPath11(bis,name,schid,conn);

			}
			
			Date pDate=new Date();
			String staffName = DBM.employeeNameByuserNameWithSchid(userid, schid, conn);
			if(staffName.equals(""))
			{
				staffName = userid.toUpperCase()+"("+DBM.userTypeOfUser(userid, schid, conn)+")";
			}
			
			int i=DBJ.submitOnlineLacture(classid,sectionid,subjectid,userid, pDate,name,link,title, desc,schid,conn);
			if(i>0)
			{
				String className = new DatabaseMethods1().classNameFromidSchid(schid,classid, session, conn);
				String sectionName = "";
				String clsSection = "";
				if(sectionid.equalsIgnoreCase("All"))
				{
					sectionName = "All";
					clsSection = className;
				}
				else
				{
					sectionName = new DatabaseMethods1().sectionNameByIdSchid(schid,sectionid, conn);
					clsSection = className+"-"+sectionName;
				}

				String subjectName = "";
				if(subjectid.equalsIgnoreCase("All"))
				{
					subjectName = "All";
				}
				else
				{
					subjectName = DBJ.subjectNameFromid(subjectid, schid, conn);
				}
			//	String cls = "Class : "+clsSection+"\nSubject : "+subjectName;
				//String desp ="Online lecture added for <"+subjectName+">";

				String desp = "Dear User,\n"
						+ "Kindly check \"E- Learning\" icon as a file is being uploaded by "+staffName+" subject teacher of "+subjectName+" in class "+clsSection+" under topic "+title+".\n"
						+ "\n"
						+ "Regards";
				
				Runnable r = new Runnable()
				{
					public void run()
					{
						ArrayList<EmployeeInfo> principleId = DBM.getPrincipleId(schid, conn);
						ArrayList<EmployeeInfo> cordinators = DBJ.cordinatorListByClassId(classid, schid, session, "active", conn);
						ArrayList<EmployeeInfo> notifyStaff = new ArrayList<EmployeeInfo>();
						notifyStaff.addAll(principleId);
						notifyStaff.addAll(cordinators);
						
						DBJ.adminnotification("E-Learning",desp,"admin-"+schid,schid,"",conn);
						for(EmployeeInfo empi : notifyStaff) 
						{
							DBJ.adminnotification("E-Learning",desp,"staff"+"-"+empi.getId()+"-"+schid,schid,"",conn);
						}
						
						if(subjectid.equalsIgnoreCase("all"))
						{
							if(sectionid.equalsIgnoreCase("All"))
							{
								DBJ.notification("E-Learning",desp,classid+"-"+schid,schid,"",conn);
								//studentList=objStd.studentDetail("", "", classid,QueryConstants.BY_CLASS,QueryConstants.ATTENDANCE,null,null,"","","","", session,schid, stdColumnList, conn);
							}
							else
							{
								DBJ.notification("E-Learning",desp,sectionid+"-"+classid+"-"+schid,schid,"",conn);
								//studentList=objStd.studentDetail("", sectionid, classid,QueryConstants.BY_CLASS_SECTION,QueryConstants.ATTENDANCE_RFID,null,null,"","","","", session,schid, stdColumnList, conn);
							}
						}
						else
						{
							if(sectionid.equalsIgnoreCase("All"))
							{
								ArrayList<SelectItem> allSection = DBJ.allSection(classid, schid, conn);
								if(allSection.size()>0)
								{
									subjectType=DBJ.subjectNameAndTypeFromid(subjectid,schid,conn);
									String[] temp=subjectType.split(",");
									if(temp[1].equalsIgnoreCase("Mandatory"))
									{
										DBJ.notification("E-Learning",desp,classid+"-"+schid,schid,"",conn);
										//studentList=objStd.studentDetail("", "", classid,QueryConstants.BY_CLASS,QueryConstants.ATTENDANCE,null,null,"","","","", session,schid, stdColumnList, conn);
									}
									else
									{
										studentList=DBM.getAllStudentStrentgthForOptional(schid,subjectid,sectionid,classid,"fromJson",conn);
										for(StudentInfo ss : studentList)
										{
											if(ss.getFathersPhone()==ss.getMothersPhone())
											{
												DBJ.notification("E-Learning",desp, ss.getFathersPhone()+"-"+ss.getAddNumber()+"-"+schid,schid,"",conn);
											}
											else
											{
												DBJ.notification("E-Learning",desp, ss.getFathersPhone()+"-"+ss.getAddNumber()+"-"+schid,schid,"",conn);
												DBJ.notification("E-Learning",desp, ss.getMothersPhone()+"-"+ss.getAddNumber()+"-"+schid,schid,"",conn);
											}
										}
									}
								}
							}
							else
							{
								subjectType=DBJ.subjectNameAndTypeFromid(subjectid,schid,conn);
								String[] temp=subjectType.split(",");
								if(temp[1].equalsIgnoreCase("Mandatory"))
								{
									DBJ.notification("E-Learning",desp,sectionid+"-"+classid+"-"+schid,schid,"",conn);
									//studentList=objStd.studentDetail("", sectionid, classid,QueryConstants.BY_CLASS_SECTION,QueryConstants.ATTENDANCE_RFID,null,null,"","","","", session,schid, stdColumnList, conn);
								}
								else
								{
									studentList=DBM.getAllStudentStrentgthForOptional(schid,subjectid,sectionid,classid,"fromJson",conn);
									for(StudentInfo ss : studentList)
									{
										if(ss.getFathersPhone()==ss.getMothersPhone())
										{
											DBJ.notification("E-Learning",desp, ss.getFathersPhone()+"-"+ss.getAddNumber()+"-"+schid,schid,"",conn);
										}
										else
										{
											DBJ.notification("E-Learning",desp, ss.getFathersPhone()+"-"+ss.getAddNumber()+"-"+schid,schid,"",conn);
											DBJ.notification("E-Learning",desp, ss.getMothersPhone()+"-"+ss.getAddNumber()+"-"+schid,schid,"",conn);
										}
									}
								}
							}
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
			
			obj.put("status",status);
			arr.add(obj);
			mainobj.put("SchoolJson", arr);
			json=mainobj.toJSONString();	
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
	
		
		
		
		
	}
	
	
	public void renderJson() throws IOException
	{
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		externalContext.setResponseContentType("application/json");
		externalContext.setResponseCharacterEncoding("UTF-8");
		externalContext.getResponseOutputWriter().write(json);
		facesContext.responseComplete();

	}
	
}
