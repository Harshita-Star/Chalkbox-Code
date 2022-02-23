package schooldata;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

@ManagedBean(name="deleteTablesData")
@ViewScoped
public class DeleteTablesDataBean implements Serializable{
	
	ArrayList<String> list = new ArrayList<String>();
	
	ArrayList<SchoolInfo> schooList = new ArrayList<SchoolInfo>(); 
	DatabaseMethods1 dbm = new DatabaseMethods1();
	SchoolInfo selectedRow;
	
	
  public DeleteTablesDataBean()
  {
	  Connection con = DataBaseConnection.javaConnection();
	  
	  schooList = dbm.allSchoolList(con);
	  list = dbm.allTablesNotToDelete(con);

	 
	  
	  try {
		con.close();
	} catch (Exception e) {
		e.printStackTrace();
	}
	  
  }

 public  void delete()
{

  int j=1;
  Connection con = DataBaseConnection.javaConnection();
  String schid = selectedRow.getId();
 

  try 
  {
    DatabaseMetaData md = con.getMetaData();
    ResultSet rs = md.getTables(null, null, "%", null);
  
    while (rs.next())
    {
    	 // System.out.println(rs.getString(3));
     if(!list.contains(rs.getString(3)))
     {  
      PreparedStatement stmt = null;

      try 
      {
	 
           String qu1 = "delete from "+con.getCatalog()+"."+rs.getString(3)+" where schid='"+schid+"'";
            // System.out.println(qu1);
           stmt = con.prepareStatement(qu1);

           j=stmt.executeUpdate();

      } 
      catch (SQLException ee) {
		ee.printStackTrace();  
	   }
      catch (Exception e) 
      {
        e.printStackTrace();

      } 
      finally
      {
       if (stmt != null) 
       {
        try
        {
           stmt.close();
        }
        catch (SQLException e) 
        {
          e.printStackTrace();
        }
      }
    }
 
  }
 }
} 
  catch (Exception e) {
e.printStackTrace();
}

   
 if(j>0)
 {
	 FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Deletion Successful"));
	 
 }
  
try {
con.close();
} catch (Exception e) {
 e.printStackTrace();
}

}



public ArrayList<SchoolInfo> getSchooList() {
	return schooList;
}

public void setSchooList(ArrayList<SchoolInfo> schooList) {
	this.schooList = schooList;
}

public SchoolInfo getSelectedRow() {
	return selectedRow;
}

public void setSelectedRow(SchoolInfo selectedRow) {
	this.selectedRow = selectedRow;
}


 
 

}
