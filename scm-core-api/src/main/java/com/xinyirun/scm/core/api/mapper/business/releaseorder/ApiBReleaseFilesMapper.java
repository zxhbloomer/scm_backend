package com.xinyirun.scm.core.api.mapper.business.releaseorder;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.busniess.releaseorder.BReleaseFilesEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 放货指令/借货指令附件表 Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2024-04-17
 */
@Repository
public interface ApiBReleaseFilesMapper extends BaseMapper<BReleaseFilesEntity> {

    @Update("<script>"
            + "		update                                                                                              "
            + "			b_release_files t                                                                               "
            + "		inner join b_release_order t1 on t.release_order_code = t1.code                                     "
            + "			set                                                                                             "
            + "				t.release_order_id = t1.id                                                                  "
            + "         where t.release_order_code in                                                                   "
            + "        <foreach collection='p1' item='item' index='index' open='(' separator=',' close=')'>             "
            + "         #{item}                                                                                         "
            + "        </foreach>                                                                                       "
            + "</script>")
    void updateB_release_order_file30(@Param("p1") List<String> codes);

}
