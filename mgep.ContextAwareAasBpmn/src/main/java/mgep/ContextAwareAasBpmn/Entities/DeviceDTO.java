package mgep.ContextAwareAasBpmn.Entities;

import java.io.Serializable;
import java.util.List;

public class DeviceDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private String aasIdentifier;
	private String apiDocumentation;
	private String description;
	private String identifier;
	private String ipAddress;
	private String isOnline;
	private String name;
	private String networkLatency;
	private List<SensorDTO> sensors;
	private List<ServiceDTO> services;
	
	public DeviceDTO() {
		super();
	}
	
	public String getAasIdentifier() {
		return aasIdentifier;
	}
	public void setAasIdentifier(String aasIdentifier) {
		this.aasIdentifier = aasIdentifier;
	}
	public String getApiDocumentation() {
		return apiDocumentation;
	}
	public void setApiDocumentation(String apiDocumentation) {
		this.apiDocumentation = apiDocumentation;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getIdentifier() {
		return identifier;
	}
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public String getIsOnline() {
		return isOnline;
	}
	public void setIsOnline(String isOnline) {
		this.isOnline = isOnline;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNetworkLatency() {
		return networkLatency;
	}
	public void setNetworkLatency(String networkLatency) {
		this.networkLatency = networkLatency;
	}
	public List<SensorDTO> getSensors() {
		return sensors;
	}
	public void setSensors(List<SensorDTO> sensors) {
		this.sensors = sensors;
	}
	public List<ServiceDTO> getServices() {
		return services;
	}
	public void setServices(List<ServiceDTO> services) {
		this.services = services;
	}
	
	
}
