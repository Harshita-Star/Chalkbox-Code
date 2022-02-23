package Json;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
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
import sun.misc.BASE64Decoder;

@ManagedBean(name="addHomeWorkJson")
@ViewScoped
public class addHomeworkTostudent implements Serializable
{
	String studentjson;
	String data;
	String json,sms;
	ArrayList<StudentInfo> studentList=new ArrayList<>();
	String classid,sectionid,subjectid,desc,schid,imagePath,subjectType,userid;
	Connection conn=DataBaseConnection.javaConnection();
	DataBaseMeathodJson DBJ=new DataBaseMeathodJson();
	DatabaseMethods1 DBM= new DatabaseMethods1();

	String notificationtitle="";
	public addHomeworkTostudent()
	{

		try 
        {
			Map<String, String> params =FacesContext.getCurrentInstance().
					getExternalContext().getRequestParameterMap();
			userid=params.get("userid");
			classid = params.get("classid");
			String sectionid1 = params.get("sectionid");
		
			subjectid = params.get("subjectid");
			desc = params.get("desc");
			schid = params.get("Schoolid");
			imagePath = params.get("image");
			sms = params.get("sms");
			String type=params.get("type");
			
			if(type==null||type.equals(""))
			{
				type="homework";
			}
			
			
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
					name=type+String.valueOf(rendomNumber)+".jpg";
					DBJ.makeScanPath11(bis,name,schid,conn);

				}
				Date pDate=new Date();
				Date assShowDate=new Date();
			
				String sectionArr[]=sectionid1.split(",");
				int i=0;
				String desc1=desc;
				for(int j=0;j<sectionArr.length;j++)
				{
					
					sectionid=sectionArr[j];
				
				i=DBJ.submitAssignment(classid,sectionid,subjectid,userid, pDate, assShowDate,name, "", "","", "", "", type, desc1,schid,conn);
				
				if(i>0)
				{

					if(type.equalsIgnoreCase("homework"))
					{
						notificationtitle="Home Work";
					}
					else
					{
						notificationtitle="Notes";
					}
					String className = DBM.classNameFromidSchid(schid,classid, session, conn);
					
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

					String cls = "Class : "+clsSection+"\nSubject : "+subjectName;
					desc = cls+"\n"+desc1;


					Runnable r = new Runnable()
					{
						public void run()
						{
							Connection con = DataBaseConnection.javaConnection();
							if(sms==null || sms.equals(""))
							{

							}
							else
							{
								if(subjectid.equalsIgnoreCase("all"))
								{
									if(sectionid.equalsIgnoreCase("All"))
									{
										DBJ.notification(notificationtitle,desc,classid+"-"+schid,schid,"",con);
										studentList=objStd.studentDetail("", "", classid,QueryConstants.BY_CLASS,QueryConstants.ATTENDANCE,null,null,"","","","", session,schid, stdColumnList, con);
									}
									else
									{
										DBJ.notification(notificationtitle,desc,sectionid+"-"+classid+"-"+schid,schid,"",con);
										studentList=objStd.studentDetail("", sectionid, classid,QueryConstants.BY_CLASS_SECTION,QueryConstants.ATTENDANCE_RFID,null,null,"","","","", session,schid, stdColumnList, con);
									}
								}
								else
								{
									if(sectionid.equalsIgnoreCase("All"))
									{
										ArrayList<SelectItem> allSection = DBJ.allSection(classid, schid, con);
										if(allSection.size()>0)
										{
											subjectType=DBJ.subjectNameAndTypeFromid(subjectid,schid,con);
											String[] temp=subjectType.split(",");
											if(temp[1].equalsIgnoreCase("Mandatory"))
											{
												DBJ.notification(notificationtitle,desc,classid+"-"+schid,schid,"",con);
												studentList=objStd.studentDetail("", "", classid,QueryConstants.BY_CLASS,QueryConstants.ATTENDANCE,null,null,"","","","", session,schid, stdColumnList, con);
											}
											else
											{
												studentList=DBM.getAllStudentStrentgthForOptional(schid,subjectid,sectionid,classid,"fromJson",con);
												for(StudentInfo ss : studentList)
												{
													if(ss.getFathersPhone()==ss.getMothersPhone())
													{
														DBJ.notification(notificationtitle,desc, ss.getFathersPhone()+"-"+ss.getAddNumber()+"-"+schid,schid,"",con);
													}
													else
													{
														DBJ.notification(notificationtitle,desc, ss.getFathersPhone()+"-"+ss.getAddNumber()+"-"+schid,schid,"",con);
														DBJ.notification(notificationtitle,desc, ss.getMothersPhone()+"-"+ss.getAddNumber()+"-"+schid,schid,"",con);
													}
												}
											}
										}
									}
									else
									{
										subjectType=DBJ.subjectNameAndTypeFromid(subjectid,schid,con);
										String[] temp=subjectType.split(",");
										if(temp[1].equalsIgnoreCase("Mandatory"))
										{
											DBJ.notification(notificationtitle,desc,sectionid+"-"+classid+"-"+schid,schid,"",con);
											studentList=objStd.studentDetail("", sectionid, classid,QueryConstants.BY_CLASS_SECTION,QueryConstants.ATTENDANCE_RFID,null,null,"","","","", session,schid, stdColumnList, con);
										}
										else
										{
											studentList=DBM.getAllStudentStrentgthForOptional(schid,subjectid,sectionid,classid,"fromJson",con);
											for(StudentInfo ss : studentList)
											{
												if(ss.getFathersPhone()==ss.getMothersPhone())
												{
													DBJ.notification(notificationtitle,desc, ss.getFathersPhone()+"-"+ss.getAddNumber()+"-"+schid,schid,"",con);
												}
												else
												{
													DBJ.notification(notificationtitle,desc, ss.getFathersPhone()+"-"+ss.getAddNumber()+"-"+schid,schid,"",con);
													DBJ.notification(notificationtitle,desc, ss.getMothersPhone()+"-"+ss.getAddNumber()+"-"+schid,schid,"",con);
												}
											}
										}
									}
								}

								if(sms.equalsIgnoreCase("yes"))
								{
									SchoolInfoList info=DBJ.fullSchoolInfo(schid, con);

									if(studentList.size()>0)
									{
										String contactNumber = "";
										String addNumber="";
										int x=0;


										String typeMessage="Dear Parent,"+"\nHomework for\n"+ desc+"\n"+"Regards\n"+info.getSmsSchoolName();
										for(StudentInfo ss : studentList)
										{
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
											DBJ.messageurl1(contactNumber, typeMessage,addNumber,schid, con);
										}
									}

								}
							}

							try {
								con.close();
							} catch (Exception e) {
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
       catch (Exception e) 
        {
			// TODO: handle exception
		}
		finally {
			try {
				conn.close();
			} catch (Exception e2) {
				// TODO: handle exception
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
