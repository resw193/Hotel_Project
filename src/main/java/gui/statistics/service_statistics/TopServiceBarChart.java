package gui.statistics.service_statistics;

import entity.ServiceRanking;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TopServiceBarChart extends JPanel {
    private final ArrayList<ServiceRanking> data = new ArrayList<>();

    private final Color BG = new Color(0x0B1F33);
    private final Color PLOT_BG = new Color(0x111E2B);
    private final Color GRID = new Color(0x183447);
    private final Color NAME_COL = new Color(0xFACC15);
    private final Color AXIS_COL = new Color(0x406F8F);

    private float barSlotRatio = 0.46f;
    private int minGapPx = 16;

    public TopServiceBarChart() {
        setOpaque(true);
        setBackground(BG);
        setPreferredSize(new Dimension(260, 420));
    }

    public void setData(List<ServiceRanking> list) {
        data.clear();
        if (list != null) data.addAll(list);
        repaint();
    }

    public void setBarSpacing(float barSlotRatio, int minGapPx) {
        this.barSlotRatio = Math.max(0.2f, Math.min(0.8f, barSlotRatio));
        this.minGapPx = Math.max(4, minGapPx);
        repaint();
    }

    private String ellipsize(String s, int maxW, FontMetrics fm) {
        if (fm.stringWidth(s) <= maxW) return s;
        String ell = "...";
        int allow = maxW - fm.stringWidth(ell);
        if (allow <= 0) return ell;
        int i = 0;
        while (i < s.length() && fm.stringWidth(s.substring(0, i + 1)) <= allow) i++;
        return s.substring(0, i) + ell;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int n = (data == null) ? 0 : data.size();
        if (n == 0) {
            g2.dispose();
            return;
        }

        int left = 56, right = 18, bottom = 46, top = 24;
        int plotW = getWidth() - left - right;
        int plotH = getHeight() - top - bottom;

        // slot = bar + gap
        double slot = plotW * 1.0 / n;
        int barW = (int) Math.max(14, Math.min(64, slot * barSlotRatio));
        int gap = (int) Math.max(minGapPx, Math.round(slot - barW));
        slot = barW + gap;

        double totalWidth = n * slot;
        double xStart = left + Math.max(0, (plotW - totalWidth) / 2.0) + gap / 2.0;

        // font
        Font labelFont = getFont().deriveFont(Font.BOLD, 11f);
        Font valueFont = getFont().deriveFont(Font.BOLD, 12f);
        FontMetrics fmLabel = g2.getFontMetrics(labelFont);
        FontMetrics fmValue = g2.getFontMetrics(valueFont);

        long maxQty = data.stream().mapToLong(ServiceRanking::getTotalQuantity).max().orElse(1);
        for (int i = 0; i < n; i++) {
            var it = data.get(i);
            double x = xStart + i * slot;
            double hRatio = (double) it.getTotalQuantity() / maxQty;
            int barH = (int) Math.round(hRatio * (plotH - 6));

            int bx = (int) Math.round(x);
            int by = top + (plotH - barH);


            g2.setPaint(new GradientPaint(0, by, new Color(0xFFF3B0),
                    0, by + barH, new Color(0xFF7A45)));
            g2.fillRoundRect(bx, by, barW, barH, 10, 10);

            // SỐ LƯỢNG TRÊN CỘT
            String val = String.valueOf(it.getTotalQuantity());
            g2.setFont(valueFont);
            int vx = bx + barW / 2 - fmValue.stringWidth(val) / 2;
            int vy = by - 6;
            if (vy - fmValue.getAscent() < top + 2) {
                vy = by + Math.min(barH - 4, fmValue.getAscent() + 4);
                g2.setColor(new Color(0x0B1F33));
            } else {
                g2.setColor(new Color(0xFFFFFF)); // trắng nổi bật
            }
            g2.drawString(val, vx, vy);

            g2.setFont(labelFont);
            int maxLabelW = (int) Math.round(slot);
            String name = ellipsize(it.getServiceName(), maxLabelW, fmLabel);
            int nx = bx + barW / 2 - fmLabel.stringWidth(name) / 2;
            int ny = getHeight() - 12;
            g2.setColor(new Color(0xFDE68A));
            g2.drawString(name, nx, ny);
        }
        g2.dispose();
    }


    private void drawCentered(Graphics2D g2, String s, int cx, int y) {
        FontMetrics fm = g2.getFontMetrics();
        int w = fm.stringWidth(s);
        g2.setColor(new Color(0xFDE68A));
        g2.drawString(s, cx - w / 2, y);
    }
}

