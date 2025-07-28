package com.xinyirun.scm.bean.system.vo.business.so.soorder;

import com.xinyirun.scm.bean.system.config.base.BaseVo;
import com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Description: 销售订单附件VO
 * @CreateTime : 2025/7/23 16:05
 */

@Data
@EqualsAndHashCode(callSuper = false)  
@NoArgsConstructor
public class BSoOrderAttachVo extends BaseVo implements Serializable {

    @Serial
    private static final long serialVersionUID = -502576582786446655L;

    private Integer id;

    /**
     * 销售订单ID
     */
    private Integer so_order_id;

    /**
     * 附件文件
     */
    private Integer one_file;
    private List<SFileInfoVo> one_files;

    /**
     * 创建人
     */
    private String c_name;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 更新人
     */
    private String u_name;

    /**
     * 更新时间
     */
    private LocalDateTime u_time;
}