package com.xinyirun.scm.ai.mcp.P00000044.service;

import com.xinyirun.scm.ai.mcp.P00000044.mapper.PermissionAiMapper;
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
                        找到多个页面，请以列表清单（markdown格式），展示所有前10个页面(显示page_code和page_name)。
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
                result.put("_aiHint", """ 
                        判断用户是否需要展示权限列表清单：
                        1、如果有必要展示权限列表清单，必须以清晰的列表形式（markdown格式）展示用户拥有的按钮权限，每个权限显示perms(权限标识)和descr(权限描述)，帮助用户理解自己能执行哪些操作")
                        2、如果没有必要展示权限列表，就告知用户您拥有xx权限，可以进行xx操作。
                        如：您拥有保存权限，可以新增采购合同
                        """
                        );
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

    /**
     * 根据页面编码查询用户可访问的菜单路径
     *
     * @param staffId 员工ID
     * @param pageCode 页面编码
     * @return 查询结果,包含菜单路径列表
     */
    public Map<String, Object> getPageMenuPaths(Long staffId, String pageCode) {
        Map<String, Object> result = new HashMap<>();

        try {
            List<Map<String, Object>> paths = permissionAiMapper.findMenuPathsByPageCode(staffId, pageCode);

            result.put("success", true);
            result.put("pageCode", pageCode);
            result.put("count", paths.size());
            result.put("paths", paths);

            // 根据结果生成不同的 AI 处理指令
            if (paths.isEmpty()) {
                result.put("message", "未找到该页面的菜单路径或用户无访问权限");
                result.put("_aiHint", "用户没有该页面的访问权限或页面编码不存在，请友好告知并建议：1)检查页面编码是否正确 2)联系管理员确认权限");
            } else if (paths.size() == 1) {
                result.put("message", "找到1个菜单路径");
                result.put("_aiHint", "找到唯一菜单路径，请展示：page_code、name(或meta_title)和path，让用户知道该页面在哪个菜单路径下");
            } else {
                result.put("message", "找到" + paths.size() + "个菜单路径");
                result.put("_aiHint", "该页面在多个不同菜单位置下，请以列表形式展示所有路径(显示page_code、name/meta_title、path)，询问用户需要访问哪个路径");
            }

        } catch (Exception e) {
            log.error("查询菜单路径失败: staffId={}, pageCode={}", staffId, pageCode, e);
            result.put("success", false);
            result.put("message", "查询菜单路径失败: " + e.getMessage());
            result.put("error", e.getClass().getSimpleName());
            result.put("_aiHint", "查询出错，请安抚用户并建议稍后重试，或联系系统管理员");
        }

        return result;
    }

    /**
     * 生成打开页面的跳转指令
     *
     * 根据提供的页面路径,生成前端可识别的页面跳转指令
     *
     * @param staffId 员工ID
     * @param pagePath 页面路径,如"/20_master/goods"、"/10_system/user"
     * @return 包含跳转指令的Map
     */
    public Map<String, Object> generateOpenPageInstruction(Long staffId, String pagePath) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 验证路径格式
            if (pagePath == null || pagePath.trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "页面路径不能为空");
                result.put("_aiHint", "页面路径无效,请先使用getPageMenuPaths工具查询有效路径");
                return result;
            }

            // 生成跳转指令
            result.put("success", true);
            result.put("action", "openPage");
            result.put("url", pagePath);
            result.put("target", "_self");
            result.put("message", "页面跳转指令已生成");
            result.put("_aiHint", "已生成页面跳转指令,前端将自动打开该页面");

            log.info("生成页面跳转指令成功: staffId={}, pagePath={}", staffId, pagePath);
            log.info("【DEBUG-openPage】返回JSON: {}", result);

        } catch (Exception e) {
            log.error("生成页面跳转指令失败: staffId={}, pagePath={}", staffId, pagePath, e);
            result.put("success", false);
            result.put("message", "生成页面跳转指令失败: " + e.getMessage());
            result.put("error", e.getClass().getSimpleName());
            result.put("_aiHint", "生成跳转指令出错,请检查页面路径格式");
        }

        return result;
    }
}
