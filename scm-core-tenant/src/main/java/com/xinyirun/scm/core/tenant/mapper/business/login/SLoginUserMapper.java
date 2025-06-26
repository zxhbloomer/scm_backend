package com.xinyirun.scm.core.tenant.mapper.business.login;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.tenant.manager.user.SLoginUserEntity;
import com.xinyirun.scm.bean.system.bo.tenant.manager.user.SLoginUserBo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 用户登录系统表（用户名密码），与各个租户系统联动，实时更新 Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2025-05-16
 */
public interface SLoginUserMapper extends BaseMapper<SLoginUserEntity> {

    /**
     *
     * @param p1
     * @return
     */
    @Select( "                                                          "
            + "   select t1.*,                                          "
            + "  			 t2.`code` as staff_code                    "
            + "     from s_login_user t1                                "
            + "    where t1.login_name = #{p1}                         "
//        + "       and t.is_enable = true                       "
//        + "       and t.is_del = false                         "
            + "                                                    ")
    List<SLoginUserBo> getDataByName(@Param("p1") String p1);
}
