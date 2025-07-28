package com.xinyirun.scm.core.app.serviceimpl.master.enterprise;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.promeg.pinyinhelper.Pinyin;
import com.xinyirun.scm.bean.app.ao.result.AppCheckResultAo;
import com.xinyirun.scm.bean.app.result.utils.v1.AppCheckResultUtil;
import com.xinyirun.scm.bean.app.vo.master.enterprise.AppMEnterpriseVo;
import com.xinyirun.scm.bean.entity.business.wms.out.BOutEntity;
import com.xinyirun.scm.bean.entity.business.wms.outplan.BOutPlanEntity;
import com.xinyirun.scm.bean.entity.master.enterprise.MEnterpriseAttachEntity;
import com.xinyirun.scm.bean.entity.master.enterprise.MEnterpriseEntity;
import com.xinyirun.scm.bean.entity.master.enterprise.MEnterpriseHisEntity;
import com.xinyirun.scm.bean.entity.master.enterprise.MEnterpriseTypesEntity;
import com.xinyirun.scm.bean.entity.sys.config.config.SConfigEntity;
import com.xinyirun.scm.bean.entity.sys.config.dict.SDictDataEntity;
import com.xinyirun.scm.bean.entity.sys.file.SFileEntity;
import com.xinyirun.scm.bean.entity.sys.file.SFileInfoEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.DeleteResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.DeleteResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.UpdateResultUtil;
import com.xinyirun.scm.bean.system.vo.business.bpm.AppStaffUserBpmInfoVo;
import com.xinyirun.scm.bean.system.vo.business.bpm.BBpmProcessVo;
import com.xinyirun.scm.bean.system.vo.business.bpm.OrgUserVo;
import com.xinyirun.scm.bean.system.vo.excel.customer.MEnterpiseExcelVo;
import com.xinyirun.scm.bean.system.vo.master.enterprise.MEnterpriseAttachVo;
import com.xinyirun.scm.bean.system.vo.master.enterprise.MEnterpriseHisVo;
import com.xinyirun.scm.bean.system.vo.master.enterprise.MEnterpriseImportVo;
import com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo;
import com.xinyirun.scm.bean.utils.security.SecurityUtil;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.core.app.mapper.master.enterpise.AppMEnterpriseAttachMapper;
import com.xinyirun.scm.core.app.mapper.master.enterpise.AppMEnterpriseHisMapper;
import com.xinyirun.scm.core.app.mapper.master.enterpise.AppMEnterpriseMapper;
import com.xinyirun.scm.core.app.mapper.master.enterpise.AppMEnterpriseTypesMapper;
import com.xinyirun.scm.core.app.service.master.enterprise.AppIMEnterpriseService;
import com.xinyirun.scm.core.app.serviceimpl.base.v1.AppBaseServiceImpl;
import com.xinyirun.scm.core.bpm.service.business.IBpmProcessTemplatesService;
import com.xinyirun.scm.core.system.mapper.business.wms.in.BInMapper;
import com.xinyirun.scm.core.system.mapper.business.wms.inplan.BInPlanMapper;
import com.xinyirun.scm.core.system.mapper.business.wms.out.BOutMapper;
import com.xinyirun.scm.core.system.mapper.business.wms.outplan.BOutPlanMapper;
import com.xinyirun.scm.core.system.mapper.sys.config.dict.SDictDataMapper;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileInfoMapper;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileMapper;
import com.xinyirun.scm.core.system.service.sys.config.config.ISConfigService;
import com.xinyirun.scm.core.system.service.sys.file.ISFileService;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.MEnterpriseAutoCodeServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Service
public class AppMEnterpriseServiceImpl extends AppBaseServiceImpl<AppMEnterpriseMapper, MEnterpriseEntity> implements AppIMEnterpriseService {

    @Autowired
    private AppMEnterpriseMapper mapper;

    @Autowired
    private MEnterpriseAutoCodeServiceImpl mEnterpriseAutoCodeService;

    @Autowired
    private SFileMapper fileMapper;

    @Autowired
    private ISFileService isFileService;

    @Autowired
    private SFileInfoMapper fileInfoMapper;

    @Autowired
    private AppMEnterpriseAttachMapper mEnterpriseAttachMapper;

    @Autowired
    private AppMEnterpriseMapper mEnterpriseMapper;

    @Autowired
    private AppMEnterpriseHisMapper mEnterpriseHisMapper;

    @Autowired
    private AppMEnterpriseTypesMapper mEnterpriseTypesMapper;

    @Autowired
    private ISConfigService configService;

    @Autowired
    private SDictDataMapper sDictDataMapper;

    @Autowired
    private BOutPlanMapper bOutPlanMapper;

    @Autowired
    private BOutMapper bOutMapper;

    @Autowired
    private BInMapper bInMapper;

