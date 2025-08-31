package com.xinyirun.scm.core.system.serviceimpl.master.org;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.entity.master.org.MCompanyEntity;
import com.xinyirun.scm.bean.system.result.utils.v1.CheckResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.UpdateResultUtil;
import com.xinyirun.scm.bean.system.vo.master.org.MCompanyVo;
import com.xinyirun.scm.bean.system.vo.master.org.MCompanyExportVo;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.core.system.mapper.master.org.MCompanyMapper;
import com.xinyirun.scm.core.system.mapper.master.org.MOrgMapper;
import com.xinyirun.scm.core.system.service.master.enterprise.IMEnterpriseService;
import com.xinyirun.scm.core.system.service.master.org.IMCompanyService;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.MCompanyAutoCodeServiceImpl;
import com.xinyirun.scm.core.system.serviceimpl.base.v1.BaseServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 公司主表 服务实现类
 * </p>
 *
 * @author zxh
 * @since 2019-08-23
 */
@Service
public class MCompanyServiceImpl extends BaseServiceImpl<MCompanyMapper, MCompanyEntity> implements IMCompanyService {

    @Autowired
    private MCompanyMapper mapper;

    @Autowired
    private MCompanyAutoCodeServiceImpl autoCode;

    @Autowired
    private IMEnterpriseService enterpriseService;

    @Autowired
    private MOrgMapper orgMapper;

