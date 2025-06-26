package com.xinyirun.scm.core.system.serviceimpl.master.vehicle;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
//import com.xinyirun.scm.bean.app.vo.master.vehicle.AppMVehicleVo;
import com.xinyirun.scm.bean.entity.busniess.monitor.BMonitorEntity;
import com.xinyirun.scm.bean.entity.master.vehicle.MVehicleEntity;
import com.xinyirun.scm.bean.entity.sys.file.SFileEntity;
import com.xinyirun.scm.bean.entity.sys.file.SFileInfoEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.CheckResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.UpdateResultUtil;
import com.xinyirun.scm.bean.system.vo.business.track.BVehicleValidateVo;
import com.xinyirun.scm.bean.system.vo.master.vehicle.MVehicleVo;
import com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.system.mapper.business.monitor.BMonitorMapper;
import com.xinyirun.scm.core.system.mapper.master.vehicle.MVehicleMapper;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileInfoMapper;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileMapper;
import com.xinyirun.scm.core.system.service.master.vehicle.MVehicleService;
import com.xinyirun.scm.core.system.service.track.gsh56.IBTrackGsh56Service;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.MVehicleAutoCodeServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Service
public class MVehicleServiceImpl extends BaseServiceImpl<MVehicleMapper, MVehicleEntity> implements MVehicleService {

    @Autowired
    private MVehicleMapper mapper;

    @Autowired
    private SFileMapper fileMapper;

    @Autowired
    private SFileInfoMapper fileInfoMapper;

    @Autowired
    private MVehicleAutoCodeServiceImpl autoCodeService;

    @Autowired
    private BMonitorMapper bMonitorMapper;

    @Autowired
    private IBTrackGsh56Service ibTrackGsh56Service;

    /**
     * 查询分页数据
     */
    @Override
    public IPage<MVehicleVo> selectPage(MVehicleVo searchCondition) {
        // 分页条件
        Page<MVehicleEntity> pageCondition =
                new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());

        IPage<MVehicleVo> pages = mapper.selectPage(pageCondition, searchCondition);
        List<MVehicleVo> list = new ArrayList<>();
        for (MVehicleVo vo : pages.getRecords()) {
            list.add(getFile(vo));
        }
        pages.setRecords(list);

