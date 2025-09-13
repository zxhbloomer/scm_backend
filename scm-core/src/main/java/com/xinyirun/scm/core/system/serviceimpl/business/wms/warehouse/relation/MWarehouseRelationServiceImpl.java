package com.xinyirun.scm.core.system.serviceimpl.business.wms.warehouse.relation;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.business.wms.warehouse.BWarehouseGroupRelationEntity;
import com.xinyirun.scm.bean.entity.business.wms.warehouse.relation.MWarehouseRelationEntity;
import com.xinyirun.scm.bean.entity.master.org.MGroupEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.CheckResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.utils.common.tree.TreeUtil;
import com.xinyirun.scm.bean.system.vo.business.wms.warehouse.relation.*;
import com.xinyirun.scm.bean.system.vo.master.warehouse.MWarehouseVo;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.system.mapper.business.warehouse.relation.MWarehouseRelationMapper;
import com.xinyirun.scm.core.system.service.business.warehouse.IBWarehouseGroupRelationService;
import com.xinyirun.scm.core.system.service.business.warehouse.relation.IBWarehouseRelationService;
import com.xinyirun.scm.core.system.service.business.warehouse.relation.IMWarehouseRelationService;
import com.xinyirun.scm.core.system.service.common.ICommonComponentService;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2022-02-27
 */
@Service
public class MWarehouseRelationServiceImpl extends ServiceImpl<MWarehouseRelationMapper, MWarehouseRelationEntity> implements IMWarehouseRelationService {

    @Autowired
    private MWarehouseRelationMapper mapper;

    @Autowired
    private ICommonComponentService iCommonComponentService;

    @Autowired
    private IBWarehouseGroupRelationService ibWarehouseGroupRelationService;

    @Autowired
    private IBWarehouseRelationService ibWarehouseRelationService;

    /**
     * 获取所有数据，左侧树数据
     */
    @Override
    public List<MRelationTreeVo> getTreeList(MRelationTreeVo searchCondition) {
        // 查询 数据
        List<MRelationTreeVo> list = mapper.getTreeList(searchCondition);
        List<MRelationTreeVo> rtnList = TreeUtil.getTreeList(list);
        return rtnList;
    }

    /**
     * 插入一条记录（选择字段，策略插入）
     * @param vo 实体对象
     * @return
     */
    @Override
    public InsertResultAo<Integer> insert(MWarehouseRelationVo vo) {
        // 设置entity
        vo.setSerial_type(DictConstant.DICT_B_WAREHOUSE_GROUP);

        // 插入前check
        CheckResultAo cr = checkLogic(vo, CheckResultAo.INSERT_CHECK_TYPE);

        if (cr.isSuccess() == false) {
            throw new BusinessException(cr.getMessage());
        }
        // 获取父亲的entity
        MWarehouseRelationEntity parentEntity = getById(vo.getParent_id());
        Integer son_count = parentEntity.getSon_count();
        son_count = (son_count == null ? 0 : son_count)  + 1;
        parentEntity.setSon_count(son_count);
        // 保存父亲的儿子的个数
//        parentEntity.setC_id(null);
//        parentEntity.setC_time(null);
        mapper.updateById(parentEntity);

        // 获取父亲的code
        String parentCode = parentEntity.getCode();
        // 计算当前编号
        // 获取当前son_count
        // 0 代表前面补充0
        // 4 代表长度为4
        // d 代表参数为正数型
        String str = String.format("%04d", son_count);
        vo.setCode(parentCode + str);
        vo.setSon_count(0);

        // 执行插入操作
        MWarehouseRelationEntity entity = (MWarehouseRelationEntity) BeanUtilsSupport.copyProperties(vo,MWarehouseRelationEntity.class);
        int insert_result = mapper.insert(entity);

        return InsertResultUtil.OK(insert_result);
    }

    /**
     * 查询添加的子结点是否合法
     *
     * @return
     */
    public Integer selectNodeInsertStatus(String code, String type) {
        // 查询 数据
        Integer count = mapper.selectNodeInsertStatus(code, type);
        return count;
    }

    /**
     * 查询添加的子结点是否合法，子结点被重复选择使用的情况
     *
     * @return
     */
    public Integer getCountBySerial(MWarehouseRelationVo entity, Integer equal_id) {
        // 查询 数据
        Integer count = mapper.getCountBySerial(entity, equal_id);
        return count;
    }

    /**
     * check逻辑
     * @return
     */
    public CheckResultAo checkLogic(MWarehouseRelationVo vo, String moduleType){
        Integer count = 0;
        switch (moduleType) {
            case CheckResultAo.INSERT_CHECK_TYPE:
                // 查看子结点是否正确：
                Integer countInsert = this.selectNodeInsertStatus(vo.getCode(),vo.getType());
                if(countInsert > 0){
                    String nodeTypeName = iCommonComponentService.getDictName(DictConstant.DICT_ORG_SETTING_TYPE, vo.getType());
                    return CheckResultUtil.NG("新增保存出错：新增的子结点类型不能是" + "【" + nodeTypeName + "】", countInsert);
                }
                // 查看当前结点是否已经被选择使用
                count = getCountBySerial(vo, null);
                if(count > 0){
                    return CheckResultUtil.NG("新增保存出错：您选择的结点已经存在，请选择尚未被使用的仓库分组。", count);
                }
                break;
            case CheckResultAo.UPDATE_CHECK_TYPE:
                // 查看子结点是否正确：租户->集团->企业->部门->岗位->员工
                Integer countUpdate = this.selectNodeInsertStatus(vo.getCode(),vo.getType());
                if(countUpdate > 0){
                    String nodeTypeName = iCommonComponentService.getDictName(DictConstant.DICT_ORG_SETTING_TYPE, vo.getType());
                    return CheckResultUtil.NG("新增保存出错：更新的当前结点类型不能是" + "【" + nodeTypeName + "】", countUpdate);
                }
                // 查看当前结点是否已经被选择使用
                count = getCountBySerial(vo, vo.getId());
                if(count > 0){
                    return CheckResultUtil.NG("新增保存出错：您选择的结点已经存在，请选择尚未被使用的仓库分组。", count);
                }
                break;
            default:
        }
        return CheckResultUtil.OK();
    }

