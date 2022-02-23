package student_module;

import java.io.Serializable;

public class VaccinationInfo implements Serializable
{
	String id="No", chicken_pox="No", dtp="No", hepatitis="No", hib="No", mmr="No", meningitis="No", polio="No", rabies="No",
			tb_bcg="No", typhoid="No", enq_id="No", parent_id="No", student_id="No", schid="";

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getChicken_pox() {
		return chicken_pox;
	}

	public void setChicken_pox(String chicken_pox) {
		this.chicken_pox = chicken_pox;
	}

	public String getDtp() {
		return dtp;
	}

	public void setDtp(String dtp) {
		this.dtp = dtp;
	}

	public String getHepatitis() {
		return hepatitis;
	}

	public void setHepatitis(String hepatitis) {
		this.hepatitis = hepatitis;
	}

	public String getHib() {
		return hib;
	}

	public void setHib(String hib) {
		this.hib = hib;
	}

	public String getMmr() {
		return mmr;
	}

	public void setMmr(String mmr) {
		this.mmr = mmr;
	}

	public String getMeningitis() {
		return meningitis;
	}

	public void setMeningitis(String meningitis) {
		this.meningitis = meningitis;
	}

	public String getPolio() {
		return polio;
	}

	public void setPolio(String polio) {
		this.polio = polio;
	}

	public String getRabies() {
		return rabies;
	}

	public void setRabies(String rabies) {
		this.rabies = rabies;
	}

	public String getTb_bcg() {
		return tb_bcg;
	}

	public void setTb_bcg(String tb_bcg) {
		this.tb_bcg = tb_bcg;
	}

	public String getTyphoid() {
		return typhoid;
	}

	public void setTyphoid(String typhoid) {
		this.typhoid = typhoid;
	}

	public String getEnq_id() {
		return enq_id;
	}

	public void setEnq_id(String enq_id) {
		this.enq_id = enq_id;
	}

	public String getParent_id() {
		return parent_id;
	}

	public void setParent_id(String parent_id) {
		this.parent_id = parent_id;
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
}
