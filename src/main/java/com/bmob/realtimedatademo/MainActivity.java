package com.bmob.realtimedatademo;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobRealTimeData;
import cn.bmob.v3.listener.FindCallback;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.ValueEventListener;
import android.app.Activity;
import android.app.DownloadManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {
	
	ListView lv_data;
	Button btn_send;
	EditText et_name, et_content;
	
	MyAdapter myAdapter;
	List<Chat> messages = new ArrayList<Chat>();
	BmobRealTimeData data = new BmobRealTimeData();

	public static final String TAG="TAG";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_main);
		
		init();
		
		et_name = (EditText) findViewById(R.id.et_name);
		et_content = (EditText) findViewById(R.id.et_content);
		lv_data = (ListView) findViewById(R.id.lv_data);
		
		myAdapter = new MyAdapter();
		lv_data.setAdapter(myAdapter);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		String name = et_name.getText().toString();
		String content = et_content.getText().toString();
		if(TextUtils.isEmpty(name) || TextUtils.isEmpty(content)){
			Toast.makeText(this, "用户名和内容不能为空", Toast.LENGTH_SHORT).show();
			return;
		}else{
			sendMsg(name, content);
		}
	}
	
	
	private void init(){
		Bmob.initialize(this,"f1191f149aebfe95adf36a76253697ad");
		data.start(this, new ValueEventListener() {
			
			@Override
			public void onDataChange(JSONObject arg0) {
				Log.i(TAG, "onDataChange:"+arg0.toString());
				if(BmobRealTimeData.ACTION_UPDATETABLE.equals(arg0.optString("action"))){
					JSONObject data = arg0.optJSONObject("data");
					messages.add(0,new Chat(data.optString("name"), data.optString("content")));
					myAdapter.notifyDataSetChanged();
				}
				
			}
			
			@Override
			public void onConnectCompleted() {
				// TODO Auto-generated method stub
				if(data.isConnected()){
					data.subTableUpdate("Chat");
				}
			}
		});

		BmobQuery<Chat> query=new BmobQuery<>();
		query.order("-createdAt");
		query.findObjects(this, new FindListener<Chat>() {
			@Override
			public void onSuccess(List<Chat> list) {
				if(list.size()>0){
					for(Chat chat:list){
						messages.add(chat);
						Log.i(TAG, "请求成功:"+chat);
					}
				}else{
					Log.i(TAG, "请求失败");
				}
			}

			@Override
			public void onError(int i, String s) {
				Toast.makeText(MainActivity.this, "失败:"+s+";错误码:"+i, Toast.LENGTH_SHORT).show();
				Log.i(TAG, "onFailure: "+s+";错误码:"+i);
			}
		});
	}
	
	private void sendMsg(String name, String msg){
		Chat chat = new Chat(name, msg);
		chat.save(this, new SaveListener() {
			
			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				et_content.setText("");
			}
			
			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}
		});
		
	}

	private class MyAdapter extends BaseAdapter{
		
		ViewHolder holder;

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return messages.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return messages.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if(convertView == null){
				convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.list_item, null);
				holder = new ViewHolder();
				
				holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
				holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
				holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
				
				convertView.setTag(holder);
			}else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			Chat chat = messages.get(position);
			holder.tv_name.setText(chat.getName());
			holder.tv_content.setText(chat.getContent());
			holder.tv_time.setText(chat.getCreatedAt());
			
			return convertView;
		}
		
		class ViewHolder{
			TextView tv_name;
			TextView tv_content;
			TextView tv_time;
		}
		
	}

}
