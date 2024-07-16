package com.bombombom.devs.external.config;

import com.bombombom.devs.book.repository.BookElasticsearchCustomRepository;
import com.bombombom.devs.book.repository.BookElasticsearchRepository;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.images.builder.ImageFromDockerfile;

@TestConfiguration
@EnableElasticsearchRepositories(basePackageClasses = {
    BookElasticsearchRepository.class,
    BookElasticsearchCustomRepository.class
})
public class ElasticsearchTestConfig extends ElasticsearchConfiguration {

    private static final GenericContainer<?> container;

    static {
        container = new GenericContainer<>(
            new ImageFromDockerfile().withDockerfileFromBuilder(b -> {
                b.from("docker.elastic.co/elasticsearch/elasticsearch:8.13.4").build();
            })
        ).withExposedPorts(9200)
            .withEnv("node.name", "es-test")
            .withEnv("discovery.type", "single-node")
            .withEnv("xpack.security.enabled", "false")
            .withEnv("xpack.security.http.ssl.enabled", "false")
            .withEnv("xpack.security.http.ssl.verification_mode", "certificate")
            .withEnv("xpack.security.transport.ssl.enabled", "false")
            .withEnv("xpack.security.transport.ssl.verification_mode", "certificate")
            .withEnv("xpack.license.self_generated.type", "basic")
            .withEnv("ES_JAVA_OPTS", "-Xms512m -Xmx512m");
        container.start();
    }

    @Override
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
            .connectedTo(container.getHost() + ":" + container.getMappedPort(9200))
            .build();
    }
}
