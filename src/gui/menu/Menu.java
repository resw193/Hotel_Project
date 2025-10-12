package gui.menu;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.UIScale;

import gui.menu.mode.LightDarkMode;
import gui.menu.mode.ToolBarAccentColor;

public class Menu extends JPanel {

	private final String menuItems[][] = {
			{"Quản lý phòng"},
			{"Quản lý đặt phòng"},
			{"Quản lý hóa đơn"},
			{"Quản lý khách hàng", "Thông tin khách hàng", "Thống kê"},
			{"Quản lý dịch vụ"},
			{"Thông tin cá nhân"}
	};

	private final List<MenuEvent> events = new ArrayList<>();
	private boolean menuFull = true;
	private final String headerName = "MIMOSA Hotel";

	protected final boolean hideMenuTitleOnMinimum = true;
	protected final int menuTitleLeftInset = 5;
	protected final int menuTitleVgap = 5;
	protected final int menuMaxWidth = 250;
	protected final int menuMinWidth = 60;
	protected final int headerFullHgap = 5;

	private JLabel header;
	private JScrollPane scroll;
	private JPanel panelMenu;
	private LightDarkMode lightDarkMode;
	private ToolBarAccentColor toolBarAccentColor;

	public Menu() {
		init();
	}

	private void init() {
		setLayout(new MenuLayout());
		setBackground(Color.WHITE); // Chuyên nghiệp: Nền trắng
		setBorder(new AbstractBorder() {}); // Viền solid default

		putClientProperty(FlatClientProperties.STYLE,
				"arc:8;" +
						"background:#FFFFFF"); // Nền trắng

		// Header
		header = new JLabel(headerName);
		header.setFont(new Font("SansSerif", Font.BOLD, 18)); // Font chuyên nghiệp
		header.setIcon(new ImageIcon(getClass().getResource("/other/logo/mimosa_hotel_logo.jpg")));
		header.setForeground(Color.BLUE); // Màu xanh dương chuyên nghiệp
		header.putClientProperty(FlatClientProperties.STYLE,
				"border:5,5,5,5");

		// Menu
		scroll = new JScrollPane();
		panelMenu = new JPanel(new MenuItemLayout(this));
		panelMenu.setBackground(Color.LIGHT_GRAY); // Xám nhạt
		panelMenu.putClientProperty(FlatClientProperties.STYLE,
				"border:5,5,5,5;" +
						"background:#F0F0F0");

		scroll.setViewportView(panelMenu);
		scroll.setBackground(Color.WHITE);
		JScrollBar vscroll = scroll.getVerticalScrollBar();
		vscroll.setUnitIncrement(10);
		vscroll.putClientProperty(FlatClientProperties.STYLE,
				"width:10;" +
						"background:#F0F0F0;" +
						"track:#F0F0F0;" +
						"thumb:#A0A0A0;" +
						"trackArc:999");

		createMenu();
		lightDarkMode = new LightDarkMode();
		toolBarAccentColor = new ToolBarAccentColor(this);
		toolBarAccentColor.setVisible(FlatUIUtils.getUIBoolean("AccentControl.show", false));

		add(header);
		add(scroll);
		add(lightDarkMode);
		add(toolBarAccentColor);
	}

	private void createMenu() {
		int index = 0;
		for (int i = 0; i < menuItems.length; i++) {
			String menuName = menuItems[i][0];
			MenuItem menuItem = new MenuItem(this, menuItems[i], index++, events);
			panelMenu.add(menuItem);
		}
	}

	public boolean isMenuFull() {
		return menuFull;
	}

	public void setMenuFull(boolean menuFull) {
		this.menuFull = menuFull;
		if (menuFull) {
			header.setText(headerName);
			header.setHorizontalAlignment(getComponentOrientation().isLeftToRight() ? JLabel.LEFT : JLabel.RIGHT);
		} else {
			header.setText("");
			header.setHorizontalAlignment(JLabel.CENTER);
		}
		for (Component com : panelMenu.getComponents()) {
			if (com instanceof MenuItem) {
				((MenuItem) com).setFull(menuFull);
			}
		}
		lightDarkMode.setMenuFull(menuFull);
		toolBarAccentColor.setMenuFull(menuFull);
	}

