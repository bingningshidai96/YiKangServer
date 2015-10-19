package com.yikang.app.yikangserver.data;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import android.util.SparseArray;
/**
 * 老人信息的数据项
 * SparseArray具有以下特征：
 * 		1.内部采用数组存储结构
 * 		2.key只能是int型
 * 		3.内部按照key值从小到大对键值排序。查找时采用二分法
 * @author LGhui
 *
 */
public class MyData {
	//性别
	public static  final SparseArray<String> sexMap = new SparseArray<String>(2);
	//证件类型
	public static  final SparseArray<String> identTypeMap =  new SparseArray<String>(4);
	//民族
	public static  final SparseArray<String> raceMap =  new SparseArray<String>(2);
	//宗教信仰
	public static  final SparseArray<String> faithMap = new SparseArray<String>(6); 
	//和谁一起住
	public static  final SparseArray<String> livWithMap = new SparseArray<String>(7);
	//费用支付类型
	public static  final SparseArray<String> payMentMap = new SparseArray<String>(4);
	//收入来源
	public static  final SparseArray<String> inCometSourceMap= new SparseArray<String>(7);
	//房屋朝向
	public static  final SparseArray<String> roomOritentationMap= new SparseArray<String>(8);
	//是否有外窗
	public static  final SparseArray<String> outWindowMap= new SparseArray<String>(2);
	//城市
	public static  final SparseArray<City> cityMap= new SparseArray<City>(2);
	
	
	//
	public static final SparseArray<String> profeLeversMap = new SparseArray<String>(2);
	
	public static final SparseArray<String>  professionMap = new SparseArray<String>(6);
	
	static{
		professionMap.put(0, "评估师");
		professionMap.put(1, "康复师");
		professionMap.put(2, "护理师");
		professionMap.put(3, "心理干预师");
		professionMap.put(4, "中医调理师");
		professionMap.put(5, "营养师");
		
		
		profeLeversMap.put(0, "全职");
		profeLeversMap.put(1, "兼职");
	}
	
	
	static{
		sexMap.put(0,"女");
		sexMap.put(1,"男");
		
		identTypeMap.put(0,"身份证");
		identTypeMap.put(1,"护照");
		identTypeMap.put(2,"警官证");
		identTypeMap.put(3,"其他");
		
		raceMap.put(0,"汉族");
		raceMap.put(1,"其他");
		
		faithMap.put(-1,"无宗教信仰");
		faithMap.put(1,"基督教");
		faithMap.put(2,"佛教");
		faithMap.put(3,"道教");
		faithMap.put(4,"伊斯兰教");
		faithMap.put(5,"邪教威胁");
		
		
		livWithMap.put(0,"独居");
		livWithMap.put(1,"与配偶/伴侣居住");
		livWithMap.put(2,"与子女居住");
		livWithMap.put(3,"与子女居住");
		livWithMap.put(4,"与其他亲属居住");
		livWithMap.put(5,"与无亲属关系的人居住");
		livWithMap.put(6,"住养老机构");
		
		inCometSourceMap.put(0,"退休金");
		inCometSourceMap.put(1,"子女补贴");
		inCometSourceMap.put(2,"亲友资助");
		inCometSourceMap.put(3,"其他");
		
		
		
		payMentMap.put(0,"城镇职工医疗保险");
		payMentMap.put(1,"城镇居民医疗保险");
		payMentMap.put(2,"新型农村合作医疗");
		payMentMap.put(3,"商业医疗保险");
		payMentMap.put(4,"公费");
		payMentMap.put(5,"全自费");
		payMentMap.put(6,"其他");
		
		roomOritentationMap.put(0,"东");
		roomOritentationMap.put(1,"东南");
		roomOritentationMap.put(2,"南");
		roomOritentationMap.put(3,"西南");
		roomOritentationMap.put(4,"西");
		roomOritentationMap.put(5,"西北");
		roomOritentationMap.put(6,"北");
		roomOritentationMap.put(7,"东北");
		
		outWindowMap.put(0,"否");
		outWindowMap.put(1,"是");

		//北京市区
		final SparseArray<String> beijingArea = new SparseArray<String>(6);
		//上海市区
		final SparseArray<String> shanghaiArea = new SparseArray<String>(6);
		
		beijingArea.put(3, "东城区");
		beijingArea.put(4, "西城区");
		beijingArea.put(5, "朝阳区");
		beijingArea.put(6, "海淀区");
		beijingArea.put(7, "丰台区");
		beijingArea.put(8, "石景山区");
		beijingArea.put(9, "门头沟区");
		beijingArea.put(10, "房山区");
		beijingArea.put(11, "通州区");
		beijingArea.put(12, "顺义区");
		beijingArea.put(13, "昌平区");
		beijingArea.put(14, "大兴区");
		beijingArea.put(15, "怀柔区");
		beijingArea.put(16, "平谷区");
		beijingArea.put(17, "密云县");
		beijingArea.put(18, "延庆县");
		
		shanghaiArea.put(0, "黄浦区");
		shanghaiArea.put(1, "浦东新区");
		shanghaiArea.put(2, "徐汇区");
		shanghaiArea.put(3, "长宁区");
		shanghaiArea.put(4, "静安区");
		shanghaiArea.put(5, "普陀区");
		shanghaiArea.put(6, "闸北区");
		shanghaiArea.put(7, "虹口区");
		shanghaiArea.put(8, "杨浦区");
		shanghaiArea.put(9, "闵行区");
		shanghaiArea.put(10, "宝山区");
		shanghaiArea.put(11, "嘉定区");
		shanghaiArea.put(12, "金山区");
		shanghaiArea.put(13, "松江区");
		shanghaiArea.put(14, "青浦区");
		shanghaiArea.put(15, "奉贤区");
		shanghaiArea.put(16, "崇明县");
		
		City beijing = new City("北京",beijingArea);
		City shanghai = new City("上海",shanghaiArea);
		
		cityMap.put(2, beijing);
		cityMap.put(0, shanghai);
		
	}
	
	
	/**
	 * 获得用于共用户选择的各个选项
	 * @param array 
	 * @return
	 */
	public static <T> List<T> getItems(SparseArray<T> array){
		List<T> list = new ArrayList<T>();
		for(int i=0;i<array.size();i++){
			list.add(array.valueAt(i));
		}
		return list;
		
	}
	
	
	
