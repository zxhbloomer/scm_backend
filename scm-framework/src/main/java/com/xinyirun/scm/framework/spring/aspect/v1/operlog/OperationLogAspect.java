package com.xinyirun.scm.framework.spring.aspect.v1.operlog;

import com.xinyirun.scm.bean.system.bo.log.operate.OperationDataBo;
import com.xinyirun.scm.bean.system.bo.session.user.system.UserSessionBo;
import com.xinyirun.scm.bean.entity.log.operate.SLogOperDetailEntity;
import com.xinyirun.scm.bean.entity.log.operate.SLogOperEntity;
import com.xinyirun.scm.bean.system.utils.servlet.ServletUtil;
import com.xinyirun.scm.bean.system.vo.sys.config.dict.SDictDataVo;
import com.xinyirun.scm.common.annotations.LogByIdAnnotion;
import com.xinyirun.scm.common.annotations.LogByIdsAnnotion;
import com.xinyirun.scm.common.annotations.OperationLogAnnotion;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.enums.OperationEnum;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.properies.SystemConfigProperies;
import com.xinyirun.scm.common.utils.annotation.AnnotationResolverUtil;
import com.xinyirun.scm.core.system.mapper.log.operate.SLogOperMapper;
import com.xinyirun.scm.core.system.service.log.operate.ISLogOperDetailService;
import com.xinyirun.scm.core.system.service.log.operate.ISLogOperService;
import com.xinyirun.scm.core.system.service.sys.config.dict.ISDictDataService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 操作日志
 */
@Aspect
@Component
@Slf4j
public class OperationLogAspect {

	@Autowired
	SLogOperMapper sLogOperMapper;

    @Autowired
    ISLogOperService isLogOperService;

    @Autowired
    ISDictDataService isDictDataService;

    @Autowired
    ISLogOperDetailService isLogOperDetailService;

	@Autowired
	private SystemConfigProperies systemConfigProperies;

	@Pointcut("@annotation(com.xinyirun.scm.common.annotations.OperationLogAnnotion)")
	public void pointcut() {
		// do nothing
	}

	@Around("pointcut()")
	public Object logAround(final ProceedingJoinPoint p) throws Throwable {
		MethodSignature signature = (MethodSignature) p.getSignature();
		Method method = signature.getMethod();
		OperationLogAnnotion operationlog = method.getAnnotation(OperationLogAnnotion.class);

		OperationEnum type = operationlog.type();
		if(operationlog.logByIds().length > 0){
			/**
			 * 更新、新增、逻辑删除、物理删除、拖拽
			 */
			return doOperationLogByIdsProcess(p, operationlog);
		}
		if(operationlog.logById().length > 0){
			/**
			 * 更新、新增、逻辑删除、物理删除、拖拽
			 */
			return doOperationLogByIdProcess(p, operationlog);
		}
		throw new BusinessException("操作日志发生错误：未找到相应的操作日志逻辑【"+ type.getName() + "、" + type.getCode()  +"】");
	}

