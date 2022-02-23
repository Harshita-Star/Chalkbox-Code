package reports_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import exam_module.DataBaseMethodsBLMExam;
import exam_module.DataBaseMethodsExam;
import exam_module.ExamInfo;
import exam_module.SubExamInfo;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.ExamSettingInfo;
import schooldata.StudentInfo;
import schooldata.SubjectGroupInfo;
import schooldata.SubjectInfo;
import schooldata.TermInfo;

@ManagedBean(name="resultList")
@ViewScoped
public class ResultListOfStudentBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	String name,className,sectionName;
	ArrayList<StudentInfo> studentList=new ArrayList<>();
	boolean show;
	int subjectListSize;
	ArrayList<SelectItem> classSection,sectionList,termList;
	ArrayList<String> selectedTerm;
	String selectedClassSection,selectedSection;
	ExamSettingInfo examSetting=new ExamSettingInfo();
	String session,schid;
	String termTotal,termTotalName, termGrade, termGradeName, finalTotal, finalTotalName, finalGrade, finalGradeName, finalPercent, finalPercentName, examMarks, examName,rowTotal,rowTotalName,rowPercent,rowPercentName,rowGrade,rowGradeName,seperate_disci,reflectMarks;
	DataBaseMethodsBLMExam objBLM=new DataBaseMethodsBLMExam();
	DataBaseMethodsExam objExam=new  DataBaseMethodsExam();
	DatabaseMethods1 obj=new DatabaseMethods1();

	public void allSections()
	{
		Connection conn=DataBaseConnection.javaConnection();
		sectionList=obj.allSection(selectedClassSection,conn);
		termList=obj.selectedTermOfClass(selectedClassSection,conn,session,schid);
		try {
			conn.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}

	public ResultListOfStudentBean()
	{
		Connection conn=DataBaseConnection.javaConnection();
		classSection=obj.allClass(conn);
		schid=obj.schoolId();
		session=obj.selectedSessionDetails(schid, conn);
		try {
			conn.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}
	
	public ArrayList<SubjectInfo> createSubjectList(String classid,String sectionid,String subjectType,Connection conn,String addNumber)
	{
		ArrayList<SubjectGroupInfo> subGroup=objExam.subjectListForStudentWithSubjectGroup(classid, session, addNumber, sectionid,subjectType,conn,rowTotal,rowTotalName,rowPercent,rowPercentName,rowGrade,rowGradeName,seperate_disci,schid);
		if(subGroup!=null && subGroup.size()>0)
			subjectListSize=subGroup.get(0).getListSize();
		ArrayList<SubjectInfo> tempList=new ArrayList<>();
		for(SubjectGroupInfo sub:subGroup)
		{
			if(sub.getSubjectList().size() == 1) 
			{
				SubjectInfo ss1 = sub.getSubjectList().get(0);
				ss1.setHeader(true);
				ss1.setDescription("single");
				tempList.add(ss1);
			}
			else 
			{
				SubjectInfo subject=new SubjectInfo();
				subject.setSubjectName(sub.getGroupName());
				subject.setHeader(true);
				subject.setSubjectId("");
				subject.setDescription("group");
				tempList.add(subject);
				tempList.addAll(sub.getSubjectList());
			}
		}
		return tempList;
	}

	
	public void generatePromotion()
	{
		Connection conn=DataBaseConnection.javaConnection();

		studentList=objExam.studentBasicDetailsForMarksheet(schid,"", conn,"byClass",selectedSection);
		if(studentList.size()>0)
		{
			show=true;
			String classId = "",sectionId = "",addNumber="";
			className=studentList.get(0).getClassName();
			sectionName=studentList.get(0).getSectionName();
			classId=studentList.get(0).getClassId();
			sectionId=studentList.get(0).getSectionid();
			
			String termId="";
			for(String term:selectedTerm)
			{
				termId+=term+"','";
			}
			termId=termId.substring(0,termId.lastIndexOf("','"));
			
			checkExamSetting(selectedClassSection, conn);
			ArrayList<String> scholasticColumnsList = new ArrayList<>();
			ArrayList<TermInfo> termList=objBLM.termListByClassTermId(termId, selectedClassSection, session, "scholastic", conn, "all", termTotal, termTotalName, termGrade, termGradeName, finalTotal, finalTotalName, finalGrade, finalGradeName, finalPercent, finalPercentName, examMarks, examName,reflectMarks,schid);
			int termSize=0,ptFlag=0;
			for(TermInfo term : termList)
			{
				ptFlag=0;
				if(Integer.parseInt(term.getTermId())>0)
					termSize++;
				for(ExamInfo exam : term.getExamInfoList())
				{
					if(exam.getInclude().equals("Yes"))
					{
						if(exam.getSubExamList()!=null)
						{
							if(exam.getExamTaken().equals("other"))
							{
								for(SubExamInfo subExam : exam.getSubExamList())
									scholasticColumnsList.add(term.getTermId()+"term"+subExam.getMainExamId()+"total-obtained"+subExam.getSubExamName());
							}
							else
							{
								if(ptFlag==0)
								{
									scholasticColumnsList.add(term.getTermId()+"term"+"P.T."+"total-obtained"+examSetting.getExamName());
									ptFlag=1;
								}
							}
						}
						else
							scholasticColumnsList.add(term.getTermId()+"term"+""+"total-obtained"+"");
					}
				}
			}
			String comeTestType="all";
			for(StudentInfo allInfo : studentList)
			{
				addNumber=allInfo.getAddNumber();
				allInfo.setSession(session);
				allInfo.setMarkList(createSubjectList(classId, sectionId, "scholastic", conn,addNumber));
				objExam.fillStudentMarks(sectionId,addNumber,allInfo.getMarkList(),session,termList,conn,allInfo,scholasticColumnsList,termSize,"scholastic",allInfo.getMarkList().size(),examSetting,"report",comeTestType,schid);
			}

			Collections.sort(studentList,new MySalaryComp());

			int counter=1;
			if(studentList.size()>0 )
			{
				String marks="",marksBack="";
				for(int i=0;i<studentList.size();i++)
				{
					marks=studentList.get(i).getMarks().split(" / ")[0];
					if(i!=0)
						marksBack=studentList.get(i-1).getMarks().split(" / ")[0];
					if(marks.equals("-1"))
					{
						studentList.get(i).setRank("");
						studentList.get(i).setMarks("AB");
					}
					else if(marks.equals("0"))
					{
						studentList.get(i).setRank("");
					}
					else if(i==0 || (marks.equals(marksBack)))
					{
						studentList.get(i).setRank(String.valueOf(counter));
					}
					else
					{
						++counter;
						studentList.get(i).setRank(String.valueOf(counter));
					}
				}
			}
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("No Student Found! Please Try Again."));
		}
		try {
			conn.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}
	
	public void checkExamSetting(String classid,Connection conn)
	{
		examSetting=objExam.examSettingDetail(classid,conn);
		termTotal=examSetting.getTermTotal();
		termTotalName=examSetting.getTermTotalName();
		termGrade=examSetting.getTermGrade();
		termGradeName=examSetting.getTermGradeName();
		finalTotal=examSetting.getFinalTotal();
		finalTotalName=examSetting.getFinalTotalName();
		finalGrade=examSetting.getFinalGrade();
		finalGradeName=examSetting.getFinalGradeName();
		finalPercent=examSetting.getFinalPercent();
		finalPercentName=examSetting.getFinalPercentName();
		examMarks=examSetting.getExamMarks();
		rowTotal=examSetting.getRowTotal();
		rowTotalName=examSetting.getRowTotalName();
		rowPercent=examSetting.getRowPercent();
		rowPercentName=examSetting.getRowPercentName();
		rowGrade=examSetting.getRowGrade();
		rowGradeName=examSetting.getRowGradeName();
		seperate_disci=examSetting.getSepearte_disci();
		reflectMarks=examSetting.getReflectMark();
	}

	public ArrayList<SelectItem> getSectionList() {
		return sectionList;
	}
	public void setSectionList(ArrayList<SelectItem> sectionList) {
		this.sectionList = sectionList;
	}
	public String getSelectedSection() {
		return selectedSection;
	}
	public void setSelectedSection(String selectedSection) {
		this.selectedSection = selectedSection;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ArrayList<StudentInfo> getStudentList() {
		return studentList;
	}
	public void setStudentList(ArrayList<StudentInfo> studentList) {
		this.studentList = studentList;
	}
	public boolean isShow() {
		return show;
	}
	public void setShow(boolean show) {
		this.show = show;
	}
	public ArrayList<SelectItem> getClassSection() {
		return classSection;
	}
	public void setClassSection(ArrayList<SelectItem> classSection) {
		this.classSection = classSection;
	}
	public String getSelectedClassSection() {
		return selectedClassSection;
	}
	public void setSelectedClassSection(String selectedClassSection) {
		this.selectedClassSection = selectedClassSection;
	}
	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getSectionName() {
		return sectionName;
	}

	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}

	public ArrayList<SelectItem> getTermList() {
		return termList;
	}

	public void setTermList(ArrayList<SelectItem> termList) {
		this.termList = termList;
	}

	public ArrayList<String> getSelectedTerm() {
		return selectedTerm;
	}

	public void setSelectedTerm(ArrayList<String> selectedTerm) {
		this.selectedTerm = selectedTerm;
	}

	class MySalaryComp implements Comparator<StudentInfo>{

		@Override
		public int compare(StudentInfo e1, StudentInfo e2)
		{
			// // System.out.println(e1.getMarks());
			double mark1=Double.parseDouble(e1.getMarks().split(" / ")[0]);
			double mark2=Double.parseDouble(e2.getMarks().split(" / ")[0]);
			if(mark1 < mark2){
				return 1;
			} else {
				return -1;
			}
		}
	}
}