    @Autowired
    private BInPlanMapper bInPlanMapper;

    @Autowired
    IBpmProcessTemplatesService bpmProcessTemplatesService;

    @Override
    public IPage<AppMEnterpriseVo> selectPage(AppMEnterpriseVo searchCondition) {
        // 分页条件
        Page<AppMEnterpriseVo> pageCondition =
                new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());
        return mapper.selectPage(pageCondition, searchCondition);
    }

    /**
     * 新增企业，业务数据
     */
    public InsertResultAo<Integer> insertBussinessData(AppMEnterpriseVo vo) {
        // 插入前check
        AppCheckResultAo cr = checkLogic(vo, AppCheckResultAo.INSERT_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }

        // 1.保存企业信息
        MEnterpriseEntity entity = (MEnterpriseEntity) BeanUtilsSupport.copyProperties(vo, MEnterpriseEntity.class);
        // 企业名称全拼，简拼
        entity.setName_pinyin(Pinyin.toPinyin(entity.getName(), ""));
        entity.setName_short_pinyin(simplifiedSpelling(entity.getName()));
        // 法人名称全拼，简拼
        if (StringUtils.isNotEmpty(vo.getLegal_person())){
            entity.setLegal_person_pinyin(Pinyin.toPinyin(entity.getLegal_person(), ""));
            entity.setLegal_person_short_pinyin(simplifiedSpelling(entity.getLegal_person()));
        }
        /** 审批中 */
        entity.setStatus(DictConstant.DICT_M_ENTERPRISE_STATUS_ONE);
        entity.setCode(mEnterpriseAutoCodeService.autoCode().getCode());

        int rtn = mapper.insert(entity);
        if (rtn == 0) {
            throw new UpdateErrorException("新增失败");
        }

        vo.setId(entity.getId());

        // 2.保存企业类型附表
        for (String typeId : vo.getType_ids()) {
            MEnterpriseTypesEntity mEnterpriseTypesEntity = new MEnterpriseTypesEntity();
            mEnterpriseTypesEntity.setEnterprise_id(entity.getId());
            mEnterpriseTypesEntity.setType(typeId);
            mEnterpriseTypesMapper.insert(mEnterpriseTypesEntity);
        }

        // 3.保存附件
        SFileEntity fileEntity = new SFileEntity();
        fileEntity.setSerial_id(entity.getId());
        fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_M_ENTERPRISE);

        MEnterpriseAttachEntity enterpriseInfoEntity = insertFile(new SFileEntity(), vo, new MEnterpriseAttachEntity());
        enterpriseInfoEntity.setEnterprise_id(entity.getId());
        int insert = mEnterpriseAttachMapper.insert(enterpriseInfoEntity);
        if (insert == 0) {
            throw new UpdateErrorException("新增失败");
        }

        // 插入逻辑保存
        return InsertResultUtil.OK(rtn);
    }

    /**
     * 转换为简拼
     */
    private String simplifiedSpelling(String pinyin){
        StringBuilder str = new StringBuilder("");
        for (char c: pinyin.toCharArray()) {
            str.append(Pinyin.toPinyin(c).substring(0,1));
        }
        return str.toString();
    }

    /**
     * 附件逻辑 全删全增
     */
    public MEnterpriseAttachEntity insertFile(SFileEntity fileEntity, AppMEnterpriseVo vo, MEnterpriseAttachEntity extra) {

        // LOGO附件全删
        /*if (vo.getLogo_file()!=null){
            deleteFile(vo.getLogo_file());
        }*/

        // LOGO附件新增
        if (vo.getLogo_files() != null && vo.getLogo_files().size() > 0) {

            // 主表新增
            fileMapper.insert(fileEntity);
            // 详情表新增
            for (SFileInfoVo logoFile : vo.getLogo_files()) {
                SFileInfoEntity fileInfoEntity = new SFileInfoEntity();
                logoFile.setF_id(fileEntity.getId());
                BeanUtilsSupport.copyProperties(logoFile, fileInfoEntity);
                fileInfoEntity.setFile_name(logoFile.getFileName());
                fileInfoEntity.setId(null);
                fileInfoMapper.insert(fileInfoEntity);
            }
            // LOGO附件id
            extra.setLogo_id(fileEntity.getId());
            fileEntity.setId(null);
        }else {
            extra.setLogo_id(null);
        }

        //  营业执照附件全删
       /* if (vo.getLicense_att_file()!=null){
            deleteFile(vo.getLicense_att_file());
        }*/
        // 营业执照附件新增
        if (vo.getLicense_att_files() != null && vo.getLicense_att_files().size() > 0) {
            // 主表新增
            fileMapper.insert(fileEntity);
            // 详情表新增
            for (SFileInfoVo business_license : vo.getLicense_att_files()) {
                SFileInfoEntity fileInfoEntity = new SFileInfoEntity();
                business_license.setF_id(fileEntity.getId());
                BeanUtilsSupport.copyProperties(business_license, fileInfoEntity);
                fileInfoEntity.setFile_name(business_license.getFileName());
                fileInfoEntity.setId(null);
                fileInfoMapper.insert(fileInfoEntity);
            }
            // 营业执照附件id
            extra.setLicense_att_id(fileEntity.getId());
            fileEntity.setId(null);
        }else {
            extra.setLicense_att_id(null);
        }

        //   法人身份证正面附件全删
       /* if (vo.getLr_id_front_att_file()!=null){
            deleteFile(vo.getLr_id_front_att_file());
        }*/
        // 法人身份证正面附件新增
        if (vo.getLr_id_front_att_files() != null && vo.getLr_id_front_att_files().size() > 0) {
            // 主表新增
            fileMapper.insert(fileEntity);
            // 详情表新增
            for (SFileInfoVo positive_id_card : vo.getLr_id_front_att_files()) {
                SFileInfoEntity fileInfoEntity = new SFileInfoEntity();
                positive_id_card.setF_id(fileEntity.getId());
                BeanUtilsSupport.copyProperties(positive_id_card, fileInfoEntity);
                fileInfoEntity.setFile_name(positive_id_card.getFileName());
                fileInfoEntity.setId(null);
                fileInfoMapper.insert(fileInfoEntity);
            }
            // 法人身份证正面附件新增id
            extra.setLr_id_front_att_id(fileEntity.getId());
            fileEntity.setId(null);
        }else {
            extra.setLr_id_front_att_id(null);
        }

        //  法人身份证反面附件全删
        /*if (vo.getLr_id_back_att_file()!=null){
            deleteFile(vo.getLr_id_back_att_file());
        }*/
        // 法人身份证反面附件新增
        if (vo.getLr_id_back_att_files() != null && vo.getLr_id_back_att_files().size() > 0) {
            // 主表新增
            fileMapper.insert(fileEntity);
            // 详情表新增
            for (SFileInfoVo reverse_id_card : vo.getLr_id_back_att_files()) {
                SFileInfoEntity fileInfoEntity = new SFileInfoEntity();
                reverse_id_card.setF_id(fileEntity.getId());
                BeanUtilsSupport.copyProperties(reverse_id_card, fileInfoEntity);
                fileInfoEntity.setFile_name(reverse_id_card.getFileName());
                fileInfoEntity.setId(null);
                fileInfoMapper.insert(fileInfoEntity);
            }
            // 法人身份证反面附件id
            extra.setLr_id_back_att_id(fileEntity.getId());
            fileEntity.setId(null);
        }else {
            extra.setLr_id_back_att_id(null);
        }

        //  其他附件附件全删
       /* if (vo.getDoc_att_file()!=null){
            deleteFile(vo.getDoc_att_file());
        }*/
        // 其他附件新增
        if (vo.getDoc_att_files() != null && vo.getDoc_att_files().size() > 0) {
            // 主表新增
            fileMapper.insert(fileEntity);
            // 详情表新增
            for (SFileInfoVo other_file : vo.getDoc_att_files()) {
                SFileInfoEntity fileInfoEntity = new SFileInfoEntity();
                other_file.setF_id(fileEntity.getId());
                BeanUtilsSupport.copyProperties(other_file, fileInfoEntity);
                fileInfoEntity.setFile_name(other_file.getFileName());
                fileInfoEntity.setId(null);
                fileInfoMapper.insert(fileInfoEntity);
            }
            // 其他附件id
            extra.setDoc_att_id(fileEntity.getId());
            fileEntity.setId(null);
        }else {
            extra.setDoc_att_id(null);
        }

        return extra;
    }

    /**
     * 根据文件ID删除文件信息
     */
    private void deleteFile(Integer fileId) {
        if (fileId != null) {
            fileInfoMapper.delete(Wrappers.<SFileInfoEntity>lambdaQuery().eq(SFileInfoEntity::getF_id, fileId));
        }
    }

    /**
     * 增加审批流 企业信息更新
     * 更新业务数据
     */
    public UpdateResultAo<Integer> updateBussinessData(AppMEnterpriseVo vo) {
        // 更新前check
        AppCheckResultAo cr = checkLogic(vo, AppCheckResultAo.UPDATE_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }

        MEnterpriseEntity entity = (MEnterpriseEntity) BeanUtilsSupport.copyProperties(vo, MEnterpriseEntity.class);
        // 企业名称全拼，简拼
        entity.setName_pinyin(Pinyin.toPinyin(entity.getName(), ""));
        entity.setName_short_pinyin(simplifiedSpelling(entity.getName()));
        // 法人名称全拼，简拼
        if (StringUtils.isNotEmpty(vo.getLegal_person())){
            entity.setLegal_person_pinyin(Pinyin.toPinyin(entity.getLegal_person(), ""));
            entity.setLegal_person_short_pinyin(simplifiedSpelling(entity.getLegal_person()));
        }
        /** 审批中 */
        entity.setStatus(DictConstant.DICT_M_ENTERPRISE_STATUS_ONE);
        int updCount = mapper.updateById(entity);
        if(updCount == 0){
            throw new UpdateErrorException("您提交的数据已经被修改，请查询后重新编辑更新。");
        }

        // 2.更新企业类型附表
        mEnterpriseTypesMapper.delete(Wrappers.<MEnterpriseTypesEntity>lambdaQuery().eq(MEnterpriseTypesEntity::getEnterprise_id, entity.getId()));
        for (String typeId : vo.getType_ids()) {
            MEnterpriseTypesEntity enterpriseTypesEntity = new MEnterpriseTypesEntity();
            enterpriseTypesEntity.setEnterprise_id(entity.getId());
            enterpriseTypesEntity.setType(typeId);
            mEnterpriseTypesMapper.insert(enterpriseTypesEntity);
        }

        // 3.保存附件
        SFileEntity fileEntity = new SFileEntity();
        fileEntity.setSerial_id(entity.getId());
        fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_M_ENTERPRISE);

        MEnterpriseAttachVo mEnterpriseAttachVo = mEnterpriseAttachMapper.selectEnterpriseId(entity.getId());
        MEnterpriseAttachEntity mEnterpriseAttachEntity = (MEnterpriseAttachEntity) BeanUtilsSupport.copyProperties(mEnterpriseAttachVo, MEnterpriseAttachEntity.class);
        insertFile(fileEntity, vo, mEnterpriseAttachEntity);
        mEnterpriseAttachEntity.setEnterprise_id(entity.getId());
        int update = mEnterpriseAttachMapper.updateById(mEnterpriseAttachEntity);
        if(update == 0){
            throw new UpdateErrorException("您提交的数据已经被修改，请查询后重新编辑更新。");
        }

        // 4.未初始化审批流数据，不启动审批流
