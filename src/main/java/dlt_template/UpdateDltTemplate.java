package dlt_template;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.primefaces.PrimeFaces;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;

@ManagedBean(name="updateDltStatus")
@ViewScoped

public class UpdateDltTemplate implements Serializable{
	
	String keyword="-1",type,lang,content,addKeyword,keywordAdd,schType="-1";
	ArrayList<String> selectedSchools = new ArrayList<>();
	ArrayList<SelectItem> allSchools = new ArrayList<>();
	String selectedSchool = "-1";
	boolean showNonCheck=false,showCheck=false;
	boolean showschooltag = false;
	ArrayList<SelectItem> allKeywords  = new ArrayList<>();
	
	String status="pending";
	ArrayList<DltTemplateInfo> selectedTemp = new ArrayList<>();
	boolean showTable = false;
	boolean showLable,showInput;
	ArrayList<DltTemplateInfo> allTemps = new ArrayList<>();
	
	String language,selkeyword;
	DltTemplateInfo selectedTemplets = new DltTemplateInfo();
	
	public UpdateDltTemplate() {
		Connection conn = DataBaseConnection.javaConnection();
		allSchools = new DatabaseMethods1().getAllSchool(conn);
		lang = "english";
		chooseLang();
		getAllTemplates();
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	public void chooseLang() {
		Connection conn = DataBaseConnection.javaConnection();
		allKeywords = new DltDatabaseMethod().getKeywordWithLanguage(lang,conn);
		
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getAllTemplates() {
		Connection conn = DataBaseConnection.javaConnection();
		if(status.equalsIgnoreCase("Pending")) {
			showLable = false;
			showInput = true;
		}else {
			showLable = true;
			showInput = false;
		}
		allTemps = new DltDatabaseMethod().allTemplates(lang,keyword,status,selectedSchool,conn);
		
		if(allTemps.size()>0) {
			showTable = true;
		}
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void getTemps(String val) {
		
		status = val;
		
		if(status.equalsIgnoreCase("Pending")) {
			showLable = false;
			showInput = true;
		}else {
			showLable = true;
			showInput = false;
		}
		getAllTemplates();
		
	}
	
	public void updateTemp() {
		Connection conn = DataBaseConnection.javaConnection();

		int out = new DltDatabaseMethod().updateData(selectedTemp,conn);
		
		if(out>0) {
			allTemps = new DltDatabaseMethod().allTemplates(lang,keyword,status,selectedSchool,conn);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Template Updated"));
		}
		
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void editDltTemplateDetails() {
		selkeyword = selectedTemplets.getKeyword().toLowerCase();
		type = selectedTemplets.getType();
		language = selectedTemplets.getLanguage();
		content = selectedTemplets.getContent();
	}
	
	public void update() {
		Connection conn = DataBaseConnection.javaConnection();
		
		
		
		int st = new DltDatabaseMethod().updateTemplate(selectedTemplets.getId(),selkeyword.toUpperCase(),type,language,content,conn);
		if(st>0) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Template Updated"));
			PrimeFaces.current().executeInitScript("PF('editDialog').hide()");
			keyword="";
			type="";
			language="";
			content="";
			allTemps = new DltDatabaseMethod().allTemplates(lang,keyword,status,selectedSchool,conn);
		}
		
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void delete() {
		Connection conn = DataBaseConnection.javaConnection();
		
		int st = new DltDatabaseMethod().deleteTemplate(selectedTemplets.getId(),conn);
		if(st>0) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Template Deleted"));
			PrimeFaces.current().executeInitScript("PF('dlg2').hide()");
			allTemps = new DltDatabaseMethod().allTemplates(lang,keyword,status,selectedSchool,conn);
		}
		
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	public ArrayList<DltTemplateInfo> getAllTemps() {
		return allTemps;
	}

	public void setAllTemps(ArrayList<DltTemplateInfo> allTemps) {
		this.allTemps = allTemps;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public ArrayList<DltTemplateInfo> getSelectedTemp() {
		return selectedTemp;
	}

	public void setSelectedTemp(ArrayList<DltTemplateInfo> selectedTemp) {
		this.selectedTemp = selectedTemp;
	}

	public boolean isShowTable() {
		return showTable;
	}

	public void setShowTable(boolean showTable) {
		this.showTable = showTable;
	}

	public boolean isShowLable() {
		return showLable;
	}

	public void setShowLable(boolean showLable) {
		this.showLable = showLable;
	}

	public boolean isShowInput() {
		return showInput;
	}

	public void setShowInput(boolean showInput) {
		this.showInput = showInput;
	}

	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getAddKeyword() {
		return addKeyword;
	}
	public void setAddKeyword(String addKeyword) {
		this.addKeyword = addKeyword;
	}
	public String getKeywordAdd() {
		return keywordAdd;
	}
	public void setKeywordAdd(String keywordAdd) {
		this.keywordAdd = keywordAdd;
	}
	public String getSchType() {
		return schType;
	}
	public void setSchType(String schType) {
		this.schType = schType;
	}
	public ArrayList<String> getSelectedSchools() {
		return selectedSchools;
	}
	public void setSelectedSchools(ArrayList<String> selectedSchools) {
		this.selectedSchools = selectedSchools;
	}
	public ArrayList<SelectItem> getAllSchools() {
		return allSchools;
	}
	public void setAllSchools(ArrayList<SelectItem> allSchools) {
		this.allSchools = allSchools;
	}
	public String getSelectedSchool() {
		return selectedSchool;
	}
	public void setSelectedSchool(String selectedSchool) {
		this.selectedSchool = selectedSchool;
	}
	public boolean isShowNonCheck() {
		return showNonCheck;
	}
	public void setShowNonCheck(boolean showNonCheck) {
		this.showNonCheck = showNonCheck;
	}
	public boolean isShowCheck() {
		return showCheck;
	}
	public void setShowCheck(boolean showCheck) {
		this.showCheck = showCheck;
	}
	public boolean isShowschooltag() {
		return showschooltag;
	}
	public void setShowschooltag(boolean showschooltag) {
		this.showschooltag = showschooltag;
	}
	public ArrayList<SelectItem> getAllKeywords() {
		return allKeywords;
	}
	public void setAllKeywords(ArrayList<SelectItem> allKeywords) {
		this.allKeywords = allKeywords;
	}


	public String getLanguage() {
		return language;
	}


	public void setLanguage(String language) {
		this.language = language;
	}


	public String getSelkeyword() {
		return selkeyword;
	}


	public void setSelkeyword(String selkeyword) {
		this.selkeyword = selkeyword;
	}


	public DltTemplateInfo getSelectedTemplets() {
		return selectedTemplets;
	}


	public void setSelectedTemplets(DltTemplateInfo selectedTemplets) {
		this.selectedTemplets = selectedTemplets;
	}
	
	
	

}
