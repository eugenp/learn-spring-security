package com.baeldung.lsso.scheduler;

import com.baeldung.lsso.web.model.ProjectModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
public class ProjectRetrieveScheduler {

    @Value("${resourceserver.api.project.url:http://localhost:8081/lsso-resource-server/api/projects/}")
    private String projectApiUrl;

    private static final Logger LOG = LoggerFactory.getLogger(ProjectRetrieveScheduler.class);

    private WebClient webClient;

    @Autowired
    public ProjectRetrieveScheduler(WebClient webClient) {
        this.webClient = webClient;
    }

    @Scheduled(fixedRate = 20000)
    public void scheduleResourceRetrieval() {
        List<ProjectModel> retrievedProjects = webClient.get()
            .uri(projectApiUrl)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<List<ProjectModel>>() {})
            .block();

        LOG.info("Projects in the repository: " + retrievedProjects.size());
    }
}
