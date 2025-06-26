package com.xinyirun.scm.core.system.serviceimpl.business.allocate;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.busniess.allocate.BAllocateDetailEntity;
import com.xinyirun.scm.bean.entity.busniess.allocate.BAllocateEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.CheckResultUtil;
import com.xinyirun.scm.bean.system.vo.business.allocate.BAllocateVo;
import com.xinyirun.scm.bean.utils.security.SecurityUtil;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.core.system.mapper.business.allocate.BAllocateDetailMapper;
import com.xinyirun.scm.core.system.mapper.business.allocate.BAllocateMapper;
import com.xinyirun.scm.core.system.service.business.allocate.IBAllocateService;
import com.xinyirun.scm.core.system.service.wms.inplan.IBInPlanDetailService;
import com.xinyirun.scm.core.system.service.business.out.IBOutPlanDetailService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import com.xinyirun.scm.core.system.serviceimpl.business.todo.TodoService;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 库存调整 服务实现类
 * </p>
 *
 * @author wwl
 * @since 2021-12-09
 */
@Service
public class IBAllocateServiceImpl extends BaseServiceImpl<BAllocateMapper, BAllocateEntity> implements IBAllocateService {

    @Autowired
    private BAllocateMapper bAllocateMapper;

    @Autowired
    private BAllocateDetailMapper bAllocateDetailMapper;

    @Autowired
    private IBInPlanDetailService ibInPlanDetailService;

    @Autowired
    private IBOutPlanDetailService ibOutPlanDetailService;

    @Autowired
    private TodoService todoService;

    /**
     * 查询分页列表
     */
    @Override
    public IPage<BAllocateVo> selectPage(BAllocateVo searchCondition) {
        // 分页条件
        Page<BAllocateDetailEntity> pageCondition = new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());

        searchCondition.setStaff_id(SecurityUtil.getStaff_id());

        // 查询入库计划page
        IPage<BAllocateVo> result = bAllocateMapper.selectPage(pageCondition, searchCondition);
        return result;
    }

    /**
     * 查询调整单数据
     */
    @Override
    public BAllocateVo get(BAllocateVo vo) {
        // 查询调整单page
        BAllocateVo allocateVo = bAllocateMapper.get(vo.getId());
        // 查询调整单明细list
        allocateVo.setDetailList(bAllocateDetailMapper.getAllocateDetailList(allocateVo));
        return allocateVo;
    }


    /**
     * id查询返回入库计划更新对象
     */
    @Override
    public BAllocateVo selectById(int id) {
        return bAllocateMapper.selectId(id);
    }

    /**
     * 删除
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(List<BAllocateVo> searchCondition) {
        int updCount = 0;

        List<BAllocateDetailEntity> list = bAllocateMapper.selectIds(searchCondition);
        for(BAllocateDetailEntity entity : list) {
            // check
            checkLogic(entity,CheckResultAo.DELETE_CHECK_TYPE);
            updCount = bAllocateDetailMapper.deleteById(entity.getId());
            if(updCount == 0){
                throw new UpdateErrorException("您提交的数据已经被修改，请查询后重新编辑更新。");
            }
        }
    }

    /**
     * check逻辑
     */
    public CheckResultAo checkLogic(BAllocateDetailEntity entity, String moduleType) {
        switch (moduleType) {
            case CheckResultAo.AUDIT_CHECK_TYPE:
                // 是否已提交状态
                if( Objects.equals(entity.getStatus(), DictConstant.DICT_B_ALLOCATE_STATUS_PASSED)) {
                    throw new BusinessException("无法重复审核");
                }
                break;
            case CheckResultAo.DELETE_CHECK_TYPE:
                // 是否已审核状态
                if(Objects.equals(entity.getStatus(), DictConstant.DICT_B_ALLOCATE_STATUS_PASSED)) {
                    throw new BusinessException("审核已通过的数据无法删除");
                }
                break;
            default:
                break;
        }
        return CheckResultUtil.OK();
    }

}
