package com.xinyirun.scm.core.api.mapper.business.in.order.deliveryconfirm;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.api.bo.steel.ApiDeliveryConfirmBo;
import com.xinyirun.scm.bean.api.vo.business.orderdoc.ApiDeliveryConfirmVo;
import com.xinyirun.scm.bean.entity.busniess.in.order.BInOrderEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApiInOrderGoodsDeliveryConfirmMapper extends BaseMapper<BInOrderEntity> {

    /**
     * 按订单id,获取确认函
     * @param bo
     * @return
     */
    @Select("    "
            + "         select t4.contract_no as contractCode,                                                       "
            + "                t3.file_name   as fileName,                                                           "
            + "                SUBSTRING_INDEX(t3.file_name,'.', -1) as fileSuffix,                                  "
            + "                t3.url as fileUrl,                                                                    "
            + "                t5.goods_code as oodsCode,                                                            "
            + "                t5.name as goodsName,                                                                 "
            + "                t5.goods_code as goodsCode,                                                           "
            + "                t5.spec as goodsSpecName,                                                             "
            + "                t4.order_no as orderCode,                                                             "
            + "                c_staff.name as uploaderName,                                                         "
            + "                DATE_FORMAT(t3.c_time, '%Y-%m-%d %H:%i:%s') as uploaderTime,                          "
            + "                row_number() over(partition by t3.f_id order by t3.c_time ) as no                     "
            + "           from b_in_order_goods t1                                                                   "
            + "     inner join s_file t2                                                                             "
            + "             on t1.id = t2.serial_id                                                                  "
            + "            and t2.serial_type = 'b_in_order_goods'                                                   "
            + "     inner join s_file_info t3                                                                        "
            + "             on t3.f_id = t2.id                                                                       "
            + "     inner join b_in_order t4                                                                         "
            + "             on t4.id = t1.order_id                                                                   "
            + "     inner join m_goods_spec t5                                                                       "
            + "             on t5.id = t1.sku_id                                                                     "
            + "      LEFT JOIN m_staff c_staff                                                                       "
            + "             on t3.c_id = c_staff.id                                                                  "
            + "          where t1.id = #{p1.in_order_goods_id,jdbcType=INTEGER}                                                                            "
            + "      ")
    List<ApiDeliveryConfirmVo> getDeliveryConfirmLists(@Param("p1") ApiDeliveryConfirmBo bo);
}
