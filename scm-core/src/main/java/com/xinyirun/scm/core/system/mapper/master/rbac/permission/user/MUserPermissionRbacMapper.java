package com.xinyirun.scm.core.system.mapper.master.rbac.permission.user;

import com.xinyirun.scm.bean.system.bo.session.user.rbac.PermissionMenuBo;
import com.xinyirun.scm.bean.system.bo.session.user.rbac.PermissionMenuMetaBo;
import com.xinyirun.scm.bean.system.bo.session.user.rbac.PermissionOperationBo;
import com.xinyirun.scm.bean.system.bo.session.user.rbac.PermissionTopNavDetailBo;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.core.system.config.mybatis.typehandlers.PermissionMenuMetaBoTypeHandler;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @ClassName: 获取用户权限
 * @Author: zxh
 * @date: 2020/8/26
 * @Version: 1.0
 */
@Repository
public interface MUserPermissionRbacMapper {

    /**
     * 获取所有路由数据
     * @param menu_root_id
     * @return
     */
    @Select("                                                                                                        "
        + "        SELECT                                                                                            "
        + "               t1.id,                                                                                     "
        + "               t1.CODE,                                                                                   "
        + "               t1.page_code as NAME,                                                                      "
        + "               t1.path,                                                                                   "
        + "               t1.route_name,                                                                             "
        + "               t1.component,                                                                              "
        + "               t1.affix ,                                                                                 "
        + "               JSON_OBJECT('title',t1.meta_title,                                                         "
        + "                           'icon',t1.meta_icon,                                                           "
        + "                           'affix',t1.affix,                                                              "
        + "                           'name',t1.meta_title,                                                          "
        + "                           'page_code',t1.page_code,                                                      "
//        + "                           'active_topnav_index', t3.index) as meta                                       "
        + "                           'active_topnav_code',SUBSTRING( t3.CODE, 1, 8 ),                               "
        + "                           'active_topnav_index', SUBSTRING(t3.code,8,1)) as meta                         "
        + "          FROM m_permission_menu t1                                                                       "
        + "            left join (                                                                                   "
        + "                         SELECT                                                                           "
        + "                                subt1.id,                                                                 "
        + "                                subt1.code,                                                               "
        + "                                subt1.permission_id ,                                                     "
        + "                                ROW_NUMBER() OVER (PARTITION by permission_id  ORDER by subt1.CODE ) `index`    "
        + "                           FROM                                                                           "
        + "                                m_permission_menu subt1                                                   "
        + "                          WHERE subt1.type = 'T'                                                          "
//        + "                            and subt1.is_enable = true                                                    "
        + "                  ) t3 on t3.code = left(t1.code,8)                                                       "
        + "                      and t1.permission_id =t3.permission_id                                              "
        + "         where t1.type = '"+ DictConstant.DICT_SYS_MENU_TYPE_PAGE +"'                                     "
        + "           and t1.root_id = #{p2}                                                                         "
        + "           and EXISTS (                                                                                   "
        + "                        SELECT 1                                                                          "
        + "                 		 FROM  m_staff_org subt1                                                                  "
        + "                    INNER JOIN  m_role_position subt2 on subt1.serial_id = subt2.position_id                       "
        + "                    INNER JOIN  s_role subt3 on subt3.id = subt2.role_id                                           "
        + "                                         and subt3.is_del = " + DictConstant.DICT_SYS_DELETE_MAP_NO + "            "
        + "                    INNER JOIN  m_permission_role subt4 on subt2.role_id = subt4.role_id                           "
        + "                    INNER JOIN  m_permission subt5 on subt5.id = subt4.permission_id                               "
        + "                                               and subt5.is_del = " + DictConstant.DICT_SYS_DELETE_MAP_NO +  "     "
        + "                 	    WHERE  subt1.staff_id = #{p1}                                                             "
        + "                 		  AND  subt1.serial_type = '"+ DictConstant.DICT_SYS_CODE_TYPE_M_POSITION +"'             "
        + "                 		  and  subt5.id = t1.permission_id                                                        "
        + "     		 )                                                                               "
            + "     		GROUP BY t1.code                                                                               "
        + "                                                                                  ")
    @Results({
        @Result(property = "meta", column = "meta", javaType = PermissionMenuMetaBo.class, typeHandler = PermissionMenuMetaBoTypeHandler.class),
    })
    List<PermissionMenuBo> getAllRouters(@Param("p1") Long staff_id, @Param("p2") Long menu_root_id);

