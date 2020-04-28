package com.baeldung.lsso;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.baeldung.lsso.web.model.ProjectModel;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ClientCredentialsFlowLiveTest {

    @Value("${resourceserver.api.project.url:http://localhost:8081/lsso-resource-server/api/projects/}")
    private String projectApiUrl;

    @Autowired
    private WebClient webClient;

    @BeforeAll
    public static void clearLogList() {
        LoggerListAppender.clearEventList();
    }

    @Test
    public void givenClientCredentialsGrantProperties_whenUseToken_thenSuccess() {
        List<ProjectModel> retrievedProjects = webClient.get()
            .uri(projectApiUrl)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<List<ProjectModel>>() {})
            .block();

        assertThat(3).isEqualTo(retrievedProjects.size());
        assertThat(LoggerListAppender.getEvents()).haveAtLeastOne(eventContains("Projects in the repository: 3"));
    }


    private Condition<ILoggingEvent> eventContains(String substring) {
        return new Condition<ILoggingEvent>(entry -> (substring == null || (entry.getFormattedMessage() != null && entry.getFormattedMessage().contains(substring))), String.format("entry with message '%s'", substring));
    }
}