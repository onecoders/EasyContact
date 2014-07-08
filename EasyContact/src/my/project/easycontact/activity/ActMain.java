package my.project.easycontact.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import my.project.easycontact.R;
import my.project.easycontact.adapter.AdaContact;
import my.project.easycontact.model.ContactItem;
import my.project.easycontact.util.Utils;
import my.project.easycontact.view.AlphaView;
import my.project.easycontact.view.AlphaView.OnAlphaChangedListener;
import my.project.easycontact.view.SlideExpandableListView.SlideExpandableListAdapter;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Organization;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;

public class ActMain extends ActBase implements OnAlphaChangedListener {

	private ListView listView;
	private List<ContactItem> list;
	private AdaContact adapter;

	private AlphaView alphaView;
	private TextView overlay;

	private WindowManager windowManager;
	private AsyncQueryHandler queryHandler;
	private HashMap<String, Integer> alphaIndexer; // 存放存在的汉语拼音首字母和与之对应的列表位置
	private OverlayThread overlayThread;

	private Handler handler = new Handler();

	private static final String[] PROJECTION = { Phone._ID, Phone.DISPLAY_NAME,
			Phone.NUMBER, Photo.PHOTO_ID, Phone.CONTACT_ID, Organization.DATA,
			Organization.OFFICE_LOCATION, Email.DATA, "sort_key" };

	private static final int PHONES_ID_INDEX = 0;
	private static final int PHONES_DISPLAY_NAME_INDEX = 1;
	private static final int PHONES_NUMBER_INDEX = 2;
	private static final int PHONES_PHOTO_ID_INDEX = 3;
	private static final int PHONES_CONTACT_ID_INDEX = 4;
	private static final int ORGANIZATION_COMPANY_INDEX = 5;
	private static final int ORGANIZATION_OFFICE_LOCATION_INDEX = 6;
	private static final int EMAIL_ADDRESS_INDEX = 7;
	private static final int PHONES_SORT_KEY = 8;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_main);
		init();
	}

	private void init() {
		queryHandler = new MyAsyncQueryHandler(getContentResolver());
		list = new ArrayList<ContactItem>();
		alphaIndexer = new HashMap<String, Integer>();
		overlayThread = new OverlayThread();
		intitWidget();
		initOverlay();
	}

	// 初始化控件
	private void intitWidget() {
		listView = (ListView) findViewById(R.id.list_view);
		alphaView = (AlphaView) findViewById(R.id.alphaView);
		alphaView.setOnAlphaChangedListener(this);
	}

	// 初始化汉语拼音首字母弹出提示框
	private void initOverlay() {
		LayoutInflater inflater = LayoutInflater.from(this);
		overlay = (TextView) inflater.inflate(R.layout.overlay, null);
		overlay.setVisibility(View.INVISIBLE);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_APPLICATION,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
						| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
				PixelFormat.TRANSLUCENT);
		windowManager = (WindowManager) this
				.getSystemService(Context.WINDOW_SERVICE);
		windowManager.addView(overlay, lp);
	}

	@Override
	protected void onResume() {
		super.onResume();
		startQuery();
	}

	private void startQuery() {
		showProgressHUD();
		queryHandler.startQuery(1, null, Phone.CONTENT_URI, PROJECTION,
				"data1 is not null", null, "sort_key COLLATE LOCALIZED asc");
	}

	@Override
	protected void onStop() {
		try {
			windowManager.removeViewImmediate(overlay);
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onStop();
	}

	// 异步查询类
	private class MyAsyncQueryHandler extends AsyncQueryHandler {

		public MyAsyncQueryHandler(ContentResolver cr) {
			super(cr);
		}

		@Override
		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
			list.clear();
			if (cursor != null) {
				while (cursor.moveToNext()) {
					ContactItem item = new ContactItem();
					item.set_id(cursor.getLong(PHONES_ID_INDEX));
					item.setName(cursor.getString(PHONES_DISPLAY_NAME_INDEX));
					item.setNumber(Utils.formatNumber(cursor
							.getString(PHONES_NUMBER_INDEX)));
					item.setPhotoid(cursor.getLong(PHONES_PHOTO_ID_INDEX));
					item.setContactid(cursor.getLong(PHONES_CONTACT_ID_INDEX));
					item.setCompany(cursor
							.getString(ORGANIZATION_COMPANY_INDEX));
					item.setOfficeLocation(cursor
							.getString(ORGANIZATION_OFFICE_LOCATION_INDEX));
					item.setEmail(cursor.getString(EMAIL_ADDRESS_INDEX));
					item.setAlpha(Utils.formatAlpha(cursor
							.getString(PHONES_SORT_KEY)));
					list.add(item);
				}
				cursor.close();
			}
			if (list.size() > 0) {
				initAlphaIndexer();
				setAdapter();
			}
			dismissProgressHUD();
		}

	}

	private void initAlphaIndexer() {
		for (int i = 0; i < list.size(); i++) {
			// 当前汉语拼音首字母
			String currentAlpha = list.get(i).getAlpha();
			// 上一个汉语拼音首字母，如果不存在为“ ”
			String previewAlpha = (i - 1) >= 0 ? list.get(i - 1).getAlpha()
					: " ";
			if (!previewAlpha.equals(currentAlpha)) {
				String alpha = list.get(i).getAlpha();
				alphaIndexer.put(alpha, i);
			}
		}
	}

	private void setAdapter() {
		if (adapter == null) {
			adapter = new AdaContact(this, list);
			listView.setAdapter(new SlideExpandableListAdapter(adapter));
		} else {
			adapter.notifyDataSetChanged();
		}
	}

	// 设置overlay不可见
	private class OverlayThread implements Runnable {

		@Override
		public void run() {
			overlay.setVisibility(View.GONE);
		}

	}

	@Override
	public void OnAlphaChanged(String s, int index) {
		if (s != null && s.trim().length() > 0) {
			overlay.setText(s);
			overlay.setVisibility(View.VISIBLE);
			handler.removeCallbacks(overlayThread);
			handler.postDelayed(overlayThread, 700);
			if (alphaIndexer.get(s) != null) {
				int position = alphaIndexer.get(s);
				listView.setSelection(position);
			}
		}
	}

}
