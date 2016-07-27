package cn.itcast.util;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

public class BitmapAsyncTask extends AsyncTask<String, Void, Bitmap> {
	LoadImageCallBack loadImageCallBack;

	public BitmapAsyncTask(LoadImageCallBack loadImageCallBack) {
		this.loadImageCallBack = loadImageCallBack;
	}

	public interface LoadImageCallBack {
		public void beforeLoadImage();

		public void afterLoadImage(Bitmap bitmap);

	}

	@Override
	protected void onPreExecute() {
		loadImageCallBack.beforeLoadImage();
		super.onPreExecute();
	}

	@Override
	protected void onPostExecute(Bitmap result) {
		loadImageCallBack.afterLoadImage(result);
		super.onPostExecute(result);
	}

	// 通过图片url获取bitmap图片
	// 运行在子线程
	@Override
	protected Bitmap doInBackground(String... params) {
		String path = params[0];
		try {
			URL url = new URL(path);
			URLConnection conn = url.openConnection();
			InputStream is = conn.getInputStream();
			Bitmap bitmap = BitmapFactory.decodeStream(is);
			return bitmap;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
