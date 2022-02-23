package exam_module;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import org.primefaces.PrimeFaces;

import Json.DataBaseMeathodJson;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethodWorkLog;
import schooldata.DatabaseMethods1;
import schooldata.ExtraFieldInfo;

@ManagedBean (name="examSetting")
@ViewScoped
public class ExamSettingBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	String periodicTest,actualMark,reflectMark,finalPT,roundOff,roundOffPercent="no",roundOffType,extraField,finalMarks="total",mlCase="no",abCase="no",header1,header2,header3,otherField;
	String coscholTerm="each",otherTerm="each",coscholHeader,otherHeader,addHeader,seperate_disci="no",disci_sub="false",disci_term="each",disci_header,termNameCoschol="begin",termNameDisci="begin",termNameOther="begin",selectedClass; 
	ArrayList<SelectItem> ptList,classList,subjectList,manadatorySubList,optionalSubList;
	ArrayList<String> selectedSubject;
	String gradeScaleFormat="vertical",signRank="",mandatorySubject,optionalSubject;
	boolean showType,showData,showExtraTable,showOtherTable,showType1,showType2,showOptionalSubject;
	String testName,extraFieldPlace,extraFormat,rank="total",marksFormat,schid,std_image="true",showHeader="true",additional_sub="false",coschol_sub="true",other_sub="false",marks="no",marks_grade="marks",session,gradeScaleScholastic="false",gradeScaleCoschol="false";
	ArrayList<ExtraFieldInfo> extraFieldList,selExtraFieldList,otherValueList,selOtherValueList,signList,teacherSignList,marksheetDetails;
	DatabaseMethods1 db=new DatabaseMethods1();
	DataBaseMethodsExam objExam=new DataBaseMethodsExam();
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();
	String position,username,userType;
	String msg="";
	String fullExamNameAllow="no";
	
	public ExamSettingBean()
	{
		position = "top";
		Connection conn=DataBaseConnection.javaConnection();
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		username=(String) ss.getAttribute("username");
		userType=(String) ss.getAttribute("type");
		schid=db.schoolId();
		session=db.selectedSessionDetails(schid, conn);
		
		if(userType.equalsIgnoreCase("admin")
				|| userType.equalsIgnoreCase("authority")
				|| userType.equalsIgnoreCase("principal")
				|| userType.equalsIgnoreCase("vice principal")
				|| userType.equalsIgnoreCase("front office")
				|| userType.equalsIgnoreCase("office staff") 
				|| userType.equalsIgnoreCase("Accounts"))
		{
			classList=db.allClass(conn);
		}
		else if(userType.equalsIgnoreCase("academic coordinator") 
				|| userType.equalsIgnoreCase("Administrative Officer"))
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schid,conn);
			classList=db.cordinatorClassList(empid, schid, conn);
		}
		else
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schid,conn);
			classList=db.allClassListForClassTeacher(empid,schid,conn);

		}
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void checkExtraField()
	{
		if(extraField.equals("yes"))
		{
			showExtraTable=true;
		}
		else
		{
			showExtraTable=false;
		}
	}
	
	public void checkOtherField()
	{
		if(otherField.equals("yes"))
		{
			showOtherTable=true;
		}
		else
		{
			showOtherTable=false;
		}
	}
	
	public void editExamSetting()
	{
		FacesContext fc = FacesContext.getCurrentInstance();
		ExternalContext ec = fc.getExternalContext();
		try {
			ec.redirect("editViewExamSetting.xhtml");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void goToBulkEdit()
	{
		FacesContext fc = FacesContext.getCurrentInstance();
		ExternalContext ec = fc.getExternalContext();
		try {
			ec.redirect("editBulkExamSetting.xhtml");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void searchClassData()
	{
		Connection conn=DataBaseConnection.javaConnection();
		
		boolean info=objExam.checkExamSetting(selectedClass,conn);
		if(info==true)
		{
			String className=db.classNameFromidSchid(schid,selectedClass,session,conn);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Settings are Already Added For Class "+className+"  ... Please Go To Edit section for Changes"));
			showData=false;
		}
		else
		{
			showData=true;

			extraFieldList=new ArrayList<>();otherValueList=new ArrayList<>();
			signList=new ArrayList<>();marksheetDetails=new ArrayList<>();selExtraFieldList=new ArrayList<>();selOtherValueList=new ArrayList<>();
			teacherSignList=new ArrayList<>();
			
			objExam.makeExtraFieldList(extraFieldList);
			objExam.makeMarksheetDetails(marksheetDetails);
			objExam.makeSignList(signList,teacherSignList,selectedClass,conn,schid,session);
			objExam.makeOtherValueList(otherValueList);
			
			testName="PT";
			periodicTest="0";
			actualMark="0";
			reflectMark="0";
			finalPT="0";
			subjectList=db.selectedSubjectTypeofClassSectionForExam(selectedClass, "scholastic", conn,schid);
			int mandatory=0,optional=0;
			
			selectedSubject=new ArrayList<>();
			for(SelectItem list:subjectList)
			{
				selectedSubject.add(list.getValue().toString());
				if(list.getDescription().equals("Mandatory"))
					mandatory++;
				else if(list.getDescription().equals("Optional"))
					optional++;
			}
			
			manadatorySubList=new ArrayList<>();
			for(int i=1;i<=mandatory;i++)
			{
				SelectItem item=new SelectItem();
				item.setLabel(String.valueOf(i));
				item.setValue(String.valueOf(i));
				manadatorySubList.add(item);
			}
			
			optionalSubList=new ArrayList<>();
			for(int i=1;i<=optional;i++)
			{
				SelectItem item=new SelectItem();
				item.setLabel(String.valueOf(i));
				item.setValue(String.valueOf(i));
				optionalSubList.add(item);
			}
			if(optionalSubList.size()>0)
				showOptionalSubject=true;
			else
				showOptionalSubject=false;
		
			mandatorySubject=String.valueOf(mandatory);
			optionalSubject=String.valueOf(optional);
			
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
//	public void makeSubjectList()
//	{
//		int mandatory=0,optional=0;
//		
//		for(String subject:selectedSubject)
//		{
//			for(SelectItem list:subjectList)
//			{
//				if(subject.equals(list.getValue()) && list.getDescription().equals("Mandatory"))
//				{
//					mandatory++;
//					break;
//				}
//				else if(subject.equals(list.getValue()) && list.getDescription().equals("Optional"))
//				{
//					optional++;
//					break;
//				}
//			}
//		}
//		
//		manadatorySubList=new ArrayList<>();
//		for(int i=1;i<=mandatory;i++)
//		{
//			SelectItem item=new SelectItem();
//			item.setLabel(String.valueOf(i));
//			item.setValue(String.valueOf(i));
//			manadatorySubList.add(item);
//		}
//		
//		optionalSubList=new ArrayList<>();
//		for(int i=1;i<=optional;i++)
//		{
//			SelectItem item=new SelectItem();
//			item.setLabel(String.valueOf(i));
//			item.setValue(String.valueOf(i));
//			optionalSubList.add(item);
//		}
//		if(optionalSubList.size()>0)
//			showOptionalSubject=true;
//		else
//			showOptionalSubject=false;
//		mandatorySubject=String.valueOf(mandatory);
//		optionalSubject=String.valueOf(optional);
//	}

	public void createPTList()
	{
		ptList=new ArrayList<>();
		for(int i=1;i<=Integer.parseInt(periodicTest);i++)
		{
			SelectItem ll=new SelectItem();
			ll.setLabel(String.valueOf(i));
			ll.setValue(String.valueOf(i));
			ptList.add(ll);
		}
	}

	public void checkRoundOff()
	{
		if(roundOff.equalsIgnoreCase("Yes"))
		{
			showType=true;
			showType1=true;showType2=false;
		}
		else
		{
			showType=false;
			showType2=true;showType1=false;
		}
	}
	
	public void checkSignRank()
	{
		for(ExtraFieldInfo sign:teacherSignList)
		{
			sign.setRollNo(signRank);
		}
	}

	public void addNow()
	{
		Connection conn=DataBaseConnection.javaConnection();
		
		int flag1=0,flag=0,flag2=0;
		String extraValues="",otherValues="",termTotal="",termGrade="",finalTotal="",finalGrade="",finalPercent="",rowTotal="",rowPercent="",rowGrade="",sign1="",sign2="",sign3="",sign4="";
		if(extraField.equals("yes") && selExtraFieldList.size()<=0)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please select atleast one extra value"));
			flag=1;
		}
		
//		int size=0;
//		if(selectedSubject!=null)
//			size=selectedSubject.size();
//		else
//		{
//			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please select atleast one subject"));
//			flag=1;
//		}
//		
//		if(Integer.parseInt(mandatorySubject)+Integer.parseInt(optionalSubject)!=size)
//		{
//			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No. Of Mandatory / optional Subject should be equal to number of subject in marksheet"));
//			flag=1;
//		}
//		
		if(flag==0)
		{
			if(otherField.equals("yes") && selOtherValueList.size()<=0)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please select atleast one other value"));
				flag1=1;
			}
			if(flag1==0)
			{
				for (ExtraFieldInfo info :signList) 
				{
					if ((info.getFile().getSize()>0) &&(info.getExtraValue().trim().equalsIgnoreCase(""))) 
					{
						flag2=1;
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Empty Signature Name at Row "+info.getsNo()));
						break;
					}
				}
				
				for (ExtraFieldInfo info :teacherSignList) 
				{
					if ((info.getFile().getSize()>0) &&(info.getExtraValue().trim().equalsIgnoreCase(""))) 
					{
						flag2=1;
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Empty Signature Name at Row "+info.getsNo()));
						break;
					}
				}
				
				if(flag2==0)
				{
					if(selExtraFieldList!=null && selExtraFieldList.size()>0)
					{
						for(ExtraFieldInfo info:selExtraFieldList)
						{
							extraValues+=info.getExtraValue()+"-"+info.getRollNo()+"-"+info.getAttendance()+",";
						}
					}
					if(extraValues.contains(","))
						extraValues=extraValues.substring(0,extraValues.length()-1);
					
					/*if(selOtherValueList!=null && selOtherValueList.size()>0)
					{
						for(ExtraFieldInfo info:selOtherValueList)
						{
							otherValues+=info.getExtraValue()+"-"+info.getRollNo()+"-"+info.getLabel()+"-"+info.getSignType()+"-"+info.getRemark()+",";
						}
					}
					if(otherValues.contains(","))
						otherValues=otherValues.substring(0,otherValues.length()-1);*/
					
					
					
					if(selOtherValueList!=null && selOtherValueList.size()>0)
					{
						for(ExtraFieldInfo info:selOtherValueList)
						{
							otherValues+=info.getExtraValue()+"-"+info.getRollNo()+"-"+info.getLabel()+"-"+info.getSignType()+"-"+info.getRemark()+"%@%";
						}
					}
					
					if(otherValues.contains("%@%"))
						otherValues=otherValues.substring(0,otherValues.length()-3);

					
					termTotal=marksheetDetails.get(0).getStudentName()+","+marksheetDetails.get(0).getLabel()+","+marksheetDetails.get(0).getClassName();
					termGrade=marksheetDetails.get(1).getStudentName()+","+marksheetDetails.get(1).getLabel()+","+marksheetDetails.get(1).getClassName();
					finalTotal=marksheetDetails.get(2).getStudentName()+","+marksheetDetails.get(2).getLabel()+","+marksheetDetails.get(2).getClassName();
					finalGrade=marksheetDetails.get(3).getStudentName()+","+marksheetDetails.get(3).getLabel()+","+marksheetDetails.get(3).getClassName();

					finalPercent=marksheetDetails.get(4).getStudentName()+","+marksheetDetails.get(4).getLabel()+","+marksheetDetails.get(4).getClassName();
					rowTotal=marksheetDetails.get(5).getStudentName()+","+marksheetDetails.get(5).getLabel()+","+marksheetDetails.get(5).getClassName()+","+marksheetDetails.get(5).getRollNo();

					rowPercent=marksheetDetails.get(6).getStudentName()+","+marksheetDetails.get(6).getLabel()+","+marksheetDetails.get(6).getClassName()+","+marksheetDetails.get(6).getRollNo();
					rowGrade=marksheetDetails.get(7).getStudentName()+","+marksheetDetails.get(7).getLabel()+","+marksheetDetails.get(7).getClassName()+","+marksheetDetails.get(7).getRollNo();
					
					int i=0;
					ArrayList<String> signNameList=new ArrayList<>();
					
					Map<String, String> map=new HashMap<String, String>();
					
					for(ExtraFieldInfo sign:teacherSignList)
					{
						String signImageName="";
						if(!sign.getExtraValue().trim().equals("") && sign.getClassId().equals(selectedClass)) 
						{
							if(sign.getFile()!=null && sign.getLabel().equals("image"))
							{
								
								String ext=sign.getFile().getFileName().substring(sign.getFile().getFileName().lastIndexOf(".")+1);
								signImageName=sign.getExtraValue().trim()+"_"+selectedClass+"_"+sign.getSectionId()+"_"+schid+"_"+session+"."+ext;
								db.makeProfileSchid(schid,sign.getFile(),signImageName,conn);
								map.put(sign.getSectionId(),sign.getRollNo()+"$"+sign.getExtraValue()+"$"+sign.getLabel()+"$"+signImageName+"$"+sign.getAlignment());
							}
							else
							{
								map.put(sign.getSectionId(),sign.getRollNo()+"$"+sign.getExtraValue()+"$"+sign.getLabel()+"$$"+sign.getAlignment());
							}
						}
						else if(sign.getClassId().equals(selectedClass))
						{
							map.put(sign.getSectionId(),"");
						}
					}
					sign1=map.toString();
					
					for(ExtraFieldInfo sign:signList)
					{
						String signImageName="";
						if(!sign.getExtraValue().trim().equals("")) 
						{
							if(sign.getFile()!=null && sign.getLabel().equals("image"))
							{
								String ext=sign.getFile().getFileName().substring(sign.getFile().getFileName().lastIndexOf(".")+1);
								signImageName=sign.getExtraValue().trim()+"_"+selectedClass+"_"+schid+"_"+session+"."+ext;
								db.makeProfileSchid(schid,sign.getFile(),signImageName,conn);
								signNameList.add(sign.getRollNo()+","+sign.getExtraValue()+","+sign.getLabel()+","+signImageName+","+sign.getAlignment());
							}
							else
							{
								signNameList.add(sign.getRollNo()+","+sign.getExtraValue()+","+sign.getLabel()+",,"+sign.getAlignment());
							}
						}
						else
						{
							signNameList.add("");
						}
					}
					sign2=signNameList.get(0);
					sign3=signNameList.get(1);
					sign4=signNameList.get(2);
					
//					String subject=selectedSubject.toString();
//					subject=subject.replace("[","").replace("]","").replace(" ","");
					
					i=objExam.addExamSetting(testName,periodicTest,actualMark,reflectMark,finalPT,header1,header2,header3,finalMarks,
					mlCase,abCase,rank,roundOff,roundOffType,marksFormat,extraField,otherField,extraFieldPlace,extraFormat,extraValues,
					otherValues,termTotal,termGrade,finalTotal,finalGrade,rowTotal,rowPercent,schid,selectedClass,sign1,sign2,sign3,sign4,
					gradeScaleScholastic,std_image,showHeader,coschol_sub,coscholHeader,coscholTerm,termNameCoschol,additional_sub,addHeader,other_sub
					,otherHeader,otherTerm,termNameOther,seperate_disci,disci_sub,disci_term,disci_header,termNameDisci,finalPercent,marks,
					rowGrade,roundOffPercent,conn,marks_grade,session,gradeScaleFormat,mandatorySubject,optionalSubject,gradeScaleCoschol,position,fullExamNameAllow);
					if(i>=1)
					{
						String refNo;
						try {
							 refNo=addWorkLog(conn,extraValues,
									otherValues,termTotal,termGrade,finalTotal,finalGrade,rowTotal,rowPercent,sign1,sign2,sign3,sign4,finalPercent,rowGrade);

						} catch (Exception e) {
							e.printStackTrace();
						}
						msg = "Exam Settings Added Sucessfully";
						//FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Exam Settings Added Sucessfully"));
						
						PrimeFaces.current().executeInitScript("PF('cnfDia').show();");
						roundOff=roundOffPercent=roundOffType=extraField=otherField="";
						gradeScaleScholastic=gradeScaleCoschol="false";
						extraFieldList=selExtraFieldList=otherValueList=selOtherValueList=marksheetDetails=signList=new ArrayList<>();
						selectedClass="";mandatorySubject=optionalSubject="";
						selectedSubject=new ArrayList<>();
						manadatorySubList=optionalSubList=new ArrayList<>();
						subjectList=new ArrayList<>();

						testName="PT";
						periodicTest=actualMark=reflectMark=finalPT="0";
						std_image=showHeader=coschol_sub=additional_sub=other_sub=marks="";gradeScaleFormat="vertical";
						header1=header2=header3=finalMarks=mlCase=abCase=rank=marksFormat=extraFieldPlace=extraFormat="";
						showType=showType1=showType2=showData=false;
					}
					else
					{
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("An Error Occured... Please Try Again"));
					}
				}
			}
		}
				try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	public void refresh() throws IOException {
		msg = "";
		PrimeFaces.current().executeInitScript("PF('cnfDia').hide();");
		FacesContext fc=FacesContext.getCurrentInstance();
		
		fc.getExternalContext().redirect("examSetting.xhtml");
		//PrimeFaces.current().executeInitScript("window.open('examSetting.xhtml')");
	}
	
	public String addWorkLog(Connection conn,String extraValues,
			String otherValues,String termTotal,String termGrade,String finalTotal,String finalGrade,String rowTotal,String rowPercent,String sign1,String sign2,String sign3,String sign4,String finalPercent,String rowGrade)
	{
	    String value = "";
		String language= "";
		
		language += selectedClass; 
		value = testName+" -- "+periodicTest+" -- "+actualMark+" -- "+reflectMark+" -- "+finalPT+" -- "+header1+" -- "+header2+" -- "+header3+" -- "+finalMarks+" -- "+
				mlCase+" -- "+abCase+" -- "+rank+" -- "+roundOff+" -- "+roundOffType+" -- "+marksFormat+" -- "+extraField+" -- "+otherField+" -- "+extraFieldPlace+" -- "+extraFormat+" -- "+extraValues+" -- "+
				otherValues+" -- "+termTotal+" -- "+termGrade+" -- "+finalTotal+" -- "+finalGrade+" -- "+rowTotal+" -- "+rowPercent+" -- "+sign1+" -- "+sign2+" -- "+sign3+" -- "+sign4+" -- "+
				gradeScaleScholastic+" -- "+std_image+" -- "+showHeader+" -- "+coschol_sub+" -- "+coscholHeader+" -- "+coscholTerm+" -- "+termNameCoschol+" -- "+additional_sub+" -- "+addHeader+" -- "+other_sub+" -- "+
				otherHeader+" -- "+otherTerm+" -- "+termNameOther+" -- "+seperate_disci+" -- "+disci_sub+" -- "+disci_term+" -- "+disci_header+" -- "+termNameDisci+" -- "+finalPercent+" -- "+marks+" -- "+
				rowGrade+" -- "+roundOffPercent+" -- "+marks_grade+" -- "+gradeScaleCoschol;
	
		String refNo = workLg.saveWorkLogMehod(language,"Exam Setting","WEB",value,conn);
		return refNo;
	}

	
	public void closeDialog()
	{
		PrimeFaces.current().executeInitScript("PF('upDlg').hide()");
		
	}
	
	public String getPeriodicTest() {
		return periodicTest;
	}
	public void setPeriodicTest(String periodicTest) {
		this.periodicTest = periodicTest;
	}
	public String getActualMark() {
		return actualMark;
	}
	public void setActualMark(String actualMark) {
		this.actualMark = actualMark;
	}
	public String getReflectMark() {
		return reflectMark;
	}
	public void setReflectMark(String reflectMark) {
		this.reflectMark = reflectMark;
	}
	public String getFinalPT() {
		return finalPT;
	}
	public void setFinalPT(String finalPT) {
		this.finalPT = finalPT;
	}
	public String getRoundOff() {
		return roundOff;
	}
	public void setRoundOff(String roundOff) {
		this.roundOff = roundOff;
	}
	public String getRoundOffType() {
		return roundOffType;
	}
	public void setRoundOffType(String roundOffType) {
		this.roundOffType = roundOffType;
	}
	public ArrayList<SelectItem> getPtList() {
		return ptList;
	}
	public void setPtList(ArrayList<SelectItem> ptList) {
		this.ptList = ptList;
	}
	public boolean isShowType() {
		return showType;
	}
	public void setShowType(boolean showType) {
		this.showType = showType;
	}
	public ArrayList<SelectItem> getClassList() {
		return classList;
	}
	public String getExtraField() {
		return extraField;
	}

	public void setExtraField(String extraField) {
		this.extraField = extraField;
	}

	public boolean isShowExtraTable() {
		return showExtraTable;
	}

	public void setShowExtraTable(boolean showExtraTable) {
		this.showExtraTable = showExtraTable;
	}

	public void setClassList(ArrayList<SelectItem> classList) {
		this.classList = classList;
	}
	public boolean isShowData() {
		return showData;
	}
	public void setShowData(boolean showData) {
		this.showData = showData;
	}
	public String getTestName() {
		return testName;
	}
	public void setTestName(String testName) {
		this.testName = testName;
	}
	public String getFinalMarks() {
		return finalMarks;
	}
	public void setFinalMarks(String finalMarks) {
		this.finalMarks = finalMarks;
	}

	public String getSelectedClass() {
		return selectedClass;
	}
	public void setSelectedClass(String selectedClass) {
		this.selectedClass = selectedClass;
	}
	public ArrayList<SelectItem> getSubjectList() {
		return subjectList;
	}
	public void setSubjectList(ArrayList<SelectItem> subjectList) {
		this.subjectList = subjectList;
	}
	public ArrayList<String> getSelectedSubject() {
		return selectedSubject;
	}
	public void setSelectedSubject(ArrayList<String> selectedSubject) {
		this.selectedSubject = selectedSubject;
	}
	public String getMlCase() {
		return mlCase;
	}

	public void setMlCase(String mlCase) {
		this.mlCase = mlCase;
	}

	public String getAbCase() {
		return abCase;
	}

	public void setAbCase(String abCase) {
		this.abCase = abCase;
	}

	public String getHeader1() {
		return header1;
	}

	public void setHeader1(String header1) {
		this.header1 = header1;
	}

	public String getHeader2() {
		return header2;
	}

	public void setHeader2(String header2) {
		this.header2 = header2;
	}

	public String getHeader3() {
		return header3;
	}

	public void setHeader3(String header3) {
		this.header3 = header3;
	}

	public ArrayList<ExtraFieldInfo> getExtraFieldList() {
		return extraFieldList;
	}

	public void setExtraFieldList(ArrayList<ExtraFieldInfo> extraFieldList) {
		this.extraFieldList = extraFieldList;
	}

	public String getExtraFieldPlace() {
		return extraFieldPlace;
	}

	public void setExtraFieldPlace(String extraFieldPlace) {
		this.extraFieldPlace = extraFieldPlace;
	}

	public ArrayList<ExtraFieldInfo> getSelExtraFieldList() {
		return selExtraFieldList;
	}

	public void setSelExtraFieldList(ArrayList<ExtraFieldInfo> selExtraFieldList) {
		this.selExtraFieldList = selExtraFieldList;
	}
	public ArrayList<ExtraFieldInfo> getOtherValueList() {
		return otherValueList;
	}
	public String getOtherField() {
		return otherField;
	}


	public void setOtherField(String otherField) {
		this.otherField = otherField;
	}


	public void setOtherValueList(ArrayList<ExtraFieldInfo> otherValueList) {
		this.otherValueList = otherValueList;
	}


	public ArrayList<ExtraFieldInfo> getSelOtherValueList() {
		return selOtherValueList;
	}


	public void setSelOtherValueList(ArrayList<ExtraFieldInfo> selOtherValueList) {
		this.selOtherValueList = selOtherValueList;
	}


	public boolean isShowOtherTable() {
		return showOtherTable;
	}


	public void setShowOtherTable(boolean showOtherTable) {
		this.showOtherTable = showOtherTable;
	}


	public String getExtraFormat() {
		return extraFormat;
	}


	public void setExtraFormat(String extraFormat) {
		this.extraFormat = extraFormat;
	}

	public String getRank() {
		return rank;
	}


	public void setRank(String rank) {
		this.rank = rank;
	}


	public boolean isShowType1() {
		return showType1;
	}


	public void setShowType1(boolean showType1) {
		this.showType1 = showType1;
	}


	public boolean isShowType2() {
		return showType2;
	}


	public void setShowType2(boolean showType2) {
		this.showType2 = showType2;
	}


	public String getMarksFormat() {
		return marksFormat;
	}


	public void setMarksFormat(String marksFormat) {
		this.marksFormat = marksFormat;
	}


	public ArrayList<ExtraFieldInfo> getSignList() {
		return signList;
	}


	public void setSignList(ArrayList<ExtraFieldInfo> signList) {
		this.signList = signList;
	}


	public ArrayList<ExtraFieldInfo> getMarksheetDetails() {
		return marksheetDetails;
	}


	public void setMarksheetDetails(ArrayList<ExtraFieldInfo> marksheetDetails) {
		this.marksheetDetails = marksheetDetails;
	}
	public String getGradeScaleScholastic() {
		return gradeScaleScholastic;
	}
	public void setGradeScaleScholastic(String gradeScaleScholastic) {
		this.gradeScaleScholastic = gradeScaleScholastic;
	}
	public String getGradeScaleCoschol() {
		return gradeScaleCoschol;
	}
	public void setGradeScaleCoschol(String gradeScaleCoschol) {
		this.gradeScaleCoschol = gradeScaleCoschol;
	}
	public String getStd_image() {
		return std_image;
	}
	public void setStd_image(String std_image) {
		this.std_image = std_image;
	}
	public String getShowHeader() {
		return showHeader;
	}
	public void setShowHeader(String showHeader) {
		this.showHeader = showHeader;
	}
	public String getAdditional_sub() {
		return additional_sub;
	}
	public void setAdditional_sub(String additional_sub) {
		this.additional_sub = additional_sub;
	}
	public String getCoschol_sub() {
		return coschol_sub;
	}
	public void setCoschol_sub(String coschol_sub) {
		this.coschol_sub = coschol_sub;
	}
	public String getOther_sub() {
		return other_sub;
	}
	public void setOther_sub(String other_sub) {
		this.other_sub = other_sub;
	}
	public String getRoundOffPercent() {
		return roundOffPercent;
	}
	public void setRoundOffPercent(String roundOffPercent) {
		this.roundOffPercent = roundOffPercent;
	}
	public String getMarks() {
		return marks;
	}
	public void setMarks(String marks) {
		this.marks = marks;
	}
	public String getCoscholTerm() {
		return coscholTerm;
	}
	public void setCoscholTerm(String coscholTerm) {
		this.coscholTerm = coscholTerm;
	}
	public String getOtherTerm() {
		return otherTerm;
	}
	public void setOtherTerm(String otherTerm) {
		this.otherTerm = otherTerm;
	}
	
	public String getTermNameCoschol() {
		return termNameCoschol;
	}
	public void setTermNameCoschol(String termNameCoschol) {
		this.termNameCoschol = termNameCoschol;
	}
	public String getTermNameDisci() {
		return termNameDisci;
	}
	public void setTermNameDisci(String termNameDisci) {
		this.termNameDisci = termNameDisci;
	}
	public String getTermNameOther() {
		return termNameOther;
	}
	public void setTermNameOther(String termNameOther) {
		this.termNameOther = termNameOther;
	}
	public String getCoscholHeader() {
		return coscholHeader;
	}
	public void setCoscholHeader(String coscholHeader) {
		this.coscholHeader = coscholHeader;
	}
	public String getOtherHeader() {
		return otherHeader;
	}
	public void setOtherHeader(String otherHeader) {
		this.otherHeader = otherHeader;
	}
	public String getAddHeader() {
		return addHeader;
	}
	public void setAddHeader(String addHeader) {
		this.addHeader = addHeader;
	}
	public String getSeperate_disci() {
		return seperate_disci;
	}
	public void setSeperate_disci(String seperate_disci) {
		this.seperate_disci = seperate_disci;
	}
	public String getDisci_sub() {
		return disci_sub;
	}
	public void setDisci_sub(String disci_sub) {
		this.disci_sub = disci_sub;
	}
	public String getDisci_term() {
		return disci_term;
	}
	public void setDisci_term(String disci_term) {
		this.disci_term = disci_term;
	}
	public String getDisci_header() {
		return disci_header;
	}
	public void setDisci_header(String disci_header) {
		this.disci_header = disci_header;
	}
	public String getMarks_grade() {
		return marks_grade;
	}
	public void setMarks_grade(String marks_grade) {
		this.marks_grade = marks_grade;
	}
	public String getGradeScaleFormat() {
		return gradeScaleFormat;
	}
	public void setGradeScaleFormat(String gradeScaleFormat) {
		this.gradeScaleFormat = gradeScaleFormat;
	}
	public ArrayList<ExtraFieldInfo> getTeacherSignList() {
		return teacherSignList;
	}
	public void setTeacherSignList(ArrayList<ExtraFieldInfo> teacherSignList) {
		this.teacherSignList = teacherSignList;
	}
	public String getSignRank() {
		return signRank;
	}
	public void setSignRank(String signRank) {
		this.signRank = signRank;
	}
	public ArrayList<SelectItem> getManadatorySubList() {
		return manadatorySubList;
	}
	public void setManadatorySubList(ArrayList<SelectItem> manadatorySubList) {
		this.manadatorySubList = manadatorySubList;
	}
	public ArrayList<SelectItem> getOptionalSubList() {
		return optionalSubList;
	}
	public void setOptionalSubList(ArrayList<SelectItem> optionalSubList) {
		this.optionalSubList = optionalSubList;
	}
	public String getMandatorySubject() {
		return mandatorySubject;
	}
	public void setMandatorySubject(String mandatorySubject) {
		this.mandatorySubject = mandatorySubject;
	}
	public String getOptionalSubject() {
		return optionalSubject;
	}
	public void setOptionalSubject(String optionalSubject) {
		this.optionalSubject = optionalSubject;
	}
	public boolean isShowOptionalSubject() {
		return showOptionalSubject;
	}
	public void setShowOptionalSubject(boolean showOptionalSubject) {
		this.showOptionalSubject = showOptionalSubject;
	}
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getFullExamNameAllow() {
		return fullExamNameAllow;
	}
	public void setFullExamNameAllow(String fullExamNameAllow) {
		this.fullExamNameAllow = fullExamNameAllow;
	}
	public String getSchid() {
		return schid;
	}
	public void setSchid(String schid) {
		this.schid = schid;
	}
	
	
	
	
}
