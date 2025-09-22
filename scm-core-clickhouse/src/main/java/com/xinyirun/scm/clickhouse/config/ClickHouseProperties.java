package com.xinyirun.scm.clickhouse.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * ClickHouse Client V2 配置属性类
 * 完全匹配 application-dev.yml 中的 scm.clickhouse 配置
 * 支持 kebab-case 到 camelCase 的自动映射
 * 
 * @author SCM System
 * @since 1.0.39
 */
@Component
@ConfigurationProperties(prefix = "scm.clickhouse")
public class ClickHouseProperties {

    /**
     * 是否启用ClickHouse模块
     * 对应 YAML: enabled
     */
    private boolean enabled = false;

    /**
     * ClickHouse HTTP端点列表 (Client V2使用HTTP协议)
     * 对应 YAML: endpoints (List)
     */
    private List<String> endpoints = List.of("http://127.0.0.1:8123");

    /**
     * 默认数据库名称
     * 对应 YAML: database
     */
    private String database = "";

    /**
     * 用户名 (YAML将覆盖此默认值为 "app")
     * 对应 YAML: username
     */
    private String username = "";

    /**
     * 密码 (YAML将覆盖此默认值为 "app_password")
     * 对应 YAML: password
     */
    private String password = "";

    /**
     * 是否启用服务器响应压缩
     * 对应 YAML: compress-server-response
     */
    private boolean compressServerResponse = true;

    /**
     * 是否启用客户端请求压缩
     * 对应 YAML: compress-client-request
     */
    private boolean compressClientRequest = false;

    /**
     * 客户端配置
     * 对应 YAML: client.*
     */
    private ClientConfig client = new ClientConfig();

    /**
     * 超时配置
     * 对应 YAML: timeout.*
     */
    private Timeout timeout = new Timeout();

    /**
     * 重试配置
     * 对应 YAML: retry.*
     */
    private Retry retry = new Retry();

    /**
     * 监控配置
     * 对应 YAML: monitoring.*
     */
    private Monitoring monitoring = new Monitoring();

    /**
     * 性能配置
     * 对应 YAML: performance.*
     */
    private Performance performance = new Performance();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
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

    public List<String> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(List<String> endpoints) {
        this.endpoints = endpoints;
    }

    public boolean isCompressServerResponse() {
        return compressServerResponse;
    }

    public void setCompressServerResponse(boolean compressServerResponse) {
        this.compressServerResponse = compressServerResponse;
    }

    public boolean isCompressClientRequest() {
        return compressClientRequest;
    }

    public void setCompressClientRequest(boolean compressClientRequest) {
        this.compressClientRequest = compressClientRequest;
    }

    public ClientConfig getClient() {
        return client;
    }

    public void setClient(ClientConfig client) {
        this.client = client;
    }

    public Timeout getTimeout() {
        return timeout;
    }

    public void setTimeout(Timeout timeout) {
        this.timeout = timeout;
    }

    public Retry getRetry() {
        return retry;
    }

    public void setRetry(Retry retry) {
        this.retry = retry;
    }

    public Monitoring getMonitoring() {
        return monitoring;
    }

    public void setMonitoring(Monitoring monitoring) {
        this.monitoring = monitoring;
    }

    public Performance getPerformance() {
        return performance;
    }

    public void setPerformance(Performance performance) {
        this.performance = performance;
    }

    /**
     * 获取主端点 (第一个端点)
     */
    public String getPrimaryEndpoint() {
        return endpoints.isEmpty() ? "http://127.0.0.1:8123" : endpoints.get(0);
    }

    /**
     * 客户端配置
     * 对应 YAML: client.*
     */
    public static class ClientConfig {
        /**
         * 缓冲区大小 - 对应 YAML: buffer-size
         */
        private int bufferSize = 8192;
        
        /**
         * 队列长度 - 对应 YAML: queue-length (0表示无限制)
         */
        private int queueLength = 0;
        
        /**
         * 数组中使用对象 - 对应 YAML: use-objects-in-array
         */
        private boolean useObjectsInArray = false;
        
        /**
         * 使用二进制字符串 - 对应 YAML: use-binary-string
         */
        private boolean useBinaryString = false;
        
        /**
         * 扩展无符号类型 - 对应 YAML: widen-unsigned-types
         */
        private boolean widenUnsignedTypes = false;

        public int getBufferSize() { return bufferSize; }
        public void setBufferSize(int bufferSize) { this.bufferSize = bufferSize; }

        public int getQueueLength() { return queueLength; }
        public void setQueueLength(int queueLength) { this.queueLength = queueLength; }

        public boolean isUseObjectsInArray() { return useObjectsInArray; }
        public void setUseObjectsInArray(boolean useObjectsInArray) { this.useObjectsInArray = useObjectsInArray; }

        public boolean isUseBinaryString() { return useBinaryString; }
        public void setUseBinaryString(boolean useBinaryString) { this.useBinaryString = useBinaryString; }

        public boolean isWidenUnsignedTypes() { return widenUnsignedTypes; }
        public void setWidenUnsignedTypes(boolean widenUnsignedTypes) { this.widenUnsignedTypes = widenUnsignedTypes; }
    }

