package com.xinyirun.scm.bean.app.bo.inventory.warehouse;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 库区bo
 * </p>
 *
 */
@Data
public class AppMLocationBo implements Serializable {


    private static final long serialVersionUID = -8708240294657716348L;
    /**
     * 主键
     */
    private Integer id;

    /**
     * 编码
     */
    private String code;

    /**
     * 名称
     */
    private String name;

    /**
     * 简称
     */
    private String short_name;

    /**
     * 仓库id
     */
    private Integer warehouse_id;

    /**
     * 是否锁定盘点 :0否,1是
     */
    private Boolean inventory;

    /**
     * 是否默认库位/库区:0否,1是
     */
    private Boolean is_default;

    /**
     * 描述
     */
    private String des;

    /**
     * 状态 0启用 1停用
     */
    private Boolean status;

    /**
     * 状态 0启用 1停用
     */
    private Boolean enable;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    private LocalDateTime u_time;

    /**
     * 创建人id
     */
    private Long c_id;

    /**
     * 修改人id
     */
    private Long u_id;

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;

    /**
     * 1库区对多库位
     */
    private List<AppMBinBo> bins;

    /**
     * 1库区1库位，默认库位时使用
     */
    private AppMBinBo bin;
}

