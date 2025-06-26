package com.xinyirun.scm.core.system.mapper.log.operate;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.log.operate.SLogOperEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zxh
 * @since 2019-07-04
 */
@Repository
public interface SLogOperMapper extends BaseMapper<SLogOperEntity> {

    /**
     * 查询任意sql，仅在操作日志中工作
     * @param sql
     * @return
     */
    @SelectProvider(type= SLogOperMapper.OperationLogMapperProvider.class,method="selectAnyTalbeSQL")
    public Map<String,Object> selectAnyTalbe(@Param("sql")String sql);

    /**
     * 查询任意sql，仅在操作日志中工作
     */
    public static class OperationLogMapperProvider{
        public String selectAnyTalbeSQL(Map<String,String> map) {
            return map.get("sql");
        }
    }
}
