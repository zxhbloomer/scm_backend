package com.xinyirun.scm.bean.system.vo.business.upload;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 库存调整
 * </p>
 *
 * @author wwl
 * @since 2021-12-09
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class BImportVo implements Serializable {

    private static final long serialVersionUID = -7742164944730864954L;
    /**
     * 主键id
     */
    private Integer id;

    /**
     * 业务id
     */
    private Integer serial_id;

    /**
     * 导入文件url
     */
    private String upload_url;

    /**
     * 错误信息url
     */
    private String error_url;

    /**
     * 调整原因
     */
    private String remark;

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
    @Version
    private Integer dbversion;


}
