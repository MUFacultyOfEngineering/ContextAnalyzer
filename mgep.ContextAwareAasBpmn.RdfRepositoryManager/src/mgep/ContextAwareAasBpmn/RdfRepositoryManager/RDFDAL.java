package mgep.ContextAwareAasBpmn.RdfRepositoryManager;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.algebra.Str;

import mgep.ContextAwareAasBpmn.Entities.*;
import mgep.ContextAwareAasBpmn.Core.*;

public class RDFDAL {
	static Logger log = LogManager.getLogger(RDFDAL.class.getName());
	
	/**
	 * Get instance of ServiceDTO from a BindingSet carrying Service data from ontology
	 * @param modelObj The service data
	 * @return
	 */
	private ServiceDTO mapServiceModelObjToDTO(BindingSet modelObj) {
		if(modelObj == null) return null;
		
		var aasId = modelObj.getBinding("aasIdentifier").getValue().stringValue();
		var aasIdShort = modelObj.getBinding("aasIdShort").getValue().stringValue();
		var deviceName = modelObj.getBinding("deviceName").getValue().stringValue();
		var servName = modelObj.getBinding("serviceName").getValue().stringValue();
		var serviceIdentifier = modelObj.getBinding("serviceIdentifier").getValue().stringValue();
		var serviceUrl = modelObj.getBinding("serviceUrl").getValue().stringValue();
		var serviceMethod = modelObj.getBinding("serviceMethod").getValue().stringValue();
		var serviceDescription = modelObj.getBinding("serviceDescription").getValue().stringValue();
		var serviceIsAsync = Boolean.parseBoolean(modelObj.getBinding("serviceIsAsync").getValue().stringValue());
		
		var serviceObj = new ServiceDTO();
		serviceObj.setAasIdShort(aasIdShort);
		serviceObj.setAasIdentifier(aasId);
		serviceObj.setDeviceName(deviceName);
		serviceObj.setServiceIdentifier(serviceIdentifier);
		serviceObj.setServiceUrl(serviceUrl);
		serviceObj.setServiceMethod(serviceMethod);
		serviceObj.setServiceIsAsync(serviceIsAsync);
		serviceObj.setServiceName(servName);
		serviceObj.setServiceDescription(serviceDescription);
		
		return serviceObj;
	}
	
	private AssetAdministrationShellDTO mapAdminShellModelObjToDTO(BindingSet modelObj) {
		if(modelObj == null) return null;
		
		var aasIdentifier = modelObj.getBinding("aasIdentifier").getValue().stringValue();
		var aasIdShort = modelObj.getBinding("aasIdShort").getValue().stringValue();
		var aasName = modelObj.getBinding("aasName").getValue().stringValue();
		
		var shellObj = new AssetAdministrationShellDTO();
		shellObj.setAasIdentifier(aasIdentifier);
		shellObj.setAasIdShort(aasIdShort);
		shellObj.setAasName(aasName);
		
		return shellObj;
	}
	
	
	public ServiceDTO GetServiceByServiceId(String serviceId) {
		log.info("Enter GetServiceByAasId");		
		var repManager = new RDFRepositoryManager(Tools.GRAPHDB_SERVER);
		
		//prepare select
		var query = "PREFIX rdf: <" + Tools.RDF_IRI + ">\r\n"
				+ "PREFIX dsOnt: <" + Tools.DEVICE_SERVICE_ONT_IRI + ">\r\n"
				+ "select * where {\r\n"
				+ "    ?device dsOnt:aasIdentifier ?aasIdentifier .\r\n"
				+ "    ?device dsOnt:aasIdShort ?aasIdShort .\r\n"
				+ "    ?device dsOnt:aasName ?aasName .\r\n"
				+ "    ?device dsOnt:deviceName ?deviceName .\r\n"
				+ "    ?device dsOnt:hasService ?service .\r\n"
				+ "	   ?service dsOnt:serviceIdentifier ?serviceIdentifier .\r\n"
				+ "    ?service dsOnt:serviceName ?serviceName .\r\n"
				+ "    ?service dsOnt:serviceURL ?serviceUrl .\r\n"
				+ "    ?service dsOnt:serviceMethod ?serviceMethod .\r\n"
				+ "    ?service dsOnt:serviceIsAsync ?serviceIsAsync .\r\n"
				+ "    ?service dsOnt:serviceDescription ?serviceDescription .\r\n"
				+ "    filter (?serviceIdentifier = \"" + serviceId + "\") .\r\n"
				+ "} limit 100";
		
		//execute select and map object
		ServiceDTO serviceObj = null;
		try {
			var bindingSet = repManager.makeSPARQLquery(Tools.REPOSITORY_ID, query);
			if(bindingSet.isEmpty()) return null;
			
			serviceObj = mapServiceModelObjToDTO(bindingSet.get(0));
			
			//set input, output and quality parameters
			serviceObj.setServiceInputParameters(GetServiceInputParametersByServiceId(serviceObj.getServiceIdentifier()));
			serviceObj.setServiceOutputParameters(GetServiceOutputParametersByServiceId(serviceObj.getServiceIdentifier()));
			serviceObj.setServiceQualityParameters(GetServiceQualityParametersByServiceId(serviceObj.getServiceIdentifier()));
		} catch (Exception e) {
			log.catching(e);
			System.out.println(e.getMessage());
		}
		
		return serviceObj;
	}
	
	public ServiceDTO GetServiceByName(String aasIdentifier, String serviceName) {
		log.info("Enter GetServiceByAasId");		
		var repManager = new RDFRepositoryManager(Tools.GRAPHDB_SERVER);
		
		//prepare select
		var query = "PREFIX rdf: <" + Tools.RDF_IRI + ">\r\n"
				+ "PREFIX dsOnt: <" + Tools.DEVICE_SERVICE_ONT_IRI + ">\r\n"
				+ "select * where {\r\n"
				+ "    ?device dsOnt:aasIdentifier ?aasIdentifier .\r\n"
				+ "    ?device dsOnt:aasIdShort ?aasIdShort .\r\n"
				+ "    ?device dsOnt:aasName ?aasName .\r\n"
				+ "    ?device dsOnt:deviceName ?deviceName .\r\n"
				+ "    ?device dsOnt:hasService ?service .\r\n"
				+ "	   ?service dsOnt:serviceIdentifier ?serviceIdentifier .\r\n"
				+ "    ?service dsOnt:serviceName ?serviceName .\r\n"
				+ "    ?service dsOnt:serviceURL ?serviceUrl .\r\n"
				+ "    ?service dsOnt:serviceMethod ?serviceMethod .\r\n"
				+ "    ?service dsOnt:serviceIsAsync ?serviceIsAsync .\r\n"
				+ "    ?service dsOnt:serviceDescription ?serviceDescription .\r\n"
				+ "    filter (?aasIdentifier = \"" + aasIdentifier + "\") .\r\n"
				+ "    filter (?serviceName = \"" + serviceName + "\") .\r\n"
				+ "} limit 100";
		
		//execute select and map object
		ServiceDTO serviceObj = null;
		try {
			var bindingSet = repManager.makeSPARQLquery(Tools.REPOSITORY_ID, query);
			if(bindingSet.isEmpty()) return null;
			
			serviceObj = mapServiceModelObjToDTO(bindingSet.get(0));
			
			//set input, output and quality parameters
			serviceObj.setServiceInputParameters(GetServiceInputParametersByServiceId(serviceObj.getServiceIdentifier()));
			serviceObj.setServiceOutputParameters(GetServiceOutputParametersByServiceId(serviceObj.getServiceIdentifier()));
			serviceObj.setServiceQualityParameters(GetServiceQualityParametersByServiceId(serviceObj.getServiceIdentifier()));
		} catch (Exception e) {
			log.catching(e);
			System.out.println(e.getMessage());
		}
		
		return serviceObj;
	}
	
