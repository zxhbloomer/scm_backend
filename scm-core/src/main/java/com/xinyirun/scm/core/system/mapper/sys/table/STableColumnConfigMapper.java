package com.xinyirun.scm.core.system.mapper.sys.table;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.bean.entity.sys.table.STableColumnConfigEntity;
import com.xinyirun.scm.bean.system.vo.sys.table.STableColumnConfigVo;
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
 * @since 2022-08-29
 */
@Repository
public interface STableColumnConfigMapper extends BaseMapper<STableColumnConfigEntity> {

    /**
     * 查看页面查询列表
     */
    @Select("    "
            + "		SELECT                                                                                              "
            + "			t1.id,                                                                                          "
            + "			t1.NAME,                                                                                        "
            + "			t1.label,                                                                                       "
            + "			t1.sort,                                                                                        "
            + "			t1.fix,                                                                                         "
            + "			t2.NAME table_name,                                                                             "
            + "			t2.page_code,                                                                                   "
            + "			t2.type,                                                                                        "
            + "			t2.start_column_index,                                                                          "
            + "			t2.staff_id,                                                                                    "
            + "			t1.is_enable,                                                                                   "
            + "			t1.is_delete,                                                                                   "
            + "         t3.max_sort,                                                                                    "
            + "         t3.min_sort                                                                                     "
            + "		FROM                                                                                                "
            + "			s_table_column_config t1                                                                        "
            + "			INNER JOIN s_table_config t2 ON t1.table_code = t2.CODE                                         "
            + "  	    INNER JOIN (                                                                                    "
            + "  	    	SELECT                                                                                      "
            + "  	    		max( subt1.sort ) AS max_sort,                                                          "
            + "  	    		min( subt1.sort ) AS min_sort,                                                          "
            + "  	    		subt2.staff_id,                                                                         "
            + "  	    		subt1.table_code                                                                        "
            + "  	    	FROM                                                                                        "
            + "  	    		s_table_column_config subt1                                                             "
            + "			    INNER JOIN s_table_config subt2 ON subt1.table_code = subt2.CODE                            "
            + "  	    		WHERE subt1.fix = FALSE                                                                 "
            + "  	    	GROUP BY                                                                                    "
            + "  	    		subt1.table_code,subt2.staff_id                                                         "
            + "  	    ) t3 ON t1.table_code = t3.table_code  and t2.staff_id = t3.staff_id                            "
            + "			WHERE TRUE                                                                                      "
            + "         AND t2.staff_id = #{p1.staff_id,jdbcType=INTEGER}                                               "
            + "         AND t2.page_code = #{p1.page_code,jdbcType=VARCHAR}                                             "
            + "         AND (t1.is_enable = #{p1.is_enable,jdbcType=BOOLEAN} or #{p1.is_enable,jdbcType=BOOLEAN} is null)"
            + "         ORDER BY t1.sort                                                                                "
            + "      ")
    List<STableColumnConfigVo> list(@Param("p1") STableColumnConfigVo searchCondition);

    /**
     * 查询列表
     */
    @Select("    "
            + "     DELETE t1                                                                                           "
            + "		FROM                                                                                                "
            + "			s_table_column_config t1                                                                        "
            + "			INNER JOIN s_table_config t2 ON t1.table_code = t2.CODE                                         "
            + "			WHERE TRUE                                                                                      "
            + "         AND t2.staff_id = #{p1.staff_id,jdbcType=INTEGER}                                               "
            + "         AND t2.page_code = #{p1.page_code,jdbcType=VARCHAR}                                             "
            + "      ")
    void delete(@Param("p1") STableColumnConfigVo searchCondition);

    /**
     * check用户数据是否与original数据一致
     */
    @Select("    "
    + " 		SELECT                                                                                                                        "
            + " 			tab1.count1 = tab2.count2 flag                                                                                            "
            + " 		FROM                                                                                                                          "
            + " 			(                                                                                                                         "
            + " 			SELECT                                                                                                                    "
            + " 				count( 1 ) count1                                                                                                     "
            + " 			FROM                                                                                                                      "
            + " 				s_table_column_config t1                                                                                              "
            + " 				INNER JOIN s_table_config t3 ON t1.table_id = t3.id                                                                   "
            + " 				INNER JOIN s_table_column_config_original t2 ON t1.NAME = t2.NAME                                                     "
            + " 				AND t1.label = t2.label                                                                                               "
            + " 				AND t1.fix = t2.fix                                                                                                   "
            + " 				AND t3.page_code = t2.page_code                                                                                       "
            + " 			WHERE                                                                                                                     "
            + " 				t3.staff_id = #{p1.staff_id,jdbcType=INTEGER}                                                                         "
            + " 				AND t3.page_code = #{p1.page_code,jdbcType=VARCHAR}                                                                   "
            + " 			) tab1,                                                                                                                   "
            + " 			( SELECT count( 1 ) count2 FROM s_table_column_config_original t1                                                         "
            + "                 WHERE t1.page_code = #{p1.page_code,jdbcType=VARCHAR} ) tab2                                                          "
            + "      ")
    Boolean check(@Param("p1") STableColumnConfigVo condition);

}
