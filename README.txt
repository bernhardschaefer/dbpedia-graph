# Setup

# Build
# Execute maven as follows; this creates a jar with all dependencies in the
# target folder
mvn -DskipTests=true compile assembly:single

# Importing DBpedia dumps
# Execute DBpedia Graph Loader. As arguments one or more directories or dump
# files need to be provided.
java -Xms1G -Xmx4G \
	-cp target/dbpedia-graphdb-0.0.1-SNAPSHOT-jar-with-dependencies.jar \
	de.unima.dws.dbpediagraph.graphdb.loader.DBpediaGraphLoader \
	/path/to/dir/with/dumps/

