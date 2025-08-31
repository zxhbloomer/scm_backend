package com.xinyirun.scm.controller.master.org;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.entity.master.org.MPositionEntity;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.master.org.MPositionExportVo;
import com.xinyirun.scm.bean.system.vo.master.org.MPositionVo;
import com.xinyirun.scm.bean.system.vo.sys.pages.SPagesVo;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.constant.PageCodeConstant;
import com.xinyirun.scm.common.exception.system.InsertErrorException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.core.system.service.master.org.IMPositionService;
import com.xinyirun.scm.core.system.service.master.rbac.permission.role.IMRolePositionService;
import com.xinyirun.scm.core.system.service.master.rbac.permission.IMPermissionPositionService;
import com.xinyirun.scm.core.system.service.sys.pages.ISPagesService;
import com.xinyirun.scm.core.system.mapper.sys.pages.SPagesMapper;
import com.xinyirun.scm.excel.export.EasyExcelUtil;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.utils.DateTimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author zhangxh
 */
@RestController
@RequestMapping(value = "/api/v1/org/position")
@Slf4j
// @Api(tags = "岗位表相关")
public class OrgPositionController extends SystemBaseController {

    @Autowired
    private IMPositionService service;

    @Autowired
    private IMRolePositionService rolePositionService;

    @Autowired
    private IMPermissionPositionService permissionPositionService;

    @Autowired
    private ISPagesService sPagesService;

    @Autowired
    private SPagesMapper sPagesMapper;

    @Autowired
    private RestTemplate restTemplate;

    @SysLogAnnotion("根据查询条件，获取岗位主表信息")
    @PostMapping("/id")
    @ResponseBody
    public ResponseEntity<JsonResultAo<MPositionVo>> id(@RequestBody(required = false)
        MPositionVo searchCondition)  {
        MPositionVo entity = service.selectByid(searchCondition.getId());
        return ResponseEntity.ok().body(ResultUtil.OK(entity));
    }

    @SysLogAnnotion("根据查询条件，获取岗位主表信息")
    @PostMapping("/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<IPage<MPositionVo>>> list(@RequestBody(required = false) MPositionVo searchCondition)  {
        IPage<MPositionVo> entity = service.selectPage(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(entity));
    }

    @SysLogAnnotion("根据查询条件，获取岗位仓库权限信息")
    @PostMapping("/detail")
    @ResponseBody
    public ResponseEntity<JsonResultAo<MPositionVo>> getWarehousePermission(@RequestBody(required = false) MPositionVo searchCondition)  {
        MPositionVo vo = service.getDetail(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(vo));
    }

    @SysLogAnnotion("岗位主表数据更新保存")
    @PostMapping("/warehousepermission/save")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<MPositionVo>> saveWarehousePermission(@RequestBody(required = false) MPositionVo vo) {
        service.updateWarehousePermission(vo);
        return ResponseEntity.ok().body(ResultUtil.OK(vo));
    }

