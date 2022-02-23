package correction_module;

import schooldata.DatabaseMethods1;

public class PasswordGeneration {
static DatabaseMethods1 dd=new DatabaseMethods1();
	
	
	public static void main(String[] args) 
	{
		String encrypt="GnF1XS0IVmc=";
		String ss=dd.xorMessage(dd.base64decode(encrypt),dd.pwdkey);
		  System.out.println("password.."+ss);
		
//		String txt = dd.xorMessage("9829170548", dd.pwdkey);
//		String newpassword = dd.base64encode(txt);
		 // System.out.println(newpassword);
	}
}
