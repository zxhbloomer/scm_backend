package com.xinyirun.scm.core.system.serviceimpl.master.driver;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.entity.business.monitor.BMonitorEntity;
import com.xinyirun.scm.bean.entity.master.driver.MDriverEntity;
import com.xinyirun.scm.bean.entity.sys.file.SFileEntity;
import com.xinyirun.scm.bean.entity.sys.file.SFileInfoEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.CheckResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.UpdateResultUtil;
import com.xinyirun.scm.bean.system.vo.master.driver.MDriverExportVo;
import com.xinyirun.scm.bean.system.vo.master.driver.MDriverVo;
import com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.system.mapper.business.monitor.BMonitorMapper;
import com.xinyirun.scm.core.system.mapper.master.driver.MDriverMapper;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileInfoMapper;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileMapper;
import com.xinyirun.scm.core.system.service.master.driver.IMDriverService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.MDriverAutoCodeServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
public class MDriverServiceImpl extends BaseServiceImpl<MDriverMapper, MDriverEntity> implements IMDriverService {
    @Autowired
    private MDriverMapper mapper;

    @Autowired
    private SFileMapper fileMapper;

    @Autowired
    private SFileInfoMapper fileInfoMapper;

    @Autowired
    private MDriverAutoCodeServiceImpl autoCodeService;

    @Autowired
    private BMonitorMapper bMonitorMapper;

    /**
     * 查询司机分页
     */
    @Override
    public IPage<MDriverVo> selectPage(MDriverVo searchCondition) {
        // 分页条件
        Page<MDriverEntity> pageCondition = new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());

        IPage<MDriverVo> list =  mapper.selectPage(pageCondition, searchCondition);

        for (MDriverVo vo : list.getRecords()) {

            setFile(vo);
        }

