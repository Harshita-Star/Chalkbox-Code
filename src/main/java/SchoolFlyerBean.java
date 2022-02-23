import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.io.FilenameUtils;
import org.primefaces.model.file.UploadedFile;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;

@ManagedBean(name="schoolFlyerBean")
@ViewScoped
public class SchoolFlyerBean implements Serializable 
{

	private Date validdate;
	transient
	UploadedFile image;
	private DatabaseMethods1 dbObj = new DatabaseMethods1();
	private String frequency;
	
	public SchoolFlyerBean()
	{
		frequency = "every time";
		validdate=new Date();
		image = null;
	}
	
	
	public String submit()
	{
		Connection conn = DataBaseConnection.javaConnection();
		try 
		{
			boolean extensionChecker=false;
			
			boolean status=dbObj.checkStatusOfThoughtDetails(validdate,dbObj.schoolId(),conn);
			
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
				int i=dbObj.addThoughtDetail(image,validdate,"student",dbObj.schoolId(),frequency,conn);
				if(i>=1)
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Details Added Successfullly"));
				}
				else
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error Occured Try Again !!"));
				}
			}
			else
			{
				int i=dbObj.updateThoughtDetail(image,validdate,"student",dbObj.schoolId(),conn);
				if(i>=1)
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Details Updated Successfullly"));
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
		}
		catch (Exception e) 
		{
			// TODO: handle exception
		}
		finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return "addFlyer.xhtml";
	}


	public Date getValiddate() {
		return validdate;
	}
	public void setValiddate(Date validdate) {
		this.validdate = validdate;
	}
	public UploadedFile getImage() {
		return image;
	}
	public void setImage(UploadedFile image) {
		this.image = image;
	}
	public String getFrequency() {
		return frequency;
	}
	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}
	
}
