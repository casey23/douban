package com.example.douban;

import java.util.List;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.itcast.util.BitmapAsyncTask;
import cn.itcast.util.BitmapAsyncTask.LoadImageCallBack;

import com.google.gdata.data.Link;
import com.google.gdata.data.TextContent;
import com.google.gdata.data.douban.UserEntry;

public class MyInfoActivity extends BasicActivity implements OnClickListener {
	private TextView tvUserDescription;
	private ImageView imgUser;
	private TextView tvUserName;
	private TextView tvUserAddress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.my_info);
		super.onCreate(savedInstanceState); // 先设置view,再执行父类的setupView方法

	}

	@Override
	public void setupView() {
		imgUser = (ImageView) this.findViewById(R.id.imgUser);
		tvUserName = (TextView) this.findViewById(R.id.tvUserName);
		tvUserAddress = (TextView) this.findViewById(R.id.tvUserAddress);
		tvUserDescription = (TextView) this.findViewById(R.id.tvUserDescription);
		rlLoading = (RelativeLayout) this.findViewById(R.id.rlLoading);
		tvTitle = (TextView) this.findViewById(R.id.tvTitle);
		imgBack = (ImageButton) this.findViewById(R.id.imgBack);
	}

	@Override
	public void setListener() {
		imgBack.setOnClickListener(this);

	}

	@Override
	public void fillData() {

		new AsyncTask<Void, Void, Void>() {

			private String name;
			private String address;
			private String info;
			private String imgPath;

			protected void onPreExecute() {
				showLoading();
				super.onPreExecute();
			};

			protected void onPostExecute(Void result) {
				hiddenLoading();
				super.onPostExecute(result);
				tvUserName.setText(name);
				tvUserAddress.setText(address);
				tvUserDescription.setText(info);
				
				new BitmapAsyncTask(new LoadImageCallBack() {
					
					@Override
					public void beforeLoadImage() {
						imgUser.setImageResource(R.drawable.ic_launcher);
					}
					
					@Override
					public void afterLoadImage(Bitmap bitmap) {
						if(bitmap!=null){
							imgUser.setImageBitmap(bitmap);
						}
					}

				}).execute(imgPath);
				
				
			};

			@Override
			protected Void doInBackground(Void... params) {
				try {
					UserEntry entry = doubanService.getAuthorizedUser();
					 name = entry.getTitle().getPlainText();
					 address = entry.getLocation();
					 info = ((TextContent)entry.getContent()).getContent().getPlainText();
					for(Link link:entry.getLinks()){
						if("icon".equals(link.getRel())){
							imgPath = link.getHref();
						}
					}
				} catch (Exception e) {
					
					e.printStackTrace();
				}
				
				return null;
			}

		}.execute();

	}


	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.imgBack:
			finish();
			break;
		}
	}

}
