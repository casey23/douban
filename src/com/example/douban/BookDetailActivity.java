package com.example.douban;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONArray;
import org.json.JSONObject;

import net.htmlparser.jericho.Source;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class BookDetailActivity extends Activity implements OnClickListener {
	private TextView tvTitle, tvSummary;
	private Button btnClose;
	private LinearLayout llShowing;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_book_detail);
		String isbn = getIntent().getStringExtra("isbn");
		setupView();
		setOnListen();
		fillData(isbn);
	}

	private void fillData(String isbn) {
		new AsyncTask<String, Void, Boolean>() {

			private String title;
			private String summary;
			private String price;

			protected void onPreExecute() {
				llShowing.setVisibility(View.VISIBLE);
				super.onPreExecute();
			};

			protected void onPostExecute(Boolean result) {
				llShowing.setVisibility(View.INVISIBLE);
				if (result) {
					tvTitle.setText(title + " / " + price);
					tvSummary.setText(summary);
				} else {
					Toast.makeText(getApplicationContext(), "网络连接异常", 0).show();
				}
				super.onPostExecute(result);
			};

			@Override
			protected Boolean doInBackground(String... params) {
				String isbn = params[0];
				String path = getResources().getString(R.string.bookJson);
				// http://api.douban.com/book/subject/isbn/7543632608?alt=json
				try {
					path = path + isbn + "?alt=json";
					URL url = new URL(path);
					URLConnection conn = url.openConnection();
					InputStreamReader is = new InputStreamReader(conn.getInputStream(), "utf-8");
					Source source = new Source(is);
					String jsonStr = source.toString();
					JSONObject obj = new JSONObject(jsonStr);

					// 获取标题
					String titleJson = obj.get("title").toString();
					JSONObject titleObj = new JSONObject(titleJson);
					title = titleObj.get("$t").toString();

					// 获取摘要
					String summaryJson = obj.get("summary").toString();
					JSONObject summaryObj = new JSONObject(summaryJson);
					summary = summaryObj.get("$t").toString();

					// 获取价格
					String priceStr = obj.get("db:attribute").toString();
					JSONArray priceArr = new JSONArray(priceStr);
					for (int i = 0; i < priceArr.length(); i++) {
						JSONObject paramObj = new JSONObject(priceArr.get(i).toString());
						if ("price".equals(paramObj.get("@name").toString())) {
							price = paramObj.get("$t").toString();
						}
					}
					return true;
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}

			}
		}.execute(isbn);

	}

	private void setOnListen() {
		btnClose.setOnClickListener(this);
	}

	private void setupView() {

		tvTitle = (TextView) this.findViewById(R.id.tvTitle);
		tvSummary = (TextView) this.findViewById(R.id.tvSummary);
		btnClose = (Button) this.findViewById(R.id.btnClose);
		llShowing = (LinearLayout) this.findViewById(R.id.llShowing);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnClose:
			finish();
			break;
		}

	}

}
