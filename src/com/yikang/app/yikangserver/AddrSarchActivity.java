package com.yikang.app.yikangserver;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.yikang.app.yikangserver.AddrInputTipsModel.OnTipsResultListener;
import com.yikang.app.yikangserver.bean.SimpleAddr;
import com.yikang.app.yikangserver.utils.LOG;


/**
 * 搜索地址的activity，负责确定一个位置
 * @author LGhui
 *
 */
public class AddrSarchActivity extends BaseActivity 
		implements OnClickListener, OnTipsResultListener{
	private static String TAG="AddrSarchActivity";
	private String cityCode="010";
	
	private AddrInputTipsModel inputTipsModel; //提供搜索的提示处理
	private EditText edtAddr;
	private Button btConfirm; //确认按钮
	private TextView tvMyAddr;
	
	private ListView lvAddrs; //显示提示的listView
	private List<SimpleAddr> addrList = new ArrayList<SimpleAddr>();
	private MyAddrAdapter adapter;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initContent();
		initTitleBar(getResources().getString(R.string.choose_addr_title));
		inputTipsModel = new AddrInputTipsModel(this, cityCode);
		inputTipsModel.setOnSearchResultListener(this);
	}
	
	@Override
	protected void findViews() { 
		edtAddr = (EditText) findViewById(R.id.edt_addrSearch_input);
		tvMyAddr = (TextView) findViewById(R.id.tv_addrSearch_myaddr);
		btConfirm = (Button) findViewById(R.id.bt_addrSearch_confirm);
		lvAddrs = (ListView) findViewById(R.id.lv_addrSearch_addrs_prompt);
	}

	@Override
	protected void setContentView() {
		setContentView(R.layout.activity_addrsearch);
	}

	@Override
	protected void getData() {}

	@Override
	protected void initViewConent() {
		//初始化listView
		adapter = new MyAddrAdapter();
		lvAddrs.setAdapter(adapter);
		
		btConfirm.setOnClickListener(this);
		edtAddr.addTextChangedListener(new TextWatcher() { 
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {}
			@Override
			public void afterTextChanged(Editable s) {
				//当文字改变时就发起搜索
				LOG.d(TAG, "[addTextChangedListener.afterTextChanged]");
				String keyWord = s.toString();
				inputTipsModel.setKeyWord(keyWord); //当文字改变，便开始搜索
				inputTipsModel.requestInputtips(); //开始异步搜索
			}
		});
		
		//设置item的点击事件，点击某一条地址之后，将选中的地址填写到输入框中
		lvAddrs.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				edtAddr.setText(addrList.get(position).title);
			}
		});
		
	}
	
	@Override
	public void onClick(View v) { 
		int id = v.getId();
		switch (id) {
		case R.id.bt_addrSearch_confirm: //将结果回传该activity的启动者
			String addr = edtAddr.getText().toString();
			Intent intent = new Intent();
			intent.putExtra("addr", addr);
			setResult(RESULT_OK, intent);
			finish();
			break;

		default:
			break;
		}
		
	}
	
	/**
	 * 返回搜索结果的回调方法，OnSearchResultListener接口的方法
	 */
	@Override
	public void onTipsResult(List<SimpleAddr> addrs,boolean isRusultOK) {
		addrList.clear();
		if(isRusultOK){ 
			Log.i(TAG, "接受到"+addrs.size()+"条数据");
			addrList.addAll(addrs);
			adapter.notifyDataSetChanged();
		}
			
	}
	
	
	
	
	/**
	 * 显示提示地址的listView的adapter
	 * @author LGhui
	 *
	 */
	class MyAddrAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return addrList.size();
		}

		@Override
		public Object getItem(int position) {
			return addrList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder ;
			if(convertView == null){
				convertView= getLayoutInflater().inflate(R.layout.item_list_addr_info, parent,false);
				holder = new ViewHolder();
				holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_itme_addr_title);
				holder.district = (TextView) convertView.findViewById(R.id.tv_item_addr_detail);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.district.setText(addrList.get(position).district);
			holder.tvTitle.setText(addrList.get(position).title);
			return convertView;
		}
		
		class ViewHolder{
			public TextView tvTitle;
			public TextView district;
		}
		
	}
}
