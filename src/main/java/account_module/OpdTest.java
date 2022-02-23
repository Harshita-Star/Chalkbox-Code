package account_module;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.primefaces.PrimeFaces;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import schooldata.DataBaseConnection;
import schooldata.DatabaseMethodWorkLog;
import schooldata.DatabaseMethods1;
import session_work.RegexPattern;


@ManagedBean(name = "OpdTest")
@ViewScoped
public class OpdTest implements Serializable
{
	String regex=RegexPattern.REGEX;
	String category,name,schId,prename,financialYear;
	TreeNode val,val1,val2,val3;
	TreeNode root,root1,root2,root3;
	TreeNode A1,A2,A3,A4,A5,A6,A7;
	String catename="",category1="",maincate;
	boolean chk=false,chk1=true;
	UserInfo selected;
	double liabiltie_total,expense_total,income_total,assets_total;

	ArrayList<SelectItem> maincatelist= new ArrayList<>();
	ArrayList<SelectItem> catelist= new ArrayList<>();
	ArrayList<String> list1= new ArrayList<>();
	ArrayList<DetailsInfo> list4= new ArrayList<>();
	ArrayList<DetailsInfo> list6=new ArrayList<>();
	ArrayList<DetailsInfo> list7= new ArrayList<>();
	ArrayList<DetailsInfo> list8=new ArrayList<>();
	ArrayList<String> list2= new ArrayList<>();
	ArrayList<String> list3=new ArrayList<>();
	ArrayList<String> list5= new ArrayList<>();
	ArrayList<String> list9=new ArrayList<>();
	ArrayList<String> list10= new ArrayList<>();
	DatabaseMethods1 obj1 = new DatabaseMethods1();
	DatabaseMethodWorkLog workLg = new DatabaseMethodWorkLog();
	Method mtd = new Method();

