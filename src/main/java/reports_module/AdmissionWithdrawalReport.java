package reports_module;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFPicture;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import Json.DataBaseMeathodJson;
import schooldata.DataBaseConnection;
import schooldata.DataBaseMethodStudent;
import schooldata.DatabaseMethods1;
import schooldata.SchoolInfoList;
import schooldata.StudentInfo;
import session_work.QueryConstants;
import tc_module.DateToWords;

@ManagedBean(name="admissionWithdrawalReport")
@ViewScoped

public class AdmissionWithdrawalReport implements Serializable
{
	ArrayList<StudentInfo> withdrawList =new ArrayList<>();

	DatabaseMethods1 obj=new DatabaseMethods1();
	SchoolInfoList ls = new SchoolInfoList();
	String selectedCLassSection,selectedSection,schoolId,sessionValue;
	ArrayList<SelectItem> sectionList,classSection;
	SimpleDateFormat sdf= new SimpleDateFormat("dd/MM/yyyy");
	DateToWords dtw = new DateToWords();
	Boolean showExcelExport = false;
	SchoolInfoList schoolDetails;

	public AdmissionWithdrawalReport()
	{
		Connection conn = DataBaseConnection.javaConnection();
		schoolId = obj.schoolId();
		sessionValue = obj.selectedSessionDetails(schoolId,conn);
		schoolDetails =new DatabaseMethods1().fullSchoolInfo(conn);

		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		String username=(String) ss.getAttribute("username");
		String userType=(String) ss.getAttribute("type");

		selectedCLassSection = "";
		selectedSection = "";
		
		if(userType.equalsIgnoreCase("admin")
				|| userType.equalsIgnoreCase("authority")
				|| userType.equalsIgnoreCase("principal")
				|| userType.equalsIgnoreCase("vice principal")
				|| userType.equalsIgnoreCase("front office") || userType.equalsIgnoreCase("office staff")
				|| userType.equalsIgnoreCase("Accounts"))
		{
			classSection = new ArrayList<SelectItem>();
			SelectItem si = new SelectItem();
			si.setLabel("All");
			si.setValue("-1");
			classSection.add(si);

			ArrayList<SelectItem> temp =new DatabaseMethods1().allClass(conn);

			if(temp.size()>0)
			{
				classSection.addAll(temp);
			}
			
			selectedCLassSection = "-1";
			selectedSection = "-1";
			
			reportConst(conn);
		}
		else if (userType.equalsIgnoreCase("academic coordinator") 
					|| userType.equalsIgnoreCase("Administrative Officer"))
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schoolId,conn);
			classSection = obj.cordinatorClassList(empid, schoolId, conn);
		}
		
		ls=obj.fullSchoolInfo(conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void reportConst(Connection conn)
	{
		ArrayList<String> list=new DataBaseMethodStudent().basicFieldsForStudentListForPromotion();
		
		withdrawList=new DataBaseMethodStudent().studentDetail("", selectedSection, selectedCLassSection, QueryConstants.BY_CLASS_SECTION, QueryConstants.CLASS_STRENGTH, null, null, "",QueryConstants.KEYWORD,"","",sessionValue, schoolId, list, conn);

		showExcelExport = false;
		if(withdrawList.size()>0)
		{
			showExcelExport = true;
		}

		for(StudentInfo wt: withdrawList)
		{
			if(wt.getAadharNo()== null) {
				wt.setStudentDetails(wt.getFname());
			}
			else if(wt.getAadharNo().equalsIgnoreCase(""))
			{
				wt.setStudentDetails(wt.getFname());
			}
			else
			{
				wt.setStudentDetails(wt.getFname()+" / "+wt.getAadharNo());
			}
			
			
			if(wt.getMotherOccupation()==null ||  wt.getMotherOccupation().equalsIgnoreCase(""))
			{
				wt.setMotherDetails(wt.getMotherName());
			}
			else if(wt.getMotherName().equalsIgnoreCase(""))
			{
				wt.setMotherDetails(wt.getMotherOccupation());
			}
			else
			{
				wt.setMotherDetails(wt.getMotherName()+" / "+wt.getMotherOccupation());
			}
			
			
			if(wt.getFathersOccupation()==null || wt.getFathersOccupation().equalsIgnoreCase(""))
			{
				wt.setFatherDetails(wt.getFathersName());
			}
			else if(wt.getFathersName().equalsIgnoreCase(""))
			{
				wt.setMotherDetails(wt.getFathersOccupation());
			}
			else
			{
				wt.setFatherDetails(wt.getFathersName()+" / "+wt.getFathersOccupation());
			}
			
			
			
//			if(wt.getFatherAadhaar().equalsIgnoreCase("")&&wt.getFatherDesignation().equalsIgnoreCase(""))
//			{
//				wt.setFatherDetails(wt.getFathersName());
//			}
//			else if((!wt.getFatherAadhaar().equalsIgnoreCase(""))&&wt.getFatherDesignation().equalsIgnoreCase(""))
//			{
//				wt.setFatherDetails(wt.getFathersName()+" / "+wt.getFatherAadhaar());
//			}
//			else if((wt.getFatherAadhaar().equalsIgnoreCase(""))&&(!wt.getFatherDesignation().equalsIgnoreCase("")))
//			{
//				wt.setFatherDetails(wt.getFathersName()+" / "+wt.getFatherDesignation());
//			}
//			else
//			{
//				wt.setFatherDetails(wt.getFathersName()+" / "+wt.getFatherDesignation()+" / "+wt.getFatherAadhaar() );
//			}
			
			if(!wt.getDobString().equalsIgnoreCase(""))
			{
				wt.setDobString(wt.getDobString()+" - "+dtw.DateToWordsConvert(sdf.format(wt.getDob())));
			}
			
			
			if((wt.getAddress()==null || wt.getAddress().equalsIgnoreCase("")) && (wt.getOccupation()==null || wt.getOccupation().equalsIgnoreCase("")))
			{
				wt.setGuardianDetails(wt.getGname());
			}
			else if((wt.getAddress()!=null && !wt.getAddress().equalsIgnoreCase(""))&&(wt.getOccupation()==null ||wt.getOccupation().equalsIgnoreCase("")))
			{
				wt.setGuardianDetails(wt.getGname()+" / "+wt.getAddress());
			}
			else if((wt.getAddress()==null ||  wt.getAddress().equalsIgnoreCase(""))&&((wt.getOccupation()!=null && !wt.getOccupation().equalsIgnoreCase(""))))
			{
				wt.setGuardianDetails(wt.getGname()+" / "+wt.getOccupation());
			}
			else
			{
				wt.setGuardianDetails(wt.getGname()+" / "+wt.getAddress()+" / "+wt.getOccupation());
			}
			
			
		}
	}
	
	public void allSections()
	{
		Connection conn = DataBaseConnection.javaConnection();
		sectionList=obj.allSection(selectedCLassSection,conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	
	public void toNum(Object doc)
    {
        XSSFWorkbook book = (XSSFWorkbook)doc;
        XSSFSheet sheet = book.getSheetAt(0);
       
        
        sheet.createFreezePane(0, 1);
         
            
        XSSFRow he = sheet.getRow(0);
        he.setHeight((short)1550);

          
        try
        {
       
          URL url = new URL(schoolDetails.getDownloadpath()+schoolDetails.getMarksheetHeader());
          InputStream my_banner_image = new BufferedInputStream(url.openStream());
          byte[] bytes = IOUtils.toByteArray(my_banner_image);
          XSSFDrawing drawing = sheet.createDrawingPatriarch();
               
               ClientAnchor my_anchor = new XSSFClientAnchor();
               my_anchor.setRow1(0);
               my_anchor.setRow2(1);
               my_anchor.setCol1(1);
               my_anchor.setCol2(7);
               int my_picture_id = book.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
            //   cellll.setCellValue(my_picture_id);
                   my_banner_image.close();                
             
                  XSSFPicture  my_picture = drawing.createPicture(my_anchor, my_picture_id);
   
          } catch (IOException ioex) {
                     
                  }

        }
	
	
	public void searchStudentByClassSection()
	{
		Connection conn = DataBaseConnection.javaConnection();

		withdrawList = new ArrayList<StudentInfo>();
		
		withdrawList=obj.searchStudentListByClassSection(selectedCLassSection,selectedSection,conn);
		showExcelExport = false;
		if(withdrawList.size()>0)
		{
			showExcelExport = true;
		}
		for(StudentInfo wt: withdrawList)
		{
			if(wt.getAadharNo().equalsIgnoreCase(""))
			{
				wt.setStudentDetails(wt.getFname());
			}
			else
			{
				wt.setStudentDetails(wt.getFname()+" / "+wt.getAadharNo());
			}
			
			
			if(wt.getMotherAadhaar().equalsIgnoreCase(""))
			{
				wt.setMotherDetails(wt.getMotherName());
			}
			else
			{
				wt.setMotherDetails(wt.getMotherName()+" / "+wt.getMotherAadhaar());
			}
			
			
			
			if(wt.getFatherAadhaar().equalsIgnoreCase("")&&wt.getFatherDesignation().equalsIgnoreCase(""))
			{
				wt.setFatherDetails(wt.getFathersName());
			}
			else if((!wt.getFatherAadhaar().equalsIgnoreCase(""))&&wt.getFatherDesignation().equalsIgnoreCase(""))
			{
				wt.setFatherDetails(wt.getFathersName()+" / "+wt.getFatherAadhaar());
			}
			else if((wt.getFatherAadhaar().equalsIgnoreCase(""))&&(!wt.getFatherDesignation().equalsIgnoreCase("")))
			{
				wt.setFatherDetails(wt.getFathersName()+" / "+wt.getFatherDesignation());
			}
			else
			{
				wt.setFatherDetails(wt.getFathersName()+" / "+wt.getFatherDesignation()+" / "+wt.getFatherAadhaar() );
			}
			
			
			if(wt.getAddress().equalsIgnoreCase("")&&wt.getOccupation().equalsIgnoreCase(""))
			{
				wt.setGuardianDetails(wt.getGname());
			}
			else if((!wt.getAddress().equalsIgnoreCase(""))&&wt.getOccupation().equalsIgnoreCase(""))
			{
				wt.setGuardianDetails(wt.getGname()+" / "+wt.getAddress());
			}
			else if((wt.getAddress().equalsIgnoreCase(""))&&(!wt.getOccupation().equalsIgnoreCase("")))
			{
				wt.setGuardianDetails(wt.getGname()+" / "+wt.getOccupation());
			}
			else
			{
				wt.setGuardianDetails(wt.getGname()+" / "+wt.getAddress()+" / "+wt.getOccupation());
			}
			
			
		}
		
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<StudentInfo> getWithdrawList() {
		return withdrawList;
	}

	public void setWithdrawList(ArrayList<StudentInfo> withdrawList) {
		this.withdrawList = withdrawList;
	}



	public SchoolInfoList getLs() {
		return ls;
	}

	public void setLs(SchoolInfoList ls) {
		this.ls = ls;
	}

	public String getSelectedCLassSection() {
		return selectedCLassSection;
	}

	public void setSelectedCLassSection(String selectedCLassSection) {
		this.selectedCLassSection = selectedCLassSection;
	}

	public String getSelectedSection() {
		return selectedSection;
	}

	public void setSelectedSection(String selectedSection) {
		this.selectedSection = selectedSection;
	}

	public ArrayList<SelectItem> getSectionList() {
		return sectionList;
	}

	public void setSectionList(ArrayList<SelectItem> sectionList) {
		this.sectionList = sectionList;
	}

	public ArrayList<SelectItem> getClassSection() {
		return classSection;
	}

	public void setClassSection(ArrayList<SelectItem> classSection) {
		this.classSection = classSection;
	}

	public Boolean getShowExcelExport() {
		return showExcelExport;
	}

	public void setShowExcelExport(Boolean showExcelExport) {
		this.showExcelExport = showExcelExport;
	}
	
	
	
	
}
