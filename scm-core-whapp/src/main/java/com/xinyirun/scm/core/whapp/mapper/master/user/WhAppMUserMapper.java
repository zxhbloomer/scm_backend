package com.xinyirun.scm.core.whapp.mapper.master.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.master.user.MUserEntity;
import com.xinyirun.scm.bean.system.vo.master.tree.TreeDataVo;
import com.xinyirun.scm.bean.system.vo.master.user.MUserVo;
import com.xinyirun.scm.common.constant.DictConstant;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 *  用户表 Mapper 接口
 * </p>
 *
 * @author jobob
 * @since 2019-06-24
 */
@Repository
public interface WhAppMUserMapper extends BaseMapper<MUserEntity> {
    /**
     * 页面查询列表
     * @return
     */
    @Select("                                                                                                                    "
            + "      SELECT                                                                                                          "
            + "              t1.* ,                                                                                                  "
            + "            	 t2.label as type_text                                                                                   "
            + "        FROM                                                                                                          "
            + "              m_user t1                                                                                               "
            + "   left join  v_dict_info t2 on t2.code = 'usr_login_type' and t1.type = t2.dict_value                                "
            + "       where  true                                                                                                    "
            + "         and  (t1.id = #{p1})                                                                                         "
//        + "         and (t1.tenant_id  = #{p2} or #{p2} is null)                                                                "
            + "                                                                                                                      ")
    MUserVo selectUserById(@Param("p1") Long id );
}
