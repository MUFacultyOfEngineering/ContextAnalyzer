package mgep.ContextAwareAasBpmn.Entities;

import java.io.Serializable;
import java.util.List;

public class ServiceDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private String aasIdentifier;
	private String url;
	private String method;
	private boolean isAsync;
	private String name;
	private String serviceIdentifier;
	private String description;
	private String requestBody;
	private String responseBody;
	private List<ServiceParameterDTO> inputParameters;
	private List<ServiceParameterDTO> outputParameters;
	private List<QualityParameterDTO> qualityParameters;
	
	public ServiceDTO(String aasIdentifier, String serviceIdentifier, String url, String method, boolean isAsync, String name, String description) {
		super();
		this.aasIdentifier = aasIdentifier;
		this.serviceIdentifier = serviceIdentifier;
		this.url = url;
		this.method = method;
		this.isAsync = isAsync;
		this.name = name;
		this.description=description;
	}

	public String getAasIdentifier() {
		return aasIdentifier;
	}

	public void setAasIdentifier(String aasIdentifier) {
		this.aasIdentifier = aasIdentifier;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public boolean isAsync() {
		return isAsync;
	}

	public void setAsync(boolean isAsync) {
		this.isAsync = isAsync;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getServiceIdentifier() {
		return serviceIdentifier;
	}

	public void setServiceIdentifier(String serviceIdentifier) {
		this.serviceIdentifier = serviceIdentifier;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getRequestBody() {
		return requestBody;
	}

	public void setRequestBody(String requestBody) {
		this.requestBody = requestBody;
	}

	public String getResponseBody() {
		return responseBody;
	}

	public void setResponseBody(String responseBody) {
		this.responseBody = responseBody;
	}

	public List<ServiceParameterDTO> getInputParameters() {
		return inputParameters;
	}

	public void setInputParameters(List<ServiceParameterDTO> inputParameters) {
		this.inputParameters = inputParameters;
	}

	public List<ServiceParameterDTO> getOutputParameters() {
		return outputParameters;
	}

	public void setOutputParameters(List<ServiceParameterDTO> outputParameters) {
		this.outputParameters = outputParameters;
	}

	public List<QualityParameterDTO> getQualityParameters() {
		return qualityParameters;
	}

	public void setQualityParameters(List<QualityParameterDTO> qualityParameters) {
		this.qualityParameters = qualityParameters;
	}
}
