package my.project.easycontact.adapter;

import java.io.InputStream;
import java.util.List;

import my.project.easycontact.R;
import my.project.easycontact.model.ContactItem;
import my.project.easycontact.util.ImageUtil;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class AdaContact extends ArrayAdapter<ContactItem> {

	public AdaContact(Context context, List<ContactItem> objects) {
		super(context, 0, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.list_item, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final ContactItem item = getItem(position);
		holder.name.setText(item.getName());
		holder.number.setText(item.getNumber());

		// photoid 大于0 表示联系人有头像 如果没有给此人设置头像则给他一个默认的
		Bitmap contactPhoto;
		if (item.getPhotoid() > 0) {
			Uri uri = ContentUris.withAppendedId(
					ContactsContract.Contacts.CONTENT_URI, item.getContactid());
			InputStream input = ContactsContract.Contacts
					.openContactPhotoInputStream(getContext()
							.getContentResolver(), uri);
			contactPhoto = BitmapFactory.decodeStream(input);
			contactPhoto = ImageUtil.getRoundedCornerBitmap(contactPhoto, 10);
		} else {
			contactPhoto = BitmapFactory.decodeResource(getContext()
					.getResources(), R.drawable.ic_avatar);
		}
		holder.photo.setImageBitmap(contactPhoto);

		String currentAlpha = item.getAlpha();
		String previewAlpha = (position - 1) >= 0 ? getItem(position - 1)
				.getAlpha() : " ";
		if (!previewAlpha.equals(currentAlpha)) {
			holder.alpha.setVisibility(View.VISIBLE);
			holder.alpha.setText(currentAlpha);
		} else {
			holder.alpha.setVisibility(View.GONE);
		}
		holder.nameInCard.setText(item.getName());
		holder.dial.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 调用系统方法拨打电话
				Intent dialIntent = new Intent(Intent.ACTION_CALL, Uri
						.parse("tel:" + item.getNumber()));
				getContext().startActivity(dialIntent);
			}
		});
		return convertView;
	}

	private final class ViewHolder {
		TextView alpha;
		ImageView photo;
		TextView name;
		TextView number;
		TextView nameInCard;
		Button dial;
		Button sms;
		Button mail;

		public ViewHolder(View v) {
			alpha = (TextView) v.findViewById(R.id.alpha_text);
			photo = (ImageView) v.findViewById(R.id.photo);
			name = (TextView) v.findViewById(R.id.name);
			number = (TextView) v.findViewById(R.id.number);
			nameInCard = (TextView) v.findViewById(R.id.expandable)
					.findViewById(R.id.name_in_card);
			dial = (Button) v.findViewById(R.id.expandable).findViewById(
					R.id.btn_dial);
			sms = (Button) v.findViewById(R.id.expandable).findViewById(
					R.id.btn_sms);
			mail = (Button) v.findViewById(R.id.expandable).findViewById(
					R.id.btn_mail);
		}

	}

}
