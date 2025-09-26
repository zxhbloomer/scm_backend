# MeterSphere AI模型设置功能迁移到SCM-AI模块详细计划

## 需求重复
把MeterSphere项目中关于模型设置的前后台逻辑，全面的复制到SCM系统中（scm-ai模块中）。现在先处理后台端的逻辑，包括数据库、controller、service、mapper、SQL等。基于分析文档AI_Model_Complete_Inventory.md，详细设计按文件进行设计，不可以臆想，具体包保存到：
- Controller: `scm-ai\src\main\java\com\xinyirun\scm\ai\controller\model`
- Mapper: `scm-ai\src\main\java\com\xinyirun\scm\ai\core\mapper\model`（包含XML）
- Service: `scm-ai\src\main\java\com\xinyirun\scm\ai\core\service\model`

## 1. SCM-AI代码风格分析总结

### Domain层特点
- 使用@Data注解简化getter/setter
- Schema验证注解(@NotBlank, @Size, @NotNull等)
- MyBatis-Plus的Column枚举用于动态查询
- 中文注释和Schema描述，符合国际化需求
- 实现Serializable接口

### Service层特点
- @Service + @Transactional(rollbackFor = Exception.class)标准配置
- @Resource依赖注入（不使用@Autowired）
- 详细的中文Javadoc注释
- 完整的业务逻辑处理和异常处理
- 静态常量定义（如DEFAULT_OWNER）
- 私有方法提取复用逻辑

### Controller层特点
- @RestController + @RequestMapping路径映射
- @Tag、@Operation文档注解（OpenAPI 3.0）
- @DS("master")数据源注解用于读写分离
- SessionUtils.getUserId()获取当前用户信息
- @Validated + @RequestBody参数验证
- 返回具体业务对象，不使用统一Result包装

### Mapper层特点
- 继承MyBatis-Plus基础Mapper接口
- ExtMapper用于复杂查询和自定义SQL
- 配套XML文件存放复杂SQL
- MyBatis-Plus Example查询用于动态条件

## 2. MeterSphere AI模型功能分析

### 核心表结构：ai_model_source
```sql
CREATE TABLE ai_model_source (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(255) NOT NULL COMMENT '模型名称',
    type VARCHAR(50) NOT NULL COMMENT '模型类别（大语言/视觉/音频）',
    provider_name VARCHAR(255) NOT NULL COMMENT '模型供应商',
    permission_type VARCHAR(50) NOT NULL COMMENT '模型类型（公有/私有）',
    status BIT NOT NULL COMMENT '模型连接状态',
    owner VARCHAR(255) NOT NULL COMMENT '模型拥有者',
    owner_type VARCHAR(255) NOT NULL COMMENT '模型拥有者类型（个人/企业）',
    base_name VARCHAR(255) COMMENT '基础名称',
    model_type VARCHAR(50) NOT NULL COMMENT '模型类型',
    app_key VARCHAR(255) NOT NULL COMMENT '模型key',
    api_url VARCHAR(255) NOT NULL COMMENT '模型url',
    adv_settings VARCHAR(255) NOT NULL COMMENT '模型参数配置值',
    create_time BIGINT COMMENT '创建时间',
    create_user VARCHAR(50) COMMENT '创建人(操作人)',
    is_default BOOLEAN COMMENT '是否为默认模型',
    ai_config_id VARCHAR(50) COMMENT '配置ID'
);
```

### 核心功能点
1. **模型源管理**：增删改查AI模型配置
2. **模型验证**：验证模型连接有效性
3. **权限控制**：PUBLIC（系统级）/PRIVATE（个人级）
4. **多供应商支持**：OpenAI、DeepSeek、ZhiPu AI等
5. **高级参数配置**：temperature、top_p、max_tokens等
6. **AppKey加密**：显示时进行掩码处理

## 3. 数据库迁移策略

### 3.1 表结构适配SCM规范
SCM已存在ai_model_source表，需要对比字段差异：

**MeterSphere -> SCM字段映射：**
```sql
-- SCM现有字段保持不变，新增MeterSphere缺少的字段
ALTER TABLE ai_model_source ADD COLUMN IF NOT EXISTS model_type VARCHAR(50) COMMENT '模型类型';
ALTER TABLE ai_model_source ADD COLUMN IF NOT EXISTS base_name VARCHAR(255) COMMENT '基础名称';
ALTER TABLE ai_model_source ADD COLUMN IF NOT EXISTS ai_config_id VARCHAR(50) COMMENT '配置ID';
```

