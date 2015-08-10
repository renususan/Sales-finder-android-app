/*Author -Renu Thomas
 * This class return name,sale price & regular price of the item related to the item
 * */
package ds.bestbuysale;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import android.os.AsyncTask;

/*
 * This class provides capabilities to search for an item on BestBuy.com given a search term.  The method "search" is the entry to the class.
 * Network operations cannot be done from the UI thread, therefore this class makes use of an AsyncTask inner class that will do the network
 * operations in a separate worker thread.  However, any UI updates should be done in the UI thread so avoid any synchronization problems.
 * onPostExecution runs in the UI thread, and it calls the  textReady method to do the update.   
 * 
 */
public class GetSaleDetailsText {
	SaleFinder sf = null;

	/*
	 * search is the public GetPicture method. Its arguments are the search
	 * term, and the InterestingPicture object that called it. This provides a
	 * callback path such that the pictureReady method in that object is called
	 * when the picture is available from the search.
	 */
	public void search(String searchTerm, SaleFinder sf) {
		this.sf = sf;
		new AsyncBestBuyrSearch().execute(searchTerm);
	}

	/*
	 * AsyncTask provides a simple way to use a thread separate from the UI
	 * thread in which to do network operations. doInBackground is run in the
	 * helper thread. onPostExecute is run in the UI thread, allowing for safe
	 * UI updates.
	 */
	private class AsyncBestBuyrSearch extends AsyncTask<String, Void, String[]> {
		protected String[] doInBackground(String... urls) {
			return search(urls[0]);
		}

		protected void onPostExecute(String[] saleDetails) {

			sf.textReady(saleDetails);
		}

		/*
		 * Search BestBuy.com for the searchTerm argument, and return a string
		 * array (name of item, sale price and regular price) that can be put in
		 * textView
		 */
		private String[] search(String searchTerm) {
			System.out.println(searchTerm);
			String resp = "";
			String xml = "";
			URL url;
			String saleDetails[] = new String[3];

			try {
				/*url of the app deployed in google app engine*/
				url = new URL(
						"http://1-dot-salefinder18.appspot.com/salefinderweb?search="
								+ searchTerm);

				HttpURLConnection connection = (HttpURLConnection) url
						.openConnection();
				BufferedReader in = new BufferedReader(new InputStreamReader(
						connection.getInputStream(), "UTF-8"));
				while ((resp = in.readLine()) != null) {
					xml += resp;
				}
				/* convert xml into document object */
				Document doc = getRemoteXML(xml);
				if (doc == null) {
					saleDetails = null;
					System.out.println("empty doc");
				} else {
					NodeList node = doc.getElementsByTagName("Product");
					for (int i = 0; i < node.getLength(); i++) {
						Node e = (Node) node.item(i);
						NodeList n2 = e.getChildNodes();
						for (int j = 0; j < n2.getLength(); j++) {
							Node e1 = (Node) n2.item(j);
							if (e1.getNodeName().equalsIgnoreCase("name")) {
								saleDetails[0] = e1.getTextContent();
							}
							if (e1.getNodeName().equalsIgnoreCase("salesPrice")) {
								saleDetails[1] = e1.getTextContent();
							}
							if (e1.getNodeName().equalsIgnoreCase(
									"regularPrice")) {
								saleDetails[2] = e1.getTextContent();
							}
						}
					}

				}

			} catch (MalformedURLException e1) {
				e1.printStackTrace();

			} catch (IOException e) {
				e.printStackTrace();
			}
			return saleDetails;

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

		
	}
}