    /**
     * 超时配置
     * 对应 YAML: timeout.*
     */
    public static class Timeout {
        /**
         * 连接超时(毫秒) - 对应 YAML: connection
         */
        private int connection = 30000;
        
        /**
         * 查询超时(毫秒) - 对应 YAML: query
         */
        private int query = 60000;
        
        /**
         * Socket超时(毫秒) - 对应 YAML: socket
         */
        private int socket = 60000;
        
        /**
         * 最大执行时间(秒) - 对应 YAML: max-execution-time
         */
        private int maxExecutionTime = 300;

        public int getConnection() { return connection; }
        public void setConnection(int connection) { this.connection = connection; }

        public int getQuery() { return query; }
        public void setQuery(int query) { this.query = query; }

        public int getSocket() { return socket; }
        public void setSocket(int socket) { this.socket = socket; }

        public int getMaxExecutionTime() { return maxExecutionTime; }
        public void setMaxExecutionTime(int maxExecutionTime) { this.maxExecutionTime = maxExecutionTime; }
    }

    /**
     * 重试配置
     */
    public static class Retry {
        private int maxAttempts = 3;
        private long delay = 1000;
        private double multiplier = 2.0;

        public int getMaxAttempts() { return maxAttempts; }
        public void setMaxAttempts(int maxAttempts) { this.maxAttempts = maxAttempts; }

        public long getDelay() { return delay; }
        public void setDelay(long delay) { this.delay = delay; }

        public double getMultiplier() { return multiplier; }
        public void setMultiplier(double multiplier) { this.multiplier = multiplier; }
    }

    /**
     * 监控配置
     */
    public static class Monitoring {
        private boolean healthCheckEnabled = true;
        private int healthCheckInterval = 30;
        private boolean metricsEnabled = true;

        public boolean isHealthCheckEnabled() { return healthCheckEnabled; }
        public void setHealthCheckEnabled(boolean healthCheckEnabled) { this.healthCheckEnabled = healthCheckEnabled; }

        public int getHealthCheckInterval() { return healthCheckInterval; }
        public void setHealthCheckInterval(int healthCheckInterval) { this.healthCheckInterval = healthCheckInterval; }

        public boolean isMetricsEnabled() { return metricsEnabled; }
        public void setMetricsEnabled(boolean metricsEnabled) { this.metricsEnabled = metricsEnabled; }
    }

    /**
     * 性能配置 - 基于ClickHouse Java v2最佳实践
     * 对应 YAML: performance.*
     */
    public static class Performance {
        /**
         * 最大连接数 - 对应 YAML: max-connections
         */
        private int maxConnections = 20;

        /**
         * LZ4解压缓冲区大小 (字节) - 对应 YAML: lz4-uncompressed-buffer-size
         */
        private int lz4UncompressedBufferSize = 1048576; // 1MB

        /**
         * Socket接收缓冲区大小 (字节) - 对应 YAML: socket-receive-buffer-size
         */
        private int socketReceiveBufferSize = 1000000; // 1MB

        /**
         * 客户端网络缓冲区大小 (字节) - 对应 YAML: client-network-buffer-size
         */
        private int clientNetworkBufferSize = 1000000; // 1MB

        /**
         * 是否预热连接池 - 对应 YAML: warmup-connections
         */
        private boolean warmupConnections = true;

        /**
         * 连接预热超时时间 (秒) - 对应 YAML: warmup-timeout-seconds
         */
        private int warmupTimeoutSeconds = 10;

        /**
         * 数据格式（推荐使用RowBinaryWithNamesAndTypes获得更好性能）
         * 对应 YAML: preferred-format
         */
        private String preferredFormat = "RowBinaryWithNamesAndTypes";

        public int getMaxConnections() { return maxConnections; }
        public void setMaxConnections(int maxConnections) { this.maxConnections = maxConnections; }

        public int getLz4UncompressedBufferSize() { return lz4UncompressedBufferSize; }
        public void setLz4UncompressedBufferSize(int lz4UncompressedBufferSize) { this.lz4UncompressedBufferSize = lz4UncompressedBufferSize; }

        public int getSocketReceiveBufferSize() { return socketReceiveBufferSize; }
        public void setSocketReceiveBufferSize(int socketReceiveBufferSize) { this.socketReceiveBufferSize = socketReceiveBufferSize; }

        public int getClientNetworkBufferSize() { return clientNetworkBufferSize; }
        public void setClientNetworkBufferSize(int clientNetworkBufferSize) { this.clientNetworkBufferSize = clientNetworkBufferSize; }

        public boolean isWarmupConnections() { return warmupConnections; }
        public void setWarmupConnections(boolean warmupConnections) { this.warmupConnections = warmupConnections; }

        public int getWarmupTimeoutSeconds() { return warmupTimeoutSeconds; }
        public void setWarmupTimeoutSeconds(int warmupTimeoutSeconds) { this.warmupTimeoutSeconds = warmupTimeoutSeconds; }

        public String getPreferredFormat() { return preferredFormat; }
        public void setPreferredFormat(String preferredFormat) { this.preferredFormat = preferredFormat; }
    }
}