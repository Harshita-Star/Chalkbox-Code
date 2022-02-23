package student_module;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class OnlineAdmInfo implements Serializable
{
	DocumentInfo docinfo = new DocumentInfo();
	MedicalInfo medinfo = new MedicalInfo();
	VaccinationInfo vacinfo = new VaccinationInfo();
	SiblingAEInfo sibinfo = new SiblingAEInfo();

	String id="", year="", classid="", classname="", applydateStr="", st_name="", gender="Male", dobStr="", birth_place="", last_school="", nationality="UNITED ARAB EMIRATES", religion="", lang="",
			ppt_no="", ppt_issue_dateStr="", ppt_exp_dateStr="", ppt_place="", visa_no="", visa_exp_dateStr="", visa_issue_dateStr="", other_lang="",address="", ethnicity="", emid="",
			eng_prof="Beginner", independence="Need Support", personal="Need Support", general="Need Support", demotion="No", sen="No", sen_file="", exceptional="No", exceptional_rem="", attention="No", behaviour="No",
			learning="No", physical="No", parent_type="New Parents", parent_id="", parent_pwd="", applicant_name="", applicant_relation="Father", f_name="", f_occupation="", f_employer="",
			f_mob="", f_email="", f_work="", f_address="", f_emid="", m_name="", m_occupation="", m_employer="", m_mob="", m_email="", m_work="", m_address="", m_emid="",
			receipient="Father", fee_payee="Parents", company="", bill_address="", comp_phone="", comp_email="", online_admcol="", g_name="", g_occupation="", g_employer="", g_mob="",
			g_email="", g_work="", g_address="", g_emid="", g_rel="", e1_name="", e1_mob="", e1_rel="", e2_name="", e2_mob="", e2_rel="", bus_service="No", pickup_name="", f_image="",
			m_image="", g_image="", st_image="", food_type="Veg", login_id="", student_id="", schid="", status="",other_rel="",g_nation="",
			sch_status="",remark="";

	ArrayList<String> pickupList = new ArrayList<>();

	Date apply_date=new Date(),dob,ppt_issue_date,ppt_exp_date,visa_exp_date, visa_issue_date;

	boolean disableDlt=true,disableEdit=true,disableAccept=true,disableReject=true,disableReview=true;

	int sno;

	public DocumentInfo getDocinfo() {
		return docinfo;
	}

	public void setDocinfo(DocumentInfo docinfo) {
		this.docinfo = docinfo;
	}

	public MedicalInfo getMedinfo() {
		return medinfo;
	}

	public void setMedinfo(MedicalInfo medinfo) {
		this.medinfo = medinfo;
	}

	public VaccinationInfo getVacinfo() {
		return vacinfo;
	}

	public void setVacinfo(VaccinationInfo vacinfo) {
		this.vacinfo = vacinfo;
	}

	public SiblingAEInfo getSibinfo() {
		return sibinfo;
	}

	public void setSibinfo(SiblingAEInfo sibinfo) {
		this.sibinfo = sibinfo;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getClassid() {
		return classid;
	}

	public void setClassid(String classid) {
		this.classid = classid;
	}

	public String getClassname() {
		return classname;
	}

	public void setClassname(String classname) {
		this.classname = classname;
	}

	public String getApplydateStr() {
		return applydateStr;
	}

	public void setApplydateStr(String applydateStr) {
		this.applydateStr = applydateStr;
	}

	public String getSt_name() {
		return st_name;
	}

	public void setSt_name(String st_name) {
		this.st_name = st_name;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getDobStr() {
		return dobStr;
	}

	public void setDobStr(String dobStr) {
		this.dobStr = dobStr;
	}

	public String getBirth_place() {
		return birth_place;
	}

	public void setBirth_place(String birth_place) {
		this.birth_place = birth_place;
	}

	public String getLast_school() {
		return last_school;
	}

	public void setLast_school(String last_school) {
		this.last_school = last_school;
	}

	public String getNationality() {
		return nationality;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	public String getReligion() {
		return religion;
	}

	public void setReligion(String religion) {
		this.religion = religion;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public String getPpt_no() {
		return ppt_no;
	}

	public void setPpt_no(String ppt_no) {
		this.ppt_no = ppt_no;
	}

	public String getPpt_issue_dateStr() {
		return ppt_issue_dateStr;
	}

	public void setPpt_issue_dateStr(String ppt_issue_dateStr) {
		this.ppt_issue_dateStr = ppt_issue_dateStr;
	}

	public String getPpt_exp_dateStr() {
		return ppt_exp_dateStr;
	}

	public void setPpt_exp_dateStr(String ppt_exp_dateStr) {
		this.ppt_exp_dateStr = ppt_exp_dateStr;
	}

	public String getPpt_place() {
		return ppt_place;
	}

	public void setPpt_place(String ppt_place) {
		this.ppt_place = ppt_place;
	}

	public String getVisa_no() {
		return visa_no;
	}

	public void setVisa_no(String visa_no) {
		this.visa_no = visa_no;
	}

	public String getVisa_exp_dateStr() {
		return visa_exp_dateStr;
	}

	public void setVisa_exp_dateStr(String visa_exp_dateStr) {
		this.visa_exp_dateStr = visa_exp_dateStr;
	}

	public String getVisa_issue_dateStr() {
		return visa_issue_dateStr;
	}

	public void setVisa_issue_dateStr(String visa_issue_dateStr) {
		this.visa_issue_dateStr = visa_issue_dateStr;
	}

	public String getOther_lang() {
		return other_lang;
	}

	public void setOther_lang(String other_lang) {
		this.other_lang = other_lang;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getEthnicity() {
		return ethnicity;
	}

	public void setEthnicity(String ethnicity) {
		this.ethnicity = ethnicity;
	}

	public String getEmid() {
		return emid;
	}

	public void setEmid(String emid) {
		this.emid = emid;
	}

	public String getEng_prof() {
		return eng_prof;
	}

	public void setEng_prof(String eng_prof) {
		this.eng_prof = eng_prof;
	}

	public String getIndependence() {
		return independence;
	}

	public void setIndependence(String independence) {
		this.independence = independence;
	}

	public String getPersonal() {
		return personal;
	}

	public void setPersonal(String personal) {
		this.personal = personal;
	}

	public String getGeneral() {
		return general;
	}

	public void setGeneral(String general) {
		this.general = general;
	}

	public String getDemotion() {
		return demotion;
	}

	public void setDemotion(String demotion) {
		this.demotion = demotion;
	}

	public String getSen() {
		return sen;
	}

	public void setSen(String sen) {
		this.sen = sen;
	}

	public String getSen_file() {
		return sen_file;
	}

	public void setSen_file(String sen_file) {
		this.sen_file = sen_file;
	}

	public String getExceptional() {
		return exceptional;
	}

	public void setExceptional(String exceptional) {
		this.exceptional = exceptional;
	}

	public String getExceptional_rem() {
		return exceptional_rem;
	}

	public void setExceptional_rem(String exceptional_rem) {
		this.exceptional_rem = exceptional_rem;
	}

	public String getAttention() {
		return attention;
	}

	public void setAttention(String attention) {
		this.attention = attention;
	}

	public String getBehaviour() {
		return behaviour;
	}

	public void setBehaviour(String behaviour) {
		this.behaviour = behaviour;
	}

	public String getLearning() {
		return learning;
	}

	public void setLearning(String learning) {
		this.learning = learning;
	}

	public String getPhysical() {
		return physical;
	}

	public void setPhysical(String physical) {
		this.physical = physical;
	}

	public String getParent_type() {
		return parent_type;
	}

	public void setParent_type(String parent_type) {
		this.parent_type = parent_type;
	}

	public String getParent_id() {
		return parent_id;
	}

	public void setParent_id(String parent_id) {
		this.parent_id = parent_id;
	}

	public String getParent_pwd() {
		return parent_pwd;
	}

	public void setParent_pwd(String parent_pwd) {
		this.parent_pwd = parent_pwd;
	}

	public String getApplicant_name() {
		return applicant_name;
	}

	public void setApplicant_name(String applicant_name) {
		this.applicant_name = applicant_name;
	}

	public String getApplicant_relation() {
		return applicant_relation;
	}

	public void setApplicant_relation(String applicant_relation) {
		this.applicant_relation = applicant_relation;
	}

	public String getF_name() {
		return f_name;
	}

	public void setF_name(String f_name) {
		this.f_name = f_name;
	}

	public String getF_occupation() {
		return f_occupation;
	}

	public void setF_occupation(String f_occupation) {
		this.f_occupation = f_occupation;
	}

	public String getF_employer() {
		return f_employer;
	}

	public void setF_employer(String f_employer) {
		this.f_employer = f_employer;
	}

	public String getF_mob() {
		return f_mob;
	}

	public void setF_mob(String f_mob) {
		this.f_mob = f_mob;
	}

	public String getF_email() {
		return f_email;
	}

	public void setF_email(String f_email) {
		this.f_email = f_email;
	}

	public String getF_work() {
		return f_work;
	}

	public void setF_work(String f_work) {
		this.f_work = f_work;
	}

	public String getF_address() {
		return f_address;
	}

	public void setF_address(String f_address) {
		this.f_address = f_address;
	}

	public String getF_emid() {
		return f_emid;
	}

	public void setF_emid(String f_emid) {
		this.f_emid = f_emid;
	}

	public String getM_name() {
		return m_name;
	}

	public void setM_name(String m_name) {
		this.m_name = m_name;
	}

	public String getM_occupation() {
		return m_occupation;
	}

	public void setM_occupation(String m_occupation) {
		this.m_occupation = m_occupation;
	}

	public String getM_employer() {
		return m_employer;
	}

	public void setM_employer(String m_employer) {
		this.m_employer = m_employer;
	}

	public String getM_mob() {
		return m_mob;
	}

	public void setM_mob(String m_mob) {
		this.m_mob = m_mob;
	}

	public String getM_email() {
		return m_email;
	}

	public void setM_email(String m_email) {
		this.m_email = m_email;
	}

	public String getM_work() {
		return m_work;
	}

	public void setM_work(String m_work) {
		this.m_work = m_work;
	}

	public String getM_address() {
		return m_address;
	}

	public void setM_address(String m_address) {
		this.m_address = m_address;
	}

	public String getM_emid() {
		return m_emid;
	}

	public void setM_emid(String m_emid) {
		this.m_emid = m_emid;
	}

	public String getReceipient() {
		return receipient;
	}

	public void setReceipient(String receipient) {
		this.receipient = receipient;
	}

	public String getFee_payee() {
		return fee_payee;
	}

	public void setFee_payee(String fee_payee) {
		this.fee_payee = fee_payee;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getBill_address() {
		return bill_address;
	}

	public void setBill_address(String bill_address) {
		this.bill_address = bill_address;
	}

	public String getComp_phone() {
		return comp_phone;
	}

	public void setComp_phone(String comp_phone) {
		this.comp_phone = comp_phone;
	}

	public String getComp_email() {
		return comp_email;
	}

	public void setComp_email(String comp_email) {
		this.comp_email = comp_email;
	}

	public String getOnline_admcol() {
		return online_admcol;
	}

	public void setOnline_admcol(String online_admcol) {
		this.online_admcol = online_admcol;
	}

	public String getG_name() {
		return g_name;
	}

	public void setG_name(String g_name) {
		this.g_name = g_name;
	}

	public String getG_occupation() {
		return g_occupation;
	}

	public void setG_occupation(String g_occupation) {
		this.g_occupation = g_occupation;
	}

	public String getG_employer() {
		return g_employer;
	}

	public void setG_employer(String g_employer) {
		this.g_employer = g_employer;
	}

	public String getG_mob() {
		return g_mob;
	}

	public void setG_mob(String g_mob) {
		this.g_mob = g_mob;
	}

	public String getG_email() {
		return g_email;
	}

	public void setG_email(String g_email) {
		this.g_email = g_email;
	}

	public String getG_work() {
		return g_work;
	}

	public void setG_work(String g_work) {
		this.g_work = g_work;
	}

	public String getG_address() {
		return g_address;
	}

	public void setG_address(String g_address) {
		this.g_address = g_address;
	}

	public String getG_emid() {
		return g_emid;
	}

	public void setG_emid(String g_emid) {
		this.g_emid = g_emid;
	}

	public String getG_rel() {
		return g_rel;
	}

	public void setG_rel(String g_rel) {
		this.g_rel = g_rel;
	}

	public String getE1_name() {
		return e1_name;
	}

	public void setE1_name(String e1_name) {
		this.e1_name = e1_name;
	}

	public String getE1_mob() {
		return e1_mob;
	}

	public void setE1_mob(String e1_mob) {
		this.e1_mob = e1_mob;
	}

	public String getE1_rel() {
		return e1_rel;
	}

	public void setE1_rel(String e1_rel) {
		this.e1_rel = e1_rel;
	}

	public String getE2_name() {
		return e2_name;
	}

	public void setE2_name(String e2_name) {
		this.e2_name = e2_name;
	}

	public String getE2_mob() {
		return e2_mob;
	}

	public void setE2_mob(String e2_mob) {
		this.e2_mob = e2_mob;
	}

	public String getE2_rel() {
		return e2_rel;
	}

	public void setE2_rel(String e2_rel) {
		this.e2_rel = e2_rel;
	}

	public String getBus_service() {
		return bus_service;
	}

	public void setBus_service(String bus_service) {
		this.bus_service = bus_service;
	}

	public String getPickup_name() {
		return pickup_name;
	}

	public void setPickup_name(String pickup_name) {
		this.pickup_name = pickup_name;
	}

	public String getF_image() {
		return f_image;
	}

	public void setF_image(String f_image) {
		this.f_image = f_image;
	}

	public String getM_image() {
		return m_image;
	}

	public void setM_image(String m_image) {
		this.m_image = m_image;
	}

	public String getG_image() {
		return g_image;
	}

	public void setG_image(String g_image) {
		this.g_image = g_image;
	}

	public String getSt_image() {
		return st_image;
	}

	public void setSt_image(String st_image) {
		this.st_image = st_image;
	}

	public String getFood_type() {
		return food_type;
	}

	public void setFood_type(String food_type) {
		this.food_type = food_type;
	}

	public String getLogin_id() {
		return login_id;
	}

	public void setLogin_id(String login_id) {
		this.login_id = login_id;
	}

	public String getStudent_id() {
		return student_id;
	}

	public void setStudent_id(String student_id) {
		this.student_id = student_id;
	}

	public String getSchid() {
		return schid;
	}

	public void setSchid(String schid) {
		this.schid = schid;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getOther_rel() {
		return other_rel;
	}

	public void setOther_rel(String other_rel) {
		this.other_rel = other_rel;
	}

	public Date getApply_date() {
		return apply_date;
	}

	public void setApply_date(Date apply_date) {
		this.apply_date = apply_date;
	}

	public Date getDob() {
		return dob;
	}

	public void setDob(Date dob) {
		this.dob = dob;
	}

	public Date getPpt_issue_date() {
		return ppt_issue_date;
	}

	public void setPpt_issue_date(Date ppt_issue_date) {
		this.ppt_issue_date = ppt_issue_date;
	}

	public Date getPpt_exp_date() {
		return ppt_exp_date;
	}

	public void setPpt_exp_date(Date ppt_exp_date) {
		this.ppt_exp_date = ppt_exp_date;
	}

	public Date getVisa_exp_date() {
		return visa_exp_date;
	}

	public void setVisa_exp_date(Date visa_exp_date) {
		this.visa_exp_date = visa_exp_date;
	}

	public Date getVisa_issue_date() {
		return visa_issue_date;
	}

	public void setVisa_issue_date(Date visa_issue_date) {
		this.visa_issue_date = visa_issue_date;
	}

	public int getSno() {
		return sno;
	}

	public void setSno(int sno) {
		this.sno = sno;
	}

	public String getG_nation() {
		return g_nation;
	}

	public void setG_nation(String g_nation) {
		this.g_nation = g_nation;
	}

	public ArrayList<String> getPickupList() {
		return pickupList;
	}

	public void setPickupList(ArrayList<String> pickupList) {
		this.pickupList = pickupList;
	}

	public boolean isDisableDlt() {
		return disableDlt;
	}

	public void setDisableDlt(boolean disableDlt) {
		this.disableDlt = disableDlt;
	}

	public boolean isDisableEdit() {
		return disableEdit;
	}

	public void setDisableEdit(boolean disableEdit) {
		this.disableEdit = disableEdit;
	}

	public String getSch_status() {
		return sch_status;
	}

	public void setSch_status(String sch_status) {
		this.sch_status = sch_status;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public boolean isDisableAccept() {
		return disableAccept;
	}

	public void setDisableAccept(boolean disableAccept) {
		this.disableAccept = disableAccept;
	}

	public boolean isDisableReject() {
		return disableReject;
	}

	public void setDisableReject(boolean disableReject) {
		this.disableReject = disableReject;
	}

	public boolean isDisableReview() {
		return disableReview;
	}

	public void setDisableReview(boolean disableReview) {
		this.disableReview = disableReview;
	}


}
