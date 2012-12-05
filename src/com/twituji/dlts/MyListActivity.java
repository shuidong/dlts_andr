package com.twituji.dlts;

import java.util.ArrayList;

import android.app.ListActivity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;

public class MyListActivity extends ListActivity {
	protected RssListAdapter mAdapter;
	public RssListAdapter getMAdapter() {
		return mAdapter;
	}

	protected TextView createAt;
	protected static MySQLiteHelper myHelper;
	
	//情報取る時間を設定
	public void setCreateAt(String createAt) {
		this.createAt.setText(this.getApplicationContext().getString(
				R.string.createAt) + createAt);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		myHelper = new MySQLiteHelper(this, "dlts.db", null, 1);
	}

	public static ArrayList<String> queryData() {
		ArrayList<String> rst = new ArrayList<String>();
		SQLiteDatabase db = myHelper.getWritableDatabase();
		Cursor cursor = db.query("fav_info", new String[] {"name"}, null, null, null, null, null);
		for(cursor.moveToFirst(); !(cursor.isAfterLast()); cursor.moveToNext()){
			rst.add(cursor.getString(0));
		}
		cursor.close();
		db.close();
		return rst;
	}
}
