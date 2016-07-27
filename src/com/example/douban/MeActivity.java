package com.example.douban;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MeActivity extends Activity implements OnItemClickListener {
	private ListView meListView;
	private SharedPreferences sp;
	private static final String[] arr = { "我读", "我看", "我听", "我评", "我的日记", "我的资料", "小组" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_me);
		sp = getSharedPreferences("config", Context.MODE_PRIVATE);
		meListView = (ListView) this.findViewById(R.id.meListview);
		meListView.setAdapter(new ArrayAdapter<>(this, R.layout.activity_me_item, R.id.fav_title, arr));
		meListView.setOnItemClickListener(this);
	}

	private boolean isUserAuthorized() {
		String accessToken = sp.getString("accessToken", "");
		String tokenSecret = sp.getString("tokenSecret", "");
		if ("".equals(accessToken) || "".equals(tokenSecret)) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (isUserAuthorized()) {
			switch (position) {
			case 0:
				Intent readIntent = new Intent(MeActivity.this, MyReadActivity.class);
				startActivity(readIntent);
				break;
			case 4:
				Intent noteIntent = new Intent(MeActivity.this, MyNoteActivtiy.class);
				startActivity(noteIntent);
				break;
			case 5:
				Intent intent = new Intent(MeActivity.this, MyInfoActivity.class);
				startActivity(intent);
				break;

			default:
				break;
			}
			System.out.println("已经认证");
		} else {
			// 定向到登录界面
			System.out.println("我要跳转了");
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
		}

	}
}
