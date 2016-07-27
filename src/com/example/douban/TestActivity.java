package com.example.douban;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class TestActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		TextView tv = new TextView(this);
		tv.setText("我的测试");
		setContentView(tv);
		super.onCreate(savedInstanceState);
	}

}
