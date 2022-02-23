package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

@ManagedBean(name="setAppPermissionBean")
@ViewScoped

public class SetAppPermissionBean implements Serializable
{
	String userType="Admin",schid;
	boolean adminApp,teacherApp,studentApp,showDemo,academicApp;
	ArrayList<ModuleInfo> moduleList,userPermissionList;
	ArrayList<SelectItem> pageList,innerPageList,userList;
	ArrayList<String> selectedValue,selectedInnerValue;

	public SetAppPermissionBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		//userList=new DatabaseMethods1().getAllUserTypeForPermission(conn);
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		schid=(String) ss.getAttribute("schid");
		adminApp = (boolean) ss.getAttribute("adminApp");
		teacherApp = (boolean) ss.getAttribute("teacherApp");
		studentApp = (boolean) ss.getAttribute("studentApp");
		academicApp = (boolean) ss.getAttribute("academicApp");
		if(schid.equals("3"))
		{
			showDemo=false;
		}
		else
		{
			showDemo=true;
		}
		//userList=new DatabaseMethods1().getAllUser(schid,conn);
		moduleList=new DatabaseMethods1().allAppPermissionModules("3",adminApp,teacherApp,studentApp,academicApp,conn);
		checkUserType();
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void checkUserType()
	{
		Connection conn=DataBaseConnection.javaConnection();
		if(moduleList.size()>0)
		{
			DatabaseMethods1.permissionOfUserForApp(moduleList,schid,conn,adminApp,teacherApp,studentApp,academicApp);
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No App Modules Are Permitted To This Account"));
		}
		/*for(ModuleInfo mm : moduleList)
		{

			if(mm.getSelectedPages().length>0)
			{
				for(int i=0;i<mm.getSelectedPages().length;i++)
				{
					////("sds : "+mm.getSelectedPages()[i]);
				}
			}
		}*/

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void submitValue()
	{
		Connection conn=DataBaseConnection.javaConnection();
		DatabaseMethods1 obj=new DatabaseMethods1();

		String pagesAdmin="";
		String imgAdmin="",imgTeacher="",imgAcademic="",imgStudent="";
		String pagesTeacher="";
		String pagesStudent="";
		String pagesAcademic="";
		
		String img = "";

		if(moduleList.size()>0)
		{
			for(ModuleInfo mm : moduleList)
			{
				if(mm.getSelectedPages().length>0)
				{
					if(mm.getModuleName().equalsIgnoreCase("Admin App"))
					{
						for(int x=0;x<mm.getSelectedPages().length;x++)
						{
							img = imageNameByModuleName(mm.getSelectedPages()[x]);
							
							if(imgAdmin.equals(""))
							{
								imgAdmin=img;
							}
							else
							{
								imgAdmin=imgAdmin+","+img;
							}
							
							if(pagesAdmin.equals(""))
							{
								pagesAdmin=mm.getSelectedPages()[x];
							}
							else
							{
								pagesAdmin=pagesAdmin+","+mm.getSelectedPages()[x];
							}
						}
					}
					else if(mm.getModuleName().equalsIgnoreCase("Teacher App"))
					{
						
						for(int x=0;x<mm.getSelectedPages().length;x++)
						{
							
							img = imageNameByModuleName(mm.getSelectedPages()[x]);
							
							if(imgTeacher.equals(""))
							{
								imgTeacher=img;
							}
							else
							{
								imgTeacher=imgTeacher+","+img;
							}
							
							if(pagesTeacher.equals(""))
							{
								pagesTeacher=mm.getSelectedPages()[x];
							}
							else
							{
								pagesTeacher=pagesTeacher+","+mm.getSelectedPages()[x];
							}
						}
					}
					else if(mm.getModuleName().equalsIgnoreCase("Academic Co-ordinator App"))
					{
						
						for(int x=0;x<mm.getSelectedPages().length;x++)
						{
							
							img = imageNameByModuleName(mm.getSelectedPages()[x]);
							
							if(imgAcademic.equals(""))
							{
								imgAcademic=img;
							}
							else
							{
								imgAcademic=imgAcademic+","+img;
							}
							
							if(pagesAcademic.equals(""))
							{
								pagesAcademic=mm.getSelectedPages()[x];
							}
							else
							{
								pagesAcademic=pagesAcademic+","+mm.getSelectedPages()[x];
							}
						}
					}
					else if(mm.getModuleName().equalsIgnoreCase("Student App"))
					{
						for(int x=0;x<mm.getSelectedPages().length;x++)
						{
							img = imageNameByModuleName(mm.getSelectedPages()[x]);
							
							if(imgStudent.equals(""))
							{
								imgStudent=img;
							}
							else
							{
								imgStudent=imgStudent+","+img;
							}
							
							if(pagesStudent.equals(""))
							{
								pagesStudent=mm.getSelectedPages()[x];
							}
							else
							{
								pagesStudent=pagesStudent+","+mm.getSelectedPages()[x];
							}
						}
					}
				}
			}

			//("Admin : "+pagesAdmin);
			//("Teacher : "+pagesTeacher);
			//("Student : "+pagesStudent);

			if(pagesAdmin.equals("") && pagesTeacher.equals("") && pagesStudent.equals("") && pagesAcademic.equals(""))
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please select atleast one module to proceed."));
			}
			else
			{
				int i = obj.setUserAppPermissions(imgAdmin,pagesAdmin, pagesTeacher, pagesStudent, schid, conn,imgTeacher,imgStudent,imgAcademic,pagesAcademic);
				if(i>=1)
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Permissions Added Successfully!"));
					moduleList=new DatabaseMethods1().allAppPermissionModules("3",adminApp,teacherApp,studentApp,academicApp,conn);
					checkUserType();
				}
				else
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Something went wrong. Please Check!"));
				}
			}

		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No App Modules Are Permitted To This Account"));
		}

		/*int i = obj.setUserPermissions(userType,moduleList,schid,conn);
            if(i>=1)
            {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Permissions Added Successfully!"));
                selectedValue=selectedInnerValue=new ArrayList<>();

                for(ModuleInfo mm : moduleList)
                {
                    if(mm.getSelectedPages().length>0)
                    {
                        for(int x=0;x<mm.getSelectedPages().length;x++)
                        {
                            if(pages.equals(""))
                            {
                            		pages=mm.getSelectedPages()[x];
                            }
                            else
                            {
                            		pages=pages+"','"+mm.getSelectedPages()[x];
                            }
                        }
                    }
                }

                //String permit=new DatabaseMethods1().checkPermissionOfAdmin(userType,schid,conn);
                String permit="'"+pages+"'";
                ////(permit);
                obj.deletePermission(schid,permit,conn);
                //userType="";
		 */
		/*}
            else
            {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Something went wrong. Please Check!"));
            }*/

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private String imageNameByModuleName(String page) 
	{
		String img = "";
		String url = "http://www.chalkboxerp.in/CBXMASTER/adminappicon/";
		
		if(page.equalsIgnoreCase("Add Homework"))
		{
			img="homework.png";
		}
		else if(page.equalsIgnoreCase("Add News/Circular"))
		{
			img="news.png";
		}
		else if(page.equalsIgnoreCase("Mark Attendance"))
		{
			img="attendance.png";
		}
		else if(page.equalsIgnoreCase("Student Details") || page.equalsIgnoreCase("My class"))
		{
			img="studentdetail.png";
		}
		else if(page.equalsIgnoreCase("Concern/Feedback"))
		{
			img="concernfeedback.png";
		}
		else if(page.equalsIgnoreCase("Communication") || page.equalsIgnoreCase("Broadcast"))
		{
			img="communication.png";
		}
		else if(page.equalsIgnoreCase("Quick Registration"))
		{
			img="quickregister.png";
		}
		else if(page.equalsIgnoreCase("Leave Requests") || page.equalsIgnoreCase("Leave Request"))
		{
			img="leave.png";
		}
		else if(page.equalsIgnoreCase("School Calendar"))
		{
			img="calendar.png";
		}
		else if(page.equalsIgnoreCase("Phonebook"))
		{
			img="phonebook.png";
		}
		else if(page.equalsIgnoreCase("GPS"))
		{
			img="gps.png";
		}
		else if(page.equalsIgnoreCase("Time Table") || page.equalsIgnoreCase("My Time Table"))
		{
			img="timetable.png";
		}
		else if(page.equalsIgnoreCase("Gallery") || page.equalsIgnoreCase("School Gallery"))
		{
			img="gallery.png";
		}
		else if(page.equalsIgnoreCase("Appointment Request"))
		{
			img="appointment.png";
		}
		else if(page.equalsIgnoreCase("Schedule Meeting"))
		{
			img="meeting.png";
		}
		else if(page.equalsIgnoreCase("Income Expense"))
		{
			img="incomeexpense.png";
		}
		else if(page.equalsIgnoreCase("Visitor"))
		{
			img="visitor.png";
		}
		else if(page.equalsIgnoreCase("Raise Ticket"))
		{
			img="ticket.png";
		}
		else if(page.equalsIgnoreCase("Approvals"))
		{
			img="approval.png";
		}
		else if(page.equalsIgnoreCase("RFID Record"))
		{
			img="rfidicon.png";
		}
		else if(page.equalsIgnoreCase("My Messages"))
		{
			img="message.png";
		}
		else
		{
			img="noimage.png";
		}
		return url+img;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public ArrayList<ModuleInfo> getModuleList() {
		return moduleList;
	}

	public void setModuleList(ArrayList<ModuleInfo> moduleList) {
		this.moduleList = moduleList;
	}

	public ArrayList<SelectItem> getPageList() {
		return pageList;
	}

	public void setPageList(ArrayList<SelectItem> pageList) {
		this.pageList = pageList;
	}

	public ArrayList<SelectItem> getInnerPageList() {
		return innerPageList;
	}

	public void setInnerPageList(ArrayList<SelectItem> innerPageList) {
		this.innerPageList = innerPageList;
	}

	public ArrayList<SelectItem> getUserList() {
		return userList;
	}

	public void setUserList(ArrayList<SelectItem> userList) {
		this.userList = userList;
	}

	public ArrayList<String> getSelectedValue() {
		return selectedValue;
	}

	public void setSelectedValue(ArrayList<String> selectedValue) {
		this.selectedValue = selectedValue;
	}

	public ArrayList<String> getSelectedInnerValue() {
		return selectedInnerValue;
	}

	public void setSelectedInnerValue(ArrayList<String> selectedInnerValue) {
		this.selectedInnerValue = selectedInnerValue;
	}

	public boolean isShowDemo() {
		return showDemo;
	}

	public void setShowDemo(boolean showDemo) {
		this.showDemo = showDemo;
	}

}
