package com.xinyirun.scm.core.system.service.business.project;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.business.project.BProjectEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.DeleteResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.business.project.BProjectVo;
import com.xinyirun.scm.core.system.service.base.v1.common.bpm.IBpmCommonCallBackService;
import com.xinyirun.scm.core.system.service.base.v1.common.bpm.IBpmCancelCommonCallBackService;

import java.util.List;

/**
 * <p>
 * 项目管理表 服务类接口
 * 定义了项目管理相关的所有业务操作方法，包括基础的CRUD操作、分页查询、
 * 数据统计、业务校验、审批流程集成等功能
 * 继承了MyBatis-Plus的IService接口和BPM审批流程回调接口
 * </p>
 *
 * @author xinyirun
 * @since 2024-12-11
 */
public interface IBProjectService extends IService<BProjectEntity>, 
        IBpmCommonCallBackService<BProjectVo>, 
        IBpmCancelCommonCallBackService<BProjectVo> {

    /**
     * 分页查询项目管理列表
     * 支持多条件筛选、排序、分页等功能，返回分页结果
     * 
     * @param searchCondition 查询条件对象，包含分页参数和筛选条件
     *                       - pageCondition: 分页参数（页码、页大小、排序方式）
     *                       - status: 项目状态筛选
     *                       - type: 项目类型筛选  
     *                       - keyword: 关键字搜索（项目名称、编号等）
     *                       - dateRange: 时间范围筛选
     * @return IPage<BProjectVo> 分页查询结果，包含总记录数和当前页数据
     */
    IPage<BProjectVo> selectPage(BProjectVo searchCondition);

    /**
     * 根据ID查询项目管理详情
     * 获取指定项目的完整信息，包括基本信息、商品明细、附件、审批状态等
     * 
     * @param id 项目主键ID，不能为空
     * @return BProjectVo 项目详情对象，包含：
     *         - 基本信息（项目名称、编号、类型、状态等）
     *         - 商品明细列表（detailListData）
     *         - 附件信息（doc_att_files）
     *         - 审批流程信息
     *         - 作废信息（如果已作废）
     *         如果项目不存在则返回null
     */
    BProjectVo selectById(Integer id);

    /**
     * 查询项目管理列表的数据总条数
     * 根据查询条件统计符合条件的记录总数
     * 
     * @param searchCondition 查询条件对象，与列表查询条件一致
     * @return BProjectVo 包含总条数的结果对象，count字段为查询结果总数
     */
    BProjectVo selectListCount(BProjectVo searchCondition);

    /**
     * 按项目管理进行数据汇总统计
     * 对符合条件的项目数据进行汇总计算，包括金额合计、数量合计等
     * 
     * @param searchCondition 查询条件对象，支持各种筛选条件
     * @return BProjectVo 汇总统计结果对象，包含：
     *         - amount: 项目总金额
     *         - project_total: 项目总数量
     *         - tax_amount_sum: 税额合计
     *         - count: 项目数量统计
     */
    BProjectVo querySum(BProjectVo searchCondition);

    /**
     * 查询项目管理列表（不分页）
     * 根据条件查询所有符合条件的项目记录，主要用于下拉选择、导出等场景
     * 
     * @param searchCondition 查询条件对象，支持各种筛选条件
     *                       - status: 项目状态筛选
     *                       - type: 项目类型筛选
     *                       - keyword: 关键字搜索
     *                       - enabled: 是否启用状态
     * @return List<BProjectVo> 项目列表，无数据时返回空列表
     * @apiNote 该方法不分页，请注意数据量控制，避免返回过多记录影响性能
     */
    List<BProjectVo> selectPageListNotCount(BProjectVo searchCondition);

    /**
     * 新增项目管理记录并启动审批流程
     * 创建新的项目记录，保存基本信息、商品明细、附件等，并自动启动审批流程
     * 
     * @param vo 项目信息对象，包含完整的项目数据
     *          - 基本信息：项目名称、类型、描述等（必填）
     *          - detailListData: 商品明细列表
     *          - doc_att_files: 附件信息列表
     *          - initial_process: 审批流程初始化参数
     * @return InsertResultAo<BProjectVo> 新增操作结果
     *         - 成功时返回完整的项目信息，包括自动生成的ID和编号
     *         - 失败时isSuccess()返回false
     * @apiNote 该方法会：
     *          1. 进行数据校验
     *          2. 自动生成项目编号（如果为空）
     *          3. 保存项目基本信息
     *          4. 保存商品明细
     *          5. 保存附件信息
     *          6. 启动审批流程
     */
    InsertResultAo<BProjectVo> startInsert(BProjectVo vo);

    /**
     * 更新项目管理信息并启动审批流程
     * 更新现有项目的信息，包括基本信息、商品明细、附件等，并启动审批流程
     * 
     * @param poContractVo 项目信息对象，必须包含ID和要更新的数据
     *                    - id: 项目主键ID（必填）
     *                    - 其他字段：需要更新的项目信息
     * @return UpdateResultAo<Integer> 更新操作结果
     *         - 成功时返回更新的记录数
     *         - 失败时isSuccess()返回false
     * @apiNote 该方法支持乐观锁机制，防止并发修改冲突
     *          更新过程包括：
     *          1. 数据校验
     *          2. 更新基本信息
     *          3. 重新计算汇总数据
     *          4. 更新商品明细（全删全增）
     *          5. 更新附件信息
     *          6. 启动审批流程
     */
    UpdateResultAo<Integer> startUpdate(BProjectVo poContractVo);

    /**
     * 项目管理数据校验
     * 对项目数据进行业务规则校验，包括必填项检查、数据格式校验、业务逻辑验证等
     * 
     * @param bean 要校验的项目数据对象
     * @param checkType 校验类型，支持以下值：
     *                 - INSERT_CHECK_TYPE: 新增校验
     *                 - UPDATE_CHECK_TYPE: 更新校验
     * @return CheckResultAo 校验结果对象
     *         - isSuccess(): 校验是否通过
     *         - getMessage(): 校验失败时的错误信息
     * @apiNote 校验规则包括：
     *          - 必填字段检查（项目名称、类型等）
     *          - 数据格式验证（日期、数值等）
     *          - 业务逻辑校验（重复性检查、状态合法性等）
     *          - 商品明细数据校验
     */
    CheckResultAo checkLogic(BProjectVo bean, String checkType);

    /**
     * 批量逻辑删除项目管理记录
     * 将指定的项目记录标记为删除状态，支持批量操作
     * 
     * @param searchCondition 要删除的项目列表
     *                       - 每个项目对象必须包含ID
     * @return DeleteResultAo<Integer> 删除操作结果
     *         - 成功时返回删除的记录数
     *         - 失败时isSuccess()返回false
     * @throws BusinessException 当项目状态不允许删除或其他业务规则不满足时抛出
     * @apiNote 该操作为逻辑删除，不会物理删除数据
     *          删除前会进行业务规则校验
     *          删除后的项目将无法在正常查询中显示
     */
    DeleteResultAo<Integer> delete(List<BProjectVo> searchCondition);

    /**
     * 作废项目管理记录
     * 将指定的项目标记为作废状态，支持填写作废原因和上传作废相关附件
     * 
     * @param vo 作废信息对象
     *          - id: 要作废的项目ID（必填）
     *          - cancel_reason: 作废原因（必填）
     *          - cancel_doc_att_files: 作废相关附件
     * @return UpdateResultAo<Integer> 作废操作结果
     *         - 成功时返回更新的记录数
     *         - 失败时isSuccess()返回false
     * @apiNote 作废后的项目将无法再进行修改操作
     *          作废信息会记录到m_cancel表中，包括操作人、操作时间等
     */
    UpdateResultAo<Integer> cancel(BProjectVo vo);

    /**
     * 获取项目管理打印信息
     * 获取指定项目的打印所需信息，包括报表系统参数配置和打印格式化数据
     * 
     * @param searchCondition 查询条件对象
     *                       - id: 项目ID（必填）
     *                       - 其他打印相关参数
     * @return BProjectVo 打印信息对象，包含：
     *         - 格式化后的打印数据
     *         - 报表系统参数配置
     *         - 打印模板相关信息
     * @apiNote 该方法主要用于报表打印功能
     *          返回的数据已按照打印格式进行了处理
     */
    BProjectVo getPrintInfo(BProjectVo searchCondition);
}
