package com.xinyirun.scm.core.api.serviceimpl.business.v1.aprefund;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.busniess.po.aprefund.BApReFundEntity;
import com.xinyirun.scm.bean.entity.sys.config.config.SConfigEntity;
import com.xinyirun.scm.bean.system.vo.business.po.aprefund.BApReFundSourceAdvanceVo;
import com.xinyirun.scm.bean.system.vo.business.po.aprefund.BApReFundVo;
import com.xinyirun.scm.bean.system.vo.business.po.aprefund.BApReFundDetailVo;
import com.xinyirun.scm.bean.system.vo.sys.pages.SPagesVo;
import com.xinyirun.scm.common.constant.PageCodeConstant;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.core.api.mapper.business.aprefund.ApiApReFundMapper;
import com.xinyirun.scm.core.api.service.business.v1.aprefund.ApiApReFundService;
import com.xinyirun.scm.core.system.service.sys.config.config.ISConfigService;
import com.xinyirun.scm.core.system.service.sys.pages.ISPagesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 应付退款表 服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2025-01-14
 */
@Slf4j
@Service
public class ApiApReFundServiceImpl extends ServiceImpl<ApiApReFundMapper, BApReFundEntity> implements ApiApReFundService {

    @Autowired
    private ApiApReFundMapper mapper;

    @Autowired
    private ISConfigService isConfigService;

    @Autowired
    private ISPagesService isPagesService;


    /**
     * 获取应付退款信息
     * @param id
     */
    @Override
    public BApReFundVo selectById(Integer id) {
        BApReFundVo bApVo = mapper.selById(id);
        return getPrintInfo(bApVo);
    }

    /**
     * 获取报表系统参数，并组装打印参数
     *
     * @param searchCondition
     */
    public BApReFundVo getPrintInfo(BApReFundVo searchCondition) {
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
        param.setCode(PageCodeConstant.PAGE_PO_CONTRACT);
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
     * 应付账款管理-业务单据信息
     */
    @Override
    public List<BApReFundSourceAdvanceVo> printPoOrder(BApReFundVo searchCondition) {
        return mapper.printPoOrder(searchCondition);
    }

    /**
     * 应付账款管理-付款信息
     */
    @Override
    public List<BApReFundDetailVo> bankAccounts(BApReFundVo searchCondition) {
        return mapper.bankAccounts(searchCondition);
    }


}