        return list;
    }

    public void setFile(MDriverVo vo) {
        SFileInfoVo fileInfoVo ;
        // 身份证正面附件
        if(vo.getId_card_front() != null) {
            // 查询附件对象
            fileInfoVo = getFileInfo(vo.getId_card_front());
            vo.setId_card_frontVo(fileInfoVo);
        } else {
            vo.setId_card_frontVo(new SFileInfoVo());
        }
        // 身份证背面附件
        if(vo.getId_card_back() != null) {
            // 查询附件对象
            fileInfoVo = getFileInfo(vo.getId_card_back());
            vo.setId_card_backVo(fileInfoVo);
        } else {
            vo.setId_card_backVo(new SFileInfoVo());
        }

        // 驾驶证附件
        if(vo.getDriver_license() != null) {
            // 查询附件对象
            fileInfoVo = getFileInfo(vo.getDriver_license());
            vo.setDriver_licenseVo(fileInfoVo);
        } else {
            vo.setDriver_licenseVo(new SFileInfoVo());
        }
    }

    /**
     * 查询附件对象
     */
    public SFileInfoVo getFileInfo(Integer id) {
        SFileEntity file = fileMapper.selectById(id);
        SFileInfoEntity fileInfo = fileInfoMapper.selectFIdEntity(file.getId());
        SFileInfoVo fileInfoVo = (SFileInfoVo) BeanUtilsSupport.copyProperties(fileInfo, SFileInfoVo.class);
        fileInfoVo.setFileName(fileInfoVo.getFile_name());
        return fileInfoVo;
    }

    /**
     * 获取司机
     */
    @Override
    public MDriverVo getDetail(MDriverVo searchCondition) {
        MDriverVo resultVo = mapper.getDetail(searchCondition);
        // set附件信息返回
        return getFile(resultVo);
    }

    /**
     * id查询
     */
    @Override
    public MDriverVo selectById(int id) {
        return mapper.selectId(id);
    }

    /**
     * 新增司机
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<Integer> insert(MDriverVo vo) {
        // 插入前check
        CheckResultAo cr = checkLogic(vo, CheckResultAo.INSERT_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }

        // 插入逻辑保存
        MDriverEntity entity = (MDriverEntity) BeanUtilsSupport.copyProperties(vo, MDriverEntity.class);
        entity.setCode(autoCodeService.autoCode().getCode());
        entity.setIs_del(Boolean.FALSE);

        int rtn = mapper.insert(entity);
        vo.setId(entity.getId());
        // 附件新增逻辑
        insertFile(vo, entity);
        // 插入逻辑保存
        mapper.updateById(entity);
        return InsertResultUtil.OK(rtn);
    }

    /**
     * 附件新增逻辑
     */
    public void insertFile(MDriverVo vo, MDriverEntity entity) {
        // 附件主表
        SFileEntity fileEntity = new SFileEntity();
        fileEntity.setSerial_id(entity.getId());
        fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_M_DRIVER);
        // 身份正面附件新增
        if(vo.getId_card_frontVo() != null ) {
            // 主表新增
            fileMapper.insert(fileEntity);
            // 详情表新增
            SFileInfoEntity fileInfoEntity = new SFileInfoEntity();
            vo.getId_card_frontVo().setF_id(fileEntity.getId());
            BeanUtilsSupport.copyProperties(vo.getId_card_frontVo(),fileInfoEntity);
            fileInfoEntity.setId(null);
            fileInfoEntity.setFile_name(vo.getId_card_frontVo().getFileName());
            fileInfoMapper.insert(fileInfoEntity);
            // 身份正面附件id
            entity.setId_card_front(fileEntity.getId());
            fileEntity.setId(null);
        }
        // 身份反面附件新增
        if(vo.getId_card_backVo() != null ) {
            // 主表新增
            fileMapper.insert(fileEntity);
            // 详情表新增
            SFileInfoEntity fileInfoEntity = new SFileInfoEntity();
            vo.getId_card_backVo().setF_id(fileEntity.getId());
            BeanUtilsSupport.copyProperties(vo.getId_card_backVo(),fileInfoEntity);
            fileInfoEntity.setId(null);
            fileInfoEntity.setFile_name(vo.getId_card_backVo().getFileName());
            fileInfoMapper.insert(fileInfoEntity);
            // 身份反面附件id
            entity.setId_card_back(fileEntity.getId());
            fileEntity.setId(null);
        }
        // 驾驶证附件新增
        if(vo.getDriver_licenseVo() != null ) {
            // 主表新增
            fileMapper.insert(fileEntity);
            // 详情表新增
            SFileInfoEntity fileInfoEntity = new SFileInfoEntity();
            vo.getDriver_licenseVo().setF_id(fileEntity.getId());
            BeanUtilsSupport.copyProperties(vo.getDriver_licenseVo(),fileInfoEntity);
            fileInfoEntity.setId(null);
            fileInfoEntity.setFile_name(vo.getDriver_licenseVo().getFileName());
            fileInfoMapper.insert(fileInfoEntity);
            // 企业认证附件id
            entity.setDriver_license(fileEntity.getId());
            fileEntity.setId(null);
        }
    }

    /**
     * 修改
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> update(MDriverVo vo) {
        // 更新前check
        CheckResultAo cr = checkLogic(vo, CheckResultAo.UPDATE_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }

        MDriverEntity entity = (MDriverEntity) BeanUtilsSupport.copyProperties(vo, MDriverEntity.class);

//        MDriverInfoEntity driverInfoEntity = driverInfoMapper.selectByDriverId(vo.getId());
        // 先删除附件再新增
//        deleteFile(driverInfoEntity);
        insertFile(vo, entity);
//        driverInfoMapper.insert(driverInfoEntity);
        int updCount = mapper.updateById(entity);
        if(updCount == 0){
            throw new UpdateErrorException("您提交的数据已经被修改，请查询后重新编辑更新。");
        }
        return UpdateResultUtil.OK(updCount);
    }

    /**
     * 删除司机
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(MDriverVo searchCondition) {
//        MDriverInfoEntity driverInfoEntity = driverInfoMapper.selectByDriverId(searchCondition.getId());
//        // 删除附件
//        deleteFile(driverInfoEntity);
//        // 删除司机明细
//        driverInfoMapper.deleteById(driverInfoEntity.getId());
//        // 删除司机主表
//        int result = mapper.deleteById(searchCondition.getId());
//        if(result != 2){
//            throw new UpdateErrorException("您删除的数据不存在，请查询后重新编辑更新。");
//        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enabledByIdsIn(List<MDriverVo> searchCondition) {
        List<MDriverEntity> list = mapper.selectIdsIn(searchCondition);
        for(MDriverEntity entity : list) {
            entity.setIs_del(Boolean.TRUE);
        }
        saveOrUpdateBatch(list, 500);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void disSabledByIdsIn(List<MDriverVo> searchCondition) {
        List<MDriverEntity> list = mapper.selectIdsIn(searchCondition);
        for(MDriverEntity entity : list) {
            entity.setIs_del(Boolean.FALSE);
        }
        saveOrUpdateBatch(list, 500);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enableByIdsIn(List<MDriverVo> searchCondition) {
        List<MDriverEntity> list = mapper.selectIdsIn(searchCondition);
        for(MDriverEntity entity : list) {
            if (entity.getIs_del() == Boolean.FALSE){
                List<BMonitorEntity> selectMonitor = bMonitorMapper.selectByDriverId(entity.getId());
                if (selectMonitor.size() > 0) {
                    throw new BusinessException("删除出错：该司机信息被监管任务使用中");
                }
                entity.setIs_del(Boolean.TRUE);
            } else {
                MDriverVo driverVo = (MDriverVo) BeanUtilsSupport.copyProperties(entity, MDriverVo.class);
                CheckResultAo cr = checkLogic(driverVo, CheckResultAo.ENABLE_CHECK_TYPE);
                if (!cr.isSuccess()) {
                    throw new BusinessException(cr.getMessage());
                }
                entity.setIs_del(Boolean.FALSE);
            }
        }
        saveOrUpdateBatch(list, 500);
    }

    /**
     * 司机 列表导出
     *
     * @param param
     * @return
     */
    @Override
    public List<MDriverExportVo> selectExportList(MDriverVo param) {
        return mapper.selectExportList(param);
    }

    /**
     * 删除附件
     */
