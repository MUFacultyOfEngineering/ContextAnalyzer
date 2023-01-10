package mgep.ContextAwareAasBpmn.RdfRepositoryManager;

import java.io.File;
import java.util.List;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.GraphQueryResult;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.QueryResults;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.manager.RemoteRepositoryManager;
import org.eclipse.rdf4j.repository.util.Repositories;
import org.eclipse.rdf4j.rio.RDFFormat;

/**
 * @class RepositoryManager
 * @brief Bundle that contains the methods for managing a remote RDF repository.
 * @author Javier Cuenca Ariza
 * @date 02/02/2016
 * @todo Falta poner la opcion de que el repositorio sea local o remoto.
 */
public class RDFRepositoryManager {
	
	RemoteRepositoryManager remote_manager;
	
	/**
	 * @brief constructor of the RDF repository manager. It initializes a 
	 * remote repository manager.
	 * @param server_url url of the server that contains repositories.
	 */
	public RDFRepositoryManager(String server_url){
		
		this.remote_manager = new RemoteRepositoryManager(server_url);
		this.remote_manager.init();
		
	}
	
	/**
	 * @brief Method that opens a connection to a remote semantic repository.
	 * @param id_repository identifier of the repository.
	 * @return
	 */
	public RepositoryConnection connectToRepository(String id_repository){
		
		RepositoryConnection conn = null;
		
		try{
			conn = this.remote_manager.getRepository(id_repository).getConnection();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return conn;
		
	}
	
	/**
	 * @brief This method is used to load RDF file data to a semantic repository.
	 * @param file_location location of the RDF data file.
	 * @param id_repository semantic repository where data is loaded.
	 * @param base_uri repository base URI.
	 */
	public void loadRRDFile(String file_location, String id_repository, String base_uri){
		
		var file = new File (file_location);
		RepositoryConnection conn = this.connectToRepository(id_repository);
		
		try{
			try {
			      conn.add(file,base_uri,RDFFormat.RDFXML);
			   }
			   finally {
			      conn.close();
			   }
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * @brief Method used to make a SPARQL query to a specific repository.
	 * @param id_repository repository identifier.
	 * @param query content of the SPARQL query.
	 * @return results: query results.
	 */
	public List<BindingSet> makeSPARQLquery(String id_repository, String query){
		
		List<BindingSet> results = null;
		
		try{
			results = Repositories.tupleQuery(this.remote_manager.getRepository(id_repository), query, r -> QueryResults.asList(r));
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return results;
		
	}
	
	/**
	 * @brief Method used to make a graph SPARQL query to a specific repository.
	 * @param id_repository repository identifier.
	 * @param query content of the SPARQL query.
	 * @return results: query results.
	 */
	public GraphQueryResult makeGraphSPARQLquery(String id_repository, String query){
		
		RepositoryConnection conn = this.connectToRepository(id_repository);
		GraphQueryResult graph_result = null;
		
		try{
			graph_result = conn.prepareGraphQuery(query).evaluate();
			conn.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return graph_result;
		
	}
	
	/**
	 * @brief Method that queries all statements related to a specific resource.
	 * @param id_repository repository identifier.
	 * @param resource resource in IRI format.
	 * @return statements: set of statements related to the resource.
	 */
	public Model queryResourceStatements(String id_repository, IRI resource){
		
		Model statements_model = null;
		RepositoryConnection conn = this.connectToRepository(id_repository);
		
		try{
			var statements = conn.getStatements(resource, null, null);
		    statements_model = QueryResults.asModel(statements);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return statements_model;
		
	}
	
	/**
	 * @brief Method that adds a new object statement to a specific repository.
	 * @param id_repository repository identifier.
	 * @param subject subject of the statement in IRI format.
	 * @param predicate predicate of the statement in IRI format.
	 * @param object object of the statement in IRI format.
	 * @Note The operation is performed as a transaction.
	 */
	public void addObjectStatement(String id_repository, IRI subject, IRI predicate, IRI object){
		
		var conn = this.connectToRepository(id_repository);
		
		try{
			conn.begin();
			conn.add(subject, predicate, object);
			conn.commit();
		}catch(Exception e){
			conn.rollback();
			e.printStackTrace();
		}
		finally{
			conn.close();
		}
		
	}
	
	/**
	 * @brief Method that adds a new literal statement to a specific repository.
	 * @param id_repository repository identifier.
	 * @param subject subject of the statement in IRI format.
	 * @param predicate predicate of the statement in IRI format.
	 * @param literal literal of the statement.
	 * @Note The operation is performed as a transaction.
	 */
	public void addLiteralStatement(String id_repository, IRI subject, IRI predicate,
			Literal literal){
		
		var conn = this.connectToRepository(id_repository);
		
		try{
			conn.begin();
			conn.add(subject, predicate, literal);
			conn.commit();
		}catch(Exception e){
			conn.rollback();
			e.printStackTrace();
		}
		finally{
			conn.close();
		}
		
	}
	
	/**
	 * @brief Method that adds a group of statements into a repository.
	 * @param id_repository repository identifier.
	 * @param group_of_statements group of statements to be added.
	 * @Note The operation is performed as a transaction.
	 */
	public void addGroupOfStatements(String id_repository, Model group_of_statements){
		
		var conn = this.connectToRepository(id_repository);
		
		try{
			conn.begin();
			conn.add(group_of_statements);
			conn.commit();
		}catch(Exception e){
			conn.rollback();
			e.printStackTrace();
		}
		finally{
			conn.close();
		}
		
	}
	
	/**
	 * @brief Method that removes a object statement to a specific repository.
	 * @param id_repository repository identifier.
	 * @param subject subject of the statement in IRI format.
	 * @param predicate predicate of the statement in IRI format.
	 * @param object object of the statement in IRI format.
	 * @Note The operation is performed as a transaction.
	 */
	public void removeObjectStatement(String id_repository, IRI subject, IRI predicate, 
			IRI object){
		
		var conn = this.connectToRepository(id_repository);
		
		try{
			conn.remove(subject, predicate, object);
			conn.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			conn.close();
		}
		
	}
	
	/**
	 * @brief Method that removes a literal statement to a specific repository.
	 * @param id_repository repository identifier.
	 * @param subject subject of the statement in IRI format.
	 * @param predicate predicate of the statement in IRI format.
	 * @param literal literal of the statement.
	 * @Note The operation is performed as a transaction.
	 */
	public void removeLiteralStament(String id_repository, IRI subject, IRI predicate,
			Literal literal){
		
		var conn = this.connectToRepository(id_repository);
		
		try{
			conn.begin();
			conn.remove(subject, predicate, literal);
			conn.commit();
		}catch(Exception e){
			conn.rollback();
			e.printStackTrace();
		}
		finally{
			conn.close();
		}
		
	}

	/**
	 * @brief Method used to remove a group of statements from a repository of a specific resource. 
	 * @param id_repository identifier of the repository.
	 * @param resource resource which statements are to be removed.
	 * @Note The operation is performed as a transaction.
	 */
	public void removeGroupOfStatements(String id_repository, IRI resource){
		
		var conn = this.connectToRepository(id_repository);
		
		try{
			conn.begin();
			conn.remove(resource,null,null);
			conn.commit();
		}catch(Exception e){
			conn.rollback();
			e.printStackTrace();
		}
		finally{
			conn.close();
		}
		
	}
	
	/**
	 * @brief Method that converts a string to an IRI that represents a RDF resource of a 
	 * certain repository.
	 * @param id_repository repository to which the resource identified by the IRI belongs.
	 * @param string_to_convert string that will be converted to and IRI.
	 * @return converted_iri: created IRI from the input string.
	 */
	public IRI convertStringToIRI(String id_repository, String string_to_convert){
		
		IRI converted_iri = null;
		var value_factory = this.remote_manager.getRepository(id_repository).getValueFactory();
		
		try{
			converted_iri = value_factory.createIRI(string_to_convert);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return converted_iri;
		
	}
	
	public boolean executeQuery(String id_repository, String strQuery) {
		var conn = this.connectToRepository(id_repository);
		
		try{
			conn.begin();
			var updateOperation = conn.prepareUpdate(QueryLanguage.SPARQL, strQuery);
			updateOperation.execute();
			conn.commit();
			return true;
		}catch(Exception e){
			conn.rollback();
			e.printStackTrace();
			return false;
		}
		finally{
			conn.close();
		}
	}
}
