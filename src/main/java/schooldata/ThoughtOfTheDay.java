package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.io.FilenameUtils;
import org.primefaces.model.file.UploadedFile;
@ManagedBean(name="thoughtOfTheDay")
@ViewScoped
public class ThoughtOfTheDay implements Serializable
{
	Date validdate=new Date();
	transient
	UploadedFile image;
	ArrayList<AboutUsInfo> informationList;
	boolean status=false;
	String thoughtFor;
	public void submit()
	{
		Connection conn = DataBaseConnection.javaConnection();
		boolean extensionChecker=false;
		DatabaseMethods1 DBM = new DatabaseMethods1();
		status=DBM.checkStatusOfThoughtDetails(validdate,"all",conn);
		
		if (image != null && image.getSize() > 0)
		{
			String ext1 = FilenameUtils.getExtension(image.getFileName());
			if(ext1.equalsIgnoreCase("png")) {
				extensionChecker = true;  
			}
			else
			{
				extensionChecker = false;
			}
		}
		
	  if(extensionChecker==true)
	  {	
		
		if(status==false)
		{
			int i=DBM.addThoughtDetail(image,validdate,thoughtFor,"all","once",conn);
			if(i>=1)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Details Added Successfullly"));
				thoughtFor = "";
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error Occured Try Again !!"));
			}
		}
		else
		{
			int i=DBM.updateThoughtDetail(image,validdate,thoughtFor,"all",conn);
			if(i>=1)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Details Updated Successfullly"));
				thoughtFor = "";
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error Occured Try Again !!"));
			}
		}
	  }
	  else
	  {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Only PNG images are allowed"));
	  }

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}


	public Date getValiddate() {
		return validdate;
	}
	public void setValiddate(Date validdate) {
		this.validdate = validdate;
	}
	public ArrayList<AboutUsInfo> getInformationList() {
		return informationList;
	}
	public void setInformationList(ArrayList<AboutUsInfo> informationList) {
		this.informationList = informationList;
	}
	public UploadedFile getImage() {
		return image;
	}
	public void setImage(UploadedFile image) {
		this.image = image;
	}
	public String getThoughtFor() {
		return thoughtFor;
	}
	public void setThoughtFor(String thoughtFor) {
		this.thoughtFor = thoughtFor;
	}
	
}
