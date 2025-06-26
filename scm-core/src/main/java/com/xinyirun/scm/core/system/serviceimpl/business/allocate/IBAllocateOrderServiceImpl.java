package com.xinyirun.scm.core.system.serviceimpl.business.allocate;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.busniess.allocate.BAllocateOrderEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.DeleteResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.CheckResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.DeleteResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.UpdateResultUtil;
import com.xinyirun.scm.bean.system.vo.business.allocate.BAllocateOrderVo;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.system.mapper.business.allocate.BAllocateOrderMapper;
import com.xinyirun.scm.core.system.service.business.allocate.IBAllocateOrderService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 入库订单 服务实现类
 * </p>
 *
 * @author htt
 * @since 2021-11-02
 */
@Service
public class IBAllocateOrderServiceImpl extends BaseServiceImpl<BAllocateOrderMapper, BAllocateOrderEntity> implements IBAllocateOrderService {

    @Autowired
    private BAllocateOrderMapper mapper;

    /**
     * 查询分页列表
     * @param searchCondition
     * @return
     */
    @Override
    public IPage<BAllocateOrderVo> selectPage(BAllocateOrderVo searchCondition) {
        // 分页条件
        Page<BAllocateOrderEntity> pageCondition = new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());

        return mapper.selectPage(pageCondition, searchCondition);
    }

    @Override
    public BAllocateOrderVo selectById(int id) {
        return mapper.selectId(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<Integer> insert(BAllocateOrderVo vo) {
        // 插入前check
        CheckResultAo cr = checkLogic(vo, CheckResultAo.INSERT_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }

        // 插入逻辑保存
        BAllocateOrderEntity entity = (BAllocateOrderEntity) BeanUtilsSupport.copyProperties(vo, BAllocateOrderEntity.class);
        int rtn = mapper.insert(entity);
        vo.setId(entity.getId());

        // 插入逻辑保存
        return InsertResultUtil.OK(rtn);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> update(BAllocateOrderVo vo) {
        // 更新前check
        CheckResultAo cr = checkLogic(vo, CheckResultAo.UPDATE_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }

        BAllocateOrderEntity entity = (BAllocateOrderEntity) BeanUtilsSupport.copyProperties(vo, BAllocateOrderEntity.class);
        int updCount = mapper.updateById(entity);
        if(updCount == 0){
            throw new UpdateErrorException("您提交的数据已经被修改，请查询后重新编辑更新。");
        }
        return UpdateResultUtil.OK(updCount);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DeleteResultAo<Integer> delete(BAllocateOrderVo vo) {
        int delCount = mapper.deleteById(vo.getId());
        if(delCount == 0){
            throw new UpdateErrorException("您提交的数据不存在，请查询后重新操作。");
        }
        return DeleteResultUtil.OK(delCount);
    }


    /**
     * check逻辑
     */
    public CheckResultAo checkLogic(BAllocateOrderVo vo, String moduleType) {
        BAllocateOrderVo checkOrderVo = (BAllocateOrderVo)BeanUtilsSupport.copyProperties(vo,BAllocateOrderVo.class);
        // 按合同编号和订单编号查询是否存在数据
        List<BAllocateOrderEntity> selectByOrder = mapper.selectOrderByContract(checkOrderVo);

        switch (moduleType) {
            case CheckResultAo.INSERT_CHECK_TYPE:
                // 新增场合，不能重复
                if (selectByOrder.size() >= 1) {
                    return CheckResultUtil.NG("新增保存出错：订单编号："+vo.getOrder_no()+",合同编号："+vo.getContract_no()+"出现重复");
                }
                break;
            case CheckResultAo.UPDATE_CHECK_TYPE:
                // 更新场合，不能重复设置
                if (selectByOrder.size() >= 2) {
                    return CheckResultUtil.NG("新增保存出错：订单编号："+vo.getOrder_no()+",合同编号："+vo.getContract_no()+"出现重复");
                }
                break;
            default:
        }
        return CheckResultUtil.OK();
    }
}
