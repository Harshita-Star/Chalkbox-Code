package library_module;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;

@ManagedBean(name="editBookSettings")
@ViewScoped
public class EditBookSettingBean implements Serializable {

	double lateFees;
	DatabaseMethods1 obj=new DatabaseMethods1();
	DataBaseMethodsLibraryModule dbl = new DataBaseMethodsLibraryModule();

	public void add()
	{
		Connection conn=DataBaseConnection.javaConnection();
		int addsettings=dbl.editbooksettings(lateFees,conn);

		if(addsettings>=1)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Late Fees successfully added"));
			lateFees=0;
		}


		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void booksettings() {
		{
			FacesContext fc = FacesContext.getCurrentInstance();
			ExternalContext ec = fc.getExternalContext();
			try {
				ec.redirect("editBookSettings.xhtml");
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		}
	}

	public double getLateFees() {
		return lateFees;
	}

	public void setLateFees(double lateFees) {
		this.lateFees = lateFees;
	}

}


