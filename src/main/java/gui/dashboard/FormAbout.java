package gui.dashboard;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class FormAbout extends JPanel {

    private static final Color BG = new Color(0x0B1F33);
    private static final Color PANEL_TOP = new Color(0x0E2942);
    private static final Color CARD_BG = new Color(0x102D4A);
    private static final Color TEXT_PRIMARY = new Color(0xE9EEF6);
    private static final Color TEXT_MUTED = new Color(0xB8C4D4);
    private static final Color GOLD_PRIMARY = new Color(0xF5C452);

    public FormAbout() {
        setLayout(new MigLayout("fill, wrap, insets 0", "[grow]", "[grow 0][grow]"));
        setBackground(BG);

        // ======= TOP BAR =======
        JPanel top = new JPanel(new MigLayout("insets 12 16 12 16", "[grow]", "[]"));
        top.setBackground(PANEL_TOP);
        JLabel title = new JLabel("Giới thiệu | About");
        title.setForeground(TEXT_PRIMARY);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        top.add(title, "left");
        add(top, "growx");

        // ======= MAIN CONTENT =======
        JPanel content = new JPanel(new MigLayout("wrap 1, insets 20, gapy 18", "[grow,fill]", ""));
        content.setBackground(BG);
        add(content, "grow");

        // Card giới thiệu khách sạn
        JPanel hotelCard = createCard(
                "Giới thiệu Khách sạn Mimosa",
                "<html><p style='width:650px;'>" +
                        "Khách sạn <b style='color:#F5C452;'>Mimosa</b>, tọa lạc tại số 298, đường Đầm Nại, Ninh Chữ, Ninh Hải, Ninh Thuận, " +
                        "là một điểm dừng chân lý tưởng dành cho khách du lịch, với vị trí thuận tiện gần các điểm tham quan nổi tiếng. " +
                        "Khách sạn cung cấp nhiều loại phòng nghỉ đa dạng, phù hợp cho gia đình, cặp đôi hoặc khách đi công tác, " +
                        "đồng thời hỗ trợ các dịch vụ bổ sung như bữa sáng tự chọn và dịch vụ lễ tân 24/7, " +
                        "mang đến trải nghiệm thoải mái và tiện nghi cho khách hàng." +
                        "</p></html>"
        );
        content.add(hotelCard, "growx");

        // Card mô tả hệ thống và phiên bản
        JPanel versionCard = createCard(
                "Thông tin ứng dụng",
                """
                <html>
                <p style='width:650px;'>
                Ứng dụng <b style='color:#F5C452;'>Hotel Management System - Mimosa</b><br>
                Phiên bản: <b>1.0.0</b><br>
                Mô tả: Ứng dụng được phát triển nhằm hỗ trợ quản lý dịch vụ, phòng, đặt phòng và khách hàng một cách hiệu quả, trực quan.<br>
                Đơn vị phát triển: <b>Nhóm 9 - Khoa Công nghệ Thông tin</b><br>
                Nền tảng: Java Swing + Microsoft SQL Server
                </p>
                </html>
                """
        );
        content.add(versionCard, "growx");

        // Card yêu cầu tài nguyên phần cứng
        JPanel hardwareCard = createCard(
                "Yêu cầu phần cứng",
                """
                <html>
                <table style='width:650px; color:#E9EEF6; border-spacing:8px;'>
                <tr><td><b>CPU</b></td><td>Intel Core i5, 2.3 GHz</td></tr>
                <tr><td><b>RAM</b></td><td>8 GB</td></tr>
                <tr><td><b>Ổ cứng</b></td><td>360 GB</td></tr>
                <tr><td><b>Hệ kiến trúc</b></td><td>64 bit</td></tr>
                </table>
                </html>
                """
        );
        content.add(hardwareCard, "growx");

        // Card yêu cầu phần mềm
        JPanel softwareCard = createCard(
                "Yêu cầu phần mềm",
                """
                <html>
                <table style='width:650px; color:#E9EEF6; border-spacing:8px;'>
                <tr><th align='left'>Tên phần mềm</th><th align='left'>Phiên bản</th><th align='left'>Loại</th></tr>
                <tr><td>Eclipse IDE for Java EE Developers</td><td>4.28</td><td>IDE cho Java</td></tr>
                <tr><td>Microsoft SQL Server</td><td>19.0.1084.56</td><td>Hệ QTCSDL</td></tr>
                <tr><td>Microsoft Windows 11</td><td>11/11</td><td>Hệ điều hành</td></tr>
                </table>
                </html>
                """
        );
        content.add(softwareCard, "growx");
        content.add(createCard("Đơn vị phát triển - Nhóm 9",
                "Nhóm 9 gồm 5 thành viên: Nguyễn Bảo Định — Leader, Trần Ngọc Oanh — Reporter, Hoàng Ngọc Hải — Takenoter, Dương Thiên Ân — Time-checker\n"));

        // Cuộn nếu nội dung dài
        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(20);

        add(scroll, BorderLayout.CENTER);
    }

    private JPanel createCard(String header, String htmlContent) {
        JPanel card = new JPanel(new MigLayout("wrap 1, insets 16, gapy 8", "[grow,fill]", ""));
        card.setBackground(CARD_BG);
        card.setBorder(new EmptyBorder(12, 12, 12, 12));
        card.putClientProperty(FlatClientProperties.STYLE, "arc:16; borderColor:#153C5B;");

        JLabel lblHeader = new JLabel(header);
        lblHeader.setForeground(GOLD_PRIMARY);
        lblHeader.setFont(lblHeader.getFont().deriveFont(Font.BOLD, 15f));
        card.add(lblHeader, "left");

        JLabel lblContent = new JLabel(htmlContent);
        lblContent.setForeground(TEXT_MUTED);
        card.add(lblContent, "growx");

        return card;
    }
}
