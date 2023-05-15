package mgep.ContextAwareAasBpmn.Entities;

import java.io.Serializable;
import java.util.List;

public class RequestContextValServiceSelectionDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private String aasIdShort;
	private String serviceName;
	private List<QualityParameterDTO> qualityConditions;
	
	public RequestContextValServiceSelectionDTO() {
		super();
	}
	
	public RequestContextValServiceSelectionDTO(String aasIdShort, String serviceName) {
		super();
		this.aasIdShort = aasIdShort;
		this.serviceName = serviceName;
	}
	
	public RequestContextValServiceSelectionDTO(String aasIdShort, String serviceName, List<QualityParameterDTO> qualityConditions) {
		super();
		this.aasIdShort = aasIdShort;
		this.serviceName = serviceName;
		this.qualityConditions = qualityConditions;
	}
	
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public List<QualityParameterDTO> getQualityConditions() {
		return qualityConditions;
	}
	public void setQualityParameters(List<QualityParameterDTO> qualityConditions) {
		this.qualityConditions = qualityConditions;
	}
	public String getAasIdShort() {
		return aasIdShort;
	}
	public void setAasIdShort(String aasIdShort) {
		this.aasIdShort = aasIdShort;
	}
}
