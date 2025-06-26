package com.xinyirun.scm.core.system.mapper.sys.schedule.v5;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.master.user.MUserEntity;
import com.xinyirun.scm.bean.system.vo.master.user.MUserVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 *
 */
@Repository
public interface SBUserPwdWarningBatchV5Mapper extends BaseMapper<MUserEntity> {

    @Select(  " <script> "
            + "      SELECT                                                                                                          "
            + "              t1.*                                                                                                    "
            + "        FROM                                                                                                          "
            + "              m_user t1                                                                                               "
            + "       where  true                                                                                                    "
            + "         and  (t1.pwd_u_time is null or DATE_FORMAT(t1.pwd_u_time, '%Y-%m-%d' ) &lt; DATE_FORMAT(#{p1}, '%Y-%m-%d' )) "
            + "         and  t1.is_enable = 1                                                                                        "
            + "         and  t1.is_del = 0                                                                                           "
            + " </script>                                                                                                            "
    )
    List<MUserVo> selectUserByPwd(@Param("p1") LocalDate dataTime);
}
