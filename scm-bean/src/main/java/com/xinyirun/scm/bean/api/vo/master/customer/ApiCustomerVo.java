package com.xinyirun.scm.bean.api.vo.master.customer;


// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 客户企业
 * </p>
 *
 * @author htt
 * @since 2021-10-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
// @ApiModel(value = "客户企业", description = "客户企业")
public class ApiCustomerVo implements Serializable {

    private static final long serialVersionUID = 1210763526994181151L;

    /**
     * 客户编码
     */
    private String code;

    /**
     * 信用代码证
     */
    private String credit_no;

    /**
     * 客户名称
     */
    private String name;

    /**
     * 类型：0：内部企业，同步客户、货主数据，1：外部企业，同步客户数据
     */
    private String type;

    /**
     * 客户简称
     */
    private String short_name;

    /**
     * 名称拼音
     */
    private String name_pinyin;

    /**
     * 简称拼音
     */
    private String short_name_pinyin;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 创建人id
     */
    private Long c_id;

    public String getCodeAppCode() {
        return code;
    }

}
