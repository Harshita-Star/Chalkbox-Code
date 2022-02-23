package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import org.primefaces.model.file.UploadedFile;

import Json.DataBaseMeathodJson;

@ViewScoped
@ManagedBean(name = "addSyllabus")
public class AddSyllabus implements Serializable {
	private static final long serialVersionUID = 1L;
	ArrayList<SelectItem> classSection, sectionList, termList;
	String selectedCLassSection, selectedSection, assignmentPhotoPath1 = "", selectedSubject, syllabusType,
			selectedTerm, basis, description, assignmentPhotoPath2 = "", assignmentPhotoPath3 = "",
			assignmentPhotoPath4 = "", assignmentPhotoPath5 = "", assignmentName, staff, subject, type, label, des,
			schoolid, userType;
	Date assShowDate = new Date();
	Date pDate;
	boolean show = false, showTable = false, status = false, showSingle = true;
	transient UploadedFile assignmentPhoto1, assignmentPhoto2, assignmentPhoto3, assignmentPhoto4, assignmentPhoto5;
	ArrayList<SubjectInfo> subjectList;
	int j = 0;
	DatabaseMethods1 obj = new DatabaseMethods1();

	public AddSyllabus() {
		basis = "All";
		syllabusType = "Course Syllabus";
		Connection conn = DataBaseConnection.javaConnection();
		HttpSession ses = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		staff = (String) ses.getAttribute("username");
		schoolid = (String) ses.getAttribute("schoolid");
		userType = (String) ses.getAttribute("type");
		try {
			if (userType.equalsIgnoreCase("admin") || userType.equalsIgnoreCase("authority")
					|| userType.equalsIgnoreCase("principal") || userType.equalsIgnoreCase("vice principal")
					|| userType.equalsIgnoreCase("front office") || userType.equalsIgnoreCase("office staff")
					|| userType.equalsIgnoreCase("Accounts")) {
				classSection = obj.allClass(conn);
			} else if (userType.equalsIgnoreCase("academic coordinator")
					|| userType.equalsIgnoreCase("Administrative Officer")) {
				String empid = new DataBaseMeathodJson().employeeIdbyEmployeeName(staff, schoolid, conn);
				classSection = obj.cordinatorClassList(empid, schoolid, conn);
			} else {
				String empid = new DataBaseMeathodJson().employeeIdbyEmployeeName(staff, schoolid, conn);
				classSection = obj.allClassDeatilsForTeacher(empid, schoolid, conn);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void allSubjects() {
		selectedSection = "";
		Connection conn = DataBaseConnection.javaConnection();
		/*
		 * if(syllabusType.equalsIgnoreCase("Term Syllabus")) {
		 * termList=obj.selectedTermOfClass(selectedCLassSection,conn); show=true;
		 * subjectList=obj.subjectListOfParticularClass(selectedCLassSection,conn);
		 * showTable=true; } else { show=false;
		 * subjectList=obj.subjectListOfParticularClass(selectedCLassSection,conn);
		 * showTable=true; }
		 */
		// sectionList = obj.allSection(selectedCLassSection, conn);
		sectionList = new ArrayList<SelectItem>();
		if (userType.equalsIgnoreCase("admin") || userType.equalsIgnoreCase("authority")
				|| userType.equalsIgnoreCase("principal") || userType.equalsIgnoreCase("vice principal")
				|| userType.equalsIgnoreCase("front office") || userType.equalsIgnoreCase("office staff")
				|| userType.equalsIgnoreCase("academic coordinator")
				|| userType.equalsIgnoreCase("Administrative Officer") || userType.equalsIgnoreCase("Accounts")) {
			SelectItem si = new SelectItem();
			si.setLabel("All");
			si.setValue("all");
			sectionList.add(si);

			sectionList.addAll(obj.allSection(selectedCLassSection, conn));
		} else {
			String empid = new DataBaseMeathodJson().employeeIdbyEmployeeName(staff, schoolid, conn);
			sectionList = obj.allSectionForTeacher(selectedCLassSection, empid, conn);
		}
		showTable = false;
		showSingle = true;
		basis = "All";

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void subjects() {
		Connection conn = DataBaseConnection.javaConnection();
		if (basis.equalsIgnoreCase("subject")) {
			if (userType.equalsIgnoreCase("admin") || userType.equalsIgnoreCase("academic coordinator")
					|| userType.equalsIgnoreCase("authority") || userType.equalsIgnoreCase("principal")
					|| userType.equalsIgnoreCase("vice principal") || userType.equalsIgnoreCase("front office")
					|| userType.equalsIgnoreCase("office staff") || userType.equalsIgnoreCase("Administrative Officer")
					|| userType.equalsIgnoreCase("Accounts")) {
				subjectList = obj.subjectListOfParticularClass(selectedCLassSection, conn);
			} else {
				String empid = new DataBaseMeathodJson().employeeIdbyEmployeeName(staff, schoolid, conn);
				subjectList = obj.AllTeacherSubject(empid, selectedSection, schoolid, conn);
			}
			showTable = true;
			showSingle = false;
		} else {
			subjectList = new ArrayList<>();
			showTable = false;
			showSingle = true;
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String uploadSyllabus() {
		Connection conn = DataBaseConnection.javaConnection();

		// DatabaseMethods1 DBM = obj;
		try {

			pDate = new Date();

			/*
			 * if (assignmentPhoto5 != null && assignmentPhoto5.getSize() > 0) {
			 * assignmentPhotoPath5 = assignmentPhoto5.getFileName(); String
			 * exten[]=assignmentPhotoPath5.split("\\.");
			 * assignmentPhotoPath5=staff+"_"+subject+"_"+dt+"_5_"+selectedSection+"."+exten
			 * [exten.length-1];
			 * DBM.makeProfile(assignmentPhoto5,assignmentPhotoPath5,conn); }
			 */

			if (showSingle == true) {
				if ((description == null || description.equals("")) && assignmentPhoto5 == null) {
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage("Please upload a file or write a description"));
					return "";
				}
			}

			int flag = 0;
			if (basis.equalsIgnoreCase("All")) {
				if (assignmentPhoto5 != null) {
					if (!assignmentPhoto5.getFileName().toLowerCase().contains(".pdf")
							&& !assignmentPhoto5.getFileName().toLowerCase().contains(".png")
							&& !assignmentPhoto5.getFileName().toLowerCase().contains(".jpg")
							&& !assignmentPhoto5.getFileName().contains(".jpeg")) {
						flag = -1;
					} else {
						flag = flag + 1;
					}
				} else if (assignmentPhoto5 == null && !description.equals("")) {
					flag = 1;
				}
			} else {
				for (SubjectInfo ss : subjectList) {
					if (ss.getAssignmentPhoto5() != null) {
						if (!ss.getAssignmentPhoto5().getFileName().toLowerCase().contains(".pdf")
								&& !ss.getAssignmentPhoto5().getFileName().toLowerCase().contains(".png")
								&& !ss.getAssignmentPhoto5().getFileName().toLowerCase().contains(".jpg")
								&& !ss.getAssignmentPhoto5().getFileName().toLowerCase().contains(".jpeg")) {
							flag = -1;
							break;
						} else {
							flag = flag + 1;
						}
					} else if (ss.getAssignmentPhoto5() == null && !ss.getDescription().equals("")) {
						flag = 1;
					}
				}
			}
			if (flag == 0) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Please select a file first"));
			} else if (flag == -1) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage("Please Select correct file format"));
			} else {
				int x = obj.addSyllabus(selectedCLassSection, selectedSection, syllabusType, type, basis,
						assignmentPhoto5, description, sectionList, subjectList, conn);
//				int x = 1;
				// i=DBM.uploadSyllabus(selectedCLassSection,type,subjectList,selectedTerm,conn);

				if (x >= 1) {
					
					if (schoolid.equals("302") || schoolid.equals("216") || schoolid.equals("221")) {

						sendNotification(conn);

					} else {
						if (selectedSection.equalsIgnoreCase("all")) {
							obj.notification(obj.schoolId(), "Syllabus", "Your Class Syllabus:" + type + " Is Added",
									selectedCLassSection + "-" + obj.schoolId(), conn);
						} else {
							obj.notification(obj.schoolId(), "Syllabus", "Your Class Syllabus:" + type + " Is Added",
									selectedSection + "-" + selectedCLassSection + "-" + obj.schoolId(), conn);
						}
					}
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage("Syllabus Upload Successfully"));
					return "addSyllabus.xhtml";
				} else {
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error Occured!!!"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return "";
	}

	public void sendNotification(Connection conn) {
		String subjename = "";
		String hw="";
		StringBuilder subjName = new StringBuilder();
		if(basis.equals("subject")) {
			subjName.append("of Subject ");
			for(SubjectInfo subjects : subjectList) {
				if((subjects.getAssignmentPhoto5() == null && !subjects.getDescription().equals("")) || (subjects.getAssignmentPhoto5() != null && subjects.getDescription().equals("")) || (subjects.getAssignmentPhoto5() != null && !subjects.getDescription().equals(""))) {
					subjName.append(subjects.getSubjectName()+",");
				}
			} 
		}else {
			subjName.append("of All Subjects ");
		}
		
		subjename = subjName.toString();
		if(subjName.substring(subjName.length()-1).equals(",")) {
			subjename = subjName.substring(0, subjName.length()-1).toString();
		}
		
		String sid = selectedSection;
		
		String className = obj.classNameFromidSchid(schoolid,selectedCLassSection, obj.selectedSessionDetails(schoolid, conn), conn);
		String sectionName = "";
		String clsSection = "";
		
		sectionName = obj.sectionNameByIdSchid(schoolid,selectedSection, conn);
		clsSection = className+"-"+sectionName;
		if(selectedSection.equalsIgnoreCase("All")) {
			sid = "-1";

			sectionName = "All";
			clsSection = className;
		}
		
		ArrayList<StudentInfo> studentDetails = obj.searchStudentListByClassSectionSchidWise(schoolid, selectedCLassSection, sid, conn);
		
		for(StudentInfo ss : studentDetails) {
			
			hw = "Dear "+ss.getFullName()+"\r\n" + 
					"Syllabus for "+type+" "+subjename+" of class "+clsSection+" is being uploaded. \r\n"+
					"Best wishes for your "+type+" \r\n" + 
					"Kindly go through the syllabus and revise your work.\r\n" + 
					"Team Chalkbox";
			obj.notification(obj.schoolId(),"Syllabus",hw, ss.getFathersPhone()+"-"+ss.getAddNumber()+"-"+schoolid,conn);
			obj.notification(obj.schoolId(),"Syllabus",hw, ss.getMothersPhone()+"-"+ss.getAddNumber()+"-"+schoolid,conn);
			
		}

	}
//	public String addWorkLog(Connection conn)
//	{
//	    String value = "";
//		String language= "";
//	 
//		
//		DatabaseMethods1 obj = new DatabaseMethods1(); 
//		String schid = obj.schoolId();
//		String className=obj.classname(selectedCLassSection, schid, conn);
//		String sectionname =obj.sectionNameByIdSchid(schid,selectedSection, conn);
//		
//		language = " Class-"+className+" --Section-"+sectionname+" --Syllabus Type-"+syllabusType+" --Syllabus name-"+type+" --Basis-"+basis;
//        if(basis.equalsIgnoreCase("All"))
//        {
//        	value = "Description-"+description+" --File-";
//        	
//        	for (SelectItem se : sectionList) {	
//        	String dt = new SimpleDateFormat("yMdhms").format(new Date());
//			String assignmentPhotoPath5 = "";
//			if (assignmentPhoto5 != null && assignmentPhoto5.getSize() > 0) {
//				String filePath1 = assignmentPhoto5.getFileName();
//				String exten[] = filePath1.split("\\.");
//				assignmentPhotoPath5 = staff + "_" + "combinedAllSub" + "_" + dt + "_5_"
//						+ selectedCLassSection + "_" + String.valueOf(se.getValue()) + "." + exten[exten.length-1];
//			}
//			
//        	  value += "(Class Id-"+selectedCLassSection+" --Path-"+assignmentPhotoPath5+")";
//        	}	
//        }
//        
//        else
//        {
//        	
//        	
//        }
//        
//        
//		
//		
//		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Add Syllabus","WEB",value,conn);
//		return refNo;
//	}

	public ArrayList<SelectItem> getClassSection() {
		return classSection;
	}

	public void setClassSection(ArrayList<SelectItem> classSection) {
		this.classSection = classSection;
	}

	public ArrayList<SelectItem> getSectionList() {
		return sectionList;
	}

	public void setSectionList(ArrayList<SelectItem> sectionList) {
		this.sectionList = sectionList;
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

	public String getAssignmentPhotoPath1() {
		return assignmentPhotoPath1;
	}

	public void setAssignmentPhotoPath1(String assignmentPhotoPath1) {
		this.assignmentPhotoPath1 = assignmentPhotoPath1;
	}

	public String getSelectedSubject() {
		return selectedSubject;
	}

	public void setSelectedSubject(String selectedSubject) {
		this.selectedSubject = selectedSubject;
	}

	public String getSyllabusType() {
		return syllabusType;
	}

	public void setSyllabusType(String syllabusType) {
		this.syllabusType = syllabusType;
	}

	public String getAssignmentPhotoPath2() {
		return assignmentPhotoPath2;
	}

	public void setAssignmentPhotoPath2(String assignmentPhotoPath2) {
		this.assignmentPhotoPath2 = assignmentPhotoPath2;
	}

	public String getAssignmentPhotoPath3() {
		return assignmentPhotoPath3;
	}

	public void setAssignmentPhotoPath3(String assignmentPhotoPath3) {
		this.assignmentPhotoPath3 = assignmentPhotoPath3;
	}

	public String getAssignmentPhotoPath4() {
		return assignmentPhotoPath4;
	}

	public void setAssignmentPhotoPath4(String assignmentPhotoPath4) {
		this.assignmentPhotoPath4 = assignmentPhotoPath4;
	}

	public String getAssignmentPhotoPath5() {
		return assignmentPhotoPath5;
	}

	public void setAssignmentPhotoPath5(String assignmentPhotoPath5) {
		this.assignmentPhotoPath5 = assignmentPhotoPath5;
	}

	public String getAssignmentName() {
		return assignmentName;
	}

	public void setAssignmentName(String assignmentName) {
		this.assignmentName = assignmentName;
	}

	public String getStaff() {
		return staff;
	}

	public void setStaff(String staff) {
		this.staff = staff;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getDes() {
		return des;
	}

	public void setDes(String des) {
		this.des = des;
	}

	public Date getAssShowDate() {
		return assShowDate;
	}

	public void setAssShowDate(Date assShowDate) {
		this.assShowDate = assShowDate;
	}

	public Date getpDate() {
		return pDate;
	}

	public void setpDate(Date pDate) {
		this.pDate = pDate;
	}

	public UploadedFile getAssignmentPhoto1() {
		return assignmentPhoto1;
	}

	public void setAssignmentPhoto1(UploadedFile assignmentPhoto1) {
		this.assignmentPhoto1 = assignmentPhoto1;
	}

	public UploadedFile getAssignmentPhoto2() {
		return assignmentPhoto2;
	}

	public void setAssignmentPhoto2(UploadedFile assignmentPhoto2) {
		this.assignmentPhoto2 = assignmentPhoto2;
	}

	public UploadedFile getAssignmentPhoto3() {
		return assignmentPhoto3;
	}

	public void setAssignmentPhoto3(UploadedFile assignmentPhoto3) {
		this.assignmentPhoto3 = assignmentPhoto3;
	}

	public UploadedFile getAssignmentPhoto4() {
		return assignmentPhoto4;
	}

	public void setAssignmentPhoto4(UploadedFile assignmentPhoto4) {
		this.assignmentPhoto4 = assignmentPhoto4;
	}

	public UploadedFile getAssignmentPhoto5() {
		return assignmentPhoto5;
	}

	public void setAssignmentPhoto5(UploadedFile assignmentPhoto5) {
		this.assignmentPhoto5 = assignmentPhoto5;
	}

	public ArrayList<SubjectInfo> getSubjectList() {
		return subjectList;
	}

	public void setSubjectList(ArrayList<SubjectInfo> subjectList) {
		this.subjectList = subjectList;
	}

	public int getJ() {
		return j;
	}

	public void setJ(int j) {
		this.j = j;
	}

	public String getSelectedTerm() {
		return selectedTerm;
	}

	public void setSelectedTerm(String selectedTerm) {
		this.selectedTerm = selectedTerm;
	}

	public boolean isShow() {
		return show;
	}

	public void setShow(boolean show) {
		this.show = show;
	}

	public ArrayList<SelectItem> getTermList() {
		return termList;
	}

	public void setTermList(ArrayList<SelectItem> termList) {
		this.termList = termList;
	}

	public boolean isShowTable() {
		return showTable;
	}

	public void setShowTable(boolean showTable) {
		this.showTable = showTable;
	}

	public String getBasis() {
		return basis;
	}

	public void setBasis(String basis) {
		this.basis = basis;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isShowSingle() {
		return showSingle;
	}

	public void setShowSingle(boolean showSingle) {
		this.showSingle = showSingle;
	}

	public String getSchoolid() {
		return schoolid;
	}

	public void setSchoolid(String schoolid) {
		this.schoolid = schoolid;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

}
