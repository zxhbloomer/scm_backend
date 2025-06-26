package com.xinyirun.scm.core.system.mapper.master.cancel;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.master.cancel.MCancelEntity;
import com.xinyirun.scm.bean.system.vo.master.cancel.MCancelVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wwl
 * @since 2022-04-07
 */
@Repository
public interface MCancelMapper extends BaseMapper<MCancelEntity> {

    /**
     * 页面查询列表
     * @param page
     * @param searchCondition
     * @return
     */
    @Select("    "
            + "   select t.*                                                                                            "
            + "     from m_cancel t                                                                                     "
            + "   where true                                                                                            "
            + "       ")
    IPage<MCancelVo> selectPage(Page<MCancelVo> page, @Param("p1") MCancelVo searchCondition);

    /**
     * 根据serial_id和serial_type查询单条记录
     * @param searchCondition
     * @return
     */
    @Select("    "
            + "   select t.*                                                                                            "
            + "     from m_cancel t                                                                                     "
            + "   where true                                                                                            "
            + "   AND t.serial_id = #{p1.serial_id,jdbcType=INTEGER}                                                    "
            + "   AND t.serial_type = #{p1.serial_type,jdbcType=VARCHAR}                                                "
            + "   ORDER BY t.c_time DESC                                                                                "
            + "   LIMIT 1                                                                                               "
            + "       ")
    MCancelVo selectBySerialIdAndType(@Param("p1") MCancelVo searchCondition);

    /**
     * 页面查询列表
     * @param page
     * @param searchCondition
     * @return
     */
    @Select("    "
            + "   select t.*                                                                                            "
            + "     from m_cancel t                                                                                     "
            + "   where true                                                                                            "
            + "   AND t.serial_id = #{p1.serial_id,jdbcType=INTEGER}                                                    "
            + "   AND t.serial_type = #{p1.serial_type,jdbcType=VARCHAR}                                                "
            + "       ")
    List<MCancelEntity> selectList(@Param("p1") MCancelVo searchCondition);

    /**
     * 页面查询列表
     * @param page
     * @param searchCondition
     * @return
     */
    @Select("    "
            + "   DELETE                                                                                                "
            + "     from m_cancel t                                                                                     "
            + "   where true                                                                                            "
            + "   AND t.serial_id = #{p1.serial_id,jdbcType=INTEGER}                                                    "
            + "   AND t.serial_type = #{p1.serial_type,jdbcType=VARCHAR}                                                "
            + "       ")
    void deleteData(@Param("p1") MCancelVo searchCondition);

}