        return pages;
    }

    /**
     * 获取详情数据
     */
    @Override
    public MVehicleVo getDetail(MVehicleVo searchCondition) {
        MVehicleVo resultVo = mapper.getDetail(searchCondition);
        return getFile(resultVo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enable(List<MVehicleVo> searchCondition) {
        for (MVehicleVo vo : searchCondition) {
            MVehicleEntity entity = mapper.selectById(vo.getId());
            if (entity.getIs_del() == Boolean.FALSE) {
                List<BMonitorEntity> selectMonitor = bMonitorMapper.selectByVehicleId(entity.getId());
                if (selectMonitor.size() > 0) {
                    throw new BusinessException("删除出错：该车辆信息被监管任务使用中");
                }

                entity.setIs_del(Boolean.TRUE);
            } else {
                MVehicleVo vehicleVo = (MVehicleVo) BeanUtilsSupport.copyProperties(entity, MVehicleVo.class);
                CheckResultAo cr = checkLogic(vehicleVo, CheckResultAo.ENABLE_CHECK_TYPE);
                if (!cr.isSuccess()) {
                    throw new BusinessException(cr.getMessage());
                }

                entity.setIs_del(Boolean.FALSE);
            }
            mapper.updateById(entity);
        }
    }

    /**
     * 新增
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<Integer> insert(MVehicleVo vo) {
        // 插入前check
        CheckResultAo cr = checkLogic(vo, CheckResultAo.INSERT_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }
        MVehicleVo mVehicleVo = new MVehicleVo();
        mVehicleVo.setNo(vo.getNo());
        mVehicleVo.setNo_color(vo.getNo_color());
        validateVehicle(mVehicleVo, vo);

        // 插入逻辑保存
        MVehicleEntity entity = (MVehicleEntity) BeanUtilsSupport.copyProperties(vo, MVehicleEntity.class);
        entity.setCode(autoCodeService.autoCode().getCode());
        entity.setIs_del(Boolean.FALSE);
        entity.setNo(entity.getNo().toUpperCase());
        int rtn = mapper.insert(entity);
        vo.setId(entity.getId());

        // 附件新增逻辑
        insertFile(vo,entity);
        mapper.updateById(entity);
        // 插入逻辑保存
        return InsertResultUtil.OK(rtn);
    }

    /**
     * 验车逻辑
     */
    private void validateVehicle(MVehicleVo appVo, MVehicleVo vo) {
        // 腾颢
//        BVehicleValidateVo bVehicleValidateVo = ibTrackGsh56Service.checkVehicleExist(appVo);
//        vo.setValidate_log(bVehicleValidateVo.getValidate_log());
//        if (bVehicleValidateVo != null && bVehicleValidateVo.getData() != null && bVehicleValidateVo.getData().getFlag()) {
//            vo.setValidate_status("1");
//        } else {
//            vo.setValidate_status("2");
//        }
    }


    /**
     * 修改
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> update(MVehicleVo vo) {
        // 更新前check
        CheckResultAo cr = checkLogic(vo, CheckResultAo.UPDATE_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }

        MVehicleVo mVehicleVo = new MVehicleVo();
        mVehicleVo.setNo(vo.getNo());
        mVehicleVo.setNo_color(vo.getNo_color());
        validateVehicle(mVehicleVo, vo);

        MVehicleEntity entity = (MVehicleEntity) BeanUtilsSupport.copyProperties(vo, MVehicleEntity.class);

        // 先删除附件再新增
//        deleteFile(entity);
        insertFile(vo,entity);
        entity.setNo(entity.getNo().toUpperCase());
        int updCount = mapper.updateById(entity);
        if(updCount == 0){
            throw new UpdateErrorException("您提交的数据已经被修改，请查询后重新编辑更新。");
        }
        return UpdateResultUtil.OK(updCount);
    }

    /**
     * check逻辑
     */
    public CheckResultAo checkLogic(MVehicleVo vo, String moduleType) {
        List<MVehicleEntity> selectByCarNo = mapper.selectByCarNo(vo);

        switch (moduleType) {
            case CheckResultAo.INSERT_CHECK_TYPE:
                // 新增场合，不能重复
                if (!selectByCarNo.isEmpty()) {
                    return CheckResultUtil.NG("新增保存出错：车牌号出现重复", vo.getNo());
                }
                break;
            case CheckResultAo.UPDATE_CHECK_TYPE:
                // 新增场合，不能重复
                if (!selectByCarNo.isEmpty()) {
                    return CheckResultUtil.NG("更新保存出错：车牌号出现重复", vo.getNo());
                }
                break;
            case CheckResultAo.ENABLE_CHECK_TYPE:
                // 更新场合，不能重复设置
                if (!selectByCarNo.isEmpty()) {
                    return CheckResultUtil.NG("启用出错：车牌号出现重复", vo.getNo());
                }
                break;
            default:
        }
        return CheckResultUtil.OK();
    }

    /**
     * id查询
     */
    @Override
    public MVehicleVo selectById(int id) {
        return mapper.selectId(id);
    }

    /**
     * 删除车辆
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(MVehicleVo searchCondition) {
        int result = mapper.deleteById(searchCondition.getId());
        if(result != 2){
            throw new UpdateErrorException("您删除的数据不存在，请查询后重新编辑更新。");
        }
    }

    /**
     * 查询附件
     */
    public MVehicleVo getFile(MVehicleVo vo){
        if(vo.getLicense_front() != null) {
            SFileEntity file = fileMapper.selectById(vo.getLicense_front());
            List<SFileInfoEntity> fileInfos = fileInfoMapper.selectList(new QueryWrapper<SFileInfoEntity>().eq("f_id",file.getId()));
            for(SFileInfoEntity fileInfo:fileInfos) {
                SFileInfoVo fileInfoVo = (SFileInfoVo)BeanUtilsSupport.copyProperties(fileInfo, SFileInfoVo.class);
                fileInfoVo.setFileName(fileInfoVo.getFile_name());
                vo.setLicense_frontVo(fileInfoVo);
            }
        }
        if(vo.getLicense_back() != null) {
            SFileEntity file = fileMapper.selectById(vo.getLicense_back());
            List<SFileInfoEntity> fileInfos = fileInfoMapper.selectList(new QueryWrapper<SFileInfoEntity>().eq("f_id",file.getId()));
            for(SFileInfoEntity fileInfo:fileInfos) {
                SFileInfoVo fileInfoVo = (SFileInfoVo)BeanUtilsSupport.copyProperties(fileInfo, SFileInfoVo.class);
                fileInfoVo.setFileName(fileInfoVo.getFile_name());
                vo.setLicense_backVo(fileInfoVo);
            }
        }
        return vo;
    }

    /**
     * 删除附件
     */
//    public void deleteFile(MVehicleEntity entity) {
//        if(entity.getDriver_front_file_id() != null) {
//            // 删除附件主从表、法人认证字段置空
//            fileMapper.deleteById(entity.getDriver_front_file_id());
//            fileInfoMapper.delete(new QueryWrapper<SFileInfoEntity>().eq("f_id",entity.getDriver_front_file_id()));
//            entity.setDriver_front_file_id(null);
//        }
//        if(entity.getDriver_back_file_id() != null) {
//            // 删除附件主从表、法人认证字段置空
//            fileMapper.deleteById(entity.getDriver_back_file_id());
//            fileInfoMapper.delete(new QueryWrapper<SFileInfoEntity>().eq("f_id",entity.getDriver_back_file_id()));
//            entity.setDriver_back_file_id(null);
//        }
//    }

    /**
     * 附件新增逻辑
     */
    public void insertFile(MVehicleVo vo, MVehicleEntity entity) {
        // 附件主表
        SFileEntity fileEntity = new SFileEntity();
        fileEntity.setSerial_id(entity.getId());
        fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_M_VEHICLE);
        // 证件正面附件新增
        if(vo.getLicense_frontVo() != null ) {
            // 主表新增
            fileMapper.insert(fileEntity);
            // 详情表新增
            SFileInfoEntity fileInfoEntity = new SFileInfoEntity();
            vo.getLicense_frontVo().setF_id(fileEntity.getId());
            BeanUtilsSupport.copyProperties(vo.getLicense_frontVo(),fileInfoEntity);
            fileInfoEntity.setFile_name(vo.getLicense_frontVo().getFileName());
            if (fileInfoEntity.getId() != null) {
                fileInfoMapper.updateById(fileInfoEntity);
            } else {
                fileInfoMapper.insert(fileInfoEntity);
            }
            // 法人身份反面附件id
            entity.setLicense_front(fileEntity.getId());
            fileEntity.setId(null);
        }
        // 证件反面附件新增
        if(vo.getLicense_backVo() != null ) {
            // 主表新增
            fileMapper.insert(fileEntity);
            // 详情表新增
            SFileInfoEntity fileInfoEntity = new SFileInfoEntity();
            vo.getLicense_backVo().setF_id(fileEntity.getId());
            BeanUtilsSupport.copyProperties(vo.getLicense_backVo(),fileInfoEntity);
            fileInfoEntity.setFile_name(vo.getLicense_backVo().getFileName());
            if (fileInfoEntity.getId() != null) {
                fileInfoMapper.updateById(fileInfoEntity);
            } else {
                fileInfoMapper.insert(fileInfoEntity);
            }

            // 法人身份正面附件id
            entity.setLicense_back(fileEntity.getId());
            fileEntity.setId(null);
        }
    }
}
