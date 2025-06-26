package com.xinyirun.scm.core.system.mapper.sys.columns;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.sys.columns.SColumnSizeEntity;
import com.xinyirun.scm.bean.system.vo.sys.columns.SColumnSizeVo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 表格列宽 Mapper 接口
 * </p>
 *
 * @author zxh
 * @since 2020-06-09
 */
@Repository
public interface SColumnSizeMapper extends BaseMapper<SColumnSizeEntity> {

    @Select( "   "
            + "        select                                                                               "
            + "               *                                                                             "
            + "          FROM                                                                               "
            + "               s_column_size t1                                                              "
            + "         where true                                                                          "
            + "           and page_code = #{p1.page_code,jdbcType=VARCHAR}                                  "
            + "           and (type = #{p1.type,jdbcType=VARCHAR} or #{p1.type,jdbcType=VARCHAR} is null)   "
            + "           and staff_id = #{p1.staff_id,jdbcType=BIGINT}                                     "
            + "      ")
    List<SColumnSizeVo> getData(@Param("p1") SColumnSizeVo condition);

    /**
     * 更新保存
     * @param entity
     * @return
     */
    @Update("                                                                                           "
            + "    update s_column_size t                                                                   "
            + "       set t.column_property = #{p1.column_property,jdbcType=INTEGER},                       "
            + "           t.column_label = #{p1.column_label,jdbcType=INTEGER},                             "
            + "           t.min_width = #{p1.min_width,jdbcType=INTEGER},                                   "
            + "           t.real_width = #{p1.real_width,jdbcType=INTEGER}                                  "
            + "     where t.page_code = #{p1.page_code,jdbcType=VARCHAR}                                    "
            + "       and t.staff_id = #{p1.staff_id,jdbcType=BIGINT}                                       "
            + "       and t.column_index = #{p1.column_index,jdbcType=VARCHAR}                              "
            + "       and (t.type = #{p1.type,jdbcType=VARCHAR} or #{p1.type,jdbcType=VARCHAR} is null)     "
            + "                                                                          "
    )
    int saveColumnsSize(@Param("p1") SColumnSizeVo entity);


    /**
     * 删除保存
     * @param entity
     * @return
     */
    @Delete("                                                                                           "
            + "    delete                                                                                   "
            + "      from s_column_size t                                                                   "
            + "     where t.page_code = #{p1.page_code,jdbcType=VARCHAR}                                    "
            + "       and t.staff_id = #{p1.staff_id,jdbcType=BIGINT}                                       "
            + "                                                                                             "
    )
    void deleteColumnsSize(@Param("p1") SColumnSizeVo entity);
}
