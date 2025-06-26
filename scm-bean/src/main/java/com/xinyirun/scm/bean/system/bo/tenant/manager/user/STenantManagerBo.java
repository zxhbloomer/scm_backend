package com.xinyirun.scm.bean.system.bo.tenant.manager.user;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class STenantManagerBo implements Serializable {


    @Serial
    private static final long serialVersionUID = -7429942165684221333L;

    /**
     * 主键id
     */
    private Integer id;

    private String tenant;

    private String url ;

    private String database_name;

    private String user_name;

    private String password;

    private String host;

    private Boolean status;

    private Date expire_date;

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
}
