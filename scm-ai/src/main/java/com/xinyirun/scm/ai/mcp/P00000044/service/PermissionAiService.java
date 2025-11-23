package com.xinyirun.scm.ai.mcp.P00000044.service;

import com.xinyirun.scm.ai.core.mapper.mcp.PermissionAiMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 权限查询AI服务
 *
 * 提供用户页面权限和按钮权限的查询服务，供MCP工具调用
 *
 * @author zzxxhh
 * @since 2025-11-21
 */
@Slf4j
@Service
public class PermissionAiService {

    @Autowired
    private PermissionAiMapper permissionAiMapper;

    /**
     * 根据页面名称查询用户可访问的页面编码列表
     *
     * @param staffId 员工ID
     * @param pageName 页面名称(支持模糊匹配)
     * @return 查询结果,包含匹配的页面编码列表
     */
    public Map<String, Object> checkPageAccess(Long staffId, String pageName) {
        Map<String, Object> result = new HashMap<>();

        try {
            List<Map<String, Object>> pages = permissionAiMapper.findPagesByName(staffId, pageName);

            result.put("success", true);
            result.put("count", pages.size());
            result.put("pages", pages);

            // 根据结果生成不同的 AI 处理指令
            if (pages.isEmpty()) {
                result.put("message", "未找到匹配的页面或用户无访问权限");
                result.put("_aiHint", "请友好告知用户未找到匹配页面，建议：1)检查页面名称是否正确 2)换个关键词重试 3)联系管理员确认权限");
            } else if (pages.size() == 1) {
                result.put("message", "找到1个匹配页面");
                result.put("_aiHint", "找到唯一匹配页面，请展示页面信息(page_code和page_name)，并主动询问用户：'是否需要查询该页面的按钮权限？'");
            } else {
                result.put("message", "找到" + pages.size() + "个匹配页面");
                result.put("_aiHint",
                        """ 
                        找到多个页面，请以列表清单，展示所有前10个页面(显示page_code和page_name)。
                        因为无法确定用户所指的是那个页面，请求用户指定一个页面，可以查找相应的页面权限,
                        不展示过多的数据，更不能展示具体每个页面的权限，在这里要求用户给出选择，才能进行下一步，查询该页面的权限。
                        """
                                );
            }

        } catch (Exception e) {
            log.error("查询页面权限失败: staffId={}, pageName={}", staffId, pageName, e);
            result.put("success", false);
            result.put("message", "查询页面权限失败: " + e.getMessage());
            result.put("error", e.getClass().getSimpleName());
            result.put("_aiHint", "查询出错，请安抚用户并建议稍后重试，或联系系统管理员");
        }
        log.debug("权限查询AI服务mcp,提示词：{}",result.toString());
        return result;
    }

    /**
     * 根据页面编码查询用户的按钮权限列表
     *
     * @param staffId 员工ID
     * @param pageCode 页面编码
     * @return 查询结果,包含按钮权限列表
     */
    public Map<String, Object> getPageButtonPermissions(Long staffId, String pageCode) {
        Map<String, Object> result = new HashMap<>();

        try {
            List<Map<String, Object>> buttons = permissionAiMapper.findButtonPermissions(staffId, pageCode);

            result.put("success", true);
            result.put("pageCode", pageCode);
            result.put("count", buttons.size());
            result.put("buttons", buttons);

            // 根据结果生成不同的 AI 处理指令
            if (buttons.isEmpty()) {
                result.put("message", "该页面无按钮权限或用户无访问权限");
                result.put("_aiHint", "用户在该页面没有任何按钮权限，请友好告知并建议联系管理员申请所需权限");
            } else {
                result.put("message", "找到" + buttons.size() + "个按钮权限");
                result.put("_aiHint", "请以清晰的列表形式展示用户拥有的按钮权限，每个权限显示perms(权限标识)和descr(权限描述)，帮助用户理解自己能执行哪些操作");
            }

        } catch (Exception e) {
            log.error("查询按钮权限失败: staffId={}, pageCode={}", staffId, pageCode, e);
            result.put("success", false);
            result.put("message", "查询按钮权限失败: " + e.getMessage());
            result.put("error", e.getClass().getSimpleName());
            result.put("_aiHint", "查询出错，请安抚用户并建议稍后重试，或联系系统管理员");
        }

        return result;
    }
}
