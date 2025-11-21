/**
 * 权限管理 MCP (Model Context Protocol) 模块
 *
 * 页面代码: P00000044
 * 业务领域: 权限管理
 *
 * 本模块提供权限管理相关的AI工具集，让用户可以通过自然语言查询自己的页面和按钮权限。
 *
 * 模块结构:
 * ├── service/     - AI业务服务层
 * │   └── PermissionAiService - 权限AI服务，提供权限查询功能
 * ├── mapper/      - 数据库访问层
 * │   └── PermissionAiMapper - 权限查询Mapper
 * └── tools/       - MCP工具集，AI可调用的函数
 *     └── PermissionMcpTools - 权限MCP工具，包含2个主要工具
 *
 * 主要功能:
 * 1. 页面访问权限查询 - 根据页面名称模糊查询用户可访问的页面编码
 * 2. 按钮权限查询 - 根据页面编码查询用户的按钮操作权限
 *
 * 使用示例:
 * - "我能访问入库单页面吗？"
 * - "查询我的采购订单权限"
 * - "用户在P00000011页面有哪些按钮权限？"
 *
 * 技术特点:
 * - tenantCode和staffId通过ToolContext自动注入，LLM无需感知
 * - 支持LLM自动链式调用：先查页面编码 → 再查按钮权限
 * - 只读操作，不修改数据，保证系统安全
 * - JSON格式返回，便于AI理解和处理
 *
 * @author zzxxhh
 * @since 2025-11-21
 */
package com.xinyirun.scm.ai.mcp.P00000044;
