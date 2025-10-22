package gui.room;

import bus.RoomBUS;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import entity.Room;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class FormRoomManagement extends JPanel {

    private static final Color BG = new Color(0x0B1F33);
    private static final Color PANEL_TOP = new Color(0x0E2942);
    private static final Color TEXT_PRIMARY = new Color(0xE9EEF6);
    private static final Color GOLD = new Color(0xF5C452);

    private RoomBUS roomBUS = new RoomBUS();

    private JTextField txtSearch;
    private JComboBox<String> cbxFilter;
    private JButton btnAdd, btnUpdate;
    private JTable tableRoom;
    private RoomTableModel roomTableModel;
    private JPopupMenu popUpOption;

    public FormRoomManagement() {
        setLayout(new MigLayout("fill, wrap, insets 0", "[grow]", "[grow 0][grow]"));
        setBackground(BG);

        // TOP BAR: title | search | filter | add | update
        JPanel top = new JPanel(new MigLayout(
                "insets 12 16 12 16",
                "[]16[grow,fill]16[]8[]8[]",
                "[]"
        ));
        top.setBackground(PANEL_TOP);
        add(top, "growx");

        JLabel title = new JLabel("Quản lý phòng | Rooms");
        title.setForeground(TEXT_PRIMARY);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        top.add(title);

        // search theo roomID
        txtSearch = new JTextField();
        txtSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Tìm theo mã phòng (RoomID)");
        txtSearch.putClientProperty(FlatClientProperties.STYLE,
                "arc:10; background:#102D4A; foreground:#E9EEF6; borderColor:#274A6B; padding:6,10,6,10;");
        txtSearch.setEditable(true);
        txtSearch.setEnabled(true);
        txtSearch.setFocusable(true);
        txtSearch.enableInputMethods(true);
        top.add(txtSearch, "w 320!, growx");

        cbxFilter = new JComboBox<>(new String[]{"All", "Check-in"});
        cbxFilter.putClientProperty(FlatClientProperties.STYLE,
                "arc:10; background:#102D4A; foreground:#E9EEF6; borderColor:#274A6B; padding:6,12,6,12;");
        top.add(cbxFilter, "w 130!");

        FlatSVGIcon.ColorFilter goldFilter = new FlatSVGIcon.ColorFilter() {
            @Override public Color filter(Color c) { return GOLD; }
        };
        FlatSVGIcon addI = new FlatSVGIcon("gui/icon/svg/add.svg", 0.35f); addI.setColorFilter(goldFilter);
        FlatSVGIcon editI = new FlatSVGIcon("gui/icon/svg/edit.svg", 0.35f); editI.setColorFilter(goldFilter);

        btnAdd = new JButton("Thêm phòng", addI);  stylePrimary(btnAdd);
        btnUpdate = new JButton("Cập nhật", editI); stylePrimary(btnUpdate);
        top.add(btnAdd, "w 140!");
        top.add(btnUpdate, "w 120!");

        // table
        roomTableModel = new RoomTableModel();
        tableRoom = new JTable(roomTableModel);
        tableRoom.setRowHeight(36);
        tableRoom.putClientProperty(FlatClientProperties.STYLE,
                "background:#0B1F33; foreground:#E9EEF6; selectionBackground:#153C5B; selectionForeground:#E9EEF6; gridColor:#274A6B");
        tableRoom.getTableHeader().putClientProperty(FlatClientProperties.STYLE,
                "background:#102D4A; foreground:#E9EEF6; height:36;");

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        tableRoom.getColumnModel().getColumn(2).setCellRenderer(center);
        tableRoom.getColumnModel().getColumn(3).setCellRenderer(center);
        tableRoom.getColumnModel().getColumn(0).setPreferredWidth(100);
        tableRoom.getColumnModel().getColumn(1).setPreferredWidth(420);

        // cài dat su kien double-click vào phòng --> hiển thị thông tin phòng để xem
        tableRoom.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                if (tableRoom.getSelectedRow() != -1 && e.getClickCount() == 2)  {
                    String roomID = (String) roomTableModel.getValueAt(tableRoom.getSelectedRow(), 0);
                    Room room = roomBUS.getByID(roomID);
                    if (room == null) {
                        JOptionPane.showMessageDialog(FormRoomManagement.this,
                                roomBUS.getLastError() == null ? "Không tìm thấy phòng" : roomBUS.getLastError());
                        return;
                    }
                    FormRoomDetail formRoomDetail = new FormRoomDetail(room);
                    formRoomDetail.setModal(true);
                    formRoomDetail.setLocationRelativeTo(FormRoomManagement.this);
                    formRoomDetail.setVisible(true);
                }
            }
            @Override public void mouseReleased(MouseEvent e) { if (e.isPopupTrigger()) showRowPopup(e); }
            @Override public void mouseClicked(MouseEvent e) { if (SwingUtilities.isRightMouseButton(e)) showRowPopup(e); }
        });

        JScrollPane scroll = new JScrollPane(tableRoom);
        scroll.setBorder(null);
        add(scroll, "grow");

        // Popup Option
        popUpOption = new JPopupMenu();
        JMenuItem miAddService = new JMenuItem("Thêm dịch vụ cho phòng");
        popUpOption.add(miAddService);
        miAddService.addActionListener(evt -> {
            int row = tableRoom.getSelectedRow();

            if (row < 0) return;
            String roomID = (String) roomTableModel.getValueAt(row, 0);
            FormUpdateServiceToRoom formUpdateServiceToRoom = new FormUpdateServiceToRoom(FormRoomManagement.this, roomID);
            formUpdateServiceToRoom.setModal(true);
            formUpdateServiceToRoom.setLocationRelativeTo(FormRoomManagement.this);
            formUpdateServiceToRoom.setVisible(true);
        });

        // button event
        btnAdd.addActionListener(e -> {
            FormAddRoom formAddRoom = new FormAddRoom(this);
            formAddRoom.setModal(true);
            formAddRoom.setLocationRelativeTo(this);
            formAddRoom.setVisible(true);
        });

        btnUpdate.addActionListener(e -> {
            int row = tableRoom.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 phòng để cập nhật.");
                return;
            }
            Room room = roomTableModel.getRoomAt(row); // mỗi dòng là 1 room
            FormUpdateRoomInformation formUpdateRoomInformation = new FormUpdateRoomInformation(this, room);
            formUpdateRoomInformation.setModal(true);
            formUpdateRoomInformation.setLocationRelativeTo(this);
            formUpdateRoomInformation.setVisible(true);
        });

        cbxFilter.addActionListener(e -> searchAndFilter());

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { searchAndFilter(); }
            @Override public void removeUpdate(DocumentEvent e) { searchAndFilter(); }
            @Override public void changedUpdate(DocumentEvent e) { searchAndFilter(); }
        });

        txtSearch.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) { txtSearch.requestFocusInWindow(); }
        });

        // Load dữ liệu
        searchAndFilter();
    }
    public void searchAndFilter() {
        String keyword = txtSearch.getText().trim();
        String filter = String.valueOf(cbxFilter.getSelectedItem());

        ArrayList<Room> list = (ArrayList<Room>) roomBUS.searchAndFilter(keyword, filter);
        roomTableModel.setRooms(list);
    }

    @Override
    public void addNotify() {
        super.addNotify();
        SwingUtilities.invokeLater(() -> txtSearch.requestFocusInWindow());
    }

    private void showRowPopup(MouseEvent e) {
        int row = tableRoom.rowAtPoint(e.getPoint());
        if (row != -1) {
            tableRoom.setRowSelectionInterval(row, row);
            popUpOption.show(tableRoom, e.getX(), e.getY());
        }
    }

    private void stylePrimary(AbstractButton b) {
        b.putClientProperty(FlatClientProperties.STYLE,
                "arc:12; background:#F5C452; foreground:#0B1F33; borderColor:#F1B93A; hoverBackground:#FFD36E; "
                        + "focusWidth:1; innerFocusWidth:0; padding:6,12,6,12;");
    }


}
