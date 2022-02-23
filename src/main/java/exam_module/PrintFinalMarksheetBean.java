package exam_module;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.Charsets;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import exam.DatabaseMethodExam;
import schooldata.DataBaseConnection;
import schooldata.DatabaseMethods1;
import schooldata.ExamSettingInfo;
import schooldata.ExtraFieldInfo;
import schooldata.SchoolInfoList;
import schooldata.StudentInfo;
import schooldata.SubjectGroupInfo;
import schooldata.SubjectInfo;
import schooldata.TermInfo;

@ManagedBean(name = "printFinalMarksheet")
@ViewScoped
public class PrintFinalMarksheetBean implements Serializable {
	private static final long serialVersionUID = 1L;
	StudentInfo studentList;
	ArrayList<StudentInfo> barlist = new ArrayList<>();
	ArrayList<StudentInfo> listOfStudent;
	String header1 = "", header2 = "", header3 = "", termTotal = "", termTotalName = "", termGrade = "",
			termGradeName = "", finalTotal = "", finalTotalName = "", finalGrade = "", finalGradeName = "",
			finalPercent = "", finalPercentName = "", examMarks = "", rowTotal = "", rowTotalName = "", rowPercent = "",
			rowPercentName = "", rowGrade = "", rowGradeName = "", headerImagePath, schid, dateEntry, termId = "",
			comeTestType = "all", coscholTerm, otherTerm, coscholHeader, otherHeader, addHeader, className, session,
			attendColumn = "", addNumber = "", reflectMarks = "", extraValueFormat = "", disciplineTerm,
			disciplineHeader, seperate_disci, termNameCoschol = "", termNameOther = "", termNameDisci = "";
	boolean showExtraFieldTable = false, showOtherFieldTable, showUp = false, showBottom = false, showRow = false,
			showColumn = false, showHeader = false, showStudentImage = false, showCoschol_sub = false,
			showOther_sub = false, showAdditional_sub = false, showGradeScale = false, showGradeScaleCoschol = false,
			showDisci_sub = false;
	ArrayList<TermInfo> termList, termListForAdditional, termListForCoschol, termListForDiscipline, termListForOther;
	ArrayList<ExtraFieldInfo> gradeScaleListForCoSchol, gradeScaleList = new ArrayList<>();
	ArrayList<ExtraFieldInfo> extraFieldList, tempExtraFieldList = new ArrayList<>(), signList = new ArrayList<>(),
			otherValueList = new ArrayList<>();

	ArrayList<String> scholasticColumnsList = new ArrayList<>(), coscholasticColumnsList = new ArrayList<>(),
			otherColumnsList = new ArrayList<>(), disciplineColumnsList = new ArrayList<>(),
			tempGradeFieldList = new ArrayList<>(), tempGradeFieldListForCoschol = new ArrayList<>();;;
	SchoolInfoList info;
	String purpose = "marksheet", gradeScaleFormat = "";
	int subjectListSize;
	ExamSettingInfo examSetting = new ExamSettingInfo();
	ArrayList<LifeSkillsInfo> lifeSkillsList;

	DataBaseMethodsExam de = new DataBaseMethodsExam();
	DataBaseMethodsBLMExam deBLM = new DataBaseMethodsBLMExam();
	DatabaseMethods1 obj = new DatabaseMethods1();
	String techerSignLabel, principalSignLabel, ptExamName;
	ArrayList<SubjectInfo> markListOtherSubject, markListAddSub;
	boolean check = false, showNote = false, showRemark = false, showResult = false, showPromotion = false,
			showSubExamRow = false, showSubExamRowAdd = false;
	ArrayList<String> addColumnsList = new ArrayList<>(), uniFooterList = new ArrayList<>(),
			footerColumnList = new ArrayList<>();
	transient StreamedContent file;
	transient StreamedContent file2;
	String classid = "", sectionid = "", subjectId, mandatorySub, optionalSub, examId, addExam, addTerm;
	String position;
	String fullNameAllow;
	ArrayList<ExamInfo> allExamNames = new ArrayList<>();
	boolean showExmName = false;
	String portal = "";

	public PrintFinalMarksheetBean() {
		Connection conn = DataBaseConnection.javaConnection();
			
			HttpSession ss = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);

			session = (String) ss.getAttribute("session");
			termId = (String) ss.getAttribute("termId");
			classid = (String) ss.getAttribute("classid");
			sectionid = (String) ss.getAttribute("sectionid");
			comeTestType = (String) ss.getAttribute("comeTestType");
			examId = (String) ss.getAttribute("examId");
			addExam = (String) ss.getAttribute("additional");
			addTerm = (String) ss.getAttribute("addTerm");
			listOfStudent = (ArrayList<StudentInfo>) ss.getAttribute("StudentList");
			Date date = (Date) ss.getAttribute("dateOfEntry");
			dateEntry = new SimpleDateFormat("dd-MM-yyyy").format(date);
			portal = (String) ss.getAttribute("portal");
			
			if(portal != ""){
				schid = (String) ss.getAttribute("schid");
				info = obj.fullSchoolInfo(schid, conn);
				
			}else {
				info = obj.fullSchoolInfo(conn);
				schid = obj.schoolId();
			}
		
	
		if (schid.equals("298") || schid.equals("299")) {
			lifeSkillsList = de.lifeSkillsInfoList(conn);
		}

		checkExamSetting(classid, conn);

		// TERM LIST WITHOUT EXAM SELECTION.

		// termList=deBLM.termListByClassTermId(termId,classid,session,"scholastic",conn,comeTestType,termTotal,termTotalName,termGrade,termGradeName,finalTotal,finalTotalName,finalGrade,finalGradeName,finalPercent,finalPercentName,examMarks,ptExamName,reflectMarks);
		// termListForAdditional=deBLM.termListByClassTermId(termId,classid,session,"additional",conn,comeTestType,termTotal,termTotalName,termGrade,termGradeName,finalTotal,finalTotalName,finalGrade,finalGradeName,finalPercent,finalPercentName,examMarks,ptExamName,reflectMarks);

		// TERM LIST WIT EXAM SELECTION (OVERLOADED METHODS ->termListByClassTermId
		// ,->examListByClassidForScholastic).
		termList = deBLM.termListByClassTermId(termId, classid, session, "scholastic", conn, comeTestType, termTotal,
				termTotalName, termGrade, termGradeName, finalTotal, finalTotalName, finalGrade, finalGradeName,
				finalPercent, finalPercentName, examMarks, ptExamName, reflectMarks, examId , schid);
		
		termListForAdditional = deBLM.termListByClassTermId(termId, classid, session, "additional", conn, comeTestType,
				termTotal, termTotalName, termGrade, termGradeName, finalTotal, finalTotalName, finalGrade,
				finalGradeName, finalPercent, finalPercentName, examMarks, ptExamName, reflectMarks, addExam , schid);

		
		// TERM LIST WITHOUT EXAM SELECTION.

		termListForCoschol = deBLM.termListByClassTermId(termId, classid, session, "coscholastic", conn, comeTestType,
				termTotal, termTotalName, termGrade, termGradeName, finalTotal, finalTotalName, finalGrade,
				finalGradeName, finalPercent, finalPercentName, "", ptExamName, reflectMarks , schid);
		

		
		termListForDiscipline = deBLM.termListByClassTermId(termId, classid, session, "coscholastic", conn,
				comeTestType, termTotal, termTotalName, termGrade, termGradeName, finalTotal, finalTotalName,
				finalGrade, finalGradeName, finalPercent, finalPercentName, "", ptExamName, reflectMarks,schid);
	
		termListForOther = deBLM.termListByClassTermId(termId, classid, session, "other", conn, comeTestType, termTotal,
				termTotalName, termGrade, termGradeName, finalTotal, finalTotalName, finalGrade, finalGradeName,
				finalPercent, finalPercentName, "", ptExamName, reflectMarks ,schid);
		

		fillExamSettingForExtraField(classid, conn);

		for (StudentInfo allInfo : listOfStudent) {
			allInfo.setSession(session);

			addNumber = String.valueOf(allInfo.getAddNumber());

			scholasticMarks(allInfo, classid, sectionid, conn);
			if (showCoschol_sub == true)
				coscholasticMarks(allInfo, classid, sectionid, conn, schid);
			if (showDisci_sub == true && seperate_disci.equals("yes"))
				disciplineMarks(allInfo, classid, sectionid, conn, schid);
			if (showOther_sub == true)
				otherSubjectMarks(allInfo, classid, sectionid, conn, schid);
			if (showAdditional_sub == true)
				additionalMarks(allInfo, classid, sectionid, conn, schid);
			if (showExtraFieldTable == true)
				extraField(allInfo, classid, sectionid, conn, schid);
			if (showOtherFieldTable = true) {
				otherFieldSetting();
				otherValuesField(allInfo, classid, sectionid, conn, schid);
			}

			/*
			 * if(schid.equals("5")) {
			 * result=allInfo.getExamResult().replace("Compartment In ",
			 * "Conditionally promoted in "); allInfo.setExamResult(result); }
			 */
		}
		
//		System.out.println(listOfStudent.get(0).getMarkList().size());
		
		// // System.out.println(examId);
		// // System.out.println(fullNameAllow);
		ArrayList<ExamInfo> examNames = new DatabaseMethodExam().getPeridicExamDetails(classid, session, schid, conn);
		examNames = new DatabaseMethodExam().getExamNamesForClass(examId, conn, examNames);
		if (fullNameAllow.equalsIgnoreCase("yes")) {
			allExamNames = new ArrayList<>();
			ArrayList<ExamInfo> ExamNames = new DatabaseMethodExam().getALlFullNames(schid, session, conn);
			if (ExamNames.size() > 0) {
				for (ExamInfo a : ExamNames) {
					String[] classIds = a.getClassid().substring(1, a.getClassid().length() - 1).trim().split("-");
					for (int i = 0; i < classIds.length; i++) {
						if (classid.equalsIgnoreCase(classIds[i].trim())) {
							// // System.out.println("in if condition");
							String[] fname = a.getFullName().substring(1, a.getFullName().length() - 1).trim()
									.split("&&");
							for (int j = 0; j < fname.length; j++) {
								for (ExamInfo em : examNames) {
									if (em.getExamName().equalsIgnoreCase(fname[j].split(":")[0].trim())) {
										ExamInfo exmanem = new ExamInfo();
										exmanem.setExamid(fname[j].split(":")[0].trim());
										exmanem.setExamName(fname[j].split(":")[1].trim());
										allExamNames.add(exmanem);
									}
								}
							}
						}
					}
				}
			}
		}

		if (allExamNames.size() > 0) {
			showExmName = true;
		}

		// makeBarChart();

		try {
			conn.close();
		} catch (SQLException e) {

			e.printStackTrace();
		}
	}

