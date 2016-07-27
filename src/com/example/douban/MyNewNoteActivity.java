package com.example.douban;

import android.app.Application;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

import cn.itcast.domain.Note;
import cn.itcast.util.MyApp;

import com.google.gdata.data.PlainTextConstruct;

public class MyNewNoteActivity extends BasicActivity implements OnClickListener {
	private EditText edtTitle, edtContent;
	private RadioButton rbPrivate, rbPublic, rbFriend;
	private CheckBox cbCanReply;
	private Button btnSave, btnCancel;
	private ProgressDialog pd;
	private boolean isEditNote;
	private Note note;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_my_note_edit);
		super.onCreate(savedInstanceState);
		pd = new ProgressDialog(this);
		isEditNote = getIntent().getBooleanExtra("editNote", false);
		if (isEditNote) {
			MyApp myApp = (MyApp) getApplication();
			note = myApp.note;
			edtTitle.setText(note.getTitle());
			edtContent.setText(note.getContent());
			String privacy = note.getPrivacy();
			if ("public".equals(privacy)) {
				rbPublic.setChecked(true);
			} else if ("friend".equals(privacy)) {
				rbFriend.setChecked(true);
			} else {
				rbPrivate.setChecked(true);
			}
			String canReply = note.getCanReply();
			if ("yes".equals(canReply)) {
				cbCanReply.setChecked(true);
			} else {
				cbCanReply.setChecked(false);
			}
		}
		
	}

	@Override
	public void setupView() {
		rlLoading = (RelativeLayout) this.findViewById(R.id.rlLoading);
		edtTitle = (EditText) this.findViewById(R.id.edtTitle);
		edtContent = (EditText) this.findViewById(R.id.edtContent);
		rbPrivate = (RadioButton) this.findViewById(R.id.rbPrivate);
		rbPublic = (RadioButton) this.findViewById(R.id.rbPublic);
		rbFriend = (RadioButton) this.findViewById(R.id.rbFriend);
		cbCanReply = (CheckBox) this.findViewById(R.id.cbCanReply);
		btnSave = (Button) this.findViewById(R.id.btnSave);
		btnCancel = (Button) this.findViewById(R.id.btnCancel);

	}

	@Override
	public void setListener() {
		btnSave.setOnClickListener(this);
		btnCancel.setOnClickListener(this);

	}

	@Override
	public void fillData() {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnSave:
			String canReply = null;
			String auth = null;
			String title = edtTitle.getText().toString();
			String content = edtContent.getText().toString();
			if (rbPrivate.isChecked()) {
				auth = "private";
			} else if (rbPublic.isChecked()) {
				auth = "public";
			} else {
				auth = "friend";
			}
			if (cbCanReply.isChecked()) {
				canReply = "yes";
			} else {
				canReply = "no";
			}
			createNewNote(title, content, auth, canReply);
			break;
		case R.id.btnCancel:
			finish();
			break;

		}

	}

	private void createNewNote(String title, String content, String pub, String canReply) {
		new AsyncTask<String, Void, Boolean>() {

			protected void onPreExecute() {
				pd.setMessage("正在保存");
				pd.show();
				super.onPreExecute();
			};

			protected void onPostExecute(Boolean result) {
				pd.dismiss();
				if (result) {
					setResult(200);
					finish();
				} else {
					showToast("发表日记失败");
				}
				super.onPostExecute(result);
			};

			@Override
			protected Boolean doInBackground(String... params) {
				String title = params[0];
				String content = params[1];
				String pub = params[2];
				String canReply = params[3];
				try {
					if(isEditNote){
						doubanService.updateNote(note.getNoteEntry(), new PlainTextConstruct(title), new PlainTextConstruct(content), pub, canReply);
					}else{
						doubanService.createNote(new PlainTextConstruct(title), new PlainTextConstruct(content), pub, canReply);
					}
					return true;
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
			}
		}.execute(title, content, pub, canReply);

	}

}