    /**
     * 获取所有路由数据
     * @param menu_root_id
     * @return
     */
    @Select("                                                                                                        "
        + "        SELECT                                                                                            "
        + "               CONCAT(t1.id,'') as id                                                                     "
        + "          FROM m_menu t1                                                                                  "
        + "         where t1.type = '"+ DictConstant.DICT_SYS_MENU_TYPE_NODE +"'                                     "
        + "           and t1.root_id = #{p1}                                                                         "
        + "                                                                                  ")
    List<String> getAllNodesId(@Param("p1") Long menu_root_id);

    /**
     * 按所有的顶部导航栏，并传入路径，找到相应的被选中的顶部导航栏
     * @param path
     * @return
     */
    @Select("                                                                                                                                       "
        + "   select tab1.*,                                                                                                                        "
        + "          CONCAT(@rownum := @rownum +1,'') AS `sort_index`,                                                                              "
        + "          SUBSTRING(tab1.code,8,1) as `index`                                                                                            "
        + "     from                                                                                                                                "
        + "         ( select t1.*,                                                                                                                  "
        + "                 t2.code active_code,                                                                                                    "
        + "                 JSON_OBJECT('title',t1.meta_title,                                                                                      "
        + "                             'icon',t1.meta_icon,                                                                                        "
        + "                             'top_nav_code',t1.code,                                                                                     "
        + "                             'name',t1.meta_title) as meta                                                                               "
        + "            from m_menu t1                                                                                                               "
        + "       left join (                                                                                                                       "
        + "                   select left( code, 8 ) code                                                                                           "
        + "                     from m_menu                                                                                                         "
        + "                    where path = #{p2}                                                                                                   "
        + "                 )  t2 on t1.code = t2.code                                                                                              "
        + "                                                                                                               "
        + "           where t1.type = '"+ DictConstant.DICT_SYS_MENU_TYPE_TOPNAV +"'                                                                "
        + "             and t1.root_id = #{p1}                                                                                                      "
        + "             and exists (                                                                                                                "
        + "                        select *                                                                                                         "
        + "            				 from (                                                                                                         "
        + "            		               select  subt0.root_id,                                                                                   "
        + "            		               	       subt0.menu_id                                                                                    "
        + "            	                     from  m_permission_menu subt0                                                                          "
        + "            	                    where  subt0.root_id = #{p1}                                                                            "
        + "            		               	  and subt0.type = '"+ DictConstant.DICT_SYS_MENU_TYPE_TOPNAV +"'                                       "
        + "            	                      and EXISTS (                                                                                          "
        + "            	                          SELECT subt5.id                                                                                   "
        + "            	                    		FROM  m_staff_org subt1                                                                         "
        + "                                   INNER JOIN  m_role_position subt2 on subt1.serial_id = subt2.position_id                              "
        + "                                   INNER JOIN  s_role subt3 on subt3.id = subt2.role_id                                                  "
        + "                                                           and subt3.is_del = " + DictConstant.DICT_SYS_DELETE_MAP_NO + "                "
        + "                                   INNER JOIN  m_permission_role subt4 on subt2.role_id = subt4.role_id                                  "
        + "                                   INNER JOIN  m_permission subt5 on subt5.id = subt4.permission_id                                      "
        + "                                                                 and subt5.is_del = " + DictConstant.DICT_SYS_DELETE_MAP_NO + "          "
        + "            	                    	   WHERE  subt1.staff_id = #{p3}                                                                    "
        + "            	                    		 AND  subt1.serial_type = 'm_position'                                                          "
        + "            	                    		 and  subt5.id = subt0.permission_id                                                            "
        + "            		               				)                                                                                           "
        + "            		                  	GROUP BY subt0.CODE                                                                                 "
        + "            							) tab                                                                                               "
        + "            				 where tab.root_id = t1.root_id                                                                                 "
        + "            				   and tab.menu_id = t1.id                                                                                      "
        + "            		)                                                                                                                       "
        + "        order by t1.code                                                                                                                 "
        + "         ) as tab1,                                                                                                                      "
        + "      (SELECT @rownum := 0) tab2                                                                                                         "
        + "                                                                                                                                         "
        + "                                                                                      ")
    @Results({
        @Result(property = "meta", column = "meta", javaType = PermissionMenuMetaBo.class, typeHandler = PermissionMenuMetaBoTypeHandler.class),
    })
    List<PermissionTopNavDetailBo> getTopNavByPath(@Param("p1")Long menu_root_id, @Param("p2")String path , @Param("p3") Long staff_id, @Param("p4") String topNavCode);

