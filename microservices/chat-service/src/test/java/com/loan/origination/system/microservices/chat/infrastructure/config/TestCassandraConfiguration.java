package com.loan.origination.system.microservices.chat.infrastructure.config;

import org.springframework.ai.chat.memory.repository.cassandra.CassandraChatMemoryRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import static org.mockito.Mockito.mock;

/**
 * Test-only Spring configuration that provides a mock {@link CassandraChatMemoryRepository}.
 *
 * <p>Activated only when the {@code test} profile is active. This allows
 * {@link com.loan.origination.system.microservices.chat.infrastructure.input.rest.ChatControllerIT}
 * (and other {@code @SpringBootTest} tests that use the {@code test} profile) to load the full
 * application context without requiring a live Cassandra cluster. The Cassandra and Spring AI
 * Cassandra auto-configurations are excluded via {@code application-test.yml}.
 *
 * <p>The mock satisfies the constructor dependency in
 * {@link BeanConfiguration#chatMemory(CassandraChatMemoryRepository)}
 * so the {@code ChatMemory} and {@code ChatClient} beans can be created normally.
 */
@Configuration
@Profile("test")
public class TestCassandraConfiguration {

    /**
     * Provides a Mockito mock of {@link CassandraChatMemoryRepository} so that
     * {@link BeanConfiguration#chatMemory} can be wired without a real Cassandra connection.
     *
     * @return a no-op mock instance
     */
    @Bean
    public CassandraChatMemoryRepository cassandraChatMemoryRepository() {
        return mock(CassandraChatMemoryRepository.class);
    }
}