	public AssetAdministrationShellDTO GetShellByServiceId(String serviceId) {
		log.info("Enter GetShellByServiceId");		
		var repManager = new RDFRepositoryManager(Tools.GRAPHDB_SERVER);

		//prepare select
		var query = "PREFIX rdf: <" + Tools.RDF_IRI + ">\r\n"
				+ "PREFIX dsOnt: <" + Tools.DEVICE_SERVICE_ONT_IRI + ">\r\n"
				+ "select * where {\r\n"
				+ "    ?device dsOnt:aasIdentifier ?aasIdentifier .\r\n"
				+ "    ?device dsOnt:aasIdShort ?aasIdShort .\r\n"
				+ "    ?device dsOnt:aasName ?aasName .\r\n"
				+ "    ?device dsOnt:deviceName ?deviceName .\r\n"
				+ "    ?device dsOnt:hasService ?service .\r\n"
				+ "	   ?service dsOnt:serviceIdentifier ?serviceIdentifier .\r\n"
				+ "    ?service dsOnt:serviceName ?serviceName .\r\n"
				+ "    ?service dsOnt:serviceURL ?serviceUrl .\r\n"
				+ "    ?service dsOnt:serviceMethod ?serviceMethod .\r\n"
				+ "    ?service dsOnt:serviceIsAsync ?serviceIsAsync .\r\n"
				+ "    filter (?serviceIdentifier = \"" + serviceId + "\") .\r\n"
				+ "} limit 100";
		
		//execute select and map object
		AssetAdministrationShellDTO aasShellObj = null;
		try {
			var bindingSet = repManager.makeSPARQLquery(Tools.REPOSITORY_ID, query);
			if(bindingSet.isEmpty()) return null;
			
			return mapAdminShellModelObjToDTO(bindingSet.get(0));
		} catch (Exception e) {
			log.catching(e);
			System.out.println(e.getMessage());
		}
		
		return aasShellObj;
	}


	public List<ServiceDTO> GetServicesByAasId(String aasIdentifier) {
		log.info("Enter GetServicesByAasId");		
		var repManager = new RDFRepositoryManager(Tools.GRAPHDB_SERVER);
		
		//prepare select
		var query = "PREFIX rdf: <" + Tools.RDF_IRI + ">\r\n"
				+ "PREFIX dsOnt: <" + Tools.DEVICE_SERVICE_ONT_IRI + ">\r\n"
				+ "select * where {\r\n"
				+ "    ?device dsOnt:aasIdentifier ?aasIdentifier .\r\n"
				+ "    ?device dsOnt:aasIdShort ?aasIdShort .\r\n"
				+ "    ?device dsOnt:aasName ?aasName .\r\n"
				+ "    ?device dsOnt:deviceName ?deviceName .\r\n"
				+ "    ?device dsOnt:hasService ?service .\r\n"
				+ "	   ?service dsOnt:serviceIdentifier ?serviceIdentifier .\r\n"
				+ "    ?service dsOnt:serviceName ?serviceName .\r\n"
				+ "    ?service dsOnt:serviceURL ?serviceUrl .\r\n"
				+ "    ?service dsOnt:serviceMethod ?serviceMethod .\r\n"
				+ "    ?service dsOnt:serviceIsAsync ?serviceIsAsync .\r\n"
				+ "    ?service dsOnt:serviceDescription ?serviceDescription .\r\n"
				+ "    filter (?aasIdentifier = \"" + aasIdentifier + "\") .\r\n"
				+ "} limit 100";
		
		//execute select and map object
		var lServices = new ArrayList<ServiceDTO>();
		try {
			var bindingSet = repManager.makeSPARQLquery(Tools.REPOSITORY_ID, query);			
			for (var item : bindingSet) {
				var serviceObj = mapServiceModelObjToDTO(item);
						
				//set input, output and quality parameters
				serviceObj.setServiceInputParameters(GetServiceInputParametersByServiceId(serviceObj.getServiceIdentifier()));
				serviceObj.setServiceOutputParameters(GetServiceOutputParametersByServiceId(serviceObj.getServiceIdentifier()));
				serviceObj.setServiceQualityParameters(GetServiceQualityParametersByServiceId(serviceObj.getServiceIdentifier()));
				lServices.add(serviceObj);
			}
		} catch (Exception e) {
			log.catching(e);
			System.out.println(e.getMessage());
		}
		
		return lServices;
	}
	
