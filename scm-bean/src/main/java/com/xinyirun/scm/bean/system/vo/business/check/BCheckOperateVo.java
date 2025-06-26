package com.xinyirun.scm.bean.system.vo.business.check;

import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
// import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 盘点
 * </p>
 *
 * @author wwl
 * @since 2021-12-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
// @ApiModel(value = "盘点操作", description = "盘点操作")
public class BCheckOperateVo implements Serializable {

    private static final long serialVersionUID = -3087565918671253970L;
    /**
     * id
     */
    private Integer id;

    /**
     * 编号
     */
    private String code;

    /**
     * 货主id
     */
    private Integer owner_id;

    /**
     * 货主code
     */
    private String owner_code;

    /**
     * 货主code
     */
    private String owner_name;

    /**
     * 仓库id
     */
    private Integer warehouse_id;

    /**
     * 仓库code
     */
    private String warehouse_code;

    /**
     * 仓库code
     */
    private String warehouse_name;

    /**
     * 单据状态
     */
    private String status;

    /**
     * 单据状态名称
     */
    private String status_name;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    private LocalDateTime u_time;

    /**
     * 创建时间
     */
    private String c_name;

    /**
     * 修改时间
     */
    private String u_name;


    /**
     * 创建人id
     */
    private Integer c_id;

    /**
     * 修改人id
     */
    private Integer u_id;

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;

    /**
     * 明细列表
     */
    List<BCheckOperateDetailVo> detailList;

    /**
     * 换页条件
     */
    private PageCondition pageCondition;

}
