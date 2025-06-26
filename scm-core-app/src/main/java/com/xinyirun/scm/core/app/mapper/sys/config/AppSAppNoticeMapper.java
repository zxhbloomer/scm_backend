package com.xinyirun.scm.core.app.mapper.sys.config;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.app.vo.sys.config.AppNoticeVo;
import com.xinyirun.scm.bean.entity.sys.config.config.SAppNoticeEntity;
import com.xinyirun.scm.common.constant.DictConstant;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2022-02-24
 */
@Repository
public interface AppSAppNoticeMapper extends BaseMapper<SAppNoticeEntity> {

    /**
     * 查询logo详情
     */
    @Select("    "
            +"			SELECT                                                                                          "
            +"				*                                                                                           "
            +"			FROM                                                                                            "
            +"				s_app_notice t1                                                                             "
            +"			WHERE                                                                                           "
            +"			   true                                                                                         "
            +"			ORDER BY                                                                                        "
            +"				CAST(t1.version_code AS SIGNED) desc                                                        "
            +"				LIMIT 1                                                                                     "
            + "      ")
    AppNoticeVo selectOne();

    /**
     * 查询logo详情
     */
    @Select("    "
            +"			SELECT                                                                                          "
            +"				*                                                                                           "
            +"			FROM                                                                                            "
            +"				s_app_notice t1                                                                             "
            +"			WHERE                                                                                           "
            +"			   true                                                                                         "
            +"			AND t1.type='"+ DictConstant.DICT_S_APP_NOTICE_TYPE1 +"'                                        "
            +"			ORDER BY                                                                                        "
            +"				CAST(t1.version_code AS SIGNED) desc                                                        "
            +"				LIMIT 1                                                                                     "
            + "      ")
    AppNoticeVo getLatestForceVersion();

}
