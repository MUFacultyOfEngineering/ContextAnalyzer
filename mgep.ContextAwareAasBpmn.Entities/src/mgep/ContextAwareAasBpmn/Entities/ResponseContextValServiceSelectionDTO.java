package mgep.ContextAwareAasBpmn.Entities;

import java.io.Serializable;

public class ResponseContextValServiceSelectionDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private boolean canExecute;
	private String message;
	private ServiceDTO suggestedService;
	
	public ResponseContextValServiceSelectionDTO(boolean canExecute, String message) {
		super();
		this.canExecute = canExecute;
		this.message = message;
	}

	public boolean isCanExecute() {
		return canExecute;
	}

	public void setCanExecute(boolean canExecute) {
		this.canExecute = canExecute;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public ServiceDTO getSuggestedService() {
		return suggestedService;
	}

	public void setSuggestedService(ServiceDTO suggestedService) {
		this.suggestedService = suggestedService;
	}

}
