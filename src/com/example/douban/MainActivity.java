package com.example.douban;

import cn.itcast.util.SettingActivity;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class MainActivity extends TabActivity {
	private TabHost mTabHost;
	private LayoutInflater inflater;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		inflater = LayoutInflater.from(this);
		mTabHost = getTabHost();
		mTabHost.addTab(getDouBanSpec());
		mTabHost.addTab(getNewBookTab());
		mTabHost.setCurrentTabByTag("myDouban");
		 mTabHost.addTab(getNewSpec2());

	}

	private TabSpec getDouBanSpec() {
		TabSpec spec = mTabHost.newTabSpec("myDouban");
		Intent intent = new Intent(this, MeActivity.class);
		spec.setContent(intent);
		spec.setIndicator(getSpeceIndicator("我的豆瓣", R.drawable.tab_main_nav_me));
		return spec;
	}

	private TabSpec getNewBookTab() {
		TabSpec spec = mTabHost.newTabSpec("newBook");
		Intent intent = new Intent(this, NewBookActivity.class);
		spec.setContent(intent);
		spec.setIndicator(getSpeceIndicator("豆瓣新书", R.drawable.tab_main_nav_book));
		return spec;
	}

	private TabSpec getNewSpec2() {
		TabSpec spec = mTabHost.newTabSpec("setting");
		Intent intent = new Intent(this, SettingActivity.class);
		spec.setContent(intent);
		spec.setIndicator(getSpeceIndicator("设置", R.drawable.tab_main_nav_more));
		return spec;
	}

	/**
	 * 获取自定义spec界面
	 * 
	 * @param name
	 * @param iconId
	 * @return
	 */
	private View getSpeceIndicator(String name, int iconId) {
		View view = inflater.inflate(R.layout.tab_main_nav, null);
		ImageView ivIcon = (ImageView) view.findViewById(R.id.ivIcon);
		TextView tvTitle = (TextView) view.findViewById(R.id.tvTitle);
		ivIcon.setImageResource(iconId);
		tvTitle.setText(name);
		return view;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater flater = new MenuInflater(this);
		flater.inflate(R.menu.main_tab, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.main_tab_clear_user:
			System.out.println("清除账号");
			SharedPreferences sp = getSharedPreferences("config", Context.MODE_PRIVATE);
			Editor edit = sp.edit();
			edit.putString("accessToken", "");
			edit.putString("tokenSecret", "");
			edit.commit();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
