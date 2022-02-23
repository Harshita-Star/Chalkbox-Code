package exam;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import org.primefaces.PrimeFaces;
import org.primefaces.model.file.UploadedFile;

import Json.DataBaseMeathodJson;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.SchoolInfoList;

@ManagedBean(name="viewQuestionBean")
@ViewScoped
public class ViewQuestionBean implements Serializable{
	
	String selectedClass,selectedSubject,examName,serialNumber,question,optionA,optionB,optionC,optionD,session,schid,solution,answer;
	String quPath,op1Path,op2Path,op3Path,op4Path,solPath;
	String examLang,username,userType;
	boolean disable;
	ArrayList<SelectItem> classList;
	ArrayList<SelectItem> examList;
	ArrayList<QuestionInfo> questionList;
	ArrayList<QuestionInfo> selectdQuestions;
	ArrayList<SelectItem> subjectList;
	QuestionInfo selectedQuestion;
	DatabaseMethods1 obj=new DatabaseMethods1();
	DatabaseMethodExam dbExam = new DatabaseMethodExam();
	transient UploadedFile que,op1,op2,op3,op4,solu;
	boolean show;
	
	public ViewQuestionBean() {
		Connection conn=DataBaseConnection.javaConnection();
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		username=(String) ss.getAttribute("username");
		userType=(String) ss.getAttribute("type");
		schid=obj.schoolId();
		session=obj.selectedSessionDetails(schid, conn);
		
		if(userType.equalsIgnoreCase("admin")
				|| userType.equalsIgnoreCase("authority")
				|| userType.equalsIgnoreCase("principal")
				|| userType.equalsIgnoreCase("vice principal")
				|| userType.equalsIgnoreCase("front office")
				|| userType.equalsIgnoreCase("office staff")
				|| userType.equalsIgnoreCase("Accounts"))
		{
			classList=obj.allClass(conn);
		}
		else if(userType.equalsIgnoreCase("academic coordinator") 
				|| userType.equalsIgnoreCase("Administrative Officer"))
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schid,conn);
			classList=obj.cordinatorClassList(empid, schid, conn);
		}
		else
		{
			String empid=new DataBaseMeathodJson().employeeIdbyEmployeeName(username,schid,conn);
			classList=obj.allClassListForClassTeacher(empid,schid,conn);
		}
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void getSubjects() {
		Connection conn = DataBaseConnection.javaConnection();
		subjectList = obj.allSubjectClassWise(selectedClass, conn);
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getExams() {
		Connection conn = DataBaseConnection.javaConnection();
		examList = dbExam.allExamsForClass(selectedClass,session,schid,"Active",conn);
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void submit() {
		Connection conn = DataBaseConnection.javaConnection();
		 // System.out.println(examLang);
		questionList = dbExam.getAllQuestionList(selectedClass,selectedSubject,examName,examLang,schid,conn);
		
		if(disable==false) {
			for(QuestionInfo a:questionList) {
				if(a.getQuestionType().equalsIgnoreCase("hindi")) {
					a.setHindiQuestion(a.getQuestion());
					a.setQuestion("Yes");
				}else {
					a.setHindiQuestion("Yes");
				}
			}
		}else {
			for(QuestionInfo a:questionList) {
				if(a.getQuestionType().equalsIgnoreCase("hindi")) {
					a.setHindiQuestion(a.getQuestion());
					a.setQuestion("No");
				}else {
					a.setHindiQuestion("No");
				}
			}
		}
		
		if(questionList.size()>0) {
			show = true;
		}else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("There is no Questions in this exam."));
		}
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getLaguage() {
		Connection conn = DataBaseConnection.javaConnection();
		examLang = dbExam.getExamLaguage(examName, conn);
		if(examLang.equalsIgnoreCase("both")) {
			disable = false;
		}else {
			disable = true;
		}
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void editDetails() {
		if(examLang.equalsIgnoreCase("hindi")) {
			question=selectedQuestion.getHindiQuestion();
		}else {
			question=selectedQuestion.getQuestion();
		}
			Connection conn = DataBaseConnection.javaConnection();
			SchoolInfoList info=obj.fullSchoolInfo(conn);
			String savePath = info.getDownloadpath();
			serialNumber=selectedQuestion.getSerialNumber();
			optionA=selectedQuestion.getOptionA();
			optionB=selectedQuestion.getOptionB();
			optionC=selectedQuestion.getOptionC();
			optionD=selectedQuestion.getOptionD();
			solution=selectedQuestion.getSolution();
			answer=selectedQuestion.getAnswer();
			quPath = savePath+selectedQuestion.getQuestionImage();
			op1Path=savePath+selectedQuestion.getOptionAImage();
			op2Path=savePath+selectedQuestion.getOptionBImage();
			op3Path=savePath+selectedQuestion.getOptionCImage();
			op4Path=savePath+selectedQuestion.getOptionDImage();
			solPath=savePath+selectedQuestion.getSolutionImage();
			PrimeFaces.current().executeInitScript("PF('editDialog').show();");
		
	}
	
	public void update() {
		Connection conn = DataBaseConnection.javaConnection();
		
		if (que != null && que.getSize() > 0)
		{
			String exten[]=que.getFileName().split("\\.");
			quPath="Question-"+examName+"-"+serialNumber+"-"+schid+"-"+exten[exten.length-1];
			obj.makeScanPath(que, quPath,conn);
		}
		else
		{
			quPath=selectedQuestion.getQuestionImage();
		}
		
		if (op1 != null && op1.getSize() > 0)
		{
			String exten[]=op1.getFileName().split("\\.");
			op1Path="Option1-"+examName+"-"+serialNumber+"-"+schid+"-"+exten[exten.length-1];
			obj.makeScanPath(op1, op1Path,conn);
		}
		else
		{
			op1Path=selectedQuestion.getOptionAImage();
		}
		if (op2 != null && op2.getSize() > 0)
		{
			String exten[]=op2.getFileName().split("\\.");
			op2Path="Option2-"+examName+"-"+serialNumber+"-"+schid+"-"+exten[exten.length-1];
			obj.makeScanPath(op2, op2Path,conn);
		}
		else
		{
			op2Path=selectedQuestion.getOptionBImage();
		}
		if (op3 != null && op3.getSize() > 0)
		{
			String exten[]=op3.getFileName().split("\\.");
			op3Path="Option3-"+examName+"-"+serialNumber+"-"+schid+"-"+exten[exten.length-1];
			obj.makeScanPath(op3, op3Path,conn);
		}
		else
		{
			op3Path=selectedQuestion.getOptionCImage();
		}
		if (op4 != null && op4.getSize() > 0)
		{
			String exten[]=op4.getFileName().split("\\.");
			op4Path="Option4-"+examName+"-"+serialNumber+"-"+schid+"-"+exten[exten.length-1];
			obj.makeScanPath(op4, op4Path,conn);
		}
		else
		{
			op4Path=selectedQuestion.getOptionDImage();
		}
		if (solu != null && solu.getSize() > 0)
		{
			String exten[]=solu.getFileName().split("\\.");
			solPath="Solution-"+examName+"-"+serialNumber+"-"+schid+"-"+exten[exten.length-1];
			obj.makeScanPath(solu, solPath,conn);
		}
		else
		{
			solPath = selectedQuestion.getSolutionImage();
		}
		int status = dbExam.updateQuestions(selectedQuestion.getId(),serialNumber,question,optionA,optionB,optionC,optionD,solution,answer,quPath,
				op1Path,op2Path,op3Path,op4Path,solPath,conn);
		if(status>0) {
			PrimeFaces.current().executeInitScript("PF('editDialog').hide();");
			questionList = dbExam.getAllQuestionList(selectedClass,selectedSubject,examName,examLang,schid,conn);
		}else {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error Occur"));
		}
		
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void deleteExam() {
		Connection conn = DataBaseConnection.javaConnection();
		if(selectdQuestions.isEmpty()) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("First Select atleast one question for delete.."));
		}else {
			int status = dbExam.deleteQuestions(selectdQuestions,"Deleted",conn);
			if(status>0) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Question Deleted", "Question Deleted"));
				questionList = dbExam.getAllQuestionList(selectedClass,selectedSubject,examName,examLang,schid,conn);
			}else {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Occur", "Error Occur"));
			}
		}
		
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void view() {
		Connection conn = DataBaseConnection.javaConnection();
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		ss.setAttribute("classid", selectedClass);
		ss.setAttribute("subjectid", selectedSubject);
		ss.setAttribute("examid", examName);
		ss.setAttribute("language", examLang);
		ss.setAttribute("render", dbExam.getExamLaguage(examName, conn));
		PrimeFaces.current().executeInitScript("window.open('questionFormat.xhtml');");
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getSelectedClass() {
		return selectedClass;
	}


	public void setSelectedClass(String selectedClass) {
		this.selectedClass = selectedClass;
	}



	public String getSelectedSubject() {
		return selectedSubject;
	}



	public void setSelectedSubject(String selectedSubject) {
		this.selectedSubject = selectedSubject;
	}



	public String getExamName() {
		return examName;
	}



	public void setExamName(String examName) {
		this.examName = examName;
	}



	public String getSerialNumber() {
		return serialNumber;
	}



	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}



	public String getQuestion() {
		return question;
	}



	public void setQuestion(String question) {
		this.question = question;
	}



	public String getOptionA() {
		return optionA;
	}



	public void setOptionA(String optionA) {
		this.optionA = optionA;
	}



	public String getOptionB() {
		return optionB;
	}



	public void setOptionB(String optionB) {
		this.optionB = optionB;
	}



	public String getOptionC() {
		return optionC;
	}



	public void setOptionC(String optionC) {
		this.optionC = optionC;
	}



	public String getOptionD() {
		return optionD;
	}



	public void setOptionD(String optionD) {
		this.optionD = optionD;
	}



	public String getSession() {
		return session;
	}



	public void setSession(String session) {
		this.session = session;
	}



	public String getSchid() {
		return schid;
	}



	public void setSchid(String schid) {
		this.schid = schid;
	}



	public String getSolution() {
		return solution;
	}



	public void setSolution(String solution) {
		this.solution = solution;
	}



	public String getAnswer() {
		return answer;
	}



	public void setAnswer(String answer) {
		this.answer = answer;
	}



	public String getQuPath() {
		return quPath;
	}



	public void setQuPath(String quPath) {
		this.quPath = quPath;
	}



	public String getOp1Path() {
		return op1Path;
	}



	public void setOp1Path(String op1Path) {
		this.op1Path = op1Path;
	}



	public String getOp2Path() {
		return op2Path;
	}



	public void setOp2Path(String op2Path) {
		this.op2Path = op2Path;
	}



	public String getOp3Path() {
		return op3Path;
	}



	public void setOp3Path(String op3Path) {
		this.op3Path = op3Path;
	}



	public String getOp4Path() {
		return op4Path;
	}



	public void setOp4Path(String op4Path) {
		this.op4Path = op4Path;
	}



	public String getSolPath() {
		return solPath;
	}



	public void setSolPath(String solPath) {
		this.solPath = solPath;
	}



	public ArrayList<SelectItem> getClassList() {
		return classList;
	}



	public void setClassList(ArrayList<SelectItem> classList) {
		this.classList = classList;
	}



	public ArrayList<SelectItem> getExamList() {
		return examList;
	}



	public void setExamList(ArrayList<SelectItem> examList) {
		this.examList = examList;
	}



	public ArrayList<SelectItem> getSubjectList() {
		return subjectList;
	}



	public void setSubjectList(ArrayList<SelectItem> subjectList) {
		this.subjectList = subjectList;
	}



	public DatabaseMethods1 getObj() {
		return obj;
	}



	public void setObj(DatabaseMethods1 obj) {
		this.obj = obj;
	}



	public DatabaseMethodExam getDbExam() {
		return dbExam;
	}



	public void setDbExam(DatabaseMethodExam dbExam) {
		this.dbExam = dbExam;
	}



	public UploadedFile getQue() {
		return que;
	}



	public void setQue(UploadedFile que) {
		this.que = que;
	}



	public UploadedFile getOp1() {
		return op1;
	}



	public void setOp1(UploadedFile op1) {
		this.op1 = op1;
	}



	public UploadedFile getOp2() {
		return op2;
	}



	public void setOp2(UploadedFile op2) {
		this.op2 = op2;
	}



	public UploadedFile getOp3() {
		return op3;
	}



	public void setOp3(UploadedFile op3) {
		this.op3 = op3;
	}



	public UploadedFile getOp4() {
		return op4;
	}



	public void setOp4(UploadedFile op4) {
		this.op4 = op4;
	}



	public UploadedFile getSolu() {
		return solu;
	}



	public void setSolu(UploadedFile solu) {
		this.solu = solu;
	}



	public ArrayList<QuestionInfo> getQuestionList() {
		return questionList;
	}



	public void setQuestionList(ArrayList<QuestionInfo> questionList) {
		this.questionList = questionList;
	}



	public boolean isShow() {
		return show;
	}



	public void setShow(boolean show) {
		this.show = show;
	}



	public ArrayList<QuestionInfo> getSelectdQuestions() {
		return selectdQuestions;
	}



	public void setSelectdQuestions(ArrayList<QuestionInfo> selectdQuestions) {
		this.selectdQuestions = selectdQuestions;
	}



	public QuestionInfo getSelectedQuestion() {
		return selectedQuestion;
	}



	public void setSelectedQuestion(QuestionInfo selectedQuestion) {
		this.selectedQuestion = selectedQuestion;
	}



	public String getExamLang() {
		return examLang;
	}



	public void setExamLang(String examLang) {
		this.examLang = examLang;
	}

	public boolean isDisable() {
		return disable;
	}

	public void setDisable(boolean disable) {
		this.disable = disable;
	}
	
	
	

}
