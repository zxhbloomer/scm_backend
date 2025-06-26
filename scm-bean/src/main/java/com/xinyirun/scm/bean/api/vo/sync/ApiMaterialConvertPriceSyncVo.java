package com.xinyirun.scm.bean.api.vo.sync;

import com.xinyirun.scm.bean.api.vo.business.price.ApiMaterialConvertPriceVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

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
public class ApiMaterialConvertPriceSyncVo implements Serializable {

    private static final long serialVersionUID = -8287227952777024659L;

    List<ApiMaterialConvertPriceVo> list;
}