	/**
	 * 更新，by id
	 * @param p
	 * @param operationlog
	 */
	public Object doOperationLogByIdProcess(final ProceedingJoinPoint p,final OperationLogAnnotion operationlog) {
		// 主表
		SLogOperEntity operEntity = new SLogOperEntity();
		operEntity.setName(operationlog.name());
		operEntity.setType(operationlog.type().getName());
		operEntity.setPlatform(SystemConstants.PLATFORM.PC);
		UserSessionBo bo = (UserSessionBo)ServletUtil.getUserSession();
		operEntity.setOper_id(bo.getStaff_Id());
		operEntity.setOper_name(bo.getStaff_info().getName());
		operEntity.setOper_time(LocalDateTime.now());
		Object[] args = p.getArgs();


		/** new 操作日志，执行过程bean */
		List<OperationDataBo> operationDataBoList = new ArrayList<>();
		/**
		 * 先获取旧值
		 */
		for(LogByIdAnnotion operationDetail : operationlog.logById()){
			// 参数
			Object paraId = AnnotationResolverUtil.newInstance().resolver(p, operationDetail.id());

			String[] cloum = operationDetail.cloums();
			StringBuilder sql = new StringBuilder();
			String sqlTemplate = "";
			String logTable = operationDetail.table_name();
			Long id = (Long)paraId;
			/**
			 * 获取表字段
			 */
			Map<String, Object> columnCommentMap = new LinkedHashMap<>();
			SDictDataVo searchCondition = new SDictDataVo();
			searchCondition.setIs_del(false);
			searchCondition.setTable_name(logTable);
			List<SDictDataVo> columnCommentList = isDictDataService.selectColumnComment(searchCondition);
			for (SDictDataVo col : columnCommentList) {
				columnCommentMap.put(col.getColumn_name(), col.getColumn_comment());
			}
			// 获取更新前数据
			if (cloum.length == 0) {
				List<String> list = new ArrayList<String>();
				for(Map.Entry<String, Object> entry : columnCommentMap.entrySet()) {
					list.add(entry.getKey());
				}
				cloum = list.toArray(new String[list.size()]);
			}
			sql.append("SELECT ");
			for (int i = 0; i < cloum.length; i++) {
				if (i == 0) {
					sql.append("`" + cloum[i] + "` ");
				} else {
					sql.append(",`" + cloum[i] + "` ");
				}
			}
			sqlTemplate = sql.toString();
			sql.append(" FROM " + logTable + " WHERE id=" + id );
			Map<String, Object> oldMap = sLogOperMapper.selectAnyTalbe(sql.toString());

			OperationDataBo obo = new OperationDataBo();
			obo.setName(operationDetail.name());
			obo.setType(operationDetail.type());
			obo.setOper_info(operationDetail.oper_info());
			obo.setCloums(cloum);
			obo.setTable_name(logTable);
			obo.setSqlTemplate(sqlTemplate);
			obo.setColumnCommentMap(columnCommentMap);
			obo.setArgs(args);
			obo.setOldData(oldMap);
			operationDataBoList.add(obo);
		}

		/**
		 * 执行逻辑
		 */
		Object result = null;
		try {
			result = p.proceed();
		} catch (Throwable e) {
			throw new BusinessException(e);
		}

		/**
		 * 保存主表
		 */
		isLogOperService.save(operEntity);

		// 定义计数器
		int i = 0;
		/**
		 * 再获取新值，进行比较
		 */
		for(OperationDataBo obo : operationDataBoList) {
			String sqlTemplate = obo.getSqlTemplate();
			String[] cloum = obo.getCloums();
			Map<String, Object> oldMap = obo.getOldData();
			Map<String, Object> columnCommentMap = obo.getColumnCommentMap();

			// 查询新值
			// 再取一次参数，考虑新增场合
			Object paraId = AnnotationResolverUtil.newInstance().resolver(p, operationlog.logById()[i].id());
			String sql = sqlTemplate + " FROM " + obo.getTable_name() + " WHERE id=" + paraId ;
			Map<String, Object> newMap = sLogOperMapper.selectAnyTalbe(sql);
			// 删除的场合，newMap会是null，所以需要其它方式创建
			if (newMap == null) {
				newMap = new ConcurrentHashMap<String, Object>();
				Map<String, Object> finalNewMap = newMap;
				obo.getColumnCommentMap().forEach((key, value) -> {
					finalNewMap.put(key, "");
				});
			}
			List<SLogOperDetailEntity> opds = new ArrayList<>();
			for (String clm : cloum) {
				Object oldValue = (oldMap == null ? null : oldMap.get(clm));
				Object newValue = newMap.get(clm);

				/**
				 * 	更新场合：根据配置文件，判断是否开启了全操作日志
				 * 	oldValue     |    newVlaue
				 * 	  null             null         =  相同
				 * 	  object           null         =  不同
				 * 	  null             object       =  不同
				 * 	  object           object       =   equals 比较
				 */
				if(!systemConfigProperies.isOperateLogAll()){
					if(Objects.equals(oldValue, newValue)){
						continue;
					}
				}

				// 从表设置
				SLogOperDetailEntity opd = new SLogOperDetailEntity();
				opd.setOper_id(operEntity.getId());
				opd.setName(obo.getName());
				opd.setType(obo.getType().getName());
				opd.setOper_info(obo.getOper_info());
				opd.setTable_name(obo.getTable_name());
				opd.setOld_val((oldMap == null ? null : ( oldValue == null ? null : oldValue.toString() )));
				opd.setNew_val(newValue == null ? null : newValue.toString());
				opd.setClm_name(clm);
				if(columnCommentMap.get(clm) == null) {
					opd.setClm_comment(null);
				} else {
					opd.setClm_comment(columnCommentMap.get(clm).toString());
				}
				opds.add(opd);
			}
			if (!opds.isEmpty()) {
				/**
				 * 判断是否开启了操作日志
				 */
				if(systemConfigProperies.isOperateLog()){
					isLogOperDetailService.saveBatch(opds, opds.size());
				}
			}
			i = i + 1;
		}

		return result;
	}

