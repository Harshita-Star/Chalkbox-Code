package schooldata;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import exam_module.DataBaseMethodsExam;
import exam_module.ExamInfo;

@ManagedBean(name="marksheetWithApp")
@ViewScoped
public class PrintMarksheetApp {
	
	String schid,stdid,examId;
	String termid;
	ArrayList<StudentInfo> studentList = new ArrayList<>();
	ArrayList<SelectItem> allTerms = new ArrayList<>();
	ArrayList<ExamInfo> allExams = new ArrayList<>();
	DatabaseMethods1 DBM = new DatabaseMethods1();
	DataBaseMethodsExam dbexam = new DataBaseMethodsExam();
	public PrintMarksheetApp() throws IOException {
		
		Connection conn = DataBaseConnection.javaConnection();
		Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
			schid = params.get("schid");
			stdid = params.get("studentId");
			examId = params.get("examId");
			
			String session = DBM.selectedSessionDetails(schid,conn);
			
			//Getting classid And Section id WIth STudent
			StudentInfo stdinfo = DBM.studentDetailslistByAddNo(schid, stdid, conn);
			studentList.add(stdinfo);
			
			String sectionid =  stdinfo.getSectionid();
			String classid = stdinfo.getClassId();
		
			
			//getting exam and term ids
			if(examId.equalsIgnoreCase("All")) {
				StringBuilder exams = new StringBuilder();
				StringBuilder terms = new  StringBuilder();
				allTerms = DBM.selectedTermOfClass(classid, conn, session, schid);
				for (SelectItem term : allTerms) {
					allExams = dbexam.getAllExamsForClass(classid, "scholastic", schid, session,term.getValue().toString(), conn);
					for (ExamInfo exm : allExams) {	
						exams.append(Integer.toString(exm.getId()) + "','");
					}	
					terms.append(term.getValue().toString() + "','");
				}
				termid = terms.substring(0, terms.lastIndexOf("','"));
				examId = exams.substring(0, exams.lastIndexOf("','"));
			}else {
				termid =  dbexam.getTermIdFromExamId(examId,conn);
			}
			
			Boolean examSettings = DBM.checkExamSettingMade(classid, session, schid, conn);
			if(examSettings) {
				String pageName = dbexam.marksheetPageName(session, classid, conn);
				
				HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext()
						.getSession(false);
				
				ss.setAttribute("schid", schid);
				ss.setAttribute("portal", "App");
				ss.setAttribute("comeTestType", "all");
				ss.setAttribute("session", DatabaseMethods1.selectedSessionDetails(schid, conn));
				ss.setAttribute("examId", examId);
				ss.setAttribute("additional", "");
				ss.setAttribute("addTerm", "");
				ss.setAttribute("StudentList", studentList);
				ss.setAttribute("dateOfEntry", new Date());
				ss.setAttribute("termId", termid);
				ss.setAttribute("classid", classid);
				ss.setAttribute("sectionid",sectionid);

				 ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
				 HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
					String url = request.getRequestURL().toString();
					String baseURL = url.substring(0, url.length() - request.getRequestURI().length()) + request.getContextPath() + "/";
				 
				 
				 if (pageName.equals("no_page")) {
					 externalContext.redirect(baseURL+"printFinalMarksheet.xhtml");
				} else {
					 externalContext.redirect(baseURL+pageName+".xhtml");
				}
				 
//				 externalContext.redirect("http://localhost:8080/CBX-Code/printFinalMarksheet.xhtml");
				
			}
	}
	
	public void renderJson() throws IOException
	{
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		externalContext.setResponseContentType("application/json");
		externalContext.setResponseCharacterEncoding("UTF-8");
		facesContext.responseComplete();
	}

}
