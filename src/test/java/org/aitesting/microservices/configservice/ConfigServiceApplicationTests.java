package org.aitesting.microservices.configservice;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.SocketUtils;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ConfigServiceApplication.class)
@ActiveProfiles("test")
public class ConfigServiceApplicationTests {

    protected static final Logger LOG = LoggerFactory.getLogger(ConfigServiceApplication.class);
    private static int configPort = SocketUtils.findAvailableTcpPort();
    private static ConfigurableApplicationContext server;
    private static final String baseURI = "http://localhost:" + configPort;

    // TODO: Test with local repository
    @BeforeClass
    public static void tearUp() throws IOException {
        final String repo = "https://github.com/AITestingOrg/configuration-repository/";
        server = SpringApplication.run(org.springframework.cloud.config.server.ConfigServerApplication.class,
                "--server.port=" + configPort, "--spring.config.name=configurationservice",
                "--spring.cloud.config.server.git.uri=" + repo);
        System.setProperty("config.port", "" + configPort);
    }

    @AfterClass
    public static void tearDown() {
        System.clearProperty("config.port");
        if (server != null) {
            server.close();
        }
    }
    
    @Test
    public void retrieveConfigFileUserServiceSuccess() {
        Map res = new TestRestTemplate().getForObject(baseURI + "/userservice/dev", Map.class);
        assertThat(res).containsKey("propertySources");
        String property = (String) res.get("name");
        assertThat(property).contains("userservice");
    }

    @Test
    public void retrieveConfigFileTripCmdSuccess() {
        Map res = new TestRestTemplate().getForObject(baseURI + "/tripmanagementcmd/dev", Map.class);
        assertThat(res).containsKey("propertySources");
        String property = (String) res.get("name");
        assertThat(property).contains("tripmanagementcmd");
    }

    @Test
    public void launchServiceFail() {
        try {
            new SpringApplicationBuilder().sources(ConfigServiceApplication.class).run("--server.port=0",
                    "--spring.cloud.config.enabled=true", "--spring.cloud.config.fail-fast=true",
                    "--spring.cloud.config.uri=http://server-host-doesnt-exist:1234");
            fail("failFast option did not produce an exception");
        } catch (Exception e) {
            assertTrue("Exception not caused by fail fast", e.getMessage().contains("fail fast"));
        }
    }

}