    /**
     * 搜索permission_menu_id
     * @param staff_id
     * @return
     */
    @Select("                                                                                                           "
            + "      			SELECT distinct t6.root_id                                                              "
            + "      			  FROM  m_staff_org t1                                                                  "
            + "        INNER JOIN  m_role_position t2 on t1.serial_id = t2.position_id                                  "
            + "        INNER JOIN  s_role t3 on t3.id = t2.role_id                                                      "
            + "                             and t3.is_del = " + DictConstant.DICT_SYS_DELETE_MAP_NO + "                 "
            + "      	INNER JOIN  m_permission_role t4 on t2.role_id = t4.role_id                                     "
            + "        INNER JOIN  m_permission t5 on t5.id = t4.permission_id                                          "
            + "                                   and t5.is_del = " + DictConstant.DICT_SYS_DELETE_MAP_NO + "           "
            + "      	INNER JOIN  m_permission_menu t6 on t6.permission_id = t5.id                                    "
            + "      	   	 WHERE  t1.staff_id = #{p1}                                                                 "
            + "      		   AND  t1.serial_type = '"+ DictConstant.DICT_SYS_CODE_TYPE_M_POSITION +"'                 "
            + "                                                                                                         ")
    Long getPermissionMenuRootId( @Param("p1") Long staff_id );

    /**
     * 按所有的顶部导航栏
     * @return
     */
    @Select("                                                                                                                                       "
        + "   select                                                                                                                                "
        + "          tab1.*,                                                                                                                        "
//        + "          CONCAT(@rownum := @rownum +1,'') AS `index`                                                                                    "
        + "          SUBSTRING(tab1.code,8,1) as `index`                                                                                            "
        + "    from (                                                                                                                               "
        + "          select t1.*,                                                                                                                   "
        + "                 JSON_OBJECT('title',t1.meta_title,                                                                                      "
        + "                             'icon',t1.meta_icon,                                                                                        "
        + "                             'top_nav_code', t1.code,                                                                                        "
        + "                             'name',t1.meta_title) as meta                                                                               "
        + "            from m_menu t1                                                                                                               "
        + "           where t1.type = '"+ DictConstant.DICT_SYS_MENU_TYPE_TOPNAV +"'                                                                "
        + "             and t1.root_id = #{p1}                                                                                                      "
        + "             and exists (                                                                                                                "
        + "                        select *                                                                                                         "
        + "            				 from (                                                                                                         "
        + "            		               select  subt0.root_id,                                                                                   "
        + "            		               	       subt0.menu_id                                                                                    "
        + "            	                     from  m_permission_menu subt0                                                                          "
        + "            	                    where  subt0.root_id = #{p1}                                                                            "
        + "            		               	  and subt0.type = '"+ DictConstant.DICT_SYS_MENU_TYPE_TOPNAV +"'                                       "
        + "            	                      and EXISTS (                                                                                          "
        + "            	                          SELECT subt5.id                                                                                   "
        + "            	                    		FROM  m_staff_org subt1                                                                         "
        + "                                   INNER JOIN  m_role_position subt2 on subt1.serial_id = subt2.position_id                              "
        + "                                   INNER JOIN  s_role subt3 on subt3.id = subt2.role_id                                                  "
        + "                                                           and subt3.is_del = " + DictConstant.DICT_SYS_DELETE_MAP_NO + "                "
        + "                                   INNER JOIN  m_permission_role subt4 on subt2.role_id = subt4.role_id                                  "
        + "                                   INNER JOIN  m_permission subt5 on subt5.id = subt4.permission_id                                      "
        + "                                                                 and subt5.is_del = " + DictConstant.DICT_SYS_DELETE_MAP_NO + "          "
        + "            	                    	   WHERE  subt1.staff_id = #{p2}                                                                    "
        + "            	                    		 AND  subt1.serial_type = 'm_position'                                                          "
        + "            	                    		 and  subt5.id = subt0.permission_id                                                            "
        + "            		               				)                                                                                           "
        + "            		                  	GROUP BY subt0.CODE                                                                                 "
        + "            							) tab                                                                                               "
        + "            				 where tab.root_id = t1.root_id                                                                                 "
        + "            				   and tab.menu_id = t1.id                                                                                      "
        + "            		)                                                                                                                       "
        + "        order by t1.code                                                                                                                 "
        + "       )  as tab1                                                                                                                       "
//        + "        (SELECT  @rownum := 0) tab2                                                                                                      "
        + "                                                                                                                                         "
        + "                                                                                                                                         ")
    @Results({
        @Result(property = "meta", column = "meta", javaType = PermissionMenuMetaBo.class, typeHandler = PermissionMenuMetaBoTypeHandler.class),
    })
    List<PermissionTopNavDetailBo> getTopNav(@Param("p1")Long menu_root_id, @Param("p2") Long staff_id, @Param("p3") String topNavCode);

