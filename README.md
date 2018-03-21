# Configuration Service

The Configuration Service pulls the configuration files from the repo:
https://github.com/AITestingOrg/configuration-repository

The configuration files should be stored with the next structure:
/{application}/{application}-{profile}.yml

The {application} should match spring.application.name
The {profile} should match sping.profiles.actives

In the gradle.build file the next dependency should be present:

```java
compile('org.springframework.cloud:spring-cloud-starter-config')
```

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

The default configuration port is 8888.
