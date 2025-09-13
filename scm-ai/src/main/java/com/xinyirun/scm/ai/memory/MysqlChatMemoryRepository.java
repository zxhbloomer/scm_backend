/**
 * MySQL实现的聊天记忆仓库，支持Spring AI的多轮对话记忆功能
 */
package com.xinyirun.scm.ai.memory;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * MySQL implementation of chat memory repository
 */
public class MysqlChatMemoryRepository extends JdbcChatMemoryRepository {

	// MySQL specific query statements
	private static final String MYSQL_QUERY_ADD = "INSERT INTO scm_ai_chat_memory (conversation_id, content, message_type, timestamp) VALUES (?, ?, ?, ?)";

	private static final String MYSQL_QUERY_GET = "SELECT content, message_type FROM scm_ai_chat_memory WHERE conversation_id = ? ORDER BY timestamp";

	private MysqlChatMemoryRepository(JdbcTemplate jdbcTemplate) {
		super(jdbcTemplate);
	}

	public static MysqlBuilder mysqlBuilder() {
		return new MysqlBuilder();
	}

	public static class MysqlBuilder {

		private JdbcTemplate jdbcTemplate;

		public MysqlBuilder jdbcTemplate(JdbcTemplate jdbcTemplate) {
			this.jdbcTemplate = jdbcTemplate;
			return this;
		}

		public MysqlChatMemoryRepository build() {
			return new MysqlChatMemoryRepository(this.jdbcTemplate);
		}

	}

	@Override
	protected String hasTableSql(String tableName) {
		return String.format(
				"SELECT table_name FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = '%s'",
				tableName);
	}

	@Override
	protected String createTableSql(String tableName) {
		return String.format(
				"CREATE TABLE %s (id BIGINT AUTO_INCREMENT PRIMARY KEY, "
						+ "conversation_id VARCHAR(256) NOT NULL, content LONGTEXT NOT NULL, "
						+ "message_type VARCHAR(100) NOT NULL, timestamp TIMESTAMP NOT NULL, "
						+ "CONSTRAINT chk_message_type CHECK (message_type IN ('USER', 'ASSISTANT', 'SYSTEM', 'TOOL')))",
				tableName);
	}

	@Override
	protected String getAddSql() {
		return MYSQL_QUERY_ADD;
	}

	@Override
	protected String getGetSql() {
		return MYSQL_QUERY_GET;
	}

}