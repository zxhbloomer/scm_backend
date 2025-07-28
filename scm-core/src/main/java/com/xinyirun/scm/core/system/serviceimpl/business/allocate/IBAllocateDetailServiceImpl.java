package com.xinyirun.scm.core.system.serviceimpl.business.allocate;

import com.xinyirun.scm.bean.entity.business.allocate.BAllocateDetailEntity;
import com.xinyirun.scm.bean.entity.business.allocate.BAllocateEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.vo.business.allocate.BAllocateDetailVo;
import com.xinyirun.scm.bean.system.vo.business.allocate.BAllocateVo;
import com.xinyirun.scm.bean.system.vo.master.customer.MCustomerVo;
import com.xinyirun.scm.bean.system.vo.master.customer.MOwnerVo;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.system.mapper.business.allocate.BAllocateDetailMapper;
import com.xinyirun.scm.core.system.mapper.business.allocate.BAllocateMapper;
import com.xinyirun.scm.core.system.service.business.allocate.IBAllocateDetailService;
import com.xinyirun.scm.core.system.service.master.customer.IMCustomerService;
import com.xinyirun.scm.core.system.service.master.customer.IMOwnerService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import com.xinyirun.scm.core.system.serviceimpl.business.todo.TodoService;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.BAllocateAutoCodeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 库存调整 服务实现类
 * </p>
 *
 * @author wwl
 * @since 2021-12-09
 */
@Service
public class IBAllocateDetailServiceImpl extends BaseServiceImpl<BAllocateDetailMapper, BAllocateDetailEntity> implements IBAllocateDetailService {
    
    @Autowired
    private BAllocateDetailMapper mapper;

    @Autowired
    private BAllocateMapper bAllocateMapper;

    @Autowired
    private IMCustomerService imCustomerService;

    @Autowired
    private IMOwnerService imOwnerService;

    @Autowired
    private BAllocateAutoCodeServiceImpl autoCode;

    @Autowired
    private TodoService todoService;

    @Override
    public InsertResultAo<Integer> insert(BAllocateVo vo) {
        BAllocateEntity entity = new BAllocateEntity();
        BeanUtilsSupport.copyProperties(vo, entity);
        entity.setOut_warehouse_id(vo.getOut_warehouse_id());

        MOwnerVo outOwner = imOwnerService.selectById(entity.getOut_owner_id());
        MCustomerVo outConsignor = imCustomerService.selectByCreditNo(outOwner.getCredit_no());
        if (outConsignor != null) {
            entity.setOut_consignor_id(outConsignor.getId());
            entity.setOut_consignor_code(outConsignor.getCode());
        }

        MOwnerVo inOwner = imOwnerService.selectById(entity.getIn_owner_id());
        MCustomerVo inConsignor = imCustomerService.selectByCreditNo(inOwner.getCredit_no());
        if (inConsignor != null) {
            entity.setIn_consignor_id(inConsignor.getId());
            entity.setIn_consignor_code(inConsignor.getCode());
        }

        // 生成单号
        String code = autoCode.autoCode().getCode();
        entity.setCode(code);

        int rtn = bAllocateMapper.insert(entity);
        vo.setId(entity.getId());

        for (BAllocateDetailVo detailVo: vo.getDetailList()) {
            BAllocateDetailEntity detailEntity = new BAllocateDetailEntity();
            BeanUtilsSupport.copyProperties(detailVo, detailEntity);
            detailEntity.setAllocate_id(entity.getId());
            detailEntity.setStatus(DictConstant.DICT_B_ALLOCATE_STATUS_SAVED);
            mapper.insert(detailEntity);

            // 生成待办
            todoService.insertTodo(detailEntity.getId(), SystemConstants.SERIAL_TYPE.B_ALLOCATE_DETAIL, SystemConstants.PERMS.B_ALLOCATE_AUDIT);

        }
        return InsertResultUtil.OK(rtn);
    }

    @Override
    public UpdateResultAo<Integer> update(BAllocateVo vo) {
        return null;
    }
}