	public List<ServiceDTO> GetServicesByDescription(String description) {
		log.info("Enter GetServicesByDescription");		
		var repManager = new RDFRepositoryManager(Tools.GRAPHDB_SERVER);
		
		//prepare select
		var query = "PREFIX rdf: <" + Tools.RDF_IRI + ">\r\n"
				+ "PREFIX dsOnt: <" + Tools.DEVICE_SERVICE_ONT_IRI + ">\r\n"
				+ "select * where {\r\n"
				+ "    ?device dsOnt:aasIdentifier ?aasIdentifier .\r\n"
				+ "    ?device dsOnt:aasIdShort ?aasIdShort .\r\n"
				+ "    ?device dsOnt:aasName ?aasName .\r\n"
				+ "    ?device dsOnt:deviceName ?deviceName .\r\n"
				+ "    ?device dsOnt:hasService ?service .\r\n"
				+ "	   ?service dsOnt:serviceIdentifier ?serviceIdentifier .\r\n"
				+ "    ?service dsOnt:serviceName ?serviceName .\r\n"
				+ "    ?service dsOnt:serviceURL ?serviceUrl .\r\n"
				+ "    ?service dsOnt:serviceMethod ?serviceMethod .\r\n"
				+ "    ?service dsOnt:serviceIsAsync ?serviceIsAsync .\r\n"
				+ "    ?service dsOnt:serviceDescription ?serviceDescription .\r\n"
				+ "    filter contains(lcase(str(?serviceDescription)), lcase(\"" + description + "\")) .\r\n"
				+ "} limit 100";
		
		//execute select and map object
		var lServices = new ArrayList<ServiceDTO>();
		try {
			var bindingSet = repManager.makeSPARQLquery(Tools.REPOSITORY_ID, query);			
			for (var item : bindingSet) {
				var serviceObj = mapServiceModelObjToDTO(item);
				
				//set input, output and quality parameters
				serviceObj.setServiceInputParameters(GetServiceInputParametersByServiceId(serviceObj.getServiceIdentifier()));
				serviceObj.setServiceOutputParameters(GetServiceOutputParametersByServiceId(serviceObj.getServiceIdentifier()));
				serviceObj.setServiceQualityParameters(GetServiceQualityParametersByServiceId(serviceObj.getServiceIdentifier()));
				lServices.add(serviceObj);
			}
		} catch (Exception e) {
			log.catching(e);
			System.out.println(e.getMessage());
		}
		
		return lServices;
	}
	
	public List<ServiceDTO> GetServicesByParamName(String inputParamName, String outputParamName) {
		log.info("Enter GetServicesByParamName");		
		var repManager = new RDFRepositoryManager(Tools.GRAPHDB_SERVER);
		
		//prepare select
		var query = "PREFIX rdf: <" + Tools.RDF_IRI + ">\r\n"
				+ "PREFIX dsOnt: <" + Tools.DEVICE_SERVICE_ONT_IRI + ">\r\n"
				+ "select * where {\r\n"
				+ "    ?device dsOnt:aasIdentifier ?aasIdentifier .\r\n"
				+ "    ?device dsOnt:aasIdShort ?aasIdShort .\r\n"
				+ "    ?device dsOnt:aasName ?aasName .\r\n"
				+ "    ?device dsOnt:deviceName ?deviceName .\r\n"
				+ "    ?device dsOnt:hasService ?service .\r\n"
				+ "	   ?service dsOnt:serviceIdentifier ?serviceIdentifier .\r\n"
				+ "    ?service dsOnt:serviceName ?serviceName .\r\n"
				+ "    ?service dsOnt:serviceURL ?serviceUrl .\r\n"
				+ "    ?service dsOnt:serviceMethod ?serviceMethod .\r\n"
				+ "    ?service dsOnt:serviceIsAsync ?serviceIsAsync .\r\n"
				+ "    ?service dsOnt:serviceDescription ?serviceDescription .\r\n"
				+ "    ?service dsOnt:hasInput ?input .\r\n"
				+ "    ?input dsOnt:parameterName ?serviceInputParameterName .\r\n"
				+ "    ?input dsOnt:parameterType ?serviceInputParameterType .\r\n"
				+ "    ?input dsOnt:parameterValue ?serviceInputParameterValue .\r\n"
				+ "    ?service dsOnt:hasOutput ?output .\r\n"
				+ "    ?output dsOnt:parameterName ?serviceOutputParameterName .\r\n"
				+ "    ?output dsOnt:parameterType ?serviceOutputParameterType .\r\n"
				+ "    ?output dsOnt:parameterValue ?serviceOutputParameterValue .\r\n"
				+ "    filter contains(lcase(str(?serviceInputParameterName)), lcase(\"" + inputParamName + "\")) .\r\n"
				+ "    filter contains(lcase(str(?serviceOutputParameterName)), lcase(\"" + outputParamName + "\")) .\r\n"
				+ "} limit 100";
		
		//execute select and map object
		var lServices = new ArrayList<ServiceDTO>();
		try {
			var bindingSet = repManager.makeSPARQLquery(Tools.REPOSITORY_ID, query);			
			for (var item : bindingSet) {
				var serviceObj = mapServiceModelObjToDTO(item);
				
				//set input, output and quality parameters
				serviceObj.setServiceInputParameters(GetServiceInputParametersByServiceId(serviceObj.getServiceIdentifier()));
				serviceObj.setServiceOutputParameters(GetServiceOutputParametersByServiceId(serviceObj.getServiceIdentifier()));
				serviceObj.setServiceQualityParameters(GetServiceQualityParametersByServiceId(serviceObj.getServiceIdentifier()));
				
				lServices.add(serviceObj);
			}
		} catch (Exception e) {
			log.catching(e);
			System.out.println(e.getMessage());
		}
		
		return lServices;
	}
	
	public List<ParameterDTO> GetServiceInputParametersByServiceId(String serviceId) {
		log.info("Enter GetServiceInputParametersByServiceId");		
		var repManager = new RDFRepositoryManager(Tools.GRAPHDB_SERVER);
		
		//prepare select
		var query = "PREFIX rdf: <" + Tools.RDF_IRI + ">\r\n"
				+ "PREFIX dsOnt: <" + Tools.DEVICE_SERVICE_ONT_IRI + ">\r\n"
				+ "select * where {\r\n"
				+ "	   ?service dsOnt:serviceIdentifier ?serviceIdentifier .\r\n"
				+ "    ?service dsOnt:hasInput ?input .\r\n"
				+ "    ?input dsOnt:parameterName ?serviceInputParameterName .\r\n"
				+ "    ?input dsOnt:parameterType ?serviceInputParameterType .\r\n"
				+ "    ?input dsOnt:parameterValue ?serviceInputParameterValue .\r\n"
				+ "    filter (?serviceIdentifier = \"" + serviceId + "\") .\r\n"
				+ "} limit 100";
		
		//execute select and map object
		var lInputParams = new ArrayList<ParameterDTO>();
		try {
			var bindingSet = repManager.makeSPARQLquery(Tools.REPOSITORY_ID, query);			
			for (var item : bindingSet) {
				var serviceInputParameterName = item.getBinding("serviceInputParameterName").getValue().stringValue();
				var serviceInputParameterType = item.getBinding("serviceInputParameterType").getValue().stringValue();
				var serviceInputParameterValue = item.getBinding("serviceInputParameterValue").getValue().stringValue();
				
				var inputParamObj = new ParameterDTO();
				inputParamObj.setParameterName(serviceInputParameterName);
				inputParamObj.setParameterType(serviceInputParameterType);
				inputParamObj.setParameterValue(serviceInputParameterValue);
				
				lInputParams.add(inputParamObj);
			}
		} catch (Exception e) {
			log.catching(e);
			System.out.println(e.getMessage());
		}
		
		return lInputParams;
	}
	
