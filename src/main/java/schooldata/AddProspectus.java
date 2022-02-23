package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.model.file.UploadedFile;
@ManagedBean(name="addProspectus")
@ViewScoped
public class AddProspectus implements Serializable
{
	transient
	UploadedFile image;
	boolean status=false;
	public void submit()
	{
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM = new DatabaseMethods1();
		status=DBM.checkStatusOfDetails(conn);
		if(status==false)
		{
			int i=DBM.addProspectusDetail(image,conn);
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
			int i=DBM.updateProspectusDetail(image,conn);
			if(i>=1)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Details Updated Successfullly"));
			}
			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error Occured Try Again !!"));
			}
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	public UploadedFile getImage() {
		return image;
	}
	public void setImage(UploadedFile image) {
		this.image = image;
	}
	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}

}
