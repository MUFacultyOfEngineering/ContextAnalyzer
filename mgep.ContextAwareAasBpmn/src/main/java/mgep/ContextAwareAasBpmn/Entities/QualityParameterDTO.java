package mgep.ContextAwareAasBpmn.Entities;

import java.io.Serializable;

public class QualityParameterDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name;
	private String correspondsTo;
	private String dataType;
	private String value;
	private String evaluationExpression;
	
	public QualityParameterDTO() {
		super();
	}
	
	public QualityParameterDTO(String name, String evaluationExpression) {
		super();
		this.name=name;
		this.evaluationExpression = evaluationExpression;
	}
	
	public QualityParameterDTO(String name, String correspondsTo, String dataType, String value, String evaluationExpression) {
		super();
		this.name=name;
		this.correspondsTo=correspondsTo;
		this.dataType = dataType;
		this.value=value;
		this.evaluationExpression = evaluationExpression;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCorrespondsTo() {
		return correspondsTo;
	}

	public void setCorrespondsTo(String correspondsTo) {
		this.correspondsTo = correspondsTo;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getEvaluationExpression() {
		return evaluationExpression;
	}

	public void setEvaluationExpression(String evaluationExpression) {
		this.evaluationExpression = evaluationExpression;
	}
	
}
