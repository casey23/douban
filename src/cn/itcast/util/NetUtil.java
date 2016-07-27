package cn.itcast.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.TextExtractor;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import cn.itcast.domain.NewBook;

import com.example.douban.R;
import com.google.gdata.client.douban.DoubanService;

public class NetUtil {

	public static String isNeedCaptcha(Context context) throws Exception {
		String loginUrl = context.getResources().getString(R.string.loginUrl);

		URL url = new URL(loginUrl);

		URLConnection conn = url.openConnection();
		Source source = new Source(conn);
		List<Element> elements = source.getAllElements("input");
		for (Element element : elements) {
			String result = element.getAttributeValue("name");
			if ("captcha-id".equals(result)) {
				return element.getAttributeValue("value");
			}
		}
		return null;
	}

	public static DoubanService getDoubanService(Context context) {

		String apiKey = "059ef56f6b705e1210dce04e42511a36";
		String secret = "006ba4a489916c13";
		DoubanService myService = new DoubanService("subApplication", apiKey, secret);
		String[] arr = getServiceToken(context);
		System.out.println(arr[0]);
		System.out.println(arr[1]);
		myService.setAccessToken(arr[0], arr[1]);
		return myService;
	}

	/*
	 * 获取验证码图片
	 */
	public static Bitmap getCaptcha(Context context, String result) throws Exception {
		String captchaUrl = context.getResources().getString(R.string.captchaUrl);
		URL url = new URL(captchaUrl + result + "&size=s");
		URLConnection connection = url.openConnection();
		InputStream in = connection.getInputStream();
		Bitmap bitmap = BitmapFactory.decodeStream(in);
		return bitmap;

	}

	public static boolean login(String name, String pwd, String captcha, String captchaId, Context context) throws Exception {
		boolean flag = false;
		String apiKey = "059ef56f6b705e1210dce04e42511a36";
		String secret = "006ba4a489916c13";

		DoubanService myService = new DoubanService("subApplication", apiKey, secret);
		System.out.println("please paste the url in your webbrowser, complete the authorization then come back:");
		String url = myService.getAuthorizationUrl(null);
		System.out.println(url);
		String oauth_token = url.substring(url.indexOf("=") + 1, url.length());
		CookieStore cookie = getCookie(name, pwd, captcha, captchaId, context, oauth_token);

		getToken(url, cookie);

		ArrayList<String> token = myService.getAccessToken();
		String accessToken = token.get(0);
		String tokenSecret = token.get(1);
		SharedPreferences sp = context.getSharedPreferences("config", context.MODE_PRIVATE);
		Editor edit = sp.edit();
		edit.putString("accessToken", accessToken);
		edit.putString("tokenSecret", tokenSecret);
		edit.commit();
		System.out.println(accessToken);
		System.out.println(tokenSecret);
		flag = true;
		return flag;
	}

