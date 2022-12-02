package mgep.ContextAwareAasBpmn.API;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import org.apache.logging.log4j.*;

import mgep.ContextAwareAasBpmn.DataAccess.RDFDAL;
import mgep.ContextAwareAasBpmn.Entities.RequestContextValServiceSelectionDTO;

@Path("/ContextAnalyzer")
public class ContextAnalyzer {
	static Logger log = LogManager.getLogger(ContextAnalyzer.class.getName());
    
    @GET
    @Path("/GetShells")
    @Produces(MediaType.APPLICATION_JSON)
    public Response GetShells(@QueryParam("aasIdentifier") @DefaultValue("") String aasIdentifier) {
		return Response.ok(new RDFDAL().GetShells(aasIdentifier)).build();
    }
    
    @GET
    @Path("/GetShellByServiceId")
    @Produces(MediaType.APPLICATION_JSON)
    public Response GetShellByServiceId(@QueryParam("serviceIdentifier") String serviceIdentifier) {
    	return Response.ok(new RDFDAL().GetShellByServiceId(serviceIdentifier)).build();
    }
    
    @GET
    @Path("/GetServiceByServiceId")
    @Produces(MediaType.APPLICATION_JSON)
    public Response GetServiceByServiceId(@QueryParam("serviceIdentifier") String serviceIdentifier) {
		return Response.ok(new RDFDAL().GetServiceByServiceId(serviceIdentifier)).build();
    }
    
    @GET
    @Path("/GetServiceByName")
    @Produces(MediaType.APPLICATION_JSON)
    public Response GetServiceByName(@QueryParam("aasIdentifier") String aasIdentifier, @QueryParam("serviceName") String serviceName) {
		return Response.ok(new RDFDAL().GetServiceByName(aasIdentifier, serviceName)).build();
    }
    
    @GET
    @Path("/GetServicesByAasId")
    @Produces(MediaType.APPLICATION_JSON)
    public Response GetServicesByAasId(@QueryParam("aasIdentifier") @DefaultValue("") String aasIdentifier) {
    	return Response.ok(new RDFDAL().GetServicesByAasId(aasIdentifier)).build();
    }
    
    @GET
    @Path("/GetServiceInputParametersByServiceId")
    @Produces(MediaType.APPLICATION_JSON)
    public Response GetServiceInputParametersByServiceId(@QueryParam("serviceIdentifier") @DefaultValue("") String serviceIdentifier) {
    	return Response.ok(new RDFDAL().GetServiceInputParametersByServiceId(serviceIdentifier)).build();
    }
    
    @GET
    @Path("/GetServiceOutputParametersByServiceId")
    @Produces(MediaType.APPLICATION_JSON)
    public Response GetServiceOutputParametersByServiceId(@QueryParam("serviceIdentifier") @DefaultValue("") String serviceIdentifier) {
    	return Response.ok(new RDFDAL().GetServiceOutputParametersByServiceId(serviceIdentifier)).build();
    }
    
    @GET
    @Path("/GetServiceQualityParametersByServiceId")
    @Produces(MediaType.APPLICATION_JSON)
    public Response GetServiceQualityParametersByServiceId(@QueryParam("serviceIdentifier") @DefaultValue("") String serviceIdentifier) {
    	return Response.ok(new RDFDAL().GetServiceQualityParametersByServiceId(serviceIdentifier)).build();
    }
    
    @GET
    @Path("/GetServicesByDescription")
    @Produces(MediaType.APPLICATION_JSON)
    public Response GetServicesByDescription(@QueryParam("description") @DefaultValue("") String description) {
    	return Response.ok(new RDFDAL().GetServicesByDescription(description)).build();    	
    }
    
    @GET
    @Path("/GetServicesByParamName")
    @Produces(MediaType.APPLICATION_JSON)
    public Response GetServicesByParamName(@QueryParam("inputParamName") @DefaultValue("") String inputParamName, @QueryParam("outputParamName") @DefaultValue("") String outputParamName) {
    	return Response.ok(new RDFDAL().GetServicesByParamName(inputParamName, outputParamName)).build();
    }

    @POST
    @Path("/ValidateContextSelectBestService")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response ValidateContextSelectBestService(RequestContextValServiceSelectionDTO requestObj) {
    	return Response.ok(new RDFDAL().ValidateContextSelectBestService(requestObj)).build();    	
    }   
}
