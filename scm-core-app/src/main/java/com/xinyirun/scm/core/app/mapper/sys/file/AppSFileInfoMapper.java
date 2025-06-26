package com.xinyirun.scm.core.app.mapper.sys.file;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.sys.file.SFileInfoEntity;
import com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 附件信息 Mapper 接口
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Repository
public interface AppSFileInfoMapper extends BaseMapper<SFileInfoEntity> {

    String common_select = "  "
            + "     SELECT                                                                                              "
            + "            t.*,                                                                                         "
            + "            t1.name as c_name,                                                                           "
            + "            t2.name as u_name                                                                            "
            + "       FROM                                                                                              "
            + "  	       s_file_info t                                                                                "
            + "  LEFT JOIN m_staff t1 ON t.c_id = t1.id                                                                 "
            + "  LEFT JOIN m_staff t2 ON t.u_id = t2.id                                                                 "
            + "  LEFT JOIN s_file t3 ON t3.id = t.f_id                                                                  "
            + "                                                                                                         "
            ;

    /**
     * 页面查询列表
     * @param f_id
     * @return
     */
    @Select("    "
            + common_select
            + "  where true "
            + "    and (t3.id =  #{p1,jdbcType=INTEGER} )                                                               "
            + "      ")
    SFileInfoVo selectListByFId(@Param("p1") int f_id);

    /**
     * 按条件获取所有数据，没有分页
     * @param id
     * @return
     */
    @Select("    "
            + common_select
            + "  where t.id =  #{p1}"
            + "      ")
    SFileInfoVo selectId(@Param("p1") int id);

    /**
     * 按条件获取所有数据，没有分页
     */
    @Select("    "
            + common_select
            + "  where t.f_id =  #{p1}"
            + "      ")
    SFileInfoEntity selectFIdEntity(@Param("p1") int id);
}
