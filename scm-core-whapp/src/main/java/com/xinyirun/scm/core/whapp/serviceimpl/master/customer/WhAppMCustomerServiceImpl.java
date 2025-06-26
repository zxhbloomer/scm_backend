package com.xinyirun.scm.core.whapp.serviceimpl.master.customer;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.promeg.pinyinhelper.Pinyin;
import com.xinyirun.scm.bean.app.ao.result.AppCheckResultAo;
import com.xinyirun.scm.bean.entity.busniess.monitor.BMonitorEntity;
import com.xinyirun.scm.bean.entity.master.customer.MCustomerEntity;
import com.xinyirun.scm.bean.entity.master.customer.MCustomerInfoEntity;
import com.xinyirun.scm.bean.entity.master.customer.MCustomerTypeEntity;
import com.xinyirun.scm.bean.entity.sys.file.SFileEntity;
import com.xinyirun.scm.bean.entity.sys.file.SFileInfoEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.CheckResultUtil;
import com.xinyirun.scm.bean.system.vo.master.enterprise.MEnterpriseVo;
import com.xinyirun.scm.bean.whapp.ao.result.WhAppCheckResultAo;
import com.xinyirun.scm.bean.whapp.ao.result.WhAppInsertResultAo;
import com.xinyirun.scm.bean.whapp.ao.result.WhAppUpdateResultAo;
import com.xinyirun.scm.bean.whapp.result.utils.v1.WhAppCheckResultUtil;
import com.xinyirun.scm.bean.whapp.result.utils.v1.WhAppInsertResultUtil;
import com.xinyirun.scm.bean.whapp.result.utils.v1.WhAppUpdateResultUtil;
import com.xinyirun.scm.bean.whapp.vo.master.customer.WhAppMCustomerInfoVo;
import com.xinyirun.scm.bean.whapp.vo.master.customer.WhAppMCustomerTypeVo;
import com.xinyirun.scm.bean.whapp.vo.master.customer.WhAppMCustomerVo;
import com.xinyirun.scm.bean.whapp.vo.sys.file.WhAppSFileInfoVo;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.exception.app.AppBusinessException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.common.exception.whapp.WhAppBusinessException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.core.whapp.mapper.business.monitor.WhAppBMonitorMapper;
import com.xinyirun.scm.core.whapp.mapper.master.customer.WhAppCustomerInfoMapper;
import com.xinyirun.scm.core.whapp.mapper.master.customer.WhAppMCustomerMapper;
import com.xinyirun.scm.core.whapp.mapper.master.customer.WhAppMCustomerTypeMapper;
import com.xinyirun.scm.core.whapp.mapper.sys.file.WhAppSFileInfoMapper;
import com.xinyirun.scm.core.whapp.mapper.sys.file.WhAppSFileMapper;
import com.xinyirun.scm.core.whapp.service.master.customer.WhAppIMCustomerService;
import com.xinyirun.scm.core.whapp.serviceimpl.base.v1.WhAppBaseServiceImpl;
import com.xinyirun.scm.core.whapp.serviceimpl.common.autocode.WhAppMCustomerAutoCodeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Service
public class WhAppMCustomerServiceImpl extends WhAppBaseServiceImpl<WhAppMCustomerMapper, MCustomerEntity> implements WhAppIMCustomerService {

    @Autowired
    private WhAppMCustomerMapper mapper;

    @Autowired
    private WhAppCustomerInfoMapper customerInfoMapper;

    @Autowired
    private WhAppSFileMapper fileMapper;

    @Autowired
    private WhAppSFileInfoMapper fileInfoMapper;

    @Autowired
    private WhAppMCustomerAutoCodeServiceImpl customerAutoCodeService;

    @Autowired
    private WhAppBMonitorMapper monitorMapper;

    @Autowired
    private WhAppMCustomerTypeMapper customerTypeMapper;

    /**
     * 查询承运商list
     */
    @Override
    public List<WhAppMCustomerVo> list(WhAppMCustomerVo searchCondition) {
        return mapper.selectByList(searchCondition);
    }

