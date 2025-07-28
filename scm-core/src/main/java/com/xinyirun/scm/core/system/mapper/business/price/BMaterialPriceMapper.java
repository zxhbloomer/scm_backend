package com.xinyirun.scm.core.system.mapper.business.price;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.api.vo.sync.ApiBMaterialPriceVo;
import com.xinyirun.scm.bean.entity.business.price.BMaterialPriceEntity;
import com.xinyirun.scm.bean.system.vo.business.price.BMaterialPriceVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author xinyirun
 * @since 2022-12-15
 */
@Repository
public interface BMaterialPriceMapper extends BaseMapper<BMaterialPriceEntity> {

    @Select(""
            +  "    SELECT                                                                                              "
            +  "    	t.id,                                                                                           "
            +  "    	t.goods_id,                                                                                     "
            +  "    	t.goods_code,                                                                                   "
            +  "    	t.goods_name,                                                                                   "
            +  "    	t.sku_id,                                                                                       "
            +  "    	t.sku_code,                                                                                     "
            +  "    	t.sku_name,                                                                                     "
            +  "    	t.type,                                                                                         "
            +  "    	t.query_code,                                                                                   "
            +  "    	t.price,                                                                                        "
            +  "    	t.c_time                                                                                        "
            +  "    FROM                                                                                                "
            +  "    	b_material_price t                                                                              "
            +  "    WHERE                                                                                               "
            +  "    	(                                                                                               "
            +  "    		CONCAT(                                                                                     "
            +  "    			IFNULL( goods_code, '' ),                                                               "
            +  "    			'_',                                                                                    "
            +  "    			IFNULL( goods_name, '' ),                                                               "
            +  "    			'_',                                                                                    "
            +  "    			IFNULL( t.sku_code, '' ),                                                               "
            +  "    			'_',                                                                                    "
            +  "    		IFNULL( t.sku_name, '' )                                                                    "
            +  "    ) LIKE concat ('%', #{p1.goods_name}, '%') or #{p1.goods_name} is null or #{p1.goods_name} = '')    "
    )
    IPage<BMaterialPriceVo> selectPageList(Page<BMaterialPriceVo> page, @Param("p1") BMaterialPriceVo param);

    @Select({
              "   select                                                                                                "
            + "	    id,                                                                                                 "
            + "	    goods_id,                                                                                           "
            + "	    goods_code,                                                                                         "
            + "	    goods_name,                                                                                         "
            + "	    sku_id,                                                                                             "
            + "	    sku_code,                                                                                           "
            + "	    sku_name,                                                                                           "
            + "	    type,                                                                                               "
            + "	    query_code,                                                                                         "
            + "	    ifnull(price, 0) price,                                                                             "
            + "	    c_time						                                                                        "
            + "  from b_material_price                                                                                  "
    })
    public List<ApiBMaterialPriceVo> getMaterialPriceList();
}