    /**
     * 获取系统菜单
     * @param top_nav_code
     * @return
     */
    @Select("                                                                                                        "
        + "                    WITH recursive tab1 AS (                                                              "
        + "                        SELECT                                                                            "
        + "                               t0.id AS menu_id,                                                          "
        + "                               t0.parent_id,                                                              "
        + "                               1 LEVEL,                                                                   "
        + "                               t0.NAME,                                                                   "
        + "                               t0.NAME AS depth_name,                                                     "
        + "                               cast(t0.id AS CHAR ( 50 )) depth_id                                        "
        + "                          FROM m_menu t0                                                                  "
        + "                         WHERE t0.parent_id IS NULL                                                       "
        + "                        UNION ALL                                                                         "
        + "                        SELECT                                                                            "
        + "                               t2.id AS menu_id,                                                          "
        + "                               t2.parent_id,                                                              "
        + "                               t1.LEVEL + 1 AS LEVEL,                                                     "
        + "                               t2.NAME,                                                                   "
        + "                               CONCAT( t1.depth_name, '>', t2.NAME ) depth_name,                          "
        + "                               CONCAT(                                                                    "
        + "                                   cast(                                                                  "
        + "                                   t1.depth_id AS CHAR ( 50 )),                                           "
        + "                                   ',',                                                                   "
        + "                                   cast(                                                                  "
        + "                                   t2.id AS CHAR ( 50 ))) depth_id                                        "
        + "                          FROM m_menu t2,                                                                 "
        + "                               tab1 t1                                                                    "
        + "                         WHERE t2.parent_id = t1.menu_id                                                  "
        + "                        )                                                                                 "
        + "                SELECT                                                                                    "
        + "                       t2.id,                                                                             "
        + "                       t2.is_default,                                                                     "
        + "                       t1.menu_id,                                                                        "
        + "                       t1.menu_id AS `value`,                                                             "
        + "                       t1.parent_id,                                                                      "
        + "                       t2.root_id,                                                                        "
        + "                       t1.LEVEL,                                                                          "
        + "                       t2.type,                                                                           "
        + "                       t2.page_id,                                                                        "
        + "                       t2.page_code,                                                                      "
        + "                       t2.path,                                                                           "
        + "                       t2.route_name,                                                                     "
        + "                       t2.affix,                                                                          "
        + "                       JSON_OBJECT('title',t2.meta_title,                                                 "
        + "                                   'icon',t2.meta_icon,                                                   "
        + "                                   'affix',t2.affix,                                                      "
        + "                                   'name',t2.meta_title) as meta                                          "
        + "                  FROM tab1 t1                                                                            "
        + "            INNER JOIN m_menu t2 ON t1.menu_id = t2.id                                                    "
        + "                 WHERE TRUE                                                                               "
        + "                   AND t2.code like CONCAT (#{p2},'%')                                                    "
        + "                   AND t2.code <> #{p2}                                                                   "
        + "              ORDER BY t2.CODE;                                                                           "
        + "                ")
    @Results({
        @Result(property = "meta", column = "meta", javaType = PermissionMenuMetaBo.class, typeHandler = PermissionMenuMetaBoTypeHandler.class),
    })
    List<PermissionMenuBo> getSystemMenu(@Param("p2") String top_nav_code);

