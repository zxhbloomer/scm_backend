package com.xinyirun.scm.core.system.mapper.sys.app;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.sys.config.config.SAppConfigEntity;
import com.xinyirun.scm.bean.system.vo.sys.config.config.SAppConfigVo;
import com.xinyirun.scm.bean.system.vo.sys.config.config.SConfigVo;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * app配置表 Mapper 接口
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Repository
public interface SAppConfigMapper extends BaseMapper<SAppConfigEntity> {

    /**
     * @param p1
     * @return
     */
    @Select( "                                                     "
            + "    select t.*                                      "
            + "      from s_app_config t                           "
            + "     where t.app_key = #{p1}                        "
            + "                                                    ")
    SAppConfigEntity getDataByAppKey(@Param("p1") String p1);

    /**
     * @param p1
     * @return
     */
    @Select( "                                                     "
            + "    select t.*                                      "
            + "      from s_app_config t                           "
            + "     where t.code = #{p1}                        "
            + "                                                    ")
    SAppConfigEntity getDataByCode(@Param("p1") String p1);


    /**
     * @param p1
     * @param p2
     * @return
     */
    @Select( "                                                     "
            + "    select t.*                                      "
            + "      from s_app_config t                           "
            + "     where t.app_key = #{p1}                        "
            + "       and t.secret_key = #{p2}                     "
            + "                                                    ")
    SAppConfigEntity getDataByAppKeyAndSecurityKey(@Param("p1") String p1,@Param("p2") String p2);

    @Select( "                                                                                             "
            + "    select t.*                                                                              "
            + "      from s_app_config t                                                                   "
            + "     where true                                                                             "
            + "       and (t.id = #{p1.id,jdbcType=INTEGER} or #{p1.id,jdbcType=INTEGER} is null)          "
            + "       and (t.code = #{p1.code,jdbcType=VARCHAR} or #{p1.code,jdbcType=VARCHAR} is null)    "
            + "  order by t.code                                                                           "
            + "                                                                                    ")
    List<SAppConfigVo> getListData(@Param("p1") SAppConfigVo p1);
}