	/**
	 * 更新，by id
	 * @param p
	 * @param operationlog
	 */
	public Object doOperationLogByIdsProcess(final ProceedingJoinPoint p,final OperationLogAnnotion operationlog)
		throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		// 主表
		SLogOperEntity operEntity = new SLogOperEntity();
		operEntity.setName(operationlog.name());
		operEntity.setType(operationlog.type().getName());
		operEntity.setPlatform(SystemConstants.PLATFORM.PC);
		UserSessionBo bo = ServletUtil.getUserSession();
		operEntity.setOper_id(bo.getStaff_Id());
		operEntity.setOper_name(bo.getStaff_info().getName());
		operEntity.setOper_time(LocalDateTime.now());
		Object[] args = p.getArgs();

		/** new 操作日志，执行过程bean */
		List<OperationDataBo> operationDataBoList = new ArrayList<>();
		/**
		 * 先获取旧值
		 */
		for(LogByIdsAnnotion operationDetail : operationlog.logByIds()){
			String[] cloum = operationDetail.cloums();
			String logTable = operationDetail.table_name();
			/**
			 * 获取表字段
			 */
			Map<String, Object> columnCommentMap = new LinkedHashMap<>();
			SDictDataVo searchCondition = new SDictDataVo();
			searchCondition.setIs_del(false);
			searchCondition.setTable_name(logTable);
			List<SDictDataVo> columnCommentList = isDictDataService.selectColumnComment(searchCondition);
			for (SDictDataVo col : columnCommentList) {
				columnCommentMap.put(col.getColumn_name(), col.getColumn_comment());
			}
			// 获取更新前数据
			if (cloum.length == 0) {
				List<String> list = new ArrayList<String>();
				for(Map.Entry<String, Object> entry : columnCommentMap.entrySet()) {
					list.add(entry.getKey());
				}
				cloum = list.toArray(new String[list.size()]);
			}

			/**
			 * 获取参数 List<Bean>
			 */
			Class argsClass = args[operationDetail.id_position().getCode()].getClass();
			Method sizeMethod = argsClass.getDeclaredMethod("size", new Class[0]);
			// list的size
			int sizeValue = (int)sizeMethod.invoke(args[operationDetail.id_position().getCode()]);

			/**
			 *  循环一下，ids
			 * */
			for (int j = 0; j < sizeValue; j++) {
				StringBuilder sql = new StringBuilder();
				String sqlTemplate = "";

				// 获取list中的值
				Method getMethod = argsClass.getDeclaredMethod("get", int.class);
				Object bean = getMethod.invoke(args[operationDetail.id_position().getCode()],j);
				// 获取id
				Method getIdMethod = bean.getClass().getDeclaredMethod("getId");
				Long id = (Long)getIdMethod.invoke(bean);

				sql.append("SELECT ");
				for (int i = 0; i < cloum.length; i++) {
					if (i == 0) {
						sql.append("`" + cloum[i] + "` ");
					} else {
						sql.append(",`" + cloum[i] + "` ");
					}
				}
				sqlTemplate = sql.toString();
				sql.append(" FROM " + logTable + " WHERE id=" + id );
				Map<String, Object> oldMap = sLogOperMapper.selectAnyTalbe(sql.toString());

				OperationDataBo obo = new OperationDataBo();
				obo.setPara_position(operationDetail.id_position().getCode());
				obo.setIds_index(j);
				obo.setName(operationDetail.name());
				obo.setType(operationDetail.type());
				obo.setOper_info(operationDetail.oper_info());
				obo.setCloums(cloum);
				obo.setTable_name(logTable);
				obo.setSqlTemplate(sqlTemplate);
				obo.setColumnCommentMap(columnCommentMap);
				obo.setArgs(args);
				obo.setOldData(oldMap);
				operationDataBoList.add(obo);
			}
		}