	/**
	 * 通过字符串的值或的对应的编号
	 * @param value
	 * @param array
	 * @return
	 */
	public static <T> int getIntKey(T value,SparseArray<T> array){
		int index = -100;
		for (int i = 0; i < array.size(); i++) {
			if(array.valueAt(i).equals(value)){
				index = i;
				break;
			}
		}
		return array.keyAt(index);
	}
	
	
	/**
	 * 通过字符串的值或的对应的编号,使用自定义比较器
	 * @param value
	 * @param array
	 * @return
	 */
	public static <T> int getIntKey(T value,SparseArray<T> array,Comparator<T> comparator){
		int index = -100;
		if(comparator==null){
			getIntKey(value, array);
		}
		for (int i = 0; i < array.size(); i++) {
			if(comparator.compare(array.valueAt(i),value)==0){
				index = i;
				break;
			}
		}
		return array.keyAt(index);
	}
	
	/**
	 * 考虑到数据的一致性，布尔值也用SparseArray表示
	 * @param value
	 * @param array
	 * @return
	 */
	public static <T> boolean getBooleanKey(T value,SparseArray<T> array){
		int key = getIntKey(value, array);
		return key==1;
	}
	
	/**
	 * 根据key返回value
	 * @param key
	 * @param array
	 * @return
	 */
	public static <T> T getValue(int key,SparseArray<T> array){
		return array.get(key);
	}
	
	
	/**
	 * 城市
	 *
	 */
	public static class City{
		private final String name;
		private final SparseArray<String> areas; //区列表
		
		public City(String name,SparseArray<String> areas) {
			this.areas = areas;
			this.name = name;
		}
		
		public String getName(){
			return name;
		}
		
		public List<String> getListArea(){
			return getItems(areas);
					
		}
		
		public SparseArray<String> getSparseArea(){
			return areas;
					
		}
		
		public int getAreaIntkey(String value){
			return getIntKey(value, areas);
		}
		
		public String getAreaValue(int key){
			 return areas.get(key);
		}
	}
}
