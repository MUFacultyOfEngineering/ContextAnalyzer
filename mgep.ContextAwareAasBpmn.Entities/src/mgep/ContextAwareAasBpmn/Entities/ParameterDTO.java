package mgep.ContextAwareAasBpmn.Entities;

import java.io.Serializable;

public class ParameterDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	private String parameterName;
	private String parameterType;
	private String parameterValue;
	
	public ParameterDTO() {
		super();
	}

	public ParameterDTO(String parameterName, String parameterType, String parameterValue) {
		super();
		this.parameterName = parameterName;
		this.parameterType = parameterType;
		this.parameterValue = parameterValue;
	}

	public String getParameterName() {
		return parameterName;
	}

	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}

	public String getParameterType() {
		return parameterType;
	}

	public void setParameterType(String parameterType) {
		this.parameterType = parameterType;
	}

	public String getParameterValue() {
		return parameterValue;
	}

	public void setParameterValue(String parameterValue) {
		this.parameterValue = parameterValue;
	}

}