	public List<ParameterDTO> GetServiceOutputParametersByServiceId(String serviceId) {
		log.info("Enter GetServiceInputParametersByServiceId");		
		var repManager = new RDFRepositoryManager(Tools.GRAPHDB_SERVER);
		
		//prepare select
		var query = "PREFIX rdf: <" + Tools.RDF_IRI + ">\r\n"
				+ "PREFIX dsOnt: <" + Tools.DEVICE_SERVICE_ONT_IRI + ">\r\n"
				+ "select * where {\r\n"
				+ "	   ?service dsOnt:serviceIdentifier ?serviceIdentifier .\r\n"
				+ "    ?service dsOnt:hasOutput ?output .\r\n"
				+ "    ?output dsOnt:parameterName ?serviceOutputParameterName .\r\n"
				+ "    ?output dsOnt:parameterType ?serviceOutputParameterType .\r\n"
				+ "    ?output dsOnt:parameterValue ?serviceOutputParameterValue .\r\n"
				+ "    filter (?serviceIdentifier = \"" + serviceId + "\") .\r\n"
				+ "} limit 100";
		
		//execute select and map object
		var lOutputParams = new ArrayList<ParameterDTO>();
		try {
			var bindingSet = repManager.makeSPARQLquery(Tools.REPOSITORY_ID, query);			
			for (var item : bindingSet) {
				var serviceOutputParameterName = item.getBinding("serviceOutputParameterName").getValue().stringValue();
				var serviceOutputParameterType = item.getBinding("serviceOutputParameterType").getValue().stringValue();
				var serviceOutputParameterValue = item.getBinding("serviceOutputParameterValue").getValue().stringValue();
				
				var outputParamObj = new ParameterDTO();
				outputParamObj.setParameterName(serviceOutputParameterName);
				outputParamObj.setParameterType(serviceOutputParameterType);
				outputParamObj.setParameterValue(serviceOutputParameterValue);
				
				lOutputParams.add(outputParamObj);
			}
		} catch (Exception e) {
			log.catching(e);
			System.out.println(e.getMessage());
		}
		
		return lOutputParams;
	}
	
	public List<QualityParameterDTO> GetServiceQualityParametersByServiceId(String serviceId) {
		log.info("Enter GetServiceInputParametersByServiceId");		
		var repManager = new RDFRepositoryManager(Tools.GRAPHDB_SERVER);
		
		//prepare select
		var query = "PREFIX rdf: <" + Tools.RDF_IRI + ">\r\n"
				+ "PREFIX dsOnt: <" + Tools.DEVICE_SERVICE_ONT_IRI + ">\r\n"
				+ "select * where {\r\n"
				+ "	   ?service dsOnt:serviceIdentifier ?serviceIdentifier .\r\n"
				+ "    ?service dsOnt:hasQuality ?quality .\r\n"
				+ "    ?quality dsOnt:parameterName ?qualityParameterName .\r\n"
				+ "    ?quality dsOnt:parameterType ?qualityParameterType .\r\n"
				+ "    ?quality dsOnt:parameterValue ?qualityParameterValue .\r\n"
				+ "    ?quality dsOnt:qualityParameterCorrespondsTo ?qualityParameterCorrespondsTo .\r\n"
				+ "    ?quality dsOnt:qualityParameterEvaluationExpression ?qualityParameterEvaluationExpression .\r\n"
				+ "    filter (?serviceIdentifier = \"" + serviceId + "\") .\r\n"
				+ "} limit 100";
		
		//execute select and map object
		var lQualityParams = new ArrayList<QualityParameterDTO>();
		try {
			var bindingSet = repManager.makeSPARQLquery(Tools.REPOSITORY_ID, query);			
			for (var item : bindingSet) {
				var qosObj = new QualityParameterDTO();
				qosObj.setParameterName(item.getBinding("qualityParameterName").getValue().stringValue());
				qosObj.setParameterValue(item.getBinding("qualityParameterValue").getValue().stringValue());
				qosObj.setParameterType(item.getBinding("qualityParameterType").getValue().stringValue());
				qosObj.setQualityParameterCorrespondsTo(item.getBinding("qualityParameterCorrespondsTo").getValue().stringValue());
				qosObj.setQualityParameterEvaluationExpression(item.getBinding("qualityParameterEvaluationExpression").getValue().stringValue());
				lQualityParams.add(qosObj);
			}
		} catch (Exception e) {
			log.catching(e);
			System.out.println(e.getMessage());
		}
		
		return lQualityParams;
	}
	
	public List<AssetAdministrationShellDTO> GetShells(String aasIdentifier) {
		log.info("Enter GetShells");		
		var repManager = new RDFRepositoryManager(Tools.GRAPHDB_SERVER);
		
		//prepare select
		var query = "PREFIX rdf: <" + Tools.RDF_IRI + ">\r\n"
				+ "PREFIX dsOnt: <" + Tools.DEVICE_SERVICE_ONT_IRI + ">\r\n"
				+ "select * where {\r\n"
				+ "    ?device dsOnt:aasIdentifier ?aasIdentifier .\r\n"
				+ "    ?device dsOnt:aasIdShort ?aasIdShort .\r\n"
				+ "    ?device dsOnt:aasName ?aasName .\r\n"
				+ "    ?device dsOnt:deviceName ?deviceName .\r\n"
				+ "filter contains(lcase(str(?aasIdentifier)),\"" + aasIdentifier.toLowerCase() + "\") .\r\n"
				+ "} limit 100";
		
		//execute select and map object
		var lShells = new ArrayList<AssetAdministrationShellDTO>();
		try {
			var bindingSet = repManager.makeSPARQLquery(Tools.REPOSITORY_ID, query);			
			for (var item : bindingSet) {
				lShells.add(mapAdminShellModelObjToDTO(item));
			}
		} catch (Exception e) {
			log.catching(e);
			System.out.println(e.getMessage());
		}
		
		return lShells;
	}
	