    @SysLogAnnotion("岗位主表数据更新保存")
    @PostMapping("/save")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<MPositionVo>> save(@RequestBody(required = false) MPositionEntity bean) {
        if(service.update(bean).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectByid(bean.getId()),"更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    @SysLogAnnotion("岗位主表数据新增保存")
    @PostMapping("/insert")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<MPositionVo>> insert(@RequestBody(required = false) MPositionEntity bean) {
        if(service.insert(bean).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(service.selectByid(bean.getId()),"插入成功"));
        } else {
            throw new InsertErrorException("新增保存失败。");
        }
    }

    @SysLogAnnotion("岗位信息导出")
    @PostMapping("/exportall")
    public void exportAll(@RequestBody(required = false) MPositionVo searchCondition, HttpServletResponse response) throws IOException {
        // 通过页面编码获取完整的页面对象（包含id字段）
        SPagesVo sPagesVo = sPagesMapper.selectByCode(PageCodeConstant.PAGE_POSITION);
        
        try {
            // 设置导出处理状态为true
            sPagesService.updateExportProcessingTrue(sPagesVo);
            
            // 全部导出：直接调用selectExportList查询方法
            List<MPositionExportVo> exportDataList = service.selectExportList(searchCondition);
            log.info("全部导出：查询到岗位数据 {} 条", exportDataList.size());
            
            EasyExcelUtil<MPositionExportVo> util = new EasyExcelUtil<>(MPositionExportVo.class);
            String fileName = "岗位信息导出_" + DateTimeUtil.dateTimeNow();
            util.exportExcel(fileName, "岗位信息", exportDataList, response);
        } finally {
            // 无论成功失败都要恢复导出处理状态为false
            sPagesService.updateExportProcessingFalse(sPagesVo);
        }
    }

    @SysLogAnnotion("岗位信息导出")
    @PostMapping("/export")
    public void export(@RequestBody(required = false) MPositionVo searchCondition, HttpServletResponse response) throws IOException {
        // 通过页面编码获取完整的页面对象（包含id字段）
        SPagesVo sPagesVo = sPagesMapper.selectByCode(PageCodeConstant.PAGE_POSITION);
        
        try {
            // 设置导出处理状态为true
            sPagesService.updateExportProcessingTrue(sPagesVo);
            
            // 选中导出：参数验证
            if (searchCondition == null || searchCondition.getIds() == null || searchCondition.getIds().length == 0) {
                throw new BusinessException("请选择要导出的岗位记录");
            }
            
            // 选中导出：直接调用selectExportList查询方法
            List<MPositionExportVo> exportDataList = service.selectExportList(searchCondition);
            log.info("选中导出：查询到岗位数据 {} 条", exportDataList.size());
            
            EasyExcelUtil<MPositionExportVo> util = new EasyExcelUtil<>(MPositionExportVo.class);
            String fileName = "岗位信息导出_" + DateTimeUtil.dateTimeNow();
            util.exportExcel(fileName, "岗位信息", exportDataList, response);
        } finally {
            // 无论成功失败都要恢复导出处理状态为false
            sPagesService.updateExportProcessingFalse(sPagesVo);
        }
    }

    @SysLogAnnotion("岗位主表数据逻辑删除复原")
    @PostMapping("/delete")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> delete(@RequestBody(required = false) List<MPositionVo> searchConditionList) {
        service.deleteByIdsIn(searchConditionList);
        return ResponseEntity.ok().body(ResultUtil.OK("OK"));
    }

    @SysLogAnnotion("从组织架构删除岗位")
    @PostMapping("/delete/org")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> deleteFromOrg(@RequestBody(required = false) List<MPositionVo> searchConditionList) {
        service.deleteByIdsFromOrg(searchConditionList);
        return ResponseEntity.ok().body(ResultUtil.OK("从组织架构删除成功"));
    }

    @SysLogAnnotion("获取岗位已分配的角色ID列表")
    @PostMapping("/role/assigned")
    @ResponseBody
    public ResponseEntity<JsonResultAo<List<Integer>>> getPositionAssignedRoleIds(@RequestBody Map<String, Long> request) {
        Long positionId = request.get("position_id");
        if (positionId == null) {
            throw new BusinessException("岗位ID不能为空");
        }
        List<Integer> roleIds = rolePositionService.getPositionAssignedRoleIds(positionId);
        return ResponseEntity.ok().body(ResultUtil.OK(roleIds));
    }

    @SysLogAnnotion("保存岗位角色关系（全删全插）")
    @PostMapping("/role/save")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> savePositionRoles(@RequestBody Map<String, Object> request) {
        Object positionIdObj = request.get("positionId");
        Object roleIdsObj = request.get("roleIds");
        
        // 处理positionId参数
        Long positionId;
        if (positionIdObj instanceof Number) {
            positionId = ((Number) positionIdObj).longValue();
        } else if (positionIdObj instanceof String) {
            try {
                positionId = Long.parseLong((String) positionIdObj);
            } catch (NumberFormatException e) {
                throw new BusinessException("岗位ID格式错误");
            }
        } else {
            throw new BusinessException("岗位ID不能为空");
        }
        
        // 处理roleIds参数
        List<Integer> roleIds = new ArrayList<>();
        if (roleIdsObj instanceof List) {
            List<?> list = (List<?>) roleIdsObj;
            for (Object item : list) {
                if (item instanceof Number) {
                    roleIds.add(((Number) item).intValue());
                }
            }
        }
        
        boolean success = rolePositionService.savePositionRoles(positionId, roleIds);
        if (success) {
            return ResponseEntity.ok().body(ResultUtil.OK("岗位角色保存成功"));
        } else {
            throw new BusinessException("岗位角色保存失败");
        }
    }

    @SysLogAnnotion("获取岗位已分配的权限ID列表")
    @PostMapping("/permissions/assigned")
    @ResponseBody
    public ResponseEntity<JsonResultAo<List<Long>>> getPositionAssignedPermissionIds(@RequestBody Map<String, Object> request) {
        Object positionIdObj = request.get("position_id");
        
        // 处理positionId参数
        Long positionId;
        if (positionIdObj instanceof Number) {
            positionId = ((Number) positionIdObj).longValue();
        } else if (positionIdObj instanceof String) {
            try {
                positionId = Long.parseLong((String) positionIdObj);
            } catch (NumberFormatException e) {
                throw new BusinessException("岗位ID格式错误");
            }
        } else {
            throw new BusinessException("岗位ID不能为空");
        }
        
        List<Long> permissionIds = permissionPositionService.getAssignedPermissionIds(positionId);
        return ResponseEntity.ok().body(ResultUtil.OK(permissionIds));
    }

    @SysLogAnnotion("保存岗位权限关系（全删全插）")
    @PostMapping("/permissions/save")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> savePositionPermissions(@RequestBody Map<String, Object> request) {
        Object positionIdObj = request.get("positionId");
        Object permissionIdsObj = request.get("permissionIds");
        
        // 处理positionId参数
        Long positionId;
        if (positionIdObj instanceof Number) {
            positionId = ((Number) positionIdObj).longValue();
        } else if (positionIdObj instanceof String) {
            try {
                positionId = Long.parseLong((String) positionIdObj);
            } catch (NumberFormatException e) {
                throw new BusinessException("岗位ID格式错误");
            }
        } else {
            throw new BusinessException("岗位ID不能为空");
        }
        
        // 处理permissionIds参数
        List<Long> permissionIds = new ArrayList<>();
        if (permissionIdsObj instanceof List) {
            List<?> list = (List<?>) permissionIdsObj;
            for (Object item : list) {
                if (item instanceof Number) {
                    permissionIds.add(((Number) item).longValue());
                }
            }
        }
        
        boolean success = permissionPositionService.savePositionPermissions(positionId, permissionIds);
        if (success) {
            return ResponseEntity.ok().body(ResultUtil.OK("岗位权限保存成功"));
        } else {
            throw new BusinessException("岗位权限保存失败");
        }
    }
}
