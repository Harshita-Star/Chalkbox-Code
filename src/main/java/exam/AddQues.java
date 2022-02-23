package exam;

import java.io.InputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.file.UploadedFile;

import Json.DataBaseMeathodJson;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.SchoolInfoList;

@ViewScoped
@ManagedBean(name = "questionbean")
public class AddQues implements Serializable
{
	private static final long serialVersionUID = 1L;
	String question ="";
	String selectedClass, selectedSubject, examName, serialNumber, optionA="", optionB="", optionC="", optionD="",
			session, schid, solution="", answer;
	String quPath, op1Path, op2Path, op3Path, op4Path, solPath,examLang,limit,username,userType;
	int count ;
	boolean lang,ren=true,dis,seri,check;
	ArrayList<SelectItem> classList;
	ArrayList<SelectItem> examList;
	ArrayList<SelectItem> subjectList;
	DatabaseMethods1 obj = new DatabaseMethods1();
	DatabaseMethodExam dbExam = new DatabaseMethodExam();
	transient UploadedFile que, op1, op2, op3, op4, solu, file;
	StreamedContent fileForShow;

	public AddQues() {
		Connection conn = DataBaseConnection.javaConnection();
		HttpSession ss=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		username=(String) ss.getAttribute("username");
		userType=(String) ss.getAttribute("type");
		schid = obj.schoolId();
		session = obj.selectedSessionDetails(schid, conn);

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
		examList = dbExam.allExamsForClass(selectedClass, session, schid, "Active", conn);
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getLanguage() {
		Connection conn = DataBaseConnection.javaConnection();
		examLang = dbExam.getExamLaguage(examName,conn);
		if(examLang.equals("both")) {
			check = true;
			serialNumber = null;
			seri = true;
		}else {
			serialNumber = dbExam.getSerialNo(examName,examLang,selectedClass,selectedSubject,schid,conn);
			if(serialNumber==null) {
				check = false;
				serialNumber = "1";
				seri = true;
			}else {
				check = false;
				seri = true;
			}
		}
		lang = true;
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	public void submit() {
		Connection conn = DataBaseConnection.javaConnection();
		if (que != null && que.getSize() > 0) {
			String exten[] = que.getFileName().split("\\.");
			quPath = "Question-" + examName + "-" + serialNumber + "-" + schid + "-" + exten[exten.length - 1];
			obj.makeScanPath(que, quPath, conn);
		} else {
			quPath = "";
		}

		if (op1 != null && op1.getSize() > 0) {
			String exten[] = op1.getFileName().split("\\.");
			op1Path = "Option1-" + examName + "-" + serialNumber + "-" + schid + "-" + exten[exten.length - 1];
			obj.makeScanPath(op1, op1Path, conn);
		} else {
			op1Path = "";
		}
		if (op2 != null && op2.getSize() > 0) {
			String exten[] = op2.getFileName().split("\\.");
			op2Path = "Option2-" + examName + "-" + serialNumber + "-" + schid + "-" + exten[exten.length - 1];
			obj.makeScanPath(op2, op2Path, conn);
		} else {
			op2Path = "";
		}
		if (op3 != null && op3.getSize() > 0) {
			String exten[] = op3.getFileName().split("\\.");
			op3Path = "Option3-" + examName + "-" + serialNumber + "-" + schid + "-" + exten[exten.length - 1];
			obj.makeScanPath(op3, op3Path, conn);
		} else {
			op3Path = "";
		}
		if (op4 != null && op4.getSize() > 0) {
			String exten[] = op4.getFileName().split("\\.");
			op4Path = "Option4-" + examName + "-" + serialNumber + "-" + schid + "-" + exten[exten.length - 1];
			obj.makeScanPath(op4, op4Path, conn);
		} else {
			op4Path = "";
		}
		if (solu != null && solu.getSize() > 0) {
			String exten[] = solu.getFileName().split("\\.");
			solPath = "Solution-" + examName + "-" + serialNumber + "-" + schid + "-" + exten[exten.length - 1];
			obj.makeScanPath(solu, solPath, conn);
		} else {
			solPath = "";
		}
		if(examLang.equalsIgnoreCase("english")) {
			if(question.matches("^[a-zA-Z0-9\\s?_.!]+$") && optionA.matches("^[a-zA-Z0-9\\s?_.!]+$")&& optionB.matches("^[a-zA-Z0-9\\s?_.!]+$") && 
					optionC.matches("^[a-zA-Z0-9\\s?_.!]+$") && optionD.matches("^[a-zA-Z0-9\\s?_.!]+$")) {
				dis = true;
			}else {
				dis = false;
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("You can only add questions in english laguage."));
			}
		}else if(examLang.equalsIgnoreCase("hindi")) {
			if(question.matches("^[a-zA-Z0-9\\s?_.!]+$") && optionA.matches("^[a-zA-Z0-9\\s?_.!]+$") && optionB.matches("^[a-zA-Z0-9\\s?_.!]+$") && 
					optionC.matches("^[a-zA-Z0-9\\s?_.!]+$") && optionD.matches("^[a-zA-Z0-9\\s?_.!]+$")) {
				dis = false;
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("You can only add questions in hindi laguage."));
			}else {
				dis  = true;
			}
		}else if(examLang.equalsIgnoreCase("both")) {
			if(question.matches("^[a-zA-Z0-9\\s?_.!]+$") && optionA.matches("^[a-zA-Z0-9\\s?_.!]+$") && optionB.matches("^[a-zA-Z0-9\\s?_.!]+$") && 
					optionC.matches("^[a-zA-Z0-9\\s?_.!]+$") && optionD.matches("^[a-zA-Z0-9\\s?_.!]+$")) {
				dis = true;
				examLang = "english";
			}else {
				dis  = true;
				examLang = "hindi";
			}
		}

		if(dis == true) {
			if(check==true) {
				serialNumber = dbExam.getSerialNo(examName, examLang, selectedClass, selectedSubject, schid, conn);
			}
			int limit = dbExam.checkLimitForQuestions(examName,examLang,selectedClass,selectedSubject,schid,conn);
			if(limit<=30) {
				
				int status = dbExam.addQuestion(selectedClass, selectedSubject, examName, serialNumber, question, optionA,
						optionB, optionC, optionD, solution, answer, quPath, op1Path, op2Path, op3Path, op4Path, solPath, schid,
						examLang, conn);

				if (status > 0) {
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Question Added"));
					selectedClass = "";
					selectedSubject = "";
					examName = "";
					serialNumber = "";
					question = "";
					optionA = "";
					optionB = "";
					optionC = "";
					optionD = "";
					solution = "";
					answer = "";
					quPath = null;
					op1Path = null;
					op2Path = null;
					op3Path = null;
					op4Path = null;
					solPath = null;
					examLang  = "";
					lang = false;
					seri = false;
					ren = true;
					
				} else {
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Error occurs"));
				}
			}else {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("This Exam have 30 questions."));
			}
		}
		
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void downloadSample() {
		
		
		InputStream stream = FacesContext.getCurrentInstance().getExternalContext()
				.getResourceAsStream("question_sample.xls");

//		fileForShow = new DefaultStreamedContent(stream, "image/xls", "question_sample.xls");
		fileForShow = new DefaultStreamedContent().builder().contentType("image/xls").name("question_sample.xls").stream(()->stream).build();
	}

