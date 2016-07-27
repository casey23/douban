package com.example.douban;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SplashActivity extends Activity {
	private TextView versionName;
	private LinearLayout mLinearLayout;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splash);
		 versionName = (TextView) this.findViewById(R.id.versionNumber);
		  mLinearLayout = (LinearLayout) this.findViewById(R.id.mLinearLayout);
		  versionName.setText(getVersion());
		 if(isNetWorkConnected()){
			 AlphaAnimation aa=new AlphaAnimation(0.0f, 1.0f);
			 aa.setDuration(2000);
			 mLinearLayout.setAnimation(aa);
			 mLinearLayout.startAnimation(aa);
			 new Handler().postDelayed(new LoadMainTask(),2000);
			 
		 }else{
			 showSetNetWorkDialog();
		 }
		 
	}
	private class LoadMainTask implements Runnable{

		@Override
		public void run() {
			Intent intent = new Intent(SplashActivity.this,MainActivity.class);
			startActivity(intent);
			finish();
		}
	}
	private void showSetNetWorkDialog() {
		AlertDialog.Builder builder=new Builder(this);
		builder.setTitle("设置网络");
		builder.setMessage("网络错误,请检查网络状态");
		builder.setPositiveButton("设置网络", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent();
				intent.setClassName("com.android.settings", "com.android.settings.WirelessSettings");
				startActivity(intent);
				finish();
			}
		});
		builder.setNegativeButton("取消", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
				
			}
		});
		builder.create().show();
		
	}

	/**
	 * 获取版本号
	 * @return
	 */
	private String getVersion(){
		try {
			PackageInfo info = getPackageManager().getPackageInfo(getPackageName(),0);
			return "Version"+ info.versionName;
		} catch (Exception e) {
			e.printStackTrace();
			return "Version";
		}
	}
	/**
	 * 获取网络状态
	 * @return
	 */
	private boolean isNetWorkConnected(){
		ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo info = manager.getActiveNetworkInfo();
		return (info!=null && info.isConnected());
		
	}
	

}
