package com.xinyirun.scm.core.system.mapper.sys.app;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.sys.config.config.SAppNoticeEntity;
import com.xinyirun.scm.bean.system.vo.sys.config.config.SAppNoticeVo;
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
 * @author xinyirun
 * @since 2022-02-24
 */
@Repository
public interface SAppNoticeMapper extends BaseMapper<SAppNoticeEntity> {

    /**
     * 查询logo详情
     */
    @Select("    "
            +"			SELECT                                                                                                  "
            +"				t1.*,                                                                                               "
            + "             t2.name as c_name,                                                                                  "
            + "             t3.name as u_name,                                                                                  "
            + "             t4.label as type_name                                                                               "
            + "			FROM                                                                                                    "
            + "				s_app_notice t1                                                                                     "
            + "         LEFT JOIN m_staff t2 ON t1.c_id = t2.id                                                                 "
            + "         LEFT JOIN m_staff t3 ON t1.u_id = t3.id                                                                 "
            + "         LEFT JOIN v_dict_info t4 ON t4.code = '" + DictConstant.DICT_S_APP_NOTICE_TYPE + "'                     "
            + "             and t4.dict_value = t1.type                                                                         "
            +"			WHERE                                                                                                   "
            +"			   true                                                                                                 "
            + " and (t1.version_code = #{p1.version_code,jdbcType=VARCHAR} or #{p1.version_code,jdbcType=VARCHAR} is null)      "
            + "      ")
    IPage<SAppNoticeVo> selectList(Page page, @Param("p1") SAppNoticeVo searchCondition);

    /**
     * 查询logo详情
     */
    @Select("    "
            +"			SELECT                                                                                          "
            +"				t1.*,                                                                                       "
            + "             t2.name as c_name,                                                                          "
            + "             t3.name as u_name,                                                                          "
            + "             t4.label as type_name                                                                       "
            +"			FROM                                                                                            "
            +"				s_app_notice t1                                                                             "
            + "         LEFT JOIN m_staff t2 ON t1.c_id = t2.id                                                         "
            + "         LEFT JOIN m_staff t3 ON t1.u_id = t3.id                                                         "
            + "         LEFT JOIN v_dict_info t4 ON t4.code = '" + DictConstant.DICT_S_APP_NOTICE_TYPE + "'             "
            + "             and t4.dict_value = t1.type                                                                 "
            +"			WHERE                                                                                           "
            +"			   true                                                                                         "
            + "         and t1.version_code = #{p1,jdbcType=VARCHAR}                                                    "
            + "      ")
    List<SAppNoticeVo> selectByVersionCode(@Param("p1") String code);

}