    /**
     * 获取菜单权限
     * @param staff_id
     * @return
     */
//    @Select("                                                                                                            "
//        + "             SELECT                                                                                           "
//        + "                	   t2.id,                                                                                    "
//        + "                	   t2.is_enable,                                                                             "
//        + "                	   t2.is_default,                                                                            "
//        + "                	   t1.menu_id,                                                                               "
//        + "                	   t1.menu_id AS `value`,                                                                    "
//        + "                	   t1.parent_id,                                                                             "
//        + "                	   t2.root_id,                                                                               "
//        + "                	   t1.LEVEL,                                                                                 "
//        + "                	   t2.type,                                                                                  "
//        + "                	   t2.visible,                                                                               "
//        + "                	   t2.page_id,                                                                               "
//        + "                	   t2.page_code,                                                                             "
//        + "                	   t2.route_name,                                                                            "
//        + "                	   t2.meta_title,                                                                            "
//        + "                	   t2.meta_icon,                                                                             "
//        + "                	   t2.component,                                                                             "
//        + "                	   t2.affix,                                                                                 "
//        + "                    JSON_OBJECT('title',t2.meta_title,                                                        "
//        + "                              'icon',t2.meta_icon,                                                            "
//        + "                              'affix',t2.affix,                                                               "
//        + "                              'name',t2.meta_title,                                                           "
//        + "                              'page_code',t2.page_code) as meta                                               "
//        + "               FROM                                                                                           "
//        + "                	   v_permission_tree t1                                                                      "
//        + "         INNER JOIN m_permission_menu t2 ON t1.menu_id = t2.menu_id                                           "
//        + "                AND t1.permission_id = t2.permission_id                                                       "
//        + "         INNER JOIN m_permission t3 on t3.id = t2.permission_id                                               "
//        + "         INNER JOIN m_staff t4 on t3.serial_type = '" + DictConstant.DICT_ORG_SETTING_TYPE_DEPT_SERIAL_TYPE + "' "
//        + "         	   and t3.serial_id = t4.dept_id                                                                 "
//        + "         	   and t4.id = #{p1}                                                                             "
//        + "                and t3.`is_enable` = true                                                                     "
//        + "              WHERE TRUE                                                                                      "
//        + "                AND ( t2.is_enable = true )                                                                   "
//        + "                AND t2.code like CONCAT (#{p3},'%')                                                           "
//        + "                AND t2.code <> #{p3}                                                                          "
//        + "           ORDER BY t2.CODE                                                                                   "
//        + "                ")
//    @Results({
//        @Result(property = "meta", column = "meta", javaType = PermissionMenuMetaBo.class, typeHandler = com.xinyirun.scm.core.config.mybatis.typehandlers.PermissionMenuMetaBoTypeHandler.class),
//    })
//    List<PermissionMenuBo> getPermissionMenu(@Param("p1") Long staff_id, @Param("p3") String top_nav_code);

