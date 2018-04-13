package org.aitesting.microservices.configservice;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.cloud.config.server.test.ConfigServerTestUtils;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.ResponseEntity;
//import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.SocketUtils;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import java.io.IOException;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ConfigServiceApplication.class, properties = { "spring.cloud.config.enabled:true",
        "management.security.enabled=false",
        "management.endpoints.web.exposure.include=*" }, webEnvironment = RANDOM_PORT)
public class ConfigServiceApplicationTests {

    private static int configPort = SocketUtils.findAvailableTcpPort();

    @LocalServerPort
    private int port;

    private static ConfigurableApplicationContext server;

    //TODO: Test with local repository
    @BeforeClass
    public static void startConfigServer() throws IOException {
        String baseDir = ConfigServerTestUtils.getBaseDirectory("/");
        String repo = "https://github.com/AITestingOrg/configuration-repository/";
        server = SpringApplication.run(org.springframework.cloud.config.server.ConfigServerApplication.class,
                "--server.port=" + configPort, "--spring.config.name=config-service",
                "--spring.cloud.config.server.git.uri=" + repo);
        System.setProperty("config.port", "" + configPort);
    }

    @AfterClass
    public static void close() {
        System.clearProperty("config.port");
        if (server != null) {
            server.close();
        }
    }

    @Test
    public void contextLoads() {
    }

    @Test
    public void retrieveConfigFileUserService_S001() {
        Map res = new TestRestTemplate().getForObject("http://localhost:" + port + "/user-service/dev", Map.class);
        assertThat(res).containsKey("propertySources");
        String property = (String) res.get("name");
        assertThat(property).contains("user-service");
    }

    @Test
    public void launchService_R001() {
        try {
            new SpringApplicationBuilder().sources(ConfigServiceApplication.class).run("--server.port=0",
                    "--spring.cloud.config.enabled=true", "--spring.cloud.config.fail-fast=true",
                    "--spring.cloud.config.uri=http://server-host-doesnt-exist:1234");
            fail("failFast option did not produce an exception");
        } catch (Exception e) {
            assertTrue("Exception not caused by fail fast", e.getMessage().contains("fail fast"));
        }
    }

    public static void main(String[] args) throws IOException {
        configPort = 8888;
        startConfigServer();
        SpringApplication.run(ConfigServiceApplication.class, args);
    }

}
