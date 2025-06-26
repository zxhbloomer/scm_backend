package com.xinyirun.scm.core.system.serviceimpl.business.rtwo;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.busniess.rtwo.BRtWoRouterEntity;
import com.xinyirun.scm.bean.entity.busniess.rtwo.BRtWoRouterMaterialEntity;
import com.xinyirun.scm.bean.entity.busniess.rtwo.BRtWoRouterProductEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.UpdateResultUtil;
import com.xinyirun.scm.bean.system.vo.business.rtwo.BRtWoRouterMaterialVo;
import com.xinyirun.scm.bean.system.vo.business.rtwo.BRtWoRouterProductVo;
import com.xinyirun.scm.bean.system.vo.business.rtwo.BRtWoRouterVo;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.exception.system.InsertErrorException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.system.mapper.business.rtwo.BRtWoMaterialMapper;
import com.xinyirun.scm.core.system.mapper.business.rtwo.BRtWoProductMapper;
import com.xinyirun.scm.core.system.mapper.business.rtwo.BRtWoRouterMapper;
import com.xinyirun.scm.core.system.service.business.rtwo.IBRtWoRouterMaterialService;
import com.xinyirun.scm.core.system.service.business.rtwo.IBRtWoRouterProductService;
import com.xinyirun.scm.core.system.service.business.rtwo.IBRtWoRouterService;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.BRtWoRouterAutoCodeServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 *  生产配方服务类 服务实现类
 * </p>
 *
 * @author xinyirun
 * @since 2022-12-22
 */
@Service
public class BRtWoRouterServiceImpl extends ServiceImpl<BRtWoRouterMapper, BRtWoRouterEntity> implements IBRtWoRouterService {

    @Autowired
    private IBRtWoRouterProductService productService;

    @Autowired
    private IBRtWoRouterMaterialService materialService;

    @Autowired
    private BRtWoRouterAutoCodeServiceImpl autoCodeService;

    @Autowired
    private BRtWoRouterMapper mapper;

    @Autowired
    private BRtWoProductMapper bWoProductMapper;

    @Autowired
    private BRtWoMaterialMapper bWoMaterialMapper;

    /**
     * 新增配方
     *
     * @param param 新增参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<BRtWoRouterVo> insert(BRtWoRouterVo param) {
        // 必须有一个产成品
        Assert.notEmpty(param.getProduct_list(), "请选择产成品!");
        // 判断原材料
        checkMaterial(param.getMaterial_list());
        // 添加router
        BRtWoRouterEntity entity = (BRtWoRouterEntity) BeanUtilsSupport.copyProperties(param, BRtWoRouterEntity.class);
        entity.setCode(autoCodeService.autoCode().getCode());
        // 生产配方, 默认启用
        entity.setIs_enable(DictConstant.DICT_B_ROUTER_ENABLE_1);
        int rtn = baseMapper.insert(entity);
        if (rtn == 0) {
            throw new InsertErrorException("保存失败");
        }
        // 新增产成品
        productService.insertAll(param.getProduct_list(), entity.getId(), DictConstant.DICT_B_ROUTER_PRODUCT_TYPE_C);
        // 新增副产品
        productService.insertAll(param.getCoproduct_list(), entity.getId(), DictConstant.DICT_B_ROUTER_PRODUCT_TYPE_F );
        // 保存原材料
        materialService.insertAll(param.getMaterial_list(), entity.getId());
        // 查询产成品, 副产品
        List<BRtWoRouterProductVo> product_list_all = productService.selectByRouterId(entity.getId());
        List<BRtWoRouterProductVo> coproduct_list = product_list_all.stream().filter(item -> DictConstant.DICT_B_ROUTER_PRODUCT_TYPE_F.equals(item.getType())).collect(Collectors.toList());
        List<BRtWoRouterProductVo> product_list = product_list_all.stream().filter(item -> DictConstant.DICT_B_ROUTER_PRODUCT_TYPE_C.equals(item.getType())).collect(Collectors.toList());
        // 查询原材料
        List<BRtWoRouterMaterialVo> material_list = materialService.selectByRouterId(entity.getId());
        entity.setJson_product_list(JSON.toJSONString(product_list));
        entity.setJson_material_list(JSON.toJSONString(material_list));
        entity.setJson_coproduct_list(JSON.toJSONString(coproduct_list));
        mapper.updateById(entity);
        // 更新成功后, 查询
        BRtWoRouterVo bRtWoRouterVo = mapper.selectById(entity.getId());
        return InsertResultUtil.OK(bRtWoRouterVo);
    }

    /**
     * 更新配方
     *
     * @param param 更新参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<BRtWoRouterVo> updateParam(BRtWoRouterVo param) {
        Integer id = param.getId();
        Assert.notNull(id, "ID 不能为空");
        // 判断原材料
        checkMaterial(param.getMaterial_list());
        // 更新 router
        BRtWoRouterEntity entity = (BRtWoRouterEntity) BeanUtilsSupport.copyProperties(param, BRtWoRouterEntity.class);
        int i = baseMapper.updateById(entity);
        if (i == 0) {
            throw new UpdateErrorException("更新失败");
        }
        // 更新, 考虑到有删除, 先删除全部,在新增
        productService.remove(new QueryWrapper<BRtWoRouterProductEntity>().eq("router_id", id));
        // 新增产成品
        productService.insertAll(param.getProduct_list(), id, DictConstant.DICT_B_ROUTER_PRODUCT_TYPE_C);
        // 新增副产品
        productService.insertAll(param.getCoproduct_list(), id, DictConstant.DICT_B_ROUTER_PRODUCT_TYPE_F);
        // 删除全部原材料
        materialService.remove(new QueryWrapper<BRtWoRouterMaterialEntity>().eq("router_id", id));
        // 新增全部
        materialService.insertAll(param.getMaterial_list(), id);

        // 查询产成品, 副产品
        List<BRtWoRouterProductVo> product_list_all = productService.selectByRouterId(entity.getId());
        List<BRtWoRouterProductVo> coproduct_list = product_list_all.stream().filter(item -> DictConstant.DICT_B_ROUTER_PRODUCT_TYPE_F.equals(item.getType())).collect(Collectors.toList());
        List<BRtWoRouterProductVo> product_list = product_list_all.stream().filter(item -> DictConstant.DICT_B_ROUTER_PRODUCT_TYPE_C.equals(item.getType())).collect(Collectors.toList());
        // 查询原材料
        List<BRtWoRouterMaterialVo> material_list = materialService.selectByRouterId(entity.getId());
        entity.setJson_product_list(JSON.toJSONString(product_list));
        entity.setJson_material_list(JSON.toJSONString(material_list));
        entity.setJson_coproduct_list(JSON.toJSONString(coproduct_list));
        mapper.updateById(entity);
        // 更新成功后, 查询
        BRtWoRouterVo bRtWoRouterVo = mapper.selectById(entity.getId());
        return UpdateResultUtil.OK(bRtWoRouterVo);
    }

    /**
     * 查询详情
     *
     * @param id 主键id
     * @return BWoRouterVo
     */
    @Override
    public BRtWoRouterVo getDetail(Integer id) {
        BRtWoRouterVo result = baseMapper.getById(id);
        return result;
    }
    /**
     * 分页查询生产配方
     * 不考虑合并单元格
     * @param param 入参
     * @return IPage<BWoRouterVo>
     */
    @Override
    public IPage<BRtWoRouterVo> selectPageList(BRtWoRouterVo param) {
        // 分页条件
        Page<BRtWoRouterVo> pageCondition = new Page(param.getPageCondition().getCurrent(), param.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, param.getPageCondition().getSort());
        IPage<BRtWoRouterVo> pageList = mapper.selectPageList(param, pageCondition);
        return pageList;
    }

