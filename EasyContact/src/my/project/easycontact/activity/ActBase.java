package my.project.easycontact.activity;

import my.project.easycontact.R;
import my.project.easycontact.util.MToast;
import my.project.easycontact.view.ProgressHUD;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockActivity;

/**
 * Base Activity
 * 
 * @author roy
 * @email onecoders@gmail.com
 */

public abstract class ActBase extends SherlockActivity implements
		OnCancelListener {

	private ProgressHUD mProgressHUD;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	protected void showProgressHUD() {
		mProgressHUD = ProgressHUD.show(this, getString(R.string.loading),
				true, true, this);
	}

	protected void dismissProgressHUD() {
		mProgressHUD.dismiss();
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		dismissProgressHUD();
	}

	protected void setMessage(String message) {
		mProgressHUD.setMessage(message);
	}

	protected void setMessage(int resId) {
		mProgressHUD.setMessage(getString(resId));
	}

	protected void showToast(int resId) {
		MToast.showText(this, resId);
	}

	protected void showToast(String msg) {
		MToast.showText(this, msg);
	}

}
