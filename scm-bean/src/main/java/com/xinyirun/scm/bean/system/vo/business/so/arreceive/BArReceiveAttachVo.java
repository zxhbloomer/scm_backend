package com.xinyirun.scm.bean.system.vo.business.so.arreceive;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo;

/**
 * <p>
 * 应收单附件表 Vo
 * </p>
 *
 * @author xinyirun
 * @since 2025-01-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BArReceiveAttachVo implements Serializable {

    @Serial
    private static final long serialVersionUID = -8462039185736492847L;

    private Integer id;

    /**
     * 应收单表id
     */
    private Integer ar_receive_id;

    /**
     * 收款单表code
     */
    private String ar_receive_code;

    /**
     * 应收单附件
     */
    private Integer one_file;

    /**
     * 凭证附件
     */
    private Integer two_file;

    /**
     * 创建人id
     */
    private Integer c_id;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 修改人id
     */
    private Integer u_id;

    /**
     * 修改时间
     */
    private LocalDateTime u_time;

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;

    /**
     * 作废原因
     */
    private String cancel_reason;

    /**
     * 作废附件
     */
    private Integer cancel_file;

    /**
     * 作废附件文件列表
     */
    private List<SFileInfoVo> cancel_files;

}