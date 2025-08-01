package com.xinyirun.scm.core.system.serviceimpl.business.project;


import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.bpm.BpmInstanceSummaryEntity;
import com.xinyirun.scm.bean.entity.business.project.BProjectAttachEntity;
import com.xinyirun.scm.bean.entity.business.project.BProjectEntity;
import com.xinyirun.scm.bean.entity.business.project.BProjectGoodsEntity;
import com.xinyirun.scm.bean.entity.sys.config.config.SConfigEntity;
import com.xinyirun.scm.bean.entity.sys.file.SFileEntity;
import com.xinyirun.scm.bean.entity.sys.file.SFileInfoEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.DeleteResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.DeleteResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.UpdateResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.CheckResultUtil;
import com.xinyirun.scm.bean.system.vo.business.bpm.BBpmProcessVo;
import com.xinyirun.scm.bean.system.vo.business.bpm.OrgUserVo;

import com.xinyirun.scm.bean.system.vo.business.project.BProjectAttachVo;
import com.xinyirun.scm.bean.system.vo.business.project.BProjectVo;
import com.xinyirun.scm.bean.system.vo.business.project.BProjectGoodsVo;
import com.xinyirun.scm.bean.system.vo.master.cancel.MCancelVo;
import com.xinyirun.scm.bean.system.vo.master.user.MStaffVo;
import com.xinyirun.scm.bean.system.vo.sys.file.SFileInfoVo;
import com.xinyirun.scm.bean.system.vo.sys.pages.SPagesVo;
import com.xinyirun.scm.core.system.mapper.master.user.MStaffMapper;
import com.xinyirun.scm.bean.utils.security.SecurityUtil;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.constant.PageCodeConstant;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.core.bpm.service.business.IBpmInstanceSummaryService;
import com.xinyirun.scm.core.bpm.service.business.IBpmProcessTemplatesService;
import com.xinyirun.scm.core.bpm.serviceimpl.business.BpmProcessTemplatesServiceImpl;
import com.xinyirun.scm.core.system.mapper.business.project.BProjectMapper;
import com.xinyirun.scm.core.system.mapper.business.project.BProjectGoodsMapper;
import com.xinyirun.scm.core.system.mapper.business.project.BProjectAttachMapper;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileInfoMapper;
import com.xinyirun.scm.core.system.mapper.sys.file.SFileMapper;
import com.xinyirun.scm.core.system.service.business.project.IBProjectService;
import com.xinyirun.scm.core.system.service.master.cancel.MCancelService;
import com.xinyirun.scm.core.system.service.sys.config.config.ISConfigService;
import com.xinyirun.scm.core.system.service.sys.file.ISFileService;
import com.xinyirun.scm.core.system.service.sys.pages.ISPagesService;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.BProjectAutoCodeServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 项目管理表 服务实现类
 * 
 * 主要功能包括：
 * 1. 项目管理的CRUD操作（增删改查）
 * 2. 项目数据校验和业务规则检查
 * 3. 项目商品明细管理和金额计算
 * 4. 文件附件管理（项目文档、作废附件等）
 * 5. BPM审批流程集成（项目审批、作废审批）
 * 6. 项目状态流转管理
 * 7. 打印报表功能支持
 * 8. 作废管理功能
 * 
 * 业务流程：
 * - 新增项目 → 数据校验 → 保存基础信息 → 保存商品明细 → 保存附件 → 启动审批流程
 * - 修改项目 → 数据校验 → 更新信息 → 重新计算金额 → 更新附件 → 启动审批流程
 * - 作废项目 → 作废校验 → 保存作废信息 → 启动作废审批流程
 * 
 * 集成模块：
 * - BPM工作流引擎：处理审批流程
 * - 文件管理系统：处理附件上传下载
 * - 作废管理模块：处理作废记录
 * - 自动编码模块：生成项目编号
 * - 打印报表系统：生成打印信息
 * </p>
 *
 * @author xinyirun
 * @since 2024-12-11
 */
@Service
@Slf4j
public class BProjectServiceImpl extends ServiceImpl<BProjectMapper, BProjectEntity> implements IBProjectService {

    @Autowired
    private BProjectMapper mapper;

    @Autowired
    private BProjectGoodsMapper bProjectGoodsMapper;

    @Autowired
    private BProjectAutoCodeServiceImpl bProjectAutoCodeService;

    @Autowired
    private SFileMapper fileMapper;

    @Autowired
    private SFileInfoMapper fileInfoMapper;    @Autowired
    IBpmProcessTemplatesService iBpmProcessTemplatesService;

    @Autowired
    IBpmInstanceSummaryService iBpmInstanceSummaryService;

    @Autowired
    private BpmProcessTemplatesServiceImpl bpmProcessTemplatesService;

    @Autowired
    private MCancelService mCancelService;

    @Autowired
    private ISFileService isFileService;    @Autowired
    private MStaffMapper mStaffMapper;    @Autowired
    private BProjectAttachMapper bProjectAttachMapper;

    @Autowired
    private ISConfigService isConfigService;

