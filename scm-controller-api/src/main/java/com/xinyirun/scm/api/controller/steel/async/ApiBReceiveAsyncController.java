package com.xinyirun.scm.api.controller.steel.async;


import com.xinyirun.scm.api.controller.steel.base.v1.ApiBaseController;
import com.xinyirun.scm.bean.api.vo.business.out.ApiBReceiveAsyncVo;
import com.xinyirun.scm.bean.api.vo.sync.ApiInPlanIdCodeVo;
import com.xinyirun.scm.bean.api.vo.sync.ApiReceivePlanIdCodeVo;
import com.xinyirun.scm.bean.system.vo.business.wms.out.receive.BReceiveVo;
import com.xinyirun.scm.bean.system.vo.sys.config.config.SAppConfigDetailVo;
import com.xinyirun.scm.common.annotations.SysLogApiAnnotion;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.core.system.service.business.wms.out.receive.IBReceiveService;
import com.xinyirun.scm.core.system.service.sys.config.config.ISAppConfigDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 * <p>
 * 收货单 前端控制器
 * </p>
 *
 * @author htt
 * @since 2021-09-24
 */
@Slf4j
// @Api(tags = "入库计划")
@RestController
@RequestMapping(value = "/api/service/v1/steel/async/receive")
public class ApiBReceiveAsyncController extends ApiBaseController {

    @Autowired
    private ISAppConfigDetailService isAppConfigDetailService;

    @Autowired
    private IBReceiveService ibReceiveService;


    /**
     * 调用API接口，同步收货信息
     */
    @SysLogApiAnnotion("收货单数据同步")
    @PostMapping("/execute")
    @ResponseBody
    public void execute(@RequestBody ApiBReceiveAsyncVo asyncVo) {
        if(asyncVo.getBeans() == null || asyncVo.getBeans().size() == 0){
            return;
        }
        for (BReceiveVo vo : asyncVo.getBeans()) {
            log.debug("=============同步出库单信息start=============");
            BReceiveVo bReceiveVo = ibReceiveService.selectById(vo.getId());
            // extra_code为空或者状态为作废审核中或状态为制单的不同步
            if(bReceiveVo.getExtra_code() == null || Objects.equals(bReceiveVo.getStatus(), DictConstant.DICT_B_RECEIVE_STATUS_SAVED) || Objects.equals(bReceiveVo.getStatus(), DictConstant.DICT_B_RECEIVE_STATUS_CANCEL_BEING_AUDITED)){
                continue;
            }
            ApiReceivePlanIdCodeVo apiReceivePlanIdCodeVo = new ApiReceivePlanIdCodeVo();
            apiReceivePlanIdCodeVo.setPlan_code(bReceiveVo.getPlan_code());
            apiReceivePlanIdCodeVo.setPlan_id(bReceiveVo.getPlan_id());
            apiReceivePlanIdCodeVo.setReceive_code(bReceiveVo.getCode());
            apiReceivePlanIdCodeVo.setReceive_id(bReceiveVo.getId());
            SAppConfigDetailVo sAppConfigDetail = isAppConfigDetailService.getDataByCode(SystemConstants.APP_CODE.ZT, asyncVo.getApp_config_type());
            String url = getBusinessCenterUrl(sAppConfigDetail.getUri(), SystemConstants.APP_CODE.ZT);
            ResponseEntity<ApiInPlanIdCodeVo> response = restTemplate.postForEntity(url, apiReceivePlanIdCodeVo, ApiInPlanIdCodeVo.class);
            log.debug("=============同步出库单信息返回============="+response.getBody());

            log.debug("=============同步出库单信息end=============");
        }
    }


}
