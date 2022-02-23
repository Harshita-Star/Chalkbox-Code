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

import schooldata.ClassInfo;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethodWorkLog;
import schooldata.DatabaseMethods1;
import schooldata.ExamSettingInfo;
import schooldata.ExtraFieldInfo;
import schooldata.SchoolInfoList;

@ManagedBean (name="editExamSetting")
@ViewScoped
public class EditExamSettingBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	String periodicTest,actualMark,reflectMark,finalPT,roundOff,roundOffPercent,roundOffType,extraField,finalMarks,mlCase,abCase,header1,header2,header3,otherField;
	ArrayList<SelectItem> ptList,subjectList,manadatorySubList,optionalSubList;
	ArrayList<String> selectedSubject;
	
	boolean showType,showData,showExtraTable,showOtherTable,showType1,showType2,showOptionalSubject;
	String testName,extraFieldPlace,extraFormat,rank,marksFormat,schid,marks,gradeScaleFormat,signRank,mandatorySubject,optionalSubject;
	ArrayList<ExtraFieldInfo> extraFieldList,selExtraFieldList,otherValueList,selOtherValueList,signList,teacherSignList,marksheetDetails;
	DatabaseMethods1 db=new DatabaseMethods1();
	ExamSettingInfo selectedExam;
	String idEdit,classId,className,marks_grade;
	String gradeScaleList,gradeScaleListCoschol,std_image,showHeader,additional_sub,coschol_sub,other_sub;
	String coscholTerm,otherTerm,coscholHeader,otherHeader,addHeader,seperate_disci,disci_sub,disci_term,disci_header,termNameCoschol,termNameDisci,termNameOther; 
    ExtraFieldInfo selectedImage = new ExtraFieldInfo();
	String si1,si2,si3,si4,session;
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();
	DataBaseMethodsExam objExam=new DataBaseMethodsExam();
	String position;
	String fullExamNameAllow="no";
	
	public EditExamSettingBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		
		schid=db.schoolId();
		session=db.selectedSessionDetails(schid, conn);
		
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		selectedExam = (ExamSettingInfo) ss.getAttribute("selectedExamm");
		
		
		extraFieldList=new ArrayList<>();otherValueList=new ArrayList<>();
		signList=new ArrayList<>();teacherSignList=new ArrayList<>();marksheetDetails=new ArrayList<>();selExtraFieldList=new ArrayList<>();selOtherValueList=new ArrayList<>();
		
		periodicTest=selectedExam.getNo_of_PT();
		testName=selectedExam.getExamName();
		className = selectedExam.getClassName();
		classId=selectedExam.getClassId();
		idEdit = selectedExam.getId();
		
