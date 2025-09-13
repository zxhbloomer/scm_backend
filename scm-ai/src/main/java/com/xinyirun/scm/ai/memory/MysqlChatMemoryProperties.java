/**
 * MySQL聊天记忆仓库的配置属性类
 */
package com.xinyirun.scm.ai.memory;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for MySQL chat memory repository.
 */
@ConfigurationProperties(MysqlChatMemoryProperties.CONFIG_PREFIX)
public class MysqlChatMemoryProperties {

	public static final String CONFIG_PREFIX = "spring.ai.chat.memory.repository.jdbc.mysql";

	private boolean initializeSchema = true;

	public boolean isInitializeSchema() {
		return this.initializeSchema;
	}

	public void setInitializeSchema(boolean initializeSchema) {
		this.initializeSchema = initializeSchema;
	}

	/**
	 * JDBC URL of the database.
	 */
	private String jdbcUrl;

	/**
	 * Database username.
	 */
	private String username;

	/**
	 * Database password.
	 */
	private String password;

	/**
	 * Fully qualified name of the JDBC driver class.
	 */
	private String driverClassName = "com.mysql.cj.jdbc.Driver";

	/**
	 * Whether to enable custom datasource configuration.
	 */
	private boolean enabled = false;

	public String getJdbcUrl() {
		return jdbcUrl;
	}

	public void setJdbcUrl(String jdbcUrl) {
		this.jdbcUrl = jdbcUrl;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDriverClassName() {
		return driverClassName;
	}

	public void setDriverClassName(String driverClassName) {
		this.driverClassName = driverClassName;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

}