    @Autowired
    private ISPagesService isPagesService;    /**
     * 分页查询项目管理列表
     * 根据查询条件进行分页查询，支持排序功能
     * 
     * @param searchCondition 查询条件对象，包含分页参数和筛选条件
     *                       - pageCondition: 分页参数（页码、页大小、排序字段等）
     *                       - 其他筛选条件：项目状态、类型、关键字等
     * @return IPage<BProjectVo> 分页查询结果，包含数据列表和分页信息
     * @apiNote 该方法支持动态排序，排序字段通过pageCondition.sort传入
     */
    @Override
    public IPage<BProjectVo> selectPage(BProjectVo searchCondition) {

        Page<BProjectVo> page = new Page<>(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        PageUtil.setSort(page, searchCondition.getPageCondition().getSort());

        return mapper.selectPage(page, searchCondition);
    }    /**
     * 获取项目管理详细信息
     * 根据项目ID查询完整的项目信息，包括基础信息、附件信息、作废信息等
     * 
     * @param id 项目主键ID
     * @return BProjectVo 项目详细信息对象，包含：
     *         - 基础项目信息
     *         - 附件文件列表（doc_att_files）
     *         - 作废信息（如果项目已作废）：作废原因、作废人、作废时间、作废附件
     * @throws BusinessException 当项目不存在时可能抛出异常
     * @apiNote 该方法会根据项目状态自动加载相关的作废信息
     *          状态4和5表示项目已作废，会查询作废记录
     */
    @Override
    public BProjectVo selectById(Integer id) {
        BProjectVo bProjectVo = mapper.selectId(id);

        // 其他附件信息
        List<SFileInfoVo> doc_att_files = isFileService.selectFileInfo(bProjectVo.getDoc_att_file());
        bProjectVo.setDoc_att_files(doc_att_files);

        // 查询是否存在作废记录
        if (DictConstant.DICT_B_PROJECT_STATUS_FOUR.equals(bProjectVo.getStatus()) || Objects.equals(bProjectVo.getStatus(), DictConstant.DICT_B_PROJECT_STATUS_FIVE)) {
            MCancelVo serialIdAndType = new MCancelVo();
            serialIdAndType.setSerial_id(bProjectVo.getId());
            serialIdAndType.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_PROJECT);
            MCancelVo mCancelVo = mCancelService.selectBySerialIdAndType(serialIdAndType);
            // 作废理由
            bProjectVo.setCancel_reason(mCancelVo.getRemark());
            // 作废附件信息
            if (mCancelVo.getFile_id() != null) {
                List<SFileInfoVo> cancel_doc_att_files = isFileService.selectFileInfo(mCancelVo.getFile_id());
                bProjectVo.setCancel_doc_att_files(cancel_doc_att_files);
            }

            // 通过表m_staff获取作废提交人名称
            MStaffVo searchCondition = new MStaffVo();
            searchCondition.setId(mCancelVo.getC_id());
            bProjectVo.setCancel_name(mStaffMapper.selectByid(searchCondition).getName());
            // 作废时间
            bProjectVo.setCancel_time(mCancelVo.getC_time());
        }
        return bProjectVo;
    }    /**
     * 查询项目列表的数据总条数
     * 根据查询条件统计符合条件的项目记录总数
     * 
     * @param searchCondition 查询条件对象
     * @return BProjectVo 包含总条数信息的结果对象
     * @apiNote 该方法目前返回null，需要根据业务需求实现具体逻辑
     */
    @Override
    public BProjectVo selectListCount(BProjectVo searchCondition) {
        return null;
    }

    /**
     * 项目管理数据汇总查询
     * 根据查询条件对项目数据进行汇总统计，包括金额合计、数量合计等
     * 
     * @param searchCondition 查询条件对象，支持各种筛选条件
     * @return BProjectVo 汇总统计结果，包含：
     *         - 项目总金额合计
     *         - 项目总数量合计
     *         - 税额合计
     *         - 其他统计信息
     * @apiNote 该方法调用Mapper层的汇总查询方法进行数据统计
     */
    @Override
    public BProjectVo querySum(BProjectVo searchCondition) {
        return mapper.querySum(searchCondition);
    }

    /**
     * 查询项目列表（不分页）
     * 根据条件查询所有符合条件的项目记录，主要用于下拉选择、导出等场景
     * 
     * @param searchCondition 查询条件对象
     * @return List<BProjectVo> 项目列表数据
     * @apiNote 该方法目前返回空列表，需要根据业务需求实现具体逻辑
     *          注意：不分页查询需要控制数据量，避免性能问题
     */
    @Override
    public List<BProjectVo> selectPageListNotCount(BProjectVo searchCondition) {
        return List.of();
    }    /**
     * 新增项目管理记录
     * 创建新的项目记录，包括基础信息、商品明细、附件信息的保存
     * 
     * 业务流程：
     * 1. 数据校验（必填项、重复性检查等）
     * 2. 保存项目基础信息（自动生成编号、设置初始状态等）
     * 3. 保存项目商品明细（计算金额、税额等）
     * 4. 保存附件信息
     * 
     * @param vo 项目信息对象，包含：
     *          - 基础信息：项目名称、类型、描述等
     *          - 商品明细列表：商品信息、数量、价格、税率等
     *          - 附件信息：相关文档附件
     * @return InsertResultAo<BProjectVo> 新增操作结果
     *         - 成功时返回包含ID的项目信息
     *         - 失败时抛出BusinessException
     * @throws BusinessException 当数据校验失败或保存失败时抛出
     * @apiNote 项目编号支持自动生成和手动指定两种模式
     *          - 编号为空时：自动生成项目编号
     *          - 编号不为空时：使用用户提供的编号（需通过重复性校验）
     */
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<BProjectVo> insert(BProjectVo vo) {        // 1.保存基础信息
        BProjectEntity bProjectEntity = new BProjectEntity();
        BeanUtilsSupport.copyProperties(vo, bProjectEntity);
        bProjectEntity.setStatus(DictConstant.DICT_B_PROJECT_STATUS_ONE);
        
        // 根据项目编号是否为空决定是否自动生成编号
        if (StringUtils.isEmpty(vo.getCode())) {
            // 项目编号为空时，自动生成编号
            bProjectEntity.setCode(bProjectAutoCodeService.autoCode().getCode());
        } else {
            // 项目编号不为空时，直接使用用户提供的编号
            bProjectEntity.setCode(vo.getCode());
        }
        
        /** 未删除 */
        bProjectEntity.setIs_del(Boolean.FALSE);
        /** 审批流程名称 */
        bProjectEntity.setBpm_process_name("新增项目管理审批");

        int rtn = mapper.insert(bProjectEntity);
        if (rtn == 0) {
            throw new BusinessException("新增失败");
        }
        vo.setId(bProjectEntity.getId());

        // 2.商品
        List<BProjectGoodsVo> detailListData = vo.getDetailListData();
        for (BProjectGoodsVo detailListDatum : detailListData) {
            BProjectGoodsEntity bProjectGoodsEntity = new BProjectGoodsEntity();
            BeanUtils.copyProperties(detailListDatum, bProjectGoodsEntity);
            bProjectGoodsEntity.setProject_id(bProjectEntity.getId());

            /** 计算商品总金额、税额 */
            bProjectGoodsEntity.setAmount(
                    detailListDatum.getQty().multiply(detailListDatum.getPrice()).setScale(2, RoundingMode.HALF_UP));
            bProjectGoodsEntity.setTax_amount(
                    detailListDatum.getQty().multiply(detailListDatum.getPrice())
                            .multiply(detailListDatum.getTax_rate().divide(new BigDecimal(100)))
                            .setScale(2, RoundingMode.HALF_UP));

            int detail = bProjectGoodsMapper.insert(bProjectGoodsEntity);
            if (detail == 0){
                throw new BusinessException("新增失败");
            }
        }

        // 5.保存附件信息
        SFileEntity fileEntity = new SFileEntity();
        fileEntity.setSerial_id(bProjectEntity.getId());
        fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_PROJECT);

