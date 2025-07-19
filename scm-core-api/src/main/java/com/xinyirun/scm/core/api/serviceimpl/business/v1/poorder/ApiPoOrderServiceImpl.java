package com.xinyirun.scm.core.api.serviceimpl.business.v1.poorder;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.busniess.po.poorder.BPoOrderEntity;
import com.xinyirun.scm.bean.entity.sys.config.config.SConfigEntity;
import com.xinyirun.scm.bean.system.vo.business.po.poorder.PoOrderDetailVo;
import com.xinyirun.scm.bean.system.vo.business.po.poorder.PoOrderVo;
import com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo;
import com.xinyirun.scm.bean.system.vo.sys.pages.SPagesVo;
import com.xinyirun.scm.common.constant.PageCodeConstant;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.core.api.mapper.business.poorder.ApiPoOrderMapper;
import com.xinyirun.scm.core.api.service.business.v1.poorder.ApiPoOrderService;
import com.xinyirun.scm.core.system.service.sys.config.config.ISConfigService;
import com.xinyirun.scm.core.system.service.sys.file.ISFileService;
import com.xinyirun.scm.core.system.service.sys.pages.ISPagesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 采购订单表 服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2025-01-14
 */
@Slf4j
@Service
public class ApiPoOrderServiceImpl extends ServiceImpl<ApiPoOrderMapper, BPoOrderEntity> implements ApiPoOrderService {

    @Autowired
    private ApiPoOrderMapper mapper;

    @Autowired
    private ISFileService isFileService;

    @Autowired
    private ISConfigService isConfigService;

    @Autowired
    private ISPagesService isPagesService;


    /**
     * 获取采购合同信息
     * @param id
     */
    @Override
    public PoOrderVo selectById(Integer id) {
        PoOrderVo poOrderVo = mapper.selectId(id);

        // 其他附件信息
        List<SFileInfoVo> doc_att_files = isFileService.selectFileInfo(poOrderVo.getDoc_att_file());
        poOrderVo.setDoc_att_files(doc_att_files);

        poOrderVo = getPrintInfo(poOrderVo);
        return poOrderVo;
    }

    /**
     * 获取报表系统参数，并组装打印参数
     *
     * @param searchCondition
     */
    public PoOrderVo getPrintInfo(PoOrderVo searchCondition) {
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
        param.setCode(PageCodeConstant.PAGE_PO_ORDER);
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
     * 获取合同附件
     */
    public List<SFileInfoVo> getprintEnterpriseLicense(PoOrderVo searchCondition) {
        List<SFileInfoVo> sFileInfoVoList = isFileService.selectFileInfo(searchCondition.getDoc_att_file());
        return sFileInfoVoList;
    }

    /**
     * 获取采购合同商品信息
     * @param poContractId
     */
    @Override
    public List<PoOrderDetailVo> selectGoodsById(Integer poContractId) {
        return mapper.selectGoodsById(poContractId);
    }


}
