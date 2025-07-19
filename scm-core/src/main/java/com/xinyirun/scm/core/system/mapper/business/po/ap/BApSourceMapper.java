package com.xinyirun.scm.core.system.mapper.business.po.ap;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.busniess.po.ap.BApSourceEntity;
import com.xinyirun.scm.bean.system.vo.business.po.ap.BApSourceVo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 应付账款关联单据表-源单 Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-26
 */
@Repository
public interface BApSourceMapper extends BaseMapper<BApSourceEntity> {

    /**
     * 根据应付账款ID删除源单数据
     */
    @Delete("""
            DELETE FROM b_ap_source t where t.ap_id = #{ap_id}
            """)
    void deleteByApId(Integer ap_id);

    /**
     * 根据ap_id查询源单
     */
    @Select("SELECT * FROM b_ap_source t WHERE t.ap_id = #{ap_id}")
    List<BApSourceVo> selectByApId(@Param("ap_id") Integer ap_id);

}
