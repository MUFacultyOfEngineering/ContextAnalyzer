package mgep.ContextAwareAasBpmn.Entities;

import java.io.Serializable;
import java.util.List;

public class RequestContextValServiceSelectionDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private String aasIdentifier;
	private String serviceName;
	private List<QualityParameterDTO> qualityParameters;
	
	public RequestContextValServiceSelectionDTO() {
		super();
	}
	
	public RequestContextValServiceSelectionDTO(String aasIdentifier, String serviceName) {
		super();
		this.aasIdentifier = aasIdentifier;
		this.serviceName = serviceName;
	}
	
	public RequestContextValServiceSelectionDTO(String aasIdentifier, String serviceName, List<QualityParameterDTO> qualityParameters) {
		super();
		this.aasIdentifier = aasIdentifier;
		this.serviceName = serviceName;
		this.qualityParameters = qualityParameters;
	}
	
	public String getAasIdentifier() {
		return aasIdentifier;
	}
	public void setAasIdentifier(String aasIdentifier) {
		this.aasIdentifier = aasIdentifier;
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
}
