To create new solr container with custom solrconfig.xml in docker run command:

docker run -d -v "data:/var/solr" -v $PWD/solrconfig.xml:/opt/solr/server/solr/configsets/_default/conf/solrconfig.xml -p 8983:8983 --name books-solr solr solr-precreate books

