package my.project.easycontact.adapter;

import java.util.List;

import my.project.easycontact.R;
import my.project.easycontact.model.ContactItem;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

		ContactItem item = getItem(position);
		holder.name.setText(item.getName());
		holder.number.setText(item.getNumber());

		String currentAlpha = item.getAlpha();
		String previewAlpha = (position - 1) >= 0 ? getItem(position - 1)
				.getAlpha() : " ";
		if (!previewAlpha.equals(currentAlpha)) {
			holder.alpha.setVisibility(View.VISIBLE);
			holder.alpha.setText(currentAlpha);
		} else {
			holder.alpha.setVisibility(View.GONE);
		}
		return convertView;
	}

	private final class ViewHolder {
		TextView alpha;
		TextView name;
		TextView number;

		public ViewHolder(View v) {
			alpha = (TextView) v.findViewById(R.id.alpha_text);
			name = (TextView) v.findViewById(R.id.name);
			number = (TextView) v.findViewById(R.id.number);
		}

	}

}
