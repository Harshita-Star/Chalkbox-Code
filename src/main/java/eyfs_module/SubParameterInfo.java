package eyfs_module;

import java.io.Serializable;

public class SubParameterInfo implements Serializable
{
	String id,name,subParaId,gradeId,gradeValue;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGradeId() {
		return gradeId;
	}

	public void setGradeId(String gradeId) {
		this.gradeId = gradeId;
	}

	public String getGradeValue() {
		return gradeValue;
	}

	public void setGradeValue(String gradeValue) {
		this.gradeValue = gradeValue;
	}

	public String getSubParaId() {
		return subParaId;
	}

	public void setSubParaId(String subParaId) {
		this.subParaId = subParaId;
	}
}
