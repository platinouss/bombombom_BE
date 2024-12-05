package com.bombombom.devs.external.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
public class TestContainerConfig {

    private static final DockerImageName ELASTICSEARCH_IMAGE = DockerImageName.parse(
        "docker.elastic.co/elasticsearch/elasticsearch:8.13.4");
    private static final DockerImageName REDIS_IMAGE = DockerImageName.parse("redis:7.2.5-alpine");

    static final GenericContainer<?> redis;
    static final GenericContainer<?> elasticsearch;

    static {
        elasticsearch = new GenericContainer<>(ELASTICSEARCH_IMAGE).withExposedPorts(9200)
            .withEnv("node.name", "es-test")
            .withEnv("discovery.type", "single-node")
            .withEnv("xpack.security.enabled", "false")
            .withEnv("xpack.security.http.ssl.enabled", "false")
            .withEnv("xpack.security.http.ssl.verification_mode", "certificate")
            .withEnv("xpack.security.transport.ssl.enabled", "false")
            .withEnv("xpack.security.transport.ssl.verification_mode", "certificate")
            .withEnv("xpack.license.self_generated.type", "basic")
            .withEnv("ES_JAVA_OPTS", "-Xms512m -Xmx512m")
            .waitingFor(Wait.forHttp("/").forStatusCode(200));
        elasticsearch.start();
        redis = new GenericContainer<>(REDIS_IMAGE).withExposedPorts(6379).withReuse(true);
        redis.start();

        System.setProperty("spring.data.redis.host", redis.getHost());
        System.setProperty("spring.data.redis.port", redis.getMappedPort(6379).toString());
        System.setProperty("spring.elasticsearch.uris",
            elasticsearch.getHost() + ":" + elasticsearch.getMappedPort(9200));
    }
}
