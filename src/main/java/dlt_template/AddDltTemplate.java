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

@ManagedBean(name="addDltTemp")
@ViewScoped
public class AddDltTemplate implements Serializable{
	
	String keyword="-1",type,language,content,addKeyword,keywordAdd,schType="-1";
	ArrayList<String> selectedSchools = new ArrayList<>();
	ArrayList<SelectItem> allSchools = new ArrayList<>();
	String selectedSchool ;
	boolean showNonCheck=false,showCheck=false;
	boolean showschooltag = false;
	ArrayList<SelectItem> allKeywords  = new ArrayList<>();
 	
	public AddDltTemplate() {
		Connection conn = DataBaseConnection.javaConnection();
		DltDatabaseMethod dltDb = new DltDatabaseMethod();
		allKeywords = dltDb.getAllKeywords(conn);
		
		
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void addKeywordMethod() {
		
		int count = 0;
		for(SelectItem a : allKeywords) {
			if(a.getValue().toString().equalsIgnoreCase(addKeyword)) {
				count = count + 1;
			}
		}
		if(count == 0) {
			keyword = addKeyword.toLowerCase();
			SelectItem sel = new SelectItem();
			sel.setLabel(addKeyword.toUpperCase());
			sel.setValue(addKeyword.toLowerCase());
			allKeywords.add(sel);
		}
		PrimeFaces.current().executeInitScript("PF('addKeyword').hide();");
		
	}
	
	public void renderSchools() {
		Connection conn = DataBaseConnection.javaConnection();
		
		allSchools = new DatabaseMethods1().getAllSchool(conn);
		if(schType.equals("1")){
			showCheck = true;
			showschooltag = true;
		}else {
			showCheck = false;
			showschooltag = false;
		}
		
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	public void saveTemp() {
		Connection conn = DataBaseConnection.javaConnection();
		DltDatabaseMethod dltDb = new DltDatabaseMethod();
	
		if(!keyword.equals("-1")) {
			int status = dltDb.saveTemplate(schType,selectedSchools,keyword,type,language,content,"pending",conn);
			if(status>0) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Template added"));
				keyword="-1";
				type="";
				language="";
				content="";
				addKeyword="";
				keywordAdd="";
				allKeywords = dltDb.getAllKeywords(conn);
			}
		}else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please select any keyword", "Please select any keyword"));
		}
		
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	
	public ArrayList<SelectItem> getAllKeywords() {
		return allKeywords;
	}

	public void setAllKeywords(ArrayList<SelectItem> allKeywords) {
		this.allKeywords = allKeywords;
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

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
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

}