//        if (StringUtils.isNotEmpty(vo.getInitial_process())) {
//            // 启动审批流
//            BBpmProcessVo bBpmProcessVo = new BBpmProcessVo();
//            bBpmProcessVo.setCode(SystemConstants.BPM_PROCESS_CODE.BPM_PROCESS_M_ENTERPRISE);
//            bBpmProcessVo.setSerial_type(SystemConstants.BPM_INSTANCE_TYPE.BPM_INSTANCE_M_ENTERPRISE);
//            bBpmProcessVo.setForm_data(vo.getForm_data());
//            bBpmProcessVo.setForm_json(vo);
//            bBpmProcessVo.setForm_class(vo.getClass().getName());
//            bBpmProcessVo.setSerial_id(entity.getId());
//            bBpmProcessVo.setInitial_process(vo.getInitial_process());
//            bBpmProcessVo.setProcess_users(vo.getProcess_users());
//
//            // 组装发起人信息
//            OrgUserVo orgUserVo = new OrgUserVo();
//            orgUserVo.setId(SecurityUtil.getStaff_id().toString());
//            orgUserVo.setName(SecurityUtil.getUserSession().getStaff_info().getName());
//            orgUserVo.setCode(SecurityUtil.getUserSession().getStaff_info().getCode());
//            orgUserVo.setType("user");
//            bBpmProcessVo.setOrgUserVo(orgUserVo);
//
//            // 启动出库计划审批流
//            String url = getBusinessCenterUrl("/scm/api/v1/bpm/process/createstartprocess");
//            ResponseEntity<BBpmProcessVo> response = restTemplate.postForEntity(url, bBpmProcessVo, BBpmProcessVo.class);
//            log.debug("===============启动审批流结果================" + response.getBody());
//
//            // 更新
//            entity.setBpm_instance_id(response.getBody().getId());
//            entity.setNext_approve_name(response.getBody().getNext_approve_name());
//            int updateRtn = mapper.updateById(entity);
//            if (updateRtn == 0) {
//                throw new UpdateErrorException("修改失败");
//            }
//        }

        return UpdateResultUtil.OK(updCount);
    }


    /**
     * 导出
     *
     * @param searchConditionList 参数
     * @return List<MCustomerExcelVo>
     */
    @Override
    public List<MEnterpiseExcelVo> export(AppMEnterpriseVo searchConditionList) {
        return mapper.exportList(searchConditionList);
    }

    /**
     * 获取企业类型
     *
     * @param searchCondition
     */
    @Override
    public List<AppMEnterpriseVo> getType(AppMEnterpriseVo searchCondition) {
        return mapper.getType();
    }

    /**
     * 获取详情
     */
    @Override
    public AppMEnterpriseVo getDetail(AppMEnterpriseVo searchCondition) {
        AppMEnterpriseVo appMEnterpriseVo =  mapper.getDetail(searchCondition);
        appMEnterpriseVo.setType_ids(StringUtils.isNotEmpty(appMEnterpriseVo.getType_ids_str())?appMEnterpriseVo.getType_ids_str().split(","):null);

        // logo附件信息
        List<SFileInfoVo> logo_files = isFileService.selectFileInfo(appMEnterpriseVo.getLogo_file());
        appMEnterpriseVo.setLogo_files(logo_files);

        // 营业执照附件信息
        List<SFileInfoVo> license_att_files = isFileService.selectFileInfo(appMEnterpriseVo.getLicense_att_file());
        appMEnterpriseVo.setLicense_att_files(license_att_files);

        // 身份证正面照附件信息
        List<SFileInfoVo> lr_id_front_att_files = isFileService.selectFileInfo(appMEnterpriseVo.getLr_id_front_att_file());
        appMEnterpriseVo.setLr_id_front_att_files(lr_id_front_att_files);

        // 身份证反面照附件信息
        List<SFileInfoVo> lr_id_back_att_files = isFileService.selectFileInfo(appMEnterpriseVo.getLr_id_back_att_file());
        appMEnterpriseVo.setLr_id_back_att_files(lr_id_back_att_files);

        // 其他附件信息
        List<SFileInfoVo> doc_att_files = isFileService.selectFileInfo(appMEnterpriseVo.getDoc_att_file());
        appMEnterpriseVo.setDoc_att_files(doc_att_files);

        return appMEnterpriseVo;
    }


    @Override
    public AppMEnterpriseVo selectById(Integer id) {
        return mapper.selectId(id);
    }

    /**
     * 删除企业信息
     *
     * @param searchCondition
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public DeleteResultAo<Integer> delete(List<AppMEnterpriseVo> searchCondition) {
        for (AppMEnterpriseVo appMEnterpriseVo : searchCondition) {
            // 更新前check
            AppCheckResultAo cr = checkLogic(appMEnterpriseVo, AppCheckResultAo.DELETE_CHECK_TYPE);
            if (!cr.isSuccess()) {
                throw new BusinessException(cr.getMessage());
            }

            //删除企业信息
            MEnterpriseEntity mEnterpriseEntity = mapper.selectById(appMEnterpriseVo.getId());
            int mCustomer = mapper.updateById(mEnterpriseEntity);
            if (mCustomer == 0) {
                throw new UpdateErrorException("删除企业失败");
            }
        }
        return DeleteResultUtil.OK(1);
    }

    /**
     * 导出全部
     *
     * @param searchConditionList
     */
    @Override
    public List<MEnterpiseExcelVo> exportAll(AppMEnterpriseVo searchConditionList) {

        // 导出限制开关
        SConfigEntity sConfigEntity = configService.selectByKey(SystemConstants.EXPORT_LIMIT_KEY);
        if (!Objects.isNull(sConfigEntity) && "1".equals(sConfigEntity.getValue()) && StringUtils.isNotEmpty(sConfigEntity.getExtra1())) {
            int count = mapper.selectExportNum(searchConditionList);
            if (count > Integer.parseInt(sConfigEntity.getExtra1())) {
                throw new BusinessException(String.format(sConfigEntity.getExtra2(), sConfigEntity.getExtra1()));
            }
        }
        return mapper.exportAll(searchConditionList);
    }

    /**
     * 获取所有调整信息
     *
     * @param searchCondition
     */
    @Override
    public IPage<AppMEnterpriseVo> pagelistByAdjust(AppMEnterpriseVo searchCondition) {
//        mEnterpriseHisMapper.
        // 分页条件
        Page<MEnterpriseEntity> pageCondition =
                new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());
        return  mEnterpriseHisMapper.selectPage(pageCondition, searchCondition);
    }

    /**
     * 校验企业名称
     * @param vo
     * @param vos
     * @return
     */
    public Boolean checkName(MEnterpriseImportVo vo, ArrayList<MEnterpriseImportVo> vos) {
        AppMEnterpriseVo appMEnterpriseVo = new AppMEnterpriseVo();
        appMEnterpriseVo.setName(vo.getName());
        // 查询计划明细数据
        List<AppMEnterpriseVo> selectByName = mapper.selectByName(appMEnterpriseVo);
        if (CollectionUtils.isEmpty(selectByName)) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;

        }
    }

    /**
     * 校验社会信用号
     * @param vo
     * @param vos
     * @return
     */
    public Boolean checkUscc(MEnterpriseImportVo vo, ArrayList<MEnterpriseImportVo> vos) {
        // 查询计划明细数据
        AppMEnterpriseVo selectByName = mapper.selectByUscc(vo.getUscc());
        if (selectByName == null) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    // 校验社会信用号
    public Boolean checkType(MEnterpriseImportVo vo, ArrayList<MEnterpriseImportVo> vos) {
        if (vo.getType_ids_str()==null){
            return Boolean.FALSE;
        }

        // 2.保存企业类型附表
        for (String label : vo.getType_ids_str().split(",")) {
            SDictDataEntity sDictDataEntity = sDictDataMapper.selectByCodeAndLabel(DictConstant.DICT_M_ENTERPRISE_TYPE,label.trim());
            if (sDictDataEntity==null){
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }

    /**
     * 获取详情
     */
    @Override
    public AppMEnterpriseVo getAdjustDetail(AppMEnterpriseVo searchCondition) {

        MEnterpriseHisEntity mEnterpriseHisEntity = mEnterpriseHisMapper.selectById(searchCondition.getId());

        AppMEnterpriseVo appMEnterpriseVo = JSON.parseObject(mEnterpriseHisEntity.getAdjust_info_json(),AppMEnterpriseVo.class);

        // logo附件信息
        List<SFileInfoVo> logo_files = isFileService.selectFileInfo(appMEnterpriseVo.getLogo_file());
        appMEnterpriseVo.setLogo_files(logo_files);

        // 营业执照附件信息
        List<SFileInfoVo> license_att_files = isFileService.selectFileInfo(appMEnterpriseVo.getLicense_att_file());
        appMEnterpriseVo.setLicense_att_files(license_att_files);

        // 身份证正面照附件信息
        List<SFileInfoVo> lr_id_front_att_files = isFileService.selectFileInfo(appMEnterpriseVo.getLr_id_front_att_file());
        appMEnterpriseVo.setLr_id_front_att_files(lr_id_front_att_files);

        // 身份证反面照附件信息
        List<SFileInfoVo> lr_id_back_att_files = isFileService.selectFileInfo(appMEnterpriseVo.getLr_id_back_att_file());
        appMEnterpriseVo.setLr_id_back_att_files(lr_id_back_att_files);

        // 其他附件信息
        List<SFileInfoVo> doc_att_files = isFileService.selectFileInfo(appMEnterpriseVo.getDoc_att_file());
        appMEnterpriseVo.setDoc_att_files(doc_att_files);

        return appMEnterpriseVo;
    }

    /**
     * 导入数据
     *
     * @param beans
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<MEnterpriseImportVo> importData(List<MEnterpriseImportVo> beans) {
        List<MEnterpriseImportVo>  vos = new ArrayList<>();
        for (MEnterpriseImportVo vo : beans) {
            // 1.保存企业信息
            MEnterpriseEntity entity = (MEnterpriseEntity) BeanUtilsSupport.copyProperties(vo, MEnterpriseEntity.class);
            // 企业名称全拼，简拼
            entity.setName_pinyin(Pinyin.toPinyin(entity.getName(), ""));
            entity.setName_short_pinyin(simplifiedSpelling(entity.getName()));
            // 法人名称全拼，简拼
            if (StringUtils.isNotEmpty(vo.getLegal_person())){
                entity.setLegal_person_pinyin(Pinyin.toPinyin(entity.getLegal_person(), ""));
                entity.setLegal_person_short_pinyin(simplifiedSpelling(entity.getLegal_person()));
            }

            entity.setStatus(DictConstant.DICT_M_ENTERPRISE_STATUS_ZERO);
            entity.setCode(mEnterpriseAutoCodeService.autoCode().getCode());

            int rtn = mapper.insert(entity);

            // 2.保存企业类型附表
            for (String label : vo.getType_ids_str().split(",")) {
                SDictDataEntity sDictDataEntity = sDictDataMapper.selectByCodeAndLabel(DictConstant.DICT_M_ENTERPRISE_TYPE,label.trim());
                MEnterpriseTypesEntity mEnterpriseTypesEntity = new MEnterpriseTypesEntity();
                mEnterpriseTypesEntity.setEnterprise_id(entity.getId());
                mEnterpriseTypesEntity.setType(sDictDataEntity.getDict_value());
                mEnterpriseTypesMapper.insert(mEnterpriseTypesEntity);
            }

            MEnterpriseAttachEntity mCustomerInfoEntity = new MEnterpriseAttachEntity();
            mCustomerInfoEntity.setEnterprise_id(entity.getId());
            int insert = mEnterpriseAttachMapper.insert(mCustomerInfoEntity);
            vos.add(vo);
        }
        return vos;
    }


    /**
     * 审批流程通过 更新审核状态通过
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> approveProcess(AppMEnterpriseVo searchCondition) {
        log.debug("====》审批流程通过，更新审核状态开始《====");
        MEnterpriseEntity mEnterpriseEntity = mapper.selectById(searchCondition.getId());
        List<MEnterpriseHisVo> mCustomerAdjustInfoEntities = mEnterpriseHisMapper.selectEnterpriseId(mEnterpriseEntity.getId());
        // 调整信息不为空 赋值曾用名
        if (CollectionUtils.isNotEmpty(mCustomerAdjustInfoEntities)) {
            // 获取上一条企业调整信息
            MEnterpriseHisVo enterpriseHisEntity = mCustomerAdjustInfoEntities.stream().findFirst().get();
            AppMEnterpriseVo enterpriseVo = JSON.parseObject(enterpriseHisEntity.getAdjust_info_json(), AppMEnterpriseVo.class);
            if (!mEnterpriseEntity.getName().equals(enterpriseVo.getName())) {
                mEnterpriseEntity.setFormer_name(enterpriseVo.getName());
                mEnterpriseEntity.setFormer_name_pinyin(enterpriseVo.getName_pinyin());
                mEnterpriseEntity.setFormer_name_short_pinyin(enterpriseVo.getFormer_name_short_pinyin());
            }
        }
        /** 已办结 */
        mEnterpriseEntity.setStatus(DictConstant.DICT_M_ENTERPRISE_STATUS_TWO);
        mEnterpriseEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_COMPLETE);
        int i = mapper.updateById(mEnterpriseEntity);
        if (i == 0) {
            throw new UpdateErrorException("更新审核状态失败");
        }

        // 增加企业调整信息
        MEnterpriseHisEntity mCustomerAdjustInfoEntity = new MEnterpriseHisEntity();
        AppMEnterpriseVo appMEnterpriseVo = getDetail(searchCondition);
        mCustomerAdjustInfoEntity.setEnterprise_id(mEnterpriseEntity.getId());
        mCustomerAdjustInfoEntity.setAdjust_info_json(JSONObject.toJSONString(appMEnterpriseVo));
        int insert = mEnterpriseHisMapper.insert(mCustomerAdjustInfoEntity);
        if (insert == 0) {
            throw new UpdateErrorException("更新审核状态失败");
        }
        log.debug("====》审批流程通过,更新审核状态结束《====");
        return UpdateResultUtil.OK(i);

    }

    /**
     * 审批流程拒绝 更新审核状态驳回
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> rejectProcess(AppMEnterpriseVo searchCondition) {
        log.debug("====》审批流程拒绝，更新审核状态开始《====");
        MEnterpriseEntity mEnterpriseEntity = mapper.selectById(searchCondition.getId());
        mEnterpriseEntity.setStatus(DictConstant.DICT_M_ENTERPRISE_STATUS_THREE);
        mEnterpriseEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_COMPLETE);
        int i = mapper.updateById(mEnterpriseEntity);
        if (i == 0) {
            throw new UpdateErrorException("更新审核状态失败");
        }
        log.debug("====》审批流程拒绝，更新审核状态结束《====");
        return UpdateResultUtil.OK(i);
    }

    /**
     * 审批流程撤销 更新审核状态驳回
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> revokeProcss(AppMEnterpriseVo searchCondition) {
        log.debug("====》审批流程撤销，更新审核状态开始《====");
        MEnterpriseEntity mEnterpriseEntity = mapper.selectById(searchCondition.getId());
        mEnterpriseEntity.setStatus(DictConstant.DICT_M_ENTERPRISE_STATUS_ZERO);
        mEnterpriseEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_COMPLETE);
        int i = mapper.updateById(mEnterpriseEntity);
        if (i == 0) {
            throw new UpdateErrorException("更新审核状态失败");
        }
        log.debug("====》审批流程撤销，更新审核状态结束《====");
        return UpdateResultUtil.OK(i);
    }

    /**
     * check逻辑
     */
    @Override
    public AppCheckResultAo checkLogic(AppMEnterpriseVo vo, String checkType) {
        List<AppMEnterpriseVo> selectByName = mapper.selectByName(vo);
        List<AppMEnterpriseVo> selectByUscc = mapper.selectListByUscc(vo);

        switch (checkType) {
            case CheckResultAo.INSERT_CHECK_TYPE:
                // 新增场合，不能重复
                if (selectByName.size() > 0) {
                    return AppCheckResultUtil.NG("企业名称重复，请修改后重新保存", vo.getName());
                }
                if (selectByUscc.size() > 0) {
                    return AppCheckResultUtil.NG("企业信用代码证重复，请修改后重新保存", vo.getUscc());
                }
                break;
            case CheckResultAo.UPDATE_CHECK_TYPE:
                // 更新场合，不能重复设置
                if (selectByName.size() > 0) {
                    return AppCheckResultUtil.NG("新增保存出错：名称出现重复", vo.getName());
                }
                if (selectByUscc.size() > 0) {
                    return AppCheckResultUtil.NG("新增保存出错：企业信用代码出现重复", vo.getUscc());
                }
                break;
            case CheckResultAo.DELETE_CHECK_TYPE:

                MEnterpriseEntity enterpriseEntity = mapper.selectById(vo.getId());

                // todo 判断合同下有该企业数据

                // todo 订单下有该企业数据

                // 入库计划有该企业数据
//                List<BInPlanEntity> bInPlanEntities = bInPlanMapper.selectByCustomerCode(enterpriseEntity.getCode());
//                if (!bInPlanEntities.isEmpty()) {
//                    return AppCheckResultUtil.NG("删除出错：该企业信息被入库计划使用中", vo.getName());
//                }

                // 入库单有该企业数据
//                List<BInEntity> inEntities = bInMapper.selectByCustomerCode(enterpriseEntity.getCode());
//                if (!inEntities.isEmpty()) {
//                    return AppCheckResultUtil.NG("删除出错：该企业信息被入库单使用中", vo.getName());
//                }

                // 出库计划有该企业数据
//                List<BOutPlanEntity> bOutPlanEntities = bOutPlanMapper.selectByCustomerCode(enterpriseEntity.getCode());
//                if (!bOutPlanEntities.isEmpty()) {
//                    return AppCheckResultUtil.NG("删除出错：该企业信息被入库计划使用中", vo.getName());
//                }
//
//                // 出库单有该企业数据有该企业数据
////                List<BOutEntity> bOutEntities = bOutMapper.selectByCustomerCode(enterpriseEntity.getCode());
//                if (!bOutEntities.isEmpty()) {
//                    return AppCheckResultUtil.NG("删除出错：该企业信息被入库计划使用中", vo.getName());
//                }

                break;
            default:
        }
        return AppCheckResultUtil.OK();
    }

    /**
     *  企业管理审批流程回调
     *  更新最新审批人
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> processCallBack(AppMEnterpriseVo searchCondition) {
        MEnterpriseEntity mEnterpriseEntity = mapper.selectById(searchCondition.getId());
        mEnterpriseEntity.setBpm_instance_id(searchCondition.getBpm_instance_id());
        mEnterpriseEntity.setBpm_instance_code(searchCondition.getBpm_instance_code());
        mEnterpriseEntity.setNext_approve_name(searchCondition.getNext_approve_name());
        int i = mapper.updateById(mEnterpriseEntity);
        return UpdateResultUtil.OK(i);
    }

    /**
     * 增加审批流 企业信息新增
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public InsertResultAo<Integer> insert(AppMEnterpriseVo bean) {
        // 添加企业数据
        InsertResultAo<Integer> integerInsertResultAo = insertBussinessData(bean);
        //启动审批流
        startFlowProcess(bean);
        return integerInsertResultAo;
    }

    /**
     * 增加审批流 企业信息更新
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public UpdateResultAo<Integer> update(AppMEnterpriseVo vo) {
        // 修改企业数据
        UpdateResultAo<Integer> update = updateBussinessData(vo);
        //启动审批流
        startFlowProcess(vo);
        return update;
    }

    /**
     * 启动审批流
     */
    public void startFlowProcess(AppMEnterpriseVo bean){
        // 未初始化审批流数据，不启动审批流
        if (StringUtils.isNotEmpty(bean.getInitial_process())) {
            // 启动审批流
            BBpmProcessVo bBpmProcessVo = new BBpmProcessVo();
            bBpmProcessVo.setCode(bpmProcessTemplatesService.getBpmFLowCodeByType(SystemConstants.BPM_INSTANCE_TYPE.BPM_INSTANCE_M_ENTERPRISE));
            bBpmProcessVo.setSerial_type(SystemConstants.BPM_INSTANCE_TYPE.BPM_INSTANCE_M_ENTERPRISE);
            bBpmProcessVo.setForm_data(bean.getForm_data());
            bBpmProcessVo.setForm_json(bean);
            bBpmProcessVo.setForm_class(bean.getClass().getName());
            bBpmProcessVo.setSerial_id(bean.getId());
            bBpmProcessVo.setInitial_process(bean.getInitial_process());
            bBpmProcessVo.setProcess_users(bean.getProcess_users());

            // 组装发起人信息
            OrgUserVo orgUserVo = new OrgUserVo();
            orgUserVo.setId(SecurityUtil.getStaff_id().toString());

            AppStaffUserBpmInfoVo param = new AppStaffUserBpmInfoVo();
            param.setId(SecurityUtil.getStaff_id());
            AppStaffUserBpmInfoVo appStaffUserBpmInfoVo = bpmProcessTemplatesService.getBpmDataByStaffid(param);

            orgUserVo.setName(appStaffUserBpmInfoVo.getName());
            orgUserVo.setCode(appStaffUserBpmInfoVo.getCode());
            orgUserVo.setType("user");
            bBpmProcessVo.setOrgUserVo(orgUserVo);

            // 启动出库计划审批流
            bpmProcessTemplatesService.startProcess(bBpmProcessVo);
        }
    }

}
