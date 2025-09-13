package com.xinyirun.scm.controller.business.warehouse.relation;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.business.wms.warehouse.relation.MRelationTreeVo;
import com.xinyirun.scm.bean.system.vo.business.wms.warehouse.relation.MWarehouseGroupTransferVo;
import com.xinyirun.scm.bean.system.vo.business.wms.warehouse.relation.MWarehouseRelationVo;
import com.xinyirun.scm.bean.system.vo.business.wms.warehouse.relation.MWarehouseTransferVo;
import com.xinyirun.scm.bean.system.vo.master.warehouse.MWarehouseVo;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.exception.system.InsertErrorException;
import com.xinyirun.scm.core.system.service.business.warehouse.relation.IMWarehouseRelationService;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * @author zhangxh
 */
@RestController
@RequestMapping(value = "/api/v1/relation")
@Slf4j
public class RelationController extends SystemBaseController {

    @Autowired
    private IMWarehouseRelationService service;

    @Autowired
    private RestTemplate restTemplate;

    @SysLogAnnotion("根据查询条件，获取仓库分组信息")
    @PostMapping("/tree/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<List<MRelationTreeVo>>> treeList(@RequestBody(required = false) MRelationTreeVo searchCondition) {
        List<MRelationTreeVo> vo = service.getTreeList(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(vo));
    }

//    @SysLogAnnotion("根据查询条件，获取所有的组织以及子组织数量，仅仅是数量")
//    @PostMapping("/count")
//    @ResponseBody
//    public ResponseEntity<JsonResultAo<MRelationCountsVo>> getAllRelationDataCount(@RequestBody(required = false) MWarehouseRelationVo searchCondition)  {
//        MRelationCountsVo vo = service.getAllRelationDataCount(searchCondition);
//        return ResponseEntity.ok().body(ResultUtil.OK(vo));
//    }

    @SysLogAnnotion("根据查询条件，获取仓库关系信息")
    @PostMapping("/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<List<MRelationTreeVo>>> getWarehouses(@RequestBody(required = false) MWarehouseRelationVo searchCondition)  {
        List<MRelationTreeVo> list = service.getRelations(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("根据查询条件，获取仓库信息")
    @PostMapping("/warehouse")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<MWarehouseVo>>> getWarehouseListApi(@RequestBody(required = false) MRelationTreeVo searchCondition)  {
        IPage<MWarehouseVo> list = service.getWarehouse(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("根据查询条件，获取仓库信息")
    @PostMapping("/warehouse/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<List<MWarehouseVo>>> getWarehouseAllListApi(@RequestBody(required = false) MRelationTreeVo searchCondition)  {
        List<MWarehouseVo> list = service.getWarehouseList(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("根据查询条件，获取仓库信息")
    @PostMapping("/warehouse/all")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<MWarehouseVo>>> getAllWarehouseListByPositionApi(@RequestBody(required = false) MRelationTreeVo searchCondition)  {
        IPage<MWarehouseVo> list = service.getAllWarehouseListByPosition(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("根据查询条件，获取仓库信息count")
    @PostMapping("/warehouse/count")
    @ResponseBody
    public ResponseEntity<JsonResultAo<Integer>> getAllWarehouseListByPositionCountApi(@RequestBody(required = false) MRelationTreeVo searchCondition)  {
        return ResponseEntity.ok().body(ResultUtil.OK(service.getAllWarehouseListByPositionCount(searchCondition)));
    }
    @SysLogAnnotion("仓库分组新增")
    @PostMapping("/insert")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<MWarehouseRelationVo>> insert(@RequestBody(required = false) MWarehouseRelationVo bean) {
        if(service.insert(bean).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectByid(bean),"插入成功"));
        } else {
            throw new InsertErrorException("新增保存失败。");
        }
    }

    @SysLogAnnotion("删除子节点")
    @PostMapping("/delete")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> delete(@RequestBody(required = false) MWarehouseRelationVo bean) {
        Boolean rtn = service.deleteById(bean);
        return ResponseEntity.ok().body(ResultUtil.OK("删除成功"));
    }

    @SysLogAnnotion("获取所有仓库的数据，为穿梭框服务")
    @PostMapping("/warehouse/transfer/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<MWarehouseGroupTransferVo>> getWarehouseTransferList(@RequestBody(required = false) MWarehouseTransferVo bean) {
        MWarehouseGroupTransferVo rtn = service.getWarehouseTransferList(bean);
        return ResponseEntity.ok().body(ResultUtil.OK(rtn));
    }

    @SysLogAnnotion("保存穿梭框数据，仓库设置")
    @PostMapping("/warehouse/transfer/save")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> setWarehouseTransferList(@RequestBody(required = false) MWarehouseTransferVo bean) {
        return ResponseEntity.ok().body(ResultUtil.OK(service.setWarehouseTransfer(bean)));
    }
}
