package com.xinyirun.scm.core.system.mapper.master;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.master.MAddressEntity;
import com.xinyirun.scm.bean.system.vo.master.MAddressVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 地址簿 Mapper 接口
 * </p>
 *
 * @author zxh
 * @since 2019-08-23
 */
@Repository
public interface MAddressMapper extends BaseMapper<MAddressEntity> {
    /**
     * 页面查询列表
     * @param page
     * @param searchCondition
     * @return
     */
    @Select("    "
        + "  SELECT                                                                                                        "
        + "        	t1.*,                                                                                                  "
        + "        	t2.`name` province_name,                                                                               "
        + "        	t3.`name` city_name,                                                                                   "
        + "        	t4.`name` area_name,                                                                                   "
        + "        	concat_ws(' / ',t2.`name`,t3.`name`,t4.`name`) as cascader_text,                                       "
        + "        	t5.label tag_name,                                                                                     "
        + "         c_staff.name as c_name,                                                                                "
        + "         u_staff.name as u_name                                                                                 "
        + "    FROM                                                                                                        "
        + "        	m_address t1                                                                                           "
        + "        	LEFT JOIN s_area_provinces t2 ON t1.province_code = t2.`code`                                          "
        + "        	LEFT JOIN s_area_cities t3 ON t1.city_code = t3.`code`                                                 "
        + "        	LEFT JOIN s_areas t4 ON t1.area_code = t4.`code`                                                       "
        + "        	LEFT JOIN v_dict_info t5 ON t5.code = 'sys_address_tag_type' and t1.tag = t5.dict_value                "
        + "         LEFT JOIN m_staff c_staff ON t1.c_id = c_staff.id                                                      "
        + "         LEFT JOIN m_staff u_staff ON t1.u_id = u_staff.id                                                      "
        + "  where true                                                                                                    "
        + "     and (t1.serial_type = #{p1.serial_type,jdbcType=VARCHAR} or #{p1.serial_type,jdbcType=VARCHAR} is null  )  "
        + "     and (t1.serial_id = #{p1.serial_id,jdbcType=BIGINT} or #{p1.serial_id,jdbcType=BIGINT} is null     )       "
//        + "     and (t1.tenant_id =#{p1.tenant_id,jdbcType=BIGINT} or #{p1.tenant_id,jdbcType=BIGINT} is null)             "
        + "      ")
    IPage<MAddressVo> selectPage(Page page, @Param("p1") MAddressVo searchCondition);

    /**
     * 按条件获取所有数据，没有分页
     * @param searchCondition
     * @return
     */
    @Select("    "
        + "  SELECT                                                                                                        "
        + "        	t1.*,                                                                                                  "
        + "        	t2.`name` province_name,                                                                               "
        + "        	t3.`name` city_name,                                                                                   "
        + "        	t4.`name` area_name,                                                                                   "
        + "        	t5.label tag_name,                                                                                     "
        + "         c_staff.name as c_name,                                                                                "
        + "         u_staff.name as u_name                                                                                 "
        + "    FROM                                                                                                        "
        + "        	m_address t1                                                                                           "
        + "        	LEFT JOIN s_area_provinces t2 ON t1.province_code = t2.`code`                                          "
        + "        	LEFT JOIN s_area_cities t3 ON t1.city_code = t3.`code`                                                 "
        + "        	LEFT JOIN s_areas t4 ON t1.area_code = t4.`code`                                                       "
        + "        	LEFT JOIN v_dict_info t5 ON t5.code = 'sys_address_tag_type' and t1.tag = t5.dict_value                "
        + "         LEFT JOIN m_staff c_staff ON t1.c_id = c_staff.id                                                      "
        + "         LEFT JOIN m_staff u_staff ON t1.u_id = u_staff.id                                                      "
        + "  where true                                                                                                    "
        + "     and (t1.serial_type = #{p1.serial_type,jdbcType=VARCHAR} or #{p1.serial_type,jdbcType=VARCHAR} is null  )  "
        + "     and (t1.serial_id = #{p1.serial_id,jdbcType=BIGINT} or #{p1.serial_id,jdbcType=BIGINT} is null     )     "
//        + "     and (t1.tenant_id =#{p1.tenant_id,jdbcType=BIGINT} or #{p1.tenant_id,jdbcType=BIGINT} is null)          "
        + "      ")
    List<MAddressVo> select(@Param("p1") MAddressVo searchCondition);

    /**
     * 没有分页，按id筛选条件
     * @param searchCondition
     * @return
     */
    @Select("<script>"
        + " select t.* "
        + "   from m_group t "
        + "  where true "
//        + "    and (t.tenant_id = #{p2} or #{p2} is null  )                                               "
        + "    and t.id in "
        + "        <foreach collection='p1' item='item' index='index' open='(' separator=',' close=')'>"
        + "         #{item.id}  "
        + "        </foreach>"
        + "  </script>")
    List<MAddressEntity> selectIdsIn(@Param("p1") List<MAddressEntity> searchCondition);

    /**
     * 页面查询列表
     * @return
     */
    @Select("    "
        + "  SELECT                                                                                                        "
        + "        	t1.*,                                                                                                  "
        + "        	t2.`name` province_name,                                                                               "
        + "        	t3.`name` city_name,                                                                                   "
        + "        	t4.`name` area_name,                                                                                   "
        + "        	concat_ws(' / ',t2.`name`,t3.`name`,t4.`name`) as cascader_text,                                       "
        + "        	t5.label tag_name,                                                                                     "
        + "         c_staff.name as c_name,                                                                           "
        + "         u_staff.name as u_name                                                                            "
        + "    FROM                                                                                                        "
        + "        	m_address t1                                                                                           "
        + "        	LEFT JOIN s_area_provinces t2 ON t1.province_code = t2.`code`                                          "
        + "        	LEFT JOIN s_area_cities t3 ON t1.city_code = t3.`code`                                                 "
        + "        	LEFT JOIN s_areas t4 ON t1.area_code = t4.`code`                                                       "
        + "        	LEFT JOIN v_dict_info t5 ON t5.code = 'sys_address_tag_type' and t1.tag = t5.dict_value                "
        + "         LEFT JOIN m_staff c_staff ON t1.c_id = c_staff.id                                                      "
        + "         LEFT JOIN m_staff u_staff ON t1.u_id = u_staff.id                                                      "
        + "  where true                                                                                                    "
        + "     and (t1.id = #{p1})                                                                                        "
//        + "     and (t1.tenant_id = #{p2} or #{p2} is null  )                                                              "
        + "      ")
    MAddressVo selectByid(@Param("p1") Long id);
}