	public ResponseContextValServiceSelectionDTO ValidateContextSelectBestService(RequestContextValServiceSelectionDTO requestObj) {
		log.info("Enter ValidateContextSelectBestService");		
		var repManager = new RDFRepositoryManager(Tools.GRAPHDB_SERVER);

		//prepare select requested service
		ServiceDTO requestedServiceObj = null;
		var queryRequestedService = "PREFIX rdf: <" + Tools.RDF_IRI + ">\r\n"
				+ "PREFIX dsOnt: <" + Tools.DEVICE_SERVICE_ONT_IRI + ">\r\n"
				+ "select * where {\r\n"
				+ "    ?device dsOnt:aasIdentifier ?aasIdentifier .\r\n"
				+ "    ?device dsOnt:aasIdShort ?aasIdShort .\r\n"
				+ "    ?device dsOnt:aasName ?aasName .\r\n"
				+ "    ?device dsOnt:deviceName ?deviceName .\r\n"
				+ "    ?device dsOnt:hasService ?service .\r\n"
				+ "	   ?service dsOnt:serviceIdentifier ?serviceIdentifier .\r\n"
				+ "    ?service dsOnt:serviceName ?serviceName .\r\n"
				+ "    ?service dsOnt:serviceURL ?serviceUrl .\r\n"
				+ "    ?service dsOnt:serviceMethod ?serviceMethod .\r\n"
				+ "    ?service dsOnt:serviceIsAsync ?serviceIsAsync .\r\n"
				+ "    ?service dsOnt:serviceDescription ?serviceDescription .\r\n"
				+ "    filter (?aasIdShort = \"" + requestObj.getAasIdShort() + "\") .\r\n"
				+ "    filter (?serviceName = \"" + requestObj.getServiceName() + "\") .\r\n"
				+ "} limit 100";
		
		//execute select and map object
		try {
			var bindingSet = repManager.makeSPARQLquery(Tools.REPOSITORY_ID, queryRequestedService);
			if(bindingSet.isEmpty()) {
				var contextValidationResult = new ResponseContextValServiceSelectionDTO(false, "The requested service does not match to our records. Please check you have provided the right information.");
				return contextValidationResult;
			}
			
			requestedServiceObj = mapServiceModelObjToDTO(bindingSet.get(0));
			
			//set quality parameters
			requestedServiceObj.setServiceQualityParameters(GetServiceQualityParametersByServiceId(requestedServiceObj.getServiceIdentifier()));	
		} catch (Exception e) {
			log.catching(e);
			System.out.println(e.getMessage());
			return null;
		}
		
		//prepare quality validation
		var queryQualitySelector = "";
		var queryQualityFilter = "";
		var queryQualityOrderBy = "";
		if(requestObj.getQualityConditions() != null && requestObj.getQualityConditions().size() > 0) {			
			for (var qItem : requestObj.getQualityConditions()) {
				//the eval expression sent in the request, should have 3 parts. 1) param name, 2) condition, 3) value 
				var arInputEvalExpression = qItem.getQualityParameterEvaluationExpression().trim().split(" ");
				var paramName = arInputEvalExpression[0];
				var condition = arInputEvalExpression[1];
				var value = arInputEvalExpression[2];
				
				//format semantic variable names for SPARQL
				var hasQualitySelectorVarName = "quality" + paramName;
				var qualityParameterValueVarName = "qualityParameterValue" + paramName;
				var qualityParameterDataType = requestedServiceObj.getServiceQualityParameters().stream().filter(x-> x.getParameterName().equals(paramName)).findAny().get().getParameterType().toLowerCase();
				
				//build filter section of SPARQL
				var evalExpression = String.format("xsd:%s(?%s) %s %s", qualityParameterDataType, qualityParameterValueVarName, condition, value);
				queryQualitySelector += "    ?service dsOnt:hasQuality ?" + hasQualitySelectorVarName + " .\r\n"
						+ "    ?" + hasQualitySelectorVarName + " dsOnt:parameterName \"" + paramName + "\" .\r\n"
						+ "    ?" + hasQualitySelectorVarName + " dsOnt:parameterValue ?" + qualityParameterValueVarName + " .\r\n";
				queryQualityFilter += "    filter (" + evalExpression + ") .\r\n";

				//prepare "order by" based on comparison symbol
				switch (condition) {
					case "<": case "<=":
						//<: means the lower the value, the better. "ORDER BY ASC" puts the lowest value on top of the resultset						
						if(queryQualityOrderBy.length() == 0) {
							queryQualityOrderBy = String.format("ORDER BY ASC(xsd:%s(?%s))", qualityParameterDataType, qualityParameterValueVarName);							
						}else if (queryQualityOrderBy.contains("ORDER BY")) {
							//append new parameter
							queryQualityOrderBy += String.format(" ASC(xsd:%s(?%s))", qualityParameterDataType, qualityParameterValueVarName);
						}
						break;
					case ">": case ">=":
						//>: means the higher the value, the better. "ORDER BY DESC" puts the greatest value on top of the resultset					
						if(queryQualityOrderBy.length() == 0) {
							queryQualityOrderBy = String.format("ORDER BY DESC(xsd:%s(?%s))", qualityParameterDataType, qualityParameterValueVarName);							
						}else if (queryQualityOrderBy.contains("ORDER BY")) {
							//append new parameter
							queryQualityOrderBy += String.format(" DESC(xsd:%s(?%s))", qualityParameterDataType, qualityParameterValueVarName);
						}
						break;
					default:
						//other dont need order by
						break;
				}
			}
		}
		
		//prepare select for suggested service according to evaluation of QoS
		ServiceDTO suggestedServiceObj = null;
		var queryWithFilterSuggestedService = "PREFIX rdf: <" + Tools.RDF_IRI + ">\r\n"
				+ "PREFIX dsOnt: <" + Tools.DEVICE_SERVICE_ONT_IRI + ">\r\n"
				+ "select * where {\r\n"
				+ "    ?device dsOnt:aasIdentifier ?aasIdentifier .\r\n"
				+ "    ?device dsOnt:aasIdShort ?aasIdShort .\r\n"
				+ "    ?device dsOnt:aasName ?aasName .\r\n"
				+ "    ?device dsOnt:deviceName ?deviceName .\r\n"
				+ "    ?device dsOnt:hasService ?service .\r\n"
				+ "	   ?service dsOnt:serviceIdentifier ?serviceIdentifier .\r\n"
				+ "    ?service dsOnt:serviceName ?serviceName .\r\n"
				+ "    ?service dsOnt:serviceURL ?serviceUrl .\r\n"
				+ "    ?service dsOnt:serviceMethod ?serviceMethod .\r\n"
				+ "    ?service dsOnt:serviceIsAsync ?serviceIsAsync .\r\n"
				+ "    ?service dsOnt:serviceDescription ?serviceDescription .\r\n"
				+ queryQualitySelector
				+ "    filter (?serviceName = \"" + requestedServiceObj.getServiceName() + "\") .\r\n"
				+ queryQualityFilter
				+ "} "
				+ queryQualityOrderBy
				+ "    limit 10";
		
		//execute select and map object
		try {
			var lBindingSet = repManager.makeSPARQLquery(Tools.REPOSITORY_ID, queryWithFilterSuggestedService);
			if(!lBindingSet.isEmpty()) {
				for (var item : lBindingSet) {
					var serviceIdentifier = item.getBinding("serviceIdentifier").getValue().stringValue();					
					//if is the same as the one requested, do nothing and seek for the next one
					if(serviceIdentifier.equals(requestedServiceObj.getServiceIdentifier())) continue;
					//set suggestedService and  break loop
					suggestedServiceObj = mapServiceModelObjToDTO(item);
					break;
				}
			}			
		} catch (Exception e) {
			log.catching(e);
			System.out.println(e.getMessage());
			return null;
		}
		
		//if suggestedService is still empty, means none of the services meet the Quality conditions. In that case, remove the filter and try again
		if (suggestedServiceObj == null) {
			var queryWithoutFilterSuggestedService = "PREFIX rdf: <" + Tools.RDF_IRI + ">\r\n"
					+ "PREFIX dsOnt: <" + Tools.DEVICE_SERVICE_ONT_IRI + ">\r\n"
					+ "select * where {\r\n"
					+ "    ?device dsOnt:aasIdentifier ?aasIdentifier .\r\n"
					+ "    ?device dsOnt:aasIdShort ?aasIdShort .\r\n"
					+ "    ?device dsOnt:aasName ?aasName .\r\n"
					+ "    ?device dsOnt:deviceName ?deviceName .\r\n"
					+ "    ?device dsOnt:hasService ?service .\r\n"
					+ "	   ?service dsOnt:serviceIdentifier ?serviceIdentifier .\r\n"
					+ "    ?service dsOnt:serviceName ?serviceName .\r\n"
					+ "    ?service dsOnt:serviceURL ?serviceUrl .\r\n"
					+ "    ?service dsOnt:serviceMethod ?serviceMethod .\r\n"
					+ "    ?service dsOnt:serviceIsAsync ?serviceIsAsync .\r\n"
					+ "    ?service dsOnt:serviceDescription ?serviceDescription .\r\n"
					+ queryQualitySelector
					+ "    filter (?serviceName = \"" + requestedServiceObj.getServiceName() + "\") .\r\n"
					+ "} "
					+ queryQualityOrderBy
					+ "    limit 10";
			
			//execute select and map object
			try {
				var lBindingSet = repManager.makeSPARQLquery(Tools.REPOSITORY_ID, queryWithoutFilterSuggestedService);
				if(!lBindingSet.isEmpty()) {
					for (var item : lBindingSet) {
						var serviceIdentifier = item.getBinding("serviceIdentifier").getValue().stringValue();					
						//if is the same as the one requested, do nothing and seek for the next one
						if(serviceIdentifier.equals(requestedServiceObj.getServiceIdentifier())) continue;
						//set suggestedService and  break loop
						suggestedServiceObj = mapServiceModelObjToDTO(item);
						break;
					}
				}			
			} catch (Exception e) {
				log.catching(e);
				System.out.println(e.getMessage());
				return null;
			}
		}
		
		//prepare response
		var contextValidationResult = new ResponseContextValServiceSelectionDTO(true, "OK");
		
		//if suggestedService is found: set input, output and quality parameters
		if(suggestedServiceObj != null) {
			suggestedServiceObj.setServiceInputParameters(GetServiceInputParametersByServiceId(suggestedServiceObj.getServiceIdentifier()));
			suggestedServiceObj.setServiceOutputParameters(GetServiceOutputParametersByServiceId(suggestedServiceObj.getServiceIdentifier()));
			suggestedServiceObj.setServiceQualityParameters(GetServiceQualityParametersByServiceId(suggestedServiceObj.getServiceIdentifier()));
			
			contextValidationResult.setSuggestedService(suggestedServiceObj);
			contextValidationResult.setMessage("A better service is recommended after evaluating QoS parameters");
			contextValidationResult.setCanExecute(false);
		}
		
		return contextValidationResult;
	}
	
