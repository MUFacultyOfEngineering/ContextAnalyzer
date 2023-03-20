package mgep.ContextAwareAasBpmn.Entities;

import java.io.Serializable;

public class QualityParameterDTO extends InterfaceConnectionSheetDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private String qualityParameterCorrespondsTo;
	private String qualityParameterEvaluationExpression;
	
	public QualityParameterDTO() {
		super();
	}
	
	public QualityParameterDTO(String parameterName, String parameterType, String parameterValue, String qualityParameterCorrespondsTo, String qualityParameterEvaluationExpression) {
		super(parameterName, parameterType, parameterValue);
		this.qualityParameterCorrespondsTo = qualityParameterCorrespondsTo;
		this.qualityParameterEvaluationExpression = qualityParameterEvaluationExpression;
	}


	public String getQualityParameterCorrespondsTo() {
		return qualityParameterCorrespondsTo;
	}

	public void setQualityParameterCorrespondsTo(String qualityParameterCorrespondsTo) {
		this.qualityParameterCorrespondsTo = qualityParameterCorrespondsTo;
	}

	public String getQualityParameterEvaluationExpression() {
		return qualityParameterEvaluationExpression;
	}

	public void setQualityParameterEvaluationExpression(String qualityParameterEvaluationExpression) {
		this.qualityParameterEvaluationExpression = qualityParameterEvaluationExpression;
	}
}
