package com.xinyirun.scm.bean.system.vo.business.bpm;

/**
 * @Description: 审批流程表单项
 * @CreateTime : 2024/12/2 16:50
 */

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BpmProcessFormItemsVo {

    /**
     * 标题
     */
    private String title;

    /**
     * 名称
     */
    private String name;

    /**
     * 图标
     */
    private String icon;

    /**
     * 默认值
     */
    private String value;

    /**
     * 默认属性 Number, String, Boolean
     */
    private String valueType;

    /**
     * 属性
     */
    private Props props;

    /**
     * id 对应业务表字段
     */
    private String id;

    @Data
    @EqualsAndHashCode(callSuper = false)
    @NoArgsConstructor
    public class Props {
        /**
         * 是否必填
         */
        private boolean required;

        /**
         * 是否启用打印
         */
        private boolean enablePrint;

        /**
         * 是否显示中文
         */
        private boolean showChinese;

    }
}