	public Boolean UpdateQualityParamtersOfAService(String serviceIdentifier, List<QualityParameterDTO> newQualityParameters) {
		log.info("Enter UpdateQualityParamtersOfAService");		
		var repManager = new RDFRepositoryManager(Tools.GRAPHDB_SERVER);
		
		String deleteQualityQuery = "PREFIX rdf: <" + Tools.RDF_IRI + ">"
				+ "PREFIX dsOnt: <" + Tools.DEVICE_SERVICE_ONT_IRI + ">"
				+ "delete {\r\n"
				+ "	?quality dsOnt:parameterName ?qualityParameterName .\r\n"
				+ "    ?quality dsOnt:parameterType ?qualityParameterType .\r\n"
				+ "    ?quality dsOnt:parameterValue ?qualityParameterValue .\r\n"
				+ "    ?quality dsOnt:qualityParameterCorrespondsTo ?qualityParameterCorrespondsTo .\r\n"
				+ "    ?quality dsOnt:qualityParameterEvaluationExpression ?qualityParameterEvaluationExpression .\r\n"
				+ "} where {\r\n"
				+ "	?service dsOnt:serviceIdentifier ?serviceIdentifier .\r\n"
				+ "    ?service dsOnt:hasQuality ?quality .\r\n"
				+ "    ?quality dsOnt:parameterName ?qualityParameterName .\r\n"
				+ "    ?quality dsOnt:parameterType ?qualityParameterType .\r\n"
				+ "    ?quality dsOnt:parameterValue ?qualityParameterValue .\r\n"
				+ "    ?quality dsOnt:qualityParameterCorrespondsTo ?qualityParameterCorrespondsTo .\r\n"
				+ "    ?quality dsOnt:qualityParameterEvaluationExpression ?qualityParameterEvaluationExpression .\r\n"
				+ "    filter (?serviceIdentifier = \"" + serviceIdentifier + "\") .\r\n"
				+ "}";
		
		//execute delete
		if (!repManager.executeQuery(Tools.REPOSITORY_ID, deleteQualityQuery)) return false;
		
		//prepare data for insert
		ServiceDTO service = GetServiceByServiceId(serviceIdentifier);
		String fullServiceName = String.format("%s_%s", service.getDeviceName(), service.getServiceName());
		service.setServiceQualityParameters(newQualityParameters);
		
		//execute insert
		String insertQueryQualityParams = "PREFIX rdf: <" + Tools.RDF_IRI + ">"
				+ "PREFIX dsOnt: <" + Tools.DEVICE_SERVICE_ONT_IRI+ ">"
				+ "INSERT DATA {"
				+  prepareInsertQualityParamQuery(service, fullServiceName)
				+ "};";
		return repManager.executeQuery(Tools.REPOSITORY_ID, insertQueryQualityParams);
	}
	