		/**
		 * 执行逻辑
		 */
		Object result = null;
		try {
			result = p.proceed();
		} catch (Throwable e) {
			throw new BusinessException(e);
		}

		/**
		 * 保存主表
		 */
		isLogOperService.save(operEntity);

		// 定义计数器
		int i = 0;
		/**
		 * 再获取新值，进行比较
		 */
		for(OperationDataBo obo : operationDataBoList) {
			String sqlTemplate = obo.getSqlTemplate();
			String[] cloum = obo.getCloums();
			Map<String, Object> oldMap = obo.getOldData();
			Map<String, Object> columnCommentMap = obo.getColumnCommentMap();

			/**
			 * 获取参数 List<Bean>
			 */
			Class argsClass = args[obo.getPara_position()].getClass();
			// 获取list中的值
			Method getMethod = argsClass.getDeclaredMethod("get", int.class);
			Object bean = getMethod.invoke(args[obo.getPara_position()],obo.getIds_index());
			// 获取id
			Method getIdMethod = bean.getClass().getDeclaredMethod("getId");
			Long id = (Long)getIdMethod.invoke(bean);

			// 查询新值
			// 再取一次参数，考虑新增场合
			String sql = sqlTemplate + " FROM " + obo.getTable_name() + " WHERE id=" + id ;
			Map<String, Object> newMap = sLogOperMapper.selectAnyTalbe(sql);

			List<SLogOperDetailEntity> opds = new ArrayList<>();
			for (String clm : cloum) {
				Object oldValue = (oldMap == null ? null : oldMap.get(clm));
				Object newValue = newMap.get(clm);

				/**
				 * 	更新场合：根据配置文件，判断是否开启了全操作日志
				 * 	oldValue     |    newVlaue
				 * 	  null             null         =  相同
				 * 	  object           null         =  不同
				 * 	  null             object       =  不同
				 * 	  object           object       =   equals 比较
				 */
				if(!systemConfigProperies.isOperateLogAll()){
					if(Objects.equals(oldValue, newValue)){
						continue;
					}
				}

				// 从表设置
				SLogOperDetailEntity opd = new SLogOperDetailEntity();
				opd.setOper_id(operEntity.getId());
				opd.setName(obo.getName());
				opd.setType(obo.getType().getName());
				opd.setOper_info(obo.getOper_info());
				opd.setTable_name(obo.getTable_name());
				opd.setOld_val((oldMap == null ? null : ( oldValue == null ? null : oldValue.toString())));
				opd.setNew_val(newValue == null ? null : newValue.toString());
				opd.setClm_name(clm);
				opd.setClm_comment(columnCommentMap.get(clm).toString());
				opds.add(opd);
			}
			/**
			 * 判断是否开启了操作日志
			 */
			if(systemConfigProperies.isOperateLog()){
				isLogOperDetailService.saveBatch(opds, opds.size());
			}
			i = i + 1;
		}

		return result;
	}
}
