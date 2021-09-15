# Importer

### Starting the application
To build and run the app use:
`docker-compose up --build`


Ensure that the exposed  ports are available. Initial build may take a few minutes.

Note: Starting point for the application is `MainVerticle.java`
### Design choices
- [Vert.x](https://vertx.io/) is asynchronous event driven application framework from Eclipse, used in the project. Concurrency model used in Vertx can handle resources much more efficiently.
- [Greyhound](https://github.com/wix/greyhound) is a kafka library from Wix which provides a higher-level interfaces to work with Kafka and to express richer semantics such as retry policies or  parallel message handling with ease.

### Technical Debts
- Employee get APIs/ methods were not yet implemented
- Error response from APIs needs to be standardised in JSON format
- Kafka metrics can be captured using hooks provided by Greyhound
- Distributed tracing can be achieved using open Tracing and [Jaeger](https://www.jaegertracing.io/)
- [Karate](https://github.com/intuit/karate) framework can be used for API automation testing

### Monitoring tools
#### KafDrop
KafDrop is added as part of the docker-compose file for monitoring kafka events.

http://localhost:9000

http://localhost:9000/topic/employee.v1.dev/messages?partition=0&offset=0&count=100&keyFormat=DEFAULT&format=DEFAULT

#### DB Adminier
DB records be checked in the following port.


http://localhost:8888/


user-name/pwd: root/root


database: dev



### Building with gradle

To launch your tests:
```
./gradlew clean test
```

To package your application:
```
./gradlew clean assemble
```

To run your application:
```
./gradlew clean run
```



