# Kafka_Consumer

Initial Version

Kafka Consumer project is a framework built using Spring Boot Kafka, which can be used to read from any Event Hubs for a given Topic and Group Id.

Setup: To run locally
To consume data from a specific bootstrap server one needs to configure the application.yml file for the configurations to run the application.
Follow below steps to run the application locally.

1. Clone:
    - Clone the project from
    
2. Configure:
    a. Spring properties:
      - server.port: The port on which the application to run
      - management.endpoints.web.exposure.include/exclude: This property lists the IDs of the endpoints that are exposed/not exposed
      - info.tag.environment: (dev/stage/prod) Environment on which the application will run
      - spring.profile.active: The current active profile
      - spring.jmx.enabled: (true/false) All of the Actuator endpoints are exposed - Default is true(enabled)
      - spring.boot.admin.client.url: Admin server's base URL
      - spring.security.user.name/ password: user name and password to login to the application to access the endpoints.
          The endpoints that needs credentials are specified in SecurityPermitAllConfig.java file as ".authenticated"
          ex.,: For the endpoint "/offset", need to specify as below
              http.authorizeRequests()
                .antMatchers("/offset").authenticated().and()
              .formLogin()
              .loginPage("/login")
              .permitAll()
      - spring.main.banner-mode: (on/off) To enable or desable the spring banner
      - spring.main.allow-bean-definition-overriding: (true/false) bean overriding allowed or not.
      - spring.kafka.properties.sasl.login.callback.handler.class:  authentication and data security haneler class. Here we use Service principal                                             authentication call back handler class(ServicePrincipalAuthCallback.java)
      - spring.profile: spring profile for different environments/servers

    b. Kafka Properties:
      - bootstrap-servers: Event Hubs/servers from where we need to consume data
      - properties.security.protocol: SASL_SSL security protocol is used in this project
      - properties.sasl.mechanism: OAUTHBEARER mechanism is used in this project 
      - sasl.login.callback.handler.class: Authentication and data security haneler class. Here we use Service principal authentication call back handler class                               (ServicePrincipalAuthCallback.java)
      - sasl.jaas.config: Java Authentication and Authorization Service configurations. Here we use OAuthBearerLoginModule

    c. Kafka Consumer Properties:
      - enable-auto-commit: (true/false) enabe or disable for consumer to automatically commit offsets periodically
      - auto-offset-reset: (earliest/latest) reset the offset position to earliest or latest
      - topics: list of topics from which the data to consume
      - concurrency: Number of parallel processing with no effect
      - groupId: Uniquely identifies the group of consumer processes to which this consumer belongs
      - retry: retry count to retry consumption if any failover
      - isolation.level: (read_committed/read_uncommitted) To read data from committed/uncommitted transactions.
      - max.poll.records: maximum number of records to be returned
      - partition.assignment.strategy: Strategy that will be used to assign the partition. Here we use RoundRobinAssignor strategy
      - listener.type: (single/batch) Listener type single or batch mode
      - listerner.ack-mode: commit behaviour(RECORD, BATCH, TIME, COUNT etc.,)

    d. ServicePrincipalAuthCallback:
      - Replace service principal id ("<<replace_with_sp_id>>") and service principal secret("<<replace_with_sp_secret>>") in ServicePrincipalAuthCallback.java file with the values

3. Build and Run the application:
    - cd Kafka_Consumer
    - mvn spring-boot:run -Dspring-boot.run.arguments="--env=<ENV> --topics=<TOPICS> --concurrency=<CONCURRENCY> --groupId=<GROUP_ID> --retry=<RETRY> --bootstrap-servers=                   <BOOTSTRAP_SERVERS> --enable-auto-commit=<TRUE_OR_FALSE> --auto-offset-reset=<LATEST_OR_EARLIEST> --isolation.level=o<READCOMMITTED_READUNCOMMITTED> --max.poll.records=               <MAX_POLL_RECORDS> --user=<USER> --password=<PASSWORD> --spring.profiles.active=<ACTIVE_PROFILE>"
    - OR add above command in Goal field of maven run configuration in editor(eclipse) and run
  
  Note:
    * Please replace the place holders in application.yml file with the values OR
    * Pass the values by replacing the placeholders in mvn spring-boot:run command
    * Here we have two profiles primary and secondary(for DR). So we can configure primar and secondary properties in application.yml file and pass --spring.profiles.active as             primary or secondary when running the application to pic respective profile configurations

The features that Kafka Consumer provides are:

1. Set offset for a specific Date and Time using a REST API http://localhost:8081/offset for a spcific Topic and Group Id
2. App Insight Metrics
3. Error Handling (Retry & Recovery)
  
  To test Set offset
  a. Click http://localhost:8081/offset after running the application
  b. Use username/password of spring security to login
  c. Pic specific date and time from datepicker for Offset Date field
     NOTE: Timestamp should be in GMT. (We can see Current running GMT on the same page for reference)
  d. Enter Topic Name and Group Id for which the offset has to set 
  e. And click on Set Offset.
