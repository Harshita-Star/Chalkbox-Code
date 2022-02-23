package Json;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.model.SelectItem;

import schooldata.ClassInfo;
import schooldata.DataBaseConnection;
import schooldata.SchoolInfoList;

public class NewsJson {

	SchoolInfoList ls = new SchoolInfoList();
	ArrayList<SelectItem> classSection,sectionList, subjectList;
	ArrayList<ClassInfo> selectedClassList;
	DataBaseMeathodJson DBJ=new  DataBaseMeathodJson();


	public void addNews(String concern,String schoolid,String type,String classes,String image)
	{

		Connection conn=DataBaseConnection.javaConnection();
		try {
			String name=image;
			selectedClassList = new ArrayList<>();

			if(type.equalsIgnoreCase("classwise"))
			{
				String clsSecArr[] = classes.split(",");
				String cls = "";
				String sec = "";
				for(int x=0;x<clsSecArr.length;x++)
				{
					String tempArr[] = clsSecArr[x].split("-");
					cls = tempArr[0];
					sec = tempArr[1];
					ClassInfo cc = new ClassInfo();
					cc.setClassid(cls);
					cc.setSectionId(sec);
					selectedClassList.add(cc);
				}

			}

			int i=DBJ.news(concern,schoolid,name,conn,type,selectedClassList);

			if(i>0)
			{
				Runnable r = new Runnable()
				{
					public void run()
					{
						ls=DBJ.fullSchoolInfo(schoolid,conn);
						String concernnnn = "Dear Parent,\n"+concern+"\nRegards,"+ls.getSmsSchoolName();

						if(type.equals("all"))
						{
							DBJ.notification("News",concernnnn,schoolid,schoolid,"",conn);
						}
						else
						{
							for(ClassInfo cc : selectedClassList)
							{
								DBJ.notification("News",concernnnn,cc.getSectionId()+"-"+cc.getClassid()+"-"+schoolid,schoolid,"",conn);
							}
						}
						//DBJ.notification("News",concern,schoolid,schoolid,conn);
					}
				};

				new Thread(r).start();
			}
			else
			{
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		

	}


}
