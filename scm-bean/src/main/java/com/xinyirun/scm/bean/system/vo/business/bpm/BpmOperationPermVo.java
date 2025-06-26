package com.xinyirun.scm.bean.system.vo.business.bpm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 审批按钮操作权限实体类
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BpmOperationPermVo implements Serializable {
    private static final long serialVersionUID = -45475579271153023L;
    private Operations agree;
    private Operations refuse;
    private Operations cancel;
    private Operations transfer;
    private Operations afterAdd;
    private Operations recall;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Operations implements Serializable {
        private static final long serialVersionUID = -45475579271153023L;
        //操作项别名
        private String alisa;
        //是否显示
        private Boolean show;
    }
}
