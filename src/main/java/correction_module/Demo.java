package correction_module;

import java.sql.Connection;

import Json.DataBaseMeathodJson;
import schooldata.DataBaseConnection;

public class Demo {

	public static void main(String[] args) {
		String typemessage = "Dear Parent,\n"+ "Your login OTP is 12354 valid for 5 mins.\n-CHKBOX";
		
		Connection conn=DataBaseConnection.javaConnection(); 
		
		new DataBaseMeathodJson().messageUrlWithTemplate("9660970717", typemessage,"CB115333", "302", conn,"1207161779131382060");
		new DataBaseMeathodJson().messageUrlWithTemplate("9053278759", typemessage,"CB115277", "302", conn,"1207161779131382060");
		new DataBaseMeathodJson().messageUrlWithTemplate("8453010203", typemessage,"CB110860", "302", conn,"1207161779131382060");
		new DataBaseMeathodJson().messageUrlWithTemplate("8058100200", typemessage,"CB110853", "302", conn,"1207161779131382060");
	
	}
}

