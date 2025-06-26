package com.xinyirun.scm.controller.query.inventory;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.master.inventory.query.MInventoryOwnerGoodsQueryVo;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.core.system.service.query.inventory.IMInventoryOwnerGoodsQueryService;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/**
 * <p>
 * 货主库存查询
 * </p>
 *
 * @author xyr
 * @since 2021-09-24
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/v1/query/inventory/owner_goods")
public class MInventoryOwnerGoodsQueryController extends SystemBaseController {

    @Autowired
    private IMInventoryOwnerGoodsQueryService service;

    @SysLogAnnotion("货主库存查询")
    @PostMapping("/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<MInventoryOwnerGoodsQueryVo>>> queryInventoryDetails(@RequestBody(required = false) MInventoryOwnerGoodsQueryVo searchCondition) {
        IPage<MInventoryOwnerGoodsQueryVo> list = service.queryInventoryOwnerGoods(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }
}
