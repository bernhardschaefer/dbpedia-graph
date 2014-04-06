# DBpedia Graph for Linking Entities to Wikipedia

## Build
- Execute maven as follows; this creates a jar with all dependencies in the ```/target``` folder

```
mvn compile assembly:single
```

## Configuration
- Configuration for creating a graph and for disambiguation is done using a properties file
- The path to the actual properties file needs to be configured in [redirect.properties](https://github.com/bernhardschaefer/dbpedia-graph/blob/master/src/main/resources/redirect.properties)
- A sample properties file is given by [graphdb.properties](https://github.com/bernhardschaefer/dbpedia-graph/blob/master/src/main/resources/graphdb.properties)

## Create a DBpedia graph from DBpedia datasets
1. Configure ```graph.*```, ```blueprints.*```, and ```loading.*``` related properties in the configuration file.
  - TODO explain properties
2. [Build](#build) a single jar with dependencies. 
3. Run DBpediaGraphLoader class. As arguments multiple DBpedia datasets or directories containing DBpedia datasets are allowed.

Exemplary command for running DBpediaGraphLoader:

```
java \
 -Xmx18G \
 -cp target/dbpedia-graphdb-0.0.1-SNAPSHOT-jar-with-dependencies.jar \
 de.unima.dws.dbpediagraph.loader.DBpediaGraphLoader \
 /data/dbpedia/dbpedia3.9/en/article_categories_en.nt \
 /data/dbpedia/dbpedia3.9/en/instance_types_en.nt \
 /data/dbpedia/dbpedia3.9/en/mappingbased_properties_en.nt \
 /data/dbpedia/dbpedia3.9/en/persondata_en.nt \
 /data/dbpedia/dbpedia3.9/en/redirects_en.nt \
 /data/dbpedia/dbpedia3.9/en/skos_categories_en.nt \
 /data/dbpedia/dbpedia3.9/en/topical_concepts_en.nt \
```

The following DBpedia datasets are considered useful for disambiguation:

- Category: ```article_categories_en.nt, skos_categories_en.nt, topical_concepts_en.nt```
- Infobox: ```instance_types_en.nt (Ontology), mappingbased_properties_en.nt, persondata_en.nt```
- Redirects (DBpedia Spotlight uses them for spotting & candidate generation): ```redirects_en.nt```

## DBpedia Spotlight Integration
- TODO

## Disambiguation
- TODO configuration, algorithms, etc. 
