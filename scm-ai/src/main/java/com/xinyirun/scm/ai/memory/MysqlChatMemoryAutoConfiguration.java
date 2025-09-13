/**
 * MySQL聊天记忆仓库的自动配置类，用于Spring AI集成
 */
package com.xinyirun.scm.ai.memory;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

import lombok.extern.slf4j.Slf4j;

import jakarta.activation.DataSource;

/**
 * Auto-configuration for MySQL chat memory repository.
 */
@Slf4j
@AutoConfiguration(after = JdbcTemplateAutoConfiguration.class)
@ConditionalOnClass({ MysqlChatMemoryRepository.class, DataSource.class, JdbcTemplate.class })
@ConditionalOnProperty(prefix = "spring.ai.memory.mysql", name = "enabled", havingValue = "true",
		matchIfMissing = false)
@EnableConfigurationProperties(MysqlChatMemoryProperties.class)
public class MysqlChatMemoryAutoConfiguration {

	@Bean
	@Qualifier("mysqlChatMemoryRepository")
	@ConditionalOnMissingBean(name = "mysqlChatMemoryRepository")
	MysqlChatMemoryRepository mysqlChatMemoryRepository(JdbcTemplate jdbcTemplate) {
		log.info("Configuring MySQL chat memory repository");
		return MysqlChatMemoryRepository.mysqlBuilder().jdbcTemplate(jdbcTemplate).build();
	}

}