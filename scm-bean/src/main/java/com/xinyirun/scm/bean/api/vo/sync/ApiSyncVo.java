package com.xinyirun.scm.bean.api.vo.sync;

import com.xinyirun.scm.bean.system.vo.business.sync.BSyncStatusVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 * 入库编号、id的bean
 * </p>
 *
 * @author zxh
 * @since 2021-10-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class ApiSyncVo implements Serializable {

    private static final long serialVersionUID = -7360195816063292694L;

    BSyncStatusVo statusBean;

    Object data;

    String sync_type;

    String apiSteelUrl;
}
