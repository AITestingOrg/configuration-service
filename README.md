# Configuration Service
[![Build Status](https://travis-ci.org/AITestingOrg/configuration-service.svg?branch=master)](https://travis-ci.org/AITestingOrg/configuration-service)

The Configuration Service main purpuse is to distribute configuration files accross the microservices of this project.

This is the repository where the configuration files are hosted:
https://github.com/AITestingOrg/configuration-repository

## Features

  - Centralized configuration support
  - Keeps track of configuration files versions

## Open source being used

The Configuration Service uses a number of open source projects to work properly:

* [Spring Framework]
* [Gradle]
* [Docker]

## Running the service
The Configuration Service requires [Docker] v18+ to run.

Once Docker is installed you can build from source or use the predefined images available at [Docker Hub](https://hub.docker.com/u/aista/dashboard/)

### Building from source
Using gradle wrapper:
```sh
cd configuration-service
./gradlew clean build
docker-compose -f docker-compose-local.yml up --build
```
This will build the application and generate the jar file to be placed in a container and also run a personalized version of Eureka called discovery-service.

### Using Docker Hub images
The Configuration Service is very easy to run from the images on Docker Hub.

By default, Docker will expose port 8888. You can change this within the docker-compose.yml file if necessary.

```sh
cd configuration-service
docker-compose up
```

## Hosted configuration files

The configuration files should be stored with the next structure:
`/{application}/{application}-{profile}.yml`

The `{application}` should match `spring.application.name`
The `{profile}` should match `sping.profiles.active`

## Client configuration

In order for the Java Spring client applications use the configuration service, they need to have a bootstrap.yml file in the resources folder and some configurations in the gradle.build file.

##### bootstrap.yml
It also needs to be configured in the bootstrap.yml, as instructed below:

```yml
spring:
  application:
    name: {application}
  profiles:
    active: {profile}
  cloud:
    config:
      enabled: true
      uri: {configuration-service-uri}
```
##### build.gradle
The build.gradle file must contain the next configuration:
```java
ext {
    springCloudVersion = 'Edgware.SR2'
}
...
dependencies {
...
  compile('org.springframework.cloud:spring-cloud-starter-config')
...
}
...
dependencyManagement {
	imports {
		mavenBom "org.springframework.cloud:spring-cloud-dependencies:${springCloudVersion}"
	}
}
```

#### Example of bootstrap.yml

This is an example for user-service microservice bootstrap.yml:

```yml
spring:
  application:
    name: user-service
  profiles:
    active: development
  cloud:
    config:
      enabled: true
      uri: http://configuration-service
```

### Other client applications

Other client applications can retrieve the configuration file directly from the server's API like in this example:
```
curl localhost:8888/user-service/dev
```
[//]: # (Reference links)

   [Spring Framework]: <https://spring.io/>
   [Gradle]: <https://gradle.org/>
   [Docker]: <https://www.docker.com/>