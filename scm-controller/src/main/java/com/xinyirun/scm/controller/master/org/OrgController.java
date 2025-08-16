package com.xinyirun.scm.controller.master.org;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.entity.master.org.MOrgEntity;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.common.component.NameAndValueVo;
import com.xinyirun.scm.bean.system.vo.master.org.*;
import com.xinyirun.scm.bean.system.vo.master.user.MStaffVo;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.exception.system.InsertErrorException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.core.system.service.master.org.IMOrgService;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
// import io.swagger.annotations.Api;
// import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * @author zhangxh
 */
@RestController
@RequestMapping(value = "/api/v1/org")
@Slf4j
// @Api(tags = "组织架构主表相关")
public class OrgController extends SystemBaseController {

    @Autowired
    private IMOrgService service;

    @Autowired
    private RestTemplate restTemplate;

    @SysLogAnnotion("根据查询条件，获取组织机构信息")
    // @ApiOperation(value = "获取组织机构树数据")
    @PostMapping("/tree/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<List<MOrgTreeVo>>> treeList(@RequestBody(required = false) MOrgTreeVo searchCondition) {
        List<MOrgTreeVo> vo = service.getTreeList(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(vo));
    }

    @SysLogAnnotion("根据查询条件，获取所有的组织以及子组织数量，仅仅是数量")
    // @ApiOperation(value = "根据查询条件，获取所有的组织以及子组织数量，仅仅是数量")
    @PostMapping("/count")
    @ResponseBody
    public ResponseEntity<JsonResultAo<MOrgCountsVo>> getAllOrgDataCount(@RequestBody(required = false) MOrgVo searchCondition)  {
        MOrgCountsVo vo = service.getAllOrgDataCount(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(vo));
    }

    @SysLogAnnotion("根据查询条件，获取组织架构主表信息")
    // @ApiOperation(value = "根据查询条件，获取组织架构主表信息")
    @PostMapping("/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<List<MOrgTreeVo>>> getOrgs(@RequestBody(required = false) MOrgVo searchCondition)  {
        List<MOrgTreeVo> list = service.getOrgs(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("根据查询条件，获取集团信息")
    // @ApiOperation(value = "根据查询条件，获取集团信息")
    @PostMapping("/groups")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<MGroupVo>>> getGroups(@RequestBody(required = false) MOrgTreeVo searchCondition)  {
        IPage<MGroupVo> list = service.getGroups(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("根据查询条件，获取企业信息")
    // @ApiOperation(value = "根据查询条件，获取企业信息")
    @PostMapping("/companies")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<MCompanyVo>>> getCompanies(@RequestBody(required = false) MOrgTreeVo searchCondition)  {
        IPage<MCompanyVo> list = service.getCompanies(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("根据查询条件，获取部门信息")
    // @ApiOperation(value = "根据查询条件，获取部门信息")
    @PostMapping("/depts")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<MDeptVo>>> getDepts(@RequestBody(required = false) MOrgTreeVo searchCondition)  {
        IPage<MDeptVo> list = service.getDepts(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("根据查询条件，获取岗位信息")
    // @ApiOperation(value = "根据查询条件，获取岗位信息")
    @PostMapping("/positions")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<MPositionVo>>> getPositions(@RequestBody(required = false) MOrgTreeVo searchCondition)  {
        IPage<MPositionVo> list = service.getPositions(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("根据查询条件，获取员工信息")
    // @ApiOperation(value = "根据查询条件，获取员工信息")
    @PostMapping("/staffs")
    @ResponseBody
    public ResponseEntity<JsonResultAo<List<MStaffVo>>> getStaffs(@RequestBody(required = false) MOrgVo searchCondition)  {
        List<MStaffVo> list = service.getStaffs(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    @SysLogAnnotion("组织架构主表数据更新保存")
    // @ApiOperation(value = "根据参数id，获取组织架构主表信息")
    @PostMapping("/save")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<MOrgVo>> save(@RequestBody(required = false) MOrgEntity bean) {

        if(service.update(bean).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectByid(bean.getId()),"更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @SysLogAnnotion("组织架构主表数据新增保存")
    // @ApiOperation(value = "根据参数id，获取组织架构主表信息")
    @PostMapping("/insert")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<MOrgVo>> insert(@RequestBody(required = false) MOrgEntity bean) {
        if(service.insert(bean).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectByid(bean.getId()),"插入成功"));
        } else {
            throw new InsertErrorException("新增保存失败。");
        }
    }

    @SysLogAnnotion("新增模式下，可新增子结点得类型")
    // @ApiOperation(value = "新增模式下，可新增子结点得类型")
    @PostMapping("/get_type")
    @ResponseBody
    public ResponseEntity<JsonResultAo<List<NameAndValueVo>>> getCorrectTypeByInsertStatus(@RequestBody(required = false) MOrgVo bean) {
        List<NameAndValueVo> rtn = service.getCorrectTypeByInsertStatus(bean);
        return ResponseEntity.ok().body(ResultUtil.OK(rtn));
    }

    @SysLogAnnotion("删除子节点")
    // @ApiOperation(value = "新增模式下，可新增子结点得类型")
    @PostMapping("/delete")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> delete(@RequestBody(required = false) MOrgEntity bean) {
        Boolean rtn = service.deleteById(bean);
        return ResponseEntity.ok().body(ResultUtil.OK("删除成功"));
    }

    @SysLogAnnotion("组织架构主表数据更新保存，拖拽后，全量更新")
    // @ApiOperation(value = "根据参数id，获取组织架构主表信息，拖拽后，全量更新")
    @PostMapping("/dragsave")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> dragsave(@RequestBody(required = false) List<MOrgTreeVo> beans) {
        service.dragsave(beans);
        return ResponseEntity.ok().body(ResultUtil.OK("拖拽更新成功"));
    }

    @SysLogAnnotion("获取所有员工的数据，为穿梭框服务")
    // @ApiOperation(value = "获取所有员工的数据，为穿梭框服务")
    @PostMapping("/staff/transfer/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<MStaffPositionTransferVo>> getStaffTransferList(@RequestBody(required = false) MStaffTransferVo bean) {
        MStaffPositionTransferVo rtn = service.getStaffTransferList(bean);
        return ResponseEntity.ok().body(ResultUtil.OK(rtn));
    }

    @SysLogAnnotion("保存穿梭框数据，员工岗位设置")
    // @ApiOperation(value = "保存穿梭框数据，员工岗位设置")
    @PostMapping("/staff/transfer/save")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<MStaffPositionTransferVo>> setStaffTransferList(@RequestBody(required = false) MStaffTransferVo bean) {
        return ResponseEntity.ok().body(ResultUtil.OK(service.setStaffTransfer(bean)));
    }


    @SysLogAnnotion("根据查询条件，获取员工主表信息")
    // @ApiOperation(value = "根据参数id，获取员工主表信息")
    @PostMapping("/staff/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<MStaffTabVo>> list(@RequestBody(required = false) MStaffTabDataVo searchCondition) {
        MStaffTabVo entity = service.selectStaff(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(entity));
    }

    @SysLogAnnotion("获取组织子节点数量")
    // @ApiOperation(value = "获取组织子节点数量")
    @PostMapping("/getsubcount")
    @ResponseBody
    public ResponseEntity<JsonResultAo<Integer>> getSubCount(@RequestBody MOrgSubCountRequestVo request) {
            Integer count = service.getSubCount(request.getOrg_id());
            return ResponseEntity.ok().body(ResultUtil.OK(count));
    }

}
