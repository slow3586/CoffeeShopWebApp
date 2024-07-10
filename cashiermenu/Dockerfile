FROM amazoncorretto:22-alpine
VOLUME /tmp
COPY target/*.jar app.jar
EXPOSE 8080
ENV server.ssl.enabled=false
ENV server.port=8080
ENV spring.web.resources.chain.cache=true
ENV spring.web.resources.cache.cachecontrol.no-store=false
ENV spring.web.resources.cache.cachecontrol.no-cache=false
ENV spring.devtools.livereload.enabled=false
ENTRYPOINT ["java","-jar","/app.jar"]