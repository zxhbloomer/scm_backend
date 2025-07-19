package com.xinyirun.scm.core.system.serviceimpl.master.enterprise;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.promeg.pinyinhelper.Pinyin;
import com.xinyirun.scm.bean.entity.bpm.BpmInstanceSummaryEntity;
import com.xinyirun.scm.bean.entity.busniess.wms.out.BOutEntity;
import com.xinyirun.scm.bean.entity.busniess.wms.out.BOutPlanEntity;
import com.xinyirun.scm.bean.entity.master.enterprise.MEnterpriseAttachEntity;
import com.xinyirun.scm.bean.entity.master.enterprise.MEnterpriseEntity;
import com.xinyirun.scm.bean.entity.master.enterprise.MEnterpriseHisEntity;
import com.xinyirun.scm.bean.entity.master.enterprise.MEnterpriseTypesEntity;
import com.xinyirun.scm.bean.entity.master.org.MCompanyEntity;
import com.xinyirun.scm.bean.entity.sys.config.config.SConfigEntity;
import com.xinyirun.scm.bean.entity.sys.config.dict.SDictDataEntity;
import com.xinyirun.scm.bean.entity.sys.file.SFileEntity;
import com.xinyirun.scm.bean.entity.sys.file.SFileInfoEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.DeleteResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.CheckResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.DeleteResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.UpdateResultUtil;
import com.xinyirun.scm.bean.system.vo.business.bpm.BBpmProcessVo;
import com.xinyirun.scm.bean.system.vo.business.bpm.OrgUserVo;
import com.xinyirun.scm.bean.system.vo.excel.customer.MEnterpiseExcelVo;
import com.xinyirun.scm.bean.system.vo.master.enterprise.MEnterpriseAttachVo;
import com.xinyirun.scm.bean.system.vo.master.enterprise.MEnterpriseHisVo;
import com.xinyirun.scm.bean.system.vo.master.enterprise.MEnterpriseImportVo;
import com.xinyirun.scm.bean.system.vo.master.enterprise.MEnterpriseVo;
import com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo;
import com.xinyirun.scm.bean.system.vo.sys.pages.SPagesVo;
import com.xinyirun.scm.bean.utils.security.SecurityUtil;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.constant.PageCodeConstant;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.core.bpm.serviceimpl.business.BpmProcessTemplatesServiceImpl;
import com.xinyirun.scm.core.system.mapper.business.wms.in.BInMapper;
import com.xinyirun.scm.core.system.mapper.business.wms.inplan.BInPlanMapper;
import com.xinyirun.scm.core.system.mapper.business.wms.out.BOutMapper;
import com.xinyirun.scm.core.system.mapper.business.wms.out.BOutPlanMapper;
import com.xinyirun.scm.core.system.mapper.master.enterpise.MEnterpriseAttachMapper;
import com.xinyirun.scm.core.system.mapper.master.enterpise.MEnterpriseHisMapper;
import com.xinyirun.scm.core.system.mapper.master.enterpise.MEnterpriseMapper;
import com.xinyirun.scm.core.system.mapper.master.enterpise.MEnterpriseTypesMapper;
import com.xinyirun.scm.core.system.mapper.sys.config.dict.SDictDataMapper;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileInfoMapper;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileMapper;
import com.xinyirun.scm.core.system.service.master.enterprise.IMEnterpriseService;
import com.xinyirun.scm.core.system.service.sys.config.config.ISAppConfigService;
import com.xinyirun.scm.core.system.service.sys.config.config.ISConfigService;
import com.xinyirun.scm.core.system.service.sys.file.ISFileService;
import com.xinyirun.scm.core.system.service.sys.pages.ISPagesService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.MEnterpriseAutoCodeServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.xinyirun.scm.core.bpm.service.business.IBpmInstanceSummaryService;

import java.time.LocalDateTime;
import java.util.*;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Service
public class MEnterpriseServiceImpl extends BaseServiceImpl<MEnterpriseMapper, MEnterpriseEntity> implements IMEnterpriseService {

    private static final Logger log = LoggerFactory.getLogger(MEnterpriseServiceImpl.class);