    /**
     * 查询承运商分页
     */
    @Override
    public IPage<WhAppMCustomerVo> selectPage(WhAppMCustomerVo searchCondition) {
        String defaultSort = "";

        String sort = searchCondition.getPageCondition().getSort();
        String sortType = "DESC";
        if (StringUtils.isNotEmpty(sort)) {
            if (sort.startsWith("-")) {
                sort = sort.substring(1);
            } else {
                sortType = "ASC";
            }

            // 默认增加一个按u_time倒序
            if (!sort.contains("_time")) {
                defaultSort = ", u_time desc";
            }
        }

        IPage<WhAppMCustomerVo> page = new Page<>();
        page.setCurrent(searchCondition.getPageCondition().getCurrent());
        page.setSize(searchCondition.getPageCondition().getSize());
        // 为配合APP查询速度提升，尽量不使用count查询，此处需要规避count自动查询，所以在这里设置了默认的pages和total
        page.setPages(1000);
        page.setTotal(10000);
        List<WhAppMCustomerVo> list = mapper.selectPage(searchCondition, sort, sortType, defaultSort);
        page.setRecords(list);
        if (list.size() < searchCondition.getPageCondition().getSize()) {
            page.setTotal(searchCondition.getPageCondition().getSize() * (searchCondition.getPageCondition().getCurrent() - 1) + list.size());
            page.setPages(searchCondition.getPageCondition().getCurrent());
        }
        return page;




    }

    /**
     * 获取承运商
     */
    @Override
    public WhAppMCustomerVo getDetail(WhAppMCustomerVo searchCondition) {
        WhAppMCustomerVo resultVo = mapper.getDetail(searchCondition);

        List<WhAppMCustomerTypeVo> typeVoList = customerTypeMapper.selectCustomerTypeList(searchCondition.getId());
        if (typeVoList != null && !typeVoList.isEmpty()) {
            StringBuilder type = new StringBuilder();
            for (WhAppMCustomerTypeVo typeVo : typeVoList) {
                type.append(typeVo.getType()).append(",");
            }
            // 删除最后一个多余的逗号
            if (type.length() > 0) {
                type.deleteCharAt(type.length() - 1);
            }
            resultVo.setType(type.toString());
        }

        if(resultVo != null) {
            // set附件信息返回
            getFile(resultVo);
        }
        return resultVo;
    }

