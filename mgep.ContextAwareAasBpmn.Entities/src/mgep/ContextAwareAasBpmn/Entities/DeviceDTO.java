package mgep.ContextAwareAasBpmn.Entities;

import java.io.Serializable;
import java.util.List;

public class DeviceDTO extends AssetAdministrationShellDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private String deviceApiDocumentation;
	private String deviceDescription;
	private String deviceIdentifier;
	private String deviceIPAddress;
	private boolean deviceIsOnline;
	private String deviceName;
	private String deviceNetworkLatency;
	private List<SensorDTO> sensors;
	private List<ServiceDTO> services;
	
	public DeviceDTO() {
		super();
	}

	public String getDeviceApiDocumentation() {
		return deviceApiDocumentation;
	}

	public void setDeviceApiDocumentation(String deviceApiDocumentation) {
		this.deviceApiDocumentation = deviceApiDocumentation;
	}

	public String getDeviceDescription() {
		return deviceDescription;
	}

	public void setDeviceDescription(String deviceDescription) {
		this.deviceDescription = deviceDescription;
	}

	public String getDeviceIdentifier() {
		return deviceIdentifier;
	}

	public void setDeviceIdentifier(String deviceIdentifier) {
		this.deviceIdentifier = deviceIdentifier;
	}

	public String getDeviceIPAddress() {
		return deviceIPAddress;
	}

	public void setDeviceIPAddress(String deviceIPAddress) {
		this.deviceIPAddress = deviceIPAddress;
	}

	public boolean getDeviceIsOnline() {
		return deviceIsOnline;
	}

	public void setDeviceIsOnline(boolean deviceIsOnline) {
		this.deviceIsOnline = deviceIsOnline;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getDeviceNetworkLatency() {
		return deviceNetworkLatency;
	}

	public void setDeviceNetworkLatency(String deviceNetworkLatency) {
		this.deviceNetworkLatency = deviceNetworkLatency;
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
