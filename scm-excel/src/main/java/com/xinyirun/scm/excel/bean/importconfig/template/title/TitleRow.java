package com.xinyirun.scm.excel.bean.importconfig.template.title;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 标题模板：行
 * @author zxh
 */
public class TitleRow {

	/**
	 * 列
	 */
	@Setter
	@Getter
	@JSONField
	private List<TitleCol> cols = new ArrayList<>();

	/**
	 * 构造函数
	 */
	public TitleRow() {
		this(null);
	}

	/**
	 * 构造函数
	 */
	public TitleRow(String[] titles) {
		if (titles != null) {
			addCol(titles);
		}
	}

	public boolean hasSpanCol() {
		for (TitleCol titleCol : cols) {
			if (titleCol.getColSpan() > 1) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 添加列
	 * @param title
	 * @param colSpan
	 */
	public void addCol(String title, int colSpan) {
		cols.add(new TitleCol(title, colSpan));
		if (colSpan > 1) {
			for (int i = 0; i < colSpan - 1; i++) {
				cols.add(new DummyTitleCol());
			}
		}
	}

	/**
	 * 添加列
	 */
	public void addCol(String... titles) {
		for (int i = 0; i < titles.length; i++) {
			cols.add(new TitleCol(titles[i]));
		}
	}

	/**
	 * 列数
	 * @return
	 */
	public int colSize() {
		return cols.size();
	}

	public TitleCol getCol(int col) {
		return cols.get(col);
	}

}