    /**
     * 新增承运商
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public WhAppInsertResultAo<Integer> insert(WhAppMCustomerVo vo) {

        vo.setInfoVo(new WhAppMCustomerInfoVo());
        BeanUtilsSupport.copyProperties(vo, vo.getInfoVo(), new String[]{"id"});

        // 插入前check
        WhAppCheckResultAo cr = checkLogic(vo, WhAppCheckResultAo.INSERT_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new WhAppBusinessException(cr.getMessage());
        }

        // 插入逻辑保存
        MCustomerEntity entity = (MCustomerEntity) BeanUtilsSupport.copyProperties(vo, MCustomerEntity.class);

        // 名称全拼
        entity.setName_pinyin(Pinyin.toPinyin(entity.getName(), ""));
        // 名称简拼
        StringBuilder str = new StringBuilder();
        for (char c: entity.getName().toCharArray()) {
            str.append(Pinyin.toPinyin(c).substring(0,1));
        }
        entity.setCode(customerAutoCodeService.autoCode().getCode());
        entity.setShort_name_pinyin(str.toString());
        entity.setEnable(Boolean.TRUE);
        entity.setType(null);

        int rtn = mapper.insert(entity);
        vo.setId(entity.getId());

        MCustomerInfoEntity infoEntity = new MCustomerInfoEntity();
        if (vo.getInfoVo() != null) {
            infoEntity.setTransport_no(vo.getInfoVo().getTransport_no());
            infoEntity.setName(vo.getInfoVo().getName());
            infoEntity.setLicence_no(vo.getInfoVo().getLicence_no());
            infoEntity.setContact_person(vo.getInfoVo().getContact_person());
            infoEntity.setCustomer_id(entity.getId());
        }
        // 新增承运商明细
        customerInfoMapper.insert(infoEntity);
        // 附件新增逻辑
        insertFile(vo.getInfoVo(),infoEntity);
        customerInfoMapper.updateById(infoEntity);

        // 保存企业类型
        String[] types = vo.getType().split(",");
        for (String type : types) {
            MCustomerTypeEntity mCustomerType = new MCustomerTypeEntity();
            mCustomerType.setCustomer_id(entity.getId());
            mCustomerType.setType(type);
            customerTypeMapper.insert(mCustomerType);
        }


        return WhAppInsertResultUtil.OK(rtn);
    }

    /**
     * 修改
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public WhAppUpdateResultAo<Integer> update(WhAppMCustomerVo vo) {
        // 更新前check
        WhAppCheckResultAo cr = checkLogic(vo, WhAppCheckResultAo.UPDATE_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new AppBusinessException(cr.getMessage());
        }

        MCustomerEntity entity = mapper.selectById(vo.getId());
        BeanUtilsSupport.copyProperties(vo, entity);

        // 名称全拼
        entity.setName_pinyin(Pinyin.toPinyin(entity.getName(), ""));
        // 名称简拼
        StringBuilder str = new StringBuilder("");
        for (char c: entity.getName().toCharArray()) {
            str.append(Pinyin.toPinyin(c).substring(0,1));
        }
        entity.setShort_name_pinyin(str.toString());
        entity.setEnable(Boolean.TRUE);

        MCustomerInfoEntity infoEntity = customerInfoMapper.getByCustomerId(entity.getId());
        if (infoEntity == null) {
            infoEntity = new MCustomerInfoEntity();
            BeanUtilsSupport.copyProperties(vo.getInfoVo(), infoEntity);
            infoEntity.setCustomer_id(entity.getId());
            customerInfoMapper.insert(infoEntity);
        } else {
            WhAppMCustomerInfoVo infoVo = new WhAppMCustomerInfoVo();
            BeanUtilsSupport.copyProperties(infoEntity, infoVo);
            infoVo.setLicense_att(vo.getLicense_att());
            infoVo.setLr_id_front_att(vo.getLr_id_front_att());
            infoVo.setLr_id_back_att(vo.getLr_id_back_att());
            infoVo.setDoc_att(vo.getDoc_att());
            infoVo.setLogo(vo.getLogo());
            vo.setInfoVo(infoVo);
        }
        // 先删除附件再新增
        deleteFile(vo);
        insertFile(vo.getInfoVo(), infoEntity);
        infoEntity.setTransport_no(vo.getInfoVo().getTransport_no());
        infoEntity.setName(vo.getInfoVo().getName());
        infoEntity.setLicence_no(vo.getInfoVo().getLicence_no());
        infoEntity.setContact_person(vo.getInfoVo().getContact_person());
        customerInfoMapper.updateById(infoEntity);
        entity.setU_id(null);
        entity.setU_time(null);
        entity.setType(null);
        int updCount = mapper.updateById(entity);
        if(updCount == 0){
            throw new UpdateErrorException("您提交的数据已经被修改，请查询后重新编辑更新。");
        }

        // 删除企业类型
        customerTypeMapper.delete(new QueryWrapper<MCustomerTypeEntity>().eq("customer_id", entity.getId()));

        // 保存企业类型
        String[] types = vo.getType().split(",");
        for (String type : types) {
            MCustomerTypeEntity mCustomerType = new MCustomerTypeEntity();
            mCustomerType.setCustomer_id(entity.getId());
            mCustomerType.setType(type);
            customerTypeMapper.insert(mCustomerType);
        }

        return WhAppUpdateResultUtil.OK(updCount);
    }

    /**
     * id查询
     */
    @Override
    public WhAppMCustomerVo selectById(int id) {
        return mapper.selectId(id);
    }

    /**
     * name查询
     */
    @Override
    public List<MCustomerEntity> selectByName(String name) {
        // 查询 数据
        return mapper.selectByName(name);
    }

    /**
     * 删除承运商
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(WhAppMCustomerVo searchCondition) {
        // 删除前check
        WhAppCheckResultAo cr = checkLogic(searchCondition, WhAppCheckResultAo.DELETE_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new AppBusinessException(cr.getMessage());
        }
        MCustomerEntity entity = mapper.selectById(searchCondition.getId());
        entity.setEnable(Boolean.FALSE);
        mapper.updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void top(WhAppMCustomerVo searchCondition) {
        MCustomerEntity entity = mapper.selectById(searchCondition.getId());
        entity.setTop_time(LocalDateTime.now());
        mapper.updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void canceltop(WhAppMCustomerVo searchCondition) {
        MCustomerEntity entity = mapper.selectById(searchCondition.getId());
        entity.setTop_time(null);
        mapper.updateById(entity);
    }

    /**
     * code查询
     */
    @Override
    public List<MCustomerEntity> selectByCreditNo(String CreditNo) {
        // 查询 数据
        return mapper.selectByCreditNo(CreditNo);
    }

