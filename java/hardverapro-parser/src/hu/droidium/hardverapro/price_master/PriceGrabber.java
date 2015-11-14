package hu.droidium.hardverapro.price_master;

import hu.droidium.hardverapro.ForumParser;
import hu.droidium.hardverapro.Line;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class PriceGrabber {
	private JTextArea left;
	private JTextArea right;
	public PriceGrabber(int firstPost, int lastPost) throws IOException {
		List<PriceMasterPost> allPosts = new ArrayList<PriceMasterPost>(); 
		ForumParser<PriceMasterPost> parser = new ForumParser<PriceMasterPost>(new PriceMasterPostFactory());
		for (int actualPost = lastPost; actualPost > firstPost; actualPost = actualPost - 200) {
			System.out.println("Actual post " + actualPost);
			List<PriceMasterPost> actualPosts = parser.getPosts("http://prohardver.hu/tema/mennyit_adnal_ezert_a_gepert_alkatreszert_topik/", actualPost);
			allPosts.addAll(actualPosts);
			if (actualPost == lastPost) {
				actualPost -= actualPost % 200;
				actualPost = actualPost + 200;
			}
		}
		displayPosts(allPosts);
	}

	private void displayPosts(List<PriceMasterPost> allPosts) {
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
		// Add text to left and right
		for (PriceMasterPost p : allPosts) {
			if (p.isPriceMasterPost()){
				String header = "[B](#" + p.getMessageIndex() + ") " + p.getUserName() + "[/B]\n\n";
				left.append(header);
				for (String s : p.getOriginalLines()) {
					left.append(s);
					left.append("\n");
				}
				left.append("\n\n");
				if ((p.getProcessedLines().size() > 0)) {
					right.append(header);
					for (Line line : p.getProcessedLines()) {
						PriceMasterLine pmLine = (PriceMasterLine) line;
						right.append(""+ pmLine);
						right.append("\n");
					}
					right.append("\n\n");
				}
			}
		}
	}

	public static void main(String[] args) throws IOException {
		new PriceGrabber(57400, 58973);
	}
}