	public void uploadFile() throws Exception {
		Connection conn = DataBaseConnection.javaConnection();
		String filePath = "", fileName = "", savePath = "", msg = "";
		ArrayList<QuestionInfo> qList = new ArrayList<>();
		QuestionInfo q = new QuestionInfo();
		if (file != null && file.getSize() > 0) {
			String ext = file.getFileName().substring(file.getFileName().indexOf("."));
			if (ext.contains(".xls")) {
				filePath = file.getFileName();
				fileName = file.getFileName();
				obj.makeScanPath(file, filePath, conn); // Online
				SchoolInfoList ls = obj.fullSchoolInfo(conn);
				if (ls.getProjecttype().equals("online")) {
					String folderName = ls.getUploadpath();
					savePath = folderName;
				} else {
					ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext()
							.getContext();
					String realPath = ctx.getRealPath("/");
					savePath = realPath;
				}
				filePath = savePath + filePath;
				String filename = filePath;
				List sheetData = new ArrayList();
				XSSFWorkbook workbook = null;

				// for local testing

				filePath = "D:\\" + fileName;

				try {
					workbook = new XSSFWorkbook(filePath);
				} catch (Exception e) {
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage("Excel Version Must Be greater than 2007"));
				}
				if (workbook != null) {
					for (int x = 0; x < workbook.getNumberOfSheets(); x++) {
						XSSFSheet sheet = workbook.getSheetAt(x);
						if (sheet.getLastRowNum() != 0) {
							int rowCount = sheet.getLastRowNum();
							for (int i = 1; i <= rowCount; i++) {
								Row row = sheet.getRow(i);
								if(row.getCell(0)!=null) {
									if (row.getCell(14) != null && !row.getCell(14).toString().equals("")) {
										if(examLang.equals("both")) {
											if(row.getCell(14).toString().equalsIgnoreCase("english")||row.getCell(14).toString().equalsIgnoreCase("hindi")) {
												q = setDataOnInfo(row);
												if(dis==true) {
													qList.add(q);
												}else {
													msg = "First Specify type of question and options are in same laguage that specified for exam";
												}
											}else {
												msg = "Language for questions is Hindi or English";
											}
										}else if(row.getCell(14).toString().equalsIgnoreCase(examLang)) {
												q = setDataOnInfo(row);
												if(dis == true) {
													qList.add(q);
												}else {
													msg = "First Specify type of question and options are in same laguage that specified for exam";
												}
											}else {
												msg = "Language for questions is same as default language for exam..that is  "+examLang.toUpperCase();
											}
										
									}else {
										msg = "First Specify type of question in excel 'Hindi','English'";
									}
								}
							}
						}
					}
				}
				if (msg.equals("")) {
					ArrayList<QuestionInfo> hindiList = new ArrayList<>();
					ArrayList<QuestionInfo> englishList = new ArrayList<>();
					int hindiQSize = 0;
					int englishQSize = 0;
					int hi = 0;
					for(QuestionInfo x : qList) {
						if(x.getQuestionType().equalsIgnoreCase("english")) {
							englishList.add(x);
						}else if(x.getQuestionType().equalsIgnoreCase("hindi")) {
							hindiList.add(x);
						}
					}
					
					if(hindiList.isEmpty()==false) {
						int li = Integer.valueOf(dbExam.checkLimitForQuestions(examName, "hindi", selectedClass, selectedSubject, schid, conn));
						hindiQSize = li + hindiList.size();
						if(hindiQSize>30) {
							hi = hi +1;
						}
					}
					
					if(englishList.isEmpty()==false) {
						int li = Integer.valueOf(dbExam.checkLimitForQuestions(examName, "english", selectedClass, selectedSubject, schid, conn));
						englishQSize = li + englishList.size();
						if(englishQSize>30) {
							hi = hi + 2;
						}
					}
					
					if(qList.isEmpty()==false) {
						if(hi==0) {
							int status = dbExam.addQuestionfromExcel(selectedClass,selectedSubject,examName,qList,schid,conn);
							if(status>0) {
								FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
										"Questions Updated", "Questions updated"));
							}else {
								FacesContext.getCurrentInstance().addMessage(null,
										new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Ocuurs","Error Occurs" ));
							}
						}else if(hi==1) {
							FacesContext.getCurrentInstance().addMessage(null,
									new FacesMessage(FacesMessage.SEVERITY_ERROR, "Limit for this exam for Hindi language id 30 you entered "+hindiQSize,"Limit for this exam for hindi language id 30 you entered "+hindiQSize));
						}else if(hi==2) {
							FacesContext.getCurrentInstance().addMessage(null,
									new FacesMessage(FacesMessage.SEVERITY_ERROR, "Limit for this exam for English language id 30 you entered "+englishQSize,"Limit for this exam for English language id 30 you entered "+englishQSize));
						}else if(hi == 3) {
							FacesContext.getCurrentInstance().addMessage(null,
									new FacesMessage(FacesMessage.SEVERITY_ERROR, "Limit for this exam for English & Hindi language id 30 you entered for English "+englishQSize+" and for Hindi "+hindiQSize,"Limit for this exam for English & Hindi language id 30 you entered for English "+englishQSize+" and for Hindi "+hindiQSize));
						}
					}
				} else {
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, msg));
				}
			} else {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
						"Please upload file in only '.xls' format", "Please upload file in only '.xls' format"));
			}
		} else {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error Occurs", "Error Occurs"));
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	public QuestionInfo setDataOnInfo(Row row) {
		QuestionInfo q = new QuestionInfo();
		Connection conn = DataBaseConnection.javaConnection();
		if (row.getCell(14).toString().equalsIgnoreCase("english")) {
			if(row.getCell(1).toString().matches("^[a-zA-Z0-9\\s?_.!]+$") && row.getCell(2).toString().matches("^[a-zA-Z0-9\\s?_.!]+$") && row.getCell(3).toString().matches("^[a-zA-Z0-9\\s?_.!]+$") &&
					row.getCell(4).toString().matches("^[a-zA-Z0-9\\s?_.!]+$") && row.getCell(5).toString().matches("^[a-zA-Z0-9\\s?_.!]+$")) {
				dis = true;
			}else {
				dis = false;
			}
		}else if(row.getCell(14).toString().equalsIgnoreCase("hindi")) {
			if(row.getCell(1).toString().matches("^[a-zA-Z0-9\\s?_.!]+$") && row.getCell(2).toString().matches("^[a-zA-Z0-9\\s?_.!]+$") && row.getCell(3).toString().matches("^[a-zA-Z0-9\\s?_.!]+$") &&
					row.getCell(4).toString().matches("^[a-zA-Z0-9\\s?_.!]+$") && row.getCell(5).toString().matches("^[a-zA-Z0-9\\s?_.!]+$")) {
				dis = false;
			}else {
				dis = true;
			}
		}
		
		if(dis==true) {
			
			if (row.getCell(1) == null || row.getCell(1).toString().equals("")) {
				q.setQuestion("");
			}else {
				q.setQuestion(row.getCell(1).toString());
			}
			if (row.getCell(2) == null || row.getCell(2).toString().equals("")) {
				q.setOptionA("");
			}else {
				q.setOptionA(row.getCell(2).toString());
			}
			
			if (row.getCell(3) == null || row.getCell(3).toString().equals("")) {
				q.setOptionB("");
			}else {
				q.setOptionB(row.getCell(3).toString());
			}
			if (row.getCell(4) == null || row.getCell(4).toString().equals("")) {
				q.setOptionC("");
			}else {
				q.setOptionC(row.getCell(4).toString());
			}
			if (row.getCell(5) == null || row.getCell(5).toString().equals("")) {
				q.setOptionD("");
			}else {
				q.setOptionD(row.getCell(5).toString());
			}
			if (row.getCell(6) == null || row.getCell(6).toString().equals("")) {
				q.setAnswer("");
			}else {
				q.setAnswer(String.format("%.0f",Double.valueOf(row.getCell(6).toString())));
			}
			if (row.getCell(7) == null || row.getCell(7).toString().equals("")) {
				q.setSolution("");
			}else {
				q.setSolution(row.getCell(7).toString());
			}
			if (row.getCell(8) == null || row.getCell(8).toString().equals("")) {
				q.setQuestionImage("");
			}else {
				q.setQuestionImage(row.getCell(8).toString());
			}
			if (row.getCell(9) == null || row.getCell(9).toString().equals("")) {
				q.setOptionBImage("");
			}else {
				q.setOptionAImage(row.getCell(9).toString());
			}
			if (row.getCell(10) == null || row.getCell(10).toString().equals("")) {
				q.setOptionBImage("");
			}else {
				q.setOptionBImage(row.getCell(10).toString());
			}
			if (row.getCell(11) == null || row.getCell(11).toString().equals("")) {
				q.setOptionCImage("");
			}else {
				q.setOptionCImage(row.getCell(11).toString());
			}
			if (row.getCell(12) == null || row.getCell(12).toString().equals("")) {
				q.setOptionDImage("");
			}else {
				q.setOptionDImage(row.getCell(12).toString());
			}
			if (row.getCell(13) == null || row.getCell(13).toString().equals("")) {
				q.setSolutionImage("");
			}else {
				q.setSolutionImage(row.getCell(13).toString());
			}
			q.setQuestionType(row.getCell(14).toString());
		}
		
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return q;
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

	public boolean isLang() {
		return lang;
	}

	public void setLang(boolean lang) {
		this.lang = lang;
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

	public UploadedFile getFile() {
		return file;
	}

	public void setFile(UploadedFile file) {
		this.file = file;
	}

	public StreamedContent getFileForShow() {
		return fileForShow;
	}

	public void setFileForShow(StreamedContent fileForShow) {
		this.fileForShow = fileForShow;
	}

	public String getExamLang() {
		return examLang;
	}

	public void setExamLang(String examLang) {
		this.examLang = examLang;
	}

	public boolean isRen() {
		return ren;
	}

	public void setRen(boolean ren) {
		this.ren = ren;
	}

	public boolean isDis() {
		return dis;
	}

	public void setDis(boolean dis) {
		this.dis = dis;
	}

	public boolean isSeri() {
		return seri;
	}

	public void setSeri(boolean seri) {
		this.seri = seri;
	}


}
