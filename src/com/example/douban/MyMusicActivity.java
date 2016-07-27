package com.example.douban;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class MyMusicActivity extends BasicActivity {
	private ListView lvSubject;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_my_music);
		super.onCreate(savedInstanceState);
	}

	@Override
	public void setupView() {

		lvSubject = (ListView) this.findViewById(R.id.lvSubject);
		rlLoading = (RelativeLayout) this.findViewById(R.id.rlLoading);
	}

	@Override
	public void setListener() {

	}

	@Override
	public void fillData() {
		new AsyncTask<String, Void, Bitmap>() {
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
			}

			@Override
			protected void onPostExecute(Bitmap result) {
				super.onPostExecute(result);
			}

			@Override
			protected Bitmap doInBackground(String... params) {
				
				return null;
			}
		}.execute();

	}

	private class MyMusicAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			return null;
		}

	}

}
