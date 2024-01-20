# Kafka Tool

### TODO
>Write clean and well-structured code to enable anyone from any domain to pick up project.

* Filter SQL using ‘LIKE %’ instead of filtering strings in kotlin
* Load secrets from Keycloak
* Implement job to have DB purge entries older than 7/10 days
* Add authentication – login with id and password
* Add .sh scripts for Windows users
* Add extensive testing - Wiremock

## WHAT?

RESTful Ktor web app that allows users to trace, filter, and produce Kafka events. 

## WHY?

We want to see what is actually on the Kafka queues. Currently events on queues are being processed and are also encrypted.
This allows us to inspect queues for troubleshooting purposes as well as to analyse how events should be processed.
We want to see that we can publish to, as well as subscribe from queues in a non-disruptive way (not affecting existing consumers/producers).

### Pre-Requisites
Make sure you have your hosts file set-up correctly (should contain kafka1)
Should have something like this in there 
127.0.0.1 kubernetes.docker.internal kafka1 
https://www.imore.com/how-edit-your-macs-hosts-file-and-why-you-would-want#how-to-edit-the-hosts-file 

### How to run

Once you have followed one of the steps below and the application is running, you can head to
```http://localhost:9080/topics/<topicname>``` (where ```topicName``` is the topic that you want to inspect)
in your browser to use the Web UI.

#### In Docker (preferred - need Docker Desktop running in background):

1) running local Kafka instance (E0):
```bash
$ docker-compose -f docker-compose.yml -f docker-compose.kafka.yml up -d --build
```

2) running remote Kafka instance (E1):
```bash
$ docker-compose -f docker-compose.yml -f docker-compose.e1.yml up -d --build
```
---
#### On local machine:

1) running local Kafka instance (E0): need docker desktop running in background
```bash
$ ./rune0.sh
```
This command is for unix-like environments only. In order to run this command on Windows machines you will
need to convert the contents of the ```rune0.sh``` file into a script compatible with Windows-like environments.

2) running remote Kafka instance (E1): 
```bash
$ ./rune1.sh
```

---
#### Inside IntelliJ:

Create two run configurations, one for ```Application.kt``` inside the ```kafkatool-web``` subproject and another one for ```Application.kt``` inside the ```kafkatool-restapi``` subproject.
For both run configurations add the following settings:
* Program arguments: ```-config=application.dev.conf```
* Working directory: ```$MODULE_WORKING_DIR$```
* Environment variables: ```apigeeClientId=...``` (any env variables required to run the app separated by comma)

### How to listen from or produce to ANY given topic

Inside the ```TopicConfigs.kt``` file in the ```kafkatool-shared``` subproject add the topic configuration that you would like to read from or produce to.
Following this, in order to produce to a given topic, you will need to edit the ```MessageFixture.kt``` file in the ```kafkatool-web``` subproject and add the correct structure for the message that you would like to produce. Note that in the Web UI you will be able to alter any field value before sending an event.