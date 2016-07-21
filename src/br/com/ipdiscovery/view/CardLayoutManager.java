package br.com.ipdiscovery.view;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Container;

import javax.swing.JPanel;

public class CardLayoutManager extends JPanel {

	private static final long serialVersionUID = 1L;
	private JPanel cards = new JPanel(new CardLayout());

	protected CardLayoutManager(Container pane) {
		addPanelToCardLayout(new ConfigurationGUI(this).retrievePanel());
		pane.add(cards);
	}

	public void addPanelToCardLayout(CardLayoutSetting panel) {
		cards.add((Component) panel, panel.retrivePanelName());
	}

	public void changeVisibleCardLayout(String cardName) {
		CardLayout cl = (CardLayout) (cards.getLayout());
		cl.show(cards, cardName);
	}

	public CardLayoutSetting findPanel(Class<? extends CardLayoutSetting> panel) {
		for (Component panelCard : cards.getComponents()) {
			@SuppressWarnings("rawtypes")
			Class b = panelCard.getClass();
			if (panel.isAssignableFrom(b)) {
				return (CardLayoutSetting) panelCard;
			}
		}
		return null;
	}
}
