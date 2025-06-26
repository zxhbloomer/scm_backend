package com.xinyirun.scm.bean.system.vo.business.bpm;

import com.xinyirun.scm.bean.app.config.base.AppBaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 审批流节点使用的bean
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class AppStaffUserBpmInfoVo extends AppBaseVo implements Serializable {


    @Serial
    private static final long serialVersionUID = 6975428141527841993L;
    /**
     * id:staff id
     */
    Long id;

    /**
     * 姓名
     */
    String name;
    /**
     * 编号
     */
    String code;

    /**
     * 头像
     */
    String avatar;

    /**
     * 岗位
     */
    String position;

    /**
     * 类型
     */
    String type;
}
