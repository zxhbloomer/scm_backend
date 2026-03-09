# 内容归类节点加分类指令（instruction）- 设计文档

**日期**: 2026-03-07
**状态**: 已确认

## 背景

现有"内容归类"节点（ClassifierNode）使用 LLM 根据分类名称（category_name）自动判断走哪个分支。当分类名称语义不够清晰时（如业务场景复杂、分类边界模糊），LLM 准确率会下降。

参考 Dify 的 `instruction` 字段设计，加入可选的分类指令，让用户补充说明判断逻辑。

## 需求

用户可在"内容归类"节点填写一段可选的分类指令，LLM 分类时参考该指令，提升准确率。不填时行为与现在完全一致。

## 参考

- Dify：`instruction` 字段注入到用户提示词的 `classification_instructions` JSON 字段
- FastGPT：`systemPrompt` 字段作为"背景知识"注入系统提示词

本方案采用 Dify 方式，与现有提示词结构一致。

## 数据模型变更

### 后端

**ClassifierNodeConfig.java** — 加1个字段：
```java
private String instruction;  // 分类指令，可为空
```

数据库零变更（instruction 随 node_config JSON 整体存储）。

## 提示词变更

**ClassifierPrompt.createPrompt()** 方法签名加 `instruction` 参数：

```java
public static String createPrompt(String input, List<ClassifierCategory> categories, String instruction)
```

User Input 模板从：
```
{"input_text" : ["%s"], "categories" : %s}
```

改为：
```
{"input_text" : ["%s"], "categories" : %s, "classification_instructions" : ["%s"]}
```

instruction 为 null 时传空字符串，LLM 忽略，向后兼容。

## 前端变更

**ClassifierNodeProperty.vue** — 在模型选择下方加可选 textarea：

```
模型
[默认模型 ▼]

分类指令（可选）
[placeholder: 补充说明分类判断逻辑，例如：根据 pagecode 字段判断路由数量]

类别
...
```

**ClassifierNode.vue（画布节点）** — 不改，instruction 不在画布显示。

## 影响范围

| 文件 | 改动量 |
|------|--------|
| `ClassifierNodeConfig.java` | +1字段 |
| `ClassifierPrompt.java` | 方法签名+1参数，提示词+1字段 |
| `ClassifierNode.java` | 调用传入 instruction |
| `ClassifierNodeProperty.vue` | 加 textarea |
| 数据库 | 零变更 |

## 向后兼容

老数据 instruction 为 null，`classification_instructions` 为 `[""]`，LLM 忽略空字符串，行为不变。
