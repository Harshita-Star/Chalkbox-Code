package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;


@ManagedBean(name="manageMeeting")
@ViewScoped
public class ManageMeetingBean implements Serializable
{
	private static final long serialVersionUID = 1L;
    private ArrayList<MeetingInfo> meetingList = new ArrayList<MeetingInfo>();
    private DBMethodsNew dbObj = new DBMethodsNew();
    private String schoolId,userType;
    private MeetingInfo selectedMeeting;
    private List<MeetingInfo>selectedList=new ArrayList<>();
    
	public  ManageMeetingBean()
	{
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		schoolId=(String) ss.getAttribute("schoolid");
		userType=(String) ss.getAttribute("type");
		if(userType.equalsIgnoreCase("admin")||userType.equalsIgnoreCase("principal")||userType.equalsIgnoreCase("vice principal")||userType.equalsIgnoreCase("authority"))
		{
			Connection conn = DataBaseConnection.javaConnection();
			try 
			{
				meetingList = dbObj.allMeeting(schoolId, conn);
				if(meetingList.size()==0)
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No Pending Meeting found"));
				}
			} 
			catch (Exception e) {
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
		
	}

	public String approveMeeting()
	{
		Connection conn = DataBaseConnection.javaConnection();
		try 
		{
			if(dbObj.modifyMeeting(selectedMeeting.getId(),"yes",conn))
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Meeting approve successfully"));
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error occured try again."));
			}
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		finally
		{
			try {
				conn.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			
		}
		
		
		return "manageMeeting.xhtml";
	}
	
	public String cancelMeeting()
	{
		Connection conn = DataBaseConnection.javaConnection();
		try 
		{
			if(dbObj.modifyMeeting(selectedMeeting.getId(),"inactive",conn))
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Meeting deny successfully"));
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error occured try again."));
			}
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		finally
		{
			try {
				conn.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			
		}
		
		
		return "manageMeeting.xhtml";
	}
	
	
	public String approveMultiple()
	{
		Connection conn = DataBaseConnection.javaConnection();
		try 
		{
			if(selectedList.size()>0)
			{
				for(MeetingInfo obj: selectedList)
				{
				   	dbObj.modifyMeeting(obj.getId(),"yes",conn);
				}
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Meetings approve successfully"));
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please select atleast one meeting"));
			}
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		finally
		{
			try {
				conn.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			
		}
		
		return "manageMeeting.xhtml";
	}
	
	public String denyMultiple()
	{
		Connection conn = DataBaseConnection.javaConnection();
		try 
		{
			if(selectedList.size()>0)
			{
				for(MeetingInfo obj: selectedList)
				{
				   	dbObj.modifyMeeting(obj.getId(),"inactive",conn);
				}
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Meetings deny successfully"));
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please select atleast one meeting"));
			}
			
		}
		catch (Exception e) {
			// TODO: handle exception
		}
		finally
		{
			try {
				conn.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			
		}
		
		
		return "manageMeeting.xhtml";
	}
	

	public ArrayList<MeetingInfo> getMeetingList() {
		return meetingList;
	}

	public void setMeetingList(ArrayList<MeetingInfo> meetingList) {
		this.meetingList = meetingList;
	}

	public MeetingInfo getSelectedMeeting() {
		return selectedMeeting;
	}

	public void setSelectedMeeting(MeetingInfo selectedMeeting) {
		this.selectedMeeting = selectedMeeting;
	}

	public List<MeetingInfo> getSelectedList() {
		return selectedList;
	}

	public void setSelectedList(List<MeetingInfo> selectedList) {
		this.selectedList = selectedList;
	}



}