//		subjectList=db.allSubjectListBySubjectType(classId, "scholastic", conn);
//		for(SelectItem subject:subjectList)
//		{
//			selectedSubject.add(String.valueOf(subject.getValue()));
//		}
		
		header1 = selectedExam.getHeader1();
		header2 = selectedExam.getHeader2();
		header3 = selectedExam.getHeader3();
		
		mlCase = selectedExam.getMaxMarkML();
		abCase = selectedExam.getMaxMarkAB();
		rank = selectedExam.getRank_base();
		marksFormat = selectedExam.getMarksformat();
		marks_grade = selectedExam.getMarks_grade(); 
		gradeScaleList = String.valueOf(selectedExam.isShowGradeScale());
		gradeScaleListCoschol = String.valueOf(selectedExam.isShowGradeScaleCoschol());
		gradeScaleFormat=selectedExam.getGradeScaleFormat();
		extraField = selectedExam.getExtraField();
		extraFieldPlace = selectedExam.getExtraFieldPlace();
		extraFormat = selectedExam.getExtraFormat();
		otherField = selectedExam.getOtherField();
		marks = selectedExam.getExamMarks();
		roundOffPercent = selectedExam.getRound_off_percent();
		std_image =String.valueOf(selectedExam.isStd_image());
		showHeader = String.valueOf(selectedExam.isSchool_header());
		additional_sub = String.valueOf(selectedExam.isAdditional_sub());
		other_sub = String.valueOf(selectedExam.isOther_sub());
		coschol_sub = String.valueOf(selectedExam.isCoschol_sub());
		
		coscholTerm = selectedExam.getCoscholTerm();
		coscholHeader = selectedExam.getCoscholHeader();
		termNameCoschol = selectedExam.getTermNameCoschol();
		addHeader = selectedExam.getAddHeader();
		otherTerm = selectedExam.getOtherTerm();
		otherHeader = selectedExam.getOtherHeader();
		termNameOther = selectedExam.getTermNameOther();
		seperate_disci = selectedExam.getSepearte_disci();
		disci_sub = String.valueOf(selectedExam.isDisci_sub());
		disci_header = selectedExam.getDisci_header();
		disci_term = selectedExam.getDisci_term();
		termNameDisci = selectedExam.getTermNameDisci();
		position = selectedExam.getPosition();
		
		mandatorySubject=selectedExam.getMandatorySub();
		optionalSubject=selectedExam.getOptionalSub();
		fullExamNameAllow = selectedExam.getFullExamNameAllow();
		
		makeExtraFieldList(extraFieldList);
		makeOtherValueList(otherValueList);
		
		
		createPTList();
		actualMark=selectedExam.getActualMark();
		reflectMark=selectedExam.getReflectMark();
		finalPT=selectedExam.getInclude_PT();
		roundOff=selectedExam.getRound_off();
		checkRoundOffStart();
		roundOffType=selectedExam.getRound_off_type();
		finalMarks=selectedExam.getFinalMarks();
		
		String trmTo = selectedExam.getTermTotal();
		String trmGrd = selectedExam.getTermGrade();
		String finTo = selectedExam.getFinalTotal();
		String finGrd =selectedExam.getFinalGrade();
		String rowTo =selectedExam.getRowTotal();
		String rowPer =selectedExam.getRowPercentName();
		String finalPercennt = selectedExam.getFinalPercent();
		String rowGadee = selectedExam.getRowGrade();
		makeMarksheetDetails(marksheetDetails,trmTo,trmGrd,finTo,finGrd,rowTo,rowPer,finalPercennt,rowGadee);
		
		 si1 = selectedExam.getSign1();
		 si2 = selectedExam.getSign2();
		 si3 = selectedExam.getSign3();
		 si4 = selectedExam.getSign4();
		makeSignList(teacherSignList,signList,si1,si2,si3,si4);
		
		subjectList=db.selectedSubjectTypeofClassSectionForExam(classId, "scholastic", conn,schid);
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
		roundOffType="";
		marksFormat="";
	}
	
	
	public void checkRoundOffStart()
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
	
	public String addNow()
	{
		Connection conn=DataBaseConnection.javaConnection();
		
		int flag1=0,flag=0,flag2=0;
		String extraValues="",otherValues="",termTotal="",termGrade="",finalTotal="",finalGrade="",finalPercent="",rowTotal="",rowPercent="",rowGrade="",sign1="",sign2="",sign3="",sign4="";
		if(extraField.equals("yes") && selExtraFieldList.size()<=0)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please select atleast one extra value"));
			flag=1;
		}
		
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
					
					ArrayList<String> signNameList=new ArrayList<>();
					
					Map<String, String> map=new HashMap<String, String>();
					for(ExtraFieldInfo sign:teacherSignList)
					{
						String signImageName="";
						if(!sign.getExtraValue().trim().equals("") && sign.getClassId().equals(classId)) 
						{
							
							if(sign.getFile().getSize()>0 && sign.getLabel().equals("image"))
							{
								String ext=sign.getFile().getFileName().substring(sign.getFile().getFileName().lastIndexOf(".")+1);
								signImageName=sign.getExtraValue().trim()+"_"+classId+"_"+sign.getSectionId()+"_"+schid+"_"+session+"."+ext;
								db.makeProfileSchid(schid,sign.getFile(),signImageName,conn);
								map.put(sign.getSectionId(),sign.getRollNo()+"$"+sign.getExtraValue()+"$"+sign.getLabel()+"$"+signImageName+"$"+sign.getAlignment());
							}
							else if((sign.getFile().getSize()==0 ) && (sign.getLabel().equals("image")) && (!sign.getImagePath().equalsIgnoreCase("") ))
							{
								try 
								{
									String imageValue = sign.getImagePath().substring(sign.getImagePath().lastIndexOf("/")+1);
									map.put(sign.getSectionId(),sign.getRollNo()+"$"+sign.getExtraValue()+"$"+sign.getLabel()+"$"+imageValue+"$"+sign.getAlignment());
								} catch (Exception e) {e.printStackTrace();}
							}
							else
							{
								map.put(sign.getSectionId(),sign.getRollNo()+"$"+sign.getExtraValue()+"$"+sign.getLabel()+"$$"+sign.getAlignment());
							}
						}
						else if(sign.getClassId().equals(classId))
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
							if(sign.getFile().getSize()>0 && sign.getLabel().equals("image"))
							{
								String ext=sign.getFile().getFileName().substring(sign.getFile().getFileName().lastIndexOf(".")+1);
								signImageName=sign.getExtraValue().trim()+"_"+classId+"_"+schid+"_"+session+"."+ext;
								db.makeProfileSchid(schid,sign.getFile(),signImageName,conn);
								signNameList.add(sign.getRollNo()+","+sign.getExtraValue()+","+sign.getLabel()+","+signImageName+","+sign.getAlignment());
							}
							else if( (sign.getFile().getSize()==0) && (sign.getLabel().equals("image")) && (!sign.getImagePath().equalsIgnoreCase("") ))
							{
								try 
								{
									String imageValue = sign.getImagePath().substring(sign.getImagePath().lastIndexOf("/")+1);
									signNameList.add(sign.getRollNo()+","+sign.getExtraValue()+","+sign.getLabel()+","+imageValue+","+sign.getAlignment());
								} catch (Exception e) {e.printStackTrace();}
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
					
					
					int i=objExam.EditExamSetting(idEdit,testName,periodicTest,actualMark,reflectMark,finalPT,header1,header2,header3,finalMarks,
					mlCase,abCase,rank,roundOff,roundOffType,marksFormat,extraField,otherField,extraFieldPlace,extraFormat,extraValues,
					otherValues,termTotal,termGrade,finalTotal,finalGrade,rowTotal,rowPercent,schid,classId,sign1,sign2,sign3,sign4,
					gradeScaleList,std_image,showHeader,coschol_sub,additional_sub,other_sub,finalPercent,marks,rowGrade,roundOffPercent,
					coscholTerm,otherTerm,coscholHeader,otherHeader,addHeader,seperate_disci,disci_sub,disci_term,disci_header,
					termNameCoschol,termNameDisci,termNameOther,conn,marks_grade,gradeScaleFormat,mandatorySubject,optionalSubject,gradeScaleListCoschol,position,fullExamNameAllow);
					if(i>=1)
					{
						String refNo;
						try {
							refNo=addWorkLog(conn,extraValues,
									otherValues,termTotal,termGrade,finalTotal,finalGrade,rowTotal,rowPercent,sign1,sign2,sign3,sign4,finalPercent,rowGrade);
						} catch (Exception e) {
							e.printStackTrace();
						}
						

						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Exam Setting Updated Sucessfully"));
						roundOff=roundOffPercent=roundOffType=extraField=otherField="";
						extraFieldList=selExtraFieldList=otherValueList=selOtherValueList=marksheetDetails=signList=new ArrayList<>();

						testName="PT";
						periodicTest=actualMark=reflectMark=finalPT="0";gradeScaleFormat="vertical";
						marks="";gradeScaleList=std_image=showHeader=coschol_sub=additional_sub=other_sub="";
						header1=header2=header3=finalMarks=mlCase=abCase=rank=marksFormat=extraFieldPlace=extraFormat="";
						showType=showType1=showType2=showData=false;
						
						return "editViewExamSetting.xhtml";
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
		return "";
	}
	
	
	public String addWorkLog(Connection conn,String extraValues,
			String otherValues,String termTotal,String termGrade,String finalTotal,String finalGrade,String rowTotal,String rowPercent,String sign1,String sign2,String sign3,String sign4,String finalPercent,String rowGrade)
		{
		String value = "";
		String language= classId;

		value = testName+" -- "+periodicTest+" -- "+actualMark+" -- "+reflectMark+" -- "+finalPT+" -- "+header1+" -- "+header2+" -- "+header3+" -- "+finalMarks+" -- "+
				mlCase+" -- "+abCase+" -- "+rank+" -- "+roundOff+" -- "+roundOffType+" -- "+marksFormat+" -- "+extraField+" -- "+otherField+" -- "+extraFieldPlace+" -- "+extraFormat+" -- "+extraValues+" -- "+
				otherValues+" -- "+termTotal+" -- "+termGrade+" -- "+finalTotal+" -- "+finalGrade+" -- "+rowTotal+" -- "+rowPercent+" -- "+sign1+" -- "+sign2+" -- "+sign3+" -- "+sign4+" -- "+
				gradeScaleList+" -- "+std_image+" -- "+showHeader+" -- "+coschol_sub+" -- "+coscholHeader+" -- "+coscholTerm+" -- "+termNameCoschol+" -- "+additional_sub+" -- "+addHeader+" -- "+other_sub+" -- "+
				otherHeader+" -- "+otherTerm+" -- "+termNameOther+" -- "+seperate_disci+" -- "+disci_sub+" -- "+disci_term+" -- "+disci_header+" -- "+termNameDisci+" -- "+finalPercent+" -- "+marks+" -- "+
				rowGrade+" -- "+roundOffPercent+" -- "+marks_grade;

		String refNo = workLg.saveWorkLogMehod(language,"Edit Exam Setting","WEB",value,conn);
		return refNo;
		}

	
	
	public void closeDialog()
	{
		PrimeFaces.current().executeInitScript("PF('upDlg').hide()");
		
	}
	

	public void makeExtraFieldList(ArrayList<ExtraFieldInfo> extraFieldList) 
	{
		objExam.makeExtraFieldList(extraFieldList);
		
		if(extraField.equalsIgnoreCase("yes"))
		{
			String extraVals = selectedExam.getExtraValues();
			String[] splitExtra = extraVals.split(","); 
			for(ExtraFieldInfo info1:extraFieldList)
			{
				for(int i=0;i<splitExtra.length;i++)
				{
					if(splitExtra[i].contains(info1.getExtraValue()))
					{
						String[] extraValue = splitExtra[i].split("-");
						info1.setRollNo(extraValue[1]);
						if(extraValue.length>2)
							info1.setAttendance(extraValue[2]);
						
						ExtraFieldInfo mnff = new ExtraFieldInfo();
						mnff.setsNo(info1.getsNo());
						mnff.setExtraValue(info1.getExtraValue());
						mnff.setRollNo(extraValue[1]);
						selExtraFieldList.add(mnff);
						break;
					}
					else
					{
						info1.setRollNo(String.valueOf(splitExtra.length+1));
					}
				}
			}
			showExtraTable = true;
		}
	}
	

	public void makeMarksheetDetails(ArrayList<ExtraFieldInfo> marksheetDetails,String trmTo,String trmGrd,String finTo,String finGrd,String rowTo,String rowPer,String finalPercenntt,String rowGradeee) 
	{
		objExam.makeMarksheetDetails(marksheetDetails);
		ArrayList<String> list=new ArrayList<>();
		list.add(trmTo);
		list.add(trmGrd);
		list.add(finTo);
		list.add(finGrd);
		list.add(finalPercenntt); 
		list.add(rowTo);
		list.add(rowPer);
		list.add(rowGradeee);
		
		for(ExtraFieldInfo info:marksheetDetails)
		{
			for(String value:list)
			{
				String arr[]=value.split(",");
				if(arr[0].equals(info.getStudentName()))
				{
					info.setLabel(arr[1]);
					if(arr.length>=3)
						info.setClassName(arr[2]);
					if(arr.length==4)
						info.setRollNo(arr[3]);
				}
			}
		}
	}

	
	
	

	public void makeSignList(ArrayList<ExtraFieldInfo> teacherSignList,ArrayList<ExtraFieldInfo> signList,String si1,String si2,String si3,String si4) 
	{
		Connection con = DataBaseConnection.javaConnection();
		SchoolInfoList ls = db.fullSchoolInfo(schid, con);
		String path = ls.getDownloadpath();
		if (ls.getProjecttype().equals("offline")) {
			path="";
		}
		Map<String,String> map = new HashMap<>();               
		if(!si1.equals(""))
		{
			
			
			si1 = si1.substring(1, si1.length()-1);           //remove curly brackets
			String[] keyValuePairs = si1.split(",");              //split the string to creat key-value pairs
			
			for(String pair : keyValuePairs)                        //iterate over the pairs
			{
				
			    String[] entry = pair.split("=");                   //split the pairs to get key and value 
			    if(entry.length==2) 
			    	map.put(entry[0].trim(), entry[1].trim());          //add them to the hashmap and trim whitespaces
			    else
			    	map.put(entry[0].trim(),"");  
			}
		}
		
		 ArrayList<ClassInfo> sectionList = db.allSectionList(classId, con);
		 int i=1;
		 for(ClassInfo section : sectionList)
		 {
			 if(map.containsKey(section.getSectionId()))
			 {
				 String sign=map.get(section.getSectionId());
				 ExtraFieldInfo sf = new ExtraFieldInfo();
				 sf.setImagePath("");
				 sf.setsNo(String.valueOf(i++));
				
				if(sign.contains("$"))
				{
					String[] signArr = sign.split("\\$");
					sf.setRollNo(signArr[0]);
					sf.setExtraValue(signArr[1]);
					sf.setLabel(signArr[2]);
					
					if(sf.getLabel().equals("image"))
					{
						sf.setImagePath(path+signArr[3]);
						sf.setShowImage(true);
					}
					else
					{
						sf.setImagePath("");
						sf.setShowImage(false);
					}
					 sf.setSectionId(section.getSectionId());
					 sf.setClassId(section.getClassid());
					 sf.setClassName(className+"-"+section.getSectionName());
					sf.setAlignment(signArr[4]);
					sf.setSignType("teacher");
				}
				else
				{
					sf.setRollNo("");
					sf.setExtraValue("");
					sf.setLabel("manual");
					 sf.setSectionId(section.getSectionId());
					 sf.setClassId(section.getClassid());
					 sf.setClassName(className+"-"+section.getSectionName());
					sf.setImagePath("");
					sf.setAlignment("left");
					sf.setSignType("teacher");
				}
				teacherSignList.add(sf);
			 }
			 else
			 {
				 ExtraFieldInfo sf = new ExtraFieldInfo();
				 sf.setRollNo("");
				 sf.setsNo(String.valueOf(i++));
				 sf.setExtraValue("");
				 sf.setLabel("manual");
				 sf.setSectionId(section.getSectionId());
				 sf.setClassId(section.getClassid());
				 sf.setClassName(className+"-"+section.getSectionName());
				 sf.setImagePath("");
				 sf.setAlignment("left");
				 sf.setSignType("teacher");
				 teacherSignList.add(sf);
			 }
		 }

		try {
			con.close();
		} catch (Exception e) {
		}

		
		ArrayList<String> list=new ArrayList<>();
		list.add(si2);
		list.add(si3);
		list.add(si4);
		
		i=1;
		for(String sign:list)
		{
			ExtraFieldInfo sf = new ExtraFieldInfo();
			sf.setImagePath("");
			sf.setsNo(String.valueOf(i++));
			if(sign.contains(","))
			{
				String[] signArr = sign.split(",");
				sf.setRollNo(signArr[0]);
				sf.setExtraValue(signArr[1]);
				sf.setLabel(signArr[2]);
				if(sf.getLabel().equals("image"))
				{
					sf.setImagePath(path+signArr[3]);
					sf.setShowImage(true);
				}
				else
				{
					sf.setImagePath("");
					sf.setShowImage(false);
				}
				sf.setAlignment(signArr[4]);
				sf.setSignType("other");
			}
			else
			{
				sf.setRollNo("");
				sf.setExtraValue("");
				sf.setLabel("manual");
				sf.setImagePath("");
				sf.setAlignment("left");
				sf.setSignType("other");
			}
			signList.add(sf);
		}
	}
	
	
	public String removeSignImage()
	{
    	Connection conn=DataBaseConnection.javaConnection();
		SchoolInfoList ls = db.fullSchoolInfo(schid, conn);
		String path = ls.getUploadpath();
		
		String updatedName="";
		 // System.out.println(selectedImage.getSignType());
		if(selectedImage.getSignType().equals("teacher"))
		{
			Map<String,String> map=new HashMap<>();
			for(ExtraFieldInfo sign:teacherSignList)
			{
				if(sign.getsNo().equals(selectedImage.getsNo()))
				{
					sign.setLabel("manual");
					sign.setImagePath("");
					sign.setShowImage(false);
				}
				String signImageName="";
				if(!sign.getExtraValue().trim().equals("") ) 
				{
					if(sign.getFile()!=null && sign.getFile().getSize()>0 && sign.getLabel().equals("image"))
					{
						String ext=sign.getFile().getFileName().substring(sign.getFile().getFileName().lastIndexOf(".")+1);
						signImageName=sign.getExtraValue().trim()+"_"+classId+"_"+sign.getSectionId()+"_"+schid+"_"+session+"."+ext;
						db.makeProfileSchid(schid,sign.getFile(),signImageName,conn);
						map.put(sign.getSectionId(),sign.getRollNo()+"$"+sign.getExtraValue()+"$"+sign.getLabel()+"$"+signImageName+"$"+sign.getAlignment());
					}
					else
					{
						map.put(sign.getSectionId(),sign.getRollNo()+"$"+sign.getExtraValue()+"$"+sign.getLabel()+"$$"+sign.getAlignment());
					}
				}
				else
				{
					map.put(sign.getSectionId(),"");
				}
			}
			updatedName=map.toString();
		}
		else
		{
			updatedName=selectedImage.getExtraValue()+",manual,,left";
		}
		
		int status =db.removeSignImage(selectedImage,idEdit,conn,updatedName);
		if(status>=1)
		{
//			File file = new File(path+selectedImage.getImagePath().substring(selectedImage.getImagePath().lastIndexOf("/")+1));
//			file.delete();
			FacesContext context1 = FacesContext.getCurrentInstance();
			context1.addMessage(null, new FacesMessage("Sign Image Deleted"));
			
			if(selectedImage.getSignType().equals("teacher"))
			{
				for(ExtraFieldInfo sign:teacherSignList)
				{
					if(sign.getsNo().equals(selectedImage.getsNo()))
					{
						sign.setLabel("manual");
						sign.setImagePath("");
						sign.setShowImage(false);
					}
				}
			}
			else if(selectedImage.getSignType().equals("other"))
			{
				for(ExtraFieldInfo sign:signList)
				{
					if(sign.getsNo().equals(selectedImage.getsNo()))
					{
						sign.setLabel("manual");
						sign.setImagePath("");
						sign.setShowImage(false);
					}
				}
			}
	    }
		else 
		{
			FacesContext context1 = FacesContext.getCurrentInstance();
	 		context1.addMessage(null, new FacesMessage("An Error Occured... Please Try Again"));
		}
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	
	
	public void makeOtherValueList(ArrayList<ExtraFieldInfo> extraFieldList) 
	{
		objExam.makeOtherValueList(extraFieldList);
		
		if(otherField.equalsIgnoreCase("yes"))
		{
			for(ExtraFieldInfo info1:otherValueList)
			{
				String othErValues = selectedExam.getOtherValues();
				String[] splitOther = othErValues.split("%@%");
				for(int i=0;i<splitOther.length;i++)
				{
					if(splitOther[i].contains(info1.getExtraValue()))
					{
						String[] otherValue = splitOther[i].split("-");
						
						info1.setRollNo(otherValue[1]);
						info1.setLabel(otherValue[2]);
						info1.setSignType(otherValue[3]);
						
						ExtraFieldInfo mnf = new ExtraFieldInfo();
						mnf.setExtraValue(info1.getExtraValue());
						mnf.setRollNo(otherValue[1]);
						mnf.setLabel(otherValue[2]);
						mnf.setSignType(otherValue[3]);
						if(otherValue.length>4)
						{
							info1.setRemark(otherValue[4]);
							mnf.setRemark(otherValue[4]);
						}
						selOtherValueList.add(mnf);
						break;
					}
					else
					{
						info1.setRollNo(String.valueOf(splitOther.length+1));
					}
				}
			}
			showOtherTable= true;
		}
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

	public String getClassId() {
		return classId;
	}

	public void setClassId(String classId) {
		this.classId = classId;
	}



	public String getgradeScaleList() {
		return gradeScaleList;
	}

	public void setgradeScaleList(String gradeScaleList) {
		this.gradeScaleList = gradeScaleList;
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

	public String getMarks() {
		return marks;
	}

	public void setMarks(String marks) {
		this.marks = marks;
	}

	public String getRoundOffPercent() {
		return roundOffPercent;
	}

	public void setRoundOffPercent(String roundOffPercent) {
		this.roundOffPercent = roundOffPercent;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public ExamSettingInfo getSelectedExam() {
		return selectedExam;
	}

	public void setSelectedExam(ExamSettingInfo selectedExam) {
		this.selectedExam = selectedExam;
	}

	public String getGradeScaleList() {
		return gradeScaleList;
	}

	public void setGradeScaleList(String gradeScaleList) {
		this.gradeScaleList = gradeScaleList;
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

	public ExtraFieldInfo getSelectedImage() {
		return selectedImage;
	}

	public void setSelectedImage(ExtraFieldInfo selectedImage) {
		this.selectedImage = selectedImage;
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

	public String getSignRank() {
		return signRank;
	}

	public void setSignRank(String signRank) {
		this.signRank = signRank;
	}

	public ArrayList<ExtraFieldInfo> getTeacherSignList() {
		return teacherSignList;
	}

	public void setTeacherSignList(ArrayList<ExtraFieldInfo> teacherSignList) {
		this.teacherSignList = teacherSignList;
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

	public boolean isShowOptionalSubject() {
		return showOptionalSubject;
	}

	public void setShowOptionalSubject(boolean showOptionalSubject) {
		this.showOptionalSubject = showOptionalSubject;
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

	public String getGradeScaleListCoschol() {
		return gradeScaleListCoschol;
	}

	public void setGradeScaleListCoschol(String gradeScaleListCoschol) {
		this.gradeScaleListCoschol = gradeScaleListCoschol;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
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

