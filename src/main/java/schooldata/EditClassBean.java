package schooldata;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import session_work.RegexPattern;


@ManagedBean(name="editClass")
@SessionScoped
public class EditClassBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	String regex=RegexPattern.REGEX;
	ArrayList<ClassInfo> classList;
	String nameTemp, groupId;
	int classId;
	String description="",name;
	boolean editDetailsShow;
	ClassInfo selectedClass;
	ArrayList<FeeInfo> listFee,allFeesList;
	ArrayList<SelectItem> monthList;
	ArrayList<ConnsessionList>connList;
	String selectedMonth;
	String rollno;

	public void monthDetails()
	{

		monthList=new ArrayList<>();
		//int month=selectedClass.getMonth();
		monthList=new DatabaseMethods1().monthListHandling();
	}

	@PostConstruct
	public void init() {
		Connection conn = DataBaseConnection.javaConnection();
		 // System.out.println("editclass...."+new Date());
		classList=new DatabaseMethods1().allClassList("",conn);

		/*for(ClassInfo cc:classList)
        {
            //// // System.out.println("name : "+cc.getClassName());
        }*/

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*public EditClassBean()
	{
		Connection conn = DataBaseConnection.javaConnection();
		classList=new DatabaseMethods1().allClassList(conn);

		for(ClassInfo cc:classList)
		{
			//// // System.out.println("name : "+cc.getClassName());
		}

		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}*/

	public void editDetail()
	{
		name=selectedClass.getClassName();
		nameTemp=name;
		groupId=selectedClass.getGroupid();
	}

	public void editClassDetails()
	{
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM = new DatabaseMethods1();
		boolean check=DBM.checkPromotionForSession(conn);
		if(check==true)
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("You Can Not Edit Details.. Because You Promoted Student From This Session"));
		}
		else
		{
			allFeesList=new ArrayList<>();
			classId=selectedClass.getId();
			name=selectedClass.getClassName();
			description=selectedClass.getDescription();
			nameTemp=name;
			groupId=selectedClass.getGroupid();
			//String preSession=DatabaseMethods1.selectedSessionDetails();
			listFee=DBM.classFees(groupId,/*preSession,*/conn);
			listFee.remove(listFee.size()-1);

			connList=DBM.allCategoryList(conn);


			ArrayList<String> temp=new ArrayList<>();
			for(FeeInfo mm:listFee)
			{
				temp.add(mm.getFeeId());
			}
			String a[]=new String[temp.size()];
			for(int i=0;i<temp.size();i++)
			{
				a[i]=temp.get(i);
			}
			a=removeDuplicates(a);



			for(int i=0;i<a.length;i++)
			{
				FeeInfo ff=new FeeInfo();
				Map<String, String> map=new HashMap<>();
				Map<String, String> map1=new HashMap<>();

				for(FeeInfo list : listFee)
				{

					if(list.getFeeId().equals(String.valueOf(a[i])))
					{
						ff.setSno(i+1);
						ff.setFeeName(list.getFeeName());
						ff.setConncessionid(list.getConncessionid());
						ff.setFeeId(list.getFeeId());
						map.put(list.getConncessionid(),String.valueOf(list.getFeeAmount()));
						map1.put(list.getConncessionid(),String.valueOf(list.getOldfeeAmount()));
						ff.setFeeType(list.getFeeType());
					}

				}
				ff.setNewfeeAmountmap(map);
				ff.setOlfFeeAmountmap(map1);
				allFeesList.add(ff);
			}

			FacesContext fc = FacesContext.getCurrentInstance();
			ExternalContext ec = fc.getExternalContext();

			try {
				ec.redirect("EditClassDetails.xhtml");
			} catch (IOException e) {
				
				e.printStackTrace();
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
		FacesContext fc = FacesContext.getCurrentInstance();
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM = new DatabaseMethods1();
		boolean check=DBM.checkPromotionForSession(conn);
		if(check==true)
		{
			fc.addMessage(null, new FacesMessage("You Can Not Delete Details.. Because You Promoted Student From This Session"));
		}
		else
		{
			int a=DBM.checkforstudent("mainClassId",Integer.valueOf(selectedClass.getGroupid()),conn);
			if(a==2)
			{
				fc.addMessage(null, new FacesMessage("You can not delete this class because there are students in this class" ));
			}
			else
			{
				DBM.deleteClass(selectedClass.getGroupid(),conn);
				DBM.deleteSectionafterclass(selectedClass.getGroupid(),conn);
				DBM.deleteExamafterclass(selectedClass.getGroupid(),conn);
				DBM.deleteSemesterafterclass(selectedClass.getGroupid(), conn);
				DBM.deletesubjectsafterclass(selectedClass.getGroupid(),conn);
				//DBM.deleteexamssettings(selectedClass.groupid,conn);
				DBM.deleteClassTest1(selectedClass.getGroupid(), conn);
				/*if(i>=1)
				{*/
				
				String refNo;
				try {
					refNo=addWorkLog(conn);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				classList=DBM.allClassList("",conn);
				editDetailsShow=false;

				fc.addMessage(null, new FacesMessage("Selected Class Deleted Successfully"));
				ExternalContext ec = fc.getExternalContext();
				try {
					conn.close();
					ec.redirect("editDeleteClass.xhtml");

				} catch (Exception e) {
					e.printStackTrace();
				}
				////// // System.out.println("dsdsds");
				/*}
				else
				{
					fc.addMessage(null, new FacesMessage("Something went wrong!"));
					////// // System.out.println("dsdsds");
				}*/
			}
		}


	}
	
	public String addWorkLog(Connection conn)
	{
	    String value = "";
		String language= "";
		
		DatabaseMethods1 obj = new DatabaseMethods1(); 
		String schid = obj.schoolId();
		String className=obj.classname(selectedClass.getGroupid(), schid, conn);
		//String sectionname =obj.sectionNameByIdSchid(schid,String.valueOf(selectedSection.getId()), conn);
		
		value = " ClassId-"+selectedClass.getGroupid();
		
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Delete Class","WEB",value,conn);
		return refNo;
	}

	public void editNow()
	{
		Connection conn = DataBaseConnection.javaConnection();
		try
		{
			int flag=0;
			/*if(annualFee<=0)
			{
				flag++;
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Registration fee must be greter than zero", "Validation error"));
			}
			if(tutionFee<=0)
			{
				flag++;
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Tution fee must be greter than zero", "Validation error"));
			}
			if(admissionFee<=0)
			{
				flag++;
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Addmission fee must be greter than zero", "Validation error"));
			}
			if(examFee<=0)
			{
				flag++;
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Security fee must be greter than zero", "Validation error"));
			}
			if(activityFee<=0)
			{
				flag++;
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Activity fee must be greter than zero", "Validation error"));
			}
			if(termFee<=0)
			{
				flag++;
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Term fee must be greter than zero", "Validation error"));
			}
			if(artFee<=0)
			{
				flag++;
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Art&Craft fee must be greter than zero", "Validation error"));
			}
			if(!(nameTemp.equals(name) ))
			{
				if(flag==0)
				{
					int status=new DataBaseValidator().duplicateClassName(name);
					if(status==0)
					{
						flag++;
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Duplicate entry found,choose a different class name or section", "Validation error"));
					}
				}
			}*/

			DatabaseMethods1 DBM = new DatabaseMethods1();
			if(flag==0)
			{
				int status =DBM.updateClassDetails(allFeesList,name,/* description, */groupId,connList,conn);
				if(status>=1)
				{
					String refNo3;
					try {
						refNo3=addWorkLog3(conn);
					} catch (Exception e) {
						e.printStackTrace();
					}
					DBM.updateClassName(groupId, name, conn);
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Class details updated successfully"));
					FacesContext fc = FacesContext.getCurrentInstance();
					ExternalContext ec = fc.getExternalContext();

					try {
						ec.redirect("editClass.xhtml");
					} catch (IOException e) {
						
						e.printStackTrace();
					}
				}
				else
				{
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"An error occurred, try again ", "Validation error"));
					FacesContext fc = FacesContext.getCurrentInstance();
					ExternalContext ec = fc.getExternalContext();

					try {
						ec.redirect("EditClassDetails.xhtml");
					} catch (IOException e) {
						
						e.printStackTrace();
					}
				}

			}

			classList=DBM.allClassList("",conn);
			selectedClass=new ClassInfo();
			editDetailsShow=false;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public String addWorkLog3(Connection conn)
	{
	    String value = "";
		String language= "name-"+name+"(groupId)"+groupId ;
		
		for (FeeInfo ff : allFeesList) 
		{
			value += ff.getFeeName()+"-"+ff.getFeeType()+":";
			Map<String, String> map = ff.getNewfeeAmountmap();
			Map<String, String> map2 = ff.getOlfFeeAmountmap();

			for (ConnsessionList ls : connList)
			{
				String newfeeAmount = map.get(ls.getId());
				if (newfeeAmount.equals("") || newfeeAmount == null) {
					newfeeAmount = "0";
				}

				String oldFeeAmount = map2.get(ls.getId());
				if (oldFeeAmount.equals("") || oldFeeAmount == null) {
					oldFeeAmount = "0";
				}
				
//				String tempNewFee = map.get(connList.get(0).getId());
//				String tempOldFee = map2.get(connList.get(0).getId());
//
//				if (tempNewFee.equals("") || tempNewFee == null) {
//					tempNewFee = "0";
//				}
//
//				if (tempOldFee.equals("") || tempOldFee == null) {
//					tempOldFee = "0";
//				}
//
//				int newconsssionAmount = Integer.parseInt(tempNewFee) - Integer.parseInt(newfeeAmount);
//				int oldconnessionamount = Integer.parseInt(tempOldFee) - Integer.parseInt(oldFeeAmount);
				
		       value += "("+ls.getCategory()+" --OldAmt-"+oldFeeAmount+" --NewAmt-"+newfeeAmount+")";
		
			}
			
			value += " ,, ";
			
		}
		
		
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Class fee Allocation","WEB",value,conn);
		return refNo;
	}
	
	

	public void editClassName()
	{
		Connection conn = DataBaseConnection.javaConnection();
		DatabaseMethods1 DBM = new DatabaseMethods1();
		try
		{
			if(name.equals(selectedClass.getClassName()))
			{
				FacesContext fc = FacesContext.getCurrentInstance();
				fc.addMessage(null, new FacesMessage("Class name updated successfully"));
			}
			else
			{
			  boolean duplicateCheck = DBM.checkDuplicateClassName(groupId,name,conn);
			  if(!duplicateCheck)
			  {	  
				DBM.updateClassName(groupId, name, conn);
				String refNo2;
				try {
					refNo2=addWorkLog2(conn);
				} catch (Exception e) {
					e.printStackTrace();
				}
				/*if(i>=1)
				{*/
				FacesContext fc = FacesContext.getCurrentInstance();
				fc.addMessage(null, new FacesMessage("Class name updated successfully"));
				ExternalContext ec = fc.getExternalContext();

				try {
					ec.redirect("editDeleteClass.xhtml");
				} catch (IOException e) {
					e.printStackTrace();
				}
				/*}
				else
				{
					FacesContext fc = FacesContext.getCurrentInstance();
					fc.addMessage(null, new FacesMessage("Something went wrong. Please try again!"));
				}*/
			  }
			  else
			  {
				  FacesContext fc = FacesContext.getCurrentInstance();
					fc.addMessage(null, new FacesMessage("Class Name already Present ,Try a diiferent one ")); 
			  }
			}

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public String addWorkLog2(Connection conn)
	{
	    String value = "";
		String language= name;
	
		value= "ClassId - "+groupId+" --- Name - "+name;
		
		String refNo = new DatabaseMethodWorkLog().saveWorkLogMehod(language,"Edit Class","WEB",value,conn);
		return refNo;
	}


	public static String[] removeDuplicates(String[] arr)
	{
		Set<String> alreadyPresent = new HashSet<>();
		String[] whitelist = new String[0];
		for (String nextElem : arr)
		{
			if (!alreadyPresent.contains(nextElem)) {
				whitelist = Arrays.copyOf(whitelist, whitelist.length + 1);
				whitelist[whitelist.length - 1] = nextElem;
				alreadyPresent.add(nextElem);
			}
		}
		return whitelist;
	}




	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	public boolean isEditDetailsShow() {
		return editDetailsShow;
	}
	public void setEditDetailsShow(boolean editDetailsShow) {
		this.editDetailsShow = editDetailsShow;
	}
	public ArrayList<SelectItem> getMonthList() {
		return monthList;
	}
	public void setMonthList(ArrayList<SelectItem> monthList) {
		this.monthList = monthList;
	}
	public String getSelectedMonth() {
		return selectedMonth;
	}
	public void setSelectedMonth(String selectedMonth) {
		this.selectedMonth = selectedMonth;
	}
	public String getNameTemp() {
		return nameTemp;
	}
	public void setNameTemp(String nameTemp) {
		this.nameTemp = nameTemp;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public ArrayList<ClassInfo> getClassList() {
		return classList;
	}
	public void setClassList(ArrayList<ClassInfo> classList) {
		this.classList = classList;
	}
	public int getClassId() {
		return classId;
	}
	public void setClassId(int classId) {
		this.classId = classId;
	}
	public ClassInfo getSelectedClass() {
		return selectedClass;
	}
	public void setSelectedClass(ClassInfo selectedClass) {
		this.selectedClass = selectedClass;
	}
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public ArrayList<FeeInfo> getListFee() {
		return listFee;
	}
	public void setListFee(ArrayList<FeeInfo> listFee) {
		this.listFee = listFee;
	}

	public ArrayList<FeeInfo> getAllFeesList() {
		return allFeesList;
	}

	public void setAllFeesList(ArrayList<FeeInfo> allFeesList) {
		this.allFeesList = allFeesList;
	}

	public ArrayList<ConnsessionList> getConnList() {
		return connList;
	}

	public void setConnList(ArrayList<ConnsessionList> connList) {
		this.connList = connList;
	}

	public String getRollno() {
		return rollno;
	}

	public void setRollno(String rollno) {
		this.rollno = rollno;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}
	


}
