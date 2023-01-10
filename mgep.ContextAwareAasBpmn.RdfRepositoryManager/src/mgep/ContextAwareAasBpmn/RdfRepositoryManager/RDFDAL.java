package mgep.ContextAwareAasBpmn.RdfRepositoryManager;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.rdf4j.query.BindingSet;

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
		var servName = modelObj.getBinding("serviceName").getValue().stringValue();
		var serviceIdentifier = modelObj.getBinding("serviceIdentifier").getValue().stringValue();
		var serviceUrl = modelObj.getBinding("serviceUrl").getValue().stringValue();
		var serviceMethod = modelObj.getBinding("serviceMethod").getValue().stringValue();
		var serviceDescription = modelObj.getBinding("serviceDescription").getValue().stringValue();
		var serviceIsAsync = Boolean.parseBoolean(modelObj.getBinding("serviceIsAsync").getValue().stringValue());
		
		var serviceObj = new ServiceDTO();
		serviceObj.setAasIdShort(aasIdShort);
		serviceObj.setAasIdentifier(aasId);
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
		if(requestObj.getQualityParameters() != null && requestObj.getQualityParameters().size() > 0) {			
			for (var qItem : requestObj.getQualityParameters()) {
				var hasQualitySelectorVarName = "quality" + qItem.getParameterName();
				var qualityParameterValueVarName = "qualityParameterValue" + qItem.getParameterName();
				var qualityParameterDataType = requestedServiceObj.getServiceQualityParameters().stream().filter(x-> x.getParameterName().equals(qItem.getParameterName())).findAny().get().getParameterType().toLowerCase();
				var qualityEvalRightSide = qItem.getQualityParameterEvaluationExpression().replace(qItem.getParameterName(), "");
				var evalExpression = String.format("xsd:%s(?%s) %s", qualityParameterDataType, qualityParameterValueVarName, qualityEvalRightSide);
				queryQualitySelector += "    ?service dsOnt:hasQuality ?" + hasQualitySelectorVarName + " .\r\n"
						+ "    ?" + hasQualitySelectorVarName + " dsOnt:parameterName \"" + qItem.getParameterName() + "\" .\r\n"
						+ "    ?" + hasQualitySelectorVarName + " dsOnt:parameterValue ?" + qualityParameterValueVarName + " .\r\n";
				queryQualityFilter += "    filter (" + evalExpression + ") .\r\n";

				//prepare order by based on comparison symbol				
				var comparisonSymbol = qualityEvalRightSide.trim().split(" ")[0];
				switch (comparisonSymbol) {
					case "<": case "<=":
						//<: means the lesser the value, the better. "ORDER BY ASC" puts the lessest value on top of the resultset						
						if(queryQualityOrderBy.length() == 0) {
							queryQualityOrderBy = String.format("ORDER BY ASC(xsd:%s(?%s))", qualityParameterDataType, qualityParameterValueVarName);							
						}else if (queryQualityOrderBy.contains("ORDER BY")) {
							//append new parameter
							queryQualityOrderBy += String.format(" ASC(xsd:%s(?%s))", qualityParameterDataType, qualityParameterValueVarName);
						}
						break;
					case ">": case ">=":
						//>: means the greater the value, the better. "ORDER BY DESC" puts the greatest value on top of the resultset					
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
		var querySuggestedService = "PREFIX rdf: <" + Tools.RDF_IRI + ">\r\n"
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
				+ queryQualityFilter
				+ "    filter (?serviceName = \"" + requestedServiceObj.getServiceName() + "\") .\r\n"
				+ "} "
				+ queryQualityOrderBy
				+ "    limit 10";
		
		//execute select and map object
		try {
			var lBindingSet = repManager.makeSPARQLquery(Tools.REPOSITORY_ID, querySuggestedService);
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
		
		//prepare response
		var contextValidationResult = new ResponseContextValServiceSelectionDTO(true, "OK");
		
		//if suggestedService is found: set input, output and quality parameters
		if(suggestedServiceObj != null) {
			suggestedServiceObj.setServiceInputParameters(GetServiceInputParametersByServiceId(suggestedServiceObj.getServiceIdentifier()));
			suggestedServiceObj.setServiceOutputParameters(GetServiceOutputParametersByServiceId(suggestedServiceObj.getServiceIdentifier()));
			suggestedServiceObj.setServiceQualityParameters(GetServiceQualityParametersByServiceId(suggestedServiceObj.getServiceIdentifier()));
			
			contextValidationResult.setSuggestedService(suggestedServiceObj);
			contextValidationResult.setMessage("A better service is recomended after evaluation of quality of service parameters");
			contextValidationResult.setCanExecute(false);				
		}
		
		return contextValidationResult;
	}
}