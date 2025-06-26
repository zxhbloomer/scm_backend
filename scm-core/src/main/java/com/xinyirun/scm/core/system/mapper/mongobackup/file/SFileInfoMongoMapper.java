package com.xinyirun.scm.core.system.mapper.mongobackup.file;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.sys.file.SFileInfoEntity;
import com.xinyirun.scm.bean.system.vo.mongo.file.SFileMonitorInfoMongoVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 附件详情 Mapper 接口
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Repository
public interface SFileInfoMongoMapper extends BaseMapper<SFileInfoEntity> {

    String common_select = "  "
            + "     SELECT                                                                                              "
            + "            t.*,                                                                                         "
            + "            t2.name as u_name,                                                                           "
            + "            t5.login_name as u_phone                                                                     "
            + "       FROM                                                                                              "
            + "  	       s_file_info t                                                                                "
            + "  LEFT JOIN m_staff t2 ON t.u_id = t2.id                                                                 "
            + "  LEFT JOIN s_file t3 ON t3.id = t.f_id                                                                  "
            + "  LEFT JOIN m_user t5 ON t5.staff_id = t2.id                                                             "
            + "                                                                                                         "
            ;

    /**
     * 按条件获取所有数据，没有分页
     */
    @Select("    "
            + common_select
            + "  where t.f_id =  #{p1}"
            + "      ")
    SFileMonitorInfoMongoVo selectFId(@Param("p1") int id);

}
