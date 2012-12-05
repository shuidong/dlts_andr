package com.twituji.dlts;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class RssListAdapter extends ArrayAdapter<Item> {
	private LayoutInflater mInflater;
	private TextView mTitle;
	private TextView mDescr;

	public RssListAdapter(Context context, List<Item> objects) {
		super(context, 0, objects);
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	// 1行ごとのビューを生成する
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;

		if (convertView == null) {
			view = mInflater.inflate(R.layout.item_row, null);
		}

		// 現在参照しているリストの位置からItemを取得する
		Item item = this.getItem(position);
		if (item != null) {
			// Itemから必要なデータを取り出し、それぞれTextViewにセットする
			String title = item.getTitle().toString();
			mTitle = (TextView) view.findViewById(R.id.item_title);
			mTitle.setText(title);
			String descr = item.getDescription().toString().trim();
			mDescr = (TextView) view.findViewById(R.id.item_descr);
			mDescr.setText(descr);
			
			if(Const.TS_LEVEL1.equals(descr)){
				mDescr.setTextColor(Color.GREEN);
			}
			if(Const.TS_LEVEL2.equals(descr)){
				mDescr.setTextColor(Color.YELLOW);
			}
			if(Const.TS_LEVEL3.equals(descr)){
				mDescr.setTextColor(Color.RED);
			}
		}
		return view;
	}
}

