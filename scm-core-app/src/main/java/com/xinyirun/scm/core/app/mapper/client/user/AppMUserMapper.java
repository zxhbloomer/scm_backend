package com.xinyirun.scm.core.app.mapper.client.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.app.vo.master.user.AppMUserVo;
import com.xinyirun.scm.bean.entity.master.user.MUserEntity;
import com.xinyirun.scm.bean.system.vo.master.user.MUserVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

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
public interface AppMUserMapper extends BaseMapper<MUserEntity> {

    /**
     *
     * @param p1
     * @return
     */
    @Select( "                                                 "
            + "    select t.*                                      "
            + "      from m_user t                                 "
            + "     where t.login_name = #{p1}                     "
//        + "       and t.is_enable = true                       "
            + "       and t.is_del = false                         "
            + "                                                    ")
    AppMUserVo getDataByName(@Param("p1") String p1);


    /**
     * 按条件获取所有数据，没有分页
     *
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
    AppMUserVo selectByid(@Param("p1") Long id );

    /**
     * 按条件获取所有数据，没有分页
     */
    @Select("                                                                                                    "
            + " select t.*                                                                                           "
            + "   from m_user t                                                                                      "
            + "  where true                                                                                          "
            + "    and t.login_name =  #{p1}                                                                         "
            + "    and (t.id  =  #{p2} or #{p2} is null)                                                             "
            + "    and t.is_del =  0                                                                                 "
//        + "    and (t.tenant_id  = #{p4} or #{p4} is null)                                                      "
            + "                                                                                                      ")
    List<MUserEntity> selectLoginName(@Param("p1") String login_name, @Param("p2") Long equal_id);
}
