package mgep.ContextAwareAasBpmn.Entities;

import java.io.Serializable;
import java.util.List;

public class ServiceDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private String aasIdentifier;
	private String aasIdShort;
	private String serviceDescription;
	private String serviceIdentifier;
	private boolean serviceIsAsync;
	private String serviceMethod;
	private String serviceName;
	private String serviceUrl;
	private String serviceRequestBody;
	private String serviceResponseBody;
	private List<ParameterDTO> serviceInputParameters;
	private List<ParameterDTO> serviceOutputParameters;
	private List<QualityParameterDTO> serviceQualityParameters;
	
	public ServiceDTO() {
		super();
	}	

	public ServiceDTO(String aasIdentifier, String aasIdShort, String serviceIdentifier, String serviceUrl, String serviceMethod, boolean serviceIsAsync, String serviceName, String serviceDescription) {
		super();
		this.aasIdentifier = aasIdentifier;
		this.aasIdShort = aasIdShort;
		this.serviceDescription = serviceDescription;
		this.serviceIdentifier = serviceIdentifier;
		this.serviceIsAsync = serviceIsAsync;
		this.serviceMethod = serviceMethod;
		this.serviceName = serviceName;
		this.serviceUrl = serviceUrl;
	}

	public String getAasIdentifier() {
		return aasIdentifier;
	}

	public void setAasIdentifier(String aasIdentifier) {
		this.aasIdentifier = aasIdentifier;
	}

	public String getServiceDescription() {
		return serviceDescription;
	}

	public void setServiceDescription(String serviceDescription) {
		this.serviceDescription = serviceDescription;
	}

	public String getServiceIdentifier() {
		return serviceIdentifier;
	}

	public void setServiceIdentifier(String serviceIdentifier) {
		this.serviceIdentifier = serviceIdentifier;
	}

	public boolean isServiceIsAsync() {
		return serviceIsAsync;
	}

	public void setServiceIsAsync(boolean serviceIsAsync) {
		this.serviceIsAsync = serviceIsAsync;
	}

	public String getServiceMethod() {
		return serviceMethod;
	}

	public void setServiceMethod(String serviceMethod) {
		this.serviceMethod = serviceMethod;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getServiceUrl() {
		return serviceUrl;
	}

	public void setServiceUrl(String serviceUrl) {
		this.serviceUrl = serviceUrl;
	}

	public String getServiceRequestBody() {
		return serviceRequestBody;
	}

	public void setServiceRequestBody(String serviceRequestBody) {
		this.serviceRequestBody = serviceRequestBody;
	}

	public String getServiceResponseBody() {
		return serviceResponseBody;
	}

	public void setServiceResponseBody(String serviceResponseBody) {
		this.serviceResponseBody = serviceResponseBody;
	}

	public List<ParameterDTO> getServiceInputParameters() {
		return serviceInputParameters;
	}

	public void setServiceInputParameters(List<ParameterDTO> serviceInputParameters) {
		this.serviceInputParameters = serviceInputParameters;
	}

	public List<ParameterDTO> getServiceOutputParameters() {
		return serviceOutputParameters;
	}

	public void setServiceOutputParameters(List<ParameterDTO> serviceOutputParameters) {
		this.serviceOutputParameters = serviceOutputParameters;
	}

	public List<QualityParameterDTO> getServiceQualityParameters() {
		return serviceQualityParameters;
	}

	public void setServiceQualityParameters(List<QualityParameterDTO> serviceQualityParameters) {
		this.serviceQualityParameters = serviceQualityParameters;
	}

	public String getAasIdShort() {
		return aasIdShort;
	}

	public void setAasIdShort(String aasIdShort) {
		this.aasIdShort = aasIdShort;
	}
	
}
