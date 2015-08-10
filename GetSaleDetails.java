/*Author -Renu Thomas
 * This class return image of the item related to the item
 * */
package ds.bestbuysale;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

/*
 * This class provides capabilities to search for the image of an item on sale in Best Buy given a search term/item.  The method "search" is the entry to the class.
 * Network operations cannot be done from the UI thread, therefore this class makes use of an AsyncTask inner class that will do the network
 * operations in a separate worker thread.  However, any UI updates should be done in the UI thread so avoid any synchronization problems.
 * onPostExecution runs in the UI thread, and it calls the ImageView pictureReady method to do the update.   
 * 
 */
/**
 * @author Renu
 *
 */
/**
 * @author Renu
 *
 */
public class GetSaleDetails {
	SaleFinder sp = null;

	/*
	 * search is the public GetSaleDetails method. Its arguments are the search
	 * term, and the SaleFinder object that called it. This provides a callback
	 * path such that the pictureReady method in that object is called when the
	 * picture is available from the search.
	 */
	public void search(String searchTerm, SaleFinder sp) {
		this.sp = sp;
		new AsyncBestBuyrSearch().execute(searchTerm);
	}

	/*
	 * AsyncTask provides a simple way to use a thread separate from the UI
	 * thread in which to do network operations. doInBackground is run in the
	 * helper thread. onPostExecute is run in the UI thread, allowing for safe
	 * UI updates.
	 */
	private class AsyncBestBuyrSearch extends AsyncTask<String, Void, Bitmap> {
		protected Bitmap doInBackground(String... urls) {
			return search(urls[0]);
		}

		protected void onPostExecute(Bitmap image) {
			sp.pictureReady(image);
		}

		/*
		 * Search BestBuy.com for the searchTerm argument, and return a Bitmap
		 * that can be put in an ImageView
		 */
		/**
		 * @param searchTerm
		 * @return
		 */
		private Bitmap search(String searchTerm) {
			System.out.println(searchTerm);
			String resp = "";
			String xml = "";
			Bitmap picImage = null;
			URL url;
			String imagePath = "";

			try {
				url = new URL(
						"http://1-dot-salefinder18.appspot.com/salefinderweb?search="
								+ searchTerm);
				/*open HTTPConnection*/
				HttpURLConnection connection = (HttpURLConnection) url
						.openConnection();
				BufferedReader in = new BufferedReader(new InputStreamReader(
						connection.getInputStream(), "UTF-8"));
				/*continue to read till end  */
				while ((resp = in.readLine()) != null) {
					xml += resp;
				}
				Document doc = getRemoteXML(xml);
				if (doc == null) {
					System.out.println("empty doc");
				} else {
					NodeList node = doc.getElementsByTagName("largeImage");
					for (int i = 0; i < node.getLength(); i++) {
						Node e = (Node) node.item(i);

						NodeList n2 = e.getParentNode().getChildNodes();
						for (int j = 0; j < n2.getLength(); j++) {
							/*get Image path*/
							if (e.getNodeName().equalsIgnoreCase("largeImage")) {
								imagePath = e.getTextContent();
								break;
							}
						}

					}
					URL picURL = new URL(imagePath);
					picImage = getRemoteImage(picURL);
				}
			} catch (MalformedURLException e1) {
				e1.printStackTrace();

			} catch (IOException e) {
				e.printStackTrace();
			}
			return picImage;

		}

		/*
		 * Given a url that will request XML, return a Document with that XML,
		 * else null
		 */
		private Document getRemoteXML(String url) {
			try {
				final byte[] bytes = url.getBytes();
				final ByteArrayInputStream is = new ByteArrayInputStream(bytes);
				Document doc = null;
				DocumentBuilderFactory factory = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder builder;
				builder = factory.newDocumentBuilder();
				doc = builder.parse(is);
				return doc;
			} catch (Exception e) {
				System.out.print("Yikes, hit the error: " + e);
				return null;
			}
		}

		/*
		 * Given a URL referring to an image, return a bitmap of that image
		 */
		private Bitmap getRemoteImage(final URL url) {
			try {

				final URLConnection conn = url.openConnection();
				conn.connect();
				BufferedInputStream bis = new BufferedInputStream(
						conn.getInputStream());
				Bitmap bm = BitmapFactory.decodeStream(bis);
				bis.close();
				return bm;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
	}
}
