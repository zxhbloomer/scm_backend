package com.xinyirun.scm.core.api.mapper.master.customer;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.master.customer.MCustomerEntity;
import com.xinyirun.scm.bean.api.vo.master.customer.ApiCustomerVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Repository
public interface ApiCustomerMapper extends BaseMapper<MCustomerEntity> {

    String common_select = "  "
            + "     SELECT                                                             "
            + "            t.*,                                                        "
            + "            t3.label as scope_name,                                          "
            + "            t4.label as source_name,                                          "
            + "            t5.label as type_name,                                          "
            + "            t6.label as mold_name,                                          "
            + "            concat(t.province,t.city,t.district) cascader_areas,          "
            + "            t1.name as c_name,                                          "
            + "            t2.name as u_name                                           "
            + "       FROM                                                             "
            + "  	       m_customer t                                                  "
            + "  LEFT JOIN m_staff t1 ON t.c_id = t1.id                                 "
            + "  LEFT JOIN m_staff t2 ON t.u_id = t2.id                                 "
            + "  LEFT JOIN s_dict_data t3 ON t.scope = t3.dict_value   and t3.dict_type_id=5   "
            + "  LEFT JOIN s_dict_data t4 ON t.source = t4.dict_value   and t4.dict_type_id=6   "
            + "  LEFT JOIN s_dict_data t5 ON t.type = t5.dict_value   and t5.dict_type_id=7   "
            + "  LEFT JOIN s_dict_data t6 ON t.mold = t6.dict_value   and t6.dict_type_id=8   "
            + "                                                                        "
            ;

    /**
     * 按条件获取数据
     */
    @Select("    "
            + common_select
            + "  where true "
            + "    and t.code =  #{p1,jdbcType=VARCHAR}"
            + "      ")
    MCustomerEntity selectByCode(@Param("p1") String code);

    /**
     * 按条件获取所有数据，没有分页
     */
    @Select("    "
            + common_select
            + "  where true                               "
            + "    and t.code =  #{p1,jdbcType=VARCHAR}   "
            + "      ")
    List<MCustomerEntity> selectListByCode(@Param("p1") String code);

    /**
     * 按条件获取所有数据，没有分页
     */
    @Select("    "
            + common_select
            + "  where true "
            + "    and t.name =  #{p1,jdbcType=VARCHAR}"
            + "      ")
    List<MCustomerEntity> selectListByName(@Param("p1") String name);

    /**
     * 按条件获取所有数据，没有分页
     */
    @Select("    "
            + common_select
            + "  where true "
            + "    and t.name =  #{p1,jdbcType=VARCHAR}"
            + "      ")
    MCustomerEntity selectByName(@Param("p1") String name);

    /**
     * 按条件获取所有数据，没有分页
     * @param
     * @return
     */
    @Select("    "
            + common_select
            + "  where t.code =  #{p1.code,jdbcType=VARCHAR}"
            + "      ")
    MCustomerEntity selectByCodeAppCode(@Param("p1") ApiCustomerVo vo);

    /**
     * 按条件获取所有数据，没有分页
     * @param
     * @return
     */
    @Select("    "
            + common_select
            + "  where t.credit_no =  #{p1.credit_no,jdbcType=VARCHAR}"
            + "      ")
    MCustomerEntity selectByCreditNo(@Param("p1") ApiCustomerVo vo);



}
