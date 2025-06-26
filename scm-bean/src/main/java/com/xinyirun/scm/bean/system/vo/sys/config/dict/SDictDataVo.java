package com.xinyirun.scm.bean.system.vo.sys.config.dict;

import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;

// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author zhangxh
 */
@Data
@NoArgsConstructor
// @ApiModel(value = "字典数据信息", description = "字典数据vo_bean")
@EqualsAndHashCode(callSuper=false)
public class SDictDataVo implements Serializable {

    private static final long serialVersionUID = 835262693681898034L;

    private Long id;

    private String code;

    /**
     * 字典类型表id主键
     */
    private Long dict_type_id;

    /**
     * 字典排序
     */
    private Integer sort;

    /**
     * 字典标签
     */
    private String label;

    /**
     * 字典键值
     */
    private String dict_value;

    /**
     * 说明
     */
    private String descr;

    /**
     * 是否删除
     */
    private Boolean is_del;

    /**
     * 租户代码
     */
    private String corp_code;

    /**
     * 租户名称
     */
    private String corp_name;

    private Long c_id;

    private LocalDateTime c_time;

    private Long u_id;

    private LocalDateTime u_time;

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;

    /**
     * 以下是字典分类属性
     */

    /**
     * 字典名称
     */
    private String dictTypeName;

    /**
     * 字典类型：唯一
     */
    private String dictTypeCode;

    /**
     * 字典类型说明
     */
    private String dictTypeDescr;

    /**
     * 排序的最大最小值
     */
    private int max_sort;
    private int min_sort;

    /**
     * 是否删除
     */
    private Boolean dictTypeIsdel;

    /**
     * 额外配置1～4
     */
    private String extra1;
    private String extra2;
    private String extra3;
    private String extra4;

    private String table_name;
    private String table_comment;
    private String column_name;
    private String column_comment;


    /**
     * 换页条件
     */
    private PageCondition pageCondition;

    public SDictDataVo(String code, Boolean is_del) {
        this.code = code;
        this.is_del = is_del;
    }
}
