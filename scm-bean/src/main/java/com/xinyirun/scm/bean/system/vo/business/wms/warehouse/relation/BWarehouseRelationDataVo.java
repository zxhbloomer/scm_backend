package com.xinyirun.scm.bean.system.vo.business.wms.warehouse.relation;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 
 * </p>
 *
 * @author xinyirun
 * @since 2022-02-27
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class BWarehouseRelationDataVo implements Serializable {

    private static final long serialVersionUID = -7206992980748975123L;

    /**
     * id
     */
    Integer serial_id;

    String serial_type;

    List<BWarehouseRelationVo> datas ;
}
