package my.project.easycontact.util;

import android.content.Context;
import android.widget.Toast;

public class MToast {

	private static Toast toast = null;

	public static void showText(Context context, int resId) {
		showText(context, context.getString(resId));
	}

	public static void showText(Context context, String msg) {
		if (toast == null) {
			toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
		} else {
			toast.setText(msg);
		}
		toast.show();
	}

}
