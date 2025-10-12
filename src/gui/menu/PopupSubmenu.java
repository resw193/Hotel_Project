package gui.menu;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.Path2D;
import java.io.InputStream;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.util.Animator;
import com.formdev.flatlaf.util.UIScale;

public class PopupSubmenu extends JPanel {

    private final Menu menu;
    private final int menuIndex;
    private final String[] menus;
    private JPopupMenu popup;
    private final int subMenuLeftGap = 20;
    private final int subMenuItemHeight = 30;
    private Font pressStart2P;

    public PopupSubmenu(ComponentOrientation orientation, Menu menu, int menuIndex, String[] menus) {
        this.menu = menu;
        this.menuIndex = menuIndex;
        this.menus = menus;
        applyComponentOrientation(orientation);
        init();
    }

    private void init() {
        // Tải font Press Start 2P
        try {
            InputStream fontStream = getClass().getResourceAsStream("/resources/fonts/PressStart2P-Regular.ttf");
            if (fontStream != null) {
                pressStart2P = Font.createFont(Font.TRUETYPE_FONT, fontStream).deriveFont(12f);
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(pressStart2P);
                fontStream.close();
            } else {
                System.err.println("Không tìm thấy file font PressStart2P-Regular.ttf");
                pressStart2P = new Font("Courier New", Font.PLAIN, 12);
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi tải font: " + e.getMessage());
            pressStart2P = new Font("Courier New", Font.PLAIN, 12);
        }

        setLayout(new MenuLayout());
        popup = new JPopupMenu();
        popup.setBorder((Border) new DashedBorder(new Color(139, 0, 0), 2, 4, 4)); // Viền nét đứt đỏ máu
        popup.putClientProperty(FlatClientProperties.STYLE,
                "background:#1C2526;" + // Đen bóng đêm
                        "borderColor:#1C2526;" +
                        "arc:8");

        putClientProperty(FlatClientProperties.STYLE,
                "border:0,3,0,3;" +
                        "background:#1C2526;" +
                        "foreground:#8B0000"); // Đường kẻ đỏ máu

        for (int i = 1; i < menus.length; i++) {
            JButton button = createButtonItem(menus[i]);
            final int subIndex = i;
            button.addActionListener((ActionEvent e) -> {
                menu.runEvent(menuIndex, subIndex);
                popup.setVisible(false);
            });
            add(button);
        }
        popup.add(this);
    }

    private JButton createButtonItem(String text) {
        JButton button = new JButton(text);
        button.setFont(pressStart2P.deriveFont(12f));
        button.putClientProperty(FlatClientProperties.STYLE,
                "background:#4A4E46;" + // Xám tro
                        "foreground:#8B0000;" + // Đỏ máu
                        "rolloverForeground:#483D8B;" + // Tím ma mị khi hover
                        "selectedBackground:#4A4E46;" +
                        "selectedForeground:#483D8B;" + // Tím ma mị khi chọn
                        "borderWidth:0;" +
                        "arc:8;" +
                        "focusWidth:0;" +
                        "innerFocusWidth:0;" +
                        "iconTextGap:10;" +
                        "margin:5,11,5,11");
        return button;
    }

    public void show(Component com, int x, int y) {
        // Tính toán vị trí ban đầu
        int px = menu.getComponentOrientation().isLeftToRight() ? x : -getPreferredSize().width - UIScale.scale(5);
        int startY = y - getPreferredSize().height; // Bắt đầu từ trên xuống
        int endY = y;

        // Hiệu ứng trượt xuống
        Animator animator = new Animator(300, new Animator.TimingTarget() {
            @Override
            public void timingEvent(float f) {
                int currentY = (int) (startY + (endY - startY) * f);
                popup.show(com, px, currentY);
                applyAlignment();
            }

            @Override
            public void end() {
                popup.show(com, px, endY);
                applyAlignment();
            }
        });
        animator.setResolution(1);
        animator.setInterpolator((float f) -> (float) (1 - Math.pow(1 - f, 2))); // Trượt chậm dần
        animator.start();

        SwingUtilities.updateComponentTreeUI(popup);
    }

    private void applyAlignment() {
        setComponentOrientation(menu.getComponentOrientation());
        for (Component c : getComponents()) {
            if (c instanceof JButton) {
                ((JButton) c).setHorizontalAlignment(menu.getComponentOrientation().isLeftToRight() ? JButton.LEFT : JButton.RIGHT);
            }
        }
    }

    protected void setSelectedIndex(int index) {
        int size = getComponentCount();
        for (int i = 0; i < size; i++) {
            Component com = getComponent(i);
            if (com instanceof JButton) {
                ((JButton) com).setSelected(i == index - 1);
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        int ssubMenuItemHeight = UIScale.scale(subMenuItemHeight);
        int ssubMenuLeftGap = UIScale.scale(subMenuLeftGap);
        Path2D.Double p = new Path2D.Double();
        int last = getComponent(getComponentCount() - 1).getY() + (ssubMenuItemHeight / 2);
        boolean ltr = getComponentOrientation().isLeftToRight();
        int round = UIScale.scale(8);
        int x = ltr ? (ssubMenuLeftGap - round) : (getWidth() - (ssubMenuLeftGap - round));
        p.moveTo(x, 0);
        p.lineTo(x, last - round);
        for (int i = 0; i < getComponentCount(); i++) {
            int com = getComponent(i).getY() + (ssubMenuItemHeight / 2);
            p.append(createCurve(round, x, com, ltr), false);
        }
        g2.setColor(new Color(139, 0, 0)); // Đỏ máu
        g2.setStroke(new BasicStroke(UIScale.scale(1f)));
        g2.draw(p);
        g2.dispose();
    }

    private Shape createCurve(int round, int x, int y, boolean ltr) {
        Path2D p2 = new Path2D.Double();
        p2.moveTo(x, y - round);
        p2.curveTo(x, y - round, x, y, x + (ltr ? round : -round), y);
        return p2;
    }

    // Lớp viền nét đứt
    static class DashedBorder implements Border {
        private final Color color;
        private final int thickness;
        private final int dashLength;
        private final int gapLength;

        public DashedBorder(Color color, int thickness, int dashLength, int gapLength) {
            this.color = color;
            this.thickness = thickness;
            this.dashLength = dashLength;
            this.gapLength = gapLength;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(color);
            Stroke dashed = new BasicStroke(thickness, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f,
                    new float[]{dashLength, gapLength}, 0.0f);
            g2.setStroke(dashed);
            g2.drawRect(x + thickness / 2, y + thickness / 2, width - thickness, height - thickness);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(thickness, thickness, thickness, thickness);
        }

        @Override
        public boolean isBorderOpaque() {
            return true;
        }
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
                Insets insets = parent.getInsets();
                int maxWidth = UIScale.scale(150);
                int ssubMenuLeftGap = UIScale.scale(subMenuLeftGap);
                int width = getMaxWidth(parent) + ssubMenuLeftGap;
                int height = (insets.top + insets.bottom);
                int size = parent.getComponentCount();
                for (int i = 0; i < size; i++) {
                    Component com = parent.getComponent(i);
                    if (com.isVisible()) {
                        height += UIScale.scale(subMenuItemHeight);
                        width = Math.max(width, com.getPreferredSize().width);
                    }
                }
                width += insets.left + insets.right;
                return new Dimension(Math.max(width, maxWidth), height);
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
                boolean ltr = parent.getComponentOrientation().isLeftToRight();
                Insets insets = parent.getInsets();
                int ssubMenuLeftGap = UIScale.scale(subMenuLeftGap);
                int ssubMenuItemHeight = UIScale.scale(subMenuItemHeight);
                int x = insets.left + (ltr ? ssubMenuLeftGap : 0);
                int y = insets.top;
                int width = getMaxWidth(parent);
                int size = parent.getComponentCount();
                for (int i = 0; i < size; i++) {
                    Component com = parent.getComponent(i);
                    if (com.isVisible()) {
                        com.setBounds(x, y, width, ssubMenuItemHeight);
                        y += ssubMenuItemHeight;
                    }
                }
            }
        }

        private int getMaxWidth(Container parent) {
            int size = parent.getComponentCount();
            int maxWidth = UIScale.scale(150);
            int max = 0;
            for (int i = 0; i < size; i++) {
                Component com = parent.getComponent(i);
                if (com.isVisible()) {
                    max = Math.max(max, com.getPreferredSize().width);
                }
            }
            return Math.max(max, maxWidth);
        }
    }
}
