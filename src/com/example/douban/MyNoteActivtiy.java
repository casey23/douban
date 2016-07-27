package com.example.douban;

import java.util.ArrayList;
import java.util.List;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.itcast.domain.Note;
import cn.itcast.util.MyApp;

import com.google.gdata.data.TextContent;
import com.google.gdata.data.douban.Attribute;
import com.google.gdata.data.douban.NoteEntry;
import com.google.gdata.data.douban.NoteFeed;
import com.google.gdata.data.douban.UserEntry;

public class MyNoteActivtiy extends BasicActivity implements OnClickListener {
	private static final int NEW_NOTE = 0;
	private static final int EDIT_NOTE = 1;
	private ListView lvSubject;
	private TextView tvTitleBar;
	private Button btnPre, btnNext;
	private boolean dataLoading = false;
	private MyNoteAdapter noteAdapter;
	private int noteStartIndex = 1, noteItemCount = 3;
	private ProgressDialog pb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_my_note);
		super.onCreate(savedInstanceState);
		pb = new ProgressDialog(this);
	}

	public void setupView() {
		rlLoading = (RelativeLayout) this.findViewById(R.id.rlLoading);
		lvSubject = (ListView) this.findViewById(R.id.lvSubject);
		tvTitleBar = (TextView) this.findViewById(R.id.tvTitle);
		tvTitleBar.setText("我的日记");
		btnPre = (Button) this.findViewById(R.id.btnPre);
		btnNext = (Button) this.findViewById(R.id.btnNext);
		registerForContextMenu(lvSubject);

	}

	public void setListener() {
		btnPre.setOnClickListener(this);
		btnNext.setOnClickListener(this);

	}

	public void fillData() {

		new AsyncTask<Void, Void, List<Note>>() {

			@Override
			protected void onPreExecute() {
				showLoading();
				dataLoading = true;
				super.onPreExecute();
			}

			@Override
			protected void onPostExecute(List<Note> result) {
				hiddenLoading();
				dataLoading = false;
				super.onPostExecute(result);
				if (result != null) {
					noteAdapter = new MyNoteAdapter(result);
					lvSubject.setAdapter(noteAdapter);
				} else {
					showToast("获取数据异常");
				}
			}

			@Override
			protected List<Note> doInBackground(Void... params) {

				try {
					UserEntry ue = doubanService.getAuthorizedUser();
					String uid = ue.getUid();
					NoteFeed noteFeed = doubanService.getUserNotes(uid, noteStartIndex, noteItemCount);
					List<Note> notes = new ArrayList<Note>();
					for (NoteEntry ne : noteFeed.getEntries()) {
						Note note = getNoteEntry(ne);
						notes.add(note);
					}
					return notes;
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}
		}.execute();

	}

	private static Note getNoteEntry(NoteEntry noteEntry) {
		Note note = new Note();

		note.setNoteEntry(noteEntry);
		if (noteEntry.getContent() != null) {
			// 设置日记内容
			note.setContent(((TextContent) noteEntry.getContent()).getContent().getPlainText());
		}
		// 设置标题
		note.setTitle(noteEntry.getTitle().getPlainText());

		for (Attribute attr : noteEntry.getAttributes()) {
			// 设置privacy
			if ("privacy".equals(attr.getName())) {
				note.setPrivacy(attr.getContent());
				// 设置can_reply
			} else if ("can_reply".equals(attr.getName())) {
				note.setCanReply(attr.getContent());
			}
		}
		String pubDate = noteEntry.getPublished().toString();
		note.setPubDateTime(pubDate);
		return note;

	}

	// 按钮点击事件
	@Override
	public void onClick(View v) {

		int count = noteAdapter.getCount();
		switch (v.getId()) {
		case R.id.btnPre:
			if (noteStartIndex > noteItemCount) {
				noteStartIndex = noteStartIndex - noteItemCount;
				if (!dataLoading)
					fillData();

			} else {
				showToast("已经在第一页了");
			}

			break;
		case R.id.btnNext:

			noteStartIndex = noteStartIndex + noteItemCount;
			if (noteStartIndex > 10) {
				showToast("已经最后一页了");
				return;
			}
			if (!dataLoading)
				fillData();

			break;

		default:
			break;
		}
	}

	private class MyNoteAdapter extends BaseAdapter {

		private List<Note> notes;

		public MyNoteAdapter(List<Note> notes) {
			this.notes = notes;
		}

		@Override
		public int getCount() {
			return notes.size();
		}

		@Override
		public Object getItem(int position) {
			return notes.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			if (convertView == null) {
				view = View.inflate(getApplicationContext(), R.layout.activity_my_note_item, null);
			} else {
				view = convertView;
			}

			TextView tvTitle = (TextView) view.findViewById(R.id.tvTitle);
			tvTitle.setText(notes.get(position).getTitle());
			return view;
		}

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.note_item_click, menu);

		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		int position = (int) info.id;
		Note note = (Note) lvSubject.getItemAtPosition(position);
		NoteEntry entry = note.getNoteEntry();

		switch (item.getItemId()) {
		case R.id.note_add:
			Intent intent = new Intent(MyNoteActivtiy.this, MyNewNoteActivity.class);
			startActivityForResult(intent, NEW_NOTE);
			return true;
		case R.id.note_update:
			MyApp myApp = (MyApp) getApplication();
			 myApp.note=note;
			Intent editIntent = new Intent(MyNoteActivtiy.this, MyNewNoteActivity.class);
			editIntent.putExtra("editNote", true);
			startActivityForResult(editIntent, EDIT_NOTE);
			return true;
		case R.id.note_delete:
			deleteNote(entry);
			return true;
		}
		return super.onContextItemSelected(item);
	}

	/**
	 * 删除日记
	 * @param entry
	 */
	private void deleteNote(NoteEntry entry) {
		new AsyncTask<NoteEntry, Void, Boolean>() {

			protected void onPostExecute(Boolean result) {
				pb.dismiss();
				if (result) {
					fillData();
				} else {
					showToast("删除失败");
				}
				super.onPostExecute(result);

			};

			protected void onPreExecute() {
				pb.setMessage("正在删除");
				pb.show();
				super.onPreExecute();

			};

			@Override
			protected Boolean doInBackground(NoteEntry... params) {
				try {
					doubanService.deleteNote(params[0]);
					return true;
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
			}

		}.execute(entry);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == 200) {
			noteStartIndex = 1;
			fillData();
			if(requestCode==NEW_NOTE){
				showToast("新建日记成功");
			}else if(requestCode==EDIT_NOTE){
				showToast("更新日记成功");
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
