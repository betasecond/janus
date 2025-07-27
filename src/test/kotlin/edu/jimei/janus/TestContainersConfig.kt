package edu.jimei.janus

import edu.jimei.janus.application.service.EmbeddingService
import org.mockito.kotlin.mock
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName

private val postgresContainer: PostgreSQLContainer<*> = PostgreSQLContainer(DockerImageName.parse("postgres:16-alpine"))
    .withDatabaseName("testdb")
    .withUsername("testuser")
    .withPassword("testpass")
    .withReuse(true)

private val redisContainer: GenericContainer<*> = GenericContainer(DockerImageName.parse("redis/redis-stack:latest"))
    .withExposedPorts(6379)
    .withReuse(true)

@TestConfiguration
class TestServiceConfiguration {

}


class TestContainersInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
    override fun initialize(applicationContext: ConfigurableApplicationContext) {
        if (!postgresContainer.isRunning) {
            postgresContainer.start()
        }
        if (!redisContainer.isRunning) {
            redisContainer.start()
        }
        TestPropertyValues.of(
            "spring.datasource.url=${postgresContainer.jdbcUrl}",
            "spring.datasource.username=${postgresContainer.username}",
            "spring.datasource.password=${postgresContainer.password}",
            "spring.datasource.driver-class-name=org.postgresql.Driver",
            "spring.flyway.enabled=true",
            "spring.jpa.hibernate.ddl-auto=validate",
            "spring.data.redis.host=${redisContainer.host}",
            "spring.data.redis.port=${redisContainer.getMappedPort(6379)}",
            "spring.ai.vectorstore.redis.index-name=test-index",

        ).applyTo(applicationContext.environment)
    }
}
