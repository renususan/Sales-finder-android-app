/*Author -Renu Thomas
 * */
package ds.bestbuysale;

import ds.bestbuysale.R;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

/*
 * Because this application needs access to the Internet, you need to add the appropriate permissions to the Android manifest file. 
 * Open the AndroidManifest.xml file and add the following as a child of the <manifest> element:
 * <uses-permission android:name="android.permission.INTERNET" />
 */

public class SaleFinder extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		/*
		 * The click listener will need a reference to this object, so that upon
		 * successfully finding a picture from Flickr, it can callback to this
		 * object with the resulting picture Bitmap & String array. The "this"
		 * of the OnClick will be the OnClickListener, not this SaleFinder.
		 */
		final SaleFinder sf = this;

		/*
		 * Find the "submit" button, and add a listener to it
		 */
		Button submitButton = (Button) findViewById(R.id.submit);

		// Add a listener to the send button
		submitButton.setOnClickListener(new OnClickListener() {
			public void onClick(View viewParam) {
				String searchTerm = ((EditText) findViewById(R.id.searchTerm))
						.getText().toString();
				GetSaleDetails gp = new GetSaleDetails();
				gp.search(searchTerm, sf); // Done asynchronously in another
											// thread. It calls
											// ip.pictureReady() in this thread
											// when complete.
				GetSaleDetailsText gp1 = new GetSaleDetailsText();
				gp1.search(searchTerm, sf);
			}
		});
	}

	/*method to populate the image view*/
	public void pictureReady(Bitmap picture) {
		ImageView pictureView = (ImageView) findViewById(R.id.interestingPicture);
		TextView textView1 = (TextView) findViewById(R.id.editText1);

		if (picture != null) {
			pictureView.setImageBitmap(picture);
			pictureView.setVisibility(View.VISIBLE);

		} else {
			pictureView.setImageResource(R.drawable.icon);
			pictureView.setVisibility(View.INVISIBLE);
			textView1.setText("Image not available");
			textView1.setTextSize(11);
		}
		// searchView.setText("");
		pictureView.invalidate();
	}

	/* method to populate the text views*/
	public void textReady(String[] salesDetails) {

		TextView editTextView1 = (TextView) findViewById(R.id.editText1);
		TextView editTextView2 = (TextView) findViewById(R.id.editText2);
		TextView editTextView3 = (TextView) findViewById(R.id.editText3);

		if (salesDetails != null && salesDetails.length != 0) {
			editTextView1.setText(salesDetails[0]);
			editTextView1.setTextSize(11);
			editTextView2.setText("Sale Price: $" + salesDetails[1]);
			editTextView2.setTextSize(11);
			editTextView3.setText("Regular Price: $" + salesDetails[2]);
			editTextView3.setTextSize(11);
			editTextView2.setVisibility(View.VISIBLE);
			editTextView3.setVisibility(View.VISIBLE);

		} else {
			editTextView1
					.setText("The item you are looking for is not on sale");
			editTextView2.setVisibility(View.INVISIBLE);
			editTextView3.setVisibility(View.INVISIBLE);

		}

	}

}
