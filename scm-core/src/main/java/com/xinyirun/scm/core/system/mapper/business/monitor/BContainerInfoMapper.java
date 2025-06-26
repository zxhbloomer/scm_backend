package com.xinyirun.scm.core.system.mapper.business.monitor;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.busniess.monitor.BContainerInfoEntity;
import com.xinyirun.scm.bean.system.vo.business.monitor.BContainerInfoVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2022-02-12
 */
@Repository
public interface BContainerInfoMapper extends BaseMapper<BContainerInfoEntity> {
    String common_select = "  "
            + "     SELECT                                                                                              "
            + "     	t1.id,                                                                                          "
            + "     	t1.code,                                                                                        "
            + "     	t1.waybill_code,                                                                                "
            + "     	t1.serial_type,                                                                                 "
            + "     	t1.serial_id,                                                                                   "
            + "     	t1.gross_weight,                                                                                "
            + "     	t1.tare_weight,                                                                                 "
            + "     	t1.net_weight,                                                                                  "
            + "     	t1.file_one,                                                                                    "
            + "     	t1.file_two,                                                                                    "
            + "     	t1.file_three,                                                                                  "
            + "     	t1.file_four                                                                                    "
            + "     FROM                                                                                                "
            + "     	b_container_info t1                                                                             "
            + "    ";


    /**
     * 页面查询列表
     */
    @Select("    "
            + common_select
            + "  where true                                                                                             "
            + "    and t1.serial_id = #{p1,jdbcType=VARCHAR}                                                            "
            + "    and t1.serial_type = #{p2,jdbcType=INTEGER}                                                          "
            + "      ")
    List<BContainerInfoVo> selectList(@Param("p1") int serial_id, @Param("p2") String serial_type);

    /**
     * 修改附件
     */
//    @Update("    "
//            +"	UPDATE b_container_info t                                                                               "
//            +"		SET t.file_one = #{p1.file_one,jdbcType=INTEGER},                                                   "
//            +"		t.file_two = #{p1.file_two,jdbcType=INTEGER},                                                       "
//            +"		t.file_three = #{p1.file_three,jdbcType=INTEGER},                                                   "
//            +"		t.file_four = #{p1.file_four,jdbcType=INTEGER}                                                      "
//            +"	WHERE                                                                                                   "
//            +"		t.id = #{p1.id,jdbcType=INTEGER}                                                                    "
//            + "     ")
//    void updateForFile(@Param("p1") BContainerInfoEntity entity);
}
