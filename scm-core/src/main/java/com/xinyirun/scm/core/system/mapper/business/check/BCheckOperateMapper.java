package com.xinyirun.scm.core.system.mapper.business.check;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.busniess.check.BCheckOperateEntity;
import com.xinyirun.scm.bean.system.vo.business.check.BCheckOperateVo;
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
 * @since 2021-12-29
 */
@Repository
public interface BCheckOperateMapper extends BaseMapper<BCheckOperateEntity> {

    /**
     * 没有分页，按id筛选条件
     */
    @Select("   <script>                                                                                        "
            + "  SELECT  t.*  from b_check_operate  t                                                           "
            + "  where t.id in                                                                                  "
            + "        <foreach collection='p1' item='item' index='index' open='(' separator=',' close=')'>     "
            + "         #{item.id,jdbcType=INTEGER}                                                             "
            + "        </foreach>                                                                               "
            + "  </script>    ")
    List<BCheckOperateEntity> selectIds(@Param("p1") List<BCheckOperateVo> searchCondition);

    /**
     * 按条件分页查询
     */
    @Select("    "
            + "   SELECT   t1.*,  "
            + "            t2.name c_name,                                                              											  "
            + "            t3.name u_name,                                                              											  "
            + "            t5.name as owner_name,                                                       											  "
            + "            t6.name as warehouse_name,                                                    											  "
            + "            t7.label as status_name                                                                                                    "
            + "       from b_check_operate t1                                                                                                        "
            + "       LEFT JOIN m_staff t2 ON t1.c_id = t2.id                                           											  "
            + "       LEFT JOIN m_staff t3 ON t1.u_id = t3.id                                           											  "
            + "       LEFT JOIN m_customer t5 ON t1.owner_code = t5.code                                											  "
            + "       LEFT JOIN m_warehouse t6 ON t1.warehouse_code = t6.code                           											  "
            + "       LEFT JOIN s_dict_data t7 ON t7.code = '" + DictConstant.DICT_B_CHECK_OPERATE_STATUS + "' AND t7.dict_value = t1.status          "
            + "  where true                                                                                                                            "
            + "  and (t1.owner_id = #{p1.owner_id} or #{p1.owner_id} is null)"
            + "      ")
    IPage<BCheckOperateVo> selectPage(Page page, @Param("p1") BCheckOperateVo searchCondition);

}
