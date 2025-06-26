package com.xinyirun.scm.core.system.serviceimpl.business.wo;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.bean.entity.busniess.wo.BWoRouterEntity;
import com.xinyirun.scm.bean.entity.busniess.wo.BWoRouterMaterialEntity;
import com.xinyirun.scm.bean.entity.busniess.wo.BWoRouterProductEntity;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.result.utils.v1.InsertResultUtil;
import com.xinyirun.scm.bean.system.result.utils.v1.UpdateResultUtil;
import com.xinyirun.scm.bean.system.vo.business.wo.BWoRouterMaterialVo;
import com.xinyirun.scm.bean.system.vo.business.wo.BWoRouterProductVo;
import com.xinyirun.scm.bean.system.vo.business.wo.BWoRouterVo;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.exception.system.InsertErrorException;
import com.xinyirun.scm.common.exception.system.UpdateErrorException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.core.system.mapper.business.wo.BWoMaterialMapper;
import com.xinyirun.scm.core.system.mapper.business.wo.BWoProductMapper;
import com.xinyirun.scm.core.system.mapper.business.wo.BWoRouterMapper;
import com.xinyirun.scm.core.system.service.business.wo.IBWoRouterMaterialService;
import com.xinyirun.scm.core.system.service.business.wo.IBWoRouterProductService;
import com.xinyirun.scm.core.system.service.business.wo.IBWoRouterService;
import com.xinyirun.scm.core.system.serviceimpl.common.autocode.BWoRouterAutoCodeServiceImpl;
import com.xinyirun.scm.core.system.utils.mybatis.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import jakarta.validation.constraints.NotEmpty;
import java.math.BigDecimal;
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
public class BWoRouterServiceImpl extends ServiceImpl<BWoRouterMapper, BWoRouterEntity> implements IBWoRouterService {

    @Autowired
    private IBWoRouterProductService productService;

    @Autowired
    private IBWoRouterMaterialService materialService;

    @Autowired
    private BWoRouterAutoCodeServiceImpl autoCodeService;

    @Autowired
    private BWoRouterMapper mapper;

    @Autowired
    private BWoProductMapper bWoProductMapper;

    @Autowired
    private BWoMaterialMapper bWoMaterialMapper;

    /**
     * 新增配方
     *
     * @param param 新增参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public InsertResultAo<BWoRouterVo> insert(BWoRouterVo param) {
        // 判断产成品, 副产品配比是否为 100%
        checkProduct(param.getProduct_list());
        // 判断原材料
        checkMaterial(param.getMaterial_list());
        // 添加router
        BWoRouterEntity entity = (BWoRouterEntity) BeanUtilsSupport.copyProperties(param, BWoRouterEntity.class);
        entity.setCode(autoCodeService.autoCode().getCode());
        // 生产配方, 默认启用
        entity.setIs_enable(DictConstant.DICT_B_ROUTER_ENABLE_1);
        int rtn = baseMapper.insert(entity);
        if (rtn == 0) {
            throw new InsertErrorException("保存失败");
        }
        // 新增产成品, 副产品
        productService.insertAll(param.getProduct_list(), entity.getId());
        // 保存原材料
        materialService.insertAll(param.getMaterial_list(), entity.getId());

        // 查询产成品, 副产品
        List<BWoRouterProductVo> product_list = productService.selectByRouterId(entity.getId());
        // 查询原材料
        List<BWoRouterMaterialVo> material_list = materialService.selectByRouterId(entity.getId());
        entity.setJson_product_list(JSON.toJSONString(product_list));
        entity.setJson_material_list(JSON.toJSONString(material_list));
        mapper.updateById(entity);
        // 查询 新添加的值
        BWoRouterVo bWoRouterVo = mapper.selectById(entity.getId());
        return InsertResultUtil.OK(bWoRouterVo);

    }

    /**
     * 更新配方
     *
     * @param param 更新参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UpdateResultAo<BWoRouterVo> updateParam(BWoRouterVo param) {
        Integer id = param.getId();
        Assert.notNull(id, "ID 不能为空");
        // 判断产成品, 副产品配比是否为 100%
        checkProduct(param.getProduct_list());
        // 判断原材料
        checkMaterial(param.getMaterial_list());
        // 更新 router
        BWoRouterEntity entity = (BWoRouterEntity) BeanUtilsSupport.copyProperties(param, BWoRouterEntity.class);
        int i = baseMapper.updateById(entity);
        if (i == 0) {
            throw new UpdateErrorException("更新失败");
        }
        // 更新, 考虑到有删除, 先删除全部,在新增
        productService.remove(new QueryWrapper<BWoRouterProductEntity>().eq("router_id", id));
        // 新增产成品, 副产品
        productService.insertAll(param.getProduct_list(), id);
        // 删除全部原材料
        materialService.remove(new QueryWrapper<BWoRouterMaterialEntity>().eq("router_id", id));
        // 新增全部
        materialService.insertAll(param.getMaterial_list(), id);

        // 查询产成品, 副产品
        List<BWoRouterProductVo> product_list = productService.selectByRouterId(entity.getId());
        // 查询原材料
        List<BWoRouterMaterialVo> material_list = materialService.selectByRouterId(entity.getId());
        entity.setJson_product_list(JSON.toJSONString(product_list));
        entity.setJson_material_list(JSON.toJSONString(material_list));
        mapper.updateById(entity);
        // 返回新增的数据
        BWoRouterVo bWoRouterVo = mapper.selectById(entity.getId());
        return UpdateResultUtil.OK(bWoRouterVo);
    }

    /**
     * 查询详情
     *
     * @param id 主键id
     * @return BWoRouterVo
     */
    @Override
    public BWoRouterVo getDetail(Integer id) {
        BWoRouterVo result = baseMapper.getById(id);
        // 查询产成品, 副产品, 这样写是为了兼容旧数据
        List<BWoRouterProductVo> product_list = productService.selectByRouterId(id);
        // 查询原材料
        List<BWoRouterMaterialVo> material_list = materialService.selectByRouterId(id);

        result.setProduct_list(product_list);
        result.setMaterial_list(material_list);
        return result;
    }
    /**
     * 分页查询生产配方
     * 不考虑合并单元格
     * @param param 入参
     * @return IPage<BWoRouterVo>
     */
    @Override
    public IPage<BWoRouterVo> selectPageList(BWoRouterVo param) {
        // 分页条件
        Page<BWoRouterVo> pageCondition = new Page(param.getPageCondition().getCurrent(), param.getPageCondition().getSize());
        // 通过page进行排序
        PageUtil.setSort(pageCondition, param.getPageCondition().getSort());

        IPage<BWoRouterVo> pageList = mapper.selectPageList(param, pageCondition);

//        for (BWoRouterVo vo: pageList.getRecords()) {
//            BWoRouterProductVo bWoRouterProductVo = new BWoRouterProductVo();
//            bWoRouterProductVo.setRouter_id(vo.getId());
//            List<BWoRouterProductVo> productList = bWoProductMapper.selectList(bWoRouterProductVo);
//            vo.setProduct_list(productList);
//
//            BWoRouterMaterialVo bWoRouterMaterialVo = new BWoRouterMaterialVo();
//            bWoRouterMaterialVo.setRouter_id(vo.getId());
//            List<BWoRouterMaterialVo> materialList = bWoMaterialMapper.selectList(bWoRouterMaterialVo);
//            vo.setMaterial_list(materialList);
//        }

        return pageList;
    }