    /**
     * check逻辑
     */
    public WhAppCheckResultAo checkLogic(WhAppMCustomerVo vo, String moduleType) {
        // 新增、修改操作非空判断
        if(!moduleType.equals(WhAppCheckResultAo.DELETE_CHECK_TYPE)) {
            if(vo.getName() == null) {
                return WhAppCheckResultUtil.NG("新增保存出错：企业名称不能为空");
            }
//            if(vo.getShort_name() == null) {
//                return WhAppCheckResultUtil.NG("新增保存出错：企业简称不能为空");
//            }
            if(vo.getCredit_no() == null) {
                return WhAppCheckResultUtil.NG("新增保存出错：统一社会信用代码不能为空");
            }
//            if(vo.getInfoVo() == null || vo.getInfoVo().getContact_person() == null) {
//                return WhAppCheckResultUtil.NG("新增保存出错：联系人不能为空");
//            }
//            if(vo.getContact_number() == null) {
//                return WhAppCheckResultUtil.NG("新增保存出错：联系电话不能为空");
//            }
        }

        List<MCustomerEntity> selectByName = selectByName(vo.getName());
        List<MCustomerEntity> selectByCreditNo = selectByCreditNo(vo.getCredit_no());
        List<BMonitorEntity> selectMonitor = monitorMapper.selectByCustomerId(vo.getId());
        switch (moduleType) {
            case AppCheckResultAo.INSERT_CHECK_TYPE:
                // 新增场合，不能重复
                if (!selectByName.isEmpty()) {
                    return WhAppCheckResultUtil.NG("新增保存出错：名称出现重复", vo.getName());
                }
                if (!selectByCreditNo.isEmpty()) {
                    return WhAppCheckResultUtil.NG("新增保存出错：统一社会信用代码出现重复", vo.getCredit_no());
                }
                break;
            case AppCheckResultAo.UPDATE_CHECK_TYPE:
                if (vo.getId() == null) {
                    return WhAppCheckResultUtil.NG("新增保存出错：id不能为空");
                }
                selectByName = mapper.selectByNameId(vo);
                selectByCreditNo = mapper.selectByCreditNoId(vo);
                // 更新场合，不能重复设置
                if (!selectByName.isEmpty()) {
                    return WhAppCheckResultUtil.NG("新增保存出错：名称出现重复", vo.getName());
                }
                if (!selectByCreditNo.isEmpty()) {
                    return WhAppCheckResultUtil.NG("新增保存出错：统一社会信用代码出现重复", vo.getCredit_no());
                }
                break;
            case AppCheckResultAo.DELETE_CHECK_TYPE:
                // 删除的数据在监管任务中是否有被使用
                if (!selectMonitor.isEmpty()) {
                    return WhAppCheckResultUtil.NG("删除出错：该企业信息被监管任务使用中", vo.getName());
                }
                break;
            default:
        }
        return WhAppCheckResultUtil.OK();
    }

    /**
     * 附件新增逻辑
     */
    public void insertFile(WhAppMCustomerInfoVo vo, MCustomerInfoEntity entity) {
        // 附件主表
        SFileEntity fileEntity = new SFileEntity();
        fileEntity.setSerial_id(entity.getId());
        fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_M_CUSTOMER);

