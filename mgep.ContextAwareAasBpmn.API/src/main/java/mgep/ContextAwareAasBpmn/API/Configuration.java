package mgep.ContextAwareAasBpmn.API;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ApplicationPath("api")
public class Configuration extends Application {
	static Logger log = LogManager.getLogger(Configuration.class.getName());
     
	public Configuration() {
		super();
		log.info("ContextAwareAasBpmn component has been initialized");
	}
}
