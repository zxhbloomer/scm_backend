package com.xinyirun.scm.bean.api.vo.sync;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author zxh
 * @since 2021-10-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class ApiMaterialPriceSyncVo implements Serializable {

    private static final long serialVersionUID = 7193815886680326169L;

    List<ApiBMaterialPriceVo> list;
}
