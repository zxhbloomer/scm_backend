package com.xinyirun.scm.bean.system.vo.business.track;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author xinyirun
 * @since 2022-02-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BTrackApiSinoiovVo implements Serializable {

    private static final long serialVersionUID = -3641120217266437239L;
    /**
     * 主键id
     */
    private Integer id;

    /**
     * 类型
     */
    private String type;

    /**
     * 测试url
     */
    private String test_url;

    /**
     * 生成url
     */
    private String prod_url;

}
