package com.twituji.dlts;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost.TabSpec;

public class InfoTabActivity extends TabActivity {

	public static final int MENU_ITEM_RELOAD = Menu.FIRST;
	public static final int MENU_ITEM_SETTING = Menu.FIRST + 1;
	public static final int MENU_ITEM_ABOUT = Menu.FIRST + 2;

	public static int sort_by = MyComparator.BY_TITLE;
	public static int first_view = 0;
	SharedPreferences sp;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initSetting();
		setContentView(R.layout.tab);

		Resources res = getResources(); // Resource object to get Drawables
		TabHost tabHost = getTabHost(); // The activity TabHost
		TabSpec spec; // Resusable TabSpec for each tab
		Intent intent; // Reusable Intent for each tab

		// Create an Intent to launch an Activity for the tab (to be reused)
		intent = new Intent().setClass(this, ReaderActivity.class);

		// Initialize a TabSpec for each tab and add it to the TabHost
		spec = tabHost.newTabSpec("all").setIndicator(
				this.getApplicationContext().getString(R.string.all_lines))
		// res.getDrawable(R.drawable.ic_tab_artists))
				.setContent(intent);
		tabHost.addTab(spec);

		// Do the same for the other tabs
		intent = new Intent().setClass(this, FavReaderActivity.class);
		spec = tabHost.newTabSpec("fav").setIndicator(
				this.getApplicationContext().getString(R.string.fav_lines))
		// res.getDrawable(R.drawable.ic_tab_albums))
				.setContent(intent);
		tabHost.addTab(spec);

		tabHost.setCurrentTab(InfoTabActivity.first_view);
		//tabHost.setCurrentTab(0);
	}

	/**
	 * 初期化を実行 ソート順と初期画面を取得して、設定する さらに、マークされたラインを取得
	 */
	private void initSetting() {
		sp = this.getPreferences(MODE_WORLD_WRITEABLE);
		InfoTabActivity.sort_by = sp.getInt(Const.KEY_SORT_BY,
				MyComparator.BY_TITLE);
		InfoTabActivity.first_view = sp.getInt(Const.KEY_FIRST_VIEW, 0);
		
	}

	// MENUボタンを押したときの処理
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);
		// デフォルトではアイテムを追加した順番通りに表示する
		menu.add(0, MENU_ITEM_RELOAD, 0, this.getApplicationContext()
				.getString(R.string.menu_refresh));
		menu.add(0, MENU_ITEM_SETTING, 0, this.getApplicationContext()
				.getString(R.string.menu_setting));
		menu.add(0, MENU_ITEM_ABOUT, 0, this.getApplicationContext().getString(
				R.string.menu_about));
		return result;
	}

	// MENUの項目を押したときの処理
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// 更新
		case MENU_ITEM_RELOAD:
			// アダプタを初期化し、タスクを起動する
			ArrayList<Item> mItems = new ArrayList<Item>();
			
			if(this.getTabHost().getCurrentTab() == 0){
				RssListAdapter mAdapter = new RssListAdapter(this, mItems);
				// タスクはその都度生成する
				RssParserTask task = new RssParserTask((ReaderActivity) this
						.getCurrentActivity(), mAdapter);
				task.execute(Const.DATA_XML_URL);
			}else{
				RssListAdapter mAdapter = new RssListAdapter(this, new ArrayList<Item>());
				// タスクを起動する
				RssParserTask task = new RssParserTask((FavReaderActivity) this
						.getCurrentActivity(), mAdapter, MyListActivity.queryData());
				task.execute(Const.DATA_XML_URL);
			}
			return true;
			// 　ソフトについて
		case MENU_ITEM_ABOUT:
			AlertDialog.Builder builder = new AlertDialog.Builder(this)
					.setIcon(android.R.drawable.ic_dialog_info).setTitle(
							R.string.menu_about) // ダイアログのタイトル
					.setMessage(R.string.menu_about_msg) // ダイアログに表示するメッセージ
					.setPositiveButton(android.R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
									// OKボタンを押した場合に追加の処理があればここに書く
								}
							});
			builder.show();
			return true;
			// 　アプリ設定
		case MENU_ITEM_SETTING:
			View view = getLayoutInflater().inflate(R.layout.setting, null);
			//ライン名でソートを設定された場合
			if (InfoTabActivity.sort_by == MyComparator.BY_TITLE) {
				((RadioButton) view.findViewById(R.id.sortByName))
						.setChecked(true);
			} else {
				((RadioButton) view.findViewById(R.id.sortBySts))
						.setChecked(true);
			}

			//初期画面を全部線路に設定された場合
			if (InfoTabActivity.first_view == 0) {
				((RadioButton) view.findViewById(R.id.allLine))
						.setChecked(true);
			} else {
				((RadioButton) view.findViewById(R.id.favLine))
						.setChecked(true);
			}

			RadioGroup sortWay = (RadioGroup) view.findViewById(R.id.sortWay);
			sortWay.setVisibility(View.VISIBLE);
			sortWay.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(RadioGroup arg0, int arg1) {
					//ソート順が変わる場合
					if (arg1 == R.id.sortByName) {
						InfoTabActivity.sort_by = MyComparator.BY_TITLE;
					} else {
						InfoTabActivity.sort_by = MyComparator.BY_DES;
					}
				}
			});

			RadioGroup firstView = (RadioGroup) view.findViewById(R.id.firstView);
			firstView.setVisibility(View.VISIBLE);
			firstView.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(RadioGroup arg0, int arg1) {
					//初期画面が変わる場合
					if (arg1 == R.id.allLine) {
						InfoTabActivity.first_view = 0;
					} else {
						InfoTabActivity.first_view = 1;
					}
				}
			});

			//設定ダイアログを出して
			new AlertDialog.Builder(this)
			.setTitle(getResources().getString(R.string.menu_setting))
			.setView(view).setPositiveButton(
					getResources().getString(android.R.string.ok),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							SharedPreferences.Editor ed = sp.edit();
							ed.putInt(Const.KEY_SORT_BY,
									InfoTabActivity.sort_by);
							ed.putInt(Const.KEY_FIRST_VIEW,
									InfoTabActivity.first_view);
							ed.commit();
							((MyListActivity) InfoTabActivity.this.getCurrentActivity()).getMAdapter().sort(new MyComparator(InfoTabActivity.sort_by));
						}
					}).setNegativeButton(
					getResources().getString(android.R.string.cancel),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					}).show();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
