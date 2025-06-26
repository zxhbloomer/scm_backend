package com.xinyirun.scm.common.utils.bean;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 *  封装调用Spring的BeanUtils进行bean的复制操作
 * @author zxh
 */
@Component
@Slf4j
public class BeanUtilsSupport implements BeanFactoryPostProcessor {

	/** Spring应用上下文环境 */
	private static ConfigurableListableBeanFactory beanFactory;

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		BeanUtilsSupport.beanFactory = beanFactory;
	}

	/**
	 * 复制Ao对象的内容至同等的Ao中
	 * @param source Object 被复制Ao对象
	 * @param clazz Class 复制到Ao的class
	 * @return 返回复制到Ao的对象
	 */
	public static Object copyProperties(Object source, Class clazz) {
		Object object = null;
		if (source != null) {
			try {
				object = clazz.newInstance();
			} catch (Exception e) {
				log.error("copyProperties error:", e);
			}
			copyProperties(source, object, null, null);
		}
		return object;
	}

	/**
	 * 复制Ao对象内容至同等的Ao中(拥有editable中属性才会被复制)
	 * @param source Object 被复制Ao对象
	 * @param  clazz 复制到Ao
	 * @param editable Class 拥有editable中属性才会被复制
	 */
	public static Object copyProperties(Object source, Class clazz, Class editable) {
		Object object = null;
		if (source != null) {
			try {
				object = clazz.newInstance();
			} catch (Exception e) {
				log.error("copyProperties error:", e);
			}
			copyProperties(source, object, editable, null);
		}
		return object;
	}

	/**
	 * 复制Ao对象的内容至同等的Ao中
	 * @param source Object 被复制Ao对象
	 * @param target Object 复制到Ao
	 */
	public static void copyProperties(Object source, Object target) {
		copyProperties(source, target, null, null);
	}

	/**
	 * 复制Ao对象内容至同等的Ao中(排除ignoreProperties中属性)
	 * @param source Object 被复制Ao对象
	 * @param target Object 复制到Ao
	 * @param ignoreProperties String[] 排除ignoreProperties中属性
	 */
	public static void copyProperties(Object source, Object target, String[] ignoreProperties) {
		copyProperties(source, target, null, ignoreProperties);
	}

	/**
	 * 复制Ao对象内容至同等的Ao中(拥有editable中属性才会被复制)
	 * @param source Object 被复制Ao对象
	 * @param target Object 复制到Ao
	 * @param editable Class 拥有editable中属性才会被复制
	 */
	public static void copyProperties(Object source, Object target, Class editable) {
		copyProperties(source, target, editable, null);
	}

	/**
	 * 复制集合中的对象到同等集合对象集合中
	 * @param list List 被复制Ao对象集合
	 * @param clazz Class 复制到Ao的class
	 * @return 返回复制到Ao的对象集合
	 */
	public static List copyProperties(List list, Class clazz) {
		List targetList = new ArrayList();
		try {
			for (int i = 0; i < list.size(); i++) {
				Object object = clazz.newInstance();
				Object source = list.get(i);
				copyProperties(source, object, null, null);
				targetList.add(object);
			}
		} catch (Exception e) {
			log.error("copyProperties error:", e);
		}
		return targetList;
	}

	/**
	 * 复制集合中的对象到同等集合对象集合中(拥有editable中属性才会被复制)
	 * @param list List 源Ao对象的集合
	 * @param clazz Class 目标Ao对象的集合
	 * @param editable Class 拥有editable中属性才会被复制
	 * @return 返回clazz对象集合
	 */
	public static List copyProperties(List list, Class clazz, Class editable) {
		List targetList = new ArrayList();
		try {
			for (int i = 0; i < list.size(); i++) {
				Object object = clazz.newInstance();
				Object source = list.get(i);
				copyProperties(source, object, editable, null);
				targetList.add(object);
			}
		} catch (Exception e) {
			log.error("copyProperties error:", e);
		}
		return targetList;
	}

	/**
	 * 复制集合中的对象到同等集合对象集合中(排除ignoreProperties中属性)
	 * @param list List 源Ao对象的集合
	 * @param clazz Class 目标Ao对象的集合
	 * @param ignoreProperties String 排除ignoreProperties中属性
	 * @return 返回clazz对象集合
	 */
	public static List copyProperties(List list, Class clazz, String[] ignoreProperties) {
		List targetList = new ArrayList();
		try {
			for (int i = 0; i < list.size(); i++) {
				Object object = clazz.newInstance();
				Object source = list.get(i);
				copyProperties(source, object, null, ignoreProperties);
				targetList.add(object);
			}
		} catch (Exception e) {
			log.error("copyProperties error:", e);
		}
		return targetList;
	}

	/**
	 * 复制Ao对象内容至同等的Ao中
	 * @param source Object 被复制Ao对象
	 * @param target Object 复制到Ao
	 * @param editable Class 拥有editable中属性才会被复制
	 * @param ignoreProperties String[] 被排除的Ao中属性
	 */
	private static void copyProperties(Object source, Object target, Class editable, String[] ignoreProperties) {
		try {
			if (editable == null) {
				BeanUtils.copyProperties(source, target, ignoreProperties);
			} else if (ignoreProperties == null) {
				BeanUtils.copyProperties(source, target, editable);
			}
		} catch (Exception e) {
			log.error("copyProperties error:", e);
		}
	}

	/**
	 * 获取类型为requiredType的对象
	 *
	 * @param clz
	 * @return
	 * @throws org.springframework.beans.BeansException
	 */
	public static <T> T getBean(Class<T> clz) throws BeansException {
		T result = (T)beanFactory.getBean(clz);
		return result;
	}
}
