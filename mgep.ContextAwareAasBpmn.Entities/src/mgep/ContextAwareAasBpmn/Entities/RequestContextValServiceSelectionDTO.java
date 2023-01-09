package mgep.ContextAwareAasBpmn.Entities;

import java.io.Serializable;
import java.util.List;

public class RequestContextValServiceSelectionDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private String aasIdShort;
	private String serviceName;
	private List<QualityParameterDTO> qualityParameters;
	
	public RequestContextValServiceSelectionDTO() {
		super();
	}
	
	public RequestContextValServiceSelectionDTO(String aasIdShort, String serviceName) {
		super();
		this.aasIdShort = aasIdShort;
		this.serviceName = serviceName;
	}
	
	public RequestContextValServiceSelectionDTO(String aasIdShort, String serviceName, List<QualityParameterDTO> qualityParameters) {
		super();
		this.aasIdShort = aasIdShort;
		this.serviceName = serviceName;
		this.qualityParameters = qualityParameters;
	}
	
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public List<QualityParameterDTO> getQualityParameters() {
		return qualityParameters;
	}
	public void setQualityParameters(List<QualityParameterDTO> qualityParameters) {
		this.qualityParameters = qualityParameters;
	}
	public String getAasIdShort() {
		return aasIdShort;
	}
	public void setAasIdShort(String aasIdShort) {
		this.aasIdShort = aasIdShort;
	}
}
