package schooldata;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.faces.model.SelectItem;

public class RFIDDataChangeCore {

	public static void main(String[] args) 
	{
		Connection conn = DataBaseConnection.javaConnection();
		
		ArrayList<SelectItem> list = new ArrayList<>();
		SelectItem ss = new SelectItem();
		ss.setLabel("866192031894650"); //imei
		ss.setValue("69"); //busid
		list.add(ss);
		
		ss = new SelectItem();
		ss.setLabel("866192031711771");
		ss.setValue("75");
		list.add(ss);
		
		ss = new SelectItem();
		ss.setLabel("866192031828906");
		ss.setValue("61");
		list.add(ss);
				
		ss = new SelectItem();
		ss.setLabel("866192031858044");
		ss.setValue("66");
		list.add(ss);
		
		ss = new SelectItem();
		ss.setLabel("866192031893868");
		ss.setValue("68");
		list.add(ss);
		
		ss = new SelectItem();
		ss.setLabel("866192032055756");
		ss.setValue("67");
		list.add(ss);
		
		ss = new SelectItem();
		ss.setLabel("866192031655770");
		ss.setValue("65");
		list.add(ss);
		
		ss = new SelectItem();
		ss.setLabel("869286034174842");
		ss.setValue("72");
		list.add(ss);
		
		ss = new SelectItem();
		ss.setLabel("866192031939505");
		ss.setValue("70");
		list.add(ss);
		
		ss = new SelectItem();
		ss.setLabel("866192032056119");
		ss.setValue("74");
		list.add(ss);
		
		ss = new SelectItem();
		ss.setLabel("866192031939554");
		ss.setValue("71");
		list.add(ss);
		
		ss = new SelectItem();
		ss.setLabel("866192031828914");  ///////////////////////////
		ss.setValue("73");
		list.add(ss);
		
		ss = new SelectItem();
		ss.setLabel("866192032055624");  ///////////////////////////
		ss.setValue("173");
		list.add(ss);
		
		Statement st1 = null,st2=null,st3=null,st4=null;
		String q1="",q2="",q3="",q4="";
		
		try
		{
			for(SelectItem si : list)
			{
				st1 = conn.createStatement();
				st2 = conn.createStatement();
				st3 = conn.createStatement();
				st4 = conn.createStatement();
				q1 = "UPDATE rfid_report set out_bus_morn_imei='"+si.getValue()+"-Transport' WHERE out_bus_morn_imei='"+si.getLabel()+"-Transport' and action_date >= '2019-11-01' AND schid = '224'";
				q2 = "UPDATE rfid_report set out_bus_even_imei='"+si.getValue()+"-Transport' WHERE out_bus_even_imei='"+si.getLabel()+"-Transport' and action_date >= '2019-11-01' AND schid = '224'";
				q3 = "UPDATE rfid_report set in_bus_morn_imei='"+si.getValue()+"-Transport' WHERE in_bus_morn_imei='"+si.getLabel()+"-Transport' and action_date >= '2019-11-01' AND schid = '224'";
				q4 = "UPDATE rfid_report set in_bus_even_imei='"+si.getValue()+"-Transport' WHERE in_bus_even_imei='"+si.getLabel()+"-Transport' and action_date >= '2019-11-01' AND schid = '224'";
				
				st1.executeUpdate(q1);
				st2.executeUpdate(q2);
				st3.executeUpdate(q3);
				st4.executeUpdate(q4);
				
				//// // System.out.println(si.getLabel()+"....."+si.getValue());
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		finally
		{
			try 
			{
				if(st1!=null)
				{
					st1.close();
				}
				
				if(st2!=null)
				{
					st2.close();
				}
				
				if(st3!=null)
				{
					st3.close();
				}
				
				if(st4!=null)
				{
					st4.close();
				}
			}
			catch (Exception e2)
			{
				e2.printStackTrace();
			}
			
		}
		
		
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
///BLMSR
	
	/*ss.setLabel("866192031894494"); //imei
	ss.setValue("208"); //busid
	list.add(ss);
	
	ss = new SelectItem();
	ss.setLabel("866192031940743");
	ss.setValue("195");
	list.add(ss);
	
	ss = new SelectItem();
	ss.setLabel("869286034101860");
	ss.setValue("203");
	list.add(ss);
	
	ss = new SelectItem();
	ss.setLabel("869286034187422");
	ss.setValue("209");
	list.add(ss);
	
	ss = new SelectItem();
	ss.setLabel("869286034180401");
	ss.setValue("210");
	list.add(ss);
	
	ss = new SelectItem();
	ss.setLabel("866192031924150");
	ss.setValue("204");
	list.add(ss);
	
	ss = new SelectItem();
	ss.setLabel("866192031858101");
	ss.setValue("190");
	list.add(ss);
	
	ss = new SelectItem();
	ss.setLabel("866192031858085");
	ss.setValue("196");
	list.add(ss);
	
	ss = new SelectItem();
	ss.setLabel("866192031858531");
	ss.setValue("206");
	list.add(ss);
	
	ss = new SelectItem();
	ss.setLabel("866192031815473");
	ss.setValue("199");
	list.add(ss);
	
	ss = new SelectItem();
	ss.setLabel("869286034187992");
	ss.setValue("201");
	list.add(ss);
	
	ss = new SelectItem();
	ss.setLabel("866192031939604");
	ss.setValue("197");
	list.add(ss);
	
	ss = new SelectItem();
	ss.setLabel("866192031829060");
	ss.setValue("191");
	list.add(ss);
	
	ss = new SelectItem();
	ss.setLabel("866192031894544");
	ss.setValue("198");
	list.add(ss);
	
	ss = new SelectItem();
	ss.setLabel("869286034212089");
	ss.setValue("211");
	list.add(ss);
	
	ss = new SelectItem();
	ss.setLabel("866192031858697");
	ss.setValue("207");
	list.add(ss);
	
	ss = new SelectItem();
	ss.setLabel("866192031939984");
	ss.setValue("202");
	list.add(ss);
	
	ss = new SelectItem();
	ss.setLabel("866192031924226");
	ss.setValue("205");
	list.add(ss);
	
	ss = new SelectItem();
	ss.setLabel("866192031858507");
	ss.setValue("193");
	list.add(ss);
	
	ss = new SelectItem();
	ss.setLabel("866192031816133");
	ss.setValue("189");
	list.add(ss);
	
	ss = new SelectItem();
	ss.setLabel("866192031658303");
	ss.setValue("192");
	list.add(ss);
	
	ss = new SelectItem();
	ss.setLabel("866192032055608");
	ss.setValue("200");
	list.add(ss);
	
	ss = new SelectItem();
	ss.setLabel("866192031815457");
	ss.setValue("194");
	list.add(ss);*/
	
	////BLM BLOOMING
	
	/*ss.setLabel("866192031894494"); //imei
	ss.setValue("238"); //busid
	list.add(ss);
	
	ss = new SelectItem();
	ss.setLabel("866192031940743");
	ss.setValue("225");
	list.add(ss);
	
	ss = new SelectItem();
	ss.setLabel("869286034101860");
	ss.setValue("233");
	list.add(ss);
	
	ss = new SelectItem();
	ss.setLabel("869286034187422");
	ss.setValue("239");
	list.add(ss);
	
	ss = new SelectItem();
	ss.setLabel("869286034180401");
	ss.setValue("240");
	list.add(ss);
	
	ss = new SelectItem();
	ss.setLabel("866192031924150");
	ss.setValue("234");
	list.add(ss);
	
	ss = new SelectItem();
	ss.setLabel("866192031858101");
	ss.setValue("220");
	list.add(ss);
	
	ss = new SelectItem();
	ss.setLabel("866192031858085");
	ss.setValue("226");
	list.add(ss);
	
	ss = new SelectItem();
	ss.setLabel("866192031858531");
	ss.setValue("236");
	list.add(ss);
	
	ss = new SelectItem();
	ss.setLabel("866192031815473");
	ss.setValue("229");
	list.add(ss);
	
	ss = new SelectItem();
	ss.setLabel("869286034187992");
	ss.setValue("231");
	list.add(ss);
	
	ss = new SelectItem();
	ss.setLabel("866192031939604");
	ss.setValue("227");
	list.add(ss);
	
	ss = new SelectItem();
	ss.setLabel("866192031829060");
	ss.setValue("221");
	list.add(ss);
	
	ss = new SelectItem();
	ss.setLabel("866192031894544");
	ss.setValue("228");
	list.add(ss);
	
	ss = new SelectItem();
	ss.setLabel("869286034212089");
	ss.setValue("241");
	list.add(ss);
	
	ss = new SelectItem();
	ss.setLabel("866192031858697");
	ss.setValue("237");
	list.add(ss);
	
	ss = new SelectItem();
	ss.setLabel("866192031939984");
	ss.setValue("232");
	list.add(ss);
	
	ss = new SelectItem();
	ss.setLabel("866192031924226");
	ss.setValue("235");
	list.add(ss);
	
	ss = new SelectItem();
	ss.setLabel("866192031858507");
	ss.setValue("223");
	list.add(ss);
	
	ss = new SelectItem();
	ss.setLabel("866192031816133");
	ss.setValue("219");
	list.add(ss);
	
	ss = new SelectItem();
	ss.setLabel("866192031658303");
	ss.setValue("222");
	list.add(ss);
	
	ss = new SelectItem();
	ss.setLabel("866192032055608");
	ss.setValue("230");
	list.add(ss);
	
	ss = new SelectItem();
	ss.setLabel("866192031815457");
	ss.setValue("224");
	list.add(ss);*/
	
	/////UNIVERSAL
	
	/*ss.setLabel("866192031894650"); //imei
	ss.setValue("69"); //busid
	list.add(ss);
	
	ss = new SelectItem();
	ss.setLabel("866192031711771");
	ss.setValue("75");
	list.add(ss);
	
	ss = new SelectItem();
	ss.setLabel("866192031828906");
	ss.setValue("61");
	list.add(ss);
			
	ss = new SelectItem();
	ss.setLabel("866192031858044");
	ss.setValue("66");
	list.add(ss);
	
	ss = new SelectItem();
	ss.setLabel("866192031893868");
	ss.setValue("68");
	list.add(ss);
	
	ss = new SelectItem();
	ss.setLabel("866192032055756");
	ss.setValue("67");
	list.add(ss);
	
	ss = new SelectItem();
	ss.setLabel("866192031655770");
	ss.setValue("65");
	list.add(ss);
	
	ss = new SelectItem();
	ss.setLabel("869286034174842");
	ss.setValue("72");
	list.add(ss);
	
	ss = new SelectItem();
	ss.setLabel("866192031939505");
	ss.setValue("70");
	list.add(ss);
	
	ss = new SelectItem();
	ss.setLabel("866192032056119");
	ss.setValue("74");
	list.add(ss);
	
	ss = new SelectItem();
	ss.setLabel("866192031939554");
	ss.setValue("71");
	list.add(ss);
	
	ss = new SelectItem();
	ss.setLabel("866192031828914");  ///////////////////////////
	ss.setValue("73");
	list.add(ss);
		
	ss = new SelectItem();
	ss.setLabel("866192032055624");  ///////////////////////////
	ss.setValue("173");
	list.add(ss);*/	
/*
	UPDATE rfid_report set out_bus_morn_imei='194-Transport' WHERE out_bus_morn_imei='866192031815457-Transport' and action_date >= '2019-11-01' AND schid LIKE '251';
	UPDATE rfid_report set out_bus_even_imei='194-Transport' WHERE out_bus_even_imei='866192031815457-Transport' and action_date >= '2019-11-01' AND schid LIKE '251';
	UPDATE rfid_report set in_bus_morn_imei='194-Transport' WHERE in_bus_morn_imei='866192031815457-Transport' and action_date >= '2019-11-01' AND schid LIKE '251';
	UPDATE rfid_report set in_bus_even_imei='194-Transport' WHERE in_bus_even_imei='866192031815457-Transport' and action_date >= '2019-11-01' AND schid LIKE '251';
*/

}
