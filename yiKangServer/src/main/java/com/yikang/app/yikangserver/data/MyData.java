package com.yikang.app.yikangserver.data;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import android.util.SparseArray;

/**
 * 老人信息的数据项 SparseArray具有以下特征： 1.内部采用数组存储结构 2.key只能是int型
 * 3.内部按照key值从小到大对键值排序。查找时采用二分法
 */
public class MyData {
	
	public static final int DOCTOR = 0;
	public static final int NURSING = 1;
	public static final int THERAPIST = 2;
	
	public static final int FULL_TIME =1;
	public static final int PAER_TIME =0;

	// 性别
	public static final SparseArray<String> sexMap = new SparseArray<String>(2);
	// 证件类型
	public static final SparseArray<String> identTypeMap = new SparseArray<String>(
			4);
	// 民族
	public static final SparseArray<String> raceMap = new SparseArray<String>(2);
	// 宗教信仰
	public static final SparseArray<String> faithMap = new SparseArray<String>(
			6);
	// 和谁一起住
	public static final SparseArray<String> livWithMap = new SparseArray<String>(
			7);
	// 费用支付类型
	public static final SparseArray<String> payMentMap = new SparseArray<String>(
			4);
	// 收入来源
	public static final SparseArray<String> inCometSourceMap = new SparseArray<String>(
			7);
	// 房屋朝向
	public static final SparseArray<String> roomOritentationMap = new SparseArray<String>(
			8);
	// 是否有外窗
	public static final SparseArray<String> outWindowMap = new SparseArray<String>(
			2);

	public static final SparseArray<String> profeLeversMap = new SparseArray<String>(
			2);

	public static final SparseArray<String> professionMap = new SparseArray<String>(
			6);

	static {
		professionMap.put(DOCTOR, "医生");
		professionMap.put(NURSING, "护士");
		professionMap.put(THERAPIST, "康复师");


		profeLeversMap.put(PAER_TIME, "兼职");
		profeLeversMap.put(FULL_TIME, "全职");

	}

	static {
		sexMap.put(0, "女");
		sexMap.put(1, "男");

		identTypeMap.put(0, "身份证");
		identTypeMap.put(1, "护照");
		identTypeMap.put(2, "警官证");
		identTypeMap.put(3, "其他");

		raceMap.put(0, "汉族");
		raceMap.put(1, "其他");

		faithMap.put(-1, "无宗教信仰");
		faithMap.put(1, "基督教");
		faithMap.put(2, "佛教");
		faithMap.put(3, "道教");
		faithMap.put(4, "伊斯兰教");
		faithMap.put(5, "邪教威胁");

		livWithMap.put(0, "独居");
		livWithMap.put(1, "与配偶/伴侣居住");
		livWithMap.put(2, "与子女居住");
		livWithMap.put(3, "与子女居住");
		livWithMap.put(4, "与其他亲属居住");
		livWithMap.put(5, "与无亲属关系的人居住");
		livWithMap.put(6, "住养老机构");

		inCometSourceMap.put(0, "退休金");
		inCometSourceMap.put(1, "子女补贴");
		inCometSourceMap.put(2, "亲友资助");
		inCometSourceMap.put(3, "其他");

		payMentMap.put(0, "城镇职工医疗保险");
		payMentMap.put(1, "城镇居民医疗保险");
		payMentMap.put(2, "新型农村合作医疗");
		payMentMap.put(3, "商业医疗保险");
		payMentMap.put(4, "公费");
		payMentMap.put(5, "全自费");
		payMentMap.put(6, "其他");

		roomOritentationMap.put(0, "东");
		roomOritentationMap.put(1, "东南");
		roomOritentationMap.put(2, "南");
		roomOritentationMap.put(3, "西南");
		roomOritentationMap.put(4, "西");
		roomOritentationMap.put(5, "西北");
		roomOritentationMap.put(6, "北");
		roomOritentationMap.put(7, "东北");

		outWindowMap.put(0, "否");
		outWindowMap.put(1, "是");

	}

	/**
	 * 获得用于共用户选择的各个选项
	 * 
	 * @param array
	 * @return
	 */
	public static <T> List<T> getItems(SparseArray<T> array) {
		List<T> list = new ArrayList<T>();
		for (int i = 0; i < array.size(); i++) {
			list.add(array.valueAt(i));
		}
		return list;

	}

	/**
	 * 通过字符串的值或的对应的编号
	 * 
	 * @param value
	 * @param array
	 * @return
	 */
	public static <T> int getIntKey(T value, SparseArray<T> array) {
		int index = -100;
		for (int i = 0; i < array.size(); i++) {
			if (array.valueAt(i).equals(value)) {
				index = i;
				break;
			}
		}
		return array.keyAt(index);
	}

	/**
	 * 通过字符串的值或的对应的编号,使用自定义比较器
	 * 
	 * @param value
	 * @param array
	 * @return
	 */
	public static <T> int getIntKey(T value, SparseArray<T> array,
			Comparator<T> comparator) {
		int index = -100;
		if (comparator == null) {
			getIntKey(value, array);
		}
		for (int i = 0; i < array.size(); i++) {
			if (comparator.compare(array.valueAt(i), value) == 0) {
				index = i;
				break;
			}
		}
		return array.keyAt(index);
	}

	/**
	 * 考虑到数据的一致性，布尔值也用SparseArray表示
	 * 
	 * @param value
	 * @param array
	 * @return
	 */
	public static <T> boolean getBooleanKey(T value, SparseArray<T> array) {
		int key = getIntKey(value, array);
		return key == 1;
	}

	/**
	 * 根据key返回value
	 * 
	 * @param key
	 * @param array
	 * @return
	 */
	public static <T> T getValue(int key, SparseArray<T> array) {
		return array.get(key);
	}


}
