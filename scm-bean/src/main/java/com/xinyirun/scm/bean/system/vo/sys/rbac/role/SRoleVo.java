package com.xinyirun.scm.bean.system.vo.sys.rbac.role;

import com.xinyirun.scm.bean.system.ao.fs.UploadFileResultAo;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;

// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author zhangxh
 */
@Data
@NoArgsConstructor
// @ApiModel(value = "角色返回信息", description = "角色返回vo_bean")
@EqualsAndHashCode(callSuper=false)
public class SRoleVo extends UploadFileResultAo implements Serializable {

    private static final long serialVersionUID = 2443084812232177470L;

    private Long id;

    /**
     * 角色类型
     */
    private String type;

    /**
     * 角色编码
     */
    private String code;

    /**
     * 角色名称
     */
    private String name;

    /**
     * 说明
     */
    private String descr;

    /**
     * 简称
     */
    private String simple_name;

    /**
     * 该角色向下，权限数量
     */
    private int permission_count;

    /**
     * 权限列表，用于前端显示
     */
    private List<PermissionItem> permissionList;

    /**
     * 权限项信息
     */
    @Data
    @NoArgsConstructor
    public static class PermissionItem {
        private Long id;
        private String key;
        private String label;
    }

    /**
     * 租户代码
     */
    private String corp_code;


    /**
     * 是否是已经删除(1:true-已删除,0:false-未删除)
     *
     */
    private Boolean is_del;

    /**
     * 租户名称
     */
    private String corp_name;

    private Long c_id;

    private LocalDateTime c_time;

    private Long u_id;

    private LocalDateTime u_time;

    /**
     * 换页条件
     */
    private PageCondition pageCondition;
}