	public void setSelectedMenu(int index, int subIndex) {
		runEvent(index, subIndex);
	}

	protected void setSelected(int index, int subIndex) {
		int size = panelMenu.getComponentCount();
		for (int i = 0; i < size; i++) {
			Component com = panelMenu.getComponent(i);
			if (com instanceof MenuItem) {
				MenuItem item = (MenuItem) com;
				if (item.getMenuIndex() == index) {
					item.setSelectedIndex(subIndex);
				} else {
					item.setSelectedIndex(-1);
				}
			}
		}
	}

	protected void runEvent(int index, int subIndex) {
		MenuAction menuAction = new MenuAction();
		for (MenuEvent event : events) {
			event.menuSelected(index, subIndex, menuAction);
		}
		if (!menuAction.isCancel()) {
			setSelected(index, subIndex);
		}
	}

	public void addMenuEvent(MenuEvent event) {
		events.add(event);
	}

	public void hideMenuItem() {
		for (Component com : panelMenu.getComponents()) {
			if (com instanceof MenuItem) {
				((MenuItem) com).hideMenuItem();
			}
		}
		revalidate();
	}

	public boolean isHideMenuTitleOnMinimum() {
		return hideMenuTitleOnMinimum;
	}

	public int getMenuTitleLeftInset() {
		return menuTitleLeftInset;
	}

	public int getMenuTitleVgap() {
		return menuTitleVgap;
	}

	public int getMenuMaxWidth() {
		return menuMaxWidth;
	}

	public int getMenuMinWidth() {
		return menuMinWidth;
	}

	private class MenuLayout implements LayoutManager {
		@Override
		public void addLayoutComponent(String name, Component comp) {
		}

		@Override
		public void removeLayoutComponent(Component comp) {
		}

		@Override
		public Dimension preferredLayoutSize(Container parent) {
			synchronized (parent.getTreeLock()) {
				return new Dimension(5, 5);
			}
		}

		@Override
		public Dimension minimumLayoutSize(Container parent) {
			synchronized (parent.getTreeLock()) {
				return new Dimension(0, 0);
			}
		}

		@Override
		public void layoutContainer(Container parent) {
			synchronized (parent.getTreeLock()) {
				Insets insets = parent.getInsets();
				int x = insets.left;
				int y = insets.top;
				int gap = UIScale.scale(5);
				int sheaderFullHgap = UIScale.scale(headerFullHgap);
				int width = parent.getWidth() - (insets.left + insets.right);
				int height = parent.getHeight() - (insets.top + insets.bottom);
				int iconWidth = width;
				int iconHeight = header.getPreferredSize().height;
				int hgap = menuFull ? sheaderFullHgap : 0;
				int accentColorHeight = 0;
				if (toolBarAccentColor.isVisible()) {
					accentColorHeight = toolBarAccentColor.getPreferredSize().height + gap;
				}

				header.setBounds(x + hgap, y, iconWidth - (hgap * 2), iconHeight);
				int ldgap = UIScale.scale(10);
				int ldWidth = width - ldgap * 2;
				int ldHeight = lightDarkMode.getPreferredSize().height;
				int ldx = x + ldgap;
				int ldy = y + height - ldHeight - ldgap - accentColorHeight;

				int menux = x;
				int menuy = y + iconHeight + gap;
				int menuWidth = width;
				int menuHeight = height - (iconHeight + gap) - (ldHeight + ldgap * 2) - (accentColorHeight);
				scroll.setBounds(menux, menuy, menuWidth, menuHeight);

				lightDarkMode.setBounds(ldx, ldy, ldWidth, ldHeight);

				if (toolBarAccentColor.isVisible()) {
					int tbheight = toolBarAccentColor.getPreferredSize().height;
					int tbwidth = Math.min(toolBarAccentColor.getPreferredSize().width, ldWidth);
					int tby = y + height - tbheight - ldgap;
					int tbx = ldx + ((ldWidth - tbwidth) / 2);
					toolBarAccentColor.setBounds(tbx, tby, tbwidth, tbheight);
				}
			}
		}
	}
}