	public Boolean UpdateQualityParamtersOfAllServicesOfaShell(List<ServiceDTO> lServicesWithNewQualityParameters) {
		log.info("Enter UpdateQualityParamtersOfAllServicesOfaShell");		
		var repManager = new RDFRepositoryManager(Tools.GRAPHDB_SERVER);
		
		//prepare delete
		var deleteQualityQuery = "PREFIX rdf: <" + Tools.RDF_IRI + ">\r\n"
				+ "PREFIX dsOnt: <" + Tools.DEVICE_SERVICE_ONT_IRI + ">\r\n";
		
		for (ServiceDTO serviceObj : lServicesWithNewQualityParameters) {			
			deleteQualityQuery += "delete {\r\n"
					+ "	?quality dsOnt:parameterName ?qualityParameterName .\r\n"
					+ "    ?quality dsOnt:parameterType ?qualityParameterType .\r\n"
					+ "    ?quality dsOnt:parameterValue ?qualityParameterValue .\r\n"
					+ "    ?quality dsOnt:qualityParameterCorrespondsTo ?qualityParameterCorrespondsTo .\r\n"
					+ "    ?quality dsOnt:qualityParameterEvaluationExpression ?qualityParameterEvaluationExpression .\r\n"
					+ "} where {\r\n"
					+ "	?service dsOnt:serviceIdentifier ?serviceIdentifier .\r\n"
					+ "    ?service dsOnt:hasQuality ?quality .\r\n"
					+ "    ?quality dsOnt:parameterName ?qualityParameterName .\r\n"
					+ "    ?quality dsOnt:parameterType ?qualityParameterType .\r\n"
					+ "    ?quality dsOnt:parameterValue ?qualityParameterValue .\r\n"
					+ "    ?quality dsOnt:qualityParameterCorrespondsTo ?qualityParameterCorrespondsTo .\r\n"
					+ "    ?quality dsOnt:qualityParameterEvaluationExpression ?qualityParameterEvaluationExpression .\r\n"
					+ "    filter (?serviceIdentifier = \"" + serviceObj.getServiceIdentifier() + "\") .\r\n"
					+ "};\r\n";
			
		}
		
		//execute delete
		//if (!repManager.executeQuery(Tools.REPOSITORY_ID, deleteQualityQuery)) return false;
		
		//prepare data for insert
		var insertQueryQualityParams = deleteQualityQuery; //"PREFIX rdf: <" + Tools.RDF_IRI + ">\r\n"
				//+ "PREFIX dsOnt: <" + Tools.DEVICE_SERVICE_ONT_IRI + ">\r\n";
		
		for (ServiceDTO serviceObj : lServicesWithNewQualityParameters) {
			String fullServiceName = String.format("%s_%s", serviceObj.getDeviceName(), serviceObj.getServiceName());
			var insertService = "INSERT DATA {"
					+  prepareInsertQualityParamQuery(serviceObj, fullServiceName)
					+ "};\r\n";
			insertQueryQualityParams += insertService;
		}
		
		//execute insert
		return repManager.executeQuery(Tools.REPOSITORY_ID, insertQueryQualityParams);
	}
	
	public String prepareInsertDeviceQuery(DeviceDTO deviceObj) {
		String insertQueryDevice = "PREFIX rdf: <" + Tools.RDF_IRI + ">"
				+ "PREFIX dsOnt: <" + Tools.DEVICE_SERVICE_ONT_IRI + ">"
				+ "INSERT DATA {"
				+ "    dsOnt:" + deviceObj.getDeviceName() + " rdf:type dsOnt:Device ."
				+ "    dsOnt:" + deviceObj.getDeviceName() + " dsOnt:aasIdentifier \"" + deviceObj.getAasIdentifier() + "\" ."
				+ "    dsOnt:" + deviceObj.getDeviceName() + " dsOnt:aasIdShort \"" + deviceObj.getAasIdShort() + "\" ."
				+ "    dsOnt:" + deviceObj.getDeviceName() + " dsOnt:aasName \"" + deviceObj.getAasName() + "\" ."
				+ (deviceObj.getDeviceApiDocumentation() == null ? "" : "    dsOnt:" + deviceObj.getDeviceName() + " dsOnt:deviceApiDocumentation \"" + deviceObj.getDeviceApiDocumentation() + "\" .")
				+ (deviceObj.getDeviceDescription() == null ? "" : "    dsOnt:" + deviceObj.getDeviceName() + " dsOnt:deviceDescription \"" + deviceObj.getDeviceDescription() + "\" .")
				+ "    dsOnt:" + deviceObj.getDeviceName() + " dsOnt:deviceIdentifier \"" + deviceObj.getDeviceIdentifier() + "\" ."
				+ "    dsOnt:" + deviceObj.getDeviceName() + " dsOnt:deviceIPAddress \"" + deviceObj.getDeviceIPAddress() + "\" ."
				+ "    dsOnt:" + deviceObj.getDeviceName() + " dsOnt:deviceIsOnline \"" + deviceObj.getDeviceIsOnline() + "\" ."
				+ "    dsOnt:" + deviceObj.getDeviceName() + " dsOnt:deviceName \"" + deviceObj.getDeviceName() + "\" ."
				+ (deviceObj.getDeviceNetworkLatency() == null ? "" : "    dsOnt:" + deviceObj.getDeviceName() + " dsOnt:deviceNetworkLatency \"" + deviceObj.getDeviceNetworkLatency() + "\" .")
				+ "};";
		
		String insertSensorQuery = "";		
		if(deviceObj.getSensors() != null && deviceObj.getSensors().size() > 0) {
			for (SensorDTO item : deviceObj.getSensors()) {
				insertSensorQuery += prepareInsertSensorQuery(deviceObj.getDeviceName(), item);
			}			
		}
		
		String insertQueryServices = "";
		for (ServiceDTO item : deviceObj.getServices()) {
			insertQueryServices += prepareInsertServiceQuery(deviceObj.getDeviceName(), item); 
		}
		
		return insertQueryDevice + insertSensorQuery + insertQueryServices;
	}
	
