FROM elasticsearch:8.11.3

# Disable security to match your docker-compose config
ENV discovery.type=single-node
ENV xpack.security.enabled=false
ENV xpack.security.http.ssl.enabled=false
ENV ES_JAVA_OPTS="-Xms512m -Xmx512m"
ENV network.host=0.0.0.0
