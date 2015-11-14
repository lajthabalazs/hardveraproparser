package hu.droidium.hardverapro.price_master;

import hu.droidium.hardverapro.ForumParser;
import hu.droidium.hardverapro.Line;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.IOException;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class PriceMaster {
	private JTextArea left;
	private JTextArea right;
	public PriceMaster() throws IOException {
		ForumParser<PricingPost> parser = new ForumParser<PricingPost>(new PricingPostFactory());
		List<PricingPost> posts = parser.getPosts("http://prohardver.hu/tema/mennyit_adnal_ezert_a_gepert_alkatreszert_topik/", -1);
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
		for (PricingPost p : posts) {
			if (!p.isOff() && (p.isPriceRequest())){
				String header = "[B](#" + p.getMessageIndex() + ") " + p.getUserName() + "[/B]\n\n";
				left.append(p.getDate().toString() + " > " + header);
				for (String s : p.getOriginalLines()) {
					left.append(s);
					left.append("\n");
				}
				left.append("\n\n");
				if ((p.getProcessedLines().size() > 0)) {
					right.append(header);
					for (Line line : p.getProcessedLines()) {
						PricingLine pmLine = (PricingLine) line;
						right.append(pmLine + " ~  eFt");
						right.append("\n");
					}
					right.append("\n\n");
				}
			}
		}
		appendBoth("[B]Ha bármi pontatlanságot találtatok azt jelezzéltek nekem privátban.[/B]\n");
		appendBoth("[/M]");
	}

	private void appendBoth(String s){
		left.append(s);
		right.append(s);
	}
	public static void main(String[] args) throws IOException {
		new PriceMaster();
	}
}