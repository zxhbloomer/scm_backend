package com.xinyirun.scm.core.system.mapper.business.check;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.business.check.BCheckEntity;
import com.xinyirun.scm.bean.system.vo.business.check.BCheckVo;
import com.xinyirun.scm.common.constant.DictConstant;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 盘点 Mapper 接口
 * </p>
 *
 * @author wwl
 * @since 2021-12-27
 */
@Repository
public interface BCheckMapper extends BaseMapper<BCheckEntity> {

    String common_select = "                                                                            "
            + "     SELECT                                                                              											  "
            + "            t1.*,                                                                        											  "
            + "            t2.name c_name,                                                              											  "
            + "            t3.name u_name,                                                              											  "
            + "            t4.name e_name,                                                              											  "
            + "            t5.name as owner_name,                                                       											  "
            + "            t6.name as warehouse_name,                                                   											  "
            + "            t7.label as status_name                                                         											  "
            + "       FROM                                                                              											  "
            + "  	       b_check t1                                                                   											  "
            + "       LEFT JOIN m_staff t2 ON t1.c_id = t2.id                                           											  "
            + "       LEFT JOIN m_staff t3 ON t1.u_id = t3.id                                           											  "
            + "       LEFT JOIN m_staff t4 ON t1.e_id = t4.id                                           											  "
            + "       LEFT JOIN m_owner t5 ON t1.owner_id = t5.id                               											  "
            + "       LEFT JOIN m_warehouse t6 ON t1.warehouse_code = t6.code                           											  "
            + "       LEFT JOIN (select tab1.* from s_dict_data tab1 inner join s_dict_type tab2 on tab1.dict_type_id = tab2.id                       "
            + "              where tab2.code = '"+ DictConstant.DICT_B_CHECK_STATUS +"') t7 ON t7.dict_value = t1.status                              "
            ;

    /**
     * 没有分页，按id筛选条件
     */
    @Select("   <script>                                                                                        "
            + "  SELECT  t.*  from b_check t                                                           "
            + "  where t.id in                                                                                  "
            + "        <foreach collection='p1' item='item' index='index' open='(' separator=',' close=')'>     "
            + "         #{item.id,jdbcType=INTEGER}                                                             "
            + "        </foreach>                                                                               "
            + "  </script>    ")
    List<BCheckEntity> selectIds(@Param("p1") List<BCheckVo> searchCondition);

    /**
     * 页面查询列表
     */
    @Select("    "
            + common_select
            + "   where true                                                                                                    "
            + "   and (t1.owner_id = #{p1.owner_id} or #{p1.owner_id} is null)"
            + "       ")
    IPage<BCheckVo> selectPage(Page page, @Param("p1") BCheckVo searchCondition);

}