### 3.2 数据类型适配
- MeterSphere使用BIGINT存储create_time，SCM使用Long类型
- status字段：MeterSphere为BIT，SCM为Boolean
- 保持SCM现有字段定义不变

## 4. Controller层迁移方案

### 4.1 文件结构
```
scm-ai\src\main\java\com\xinyirun\scm\ai\controller\model\
├── SystemAIModelConfigController.java
```

### 4.2 Controller设计模式
```java
@Tag(name = "AI模型配置管理")
@RestController
@RequestMapping(value = "/api/v1/ai/model")
public class SystemAIModelConfigController {

    @Resource
    private SystemAIModelConfigService systemAIModelConfigService;

    @DS("master")
    @PostMapping(value = "/edit-source")
    @Operation(summary = "编辑模型源")
    public AiModelSource editSource(@Validated @RequestBody AiModelSourceDTO request) {
        return systemAIModelConfigService.editModuleConfig(request, SessionUtils.getUserId());
    }

    @DS("master")
    @GetMapping(value = "/source/list")
    @Operation(summary = "获取模型源列表")
    public List<AiModelSourceDTO> getSourceList(@Validated AiModelSourceRequest request) {
        request.setOwner(SessionUtils.getUserId());
        return systemAIModelConfigService.getModelSourceList(request);
    }

    // ... 其他API方法
}
```

### 4.3 API端点映射
- `/api/v1/ai/model/edit-source` - 编辑模型源
- `/api/v1/ai/model/source/list` - 获取模型源列表
- `/api/v1/ai/model/source/{id}` - 获取模型源详情
- `/api/v1/ai/model/source/delete/{id}` - 删除模型源
- `/api/v1/ai/model/source/names` - 获取模型名称列表

## 5. Service层迁移方案

### 5.1 文件结构
```
scm-ai\src\main\java\com\xinyirun\scm\ai\core\service\model\
├── SystemAIModelConfigService.java
```

### 5.2 Service设计模式
```java
@Service
@Transactional(rollbackFor = Exception.class)
public class SystemAIModelConfigService {

    @Resource
    private AiModelSourceMapper aiModelSourceMapper;
    @Resource
    private ExtAiModelSourceMapper extAiModelSourceMapper;
    @Resource
    private AiChatBaseService aiChatBaseService;

    private static final String DEFAULT_OWNER = "system";

    /**
     * 编辑模型配置
     * @param aiModelSourceDTO 模型配置数据传输对象
     * @param userId 用户ID
     * @return 模型源对象
     */
    public AiModelSource editModuleConfig(AiModelSourceDTO aiModelSourceDTO, String userId) {
        // 业务逻辑实现...
    }

    // ... 其他业务方法
}
```

### 5.3 核心业务逻辑
1. **模型配置编辑**：支持新增/更新模型源
2. **名称唯一性校验**：防止重复模型名称
3. **模型连接验证**：通过AI服务验证模型有效性
4. **权限控制**：个人模型vs系统模型权限管理
5. **AppKey处理**：更新时保护原有Key信息

## 6. Mapper层迁移方案

### 6.1 文件结构
```
scm-ai\src\main\java\com\xinyirun\scm\ai\core\mapper\model\
├── AiModelSourceMapper.java (基础CRUD，继承现有)
├── ExtAiModelSourceMapper.java
├── ExtAiModelSourceMapper.xml
```

### 6.2 Mapper设计模式
```java
@Mapper
public interface ExtAiModelSourceMapper {
    /**
     * 获取模型源列表
     */
    List<AiModelSourceCreateNameDTO> list(@Param("request") AiModelSourceRequest request);

    /**
     * 获取启用的模型源名称列表
     */
    List<OptionDTO> enableSourceNameList(@Param("userId") String userId);
}
```

