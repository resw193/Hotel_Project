package gui.statistics.service_statistics;

import entity.ServiceRanking;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TopServiceBarChart extends JPanel {
    private final ArrayList<ServiceRanking> data = new ArrayList<>();

    private final Color BG        = new Color(0x0B1F33);
    private final Color PLOT_BG   = new Color(0x111E2B);
    private final Color GRID      = new Color(0x183447);
    private final Color NAME_COL  = new Color(0xFACC15);
    private final Color AXIS_COL  = new Color(0x406F8F);

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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();
        int padL = 16, padR = 8, padT = 22, padB = 32;
        int plotX = padL, plotY = padT, plotW = w - padL - padR, plotH = h - padT - padB;

        g2.setColor(PLOT_BG);
        g2.fillRoundRect(plotX, plotY, plotW, plotH, 16, 16);

        g2.setColor(GRID);
        for (int i = 0; i <= 4; i++) {
            int y = plotY + plotH - i * plotH / 4;
            g2.drawLine(plotX + 8, y, plotX + plotW - 8, y);
        }

        if (data.isEmpty()) {
            g2.setColor(new Color(255, 255, 255, 90));
            String s = "Không có dữ liệu";
            FontMetrics fm = g2.getFontMetrics(getFont().deriveFont(Font.PLAIN, 14f));
            g2.drawString(s, (w - fm.stringWidth(s)) / 2, h / 2);
            g2.dispose();
            return;
        }

        int n = data.size();
        int maxQ = data.stream().mapToInt(ServiceRanking::getTotalQuantity).max().orElse(1);

        int gap = Math.max(6, Math.min(12, plotW / Math.max(8, n * 4)));
        int barW = Math.max(12, Math.min(36, (plotW - (n - 1) * gap - 24) / n));
        int totalBars = n * barW + (n - 1) * gap;
        int x = plotX + (plotW - totalBars) / 2;

        Font nameFont = getFont().deriveFont(Font.BOLD, 12f);
        FontMetrics fmName = g2.getFontMetrics(nameFont);
        Font valueFont = getFont().deriveFont(Font.PLAIN, 12f);
        FontMetrics fmVal = g2.getFontMetrics(valueFont);

        for (ServiceRanking s : data) {
            int q = Math.max(0, s.getTotalQuantity());
            int barH = (int) ((q * 1.0 / maxQ) * (plotH - 44));
            int y = plotY + plotH - barH - 20;

            Paint old = g2.getPaint();
            g2.setPaint(new GradientPaint(0, y, new Color(255, 179, 71),
                    0, y + barH, new Color(255, 76, 41)));
            g2.fillRoundRect(x, y, barW, barH, 8, 8);
            g2.setPaint(old);

            // value trên đầu cột
            String v = String.valueOf(q);
            g2.setFont(valueFont);
            g2.setColor(Color.WHITE);
            g2.drawString(v, x + (barW - fmVal.stringWidth(v)) / 2, y - 4);

            // tên dịch vụ
            String name = s.getServiceName();
            if (name.length() > 12) name = name.substring(0, 12) + "…";
            g2.setFont(nameFont);
            g2.setColor(NAME_COL);
            g2.drawString(name,
                    x + (barW - fmName.stringWidth(name)) / 2,
                    plotY + plotH - 6);

            x += barW + gap;
        }

        g2.setColor(AXIS_COL);
        g2.drawString("SL", plotX + 6, plotY + 14);

        g2.dispose();
    }
}