    /**
     * 获取列表，页面查询
     *
     * @param searchCondition
     * @return
     */
    @Override
    public IPage<MCompanyVo> selectPage(MCompanyVo searchCondition) {
        // 分页条件
        Page<MCompanyVo> pageCondition =
            new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());
        return mapper.selectPage(pageCondition, searchCondition);
    }

    /**
     * 获取列表，查询所有数据
     *
     * @param searchCondition
     * @return
     */
    @Override
    public List<MCompanyVo> select(MCompanyVo searchCondition) {
        // 查询 数据
        List<MCompanyVo> list = mapper.select(searchCondition);
        return list;
    }

    /**
     * 获取导出列表数据 - 标准化实现
     *
     * @param searchCondition 查询条件
     * @param orderByClause 动态排序子句
     * @return 导出数据列表
     */
    @Override
    public List<MCompanyExportVo> selectExportList(MCompanyVo searchCondition) {
        // 处理动态排序
        String orderByClause = "";
        if (searchCondition.getPageCondition() != null && StringUtils.isNotEmpty(searchCondition.getPageCondition().getSort())) {
            String sort = searchCondition.getPageCondition().getSort();
            String field = sort.startsWith("-") ? sort.substring(1) : sort;
            
            // 正则验证：只允许字母、数字、下划线，防止SQL注入
            if (!field.matches("^[a-zA-Z_][a-zA-Z0-9_]*$")) {
                throw new BusinessException("非法的排序字段格式");
            }
            
            if (sort.startsWith("-")) {
                // 降序：去掉前缀-，添加DESC
                orderByClause = " ORDER BY " + field + " DESC";
            } else {
                // 升序：直接使用字段名，添加ASC
                orderByClause = " ORDER BY " + sort + " ASC";
            }
        }
        
        return mapper.selectExportList(searchCondition, orderByClause);
    }

    /**
     * 批量逻辑删除（单向删除，不支持恢复）
     * @param searchCondition
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteByIdsIn(List<MCompanyVo> searchCondition) {
        List<MCompanyEntity> list = mapper.selectIdsIn(searchCondition);
        for(MCompanyEntity entity : list) {
            // 只处理删除操作，不支持恢复
            if(entity.getIs_del()){
                // 已经删除的记录跳过
                continue;
            }
            
            // 执行删除前业务校验
            CheckResultAo cr = checkLogic(entity, CheckResultAo.DELETE_CHECK_TYPE);
            if (cr.isSuccess() == false) {
                throw new BusinessException(cr.getMessage());
            }
            
            // 设置为已删除状态
            entity.setIs_del(true);
//            entity.setTenant_id(getUserSessionTenantId());
        }
        saveOrUpdateBatch(list, 500);
    }

    /**
     * 从组织架构删除主体企业（同时删除企业实体和组织关联关系）
     * @param searchCondition
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteByIdsFromOrg(List<MCompanyVo> searchCondition) {
        List<MCompanyEntity> list = mapper.selectIdsIn(searchCondition);
        for(MCompanyEntity entity : list) {
            // 只处理删除操作，不支持恢复
            if(entity.getIs_del()){
                // 已经删除的记录跳过
                continue;
            }
            
            // 执行删除前业务校验（组织删除专用校验，跳过组织架构关联检查）
            CheckResultAo cr = checkLogicForOrgDeletion(entity);
            if (!cr.isSuccess()) {
                throw new BusinessException(cr.getMessage());
            }
            
            // 设置为已删除状态
            entity.setIs_del(true);
            
            // 从组织架构表中删除关联关系，主体企业对应节点类型为'30'
            orgMapper.deleteBySerialIdAndType(entity.getId(), "30");
        }
        saveOrUpdateBatch(list, 500);
    }

    /**
     * 插入一条记录（选择字段，策略插入）
     * @param entity 实体对象
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public InsertResultAo<Integer> insert(MCompanyEntity entity) {
//        entity.setTenant_id(getUserSessionTenantId());
        // 编号为空则自动生成编号
        if(StringUtils.isEmpty(entity.getCode())){
            entity.setCode(autoCode.autoCode().getCode());
        }
        // 插入前check
        CheckResultAo cr = checkLogic(entity, CheckResultAo.INSERT_CHECK_TYPE);
        if (cr.isSuccess() == false) {
            throw new BusinessException(cr.getMessage());
        }
        // 插入逻辑保存
        entity.setIs_del(false);
        Integer rtn = mapper.insert(entity);
        enterpriseService.insertSystemEnterpriseByOrgCompany(entity);
        return InsertResultUtil.OK(rtn);
    }

    /**
     * 更新一条记录（选择字段，策略更新）
     * @param entity 实体对象
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public UpdateResultAo<Integer> update(MCompanyEntity entity) {
        // 更新前check
        CheckResultAo cr = checkLogic(entity, CheckResultAo.UPDATE_CHECK_TYPE);
        if (cr.isSuccess() == false) {
            throw new BusinessException(cr.getMessage());
        }
        // 更新逻辑保存
//        entity.setU_id(null);
//        entity.setU_time(null);
//        entity.setTenant_id(getUserSessionTenantId());
        Integer rtn = mapper.updateById(entity);
        enterpriseService.updateSystemEnterpriseByOrgCompany(entity);
        return UpdateResultUtil.OK(rtn);
    }

    /**
     * 获取列表，查询所有数据
     *
     * @param code
     * @return
     */
    public List<MCompanyEntity> selectByCode(String code, Long equal_id) {
        // 查询 数据
        List<MCompanyEntity> list = mapper.selectByCode(code, equal_id);
        return list;
    }

    /**
     * 获取列表，查询所有数据
     *
     * @param name
     * @return
     */
    public List<MCompanyEntity> selectByName(String name, Long equal_id) {
        // 查询 数据
        List<MCompanyEntity> list = mapper.selectByName(name, equal_id);
        return list;
    }

    /**
     * 获取列表，查询所有数据
     *
     * @param name
     * @return
     */
    public List<MCompanyEntity> selectBySimpleName(String name, Long equal_id) {
        // 查询 数据
        List<MCompanyEntity> list = mapper.selectBySimpleName(name, equal_id);
        return list;
    }

    /**
     * check逻辑
     * @return
     */
    public CheckResultAo checkLogic(MCompanyEntity entity, String moduleType){
        switch (moduleType) {
            case CheckResultAo.INSERT_CHECK_TYPE:
                // 新增场合，不能重复
                List<MCompanyEntity> codeList_insertCheck = selectByCode(entity.getCode(), null);
                List<MCompanyEntity> nameList_insertCheck = selectByName(entity.getName(),  null);
                List<MCompanyEntity> simple_name_insertCheck = selectBySimpleName(entity.getSimple_name(), null);
                if (codeList_insertCheck.size() >= 1) {
                    return CheckResultUtil.NG("新增保存出错：企业编号【"+ entity.getCode() +"】出现重复!", entity.getCode());
                }
                if (nameList_insertCheck.size() >= 1) {
                    return CheckResultUtil.NG("新增保存出错：企业全称【"+ entity.getName() +"】出现重复!", entity.getName());
                }
                if (simple_name_insertCheck.size() >= 1) {
                    return CheckResultUtil.NG("新增保存出错：企业简称【"+ entity.getSimple_name() +"】出现重复!", entity.getSimple_name());
                }
                break;
            case CheckResultAo.UPDATE_CHECK_TYPE:
                // 更新场合，不能重复设置
                List<MCompanyEntity> codeList_updCheck = selectByCode(entity.getCode(), entity.getId());
                List<MCompanyEntity> nameList_updCheck = selectByName(entity.getName(), entity.getId());
                List<MCompanyEntity> simple_name_updCheck = selectBySimpleName(entity.getSimple_name(), entity.getId());

                if (codeList_updCheck.size() >= 1) {
                    return CheckResultUtil.NG("更新保存出错：企业编号【"+ entity.getCode() +"】出现重复!", entity.getCode());
                }
                if (nameList_updCheck.size() >= 1) {
                    return CheckResultUtil.NG("更新保存出错：企业全称【"+ entity.getName() +"】出现重复!", entity.getName());
                }
                if (simple_name_updCheck.size() >= 1) {
                    return CheckResultUtil.NG("更新保存出错：企业简称【"+ entity.getSimple_name() +"】出现重复!", entity.getSimple_name());
                }
                break;
            case CheckResultAo.DELETE_CHECK_TYPE:
                /** 如果逻辑删除为true，表示已经删除，无需再次删除 */
                if(entity.getIs_del()) {
                    return CheckResultUtil.OK();
                }
                
                // 1. 检查组织架构关联
                int orgCount = mapper.isExistsInOrg(entity);
                if(orgCount > 0){
                    return CheckResultUtil.NG("删除出错：该企业【"+ entity.getSimple_name() +"】在组织机构中正在被使用，不能删除！关联记录数：" + orgCount, orgCount);
                }
                
                // 2. 检查员工关联
                int staffCount = mapper.isExistsInStaff(entity);
                if(staffCount > 0){
                    return CheckResultUtil.NG("删除出错：该企业【"+ entity.getSimple_name() +"】下还有" + staffCount + "名员工，不能删除！", staffCount);
                }
                
                // 3. 检查下级组织关联
                int subOrgCount = mapper.isExistsInSubOrg(entity);
                if(subOrgCount > 0){
                    return CheckResultUtil.NG("删除出错：该企业【"+ entity.getSimple_name() +"】下还有" + subOrgCount + "个下级组织，不能删除！", subOrgCount);
                }
                
                // 4. 检查采购合同关联
                int poContractCount = mapper.isExistsInPoContract(entity);
                if(poContractCount > 0){
                    return CheckResultUtil.NG("删除出错：该企业【"+ entity.getSimple_name() +"】作为采购方还有" + poContractCount + "个有效采购合同，不能删除！", poContractCount);
                }
                
                // 5. 检查销售合同关联
                int soContractCount = mapper.isExistsInSoContract(entity);
                if(soContractCount > 0){
                    return CheckResultUtil.NG("删除出错：该企业【"+ entity.getSimple_name() +"】作为销售方还有" + soContractCount + "个有效销售合同，不能删除！", soContractCount);
                }
                
                // 6. 检查采购订单关联
                int poOrderCount = mapper.isExistsInPoOrder(entity);
                if(poOrderCount > 0){
                    return CheckResultUtil.NG("删除出错：该企业【"+ entity.getSimple_name() +"】作为采购方还有" + poOrderCount + "个有效采购订单，不能删除！", poOrderCount);
                }
                
                // 7. 检查销售订单关联
                int soOrderCount = mapper.isExistsInSoOrder(entity);
                if(soOrderCount > 0){
                    return CheckResultUtil.NG("删除出错：该企业【"+ entity.getSimple_name() +"】作为销售方还有" + soOrderCount + "个有效销售订单，不能删除！", soOrderCount);
                }
                
                // 8. 检查应付款关联
                int apPayCount = mapper.isExistsInApPay(entity);
                if(apPayCount > 0){
                    return CheckResultUtil.NG("删除出错：该企业【"+ entity.getSimple_name() +"】作为采购方还有" + apPayCount + "笔有效应付款，不能删除！", apPayCount);
                }
                
                // 9. 检查应收款关联
                int arReceiveCount = mapper.isExistsInArReceive(entity);
                if(arReceiveCount > 0){
                    return CheckResultUtil.NG("删除出错：该企业【"+ entity.getSimple_name() +"】作为销售方还有" + arReceiveCount + "笔有效应收款，不能删除！", arReceiveCount);
                }
                break;
            default:
        }
        return CheckResultUtil.OK();
    }

    /**
     * 组织删除专用校验逻辑（跳过组织架构依赖检查，只保留业务依赖检查）
     * @param entity
     * @return
     */
    public CheckResultAo checkLogicForOrgDeletion(MCompanyEntity entity){
        /** 如果逻辑删除为true，表示已经删除，无需再次删除 */
        if(entity.getIs_del()) {
            return CheckResultUtil.OK();
        }
        
        // 跳过组织架构关联检查（第1项），因为这就是从组织架构删除的操作
        
        // 2. 检查员工关联
        int staffCount = mapper.isExistsInStaff(entity);
        if(staffCount > 0){
            return CheckResultUtil.NG("删除出错：该企业【"+ entity.getSimple_name() +"】下还有" + staffCount + "名员工，不能删除！", staffCount);
        }
        
        // 3. 检查下级组织关联
        int subOrgCount = mapper.isExistsInSubOrg(entity);
        if(subOrgCount > 0){
            return CheckResultUtil.NG("删除出错：该企业【"+ entity.getSimple_name() +"】下还有" + subOrgCount + "个下级组织，不能删除！", subOrgCount);
        }
        
        // 4. 检查采购合同关联
        int poContractCount = mapper.isExistsInPoContract(entity);
        if(poContractCount > 0){
            return CheckResultUtil.NG("删除出错：该企业【"+ entity.getSimple_name() +"】作为采购方还有" + poContractCount + "个有效采购合同，不能删除！", poContractCount);
        }
        
        // 5. 检查销售合同关联
        int soContractCount = mapper.isExistsInSoContract(entity);
        if(soContractCount > 0){
            return CheckResultUtil.NG("删除出错：该企业【"+ entity.getSimple_name() +"】作为销售方还有" + soContractCount + "个有效销售合同，不能删除！", soContractCount);
        }
        
        // 6. 检查采购订单关联
        int poOrderCount = mapper.isExistsInPoOrder(entity);
        if(poOrderCount > 0){
            return CheckResultUtil.NG("删除出错：该企业【"+ entity.getSimple_name() +"】作为采购方还有" + poOrderCount + "个有效采购订单，不能删除！", poOrderCount);
        }
        
        // 7. 检查销售订单关联
        int soOrderCount = mapper.isExistsInSoOrder(entity);
        if(soOrderCount > 0){
            return CheckResultUtil.NG("删除出错：该企业【"+ entity.getSimple_name() +"】作为销售方还有" + soOrderCount + "个有效销售订单，不能删除！", soOrderCount);
        }
        
        // 8. 检查应付款关联
        int apPayCount = mapper.isExistsInApPay(entity);
        if(apPayCount > 0){
            return CheckResultUtil.NG("删除出错：该企业【"+ entity.getSimple_name() +"】作为采购方还有" + apPayCount + "笔有效应付款，不能删除！", apPayCount);
        }
        
        // 9. 检查应收款关联
        int arReceiveCount = mapper.isExistsInArReceive(entity);
        if(arReceiveCount > 0){
            return CheckResultUtil.NG("删除出错：该企业【"+ entity.getSimple_name() +"】作为销售方还有" + arReceiveCount + "笔有效应收款，不能删除！", arReceiveCount);
        }
        
        return CheckResultUtil.OK();
    }


    /**
     * 查询by id，返回结果
     *
     * @param id
     * @return
     */
    @Override
    public MCompanyVo selectByid(Long id) {
        // 查询 数据
        return mapper.selectId(id);
    }
}