    @Autowired
    private MEnterpriseMapper mapper;

    @Autowired
    private MEnterpriseAutoCodeServiceImpl mEnterpriseAutoCodeService;

    @Autowired
    private SFileMapper fileMapper;

    @Autowired
    private ISFileService isFileService;

    @Autowired
    private SFileInfoMapper fileInfoMapper;

    @Autowired
    private MEnterpriseAttachMapper mEnterpriseAttachMapper;

    @Autowired
    private MEnterpriseMapper mEnterpriseMapper;

    @Autowired
    private MEnterpriseHisMapper mEnterpriseHisMapper;

    @Autowired
    private MEnterpriseTypesMapper mEnterpriseTypesMapper;

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
    BpmProcessTemplatesServiceImpl bpmProcessTemplatesService;

    @Autowired
    ISConfigService isConfigService;

    @Autowired
    ISPagesService isPagesService;

    @Autowired
    ISAppConfigService isAppConfigService;

    @Autowired
    IBpmInstanceSummaryService iBpmInstanceSummaryService;

//    @Value("${server.port}")
//    private int port;

    @Override
    public IPage<MEnterpriseVo> selectPage(MEnterpriseVo searchCondition) {
        // 分页条件
        Page<MEnterpriseVo> pageCondition =
                new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());
        return mapper.selectPage(pageCondition, searchCondition);
    }

    /**
     * 新增企业，业务数据
     */
    public InsertResultAo<Integer> insertBussinessData(MEnterpriseVo vo) {
        // 插入前check
        CheckResultAo cr = checkLogic(vo, CheckResultAo.INSERT_CHECK_TYPE);
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

        /** 审批流程名称 */
        entity.setBpm_process_name("新增企业审批");
        /** 版本 */
        entity.setVersion(0);
        /** 未删除 */
        entity.setIs_del(Boolean.FALSE);
        /** 主体企业：0-false（不是）、1-true（是） */
        entity.setIs_sys_company(Boolean.FALSE);

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
    public MEnterpriseAttachEntity insertFile(SFileEntity fileEntity, MEnterpriseVo vo, MEnterpriseAttachEntity extra) {

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
    public UpdateResultAo<Integer> updateBussinessData(MEnterpriseVo vo) {
        // 更新前check
        CheckResultAo cr = checkLogic(vo, CheckResultAo.UPDATE_CHECK_TYPE);
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

        /** 审批流程名称 */
        entity.setBpm_process_name("更新企业审批");

        /**
         * 已审批的数据，修改时，版本号+1
         * */
        if (DictConstant.DICT_M_ENTERPRISE_STATUS_TWO.equals(vo.getStatus())) {
            entity.setVersion(entity.getVersion() + 1);
        }

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
    public List<MEnterpiseExcelVo> export(MEnterpriseVo searchConditionList) {
        return mapper.exportList(searchConditionList);
    }

    /**
     * 获取企业类型
     *
     * @param searchCondition
     */
    @Override
    public List<MEnterpriseVo> getType(MEnterpriseVo searchCondition) {
        return mapper.getType();
    }

    /**
     * 获取详情
     */
    @Override
    public MEnterpriseVo getDetail(MEnterpriseVo searchCondition) {
        MEnterpriseVo mEnterpriseVo =  mapper.getDetail(searchCondition);
        mEnterpriseVo.setType_ids(StringUtils.isNotEmpty(mEnterpriseVo.getType_ids_str())?mEnterpriseVo.getType_ids_str().split(","):null);

        // logo附件信息
        List<SFileInfoVo> logo_files = isFileService.selectFileInfo(mEnterpriseVo.getLogo_file());
        mEnterpriseVo.setLogo_files(logo_files);

        // 营业执照附件信息
        List<SFileInfoVo> license_att_files = isFileService.selectFileInfo(mEnterpriseVo.getLicense_att_file());
        mEnterpriseVo.setLicense_att_files(license_att_files);

        // 身份证正面照附件信息
        List<SFileInfoVo> lr_id_front_att_files = isFileService.selectFileInfo(mEnterpriseVo.getLr_id_front_att_file());
        mEnterpriseVo.setLr_id_front_att_files(lr_id_front_att_files);

        // 身份证反面照附件信息
        List<SFileInfoVo> lr_id_back_att_files = isFileService.selectFileInfo(mEnterpriseVo.getLr_id_back_att_file());
        mEnterpriseVo.setLr_id_back_att_files(lr_id_back_att_files);

        // 其他附件信息
        List<SFileInfoVo> doc_att_files = isFileService.selectFileInfo(mEnterpriseVo.getDoc_att_file());
        mEnterpriseVo.setDoc_att_files(doc_att_files);

        return mEnterpriseVo;
    }


    @Override
    public MEnterpriseVo selectById(Integer id) {
        return mapper.selectId(id);
    }

    /**
     * 删除企业信息
     *
     * @param searchCondition
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public DeleteResultAo<Integer> delete(List<MEnterpriseVo> searchCondition) {
        for (MEnterpriseVo mEnterpriseVo : searchCondition) {
            // 更新前check
            CheckResultAo cr = checkLogic(mEnterpriseVo, CheckResultAo.DELETE_CHECK_TYPE);
            if (!cr.isSuccess()) {
                throw new BusinessException(cr.getMessage());
            }

            //删除企业信息
            MEnterpriseEntity mEnterpriseEntity = mapper.selectById(mEnterpriseVo.getId());
            mEnterpriseEntity.setIs_del(Boolean.TRUE);
            int rtn = mapper.updateById(mEnterpriseEntity);
            if (rtn == 0) {
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
    public List<MEnterpiseExcelVo> exportAll(MEnterpriseVo searchConditionList) {

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
    public List<MEnterpriseHisVo> getAdjustList(MEnterpriseHisVo searchCondition) {
        return  mEnterpriseHisMapper.getAdjustList(searchCondition);
    }

    /**
     * 校验社会信用号
     * @param vo
     * @param vos
     * @return
     */
    public Boolean checkUscc(MEnterpriseImportVo vo, ArrayList<MEnterpriseImportVo> vos) {
        // 查询计划明细数据
        MEnterpriseVo selectByName = mapper.selectByUscc(vo.getUscc());
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
    public MEnterpriseVo getAdjustDetail(MEnterpriseVo searchCondition) {

        MEnterpriseHisEntity mEnterpriseHisEntity = mEnterpriseHisMapper.selectById(searchCondition.getId());

        MEnterpriseVo mEnterpriseVo = JSON.parseObject(mEnterpriseHisEntity.getAdjust_info_json(),MEnterpriseVo.class);

        // logo附件信息
        List<SFileInfoVo> logo_files = isFileService.selectFileInfo(mEnterpriseVo.getLogo_file());
        mEnterpriseVo.setLogo_files(logo_files);

        // 营业执照附件信息
        List<SFileInfoVo> license_att_files = isFileService.selectFileInfo(mEnterpriseVo.getLicense_att_file());
        mEnterpriseVo.setLicense_att_files(license_att_files);

        // 身份证正面照附件信息
        List<SFileInfoVo> lr_id_front_att_files = isFileService.selectFileInfo(mEnterpriseVo.getLr_id_front_att_file());
        mEnterpriseVo.setLr_id_front_att_files(lr_id_front_att_files);

        // 身份证反面照附件信息
        List<SFileInfoVo> lr_id_back_att_files = isFileService.selectFileInfo(mEnterpriseVo.getLr_id_back_att_file());
        mEnterpriseVo.setLr_id_back_att_files(lr_id_back_att_files);

        // 其他附件信息
        List<SFileInfoVo> doc_att_files = isFileService.selectFileInfo(mEnterpriseVo.getDoc_att_file());
        mEnterpriseVo.setDoc_att_files(doc_att_files);

        return mEnterpriseVo;
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
            /** 待审批 */
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
     *
     *  企业管理审批流程回调
     *  审批流程创建时
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackCreateBpm(MEnterpriseVo searchCondition){
        log.debug("====》审批流程创建成功，更新开始《====");
        MEnterpriseVo mEnterpriseVo = getDetail(searchCondition);

        /**
         * 需要判断m_enterprise.version是否和m_enterprise_his对应的企业最大的version不一致时，需要新增数据
         * 1、更新bpm_instance的摘要数据:
         * 2、新增EnterpriseHistory的数据
         * bpm_instance_summary:{}  // 企业名称：xxx，社会信用号：xxx，法人：xxx,类型：xxx
         */
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("企业名称：", mEnterpriseVo.getName());
        jsonObject.put("社会信用：", mEnterpriseVo.getUscc());
        if (mEnterpriseVo.getLegal_person()  != null) {
            jsonObject.put("法人：", mEnterpriseVo.getLegal_person());
        }
        jsonObject.put("类型：", mEnterpriseVo.getType_names());
        String json = jsonObject.toString();
        BpmInstanceSummaryEntity bpmInstanceSummaryEntity = new BpmInstanceSummaryEntity();
        bpmInstanceSummaryEntity.setProcessCode(searchCondition.getBpm_instance_code());
        bpmInstanceSummaryEntity.setSummary(json);
        bpmInstanceSummaryEntity.setProcess_definition_business_name(mEnterpriseVo.getBpm_process_name());
        iBpmInstanceSummaryService.save(bpmInstanceSummaryEntity);


        /**
         * 此时的数据库中，bpm还没有task完成的回调，所以新增数据的bpm信息为空，所以需要从参数中获取
         */
        mEnterpriseVo.setBpm_instance_id(searchCondition.getBpm_instance_id());
        mEnterpriseVo.setBpm_instance_code(searchCondition.getBpm_instance_code());
        Integer count = mapper.getMaxEnterpriseHisVersionByid(searchCondition.getId());
        if(count == null || count< mEnterpriseVo.getVersion()){
// 增加企业调整信息
            MEnterpriseHisEntity mEnterpriseHisEntity = new MEnterpriseHisEntity();

            mEnterpriseHisEntity.setEnterprise_name(mEnterpriseVo.getName());
            mEnterpriseHisEntity.setEnterprise_id(mEnterpriseVo.getId());
            mEnterpriseHisEntity.setAdjust_info_json(JSONObject.toJSONString(mEnterpriseVo));
            mEnterpriseHisEntity.setUscc(mEnterpriseVo.getUscc());
            mEnterpriseHisEntity.setVersion(mEnterpriseVo.getVersion());
            mEnterpriseHisEntity.setModify_reason(mEnterpriseVo.getModify_reason());
            if(mEnterpriseHisEntity.getVersion() == 0){
                mEnterpriseHisEntity.setModify_reason("新增");
            }
            int insert = mEnterpriseHisMapper.insert(mEnterpriseHisEntity);
            if (insert == 0) {
                throw new UpdateErrorException("更新审核状态失败");
            }
            return UpdateResultUtil.OK(1);
        }
        return UpdateResultUtil.OK(0);
    }

    /**
     * 审批流程通过 更新审核状态通过
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackApprove(MEnterpriseVo searchCondition) {
        log.debug("====》审批流程通过，更新审核状态开始《====");
        MEnterpriseEntity mEnterpriseEntity = mapper.selectById(searchCondition.getId());
        List<MEnterpriseHisVo> mCustomerAdjustInfoEntities = mEnterpriseHisMapper.selectEnterpriseId(mEnterpriseEntity.getId());
        // 调整信息不为空 赋值曾用名
        if (CollectionUtils.isNotEmpty(mCustomerAdjustInfoEntities)) {
            // 获取上一条企业调整信息
            MEnterpriseHisVo enterpriseHisEntity = mCustomerAdjustInfoEntities.stream().findFirst().get();
            MEnterpriseVo enterpriseVo = JSON.parseObject(enterpriseHisEntity.getAdjust_info_json(), MEnterpriseVo.class);
            if (!mEnterpriseEntity.getName().equals(enterpriseVo.getName())) {
                mEnterpriseEntity.setFormer_name(enterpriseVo.getName());
                mEnterpriseEntity.setFormer_name_pinyin(enterpriseVo.getName_pinyin());
                mEnterpriseEntity.setFormer_name_short_pinyin(enterpriseVo.getFormer_name_short_pinyin());
            }
        }
        /** 审批通过 */
        mEnterpriseEntity.setStatus(DictConstant.DICT_M_ENTERPRISE_STATUS_TWO);
        mEnterpriseEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_COMPLETE);
        int i = mapper.updateById(mEnterpriseEntity);
        if (i == 0) {
            throw new UpdateErrorException("更新审核状态失败");
        }

//        /**
//         * 此处不应该记录表版本
//         */
//        // 增加企业调整信息
//        MEnterpriseHisEntity mEnterpriseHisEntity = new MEnterpriseHisEntity();
//        MEnterpriseVo mEnterpriseVo = getDetail(searchCondition);
//        mEnterpriseHisEntity.setEnterprise_name(mEnterpriseVo.getName());
//        mEnterpriseHisEntity.setEnterprise_id(mEnterpriseEntity.getId());
//        mEnterpriseHisEntity.setAdjust_info_json(JSONObject.toJSONString(mEnterpriseVo));
//        mEnterpriseHisEntity.setUscc(mEnterpriseVo.getUscc());
//        mEnterpriseHisEntity.setVersion(mEnterpriseEntity.getVersion());
//        mEnterpriseHisEntity.setModify_reason(mEnterpriseEntity.getModify_reason());
//        if(mEnterpriseHisEntity.getVersion() == 0){
//            mEnterpriseHisEntity.setModify_reason("新增");
//        }
//        int insert = mEnterpriseHisMapper.insert(mEnterpriseHisEntity);
//        if (insert == 0) {
//            throw new UpdateErrorException("更新审核状态失败");
//        }
        log.debug("====》审批流程通过,更新审核状态结束《====");
        return UpdateResultUtil.OK(i);

    }

    /**
     * 审批流程拒绝 更新审核状态驳回
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackRefuse(MEnterpriseVo searchCondition) {
        log.debug("====》审批流程拒绝，更新审核状态开始《====");
        MEnterpriseEntity mEnterpriseEntity = mapper.selectById(searchCondition.getId());
        /** 审批驳回 */
        mEnterpriseEntity.setStatus(DictConstant.DICT_M_ENTERPRISE_STATUS_THREE);
        mEnterpriseEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_REFUSE);
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
    public UpdateResultAo<Integer> bpmCallBackCancel(MEnterpriseVo searchCondition) {
        log.debug("====》审批流程撤销，更新审核状态开始《====");
        MEnterpriseEntity mEnterpriseEntity = mapper.selectById(searchCondition.getId());
        mEnterpriseEntity.setStatus(DictConstant.DICT_M_ENTERPRISE_STATUS_ZERO);
        mEnterpriseEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_CANCEL);
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
    public CheckResultAo checkLogic(MEnterpriseVo vo, String checkType) {
        List<MEnterpriseVo> selectByName = mapper.validateDuplicateName(vo);
        List<MEnterpriseVo> selectByUscc = mapper.validateDuplicateUscc(vo);

        switch (checkType) {
            case CheckResultAo.INSERT_CHECK_TYPE:
                // 新增场合，不能重复
                if (selectByName.size() > 0) {
                    return CheckResultUtil.NG("企业名称重复，请修改后重新保存", vo.getName());
                }
                if (selectByUscc.size() > 0) {
                    return CheckResultUtil.NG("企业信用代码证重复，请修改后重新保存", vo.getUscc());
                }
                break;
            case CheckResultAo.UPDATE_CHECK_TYPE:
                // 更新场合，不能重复设置
                if (selectByName.size() > 0) {
                    return CheckResultUtil.NG("新增保存出错：名称出现重复", vo.getName());
                }
                if (selectByUscc.size() > 0) {
                    return CheckResultUtil.NG("新增保存出错：企业信用代码出现重复", vo.getUscc());
                }
                break;
            case CheckResultAo.DELETE_CHECK_TYPE:

                MEnterpriseEntity enterpriseEntity = mapper.selectById(vo.getId());

                // todo 判断合同下有该企业数据

                // todo 订单下有该企业数据

                // 入库计划有该企业数据
//                List<BInPlanEntity> bInPlanEntities = bInPlanMapper.selectByCustomerCode(enterpriseEntity.getCode());
//                if (!bInPlanEntities.isEmpty()) {
//                    return CheckResultUtil.NG("删除出错：该企业信息被入库计划使用中", vo.getName());
//                }

                // 入库单有该企业数据
//                List<BInEntity> inEntities = bInMapper.selectByCustomerCode(enterpriseEntity.getCode());
//                if (!inEntities.isEmpty()) {
//                    return CheckResultUtil.NG("删除出错：该企业信息被入库单使用中", vo.getName());
//                }

                // 出库计划有该企业数据
                List<BOutPlanEntity> bOutPlanEntities = bOutPlanMapper.selectByCustomerCode(enterpriseEntity.getCode());
                if (!bOutPlanEntities.isEmpty()) {
                    return CheckResultUtil.NG("删除出错：该企业信息被入库计划使用中", vo.getName());
                }

                // 出库单有该企业数据有该企业数据
                List<BOutEntity> bOutEntities = bOutMapper.selectByCustomerCode(enterpriseEntity.getCode());
                if (!bOutEntities.isEmpty()) {
                    return CheckResultUtil.NG("删除出错：该企业信息被入库计划使用中", vo.getName());
                }

                break;
            default:
        }
        return CheckResultUtil.OK();
    }

    /**
     *  企业管理审批流程回调
     *  更新最新审批人
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackSave(MEnterpriseVo searchCondition) {
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
    public InsertResultAo<Integer> insert(MEnterpriseVo bean) {
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
    public UpdateResultAo<Integer> update(MEnterpriseVo vo) {
        // 修改企业数据
        UpdateResultAo<Integer> update = updateBussinessData(vo);
        //启动审批流
        startFlowProcess(vo);
        return update;
    }

    /**
     * 启动审批流
     */
    public void startFlowProcess(MEnterpriseVo bean){
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
            orgUserVo.setName(SecurityUtil.getUserSession().getStaff_info().getName());
            orgUserVo.setCode(SecurityUtil.getUserSession().getStaff_info().getCode());
            orgUserVo.setType("user");
            bBpmProcessVo.setOrgUserVo(orgUserVo);

            // 启动出库计划审批流
            bpmProcessTemplatesService.startProcess(bBpmProcessVo);
        }
    }

    /**
     * 校验社会信用号，校验db中的数据重复
     * @param vo
     * @param vos
     * @return
     */
    public Boolean checkUsccDuplicationDb (MEnterpriseImportVo vo, ArrayList<MEnterpriseImportVo> vos) {
        // 校验社会信用号
        MEnterpriseVo selectByName = mapper.selectByUscc(vo.getUscc());
        if (selectByName == null) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    /**
     * 校验社会信用号，校验excel中的数据是否重复
     * @param vo
     * @param vos
     * @return
     */
    public Boolean checkUsccDuplicationExcel (MEnterpriseImportVo vo, ArrayList<MEnterpriseImportVo> vos) {
        Set<String> usccSet = new HashSet<>();
        /**
         * 此部分把vos数据放到set中
         */
        for (MEnterpriseImportVo item : vos) {
            String vosUscc = item.getUscc();
            if (usccSet.contains(vosUscc)) {
                return Boolean.FALSE; // 列表内存在重复，直接返回false
            }
            usccSet.add(vosUscc);
        }
        /**
         * 此部分校验vo是否在vos中重复
         */
        if (usccSet.contains(vo.getUscc())) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    /**
     *  获取报表系统参数，并组装打印参数
     */
    @Override
    public MEnterpriseVo getPrintInfo(MEnterpriseVo searchCondition){
        /**
         * 获取打印配置信息
         * 1、从s_config中获取到：print_system_config、
         */
        SConfigEntity _data = isConfigService.selectByKey(SystemConstants.PRINT_SYSTEM_CONFIG);
        String url = _data.getValue();
        String token = _data.getExtra1();

        /**
         * 获取打印配置信息
         * 2、从s_page中获取到print_code
         */
        SPagesVo param = new SPagesVo();
        param.setCode(PageCodeConstant.PAGE_ENTERPRISE_MASTER);
        SPagesVo pagesVo = isPagesService.get(param);

        /**
         * 获取打印配置信息
         * 3、从s_app_config中获取，报表系统的app_key，securit_key
         */
//        SAppConfigEntity key = isAppConfigService.getDataByAppCode(AppConfigConstant.PRINT_SYSTEM_CODE);

        String printUrl =  url + pagesVo.getPrint_code() + "?token=" + token + "&id=" + searchCondition.getId();
//        printUrl = printUrl + "&app_key=" + key.getApp_key() + "&secret_key=" + key.getSecret_key();
        searchCondition.setPrint_url(printUrl);
        searchCondition.setQr_code(printUrl);
        log.debug("打印地址：" + printUrl);
        return searchCondition;
    }

    /**
     * 获取企业下拉列表数据（交易对手、供应商）
     * @param searchCondition
     * @return
     */
    @Override
    public IPage<MEnterpriseVo> selectCounterpartySupplierGridData(MEnterpriseVo searchCondition) {
        // 分页条件
        Page<MEnterpriseVo> pageCondition =
                new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());
        return mapper.selectCounterpartySupplierGridData(pageCondition, searchCondition);
    }

    /**
     * 获取企业下拉列表数据（交易对手、买方、客户、经销商）
     * @param searchCondition
     * @return
     */
    @Override
    public IPage<MEnterpriseVo> selectCounterpartyCustomerGridData(MEnterpriseVo searchCondition) {
        // 分页条件
        Page<MEnterpriseVo> pageCondition =
                new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());
        return mapper.selectCounterpartyCustomerGridData(pageCondition, searchCondition);
    }

    /**
     * 获取企业下拉列表数据（主体企业、系统企业、供应商）
     * @param searchCondition
     * @return
     */
    @Override
    public IPage<MEnterpriseVo> selectSystemEnterpriseSupplierGridData(MEnterpriseVo searchCondition) {
        // 分页条件
        Page<MEnterpriseVo> pageCondition =
                new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());
        return mapper.selectSystemEnterpriseSupplierGridData(pageCondition, searchCondition);
    }

    /**
     * 获取企业下拉列表数据（主体企业、系统企业、买方、经销商）
     * @param searchCondition
     * @return
     */
    @Override
    public IPage<MEnterpriseVo> selectSystemEnterpriseCustomerGridData(MEnterpriseVo searchCondition) {
        // 分页条件
        Page<MEnterpriseVo> pageCondition =
                new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());
        return mapper.selectSystemEnterpriseCustomerGridData(pageCondition, searchCondition);
    }

    /**
     * 根据查询条件，获取企业列表（交易对手、供应商）
     * @param searchCondition
     * @return
     */
    @Override
    public IPage<MEnterpriseVo> getCounterpartySupplierList(MEnterpriseVo searchCondition) {
        // 分页条件
        Page<MEnterpriseVo> pageCondition =
                new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());
        return mapper.getCounterpartySupplierList(pageCondition, searchCondition);
    }

    /**
     * 根据查询条件，获取企业列表（主体企业、系统企业、买方、经销商）
     * @param searchCondition
     * @return
     */
    @Override
    public IPage<MEnterpriseVo> getSystemEnterpriseCustomerList(MEnterpriseVo searchCondition) {
        // 分页条件
        Page<MEnterpriseVo> pageCondition =
                new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());
        return mapper.getSystemEnterpriseCustomerList(pageCondition, searchCondition);
    }

    /**
     * 根据组织模块公司数据新增系统企业
     *
     * @param companyEntity 组织模块公司实体
     * @return 插入结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<Integer> insertSystemEnterpriseByOrgCompany(MCompanyEntity companyEntity) {
        // 创建新的企业实体
        MEnterpriseEntity entity = new MEnterpriseEntity();
        
        // 复制字段
        entity.setUscc(companyEntity.getCompany_no()); // 统一社会信用代码
        entity.setName(companyEntity.getName()); // 企业名称
        entity.setLegal_person(companyEntity.getJuridical_name()); // 法人代表
        entity.setRegistration_capital(companyEntity.getRegister_capital()); // 注册资本
        entity.setEst_date(companyEntity.getSetup_date() != null ? 
                           LocalDateTime.of(companyEntity.getSetup_date(), java.time.LocalTime.MIDNIGHT) : null); // 成立时间
        entity.setAddress(companyEntity.getSimple_name()); // 简称暂存到地址字段
        entity.setContact_person(null); // 联系人为空
        entity.setContact_phone(null); // 联系电话为空
        entity.setRemark(companyEntity.getDescr()); // 备注
        
        // 自动生成企业编码
        entity.setCode(mEnterpriseAutoCodeService.autoCode().getCode());
        
        // 设置审批状态为已通过(2)
        entity.setStatus("2");

        /** 版本 */
        entity.setVersion(0);
        
        // 设置是否系统企业
        entity.setIs_sys_company(true);
        
        // 设置为未删除
        entity.setIs_del(false);
        
        // 生成拼音字段
        entity.setName_pinyin(Pinyin.toPinyin(entity.getName(), ""));
        entity.setName_short_pinyin(simplifiedSpelling(entity.getName()));
        
        if (StringUtils.isNotEmpty(entity.getLegal_person())) {
            entity.setLegal_person_pinyin(Pinyin.toPinyin(entity.getLegal_person(), ""));
            entity.setLegal_person_short_pinyin(simplifiedSpelling(entity.getLegal_person()));
        }
        // 插入企业数据
        int insert = mapper.insert(entity);

        // 新增企业类型类型表
        MEnterpriseTypesEntity mEnterpriseTypesEntity = new MEnterpriseTypesEntity();
        mEnterpriseTypesEntity.setEnterprise_id(entity.getId());
        mEnterpriseTypesEntity.setType(DictConstant.DICT_M_CUSTOMER_TYPE_ZERO); // 系统企业类型:类型:5-加工厂，4-承运商，3-仓储方，2-供应商，1-客户，0-主体企业
        mEnterpriseTypesMapper.insert(mEnterpriseTypesEntity);

