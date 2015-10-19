package com.yikang.app.yikangserver.data;

public class ConstantData {
	public static final String ACTION_BROADCAST_ADD_SENOIR = "ACTION_BROADCAST_ADD_SENOIR";
	public static final String ACTION_BROADCAST_NEW_SENIOR = "ACTION_BROADCAST_NEW_SENIOR";
	public static final String ACTION_BROADCAST_NEW_SERVICE_TIEM = "ACTION_BROADCAST_NEW_SERVICE_TIEM";
	
//	/**
//	 * 
//	 * @author LGhui
//	 *
//	 */
//	public static class ServerCountData {
//		public static final String key_profession = "professions";
//		public static final String key_profess_lever = "professionLevers";
//
//		public static List<NameValue> getList(String key) {
//			String json = readFileFromAssert(AppContext.getAppContext(),
//					"data.txt");
//			JSONObject object = JSON.parseObject(json).getJSONObject(
//					"serverCountData");
//			Log.d("indo", object.toJSONString());
//			JSONArray array = object.getJSONArray(key);
//			return toList(array.toJSONString());
//
//		}
//	}
//
//	
//
//	public static class NameValue{
//		public String name;
//		public Object value;
//
//		public NameValue() {
//		}
//
//		public NameValue(String name, int value) {
//			this.name = name;
//			this.value = value;
//		}
//
//	}
//	
//	
//
//	/**
//	 * 本类是用来处理常量数据
//	 * 
//	 * @author LGhui
//	 * 
//	 */
//	public static class ConstantsUtils {
//
//		/**
//		 * 从list名值对中中获取所有的name
//		 */
//		public static ArrayList<String> getNames(List<NameValue> list) {
//			ArrayList<String> names = new ArrayList<String>();
//			for (NameValue nameValue : list) {
//				names.add(nameValue.name);
//			}
//			return names;
//		}
//
//		/**
//		 * 从list名值对中获取所有的value
//		 * 
//		 * @param list
//		 * @return
//		 */
//		public static <T> ArrayList<T> getValues(List<NameValue> list) {
//			ArrayList<T> names = new ArrayList<T>();
//			for (NameValue nameValue : list) {
//				names.add((T) nameValue.value);
//			}
//			return names;
//		}
//
//		/**
//		 * 根据value和所处的名值对获取name 如果找不到返回null
//		 * 
//		 * @param value
//		 *            即需要找的明值对值
//		 * @param list
//		 */
//		public static <T> String getName(T value, List<NameValue> list) {
//			for (NameValue nameValue : list) {
//				if (nameValue.value.equals(value)) {
//					return nameValue.name;
//				}
//			}
//			return null;
//		}
//
//		/**
//		 * 根据name和所处的名值对获取value,如果找不到就返回null
//		 * 
//		 * @param name
//		 * @param list
//		 * @return
//		 */
//		public static <T> T getValue(String name, List<NameValue> list) {
//			for (NameValue nameValue : list) {
//				if (name.equals(nameValue.name)) {
//					return (T) nameValue.value;
//				}
//			}
//			return null;
//		}
//	}
//	
//	/**
//	 * 
//	 * @param json
//	 * @return
//	 */
//	public static List<NameValue> toList(String json) {
//		List<NameValue> list = JSON.parseArray(json, NameValue.class);
//		return list;
//	}
//
//	/**
//	 * 从资产目录中读取数据
//	 * 
//	 * @param context
//	 * @param fileName
//	 * @return
//	 */
//	public static String readFileFromAssert(Context context, String fileName) {
//		AssetManager manager = context.getAssets();
//		InputStream inputStream = null;
//		try {
//			inputStream = manager.open(fileName);
//			BufferedReader reader = new BufferedReader(new InputStreamReader(
//					inputStream));
//			StringBuffer buffer = new StringBuffer();
//			String line;
//			while ((line = reader.readLine()) != null) {
//				buffer.append(line);
//			}
//			return buffer.toString();
//
//		} catch (IOException e) {
//			e.printStackTrace();
//		} finally {
//			if (inputStream != null) {
//				try {
//					inputStream.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//		return null;
//
//	}
}
