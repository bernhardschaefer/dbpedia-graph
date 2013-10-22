# Setup

# Download and Install Dbpedia Spotlight into local maven repository
# See https://github.com/dbpedia-spotlight/dbpedia-spotlight/wiki/Run-from-a-JAR
SPOTLIGHT=dbpedia-spotlight-quickstart-0.6.5
wget http://spotlight.dbpedia.org/download/release-0.6/$SPOTLIGHT.zip
cd ~/Downloads && unzip $SPOTLIGHT.zip
mvn install:install-file \
	-Dfile=~/Downloads/$SPOTLIGHT/dbpedia-spotlight-0.6.5-jar-with-dependencies.jar \
	-DgroupId=org.dbpedia.spotlight -DartifactId=spotlight \
	-Dversion=0.6.5 -Dpackaging=jar

# Build
# Execute maven as follows; this creates a jar with all dependencies in the
# target folder
mvn package

# Importing DBpedia dumps
# Execute DBpedia Graph Loader. As arguments one or more directories or dump
# files need to be provided.
java -Xms1G -Xmx4G -cp target/dbpedia-graphdb-0.0.1-SNAPSHOT-jar-with-dependencies.jar
de.unima.dws.dbpediagraph.graphdb.loader.DBpediaGraphLoader
/path/to/dir/with/dumps/

