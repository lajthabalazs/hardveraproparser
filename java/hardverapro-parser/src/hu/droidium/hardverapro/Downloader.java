package hu.droidium.hardverapro;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Downloader {

	public static List<Ad> getAds() {
		URL url;
		InputStream is = null;
		BufferedReader br;
		String line;

		try {
			url = new URL("http://hardverapro.hu/?lista_perpage=200");
			is = url.openStream(); // throws an IOException
			br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			if (br != null) {
				ArrayList<Ad> ads = new ArrayList<Ad>();
				boolean inAds = false;
				try {
					while ((line = br.readLine()) != null) {
						if (line.contains("<h3>Apróhirdetések</h3>")) {
							inAds = true;
							System.out.println("In ads");
							continue;
						}
						if (inAds) {
							Ad ad = Ad.parse(line);
							if (ad == null) {
								break;
							} else {
								ads.add(ad);
							}
						}
					}
					return ads;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		} catch (MalformedURLException mue) {
			mue.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (IOException ioe) {
				// nothing to see here
			}
		}
		return null;
	}
	
	public static String getSessionCookie() throws IOException {
		URL u = new URL("http://prohardver.hu/tema/mennyit_adnal_ezert_a_gepert_alkatreszert_topik/friss.html");
		HttpURLConnection conn = (HttpURLConnection) u.openConnection();
		conn.setRequestProperty( "User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.86 Safari/537.36" );
		conn.connect();
		return conn.getHeaderFields().get("Set-Cookie").get(0);
	}

	public static void main(String[] args) {
		getAds();
	}
}
