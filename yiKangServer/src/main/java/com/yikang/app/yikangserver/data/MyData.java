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
	// 城市
	public static final SparseArray<City> cityMap = new SparseArray<City>(2);

	
	public static final SparseArray<String> profeLeversMap = new SparseArray<String>(
			2);

	public static final SparseArray<String> professionMap = new SparseArray<String>(
			6);

	public static final SparseArray<String> departmentMap = new SparseArray<String>(
			100);
	static {
		professionMap.put(DOCTOR, "医生");
		professionMap.put(NURSING, "护士");
		professionMap.put(THERAPIST, "康复师");


		profeLeversMap.put(PAER_TIME, "兼职");
		profeLeversMap.put(FULL_TIME, "全职");

		/**
		 * 呼吸内科、消化内科、神经内科、心血管内科、肾内科、血液内科、 普外科、神经外科、心胸外科、泌尿外科、心血管外科、肝胆外科、肛肠外科、骨外科
		 * 肿瘤内科、肿瘤外科、骨肿瘤科、肿瘤康复科、肿瘤综合科 中医全科、中医内科、中医外科、中医保健科、针灸按摩科、中医骨伤科、中医肿瘤科
		 * 、肝病科、结核病、 精神科、心理咨询科 、营养科
		 */

		departmentMap.put(0, "呼吸内科");
		departmentMap.put(1, "消化内科");
		departmentMap.put(2, "神经内科");

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

		// 北京市区
		final SparseArray<String> beijingArea = new SparseArray<String>(6);
		// 北京市区
		final SparseArray<String> beijingCode = new SparseArray<String>(6);
		
		beijingArea.put(0, "东城区");
		beijingArea.put(1, "西城区");
		beijingArea.put(2, "朝阳区");
		beijingArea.put(3, "海淀区");
		beijingArea.put(4, "丰台区");
		beijingArea.put(5, "石景山区");
		beijingArea.put(6, "门头沟区");
		beijingArea.put(7, "房山区");
		beijingArea.put(8, "通州区");
		beijingArea.put(9, "顺义区");
		beijingArea.put(10, "昌平区");
		beijingArea.put(11, "大兴区");
		beijingArea.put(12, "怀柔区");
		beijingArea.put(13, "平谷区");
		beijingArea.put(14, "密云县");
		beijingArea.put(15, "延庆县");
		
		beijingCode.put(0, "110101");
		beijingCode.put(1, "110102");
		beijingCode.put(2, "110105");
		beijingCode.put(3, "110108");
		beijingCode.put(4, "110106");
		beijingCode.put(5, "110107");
		beijingCode.put(6, "110109");
		beijingCode.put(7, "110111");
		beijingCode.put(8, "110112");
		beijingCode.put(9, "110113");
		beijingCode.put(10, "110114");
		beijingCode.put(11, "110115");
		beijingCode.put(12, "110116");
		beijingCode.put(13, "110117");
		beijingCode.put(14, "110228");
		beijingCode.put(15, "110229");

		City beijing = new City("北京", "110000",beijingArea,beijingCode);
		cityMap.put(0, beijing);

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

	/**
	 * 城市
	 * 
	 */
	public static class City {
		private final String name;
		private final String cityCode;
		private final SparseArray<String> areas; // 区列表
		private final SparseArray<String> areaCodes;
		
		public City(String name, String cityCode,SparseArray<String> areas,SparseArray<String> areaCodes) {
			this.areas = areas;
			this.name = name;
			this.areaCodes =areaCodes;
			this.cityCode = cityCode;
		}
		
		public String getCityCode(){
			return cityCode;
		}
		
		public String getName() {
			return name;
		}

		public List<String> getListArea() {
			return getItems(areas);
		}

		public SparseArray<String> getSparseArea() {
			return areas;
		}

		public int getAreaIntkey(String value) {
			return getIntKey(value, areas);
		}
		
		public String getAreaValue(int key) {
			return areas.get(key);
		}
		
		
		public String getAreaCodeValue(int key){
			return areaCodes.get(key);
		}
	}
}
