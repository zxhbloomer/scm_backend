package com.xinyirun.scm.core.system.serviceimpl.mongobackup.file;

import com.xinyirun.scm.bean.entity.sys.file.SFileInfoEntity;
import com.xinyirun.scm.bean.system.vo.clickhouse.file.SFileMonitorInfoMongoVo;
import com.xinyirun.scm.core.system.mapper.mongobackup.file.SFileInfoMongoMapper;
import com.xinyirun.scm.core.system.service.mongobackup.file.ISFileInfoMongoService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 附件详情 服务实现类
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Service
@Slf4j
public class SFileInfoMongoServiceImpl extends BaseServiceImpl<SFileInfoMongoMapper, SFileInfoEntity> implements ISFileInfoMongoService {

    @Autowired
    private SFileInfoMongoMapper mapper;

    /**
     * 根据文件 id 查询
     *
     * @param id
     * @return
     */
    @Override
    public SFileMonitorInfoMongoVo selectFId(Integer id) {
        return mapper.selectFId(id);
    }


}
