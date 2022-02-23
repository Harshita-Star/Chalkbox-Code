package schooldata;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.io.FilenameUtils;
import org.json.simple.JSONObject;
import org.primefaces.model.file.UploadedFile;

@ManagedBean(name="notificationToAdminTeacherBean")
@ViewScoped
public class NotificationToAdminTeacherBean implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7851137287777700285L;
	String notification;
	String title,type;
	transient
	UploadedFile image;
	boolean showTable,showNotificationFor;
	List<SchoolInfo> schoolList,selectedList;
    String notification_for; 
	
	public NotificationToAdminTeacherBean() 
	{
		type="all";
		showNotificationFor=true;
		selectedList = new ArrayList<>();
	}
	
	public void checkType()
	{
		if(type.equalsIgnoreCase("all"))
		{
			showTable=false;
			showNotificationFor=true;
			schoolList = new ArrayList<>();
			selectedList = new ArrayList<>();
		}
		else
		{
			Connection conn=DataBaseConnection.javaConnection();
			DatabaseMethods1 obj = new DatabaseMethods1();
			showTable = true;
			showNotificationFor=false;
			selectedList = new ArrayList<>();
			schoolList = obj.allSchoolListForMasterNotification(conn);
			
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	 
	public void sendNotification()
	{
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM = new DatabaseMethods1();
		String groupid="";
		String imageurl = "";
		String fileName = "";
		
	  boolean extensionChecker = false;
	  if (image != null && image.getSize() > 0)
	  {
			String ext1 = FilenameUtils.getExtension(image.getFileName());
			if(ext1.equalsIgnoreCase("png")) {
				extensionChecker = false;  
			}
			else
			{
				extensionChecker = true;
			}
	  }
	  
	  
	  if(extensionChecker==false)
	  {		  
       
		if (image != null && image.getSize() > 0) {
			String crdt = new SimpleDateFormat("yMdhms").format(new Date());
			String exten[]=image.getFileName().split("\\.");
			fileName = "notification_"+crdt+"."+exten[exten.length-1];
			DBM.makeScanPathForMasterAdmin(image, fileName);

			imageurl = "http://www.chalkboxerp.in/CBXMASTER/"+fileName;
		}

		if(type.equalsIgnoreCase("all"))
		{
			if(notification_for.equalsIgnoreCase("Common"))
			{
				groupid="chalkbox";
			}
			else if(notification_for.equalsIgnoreCase("Teacher"))
			{
				groupid="cbteacher";
			}
			
			new DatabaseMethods1().sendmessagetoadminwithimg(title, notification, imageurl, groupid,"",conn);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Notification Sent Sucessfully!"));
			type="all";
			title=notification="";
			image = null;
			schoolList = new ArrayList<>();
			selectedList = new ArrayList<>();
			showTable = false;
			notification_for = "";
		}
		else
		{
			if(selectedList.size()>0)
			{
				for(SchoolInfo ss : selectedList)
				{
					groupid = "admin-" +ss.getId();
					new DatabaseMethods1().sendmessagetoadminwithimg(title, notification, imageurl, groupid,ss.getId(),conn);
				}
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Notification Sent Sucessfully!"));
				type="all";
				title=notification="";
				image = null;
				schoolList = new ArrayList<>();
				selectedList = new ArrayList<>();
				showTable = false;
				notification_for = "";
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please Select Atleast One School To Send The Notification"));
			}
		}
	  }
	  else
	  {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Only PNG images are allowed"));
	  }
	  
	  try {
		conn.close();
	} catch (Exception e) {
		// TODO: handle exception
	}

		//adminnotification("app_update", notification, groupid);

		//return "notificationToAdminTeacher.xhtml";
	}

	public void adminnotification(String title, String message1,String imageurl, String group
			) {
		final String apiKey = "AAAAaoFmDCc:APA91bFSCXCDvpPPN70s8v_d83f2p6EgJVkGOs_JIsg6muf5RUx1YQNFRxgNUP8jZjCwePaDEsx3KEDX3tynYMHq_ZI0qsGCZOoMipS2nc5C42JzeWkyCIu2rd5iTKN-elmUBH3ggDnt";
		URL url;
		try {
			url = new URL("https://fcm.googleapis.com/fcm/send");

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Authorization", "key=" + apiKey);

			conn.setDoOutput(true);

			JSONObject message = new JSONObject();
			message.put("to", "/topics/" + group);
			message.put("priority", "high");

			JSONObject notification = new JSONObject();
			notification.put("title", title);

			notification.put("body", message1);
			notification.put("image", imageurl);

			message.put("notification", notification);
			message.put("data", notification);

			String input = message.toString();

			// String input =
			// "{\"notification\" : {\"title\" : \"Test\"}, \"to\":\"d1zUCWK_Eaw:APA91bE9z3FOZa7zT9m4us_lHmqK9z65a02_2LkudAb3jWPMQYva_SvTe0prrAVEceOnwNpm6DzKtBQ48WTFsR0IhvX0wOYbAgW4hXhhihk4BZqHpm-ZZW-cZ6DTXZkc9Q-doGe5M1jt\"}";

			OutputStream os = conn.getOutputStream();
			os.write(input.getBytes());
			os.flush();
			os.close();

			conn.getResponseCode();

			BufferedReader in = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}


		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public String getNotification() {
		return notification;
	}

	public void setNotification(String notification) {
		this.notification = notification;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public UploadedFile getImage() {
		return image;
	}

	public void setImage(UploadedFile image) {
		this.image = image;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isShowTable() {
		return showTable;
	}

	public void setShowTable(boolean showTable) {
		this.showTable = showTable;
	}

	public List<SchoolInfo> getSchoolList() {
		return schoolList;
	}

	public void setSchoolList(List<SchoolInfo> schoolList) {
		this.schoolList = schoolList;
	}

	public List<SchoolInfo> getSelectedList() {
		return selectedList;
	}

	public void setSelectedList(List<SchoolInfo> selectedList) {
		this.selectedList = selectedList;
	}

	public String getNotification_for() {
		return notification_for;
	}

	public void setNotification_for(String notification_for) {
		this.notification_for = notification_for;
	}

	public boolean isShowNotificationFor() {
		return showNotificationFor;
	}

	public void setShowNotificationFor(boolean showNotificationFor) {
		this.showNotificationFor = showNotificationFor;
	}




}