        BProjectAttachEntity bProjectAttachEntity = insertFile(fileEntity, vo, new BProjectAttachEntity());
        bProjectAttachEntity.setProject_id(vo.getId());
        int insert = bProjectAttachMapper.insert(bProjectAttachEntity);
        if (insert == 0) {
            throw new UpdateErrorException("新增失败");
        }

        return InsertResultUtil.OK(vo);
    }    /**
     * 启动项目审批流程
     * 根据项目信息配置并启动BPM审批流程
     * 
     * 流程配置包括：
     * 1. 获取审批流程模板代码
     * 2. 设置表单数据和序列化信息
     * 3. 配置发起人信息
     * 4. 设置审批人信息
     * 5. 启动流程实例
     * 
     * @param bean 项目信息对象，包含：
     *            - initial_process: 流程初始化标识
     *            - form_data: 表单数据
     *            - process_users: 审批人信息
     *            - 其他项目基础信息
     * @apiNote 只有在initial_process字段不为空时才启动审批流程
     *          发起人信息从当前登录用户的安全上下文中获取
     */
    public void startFlowProcess(BProjectVo bean){
        // 未初始化审批流数据，不启动审批流
        if (StringUtils.isNotEmpty(bean.getInitial_process())) {
            // 启动审批流
            BBpmProcessVo bBpmProcessVo = new BBpmProcessVo();
            // 根据项目类型选择不同的BPM实例类型
            String bpmInstanceType;
            if (bean.getType() != null && bean.getType().equals(1)) {
                // 采购业务
                bpmInstanceType = SystemConstants.BPM_INSTANCE_TYPE.BPM_INSTANCE_B_PO_PROJECT;
            } else if (bean.getType() != null && bean.getType().equals(2)) {
                // 销售业务
                bpmInstanceType = SystemConstants.BPM_INSTANCE_TYPE.BPM_INSTANCE_B_SO_PROJECT;
            } else {
                // 默认使用采购项目类型
                bpmInstanceType = SystemConstants.BPM_INSTANCE_TYPE.BPM_INSTANCE_B_PO_PROJECT;
            }
            
            bBpmProcessVo.setCode(iBpmProcessTemplatesService.getBpmFLowCodeByType(bpmInstanceType));
            bBpmProcessVo.setSerial_type(bpmInstanceType);
            bBpmProcessVo.setForm_data(bean.getForm_data());
            bBpmProcessVo.setForm_json(bean);
            bBpmProcessVo.setForm_class(bean.getClass().getName());
            bBpmProcessVo.setSerial_id(bean.getId());
            bBpmProcessVo.setInitial_process(bean.getInitial_process());
            bBpmProcessVo.setProcess_users(bean.getProcess_users());

            // 组装发起人信息
            OrgUserVo orgUserVo = new OrgUserVo();
            orgUserVo.setId(SecurityUtil.getStaff_id().toString());
            orgUserVo.setName(SecurityUtil.getUserSession().getStaff_info().getName());
            orgUserVo.setCode(SecurityUtil.getUserSession().getStaff_info().getCode());
            orgUserVo.setType("user");
            bBpmProcessVo.setOrgUserVo(orgUserVo);

            // 启动审批流
            bpmProcessTemplatesService.startProcess(bBpmProcessVo);
        }
    }

    /**
     * 更新项目管理信息
     * 更新现有项目的完整信息，包括基础信息、商品明细、附件等
     * 
     * 业务流程：
     * 1. 数据校验（更新权限、重复性检查等）
     * 2. 更新项目基础信息
     * 3. 重新计算项目金额汇总数据
     * 4. 全删全增更新商品明细
     * 5. 更新附件信息
     * 
     * @param vo 项目信息对象，必须包含ID和要更新的数据
     * @return UpdateResultAo<Integer> 更新操作结果
     *         - 成功时返回更新记录数
     *         - 失败时抛出BusinessException
     * @throws BusinessException 当数据校验失败、数据已被修改或更新失败时抛出
     * @apiNote 该方法使用乐观锁机制防止并发更新冲突
     *          商品明细采用全删全增模式，确保数据一致性
     *          金额计算支持空值处理，避免空指针异常
     */
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> update(BProjectVo vo) {

        BProjectEntity bProjectEntity = (BProjectEntity) BeanUtilsSupport.copyProperties(vo, BProjectEntity.class);
        bProjectEntity.setStatus(DictConstant.DICT_B_PROJECT_STATUS_ONE);

        /**
         * 重新计算：项目总金额、总数量、总税额
         * 循环vo.getGoods_list()，
         * 计算项目总金额：sum(明细.qty * 明细.price)
         * 计算总数量：sum(明细.qty)
         * 计算总税额：sum(明细.qty * 明细.price * 明细.tax_rate/100)
         */
        List<BProjectGoodsVo> goodsList = vo.getGoods_list();
        if (goodsList != null) {
            calculateProjectAmounts(goodsList, bProjectEntity);
        }

        int updCount = mapper.updateById(bProjectEntity);
        if(updCount == 0){
            throw new BusinessException("您提交的数据已经被修改，请查询后重新编辑更新。");
        }

        /** 审批流程名称 */
        bProjectEntity.setBpm_process_name("更新项目管理审批");
        
        // 2.保存项目商品明细表 全删全增
        if (goodsList != null && !goodsList.isEmpty()) {
            // 删除原有商品明细
            bProjectGoodsMapper.deleteByProjectId(bProjectEntity.getId().toString());
            for (BProjectGoodsVo goodsVo : goodsList) {
                BProjectGoodsEntity bProjectGoodsEntity = new BProjectGoodsEntity();
                org.springframework.beans.BeanUtils.copyProperties(goodsVo, bProjectGoodsEntity);
                bProjectGoodsEntity.setProject_id(bProjectEntity.getId());

                /** 计算商品总金额、税额 */
                if (goodsVo.getQty() != null && goodsVo.getPrice() != null) {
                    bProjectGoodsEntity.setAmount(
                            goodsVo.getQty().multiply(goodsVo.getPrice()).setScale(2, RoundingMode.HALF_UP));
                }
                if (goodsVo.getQty() != null && goodsVo.getPrice() != null && goodsVo.getTax_rate() != null) {
                    bProjectGoodsEntity.setTax_amount(
                            goodsVo.getQty().multiply(goodsVo.getPrice())
                                    .multiply(goodsVo.getTax_rate().divide(new BigDecimal(100)))
                                    .setScale(2, RoundingMode.HALF_UP));
                }

                int insertResult = bProjectGoodsMapper.insert(bProjectGoodsEntity);
                if (insertResult == 0) {
                    throw new BusinessException("新增项目商品明细失败");
                }
            }
        }

        // 3.保存附件
        BProjectAttachVo bProjectAttachVo = bProjectAttachMapper.selectByProjectId(vo.getId());
        if (bProjectAttachVo != null) {
            SFileEntity fileEntity = new SFileEntity();
            fileEntity.setSerial_id(vo.getId());
            fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_PROJECT);
            BProjectAttachEntity entity =(BProjectAttachEntity) BeanUtilsSupport.copyProperties(bProjectAttachVo, BProjectAttachEntity.class);
            insertFile(fileEntity, vo, entity);
            entity.setProject_id(vo.getId());
            int insert = bProjectAttachMapper.updateById(entity);
            if (insert == 0) {
                throw new UpdateErrorException("新增附件信息失败");
            }
        }

        return UpdateResultUtil.OK(updCount);
    }

    // =================== BPM 审批流程回调方法 ===================
    /**
     * BPM审批流程创建成功回调
     * 当项目的BPM审批流程实例创建成功后，系统自动回调此方法
     * 
     * 主要功能：
     * 1. 生成流程摘要信息，包含项目关键信息（名称、类型、金额等）
     * 2. 保存流程摘要到bpm_instance_summary表，便于审批列表显示
     * 3. 记录流程业务名称，用于流程追踪和管理
     * 
     * @param searchCondition 包含BPM实例信息的项目对象
     *                       - bpm_instance_code: BPM流程实例编码
     *                       - id: 项目主键ID
     * @return UpdateResultAo<Integer> 更新操作结果，固定返回0（表示摘要记录创建成功）
     * @throws Exception 当数据库操作失败时抛出异常（由@Transactional处理回滚）
     * @apiNote 该方法由BPM引擎自动调用，不应该手动调用
     *          摘要信息以JSON格式存储，便于前端解析和显示
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackCreateBpm(BProjectVo searchCondition) {
        log.debug("====》项目管理[{}]审批流程创建成功，更新开始《====", searchCondition.getId());
        
        BProjectVo bProjectVo = selectById(searchCondition.getId());

        /**
         * 1、更新bpm_instance的摘要数据:
         * bpm_instance_summary:{}  // 项目名称、项目类型等信息
         */
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("项目名称:", bProjectVo.getName());
        jsonObject.put("项目类型:", bProjectVo.getType_name());
        if (bProjectVo.getAmount() != null) {
            jsonObject.put("项目金额:", bProjectVo.getAmount());
        }

        String json = jsonObject.toString();
        BpmInstanceSummaryEntity bpmInstanceSummaryEntity = new BpmInstanceSummaryEntity();
        bpmInstanceSummaryEntity.setProcessCode(searchCondition.getBpm_instance_code());
        bpmInstanceSummaryEntity.setSummary(json);
        bpmInstanceSummaryEntity.setProcess_definition_business_name(bProjectVo.getBpm_process_name());
        iBpmInstanceSummaryService.save(bpmInstanceSummaryEntity);

        log.debug("====》项目管理[{}]审批流程创建成功，更新结束《====", searchCondition.getId());
        return UpdateResultUtil.OK(0);
    }    /**
     * BPM审批流程通过回调
     * 当项目的审批流程通过时，系统自动回调此方法更新项目状态
     * 
     * 主要功能：
     * 1. 更新项目状态为"已通过"（状态码为2）
     * 2. 保存BPM流程实例ID和编码，用于流程追踪
     * 3. 设置下一步审批人为"已完成"状态
     * 4. 记录审批操作日志
     * 
     * @param searchCondition 包含BPM实例信息的项目对象
     *                       - id: 项目主键ID
     *                       - bpm_instance_id: BPM流程实例ID
     *                       - bpm_instance_code: BPM流程实例编码
     * @return UpdateResultAo<Integer> 更新操作结果，返回影响的记录数
     * @throws RuntimeException 当更新项目状态失败时抛出
     * @apiNote 该方法由BPM引擎自动调用，状态流转：待审批(1) → 已通过(2)
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackApprove(BProjectVo searchCondition) {
        log.debug("====》项目管理[{}]审批流程通过，更新开始《====", searchCondition.getId());

        BProjectEntity bProjectEntity = mapper.selectById(searchCondition.getId());

        bProjectEntity.setBpm_instance_id(searchCondition.getBpm_instance_id());
        bProjectEntity.setBpm_instance_code(searchCondition.getBpm_instance_code());


        bProjectEntity.setStatus(DictConstant.DICT_B_PROJECT_STATUS_TWO);  // 使用状态2表示已通过
        bProjectEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_COMPLETE);

        int i = mapper.updateById(bProjectEntity);
        if (i == 0) {
            throw new RuntimeException("更新项目审核状态失败");
        }

        log.debug("====》项目管理[{}]审批流程通过，更新结束《====", searchCondition.getId());
        return UpdateResultUtil.OK(i);
    }

    /**
     * BPM审批流程拒绝回调
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackRefuse(BProjectVo searchCondition) {
        log.debug("====》项目管理[{}]审批流程拒绝，更新开始《====", searchCondition.getId());

        BProjectEntity bProjectEntity = mapper.selectById(searchCondition.getId());
        bProjectEntity.setStatus(DictConstant.DICT_B_PROJECT_STATUS_THREE);  // 使用状态3表示已拒绝
        bProjectEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_REFUSE);

        int i = mapper.updateById(bProjectEntity);
        if (i == 0) {
            throw new RuntimeException("更新项目审核状态失败");
        }

        log.debug("====》项目管理[{}]审批流程拒绝，更新结束《====", searchCondition.getId());
        return UpdateResultUtil.OK(i);
    }

    /**
     * BPM审批流程撤销回调
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackCancel(BProjectVo searchCondition) {
        log.debug("====》项目管理[{}]审批流程撤销，更新开始《====", searchCondition.getId());

        BProjectEntity bProjectEntity = mapper.selectById(searchCondition.getId());
        bProjectEntity.setStatus(DictConstant.DICT_B_PROJECT_STATUS_ZERO);  // 使用状态0表示草稿/撤销
        bProjectEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_CANCEL);

        int i = mapper.updateById(bProjectEntity);
        if (i == 0) {
            throw new RuntimeException("更新项目审核状态失败");
        }

        log.debug("====》项目管理[{}]审批流程撤销，更新结束《====", searchCondition.getId());
        return UpdateResultUtil.OK(i);
    }

    /**
     * BPM审批流程保存最新审批人回调
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCallBackSave(BProjectVo searchCondition) {
        log.debug("====》项目管理[{}]审批流程更新最新审批人，更新开始《====", searchCondition.getId());
        BProjectEntity bProjectEntity = mapper.selectById(searchCondition.getId());
        bProjectEntity.setBpm_instance_id(searchCondition.getBpm_instance_id());
        bProjectEntity.setBpm_instance_code(searchCondition.getBpm_instance_code());
        bProjectEntity.setNext_approve_name(searchCondition.getNext_approve_name());
        
        int i = mapper.updateById(bProjectEntity);

        log.debug("====》项目管理[{}]审批流程更新最新审批人，更新结束《====", searchCondition.getId());
        return UpdateResultUtil.OK(i);
    }

    /**
     * 项目管理校验
     *
     * @param bean
     * @param checkType
     */
    @Override
    public CheckResultAo checkLogic(BProjectVo bean, String checkType) {
        BProjectEntity bProjectEntity = null;
        
        switch (checkType) {            case CheckResultAo.INSERT_CHECK_TYPE:
                // 新增校验逻辑
                if (StringUtils.isEmpty(bean.getName())) {
                    return CheckResultUtil.NG("项目名称不能为空");
                }
                
                // 校验项目编号是否重复
                if (!StringUtils.isEmpty(bean.getCode())) {
                    List<BProjectEntity> duplicateCodeList = mapper.validateDuplicateProjectCode(null, bean.getCode());
                    if (!duplicateCodeList.isEmpty()) {
                        return CheckResultUtil.NG("项目编号[" + bean.getCode() + "]已存在，请使用其他编号");
                    }
                }
                
                // 校验项目名称是否重复
                List<BProjectEntity> duplicateNameList = mapper.validateDuplicateProjectName(null, bean.getName());
                if (!duplicateNameList.isEmpty()) {
                    return CheckResultUtil.NG("项目名称[" + bean.getName() + "]已存在，请使用其他名称");
                }
                break;            case CheckResultAo.UPDATE_CHECK_TYPE:
                // 更新校验逻辑
                if (bean.getId() == null) {
                    return CheckResultUtil.NG("项目ID不能为空");
                }
                
                bProjectEntity = mapper.selectById(bean.getId());
                if (bProjectEntity == null) {
                    return CheckResultUtil.NG("项目不存在");
                }
                
                if (StringUtils.isEmpty(bean.getName())) {
                    return CheckResultUtil.NG("项目名称不能为空");
                }
                
                // 校验项目编号是否重复
                if (!StringUtils.isEmpty(bean.getCode())) {
                    List<BProjectEntity> updateDuplicateCodeList = mapper.validateDuplicateProjectCode(bean.getId(), bean.getCode());
                    if (!updateDuplicateCodeList.isEmpty()) {
                        return CheckResultUtil.NG("项目编号[" + bean.getCode() + "]已存在，请使用其他编号");
                    }
                }
                
                // 校验项目名称是否重复
                List<BProjectEntity> updateDuplicateNameList = mapper.validateDuplicateProjectName(bean.getId(), bean.getName());
                if (!updateDuplicateNameList.isEmpty()) {
                    return CheckResultUtil.NG("项目名称[" + bean.getName() + "]已存在，请使用其他名称");
                }
                break;
                
            case CheckResultAo.DELETE_CHECK_TYPE:
                // 删除校验逻辑
                if (bean.getId() == null) {
                    return CheckResultUtil.NG("项目ID不能为空");
                }
                
                bProjectEntity = mapper.selectById(bean.getId());
                if (bProjectEntity == null) {
                    return CheckResultUtil.NG("项目不存在");
                }
                  // 检查项目状态，如果已审批通过则不能删除
                if (DictConstant.DICT_B_PROJECT_STATUS_TWO.equals(bProjectEntity.getStatus())) {
                    return CheckResultUtil.NG("已审批通过的项目不能删除");
                }
                break;
                
            case CheckResultAo.AUDIT_CHECK_TYPE:
                // 审核校验逻辑
                if (bean.getId() == null) {
                    return CheckResultUtil.NG("项目ID不能为空");
                }
                
                bProjectEntity = mapper.selectById(bean.getId());
                if (bProjectEntity == null) {
                    return CheckResultUtil.NG("项目不存在");
                }
                  // 检查项目状态，只有待审批状态的项目才能审核
                if (!DictConstant.DICT_B_PROJECT_STATUS_ONE.equals(bProjectEntity.getStatus())) {
                    return CheckResultUtil.NG("只有待审批状态的项目才能审核");
                }
                break;
                
            case CheckResultAo.CANCEL_CHECK_TYPE:
                // 作废校验逻辑
                if (bean.getId() == null) {
                    return CheckResultUtil.NG("项目ID不能为空");
                }
                
                bProjectEntity = mapper.selectById(bean.getId());
                if (bProjectEntity == null) {
                    return CheckResultUtil.NG("项目不存在");
                }
                
                // 检查项目状态，只有未删除且不是已作废的项目才能作废
                if (DictConstant.DICT_B_PROJECT_STATUS_FOUR.equals(bProjectEntity.getStatus()) || DictConstant.DICT_B_PROJECT_STATUS_FIVE.equals(bProjectEntity.getStatus())) {
                    return CheckResultUtil.NG("已作废的项目不能重复作废");
                }
                break;
                
            default:
                return CheckResultUtil.NG("未知的校验类型：" + checkType);
        }
          return CheckResultUtil.OK();
    }

    /**
     * 批量逻辑删除项目管理记录
     * 
     * @param searchCondition 要删除的项目列表
     * @return DeleteResultAo<Integer> 删除操作结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public DeleteResultAo<Integer> delete(List<BProjectVo> searchCondition) {
        log.debug("====》批量删除项目管理，开始删除[{}]条记录《====", searchCondition.size());
        
        for (BProjectVo bProjectVo : searchCondition) {
            
            // 删除前校验
            CheckResultAo cr = checkLogic(bProjectVo, CheckResultAo.DELETE_CHECK_TYPE);
            if (!cr.isSuccess()) {
                throw new BusinessException(cr.getMessage());
            }

            // 逻辑删除
            BProjectEntity bProjectEntity = baseMapper.selectById(bProjectVo.getId());
            if (bProjectEntity == null) {
                throw new UpdateErrorException("您提交的数据不存在，请查询后重新操作。");
            }
            
            bProjectEntity.setIs_del(Boolean.TRUE);

            int delCount = baseMapper.updateById(bProjectEntity);
            if(delCount == 0){
                throw new UpdateErrorException("您提交的数据不存在，请查询后重新操作。");
            }
            
            log.debug("====》项目管理[{}]删除成功《====", bProjectVo.getId());
        }
        
        log.debug("====》批量删除项目管理，删除完成《====");
        return DeleteResultUtil.OK(1);
    }

    /**
     * 作废项目管理
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> cancel(BProjectVo vo) {
        log.debug("====》项目管理[{}]作废，开始《====", vo.getId());

        // 1.校验项目是否可以作废
        CheckResultAo checkResult = checkLogic(vo, CheckResultAo.CANCEL_CHECK_TYPE);
        if (!checkResult.isSuccess()) {
            throw new BusinessException(checkResult.getMessage());
        }

        // 2.更新项目状态为作废待审批
        BProjectEntity bProjectEntity = mapper.selectById(vo.getId());
        bProjectEntity.setStatus(DictConstant.DICT_B_PROJECT_STATUS_FOUR); // 状态4：作废待审批
        bProjectEntity.setBpm_process_name("项目管理作废审批");

        int updateResult = mapper.updateById(bProjectEntity);
        if (updateResult == 0) {
            throw new RuntimeException("更新项目状态失败");
        }

        // 3.处理作废附件信息
        SFileEntity fileEntity = new SFileEntity();
        fileEntity.setSerial_id(vo.getId());
        fileEntity.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_PROJECT);

        if (vo.getCancel_doc_att_files() != null && vo.getCancel_doc_att_files().size() > 0) {
            // 保存作废附件
            fileMapper.insert(fileEntity);
            for (SFileInfoVo cancel_file : vo.getCancel_doc_att_files()) {
                SFileInfoEntity fileInfoEntity = new SFileInfoEntity();
                cancel_file.setF_id(fileEntity.getId());
                BeanUtilsSupport.copyProperties(cancel_file, fileInfoEntity);
                fileInfoEntity.setFile_name(cancel_file.getFileName());
                fileInfoEntity.setId(null);
                fileInfoMapper.insert(fileInfoEntity);
            }
        }

        // 4.保存作废记录到m_cancel表
        MCancelVo mCancelVo = new MCancelVo();
        mCancelVo.setSerial_id(vo.getId());
        mCancelVo.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_PROJECT);
        mCancelVo.setRemark(vo.getCancel_reason());
        if (fileEntity.getId() != null) {
            mCancelVo.setFile_id(fileEntity.getId());
        }
        mCancelService.insert(mCancelVo);

        // 5.启动作废审批流程
        startCancelFlowProcess(vo);

        log.debug("====》项目管理[{}]作废，结束《====", vo.getId());
        return UpdateResultUtil.OK(updateResult);
    }    /**
     * 启动作废审批流程
     */
    private void startCancelFlowProcess(BProjectVo bean) {
        // 启动作废审批流
        BBpmProcessVo bBpmProcessVo = new BBpmProcessVo();
        // 根据项目类型选择不同的BPM作废实例类型
        String bpmCancelInstanceType;
        if (bean.getType() != null && bean.getType().equals(1)) {
            // 采购业务
            bpmCancelInstanceType = SystemConstants.BPM_INSTANCE_TYPE.BPM_INSTANCE_B_PO_PROJECT_CANCEL;
        } else if (bean.getType() != null && bean.getType().equals(2)) {
            // 销售业务
            bpmCancelInstanceType = SystemConstants.BPM_INSTANCE_TYPE.BPM_INSTANCE_B_SO_PROJECT_CANCEL;
        } else {
            // 默认使用采购项目作废类型
            bpmCancelInstanceType = SystemConstants.BPM_INSTANCE_TYPE.BPM_INSTANCE_B_PO_PROJECT_CANCEL;
        }
        
        bBpmProcessVo.setCode(iBpmProcessTemplatesService.getBpmFLowCodeByType(bpmCancelInstanceType));
        bBpmProcessVo.setSerial_type(bpmCancelInstanceType);
        bBpmProcessVo.setForm_data(bean.getForm_data());
        bBpmProcessVo.setForm_json(bean);
        bBpmProcessVo.setForm_class(bean.getClass().getName());
        bBpmProcessVo.setSerial_id(bean.getId());
        bBpmProcessVo.setInitial_process(bean.getInitial_process());
        bBpmProcessVo.setProcess_users(bean.getProcess_users());
        bBpmProcessVo.setRemark("项目管理作废审批");

        OrgUserVo orgUserVo = new OrgUserVo();
        orgUserVo.setId(SecurityUtil.getStaff_id().toString());
        orgUserVo.setName(SecurityUtil.getUserSession().getStaff_info().getName());
        orgUserVo.setCode(SecurityUtil.getUserSession().getStaff_info().getCode());
        orgUserVo.setType("user");
        bBpmProcessVo.setOrgUserVo(orgUserVo);

        // 启动审批流
        iBpmProcessTemplatesService.startProcess(bBpmProcessVo);
    }

    // =================== BPM 作废审批流程回调方法 ===================

    /**
     * 作废审批流程创建时
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackCreateBpm(BProjectVo searchCondition) {
        log.debug("====》项目管理[{}]作废审批流程创建成功，更新开始《====", searchCondition.getId());
        BProjectVo bProjectVo = selectById(searchCondition.getId());

        /**
         * 1、更新bpm_instance的摘要数据:
         * bpm_instance_summary:{}  // 作废理由
         */
        JSONObject jsonObject = new JSONObject();
        if (bProjectVo.getCancel_reason() != null) {
            jsonObject.put("作废理由:", bProjectVo.getCancel_reason());
        }
        jsonObject.put("项目名称:", bProjectVo.getName());
        if (bProjectVo.getType_name() != null) {
            jsonObject.put("项目类型:", bProjectVo.getType_name());
        }

        String json = jsonObject.toString();
        BpmInstanceSummaryEntity bpmInstanceSummaryEntity = new BpmInstanceSummaryEntity();
        bpmInstanceSummaryEntity.setProcessCode(searchCondition.getBpm_instance_code());
        bpmInstanceSummaryEntity.setSummary(json);
        bpmInstanceSummaryEntity.setProcess_definition_business_name(bProjectVo.getBpm_cancel_process_name());
        iBpmInstanceSummaryService.save(bpmInstanceSummaryEntity);

        log.debug("====》项目管理[{}]作废审批流程创建成功，更新结束《====", searchCondition.getId());
        return UpdateResultUtil.OK(0);
    }

    /**
     * 作废审批流程通过 更新审核状态已作废
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackApprove(BProjectVo searchCondition) {
        log.debug("====》项目管理[{}]作废审批流程通过，更新开始《====", searchCondition.getId());
        
        BProjectEntity bProjectEntity = mapper.selectById(searchCondition.getId());

        bProjectEntity.setBpm_cancel_instance_id(searchCondition.getBpm_instance_id());
        bProjectEntity.setBpm_cancel_instance_code(searchCondition.getBpm_instance_code());
        bProjectEntity.setStatus(DictConstant.DICT_B_PROJECT_STATUS_FIVE);  // 使用状态5表示已作废
        bProjectEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_COMPLETE);
        bProjectEntity.setBpm_instance_id(searchCondition.getBpm_instance_id());
        bProjectEntity.setBpm_instance_code(searchCondition.getBpm_instance_code());

        int i = mapper.updateById(bProjectEntity);
        if (i == 0) {
            throw new RuntimeException("更新项目审核状态失败");
        }

        log.debug("====》项目管理[{}]作废审批流程通过，更新结束《====", searchCondition.getId());
        return UpdateResultUtil.OK(i);
    }

    /**
     * 作废审批流程拒绝 更新审核状态通过
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackRefuse(BProjectVo searchCondition) {
        log.debug("====》项目管理[{}]作废审批流程拒绝，更新开始《====", searchCondition.getId());
        
        BProjectEntity bProjectEntity = mapper.selectById(searchCondition.getId());
        bProjectEntity.setStatus(DictConstant.DICT_B_PROJECT_STATUS_TWO);  // 恢复为通过状态
        bProjectEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_COMPLETE);
          int i = mapper.updateById(bProjectEntity);
        if (i == 0) {
            throw new RuntimeException("更新项目审核状态失败");
        }

        // 删除对应作废理由
        MCancelVo mCancelVo = new MCancelVo();
        mCancelVo.setSerial_id(bProjectEntity.getId());
        mCancelVo.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_PROJECT);
        mCancelService.delete(mCancelVo);

        log.debug("====》项目管理[{}]作废审批流程拒绝，更新结束《====", searchCondition.getId());
        return UpdateResultUtil.OK(i);
    }

    /**
     * 作废审批流程撤销 更新审核状态通过
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackCancel(BProjectVo searchCondition) {
        log.debug("====》项目管理[{}]作废审批流程撤销，更新开始《====", searchCondition.getId());
        
        BProjectEntity bProjectEntity = mapper.selectById(searchCondition.getId());
        bProjectEntity.setStatus(DictConstant.DICT_B_PROJECT_STATUS_TWO);  // 恢复为通过状态
        bProjectEntity.setNext_approve_name(DictConstant.DICT_SYS_CODE_BPM_INSTANCE_STATUS_COMPLETE);
          int i = mapper.updateById(bProjectEntity);
        if (i == 0) {
            throw new RuntimeException("更新项目审核状态失败");
        }

        // 删除对应作废理由
        MCancelVo mCancelVo = new MCancelVo();
        mCancelVo.setSerial_id(bProjectEntity.getId());
        mCancelVo.setSerial_type(DictConstant.DICT_SYS_CODE_TYPE_B_PROJECT);
        mCancelService.delete(mCancelVo);

        log.debug("====》项目管理[{}]作废审批流程撤销，更新结束《====", searchCondition.getId());
        return UpdateResultUtil.OK(i);
    }

    /**
     * 作废 更新最新审批人
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> bpmCancelCallBackSave(BProjectVo searchCondition) {
        log.debug("====》项目管理[{}]作废审批流程更新最新审批人，更新开始《====", searchCondition.getId());

        BProjectEntity bProjectEntity = mapper.selectById(searchCondition.getId());
        bProjectEntity.setBpm_cancel_instance_id(searchCondition.getBpm_instance_id());
        bProjectEntity.setBpm_cancel_instance_code(searchCondition.getBpm_instance_code());
        bProjectEntity.setNext_approve_name(searchCondition.getNext_approve_name());
        
        int i = mapper.updateById(bProjectEntity);

        log.debug("====》项目管理[{}]作废审批流程更新最新审批人，更新结束《====", searchCondition.getId());
        return UpdateResultUtil.OK(i);
    }

    /**
     * 新增数据并启动工作流
     * @param vo
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<BProjectVo> startInsert(BProjectVo vo) {
        // 1. 校验业务规则
        CheckResultAo cr = checkLogic(vo, CheckResultAo.INSERT_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }
        
        // 2.保存项目
        InsertResultAo<BProjectVo> insertResultAo = insert(vo);

        // 3.设置项目ID到vo中以便启动流程
        vo.setId(insertResultAo.getData().getId());

        // 4.启动审批流程
        startFlowProcess(vo);

        return insertResultAo;
    }

    /**
     * 项目管理  新增
     *
     * @param bProjectVo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<Integer> startUpdate(BProjectVo bProjectVo) {
        // 1. 校验业务规则
        CheckResultAo cr = checkLogic(bProjectVo, CheckResultAo.UPDATE_CHECK_TYPE);
        if (!cr.isSuccess()) {
            throw new BusinessException(cr.getMessage());
        }
        
        // 2.保存项目管理
        UpdateResultAo<Integer> insertResultAo = update(bProjectVo);

        // 3.启动审批流程
        startFlowProcess(bProjectVo);

        return insertResultAo;
    }

    /**
     * 计算项目商品总金额、总数量、总税额
     *
     * @param goodsList 商品明细列表
     * @param bProjectEntity 项目实体
     */
    private void calculateProjectAmounts(List<BProjectGoodsVo> goodsList, BProjectEntity bProjectEntity) {
        BigDecimal projectAmountSum = BigDecimal.ZERO;
        BigDecimal projectTotal = BigDecimal.ZERO;
        BigDecimal taxAmountSum = BigDecimal.ZERO;
        
        if (goodsList != null && !goodsList.isEmpty()) {
            for (BProjectGoodsVo goods : goodsList) {
                BigDecimal qty = goods.getQty() != null ? goods.getQty() : BigDecimal.ZERO;
                BigDecimal price = goods.getPrice() != null ? goods.getPrice() : BigDecimal.ZERO;
                BigDecimal taxRate = goods.getTax_rate() != null ? goods.getTax_rate() : BigDecimal.ZERO;
                
                // 计算项目总金额：sum(明细.qty * 明细.price)
                BigDecimal amount = qty.multiply(price);
                projectAmountSum = projectAmountSum.add(amount);
                
                // 计算总数量：sum(明细.qty)
                projectTotal = projectTotal.add(qty);
                
                // 计算总税额：sum(明细.qty * 明细.price * 明细.tax_rate/100)
                BigDecimal taxAmount = qty.multiply(price).multiply(taxRate).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                taxAmountSum = taxAmountSum.add(taxAmount);
            }
        }
        
        // 设置计算结果到项目实体对象
        bProjectEntity.setAmount(projectAmountSum);
        // 注意：这里假设项目实体有相应的字段，如果没有可以注释掉
        // bProjectEntity.setProject_total(projectTotal);
        // bProjectEntity.setTax_amount_sum(taxAmountSum);
    }

    /**
     * 附件逻辑 全删全增
     */
    public BProjectAttachEntity insertFile(SFileEntity fileEntity, BProjectVo vo, BProjectAttachEntity extra) {
        // 其他附件新增
        if (vo.getDoc_att_files() != null && vo.getDoc_att_files().size() > 0) {
            // 主表新增
            fileMapper.insert(fileEntity);
            // 详情表新增
            for (SFileInfoVo other_file : vo.getDoc_att_files()) {
                SFileInfoEntity fileInfoEntity = new SFileInfoEntity();
                other_file.setF_id(fileEntity.getId());
                BeanUtilsSupport.copyProperties(other_file, fileInfoEntity);
                fileInfoEntity.setFile_name(other_file.getFileName());
                fileInfoEntity.setId(null);
                fileInfoMapper.insert(fileInfoEntity);
            }
            // 其他附件id
            extra.setOne_file(fileEntity.getId());
            fileEntity.setId(null);
        }else {
            extra.setOne_file(null);        }
        return extra;
    }

    /**
     * 获取报表系统参数，并组装打印参数
     *
     * @param searchCondition
     */
    @Override
    public BProjectVo getPrintInfo(BProjectVo searchCondition) {
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
        param.setCode(PageCodeConstant.PAGE_B_PROJECT);
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

        return searchCondition;
    }
}
