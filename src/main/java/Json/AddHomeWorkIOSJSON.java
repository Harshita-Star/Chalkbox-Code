package Json;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.imageio.ImageIO;
import javax.servlet.ServletContext;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.primefaces.model.file.UploadedFile;

import schooldata.DataBaseConnection;
import schooldata.DataBaseMethodStudent;
import schooldata.DatabaseMethods1;
import schooldata.SchoolInfoList;
import schooldata.StudentInfo;
import session_work.QueryConstants;


@ManagedBean(name="addhomeworkios")
@ViewScoped
public class AddHomeWorkIOSJSON  implements Serializable{

	String studentjson;
	String data;
	String json;
	Connection conn=DataBaseConnection.javaConnection();
	String classid,sectionid,subjectid,desc,schid,imagePath,sms,subjectName,userid,subjectType;
	ArrayList<StudentInfo> studentList=new ArrayList<>();
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	DatabaseMethods1 DBM= new DatabaseMethods1();

	public AddHomeWorkIOSJSON()
	{
		try {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();
			userid=params.get("userid");
			classid = params.get("classid");
			sectionid = params.get("sectionid");
			subjectid = params.get("subjectid");
			desc = params.get("desc");
			schid = params.get("Schoolid");
			imagePath = params.get("image");
			sms = params.get("sms");
			
			Map<String, String> sysParams =FacesContext.getCurrentInstance().
					getExternalContext().getRequestHeaderMap();
			String userAgent = sysParams.get("User-Agent");
			boolean checkRequest = DBJ.checkRequestAuthenticity(userAgent); // If true, proceed else block
			JSONObject mainobj = new JSONObject();
			JSONArray arr=new JSONArray();
			JSONObject obj = new JSONObject();
			String status="not updated";
			
			DataBaseMethodStudent objStd=new DataBaseMethodStudent();
			ArrayList<String> stdColumnList=objStd.attendanceFieldList();
			String session=DatabaseMethods1.selectedSessionDetails(schid, conn);
			
			if(checkRequest)
			{
				int rendomNumber=0;
				String name="";
				if(!imagePath.equals(""))
				{
					imagePath=imagePath.replaceAll(" ", "+");
					new DatabaseMethods1(schid).fileCreation(imagePath,conn);

					byte[] imageByte = javax.xml.bind.DatatypeConverter.parseBase64Binary(imagePath);
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
				Date pDate=new Date();
				Date assShowDate=new Date();
				int i=DBJ.submitAssignment(classid,sectionid,subjectid,userid, pDate, assShowDate,name, "", "",
						"", "", "", "notes", desc,schid,conn);
				
				//subjectName = DBJ.subjectNameFromid(subjectid, schid, conn);
				if(i>0)
				{
					String className = new DatabaseMethods1().classNameFromidSchid(schid,classid, session, conn);
					String sectionName = new DatabaseMethods1().sectionNameByIdSchid(schid,sectionid, conn);
					String clsSection = className+"-"+sectionName;
					subjectName = DBJ.subjectNameFromid(subjectid, schid, conn);

					String cls = "Class : "+clsSection+"\nSubject : "+subjectName;
					desc = cls+"\n"+desc;

					//DBJ.notification("Home Work",desc,sectionid+"-"+classid+"-"+schid,schid,conn);
					Runnable r = new Runnable()
					{
						public void run()
						{
							if(sms==null || sms.equals(""))
							{

							}
							else
							{
								subjectType=DBJ.subjectNameAndTypeFromid(subjectid,schid,conn);
								String[] temp=subjectType.split(",");
								if(temp[1].equalsIgnoreCase("Mandatory"))
								{
									DBJ.notification("Home Work",desc,sectionid+"-"+classid+"-"+schid,schid,"",conn);
									
									studentList=objStd.studentDetail("", sectionid, classid,QueryConstants.BY_CLASS_SECTION,QueryConstants.ATTENDANCE_RFID,null,null,"","","","", session,schid, stdColumnList, conn);
								}
								else
								{

									studentList=DBM.getAllStudentStrentgthForOptional(schid,subjectid,sectionid,classid,"fromJson",conn);
									for(StudentInfo ss : studentList)
									{
										DBJ.notification("Home Work",desc, ss.getFathersPhone()+"-"+ss.getAddNumber()+"-"+schid,schid,"",conn);
										DBJ.notification("Home Work",desc, ss.getMothersPhone()+"-"+ss.getAddNumber()+"-"+schid,schid,"",conn);
									}
								}

								if(sms.equalsIgnoreCase("yes"))
								{
									SchoolInfoList info=DBJ.fullSchoolInfo(schid, conn);

									if(studentList.size()>0)
									{
										String contactNumber = "";
										String addNumber="";
										int x=0;

										String typeMessage="Dear Parent,"+"\nHomework for\n"+ desc+"\n"+"Regards\n"+info.getSmsSchoolName();
										for(StudentInfo ss : studentList)
										{

											//secondCounter++;
											if (String.valueOf(ss.getFathersPhone()).length() == 10
													&& !String.valueOf(ss.getFathersPhone()).equals("2222222222")
													&& !String.valueOf(ss.getFathersPhone()).equals("9999999999")
													&& !String.valueOf(ss.getFathersPhone()).equals("1111111111")
													&& !String.valueOf(ss.getFathersPhone()).equals("1234567890")
													&& !String.valueOf(ss.getFathersPhone()).equals("0123456789"))
											{
												x++;
												if(contactNumber.equals(""))
												{
													contactNumber=String.valueOf(ss.getFathersPhone());
													addNumber=ss.getAddNumber();
												}
												else
												{
													contactNumber=contactNumber+","+String.valueOf(ss.getFathersPhone());
													addNumber=addNumber+","+ss.getAddNumber();
												}
											}

										}


										if(x>0)
										{
											DBJ.messageurl1(contactNumber, typeMessage,addNumber,schid, conn);
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
			}
			else
			{
				status="not updated";
				
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
