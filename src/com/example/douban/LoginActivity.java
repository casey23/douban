package com.example.douban;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import cn.itcast.util.NetUtil;

public class LoginActivity extends Activity implements OnClickListener {
	protected static final int NEED_CAPTCHA = 10;
	protected static final int NOT_NEED_CAPTCHA = 11;
	protected static final int GET_CAPTCHA_ERROR = 12;
	protected static final int LOGIN_SUCCESS = 13;
	protected static final int LOGIN_FAIL = 14;
	private EditText edtName;
	private EditText edtPass;
	private LinearLayout llCaptcha;
	private EditText edtCaptchaValue;
	private ImageView imgCaptcha;
	private Button btnLogin, btnExit;
	private ProgressDialog pd;
	private String realCaptchaId=null;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			pd.dismiss();
			switch (msg.what) {
			case NEED_CAPTCHA:
				llCaptcha.setVisibility(View.VISIBLE);
				Bitmap bitmap = (Bitmap) msg.obj;
				imgCaptcha.setImageBitmap(bitmap);
				break;
			case NOT_NEED_CAPTCHA:
				break;
			case GET_CAPTCHA_ERROR:
				Toast.makeText(getApplicationContext(), "查询验证码失败", 0).show();
				break;
			case LOGIN_SUCCESS:
				finish();
				break;
			case LOGIN_FAIL:
				Toast.makeText(getApplicationContext(), "登陆失败", 0).show();
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		setupView();
		setListener();
	}

	// 设置点击事件
	private void setListener() {
		btnLogin.setOnClickListener(this);
		btnExit.setOnClickListener(this);
		imgCaptcha.setOnClickListener(this);

	}

	private void setupView() {

		edtName = (EditText) this.findViewById(R.id.edtEmail);
		edtPass = (EditText) this.findViewById(R.id.edtPass);
		llCaptcha = (LinearLayout) this.findViewById(R.id.ll_captcha);
		edtCaptchaValue = (EditText) this.findViewById(R.id.edtCaptchaValue);
		imgCaptcha = (ImageView) this.findViewById(R.id.imgCaptcha);
		btnLogin = (Button) this.findViewById(R.id.btnLogin);
		btnExit = (Button) this.findViewById(R.id.btnExit);
		showCaptcha();
	}

	/**
	 * 查询验证码
	 */
	private void showCaptcha() {
		pd = new ProgressDialog(this);
		pd.setMessage("正在查询验证码");
		pd.show();
		new Thread() {
			public void run() {
				try {
					realCaptchaId = NetUtil.isNeedCaptcha(getApplicationContext());
					if (realCaptchaId != null) {
						Bitmap captcha = NetUtil.getCaptcha(getApplicationContext(), realCaptchaId);
						Message msg = Message.obtain();
						msg.what = NEED_CAPTCHA;
						msg.obj = captcha;
						handler.sendMessage(msg);
					} else {
						Message msg = Message.obtain();
						msg.what = NOT_NEED_CAPTCHA;
						handler.sendMessage(msg);
					}
				} catch (Exception e) {
					e.printStackTrace();
					Message msg = Message.obtain();
					msg.what = GET_CAPTCHA_ERROR;
					handler.sendMessage(msg);
				}

			};
		}.start();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnLogin:
			String name = edtName.getText().toString();
			String pass = edtPass.getText().toString();
			String captchaValue = edtCaptchaValue.getText().toString();
			if ("".equals(name) || "".equals(pass)) {
				Toast.makeText(getApplicationContext(), "用户名或密码不能为空", 0).show();
				return;
			} else {
				if (realCaptchaId != null) {
					if ("".equals(captchaValue)) {
						Toast.makeText(getApplicationContext(), "验证码不能为空", 0).show();
						return;
					}
				}
				// 登录操作
				login(name,pass,captchaValue);
			}
			break;

		case R.id.btnExit:
			finish();
			break;
		case R.id.imgCaptcha:
			showCaptcha();
			break;
		}

	}

	private void login(final String name, final String pass, final String captchaValue) {
		pd.setMessage("正在登陆");
		pd.show();
		new Thread() {
			public void run() {
				try {
					boolean flag = NetUtil.login(name, pass, captchaValue, realCaptchaId, getApplicationContext());
					if(flag){
						Message msg=Message.obtain();
						msg.what=LOGIN_SUCCESS;
						handler.sendMessage(msg);
					}else{
						Message msg=Message.obtain();
						msg.what=LOGIN_FAIL;
						handler.sendMessage(msg);
					}
				
				} catch (Exception e) {
					e.printStackTrace();
					Message msg=Message.obtain();
					msg.what=LOGIN_FAIL;
					handler.sendMessage(msg);
				}
			};
		}.start();
	}
}
