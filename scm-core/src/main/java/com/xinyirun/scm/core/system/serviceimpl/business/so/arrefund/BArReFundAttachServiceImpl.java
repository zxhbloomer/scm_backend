package com.xinyirun.scm.core.system.serviceimpl.business.so.arrefund;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.business.so.arrefund.BArReFundAttachEntity;
import com.xinyirun.scm.bean.system.ao.result.DeleteResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.DeleteResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.UpdateResultUtil;
import com.xinyirun.scm.bean.system.vo.business.so.arrefund.BArReFundAttachVo;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.system.mapper.business.so.arrefund.BArReFundAttachMapper;
import com.xinyirun.scm.core.system.service.business.so.arrefund.IBArReFundAttachService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 应收退款附件表 服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2025-07-17
 */
@Slf4j
@Service
public class BArReFundAttachServiceImpl extends ServiceImpl<BArReFundAttachMapper, BArReFundAttachEntity> implements IBArReFundAttachService {

    @Autowired
    private BArReFundAttachMapper mapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<BArReFundAttachVo> insert(BArReFundAttachVo vo) {
        log.debug("====》应收退款附件新增，开始《====");
        
        BArReFundAttachEntity entity = (BArReFundAttachEntity) BeanUtilsSupport.copyProperties(vo, BArReFundAttachEntity.class);
        int result = mapper.insert(entity);
        if (result <= 0) {
            throw new UpdateErrorException("新增应收退款附件失败");
        }
        
        vo.setId(entity.getId());
        log.debug("====》应收退款附件新增，结束《====");
        return InsertResultUtil.OK(vo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<BArReFundAttachVo> update(BArReFundAttachVo vo) {
        log.debug("====》应收退款附件更新，开始《====");
        
        BArReFundAttachEntity entity = (BArReFundAttachEntity) BeanUtilsSupport.copyProperties(vo, BArReFundAttachEntity.class);
        int result = mapper.updateById(entity);
        if (result <= 0) {
            throw new UpdateErrorException("更新应收退款附件失败");
        }
        
        log.debug("====》应收退款附件更新，结束《====");
        return UpdateResultUtil.OK(vo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DeleteResultAo<Integer> delete(BArReFundAttachVo vo) {
        log.debug("====》应收退款附件删除，开始《====");
        
        int result = mapper.deleteById(vo.getId());
        if (result <= 0) {
            throw new UpdateErrorException("删除应收退款附件失败");
        }
        
        log.debug("====》应收退款附件删除，结束《====");
        return DeleteResultUtil.OK(result);
    }

    @Override
    public BArReFundAttachVo selectByArId(Integer arId) {
        log.debug("====》根据应收退款ID查询附件信息，arId: {}《====", arId);
        return mapper.selectByArId(arId);
    }

    @Override
    public List<BArReFundAttachVo> selectListByArId(Integer arId) {
        log.debug("====》根据应收退款ID查询附件列表，arId: {}《====", arId);
        return mapper.selectListByArId(arId);
    }

    @Override
    public BArReFundAttachVo selectByFileId(Integer fileId) {
        log.debug("====》根据文件ID查询附件信息，fileId: {}《====", fileId);
        return mapper.selectByFileId(fileId);
    }

}