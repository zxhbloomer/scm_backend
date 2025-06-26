package com.xinyirun.scm.bean.api.vo.business.out;


// import io.swagger.annotations.ApiModel;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 出库计划
 * </p>
 *
 * @author htt
 * @since 2021-10-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class ApiOutPlanDiscontinueVo implements Serializable {

    private static final long serialVersionUID = -8048949233789144352L;

    /**
     * 计划单号
     */
    private String code;

    /**
     * 中止理由
     */
    private String remark;

    /**
     * 岗位编码
     */
    private List<String> position_codes;

    /**
     * 中止附件
     */
    private List<String> file_urls;
}
