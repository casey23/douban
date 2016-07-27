package com.example.douban;

import cn.itcast.util.NetUtil;

import com.google.gdata.client.douban.DoubanService;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public abstract class BasicActivity extends Activity {
	public TextView tvName;
	public ImageView ivIcon;
	public ImageButton imgback;
	public RelativeLayout rlLoading;
	public DoubanService doubanService;
	public TextView tvTitle;
	public ImageButton imgBack;

	public abstract void setupView();

	public abstract void setListener();

	public abstract void fillData();

	public void showLoading() {
		rlLoading.setVisibility(View.VISIBLE);
		AlphaAnimation aa = new AlphaAnimation(0.0f, 1.0f);
		ScaleAnimation sa = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f);
		AnimationSet as = new AnimationSet(false);
		as.addAnimation(aa);
		as.addAnimation(sa);
		rlLoading.setAnimation(sa);
		rlLoading.startAnimation(sa);
	}

	public void hiddenLoading() {
		rlLoading.setVisibility(View.VISIBLE);
		AlphaAnimation aa = new AlphaAnimation(1.0f, 0.0f);
		ScaleAnimation sa = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f);
		AnimationSet set = new AnimationSet(false);
		set.addAnimation(aa);
		set.addAnimation(sa);
		rlLoading.setAnimation(set);
		rlLoading.startAnimation(set);
		rlLoading.setVisibility(View.INVISIBLE);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		doubanService = NetUtil.getDoubanService(getApplicationContext());
		setupView();
		setListener();
		fillData();

	}

	/**
	 * 显示toast
	 * @param text
	 */
	public void showToast(String text) {
		Toast.makeText(this, text, 0).show();
	}

}
