package com.twituji.dlts;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemLongClickListener;

public class FavReaderActivity extends MyListActivity {
	

	String addLineNm;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		createAt = (TextView) findViewById(R.id.createAt);

		if (NetworkManager.isOffline(this.getApplicationContext())) {
			// オフラインの場合、警告を表示
			AlertDialog.Builder builder = new AlertDialog.Builder(this)
					.setIcon(android.R.drawable.ic_dialog_alert).setTitle(
							R.string.error) // ダイアログのタイトル
					.setMessage(R.string.error_network_offline) // ダイアログに表示するメッセージ
					.setPositiveButton(android.R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
									// OKボタンを押した場合に追加の処理があればここに書く
									Intent mIntent = new Intent("/");
									ComponentName comp = new ComponentName(
											"com.android.settings",
											"com.android.settings.WirelessSettings");
									mIntent.setComponent(comp);
									mIntent
											.setAction("android.intent.action.VIEW");
									startActivityForResult(mIntent, 0);
								}
							}).setNeutralButton(R.string.exit,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									dialog.cancel();
									FavReaderActivity.this.finish();
								}
							});
			builder.show();

		} else {
			// Itemオブジェクトを保持するためのリストを生成し、アダプタに追加する
			mAdapter = new RssListAdapter(this, new ArrayList<Item>());
			// タスクを起動する
			RssParserTask task = new RssParserTask(this, mAdapter, queryData());
			task.execute(Const.DATA_XML_URL);
		}
		
		this.getListView().setOnItemLongClickListener(
				new OnItemLongClickListener() {
					@Override
					public boolean onItemLongClick(AdapterView<?> arg0,
							View view, int position, long arg3) {
						Item item = (Item) ((RssListAdapter) (arg0.getAdapter()))
								.getItem(position);
						addLineNm = item.getTitle();
						new AlertDialog.Builder(FavReaderActivity.this).setTitle(
								R.string.delav_title)
								// ダイアログのタイトル
								.setMessage(
										FavReaderActivity.this
												.getApplicationContext()
												.getString(R.string.delFav_msg)
												+ addLineNm) // ダイアログに表示するメッセージ
								.setPositiveButton(android.R.string.ok,
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int which) {
												dialog.dismiss();
												// OKボタンを押した場合に追加の処理があればここに書く
												FavReaderActivity.this.delRecord(addLineNm);
											}
										}).setNeutralButton(
										android.R.string.cancel,
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int whichButton) {
												dialog.cancel();
											}
										}).show();
						return true;
					}

				});
	}
	public int delRecord(String lineNM){
		for(int i = 0; i < mAdapter.getCount(); i++){
			Item it = mAdapter.getItem(i);
			if(lineNM.equalsIgnoreCase(it.getTitle())){
				mAdapter.remove(it);
			}
		}
		
		SQLiteDatabase db = myHelper.getWritableDatabase();
		try {
			db.execSQL("delete from fav_info where name ='" + lineNM
					+ "'");
			return 0;
		} catch (Exception ex) {
			return 1;
		} finally {
			db.close();
		}
		
	}
}
