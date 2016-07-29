package br.com.ipdiscovery.view;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Container;

import javax.swing.JPanel;

/**
 * 
 * @author victor.bello
 *
 *         Classe responsável por gerenciar o cardLayout
 */
public class CardLayoutManager extends JPanel {

	private static final long serialVersionUID = 1L;
	private JPanel cards;

	@SuppressWarnings("serial")
	protected CardLayoutManager(Container pane) {
		CardLayout cl = new CardLayout() {
			@Override
			public void show(java.awt.Container parent, String name) {
				super.show(parent, name);
				StartGUI.frame.pack();
				StartGUI.frame.setLocationRelativeTo(null);
			}
		};
		cards = new JPanel(cl);
		addPanelToCardLayout(new ConfigurationGUI(this).retrievePanel());
		pane.add(cards);
	}

	public void addPanelToCardLayout(CardLayoutSetting panel) {
		cards.remove((Component) panel);
		cards.add((Component) panel, panel.retrivePanelName());
	}

	public void removePanel(CardLayoutSetting panel) {
		cards.remove((Component) panel);
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
