package mgep.ContextAwareAasBpmn.Entities;

import java.io.Serializable;

public class AssetAdministrationShellDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private String aasIdentifier;
	private String aasIdShort;
	private String aasName;
	
	public AssetAdministrationShellDTO() {
	}

	public String getAasIdentifier() {
		return aasIdentifier;
	}

	public void setAasIdentifier(String aasIdentifier) {
		this.aasIdentifier = aasIdentifier;
	}

	public String getAasIdShort() {
		return aasIdShort;
	}

	public void setAasIdShort(String aasIdShort) {
		this.aasIdShort = aasIdShort;
	}

	public String getAasName() {
		return aasName;
	}

	public void setAasName(String aasName) {
		this.aasName = aasName;
	}
	

}