//	private void makeBarChart() {
//		for (StudentInfo student : listOfStudent) {
//			StudentInfo s = new StudentInfo();
//			s.setStudentName(student.getFname());
//			ArrayList<SubjectInfo> barMarks = new ArrayList<>();
//			for (SubjectInfo marks : student.getMarkList()) {
//				if (marks.getMarksMap().get("-9termfinalMarktotal-obtained") != null) {
//					Double tot = Double.valueOf(marks.getMarksMap().get("-9termfinalMarktotal-obtained").split("/")[0]);
//					Double finalTot = Double
//							.valueOf(marks.getMarksMap().get("-9termfinalMarktotal-obtained").split("/")[1]);
//					Double percentage = tot * 100 / finalTot;
//					SubjectInfo st = new SubjectInfo();
//					st.setSubjectName(marks.getSubjectName());
//					st.setMarks(percentage);
//					barMarks.add(st);
//				}
//			}
//			s.setMarkListAddSub(barMarks);
//			barlist.add(s);
//		}
//
//		createBarModel();
//	}
//	
//	private void createBarModel() {
//		for(StudentInfo st : listOfStudent) {
//			BarChartModel barmodel = new BarChartModel();
//			barmodel = initBarChart(st);
//			barmodel.setTitle("Percentile graph");
//			barmodel.setLegendPosition("ne");
//			barmodel.setAnimate(true);
//			barmodel.setExtender("barext");
//			barmodel.setZoom(true);
//			barmodel.setShowPointLabels(true);
//			//barmodel.setLegendPlacement(LegendPlacement.OUTSIDE);
//			Axis xAxis = barmodel.getAxis(AxisType.X);
//			xAxis.setLabel("Subjects");
//			Axis yAxis = barmodel.getAxis(AxisType.Y);
//			yAxis.setLabel("Percentage");
//			yAxis.setMin(10);
//			yAxis.setMax(100);
//			yAxis.setTickFormat("%d");
//			st.setBarmodel(barmodel);
//		}
//	}
//	
//	
//	private BarChartModel initBarChart(StudentInfo student) {
//		BarChartModel model = new BarChartModel();
//		ChartSeries total = new ChartSeries();
//		total.setLabel("subjects");
//		for(StudentInfo ss:barlist)
//		{
//			if(ss.getStudentName().equalsIgnoreCase(student.getFname())) {
//				for(SubjectInfo cc : ss.getMarkListAddSub()) {
//					total.set(cc.getSubjectName(), cc.getMarks());
//				}
//			}
//			
//		}
//		model.addSeries(total);
//		
//		return model;
//	}

	public void extraField(StudentInfo allInfo, String classid, String sectionid, Connection conn, String schid) {
		String termId = termListForCoschol.get(termListForCoschol.size() - 1).getTermId();
		if (extraValueFormat.equals("column")) {
			extraFieldList = deBLM.extraFieldValueForStudentColumnFormat(allInfo.getAddNumber(), session, sectionid,
					termId, termListForCoschol, attendColumn, conn);
			allInfo.setExtraFieldList(extraFieldList);
		} else {
			ArrayList<ExtraFieldInfo> tempList = new ArrayList<>();
			tempList = deBLM.extraFieldValueForStudentRowFormat(allInfo.getAddNumber(), session, sectionid, termId,
					termListForCoschol, conn, tempExtraFieldList);
			allInfo.setExtraFieldList(tempList);
		}
	}

	public void otherValuesField(StudentInfo allInfo, String classid, String sectionid, Connection conn, String schid) {
		String termId = termListForCoschol.get(termListForCoschol.size() - 1).getTermId();
		for (ExtraFieldInfo ll : otherValueList) {
			if (ll.getsNo().equals("NOTE")) {
				if (ll.getSignType().equals("manual"))
					ll.setExtraValue(ll.getLabel());
				else
					ll.setExtraValue("");
			}
			if (ll.getsNo().equals("RANK")) {
				if (ll.getSignType().equals("manual"))
					ll.setExtraValue(ll.getLabel());
				else
					ll.setExtraValue(
							deBLM.rankOfStudent(classid, sectionid, allInfo.getAddNumber(), schid, conn, session));
			}
			if (ll.getsNo().equals("PROMOTION")) {
				if (ll.getSignType().equals("manual"))
					ll.setExtraValue(ll.getLabel());
				else
					ll.setExtraValue(deBLM.promotedClassOfStudent(classid, sectionid, allInfo.getAddNumber(), schid,
							conn, session));
			}
			if (ll.getsNo().equals("RESULT")) {
				if (ll.getSignType().equals("manual"))
					ll.setExtraValue(ll.getLabel());
				else
					ll.setExtraValue(allInfo.getExamResult());
			}
			if (ll.getsNo().equals("REMARK")) {
				if (ll.getSignType().equals("manual")) {
					ll.setExtraValue(ll.getLabel());

				} else {
					// //
					// System.out.println("remark-----"+deBLM.remarkOfStudent(termId,allInfo.getAddNumber(),
					// schid, conn, session));
					ll.setExtraValue(deBLM.remarkOfStudent(termId, allInfo.getAddNumber(), schid, conn, session));

				}
			}
			if (ll.getsNo().contains("ATTENDANCE")) {

				if (ll.getSignType().equals("manual"))
					ll.setExtraValue(ll.getLabel());
				else

					// //
					// System.out.println("attendance-----"+deBLM.attendanceOfStudent(termId,allInfo.getAddNumber(),
					// schid, conn, session));
					ll.setExtraValue(deBLM.attendanceOfStudent(termId, allInfo.getAddNumber(), schid, conn, session));
			}
		}
		allInfo.setOtherValueList(otherValueList);
	}

	public void disciplineMarks(StudentInfo allInfo, String classid, String sectionid, Connection conn, String schid) {
		disciplineColumnsList.clear();
		for (TermInfo term : termListForDiscipline) {
			disciplineColumnsList.add(String.valueOf("sub" + term.getTermId()));
			disciplineColumnsList.add("grade" + term.getTermId());
		}
		allInfo.setDisciplineColumnsList(disciplineColumnsList);
		allInfo.setMarkListDiscipline(deBLM.disciplineSubjectList(classid, session, conn));
		deBLM.fillMarksForCoscholasticSubject(/* classid, */sectionid, allInfo.getMarkListDiscipline(), addNumber,
				session, termListForCoschol, "coscholastic", conn, allInfo, purpose);
	}

	public void coscholasticMarks(StudentInfo allInfo, String classid, String sectionid, Connection conn,
			String schid) {

		coscholasticColumnsList.clear();
		for (TermInfo term : termListForCoschol) {
			coscholasticColumnsList.add(String.valueOf("sub" + term.getTermId()));
			coscholasticColumnsList.add("grade" + term.getTermId());
		}
		allInfo.setCoscholasticColumnsList(coscholasticColumnsList);
		allInfo.setMarkListCoscholastic(createSubjectList(classid, sectionid, "coscholastic", conn));
		deBLM.fillMarksForCoscholasticSubject(/* classid, */sectionid, allInfo.getMarkListCoscholastic(), addNumber,
				session, termListForCoschol, "coscholastic", conn, allInfo, purpose);
	}

	public ArrayList<SubjectInfo> createSubjectList(String classid, String sectionid, String subjectType,
			Connection conn) {
		ArrayList<SubjectGroupInfo> subGroup = de.subjectListForStudentWithSubjectGroup(classid, session, addNumber,
				sectionid, subjectType, conn, rowTotal, rowTotalName, rowPercent, rowPercentName, rowGrade,
				rowGradeName, seperate_disci,schid);

		if (position.equalsIgnoreCase("down")) {
			subGroup = de.subjectListForStudentWithSubjectGroupForBottomSubGroup(classid, session, addNumber, sectionid,
					subjectType, conn, rowTotal, rowTotalName, rowPercent, rowPercentName, rowGrade, rowGradeName,
					seperate_disci,schid);

		}

		if (subGroup != null && subGroup.size() > 0)
			subjectListSize = subGroup.get(0).getListSize();
		ArrayList<SubjectInfo> tempList = new ArrayList<>();
		for (SubjectGroupInfo sub : subGroup) {
			if (sub.getSubjectList().size() == 1) {
				SubjectInfo ss1 = sub.getSubjectList().get(0);
				ss1.setHeader(true);
				ss1.setDescription("single");
				tempList.add(ss1);
			} else {
				SubjectInfo subject = new SubjectInfo();
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

	public void scholasticMarks(StudentInfo allInfo, String classid, String sectionid, Connection conn) {
		scholasticColumnsList.clear();
		int termSize = 0, ptFlag = 0, count = 0;
		for (TermInfo term : termList) {
			ptFlag = 0;
			if (Integer.parseInt(term.getTermId()) > 0)
				termSize++;
			for (ExamInfo exam : term.getExamInfoList()) {
				if (exam.getInclude().equals("Yes")) {
					if (exam.getSubExamList() != null) {
						if (exam.getExamTaken().equals("other")) {
							for (SubExamInfo subExam : exam.getSubExamList()) {
								if (!subExam.getSubExamUpperCase().trim().equals(""))
									count++;
								scholasticColumnsList.add(term.getTermId() + "term" + subExam.getMainExamId()
										+ "total-obtained" + subExam.getSubExamName());
							}
						} else {
							if (comeTestType.equals("periodic")) {
								for (SubExamInfo subExam : exam.getSubExamList()) {
									if (!subExam.getSubExamUpperCase().trim().equals(""))
										count++;
									scholasticColumnsList.add(term.getTermId() + "term" + subExam.getMainExamId()
											+ "total-obtained" + subExam.getSubExamName());
								}
							} else if (ptFlag == 0) {
								scholasticColumnsList
										.add(term.getTermId() + "term" + "P.T." + "total-obtained" + ptExamName);
								ptFlag = 1;
							}
						}
					} else
						scholasticColumnsList.add(term.getTermId() + "term" + "" + "total-obtained" + "");
				}
			}
		}
		if (count != 0)
			showSubExamRow = true;

		allInfo.setScholasticColumnsList(scholasticColumnsList);
		allInfo.setMarkList(createSubjectList(classid, sectionid, "scholastic", conn));

//		ArrayList<SubjectInfo> tempList=new ArrayList<>();
//		for(SubjectInfo info:allInfo.getMarkList())
//		{
//			if(subjectId.contains(info.getSubjectId()) || info.getSubjectId().equalsIgnoreCase("TOTAL") || info.getSubjectId().equalsIgnoreCase("PERCENTAGE") || info.getSubjectId().equalsIgnoreCase("grade"))
//			{
//				tempList.add(info);
//				if(!(info.getSubjectId().equalsIgnoreCase("TOTAL") || info.getSubjectId().equalsIgnoreCase("PERCENTAGE") || info.getSubjectId().equalsIgnoreCase("grade")))
//				{
//					subjectListSize++;
//				}
//			}
//		}
//		
//		allInfo.setMarkList(tempList);

		de.fillStudentMarks(sectionid, addNumber, allInfo.getMarkList(), session, termList, conn, allInfo,
				scholasticColumnsList, termSize, "scholastic", subjectListSize, examSetting, purpose, comeTestType,schid);
		// System.out.println(scholasticColumnsList);
		for (SubjectInfo subject : allInfo.getMarkList()) {
			// System.out.println(subject.getMarksMap());
			// // System.out.println("subject name------>"+subject.getSubjectName());
		}
		if (schid.equals("243")) {
			shemfordPassingMethod(allInfo, classid);
		}
		/*
		 * if(((schid.equals("295") && (Integer.parseInt(allInfo.getClassId())>2)))||
		 * (schid.equals("226") && ((Integer.parseInt(allInfo.getClassId())>1) &&
		 * (Integer.parseInt(allInfo.getClassId())<=3)))) { double obtain=0,max=0;
		 * for(TermInfo term:allInfo.getTermList()) { for(SubjectInfo
		 * info:allInfo.getMarkList()) { if(info.getSubjectId().equals("TOTAL") ) {
		 * if(info.getMarksMap().get(term.getTermId()+"termmainMarktotal-obtained")!=
		 * null) { String
		 * arr[]=info.getMarksMap().get(term.getTermId()+"termmainMarktotal-obtained").
		 * split(" / "); obtain+=Double.parseDouble(arr[0]);
		 * max+=Double.parseDouble(arr[1]); } } } } //yhi pr final total b set ho jaega
		 * allInfo.setPercentage(String.format("%.2f",obtain*100/max)); }
		 */
	}

	private void shemfordPassingMethod(StudentInfo allInfo, String classId) {
		if ((Integer.parseInt(classId) >= 16 && allInfo.getGraceCounter() < 3)
				|| (Integer.parseInt(classId) == 9 || Integer.parseInt(classId) == 10)) {
			double obtainForResult = 0, maxForResult = 0;
			SubjectInfo info1 = allInfo.getMarkList().get(allInfo.getMarkList().size() - 1);
			Map<String, String> map = info1.getMarksMap();
			if (map.get("obtainForResult") != null) {
				obtainForResult = Double.parseDouble(map.get("obtainForResult"));
			}
			if (map.get("maxForResult") != null) {
				maxForResult = Double.parseDouble(map.get("maxForResult"));
			}
			int per = 0, compartCounter = 0;
			String result = "";
			per = (int) (obtainForResult * 100 / maxForResult);
			if (per >= 33) {
				int flag1 = 0;
				for (SubjectInfo info : allInfo.getMarkList()) {
					map = info.getMarksMap();
					obtainForResult = Double.parseDouble(map.get("obtainForResult"));
					maxForResult = Double.parseDouble(map.get("maxForResult"));
					if (obtainForResult > 0 && maxForResult > 0) {
						per = 0;
						per = (int) (obtainForResult * 100 / maxForResult);
						if (per < 33) {
							if (!result.contains(info.getSubjectName())) {
								if (!(info.getSubjectName().contains("HINDI")
										|| info.getSubjectName().contains("ENGLISH")) && flag1 == 0) {
									flag1 = 1;
								} else {
									if (compartCounter == 0)
										result += "Compartment In ";
									result += info.getSubjectName() + " , ";
									compartCounter++;
								}
							}
						}
					}
				}
				if (compartCounter == 0) {
					result = "PASSED";
				} else if (compartCounter >= 3) {
					result = "FAILED";
				} else {
					result = result.substring(0, result.lastIndexOf(" , "));
				}
				allInfo.setExamResult(result);
			}
		}
	}

	public void checkExamSetting(String classid, Connection conn) {
		String savePath = "";
		if (info.getProjecttype().equals("online")) {
			String folderName = info.getDownloadpath();
			savePath = folderName;
		}
		
		if(portal != "") {
			examSetting = de.examSettingDetailWithSchid(schid,classid, conn);
		}else {
			examSetting = de.examSettingDetail(classid, conn);
		}
		
		if (examSetting.getOtherField().equalsIgnoreCase("yes"))
			showOtherFieldTable = true;
		else
			showOtherFieldTable = false;

		subjectId = examSetting.getSubjectId();
		mandatorySub = examSetting.getMandatorySub();
		optionalSub = examSetting.getOptionalSub();
		ptExamName = examSetting.getExamName();
		header1 = examSetting.getHeader1();
		header2 = examSetting.getHeader2();
		header3 = examSetting.getHeader3();
		showHeader = examSetting.isSchool_header();
		showStudentImage = examSetting.isStd_image();
		showCoschol_sub = examSetting.isCoschol_sub();
		showAdditional_sub = examSetting.isAdditional_sub();
		showOther_sub = examSetting.isOther_sub();
		termTotal = examSetting.getTermTotal();
		termTotalName = examSetting.getTermTotalName();
		termGrade = examSetting.getTermGrade();
		termGradeName = examSetting.getTermGradeName();
		finalTotal = examSetting.getFinalTotal();
		finalTotalName = examSetting.getFinalTotalName();
		finalGrade = examSetting.getFinalGrade();
		finalGradeName = examSetting.getFinalGradeName();
		finalPercent = examSetting.getFinalPercent();
		finalPercentName = examSetting.getFinalPercentName();
		rowTotal = examSetting.getRowTotal();
		rowTotalName = examSetting.getRowTotalName();
		rowPercent = examSetting.getRowPercent();
		rowPercentName = examSetting.getRowPercentName();
		rowGrade = examSetting.getRowGrade();
		rowGradeName = examSetting.getRowGradeName();
		examMarks = examSetting.getExamMarks();
		coscholTerm = examSetting.getCoscholTerm();
		coscholHeader = examSetting.getCoscholHeader();
		otherTerm = examSetting.getOtherTerm();
		otherHeader = examSetting.getOtherHeader();
		addHeader = examSetting.getAddHeader();
		seperate_disci = examSetting.getSepearte_disci();
		showDisci_sub = examSetting.isDisci_sub();
		disciplineTerm = examSetting.getDisci_term();
		disciplineHeader = examSetting.getDisci_header();
		showGradeScale = examSetting.isShowGradeScale();
		showGradeScaleCoschol = examSetting.isShowGradeScaleCoschol();
		termNameCoschol = examSetting.getTermNameCoschol();
		termNameOther = examSetting.getTermNameOther();
		termNameDisci = examSetting.getTermNameDisci();
		gradeScaleFormat = examSetting.getGradeScaleFormat();
		reflectMarks = examSetting.getReflectMark();
		position = examSetting.getPosition();
		fullNameAllow = examSetting.getFullExamNameAllow();

		ArrayList<String> list = examSetting.getSignList();
		String classTeacherSign = list.get(0);
		list.remove(0);

		classTeacherSign = classTeacherSign.substring(1, classTeacherSign.length() - 1); // remove curly brackets
		String[] keyValuePairs = classTeacherSign.split(","); // split the string to create key-value pairs
		Map<String, String> map = new HashMap<>();

		for (String pair : keyValuePairs) // iterate over the pairs
		{
			String[] entry = pair.split("="); // split the pairs to get key and value
			if (entry.length == 2)
				map.put(entry[0].trim(), entry[1].trim()); // add them to the hashmap and trim whitespaces
			else
				map.put(entry[0].trim(), "");
		}

		if (map.containsKey(sectionid)) {
			String sign = map.get(sectionid);
			list.add(sign);
		}

		String signArr[] = null;
		for (String sign1 : list) {
			if (sign1 != null && !sign1.trim().equals("")) {
				// System.out.println(sign1);
				if (sign1.contains(","))
					signArr = sign1.split(",");
				else if (sign1.contains("$"))
					signArr = sign1.split("\\$");
				ExtraFieldInfo sign = new ExtraFieldInfo();

				sign.setRollNo(signArr[0]);
				if (signArr[0].equals(""))
					sign.setRollNo("5");
				sign.setSignName(signArr[1]);
				sign.setSignType(signArr[2]);
				sign.setSignImageName(savePath + signArr[3]);
				if (signArr.length == 5)
					sign.setAlignment(signArr[4]);
				else
					sign.setAlignment("left");
				signList.add(sign);
			}
		}

		if (signList.size() > 0) {
			Collections.sort(signList, new RankComparator());

		}

		if (showGradeScale == true || showGradeScaleCoschol == true) {
			if (gradeScaleFormat.equals("vertical")) {
				gradeScaleList = deBLM.gradeScaleListVertical();
				gradeScaleListForCoSchol = deBLM.gradeScaleListForCoscholasticVertical();
			} else {
				tempGradeFieldList = deBLM.gradeScaleHeaderList("Scholastic");
				tempGradeFieldListForCoschol = deBLM.gradeScaleHeaderList("Coscholastic");
				gradeScaleList = deBLM.gradeScaleListHorizontal();
				if (schid.equals("216")) {
					gradeScaleListForCoSchol = deBLM.gradeScaleListForCoscholasticVertical();
				} else {
					gradeScaleListForCoSchol = deBLM.gradeScaleListForCoscholasticHorizontal();
				}

			}

		}
		if (showHeader == true) {
			headerImagePath = savePath + info.getMarksheetHeader();
		}
		// System.out.println(showGradeScaleCoschol);
	}

	public void otherFieldSetting() {
		otherValueList = new ArrayList<>();
		String otherValueArr[] = examSetting.getOtherValues().split("%@%");
		for (String value : otherValueArr) {
			String arr[] = value.split("-");
			if (arr.length > 1) {
				ExtraFieldInfo ll = new ExtraFieldInfo();
				ll.setsNo(arr[0]);
				ll.setRollNo(arr[1]);
				ll.setClassName(arr[2]);
				ll.setSignType(arr[3]);
				if (arr.length == 5)
					ll.setLabel(arr[4]);
				else
					ll.setLabel("");
				otherValueList.add(ll);
			}
		}

		if (otherValueList.size() > 0) {
			Collections.sort(otherValueList, new RankComparator());

		}

	}

	public void fillExamSettingForExtraField(String classid, Connection conn) {

		if (coscholTerm.equals("last")) {
			TermInfo term = termListForCoschol.get(termListForCoschol.size() - 1);
			termListForCoschol.clear();
			termListForCoschol.add(term);
		}

		if (otherTerm.equals("last")) {
			TermInfo term = termListForOther.get(termListForOther.size() - 1);
			termListForOther.clear();
			termListForOther.add(term);
		}

		if (disciplineTerm.equals("last")) {
			TermInfo term = termListForDiscipline.get(termListForDiscipline.size() - 1);
			termListForDiscipline.clear();
			termListForDiscipline.add(term);
		}

		if (examSetting.getExtraField().equals("yes")) {
			if (examSetting.getExtraFieldPlace().equals("top")) {
				showBottom = false;
				showUp = true;
			} else {
				showBottom = true;
				showUp = false;
			}
			extraValueFormat = examSetting.getExtraFormat();
			if (extraValueFormat.equals("column")) {
				showRow = false;
				showColumn = true;
			} else {
				showRow = true;
				showColumn = false;
			}

			String extraValues = examSetting.getExtraValues();
			String mainArr[] = extraValues.split(",");
			for (String arr : mainArr) {
				String subArr[] = arr.split("-");
				ExtraFieldInfo info = new ExtraFieldInfo();
				info.setLabel(subArr[0]);
				info.setRollNo(subArr[1]);
				if (!subArr[0].contains("Attendance")) {
					tempExtraFieldList.add(info);
				} else {
					info.setAlignment(subArr[2]);
					if (info.getAlignment().equals("all") && extraValueFormat.equals("column")) {
						attendColumn = "all";
						for (TermInfo term : termListForCoschol) {
							info = new ExtraFieldInfo();
							info.setLabel(subArr[0]);
							info.setRollNo(subArr[1]);
							info.setLabel(info.getLabel() + " " + term.getTermName());
							tempExtraFieldList.add(info);
						}
					} else {
						attendColumn = "last";
						tempExtraFieldList.add(info);
					}
				}
			}

			if (tempExtraFieldList.size() > 0) {
				Collections.sort(tempExtraFieldList, new RankComparator());
			}

			showExtraFieldTable = true;
		}
	}

	public void otherSubjectMarks(StudentInfo allInfo, String classid, String sectionid, Connection conn,
			String schid) {
		otherColumnsList.clear();
		for (TermInfo term : termListForOther) {
			otherColumnsList.add(String.valueOf("sub" + term.getTermId()));
			otherColumnsList.add("grade" + term.getTermId());
		}
		allInfo.setOtherColumnsList(otherColumnsList);
		allInfo.setMarkListOtherSubject(createSubjectList(classid, sectionid, "other", conn));
		deBLM.fillMarksForCoscholasticSubject(/* classid, */sectionid, allInfo.getMarkListOtherSubject(), addNumber,
				session, termListForOther, "other", conn, allInfo, purpose);

	}

	public void additionalMarks(StudentInfo allInfo, String classid, String sectionid, Connection conn, String schid) {
		addColumnsList.clear();
		int termSize = 0, ptFlag = 0, count = 0;

		for (TermInfo term : termListForAdditional) {
			ptFlag = 0;
			if (Integer.parseInt(term.getTermId()) > 0)
				termSize++;
			for (ExamInfo exam : term.getExamInfoList()) {
				if (exam.getInclude().equals("Yes")) {
					if (exam.getSubExamList() != null) {
						if (exam.getExamTaken().equals("other")) {
							for (SubExamInfo subExam : exam.getSubExamList()) {
								if (!subExam.getSubExamUpperCase().trim().equals(""))
									count++;
								addColumnsList.add(term.getTermId() + "term" + subExam.getMainExamId()
										+ "total-obtained" + subExam.getSubExamName());
							}
						} else {
							if (comeTestType.equals("periodic")) {
								for (SubExamInfo subExam : exam.getSubExamList()) {
									if (!subExam.getSubExamUpperCase().trim().equals(""))
										count++;
									addColumnsList.add(term.getTermId() + "term" + subExam.getMainExamId()
											+ "total-obtained" + subExam.getSubExamName());
								}
							} else if (ptFlag == 0) {
								addColumnsList.add(term.getTermId() + "term" + "P.T." + "total-obtained" + ptExamName);
								ptFlag = 1;
							}
						}
					} else
						addColumnsList.add(term.getTermId() + "term" + "" + "total-obtained" + "");
				}
			}
		}

		if (count != 0)
			showSubExamRowAdd = true;

		allInfo.setAddColumnsList(addColumnsList);
		allInfo.setMarkListAddSub(createSubjectList(classid, sectionid, "additional", conn));
		if (allInfo.getMarkListAddSub().size() > 0) {
			de.fillStudentMarks(sectionid, addNumber, allInfo.getMarkListAddSub(), session, termListForAdditional, conn,
					allInfo, addColumnsList, termSize, "additional", subjectListSize, examSetting, purpose,
					comeTestType,schid);
		}

	}

	public void exportMarkPdf() throws DocumentException, IOException, FileNotFoundException {

		Connection con = DataBaseConnection.javaConnection();
		SchoolInfoList ls = obj.fullSchoolInfo(con);

		ArrayList<ExamInfo> pdfList = new ArrayList<ExamInfo>();

		for (int los = 0; los < listOfStudent.size(); los++) {
			ExamInfo ei = new ExamInfo();

			Document document = new Document();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			PdfWriter.getInstance(document, baos);
			document.open();

			Font fo = new Font(FontFamily.HELVETICA, 12, Font.BOLD);

			// HEAD IMAGE CODE

			if (showHeader = true) {
				String src = "https://images-na.ssl-images-amazon.com/images/I/51UW1849rJL._AC_SX466_.jpg";

				// String src = "resources/avalon-layout/images/login/cake.png";

				if (!ls.getMarksheetHeader().equalsIgnoreCase("")) {
					src = ls.getDownloadpath() + ls.getMarksheetHeader();
				}
				PdfPTable tableHeader = new PdfPTable(1);

				Image im = null;
				try {

					im = Image.getInstance(src);
					im.setWidthPercentage(110);
					im.scaleAbsoluteHeight(70);
					im.scaleAbsoluteWidth(575);
					PdfPCell cell1 = new PdfPCell(im);
					cell1.setBorder(0);

					cell1.setBorderColor(BaseColor.WHITE);
					tableHeader.addCell(cell1);
				} catch (Exception e) {
					e.printStackTrace();
				}

				Image imBlckLine = null;
				try {
					String srcBlk = "https://images-na.ssl-images-amazon.com/images/I/51UW1849rJL._AC_SX466_.jpg";
					imBlckLine = Image.getInstance(srcBlk);
					imBlckLine.setWidthPercentage(110);
					imBlckLine.scaleAbsoluteHeight(1);
					imBlckLine.scaleAbsoluteWidth(575);

					PdfPCell cell2 = new PdfPCell(imBlckLine);

					tableHeader.addCell(cell2);
				} catch (Exception e) {

				}

				tableHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
				tableHeader.setWidthPercentage(110);
				document.add(tableHeader);

			}

			Font fontqNormalDetails = FontFactory.getFont(FontFactory.TIMES_ROMAN, 8, Font.NORMAL);

			// HEADERS
			Font fontBoldheaders = new Font(FontFamily.HELVETICA, 11, Font.BOLD);
			Paragraph p2 = null;
			if (!header2.equalsIgnoreCase("") && !header3.equalsIgnoreCase("")) {
				p2 = new Paragraph(header1 + "\n" + header2 + "\n" + header3, fontBoldheaders);
			} else if (!header2.equalsIgnoreCase("") && header3.equalsIgnoreCase("")) {
				p2 = new Paragraph(header1 + "\n" + header2, fontBoldheaders);
			} else {
				p2 = new Paragraph(header1, fontBoldheaders);
			}

			p2.setLeading(15);
			p2.setAlignment(Element.ALIGN_CENTER);
			document.add(p2);

			Paragraph pspacer2 = new Paragraph("\n");
			document.add(pspacer2);

			// STUDENT DETAILS
			Font fontqNormalDetailsStudent = null;
			PdfPTable table1 = null;
			if (showStudentImage == false) {
				fontqNormalDetailsStudent = FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, Font.NORMAL);
				table1 = new PdfPTable(4);
			} else {
				fontqNormalDetailsStudent = FontFactory.getFont(FontFactory.TIMES_ROMAN, 8, Font.NORMAL);
				table1 = new PdfPTable(5);
			}

			table1.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

			PdfPCell cell1 = new PdfPCell(new Paragraph("Admn.No:-", fontqNormalDetailsStudent));
			cell1.setBorder(0);
			cell1.setPadding(4);
			cell1.setBorderColor(BaseColor.WHITE);
			table1.addCell(cell1);

			PdfPCell cell2 = new PdfPCell(new Paragraph(listOfStudent.get(los).getSrNo(), fontqNormalDetailsStudent));
			cell2.setBorder(0);
			cell2.setPadding(4);
			cell2.setBorderColor(BaseColor.WHITE);
			table1.addCell(cell2);

			PdfPCell cell3 = new PdfPCell(new Paragraph("Class / Section:-", fontqNormalDetailsStudent));
			cell3.setBorder(0);
			cell3.setPadding(4);
			cell3.setBorderColor(BaseColor.WHITE);
			table1.addCell(cell3);

			PdfPCell cell4 = new PdfPCell(
					new Paragraph(listOfStudent.get(los).getClassName(), fontqNormalDetailsStudent));
			cell4.setBorder(0);
			cell4.setPadding(4);
			cell4.setBorderColor(BaseColor.WHITE);
			table1.addCell(cell4);

			PdfPCell cellExtra1 = null;
			if (showStudentImage == false) {

			} else {
				String srcS = "https://images-na.ssl-images-amazon.com/images/I/51UW1849rJL._AC_SX466_.jpg";

				if (!listOfStudent.get(los).getStudent_image().equalsIgnoreCase("")) {
					srcS = listOfStudent.get(los).getStudent_image();
				}

				Image imS = null;
				try {
					imS = Image.getInstance(srcS);
					// imS.setAlignment(Element.ALIGN_LEFT);

					imS.scaleAbsoluteHeight(64);
					imS.scaleAbsoluteWidth(64);
					cellExtra1 = new PdfPCell(imS);

					cellExtra1.setBorder(0);
					cellExtra1.setRowspan(4);
					cellExtra1.setBorderColor(BaseColor.WHITE);
					table1.addCell(cellExtra1);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			PdfPCell cell5 = new PdfPCell(new Paragraph("Student Name:-", fontqNormalDetailsStudent));
			cell5.setBorder(0);
			cell5.setPadding(4);
			cell5.setBorderColor(BaseColor.WHITE);
			table1.addCell(cell5);

			PdfPCell cell6 = new PdfPCell(new Paragraph(listOfStudent.get(los).getFname(), fontqNormalDetailsStudent));
			cell6.setBorder(0);
			cell6.setPadding(4);
			cell6.setBorderColor(BaseColor.WHITE);
			table1.addCell(cell6);

			PdfPCell cell7 = new PdfPCell(new Paragraph("Roll No.", fontqNormalDetailsStudent));
			cell7.setBorder(0);
			cell7.setPadding(4);
			cell7.setBorderColor(BaseColor.WHITE);
			table1.addCell(cell7);

			PdfPCell cell8 = new PdfPCell(new Paragraph(listOfStudent.get(los).getRollNo(), fontqNormalDetailsStudent));
			cell8.setBorder(0);
			cell8.setPadding(4);
			cell8.setBorderColor(BaseColor.WHITE);
			table1.addCell(cell8);

			PdfPCell cell9 = new PdfPCell(new Paragraph("Father's Name:-", fontqNormalDetailsStudent));
			cell9.setBorder(0);
			cell9.setPadding(4);
			cell9.setBorderColor(BaseColor.WHITE);
			table1.addCell(cell9);

			PdfPCell cell10 = new PdfPCell(
					new Paragraph(listOfStudent.get(los).getFathersName(), fontqNormalDetailsStudent));
			cell10.setBorder(0);
			cell10.setPadding(4);
			cell10.setBorderColor(BaseColor.WHITE);
			table1.addCell(cell10);

			PdfPCell cell11 = new PdfPCell(new Paragraph("Date of Birth:-", fontqNormalDetailsStudent));
			cell11.setBorder(0);
			cell11.setPadding(4);
			cell11.setBorderColor(BaseColor.WHITE);
			table1.addCell(cell11);

			PdfPCell cell12 = new PdfPCell(
					new Paragraph(listOfStudent.get(los).getDobString(), fontqNormalDetailsStudent));
			cell12.setBorder(0);
			cell12.setPadding(4);
			cell12.setBorderColor(BaseColor.WHITE);
			table1.addCell(cell12);

			PdfPCell cell13 = new PdfPCell(new Paragraph("Mother's Name:-", fontqNormalDetailsStudent));
			cell13.setBorder(0);
			cell13.setPadding(4);
			cell13.setBorderColor(BaseColor.WHITE);
			table1.addCell(cell13);

			PdfPCell cell14 = new PdfPCell(
					new Paragraph(listOfStudent.get(los).getMotherName(), fontqNormalDetailsStudent));
			cell14.setBorder(0);
			cell14.setPadding(4);
			cell14.setBorderColor(BaseColor.WHITE);
			table1.addCell(cell14);

			PdfPCell cell15 = new PdfPCell(new Paragraph("Contact No:-", fontqNormalDetailsStudent));
			cell15.setBorder(0);
			cell15.setPadding(4);
			cell15.setBorderColor(BaseColor.WHITE);
			table1.addCell(cell15);

			PdfPCell cell16 = new PdfPCell(
					new Paragraph(String.valueOf(listOfStudent.get(los).getFathersPhone()), fontqNormalDetailsStudent));
			cell16.setBorder(0);
			cell16.setPadding(4);
			cell16.setBorderColor(BaseColor.WHITE);
			table1.addCell(cell16);

			table1.setHorizontalAlignment(Element.ALIGN_CENTER);
			table1.setWidthPercentage(110);
			document.add(table1);

			// EXTRAFIELDTOPCODE UPP
			if (showExtraFieldTable == true) {
				Paragraph pspc = new Paragraph("\n");
				document.add(pspc);

				if (showUp == true) {

					if (showColumn == true) {
						Font fontq = FontFactory.getFont(FontFactory.TIMES_ROMAN, 7, Font.BOLD);
						Font fontqNormal = FontFactory.getFont(FontFactory.TIMES_ROMAN, 7, Font.NORMAL);
						PdfPTable table2 = new PdfPTable(tempExtraFieldList.size());
						table2.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

						for (int k = 0; k < tempExtraFieldList.size(); k++) {
							PdfPCell cell22 = new PdfPCell(new Phrase(tempExtraFieldList.get(k).getLabel(), fontq));
							cell22.setHorizontalAlignment(Element.ALIGN_CENTER);
							cell22.setPadding(5);
							table2.addCell(cell22);
						}
						for (int i = 0; i < extraFieldList.size(); i++) {
							for (int k = 0; k < tempExtraFieldList.size(); k++) {
								PdfPCell cell22 = new PdfPCell(new Phrase(
										extraFieldList.get(i).getExtraMap().get(tempExtraFieldList.get(k).getLabel()),
										fontqNormal));
								cell22.setHorizontalAlignment(Element.ALIGN_CENTER);
								cell22.setPadding(5);
								table2.addCell(cell22);

							}

						}

						table2.setWidthPercentage(110);

						document.add(table2);
					}

					if (showRow == true) {

						Font fontq = FontFactory.getFont(FontFactory.TIMES_ROMAN, 8, Font.BOLD);
						Font fontqNormal = FontFactory.getFont(FontFactory.TIMES_ROMAN, 8, Font.NORMAL);
						PdfPTable table2 = new PdfPTable(2);
						table2.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

						for (int k = 0; k < listOfStudent.get(los).getExtraFieldList().size(); k++) {

							PdfPCell cell22 = new PdfPCell(new Phrase(
									listOfStudent.get(los).getExtraFieldList().get(k).getLabel().toUpperCase(),
									fontqNormal));
							cell22.setHorizontalAlignment(Element.ALIGN_CENTER);
							cell22.setPadding(5);
							table2.addCell(cell22);

							PdfPCell cell222 = new PdfPCell(new Phrase(
									listOfStudent.get(los).getExtraFieldList().get(k).getExtraValue(), fontqNormal));
							cell222.setHorizontalAlignment(Element.ALIGN_CENTER);
							cell222.setPadding(5);
							table2.addCell(cell222);
						}

						table2.setWidthPercentage(110);

						document.add(table2);
					}
				}
			}

			// MAIN TAble
			Paragraph paraMain = new Paragraph("\n");
			document.add(paraMain);

			int subExamSize = 0;
			for (int j = 0; j < termList.size(); j++) {
				int examSize = termList.get(j).getExamInfoList().size();

				for (int a = 0; a < examSize; a++) {
					subExamSize += termList.get(j).getExamInfoList().get(a).getSubExamListSize();

				}

			}

			// For Row Span Calculation
			int rowspanSubject = 3;
			int fontCheckTermTotal = 0;
			int fontCheckTermGrade = 0;
			int fontCheckFinalTotal = 0;
			int fontCheckFinalGrade = 0;

			PdfPTable tableMain = new PdfPTable(1 + subExamSize);
			tableMain.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

			for (int j = 0; j < termList.size(); j++) {
				if (termList.get(j).getTermName().equalsIgnoreCase("Final Total")
						|| termList.get(j).getTermName().equalsIgnoreCase("GRAND TOTAL")
						|| termList.get(j).getTermName().equalsIgnoreCase("AVERAGE")
						|| termList.get(j).getTermName().equalsIgnoreCase("AGREEGATE MARKS")
						|| termList.get(j).getTermName().equalsIgnoreCase("GRAND TOTAL")
						|| termList.get(j).getTermName().equalsIgnoreCase("AVERAGE")
						|| termList.get(j).getTermName().equalsIgnoreCase("Term-1(50%)+Term-2(50%)")
						|| termList.get(j).getTermName().equalsIgnoreCase("FINAL MARKS")) {
					fontCheckFinalTotal = 1;

				}
				if (termList.get(j).getTermName().equalsIgnoreCase("FINAL GRADE")) {
					fontCheckFinalGrade = 1;
				}
				int examSize = termList.get(j).getExamInfoList().size();
				for (int t = 0; t < examSize; t++) {
					if (termList.get(j).getExamInfoList().get(t).getExamName().equalsIgnoreCase("TOTAL")
							|| termList.get(j).getExamInfoList().get(t).getExamName().equalsIgnoreCase("GRAND TOTAL")) {
						fontCheckTermTotal = 1;

					}
					if (termList.get(j).getExamInfoList().get(t).getExamName().equalsIgnoreCase("GRADE")) {
						fontCheckTermGrade = 1;
					}
					int subExmSizee = termList.get(j).getExamInfoList().get(t).getSubExamListSize();
					for (int m = 0; m < subExmSizee; m++) {

						if ((termList.get(j).getExamInfoList().get(t).getCount() == 0)
								&& (termList.get(j).getExamInfoList().get(t).getSubExamList().size() != 1)
								&& (termList.get(j).getExamInfoList().get(t).getSubExamList().get(0)
										.getSubExamUpperCase().equalsIgnoreCase(""))) {
							rowspanSubject = 4;
						}

					}

				}

			}

			Font fontMainBold = null;
			Font fontMainNormal = null;
			Font fontMainNormalSubject = null;
			Font fintMainData = null;

			if ((fontCheckFinalGrade == 0) && (fontCheckFinalTotal == 0) && (fontCheckTermTotal == 0)
					|| (fontCheckTermGrade == 0)) {
				fontMainBold = FontFactory.getFont(FontFactory.TIMES_ROMAN, 6, Font.BOLD);
				fontMainNormal = FontFactory.getFont(FontFactory.TIMES_ROMAN, 6);
				fintMainData = FontFactory.getFont(FontFactory.TIMES_ROMAN, 7);
				fontMainNormalSubject = FontFactory.getFont(FontFactory.HELVETICA, 6);

			} else if ((fontCheckFinalGrade == 1) && (fontCheckFinalTotal == 0) || (fontCheckTermTotal == 0)
					|| (fontCheckTermGrade == 0)) {
				fontMainBold = FontFactory.getFont(FontFactory.TIMES_ROMAN, 6, Font.BOLD);
				fontMainNormal = FontFactory.getFont(FontFactory.TIMES_ROMAN, 6);
				fintMainData = FontFactory.getFont(FontFactory.TIMES_ROMAN, 7);
				fontMainNormalSubject = FontFactory.getFont(FontFactory.HELVETICA, 6);

			} else if ((fontCheckFinalGrade == 1) && (fontCheckFinalTotal == 1) || (fontCheckTermTotal == 0)
					|| (fontCheckTermGrade == 0)) {
				fontMainBold = FontFactory.getFont(FontFactory.TIMES_ROMAN, 6, Font.BOLD);
				fontMainNormal = FontFactory.getFont(FontFactory.TIMES_ROMAN, 6);
				fintMainData = FontFactory.getFont(FontFactory.TIMES_ROMAN, 7);
				fontMainNormalSubject = FontFactory.getFont(FontFactory.HELVETICA, 6);

			} else {
				fontMainBold = FontFactory.getFont(FontFactory.TIMES_ROMAN, 5, Font.BOLD);
				fontMainNormal = FontFactory.getFont(FontFactory.TIMES_ROMAN, 5);
				fintMainData = FontFactory.getFont(FontFactory.TIMES_ROMAN, 5);
				fontMainNormalSubject = FontFactory.getFont(FontFactory.HELVETICA, 5);

			}

			// Term Row
			PdfPCell cellMSubject = new PdfPCell(new Paragraph("Subject", fontMainBold));

			cellMSubject.setHorizontalAlignment(Element.ALIGN_CENTER);
			cellMSubject.setRowspan(rowspanSubject);
			cellMSubject.setPaddingTop(20);

			tableMain.addCell(cellMSubject);

			for (int j = 0; j < termList.size(); j++) {
				PdfPCell cellM1 = new PdfPCell(new Paragraph(termList.get(j).getTermName(), fontMainBold));
				cellM1.setColspan(termList.get(j).getExamInfoList().size());
				cellM1.setPaddingBottom(4);
				cellM1.setPaddingTop(4);
				cellM1.setPaddingLeft(1);
				cellM1.setPaddingRight(1);
				cellM1.setHorizontalAlignment(Element.ALIGN_CENTER);
				tableMain.addCell(cellM1);
			}

			// ExamRow

			for (int j = 0; j < termList.size(); j++) {
				int examSize = termList.get(j).getExamInfoList().size();

				for (int t = 0; t < examSize; t++) {
					if (termList.get(j).getExamInfoList().get(t).getCount() == 0) {
						PdfPCell cellM1 = new PdfPCell(
								new Paragraph(termList.get(j).getExamInfoList().get(t).getExamName(), fontMainBold));
						cellM1.setColspan(termList.get(j).getExamInfoList().get(t).getSubExamListSize());
						cellM1.setPaddingBottom(4);
						cellM1.setPaddingTop(4);
						cellM1.setPaddingLeft(1);
						cellM1.setPaddingRight(1);
						cellM1.setHorizontalAlignment(Element.ALIGN_CENTER);
						tableMain.addCell(cellM1);
					}
				}
			}

			// SubExam Row

			for (int j = 0; j < termList.size(); j++) {
				int examSize = termList.get(j).getExamInfoList().size();
				for (int t = 0; t < examSize; t++) {
					int subExmSizee = termList.get(j).getExamInfoList().get(t).getSubExamListSize();
					for (int m = 0; m < subExmSizee; m++) {

						if ((termList.get(j).getExamInfoList().get(t).getCount() == 0)
								&& (termList.get(j).getExamInfoList().get(t).getSubExamList().size() != 1)
								&& (termList.get(j).getExamInfoList().get(t).getSubExamList().get(0)
										.getSubExamUpperCase().equalsIgnoreCase(""))) {
							PdfPCell cellM1 = new PdfPCell(new Paragraph(termList.get(j).getExamInfoList().get(t)
									.getSubExamList().get(m).getSubExamUpperCase(), fontMainBold));
							cellM1.setHorizontalAlignment(Element.ALIGN_CENTER);
							cellM1.setPaddingBottom(4);
							cellM1.setPaddingTop(4);
							cellM1.setPaddingLeft(1);
							cellM1.setPaddingRight(1);
							tableMain.addCell(cellM1);
						}

					}

				}

			}

			// Last Level Row

			for (int j = 0; j < termList.size(); j++) {
				int examSize = termList.get(j).getExamInfoList().size();
				for (int t = 0; t < examSize; t++) {
					int subExmSizee = termList.get(j).getExamInfoList().get(t).getSubExamListSize();
					for (int m = 0; m < subExmSizee; m++) {

						if (termList.get(j).getExamInfoList().get(t).getCount() == 0) {
							PdfPCell cellM1 = new PdfPCell(new Paragraph(
									termList.get(j).getExamInfoList().get(t).getSubExamList().get(m).getLastLevelName(),
									fontMainBold));
							cellM1.setHorizontalAlignment(Element.ALIGN_CENTER);
							cellM1.setPaddingBottom(4);
							cellM1.setPaddingTop(4);
							cellM1.setPaddingLeft(1);
							cellM1.setPaddingRight(1);
							tableMain.addCell(cellM1);
						}

					}

				}

			}

			// Data
			int marklistSize = listOfStudent.get(los).getMarkList().size();
			for (int j = 0; j < marklistSize; j++) {
				PdfPCell cellM111 = new PdfPCell(new Phrase(
						listOfStudent.get(los).getMarkList().get(j).getSubjectName(), fontMainNormalSubject));
				cellM111.setHorizontalAlignment(Element.ALIGN_CENTER);
				cellM111.setPaddingBottom(4);
				cellM111.setPaddingTop(4);
				cellM111.setPaddingLeft(0);
				cellM111.setPaddingRight(0);
				cellM111.setLeading(2, 1);
				tableMain.addCell(cellM111);

				for (int h = 0; h < listOfStudent.get(los).getScholasticColumnsList().size(); h++) {

					PdfPCell cellM1 = new PdfPCell(new Phrase(listOfStudent.get(los).getMarkList().get(j).getMarksMap()
							.get(scholasticColumnsList.get(h).toString()), fintMainData));
					cellM1.setHorizontalAlignment(Element.ALIGN_CENTER);
					cellM1.setPaddingBottom(4);
					cellM1.setPaddingTop(4);
					cellM1.setPaddingLeft(1);
					cellM1.setPaddingRight(1);
					tableMain.addCell(cellM1);
				}

			}

			tableMain.setWidthPercentage(110);

			document.add(tableMain);

			// Additional TAble
			if (showAdditional_sub == true) {

				Font fontMainBoldAdd = FontFactory.getFont(FontFactory.TIMES_ROMAN, 6, Font.BOLD);
				Font fontMainNormalAdd = FontFactory.getFont(FontFactory.TIMES_ROMAN, 6);
				Font fontMainNormalSubjectAdd = FontFactory.getFont(FontFactory.TIMES_ROMAN, 6);
				Paragraph paraAdd = new Paragraph("\n");
				document.add(paraAdd);

				int subExamSizeAdd = 0;
				for (int j = 0; j < termListForAdditional.size(); j++) {
					int examSizeAdd = termListForAdditional.get(j).getExamInfoList().size();

					if (examSizeAdd == 0) {
						subExamSizeAdd += 1;
					}

					for (int a = 0; a < examSizeAdd; a++) {
						subExamSizeAdd += termListForAdditional.get(j).getExamInfoList().get(a).getSubExamListSize();

					}

				}

				PdfPTable tableAdd = new PdfPTable(1 + subExamSizeAdd);
				tableAdd.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

				// For RowSpan Subject Calculation
				int rowspanSubjectAdd = 3;
				for (int j = 0; j < termListForAdditional.size(); j++) {
					int examSizeAdd = termListForAdditional.get(j).getExamInfoList().size();
					for (int t = 0; t < examSizeAdd; t++) {
						int subExmSizeeAdd = termListForAdditional.get(j).getExamInfoList().get(t).getSubExamListSize();
						for (int m = 0; m < subExmSizeeAdd; m++) {

							if ((termListForAdditional.get(j).getExamInfoList().get(t).getSubExamList().size() != 1)
									&& (termListForAdditional.get(j).getExamInfoList().get(t).getSubExamList().get(0)
											.getSubExamUpperCase().equalsIgnoreCase(""))) {
								rowspanSubjectAdd = 4;
							}

						}

					}

				}

				// Term Row
				PdfPCell cellMSubjectAdd = new PdfPCell(new Paragraph("Subject", fontMainBoldAdd));
				cellMSubjectAdd.setRowspan(rowspanSubjectAdd);

				cellMSubjectAdd.setPaddingTop(20);

				cellMSubjectAdd.setHorizontalAlignment(Element.ALIGN_CENTER);

				tableAdd.addCell(cellMSubjectAdd);

				for (int j = 0; j < termListForAdditional.size(); j++) {
					PdfPCell cellMAdd = new PdfPCell(
							new Paragraph(termListForAdditional.get(j).getTermName(), fontMainBoldAdd));
					cellMAdd.setColspan(termListForAdditional.get(j).getExamInfoList().size());
					cellMAdd.setPaddingBottom(4);
					cellMAdd.setPaddingTop(4);
					cellMAdd.setPaddingLeft(1);
					cellMAdd.setPaddingRight(1);
					cellMAdd.setHorizontalAlignment(Element.ALIGN_CENTER);
					tableAdd.addCell(cellMAdd);
				}

				// ExamRow

				for (int j = 0; j < termListForAdditional.size(); j++) {
					int examSizeAdd = termListForAdditional.get(j).getExamInfoList().size();

					for (int t = 0; t < examSizeAdd; t++) {

						PdfPCell cellM1Add = new PdfPCell(new Paragraph(
								termListForAdditional.get(j).getExamInfoList().get(t).getExamName(), fontMainBoldAdd));
						cellM1Add
								.setColspan(termListForAdditional.get(j).getExamInfoList().get(t).getSubExamListSize());
						cellM1Add.setPaddingBottom(4);
						cellM1Add.setPaddingTop(4);
						cellM1Add.setPaddingLeft(1);
						cellM1Add.setPaddingRight(1);
						cellM1Add.setHorizontalAlignment(Element.ALIGN_CENTER);
						tableAdd.addCell(cellM1Add);

					}
				}

				// SubExam Row

				for (int j = 0; j < termListForAdditional.size(); j++) {
					int examSizeAdd = termListForAdditional.get(j).getExamInfoList().size();
					for (int t = 0; t < examSizeAdd; t++) {
						int subExmSizeeAdd = termListForAdditional.get(j).getExamInfoList().get(t).getSubExamListSize();
						for (int m = 0; m < subExmSizeeAdd; m++) {

							if ((termListForAdditional.get(j).getExamInfoList().get(t).getSubExamList().size() != 1)
									&& (termListForAdditional.get(j).getExamInfoList().get(t).getSubExamList().get(0)
											.getSubExamUpperCase().equalsIgnoreCase(""))) {
								PdfPCell cellM1Add = new PdfPCell(
										new Paragraph(termListForAdditional.get(j).getExamInfoList().get(t)
												.getSubExamList().get(m).getSubExamUpperCase(), fontMainBoldAdd));
								cellM1Add.setHorizontalAlignment(Element.ALIGN_CENTER);
								cellM1Add.setPaddingBottom(4);
								cellM1Add.setPaddingTop(4);
								cellM1Add.setPaddingLeft(1);
								cellM1Add.setPaddingRight(1);
								tableAdd.addCell(cellM1Add);
							}

						}

					}

				}

				// Last Level Row

				for (int j = 0; j < termListForAdditional.size(); j++) {
					int examSizeAdd = termListForAdditional.get(j).getExamInfoList().size();
					for (int t = 0; t < examSizeAdd; t++) {
						int subExmSizeeAdd = termListForAdditional.get(j).getExamInfoList().get(t).getSubExamListSize();
						for (int m = 0; m < subExmSizeeAdd; m++) {

							PdfPCell cellM1Add = new PdfPCell(
									new Paragraph(termListForAdditional.get(j).getExamInfoList().get(t).getSubExamList()
											.get(m).getLastLevelName(), fontMainBoldAdd));
							cellM1Add.setHorizontalAlignment(Element.ALIGN_CENTER);
							cellM1Add.setPaddingBottom(4);
							cellM1Add.setPaddingTop(4);
							cellM1Add.setPaddingLeft(1);
							cellM1Add.setPaddingRight(1);
							tableAdd.addCell(cellM1Add);

						}

					}

				}

				// Data
				int marklistSizeAdd = listOfStudent.get(los).getMarkListAddSub().size();
				for (int j = 0; j < marklistSizeAdd; j++) {
					PdfPCell cellM111 = new PdfPCell(
							new Phrase(listOfStudent.get(los).getMarkListAddSub().get(j).getSubjectName(),
									fontMainNormalSubjectAdd));
					cellM111.setHorizontalAlignment(Element.ALIGN_CENTER);
					cellM111.setPaddingBottom(4);
					cellM111.setPaddingTop(4);
					cellM111.setPaddingLeft(0);
					cellM111.setPaddingRight(0);
					cellM111.setLeading(2, 1);
					tableAdd.addCell(cellM111);

					for (int h = 0; h < listOfStudent.get(los).getAddColumnsList().size(); h++) {

						PdfPCell cellM111Add = new PdfPCell(new Phrase(listOfStudent.get(los).getMarkListAddSub().get(j)
								.getMarksMap().get(addColumnsList.get(h).toString()), fontMainNormalAdd));
						cellM111Add.setHorizontalAlignment(Element.ALIGN_CENTER);
						cellM111Add.setPaddingBottom(4);
						cellM111Add.setPaddingTop(4);
						cellM111Add.setPaddingLeft(1);
						cellM111Add.setPaddingRight(1);
						tableAdd.addCell(cellM111Add);
					}

				}

				tableAdd.setWidthPercentage(110);

				document.add(tableAdd);
			}

			// Other Code
			if (showOther_sub == true) {

				Paragraph paraother = new Paragraph("\n");
				document.add(paraother);

				Font fontq = FontFactory.getFont(FontFactory.TIMES_ROMAN, 7);
				Font fontBOLD = FontFactory.getFont(FontFactory.TIMES_ROMAN, 7, Font.BOLD);
				PdfPTable tableOther = new PdfPTable(termListForOther.size() * 2);
				tableOther.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

				if (!termNameOther.trim().equalsIgnoreCase("")) {
					for (int k = 0; k < termListForOther.size(); k++) {

						if (termNameOther.equalsIgnoreCase("begin")) {
							PdfPCell cell = new PdfPCell(
									new Phrase(termListForOther.get(k).getTermName() + "-" + otherHeader, fontBOLD));
							cell.setColspan(2);
							cell.setPadding(5);
							cell.setHorizontalAlignment(Element.ALIGN_CENTER);
							tableOther.addCell(cell);

						}

						if (termNameOther.equalsIgnoreCase("end")) {
							PdfPCell cell = new PdfPCell(
									new Phrase(otherHeader + "-" + termListForOther.get(k).getTermName(), fontBOLD));
							cell.setColspan(2);
							cell.setPadding(5);
							cell.setHorizontalAlignment(Element.ALIGN_CENTER);
							tableOther.addCell(cell);

						}

						if (termNameOther.equalsIgnoreCase("dont")) {
							PdfPCell cell = new PdfPCell(new Phrase(otherHeader, fontBOLD));
							cell.setColspan(2);
							cell.setPadding(5);
							cell.setHorizontalAlignment(Element.ALIGN_CENTER);
							tableOther.addCell(cell);

						}

					}
				}

				for (int k = 0; k < termListForOther.size(); k++) {
					PdfPCell cell = new PdfPCell(new Phrase("Subject", fontBOLD));
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell.setPadding(5);

					PdfPCell cell22 = new PdfPCell(new Phrase("Grade", fontBOLD));
					cell22.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell22.setPadding(5);
					tableOther.addCell(cell);
					tableOther.addCell(cell22);
				}

//			for(int k=0;k<otherColumnsList.size();k++)
//			{	
//				tableOther.addCell(new Phrase(listOfStudent.get(0).getMarksMap().get(otherColumnsList.get(k).toString()),fontq));
//				
//			}
				for (int k = 0; k < listOfStudent.get(los).getMarkListOtherSubject().size(); k++) {
					for (int q = 0; q < listOfStudent.get(los).getOtherColumnsList().size(); q++) {

						PdfPCell cell22 = new PdfPCell(new Phrase(listOfStudent.get(los).getMarkListOtherSubject()
								.get(k).getMarksMap().get(otherColumnsList.get(q).toString()), fontq));
						cell22.setHorizontalAlignment(Element.ALIGN_CENTER);
						cell22.setPadding(5);
						tableOther.addCell(cell22);
					}
				}

				tableOther.setWidthPercentage(110);

				document.add(tableOther);
			}

			// Coschol Code
			if (showCoschol_sub == true) {
				Paragraph paraCoschol = new Paragraph("\n");
				document.add(paraCoschol);

				Font fontq = FontFactory.getFont(FontFactory.TIMES_ROMAN, 7);
				Font fontBOLD = FontFactory.getFont(FontFactory.TIMES_ROMAN, 7, Font.BOLD);
				PdfPTable tableCosc = new PdfPTable(termListForCoschol.size() * 2);
				tableCosc.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

				if (!coscholHeader.trim().equalsIgnoreCase("")) {
					for (int k = 0; k < termListForCoschol.size(); k++) {

						if (termNameDisci.equalsIgnoreCase("begin")) {
							PdfPCell cell = new PdfPCell(new Phrase(
									termListForCoschol.get(k).getTermName() + "-" + coscholHeader, fontBOLD));
							cell.setColspan(2);
							cell.setPadding(5);
							cell.setHorizontalAlignment(Element.ALIGN_CENTER);
							tableCosc.addCell(cell);

						}

						if (termNameDisci.equalsIgnoreCase("end")) {
							PdfPCell cell = new PdfPCell(new Phrase(
									coscholHeader + "-" + termListForCoschol.get(k).getTermName(), fontBOLD));
							cell.setColspan(2);
							cell.setPadding(5);
							cell.setHorizontalAlignment(Element.ALIGN_CENTER);
							tableCosc.addCell(cell);

						}

						if (termNameDisci.equalsIgnoreCase("dont")) {
							PdfPCell cell = new PdfPCell(new Phrase(coscholHeader, fontBOLD));
							cell.setColspan(2);
							cell.setPadding(5);
							cell.setHorizontalAlignment(Element.ALIGN_CENTER);
							tableCosc.addCell(cell);
						}

					}
				}

				for (int k = 0; k < termListForCoschol.size(); k++) {
					PdfPCell cell = new PdfPCell(new Phrase("Subject", fontBOLD));
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell.setPadding(5);

					PdfPCell cell22 = new PdfPCell(new Phrase("Grade", fontBOLD));
					cell22.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell22.setPadding(5);

					tableCosc.addCell(cell);
					tableCosc.addCell(cell22);

				}

				for (int k = 0; k < listOfStudent.get(los).getMarkListCoscholastic().size(); k++) {
					for (int q = 0; q < listOfStudent.get(los).getCoscholasticColumnsList().size(); q++) {

						PdfPCell cell22 = new PdfPCell(new Phrase(listOfStudent.get(los).getMarkListCoscholastic()
								.get(k).getMarksMap().get(coscholasticColumnsList.get(q).toString()), fontq));
						cell22.setHorizontalAlignment(Element.ALIGN_CENTER);
						cell22.setPadding(5);
						tableCosc.addCell(cell22);
					}
				}

				tableCosc.setWidthPercentage(110);

				document.add(tableCosc);
			}

			// Discipline Code
			if (showDisci_sub == true) {

				Paragraph paraDisci = new Paragraph("\n");
				document.add(paraDisci);

				Font fontq = FontFactory.getFont(FontFactory.TIMES_ROMAN, 7);
				Font fontBOLD = FontFactory.getFont(FontFactory.TIMES_ROMAN, 7, Font.BOLD);

				PdfPTable tableDis = new PdfPTable(termListForDiscipline.size() * 2);
				tableDis.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

				if (!disciplineHeader.trim().equalsIgnoreCase("")) {
					for (int k = 0; k < termListForDiscipline.size(); k++) {

						if (termNameDisci.equalsIgnoreCase("begin")) {
							PdfPCell cell = new PdfPCell(new Phrase(
									termListForDiscipline.get(k).getTermName() + "-" + disciplineHeader, fontBOLD));
							cell.setColspan(2);
							cell.setPadding(5);

							cell.setHorizontalAlignment(Element.ALIGN_CENTER);
							tableDis.addCell(cell);

						}

						if (termNameDisci.equalsIgnoreCase("end")) {
							PdfPCell cell = new PdfPCell(new Phrase(
									disciplineHeader + "-" + termListForDiscipline.get(k).getTermName(), fontBOLD));
							cell.setColspan(2);
							cell.setPadding(5);
							cell.setHorizontalAlignment(Element.ALIGN_CENTER);
							tableDis.addCell(cell);

						}

						if (termNameDisci.equalsIgnoreCase("dont")) {
							PdfPCell cell = new PdfPCell(new Phrase(disciplineHeader, fontBOLD));
							cell.setColspan(2);
							cell.setPadding(5);
							cell.setHorizontalAlignment(Element.ALIGN_CENTER);
							tableDis.addCell(cell);

						}

					}
				}

				for (int k = 0; k < termListForDiscipline.size(); k++) {
					PdfPCell cell = new PdfPCell(new Phrase("Subject", fontBOLD));
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell.setPadding(5);

					PdfPCell cell22 = new PdfPCell(new Phrase("Grade", fontBOLD));
					cell22.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell22.setPadding(5);

					tableDis.addCell(cell);
					tableDis.addCell(cell22);
				}

				for (int k = 0; k < listOfStudent.get(los).getMarkListDiscipline().size(); k++) {
					for (int q = 0; q < listOfStudent.get(los).getDisciplineColumnsList().size(); q++) {
						PdfPCell cell22 = new PdfPCell(new Phrase(listOfStudent.get(los).getMarkListDiscipline().get(k)
								.getMarksMap().get(disciplineColumnsList.get(q).toString()), fontq));
						cell22.setHorizontalAlignment(Element.ALIGN_CENTER);
						cell22.setPadding(5);
						tableDis.addCell(cell22);
					}
				}

				tableDis.setWidthPercentage(110);

				document.add(tableDis);
			}

			// EXTRAFIELDBOTTOMCODE DOWN
			if (showExtraFieldTable == true) {
				if (showBottom == true) {
					Paragraph paraExtaDown = new Paragraph("\n");
					document.add(paraExtaDown);

					if (showColumn == true) {

						Font fontq = FontFactory.getFont(FontFactory.TIMES_ROMAN, 7, Font.BOLD);
						Font fontqNormal = FontFactory.getFont(FontFactory.TIMES_ROMAN, 7, Font.NORMAL);
						PdfPTable table2 = new PdfPTable(tempExtraFieldList.size());
						table2.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

						for (int k = 0; k < tempExtraFieldList.size(); k++) {
							PdfPCell cell22 = new PdfPCell(new Phrase(tempExtraFieldList.get(k).getLabel(), fontq));
							cell22.setHorizontalAlignment(Element.ALIGN_CENTER);
							cell22.setPadding(5);
							table2.addCell(cell22);
						}
						for (int i = 0; i < extraFieldList.size(); i++) {
							for (int k = 0; k < tempExtraFieldList.size(); k++) {
								PdfPCell cell22 = new PdfPCell(new Phrase(
										extraFieldList.get(i).getExtraMap().get(tempExtraFieldList.get(k).getLabel()),
										fontqNormal));
								cell22.setHorizontalAlignment(Element.ALIGN_CENTER);
								cell22.setPadding(5);
								table2.addCell(cell22);

							}

						}

						table2.setWidthPercentage(110);

						document.add(table2);
					}

					if (showRow == true) {
						Font fontqNormal = FontFactory.getFont(FontFactory.TIMES_ROMAN, 8, Font.NORMAL);
						Font fontq = FontFactory.getFont(FontFactory.TIMES_ROMAN, 8, Font.BOLD);
						PdfPTable table2 = new PdfPTable(2);
						table2.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

						for (int k = 0; k < listOfStudent.get(los).getExtraFieldList().size(); k++) {
							PdfPCell cell22 = new PdfPCell(new Phrase(
									listOfStudent.get(los).getExtraFieldList().get(k).getLabel().toUpperCase(),
									fontqNormal));
							cell22.setHorizontalAlignment(Element.ALIGN_CENTER);
							cell22.setPadding(5);
							table2.addCell(cell22);

							PdfPCell cell222 = new PdfPCell(new Phrase(
									listOfStudent.get(los).getExtraFieldList().get(k).getExtraValue(), fontqNormal));
							cell222.setHorizontalAlignment(Element.ALIGN_CENTER);
							cell222.setPadding(5);
							table2.addCell(cell222);

						}

						table2.setWidthPercentage(110);

						document.add(table2);
					}
				}
			}

			// OtherVale
			Font boldFont = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
			Font boldNormal = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.NORMAL);
			Paragraph paraotherValue = new Paragraph("\n");
			document.add(paraotherValue);

			PdfPTable tableOtherVaue = new PdfPTable(2);

			// tableOtherVaue.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

			for (int p = 0; p < otherValueList.size(); p++) {

				PdfPCell cell1Oth = new PdfPCell(new Paragraph(otherValueList.get(p).getClassName() + " : ", boldFont));
				cell1Oth.setBorder(0);
				cell1Oth.setBorderColor(BaseColor.WHITE);

				cell1Oth.setPaddingLeft(-24);
				cell1Oth.setPaddingRight(0);
				tableOtherVaue.addCell(cell1Oth);

				PdfPCell cell2Oth = new PdfPCell(new Paragraph(otherValueList.get(p).getExtraValue(), boldNormal));
				cell2Oth.setBorder(0);
				cell2Oth.setBorderColor(BaseColor.WHITE);

				tableOtherVaue.addCell(cell2Oth);

			}
			tableOtherVaue.setSpacingAfter(0);
			tableOtherVaue.setSpacingBefore(0);
			tableOtherVaue.setHorizontalAlignment(Element.ALIGN_LEFT);

			tableOtherVaue.setWidthPercentage(50);
			document.add(tableOtherVaue);

			Paragraph pspc = new Paragraph("\n");
			document.add(pspc);

			// SIGN CODE
			Font foSign = new Font(FontFamily.HELVETICA, 8);
			PdfPTable tableSign = new PdfPTable(2 + signList.size());
			tableSign.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

			PdfPCell cellPlaceEmpty = new PdfPCell(new Paragraph("\n\n"));
			cellPlaceEmpty.setBorder(0);
			cellPlaceEmpty.setBorderColor(BaseColor.WHITE);
			tableSign.addCell(cellPlaceEmpty);

			PdfPCell cellDateEmpty = new PdfPCell(new Paragraph("\n\n"));
			cellDateEmpty.setBorder(0);
			cellDateEmpty.setBorderColor(BaseColor.WHITE);
			tableSign.addCell(cellDateEmpty);
			for (int k = 0; k < signList.size(); k++) {

				PdfPCell cellSign = null;
				if (signList.get(k).getSignType().equalsIgnoreCase("manual")) {
					cellSign = new PdfPCell(new Paragraph("\n\n"));
				} else {
					try {

						String srcSign = "https://images-na.ssl-images-amazon.com/images/I/51UW1849rJL._AC_SX466_.jpg";

						if (!signList.get(k).getSignImageName().equalsIgnoreCase("")) {
							srcSign = signList.get(k).getSignImageName();
						}

						Image imSign = Image.getInstance(srcSign);

						imSign.scaleAbsoluteHeight(30);
						imSign.scaleAbsoluteWidth(50);

						cellSign = new PdfPCell(imSign);
					} catch (Exception e) {
						cellSign = new PdfPCell(new Paragraph("\n\n"));
					}
				}

				cellSign.setBorder(0);
				cellSign.setBorderColor(BaseColor.WHITE);
				tableSign.addCell(cellSign);
			}

			PdfPCell cellPlace = new PdfPCell(new Paragraph("Place : " + info.add1, foSign));
			cellPlace.setBorder(0);
			cellPlace.setBorderColor(BaseColor.WHITE);
			tableSign.addCell(cellPlace);

			PdfPCell cellDate = new PdfPCell(new Paragraph("Date : " + dateEntry, foSign));
			cellDate.setBorder(0);
			cellDate.setBorderColor(BaseColor.WHITE);
			tableSign.addCell(cellDate);
			for (int k = 0; k < signList.size(); k++) {
				PdfPCell cellSign = new PdfPCell(new Paragraph(signList.get(k).getSignName(), foSign));
				cellSign.setBorder(0);
				cellSign.setBorderColor(BaseColor.WHITE);
				tableSign.addCell(cellSign);
			}

			tableSign.setWidthPercentage(110);

			document.add(tableSign);

			// GRADESCALE CODE
			if (showGradeScale == true) {

				Image imBlckLine = null;
				try {
					String srcBlk = "https://w7.pngwing.com/pngs/41/380/png-transparent-black-vertical-line-rectangle-horizontal-line-angle-black-internet-thumbnail.png";
					imBlckLine = Image.getInstance(srcBlk);
					imBlckLine.setWidthPercentage(110);
					imBlckLine.scaleAbsoluteHeight(1);
					imBlckLine.scaleAbsoluteWidth(575);
				} catch (Exception e) {
					String srcBlk = "https://images-na.ssl-images-amazon.com/images/I/51UW1849rJL._AC_SX466_.jpg";
					imBlckLine = Image.getInstance(srcBlk);
					imBlckLine.setWidthPercentage(110);
					imBlckLine.scaleAbsoluteHeight(1);
					imBlckLine.scaleAbsoluteWidth(575);
					// TODO: handle exception
				}

				PdfPTable tableHeaderG = new PdfPTable(1);

				PdfPCell cell111 = new PdfPCell(imBlckLine);
				cell111.setBorder(1);
				cell111.setBorderColor(BaseColor.BLACK);

				tableHeaderG.addCell(cell111);

				tableHeaderG.setHorizontalAlignment(Element.ALIGN_CENTER);
				tableHeaderG.setWidthPercentage(110);
				document.add(tableHeaderG);

				Font font1Bold = FontFactory.getFont(FontFactory.TIMES_ROMAN, 9, Font.BOLD);
				Font font1Norm = FontFactory.getFont(FontFactory.TIMES_ROMAN, 9);
				Paragraph pGrade = new Paragraph("Instructions\n", fontBoldheaders);
				pGrade.setAlignment(Element.ALIGN_CENTER);
				Chunk pGrade2 = new Chunk("Grading scale for scholastic areas : ", font1Bold);
				Chunk pGrade3 = new Chunk(" Grades are awarded on 8 - point grading scale as follows -\n ", font1Norm);
				document.add(pGrade);

				Paragraph paraGradeTotal = new Paragraph();
				paraGradeTotal.add(pGrade2);
				paraGradeTotal.add(pGrade3);
				paraGradeTotal.setAlignment(Element.ALIGN_LEFT);
				paraGradeTotal.setIndentationLeft(-23);
				document.add(paraGradeTotal);

				Font fontq1 = FontFactory.getFont(FontFactory.TIMES_ROMAN, 7);
				Font fontqB = FontFactory.getFont(FontFactory.TIMES_ROMAN, 7, Font.BOLD);
				PdfPTable table2 = new PdfPTable(2);
				table2.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

				PdfPCell cell22211 = new PdfPCell(new Phrase("MARKS RANGE", fontqB));
				cell22211.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell22211.setPadding(5);
				table2.addCell(cell22211);

				PdfPCell cell222212 = new PdfPCell(new Phrase("GRADE", fontqB));
				cell222212.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell222212.setPadding(5);
				table2.addCell(cell222212);

				for (int k = 0; k < gradeScaleList.size(); k++) {
					PdfPCell cell222 = new PdfPCell(new Phrase(gradeScaleList.get(k).getLabel(), fontq1));
					cell222.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell222.setPadding(5);
					table2.addCell(cell222);

					PdfPCell cell2222 = new PdfPCell(
							new Phrase(String.valueOf(gradeScaleList.get(k).getExtraValue()), fontq1));
					cell2222.setHorizontalAlignment(Element.ALIGN_CENTER);
					cell2222.setPadding(5);
					table2.addCell(cell2222);

				}

				table2.setWidthPercentage(60);
				table2.setHorizontalAlignment(Element.ALIGN_CENTER);
				document.add(table2);
			}

			document.close();

			InputStream isFromFirstData = new ByteArrayInputStream(baos.toByteArray());
			ei.setInpStream(isFromFirstData);

			pdfList.add(ei);
		}

		String fileName = "Marksheet_" + listOfStudent.get(0).getClassName() + ".zip";
		FacesContext fc = FacesContext.getCurrentInstance();
		ExternalContext ec = fc.getExternalContext();

		ec.responseReset(); // Some JSF component library or some Filter might have set some headers in the
							// buffer beforehand. We want to get rid of them, else it may collide.
		ec.setResponseContentType(Charsets.UTF_8.name()); // Check http://www.iana.org/assignments/media-types for all
															// types. Use if necessary ExternalContext#getMimeType() for
															// auto-detection based on filename.
		// ec.setResponseContentLength(); // Set it with the file size. This header is
		// optional. It will work if it's omitted, but the download progress will be
		// unknown.
		ec.setResponseHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\""); // The Save As popup
																									// magic is done
																									// here. You can
																									// give it any file
																									// name you want,
																									// this only won't
																									// work in MSIE, it
																									// will use current
																									// request URL as
																									// file name
																									// instead.

		byte[] buffer = new byte[1024];

		try {
			OutputStream output = ec.getResponseOutputStream();

			ZipOutputStream zout = new ZipOutputStream(output);

			for (int kk = 0; kk < listOfStudent.size(); kk++) {

				zout.putNextEntry(new ZipEntry("Marksheet_" + listOfStudent.get(kk).getFname() + ".pdf"));
				int length;
				while ((length = pdfList.get(kk).getInpStream().read(buffer)) > 0) {
					zout.write(buffer, 0, length);
				}
				zout.closeEntry();

				// close the InputStream
				pdfList.get(kk).getInpStream().close();

			}
			zout.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		fc.responseComplete();

		try {
			con.close();
		} catch (SQLException e1) {

			e1.printStackTrace();
		}

	}

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getAddNumber() {
		return addNumber;
	}

	public void setAddNumber(String addNumber) {
		this.addNumber = addNumber;
	}

	public StudentInfo getStudentList() {
		return studentList;
	}

	public void setStudentList(StudentInfo studentList) {
		this.studentList = studentList;
	}

	public ArrayList<String> getScholasticColumnsList() {
		return scholasticColumnsList;
	}

	public void setScholasticColumnsList(ArrayList<String> scholasticColumnsList) {
		this.scholasticColumnsList = scholasticColumnsList;
	}

	public ArrayList<String> getCoscholasticColumnsList() {
		return coscholasticColumnsList;
	}

	public void setCoscholasticColumnsList(ArrayList<String> coscholasticColumnsList) {
		this.coscholasticColumnsList = coscholasticColumnsList;
	}

	public ArrayList<String> getDisciplineColumnsList() {
		return disciplineColumnsList;
	}

	public void setDisciplineColumnsList(ArrayList<String> disciplineColumnsList) {
		this.disciplineColumnsList = disciplineColumnsList;
	}

	public ArrayList<ExtraFieldInfo> getExtraFieldList() {
		return extraFieldList;
	}

	public void setExtraFieldList(ArrayList<ExtraFieldInfo> extraFieldList) {
		this.extraFieldList = extraFieldList;
	}

	public ArrayList<StudentInfo> getListOfStudent() {
		return listOfStudent;
	}

	public void setListOfStudent(ArrayList<StudentInfo> listOfStudent) {
		this.listOfStudent = listOfStudent;
	}

	public String getDateEntry() {
		return dateEntry;
	}

	public void setDateEntry(String dateEntry) {
		this.dateEntry = dateEntry;
	}

	public SchoolInfoList getInfo() {
		return info;
	}

	public void setInfo(SchoolInfoList info) {
		this.info = info;
	}

	public String getHeaderImagePath() {
		return headerImagePath;
	}

	public void setHeaderImagePath(String headerImagePath) {
		this.headerImagePath = headerImagePath;
	}

	public String getSchid() {
		return schid;
	}

	public void setSchid(String schid) {
		this.schid = schid;
	}

	public boolean isShowNote() {
		return showNote;
	}

	public void setShowNote(boolean showNote) {
		this.showNote = showNote;
	}

	public boolean isShowRemark() {
		return showRemark;
	}

	public void setShowRemark(boolean showRemark) {
		this.showRemark = showRemark;
	}

	public boolean isShowResult() {
		return showResult;
	}

	public void setShowResult(boolean showResult) {
		this.showResult = showResult;
	}

	public boolean isShowPromotion() {
		return showPromotion;
	}

	public void setShowPromotion(boolean showPromotion) {
		this.showPromotion = showPromotion;
	}

	public boolean isCheck() {
		return check;
	}

	public void setCheck(boolean check) {
		this.check = check;
	}

	public ArrayList<ExtraFieldInfo> getGradeScaleListForCoSchol() {
		return gradeScaleListForCoSchol;
	}

	public void setGradeScaleListForCoSchol(ArrayList<ExtraFieldInfo> gradeScaleListForCoSchol) {
		this.gradeScaleListForCoSchol = gradeScaleListForCoSchol;
	}

	public ArrayList<ExtraFieldInfo> getGradeScaleList() {
		return gradeScaleList;
	}

	public void setGradeScaleList(ArrayList<ExtraFieldInfo> gradeScaleList) {
		this.gradeScaleList = gradeScaleList;
	}

	public String getGradeScaleFormat() {
		return gradeScaleFormat;
	}

	public void setGradeScaleFormat(String gradeScaleFormat) {
		this.gradeScaleFormat = gradeScaleFormat;
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

	public boolean isShowExtraFieldTable() {
		return showExtraFieldTable;
	}

	public void setShowExtraFieldTable(boolean showExtraFieldTable) {
		this.showExtraFieldTable = showExtraFieldTable;
	}

	public boolean isShowUp() {
		return showUp;
	}

	public void setShowUp(boolean showUp) {
		this.showUp = showUp;
	}

	public boolean isShowBottom() {
		return showBottom;
	}

	public void setShowBottom(boolean showBottom) {
		this.showBottom = showBottom;
	}

	public boolean isShowRow() {
		return showRow;
	}

	public void setShowRow(boolean showRow) {
		this.showRow = showRow;
	}

	public boolean isShowColumn() {
		return showColumn;
	}

	public void setShowColumn(boolean showColumn) {
		this.showColumn = showColumn;
	}

	public ArrayList<ExtraFieldInfo> getTempExtraFieldList() {
		return tempExtraFieldList;
	}

	public void setTempExtraFieldList(ArrayList<ExtraFieldInfo> tempExtraFieldList) {
		this.tempExtraFieldList = tempExtraFieldList;
	}

	public boolean isShowHeader() {
		return showHeader;
	}

	public void setShowHeader(boolean showHeader) {
		this.showHeader = showHeader;
	}

	public boolean isShowStudentImage() {
		return showStudentImage;
	}

	public void setShowStudentImage(boolean showStudentImage) {
		this.showStudentImage = showStudentImage;
	}

	public boolean isShowCoschol_sub() {
		return showCoschol_sub;
	}

	public void setShowCoschol_sub(boolean showCoschol_sub) {
		this.showCoschol_sub = showCoschol_sub;
	}

	public boolean isShowOther_sub() {
		return showOther_sub;
	}

	public void setShowOther_sub(boolean showOther_sub) {
		this.showOther_sub = showOther_sub;
	}

	public boolean isShowAdditional_sub() {
		return showAdditional_sub;
	}

	public void setShowAdditional_sub(boolean showAdditional_sub) {
		this.showAdditional_sub = showAdditional_sub;
	}

	public ArrayList<TermInfo> getTermList() {
		return termList;
	}

	public void setTermList(ArrayList<TermInfo> termList) {
		this.termList = termList;
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

	public String getDisciplineTerm() {
		return disciplineTerm;
	}

	public void setDisciplineTerm(String disciplineTerm) {
		this.disciplineTerm = disciplineTerm;
	}

	public String getDisciplineHeader() {
		return disciplineHeader;
	}

	public void setDisciplineHeader(String disciplineHeader) {
		this.disciplineHeader = disciplineHeader;
	}

	public boolean isShowGradeScale() {
		return showGradeScale;
	}

	public void setShowGradeScale(boolean showGradeScale) {
		this.showGradeScale = showGradeScale;
	}

	public boolean isShowDisci_sub() {
		return showDisci_sub;
	}

	public void setShowDisci_sub(boolean showDisci_sub) {
		this.showDisci_sub = showDisci_sub;
	}

	public ArrayList<TermInfo> getTermListForCoschol() {
		return termListForCoschol;
	}

	public void setTermListForCoschol(ArrayList<TermInfo> termListForCoschol) {
		this.termListForCoschol = termListForCoschol;
	}

	public ArrayList<TermInfo> getTermListForAdditional() {
		return termListForAdditional;
	}

	public void setTermListForAdditional(ArrayList<TermInfo> termListForAdditional) {
		this.termListForAdditional = termListForAdditional;
	}

	public ArrayList<TermInfo> getTermListForDiscipline() {
		return termListForDiscipline;
	}

	public void setTermListForDiscipline(ArrayList<TermInfo> termListForDiscipline) {
		this.termListForDiscipline = termListForDiscipline;
	}

	public ArrayList<TermInfo> getTermListForOther() {
		return termListForOther;
	}

	public void setTermListForOther(ArrayList<TermInfo> termListForOther) {
		this.termListForOther = termListForOther;
	}

	public String getTermNameCoschol() {
		return termNameCoschol;
	}

	public void setTermNameCoschol(String termNameCoschol) {
		this.termNameCoschol = termNameCoschol;
	}

	public String getTermNameOther() {
		return termNameOther;
	}

	public void setTermNameOther(String termNameOther) {
		this.termNameOther = termNameOther;
	}

	public String getTermNameDisci() {
		return termNameDisci;
	}

	public void setTermNameDisci(String termNameDisci) {
		this.termNameDisci = termNameDisci;
	}

	public ArrayList<ExtraFieldInfo> getSignList() {
		return signList;
	}

	public void setSignList(ArrayList<ExtraFieldInfo> signList) {
		this.signList = signList;
	}

	public ArrayList<ExtraFieldInfo> getOtherValueList() {
		return otherValueList;
	}

	public void setOtherValueList(ArrayList<ExtraFieldInfo> otherValueList) {
		this.otherValueList = otherValueList;
	}

	public boolean isShowSubExamRow() {
		return showSubExamRow;
	}

	public void setShowSubExamRow(boolean showSubExamRow) {
		this.showSubExamRow = showSubExamRow;
	}

	public ArrayList<LifeSkillsInfo> getLifeSkillsList() {
		return lifeSkillsList;
	}

	public void setLifeSkillsList(ArrayList<LifeSkillsInfo> lifeSkillsList) {
		this.lifeSkillsList = lifeSkillsList;
	}

	public String getComeTestType() {
		return comeTestType;
	}

	public void setComeTestType(String comeTestType) {
		this.comeTestType = comeTestType;
	}

	public StreamedContent getFile() {
		return file;
	}

	public void setFile(StreamedContent file) {
		this.file = file;
	}

	public StreamedContent getFile2() {
		return file2;
	}

	public void setFile2(StreamedContent file2) {
		this.file2 = file2;
	}

	public boolean isShowOtherFieldTable() {
		return showOtherFieldTable;
	}

	public void setShowOtherFieldTable(boolean showOtherFieldTable) {
		this.showOtherFieldTable = showOtherFieldTable;
	}

	public ArrayList<String> getTempGradeFieldList() {
		return tempGradeFieldList;
	}

	public void setTempGradeFieldList(ArrayList<String> tempGradeFieldList) {
		this.tempGradeFieldList = tempGradeFieldList;
	}

	public ArrayList<String> getTempGradeFieldListForCoschol() {
		return tempGradeFieldListForCoschol;
	}

	public void setTempGradeFieldListForCoschol(ArrayList<String> tempGradeFieldListForCoschol) {
		this.tempGradeFieldListForCoschol = tempGradeFieldListForCoschol;
	}

	public boolean isShowGradeScaleCoschol() {
		return showGradeScaleCoschol;
	}

	public void setShowGradeScaleCoschol(boolean showGradeScaleCoschol) {
		this.showGradeScaleCoschol = showGradeScaleCoschol;
	}

	public boolean isShowSubExamRowAdd() {
		return showSubExamRowAdd;
	}

	public void setShowSubExamRowAdd(boolean showSubExamRowAdd) {
		this.showSubExamRowAdd = showSubExamRowAdd;
	}

	public String getClassid() {
		return classid;
	}

	public void setClassid(String classid) {
		this.classid = classid;
	}

	public ArrayList<ExamInfo> getAllExamNames() {
		return allExamNames;
	}

	public void setAllExamNames(ArrayList<ExamInfo> allExamNames) {
		this.allExamNames = allExamNames;
	}

	public boolean isShowExmName() {
		return showExmName;
	}

	public void setShowExmName(boolean showExmName) {
		this.showExmName = showExmName;
	}

	class RankComparator implements Comparator<ExtraFieldInfo> {
		@Override
		public int compare(ExtraFieldInfo e1, ExtraFieldInfo e2) {

			if (Integer.parseInt(e1.getRollNo()) >= Integer.parseInt(e2.getRollNo())) {
				return 1;
			} else {
				return -1;
			}
		}
	}
}