package price_grabber;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ForumParser {
	
	private JTextArea left;
	private JTextArea right;

	public ForumParser() throws IOException {
		String sessionCookie = getSessionCookie();
		List<Post> posts = getPosts(sessionCookie);
		JFrame frame = new JFrame("Hardverapro");
		JPanel mainPanel = new JPanel(new GridLayout(1,1));
		left = new JTextArea("");
		right = new JTextArea("");
		JScrollPane leftPane = new JScrollPane(left);
		leftPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		leftPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		JScrollPane rightPane = new JScrollPane(right);
		rightPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		rightPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		leftPane.setPreferredSize(new Dimension(700, 1000));
		rightPane.setPreferredSize(new Dimension(700, 1000));
		mainPanel.add(leftPane);
		mainPanel.add(rightPane);
		frame.setContentPane(mainPanel);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		appendBoth("[M]");
		// Add text to left and right
		for (Post p : posts) {
			if (!p.isOff() && (!p.isPriceMasterPost())){
				String header = "[B](#" + p.getMessageIndex() + ") " + p.getUserName() + "[/B]\n\n";
				left.append(header);
				for (String s : p.getOriginalLines()) {
					left.append(s);
					left.append("\n");
				}
				left.append("\n\n");
				if ((p.getProcessedLines().length > 0)) {
					right.append(header);
					for (String s : p.getProcessedLines()) {
						right.append(s + " ~  eFt");
						right.append("\n");
					}
					right.append("\n\n");
				}
			}
		}
		appendBoth("[B]Ha bármi pontatlanságot találtatok azt jelezzéltek nekem privátban.[/B]\n");
		appendBoth("[/M]");
	}
	
	public static String getSessionCookie() throws IOException {
		URL u = new URL("http://prohardver.hu/tema/mennyit_adnal_ezert_a_gepert_alkatreszert_topik/friss.html");
		HttpURLConnection conn = (HttpURLConnection) u.openConnection();
		conn.setRequestProperty( "User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.86 Safari/537.36" );
		conn.connect();
		for (String cookie : conn.getHeaderFields().get("Set-Cookie")) {
			log(cookie);
		}
		return conn.getHeaderFields().get("Set-Cookie").get(0);
	}

	
	public static List<Post> getPosts(String sessionCookie) {
		log("Parsing posts");
		URL url;
		InputStream is = null;
		BufferedReader br;
		String line;
		int postCount = 0;
		try {
			url = new URL("http://prohardver.hu/tema/mennyit_adnal_ezert_a_gepert_alkatreszert_topik/friss.html");
			URLConnection conn = url.openConnection();
			conn.setRequestProperty( "User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.86 Safari/537.36" );
			conn.setRequestProperty("Cookie", sessionCookie + ";list_msgs=d.200.end;prf_ls_msg=d.200.end;");
			conn.connect();
			is = conn.getInputStream();
			br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			Post post = null;
			if (br != null) {
				ArrayList<Post> posts = new ArrayList<Post>();
				boolean inPosts = false;
				try {
					while ((line = br.readLine()) != null) {
						if (line.contains("<div class=\"msgblk\">")) {
							line = br.readLine();
							if (line.contains("<h4>hozzászólások</h4>")||line.contains("<h4>Hozzászólások</h4>")) {
								inPosts = true;
								continue;
							}
						}
						if (inPosts) {
							if (line.contains("<div class=\"msg flc off\">")||line.contains("<div class=\"msg flc\">")) {
								log("Parsing post " + postCount);
								post = parsePost(line, br);
								break;
							}
						}
					}
					if (post != null) {
						posts.add(post);
						while(post.hasNext()) {
							post = parsePost(post.getNextLine(), br);
							posts.add(post);
						}
					}
					return posts;
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

	private static Post parsePost(String start, BufferedReader br) throws IOException {
		Post post = new Post();
		if (start.contains(" off\">")) {
			post.setOff(true);
		} else {
			log("Post is ON");
			post.setOff(false);
		}
		String line = null;
		boolean afterMessageIndex = false;
		boolean afterUserName = false;
		boolean inText = false;
		ArrayList<String> postLines = null;
		while ((line = br.readLine()) != null) {
			log("Line " + line);
			if (line.contains("<div class=\"msg flc off\">")||line.contains("<div class=\"msg flc\">")) {
				post.setHasNext(line);
				return post;
			}
			String msg = "<a id=\"msg";
			String usr = "<a href=\"/tag/";
			if (!afterMessageIndex && line.contains(msg)) {
				line = line.substring(line.indexOf(msg) + msg.length());
				line = line.substring(0, line.indexOf("\""));
				post.setMessageIndex(Integer.parseInt(line));
				log("Post " + Integer.parseInt(line));
				afterMessageIndex = true;
			}
			else if (afterMessageIndex && ! afterUserName && line.contains(usr)) {
				line = line.substring(line.indexOf(">") + 1);
				line = line.substring(0, line.indexOf("<"));
				log("User " + line);
				post.setUserName(line);				
				afterUserName = true;
			}
			else if (line.contains("<div class=\"text\">")) {
				inText = true;
				postLines = new ArrayList<String>();
				log("In text");
			}
			else if (inText && line.contains("</div>")) {
				inText = false;
				post.setLines(postLines.toArray(new String[postLines.size()]));
				log("End text");
			}
			else if (inText && line.contains("<p class=\"mgt")) {	
				if (line.contains("<tt>")) {
					post.setPriceMasterPost(true);
				}
				String[] paragraphs = line.split("<p class=\"mgt");				
				for (String s : paragraphs) {
					s = s.trim();
					if (s.length() > 4) {
						s = s.substring(3, s.length() - 4);
						String[] lines = s.split("<br />");
						for (String l : lines){
							postLines.add(l);
						}
					}
				}
			}
		}
		post.setHasNext(null);
		return post;
	}

	public static void main(String[] args) throws IOException {
		new ForumParser();
	}

	private void appendBoth(String s){
		left.append(s);
		right.append(s);
	}
	
	private static void log(String s) {
		//System.out.println(s);
	}
}
