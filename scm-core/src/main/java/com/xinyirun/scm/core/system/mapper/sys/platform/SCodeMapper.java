package com.xinyirun.scm.core.system.mapper.sys.platform;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.sys.syscode.SCodeEntity;
import com.xinyirun.scm.bean.system.vo.sys.platform.syscode.SCodeVo;
import com.xinyirun.scm.common.constant.DictConstant;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zxh
 * @since 2019-07-04
 */
@Repository
public interface SCodeMapper extends BaseMapper<SCodeEntity> {

    String COMMON_SELECT = "                                                                                   "
        + "                                                                                                          "
        + "        SELECT                                                                                            "
        + "   	          t1.* ,                                                                                     "
        + "   	          t2.label as code_rule_label,                                                               "
        + "   	          t3.label as code_type_label,                                                               "
        + "               c_staff.name as c_name,                                                                    "
        + "               u_staff.name as u_name                                                                     "
        + "          FROM                                                                                            "
        + "   	          s_code t1                                                                                  "
        + "   	LEFT JOIN v_dict_info t2 on t2.code = '" + DictConstant.DICT_SYS_CODE_RULE_TYPE + "'          "
        + "                             and t2.dict_value = t1.rule                                                  "
        + "                             and t2.is_del = "+DictConstant.DICT_SYS_DELETE_MAP_NO+"               "
        + "   	LEFT JOIN v_dict_info t3 on t3.code = '" + DictConstant.DICT_SYS_CODE_TYPE + "'               "
        + "                             and t3.dict_value = t1.type                                                  "
        + "                             and t3.is_del = "+DictConstant.DICT_SYS_DELETE_MAP_NO+"               "
        + "     LEFT JOIN m_staff c_staff ON t1.c_id = c_staff.id                                                    "
        + "     LEFT JOIN m_staff u_staff ON t1.u_id = u_staff.id                                                    "
        + "                                                                                                          ";


    /**
     * 页面查询列表
     * @param page
     * @param searchCondition
     * @return
     */
    @Select("    "
        + COMMON_SELECT
        + "  where true                                                              "
        + "      ")
    IPage<SCodeVo> selectPage(Page page, @Param("p1") SCodeVo searchCondition);

    /**
     * 按条件获取所有数据，没有分页
     * @param searchCondition
     * @return
     */
    @Select("    "
        + COMMON_SELECT
        + "  where true "
        + "      ")
    List<SCodeVo> select(@Param("p1") SCodeVo searchCondition);

    /**
     * 按条件获取所有数据，没有分页
     * @param type
     * @return
     */
    @Select("    "
        + " select t.* "
        + "   from s_code t "
        + "  where true "
        + "    and t.type =  #{p1}   "
        + "    and (t.id  =  #{p2} or #{p2} is null)   "
        + "      ")
    List<SCodeEntity> selectByType(@Param("p1") String type, @Param("p2") Long equal_id);

    /**
     * 按条件获取所有数据，没有分页
     * @param id
     * @return
     */
    @Select("    "
        + COMMON_SELECT
        + "  where t1.id =  #{p1}"
        + "      ")
    SCodeVo selectId(@Param("p1") Long id);

//    /**
//     * 页面查询列表
//     * @param type
//     * @return
//     */
//    @Select("                                                                                                           "
//        + "        SELECT                                                                                               "
//        + "        	*                                                                                                   "
//        + "        FROM                                                                                                 "
//        + "        	s_code t                                                                                            "
//        + "        WHERE true                                                                                           "
//        + "        	 and t.type = #{p1}                                                                                 "
//        + "         for update nowait;                                                                                  "
//        + "                                                                                                             ")
//    SCodeEntity selectForUpdateNoWait(@Param("p1") String type);

    /**
     * 页面查询列表
     * @param type
     * @return
     */
    @Select("                                                                                                           "
            + "        SELECT                                                                                           "
            + "        	*                                                                                               "
            + "        FROM                                                                                             "
            + "        	s_code t                                                                                        "
            + "        WHERE true                                                                                       "
            + "        	 and t.type = #{p1}                                                                             "
            + "         for update ;                                                                                    "
            + "                                                                                                         ")
    SCodeEntity selectForUpdateWait(@Param("p1") String type);
}
