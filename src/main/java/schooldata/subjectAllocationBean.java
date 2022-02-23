package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

@ManagedBean (name="subjectallocation")
@ViewScoped
public class subjectAllocationBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	String selectedteacher,selecetedclass,selectedSection,subjectname,k,subject;
	int sno;
	boolean showtable;
	ArrayList<SelectItem> teacherlist;
	ArrayList<SelectItem> classlist,sectionList;
	ArrayList<Subjects> allsubject,list;
	ArrayList<Subjects> selectedsubject,idealselectedsubject;
	ArrayList<Subjects> subjectdeatils;
	boolean check=false;
	int i,j=1;
	DatabaseMethods1 obj=new DatabaseMethods1();
	String sessionValue,schoolId;
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();
	String sectionId;

	public subjectAllocationBean ()
	{
		Connection conn = DataBaseConnection.javaConnection();
		schoolId = obj.schoolId();
		sessionValue = obj.selectedSessionDetails(schoolId,conn);

		classlist=obj.allClass(conn);
		teacherlist=obj.allteacher(conn);
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	public void allSections()
	{
		Connection conn=DataBaseConnection.javaConnection();
		FacesContext context=FacesContext.getCurrentInstance();
		k=(String) UIComponent.getCurrentComponent(context).getAttributes().get("serialno");

		for(Subjects pp:list)
		{
			if(pp.getSerialNumber().equalsIgnoreCase(k))
			{
				selecetedclass=pp.getClassid();
				pp.setSectionList(obj.allSection(selecetedclass,conn));
				//pp.setSubjectList(new DatabaseMethods1().allSubjectsOfClass(selecetedclass,conn));
			}
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void allSubjects()
	{
		Connection conn=DataBaseConnection.javaConnection();
		FacesContext context=FacesContext.getCurrentInstance();
		k=(String) UIComponent.getCurrentComponent(context).getAttributes().get("serialno");

		for(Subjects pp:list)
		{
			if(pp.getSerialNumber().equalsIgnoreCase(k))
			{
				selecetedclass=pp.getClassid();
				selectedSection=pp.getSectionId();
				//pp.setSectionList(new DatabaseMethods1().allSection(selecetedclass,conn));
				pp.setSubjectList(obj.allSubjectsOfClass(selecetedclass,conn));
				ArrayList<String> subList=obj.allocatedSubjectToTeacher(selecetedclass,selectedSection,selectedteacher,conn);
				if(subList.size()>0)
				{
					String subjectArr[]=new String[subList.size()];
					for(int i=0;i<subList.size();i++)
					{
						subjectArr[i]=subList.get(i);
					}
					pp.setSubjectMultipalCode(subjectArr);
				}
				//pp.setSubjectMultipalCode();
			}
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void deleteNow()
	{

	}

	public void addNew()
	{
		list=new ArrayList<>();
		addMoreRow();
		showtable=true;
	}

	public void addMoreRow()
	{
		for(i=0;i<=4;i++)
		{
			Subjects ls=new Subjects();
			ls.setSerialNumber(String.valueOf(j++));
			list.add(ls);
		}
	}




	/*public void done()
	{

		allsubject=new DatabaseMethods1().searchsubject(selecetedclass);
		idealselectedsubject=new DatabaseMethods1().selectedDetails(selecetedclass,selectedSection,selectedteacher);
		for(Subjects ss:allsubject)
		{
			for(Subjects pp:idealselectedsubject)
			{
				if(ss.subjectName.equalsIgnoreCase(pp.subjectName))
				{
					ss.setValue(true);
				}
				else
				{
					ss.setValue(false);
				}
			}
		}
	}*/
	public void allocate()
	{

		Connection conn=DataBaseConnection.javaConnection();
		String res1="";
		for(Subjects ss:list)
		{
			if((!ss.getSectionId().equals(null))&&(!ss.getSectionId().equals(""))&&(ss.getSubjectMultipalCode().length>0))
			{
				selecetedclass=ss.getClassid();
				selectedSection=ss.getSectionId();
				subject="";
				String[] subjectcode=ss.getSubjectMultipalCode();

				for(int k=0;k<subjectcode.length;k++)
				{
					if(k==0)
					{

						subject=subjectcode[k];
					}
					else
					{
						subject=subject+","+subjectcode[k];
					}
				}
				res1=obj.addallocation(selecetedclass,subject,selectedteacher,selectedSection,conn);
			}
		}

		if(res1.equalsIgnoreCase("successful"))
		{
			String refNo;
			try {
				refNo=addWorkLog(conn);
			} catch (Exception e) {
				// TODO: handle exception
			}
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Subject Allocated Successfully"));
			selecetedclass=selectedteacher=selectedSection=null;
			showtable=false;
		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Must Select Atleast One subject"));
			selecetedclass=selectedteacher=selectedSection=null;
			showtable=false;
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	/*for(Subjects ss:list)
		{

			if(ss.getSelectedsubject().length>0)
			{

				for(int k=0;k<ss.getSelectedsubject().length;k++)
				{

					selecetedclass=ss.getClassid();
					selectedSection=ss.getSectionId();
					subject=ss.getSelectedsubject()[k];
					res1=new DatabaseMethods1().addallocation(selecetedclass,subject,selectedteacher,selectedSection);

				}

			}


		}*/

	/*if((!ss.getSubjectCode().equals(null))&& (!ss.getSectionId().equals(null))&&(!ss.getSubjectCode().equals("")) &&  (!ss.getSectionId().equals("")))
		{
		selecetedclass=ss.getClassid();
		selectedSection=ss.getSectionId();
		subject=ss.getSubjectCode();*/
	/*	res1=new DatabaseMethods1().addallocation(selecetedclass,subject,selectedteacher,selectedSection);
		}
	}*/
	//String res1=new DatabaseMethods1().addallocation(selecetedclass,subject,selectedteacher,selectedSection);


	/*int a=0;
String subject="";
if(selectedsubject.size()>0)
{
	for(Subjects tt: selectedsubject){

		int res=tt.getId();
		subjectdeatils=new DatabaseMethods1().subdetails(res);
	    if(subject.equals(""))
	    {
	    	subject=String.valueOf(tt.getId());

	    }
	    else
	    {
	    	subject=subject+","+String.valueOf(tt.getId());

	    }
	}
	String res1=new DatabaseMethods1().addallocation(selecetedclass,subject,selectedteacher,selectedSection);
	if(res1.equalsIgnoreCase("successful"))
	{
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Subject Allocated Successfully"));
		selecetedclass=selectedteacher=selectedSection=null;
		showtable=false;
	}
	else
	{
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Some Error Occur"));
		selecetedclass=selectedteacher=selectedSection=null;
		showtable=false;
	}

}
else
{
	FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Select atleast one subject"));
}
	 */
	public String addWorkLog(Connection conn)
	{
	    String value = selecetedclass+" --- "+subject+" --- "+selectedSection;
		String language= selectedteacher;

		String refNo = workLg.saveWorkLogMehod(language,"Subject Allocation","WEB",value,conn);
		return refNo;
	}


	public String getSelectedteacher() {
		return selectedteacher;
	}

	public void setSelectedteacher(String selectedteacher) {
		this.selectedteacher = selectedteacher;
	}

	public String getSelecetedclass() {
		return selecetedclass;
	}

	public void setSelecetedclass(String selecetedclass) {
		this.selecetedclass = selecetedclass;
	}

	public ArrayList<SelectItem> getTeacherlist() {
		return teacherlist;
	}

	public void setTeacherlist(ArrayList<SelectItem> teacherlist) {
		this.teacherlist = teacherlist;
	}

	public ArrayList<SelectItem> getClasslist() {
		return classlist;
	}

	public void setClasslist(ArrayList<SelectItem> classlist) {
		this.classlist = classlist;
	}

	public String getSubjectname() {
		return subjectname;
	}

	public void setSubjectname(String subjectname) {
		this.subjectname = subjectname;
	}

	public int getSno() {
		return sno;
	}

	public void setSno(int sno) {
		this.sno = sno;
	}

	public void setAllsubject(ArrayList<Subjects> allsubject) {
		this.allsubject = allsubject;
	}

	public ArrayList<Subjects> getAllsubject() {
		return allsubject;
	}

	public ArrayList<Subjects> getSelectedsubject() {
		return selectedsubject;
	}

	public void setSelectedsubject(ArrayList<Subjects> selectedsubject) {
		this.selectedsubject = selectedsubject;
	}

	public ArrayList<Subjects> getSubjectdeatils() {
		return subjectdeatils;
	}

	public void setSubjectdeatils(ArrayList<Subjects> subjectdeatils) {
		this.subjectdeatils = subjectdeatils;
	}

	public boolean isShowtable() {
		return showtable;
	}

	public void setShowtable(boolean showtable) {
		this.showtable = showtable;
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

	public String getSectionId() {
		return sectionId;
	}

	public void setSectionId(String sectionId) {
		this.sectionId = sectionId;
	}

	public ArrayList<Subjects> getIdealselectedsubject() {
		return idealselectedsubject;
	}

	public void setIdealselectedsubject(ArrayList<Subjects> idealselectedsubject) {
		this.idealselectedsubject = idealselectedsubject;
	}
	public ArrayList<Subjects> getList() {
		return list;
	}
	public void setList(ArrayList<Subjects> list) {
		this.list = list;
	}
	public boolean isCheck() {
		return check;
	}
	public void setCheck(boolean check) {
		this.check = check;
	}





}
