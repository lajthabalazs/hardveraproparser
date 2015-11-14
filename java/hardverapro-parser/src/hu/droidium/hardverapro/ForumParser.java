package hu.droidium.hardverapro;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ForumParser<T extends Post> {
	
	private PostFactory<T> postFactory;
	private DateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public ForumParser( PostFactory<T> postFactory) {
		this.postFactory = postFactory;
	}
	

	public List<T> getPosts(String forum, int from) throws IOException {
		String sessionCookie = Downloader.getSessionCookie();
		log("Parsing posts");
		URL url;
		InputStream is = null;
		BufferedReader br;
		String line;
		int postCount = 0;
		try {
			if (!forum.endsWith("/")) {
				forum = forum + "/";
			}
			if (from == -1) {
				forum = forum + "friss.html";
			} else {
				forum = forum + "hsz_" + from + "-" + (from -199) +  ".html";
			}
			url = new URL(forum);
			URLConnection conn = url.openConnection();
			conn.setRequestProperty( "User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.86 Safari/537.36" );
			conn.setRequestProperty("Cookie", sessionCookie + ";list_msgs=d.200.end;prf_ls_msg=d.200.end;");
			conn.connect();
			is = conn.getInputStream();
			br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			T post = null;
			if (br != null) {
				ArrayList<T> posts = new ArrayList<T>();
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
								post = parsePost(line, br, postFactory);
								break;
							}
						}
					}
					if (post != null) {
						posts.add(post);
						while(post.hasNext()) {
							post = parsePost(post.getNextLine(), br, postFactory);
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

	private T parsePost(String start, BufferedReader br, PostFactory<T> postFactory) throws IOException {
		T post;
		post = postFactory.getPost();
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
			String date = "<li class=\"time\">";
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
			} else if(afterMessageIndex && afterUserName && line.contains(date)) {
				line = line.trim();
				line = line.substring(date.length(), line.length() - 5);
				try {
					post.setDate(dateParser.parse(line));
				} catch (ParseException e) {
					post.setDate(null);
					e.printStackTrace();
				}
			} else if (line.contains("<div class=\"text\">")) {
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
					post.setHasMonospace(true);
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

	private static void log(String s) {
		//System.out.println(s);
	}
}
