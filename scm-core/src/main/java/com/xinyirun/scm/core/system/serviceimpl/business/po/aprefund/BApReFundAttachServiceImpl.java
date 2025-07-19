package com.xinyirun.scm.core.system.serviceimpl.business.po.aprefund;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.busniess.po.aprefund.BApReFundAttachEntity;
import com.xinyirun.scm.bean.system.ao.result.DeleteResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.DeleteResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.UpdateResultUtil;
import com.xinyirun.scm.bean.system.vo.business.po.aprefund.BApReFundAttachVo;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.system.mapper.business.po.aprefund.BApReFundAttachMapper;
import com.xinyirun.scm.core.system.service.business.po.aprefund.IBApReFundAttachService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 应付退款附件表 服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2025-07-17
 */
@Slf4j
@Service
public class BApReFundAttachServiceImpl extends ServiceImpl<BApReFundAttachMapper, BApReFundAttachEntity> implements IBApReFundAttachService {

    @Autowired
    private BApReFundAttachMapper mapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<BApReFundAttachVo> insert(BApReFundAttachVo vo) {
        log.debug("====》应付退款附件新增，开始《====");
        
        BApReFundAttachEntity entity = (BApReFundAttachEntity) BeanUtilsSupport.copyProperties(vo, BApReFundAttachEntity.class);
        int result = mapper.insert(entity);
        if (result <= 0) {
            throw new UpdateErrorException("新增应付退款附件失败");
        }
        
        vo.setId(entity.getId());
        log.debug("====》应付退款附件新增，结束《====");
        return InsertResultUtil.OK(vo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<BApReFundAttachVo> update(BApReFundAttachVo vo) {
        log.debug("====》应付退款附件更新，开始《====");
        
        BApReFundAttachEntity entity = (BApReFundAttachEntity) BeanUtilsSupport.copyProperties(vo, BApReFundAttachEntity.class);
        int result = mapper.updateById(entity);
        if (result <= 0) {
            throw new UpdateErrorException("更新应付退款附件失败");
        }
        
        log.debug("====》应付退款附件更新，结束《====");
        return UpdateResultUtil.OK(vo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DeleteResultAo<Integer> delete(BApReFundAttachVo vo) {
        log.debug("====》应付退款附件删除，开始《====");
        
        int result = mapper.deleteById(vo.getId());
        if (result <= 0) {
            throw new UpdateErrorException("删除应付退款附件失败");
        }
        
        log.debug("====》应付退款附件删除，结束《====");
        return DeleteResultUtil.OK(result);
    }

    @Override
    public BApReFundAttachVo selectByApId(Integer apId) {
        log.debug("====》根据应付退款ID查询附件信息，apId: {}《====", apId);
        return mapper.selectByApId(apId);
    }

    @Override
    public List<BApReFundAttachVo> selectListByApId(Integer apId) {
        log.debug("====》根据应付退款ID查询附件列表，apId: {}《====", apId);
        return mapper.selectListByApId(apId);
    }

    @Override
    public BApReFundAttachVo selectByFileId(Integer fileId) {
        log.debug("====》根据文件ID查询附件信息，fileId: {}《====", fileId);
        return mapper.selectByFileId(fileId);
    }

}