    /**
     * 启用生产配方
     *
     * @param param
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enable(List<BWoRouterVo> param) {
        saveEnable(param,  DictConstant.DICT_B_ROUTER_ENABLE_1);
    }

    /**
     * 禁用生产配方
     *
     * @param param
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void disabled(List<BWoRouterVo> param) {
        saveEnable(param, DictConstant.DICT_B_ROUTER_ENABLE_0);
    }

    private void saveEnable(List<BWoRouterVo>  param, String enable) {
        // 查询全部
        Set<Integer> ids = param.stream().map(BWoRouterVo::getId).collect(Collectors.toSet());
        List<BWoRouterEntity> entities = baseMapper.selectList(new QueryWrapper<BWoRouterEntity>().in("id", ids));
        entities.forEach(item -> item.setIs_enable(enable));
        this.updateBatchById(entities);
    }

    /**
     * 校验原材料, 配比是否为100%
     * @param material_list
     */
    private void checkMaterial(@NotEmpty(message = "请添加至少一条原材料明细") List<BWoRouterMaterialVo> material_list) {
        // 计算原材料配比, 如果配比为空会报空指针异常
        BigDecimal router = material_list.stream().map(BWoRouterMaterialVo::getRouter).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (router.compareTo(new BigDecimal(100)) != 0) {
            throw new BusinessException("原材料明细的配比之和应为100%");
        }
    }

    /**
     * 校验副产品, 产成品配比是否满足 100%
     * @param product_list 产成品 & 副产品
     */
    private void checkProduct(@NotEmpty(message = "请添加至少一条产成品、副产品明细") List<BWoRouterProductVo> product_list) {
        // 产成品个数
        int productNum = 0;
        // 配比总数
        BigDecimal router = BigDecimal.ZERO;
        // 遍历, 判断配比
        for (BWoRouterProductVo vo : product_list) {
            // 判断是否只有一个产成品
            if (DictConstant.DICT_B_ROUTER_PRODUCT_TYPE_C.equals(vo.getType())) {
                productNum ++;
            }
            router = router.add(vo.getRouter());
        }
        // 判断
        if (productNum == 0) {
            throw new BusinessException("请添加至少一条产成品明细! ");
        }
        if (productNum > 1) {
            throw new BusinessException("不可添加两条产成品! ");
        }
        if (router.compareTo(new BigDecimal(100)) != 0) {
            throw new BusinessException("产成品、副产品明细配比之和应为100%");
        }
    }
}
