package com.xinyirun.scm.core.system.mapper.wms.in.order.document;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.busniess.in.order.document.BIinOrderGoodsDeliveryDocumentEntity;
import com.xinyirun.scm.bean.system.vo.quartz.SJobVo;
import com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo;
import com.xinyirun.scm.common.constant.DictConstant;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 收货确认函 Mapper 接口
 * </p>
 *
 * @author wwl
 * @since 2022-03-12
 */
@Repository
public interface BIinOrderGoodsDeliveryDocumentMapper extends BaseMapper<BIinOrderGoodsDeliveryDocumentEntity> {

    @Select("                                                                                                 "
            + "          select *                                                                             "
            + "            from s_file_info t1                                                                "
            + "      inner join s_file t2                                                                     "
            + "              on t1.f_id = t2.id                                                               "
            + "             and t2.serial_type = '" + DictConstant.DICT_SYS_CODE_TYPE_B_IN_ORDER_GOODS +"'    "
            + "             and t2.serial_id =  #{p1,jdbcType=INTEGER}                                        "
            + "           where true                                                                          "
            + "                      ")
    List<SFileInfoVo> selectLists(@Param("p1") Integer id);
}
