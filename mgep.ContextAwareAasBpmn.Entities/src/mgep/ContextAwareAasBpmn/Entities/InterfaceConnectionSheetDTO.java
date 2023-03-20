package mgep.ContextAwareAasBpmn.Entities;

public class InterfaceConnectionSheetDTO extends ParameterDTO {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String icsCommunicationProtocol;
	private String icsCommunicationPort;
	private String icsEndpointNodeId;
	private String icsNameSpaceIndex;
	private String icsShortName;
	private String icsDescription;
	private String icsDataType;
	
	public InterfaceConnectionSheetDTO() {
		
	}
	
	public InterfaceConnectionSheetDTO(String parameterName, String parameterType, String parameterValue) {
		super(parameterName, parameterType, parameterValue);
	}
	
	public String getIcsCommunicationProtocol() {
		return icsCommunicationProtocol;
	}
	public void setIcsCommunicationProtocol(String icsCommunicationProtocol) {
		this.icsCommunicationProtocol = icsCommunicationProtocol;
	}
	public String getIcsCommunicationPort() {
		return icsCommunicationPort;
	}
	public void setIcsCommunicationPort(String icsCommunicationPort) {
		this.icsCommunicationPort = icsCommunicationPort;
	}
	public String getIcsEndpointNodeId() {
		return icsEndpointNodeId;
	}
	public void setIcsEndpointNodeId(String icsEndpointNodeId) {
		this.icsEndpointNodeId = icsEndpointNodeId;
	}
	public String getIcsNameSpaceIndex() {
		return icsNameSpaceIndex;
	}
	public void setIcsNameSpaceIndex(String icsNameSpaceIndex) {
		this.icsNameSpaceIndex = icsNameSpaceIndex;
	}
	public String getIcsShortName() {
		return icsShortName;
	}
	public void setIcsShortName(String icsShortName) {
		this.icsShortName = icsShortName;
	}
	public String getIcsDescription() {
		return icsDescription;
	}
	public void setIcsDescription(String icsDescription) {
		this.icsDescription = icsDescription;
	}
	public String getIcsDataType() {
		return icsDataType;
	}
	public void setIcsDataType(String icsDataType) {
		this.icsDataType = icsDataType;
	}
}
