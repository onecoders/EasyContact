package my.project.easycontact.adapter;

import java.io.InputStream;
import java.util.List;

import my.project.easycontact.R;
import my.project.easycontact.model.ContactItem;
import my.project.easycontact.util.ImageUtil;
import my.project.easycontact.util.MToast;
import android.content.ActivityNotFoundException;
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

	private Context mContext;

	public AdaContact(Context context, List<ContactItem> objects) {
		super(context, 0, objects);
		this.mContext = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.list_item, null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final ContactItem item = getItem(position);

		String currentAlpha = item.getAlpha();
		String previewAlpha = (position - 1) >= 0 ? getItem(position - 1)
				.getAlpha() : " ";
		holder.setContent(item, previewAlpha, currentAlpha);
		return convertView;
	}

	private final class ViewHolder {
		TextView alpha;
		ImageView photo;
		TextView name, number;
		TextView nameInCard, company, phone, email, address;
		Button dial, sms, mail;

		public ViewHolder(View v) {
			alpha = (TextView) v.findViewById(R.id.alpha_text);
			photo = (ImageView) v.findViewById(R.id.photo);
			name = (TextView) v.findViewById(R.id.name);
			number = (TextView) v.findViewById(R.id.number);
			nameInCard = (TextView) v.findViewById(R.id.expandable)
					.findViewById(R.id.name_in_card);
			company = (TextView) v.findViewById(R.id.expandable).findViewById(
					R.id.company);
			phone = (TextView) v.findViewById(R.id.expandable).findViewById(
					R.id.phone);
			email = (TextView) v.findViewById(R.id.expandable).findViewById(
					R.id.email);
			address = (TextView) v.findViewById(R.id.expandable).findViewById(
					R.id.address);
			dial = (Button) v.findViewById(R.id.expandable).findViewById(
					R.id.btn_dial);
			sms = (Button) v.findViewById(R.id.expandable).findViewById(
					R.id.btn_sms);
			mail = (Button) v.findViewById(R.id.expandable).findViewById(
					R.id.btn_mail);
		}

		public void setContent(final ContactItem item, String previewAlpha,
				String currentAlpha) {
			name.setText(item.getName());
			number.setText(item.getNumber());

			// photoid 大于0 表示联系人有头像 如果没有给此人设置头像则给他一个默认的
			Bitmap contactPhoto;
			if (item.getPhotoid() > 0) {
				Uri uri = ContentUris.withAppendedId(
						ContactsContract.Contacts.CONTENT_URI,
						item.getContactid());
				InputStream input = ContactsContract.Contacts
						.openContactPhotoInputStream(
								mContext.getContentResolver(), uri);
				contactPhoto = BitmapFactory.decodeStream(input);
				contactPhoto = ImageUtil.getRoundedCornerBitmap(contactPhoto,
						10);
			} else {
				contactPhoto = BitmapFactory.decodeResource(
						mContext.getResources(), R.drawable.ic_avatar);
			}
			photo.setImageBitmap(contactPhoto);

			if (!previewAlpha.equals(currentAlpha)) {
				alpha.setVisibility(View.VISIBLE);
				alpha.setText(currentAlpha);
			} else {
				alpha.setVisibility(View.GONE);
			}
			nameInCard.setText(item.getName());
			company.setText(item.getCompany());
			phone.setText(item.getNumber());
			email.setText(item.getEmail());
			address.setText(item.getOfficeLocation());

			OnClickListener l = new OnClickListener() {

				@Override
				public void onClick(View v) {
					switch (v.getId()) {
					case R.id.btn_dial:
						dial(item.getNumber());
						break;
					case R.id.btn_sms:
						sendSMS(item.getNumber());
						break;
					case R.id.btn_mail:
						sendMail(item.getEmail());
						break;
					default:
						break;
					}
				}
			};
			dial.setOnClickListener(l);
			sms.setOnClickListener(l);
			mail.setOnClickListener(l);
		}
	}

	private void dial(String phoneNum) {
		// 调用系统方法拨打电话
		mContext.startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
				+ phoneNum)));
	}

	private void sendSMS(String phoneNum) {
		mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("sms:"
				+ phoneNum)));
	}

	private void sendMail(String address) {
		Intent i = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto",
				address, null));
		try {
			mContext.startActivity(Intent.createChooser(i, mContext
					.getResources().getString(R.string.please_choose)));
		} catch (ActivityNotFoundException e) {
			MToast.showText(mContext, R.string.no_choose);
		}
	}

}