	public OpdTest()
	{

		maincatelist= mtd.mainlist();

		//		HttpSession ss1=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		//	    financialYear=(String) ss1.getAttribute("financialYear");
		//	    schId = (String) ss1.getAttribute("schId");
		schId = obj1.schoolId();
		Connection conn=DataBaseConnection.javaConnection();
		financialYear=obj1.selectedSessionDetails(schId,conn);
		
		liabiltie_total=mtd.alldetailtotal("Liablities",financialYear,conn);
		expense_total=mtd.alldetailtotal("EXPENSES",financialYear,conn);
		income_total=mtd.alldetailtotal("income",financialYear,conn);
		assets_total=mtd.alldetailtotal("ASSETS",financialYear,conn);
		int hmo=0;
		list5=mtd.prename();
		for( hmo=0;hmo<4;hmo++)
		{
			prename=list5.get(hmo);
		}
		{

			list1= mtd.listofid(list5.get(0),financialYear,schId,conn);

			root = new DefaultTreeNode("Root Node", null);
			TreeNode income = new DefaultTreeNode("income", root);
			income.setExpanded(true);
			income.setParent(root);

			Collections.sort(list1, String.CASE_INSENSITIVE_ORDER);
			for(int i=0;i<list1.size();i++)
			{
				String h=list1.get(i);
				char[] arr=	h.toCharArray();
				String jk="";
				int flag=0;
				int h1=Integer.valueOf(list1.get(i).length());
				for(int j=0;j<arr.length;j++)
				{
					if(flag==0)
					{
						jk=jk+arr[j];
						flag=1;
					}

					else
					{
						jk=jk+","+arr[j];
					}


				}
				list3.add(jk);
				name= mtd.name(jk,financialYear,schId,conn);


				switch(h1)
				{
				case 2:
					A1 = new DefaultTreeNode(name, income);
					A1.setExpanded(true);
					A1.setParent(income);
					break;
				case 3:
					A2 = new DefaultTreeNode(name, A1);
					A2.setExpanded(true);
					A2.setParent(A1);
					break;
				case 4:
					A3 = new DefaultTreeNode(name,A2);
					A3.setExpanded(true);
					A3.setParent(A2);
					break;

				case 5:
					A4 = new DefaultTreeNode(name, A3);
					A4.setExpanded(true);
					A4.setParent(A3);
					break;

				case 6:
					A5 = new DefaultTreeNode(name, A4);
					A5.setExpanded(true);
					A5.setParent(A4);
					break;


				case 7:
					A6 = new DefaultTreeNode(name, A5);
					A6.setExpanded(true);
					A6.setParent(A5);
					break;


				case 8:
					A7 = new DefaultTreeNode(name, A6);
					A7.setExpanded(true);
					A7.setParent(A6);
					break;


				}
			}


			list4= mtd.name1(list3,financialYear,conn);

		}

		{

			list1= mtd.listofid(list5.get(1),financialYear,schId,conn);

			root1 = new DefaultTreeNode("Root Node", null);
			TreeNode income = new DefaultTreeNode("Liablities", root1);
			income.setExpanded(true);
			income.setParent(root1);

			Collections.sort(list1, String.CASE_INSENSITIVE_ORDER);
			// //// // System.out.println(list1);
			for(int i=0;i<list1.size();i++)
			{
				String h=list1.get(i);
				char[] arr=	h.toCharArray();
				String jk="";
				int flag=0;
				int h1=Integer.valueOf(list1.get(i).length());
				for(int j=0;j<arr.length;j++)
				{
					if(flag==0)
					{
						jk=jk+arr[j];
						flag=1;
					}

					else
					{
						jk=jk+","+arr[j];
					}


				}
				// //// // System.out.println(jk);
				list2.add(jk);
				name= mtd.name(jk,financialYear,schId,conn);
				// //// // System.out.println(name);



				// //// // System.out.println(h1);





				switch(h1)
				{
				case 2:
					A1 = new DefaultTreeNode(name, income);
					A1.setExpanded(true);
					A1.setParent(income);
					break;
				case 3:
					A2 = new DefaultTreeNode(name, A1);
					A2.setExpanded(true);
					A2.setParent(A1);
					break;
				case 4:
					A3 = new DefaultTreeNode(name,A2);
					A3.setExpanded(true);
					A3.setParent(A2);
					break;

				case 5:
					A4 = new DefaultTreeNode(name, A3);
					A4.setExpanded(true);
					A4.setParent(A3);
					break;

				case 6:
					A5 = new DefaultTreeNode(name, A4);
					A5.setExpanded(true);
					A5.setParent(A4);
					break;


				case 7:
					A6 = new DefaultTreeNode(name, A5);
					A6.setExpanded(true);
					A6.setParent(A5);
					break;


				case 8:
					A7 = new DefaultTreeNode(name, A6);
					A7.setExpanded(true);
					A7.setParent(A6);
					break;

				}
			}
			// //// // System.out.println(list2);
			list6= mtd.name1(list2,financialYear,conn);

		}

		{

			list1= mtd.listofid(list5.get(2),financialYear,schId,conn);

			root2 = new DefaultTreeNode("Root Node", null);
			TreeNode income = new DefaultTreeNode("ASSETS", root2);
			income.setExpanded(true);
			income.setParent(root2);

			Collections.sort(list1, String.CASE_INSENSITIVE_ORDER);
			// //// // System.out.println(list1);
			for(int i=0;i<list1.size();i++)
			{
				String h=list1.get(i);
				char[] arr=	h.toCharArray();
				String jk="";
				int flag=0;
				int h1=Integer.valueOf(list1.get(i).length());
				for(int j=0;j<arr.length;j++)
				{
					if(flag==0)
					{
						jk=jk+arr[j];
						flag=1;
					}

					else
					{
						jk=jk+","+arr[j];
					}


				}
				// //// // System.out.println(jk);
				list9.add(jk);
				name= mtd.name(jk,financialYear,schId,conn);
				// //// // System.out.println(name);



				// //// // System.out.println(h1);





				switch(h1)
				{
				case 2:
					A1 = new DefaultTreeNode(name, income);
					A1.setExpanded(true);
					A1.setParent(income);
					break;
				case 3:
					A2 = new DefaultTreeNode(name, A1);
					A2.setExpanded(true);
					A2.setParent(A1);
					break;
				case 4:
					A3 = new DefaultTreeNode(name,A2);
					A3.setExpanded(true);
					A3.setParent(A2);
					break;

				case 5:
					A4 = new DefaultTreeNode(name, A3);
					A4.setExpanded(true);
					A4.setParent(A3);
					break;

				case 6:
					A5 = new DefaultTreeNode(name, A4);
					A5.setExpanded(true);
					A5.setParent(A4);
					break;


				case 7:
					A6 = new DefaultTreeNode(name, A5);
					A6.setExpanded(true);
					A6.setParent(A5);
					break;


				case 8:
					A7 = new DefaultTreeNode(name, A6);
					A7.setExpanded(true);
					A7.setParent(A6);
					break;

				}
			}
			// //// // System.out.println(list2);
			list7= mtd.name1(list9,financialYear,conn);

		}

		{

			list1= mtd.listofid(list5.get(3),financialYear,schId,conn);

			root3 = new DefaultTreeNode("Root Node", null);
			TreeNode income = new DefaultTreeNode("EXPENSES", root3);
			income.setExpanded(true);
			income.setParent(root3);

			Collections.sort(list1, String.CASE_INSENSITIVE_ORDER);
			// //// // System.out.println(list1);
			for(int i=0;i<list1.size();i++)
			{
				String h=list1.get(i);
				char[] arr=	h.toCharArray();
				String jk="";
				int flag=0;
				int h1=Integer.valueOf(list1.get(i).length());
				for(int j=0;j<arr.length;j++)
				{
					if(flag==0)
					{
						jk=jk+arr[j];
						flag=1;
					}

					else
					{
						jk=jk+","+arr[j];
					}


				}
				// //// // System.out.println(jk);
				list10.add(jk);
				name= mtd.name(jk,financialYear,schId,conn);
				// //// // System.out.println(name);



				// //// // System.out.println(h1);





				switch(h1)
				{
				case 2:
					A1 = new DefaultTreeNode(name, income);
					A1.setExpanded(true);
					A1.setParent(income);
					break;
				case 3:
					A2 = new DefaultTreeNode(name, A1);
					A2.setExpanded(true);
					A2.setParent(A1);
					break;
				case 4:
					A3 = new DefaultTreeNode(name,A2);
					A3.setExpanded(true);
					A3.setParent(A2);
					break;

				case 5:
					A4 = new DefaultTreeNode(name, A3);
					A4.setExpanded(true);
					A4.setParent(A3);
					break;

				case 6:
					A5 = new DefaultTreeNode(name, A4);
					A5.setExpanded(true);
					A5.setParent(A4);
					break;


				case 7:
					A6 = new DefaultTreeNode(name, A5);
					A6.setExpanded(true);
					A6.setParent(A5);
					break;


				case 8:
					A7 = new DefaultTreeNode(name, A6);
					A7.setExpanded(true);
					A7.setParent(A6);
					break;

				}
			}
			// //// // System.out.println(list2);
			list8= mtd.name1(list10,financialYear,conn);

		}
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void  refreshval()
	{
		Connection conn=DataBaseConnection.javaConnection();
		maincatelist= mtd.mainlist();
		//		HttpSession ss1=(HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		//	    financialYear=(String) ss1.getAttribute("financialYear");
		
		

		liabiltie_total=mtd.alldetailtotal("Liablities",financialYear,conn);
		expense_total=mtd.alldetailtotal("EXPENSES",financialYear,conn);
		income_total=mtd.alldetailtotal("income",financialYear,conn);
		assets_total=mtd.alldetailtotal("ASSETS",financialYear,conn);
		int hmo=0;
		list5=new Method().prename();
		for( hmo=0;hmo<4;hmo++)
		{
			prename=list5.get(hmo);
		}
		{

			list1= mtd.listofid(list5.get(0),financialYear,schId,conn);

			root = new DefaultTreeNode("Root Node", null);
			TreeNode income = new DefaultTreeNode("income", root);
			income.setExpanded(true);
			income.setParent(root);

			Collections.sort(list1, String.CASE_INSENSITIVE_ORDER);
			for(int i=0;i<list1.size();i++)
			{
				String h=list1.get(i);
				char[] arr=	h.toCharArray();
				String jk="";
				int flag=0;
				int h1=Integer.valueOf(list1.get(i).length());
				for(int j=0;j<arr.length;j++)
				{
					if(flag==0)
					{
						jk=jk+arr[j];
						flag=1;
					}

					else
					{
						jk=jk+","+arr[j];
					}


				}
				list3.add(jk);
				name= mtd.name(jk,financialYear,schId,conn);



				switch(h1)
				{
				case 2:
					A1 = new DefaultTreeNode(name, income);
					A1.setExpanded(true);
					A1.setParent(income);
					break;
				case 3:
					A2 = new DefaultTreeNode(name, A1);
					A2.setExpanded(true);
					A2.setParent(A1);
					break;
				case 4:
					A3 = new DefaultTreeNode(name,A2);
					A3.setExpanded(true);
					A3.setParent(A2);
					break;

				case 5:
					A4 = new DefaultTreeNode(name, A3);
					A4.setExpanded(true);
					A4.setParent(A3);
					break;

				case 6:
					A5 = new DefaultTreeNode(name, A4);
					A5.setExpanded(true);
					A5.setParent(A4);
					break;


				case 7:
					A6 = new DefaultTreeNode(name, A5);
					A6.setExpanded(true);
					A6.setParent(A5);
					break;


				case 8:
					A7 = new DefaultTreeNode(name, A6);
					A7.setExpanded(true);
					A7.setParent(A6);
					break;


				}



			}



			list4= mtd.name1(list3,financialYear,conn);

		}

		{

			list1= mtd.listofid(list5.get(1),financialYear,schId,conn);

			root1 = new DefaultTreeNode("Root Node", null);
			TreeNode income = new DefaultTreeNode("Liablities", root1);
			income.setExpanded(true);
			income.setParent(root1);

			Collections.sort(list1, String.CASE_INSENSITIVE_ORDER);
			// //// // System.out.println(list1);
			for(int i=0;i<list1.size();i++)
			{
				String h=list1.get(i);
				char[] arr=	h.toCharArray();
				String jk="";
				int flag=0;
				int h1=Integer.valueOf(list1.get(i).length());
				for(int j=0;j<arr.length;j++)
				{
					if(flag==0)
					{
						jk=jk+arr[j];
						flag=1;
					}

					else
					{
						jk=jk+","+arr[j];
					}


				}
				// //// // System.out.println(jk);
				list2.add(jk);
				name= mtd.name(jk,financialYear,schId,conn);
				// //// // System.out.println(name);



				// //// // System.out.println(h1);





				switch(h1)
				{
				case 2:
					A1 = new DefaultTreeNode(name, income);
					A1.setExpanded(true);
					A1.setParent(income);
					break;
				case 3:
					A2 = new DefaultTreeNode(name, A1);
					A2.setExpanded(true);
					A2.setParent(A1);
					break;
				case 4:
					A3 = new DefaultTreeNode(name,A2);
					A3.setExpanded(true);
					A3.setParent(A2);
					break;

				case 5:
					A4 = new DefaultTreeNode(name, A3);
					A4.setExpanded(true);
					A4.setParent(A3);
					break;

				case 6:
					A5 = new DefaultTreeNode(name, A4);
					A5.setExpanded(true);
					A5.setParent(A4);
					break;


				case 7:
					A6 = new DefaultTreeNode(name, A5);
					A6.setExpanded(true);
					A6.setParent(A5);
					break;


				case 8:
					A7 = new DefaultTreeNode(name, A6);
					A7.setExpanded(true);
					A7.setParent(A6);
					break;

				}
			}
			// //// // System.out.println(list2);
			list6= mtd.name1(list2,financialYear,conn);

		}

		{

			list1= mtd.listofid(list5.get(2),financialYear,schId,conn);

			root2 = new DefaultTreeNode("Root Node", null);
			TreeNode income = new DefaultTreeNode("ASSETS", root2);
			income.setExpanded(true);
			income.setParent(root2);

			Collections.sort(list1, String.CASE_INSENSITIVE_ORDER);
			// //// // System.out.println(list1);
			for(int i=0;i<list1.size();i++)
			{
				String h=list1.get(i);
				char[] arr=	h.toCharArray();
				String jk="";
				int flag=0;
				int h1=Integer.valueOf(list1.get(i).length());
				for(int j=0;j<arr.length;j++)
				{
					if(flag==0)
					{
						jk=jk+arr[j];
						flag=1;
					}

					else
					{
						jk=jk+","+arr[j];
					}


				}
				// //// // System.out.println(jk);
				list9.add(jk);
				name= mtd.name(jk,financialYear,schId,conn);
				// //// // System.out.println(name);



				// //// // System.out.println(h1);





				switch(h1)
				{
				case 2:
					A1 = new DefaultTreeNode(name, income);
					A1.setExpanded(true);
					A1.setParent(income);
					break;
				case 3:
					A2 = new DefaultTreeNode(name, A1);
					A2.setExpanded(true);
					A2.setParent(A1);
					break;
				case 4:
					A3 = new DefaultTreeNode(name,A2);
					A3.setExpanded(true);
					A3.setParent(A2);
					break;

				case 5:
					A4 = new DefaultTreeNode(name, A3);
					A4.setExpanded(true);
					A4.setParent(A3);
					break;

				case 6:
					A5 = new DefaultTreeNode(name, A4);
					A5.setExpanded(true);
					A5.setParent(A4);
					break;


				case 7:
					A6 = new DefaultTreeNode(name, A5);
					A6.setExpanded(true);
					A6.setParent(A5);
					break;


				case 8:
					A7 = new DefaultTreeNode(name, A6);
					A7.setExpanded(true);
					A7.setParent(A6);
					break;

				}
			}
			// //// // System.out.println(list2);
			list7= mtd.name1(list9,financialYear,conn);

		}

		{

			list1= mtd.listofid(list5.get(3),financialYear,schId,conn);

			root3 = new DefaultTreeNode("Root Node", null);
			TreeNode income = new DefaultTreeNode("EXPENSES", root3);
			income.setExpanded(true);
			income.setParent(root3);

			Collections.sort(list1, String.CASE_INSENSITIVE_ORDER);
			// //// // System.out.println(list1);
			for(int i=0;i<list1.size();i++)
			{
				String h=list1.get(i);
				char[] arr=	h.toCharArray();
				String jk="";
				int flag=0;
				int h1=Integer.valueOf(list1.get(i).length());
				for(int j=0;j<arr.length;j++)
				{
					if(flag==0)
					{
						jk=jk+arr[j];
						flag=1;
					}

					else
					{
						jk=jk+","+arr[j];
					}


				}
				// //// // System.out.println(jk);
				list10.add(jk);
				name= mtd.name(jk,financialYear,schId,conn);
				// //// // System.out.println(name);



				// //// // System.out.println(h1);





				switch(h1)
				{
				case 2:
					A1 = new DefaultTreeNode(name, income);
					A1.setExpanded(true);
					A1.setParent(income);
					break;
				case 3:
					A2 = new DefaultTreeNode(name, A1);
					A2.setExpanded(true);
					A2.setParent(A1);
					break;
				case 4:
					A3 = new DefaultTreeNode(name,A2);
					A3.setExpanded(true);
					A3.setParent(A2);
					break;

				case 5:
					A4 = new DefaultTreeNode(name, A3);
					A4.setExpanded(true);
					A4.setParent(A3);
					break;

				case 6:
					A5 = new DefaultTreeNode(name, A4);
					A5.setExpanded(true);
					A5.setParent(A4);
					break;


				case 7:
					A6 = new DefaultTreeNode(name, A5);
					A6.setExpanded(true);
					A6.setParent(A5);
					break;


				case 8:
					A7 = new DefaultTreeNode(name, A6);
					A7.setExpanded(true);
					A7.setParent(A6);
					break;

				}
			}
			// //// // System.out.println(list2);
			list8= mtd.name1(list10,financialYear,conn);

		}


		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/*public void tet()
	{
		TreeNode hhhh=val;
		String seq="";
		// //// // System.out.println(val);
	    do
	    {
	    	hhhh= hhhh.getParent();
	    	// //// // System.out.println(hhhh);
	    	seq=seq+hhhh.toString();
	    }while(!hhhh.toString().equals("Root Node"));
	    // //// // System.out.println(val+seq);
	    // //// // System.out.println(amount);
	}
	 */
	public String chko()
	{
		if(maincate.equals("income"))
		{			// //// // System.out.println(maincate);

			PrimeFaces context = PrimeFaces.current();
			context.executeInitScript("PF('dlg1').show();");

		}
		else
			if(maincate.equals("Liablities"))
			{
				// //// // System.out.println(catename);
				PrimeFaces context = PrimeFaces.current();
				context.executeInitScript("PF('dlg2').show();");
			}
			else
				if(maincate.equals("ASSETS"))
				{
					// //// // System.out.println(catename);
					PrimeFaces context = PrimeFaces.current();
					context.executeInitScript("PF('dlg3').show();");
				}
				else
					if(maincate.equals("EXPENSES"))
					{
						// //// // System.out.println(catename);
						PrimeFaces context = PrimeFaces.current();
						context.executeInitScript("PF('dlg4').show();");
					}
		return "date.xhmtl";

	}


	public String submit()
	{
		int ss;
		TreeNode hhhh = null;


		Connection conn=DataBaseConnection.javaConnection();
		if(maincate.equals("income"))
		{
			hhhh=val;
		}
		else
			if(maincate.equals("Liablities"))
			{
				hhhh=val1;
			}
			else
				if(maincate.equals("ASSETS"))
				{
					hhhh=val2;
				}

				else
					if(maincate.equals("EXPENSES"))
					{
						hhhh=val3;
					}

		String seq = "";
		String last=hhhh.toString();
		do
		{
			hhhh= hhhh.getParent();
			seq=seq+","+hhhh.toString();
		}while(!hhhh.toString().equals("Root Node"));

		String   seqna=last+seq;
		int flag=0;

		String temp[]=seqna.split(",");
		for(int i=temp.length-2;i>=0;i--)
		{
			if(flag==0)
			{
				category1=category1+temp[i];
				flag=1;
			}
			else
			{
				category1=category1+","+temp[i];
			}
		}

		if(!catename.equals("") && !category1.equals("") )
		{
			ss= mtd.testing1(category1,catename,maincate,financialYear,schId,conn);
			if(ss==1)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Added Successfully"));
				category1="";
				catename="";
				refreshval();
				try {
					conn.close();
				} catch (Exception e) {
					e.printStackTrace();
				}

				return "date.xhtml";
			}

			else
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("already Added"));
				category1="";
				catename="";
				return "date.xhtml";
			}

		}
		else
		{
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("error"));
			category1="";
			catename="";
			return "date.xhtml";
		}



	}









	public String getCatename() {
		return catename;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public void setCatename(String catename) {
		this.catename = catename;
	}
	public ArrayList<SelectItem> getCatelist() {
		return catelist;
	}
	public UserInfo getSelected() {
		return selected;
	}
	public void setSelected(UserInfo selected) {
		this.selected = selected;
	}
	public void setCatelist(ArrayList<SelectItem> catelist) {
		this.catelist = catelist;
	}
	public String getCategory1() {
		return category1;
	}
	public void setCategory1(String category1) {
		this.category1 = category1;
	}
	public boolean isChk() {
		return chk;
	}
	public void setChk(boolean chk) {
		this.chk = chk;
	}
	public boolean isChk1() {
		return chk1;
	}
	public void setChk1(boolean chk1) {
		this.chk1 = chk1;
	}

	public String getMaincate() {
		return maincate;
	}

	public void setMaincate(String maincate) {
		this.maincate = maincate;
	}

	public ArrayList<SelectItem> getMaincatelist() {
		return maincatelist;
	}

	public void setMaincatelist(ArrayList<SelectItem> maincatelist) {
		this.maincatelist = maincatelist;
	}



	public TreeNode getVal() {
		return val;
	}
	public void setVal(TreeNode val) {
		this.val = val;
	}
	public TreeNode getVal1() {
		return val1;
	}
	public void setVal1(TreeNode val1) {
		this.val1 = val1;
	}
	public TreeNode getVal2() {
		return val2;
	}
	public void setVal2(TreeNode val2) {
		this.val2 = val2;
	}
	public TreeNode getVal3() {
		return val3;
	}
	public void setVal3(TreeNode val3) {
		this.val3 = val3;
	}

	public TreeNode getRoot() {
		return root;
	}

	public void setRoot(TreeNode root) {
		this.root = root;
	}

	public ArrayList<String> getList1() {
		return list1;
	}

	public void setList1(ArrayList<String> list1) {
		this.list1 = list1;
	}

	public ArrayList<DetailsInfo> getList4() {
		return list4;
	}

	public void setList4(ArrayList<DetailsInfo> list4) {
		this.list4 = list4;
	}



	public TreeNode getRoot1() {
		return root1;
	}



	public void setRoot1(TreeNode root1) {
		this.root1 = root1;
	}



	public ArrayList<DetailsInfo> getList6() {
		return list6;
	}



	public void setList6(ArrayList<DetailsInfo> list6) {
		this.list6 = list6;
	}

	public TreeNode getRoot2() {
		return root2;
	}

	public void setRoot2(TreeNode root2) {
		this.root2 = root2;
	}

	public TreeNode getRoot3() {
		return root3;
	}

	public void setRoot3(TreeNode root3) {
		this.root3 = root3;
	}

	public ArrayList<DetailsInfo> getList7() {
		return list7;
	}

	public void setList7(ArrayList<DetailsInfo> list7) {
		this.list7 = list7;
	}

	public ArrayList<DetailsInfo> getList8() {
		return list8;
	}

	public void setList8(ArrayList<DetailsInfo> list8) {
		this.list8 = list8;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}
	

}
