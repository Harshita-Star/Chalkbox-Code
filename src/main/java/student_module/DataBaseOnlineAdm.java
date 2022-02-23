package student_module;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import javax.faces.model.SelectItem;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import Json.DataBaseMeathodJson;
import exam_module.ExamInfo;
import reports_module.DataBaseMethodsReports;
import schooldata.ClassTest;
import schooldata.DatabaseMethods1;
import schooldata.SchoolInfoList;
import schooldata.StudentInfo;

public class DataBaseOnlineAdm implements Serializable
{
	static DatabaseMethods1 DBM = new DatabaseMethods1();
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	public void doMail(String to,String subject,String heading,String msg)
	{
		// Recipient's email ID needs to be mentioned. - to

		// Sender's email ID needs to be mentioned
		String from = "chalkboxerp.in@gmail.com";

		// Assuming you are sending email from localhost
		String host = "smtp.gmail.com";
		String pass="chalkbox2018";
		// Get system properties
		Properties properties = System.getProperties();

		// Setup mail server

		properties.setProperty("mail.smtp.host", host);
		properties.put("mail.smtp.port", "587");
		properties.put("mail.smtp.password", pass);
		properties.put("mail.smtp.starttls.enable", "true");

		properties.put("mail.smtp.user", from);

		properties.put("mail.smtp.auth", "true");
		// Get the default Session object.
		// Session session = Session.getDefaultInstance(properties);
		Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(from,pass);
			}
		});

		try {
			// Create a default MimeMessage object.
			MimeMessage message = new MimeMessage(session);

			// Set From: header field of the header.
			message.setFrom(new InternetAddress(from));

			// Set To: header field of the header.
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

			// Set Subject: header field
			message.setSubject(subject);

			// Now set the actual message
			//  message.setText("This is actual message");
			String sb = "<head>" +
					"<style type=\"text/css\">" +
					"  .red { color: #647cb5; }" +
					"</style>" +
					"</head>" + "<div style=\"border: 2px solid #333;width: 80%;margin: auto;padding: 40px 0;background: #f0f2f4\">"+"<center><img src=\"http://chalkboxerp.in/cblogo.png\"></center>" +
					"<h2 class=\"red\">" + heading + "</h2>" +
					"<p>" + msg + "</p></div>";
			message.setContent(sb, "text/html; charset=utf-8");
			//  https://3c1703fe8d.site.internapcdn.net/newman/gfx/news/hires/2019/imagenorthea.jpg
			// Send message
			Transport.send(message);
			// ////// // System.out.println("Sent message successfully....");
		} catch (MessagingException mex) {
			mex.printStackTrace();
		}
	}

	public int signUp(String schid, String name, String mobile, String email, String unm, String pwd,Connection conn)
	{
		int i = 0;
		PreparedStatement st = null;
		try {

			String query = "insert into sign_up(name, email, mobile, unm, pwd,schid,session) values(?,?,?,?,?,?,?)";
			st = conn.prepareStatement(query);
			st.setString(1, name);
			st.setString(2, email);
			st.setString(3, mobile);
			st.setString(4, unm);
			st.setString(5, pwd);
			st.setString(6, schid);
			st.setString(7, DBM.selectedSessionDetails(schid,conn));

			i = st.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return i;
	}

	public boolean checkMobDuplicacy(String mob,String col, Connection conn) {
		Statement st = null;
		ResultSet rr = null;
		try {
			st = conn.createStatement();
			String query = "select * from sign_up where "+col+"='" + mob + "' and status='active' and session='"+DBM.selectedSessionDetails(DBM.schoolId(),conn)+"'";
			rr = st.executeQuery(query);
			if (rr.next()) {
				return true;

			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			if (rr != null) {
				try {
					rr.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	public LoginInfo signUpInfo(String id,String pwd, Connection conn) {
		Statement st = null;
		ResultSet rr = null;
		LoginInfo info = null;
		try {
			st = conn.createStatement();
			String query = "select * from sign_up where mobile='" + id + "' and pwd='"+pwd+"' and status='active' and session='"+DBM.selectedSessionDetails(DBM.schoolId(),conn)+"'";
			rr = st.executeQuery(query);
			if (rr.next()) {
				info = new LoginInfo();
				info.setName(rr.getString("name"));;
				info.setMobile(rr.getString("mobile"));
				info.setEmail(rr.getString("email"));
				info.setUnm(rr.getString("unm"));
				info.setPwd(rr.getString("pwd"));
				info.setSchid(rr.getString("schid"));

			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			if (rr != null) {
				try {
					rr.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return info;
	}

	public LoginInfo signUpInfo(String id,Connection conn) {
		Statement st = null;
		ResultSet rr = null;
		LoginInfo info = null;
		try {
			st = conn.createStatement();
			String query = "select * from sign_up where unm='" + id + "' and status='active' ";
			rr = st.executeQuery(query);
			if (rr.next()) {
				info = new LoginInfo();
				info.setName(rr.getString("name"));;
				info.setMobile(rr.getString("mobile"));
				info.setEmail(rr.getString("email"));
				info.setUnm(rr.getString("unm"));
				info.setPwd(rr.getString("pwd"));
				info.setSchid(rr.getString("schid"));

			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			if (rr != null) {
				try {
					rr.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return info;
	}


	public int insertAdmInfo(OnlineAdmInfo info,String status,Connection conn)
	{

		String query = "";

		String schoolid = DBM.schoolId();

		String admitClassName = DBM.classNameFromidSchid(schoolid,info.getClassid(), DBM.selectedSessionDetails(schoolid,conn), conn);
		info.setClassname(admitClassName);

		PreparedStatement st = null;
		ResultSet rr = null;

		query = "INSERT INTO online_adm(year, classid, classname, apply_date, st_name, gender, dob, birth_place, last_school, "
				+ "nationality, religion, lang, ppt_no, ppt_issue_date, ppt_exp_date, ppt_place, visa_no, visa_exp_date, "
				+ "visa_issue_date, other_lang, ethnicity, emid, eng_prof, independence, personal, general, demotion, sen, "
				+ "sen_file, exceptional, exceptional_rem, attention, behaviour, learning, physical, parent_type, parent_id,"
				+ " parent_pwd, applicant_name, applicant_relation, f_name, f_occupation, f_employer, f_mob, f_email, f_work, "
				+ "f_address, f_emid, m_name, m_occupation, m_employer, m_mob, m_email, m_work, m_address, m_emid, receipient, "
				+ "fee_payee, company, bill_address, comp_phone, comp_email, online_admcol, g_name, g_occupation, g_employer, "
				+ "g_mob, g_email, g_work, g_address, g_emid, g_rel, e1_name, e1_mob, e1_rel, e2_name, e2_mob, e2_rel, bus_service, "
				+ "pickup_name, f_image, m_image, g_image, st_image, food_type, login_id, student_id, schid, status, other_rel,"
				+ " address, g_nation,sch_status,remark) "
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		try {
			st = conn.prepareStatement(query,Statement.RETURN_GENERATED_KEYS);
			// st.setString(1, addmissionNumber);
			st.setString(1, info.getYear());
			st.setString(2, info.getClassid());
			st.setString(3, info.getClassname());
			st.setString(4, sdf.format(info.getApply_date()));
			st.setString(5, info.getSt_name());
			st.setString(6, info.getGender());
			st.setString(7, sdf.format(info.getDob()));
			st.setString(8, info.getBirth_place());
			st.setString(9, info.getLast_school());
			st.setString(10, info.getNationality());
			st.setString(11, info.getReligion());
			st.setString(12, info.getLang());
			st.setString(13, info.getPpt_no());
			if(info.getPpt_issue_date()==null)
			{
				st.setString(14, "");
			}
			else
			{
				st.setString(14, sdf.format(info.getPpt_issue_date()));
			}

			if(info.getPpt_exp_date()==null)
			{
				st.setString(15, "");
			}
			else
			{
				st.setString(15, sdf.format(info.getPpt_exp_date()));
			}

			st.setString(16, info.getPpt_place());
			st.setString(17, info.getVisa_no());


			if(info.getVisa_exp_date()==null)
			{
				st.setString(18, "");
			}
			else
			{
				st.setString(18, sdf.format(info.getVisa_exp_date()));
			}

			if(info.getVisa_issue_date()==null)
			{
				st.setString(19, "");
			}
			else
			{
				st.setString(19, sdf.format(info.getVisa_issue_date()));
			}

			st.setString(20, info.getOther_lang());
			st.setString(21, info.getEthnicity());
			st.setString(22, info.getEmid());
			st.setString(23, info.getEng_prof());
			st.setString(24, info.getIndependence());
			st.setString(25, info.getPersonal());
			st.setString(26, info.getGeneral());
			st.setString(27, info.getDemotion());
			st.setString(28, info.getSen());
			st.setString(29, info.getSen_file());
			st.setString(30, info.getExceptional());
			st.setString(31, info.getExceptional_rem());
			st.setString(32, info.getAttention());
			st.setString(33, info.getBehaviour());
			st.setString(34, info.getLearning());
			st.setString(35, info.getPhysical());
			st.setString(36, info.getParent_type());
			st.setString(37, info.getParent_id());
			st.setString(38, info.getParent_pwd());
			st.setString(39, info.getApplicant_name());
			st.setString(40, info.getApplicant_relation());
			st.setString(41, info.getF_name());
			st.setString(42, info.getF_occupation());
			st.setString(43, info.getF_employer());
			st.setString(44, info.getF_mob());
			st.setString(45, info.getF_email());
			st.setString(46, info.getF_work());
			st.setString(47, info.getF_address());
			st.setString(48, info.getF_emid());
			st.setString(49, info.getM_name());
			st.setString(50, info.getM_occupation());
			st.setString(51, info.getM_employer());
			st.setString(52, info.getM_mob());
			st.setString(53, info.getM_email());
			st.setString(54, info.getM_work());
			st.setString(55, info.getM_address());
			st.setString(56, info.getM_emid());
			st.setString(57, info.getReceipient());
			//f_image, m_image, g_image, st_image, food_type, login_id, student_id, schid, status, other_rel, address
			st.setString(58, info.getFee_payee());
			st.setString(59, info.getCompany());
			st.setString(60, info.getBill_address());
			st.setString(61, info.getComp_phone());
			st.setString(62, info.getComp_email());
			st.setString(63, info.getOnline_admcol());
			st.setString(64, info.getG_name());
			st.setString(65, info.getG_occupation());
			st.setString(66, info.getG_employer());
			st.setString(67, info.getG_mob());
			st.setString(68, info.getG_email());
			st.setString(69, info.getG_work());
			st.setString(70, info.getG_address());
			st.setString(71, info.getG_emid());
			st.setString(72, info.getG_rel());
			st.setString(73, info.getE1_name());
			st.setString(74, info.getE1_mob());
			st.setString(75, info.getE1_rel());
			st.setString(76, info.getE2_name());
			st.setString(77, info.getE2_mob());
			st.setString(78, info.getE2_rel());
			st.setString(79, info.getBus_service());
			if(info.getPickupList().size()>0)
			{
				st.setString(80, String.join(",", info.getPickupList()));
			}
			else
			{
				st.setString(80, "");
			}


			st.setString(81, info.getF_image());
			st.setString(82, info.getM_image());
			st.setString(83, info.getG_image());
			st.setString(84, info.getSt_image());
			st.setString(85, info.getFood_type());
			st.setString(86, info.getLogin_id());
			st.setString(87, info.getStudent_id());
			st.setString(88, schoolid);
			st.setString(89, status);
			st.setString(90, info.getOther_rel());
			st.setString(91, info.getAddress());
			st.setString(92, info.getG_nation());
			st.setString(93, status);
			st.setString(94, info.getRemark());
			st.executeUpdate();
			DBM.saveQueryLog(schoolid,st.toString(), conn);
			// if(i==1)
			// {
			// return 1;
			// }
			rr = st.getGeneratedKeys();
			if (rr.next()) {
				int i = rr.getInt(1);
				return i;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			if (rr != null) {
				try {
					rr.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return 0;
	}

	public int updateAdmInfo(OnlineAdmInfo info,Connection conn)
	{

		String query = "";
        String schid = DBM.schoolId();
		String admitClassName = DBM.classNameFromidSchid(schid,info.getClassid(), DBM.selectedSessionDetails(schid,conn), conn);
		info.setClassname(admitClassName);

		PreparedStatement st = null;
		ResultSet rr = null;

		query = "update online_adm set year=?, classid=?, classname=?, apply_date=?, st_name=?, gender=?, dob=?, birth_place=?, last_school=?, "
				+ "nationality=?, religion=?, lang=?, ppt_no=?, ppt_issue_date=?, ppt_exp_date=?, ppt_place=?, visa_no=?, visa_exp_date=?, "
				+ "visa_issue_date=?, other_lang=?, ethnicity=?, emid=?, eng_prof=?, independence=?, personal=?, general=?, demotion=?, sen=?, "
				+ "sen_file=?, exceptional=?, exceptional_rem=?, attention=?, behaviour=?, learning=?, physical=?, parent_type=?, parent_id=?,"
				+ " parent_pwd=?, applicant_name=?, applicant_relation=?, f_name=?, f_occupation=?, f_employer=?, f_mob=?, f_email=?, f_work=?, "
				+ "f_address=?, f_emid=?, m_name=?, m_occupation=?, m_employer=?, m_mob=?, m_email=?, m_work=?, m_address=?, m_emid=?, receipient=?, "
				+ "fee_payee=?, company=?, bill_address=?, comp_phone=?, comp_email=?, online_admcol=?, g_name=?, g_occupation=?, g_employer=?, "
				+ "g_mob=?, g_email=?, g_work=?, g_address=?, g_emid=?, g_rel=?, e1_name=?, e1_mob=?, e1_rel=?, e2_name=?, e2_mob=?, e2_rel=?, bus_service=?, "
				+ "pickup_name=?, f_image=?, m_image=?, g_image=?, st_image=?, food_type=?, student_id=?, other_rel=?, address=?,"
				+ " g_nation=? where id='"+info.getId()+"'";

		try {
			st = conn.prepareStatement(query);
			// st.setString(1, addmissionNumber);
			st.setString(1, info.getYear());
			st.setString(2, info.getClassid());
			st.setString(3, info.getClassname());
			st.setString(4, sdf.format(info.getApply_date()));
			st.setString(5, info.getSt_name());
			st.setString(6, info.getGender());
			st.setString(7, sdf.format(info.getDob()));
			st.setString(8, info.getBirth_place());
			st.setString(9, info.getLast_school());
			st.setString(10, info.getNationality());
			st.setString(11, info.getReligion());
			st.setString(12, info.getLang());
			st.setString(13, info.getPpt_no());
			if(info.getPpt_issue_date()==null)
			{
				st.setString(14, "");
			}
			else
			{
				st.setString(14, sdf.format(info.getPpt_issue_date()));
			}

			if(info.getPpt_exp_date()==null)
			{
				st.setString(15, "");
			}
			else
			{
				st.setString(15, sdf.format(info.getPpt_exp_date()));
			}

			st.setString(16, info.getPpt_place());
			st.setString(17, info.getVisa_no());


			if(info.getVisa_exp_date()==null)
			{
				st.setString(18, "");
			}
			else
			{
				st.setString(18, sdf.format(info.getVisa_exp_date()));
			}

			if(info.getVisa_issue_date()==null)
			{
				st.setString(19, "");
			}
			else
			{
				st.setString(19, sdf.format(info.getVisa_issue_date()));
			}

			st.setString(20, info.getOther_lang());
			st.setString(21, info.getEthnicity());
			st.setString(22, info.getEmid());
			st.setString(23, info.getEng_prof());
			st.setString(24, info.getIndependence());
			st.setString(25, info.getPersonal());
			st.setString(26, info.getGeneral());
			st.setString(27, info.getDemotion());
			st.setString(28, info.getSen());
			st.setString(29, info.getSen().equalsIgnoreCase("No") ? "" : info.getSen_file());
			st.setString(30, info.getExceptional());
			st.setString(31, info.getExceptional().equalsIgnoreCase("No") ? "" : info.getExceptional_rem());
			st.setString(32, info.getAttention());
			st.setString(33, info.getBehaviour());
			st.setString(34, info.getLearning());
			st.setString(35, info.getPhysical());
			st.setString(36, info.getParent_type());
			st.setString(37, info.getParent_id());
			st.setString(38, info.getParent_pwd());
			st.setString(39, info.getApplicant_name());
			st.setString(40, info.getApplicant_relation());
			st.setString(41, info.getF_name());
			st.setString(42, info.getF_occupation());
			st.setString(43, info.getF_employer());
			st.setString(44, info.getF_mob());
			st.setString(45, info.getF_email());
			st.setString(46, info.getF_work());
			st.setString(47, info.getF_address());
			st.setString(48, info.getF_emid());
			st.setString(49, info.getM_name());
			st.setString(50, info.getM_occupation());
			st.setString(51, info.getM_employer());
			st.setString(52, info.getM_mob());
			st.setString(53, info.getM_email());
			st.setString(54, info.getM_work());
			st.setString(55, info.getM_address());
			st.setString(56, info.getM_emid());
			st.setString(57, info.getReceipient());
			//f_image, m_image, g_image, st_image, food_type, login_id, student_id, schid, status, other_rel, address
			st.setString(58, info.getFee_payee());
			st.setString(59, info.getCompany());
			st.setString(60, info.getBill_address());
			st.setString(61, info.getComp_phone());
			st.setString(62, info.getComp_email());
			st.setString(63, info.getOnline_admcol());
			st.setString(64, info.getG_name());
			st.setString(65, info.getG_occupation());
			st.setString(66, info.getG_employer());
			st.setString(67, info.getG_mob());
			st.setString(68, info.getG_email());
			st.setString(69, info.getG_work());
			st.setString(70, info.getG_address());
			st.setString(71, info.getG_emid());
			st.setString(72, info.getG_rel());
			st.setString(73, info.getE1_name());
			st.setString(74, info.getE1_mob());
			st.setString(75, info.getE1_rel());
			st.setString(76, info.getE2_name());
			st.setString(77, info.getE2_mob());
			st.setString(78, info.getE2_rel());
			st.setString(79, info.getBus_service());
			if(info.getPickupList().size()>0)
			{
				st.setString(80, String.join(",", info.getPickupList()));
			}
			else
			{
				st.setString(80, "");
			}
			st.setString(81, info.getF_image());
			st.setString(82, info.getM_image());
			st.setString(83, info.getG_image());
			st.setString(84, info.getSt_image());
			st.setString(85, info.getFood_type());
			st.setString(86, info.getStudent_id());
			st.setString(87, info.getOther_rel());
			st.setString(88, info.getAddress());
			st.setString(89, info.getG_nation());
			//			st.setString(90, "admssion form filled");
			//			st.setString(91, "admssion form filled");
			//			st.setString(92, info.getRemark());
			int i = st.executeUpdate();
			DBM.saveQueryLog(schid,st.toString(), conn);
			return i;

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			if (rr != null) {
				try {
					rr.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return 0;
	}

	public void updateAdmStatus(String status,String schStatus,String id,Connection conn)
	{
		String query = "";
		PreparedStatement st = null;
		String schid = DBM.schoolId();
		ResultSet rr = null;

		query = "update online_adm set status=?, sch_status=? where id='"+id+"'";

		try {
			st = conn.prepareStatement(query);
			st.setString(1, status);
			st.setString(2, schStatus);
			st.executeUpdate();
			DBM.saveQueryLog(schid,st.toString(), conn);

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			if (rr != null) {
				try {
					rr.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void updateAdmRemark(String remark,String id,Connection conn)
	{
		String query = "";
		PreparedStatement st = null;
		String schid = DBM.schoolId();
		ResultSet rr = null;

		query = "update online_adm set remark=? where id='"+id+"'";

		try {
			st = conn.prepareStatement(query);
			st.setString(1, remark);
			st.executeUpdate();
			DBM.saveQueryLog(schid,st.toString(), conn);

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			if (rr != null) {
				try {
					rr.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void updateAEStudentId(String table,String stuCol,String studentId,String idCol,String id,Connection conn)
	{
		String query = "";
		PreparedStatement st = null;
		String schoolid = DBM.schoolId();
		ResultSet rr = null;

		query = "update "+table+" set "+stuCol+"=? where "+idCol+"='"+id+"' and schid='"+schoolid+"'";

		try {
			st = conn.prepareStatement(query);
			st.setString(1, studentId);
			st.executeUpdate();
			DBM.saveQueryLog(schoolid,st.toString(), conn);

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			if (rr != null) {
				try {
					rr.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void insertDocInfo(DocumentInfo docinfo,String login_id,int enq_id,String student_id, Connection conn) {
		String query = "";
		PreparedStatement st = null;
		String schoolid = DBM.schoolId();
		try {

			query = "INSERT INTO document_ae(parent_ppt, student_ppt, parent_visa, student_visa, parent_emid, student_emid, "
					+ "birth_cert, vaccination, family_book, enq_id, parent_id, student_id, schid, mother_ppt, mother_visa, "
					+ "mother_emid, g_ppt, g_visa, g_emid) "
					+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";


			st = conn.prepareStatement(query);
			// st.setString(1, addmissionNumber);
			st.setString(1, docinfo.getParent_ppt());
			st.setString(2, docinfo.getStudent_ppt());
			st.setString(3, docinfo.getParent_visa());
			st.setString(4, docinfo.getStudent_visa());
			st.setString(5, docinfo.getParent_emid());
			st.setString(6, docinfo.getStudent_emid());
			st.setString(7, docinfo.getBirth_cert());
			st.setString(8, docinfo.getVaccination());
			st.setString(9, docinfo.getFamily_book());
			st.setString(10, String.valueOf(enq_id));
			st.setString(11, login_id);
			st.setString(12, student_id);
			st.setString(13, schoolid);
			st.setString(14, docinfo.getMother_ppt());
			st.setString(15, docinfo.getMother_visa());
			st.setString(16, docinfo.getMother_emid());
			st.setString(17, docinfo.getG_ppt());
			st.setString(18, docinfo.getG_visa());
			st.setString(19, docinfo.getG_emid());

			st.executeUpdate();


		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public void updateDocInfo(DocumentInfo docinfo,String login_id,String enq_id, Connection conn) {
		String query = "";
		PreparedStatement st = null;
		String schoolid = DBM.schoolId();
		try {

			query = "update document_ae set parent_ppt=?, student_ppt=?, parent_visa=?, student_visa=?, parent_emid=?, student_emid=?, "
					+ "birth_cert=?, vaccination=?, family_book=?, mother_ppt=?, mother_visa=?, mother_emid=?, g_ppt=?, g_visa=?,"
					+ " g_emid=? where enq_id='"+enq_id+"' and parent_id='"+login_id+"' and schid='"+schoolid+"'";


			st = conn.prepareStatement(query);
			// st.setString(1, addmissionNumber);
			st.setString(1, docinfo.getParent_ppt());
			st.setString(2, docinfo.getStudent_ppt());
			st.setString(3, docinfo.getParent_visa());
			st.setString(4, docinfo.getStudent_visa());
			st.setString(5, docinfo.getParent_emid());
			st.setString(6, docinfo.getStudent_emid());
			st.setString(7, docinfo.getBirth_cert());
			st.setString(8, docinfo.getVaccination());
			st.setString(9, docinfo.getFamily_book());
			st.setString(10, docinfo.getMother_ppt());
			st.setString(11, docinfo.getMother_visa());
			st.setString(12, docinfo.getMother_emid());
			st.setString(13, docinfo.getG_ppt());
			st.setString(14, docinfo.getG_visa());
			st.setString(15, docinfo.getG_emid());

			st.executeUpdate();


		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public void insertMedicalInfo(MedicalInfo medinfo,String login_id,int enq_id,String student_id, Connection conn) {
		String query = "";
		PreparedStatement st = null;
		String schoolid = DBM.schoolId();
		try {

			query = "INSERT INTO medical_ae(asthma, diabetes, allergies, epilepsy, eyesight, surgery, fever, hearing, other, skin,"
					+ " blood_group, enq_id, parent_id, schid, student_id, asthma_rem, diabetes_rem, allergies_rem, epilepsy_rem, eyesight_rem,\n" +
					"	surgery_rem, fever_rem, hearing_rem, other_rem, skin_rem) "
					+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";


			st = conn.prepareStatement(query);
			// st.setString(1, addmissionNumber);
			st.setString(1, medinfo.getAsthma());
			st.setString(2, medinfo.getDiabetes());
			st.setString(3, medinfo.getAllergies());
			st.setString(4, medinfo.getEpilepsy());
			st.setString(5, medinfo.getEyesight());
			st.setString(6, medinfo.getSurgery());
			st.setString(7, medinfo.getFever());
			st.setString(8, medinfo.getHearing());
			st.setString(9, medinfo.getOther());
			st.setString(10, medinfo.getSkin());
			st.setString(11, medinfo.getBlood_group());
			st.setString(12, String.valueOf(enq_id));
			st.setString(13, login_id);
			st.setString(14, schoolid);
			st.setString(15, student_id);
			st.setString(16, medinfo.getAsthma_rem());
			st.setString(17, medinfo.getDiabetes_rem());
			st.setString(18, medinfo.getAllergies_rem());
			st.setString(19, medinfo.getEpilepsy_rem());
			st.setString(20, medinfo.getEyesight_rem());
			st.setString(21, medinfo.getSurgery_rem());
			st.setString(22, medinfo.getFever_rem());
			st.setString(23, medinfo.getHearing_rem());
			st.setString(24, medinfo.getOther_rem());
			st.setString(25, medinfo.getSkin_rem());

			st.executeUpdate();


		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void updateMedicalInfo(MedicalInfo medinfo,String login_id,String enq_id,Connection conn) {
		String query = "";
		PreparedStatement st = null;
		String schoolid = DBM.schoolId();
		try {

			query = "update medical_ae set asthma=?, diabetes=?, allergies=?, epilepsy=?, eyesight=?, surgery=?, fever=?, hearing=?, other=?, skin=?,"
					+ " blood_group=?, asthma_rem=?, diabetes_rem=?, allergies_rem=?, epilepsy_rem=?, eyesight_rem=?, surgery_rem=?,"
					+ " fever_rem=?, hearing_rem=?, other_rem=?, skin_rem=? where enq_id='"+enq_id+"' and parent_id='"+login_id+"' "
					+ "and schid='"+schoolid+"'";


			st = conn.prepareStatement(query);
			// st.setString(1, addmissionNumber);
			st.setString(1, medinfo.getAsthma());
			st.setString(2, medinfo.getDiabetes());
			st.setString(3, medinfo.getAllergies());
			st.setString(4, medinfo.getEpilepsy());
			st.setString(5, medinfo.getEyesight());
			st.setString(6, medinfo.getSurgery());
			st.setString(7, medinfo.getFever());
			st.setString(8, medinfo.getHearing());
			st.setString(9, medinfo.getOther());
			st.setString(10, medinfo.getSkin());
			st.setString(11, medinfo.getBlood_group());

			st.setString(12, medinfo.getAsthma().equalsIgnoreCase("No") ? "" : medinfo.getAsthma_rem());
			st.setString(13, medinfo.getDiabetes().equalsIgnoreCase("No") ? "" : medinfo.getDiabetes_rem());
			st.setString(14, medinfo.getAllergies().equalsIgnoreCase("No") ? "" : medinfo.getAllergies_rem());
			st.setString(15, medinfo.getEpilepsy().equalsIgnoreCase("No") ? "" : medinfo.getEpilepsy_rem());
			st.setString(16, medinfo.getEyesight().equalsIgnoreCase("No") ? "" : medinfo.getEyesight_rem());
			st.setString(17, medinfo.getSurgery().equalsIgnoreCase("No") ? "" : medinfo.getSurgery_rem());
			st.setString(18, medinfo.getFever().equalsIgnoreCase("No") ? "" : medinfo.getFever_rem());
			st.setString(19, medinfo.getHearing().equalsIgnoreCase("No") ? "" : medinfo.getHearing_rem());
			st.setString(20, medinfo.getOther().equalsIgnoreCase("No") ? "" : medinfo.getOther_rem());
			st.setString(21, medinfo.getSkin().equalsIgnoreCase("No") ? "" : medinfo.getSkin_rem());

			st.executeUpdate();


		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void insertVaccineInfo(VaccinationInfo vacinfo,String login_id,int enq_id,String student_id, Connection conn) {
		String query = "";
		PreparedStatement st = null;
		String schoolid = DBM.schoolId();
		try {

			query = "INSERT INTO vaccination_ae(chicken_pox, dtp, hepatitis, hib, mmr, meningitis, polio, rabies, tb_bcg, typhoid, "
					+ "enq_id, parent_id, student_id, schid) "
					+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";


			st = conn.prepareStatement(query);
			// st.setString(1, addmissionNumber);
			st.setString(1, vacinfo.getChicken_pox());
			st.setString(2, vacinfo.getDtp());
			st.setString(3, vacinfo.getHepatitis());
			st.setString(4, vacinfo.getHib());
			st.setString(5, vacinfo.getMmr());
			st.setString(6, vacinfo.getMeningitis());
			st.setString(7, vacinfo.getPolio());
			st.setString(8, vacinfo.getRabies());
			st.setString(9, vacinfo.getTb_bcg());
			st.setString(10, vacinfo.getTyphoid());
			st.setString(11, String.valueOf(enq_id));
			st.setString(12, login_id);
			st.setString(13, student_id);
			st.setString(14, schoolid);

			st.executeUpdate();


		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public void updateVaccineInfo(VaccinationInfo vacinfo,String login_id,String enq_id, Connection conn) {
		String query = "";
		PreparedStatement st = null;
		String schoolid = DBM.schoolId();
		try {

			query = "update vaccination_ae set chicken_pox=?, dtp=?, hepatitis=?, hib=?, mmr=?, meningitis=?, polio=?, rabies=?, tb_bcg=?, typhoid=? "
					+ " where enq_id='"+enq_id+"' and parent_id='"+login_id+"' and schid='"+schoolid+"'";


			st = conn.prepareStatement(query);
			// st.setString(1, addmissionNumber);
			st.setString(1, vacinfo.getChicken_pox());
			st.setString(2, vacinfo.getDtp());
			st.setString(3, vacinfo.getHepatitis());
			st.setString(4, vacinfo.getHib());
			st.setString(5, vacinfo.getMmr());
			st.setString(6, vacinfo.getMeningitis());
			st.setString(7, vacinfo.getPolio());
			st.setString(8, vacinfo.getRabies());
			st.setString(9, vacinfo.getTb_bcg());
			st.setString(10, vacinfo.getTyphoid());

			st.executeUpdate();


		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public void deleteSiblingAe(String enq_id, Connection conn)
	{
		PreparedStatement st = null;
		String schoolid = DBM.schoolId();
		try
		{
			
			String qq = "delete from sibling_ae where enq_id=? and schid=?";
			st = conn.prepareStatement(qq);
			st.setString(1, enq_id);
			st.setString(2, schoolid);
			st.executeUpdate();

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void insertSiblingInfo(ArrayList<SiblingAEInfo> completeList, String enq_id, String login_id,String studentid, Connection conn)
	{
		String query = "";
		PreparedStatement st = null;
		String schoolid = DBM.schoolId();
		try {
			for(SiblingAEInfo ss : completeList)
			{
				if(!ss.getName().equals("") && !ss.getClass_id().equals(""))
				{
					String admitClassName = DBM.classNameFromidSchid(schoolid,ss.getClass_id(), DBM.selectedSessionDetails(schoolid,conn), conn);

					query = "INSERT INTO sibling_ae(name, class_name, enq_id, login_id, schid, student_id, classid) "
							+ "values(?,?,?,?,?,?,?)";


					st = conn.prepareStatement(query);
					// st.setString(1, addmissionNumber);
					st.setString(1, ss.getName());
					st.setString(2, admitClassName);
					st.setString(3, String.valueOf(enq_id));
					st.setString(4, login_id);
					st.setString(5, schoolid);
					st.setString(6, studentid);
					st.setString(7, ss.getClass_id());

					st.executeUpdate();


				}

			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public static void parentDetailByParentId(OnlineAdmInfo info,Connection conn)
	{
		//OnlineAdmInfo info = null;
		Statement st = null;
		ResultSet rr = null;
		ArrayList<String> list = new ArrayList<>();
		String schoolid = DBM.schoolId();
		try
		{
			st = conn.createStatement();
			String qq = "select * from online_adm where login_id='"+info.getParent_id()+"' and schid='"+schoolid+"' order by id desc";
			rr = st.executeQuery(qq);
			if(rr.next())
			{
				//info = new OnlineAdmInfo();
				info.setOnline_admcol("found");
				info.setApplicant_name(rr.getString("applicant_name"));
				info.setApplicant_relation(rr.getString("applicant_relation"));
				info.setOther_rel(rr.getString("other_rel"));
				info.setF_name(rr.getString("f_name"));
				info.setF_address(rr.getString("f_address"));
				info.setF_email(rr.getString("f_email"));
				info.setF_emid(rr.getString("f_emid"));
				info.setF_employer(rr.getString("f_employer"));
				info.setF_image(rr.getString("f_image"));
				info.setF_mob(rr.getString("f_mob"));
				info.setF_occupation(rr.getString("f_occupation"));
				info.setF_work(rr.getString("f_work"));
				info.setM_name(rr.getString("m_name"));
				info.setM_address(rr.getString("m_address"));
				info.setM_email(rr.getString("m_email"));
				info.setM_emid(rr.getString("m_emid"));
				info.setM_employer(rr.getString("m_employer"));
				info.setM_image(rr.getString("m_image"));
				info.setM_mob(rr.getString("m_mob"));
				info.setM_occupation(rr.getString("m_occupation"));
				info.setM_work(rr.getString("m_work"));
				info.setG_name(rr.getString("g_name"));
				info.setG_address(rr.getString("g_address"));
				info.setG_email(rr.getString("g_email"));
				info.setG_emid(rr.getString("g_emid"));
				info.setG_employer(rr.getString("g_employer"));
				info.setG_image(rr.getString("g_image"));
				info.setG_mob(rr.getString("g_mob"));
				info.setG_occupation(rr.getString("g_occupation"));
				info.setG_work(rr.getString("g_work"));
				info.setG_rel(rr.getString("g_rel"));
				info.setG_nation(rr.getString("g_nation"));
				info.setReceipient(rr.getString("receipient"));
				info.setFee_payee(rr.getString("fee_payee"));
				info.setCompany(rr.getString("company"));
				info.setBill_address(rr.getString("bill_address"));
				info.setComp_phone(rr.getString("comp_phone"));
				info.setComp_email(rr.getString("comp_email"));
				info.setE1_name(rr.getString("e1_name"));
				info.setE1_mob(rr.getString("e1_mob"));
				info.setE1_rel(rr.getString("e1_rel"));
				info.setE2_name(rr.getString("e2_name"));
				info.setE2_mob(rr.getString("e2_mob"));
				info.setE2_rel(rr.getString("e2_rel"));
				info.setBus_service(rr.getString("bus_service"));
				String tmp[] = rr.getString("pickup_name").split(",");
				list = new ArrayList<>();
				for(int i=0;i<tmp.length;i++)
				{
					list.add(tmp[i]);
				}
				info.setPickup_name(rr.getString("pickup_name"));
				info.setPickupList(list);
			}
			else
			{
				info.setOnline_admcol("not exist");
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally {
			if (rr != null) {
				try {
					rr.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public ArrayList<SiblingAEInfo> siblingListByEnqId(String id, Connection conn)
	{
		ArrayList<SiblingAEInfo> list = new ArrayList<>();
		Statement st = null;
		ResultSet rr = null;
		String schoolid = DBM.schoolId();
		int i = 1;
		try
		{
			st = conn.createStatement();
			String qq = "select * from sibling_ae where enq_id='"+id+"' and schid='"+schoolid+"'";
			rr = st.executeQuery(qq);
			while(rr.next())
			{
				SiblingAEInfo ii = new SiblingAEInfo();
				ii.setSno(i++);
				ii.setClass_id(rr.getString("classid"));
				ii.setClass_name(rr.getString("class_name"));
				ii.setName(rr.getString("name"));
				ii.setStudent_id(rr.getString("student_id"));
				ii.setLogin_id(rr.getString("login_id"));
				list.add(ii);
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally {
			if (rr != null) {
				try {
					rr.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return list;
	}

	public OnlineAdmInfo onlineAdmInfoById(String enqId,String col,Connection conn)
	{
		OnlineAdmInfo info = null;
		Statement st = null;
		ResultSet rr = null;
		ArrayList<String> list = new ArrayList<>();
		String schoolid = DBM.schoolId();
		try
		{
			st = conn.createStatement();
			String qq = "select * from online_adm where "+col+"='"+enqId+"' and schid='"+schoolid+"'";
			rr = st.executeQuery(qq);
			if(rr.next())
			{
				info = new OnlineAdmInfo();
				info.setOnline_admcol("found");
				info.setYear(rr.getString("year"));
				info.setClassid(rr.getString("classid"));
				info.setClassname(rr.getString("classname"));
				info.setApply_date(rr.getDate("apply_date"));
				info.setApplydateStr(sdf.format(info.getApply_date()));
				info.setSt_name(rr.getString("st_name"));
				info.setSt_image(rr.getString("st_image"));
				info.setGender(rr.getString("gender"));
				info.setDob(rr.getDate("dob"));
				info.setDobStr(sdf.format(info.getDob()));
				info.setBirth_place(rr.getString("birth_place"));
				info.setLast_school(rr.getString("last_school"));
				info.setNationality(rr.getString("nationality"));
				info.setReligion(rr.getString("religion"));
				info.setLang(rr.getString("lang"));
				info.setPpt_no(rr.getString("ppt_no"));
				if(rr.getString("ppt_issue_date")==null || rr.getString("ppt_issue_date").equals(""))
				{
					info.setPpt_issue_date(null);
					info.setPpt_issue_dateStr("");
				}
				else
				{
					info.setPpt_issue_date(rr.getDate("ppt_issue_date"));
					info.setPpt_issue_dateStr(sdf.format(info.getPpt_issue_date()));
				}

				if(rr.getString("ppt_exp_date")==null || rr.getString("ppt_exp_date").equals(""))
				{
					info.setPpt_exp_date(null);
					info.setPpt_exp_dateStr("");
				}
				else
				{
					info.setPpt_exp_date(rr.getDate("ppt_exp_date"));
					info.setPpt_exp_dateStr(sdf.format(info.getPpt_exp_date()));
				}
				info.setPpt_place(rr.getString("ppt_place"));
				info.setVisa_no(rr.getString("visa_no"));
				if(rr.getString("visa_issue_date")==null || rr.getString("visa_issue_date").equals(""))
				{
					info.setVisa_issue_date(null);
					info.setVisa_issue_dateStr("");
				}
				else
				{
					info.setVisa_issue_date(rr.getDate("visa_issue_date"));
					info.setVisa_issue_dateStr(sdf.format(info.getVisa_issue_date()));
				}

				if(rr.getString("visa_exp_date")==null || rr.getString("visa_exp_date").equals(""))
				{
					info.setVisa_exp_date(null);
					info.setVisa_exp_dateStr("");
				}
				else
				{
					info.setVisa_exp_date(rr.getDate("visa_exp_date"));
					info.setVisa_exp_dateStr(sdf.format(info.getVisa_exp_date()));
				}
				info.setOther_lang(rr.getString("other_lang"));
				info.setEthnicity(rr.getString("ethnicity"));
				info.setEmid(rr.getString("emid"));
				info.setEng_prof(rr.getString("eng_prof"));
				info.setIndependence(rr.getString("independence"));
				info.setPersonal(rr.getString("personal"));
				info.setGeneral(rr.getString("general"));
				info.setDemotion(rr.getString("demotion"));
				info.setSen_file(rr.getString("sen_file"));
				info.setExceptional(rr.getString("exceptional"));
				info.setExceptional_rem(rr.getString("exceptional_rem"));
				info.setAttention(rr.getString("attention"));
				info.setBehaviour(rr.getString("behaviour"));
				info.setLearning(rr.getString("learning"));
				info.setPhysical(rr.getString("physical"));
				info.setParent_type(rr.getString("parent_type"));
				info.setParent_id(rr.getString("parent_id"));
				info.setParent_pwd(rr.getString("parent_pwd"));

				info.setApplicant_name(rr.getString("applicant_name"));
				info.setApplicant_relation(rr.getString("applicant_relation"));
				info.setOther_rel(rr.getString("other_rel"));
				info.setF_name(rr.getString("f_name"));
				info.setF_address(rr.getString("f_address"));
				info.setF_email(rr.getString("f_email"));
				info.setF_emid(rr.getString("f_emid"));
				info.setF_employer(rr.getString("f_employer"));
				info.setF_image(rr.getString("f_image"));
				info.setF_mob(rr.getString("f_mob"));
				info.setF_occupation(rr.getString("f_occupation"));
				info.setF_work(rr.getString("f_work"));
				info.setM_name(rr.getString("m_name"));
				info.setM_address(rr.getString("m_address"));
				info.setM_email(rr.getString("m_email"));
				info.setM_emid(rr.getString("m_emid"));
				info.setM_employer(rr.getString("m_employer"));
				info.setM_image(rr.getString("m_image"));
				info.setM_mob(rr.getString("m_mob"));
				info.setM_occupation(rr.getString("m_occupation"));
				info.setM_work(rr.getString("m_work"));
				info.setG_name(rr.getString("g_name"));
				info.setG_address(rr.getString("g_address"));
				info.setG_email(rr.getString("g_email"));
				info.setG_emid(rr.getString("g_emid"));
				info.setG_employer(rr.getString("g_employer"));
				info.setG_image(rr.getString("g_image"));
				info.setG_mob(rr.getString("g_mob"));
				info.setG_occupation(rr.getString("g_occupation"));
				info.setG_work(rr.getString("g_work"));
				info.setG_rel(rr.getString("g_rel"));
				info.setG_nation(rr.getString("g_nation"));
				info.setReceipient(rr.getString("receipient"));
				info.setFee_payee(rr.getString("fee_payee"));
				info.setCompany(rr.getString("company"));
				info.setBill_address(rr.getString("bill_address"));
				info.setComp_phone(rr.getString("comp_phone"));
				info.setComp_email(rr.getString("comp_email"));
				info.setE1_name(rr.getString("e1_name"));
				info.setE1_mob(rr.getString("e1_mob"));
				info.setE1_rel(rr.getString("e1_rel"));
				info.setE2_name(rr.getString("e2_name"));
				info.setE2_mob(rr.getString("e2_mob"));
				info.setE2_rel(rr.getString("e2_rel"));
				info.setBus_service(rr.getString("bus_service"));
				String tmp[] = rr.getString("pickup_name").split(",");
				list = new ArrayList<>();
				for(int i=0;i<tmp.length;i++)
				{
					list.add(tmp[i]);
				}
				info.setPickup_name(rr.getString("pickup_name"));
				info.setPickupList(list);
				info.setFood_type(rr.getString("food_type"));
				info.setLogin_id(rr.getString("login_id"));
				info.setStudent_id(rr.getString("student_id"));
				info.setAddress(rr.getString("address"));
				info.setId(rr.getString("id"));
				info.setStatus(rr.getString("status"));
				info.setSch_status(rr.getString("sch_status"));
				info.setRemark(rr.getString("remark"));

				info.setMedinfo(medicalInfoById(enqId, "enq_id", conn));
				info.setVacinfo(vaccineInfoById(enqId, "enq_id", conn));
				info.setDocinfo(documentInfoById(enqId, "enq_id", conn));

			}
			else
			{
				info = new OnlineAdmInfo();
				info.setOnline_admcol("not exist");
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally {
			if (rr != null) {
				try {
					rr.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return info;
	}

	public MedicalInfo medicalInfoById(String id, String col,Connection conn)
	{
		MedicalInfo info = null;
		Statement st = null;
		ResultSet rr = null;
		String schoolid = DBM.schoolId();
		try
		{
			st = conn.createStatement();
			String qq = "select * from medical_ae where "+col+"='"+id+"' and schid='"+schoolid+"'";
			rr = st.executeQuery(qq);
			if(rr.next())
			{
				info = new MedicalInfo();

				info.setId(rr.getString("id"));

				info.setAsthma(rr.getString("asthma"));
				info.setAsthma_rem(rr.getString("asthma_rem"));
				info.setDiabetes(rr.getString("diabetes"));
				info.setDiabetes_rem(rr.getString("diabetes_rem"));
				info.setAllergies(rr.getString("allergies"));
				info.setAllergies_rem(rr.getString("allergies_rem"));
				info.setEpilepsy(rr.getString("epilepsy"));
				info.setEpilepsy_rem(rr.getString("epilepsy_rem"));
				info.setEyesight(rr.getString("eyesight"));
				info.setEyesight_rem(rr.getString("eyesight_rem"));
				info.setSurgery(rr.getString("surgery"));
				info.setSurgery_rem(rr.getString("surgery_rem"));
				info.setFever(rr.getString("fever"));
				info.setFever_rem(rr.getString("fever_rem"));
				info.setHearing(rr.getString("hearing"));
				info.setHearing_rem(rr.getString("hearing_rem"));
				info.setOther(rr.getString("other"));
				info.setOther_rem(rr.getString("other_rem"));
				info.setSkin(rr.getString("skin"));
				info.setSkin_rem(rr.getString("skin_rem"));
				info.setBlood_group(rr.getString("blood_group"));
				info.setEnq_id(rr.getString("enq_id"));
				info.setParent_id(rr.getString("parent_id"));
				info.setStudent_id(rr.getString("student_id"));

			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally {
			if (rr != null) {
				try {
					rr.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return info;
	}

	public VaccinationInfo vaccineInfoById(String id, String col,Connection conn)
	{
		VaccinationInfo info = null;
		Statement st = null;
		ResultSet rr = null;
		String schoolid = DBM.schoolId();
		try
		{
			st = conn.createStatement();
			String qq = "select * from vaccination_ae where "+col+"='"+id+"' and schid='"+schoolid+"'";
			rr = st.executeQuery(qq);
			if(rr.next())
			{
				info = new VaccinationInfo();

				info.setId(rr.getString("id"));

				info.setChicken_pox(rr.getString("chicken_pox"));
				info.setDtp(rr.getString("dtp"));
				info.setHepatitis(rr.getString("hepatitis"));
				info.setHib(rr.getString("hib"));
				info.setMmr(rr.getString("mmr"));
				info.setMeningitis(rr.getString("meningitis"));
				info.setPolio(rr.getString("polio"));
				info.setRabies(rr.getString("rabies"));
				info.setTb_bcg(rr.getString("tb_bcg"));
				info.setTyphoid(rr.getString("typhoid"));
				info.setEnq_id(rr.getString("enq_id"));
				info.setParent_id(rr.getString("parent_id"));
				info.setStudent_id(rr.getString("student_id"));

			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally {
			if (rr != null) {
				try {
					rr.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return info;
	}

	public DocumentInfo documentInfoById(String id, String col,Connection conn)
	{
		DocumentInfo info = null;
		Statement st = null;
		ResultSet rr = null;
		String schoolid = DBM.schoolId();
		try
		{
			st = conn.createStatement();
			String qq = "select * from document_ae where "+col+"='"+id+"' and schid='"+schoolid+"'";
			rr = st.executeQuery(qq);
			if(rr.next())
			{
				info = new DocumentInfo();

				info.setId(rr.getString("id"));
				info.setParent_ppt(rr.getString("parent_ppt"));
				info.setParent_emid(rr.getString("parent_emid"));
				info.setParent_visa(rr.getString("parent_visa"));
				info.setMother_ppt(rr.getString("mother_ppt"));
				info.setMother_emid(rr.getString("mother_emid"));
				info.setMother_visa(rr.getString("mother_visa"));
				info.setStudent_ppt(rr.getString("student_ppt"));
				info.setStudent_emid(rr.getString("student_emid"));
				info.setStudent_visa(rr.getString("student_visa"));
				info.setG_emid(rr.getString("g_emid"));
				info.setG_ppt(rr.getString("g_ppt"));
				info.setG_visa(rr.getString("g_visa"));
				info.setBirth_cert(rr.getString("birth_cert"));
				info.setVaccination(rr.getString("vaccination"));
				info.setFamily_book(rr.getString("family_book"));
				info.setEnq_id(rr.getString("enq_id"));
				info.setParent_id(rr.getString("parent_id"));
				info.setStudent_id(rr.getString("student_id"));

			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally {
			if (rr != null) {
				try {
					rr.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return info;
	}

	public int deleteApplication(String id, Connection conn)
	{
		PreparedStatement st = null;
		String schoolid = DBM.schoolId();
		int i = 0;
		try
		{
			
			String qq = "delete from online_adm where id=? and schid=?";
			st = conn.prepareStatement(qq);
			st.setString(1, id);
			st.setString(2, schoolid);
			i = st.executeUpdate();

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return i;
	}

	public int deleteDocument(String id, Connection conn)
	{
		PreparedStatement st = null;
		String schoolid = DBM.schoolId();
		int i = 0;
		try
		{
			
			String qq = "delete from document_ae where enq_id=? and schid=?";
			st = conn.prepareStatement(qq);
			st.setString(1, id);
			st.setString(2, schoolid);
			i = st.executeUpdate();

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return i;
	}

	public int deleteMedical(String id, Connection conn)
	{
		PreparedStatement st = null;
		String schoolid = DBM.schoolId();
		int i = 0;
		try
		{
			
			String qq = "delete from medical_ae where enq_id=? and schid=?";
			st = conn.prepareStatement(qq);
			st.setString(1, id);
			st.setString(2, schoolid);
			i = st.executeUpdate();

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return i;
	}

	public int deleteVaccination(String id, Connection conn)
	{
		PreparedStatement st = null;
		String schoolid = DBM.schoolId();
		int i = 0;
		try
		{
			
			String qq = "delete from vaccination_ae where enq_id=? and schid=?";
			st = conn.prepareStatement(qq);
			st.setString(1, id);
			st.setString(2, schoolid);
			i = st.executeUpdate();

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return i;
	}

	public ArrayList<OnlineAdmInfo> myApplication(String unm,String col, Connection conn)
	{
		ArrayList<OnlineAdmInfo> list = new ArrayList<>();
		Statement st = null;
		ResultSet rr = null;
		String schoolid = DBM.schoolId();
		String qq = "";
		int i = 1;
		try
		{
			st = conn.createStatement();
			if(unm.equals("all reg"))
			{
				qq = "select * from online_adm where sch_status IN ('pending','admission form pending','application rejected') and schid='"+schoolid+"'";
			}
			else if(unm.equals("all adm"))
			{
				qq = "select * from online_adm where sch_status NOT IN ('pending','admission form pending','application rejected') and schid='"+schoolid+"'";
			}
			else
			{
				qq = "select * from online_adm where "+col+"='"+unm+"' and schid='"+schoolid+"'";
			}

			rr = st.executeQuery(qq);
			while(rr.next())
			{
				OnlineAdmInfo info = new OnlineAdmInfo();
				info.setSno(i++);
				info.setOnline_admcol("found");
				info.setYear(rr.getString("year"));
				info.setClassid(rr.getString("classid"));
				info.setClassname(rr.getString("classname"));
				info.setApply_date(rr.getDate("apply_date"));
				info.setApplydateStr(sdf.format(info.getApply_date()));
				info.setSt_name(rr.getString("st_name"));
				info.setSt_image(rr.getString("st_image"));
				info.setGender(rr.getString("gender"));
				info.setDob(rr.getDate("dob"));
				info.setDobStr(sdf.format(info.getDob()));
				/*info.setBirth_place(rr.getString("birth_place"));
				info.setLast_school(rr.getString("last_school"));
				info.setNationality(rr.getString("nationality"));
				info.setReligion(rr.getString("religion"));
				info.setLang(rr.getString("lang"));
				info.setPpt_no(rr.getString("ppt_no"));
				if(rr.getString("ppt_issue_date")==null || rr.getString("ppt_issue_date").equals(""))
				{
					info.setPpt_issue_date(null);
					info.setPpt_issue_dateStr("");
				}
				else
				{
					info.setPpt_issue_date(rr.getDate("ppt_issue_date"));
					info.setPpt_issue_dateStr(sdf.format(info.getPpt_issue_date()));
				}

				if(rr.getString("ppt_exp_date")==null || rr.getString("ppt_exp_date").equals(""))
				{
					info.setPpt_exp_date(null);
					info.setPpt_exp_dateStr("");
				}
				else
				{
					info.setPpt_exp_date(rr.getDate("ppt_exp_date"));
					info.setPpt_exp_dateStr(sdf.format(info.getPpt_exp_date()));
				}
				info.setPpt_place(rr.getString("ppt_place"));
				info.setVisa_no(rr.getString("visa_no"));
				if(rr.getString("visa_issue_date")==null || rr.getString("visa_issue_date").equals(""))
				{
					info.setVisa_issue_date(null);
					info.setVisa_issue_dateStr("");
				}
				else
				{
					info.setVisa_issue_date(rr.getDate("visa_issue_date"));
					info.setVisa_issue_dateStr(sdf.format(info.getVisa_issue_date()));
				}

				if(rr.getString("visa_exp_date")==null || rr.getString("visa_exp_date").equals(""))
				{
					info.setVisa_exp_date(null);
					info.setVisa_exp_dateStr("");
				}
				else
				{
					info.setVisa_exp_date(rr.getDate("visa_exp_date"));
					info.setVisa_exp_dateStr(sdf.format(info.getVisa_exp_date()));
				}
				info.setOther_lang(rr.getString("other_lang"));
				info.setEthnicity(rr.getString("ethnicity"));
				info.setEmid(rr.getString("emid"));
				info.setEng_prof(rr.getString("eng_prof"));
				info.setIndependence(rr.getString("independence"));
				info.setPersonal(rr.getString("personal"));
				info.setGeneral(rr.getString("general"));
				info.setDemotion(rr.getString(""));
				info.setSen_file(rr.getString("sen_file"));
				info.setExceptional(rr.getString("exceptional"));
				info.setExceptional_rem(rr.getString("exceptional_rem"));
				info.setAttention(rr.getString("attention"));
				info.setBehaviour(rr.getString("behaviour"));
				info.setLearning(rr.getString("learning"));
				info.setPhysical(rr.getString("physical"));
				info.setParent_type(rr.getString("parent_type"));
				info.setParent_id(rr.getString("parent_id"));
				info.setParent_pwd(rr.getString("parent_pwd"));*/

				info.setApplicant_name(rr.getString("applicant_name"));
				info.setApplicant_relation(rr.getString("applicant_relation"));
				info.setOther_rel(rr.getString("other_rel"));
				/*info.setF_name(rr.getString("f_name"));
				info.setF_address(rr.getString("f_address"));
				info.setF_email(rr.getString("f_email"));
				info.setF_emid(rr.getString("f_emid"));
				info.setF_employer(rr.getString("f_employer"));
				info.setF_image(rr.getString("f_image"));
				info.setF_mob(rr.getString("f_mob"));
				info.setF_occupation(rr.getString("f_occupation"));
				info.setF_work(rr.getString("f_work"));
				info.setM_name(rr.getString("m_name"));
				info.setM_address(rr.getString("m_address"));
				info.setM_email(rr.getString("m_email"));
				info.setM_emid(rr.getString("m_emid"));
				info.setM_employer(rr.getString("m_employer"));
				info.setM_image(rr.getString("m_image"));
				info.setM_mob(rr.getString("m_mob"));
				info.setM_occupation(rr.getString("m_occupation"));
				info.setM_work(rr.getString("m_work"));
				info.setG_name(rr.getString("g_name"));
				info.setG_address(rr.getString("g_address"));
				info.setG_email(rr.getString("g_email"));
				info.setG_emid(rr.getString("g_emid"));
				info.setG_employer(rr.getString("g_employer"));
				info.setG_image(rr.getString("g_image"));
				info.setG_mob(rr.getString("g_mob"));
				info.setG_occupation(rr.getString("g_occupation"));
				info.setG_work(rr.getString("g_work"));
				info.setG_rel(rr.getString("g_rel"));
				info.setG_nation(rr.getString("g_nation"));
				info.setReceipient(rr.getString("receipient"));
				info.setFee_payee(rr.getString("fee_payee"));
				info.setCompany(rr.getString("company"));
				info.setBill_address(rr.getString("bill_address"));
				info.setComp_phone(rr.getString("comp_phone"));
				info.setComp_email(rr.getString("comp_email"));
				info.setE1_name(rr.getString("e1_name"));
				info.setE1_mob(rr.getString("e1_mob"));
				info.setE1_rel(rr.getString("e1_rel"));
				info.setE2_name(rr.getString("e2_name"));
				info.setE2_mob(rr.getString("e2_mob"));
				info.setE2_rel(rr.getString("e2_rel"));
				info.setBus_service(rr.getString("bus_service"));
				String tmp[] = rr.getString("pickup_name").split(",");
				list = new ArrayList<>();
				for(int i=0;i<tmp.length;i++)
				{
					list.add(tmp[i]);
				}
				info.setPickup_name(rr.getString("pickup_name"));
				info.setPickupList(list);
				info.setFood_type(rr.getString("food_type"));*/
				info.setLogin_id(rr.getString("login_id"));
				info.setStudent_id(rr.getString("student_id"));
				info.setAddress(rr.getString("address"));
				info.setId(rr.getString("id"));
				info.setStatus(rr.getString("status"));
				info.setSch_status(rr.getString("sch_status"));
				info.setRemark(rr.getString("remark"));

				if(info.getStatus().equalsIgnoreCase("pending"))
				{
					info.setDisableDlt(false);
					info.setDisableEdit(true);
				}
				else if(info.getStatus().equalsIgnoreCase("application accepted"))
				{
					info.setDisableDlt(false);
					info.setDisableEdit(false);
				}
				else if(info.getStatus().equalsIgnoreCase("review admission form"))
				{
					info.setDisableDlt(false);
					info.setDisableEdit(false);
				}
				else if(info.getStatus().equalsIgnoreCase("application rejected"))
				{
					info.setDisableDlt(false);
					info.setDisableEdit(true);
				}
				else if(info.getStatus().equalsIgnoreCase("edit requested"))
				{
					info.setDisableDlt(true);
					info.setDisableEdit(false);
				}
				else if(info.getStatus().equalsIgnoreCase("admission form rejected"))
				{
					info.setDisableDlt(false);
					info.setDisableEdit(true);
				}
				else if(info.getStatus().equalsIgnoreCase("admission accepted"))
				{
					info.setDisableDlt(true);
					info.setDisableEdit(true);
				}
				//////////
				if(info.getStatus().equalsIgnoreCase("pending"))
				{
					info.setDisableAccept(false);
					info.setDisableReject(false);
					info.setDisableReview(true);
				}
				else if(info.getStatus().equalsIgnoreCase("admission form pending"))
				{
					info.setDisableAccept(true);
					info.setDisableReject(true);
					info.setDisableReview(true);
				}
				else if(info.getStatus().equalsIgnoreCase("application rejected"))
				{
					info.setDisableAccept(true);
					info.setDisableReject(true);
					info.setDisableReview(true);
				}
				else if(info.getStatus().equalsIgnoreCase("admission form filled"))
				{
					info.setDisableAccept(false);
					info.setDisableReject(false);
					info.setDisableReview(false);
				}
				else if(info.getStatus().equalsIgnoreCase("review admission form"))
				{
					info.setDisableAccept(false);
					info.setDisableReject(false);
					info.setDisableReview(true);
				}
				else if(info.getStatus().equalsIgnoreCase("edit requested"))
				{
					info.setDisableAccept(true);
					info.setDisableReject(true);
					info.setDisableReview(true);
				}
				else if(info.getStatus().equalsIgnoreCase("admission form rejected"))
				{
					info.setDisableAccept(true);
					info.setDisableReject(true);
					info.setDisableReview(true);
				}
				else if(info.getStatus().equalsIgnoreCase("admission accepted"))
				{
					info.setDisableAccept(true);
					info.setDisableReject(true);
					info.setDisableReview(true);
				}

				info.setMedinfo(medicalInfoById(info.getId(), "enq_id", conn));
				info.setVacinfo(vaccineInfoById(info.getId(), "enq_id", conn));
				info.setDocinfo(documentInfoById(info.getId(), "enq_id", conn));

				list.add(info);
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally {
			if (rr != null) {
				try {
					rr.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return list;
	}

	public ArrayList<SelectItem> allClassSession(String session,Connection conn) {
		///String session = selectedSessionDetails();
		ArrayList<SelectItem> temp = new ArrayList<>();

		Statement st = null;
		ResultSet rr = null;
		try {
			st = conn.createStatement();
			// String query="SELECT distinct * FROM class as c1 where id = (select max(id)
			// from class where c1.name = class.name and session='"+session+"') and
			// status='ACTIVE' and schid='"+schoolId()+"' and session= '"+session+"' group
			// by name order by groupid asc";
			String query = "select * from class where status='ACTIVE' and session='" + session + "' and schid='"
					+ DBM.schoolId() + "' group by groupid order by convert(rank,decimal) asc";

			rr = st.executeQuery(query);
			while (rr.next()) {
				SelectItem ss = new SelectItem();
				ss.setLabel(rr.getString("name"));
				ss.setValue(rr.getString("groupid"));

				temp.add(ss);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			if (rr != null) {
				try {
					rr.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return temp;
	}


	public void transportDataEntryAe(Date date, String sid, String routeidS, String status, String classid, Connection conn,String currSession) {
		SchoolInfoList info = DBM.fullSchoolInfo(conn);
		int startSessionMonth = Integer.valueOf(info.getSchoolSession().split("-")[0]);
		int endSessionMonth = Integer.valueOf(info.getSchoolSession().split("-")[1]) + 12;
		String schid = DBM.schoolId();


		int routeid = 0;
		if (routeidS.equals("")) {
			routeid = 0;
		} else {
			routeid = Integer.parseInt(routeidS);
		}

		int tempRoute = routeid;

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int admissionMonth = 0;
		int year = cal.get(Calendar.YEAR);
		int tempYear = year;
		int var = new DatabaseMethods1(DBM.schoolId()).getpromostionclass(String.valueOf(sid), conn);
		if (var == 1) {
			admissionMonth = startSessionMonth;
		} else {
			if (info.getFeeStart().equalsIgnoreCase("session_date")) {
				admissionMonth = startSessionMonth;
			} else {
				admissionMonth = cal.get(Calendar.MONTH) + 1;
			}

		}
		int month = startSessionMonth;

		String tempStatus = status;

		for (int i = 1; i <= 12; i++) {
			if (month == 13) {
				month = 1;
			}
			if (tempStatus.equals("Yes")) {
				if (admissionMonth >= startSessionMonth && admissionMonth <= 12) {
					if (month >= startSessionMonth) {
						tempYear = year;
						if (admissionMonth > month) {

							status = "No";
						} else {

							status = "Yes";
						}
					} else {
						tempYear = year + 1;
						status = "Yes";
					}
				} else {
					if (admissionMonth <= endSessionMonth) {
						if (month >= startSessionMonth) {

							status = "No";
							tempYear = year - 1;
						} else {
							tempYear = year;
							if (admissionMonth > month) {

								status = "No";
							} else {

								status = "Yes";
							}
						}
					}
				}
			} else {
				if (admissionMonth >= startSessionMonth) {
					if (month >= startSessionMonth) {
						tempYear = year;
					} else {
						tempYear = year + 1;
					}
				} else {
					if (month >= startSessionMonth) {
						tempYear = year - 1;
						// // ////// // System.out.println("temp year..."+tempYear);
						// break;
					} else {
						tempYear = year;
					}
				}
			}

			/*
			 * if(schoolId().equals("215") && month==6) { status="No"; routeid=0; } else {
			 * if(routeidS.equals("")) { routeid=0; } else {
			 * routeid=Integer.parseInt(routeidS); } }
			 */

			PreparedStatement st = null;
			try {

				String sts = "";
				if(status.equalsIgnoreCase("Yes"))
				{
					sts = new DataBaseMethodsReports().checkWhetherTransportWaived(schid, currSession, String.valueOf(month),classid, conn);
					status=sts;
				}

				if (status.equalsIgnoreCase("No"))
				{
					routeid = 0;
				}
				else
				{
					routeid = tempRoute;
				}

				String query = "insert into transport_fee_table (student_id,route_id,month,status,year,session,schid) values(?,?,?,?,?,?,?) ";
				st = conn.prepareStatement(query);
				st.setString(1, sid);
				st.setInt(2, routeid);
				st.setInt(3, month);
				st.setString(4, status);
				st.setInt(5, tempYear);
				st.setString(6, currSession);
				st.setString(7, schid);
				st.executeUpdate();
				DBM.saveQueryLog(schid,st.toString(), conn);
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				if (st != null) {
					try {
						st.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
			month++;
		}

	}

	public ArrayList<ClassTest> selectedClassTestListSession(String session,String classId, String section, Connection conn) {
		//String session = DatabaseMethods1.selectedSessionDetails();
		int count = 1;
		ArrayList<ClassTest> list = new ArrayList<>();
		String schid = DBM.schoolId();

		Statement st = null;
		ResultSet rr = null;
		try {
			st = conn.createStatement();
			String query = "select * from class_test where class='" + classId + "' and session='" + session
					+ "' and section='" + section + "' and status='ACTIVE' and schid='" + schid + "'";
			rr = st.executeQuery(query);
			while (rr.next()) {
				ClassTest ff = new ClassTest();
				ff.setSno(count++);
				ff.setClassid(rr.getString("class"));
				ff.setSectionid(rr.getString("section"));
				ff.setSubid(rr.getString("subject"));
				ff.setTestName(rr.getString("testName"));
				ff.setTestMarks(rr.getString("testMarks"));
				ff.setIdTest(rr.getString("testId"));
				ff.setId(rr.getString("idclass_test"));
				list.add(ff);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			if (rr != null) {
				try {
					rr.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return list;
	}

	public ArrayList<ExamInfo> selectedClassExamListSession(String session,/* String className1, */String selectedSection, Connection conn) {
		//String session = DatabaseMethods1.selectedSessionDetails();
		ArrayList<ExamInfo> list = new ArrayList<>();
		String schid = DBM.schoolId();
		Statement st = null;
		ResultSet rr = null;
		try {
			st = conn.createStatement();
			String query = "SELECT * FROM student_performance_cbse where sectionId='" + selectedSection + "' and session='"
					+ session + "' and schid='" + schid + "' group by subExamName,mainExamId,termId,subjectId,examType";
			rr = st.executeQuery(query);
			while (rr.next()) {
				ExamInfo ff = new ExamInfo();
				ff.setClassid(rr.getString("sectionId"));
				ff.setSubjectid(rr.getString("subjectId"));
				ff.setSemesterid(rr.getString("termId"));
				if (rr.getString("mainExamId") == null) {

				} else {
					ff.setExamid(rr.getString("mainExamId"));
				}
				ff.setExamType(rr.getString("examType"));
				ff.setExamName(rr.getString("subExamName"));
				ff.setMaxMark(rr.getString("maxMarks"));


				list.add(ff);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			if (rr != null) {
				try {
					rr.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return list;
	}

	public boolean checkClassTestPerformanceStatusSession(String session,String id, Connection conn) {
		Statement st = null;
		ResultSet rr = null;
		String schid = DBM.schoolId();

		//String session = DatabaseMethods1.selectedSessionDetails();

		try {
			st = conn.createStatement();
			String qq = "select id from testperformance where testid='" + id + "' and schid='" + schid
			+ "' and session='" + session + "'";
			rr = st.executeQuery(qq);
			if (rr.next()) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			if (rr != null) {
				try {
					rr.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	public int entryOfNewStudentInClassTestPerformanceSession(String schid,String session,String addmissionNumber, String id, Connection conn) {
		PreparedStatement pst = null;
		//String session = selectedSessionDetails();
		int i = 0;
		try {
			String query = "insert into testperformance ( addNumber, testid, marks, schid, session) values(?,?,?,?,?)";
			pst = conn.prepareStatement(query);

			pst.setString(1, addmissionNumber);
			pst.setString(2, id);
			pst.setString(3, "AB");
			pst.setString(4, schid);
			pst.setString(5, session);
			i = pst.executeUpdate();
			DBM.saveQueryLog(schid,pst.toString(), conn);

			if (i > 0) {

				return i;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return i;
	}

	public int entryOfNewStudentInExamPerformanceSession(String schid,String session,String addmissionNumber, String classid, String subjectid,
			String semesterid, String examid, String examType, Connection conn, String maxMarks, String subExamName) {
		PreparedStatement pst = null;
		//String session = selectedSessionDetails();
		String query = "";
		int i = 0;
		try {
			/*if (examType.equalsIgnoreCase("scholastic")) {

			 */

			query="insert into student_performance_cbse (studentId, sectionId, subjectId,maxMarks, marksObtained,subExamName,"
					+ " mainExamId,examType,session,termId,schId) values(?,?,?,?,?,?,?,?,?,?,?) ";

			pst = conn.prepareStatement(query);

			pst.setString(1,addmissionNumber);
			pst.setString(2,classid);
			pst.setString(3,subjectid);
			pst.setString(4,maxMarks);
			pst.setString(5,"");
			pst.setString(6,subExamName);
			pst.setString(7,examid);
			pst.setString(8,examType);
			pst.setString(9,session);
			pst.setString(10, semesterid);
			pst.setString(11, schid);
			i = pst.executeUpdate();

			if (i > 0) {

				return i;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return i;
	}

	public int studentRegistrationSession(String session,String admRemark,Date addmissionDate, String fname, Date dob, /* String className1, */
			String selectedSection, String aadharNo, long fathersPhone, String currAdd, String perAdd, String gender,
			String nationality, String religion, String category, String bpl, String bplCardNo, int pincode,
			String singleChild, String singleParent, String caste, String country, String ffname, String mname,
			String fatherEmail, String fatherAadharNo, String motherAadhaar, long mothersPhone, String residencePhone,
			String livingWith, String lastSchoolName, String passedClass, String medium, String pResult,
			String boardName, String p_percent, String pReason, /* Date tcDate, */
			String height, String weight, String eyes, String fname1, String relation1, String occupation1,
			String phone1, String address1, String fname2, String relation2, String occupation2, String phone2,
			String address2, String fatherQualification, String fatherDesignation, String fatherOfficeAdd,
			String fatherSchoolEmp, String motherQualification, String motherDesignation, String motherOfficeAdd,
			String motherSchoolEmp, String route, String concession, String hostal, String houseName, String sname1,
			String sClassName1, String sname2, String sClassName2, ArrayList<String> documentsSubmitted,
			String photo, String fatherPhoto, String motherPhoto, String g1Photo,
			String g2Photo, String motherEmail, String bloodGroup,
			/* String lname1,String lname2, */String fatherofficecontcatno, String motherofficecontactno,
			String studentstatus, String rollno, String discountFee, String disability, String handicap,
			Connection conn, String studentPhone, String srno,String concessionStatus,String enqUAEId,String classid) {

		//String session = DatabaseMethods1.selectedSessionDetails();
		String schid = DBM.schoolId();
		Timestamp addmissinDate = new Timestamp(addmissionDate.getTime());
		addmissinDate.setHours(00);
		addmissinDate.setMinutes(00);
		addmissinDate.setSeconds(00);
		addmissinDate.setNanos(0);
		Timestamp dobDate = new Timestamp(dob.getTime());
		dobDate.setHours(00);
		dobDate.setMinutes(00);
		dobDate.setSeconds(00);
		dobDate.setNanos(0);
		Timestamp tcDt = null;
		String query = "";
		String documnet = "";
		if (documentsSubmitted.size() > 0) {
			for (String ss : documentsSubmitted) {
				if (documnet.equals("")) {
					documnet = ss;
				} else {
					documnet = documnet + "," + ss;
				}

			}
		}

		String admitclassid = DBM.classSectionNameFromidSchid(schid,selectedSection, conn);

		String admitClassName = DBM.classNameFromidSchid(schid,admitclassid, session, conn);
		PreparedStatement st = null;
		ResultSet rr = null;

		query = "insert into registration1 (postdate,fname,fathersname,mname,classid,transportroute,dob,gender,nationality,"
				+ "religion,currentAddress,permAddress,pincode,country,fathersPhone,mothersPhone,status,categoryid,session,residencePhone,"
				+ "fatheroccupation,documents,student_image_path,bpl,bplCardNo,concession,caste,singleChild,bloodGroup,aadharNo,"
				+ "SingleParent,livingWith,fatherEmail,motherEmail,fatherImage,motherImage,g1Image,g2Image,sendMessageMobileNo,admitClass,schid,g1name,relation,occupation,mobile,lastSchoolName,passedClass,"
				+ "medium1, sname1, sclassid1, sname2, sclassid2,address,gname2,relation2,occupation2,phone2,"
				+ "address2,pResult,p_percent,pReason,height,weight,eyes,fatherQualification,fatheroccupation,"
				+ "fatherofficecontactno, fatherOfficeAdd,FatherAadhaar, fatherSchoolEmp, motherQualification,"
				+ "motherOccupation, motherofficecontacno, motherOfficeAdd,motherAadhaar,"
				+ "motherSchoolEmp,tcdt,board,hostel,housename,student_status,rollno,discounttransportFee,disability,"
				+ " handicapped,student_phone,sr_no,concession_status,adm_remark,enq_uae_id,mainClassId) "
				+ "value(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		// }

		try {
			st = conn.prepareStatement(query,Statement.RETURN_GENERATED_KEYS);
			// st.setString(1, addmissionNumber);
			st.setTimestamp(1, addmissinDate);
			st.setString(2, fname);
			st.setString(3, ffname);
			st.setString(4, mname);
			st.setString(5, selectedSection);
			st.setString(6, route);
			st.setTimestamp(7, dobDate);
			st.setString(8, gender);
			st.setString(9, nationality);
			st.setString(10, religion);
			st.setString(11, currAdd);
			st.setString(12, perAdd);
			st.setInt(13, pincode);
			st.setString(14, country);
			st.setLong(15, fathersPhone);
			st.setLong(16, mothersPhone);
			st.setString(17, "ACTIVE");
			st.setString(18, category);
			st.setString(19, session);
			st.setString(20, residencePhone);
			st.setString(21, fatherDesignation);
			st.setString(22, documnet);
			st.setString(23, photo);
			st.setString(24, bpl);
			st.setString(25, bplCardNo);
			st.setString(26, concession);
			st.setString(27, caste);
			st.setString(28, singleChild);
			st.setString(29, bloodGroup);
			st.setString(30, aadharNo);
			st.setString(31, singleParent);
			st.setString(32, livingWith);
			st.setString(33, fatherEmail);
			st.setString(34, motherEmail);
			st.setString(35, fatherPhoto);
			st.setString(36, motherPhoto);
			st.setString(37, g1Photo);
			st.setString(38, g2Photo);
			st.setString(39, String.valueOf(fathersPhone));
			st.setString(40, admitClassName);
			st.setString(41, DBM.schoolId());
			st.setString(42, fname1);
			st.setString(43, relation1);
			st.setString(44, occupation1);
			st.setString(45, phone1);
			st.setString(46, lastSchoolName);
			st.setString(47, passedClass);
			st.setString(48, medium);
			st.setString(49, sname1);
			st.setString(50, sClassName1);
			st.setString(51, sname2);
			st.setString(52, sClassName2);
			st.setString(53, address1);
			st.setString(54, fname2);
			st.setString(55, relation2);
			st.setString(56, occupation2);
			st.setString(57, phone2);
			st.setString(58, address2);
			st.setString(59, pResult);
			st.setString(60, p_percent);
			st.setString(61, pReason);
			st.setString(62, height);
			st.setString(63, weight);
			st.setString(64, eyes);
			st.setString(65, fatherQualification);
			st.setString(66, fatherDesignation);
			st.setString(67, fatherofficecontcatno);
			st.setString(68, fatherOfficeAdd);
			st.setString(69, fatherAadharNo);
			st.setString(70, fatherSchoolEmp);
			st.setString(71, motherQualification);
			st.setString(72, motherDesignation);
			st.setString(73, motherofficecontactno);
			st.setString(74, motherOfficeAdd);
			st.setString(75, motherAadhaar);
			st.setString(76, motherSchoolEmp);
			st.setTimestamp(77, tcDt);
			st.setString(78, boardName);
			st.setString(79, hostal);
			st.setString(80, houseName);
			st.setString(81, studentstatus);
			st.setString(82, rollno);
			st.setString(83, discountFee);
			st.setString(84, disability);
			st.setString(85, handicap);
			st.setString(86, studentPhone);
			st.setString(87, srno);
			st.setString(88, concessionStatus);
			st.setString(89, admRemark);
			st.setString(90, enqUAEId);
			st.setString(91, classid);


			st.executeUpdate();
			DBM.saveQueryLog(schid,st.toString(), conn);
			// if(i==1)
			// {
			// return 1;
			// }
			rr = st.getGeneratedKeys();
			if (rr.next()) {
				int i = rr.getInt(1);
				return i;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return 0;
	}

	public int updateStudent(int id, String addNumber, String fname, String fathersname, String mothersname,
			Date startingDate, Date dob, String className, String gender, String nationality, String religion,
			String currentAddress, String permanentAddress, int pincode, String country, long fathersPhone,
			long mothersPhone, String category, String fathersOccupation, String residencePhone,
			String photo, String bpl, String bplCardNo, String concession,
			String caste, String singleChild, String bloodGroup, String aadharNo, String singleParent,
			String livingWith, String fatherEmail, String motherEmail, String photoFather, String photoMother,
			String photoG1, String photoG2, /* String sendMessageMobileNo, */
			String routeName1, String discountFee, String rollno, String disability, String handicapped,
			Connection conn, String fatherAadharNo, String motherAadhaar, String lastSchoolName, String passedClass,
			String medium, String pResult, String boardName, String p_percent, String pReason, Date tcDate,
			String height, String weight, String eyes, String fname1, String relation1, String occupation1,
			String phone1, String address1, String fname2, String relation2, String occupation2, String phone2,
			String address2, String fatherQualification, String fatherDesignation, String fatherofficecontcatno,
			String fatherOfficeAdd, String fatherSchoolEmp, String motherQualification, String motherDesignation,
			String motherofficecontactno, String motherOfficeAdd, String motherSchoolEmp, String hostal, String sname1,
			String sClassName1, String sname2, String sClassName2, String studentPhone, String houseName,
			ArrayList<String> documentsSubmitted) {
		
		String schid = DBM.schoolId();
		String session = DBM.selectedSessionDetails(schid,conn);
		Timestamp jDate = new Timestamp(startingDate.getTime());
		Timestamp DOB = new Timestamp(dob.getTime());
		Timestamp tcdt = null;
		if (tcDate != null) {
			tcdt = new Timestamp(tcDate.getTime());
		}
		String query = "";

		String documnet = "";
		if (documentsSubmitted.size() > 0) {
			for (String ss : documentsSubmitted) {
				if (documnet.equals("")) {
					documnet = ss;
				} else {
					documnet = documnet + "," + ss;
				}

			}
		}
		/*
		String photo = previousImage;
		if (studentImage != null) {

			photo = studentImage.getFileName();
			makeProfile(studentImage, photo, conn);
		} else {

		}
		String photoFather = preFatherImg;
		if (fatherImage != null) {
			photoFather = fatherImage.getFileName();
			makeProfile(fatherImage, photoFather, conn);
		}

		String photoMother = preMotherImg;
		if (motherImage != null) {
			photoMother = studentImage.getFileName();
			makeProfile(motherImage, photoMother, conn);
		}

		String photoG1 = preG1Img;
		if (g1Image != null) {
			photoG1 = g1Image.getFileName();
			makeProfile(g1Image, photoG1, conn);
		}

		String photoG2 = preG2Img;
		if (g2Image != null) {
			photoG2 = g2Image.getFileName();
			makeProfile(g2Image, photoG2, conn);
		}
		 */
		PreparedStatement st = null;

		query = "update registration1 set fname=? ,student_image_path=?, dob=?,gender=?,postdate=?,fathersname=?,mname=?,classid=?,"
				+ "nationality=?,religion=?,pincode=?,country=?,currentAddress=?,permAddress=?,fathersPhone=?,mothersPhone=?,categoryid=?,"
				+ "residencePhone=?,fatheroccupation=?,bpl=?,bplCardNo=?,concession=?,caste=?,singleChild=?,bloodGroup=?,aadharNo=?,"
				+ "SingleParent=?,livingWith=?,fatherEmail=?,motherEmail=?,fatherImage=?,motherImage=?,g1Image=?,g2Image=?,"
				+ "sendMessageMobileNo=?,transportroute=?,discounttransportFee=?,rollno=?,disability=?, handicapped=?, "
				+ "FatherAadhaar=?,motherAadhaar=?,lastSchoolName=?,passedClass=?,medium1=?,pResult=?,board=?,p_percent=?,pReason=?, "
				+ "tcdt=?,height=?,weight=?,eyes=?,g1name=?,relation=?,occupation=?,mobile=?,address=?,gname2=?,relation2=?,occupation2=?, "
				+ " phone2=?,address2=?,fatherQualification=?,fatherdesignation=?,fatherofficecontactno=?,fatherOfficeAdd=?, fatherSchoolEmp=?, "
				+ " motherQualification=?,motherOccupation=?,motherofficecontacno=?,motherOfficeAdd=?,motherSchoolEmp=?,hostel=?,sname1=?, "
				+ " sclassid1=?,sname2=?,sclassid2=?,student_phone=?,sr_no=?,documents=?, housename=? where id='" + id
				+ "' and session='" + session + "' and status='ACTIVE' and schid='" + schid + "' ";

		try {
			st = conn.prepareStatement(query);
			st.setString(1, fname);
			st.setString(2, photo);
			st.setTimestamp(3, DOB);
			st.setString(4, gender);
			st.setTimestamp(5, jDate);
			st.setString(6, fathersname);
			st.setString(7, mothersname);
			st.setString(8, className);
			st.setString(9, nationality);
			st.setString(10, religion);
			st.setInt(11, pincode);
			st.setString(12, country);
			st.setString(13, currentAddress);
			st.setString(14, permanentAddress);
			st.setLong(15, fathersPhone);
			st.setLong(16, mothersPhone);
			st.setString(17, category);
			st.setString(18, residencePhone);
			st.setString(19, fathersOccupation);

			st.setString(20, bpl);
			st.setString(21, bplCardNo);
			st.setString(22, concession);
			st.setString(23, caste);
			st.setString(24, singleChild);
			st.setString(25, bloodGroup);
			st.setString(26, aadharNo);
			st.setString(27, singleParent);
			st.setString(28, livingWith);
			st.setString(29, fatherEmail);
			st.setString(30, motherEmail);
			st.setString(31, photoFather);
			st.setString(32, photoMother);
			st.setString(33, photoG1);
			st.setString(34, photoG2);
			st.setString(35, String.valueOf(fathersPhone));
			// st.setString(36, className);
			st.setString(36, routeName1);
			st.setString(37, discountFee);
			st.setString(38, rollno);
			st.setString(39, disability);
			st.setString(40, handicapped);
			st.setString(41, fatherAadharNo);
			st.setString(42, motherAadhaar);
			st.setString(43, lastSchoolName);
			st.setString(44, passedClass);
			st.setString(45, medium);
			st.setString(46, pResult);
			st.setString(47, boardName);
			st.setString(48, p_percent);
			st.setString(49, pReason);
			st.setTimestamp(50, tcdt);
			st.setString(51, height);
			st.setString(52, weight);
			st.setString(53, eyes);
			st.setString(54, fname1);
			st.setString(55, relation1);
			st.setString(56, occupation1);
			st.setString(57, phone1);
			st.setString(58, address1);
			st.setString(59, fname2);
			st.setString(60, relation2);
			st.setString(61, occupation2);
			st.setString(62, phone2);
			st.setString(63, address2);
			st.setString(64, fatherQualification);
			st.setString(65, fatherDesignation);
			st.setString(66, fatherofficecontcatno);
			st.setString(67, fatherOfficeAdd);
			st.setString(68, fatherSchoolEmp);
			st.setString(69, motherQualification);
			st.setString(70, motherDesignation);
			st.setString(71, motherofficecontactno);
			st.setString(72, motherOfficeAdd);
			st.setString(73, motherSchoolEmp);
			st.setString(74, hostal);
			st.setString(75, sname1);
			st.setString(76, sClassName1);
			st.setString(77, sname2);
			st.setString(78, sClassName2);
			st.setString(79, studentPhone);
			st.setString(80, addNumber);
			st.setString(81, documnet);
			st.setString(82, houseName);

			int i = st.executeUpdate();
			DBM.saveQueryLog(schid,st.toString(), conn);
			if (i == 1) {
				return 1;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return 0;
	}

	public int countAllAppointment(Connection conn, String string, String schid, String table) {
		Statement st=null;
		ResultSet rr=null;
		//String session=selectedSessionDetails();
		int count= 0;


		try {
			st=conn.createStatement();
			String query="select count(id) as total from "+table+" where status='"+string+"' and schid='"+schid+"'";

			rr = st.executeQuery(query);
			if (rr.next())
			{
				count=rr.getInt("total");
			}



		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (rr != null) {
				try {
					rr.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return count;
	}

	public ArrayList<AppointmentInfo> selectAllTypeAppointmentList(Connection conn, String string, String schid) {
		ArrayList<AppointmentInfo>list = new ArrayList<>();
		int i=1;
		Statement st = null;
		ResultSet rr = null;
		//String session = DatabaseMethods1.selectedSessionDetails();

		try {
			st = conn.createStatement();
			String qq = "select * from parent_appointment where status='"+string+"' and schid='"+schid+"' order by id desc";
			rr = st.executeQuery(qq);
			// ////// // System.out.println("listsize"+qq);
			while (rr.next()) {
				AppointmentInfo ss = new AppointmentInfo();
				ss.setSchId(rr.getString("schid"));
				StudentInfo info = DBM.studentDetailslistByAddNoAllStatus(schid,rr.getString("student_id"), conn);
				if(info.getFname()==null || info.getFname().equals("") || info.getFname().contains("null"))
				{
					ss.setStudentName("");
				}
				else
				{
					ss.setStudentName(info.getFname());
				}
				
				if(info.getClassName()==null || info.getClassName().equals("") || info.getClassName().contains("null"))
				{
					ss.setClassName("");
				}
				else
				{
					ss.setClassName(info.getClassName());
				}
				
				ss.setStudentId(rr.getString("student_id"));
				ss.setStrAppointmentdate(new SimpleDateFormat("dd-MM-yyyy").format(rr.getDate("appointment_date")));
				ss.setSno(i++);
				ss.setId(rr.getString("id"));
				ss.setDescription(rr.getString("description"));
				ss.setToMeet(rr.getString("to_meet"));
				ss.setStatus(rr.getString("status"));
				String status=rr.getString("status");
				ss.setRemark(rr.getString("remark"));
				if(status.equalsIgnoreCase("pending"))
				{
					ss.setShowApproved(false);
					ss.setShowRejected(false);
					ss.setShowRemark(false);
				}
				else if(status.equalsIgnoreCase("approved"))
				{
					ss.setShowApproved(true);
					ss.setShowRejected(true);
					ss.setShowRemark(false);
				}
				else if(status.equalsIgnoreCase("rejected"))
				{
					ss.setShowApproved(true);
					ss.setShowRejected(true);
					ss.setShowRemark(false);
				}
				ss.setAppointment_time(rr.getString("appointment_time"));
				ss.setStrAdddate(rr.getString("add_date"));
				if(info.getStatus().equalsIgnoreCase("active"))
				{
					list.add(ss);
		
				}
			
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			if (rr != null) {
				try {
					rr.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}


	public int updateRemarkByAdmin(Connection conn, String id, String remark, String userid) {
		PreparedStatement st = null;
		int i = 0;
		try
		{
			String qq = "update parent_appointment set remark=?,action_by=? where id='"+id+"'";

			st = conn.prepareStatement(qq);
			// st.setString(1, addmissionNumber);
			st.setString(1, remark);
			st.setString(2, userid);

			// ////// // System.out.println("update"+st.toString());
			i=st.executeUpdate();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return i;
	}

	public int updateStatusByAdmin(Connection conn, String id, String string, String remark, String userid,String table) {
		PreparedStatement st = null;
		int i = 0;
		try
		{
			String qq = "";

			qq = "update "+table+" set status=?,remark=?,action_by=? where id='"+id+"'";

			st = conn.prepareStatement(qq);
			// st.setString(1, addmissionNumber);
			st.setString(1, string);
			st.setString(2, remark);
			st.setString(3, userid);

			// ////// // System.out.println("update"+st.toString());
			i=st.executeUpdate();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return i;
	}

	public int submitParentAppointmentRequest(Connection conn, Date appointmentDate, Date currentDate, String Pending, String description,
			String toMeet, String schoolId, String username, String selectTime) {
		String t1 = new SimpleDateFormat("yyyy-MM-dd").format(appointmentDate);
		String t2 = new SimpleDateFormat("dd-MM-yyyy hh:mm a").format(currentDate);
		PreparedStatement pst = null;
		//String session = selectedSessionDetails();
		String query = "";
		int i = 0;
		try {
			/*if (examType.equalsIgnoreCase("scholastic")) {

			 */

			query="insert into parent_appointment(student_id,schid,appointment_date,description,to_meet,status,add_date,appointment_time,modify_status) values(?,?,?,?,?,?,?,?,?)";

			pst = conn.prepareStatement(query);

			pst.setString(1,username);
			pst.setString(2,schoolId);
			pst.setString(3,t1);
			pst.setString(4,description);
			pst.setString(5,toMeet);
			pst.setString(6,Pending);
			pst.setString(7,t2);
			pst.setString(8,selectTime);
			pst.setString(9,"No");

			i = pst.executeUpdate();
			// ////// // System.out.println("insert"+pst.toString());

			if (i > 0) {

				return i;
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return i;
	}


	public ArrayList<AppointmentInfo> selectAppointmentList(Connection conn, String schoolId, String username) {
		ArrayList<AppointmentInfo>list = new ArrayList<>();
		int i=1;
		Statement st = null;
		ResultSet rr = null;
		//String session = DatabaseMethods1.selectedSessionDetails();

		try {
			st = conn.createStatement();
			String qq = "select * from parent_appointment where student_id='"+username+"' and schid='"+schoolId+"' order by id desc";
			rr = st.executeQuery(qq);
			// ////// // System.out.println("listsize"+qq);
			while (rr.next()) {
				AppointmentInfo ss = new AppointmentInfo();
				ss.setSchId(rr.getString("schid"));
				ss.setStudentId(rr.getString("student_id"));
				ss.setStrAppointmentdate(new SimpleDateFormat("dd-MM-yyyy").format(rr.getDate("appointment_date")));
				ss.setSno(i++);
				ss.setId(rr.getString("id"));
				ss.setDescription(rr.getString("description"));
				ss.setToMeet(rr.getString("to_meet"));
				ss.setStatus(rr.getString("status"));
				ss.setRemark(rr.getString("remark"));
				String status=rr.getString("status");
				ss.setModify_status(rr.getString("modify_status"));
				String modifyStatus=rr.getString("modify_status");
				if(modifyStatus.equalsIgnoreCase("yes"))
				{
					ss.setShowModified(true);
				}
				else
				{
					ss.setShowModified(false);
				}
				if(status.equalsIgnoreCase("pending"))
				{
					ss.setShowDelete(false);
					ss.setShowModified(false);
				}
				else if(status.equalsIgnoreCase("approved"))
				{
					ss.setShowDelete(true);
					ss.setShowModified(true);
				}
				else if(status.equalsIgnoreCase("rejected") && modifyStatus.equalsIgnoreCase("No"))
				{
					ss.setShowDelete(true);
					ss.setShowModified(false);
				}
				else if(status.equalsIgnoreCase("rejected") && modifyStatus.equalsIgnoreCase("Yes"))
				{
					ss.setShowDelete(true);
					ss.setShowModified(true);
				}


				ss.setAppointment_time(rr.getString("appointment_time"));
				ss.setStrAdddate(rr.getString("add_date"));
				list.add(ss);

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			if (rr != null) {
				try {
					rr.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}

	public int updateAppointmentDateAndTime(Connection conn, String id, Date appointmentDate, Date currentDate,
			String selectTime) {
		String t1=new SimpleDateFormat("yyyy-MM-dd").format(appointmentDate);
		String t2=new SimpleDateFormat("dd-MM-yyyy hh:mm a").format(currentDate);
		PreparedStatement st = null;
		int i = 0;
		try
		{
			String qq = "update parent_appointment set appointment_date=?, add_date=?, appointment_time=? where id='"+id+"'";

			st = conn.prepareStatement(qq);
			// st.setString(1, addmissionNumber);
			st.setString(1, t1);
			st.setString(2, t2);
			st.setString(3, selectTime);
			// ////// // System.out.println("update"+st.toString());
			i=st.executeUpdate();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return i;
	}

	public int deleteAppointmentRequest(Connection conn, String id,String table) {
		int i = 0;
		PreparedStatement st = null;
		try
		{
			
			String qq = "delete from "+table+" where id=?";
			st = conn.prepareStatement(qq);
			st.setString(1, id);
			i = st.executeUpdate();

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return i;
	}

	public int updateModifyStatus(Connection conn, String id, String yes) {
		PreparedStatement st = null;
		int i = 0;
		try
		{
			String qq = "update parent_appointment set modify_status=? where id='"+id+"'";

			st = conn.prepareStatement(qq);
			// st.setString(1, addmissionNumber);
			st.setString(1, yes);

			// ////// // System.out.println("update"+st.toString());
			i=st.executeUpdate();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return i;
	}

	public int sendTeacherAppointmentRequest(Connection conn, Date appointmentDate, Date currentDate,
			String description, String appointmentTime, String name, String id, String type, String status, String schid) {
		String t1 = new SimpleDateFormat("yyyy-MM-dd").format(appointmentDate);
		String t2 = new SimpleDateFormat("dd-MM-yyyy hh:mm a").format(currentDate);
		PreparedStatement pst = null;
		//String session = selectedSessionDetails();
		String query = "";
		int i = 0;
		try {
			/*if (examType.equalsIgnoreCase("scholastic")) {

			 */

			query="insert into consent_table(student_id,appointment_date,appointment_time,type,add_date,description,user_id,status,schid) values(?,?,?,?,?,?,?,?,?)";

			pst = conn.prepareStatement(query);

			pst.setString(1,id);
			pst.setString(2,t1);
			pst.setString(3,appointmentTime);
			pst.setString(4,type);
			pst.setString(5,t2);
			pst.setString(6,description);
			pst.setString(7,name);
			pst.setString(8,status);
			pst.setString(9,schid);

			i = pst.executeUpdate();
			////// // System.out.println("insert"+pst.toString());

			if (i > 0) {

				return i;
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return i;
	}

	public ArrayList<AppointmentInfo> selectAllTypeAppointmentList(Connection conn, String status, String schoolId,
			String username) {
		ArrayList<AppointmentInfo>list = new ArrayList<>();
		int i=1;
		Statement st = null;
		ResultSet rr = null;
		//String session = DatabaseMethods1.selectedSessionDetails();

		try {
			st = conn.createStatement();
			String qq = "select * from consent_table where status='"+status+"' and schid='"+schoolId+"' and user_id='"+username+"'";
			rr = st.executeQuery(qq);
			////// // System.out.println("listsize"+qq);
			while (rr.next()) {
				AppointmentInfo ss = new AppointmentInfo();
				ss.setSchId(rr.getString("schid"));
				StudentInfo info = DBM.studentDetailslistByAddNoAllStatus(schoolId,rr.getString("student_id"), conn);
				ss.setStudentName(info.getFname());
				ss.setClassName(info.getClassName());
				ss.setStudentId(rr.getString("student_id"));
				ss.setStrAppointmentdate(rr.getString("appointment_date"));
				ss.setSno(i++);
				ss.setId(rr.getString("id"));
				ss.setDescription(rr.getString("description"));
				ss.setStatus(rr.getString("status"));
				if(ss.getStatus().equalsIgnoreCase("pending"))
				{
					ss.setShowDelete(false);
				}
				else
				{
					ss.setShowDelete(true);
				}
				ss.setRemark(rr.getString("remark"));
				ss.setAppointment_time(rr.getString("appointment_time"));
				ss.setStrAdddate(rr.getString("add_date"));
				list.add(ss);

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			if (rr != null) {
				try {
					rr.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}

	public ArrayList<AppointmentInfo> selectAllTypeTeacherAppointmentList(Connection conn, String status,
			String schoolId,String type,String userId) {
		ArrayList<AppointmentInfo>list = new ArrayList<>();
		int i=1;
		Statement st = null;
		ResultSet rr = null;
		//String session = DatabaseMethods1.selectedSessionDetails();
		String qq="";
		try {
			st = conn.createStatement();
			if(type.equalsIgnoreCase("student"))
			{
				qq = "select * from consent_table where status='"+status+"' and schid='"+schoolId+"' and student_id='"+userId+"'";
			}
			else if(type.equalsIgnoreCase("admin")
					|| type.equalsIgnoreCase("academic coordinator")
					|| type.equalsIgnoreCase("authority")
					|| type.equalsIgnoreCase("principal")
					|| type.equalsIgnoreCase("vice principal")
					|| type.equalsIgnoreCase("front office") 
					|| type.equalsIgnoreCase("office staff") )
			{
				qq = "select * from consent_table where status='"+status+"' and schid='"+schoolId+"'";
			}
			else
			{
				qq = "select * from consent_table where status='"+status+"' and schid='"+schoolId+"' and user_id='"+userId+"'";
			}

			rr = st.executeQuery(qq);
			////// // System.out.println("listsize"+qq);
			while (rr.next()) {
				AppointmentInfo ss = new AppointmentInfo();
				ss.setSchId(rr.getString("schid"));
				StudentInfo info = DBM.studentDetailslistByAddNoAllStatus(schoolId,rr.getString("student_id"), conn);
				ss.setStudentName(info.getFname());
				ss.setClassName(info.getClassName());
				ss.setStudentId(rr.getString("student_id"));
				ss.setStrAppointmentdate(new SimpleDateFormat("dd-MM-yyyy").format(rr.getDate("appointment_date")));
				ss.setSno(i++);
				ss.setId(rr.getString("id"));
				ss.setType(rr.getString("type"));
				ss.setDescription(rr.getString("description"));
				ss.setStatus(rr.getString("status"));
				ss.setRemark(rr.getString("remark"));
				if(ss.getStatus().equalsIgnoreCase("pending"))
				{
					ss.setShowDelete(false);
				}
				else
				{
					ss.setShowDelete(true);
				}
				ss.setUserId(rr.getString("user_id"));
				ss.setAddedBy(new DataBaseMeathodJson().employeeNameByUsername(ss.getUserId(), schoolId, conn));
				ss.setAppointment_time(rr.getString("appointment_time"));
				ss.setStrAdddate(rr.getString("add_date"));

				list.add(ss);

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			if (rr != null) {
				try {
					rr.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}



	public ArrayList<AppointmentInfo> selectAllTypeTeacherAppointmentListForJson(Connection conn, String schid,
			String type, String username) {
		ArrayList<AppointmentInfo>list = new ArrayList<>();
		int i=1;
		Statement st = null;
		ResultSet rr = null;
		//String session = DatabaseMethods1.selectedSessionDetails();
		String qq="";
		try {
			st = conn.createStatement();
			if(type.equalsIgnoreCase("student"))
			{
				qq = "select * from consent_table where schid='"+schid+"' and student_id='"+username+"'";
			}
			else if(type.equalsIgnoreCase("admin")
					|| type.equalsIgnoreCase("academic coordinator")
					|| type.equalsIgnoreCase("authority")
					|| type.equalsIgnoreCase("principal")
					|| type.equalsIgnoreCase("vice principal")
					|| type.equalsIgnoreCase("front office") 
					|| type.equalsIgnoreCase("office staff") )
			{
				qq = "select * from consent_table where schid='"+schid+"'";
			}
			else
			{
				qq = "select * from consent_table where schid='"+schid+"' and user_id='"+username+"'";
			}

			rr = st.executeQuery(qq);
			////// // System.out.println("listsize"+qq);
			while (rr.next()) {
				AppointmentInfo ss = new AppointmentInfo();
				ss.setSchId(rr.getString("schid"));
				StudentInfo info = DBM.studentDetailslistByAddNoAllStatus(schid,rr.getString("student_id"), conn);
				if(info.getFname()==null || info.getFname().equals("") || info.getFname().contains("null"))
				{
					ss.setStudentName("");
				}
				else
				{
					ss.setStudentName(info.getFname());
				}
				
				if(info.getClassName()==null || info.getClassName().equals("") || info.getClassName().contains("null"))
				{
					ss.setClassName("");
				}
				else
				{
					ss.setClassName(info.getClassName());
				}
				
				ss.setStudentId(rr.getString("student_id"));
				ss.setStrAppointmentdate(new SimpleDateFormat("dd-MM-yyyy").format(rr.getDate("appointment_date")));
				ss.setSno(i++);
				ss.setId(rr.getString("id"));
				ss.setType(rr.getString("type"));
				ss.setDescription(rr.getString("description"));
				ss.setStatus(rr.getString("status"));
				ss.setRemark(rr.getString("remark"));
				if(ss.getStatus().equalsIgnoreCase("pending"))
				{
					ss.setShowDelete(false);
				}
				else
				{
					ss.setShowDelete(true);
				}
				ss.setUserId(rr.getString("user_id"));
				ss.setAddedBy(new DataBaseMeathodJson().employeeNameByUsername(ss.getUserId(), schid, conn));
				ss.setAppointment_time(rr.getString("appointment_time"));
				ss.setStrAdddate(rr.getString("add_date"));

				list.add(ss);

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			if (rr != null) {
				try {
					rr.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}



	public int insertStudentPhotoUpload(Connection conn, String studentId, String schoolId, String string, String name1,
			String name2, String name3) {
		PreparedStatement pst=null;
		int i=0;
		try
		{
			String query="insert into student_photo_request (student_id,schid,s_photo,f_photo,m_photo,s_status,f_status,m_status,s_rem,f_rem,m_rem) values (?,?,?,?,?,?,?,?,?,?,?)";
			pst = conn.prepareStatement(query);
			pst.setString(1, studentId);
			pst.setString(2, schoolId);
			pst.setString(3, name1);
			pst.setString(4, name2);
			pst.setString(5, name3);
			pst.setString(6, string);
			pst.setString(7, string);
			pst.setString(8, string);
			pst.setString(9, " ");
			pst.setString(10, " ");
			pst.setString(11, " ");

			i = pst.executeUpdate();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(pst!=null)
					pst.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return i;
	}

	public String selectPhotosByStudentId(Connection conn, String studentId, String schoolId) {
		String photo="";
		Statement st=null;
		ResultSet rr=null;
		try {
			st=conn.createStatement();
			String query="select * from student_photo_request where student_id='"+studentId+"' and schid='"+schoolId+"'";
			rr = st.executeQuery(query);
			if (rr.next())
			{
				photo=rr.getString("s_photo")+","+rr.getString("f_photo")+","+rr.getString("m_photo")+","+rr.getString("s_status")+","+rr.getString("f_status")+","+rr.getString("m_status")+","+rr.getString("s_rem")+","+rr.getString("f_rem")+","+rr.getString("m_rem");
			}



		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (rr != null) {
				try {
					rr.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return photo;
	}

	public int checkStudentEntryByStudentId(Connection conn, String studentId, String schoolId) {
		int i=0;
		Statement st=null;
		ResultSet rr=null;
		try {
			st=conn.createStatement();
			String query="select * from student_photo_request where student_id='"+studentId+"' and schid='"+schoolId+"'";

			rr = st.executeQuery(query);
			if (rr.next())
			{
				i=1;
			}



		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (rr != null) {
				try {
					rr.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return i;
	}

	public int updateStudentPhotos(Connection conn, String studentId, String schoolId, String s1, String s2, String s3,
			String name1, String name2, String name3) {
		int i = 0;
		PreparedStatement st=null;
		try
		{
			String qq = "update student_photo_request set s_photo=?,f_photo=?,m_photo=?,s_status=?,f_status=?,m_status=? where student_id='"+studentId+"' and schid='"+schoolId+"'";

			st = conn.prepareStatement(qq);
			// st.setString(1, addmissionNumber);
			st.setString(1, name1);
			st.setString(2, name2);
			st.setString(3, name3);
			st.setString(4, s1);
			st.setString(5, s2);
			st.setString(6, s3);


			////// // System.out.println("update"+st.toString());
			i=st.executeUpdate();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return i;
	}

	public ArrayList<StudentPhotoInfo> selectAllPendingStudentPhotos(Connection conn, String schoolId) {
		ArrayList<StudentPhotoInfo>list = new ArrayList<>();
		int i=1;
		Statement st = null;
		ResultSet rr = null;
		//String session = DatabaseMethods1.selectedSessionDetails();

		try {
			st = conn.createStatement();
			String qq = "select * from student_photo_request where schid='"+schoolId+"' and (s_status='pending' or f_status='pending' or m_status='pending')";
			rr = st.executeQuery(qq);
			////// // System.out.println("listsize"+qq);
			while (rr.next()) {
				StudentPhotoInfo ss = new StudentPhotoInfo();
				ss.setSchid(rr.getString("schid"));
				StudentInfo info = DBM.studentDetailslistByAddNoAllStatus(schoolId,rr.getString("student_id"), conn);
				ss.setStudentName(info.getFname());
				ss.setClassName(info.getClassName());
				ss.setId(rr.getString("id"));
				ss.setStudentId(rr.getString("student_id"));

				ss.setsPhoto(rr.getString("s_photo"));
				ss.setsStatus(rr.getString("s_status"));

				ss.setfPhoto(rr.getString("f_photo"));
				ss.setfStatus(rr.getString("f_status"));

				ss.setmPhoto(rr.getString("m_photo"));
				ss.setmStatus(rr.getString("m_status"));

				if(rr.getString("s_status").equalsIgnoreCase("pending"))
				{
					ss.setShowSphoto(true);
				}
				else
				{
					ss.setShowSphoto(false);
				}
				if(rr.getString("f_status").equalsIgnoreCase("pending"))
				{
					ss.setShowFphoto(true);
				}
				else
				{
					ss.setShowFphoto(false);
				}
				if(rr.getString("m_status").equalsIgnoreCase("pending"))
				{
					ss.setShowMphoto(true);
				}
				else
				{
					ss.setShowMphoto(false);
				}
				ss.setSno(i++);

				list.add(ss);

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			if (rr != null) {
				try {
					rr.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}

	public int updateSinglePhotoStatus(Connection conn, String studentId, String schoolId, String status, String column,
			String remCol, String remark) {
		int i = 0;
		String qq="";
		PreparedStatement st=null;
		try
		{
			qq = "update student_photo_request set "+column+"=?,"+remCol+"=? where student_id='"+studentId+"' and schid='"+schoolId+"'";

			st = conn.prepareStatement(qq);
			// st.setString(1, addmissionNumber);
			st.setString(1, status);
			st.setString(2, remark);

			////// // System.out.println("update"+st.toString());
			i=st.executeUpdate();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return i;
	}

	public int updatePhotoStatusByStudentID(Connection conn, String studentId, String schoolId, String status) {
		int i = 0;
		String qq="";
		PreparedStatement st=null;
		try
		{
			qq = "update student_photo_request set s_status=? ,f_status=?, m_status=? where student_id='"+studentId+"' and schid='"+schoolId+"'";

			st = conn.prepareStatement(qq);
			// st.setString(1, addmissionNumber);
			st.setString(1, status);
			st.setString(2, status);
			st.setString(3, status);

			////// // System.out.println("update"+st.toString());
			i=st.executeUpdate();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return i;
	}


	public int updateStudentImagePathInRegistration1(Connection conn, String getsPhoto, String col,
			String studentId, String schoolId) {
		int i = 0;
		String qq="";
		PreparedStatement st=null;
		try
		{
			qq = "update registration1 set "+col+"=? where addNumber='"+studentId+"' and schid='"+schoolId+"'";

			st = conn.prepareStatement(qq);
			// st.setString(1, addmissionNumber);
			st.setString(1, getsPhoto);


			////// // System.out.println("update"+st.toString());
			i=st.executeUpdate();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return i;
	}

	public ArrayList<SelectItem> selectAllAppointMentPerson(Connection conn) {
		ArrayList<SelectItem>list = new ArrayList<>();
		String schid = DBM.schoolId();

		Statement st = null;
		ResultSet rs = null;
		try {
			st = conn.createStatement();
			String qq = "Select * from appointment_person where schid='" + schid + "'";
			// ////// // System.out.println(qq);
			rs = st.executeQuery(qq);

			while (rs.next()) {
				SelectItem ss = new SelectItem();
				ss.setLabel(rs.getString("name"));
				ss.setValue(rs.getString("designation"));
				ss.setDescription(rs.getString("id"));

				list.add(ss);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}

	public String checkDuplicacyOfNameAndDesignation(Connection conn, String name, String designation) {
		String value="";
		String schid = DBM.schoolId();
		PreparedStatement st=null;
		ResultSet rr=null;
		try {
			
			String query="select * from appointment_person where schid=? and name=? and designation=?";
			st=conn.prepareStatement(query);
			st.setString(1, schid);
			st.setString(2, name);
			st.setString(3, designation);
			rr = st.executeQuery();
			if (rr.next())
			{
				value="true";
			}



		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (rr != null) {
				try {
					rr.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return value;
	}

	public int deleteAppointmentSettingByTableId(Connection conn, String id) {
		int i = 0;
		PreparedStatement st = null;
		try
		{
			
			String qq = "delete from appointment_person where id=?";
			st = conn.prepareStatement(qq);
			st.setString(1, id);
			i = st.executeUpdate();

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return i;
	}

	public int updateNameAndDesignation(Connection conn, String name, String designation, String id) {
		int i = 0;
		String qq="";
		PreparedStatement st=null;
		String schid = DBM.schoolId();

		try
		{
			qq = "update appointment_person set name=?, designation=? where schid='"+schid+"' and id='"+id+"'";

			st = conn.prepareStatement(qq);
			// st.setString(1, addmissionNumber);
			st.setString(1, name);
			st.setString(2, designation);


			////// // System.out.println("update"+st.toString());
			i=st.executeUpdate();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return i;
	}

	public ArrayList<SelectItem> selectToMeetList(Connection conn, String schoolId) {
		ArrayList<SelectItem>list = new ArrayList<>();
		Statement st = null;
		ResultSet rs = null;
		try {
			st = conn.createStatement();
			String qq = "Select * from appointment_person where schid='" + schoolId + "'";
			// ////// // System.out.println(qq);
			rs = st.executeQuery(qq);

			while (rs.next()) {
				SelectItem ss = new SelectItem();
				ss.setLabel(rs.getString("name")+" / "+rs.getString("designation"));
				ss.setValue(rs.getString("name")+" / "+rs.getString("designation"));

				list.add(ss);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}

	public int submitAppointmentSetting(Connection conn, String schoolId, String name, String designation) {
		PreparedStatement pst=null;
		int i=0;
		try
		{
			String query="insert into appointment_person (name,designation,schid) values (?,?,?)";
			pst = conn.prepareStatement(query);
			pst.setString(1, name);
			pst.setString(2, designation);
			pst.setString(3, schoolId);


			i = pst.executeUpdate();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(pst!=null)
					pst.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return i;
	}

	public int updateAppointmentSettingInSchoolInfo(Connection conn, String schoolId, String startTime,
			String endTime) {
		int i = 0;
		String qq="";
		PreparedStatement st=null;
		try
		{
			qq = "update schoolinfo set appointStartTime=?, appointEndTime=? where schid='"+schoolId+"'";

			st = conn.prepareStatement(qq);
			// st.setString(1, addmissionNumber);
			st.setString(1, startTime);
			st.setString(2, endTime);


			////// // System.out.println("update"+st.toString());
			i=st.executeUpdate();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return i;
	}

	public int updateStudentPhotoByType(String studentid, String photoCol, String statusCol, String status,
			String imagefile, Connection conn)
	{
		PreparedStatement st = null;
		int i = 0;
		try
		{
			
			String qq = "update student_photo_request set "+photoCol+"=?,"+statusCol+"=? "
					+ "where student_id='"+studentid+"'";
			st = conn.prepareStatement(qq);
			st.setString(1, imagefile);
			st.setString(2, status);
			i = st.executeUpdate();

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return i;
	}
	
	public int addConsent(String selectedCLassSection, String selectedSection, String des, Date assShowDate,
			String assignmentPhotoPath1, String schoolId, String username, String type, Connection conn) 
	{
		int j = 0;
		String ldt = new SimpleDateFormat("yyyy-MM-dd").format(assShowDate);
		PreparedStatement st1 = null;
		try {
			String query = "insert into general_consent(class_id, section_id, description, last_date, attachment, schid, username, "
					+ "usertype) values(?,?,?,?,?,?,?,?)";

			st1 = conn.prepareStatement(query);
			st1.setString(1, selectedCLassSection);
			st1.setString(2, selectedSection);
			st1.setString(3, des);
			st1.setString(4, ldt);
			st1.setString(5, assignmentPhotoPath1);
			st1.setString(6, schoolId);
			st1.setString(7, username);
			st1.setString(8, type);
			int i = st1.executeUpdate();
			DBM.saveQueryLog(schoolId, st1.toString(), conn);

			if (i >= 1)
				return i;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (st1 != null) {
				try {
					st1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

		}
		return j;
	}
}
