package com.xinyirun.scm.core.system.service.business.so.socontract;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.business.so.socontract.BSoContractEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.DeleteResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.business.so.socontract.BSoContractVo;
import com.xinyirun.scm.bean.system.vo.business.so.socontract.BSoContractImportVo;
import com.xinyirun.scm.bean.system.vo.business.so.socontract.BSoContractExportVo;
import com.xinyirun.scm.core.system.service.base.v1.common.bpm.IBpmCancelCommonCallBackService;
import com.xinyirun.scm.core.system.service.base.v1.common.bpm.IBpmCommonCallBackService;

import java.io.IOException;
import java.util.List;

/**
 * <p>
 * 销售合同表 服务类
 * </p>
 *
 * @author xinyirun
 * @since 2025-01-22
 */
public interface IBSoContractService extends IService<BSoContractEntity> ,
        IBpmCommonCallBackService<BSoContractVo>,
        IBpmCancelCommonCallBackService<BSoContractVo> {

    /**
     * 销售合同  新增
     */
    InsertResultAo<BSoContractVo> startInsert(BSoContractVo soContractVo);

    /**
     * 分页查询
     */
    IPage<BSoContractVo> selectPage(BSoContractVo searchCondition);

    /**
     * 获取销售合同信息
     */
    BSoContractVo selectById(Integer id);

    /**
     * 更新销售合同信息
     */
    UpdateResultAo<Integer> startUpdate(BSoContractVo soContractVo);

    /**
     * 删除销售合同信息
     */
    DeleteResultAo<Integer> delete(List<BSoContractVo> searchCondition);

    /**
     * 按销售合同合计
     */
    BSoContractVo querySum(BSoContractVo searchCondition);

    /**
     * 销售合同校验
     */
    CheckResultAo checkLogic(BSoContractVo bean, String checkType);



    /**
     * 获取报表系统参数，并组装打印参数
     */
    BSoContractVo getPrintInfo(BSoContractVo searchCondition);

    /**
     * 导出查询
     */
    List<BSoContractVo> selectExportList(BSoContractVo param);

    /**
     * 作废
     */
    UpdateResultAo<Integer> cancel(BSoContractVo searchCondition);

    /**
     * 完成
     */
    UpdateResultAo<Integer> complete(BSoContractVo searchCondition);

    /**
     * 完成校验
     */
    CheckResultAo validateComplete(BSoContractVo contractVo);

    /**
     * 导入数据
     */
    List<BSoContractImportVo> importData(List<BSoContractImportVo> beans);

    /**
     * 获取全部销售合同导出数据
     * 根据查询条件获取符合条件的所有销售合同数据，进行数据转换为扁平化导出格式
     * 包含导出状态管理、导出数量限制检查、数据转换等业务逻辑
     * 
     * @param param 查询条件参数，支持与列表查询相同的筛选条件：
     *              - project_code: 项目编号模糊查询
     *              - contract_code: 合同编号模糊查询  
     *              - type: 合同类型筛选
     *              - status: 合同状态筛选
     *              - status_list: 合同状态数组筛选
     *              - customer_id: 客户ID筛选
     *              - seller_id: 销售方ID筛选
     *              - delivery_type: 运输方式筛选
     *              - start_time: 开始时间筛选
     *              - over_time: 结束时间筛选
     * @return List<BSoContractExportVo> 导出数据列表，已进行扁平化处理
     *         - 将销售合同的嵌套商品明细展开为独立记录
     *         - 每行记录包含合同基础信息 + 单个商品明细信息
     *         - 如果合同无商品明细，则返回合同基础信息记录
     * @throws IOException 当导出状态冲突、数据量超限、查询数据失败或数据转换异常时抛出
     * @apiNote 业务特点：
     *          1. 检查导出状态，防止并发导出冲突
     *          2. 系统配置导出数量限制检查
     *          3. 将嵌套的商品明细JSON展开为扁平结构
     *          4. 支持数字格式化、百分比格式化等数据处理
     *          5. 导出完成后自动恢复导出状态
     */
    List<BSoContractExportVo> exportAll(BSoContractVo param) throws IOException;

    /**
     * 获取选中的销售合同导出数据
     * 根据传入的销售合同VO列表获取指定的合同数据，进行数据转换为扁平化导出格式
     * 包含导出状态管理、导出数量限制检查、数据转换等业务逻辑
     * 
     * @param searchConditionList 要导出的销售合同VO列表，不能为空
     *                           - 每个BSoContractVo对象必须包含id字段
     *                           - 支持单条记录导出（list中只有一个VO）
     *                           - 支持多条记录批量导出（list中包含多个VO）
     * @return List<BSoContractExportVo> 导出数据列表，已进行扁平化处理
     *         - 将销售合同的嵌套商品明细展开为独立记录
     *         - 每行记录包含合同基础信息 + 单个商品明细信息
     *         - 如果合同无商品明细，则返回合同基础信息记录
     * @throws IOException 当导出状态冲突、数据量超限、VO列表为空、查询数据失败或数据转换异常时抛出
     * @apiNote 业务特点：
     *          1. 检查导出状态，防止并发导出冲突
     *          2. 系统配置导出数量限制检查
     *          3. 将嵌套的商品明细JSON展开为扁平结构
     *          4. 支持数字格式化、百分比格式化等数据处理
     *          5. 导出完成后自动恢复导出状态
     *          6. 前端使用场景：选中单条记录导出、多条记录批量导出、全选当前页导出
     */
    List<BSoContractExportVo> exportByIds(List<BSoContractVo> searchConditionList) throws IOException;
}