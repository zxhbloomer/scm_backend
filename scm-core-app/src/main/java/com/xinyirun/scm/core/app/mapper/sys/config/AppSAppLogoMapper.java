package com.xinyirun.scm.core.app.mapper.sys.config;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.app.vo.sys.config.AppLogoVo;
import com.xinyirun.scm.bean.entity.sys.config.config.SAppLogoEntity;
import com.xinyirun.scm.common.constant.SystemConstants;
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
public interface AppSAppLogoMapper extends BaseMapper<SAppLogoEntity> {

    /**
     * 查询logo详情
     */
    @Select("    "
            +"	SELECT                                                           "
            +"		*                                                            "
            +"	FROM                                                             "
            +"		s_app_logo t1                                                "
            +"	WHERE                                                            "
            +"		t1.is_enable = '"+ SystemConstants.ENABLE_TRUE+"'            "
            +"	limit 1                                                          "
            + "      ")
    AppLogoVo selectOne ();

}