    /**
     * 获取数据byid
     * @param bean
     * @return
     */
    @Override
    public MWarehouseRelationVo selectByid(MWarehouseRelationVo bean){
        return mapper.selectByid(bean);
    }

    /**
     * 根据code，进行 like 'code%'，匹配当前结点以及子结点
     * @param vo
     * @return
     */
    @Override
    public List<MWarehouseRelationVo> getDataByCode(MWarehouseRelationVo vo) {
        List<MWarehouseRelationVo> rtnList = mapper.getDataByCode(vo);
        return rtnList;
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean deleteById(MWarehouseRelationVo vo) {
        // 检索子组织数据
        List<MWarehouseRelationVo> rtnList = getDataByCode(vo);
        rtnList.forEach(bean -> {
            // check
            BWarehouseRelationVo searchCondition = new BWarehouseRelationVo();
            searchCondition.setWarehouse_relation_code(bean.getCode());
            Integer count = ibWarehouseRelationService.selectCountByRelationCode(searchCondition);
            if (count != null && count > 0) {
                throw new BusinessException("请先解除该仓库组和岗位关系，再作删除操作");
            }
            // 删除 数据
            mapper.deleteById(bean.getId());
        });

        return true;
    }

    /**
     * 获取所有的组织数据
     * @param searchCondition
     * @return
     */
    @Override
    public MRelationCountsVo getAllRelationDataCount(MWarehouseRelationVo searchCondition) {
        MRelationCountsVo mRelationCountsVo = mapper.getAllRelationDataCount(searchCondition);
        return mRelationCountsVo;
    }

    /**
     * 获取仓库分组数据
     * @param searchCondition
     * @return
     */
    @Override
    public List<MRelationTreeVo> getRelations(MWarehouseRelationVo searchCondition) {
        List<MRelationTreeVo> listRelations = select(searchCondition);
        return listRelations;
    }

    /**
     * 获取列表，查询所有数据
     *
     * @param searchCondition
     * @return
     */
    @Override
    public List<MRelationTreeVo> select(MWarehouseRelationVo searchCondition) {
        // 查询 数据
        List<MRelationTreeVo> list = mapper.select(searchCondition);
        List<MRelationTreeVo> rtnList = TreeUtil.getTreeList(list);
        return rtnList;
    }

    /**
     * 获取仓库数据
     * @param searchCondition
     * @return
     */
    @Override
    public IPage<MWarehouseVo> getAllWarehouseListByPosition(MRelationTreeVo searchCondition) {
        // 分页条件
        Page<MGroupEntity> pageCondition =
                new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());
        IPage<MWarehouseVo> listGroup = mapper.getAllWarehouseListByPosition(pageCondition, searchCondition);
        return listGroup;
    }

    /**
     * 获取仓库数据
     * @param searchCondition
     * @return
     */
    @Override
    public Integer getAllWarehouseListByPositionCount(MRelationTreeVo searchCondition) {
        return mapper.getAllWarehouseListByPositionCount( searchCondition);
    }

    /**
     * 获取仓库数据
     * @param searchCondition
     * @return
     */
    @Override
    public IPage<MWarehouseVo> getWarehouse(MRelationTreeVo searchCondition) {
        // 分页条件
        Page<MGroupEntity> pageCondition =
                new Page(searchCondition.getPageCondition().getCurrent(), searchCondition.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, searchCondition.getPageCondition().getSort());
        IPage<MWarehouseVo> list = mapper.getWarehousePageList(pageCondition, searchCondition);
        return list;
    }

    @Override
    public List<MWarehouseVo> getWarehouseList(MRelationTreeVo searchCondition) {
        return mapper.getWarehouseList(searchCondition);
    }

    /**
     * 保存穿梭框数据，仓库组设置
     * @return
     */
    @Override
    public MWarehouseGroupTransferVo getWarehouseTransferList(MWarehouseTransferVo condition) {

        MWarehouseGroupTransferVo rtn = new MWarehouseGroupTransferVo();
        // 获取全部用户
        rtn.setWarehouse_all(mapper.getAllWarehouseTransferList(condition));
        // 获取该该关系已经设置过得仓库
        List<Long> rtnList = mapper.getUsedWarehouseTransferList(condition);
        rtn.setWarehouses(rtnList.toArray(new Long[rtnList.size()]));
        return rtn;
    }

    /**
     * 保存穿梭框数据，仓库组设置
     * @param bean 仓库id list
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String setWarehouseTransfer(MWarehouseTransferVo bean) {
        // 删除剔除的仓库
        mapper.realDeleteById(bean);
        // 增加选择的仓库
        List<BWarehouseGroupRelationEntity> lists = new ArrayList<>();
        for(int i=0; i < bean.getWarehouses().length; i++){
            BWarehouseGroupRelationEntity entity = new BWarehouseGroupRelationEntity();
            entity.setWarehouse_group_id(bean.getWarehouse_group_id());
            entity.setWarehouse_id(bean.getWarehouses()[i]);
            lists.add(entity);
        }
        ibWarehouseGroupRelationService.saveBatch(lists);
        return "OK";
    }
}
