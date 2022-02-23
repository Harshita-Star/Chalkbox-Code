package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.PrimeFaces;

@ManagedBean(name="backUp")
@ViewScoped
public class BackUpBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	String selected;
	boolean showBackup;
	boolean show;
	ArrayList<BackUp> dataList;
	BackUp selectedRow;
	String fileName;

	public BackUpBean(){
		Connection conn = DataBaseConnection.javaConnection();
		dataList=new DataBaseMethodsBackUpModule().showFileName(conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void backUpRestore(){
		if(selected.equals("backup")){
			showBackup=true;
			show=false;
		}
		else if(selected.equals("restore")){
			showBackup=false;
			show=true;
		}
	}

	public void createBackUp(){
		Connection conn = DataBaseConnection.javaConnection();
		boolean b=DataBaseMethodsBackUpModule.backUp(conn);
		if(b==true){
			FacesContext fc=FacesContext.getCurrentInstance();
			fc.addMessage(null, new FacesMessage("Backup created successfully"));
			selected=null;
			showBackup=false;
			dataList=new DataBaseMethodsBackUpModule().showFileName(conn);

		}
		else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"An error occurred, try again", "Validation error"));
			selected=null;
			showBackup=false;
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		PrimeFaces context = PrimeFaces.current();
		context.executeInitScript("PF('dlg2').hide();");
	}

	public void restore(){
		//Connection conn = DataBaseConnection.javaConnection();
		if(selectedRow!=null){
			fileName=selectedRow.getFileName();
			boolean b=DataBaseMethodsBackUpModule.restoreDB(fileName);
			if(b==true){
				FacesContext fc=FacesContext.getCurrentInstance();
				fc.addMessage(null, new FacesMessage("Database restore successfully"));
				selected=null;
				show=false;
				selectedRow=null;
			}

			else {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"File not found", "Validation error"));
				selected=null;
				show=false;
				selectedRow=null;
				showBackup=false;
			}
		}
		PrimeFaces context = PrimeFaces.current();
		context.executeInitScript("PF('dlg2').hide();");

	}

	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public BackUp getSelectedRow() {
		return selectedRow;
	}
	public void setSelectedRow(BackUp selectedRow) {
		this.selectedRow = selectedRow;
	}
	public ArrayList<BackUp> getDataList() {
		return dataList;
	}
	public void setDataList(ArrayList<BackUp> dataList) {
		this.dataList = dataList;
	}
	public boolean isShowBackup() {
		return showBackup;
	}
	public void setShowBackup(boolean showBackup) {
		this.showBackup = showBackup;
	}
	public boolean isShow() {
		return show;
	}
	public void setShow(boolean show) {
		this.show = show;
	}
	public String getSelected() {
		return selected;
	}
	public void setSelected(String selected) {
		this.selected = selected;
	}
}