    @Select("                                                                                                            "
            + "             SELECT                                                                                           "
            + "                	   t0.code,                                                                                  "
            + "                	   t2.id,                                                                                    "
            + "                	   t2.path,                                                                                  "
            + "                	   t2.is_enable,                                                                             "
            + "                	   t2.is_default,                                                                            "
            + "                	   t2.menu_id,                                                                               "
            + "                	   t2.menu_id AS `value`,                                                                    "
            + "                	   t2.parent_id,                                                                             "
            + "                	   t2.root_id,                                                                               "
            + "                	   t0.LEVEL,                                                                                  "
            + "                	   t2.type,                                                                                  "
            + "                	   t2.visible,                                                                               "
            + "                	   t2.page_id,                                                                               "
            + "                	   t2.page_code,                                                                             "
            + "                	   t2.route_name,                                                                            "
            + "                	   t2.meta_title,                                                                            "
            + "                	   t2.meta_icon,                                                                             "
            + "                	   t2.component,                                                                             "
            + "                	   t2.affix,                                                                                 "
            + "                    JSON_OBJECT('title',t2.meta_title,                                                        "
            + "                              'icon',t2.meta_icon,                                                            "
            + "                              'affix',t2.affix,                                                               "
            + "                              'name',t2.meta_title,                                                           "
            + "                              'page_code',t2.page_code) as meta                                               "
            + "               FROM v_menu_tree t0                                                                            "
            + "         INNER JOIN (                                                                                         "
            + "        		     	select t0.*                                                                              "
            + "        		     		from m_permission_menu t0                                                            "
            + "        		     	 where t0.root_id = #{p3}                                                                "
            + "        		     		 and EXISTS (                                                                        "
            + "        		               SELECT t5.id                                                                      "
            + "        		     			 FROM  m_staff_org t1                                                            "
            + "                        INNER JOIN  m_role_position t2 on t1.serial_id = t2.position_id                       "
            + "                        INNER JOIN  s_role t3 on t3.id = t2.role_id                                           "
            + "                                             and t3.is_del = " + DictConstant.DICT_SYS_DELETE_MAP_NO + "      "
            + "                        INNER JOIN  m_permission_role t4 on t2.role_id = t4.role_id                           "
            + "                        INNER JOIN  m_permission t5 on t5.id = t4.permission_id                               "
            + "                                                   and t5.is_del = " + DictConstant.DICT_SYS_DELETE_MAP_NO + "     "
            + "        		     		    WHERE  t1.staff_id = #{p1}                                                       "
            + "        		     			  AND  t1.serial_type = '"+ DictConstant.DICT_SYS_CODE_TYPE_M_POSITION +"'       "
            + "        		     			  and  t5.id = t0.permission_id                                                  "
            + "        						 )                                                                               "
            + "                     ) t2 on t2.code = t0.code                                                                "
            + "              WHERE TRUE                                                                                      "
            + "                AND t0.code like CONCAT (#{p2,jdbcType=VARCHAR},'%')                                          "
            + "                AND t0.code <> #{p2}                                                                          "
            + "        	 GROUP BY t0.code                                                                                    "
            + "           ORDER BY t0.CODE                                                                                   "
            + "                ")
    @Results({
            @Result(property = "meta", column = "meta", javaType = PermissionMenuMetaBo.class, typeHandler = PermissionMenuMetaBoTypeHandler.class),
    })
    List<PermissionMenuBo> getPermissionMenu(@Param("p1") Long staff_id, @Param("p2") String top_nav_code, @Param("p3") Long menu_root_id);
    /**
     * 获取权限操作数据
     * @param staff_id
     * @return
     */
    @Select("                                                                   "
        + "            SELECT  distinct                                                                                                "
        + "                    t6.page_id,                                                                                             "
        + "                    t6.`code` as page_code,                                                                                 "
        + "                    t6.`name` as page_name,                                                                                 "
        + "                    t6.component as page_path,                                                                              "
        + "                    t6.perms,                                                                                               "
        + "                    t6.meta_title,                                                                                          "
        + "                    t6.meta_icon,                                                                                           "
        + "                    t7.perms as operation_perms,                                                                            "
        + "                    t7.descr as operation_descr                                                                             "
        + "              FROM  m_staff_org t1                                                                                          "
        + "        INNER JOIN  m_role_position t2 on t1.serial_id = t2.position_id                                                     "
        + "        INNER JOIN  s_role t3 on t3.id = t2.role_id                                                                         "
        + "                             and t3.is_del = " + DictConstant.DICT_SYS_DELETE_MAP_NO + "                                    "
        + "        INNER JOIN  m_permission_role t4 on t2.role_id = t4.role_id                                                         "
        + "        INNER JOIN  m_permission t5 on t5.id = t4.permission_id                                                             "
        + "                                   and t5.is_del = " + DictConstant.DICT_SYS_DELETE_MAP_NO + "                              "
        + "        INNER JOIN  m_permission_pages t6 on t6.permission_id = t5.id                                                       "
        + "        INNER JOIN  m_permission_operation t7 ON t7.permission_page_id = t6.id                                                    "
        + "                                             AND t7.permission_id = t6.permission_id                                        "
        + "             WHERE  t1.staff_id = #{p1}                                                                                     "
        + "               AND  t1.serial_type = '"+ DictConstant.DICT_SYS_CODE_TYPE_M_POSITION +"'                                     "
        + "          ORDER BY  t6.page_id                                                                                              "
        + "                                                                                 ")
    List<PermissionOperationBo> getPermissionOperation(@Param("p1") Long staff_id);

    /**
     * 获取redirect数据
     * @return
     */
    @Select("                                                                                 "
        + "          select t1.*,                                                             "
        + "                 JSON_OBJECT('title',t3.meta_title,                                "
        + "                           'icon',t1.meta_icon,                                    "
        + "                           'affix',t1.affix,                                       "
        + "                           'name',t1.meta_title) as meta                           "
        + "            from m_menu as t1,                                                     "
        + "                 m_menu_redirect as t2 ,                                           "
        + "                 s_pages t3                                                        "
        + "           where t2.menu_page_id = t1.id                                           "
        + "             and t1.page_id = t3.id                                                "
        + "      ")
    @Results({
        @Result(property = "meta", column = "meta", javaType = PermissionMenuMetaBo.class, typeHandler = PermissionMenuMetaBoTypeHandler.class),
    })
    PermissionMenuBo getRedirectData();
}
