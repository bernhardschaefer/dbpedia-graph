# Setup

# Build
- Execute maven as follows; this creates a jar with all dependencies in the target folder
'''
mvn -DskipTests=true compile assembly:single
'''

# Importing DBpedia dumps
- Execute DBpedia Graph Loader. As arguments one or more directories or dump files need to be provided.
'''
java -Xms1G -Xmx4G \
	-cp target/dbpedia-graphdb-0.0.1-SNAPSHOT-jar-with-dependencies.jar \
	de.unima.dws.dbpediagraph.loader.DBpediaGraphLoader \
	/path/to/dir/with/dumps/
'''

# Dump files
## Useful
- article_categories_en.nt --> Category
- instance_types_en.nt --> Ontology 
- mappingbased_properties_en.nt --> Infobox properties (e.g. associatedBand, hometown, ...)
- persondata_en.nt --> Infobox properties for persons (e.g. hometown, birthPlace, ...)
- redirects_en.nt --> Redirects (DBpedia Spotlight uses them for spotting & candidate generation)
- skos_categories_en.nt --> Category
- topical_concepts_en.nt --> Categories (and their skos concepts)

## Not useful
- disambiguations_en.nt --> Disambiguations (connect otherwise unrelated entities due to their similar name)
- *id*, *label*, *link* --> Do not contain direct connections between resources