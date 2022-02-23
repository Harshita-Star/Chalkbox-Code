import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.model.file.UploadedFile;

import schooldata.AboutUsInfo;
import schooldata.DBMethodsNew;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;

@ManagedBean(name="manageFlyerBean")
@ViewScoped
public class ManageFlyerBean implements Serializable {

	

	private AboutUsInfo selectedRow;
	transient UploadedFile image;
	private List<AboutUsInfo> informationList = new ArrayList<>();
	private DBMethodsNew dbObj = new DBMethodsNew();
	private DatabaseMethods1 dbobjM = new DatabaseMethods1();
	private Date thoughtDate;
	private String frequency;
	
	
	 public ManageFlyerBean()
	 {
		 Connection con = DataBaseConnection.javaConnection();
		 try
		 {
			informationList = dbObj.allFlyerDetails(con);
			if(informationList.size()==0)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No Flyer found."));
			}
		 } 
		 catch (Exception e)
		 {
			// TODO: handle exception
		 }
		 finally 
		 {
			 try {
				con.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
			
		}
		 
	 }
	 
	 public void editImage()
	 {
		 thoughtDate=  selectedRow.getValidaDate();
		 frequency = selectedRow.getFrequency();
	 }
	 
	 
	 public String updateFlyer() 
	 {
		 Connection con = DataBaseConnection.javaConnection();
		 try 
		 {
			if(thoughtDate.equals(selectedRow.getValidaDate()))
			{
				if(dbObj.editFlyer(selectedRow.getId(),thoughtDate,frequency,con))
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Flyer update successfully"));
				}
				else
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error occurred try again. "));
				}
			}
			else
			{
				if(dbobjM.checkStatusOfThoughtDetails(thoughtDate,dbobjM.schoolId(),con))
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Flyer already exist for this date"));
				}
				else
				{
					if(dbObj.editFlyer(selectedRow.getId(),thoughtDate,frequency,con))
					{
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Flyer update successfully"));
					}
					else
					{
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error occurred try again. "));
					}
				}
			}
		 }
		 catch (Exception e) {
			// TODO: handle exception
		}
		 finally {
			try {
				con.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
		 
		 return "manageFlyer.xhtml";
	 }
	 
	 
	 public String deleteFlyer()
	 {
		 Connection con = DataBaseConnection.javaConnection();
		 try 
		 {
			if(dbObj.deleteFlyer(selectedRow.getId(),con))
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Flyer delete successfully"));
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error occurred try again. "));
			}
			 
			 
		 }
		 catch (Exception e) {
			// TODO: handle exception
		}
		 finally {
			try {
				con.close();
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
		 
		 return "manageFlyer.xhtml";
	 }
	 


	public List<AboutUsInfo> getInformationList() {
		return informationList;
	}
	public void setInformationList(List<AboutUsInfo> informationList) {
		this.informationList = informationList;
	}
	public AboutUsInfo getSelectedRow() {
		return selectedRow;
	}
	public void setSelectedRow(AboutUsInfo selectedRow) {
		this.selectedRow = selectedRow;
	}
	public UploadedFile getImage() {
		return image;
	}
	public void setImage(UploadedFile image) {
		this.image = image;
	}
	public Date getThoughtDate() {
		return thoughtDate;
	}
	public void setThoughtDate(Date thoughtDate) {
		this.thoughtDate = thoughtDate;
	}

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}


}