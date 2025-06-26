package com.xinyirun.scm.core.system.mapper.sys.app;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.sys.config.config.SAppConfigDetailEntity;
import com.xinyirun.scm.bean.system.vo.sys.config.config.SAppConfigDetailVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * app配置表 Mapper 接口
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Repository
public interface SAppConfigDetailMapper extends BaseMapper<SAppConfigDetailEntity> {

    /**
     * @param p1
     * @return
     */
    @Select( "                                                     "
            + "    select t.*                                      "
            + "      from s_app_config_detail t                    "
            + "     where t.code = #{p1}                           "
            + "     and  t.type = #{p2}                            "
            + "                                                    ")
    SAppConfigDetailVo getDataByCode(@Param("p1") String p1, @Param("p2") String p2);

}
