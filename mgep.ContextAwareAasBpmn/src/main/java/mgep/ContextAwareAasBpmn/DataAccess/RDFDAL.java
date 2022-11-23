package mgep.ContextAwareAasBpmn.DataAccess;
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
		
		String aasId = modelObj.getBinding("aasIdentifier").getValue().stringValue();
		String servName = modelObj.getBinding("serviceName").getValue().stringValue();
		String serviceIdentifier = modelObj.getBinding("serviceIdentifier").getValue().stringValue();
		String serviceUrl = modelObj.getBinding("serviceUrl").getValue().stringValue();
		String serviceMethod = modelObj.getBinding("serviceMethod").getValue().stringValue();
		String serviceDescription = modelObj.getBinding("serviceDescription").getValue().stringValue();
		boolean serviceIsAsync = Boolean.parseBoolean(modelObj.getBinding("serviceIsAsync").getValue().stringValue());
		
		return new ServiceDTO(aasId, serviceIdentifier, serviceUrl, serviceMethod, serviceIsAsync, servName, serviceDescription);
	}
	
	
	public ServiceDTO GetServiceByServiceId(String serviceId) {
		log.info("Enter GetServiceByAasId");		
		RDFRepositoryManager repManager = new RDFRepositoryManager(Tools.GRAPHDB_SERVER);
		
		//prepare select
		String query = "PREFIX rdf: <" + Tools.RDF_IRI + ">\r\n"
				+ "PREFIX dsOnt: <" + Tools.DEVICE_SERVICE_ONT_IRI + ">\r\n"
				+ "select ?deviceName ?aasIdentifier ?serviceName ?serviceIdentifier ?serviceUrl ?serviceMethod ?serviceIsAsync ?serviceDescription where {\r\n"
				+ "    ?device dsOnt:deviceName ?deviceName .\r\n"
				+ "    ?device dsOnt:hasAasIdentifier ?hasAssIdentifier .\r\n"
				+ "    ?hasAssIdentifier dsOnt:deviceAasIdentifier ?aasIdentifier .\r\n"
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
			List<BindingSet> bindingSet = repManager.makeSPARQLquery(Tools.REPOSITORY_ID, query);
			if(bindingSet.isEmpty()) return null;
			
			serviceObj = mapServiceModelObjToDTO(bindingSet.get(0));
			
			//set input, output and quality parameters
			serviceObj.setInputParameters(GetServiceInputParametersByServiceId(serviceObj.getServiceIdentifier()));
			serviceObj.setOutputParameters(GetServiceOutputParametersByServiceId(serviceObj.getServiceIdentifier()));
			serviceObj.setQualityParameters(GetServiceQualityParametersByServiceId(serviceObj.getServiceIdentifier()));
		} catch (Exception e) {
			log.catching(e);
			System.out.println(e.getMessage());
		}
		
		return serviceObj;
	}
	
	public ServiceDTO GetServiceByName(String aasIdentifier, String serviceName) {
		log.info("Enter GetServiceByAasId");		
		RDFRepositoryManager repManager = new RDFRepositoryManager(Tools.GRAPHDB_SERVER);
		
		//prepare select
		String query = "PREFIX rdf: <" + Tools.RDF_IRI + ">\r\n"
				+ "PREFIX dsOnt: <" + Tools.DEVICE_SERVICE_ONT_IRI + ">\r\n"
				+ "select ?deviceName ?aasIdentifier ?serviceName ?serviceIdentifier ?serviceUrl ?serviceMethod ?serviceIsAsync ?serviceDescription where {\r\n"
				+ "    ?device dsOnt:deviceName ?deviceName .\r\n"
				+ "    ?device dsOnt:hasAasIdentifier ?hasAssIdentifier .\r\n"
				+ "    ?hasAssIdentifier dsOnt:deviceAasIdentifier ?aasIdentifier .\r\n"
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
			List<BindingSet> bindingSet = repManager.makeSPARQLquery(Tools.REPOSITORY_ID, query);
			if(bindingSet.isEmpty()) return null;
			
			serviceObj = mapServiceModelObjToDTO(bindingSet.get(0));
			
			//set input, output and quality parameters
			serviceObj.setInputParameters(GetServiceInputParametersByServiceId(serviceObj.getServiceIdentifier()));
			serviceObj.setOutputParameters(GetServiceOutputParametersByServiceId(serviceObj.getServiceIdentifier()));
			serviceObj.setQualityParameters(GetServiceQualityParametersByServiceId(serviceObj.getServiceIdentifier()));
		} catch (Exception e) {
			log.catching(e);
			System.out.println(e.getMessage());
		}
		
		return serviceObj;
	}
	
	public AssetAdminShellDTO GetShellByServiceId(String serviceId) {
		log.info("Enter GetShellByServiceId");		
		RDFRepositoryManager repManager = new RDFRepositoryManager(Tools.GRAPHDB_SERVER);

		//prepare select
		String query = "PREFIX rdf: <" + Tools.RDF_IRI + ">\r\n"
				+ "PREFIX dsOnt: <" + Tools.DEVICE_SERVICE_ONT_IRI + ">\r\n"
				+ "select ?deviceName ?aasIdentifier ?serviceName ?serviceIdentifier ?serviceUrl ?serviceMethod ?serviceIsAsync where {\r\n"
				+ "    ?device dsOnt:deviceName ?deviceName .\r\n"
				+ "    ?device dsOnt:hasAasIdentifier ?hasAssIdentifier .\r\n"
				+ "    ?hasAssIdentifier dsOnt:deviceAasIdentifier ?aasIdentifier .\r\n"
				+ "    ?device dsOnt:hasService ?service .\r\n"
				+ "	   ?service dsOnt:serviceIdentifier ?serviceIdentifier .\r\n"
				+ "    ?service dsOnt:serviceName ?serviceName .\r\n"
				+ "    ?service dsOnt:serviceURL ?serviceUrl .\r\n"
				+ "    ?service dsOnt:serviceMethod ?serviceMethod .\r\n"
				+ "    ?service dsOnt:serviceIsAsync ?serviceIsAsync .\r\n"
				+ "    filter (?serviceIdentifier = \"" + serviceId + "\") .\r\n"
				+ "} limit 100";
		
		//execute select and map object
		AssetAdminShellDTO aasShellObj = null;
		try {
			List<BindingSet> bindingSet = repManager.makeSPARQLquery(Tools.REPOSITORY_ID, query);
			if(bindingSet.isEmpty()) return null;
			
			BindingSet item = bindingSet.get(0);
			String aasId = item.getBinding("aasIdentifier").getValue().stringValue();
			String deviceName = item.getBinding("deviceName").getValue().stringValue();
			aasShellObj = new AssetAdminShellDTO(aasId, deviceName);

		} catch (Exception e) {
			log.catching(e);
			System.out.println(e.getMessage());
		}
		
		return aasShellObj;
	}


	public List<ServiceDTO> GetServicesByAasId(String aasIdentifier) {
		log.info("Enter GetServicesByAasId");		
		RDFRepositoryManager repManager = new RDFRepositoryManager(Tools.GRAPHDB_SERVER);
		
		//prepare select
		String query = "PREFIX rdf: <" + Tools.RDF_IRI + ">\r\n"
				+ "PREFIX dsOnt: <" + Tools.DEVICE_SERVICE_ONT_IRI + ">\r\n"
				+ "select ?deviceName ?aasIdentifier ?serviceName ?serviceIdentifier ?serviceUrl ?serviceMethod ?serviceIsAsync ?serviceDescription where {\r\n"
				+ "    ?device dsOnt:deviceName ?deviceName .\r\n"
				+ "    ?device dsOnt:hasAasIdentifier ?hasAssIdentifier .\r\n"
				+ "    ?hasAssIdentifier dsOnt:deviceAasIdentifier ?aasIdentifier .\r\n"
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
		List<ServiceDTO> lServices = new ArrayList<ServiceDTO>();
		try {
			List<BindingSet> bindingSet = repManager.makeSPARQLquery(Tools.REPOSITORY_ID, query);			
			for (BindingSet item : bindingSet) {
				ServiceDTO serviceObj = mapServiceModelObjToDTO(item);
						
				//set input, output and quality parameters
				serviceObj.setInputParameters(GetServiceInputParametersByServiceId(serviceObj.getServiceIdentifier()));
				serviceObj.setOutputParameters(GetServiceOutputParametersByServiceId(serviceObj.getServiceIdentifier()));
				serviceObj.setQualityParameters(GetServiceQualityParametersByServiceId(serviceObj.getServiceIdentifier()));
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
		RDFRepositoryManager repManager = new RDFRepositoryManager(Tools.GRAPHDB_SERVER);
		
		//prepare select
		String query = "PREFIX rdf: <" + Tools.RDF_IRI + ">\r\n"
				+ "PREFIX dsOnt: <" + Tools.DEVICE_SERVICE_ONT_IRI + ">\r\n"
				+ "select ?deviceName ?aasIdentifier ?serviceName ?serviceIdentifier ?serviceUrl ?serviceMethod ?serviceIsAsync ?serviceDescription where {\r\n"
				+ "    ?device dsOnt:deviceName ?deviceName .\r\n"
				+ "    ?device dsOnt:hasAasIdentifier ?hasAssIdentifier .\r\n"
				+ "    ?hasAssIdentifier dsOnt:deviceAasIdentifier ?aasIdentifier .\r\n"
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
		List<ServiceDTO> lServices = new ArrayList<ServiceDTO>();
		try {
			List<BindingSet> bindingSet = repManager.makeSPARQLquery(Tools.REPOSITORY_ID, query);			
			for (BindingSet item : bindingSet) {
				ServiceDTO serviceObj = mapServiceModelObjToDTO(item);
				
				//set input, output and quality parameters
				serviceObj.setInputParameters(GetServiceInputParametersByServiceId(serviceObj.getServiceIdentifier()));
				serviceObj.setOutputParameters(GetServiceOutputParametersByServiceId(serviceObj.getServiceIdentifier()));
				serviceObj.setQualityParameters(GetServiceQualityParametersByServiceId(serviceObj.getServiceIdentifier()));
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
		RDFRepositoryManager repManager = new RDFRepositoryManager(Tools.GRAPHDB_SERVER);
		
		//prepare select
		String query = "PREFIX rdf: <" + Tools.RDF_IRI + ">\r\n"
				+ "PREFIX dsOnt: <" + Tools.DEVICE_SERVICE_ONT_IRI + ">\r\n"
				+ "select ?deviceName ?aasIdentifier ?serviceName ?serviceIdentifier ?serviceUrl ?serviceMethod ?serviceIsAsync ?serviceDescription where {\r\n"
				+ "    ?device dsOnt:deviceName ?deviceName .\r\n"
				+ "    ?device dsOnt:hasAasIdentifier ?hasAssIdentifier .\r\n"
				+ "    ?hasAssIdentifier dsOnt:deviceAasIdentifier ?aasIdentifier .\r\n"
				+ "    ?device dsOnt:hasService ?service .\r\n"
				+ "	   ?service dsOnt:serviceIdentifier ?serviceIdentifier .\r\n"
				+ "    ?service dsOnt:serviceName ?serviceName .\r\n"
				+ "    ?service dsOnt:serviceURL ?serviceUrl .\r\n"
				+ "    ?service dsOnt:serviceMethod ?serviceMethod .\r\n"
				+ "    ?service dsOnt:serviceIsAsync ?serviceIsAsync .\r\n"
				+ "    ?service dsOnt:serviceDescription ?serviceDescription .\r\n"
				+ "    ?service dsOnt:hasInput ?hasInput .\r\n"
				+ "    ?hasInput dsOnt:serviceParameterName ?serviceInputParameterName .\r\n"
				+ "    ?hasInput dsOnt:serviceParameterType ?serviceInputParameterType .\r\n"
				+ "    ?hasInput dsOnt:serviceParameterValue ?serviceInputParameterValue .\r\n"
				+ "    ?service dsOnt:hasOutput ?hasOutput .\r\n"
				+ "    ?hasOutput dsOnt:serviceParameterName ?serviceOutputParameterName .\r\n"
				+ "    ?hasOutput dsOnt:serviceParameterType ?serviceOutputParameterType .\r\n"
				+ "    ?hasOutput dsOnt:serviceParameterValue ?serviceOutputParameterValue .\r\n"
				+ "    filter contains(lcase(str(?serviceInputParameterName)), lcase(\"" + inputParamName + "\")) .\r\n"
				+ "    filter contains(lcase(str(?serviceOutputParameterName)), lcase(\"" + outputParamName + "\")) .\r\n"
				+ "} limit 100";
		
		//execute select and map object
		List<ServiceDTO> lServices = new ArrayList<ServiceDTO>();
		try {
			List<BindingSet> bindingSet = repManager.makeSPARQLquery(Tools.REPOSITORY_ID, query);			
			for (BindingSet item : bindingSet) {
				ServiceDTO serviceObj = mapServiceModelObjToDTO(item);
				
				//set input, output and quality parameters
				serviceObj.setInputParameters(GetServiceInputParametersByServiceId(serviceObj.getServiceIdentifier()));
				serviceObj.setOutputParameters(GetServiceOutputParametersByServiceId(serviceObj.getServiceIdentifier()));
				serviceObj.setQualityParameters(GetServiceQualityParametersByServiceId(serviceObj.getServiceIdentifier()));
				
				lServices.add(serviceObj);
			}
		} catch (Exception e) {
			log.catching(e);
			System.out.println(e.getMessage());
		}
		
		return lServices;
	}
	
	public List<ServiceParameterDTO> GetServiceInputParametersByServiceId(String serviceId) {
		log.info("Enter GetServiceInputParametersByServiceId");		
		RDFRepositoryManager repManager = new RDFRepositoryManager(Tools.GRAPHDB_SERVER);
		
		//prepare select
		String query = "PREFIX rdf: <" + Tools.RDF_IRI + ">\r\n"
				+ "PREFIX dsOnt: <" + Tools.DEVICE_SERVICE_ONT_IRI + ">\r\n"
				+ "select ?serviceInputParameterName ?serviceInputParameterType ?serviceInputParameterValue where {\r\n"
				+ "	   ?service dsOnt:serviceIdentifier ?serviceIdentifier .\r\n"
				+ "    ?service dsOnt:hasInput ?hasInput .\r\n"
				+ "    ?hasInput dsOnt:serviceParameterName ?serviceInputParameterName .\r\n"
				+ "    ?hasInput dsOnt:serviceParameterType ?serviceInputParameterType .\r\n"
				+ "    ?hasInput dsOnt:serviceParameterValue ?serviceInputParameterValue .\r\n"
				+ "    filter (?serviceIdentifier = \"" + serviceId + "\") .\r\n"
				+ "} limit 100";
		
		//execute select and map object
		List<ServiceParameterDTO> lInputParams = new ArrayList<ServiceParameterDTO>();
		try {
			List<BindingSet> bindingSet = repManager.makeSPARQLquery(Tools.REPOSITORY_ID, query);			
			for (BindingSet item : bindingSet) {
				String serviceInputParameterName = item.getBinding("serviceInputParameterName").getValue().stringValue();
				String serviceInputParameterType = item.getBinding("serviceInputParameterType").getValue().stringValue();
				String serviceInputParameterValue = item.getBinding("serviceInputParameterValue").getValue().stringValue();
				lInputParams.add(new ServiceParameterDTO(serviceInputParameterName, serviceInputParameterType, serviceInputParameterValue));
			}
		} catch (Exception e) {
			log.catching(e);
			System.out.println(e.getMessage());
		}
		
		return lInputParams;
	}
	
	public List<ServiceParameterDTO> GetServiceOutputParametersByServiceId(String serviceId) {
		log.info("Enter GetServiceInputParametersByServiceId");		
		RDFRepositoryManager repManager = new RDFRepositoryManager(Tools.GRAPHDB_SERVER);
		
		//prepare select
		String query = "PREFIX rdf: <" + Tools.RDF_IRI + ">\r\n"
				+ "PREFIX dsOnt: <" + Tools.DEVICE_SERVICE_ONT_IRI + ">\r\n"
				+ "select ?serviceOutputParameterName ?serviceOutputParameterType ?serviceOutputParameterValue where {\r\n"
				+ "	   ?service dsOnt:serviceIdentifier ?serviceIdentifier .\r\n"
				+ "    ?service dsOnt:hasOutput ?hasOutput .\r\n"
				+ "    ?hasOutput dsOnt:serviceParameterName ?serviceOutputParameterName .\r\n"
				+ "    ?hasOutput dsOnt:serviceParameterType ?serviceOutputParameterType .\r\n"
				+ "    ?hasOutput dsOnt:serviceParameterValue ?serviceOutputParameterValue .\r\n"
				+ "    filter (?serviceIdentifier = \"" + serviceId + "\") .\r\n"
				+ "} limit 100";
		
		//execute select and map object
		List<ServiceParameterDTO> lOutputParams = new ArrayList<ServiceParameterDTO>();
		try {
			List<BindingSet> bindingSet = repManager.makeSPARQLquery(Tools.REPOSITORY_ID, query);			
			for (BindingSet item : bindingSet) {
				String serviceOutputParameterName = item.getBinding("serviceOutputParameterName").getValue().stringValue();
				String serviceOutputParameterType = item.getBinding("serviceOutputParameterType").getValue().stringValue();
				String serviceOutputParameterValue = item.getBinding("serviceOutputParameterValue").getValue().stringValue();
				lOutputParams.add(new ServiceParameterDTO(serviceOutputParameterName, serviceOutputParameterType, serviceOutputParameterValue));
			}
		} catch (Exception e) {
			log.catching(e);
			System.out.println(e.getMessage());
		}
		
		return lOutputParams;
	}
	
	public List<QualityParameterDTO> GetServiceQualityParametersByServiceId(String serviceId) {
		log.info("Enter GetServiceInputParametersByServiceId");		
		RDFRepositoryManager repManager = new RDFRepositoryManager(Tools.GRAPHDB_SERVER);
		
		//prepare select
		String query = "PREFIX rdf: <" + Tools.RDF_IRI + ">\r\n"
				+ "PREFIX dsOnt: <" + Tools.DEVICE_SERVICE_ONT_IRI + ">\r\n"
				+ "select ?qualityParameterName ?qualityParameterCorrespondsTo ?qualityParameterType ?qualityParameterValue ?qualityParameterEvaluationExpression where {\r\n"
				+ "	   ?service dsOnt:serviceIdentifier ?serviceIdentifier .\r\n"
				+ "    ?service dsOnt:hasQuality ?hasQuality .\r\n"
				+ "    ?hasQuality dsOnt:qualityParameterName ?qualityParameterName .\r\n"
				+ "    ?hasQuality dsOnt:qualityParameterType ?qualityParameterType .\r\n"
				+ "    ?hasQuality dsOnt:qualityParameterValue ?qualityParameterValue .\r\n"
				+ "    ?hasQuality dsOnt:qualityParameterCorrespondsTo ?qualityParameterCorrespondsTo .\r\n"
				+ "    ?hasQuality dsOnt:qualityParameterEvaluationExpression ?qualityParameterEvaluationExpression .\r\n"
				+ "    filter (?serviceIdentifier = \"" + serviceId + "\") .\r\n"
				+ "} limit 100";
		
		//execute select and map object
		List<QualityParameterDTO> lQualityParams = new ArrayList<QualityParameterDTO>();
		try {
			List<BindingSet> bindingSet = repManager.makeSPARQLquery(Tools.REPOSITORY_ID, query);			
			for (BindingSet item : bindingSet) {
				QualityParameterDTO qosObj = new QualityParameterDTO();
				qosObj.setName(item.getBinding("qualityParameterName").getValue().stringValue());
				qosObj.setValue(item.getBinding("qualityParameterValue").getValue().stringValue());
				qosObj.setDataType(item.getBinding("qualityParameterType").getValue().stringValue());
				qosObj.setCorrespondsTo(item.getBinding("qualityParameterCorrespondsTo").getValue().stringValue());
				qosObj.setEvaluationExpression(item.getBinding("qualityParameterEvaluationExpression").getValue().stringValue());
				lQualityParams.add(qosObj);
			}
		} catch (Exception e) {
			log.catching(e);
			System.out.println(e.getMessage());
		}
		
		return lQualityParams;
	}
	
	public List<AssetAdminShellDTO> GetShells(String aasIdentifier) {
		log.info("Enter GetShells");		
		RDFRepositoryManager repManager = new RDFRepositoryManager(Tools.GRAPHDB_SERVER);
		
		//prepare select
		String query = "PREFIX rdf: <" + Tools.RDF_IRI + ">\r\n"
				+ "PREFIX dsOnt: <" + Tools.DEVICE_SERVICE_ONT_IRI + ">\r\n"
				+ "select ?deviceName ?aasIdentifier where {\r\n"
				+ "    ?device dsOnt:deviceName ?deviceName .\r\n"
				+ "    ?device dsOnt:hasAasIdentifier ?hasAssIdentifier .\r\n"
				+ "    ?hasAssIdentifier dsOnt:deviceAasIdentifier ?aasIdentifier .\r\n"
				+ "filter contains(lcase(str(?aasIdentifier)),\"" + aasIdentifier.toLowerCase() + "\") .\r\n"
				+ "} limit 100";
		
		//execute select and map object
		List<AssetAdminShellDTO> lShells = new ArrayList<AssetAdminShellDTO>();
		try {
			List<BindingSet> bindingSet = repManager.makeSPARQLquery(Tools.REPOSITORY_ID, query);			
			for (BindingSet item : bindingSet) {
				String deviceName = item.getBinding("deviceName").getValue().stringValue();
				String aasId = item.getBinding("aasIdentifier").getValue().stringValue();
				lShells.add(new AssetAdminShellDTO(aasId,deviceName));
			}
		} catch (Exception e) {
			log.catching(e);
			System.out.println(e.getMessage());
		}
		
		return lShells;
	}
	
	public ResponseContextValServiceSelectionDTO ValidateContextSelectBestService(RequestContextValServiceSelectionDTO requestObj) {
		log.info("Enter ValidateContextSelectBestService");		
		RDFRepositoryManager repManager = new RDFRepositoryManager(Tools.GRAPHDB_SERVER);

		//prepare select requested service
		ServiceDTO requestedServiceObj = null;
		String queryRequestedService = "PREFIX rdf: <" + Tools.RDF_IRI + ">\r\n"
				+ "PREFIX dsOnt: <" + Tools.DEVICE_SERVICE_ONT_IRI + ">\r\n"
				+ "select ?deviceName ?aasIdentifier ?serviceName ?serviceIdentifier ?serviceUrl ?serviceMethod ?serviceIsAsync ?serviceDescription where {\r\n"
				+ "    ?device dsOnt:deviceName ?deviceName .\r\n"
				+ "    ?device dsOnt:hasAasIdentifier ?hasAssIdentifier .\r\n"
				+ "    ?hasAssIdentifier dsOnt:deviceAasIdentifier ?aasIdentifier .\r\n"
				+ "    ?device dsOnt:hasService ?service .\r\n"
				+ "	   ?service dsOnt:serviceIdentifier ?serviceIdentifier .\r\n"
				+ "    ?service dsOnt:serviceName ?serviceName .\r\n"
				+ "    ?service dsOnt:serviceURL ?serviceUrl .\r\n"
				+ "    ?service dsOnt:serviceMethod ?serviceMethod .\r\n"
				+ "    ?service dsOnt:serviceIsAsync ?serviceIsAsync .\r\n"
				+ "    ?service dsOnt:serviceDescription ?serviceDescription .\r\n"
				+ "    filter (?aasIdentifier = \"" + requestObj.getAasIdentifier() + "\") .\r\n"
				+ "    filter (?serviceName = \"" + requestObj.getServiceName() + "\") .\r\n"
				+ "} limit 100";
		
		//execute select and map object
		try {
			List<BindingSet> bindingSet = repManager.makeSPARQLquery(Tools.REPOSITORY_ID, queryRequestedService);
			if(bindingSet.isEmpty()) {
				ResponseContextValServiceSelectionDTO contextValidationResult = new ResponseContextValServiceSelectionDTO(false, "The requested service does not match to our records. Please check you have provided the right information.");
				return contextValidationResult;
			}
			
			requestedServiceObj = mapServiceModelObjToDTO(bindingSet.get(0));
			
			//set input, output and quality parameters
			requestedServiceObj.setInputParameters(GetServiceInputParametersByServiceId(requestedServiceObj.getServiceIdentifier()));
			requestedServiceObj.setOutputParameters(GetServiceOutputParametersByServiceId(requestedServiceObj.getServiceIdentifier()));
			requestedServiceObj.setQualityParameters(GetServiceQualityParametersByServiceId(requestedServiceObj.getServiceIdentifier()));	
		} catch (Exception e) {
			log.catching(e);
			System.out.println(e.getMessage());
			return null;
		}
		
		//prepare quality validation
		String queryQualitySelector = "";
		String queryQualityFilter = "";
		if(requestObj.getQualityParameters() != null && requestObj.getQualityParameters().size() > 0) {			
			for (QualityParameterDTO qItem : requestObj.getQualityParameters()) {
				String hasQualitySelectorVarName = "hasQuality" + qItem.getName();
				String qualityParameterValueVarName = "qualityParameterValue" + qItem.getName();
				String qualityParameterDataType = requestedServiceObj.getQualityParameters().stream().filter(x-> x.getName().equals(qItem.getName())).findAny().get().getDataType().toLowerCase();
				String qualityEvalRightSide = qItem.getEvaluationExpression().replace(qItem.getName(), "");
				String evalExpression = String.format("xsd:%s(?%s) %s", qualityParameterDataType, qualityParameterValueVarName, qualityEvalRightSide);
				queryQualitySelector += "    ?service dsOnt:hasQuality ?" + hasQualitySelectorVarName + " .\r\n"
						+ "    ?" + hasQualitySelectorVarName + " dsOnt:qualityParameterName \"" + qItem.getName() + "\" .\r\n"
						+ "    ?" + hasQualitySelectorVarName + " dsOnt:qualityParameterValue ?" + qualityParameterValueVarName + " .\r\n";
				queryQualityFilter += "    filter (" + evalExpression + ") .\r\n";
			}
		}
		
		//prepare select for suggested service according to evaluation of QoS
		ServiceDTO suggestedServiceObj = null;
		String querySuggestedService = "PREFIX rdf: <" + Tools.RDF_IRI + ">\r\n"
				+ "PREFIX dsOnt: <" + Tools.DEVICE_SERVICE_ONT_IRI + ">\r\n"
				+ "select ?deviceName ?aasIdentifier ?serviceName ?serviceIdentifier ?serviceUrl ?serviceMethod ?serviceIsAsync ?serviceDescription where {\r\n"
				+ "    ?device dsOnt:deviceName ?deviceName .\r\n"
				+ "    ?device dsOnt:hasAasIdentifier ?hasAssIdentifier .\r\n"
				+ "    ?hasAssIdentifier dsOnt:deviceAasIdentifier ?aasIdentifier .\r\n"
				+ "    ?device dsOnt:hasService ?service .\r\n"
				+ "	   ?service dsOnt:serviceIdentifier ?serviceIdentifier .\r\n"
				+ "    ?service dsOnt:serviceName ?serviceName .\r\n"
				+ "    ?service dsOnt:serviceURL ?serviceUrl .\r\n"
				+ "    ?service dsOnt:serviceMethod ?serviceMethod .\r\n"
				+ "    ?service dsOnt:serviceIsAsync ?serviceIsAsync .\r\n"
				+ "    ?service dsOnt:serviceDescription ?serviceDescription .\r\n"
				+ queryQualitySelector
				+ queryQualityFilter
				+ "    filter (?serviceName = \"" + requestedServiceObj.getName() + "\") .\r\n"
				+ "} limit 100";
		
		//execute select and map object
		try {
			List<BindingSet> lBindingSet = repManager.makeSPARQLquery(Tools.REPOSITORY_ID, querySuggestedService);
			if(!lBindingSet.isEmpty()) {
				for (BindingSet item : lBindingSet) {
					suggestedServiceObj = mapServiceModelObjToDTO(item);
					
					//if is the same as the one requested, do nothing and seek for the next one
					if(suggestedServiceObj.getServiceIdentifier().equals(requestedServiceObj.getServiceIdentifier())) continue;
					//if already selected, break loop
					if(suggestedServiceObj != null) break;
				}
			}			
		} catch (Exception e) {
			log.catching(e);
			System.out.println(e.getMessage());
			return null;
		}
		
		//prepare response
		ResponseContextValServiceSelectionDTO contextValidationResult = new ResponseContextValServiceSelectionDTO(true, "OK");
		contextValidationResult.setRequestedService(requestedServiceObj);
		
		//if suggestedService is found: set input, output and quality parameters
		if(suggestedServiceObj != null) {
			suggestedServiceObj.setInputParameters(GetServiceInputParametersByServiceId(suggestedServiceObj.getServiceIdentifier()));
			suggestedServiceObj.setOutputParameters(GetServiceOutputParametersByServiceId(suggestedServiceObj.getServiceIdentifier()));
			suggestedServiceObj.setQualityParameters(GetServiceQualityParametersByServiceId(suggestedServiceObj.getServiceIdentifier()));
			
			contextValidationResult.setSuggestedService(suggestedServiceObj);
			contextValidationResult.setMessage("A better service is recomended after evaluation of quality of service parameters");
			contextValidationResult.setCanExecute(false);				
		}
		
		return contextValidationResult;
	}
}