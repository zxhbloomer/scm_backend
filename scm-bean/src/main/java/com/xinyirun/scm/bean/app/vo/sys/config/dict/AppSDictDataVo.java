package com.xinyirun.scm.bean.app.vo.sys.config.dict;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

// import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author htt
 */
@Data
@NoArgsConstructor
// @Schema( name = "app字典数据信息", description = "app字典数据信息")
@EqualsAndHashCode(callSuper=false)
public class AppSDictDataVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 3626447887213643593L;

    private Long id;

    /**
     * 字典类型表id主键
     */
    private Long dict_type_id;

    /**
     * 字典标签
     */
    private String label;

    /**
     * 字典键值
     */
    private String dict_value;

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
     * 字典名称
     */
    private String dictTypeName;

    /**
     * 字典类型：唯一
     */
    private String dictTypeCode;

}
