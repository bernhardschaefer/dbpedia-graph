# -Xmx18G \
java \
 -cp target/dbpedia-graphdb-0.0.1-SNAPSHOT-jar-with-dependencies.jar \
 de.unima.dws.dbpediagraph.loader.DBpediaGraphLoader \
 /data/dbpedia/dbpedia3.9/en/article_categories_en.nt \
 /data/dbpedia/dbpedia3.9/en/instance_types_en.nt \
 /data/dbpedia/dbpedia3.9/en/mappingbased_properties_en.nt \
 /data/dbpedia/dbpedia3.9/en/persondata_en.nt \
 /data/dbpedia/dbpedia3.9/en/redirects_en.nt \
 /data/dbpedia/dbpedia3.9/en/skos_categories_en.nt \
 /data/dbpedia/dbpedia3.9/en/topical_concepts_en.nt \
