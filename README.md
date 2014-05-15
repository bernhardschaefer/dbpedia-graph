## DBpedia Graph Project
- This project exploits DBpedia for disambiguating ambiguous entities and linking them to Wikipedia.
- To this end a DBpedia graph is created and used for disambiguation, thereby adapting the method of Navigli & Lapata (2010) ([paper link](http://ieeexplore.ieee.org/xpls/abs_all.jsp?arnumber=4782967))
- It is developed as a maven project and integrated into a [DBpedia Spotlight fork](https://github.com/bernhardschaefer/dbpedia-spotlight) (see [DBpedia Spotlight Fork](#dbpedia-spotlight-fork) section for more details).

### Build
- Execute maven as follows; this creates a jar with all dependencies in the ```/target``` folder

```
mvn compile assembly:single
```

### Configuration
- Configuration for creating a graph and for disambiguation is done using a properties file
- The path to the actual properties file needs to be configured in [redirect.properties](https://github.com/bernhardschaefer/dbpedia-graph/blob/master/src/main/resources/redirect.properties)
- A sample properties file, which explains each property, is given by [graphdb.properties](https://github.com/bernhardschaefer/dbpedia-graph/blob/master/src/main/resources/graphdb.properties).
- The property file is reread at each request if it was changed.

### Create a DBpedia graph from DBpedia datasets
1. Configure ```graph.*```, ```blueprints.*```, and ```loading.*``` related properties in the configuration file
2. [Build](#build) a single jar with dependencies. 
3. Run DBpediaGraphLoader class. As arguments multiple DBpedia datasets or directories containing DBpedia datasets are allowed.

Exemplary command for running DBpediaGraphLoader:

```
java \
-Xmx20G \
-Dlog4j.configuration=file:///data/spotlight/git/dbpedia-graph/src/test/resources/log4j.xml \
-cp target/dbpedia-graph-0.0.1-SNAPSHOT-jar-with-dependencies.jar \
de.unima.dws.dbpediagraph.loader.DBpediaGraphLoader \
/data/dbpedia/dbpedia3.9/en/article_categories_en.nt \
/data/dbpedia/dbpedia3.9/en/skos_categories_en.nt \
/data/dbpedia/dbpedia3.9/en/topical_concepts_en.nt \
/data/dbpedia/dbpedia3.9/en/instance_types_en.nt \
/data/dbpedia/dbpedia3.9/en/mappingbased_properties_en.nt \
/data/dbpedia/dbpedia3.9/en/persondata_en.nt \
/data/dbpedia/dbpedia3.9/en/redirects_en.nt \
```

The following DBpedia datasets are considered useful for disambiguation:

- Category: ```article_categories_en.nt, skos_categories_en.nt, topical_concepts_en.nt```
- Infobox: ```instance_types_en.nt (Ontology), mappingbased_properties_en.nt, persondata_en.nt```
- Redirects (DBpedia Spotlight uses them for spotting & candidate generation): ```redirects_en.nt```

## DBpedia Spotlight Fork
- The DBpedia Graph project is integrated into a [DBpedia Spotlight fork](https://github.com/bernhardschaefer/dbpedia-spotlight).
- Here, the branch [v0.6](https://github.com/bernhardschaefer/dbpedia-spotlight/tree/v0.6) contains all the modified code based on the DBpedia Spotlight version 0.6. This branch needs to be updated at some point to contain the Spotlight master code changes (see [TODO](#todo)).
- Integration with Spotlight is needed because the DBpedia Graph is only used for disambiguation and does not perform spotting or generation of candidate entities.
- The code changes are done in the core module of DBpedia Spotlight. The affected classes are described in the [core module](#core-module-code-changes) section.

### Core Module Code Changes
- [DBGraphDisambiguator](https://github.com/bernhardschaefer/dbpedia-spotlight/blob/v0.6/core/src/main/scala/org/dbpedia/spotlight/graphdb/DBGraphDisambiguator.scala): Interface between Spotlight and DBpedia Graph project.
  - Generates candidate entities for the document.
  - Converts forth and back between Spotlight and DBpedia Graph model.
  - Prunes candidate set of entities using [CandidateFilter](https://github.com/bernhardschaefer/dbpedia-graph/blob/master/src/main/java/de/unima/dws/dbpediagraph/subgraph/CandidateFilter.java).
  - Creates subgraph using implementation of [SubgraphConstruction](https://github.com/bernhardschaefer/dbpedia-graph/blob/master/src/main/java/de/unima/dws/dbpediagraph/subgraph/SubgraphConstruction.java).
  - Disambiguates bestK entities using implementation of [GraphDisambiguator](https://github.com/bernhardschaefer/dbpedia-graph/blob/master/src/main/java/de/unima/dws/dbpediagraph/disambiguate/GraphDisambiguator.java).
- [DBMergedDisambiguator](https://github.com/bernhardschaefer/dbpedia-spotlight/blob/v0.6/core/src/main/scala/org/dbpedia/spotlight/graphdb/DBMergedDisambiguator.scala): Federated disambiguator that combines DBpedia Graph and Spotlight disambiguation.
  - Two feature combination approach: Combines the bestK lists of entities of Spotlight and DBpedia Graph. Treats Spotlight as a black-box system.
  - Four feature combination approach: Combines the scores of all 3 Spotlight features with the DBpedia graph scores for the bestK entities into a final score.
- [SpotlightConfiguration](https://github.com/bernhardschaefer/dbpedia-spotlight/blob/v0.6/core/src/main/java/org/dbpedia/spotlight/model/SpotlightConfiguration.java): Added GraphBased and Merged disambiguation policies to the DisambiguationPolicy enum in line [61](https://github.com/bernhardschaefer/dbpedia-spotlight/blob/v0.6/core/src/main/java/org/dbpedia/spotlight/model/SpotlightConfiguration.java#L61).
- [SpotlightModel](https://github.com/bernhardschaefer/dbpedia-spotlight/blob/v0.6/core/src/main/scala/org/dbpedia/spotlight/db/SpotlightModel.scala): Added GraphBased and Merged Disambiguators to the statistical system and mapped them to the respective disambiguator classes.
- [pom.xml](https://github.com/bernhardschaefer/dbpedia-spotlight/blob/master/core/pom.xml): DBpedia Graph project dependency (line [247](https://github.com/bernhardschaefer/dbpedia-spotlight/blob/master/core/pom.xml#L247)).

### Run
1. Install DBpedia Graph project into local maven repository using ```mvn install``` (make sure you have created a graph and configured the properties file).
2. Package the Spotlight fork using ```mvn package```. This creates a jar with dependencies in the ```dist/target/``` folder.
3. Download and extract the English Spotlight model ```en.tar.gz``` for version 0.6 from the [Downloads Page](http://spotlight.sztaki.hu/downloads/raw).
4. Run the Spotlight fork jar.

Exemplary command for running the Spotlight fat jar:

```
java -Xmx20G -jar dist/target/dbpedia-spotlight-0.6-jar-with-dependencies.jar /path/to/your/spotlight/model/directory/ http://localhost:2222/rest
```

## Spotlight Demo
- For running the [Spotlight demo web application](http://dbpedia-spotlight.github.io/demo/) with DBpedia Graph, the [demo fork](https://github.com/bernhardschaefer/demo) can be used.
- The demo fork allows to choose the GraphBased or Merged disambiguator on the demo page.
- To run it, serve the files from an http server like apache.
- For a quick demo, the python built-in http server module can be run from the demo directory using: ```python -m SimpleHTTPServer```.

## TODO
- ```//TODO```'s in code
- Sync branch v0.6 with the Spotlight master
- Big TODO: Separate DBpedia Graph and Spotlight project so that both communicate using HTTP.
- ...
