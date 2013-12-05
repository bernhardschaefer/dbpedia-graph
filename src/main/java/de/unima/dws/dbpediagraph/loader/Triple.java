package de.unima.dws.dbpediagraph.loader;

import org.openrdf.model.*;
import org.openrdf.model.impl.URIImpl;

import de.unima.dws.dbpediagraph.graph.UriTransformer;

/**
 * Light RDF triple implementation which decodes subject, predicate, and object assuming UTF-8 representation.
 * 
 * @author Bernhard Schäfer
 * 
 */
class Triple {
	private final String subject;
	private final String predicate;
	private final String object;

	Triple(Resource subject, URI predicate, Value object) {
		this.subject = UriTransformer.decode(subject.stringValue());
		this.predicate = UriTransformer.decode(predicate.stringValue());
		if (object instanceof Literal) // only decode if the object is an url
			this.object = object.stringValue();
		else
			this.object = UriTransformer.decode(object.stringValue());
	}

	String subject() {
		return subject;
	}

	String predicate() {
		return predicate;
	}

	String object() {
		return object;
	}

	static Triple fromStatement(Statement st) {
		return new Triple(st.getSubject(), st.getPredicate(), st.getObject());
	}

	/**
	 * This method is mostly for testing purposes. It assumes that the object is an URI.
	 */
	static Triple fromStringUris(String sub, String pred, String obj) {
		return new Triple(new URIImpl(sub), new URIImpl(pred), new URIImpl(obj));
	}

}
