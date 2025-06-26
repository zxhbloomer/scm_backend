package com.xinyirun.scm.core.api.serviceimpl.master.v1.enterprise;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.master.customer.MCustomerEntity;
import com.xinyirun.scm.bean.entity.master.enterprise.MEnterpriseEntity;
import com.xinyirun.scm.bean.entity.master.org.MPositionEntity;
import com.xinyirun.scm.bean.entity.sys.config.config.SConfigEntity;
import com.xinyirun.scm.bean.system.vo.master.enterprise.MEnterpriseVo;
import com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo;
import com.xinyirun.scm.bean.system.vo.sys.pages.SPagesVo;
import com.xinyirun.scm.common.constant.PageCodeConstant;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.core.api.mapper.master.enterprise.ApiEnterpriseMapper;
import com.xinyirun.scm.core.api.mapper.master.position.ApiPositionMapper;
import com.xinyirun.scm.core.api.service.master.v1.enterprise.ApiEnterpriseService;
import com.xinyirun.scm.core.system.service.sys.config.config.ISAppConfigService;
import com.xinyirun.scm.core.system.service.sys.config.config.ISConfigService;
import com.xinyirun.scm.core.system.service.sys.file.ISFileService;
import com.xinyirun.scm.core.system.service.sys.pages.ISPagesService;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * <p>
 *  企业服务类
 * </p>
 *
 */
@Service
public class ApiEnterpriseServiceImpl extends BaseServiceImpl<ApiEnterpriseMapper, MEnterpriseEntity> implements ApiEnterpriseService {

    @Autowired
    ApiEnterpriseMapper mapper;

    @Autowired
    private ISFileService isFileService;

    @Autowired
    ISConfigService isConfigService;

    @Autowired
    ISPagesService isPagesService;

    @Autowired
    ISAppConfigService isAppConfigService;

    /**
     * 获取企业详情
     * @param searchCondition
     * @return
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

        mEnterpriseVo = getPrintInfo(mEnterpriseVo);

        return mEnterpriseVo;
    }

    /**
     *  获取报表系统参数，并组装打印参数
     */
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
     * 获取营业执照
     */
    public List<SFileInfoVo> getprintEnterpriseLicense(MEnterpriseVo searchCondition) {
        List<SFileInfoVo> sFileInfoVoList = isFileService.selectFileInfo(searchCondition.getFile_id());
        return sFileInfoVoList;
    }
}
