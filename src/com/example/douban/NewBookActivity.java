package com.example.douban;

import java.lang.ref.SoftReference;
import java.util.List;
import java.util.Map;

import cn.itcast.domain.Book;
import cn.itcast.domain.NewBook;
import cn.itcast.util.BitmapAsyncTask;
import cn.itcast.util.NetUtil;
import cn.itcast.util.BitmapAsyncTask.LoadImageCallBack;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class NewBookActivity extends BasicActivity implements OnItemClickListener {
	private ListView lvSubject;
	private TextView tvTitle;
	private List<NewBook> books;
	Map<String, SoftReference<Bitmap>> softRef;
	private NewBookAdapter bookAdapter;
	private boolean isDownLoadImg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_new_book);
		super.onCreate(savedInstanceState);
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		isDownLoadImg = sp.getBoolean("downLoadImg", true);
	}

	@Override
	public void setupView() {
		lvSubject = (ListView) this.findViewById(R.id.lvSubject);
		rlLoading = (RelativeLayout) this.findViewById(R.id.rlLoading);
		TextView tvTitle = (TextView) this.findViewById(R.id.tvTitle);
		tvTitle.setText("豆瓣新书");
		

	}

	@Override
	public void setListener() {
		lvSubject.setOnItemClickListener(this);

	}

	@Override
	public void fillData() {
		getBookData();
	}

	private void getBookData() {
		new AsyncTask<Void, Void, Boolean>() {

			protected void onPreExecute() {
				showLoading();
				super.onPreExecute();
			};

			protected void onPostExecute(Boolean result) {
				hiddenLoading();
				super.onPostExecute(result);
				if (result) {
					if (bookAdapter == null) {
						bookAdapter = new NewBookAdapter(books);
						lvSubject.setAdapter(bookAdapter);
					} else {
						bookAdapter.notifyDataSetChanged();
					}
				} else {
					showToast("网络连接失败");
				}
			};

			@Override
			protected Boolean doInBackground(Void... params) {
				try {
					books = NetUtil.getNewBook(getApplicationContext());
					return true;
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}

			}

		}.execute();
	}

	private class NewBookAdapter extends BaseAdapter {

		private List<NewBook> books;

		public NewBookAdapter(List<NewBook> books) {
			this.books = books;
		}

		@Override
		public int getCount() {
			return books.size();
		}

		@Override
		public Object getItem(int position) {
			return books.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			if (convertView == null) {
				view = View.inflate(getApplicationContext(), R.layout.activity_new_book_item, null);
			} else {
				view = convertView;
			}
			final ImageView ivBook = (ImageView) view.findViewById(R.id.ivBook);
			RatingBar rbBook = (RatingBar) view.findViewById(R.id.rbBook);
			TextView tvBookName = (TextView) view.findViewById(R.id.tvBookName);
			TextView tvBookDesc = (TextView) view.findViewById(R.id.tvBookDesc);
			NewBook book = books.get(position);
			tvBookName.setText(book.getName());
			tvBookDesc.setText(book.getDescription());
			rbBook.setRating(5.0f);

			// 判断是否下载图片
			if (isDownLoadImg) {
				new BitmapAsyncTask(new LoadImageCallBack() {

					@Override
					public void beforeLoadImage() {

						ivBook.setImageResource(R.drawable.book);
					}

					@Override
					public void afterLoadImage(Bitmap bitmap) {

						if (bitmap != null) {
							ivBook.setImageBitmap(bitmap);
						} else {
							ivBook.setImageResource(R.drawable.book);
						}
					}
				}).execute(book.getImgPath());
			} else {
				ivBook.setImageResource(R.drawable.book);
			}
			return view;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		NewBook book = (NewBook) lvSubject.getItemAtPosition(position);
		String isbn = book.getIsbn();
		String isbnStr = NetUtil.getNewBookISBN(isbn);
		Intent intent = new Intent(this, NewBookDetailActivity.class);
		intent.putExtra("isbn", isbnStr);
		startActivity(intent);

	}

}