    /**
     * 启用生产配方
     *
     * @param param
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enable(List<BRtWoRouterVo> param) {
        saveEnable(param,  DictConstant.DICT_B_ROUTER_ENABLE_1);
    }

    /**
     * 禁用生产配方
     *
     * @param param
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void disabled(List<BRtWoRouterVo> param) {
        saveEnable(param, DictConstant.DICT_B_ROUTER_ENABLE_0);
    }

    private void saveEnable(List<BRtWoRouterVo>  param, String enable) {
        // 查询全部
        Set<Integer> ids = param.stream().map(BRtWoRouterVo::getId).collect(Collectors.toSet());
        List<BRtWoRouterEntity> entities = baseMapper.selectList(new QueryWrapper<BRtWoRouterEntity>().in("id", ids));
        entities.forEach(item -> item.setIs_enable(enable));
        this.updateBatchById(entities);
    }

    /**
     * 校验原材料, 配比是否为100%
     * @param material_list
     */
    private void checkMaterial(@NotEmpty(message = "请添加至少一条原材料明细") List<BRtWoRouterMaterialVo> material_list) {
        // 计算原材料配比, 如果配比为空会报空指针异常
//        BigDecimal router = material_list.stream().map(BRtWoRouterMaterialVo::getRouter).reduce(BigDecimal.ZERO, BigDecimal::add);
//        if (router.compareTo(new BigDecimal(100)) != 0) {
//            throw new BusinessException("原材料明细的配比之和应为100%");
//        }
    }

    /**
     * 校验副产品, 产成品配比是否满足 100%
     * @param product_list 产成品 & 副产品
     */
  /*  private void checkProduct(@NotEmpty(message = "请添加至少一条产成品、副产品明细") List<BRtWoRouterProductVo> product_list) {
        // 产成品个数
        int productNum = 0;
        // 遍历, 判断配比
        for (BRtWoRouterProductVo vo : product_list) {
            // 判断是否只有一个产成品
            if (DictConstant.DICT_B_ROUTER_PRODUCT_TYPE_C.equals(vo.getType())) {
                productNum ++;
            }
        }
        // 判断
        if (productNum == 0) {
            throw new BusinessException("请添加至少一条产成品明细! ");
        }
        if (productNum > 1) {
            throw new BusinessException("不可添加两条产成品! ");
        }
    }*/
}