	private String prepareInsertSensorQuery(String deviceName, SensorDTO sensorObj) {		
		String fullSensorName = String.format("%s_%s", deviceName, sensorObj.getSensorName());
		
		String insertQuery = "PREFIX rdf: <" + Tools.RDF_IRI + ">"
				+ "PREFIX dsOnt: <" + Tools.DEVICE_SERVICE_ONT_IRI+ ">"
				+ "INSERT DATA {"
				+ "    dsOnt:" + fullSensorName + " rdf:type dsOnt:Sensor ."
				+ "    dsOnt:" + fullSensorName + " dsOnt:sensorDescription \"" + sensorObj.getSensorDescription() + "\" ."
				+ "    dsOnt:" + fullSensorName + " dsOnt:sensorIdentifier \"" + sensorObj.getSensorIdentifier() + "\" ."
				+ "    dsOnt:" + fullSensorName + " dsOnt:sensorName \"" + sensorObj.getSensorName() + "\" ."
				+ "    dsOnt:" + fullSensorName + " dsOnt:sensorType dsOnt:" + sensorObj.getSensorType().name() + " ."
				+ "    dsOnt:" + fullSensorName + " dsOnt:sensorValueDataType \"" + sensorObj.getSensorValueDataType() + "\" ."
				+ "    dsOnt:" + fullSensorName + " dsOnt:sensorValueDataValue \"" + sensorObj.getSensorValueDataValue() + "\" ."
				+ "    dsOnt:" + fullSensorName + " dsOnt:sensorValueUnit \"" + sensorObj.getSensorValueDataUnit() + "\" ."
				+ "    dsOnt:" + deviceName + " dsOnt:hasSensor dsOnt:" + fullSensorName
				+ "};";
		return insertQuery;
	}
	
	private String prepareInsertServiceQuery(String deviceName, ServiceDTO service) {		
		String fullServiceName = String.format("%s_%s", deviceName, service.getServiceName());				
		String insertQueryInputParams = prepareInsertInputParamQuery(service, fullServiceName);
		String insertQueryOutputParams = prepareInsertOutputParamQuery(service, fullServiceName);		
		String insertQueryQualityParams = prepareInsertQualityParamQuery(service, fullServiceName);
		
		String insertServiceQuery="PREFIX rdf: <" + Tools.RDF_IRI + ">"
				+ "PREFIX dsOnt: <" + Tools.DEVICE_SERVICE_ONT_IRI+ ">"
				+ "INSERT DATA {"
				+ "    dsOnt:" + fullServiceName + " rdf:type dsOnt:Service ."
				+ "    dsOnt:" + fullServiceName + " dsOnt:serviceDescription \"" + service.getServiceDescription() + "\" ."
				+ "    dsOnt:" + fullServiceName + " dsOnt:serviceIdentifier \"" + service.getServiceIdentifier() + "\" ."
				+ "    dsOnt:" + fullServiceName + " dsOnt:serviceIsAsync \"" + service.isServiceIsAsync() + "\" ."
				+ "    dsOnt:" + fullServiceName + " dsOnt:serviceMethod \"" + service.getServiceMethod() + "\" ."
				+ "    dsOnt:" + fullServiceName + " dsOnt:serviceName \"" + service.getServiceName() + "\" ."
				+ "    dsOnt:" + fullServiceName + " dsOnt:serviceURL \"" + service.getServiceUrl() + "\" ."
				+ insertQueryInputParams
				+ insertQueryOutputParams
				+ insertQueryQualityParams
				+ "    dsOnt:" + deviceName + " dsOnt:hasService dsOnt:" + fullServiceName
				+ "};";
		return insertServiceQuery;
	}
	
	private String prepareInsertInputParamQuery(ServiceDTO service, String fullServiceName) {
		String insertQueryInputParams = "";
		
		if (service.getServiceInputParameters() != null && service.getServiceInputParameters().size() > 0) {
			for (ParameterDTO item : service.getServiceInputParameters()) {
				String paramName =  String.format("%s_Input_%s", fullServiceName, item.getParameterName());
				insertQueryInputParams += "    dsOnt:" + paramName + " rdf:type dsOnt:Input ."
						+ "    dsOnt:" + paramName + " dsOnt:parameterName \"" + item.getParameterName() + "\" ."
						+ "    dsOnt:" + paramName + " dsOnt:parameterType \"" + item.getParameterType() + "\" ."
						+ "    dsOnt:" + paramName + " dsOnt:parameterValue \"" + item.getParameterValue() + "\" ."
						+ "    dsOnt:" + fullServiceName + " dsOnt:hasInput dsOnt:" + paramName
						+ " .";
			}			
		}
		
		return insertQueryInputParams;
	}
	
	private String prepareInsertOutputParamQuery(ServiceDTO service, String fullServiceName) {
		String insertQueryOutputParams = "";
		
		if (service.getServiceOutputParameters() != null && service.getServiceOutputParameters().size() > 0) {
			for (ParameterDTO item : service.getServiceOutputParameters()) {
				String paramName =  String.format("%s_Output_%s", fullServiceName, item.getParameterName());
				insertQueryOutputParams += "    dsOnt:" + paramName + " rdf:type dsOnt:Output ."
						+ "    dsOnt:" + paramName + " dsOnt:parameterName \"" + item.getParameterName() + "\" ."
						+ "    dsOnt:" + paramName + " dsOnt:parameterType \"" + item.getParameterType() + "\" ."
						+ "    dsOnt:" + paramName + " dsOnt:parameterValue \"" + item.getParameterValue() + "\" ."
						+ "    dsOnt:" + fullServiceName + " dsOnt:hasOutput dsOnt:" + paramName
						+ " .";
			}
		}
		
		return insertQueryOutputParams;
	}
	
	private String prepareInsertQualityParamQuery(ServiceDTO service, String fullServiceName) {
		String insertQueryQualityParams = "";
		
		if (service.getServiceQualityParameters() != null && service.getServiceQualityParameters().size() > 0) {
			for (QualityParameterDTO item : service.getServiceQualityParameters()) {
				String paramName =  String.format("%s_Quality_%s", fullServiceName, item.getParameterName());
				insertQueryQualityParams += "    dsOnt:" + paramName + " rdf:type dsOnt:Quality ."
						+ "    dsOnt:" + paramName + " dsOnt:parameterName \"" + item.getParameterName() + "\" .\r\n"
						+ "    dsOnt:" + paramName + " dsOnt:parameterType \"" + item.getParameterType() + "\" .\r\n"
						+ "    dsOnt:" + paramName + " dsOnt:parameterValue \"" + item.getParameterValue() + "\" .\r\n"
						+ "    dsOnt:" + paramName + " dsOnt:qualityParameterCorrespondsTo \"" + item.getQualityParameterCorrespondsTo() + "\" .\r\n"
						+ "    dsOnt:" + paramName + " dsOnt:qualityParameterEvaluationExpression \"" + item.getQualityParameterEvaluationExpression() + "\" .\r\n"
						+ "    dsOnt:" + fullServiceName + " dsOnt:hasQuality dsOnt:" + paramName + " .\r\n";
			}			
		}
		
		return insertQueryQualityParams;
	}
}