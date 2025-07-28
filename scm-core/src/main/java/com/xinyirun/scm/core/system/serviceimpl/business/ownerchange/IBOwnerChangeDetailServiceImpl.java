package com.xinyirun.scm.core.system.serviceimpl.business.ownerchange;

import com.xinyirun.scm.bean.entity.business.ownerchange.BOwnerChangeDetailEntity;
import com.xinyirun.scm.bean.entity.business.ownerchange.BOwnerChangeEntity;
import com.xinyirun.scm.bean.entity.sys.file.SFileEntity;
import com.xinyirun.scm.bean.entity.sys.file.SFileInfoEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.UpdateResultUtil;
import com.xinyirun.scm.bean.system.vo.business.ownerchange.BOwnerChangeDetailVo;
import com.xinyirun.scm.bean.system.vo.business.ownerchange.BOwnerChangeVo;
import com.xinyirun.scm.bean.system.vo.master.customer.MCustomerVo;
import com.xinyirun.scm.bean.system.vo.master.customer.MOwnerVo;
import com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.system.mapper.business.ownerchange.BOwnerChangeDetailMapper;
import com.xinyirun.scm.core.system.mapper.business.ownerchange.BOwnerChangeMapper;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileInfoMapper;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileMapper;
import com.xinyirun.scm.core.system.service.business.ownerchange.IBOwnerChangeDetailService;
import com.xinyirun.scm.core.system.service.master.customer.IMCustomerService;
import com.xinyirun.scm.core.system.service.master.customer.IMOwnerService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import com.xinyirun.scm.core.system.serviceimpl.business.todo.TodoService;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.BOwnerChangeAutoCodeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 库存调整 服务实现类
 * </p>
 *
 * @author wwl
 * @since 2021-12-09
 */
@Service
public class IBOwnerChangeDetailServiceImpl extends BaseServiceImpl<BOwnerChangeDetailMapper, BOwnerChangeDetailEntity> implements IBOwnerChangeDetailService {

    @Autowired
    private BOwnerChangeDetailMapper mapper;

    @Autowired
    private BOwnerChangeMapper ownerChangeMapper;

    @Autowired
    private IMCustomerService imCustomerService;

    @Autowired
    private IMOwnerService imOwnerService;

    @Autowired
    private BOwnerChangeAutoCodeServiceImpl autoCode;

    @Autowired
    private TodoService todoService;

    @Autowired
    private SFileMapper fileMapper;

    @Autowired
    private SFileInfoMapper fileInfoMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<Integer> insert(BOwnerChangeVo vo) {
        BOwnerChangeEntity entity = new BOwnerChangeEntity();
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

        int rtn = ownerChangeMapper.insert(entity);
        vo.setId(entity.getId());

        for (BOwnerChangeDetailVo detailVo: vo.getDetailList()) {
            BOwnerChangeDetailEntity detailEntity = new BOwnerChangeDetailEntity();
            BeanUtilsSupport.copyProperties(detailVo, detailEntity);
            detailEntity.setOwner_change_id(entity.getId());
            detailEntity.setStatus(DictConstant.DICT_B_OWNER_CHANGE_STATUS_SAVED);
            mapper.insert(detailEntity);

            // 生成待办
            todoService.insertTodo(detailEntity.getId(), SystemConstants.SERIAL_TYPE.B_OWNERCHANGE_DETAIL, SystemConstants.PERMS.B_OWNER_CHANGE_AUDIT);

        }

        // 保存附件信息
        insertFiles(entity, vo);

        ownerChangeMapper.updateById(entity);

        vo.setId(entity.getId());

        return InsertResultUtil.OK(rtn);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> update(BOwnerChangeVo vo) {
        BOwnerChangeEntity entity = ownerChangeMapper.selectById(vo.getOwner_change_id());
        BeanUtilsSupport.copyProperties(vo, entity, new String[]{"id"});
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

        for (BOwnerChangeDetailVo detailVo: vo.getDetailList()) {
            BOwnerChangeDetailEntity detailEntity = mapper.selectById(detailVo.getId());
            BeanUtilsSupport.copyProperties(detailVo, detailEntity);
            detailEntity.setOwner_change_id(entity.getId());
            detailEntity.setStatus(DictConstant.DICT_B_OWNER_CHANGE_STATUS_SAVED);
            mapper.updateById(detailEntity);

        }

        // 保存附件信息
        insertFiles(entity, vo);
        int rtn = ownerChangeMapper.updateById(entity);
        vo.setId(entity.getId());

        return UpdateResultUtil.OK(rtn);
    }

    /**
     * 附件新增逻辑
     */
    public void insertFiles(BOwnerChangeEntity entity, BOwnerChangeVo vo) {
        // 磅单附件新增
        if(vo.getFile_files() != null && vo.getFile_files().size() > 0) {
            // 附件主表
            SFileEntity fileEntity = new SFileEntity();
            fileEntity.setSerial_id(entity.getId());
            fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_OWNER_CHANGE);
            // 主表新增
            fileMapper.insert(fileEntity);
            // 详情表新增
            for(SFileInfoVo fileInfoVo:vo.getFile_files()) {
                insertFileInfo(fileEntity,fileInfoVo);
            }
            // 磅单附件id
            entity.setFiles(fileEntity.getId());
        }
    }

    /**
     * 新增附件详情数据
     */
    public void insertFileInfo(SFileEntity fileEntity,SFileInfoVo fileInfoVo) {
        SFileInfoEntity fileInfoEntity = new SFileInfoEntity();
        fileInfoVo.setF_id(fileEntity.getId());
        BeanUtilsSupport.copyProperties(fileInfoVo,fileInfoEntity);
        fileInfoMapper.insert(fileInfoEntity);
    }

}