//    public void deleteFile(MDriverInfoEntity entity) {
//        if(entity.getIdentity_front() != null) {
//            // 删除附件主从表、身份证正面字段置空
//            fileMapper.deleteById(entity.getIdentity_front());
//            fileInfoMapper.delete(new QueryWrapper<SFileInfoEntity>().eq("f_id",entity.getIdentity_front()));
//            entity.setIdentity_front(null);
//        }
//        if(entity.getIdentity_back() != null) {
//            // 删除附件主从表、身份证反面字段置空
//            fileMapper.deleteById(entity.getIdentity_back());
//            fileInfoMapper.delete(new QueryWrapper<SFileInfoEntity>().eq("f_id",entity.getIdentity_back()));
//            entity.setIdentity_back(null);
//        }
//        if(entity.getLicense() != null) {
//            // 删除附件主从表、驾驶证字段置空
//            fileMapper.deleteById(entity.getLicense());
//            fileInfoMapper.delete(new QueryWrapper<SFileInfoEntity>().eq("f_id",entity.getLicense()));
//            entity.setLicense(null);
//        }
//    }

    /**
     * check逻辑
     */
    public CheckResultAo checkLogic(MDriverVo vo, String moduleType) {
        List<MDriverEntity> selectByIdCard = mapper.selectByIdCard(vo);
        List<MDriverEntity> selectByPhone = mapper.selectByPhone(vo);

        switch (moduleType) {
            case CheckResultAo.INSERT_CHECK_TYPE:
                // 新增场合，不能重复
                if (selectByIdCard.size() > 0) {
                    String msg =idCardDuplicateErrorMessage(selectByIdCard);
                    return CheckResultUtil.NG("新增保存" + msg, vo.getId_card());
//                    return CheckResultUtil.NG("新增保存出错：身份证号出现重复", vo.getId_card());
                }
                if (selectByPhone.size() > 0) {
                    return CheckResultUtil.NG("新增保存出错：手机号出现重复", vo.getMobile_phone());
                }
                break;
            case CheckResultAo.UPDATE_CHECK_TYPE:
                // 更新场合，不能重复设置
                if (selectByIdCard.size() > 0) {
                    String msg =idCardDuplicateErrorMessage(selectByIdCard);
                    return CheckResultUtil.NG("新增保存" + msg, vo.getId_card());
//                    return CheckResultUtil.NG("新增保存出错：身份证号出现重复", vo.getId_card());
                }
                if (selectByPhone.size() > 0) {
                    return CheckResultUtil.NG("新增保存出错：手机号出现重复", vo.getMobile_phone());
                }
                break;
            case CheckResultAo.ENABLE_CHECK_TYPE:
                // 更新场合，不能重复设置
                if (selectByIdCard.size() > 0) {
                    String msg =idCardDuplicateErrorMessage(selectByIdCard);
                    return CheckResultUtil.NG("启用" + msg, vo.getId_card());
                }
                if (selectByPhone.size() > 0) {
                    return CheckResultUtil.NG("启用出错：手机号出现重复", vo.getMobile_phone());
                }
                break;
            default:
        }
        return CheckResultUtil.OK();
    }

    /**
     * 身份证重复错误信息
     * @param selectByIdCard
     * @return
     */
    private String idCardDuplicateErrorMessage(List<MDriverEntity> selectByIdCard) {
        MDriverEntity entity = selectByIdCard.get(0);
        StringBuilder sb = new StringBuilder("出错：该身份证号与");
        if (entity.getIs_del()) {
            sb.append("已删除");
        } else {
            sb.append("未删除");
        }
        sb.append("用户（姓名：");
        sb.append(entity.getName());
        sb.append("，手机号：");
        sb.append(entity.getMobile_phone());
        sb.append("）出现重复！");
        return sb.toString();
    }

    /**
     * 查询附件
     */
    public MDriverVo getFile(MDriverVo vo){
        // 身份证正面附件
        if(vo.getId_card_front() != null) {
            SFileEntity file = fileMapper.selectById(vo.getId_card_front());
            List<SFileInfoEntity> fileInfos = fileInfoMapper.selectList(new QueryWrapper<SFileInfoEntity>().eq("f_id",file.getId()));
            for(SFileInfoEntity fileInfo:fileInfos) {
                SFileInfoVo fileInfoVo = (SFileInfoVo) BeanUtilsSupport.copyProperties(fileInfo, SFileInfoVo.class);
                fileInfoVo.setFileName(fileInfoVo.getFile_name());
                vo.setId_card_frontVo(fileInfoVo);
            }
        }
        // 身份证反面附件
        if(vo.getId_card_back() != null) {
            SFileEntity file = fileMapper.selectById(vo.getId_card_back());
            List<SFileInfoEntity> fileInfos = fileInfoMapper.selectList(new QueryWrapper<SFileInfoEntity>().eq("f_id",file.getId()));
            for(SFileInfoEntity fileInfo:fileInfos) {
                SFileInfoVo fileInfoVo = (SFileInfoVo) BeanUtilsSupport.copyProperties(fileInfo, SFileInfoVo.class);
                fileInfoVo.setFileName(fileInfoVo.getFile_name());
                vo.setId_card_backVo(fileInfoVo);
            }
        }
        // 驾驶证附件
        if(vo.getDriver_license() != null) {
            SFileEntity file = fileMapper.selectById(vo.getDriver_license());
            List<SFileInfoEntity> fileInfos = fileInfoMapper.selectList(new QueryWrapper<SFileInfoEntity>().eq("f_id",file.getId()));
            for(SFileInfoEntity fileInfo:fileInfos) {
                SFileInfoVo fileInfoVo = (SFileInfoVo)BeanUtilsSupport.copyProperties(fileInfo, SFileInfoVo.class);
                fileInfoVo.setFileName(fileInfoVo.getFile_name());
                vo.setDriver_licenseVo(fileInfoVo);
            }
        }
        return vo;
    }
}
