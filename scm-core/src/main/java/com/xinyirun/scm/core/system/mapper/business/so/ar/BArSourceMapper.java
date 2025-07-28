package com.xinyirun.scm.core.system.mapper.business.so.ar;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.business.so.ar.BArSourceEntity;
import com.xinyirun.scm.bean.system.vo.business.so.ar.BArSourceVo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 应收账款关联单据表-源单 Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-26
 */
@Repository
public interface BArSourceMapper extends BaseMapper<BArSourceEntity> {

    /**
     * 根据应收账款ID删除源单数据
     */
    @Delete("""
            DELETE FROM b_ar_source t where t.ar_id = #{ar_id}
            """)
    void deleteByArId(Integer ar_id);

    /**
     * 根据ar_id查询源单
     */
    @Select("SELECT * FROM b_ar_source t WHERE t.ar_id = #{ar_id}")
    List<BArSourceVo> selectByArId(@Param("ar_id") Integer ar_id);

}