package com.example.douban;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.itcast.domain.Book;
import cn.itcast.util.BitmapAsyncTask;
import cn.itcast.util.BitmapAsyncTask.LoadImageCallBack;

import com.google.gdata.data.Link;
import com.google.gdata.data.douban.Attribute;
import com.google.gdata.data.douban.CollectionEntry;
import com.google.gdata.data.douban.CollectionFeed;
import com.google.gdata.data.douban.Subject;
import com.google.gdata.data.douban.UserEntry;
import com.google.gdata.data.extensions.Rating;

public class MyReadActivity extends BasicActivity implements OnScrollListener, OnItemClickListener {
	private ListView lvSubject;
	private TextView tvTitleBar;
	private MyBookAdapter bookAdapter;
	Map<String, SoftReference<Bitmap>> softRef;
	private int bookStartIndex, bookItemCount;
	private boolean dataLoading;
	private KillBroadCast receiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_my_read);
		super.onCreate(savedInstanceState);
		softRef = new HashMap<String, SoftReference<Bitmap>>();
		bookStartIndex = 1;
		bookItemCount = 5;
		dataLoading = false;
	}

	@Override
	public void setupView() {
		rlLoading = (RelativeLayout) this.findViewById(R.id.rlLoading);
		lvSubject = (ListView) this.findViewById(R.id.lvSubject);
		tvTitleBar = (TextView) this.findViewById(R.id.tvTitle);
		tvTitleBar.setText("我读");
		// 注册广播.用在内存过低情形
		IntentFilter filter = new IntentFilter();
		filter.addAction("kill_activity_action");
		receiver = new KillBroadCast();
		this.registerReceiver(receiver, filter);
	}

	@Override
	public void setListener() {
		lvSubject.setOnScrollListener(this);
		lvSubject.setOnItemClickListener(this);

	}

	@Override
	public void fillData() {

		new AsyncTask<Void, Void, List<Book>>() {
			private List<Book> books;

			@Override
			protected void onPreExecute() {
				showLoading();
				dataLoading = true;
				super.onPreExecute();
			}

			protected void onPostExecute(List<Book> result) {
				hiddenLoading();
				super.onPostExecute(result);
				if (result != null) {
					if (bookAdapter == null) {
						bookAdapter = new MyBookAdapter(result);
						lvSubject.setAdapter(bookAdapter);
					} else {
						bookAdapter.addData(result);
						bookAdapter.notifyDataSetChanged();
					}
				} else {
					showToast("获取数据失败");
				}
				dataLoading = false;
			};

			@Override
			protected List<Book> doInBackground(Void... params) {
				try {
					UserEntry ue = doubanService.getAuthorizedUser();
					String uid = ue.getUid();
					CollectionFeed feeds = doubanService.getUserCollections(uid, "book", null, null, bookStartIndex, bookItemCount);
					books = new ArrayList<Book>();
					for (CollectionEntry ce : feeds.getEntries()) {
						Book book = getBook(ce.getSubjectEntry());
						if (book != null)
							books.add(book);
					}
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
				return books;
			};

		}.execute();

	}

	/**
	 * 获取图书信息
	 * 
	 * @param se
	 * @return
	 */
	private Book getBook(Subject se) {
		if (se != null) {
			Book book = new Book();
			String title = se.getTitle().getPlainText();
			book.setName(title);

			// 获取书籍的描述
			StringBuilder sb = new StringBuilder();
			for (Attribute attr : se.getAttributes()) {
				if ("author".equals(attr.getName())) {
					sb.append(attr.getContent());
					sb.append("/");
				} else if ("publisher".equals(attr.getName())) {
					sb.append(attr.getContent());
					sb.append("/");
				} else if ("pubdate".equals(attr.getName())) {
					sb.append(attr.getContent());
					sb.append("/");
				} else if ("isbn10".equals(attr.getName())) {
					sb.append(attr.getContent());
					sb.append("/");
				}
			}
			book.setDesc(sb.toString());

			// 获取书籍的评分
			Rating rating = se.getRating();
			if (rating != null) {
				book.setRating(rating.getAverage());
			} else {
				book.setRating(0);
			}
			for (Link link : se.getLinks()) {
				if ("image".equals(link.getRel())) {
					book.setBookUrl(link.getHref());
				}
			}
			return book;
		} else {
			return null;
		}
	}

	/**
	 * listview的适配器
	 * 
	 * @author Administrator
	 * 
	 */

	private class MyBookAdapter extends BaseAdapter {
		private List<Book> books;

		public MyBookAdapter(List<Book> books) {
			this.books = books;
		}

		public void addData(List<Book> books) {
			for (Book book : books)
				this.books.add(book);
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

		// 设置book_item的数据
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View view = View.inflate(MyReadActivity.this, R.layout.activity_my_read_book_item, null);
			final ImageView ivBook = (ImageView) view.findViewById(R.id.ivBook);
			RatingBar rbBook = (RatingBar) view.findViewById(R.id.rbBook);
			TextView tvBookName = (TextView) view.findViewById(R.id.tvBookName);
			TextView tvBookDesc = (TextView) view.findViewById(R.id.tvBookDesc);
			Book book = books.get(position);

			// 设置图书评分
			if (book.getRating() != 0) {
				rbBook.setRating(book.getRating());
			} else {
				rbBook.setVisibility(View.INVISIBLE);
			}
			// 设置图书名字
			tvBookName.setText(book.getName());
			// 设置图书描述
			tvBookDesc.setText(book.getDesc());

			// 设置图书的图片
			String bookUrl = book.getBookUrl();
			getBookImg2(ivBook, bookUrl);

			return view;
		}

	}

	/**
	 * 通过url获取图书的图片 采用SD卡缓存
	 * 
	 * @param ivBook
	 * @param bookUrl
	 */
	private void getBookImg(final ImageView ivBook, String bookUrl) {

		final String fileName = bookUrl.substring(bookUrl.lastIndexOf("/"), bookUrl.length());
		File file = new File("/sdcard/" + fileName);
		if (file.exists()) {
			ivBook.setImageURI(Uri.fromFile(file));
			System.out.println("使用SD卡缓存");
		} else {

			BitmapAsyncTask task = new BitmapAsyncTask(new LoadImageCallBack() {

				@Override
				public void beforeLoadImage() {
					ivBook.setImageResource(R.drawable.book);
				}

				@Override
				public void afterLoadImage(Bitmap bitmap) {
					if (bitmap != null) {
						try {
							File file = new File("/sdcard/" + fileName);
							FileOutputStream fos;
							fos = new FileOutputStream(file);
							bitmap.compress(CompressFormat.JPEG, 100, fos);
							System.out.println("使用互联网下载图片");
						} catch (Exception e) {
							e.printStackTrace();
						}
						ivBook.setImageBitmap(bitmap);
					} else {
						ivBook.setImageResource(R.drawable.book);
					}

				}
			});
			task.execute(bookUrl);
		}
	}

	/**
	 * 通过url获取图书的图片 使用内存缓存方式
	 * 
	 * @param ivBook
	 * @param bookUrl
	 */
	private void getBookImg2(final ImageView ivBook, String bookUrl) {

		final String fileName = bookUrl.substring(bookUrl.lastIndexOf("/"), bookUrl.length());
		if (softRef.containsKey(fileName)) {
			SoftReference<Bitmap> soft = softRef.get(fileName);
			if (soft != null) {
				Bitmap bitmap = soft.get();
				ivBook.setImageBitmap(bitmap);
				System.out.println("使用内存缓存");
			}

		} else {

			BitmapAsyncTask task = new BitmapAsyncTask(new LoadImageCallBack() {

				@Override
				public void beforeLoadImage() {
					ivBook.setImageResource(R.drawable.book);
				}

				@Override
				public void afterLoadImage(Bitmap bitmap) {
					if (bitmap != null) {
						softRef.put(fileName, new SoftReference<Bitmap>(bitmap));
						ivBook.setImageBitmap(bitmap);
						System.out.println("使用互联网");
					} else {
						ivBook.setImageResource(R.drawable.book);
					}

				}
			});
			task.execute(bookUrl);
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		switch (scrollState) {
		case OnScrollListener.SCROLL_STATE_IDLE:
			int position = view.getLastVisiblePosition();
			int count = bookAdapter.getCount();
			// listview拖动到最后一条时，加载更多数据
			if (position == count - 1) {
				System.out.println("加载更多数据");
				bookStartIndex = bookStartIndex + bookItemCount;
				if (dataLoading != true)
					fillData();
			}
			break;

		}

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

	}

	private class KillBroadCast extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			softRef = null;
			showToast("内存量过低");
			finish();

		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Book book = (Book) lvSubject.getItemAtPosition(position);
		String desc = book.getDesc();
		String isbn = desc.substring(0, desc.indexOf("/"));
		Intent intent = new Intent(this, BookDetailActivity.class);
		intent.putExtra("isbn", isbn);
		startActivity(intent);
	}
}
