package com.twituji.dlts;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Xml;

public class RssParserTask extends AsyncTask<String, Integer, RssListAdapter> {
	private MyListActivity mActivity;
	private RssListAdapter mAdapter;
	private ProgressDialog mProgressDialog;
	
	private String mCreateAt;
	public ArrayList<String> filters;
	
	// コンストラクタ
	public RssParserTask(MyListActivity activity, RssListAdapter adapter) {
		mActivity = activity;
		mAdapter = adapter;
	}
	
	public RssParserTask(MyListActivity activity, RssListAdapter adapter, ArrayList<String> filters) {
		mActivity = activity;
		mAdapter = adapter;
		this.filters = filters;
	}

	// タスクを実行した直後にコールされる
	@Override
	protected void onPreExecute() {
		// プログレスバーを表示する
		mProgressDialog = new ProgressDialog(mActivity);
		mProgressDialog.setMessage(mActivity.getApplicationContext().getString(R.string.doing));
		mProgressDialog.show();
	}

	// バックグラウンドにおける処理を担う。タスク実行時に渡された値を引数とする
	@Override
	protected RssListAdapter doInBackground(String... params) {
		RssListAdapter result = null;
		try {
			// HTTP経由でアクセスし、InputStreamを取得する
			URL url = new URL(params[0]);
			InputStream is = url.openConnection().getInputStream();
			result = parseXml(is);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		// ここで返した値は、onPostExecuteメソッドの引数として渡される
		return result;
	}

	// メインスレッド上で実行される
	@Override
	protected void onPostExecute(RssListAdapter result) {
		mProgressDialog.dismiss();
		if(null == result){//APIサーバーと接続できない場合
			AlertDialog.Builder builder = new AlertDialog.Builder(mActivity)
			.setTitle(R.string.error)                    // ダイアログのタイトル
			.setIcon(android.R.drawable.ic_dialog_alert)
            .setMessage(R.string.error_database_inacitve)  // ダイアログに表示するメッセージ
            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    // OKボタンを押した場合に追加の処理があればここに書く
                }
            });
			builder.show();
		}else{
			if(null == mCreateAt){//交通情報サーバー接続できない場合
				AlertDialog.Builder builder = new AlertDialog.Builder(mActivity)
				.setTitle(R.string.error)                    // ダイアログのタイトル
				.setIcon(android.R.drawable.ic_dialog_alert)
	            .setMessage(R.string.error_cannot_get_data)  // ダイアログに表示するメッセージ
	            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int which) {
	                    dialog.dismiss();
	                    // OKボタンを押した場合に追加の処理があればここに書く
	                }
	            });
				builder.show();
			}else{
				mActivity.setCreateAt(mCreateAt);
				Comparator<Item> comparator = new MyComparator(InfoTabActivity.sort_by);
				result.sort(comparator);
				mActivity.setListAdapter(result);
			}
		}
		
	}

	// XMLをパースする
	public RssListAdapter parseXml(InputStream is) throws IOException, XmlPullParserException {
		XmlPullParser parser = Xml.newPullParser();
		try {
			parser.setInput(is, null);
			int eventType = parser.getEventType();
			Item currentItem = null;
			while (eventType != XmlPullParser.END_DOCUMENT) {
				String tag = null;
				switch (eventType) {
					case XmlPullParser.START_TAG:
						tag = parser.getName();
						if(tag.equals("created_at")){
							mCreateAt = parser.nextText();
						}else if (tag.equals("status")) {
							currentItem = new Item();
						} else if (currentItem != null) {
							if (tag.equals("area")) {
								currentItem.setTitle(parser.nextText());
							} else if (tag.equals("value")) {
								currentItem.setDescription(parser.nextText());
							} 
						}
						break;
					case XmlPullParser.END_TAG:
						tag = parser.getName();
						if (tag.equals("status")) {
							if(null == filters){
								mAdapter.add(currentItem);
							}else{
								for(String tmp : filters) {
									if(tmp.equals(currentItem.getTitle())){
										mAdapter.add(currentItem);
										break;
									}
								}
							}
						}
						break;
				}
				eventType = parser.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mAdapter;
	}

}