### 6.3 XML SQL设计
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.xinyirun.scm.ai.core.mapper.model.ExtAiModelSourceMapper">

    <select id="list" resultType="com.xinyirun.scm.ai.bean.dto.AiModelSourceCreateNameDTO">
        SELECT ams.*, su.name as create_user_name
        FROM ai_model_source ams
        LEFT JOIN system_user su ON ams.create_user = su.id
        <where>
            <if test="request.owner != null and request.owner != ''">
                AND (ams.owner = #{request.owner} OR ams.owner = 'system')
            </if>
            <if test="request.name != null and request.name != ''">
                AND ams.name LIKE CONCAT('%', #{request.name}, '%')
            </if>
        </where>
        ORDER BY ams.create_time DESC
    </select>

</mapper>
```

## 7. DTO对象迁移方案

### 7.1 需要创建的DTO类
```
scm-ai\src\main\java\com\xinyirun\scm\ai\bean\dto\
├── AiModelSourceDTO.java
├── AiModelSourceRequest.java  
├── AiModelSourceCreateNameDTO.java
├── AdvSettingDTO.java
```

### 7.2 DTO设计规范
```java
@Data
@Schema(description = "AI模型源数据传输对象")
public class AiModelSourceDTO implements Serializable {
    
    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{ai_model_source.id.not_blank}", groups = {Updated.class})
    @Size(min = 1, max = 50, message = "{ai_model_source.id.length_range}", groups = {Created.class, Updated.class})
    private String id;

    // ... 其他字段定义
}
```

## 8. 实施步骤

### Phase 1: 数据层基础
1. ✅ 确认ai_model_source表结构（已存在）
2. 🔄 创建ExtAiModelSourceMapper和XML
3. 🔄 创建所需DTO对象

### Phase 2: 业务层实现  
4. 🔄 实现SystemAIModelConfigService核心业务逻辑
5. 🔄 集成现有AiChatBaseService进行模型验证
6. 🔄 实现权限控制和数据校验逻辑

### Phase 3: 控制层实现
7. 🔄 实现SystemAIModelConfigController
8. 🔄 配置API路由和参数验证
9. 🔄 集成OpenAPI文档注解

### Phase 4: 测试验证
10. 🔄 单元测试覆盖核心业务逻辑
11. 🔄 集成测试验证API功能
12. 🔄 与现有AI功能联调测试

## 9. 关键技术细节

### 9.1 模型验证逻辑
```java
private void validModel(@NotNull AiModelSourceDTO aiModelSourceDTO) {
    try {
        var aiChatOption = AIChatOption.builder()
                .module(aiModelSourceDTO)
                .prompt("How are you?")
                .build();
        var response = aiChatBaseService.chat(aiChatOption).content();
        if (StringUtils.isBlank(response)) {
            throw new MSException("模型链接失败，请检查配置");
        }
    } catch (Exception e) {
        // 异常处理逻辑
    }
}
```

### 9.2 AppKey掩码处理
```java
public static String maskSkString(String input) {
    if (StringUtils.isBlank(input) || input.length() <= 6) {
        return input;
    }
    String prefix = input.substring(0, 4);
    String suffix = input.substring(input.length() - 2);
    return prefix + "**** " + suffix;
}
```

### 9.3 权限控制策略
- PUBLIC模型：owner = "system"，所有用户可见
- PRIVATE模型：owner = userId，仅创建者可见
- 查询时：个人用户看到自己的+系统的模型

## 10. 兼容性考虑

### 10.1 与现有功能集成
- 保持现有ai_model_source表结构不变
- 新增字段向后兼容
- 与现有AiChatBaseService集成
- 复用现有的AI引擎框架

### 10.2 数据迁移策略
- 现有数据保持不变
- 新增字段允许NULL值
- 提供默认值填充脚本

## 11. 风险评估

### 11.1 技术风险
- 🟡 现有AI功能可能受影响 -> 充分测试
- 🟡 数据库字段冲突 -> 提前验证表结构
- 🟢 代码风格适配 -> 已分析SCM规范

### 11.2 业务风险  
- 🟡 模型验证可能失败 -> 增加异常处理
- 🟡 权限控制复杂 -> 详细测试权限逻辑
- 🟢 功能完整性 -> 基于MeterSphere完整功能

## 12. 总结

本迁移计划基于MeterSphere AI模型功能的完整分析和SCM-AI模块的代码风格研究，提供了详细的实施方案。重点关注：
1. **完整功能迁移**：包含模型管理的所有核心功能
2. **代码风格适配**：严格遵循SCM-AI模块现有规范
3. **业务逻辑保持**：保留MeterSphere的核心业务逻辑
4. **技术架构融合**：与SCM现有技术栈无缝集成

实施过程中需要严格按照SCM-AI模块的代码规范，确保迁移后的代码与现有代码保持一致的风格和质量。