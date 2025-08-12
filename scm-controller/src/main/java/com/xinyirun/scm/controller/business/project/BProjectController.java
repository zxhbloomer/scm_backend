package com.xinyirun.scm.controller.business.project;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.JsonResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.bean.system.vo.business.project.BProjectVo;
import com.xinyirun.scm.bean.system.vo.business.project.BProjectExportVo;
import com.xinyirun.scm.common.annotations.RepeatSubmitAnnotion;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.InsertErrorException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.core.system.service.business.project.IBProjectService;
import com.xinyirun.scm.excel.export.EasyExcelUtil;
import com.xinyirun.scm.excel.merge.ProjectMergeStrategy;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * <p>
 * 项目管理表 前端控制器
 * 负责处理项目管理相关的HTTP请求，包括项目的增删改查、审批流程管理、数据校验、打印等功能
 * 支持分页查询、条件过滤、数据统计等操作
 * 集成了审批流程（BPM）、文件上传、重复提交防护等功能
 * </p>
 *
 * @author xinyirun
 * @since 2024-12-11
 */
@RestController
@Slf4j
@RequestMapping("/api/v1/project")
public class BProjectController extends SystemBaseController {

    @Autowired
    private IBProjectService ibProjectService;

    /**
     * 分页查询项目管理列表
     * 支持多条件筛选、排序、分页等功能
     * 
     * @param searchCondition 查询条件对象，包含分页参数、筛选条件等
     *                       - pageCondition: 分页参数（页码、页大小、排序方式）
     *                       - status: 项目状态筛选
     *                       - type: 项目类型筛选
     *                       - keyword: 关键字搜索（项目名称、编号等）
     *                       - dateRange: 时间范围筛选
     * @return ResponseEntity<JsonResultAo<IPage<BProjectVo>>> 分页查询结果
     *         - 成功时返回分页数据，包含总记录数、当前页数据等
     *         - 失败时返回错误信息
     * @throws BusinessException 当查询条件不合法或系统异常时抛出
     */
    @PostMapping("/pagelist")
    @SysLogAnnotion("根据查询条件查询列表")
    public ResponseEntity<JsonResultAo<IPage<BProjectVo>>> pagelist(@RequestBody(required = false) BProjectVo searchCondition) {
        IPage<BProjectVo> list = ibProjectService.selectPage(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    /**
     * 根据ID查询项目管理详情
     * 获取指定项目的完整信息，包括基本信息、商品明细、附件、审批状态等
     * 
     * @param searchCondition 查询条件对象，必须包含项目ID
     *                       - id: 项目主键ID（必填）
     * @return ResponseEntity<JsonResultAo<BProjectVo>> 项目详情数据
     *         - 成功时返回完整的项目信息，包括：
     *           * 基本信息（项目名称、编号、类型、状态等）
     *           * 商品明细列表
     *           * 附件信息
     *           * 审批流程信息
     *           * 作废信息（如果已作废）
     *         - 项目不存在时返回null
     * @throws BusinessException 当ID为空或项目不存在时抛出
     */
    @PostMapping("/get")
    @SysLogAnnotion("查询详情")
    public ResponseEntity<JsonResultAo<BProjectVo>> get(@RequestBody(required = false) BProjectVo searchCondition) {
        BProjectVo detail = ibProjectService.selectById(searchCondition.getId());
        return ResponseEntity.ok().body(ResultUtil.OK(detail));
    }

    /**
     * 查询项目管理列表（不分页）
     * 根据条件查询所有符合条件的项目记录，主要用于下拉选择、导出等场景
     * 
     * @param searchCondition 查询条件对象，支持各种筛选条件
     *                       - status: 项目状态筛选
     *                       - type: 项目类型筛选
     *                       - keyword: 关键字搜索
     *                       - enabled: 是否启用状态
     * @return ResponseEntity<JsonResultAo<List<BProjectVo>>> 项目列表数据
     *         - 成功时返回符合条件的所有项目记录
     *         - 无数据时返回空列表
     * @apiNote 该接口不分页，请注意数据量控制，避免返回过多记录影响性能
     */
    @SysLogAnnotion("查询列表, 不分页")
    @PostMapping("/list")
    @ResponseBody
    public ResponseEntity<JsonResultAo<List<BProjectVo>>> list(@RequestBody(required = false) BProjectVo searchCondition) {
        List<BProjectVo> list = ibProjectService.selectPageListNotCount(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(list));
    }

    /**
     * 查询项目管理列表的数据总条数
     * 根据查询条件统计符合条件的记录总数，用于前端显示数据统计信息
     * 
     * @param searchCondition 查询条件对象，与列表查询条件一致
     * @return ResponseEntity<JsonResultAo<BProjectVo>> 包含总条数的结果对象
     *         - count字段包含查询结果的总记录数
     * @throws BusinessException 当查询条件不合法时抛出
     */
    @SysLogAnnotion("查询列表数据条数")
    @PostMapping("/list/count")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BProjectVo>> selectListCount(@RequestBody(required = false) BProjectVo searchCondition) {
        BProjectVo result = ibProjectService.selectListCount(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    /**
     * 按项目管理进行数据汇总统计
     * 对符合条件的项目数据进行汇总计算，包括金额合计、数量合计等统计信息
     * 
     * @param searchCondition 查询条件对象，支持各种筛选条件
     * @return ResponseEntity<JsonResultAo<BProjectVo>> 汇总统计结果
     *         - 包含各项汇总数据：
     *           * 项目总金额
     *           * 项目总数量  
     *           * 税额合计
     *           * 项目数量统计
     * @throws BusinessException 当查询条件不合法时抛出
     */
    @SysLogAnnotion("按项目管理合计")
    @PostMapping("/sum")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BProjectVo>> querySum(@RequestBody(required = false) BProjectVo searchCondition) {
        BProjectVo result = ibProjectService.querySum(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(result));
    }

    /**
     * 新增项目管理记录并启动审批流程
     * 创建新的项目记录，保存基本信息、商品明细、附件等，并自动启动审批流程
     * 
     * @param searchCondition 项目信息对象，包含完整的项目数据
     *                       - 基本信息：项目名称、类型、描述等（必填）
     *                       - 商品明细：商品列表及相关信息
     *                       - 附件信息：相关文档附件
     *                       - 审批信息：审批流程配置
     * @return ResponseEntity<JsonResultAo<BProjectVo>> 新增结果
     *         - 成功时返回完整的项目信息，包括自动生成的ID和编号
     *         - 失败时抛出相应异常
     * @throws InsertErrorException 当数据保存失败时抛出
     * @throws BusinessException 当数据校验失败或业务规则不满足时抛出
     * @apiNote 该接口使用@RepeatSubmitAnnotion注解防止重复提交
     */
    @SysLogAnnotion("项目管理 新增")
    @PostMapping("/insert")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<BProjectVo>> insert(@RequestBody(required = false) BProjectVo searchCondition) {
        InsertResultAo<BProjectVo> resultAo =  ibProjectService.startInsert(searchCondition);
        if (resultAo.isSuccess()) {
            return ResponseEntity.ok().body(ResultUtil.OK(ibProjectService.selectById(searchCondition.getId()),"新增成功"));
        } else {
            throw new InsertErrorException("新增成功，请编辑后重新新增。");
        }
    }

    /**
     * 修改项目管理数据并启动审批流程
     * 更新现有项目的信息，包括基本信息、商品明细、附件等，并启动审批流程
     * 
     * @param searchCondition 项目信息对象，必须包含ID和要更新的数据
     *                       - id: 项目主键ID（必填）
     *                       - 其他字段：需要更新的项目信息
     * @return ResponseEntity<JsonResultAo<BProjectVo>> 更新结果
     *         - 成功时返回更新后的完整项目信息
     *         - 失败时抛出相应异常
     * @throws UpdateErrorException 当数据已被其他用户修改或更新失败时抛出
     * @throws BusinessException 当数据校验失败或业务规则不满足时抛出
     * @apiNote 该接口使用@RepeatSubmitAnnotion注解防止重复提交
     *          支持乐观锁机制，防止并发修改冲突
     */
    @SysLogAnnotion("修改数据")
    @PostMapping("/save")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<BProjectVo>> save(@RequestBody(required = false) BProjectVo searchCondition) {
        if(ibProjectService.startUpdate(searchCondition).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(ibProjectService.selectById(searchCondition.getId()),"更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    /**
     * 项目管理数据校验
     * 对项目数据进行业务规则校验，包括必填项检查、数据格式校验、业务逻辑验证等
     * 
     * @param searchCondition 要校验的项目数据对象
     *                       - check_type: 校验类型（新增校验/更新校验）
     *                       - 其他项目数据字段
     * @return ResponseEntity<JsonResultAo<String>> 校验结果
     *         - 校验通过时返回"OK"
     *         - 校验失败时抛出BusinessException
     * @throws BusinessException 当数据校验不通过时抛出，包含具体的错误信息
     * @apiNote 该接口使用@RepeatSubmitAnnotion注解防止重复提交
     *          校验规则包括：
     *          - 必填字段检查
     *          - 数据格式验证
     *          - 业务逻辑校验（如重复性检查等）
     */
    @SysLogAnnotion("项目管理校验")
    @PostMapping("/validate")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<String>> validate(@RequestBody(required = false) BProjectVo searchCondition) {
        CheckResultAo checkResultAo = ibProjectService.checkLogic(searchCondition, searchCondition.getCheck_type());
        if (!checkResultAo.isSuccess()) {
            throw new BusinessException(checkResultAo.getMessage());
        }else {
            return ResponseEntity.ok().body(ResultUtil.OK("OK"));
        }
    }

    /**
     * 作废项目管理记录
     * 将指定的项目标记为作废状态，支持填写作废原因和上传作废相关附件
     * 
     * @param searchCondition 作废信息对象
     *                       - id: 要作废的项目ID（必填）
     *                       - cancel_reason: 作废原因（必填）
     *                       - cancel_doc_att_files: 作废相关附件
     * @return ResponseEntity<JsonResultAo<BProjectVo>> 作废操作结果
     *         - 成功时返回操作成功信息
     *         - 失败时抛出相应异常
     * @throws UpdateErrorException 当数据已被修改或作废失败时抛出
     * @throws BusinessException 当项目状态不允许作废或其他业务规则不满足时抛出
     * @apiNote 作废后的项目将无法再进行修改操作
     *          作废信息会记录操作人、操作时间等信息
     */
    @SysLogAnnotion("项目管理，作废")
    @PostMapping("/cancel")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BProjectVo>> cancel(@RequestBody(required = false) BProjectVo searchCondition) {        if(ibProjectService.cancel(searchCondition).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(null,"更新成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    /**
     * 批量逻辑删除项目管理记录
     * 将指定的项目记录标记为删除状态，支持批量操作
     * 
     * @param searchCondition 要删除的项目列表
     *                       - 每个项目对象必须包含ID
     * @return ResponseEntity<JsonResultAo<BProjectVo>> 删除操作结果
     *         - 成功时返回删除成功信息
     *         - 失败时抛出相应异常
     * @throws UpdateErrorException 当数据已被修改或删除失败时抛出
     * @throws BusinessException 当项目状态不允许删除或其他业务规则不满足时抛出
     * @apiNote 该操作为逻辑删除，不会物理删除数据
     *          删除后的项目将无法在正常查询中显示
     */
    @SysLogAnnotion("根据查询条件，项目管理逻辑删除")
    @PostMapping("/delete")
    public ResponseEntity<JsonResultAo<BProjectVo>> delete(@RequestBody(required = false) List<BProjectVo> searchCondition) {
        if(ibProjectService.delete(searchCondition).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(null,"删除成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新编辑更新。");
        }
    }

    /**
     * 项目管理完成操作
     * 完成指定的项目，需要校验关联的销售合同状态
     * 
     * @param searchCondition 完成信息对象
     *                       - id: 要完成的项目ID（必填）
     *                       - complete_reason: 完成说明（可选）
     * @return ResponseEntity<JsonResultAo<BProjectVo>> 完成操作结果
     *         - 成功时返回完成成功信息
     *         - 失败时抛出相应异常
     * @throws UpdateErrorException 当项目状态不允许完成或数据已被修改时抛出
     * @throws BusinessException 当存在未完成的销售合同时抛出
     * @apiNote 该接口使用@RepeatSubmitAnnotion注解防止重复提交
     *          只有状态为"执行中"的项目才能执行完成操作
     *          需要校验项目下所有销售合同状态（必须全部完成或作废）
     */
    @SysLogAnnotion("项目管理，完成")
    @PostMapping("/complete")
    @ResponseBody
    @RepeatSubmitAnnotion
    public ResponseEntity<JsonResultAo<BProjectVo>> complete(@RequestBody(required = false) BProjectVo searchCondition) {
        if(ibProjectService.complete(searchCondition).isSuccess()){
            return ResponseEntity.ok().body(ResultUtil.OK(null,"项目完成成功"));
        } else {
            throw new UpdateErrorException("保存的数据已经被修改，请查询后重新操作。");
        }
    }

    /**
     * 获取项目管理打印信息
     * 获取指定项目的打印所需信息，包括报表系统参数配置和打印格式化数据
     * 
     * @param searchCondition 查询条件对象
     *                       - id: 项目ID（必填）
     *                       - 其他打印相关参数
     * @return ResponseEntity<JsonResultAo<BProjectVo>> 打印信息结果
     *         - 包含格式化后的打印数据
     *         - 包含报表系统参数配置
     *         - 包含打印模板相关信息
     * @throws BusinessException 当项目不存在或获取打印信息失败时抛出
     * @apiNote 该接口主要用于报表打印功能
     *          返回的数据已按照打印格式进行了处理
     */
    @SysLogAnnotion("获取报表系统参数，并组装打印参数")
    @PostMapping("/print")
    @ResponseBody
    public ResponseEntity<JsonResultAo<BProjectVo>> print(@RequestBody(required = false) BProjectVo searchCondition) {
        BProjectVo printInfo = ibProjectService.getPrintInfo(searchCondition);
        return ResponseEntity.ok().body(ResultUtil.OK(printInfo));
    }

    /**
     * 导出项目管理数据到Excel（全部导出）
     * 根据查询条件导出符合条件的项目数据，支持多级表头和商品明细展开
     * 导出数据与列表查询结果完全一致，确保数据准确性
     * 
     * @param param 查询条件参数，支持与列表查询相同的筛选条件：
     *              - name: 项目名称模糊查询
     *              - code: 项目编号模糊查询  
     *              - type: 项目类型筛选
     *              - status: 项目状态筛选
     *              - status_list: 项目状态数组筛选
     *              - supplier_id: 供应商ID筛选
     *              - purchaser_id: 采购方ID筛选
     *              - delivery_type: 运输方式筛选
     *              - start_time: 开始时间筛选
     *              - over_time: 结束时间筛选
     * @param response HTTP响应对象，用于设置下载文件的响应头
     * @throws IOException 当文件写入失败时抛出
     * @throws BusinessException 当查询数据失败或数据转换异常时抛出
     * @apiNote 导出功能特点：
     *          1. 使用EasyExcel进行高性能Excel生成
     *          2. 支持多级表头，商品信息使用"商品"作为父级表头
     *          3. 将项目的嵌套商品明细展开为扁平结构，每行包含一个商品明细
     *          4. 支持数字格式化、日期格式化、货币格式化
     *          5. 自动调整列宽以适应内容长度
     *          6. 文件名包含时间戳，避免重复下载冲突
     */
    @PostMapping("/exportall")
    @SysLogAnnotion("全部导出")
    public void exportAll(@RequestBody(required = false) BProjectVo param, HttpServletResponse response) throws IOException {
        log.info("开始执行项目管理全部导出，查询条件：{}", param);
        
        try {
            // 1. 调用Service获取导出数据
            List<BProjectExportVo> exportDataList = ibProjectService.exportAll(param);
            
            // 2. 创建项目合并策略（临时启用调试模式）
            ProjectMergeStrategy mergeStrategy = new ProjectMergeStrategy(true);
            log.info("启用项目合并策略：{}", mergeStrategy.getStrategyInfo());
            
            // 3. 使用带合并策略的EasyExcelUtil进行Excel生成
            String fileName = "项目管理导出_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            String sheetName = "项目管理数据";
            
            EasyExcelUtil<BProjectExportVo> excelUtil = new EasyExcelUtil<>(BProjectExportVo.class);
            excelUtil.exportExcelWithMergeStrategy(fileName, sheetName, exportDataList, response, mergeStrategy);
            
            log.info("项目管理全部导出完成（含单元格合并），文件名：{}，记录数：{}", fileName, exportDataList.size());
            
        } catch (Exception e) {
            log.error("项目管理全部导出失败：{}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 导出项目管理数据到Excel（选中导出）
     * 根据传入的项目VO列表导出指定的项目数据，支持多级表头和商品明细展开
     * 适用于前端选中单条或多条记录的导出需求
     * 
     * @param searchConditionList 要导出的项目VO列表，包含项目ID等信息
     *                           - 每个BProjectVo对象必须包含id字段
     *                           - 支持单条记录导出（list中只有一个VO）
     *                           - 支持多条记录批量导出（list中包含多个VO）
     * @param response HTTP响应对象，用于设置下载文件的响应头
     * @throws IOException 当文件写入失败时抛出
     * @throws BusinessException 当VO列表为空、查询数据失败或数据转换异常时抛出
     * @apiNote 导出功能特点：
     *          1. 使用EasyExcel进行高性能Excel生成
     *          2. 支持多级表头，商品信息使用"商品"作为父级表头
     *          3. 将项目的嵌套商品明细展开为扁平结构，每行包含一个商品明细
     *          4. 支持数字格式化、日期格式化、货币格式化
     *          5. 文件名包含时间戳，避免重复下载冲突
     *          6. 前端使用场景：选中单条记录导出、多条记录批量导出、全选当前页导出
     */
    @PostMapping("/export")
    @SysLogAnnotion("选中导出")
    public void export(@RequestBody(required = false) List<BProjectVo> searchConditionList, HttpServletResponse response) throws IOException {
        log.info("开始执行项目管理选中导出，VO列表：{}", searchConditionList != null ? searchConditionList.size() + "条记录" : "null");
        
        try {
            // 1. 调用Service获取导出数据
            List<BProjectExportVo> exportDataList = ibProjectService.exportByIds(searchConditionList);
            
            // 2. 创建项目合并策略（临时启用调试模式）
            ProjectMergeStrategy mergeStrategy = new ProjectMergeStrategy(true);
            log.info("启用项目合并策略：{}", mergeStrategy.getStrategyInfo());
            
            // 3. 使用带合并策略的EasyExcelUtil进行Excel生成
            String fileName = "项目管理导出_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            String sheetName = "项目管理数据";
            
            EasyExcelUtil<BProjectExportVo> excelUtil = new EasyExcelUtil<>(BProjectExportVo.class);
            excelUtil.exportExcelWithMergeStrategy(fileName, sheetName, exportDataList, response, mergeStrategy);
            
            log.info("项目管理选中导出完成（含单元格合并），文件名：{}，记录数：{}", fileName, exportDataList.size());
            
        } catch (Exception e) {
            log.error("项目管理选中导出失败：{}", e.getMessage(), e);
            throw e;
        }
    }

}