	/**
	 * 页面授权。实现授权操作
	 * 
	 * @param url
	 *            跳转的地址
	 * @param cookie
	 *            cookie
	 * @throws Exception
	 */
	private static void getToken(String url, CookieStore cookie) throws Exception {
		HttpPost post1 = new HttpPost(url);
		String oauth_token = url.substring(url.indexOf("=") + 1, url.length());
		System.out.println(oauth_token);
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("ck", "Gauy"));
		pairs.add(new BasicNameValuePair("oauth_token", oauth_token));
		pairs.add(new BasicNameValuePair("oauth_callback", ""));
		pairs.add(new BasicNameValuePair("ssid", "876739f5"));
		pairs.add(new BasicNameValuePair("confirm", "同意"));
		UrlEncodedFormEntity entity2 = new UrlEncodedFormEntity(pairs, "utf-8");
		post1.setEntity(entity2);
		DefaultHttpClient client2 = new DefaultHttpClient();
		client2.setCookieStore(cookie);
		HttpResponse response = client2.execute(post1);
		InputStream is = response.getEntity().getContent();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = is.read(buffer)) != -1) {
			bos.write(buffer, 0, len);
		}
		is.close();
	}

	/**
	 * 页面登陆
	 * 
	 * @param name
	 *            账号
	 * @param pwd
	 *            密码
	 * @param captcha
	 *            验证码
	 * @param captchaId
	 *            验证码id
	 * @param context
	 *            上下文
	 * @param token
	 *            授权token
	 * @return 登陆成功后的cookie
	 * @throws Exception
	 */
	private static CookieStore getCookie(String name, String pwd, String captcha, String captchaId, Context context, String token) throws Exception {
		String loginUrl = context.getResources().getString(R.string.loginUrl);
		HttpPost post = new HttpPost(loginUrl);
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("source", "None"));
		pairs.add(new BasicNameValuePair("redir", "https://www.douban.com/service/auth/authorize?oauth_token=" + token));
		pairs.add(new BasicNameValuePair("form_email", name));
		pairs.add(new BasicNameValuePair("form_password", pwd));
		pairs.add(new BasicNameValuePair("captcha-solution", captcha));
		pairs.add(new BasicNameValuePair("captcha-id", captchaId));
		pairs.add(new BasicNameValuePair("login", "登录"));

		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(pairs, "utf-8");
		post.setEntity(entity);
		// 创建一个浏览器
		DefaultHttpClient client = new DefaultHttpClient();
		// 完成了用户登录豆瓣操作
		HttpResponse response = client.execute(post);
		// 返回状态码
		int code = response.getStatusLine().getStatusCode();
		System.out.println(code);
		Source source = new Source(response.getEntity().getContent());
		String string = source.toString();
		CookieStore CookieStore = client.getCookieStore();
		return CookieStore;
	}

	private static String[] getServiceToken(Context context) {
		String[] arr = new String[2];
		SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		String accessToken = sp.getString("accessToken", "");
		String tokenSecret = sp.getString("tokenSecret", "");
		arr[0] = accessToken;
		arr[1] = tokenSecret;
		return arr;

	}

	/**
	 * 获取新书
	 * 
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public static List<NewBook> getNewBook(Context context) throws Exception {
		String path = context.getResources().getString(R.string.newBookPath);

		URL url = new URL(path);
		URLConnection con = url.openConnection();
		Source source = new Source(con);
		List<Element> liElements = source.getAllElements("li");
		List<NewBook> books = new ArrayList<NewBook>();
		for (Element liElement : liElements) {
			List<Element> childElements = liElement.getChildElements();
			if (childElements.size() == 2) {
				String div = childElements.get(0).getAttributeValue("class");
				if ("detail-frame".equals(div)) {
					NewBook book = new NewBook();
					List<Element> details = childElements.get(0).getChildElements();
					String name = details.get(0).getTextExtractor().toString();
					book.setName(name);
					String description = details.get(1).getTextExtractor().toString();
					book.setDescription(description);
					String summary = details.get(2).getTextExtractor().toString();
					book.setSummary(summary);
					String href = childElements.get(1).getAttributeValue("href");
					book.setIsbn(href);
					String imgPath = childElements.get(1).getChildElements().get(0).getAttributeValue("src").toString();
					book.setImgPath(imgPath);

					books.add(book);
				}
			}
		}
		return books;

	}

	/**
	 * 获取新书的ISBN
	 * @param path 新书的路径
	 * @return
	 */
	public static String getNewBookISBN(String path) {
		String isbn = null;
		if (path == null)
			return null;
		try {
			URL url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			Source source = new Source(conn.getInputStream());
			String resu = source.toString();
			BufferedReader br = new BufferedReader(new StringReader(resu));
			String temp = null;
			StringBuilder sb = new StringBuilder();
			while ((temp = br.readLine()) != null) {
				sb.append(temp);
				sb.append("\n");

				if (temp.contains("ISBN:")) {
					int start = temp.indexOf("</span>")+"</span>".length();
					int end = temp.indexOf("<br/>");
					isbn = temp.substring(start, end).trim().toString();
				}
			}
			return isbn;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

}