//        插入数据库
        return InsertResultUtil.OK(1);
    }
    
    /**
     * 根据组织模块公司数据更新系统企业
     *
     * @param companyEntity 组织模块公司实体
     * @return 更新结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> updateSystemEnterpriseByOrgCompany(MCompanyEntity companyEntity) {
        // 根据统一社会信用代码查找企业
        MEnterpriseEntity existingEntity = getOne(
            Wrappers.<MEnterpriseEntity>lambdaQuery()
                .eq(MEnterpriseEntity::getUscc, companyEntity.getCompany_no())
                .eq(MEnterpriseEntity::getIs_del, false)
        );
        
        if (existingEntity == null) {
            // 如果找不到，则新增
            insertSystemEnterpriseByOrgCompany(companyEntity);
            // 如果存在，则更新
            return UpdateResultUtil.OK(1);
        }
        // 如果存在，则更新
        // 复制字段
        existingEntity.setName(companyEntity.getName()); // 企业名称
        existingEntity.setLegal_person(companyEntity.getJuridical_name()); // 法人代表
        existingEntity.setRegistration_capital(companyEntity.getRegister_capital()); // 注册资本
        existingEntity.setEst_date(companyEntity.getSetup_date() != null ? 
                                   LocalDateTime.of(companyEntity.getSetup_date(), java.time.LocalTime.MIDNIGHT) : null); // 成立时间
        existingEntity.setAddress(companyEntity.getSimple_name()); // 简称暂存到地址字段
        existingEntity.setRemark(companyEntity.getDescr()); // 备注

        // 更新拼音字段
        existingEntity.setName_pinyin(Pinyin.toPinyin(existingEntity.getName(), ""));
        existingEntity.setName_short_pinyin(simplifiedSpelling(existingEntity.getName()));

        /** 版本 */
        existingEntity.setVersion(existingEntity.getVersion() + 1);
        
        if (StringUtils.isNotEmpty(existingEntity.getLegal_person())) {
            existingEntity.setLegal_person_pinyin(Pinyin.toPinyin(existingEntity.getLegal_person(), ""));
            existingEntity.setLegal_person_short_pinyin(simplifiedSpelling(existingEntity.getLegal_person()));
        }
        return UpdateResultUtil.OK(mapper.updateById(existingEntity));
    }
}