        // 处理附件新增
//        processFile(vo.getId_front_file(), fileEntity, entity::setId_front_file_id);
//        processFile(vo.getId_back_file(), fileEntity, entity::setId_back_file_id);
//        processFile(vo.getConfirm_file(), fileEntity, entity::setConfirm_file_id);
//        processFile(vo.getLicence_confirm_file(), fileEntity, entity::setLicence_confirm_file_id);
        processFile(vo.getLicense_att(), fileEntity, entity::setLicense_att_id);
        processFile(vo.getLr_id_front_att(), fileEntity, entity::setLr_id_front_att_id);
        processFile(vo.getLr_id_back_att(), fileEntity, entity::setLr_id_back_att_id);
        processFile(vo.getDoc_att(), fileEntity, entity::setDoc_att_id);
        processFile(vo.getLogo(), fileEntity, entity::setLogo_id);
    }

    private void processFile(WhAppSFileInfoVo fileVo, SFileEntity fileEntity, Consumer<Integer> setFileId) {
        if (fileVo != null) {
            // 主表新增
            fileMapper.insert(fileEntity);

            // 详情表新增
            SFileInfoEntity fileInfoEntity = new SFileInfoEntity();
            fileVo.setF_id(fileEntity.getId());
            BeanUtilsSupport.copyProperties(fileVo, fileInfoEntity);
            fileInfoEntity.setFile_name(fileVo.getFileName());
            fileInfoEntity.setId(null);
            fileInfoMapper.insert(fileInfoEntity);

            // 设置附件id
            setFileId.accept(fileEntity.getId());

            // 重置fileEntity的id
            fileEntity.setId(null);
        } else {
            setFileId.accept(null);
        }
    }

    private void processFile(List<WhAppSFileInfoVo> fileVos, SFileEntity fileEntity, Consumer<Integer> setFileId) {
        if (fileVos != null && !fileVos.isEmpty()) {
            // 主表新增
            fileMapper.insert(fileEntity);
            for (WhAppSFileInfoVo fileVo : fileVos) {
                // 详情表新增
                SFileInfoEntity fileInfoEntity = new SFileInfoEntity();
                fileVo.setF_id(fileEntity.getId());
                BeanUtilsSupport.copyProperties(fileVo, fileInfoEntity);
                fileInfoEntity.setFile_name(fileVo.getFileName());
                fileInfoEntity.setId(null);
                fileInfoMapper.insert(fileInfoEntity);
            }

            // 设置附件id
            setFileId.accept(fileEntity.getId());

            // 重置fileEntity的id
            fileEntity.setId(null);
        } else {
            setFileId.accept(null);
        }
    }

    /**
     * 删除附件
     */
    public void deleteFile(WhAppMCustomerVo vo) {
        MCustomerInfoEntity entity = customerInfoMapper.getByCustomerId(vo.getId());
        if (entity != null) {
            if (entity.getLicense_att_id() != null) {
                // 删除附件主从表、营业执照附件字段置空
                fileMapper.deleteById(entity.getLicense_att_id());
                fileInfoMapper.delete(new QueryWrapper<SFileInfoEntity>().eq("f_id", entity.getLicense_att_id()));
                entity.setLicense_att_id(null);
            }
            if (entity.getLr_id_front_att_id() != null) {
                // 删除附件主从表、法人身份证正面附件字段置空
                fileMapper.deleteById(entity.getLr_id_front_att_id());
                fileInfoMapper.delete(new QueryWrapper<SFileInfoEntity>().eq("f_id", entity.getLr_id_front_att_id()));
                entity.setLr_id_front_att_id(null);
            }
            if (entity.getLr_id_back_att_id() != null) {
                // 删除附件主从表、法人身份证背面附件字段置空
                fileMapper.deleteById(entity.getLr_id_back_att_id());
                fileInfoMapper.delete(new QueryWrapper<SFileInfoEntity>().eq("f_id", entity.getLr_id_back_att_id()));
                entity.setLr_id_back_att_id(null);
            }
            if (entity.getDoc_att_id() != null) {
                // 删除附件主从表、其他材料附件字段置空
                fileMapper.deleteById(entity.getDoc_att_id());
                fileInfoMapper.delete(new QueryWrapper<SFileInfoEntity>().eq("f_id", entity.getDoc_att_id()));
                entity.setDoc_att_id(null);
            }
            if (entity.getLogo_id() != null) {
                // 删除附件主从表、公司logo附件字段置空
                fileMapper.deleteById(entity.getLogo_id());
                fileInfoMapper.delete(new QueryWrapper<SFileInfoEntity>().eq("f_id", entity.getLogo_id()));
                entity.setLogo_id(null);
            }
        }

    }

    /**
     * 查询附件
     */
    private void getFile(WhAppMCustomerVo vo) {
        // 查询承运商明细数据对象
        MCustomerInfoEntity infoEntity = customerInfoMapper.getByCustomerId(vo.getId());
        WhAppMCustomerInfoVo infoVo = (WhAppMCustomerInfoVo) BeanUtilsSupport.copyProperties(infoEntity, WhAppMCustomerInfoVo.class);

        if (infoVo != null) {
            // 查询各类附件
            populateFileInfo(infoVo.getId_front_file_id(), infoVo::setId_front_file);
            populateFileInfo(infoVo.getId_back_file_id(), infoVo::setId_back_file);
            populateFileInfo(infoVo.getConfirm_file_id(), infoVo::setConfirm_file);
            populateFileInfoList(infoVo.getLicense_att_id(), infoVo::setLicense_att);
            populateFileInfoList(infoVo.getLr_id_front_att_id(), infoVo::setLr_id_front_att);
            populateFileInfoList(infoVo.getLr_id_back_att_id(), infoVo::setLr_id_back_att);
            populateFileInfoList(infoVo.getDoc_att_id(), infoVo::setDoc_att);
            populateFileInfoList(infoVo.getLogo_id(), infoVo::setLogo);

            vo.setLicense_att(infoVo.getLicense_att());
            vo.setLr_id_front_att(infoVo.getLr_id_front_att());
            vo.setLr_id_back_att(infoVo.getLr_id_back_att());
            vo.setDoc_att(infoVo.getDoc_att());
            vo.setLogo(infoVo.getLogo());
        }

//        vo.setInfoVo(infoVo);

//        BeanUtilsSupport.copyProperties(infoVo, vo, new String[]{"id", "name"});
    }

    /**
     * 根据附件ID填充文件信息
     */
    private void populateFileInfo(Integer fileId, Consumer<WhAppSFileInfoVo> setter) {
        if (fileId != null) {
            SFileEntity file = fileMapper.selectById(fileId);
            List<SFileInfoEntity> fileInfos = fileInfoMapper.selectList(new QueryWrapper<SFileInfoEntity>().eq("f_id", file.getId()));
            for (SFileInfoEntity fileInfo : fileInfos) {
                WhAppSFileInfoVo fileInfoVo = (WhAppSFileInfoVo) BeanUtilsSupport.copyProperties(fileInfo, WhAppSFileInfoVo.class);
                fileInfoVo.setFileName(fileInfoVo.getFile_name());
                setter.accept(fileInfoVo);
            }
        }
    }

    /**
     * 根据附件ID填充文件信息-列表
     */
    private void populateFileInfoList(Integer fileId, Consumer<List<WhAppSFileInfoVo>> setter) {
        if (fileId != null) {
            List<WhAppSFileInfoVo> list = new ArrayList<>();
            SFileEntity file = fileMapper.selectById(fileId);
            List<SFileInfoEntity> fileInfos = fileInfoMapper.selectList(new QueryWrapper<SFileInfoEntity>().eq("f_id", file.getId()));
            for (SFileInfoEntity fileInfo : fileInfos) {
                if (StringUtils.isEmpty(fileInfo.getUrl())) {
                    continue;
                }
                WhAppSFileInfoVo fileInfoVo = (WhAppSFileInfoVo) BeanUtilsSupport.copyProperties(fileInfo, WhAppSFileInfoVo.class);
                fileInfoVo.setFileName(fileInfoVo.getFile_name());
                list.add(fileInfoVo);
            }
            setter.accept(list);
        }
    }

    /**
     * check逻辑
     */
    @Override
    public CheckResultAo checkLogic(MEnterpriseVo vo, String checkType) {
        List<MCustomerEntity> selectByName = mapper.selectByName(vo);
        List<MCustomerEntity> selectByCreditNo = mapper.selectByCreditCode(vo);

        switch (checkType) {
            case CheckResultAo.INSERT_CHECK_TYPE:
                // 新增场合，不能重复
                if (selectByName.size() > 0) {
                    return CheckResultUtil.NG("企业名称重复，请修改后重新保存", vo.getName());
                }
                if (selectByCreditNo.size() > 0) {
                    return CheckResultUtil.NG("企业信用代码证重复，请修改后重新保存", vo.getCredit_no());
                }
                break;
            case CheckResultAo.UPDATE_CHECK_TYPE:
                // 更新场合，不能重复设置
                if (selectByName.size() > 0) {
                    return CheckResultUtil.NG("新增保存出错：名称出现重复", vo.getName());
                }
                if (selectByCreditNo.size() > 0) {
                    return CheckResultUtil.NG("新增保存出错：企业信用代码出现重复", vo.getCredit_no());
                }
                break;
            case CheckResultAo.DELETE_CHECK_TYPE:

                MCustomerEntity mCustomerEntity = mapper.selectById(vo.getId());

                List<BMonitorEntity> selectMonitor = monitorMapper.selectByCustomerId(vo.getId());
                // 删除的数据在监管任务中是否有被使用
                if (!selectMonitor.isEmpty()) {
                    return CheckResultUtil.NG("删除出错：该企业信息被监管任务使用中", vo.getName());
                }

                // todo 判断合同下有该企业数据

                // todo 订单下有该企业数据

                // 入库计划有该企业数据

                break;
            default:
        }
        return CheckResultUtil.OK();
    }


}
