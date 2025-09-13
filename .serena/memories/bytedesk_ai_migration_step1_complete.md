# ByteDesk AI模块到SCM系统迁移 - Step 1完成记录

## 项目概述
将ByteDesk系统的AI模块完整复制到SCM系统中，确保功能的完全迁移而非简化。

## Step 1: 基础设施层复制 ✅ 已完成

### 核心适配工作
- **包名适配**: `com.bytedesk.ai` → `com.xinyirun.scm.ai`
- **ORM适配**: JPA → MyBatis Plus 
- **表名适配**: `bytedesk_ai_*` → `scm_ai_*`
- **多租户适配**: 添加 `@DataSourceAnnotion(DataSourceTypeEnum.MASTER)`
- **实体继承**: 使用SCM的 `BaseEntity<T>` 基类

### 已完成文件清单

#### model/包 (3个文件)
1. **LlmModelEntity.java**
   - 路径: `scm-ai/src/main/java/com/xinyirun/scm/ai/model/LlmModelEntity.java`
   - 功能: AI模型实体类，支持13个字段的完整定义
   - 适配: 从JPA的@Entity转换为MyBatis Plus的@TableName
   - 表名: `scm_ai_model`

2. **LlmModelMapper.java** 
   - 路径: `scm-ai/src/main/java/com/xinyirun/scm/ai/model/LlmModelMapper.java`
   - 功能: 数据访问接口，包含11个自定义查询方法
   - 适配: 添加多租户注解 `@DataSourceAnnotion(DataSourceTypeEnum.MASTER)`
   - 方法: findByProviderUid, findByType, findAllEnabled等

3. **LlmModelTypeEnum.java**
   - 路径: `scm-ai/src/main/java/com/xinyirun/scm/ai/model/LlmModelTypeEnum.java`
   - 功能: 定义13种AI模型类型枚举
   - 类型: TEXT, EMBEDDING, VISION, AUDIO, VIDEO等

#### provider/包 (3个文件)
1. **LlmProviderEntity.java**
   - 路径: `scm-ai/src/main/java/com/xinyirun/scm/ai/provider/LlmProviderEntity.java`
   - 功能: AI提供商实体类，16个字段完整定义
   - 表名: `scm_ai_provider`
   - 业务方法: isProduction(), isDevelopment(), isConfigured()等

2. **LlmProviderMapper.java**
   - 路径: `scm-ai/src/main/java/com/xinyirun/scm/ai/provider/LlmProviderMapper.java`
   - 功能: 提供商数据访问接口，14个自定义查询方法
   - 适配: 添加多租户注解
   - 方法: findByName, findByProviderType, findSystemEnabled等

3. **LlmProviderStatusEnum.java**
   - 路径: `scm-ai/src/main/java/com/xinyirun/scm/ai/provider/LlmProviderStatusEnum.java`
   - 功能: 提供商状态枚举，5种状态
   - 状态: DEVELOPMENT, PRODUCTION, TEST, MAINTENANCE, DISABLED

#### springai/config/包 (4个文件)
1. **ChatClientPrimaryConfig.java**
   - 路径: `scm-ai/src/main/java/com/xinyirun/scm/ai/springai/config/ChatClientPrimaryConfig.java`
   - 功能: ChatClient主配置类，支持11个AI提供商
   - 提供商: Zhipu, OpenAI, Azure, Ollama, DashScope, Qianfan, Moonshot等

2. **ChatModelPrimaryConfig.java**
   - 路径: `scm-ai/src/main/java/com/xinyirun/scm/ai/springai/config/ChatModelPrimaryConfig.java`
   - 功能: ChatModel主配置类，与ChatClient并行支持
   - 配置: @Primary bean选择和条件创建

3. **ObservationConfig.java**
   - 路径: `scm-ai/src/main/java/com/xinyirun/scm/ai/springai/config/ObservationConfig.java`
   - 功能: Micrometer观察配置
   - 特性: 自定义ChatClient观察约定

4. **VectorStoreConfig.java**
   - 路径: `scm-ai/src/main/java/com/xinyirun/scm/ai/springai/config/VectorStoreConfig.java`
   - 功能: Elasticsearch向量存储配置
   - 条件: 依赖EmbeddingModel和Elasticsearch启用

#### 支撑文件 (2个文件)
1. **LlmConsts.java**
   - 路径: `scm-ai/src/main/java/com/xinyirun/scm/ai/constant/LlmConsts.java`
   - 功能: 22个AI提供商的常量定义
   - 内容: 默认配置、提供商名称、类型常量

2. **CustomChatClientObservationConvention.java**
   - 路径: `scm-ai/src/main/java/com/xinyirun/scm/ai/springai/observability/CustomChatClientObservationConvention.java`
   - 功能: 自定义聊天客户端观察约定
   - 适配: 命名空间从"bytedesk"改为"scm"

### 关键技术要点

#### MyBatis Plus适配
```java
// 从JPA的@Entity转换为MyBatis Plus
@TableName("scm_ai_model")
public class LlmModelEntity extends BaseEntity<LlmModelEntity> {
    @TableField("name")
    private String name;
    // ...其他字段
}
```

#### 多租户数据源支持
```java
@Mapper
@DataSourceAnnotion(DataSourceTypeEnum.MASTER)
public interface LlmModelMapper extends BaseMapper<LlmModelEntity> {
    // 查询方法
}
```

#### Spring AI配置适配
```java
@Configuration
public class ChatClientPrimaryConfig {
    @Bean
    @Primary
    @ConditionalOnProperty(prefix = "spring.ai.zhipu", name = "api-key")
    public ChatClient zhipuChatClient(ZhiPuAiChatModel chatModel) {
        return ChatClient.builder(chatModel).build();
    }
}
```

## 用户要求强调
- **完全复制**: "不要简化，要完全复制"
- **不要臆想**: "不要臆想，不要自说自话" 
- **听指挥**: 严格按照用户指令执行

## 下一步工作计划
- **Step 2**: 服务层复制 (`springai/service/`, `springai/providers/`, `robot/`)
- **Step 3**: 应用层复制 (`workflow/`, `mcp_server/`, `controller/`)
- **Step 4**: 支撑层复制 (`robot_message/`, `robot_thread/`, `utils/`)

## 工作时间记录
- 开始时间: 继续之前会话
- 完成时间: 当前
- 状态: Step 1 已完全完成，等待用户确认进入Step 2

## 质量保证
- 所有文件编译无语法错误
- 包名和导入语句正确适配
- 多租户注解已正确添加
- 数据库表名映射完成
- Spring Boot配置注解完整