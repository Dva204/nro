


package com.girlkun.server;

import com.girlkun.Log;
import com.girlkun.consts.ConstEvent;
import com.girlkun.server.Client;
import com.girlkun.server.Maintenance;
import com.girlkun.server.ServerManager;
import com.girlkun.utils.Logger;
import java.awt.Button;
import com.girlkun.server.security.FirewallManager;


import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

import java.util.List;
import java.util.ArrayList;
import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.prefs.Preferences;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import com.girlkun.database.GirlkunDB;
import com.girlkun.models.item.Item;
import com.girlkun.models.player.Inventory;
import com.girlkun.models.player.Player;
import com.girlkun.services.InventoryServiceNew;
import com.girlkun.services.ItemService;
import com.girlkun.services.NpcService;
import com.girlkun.services.Service;
import com.girlkun.services.TaskService;

//import jdk.internal.org.jline.utils.Log;

public class obito extends JFrame {

    private Preferences preferences;
    private JLabel plCountLabel;
    private JLabel threadCountLabel;
    private JTextField minutesField;
    private JLabel messageLabel;
    private JLabel countdownLabel;
    private Timer countdownTimer;
    private int remainingSeconds;
    private ButtonGroup maintenanceGroup;
// Thêm checkbox
    private JCheckBox maintenanceOption1;
    private JCheckBox maintenanceOption2;
    private JLabel info;
    public static boolean isRunning;
/**
 *
 * @Stole By Le Phuc : 0988068490
 *
 */
    public obito() {
        preferences = Preferences.userNodeForPackage(obito.class);
        setTitle("Ngọc Rồng As");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmExit();
            }
        });
        JPanel panel = new JPanel();
        getContentPane().add(panel);
        JButton maintenanceButton = new JButton("Bảo trì");
        maintenanceButton.addActionListener(e -> showMaintenanceDialog());
        panel.add(maintenanceButton);
        JButton maintenanceButton1 = new JButton("Kick Player");
        maintenanceButton1.addActionListener(e -> kick());
        panel.add(maintenanceButton1);
        JButton maintenanceButton2 = new JButton("Thay Exp");
        maintenanceButton2.addActionListener(e -> tnsm());
        panel.add(maintenanceButton2);
        
        JButton btnNapCoin = new JButton("Nạp Coin");
        btnNapCoin.addActionListener(e -> createFormNapCoin());
        panel.add(btnNapCoin);
        
        JButton btnNextTask = new JButton("Next Nhiệm Vụ");
        btnNextTask.addActionListener(e -> sendNextTask());
        panel.add(btnNextTask);
        
        JButton buffItem = new JButton("Item Thường");
        buffItem.addActionListener(e -> createFormSenditem1());
        panel.add(buffItem);
        
        JButton btnSendItemSKH = new JButton("SEND ITEM SKH");
        panel.add(btnSendItemSKH);
        btnSendItemSKH.addActionListener(e -> createFormSendItemSKH());
        
        JButton doisukien = new JButton("Đổi Sự Kiện");
        panel.add(doisukien);
        doisukien.addActionListener(e -> createFormdoisukien());
        
         JButton khuyenmainap = new JButton("Khuyến Mãi Nạp");
        panel.add(khuyenmainap);
        khuyenmainap.addActionListener(e -> createFormkhuyenmainap());
        
        JButton btnBanIP = new JButton("Ban IP");
        panel.add(btnBanIP);
        btnBanIP.addActionListener(e -> createFormBanIP());

        JButton btnUnbanIP = new JButton("Unban IP");
        panel.add(btnUnbanIP);
        btnUnbanIP.addActionListener(e -> createFormUnbanIP());
        
        
        JButton btnOptimizeRAM = new JButton("Tối ưu RAM");
        btnOptimizeRAM.addActionListener(e -> optimizeRAM());
        panel.add(btnOptimizeRAM);
        //JButton maintenanceButton3 = new JButton("DEV BY Minh Hiếu");
        //maintenanceButton3.addActionListener(e -> startAntiDDoS());
        //panel.add(maintenanceButton3);
//        
        
        JLabel jLabel2 = new JLabel("---QUẢN LÝ---");
        panel.add(jLabel2);
        info = new JLabel("");
        // Đọc giá trị từ tệp tin
        try (BufferedReader reader = new BufferedReader(new FileReader("maintenanceConfig.txt"))) {
            String hoursLine = reader.readLine();
            String minutesLine = reader.readLine();

            int hours = Integer.parseInt(hoursLine);
            int minutes = Integer.parseInt(minutesLine);

            // Thêm giá trị vào DefaultComboBoxModel
            DefaultComboBoxModel<Integer> hoursModel = new DefaultComboBoxModel<>();
            for (int i = -1; i < 24; i++) {
                hoursModel.addElement(i);
            }
            JComboBox<Integer> hoursComboBox = new JComboBox<>(hoursModel);
            panel.add(hoursComboBox);
            hoursComboBox.setSelectedItem(hours);

            // Thêm giá trị vào DefaultComboBoxModel
            DefaultComboBoxModel<Integer> minutesModel = new DefaultComboBoxModel<>();
            for (int i = -1; i < 60; i++) {
                minutesModel.addElement(i);
            }
            JComboBox<Integer> minutesComboBox = new JComboBox<>(minutesModel);
            panel.add(minutesComboBox);
            minutesComboBox.setSelectedItem(minutes);
            JButton scheduleButton2 = new JButton("Hẹn giờ bảo trì");
            scheduleButton2.addActionListener(e -> scheduleMaintenance(hoursComboBox, minutesComboBox));
            panel.add(scheduleButton2);
            if (hours != -1 && minutes != -1) {
                scheduleMaintenance(hoursComboBox, minutesComboBox);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        messageLabel = new JLabel();
        panel.add(messageLabel);

        countdownLabel = new JLabel();
        panel.add(countdownLabel);

        panel.add(info);
        threadCountLabel = new JLabel("Số Thread : ");
        panel.add(threadCountLabel);
        plCountLabel = new JLabel("Online :");
        panel.add(plCountLabel);

        ScheduledExecutorService threadCountExecutor = Executors.newSingleThreadScheduledExecutor();
        threadCountExecutor.scheduleAtFixedRate(() -> {
            int threadCount = Thread.activeCount();
            threadCountLabel.setText("Số Thread: " + threadCount);
        }, 1, 1, TimeUnit.SECONDS);

        ScheduledExecutorService plCountExecutor = Executors.newSingleThreadScheduledExecutor();
        plCountExecutor.scheduleAtFixedRate(() -> {
            int plcount = Client.gI().getPlayers().size();
            plCountLabel.setText("Online : " + plcount);
        }, 5, 1, TimeUnit.SECONDS);
        setVisible(true);
        messageLabel.setText("Settings Time");
        ServerManager.gI().run();
        
        // Đọc giá trị từ tệp

    }

    private void showMaintenanceDialog() {
        try {
            int dialogButton = JOptionPane.YES_NO_OPTION;
            int dialogResult = JOptionPane.showConfirmDialog(this, "Bắt đầu bảo trì?", "Bảo trì", dialogButton);
            if (dialogResult == 0) {
                Logger.error("Server tiến hành bảo trì");
                Maintenance.gI().start(5);

            } else {
                System.out.println("No Option");
            }
        } catch (Exception e) {

        }

    }

    private void kick() {
        new Thread(() -> {
            Client.gI().close();
        }).start();

    }

    private void tnsm() {
        String exp = JOptionPane.showInputDialog(this, "Bảng Exp Server\n"
                + "Exp Server hiện tại: " + Manager.RATE_EXP_SERVER);
        if (exp != null) {
            Manager.RATE_EXP_SERVER = Byte.parseByte(exp);
            Logger.error("Exp hiện tại là: " + exp + "\n");
        }

    }
    private void createFormNapCoin() {
    JPanel panel = new JPanel();
    JTextField nameField = new JTextField(10);
    JTextField vndField = new JTextField(5);
    panel.add(new JLabel("Tên nhân vật:"));
    panel.add(nameField);
    panel.add(new JLabel("Số tiền VND:"));
    panel.add(vndField);

    int result = JOptionPane.showConfirmDialog(null, panel, "Nạp Coin", JOptionPane.OK_CANCEL_OPTION);
    if (result == JOptionPane.OK_OPTION) {
        String name = nameField.getText().trim();
        String vndStr = vndField.getText().trim();
        try {
            int vnd = Integer.parseInt(vndStr);
            int tongCong = vnd * (Manager.KHUYEN_MAI_NAP > 0 ? Manager.KHUYEN_MAI_NAP : 1);

            Player pl = Client.gI().getPlayer(name);
            if (pl != null) {
                pl.getSession().vnd += tongCong;
                PreparedStatement ps = null;
                try (Connection con = GirlkunDB.getConnection()) {
                    ps = con.prepareStatement("update account set vnd = (vnd + ?), tongnap = (tongnap + ?) where id = ?");
                    ps.setInt(1, tongCong);
                    ps.setInt(2, tongCong);
                    ps.setInt(3, pl.getSession().userId);
                    ps.executeUpdate();
                } catch (Exception e) {
                    Logger.logException(obito.class, e, "Lỗi update coin cho " + pl.name);
                } finally {
                    try {
                        if (ps != null) ps.close();
                    } catch (SQLException ex) {
                        System.out.println("Lỗi khi đóng PreparedStatement");
                    }
                }

                JOptionPane.showMessageDialog(null, "Đã nạp " + vnd + " VND (x" + Manager.KHUYEN_MAI_NAP + ") cho " + pl.name + " → Tổng cộng: " + tongCong + " VND");
            } else {
                JOptionPane.showMessageDialog(null, "Người chơi không online");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Số VND không hợp lệ");
        }
    }
}

    
    private void createFormdoisukien() {
    JPanel panel = new JPanel();
    JTextField skField = new JTextField(5);

    panel.add(new JLabel("<html>ID Sự kiện:<br>"
        + "1 - Lễ Hội Mùa Hè<br>"
        + "2 - Giỗ Tổ Hùng Vương<br>"
        + "3 - Tết Trung Thu<br>"
        + "4 - Lễ Hội Halloween<br>"
        + "5 - Ngày Nhà Giáo Việt Nam<br>"
        + "6 - Lễ Hội Giáng Sinh<br>"
        + "7 - Tết Nguyên Đán<br>"
        + "8 - Quốc Tế Phụ Nữ</html>"));
    panel.add(skField);

    int result = JOptionPane.showConfirmDialog(null, panel, "Đổi Sự Kiện", JOptionPane.OK_CANCEL_OPTION);
    if (result == JOptionPane.OK_OPTION) {
        String skStr = skField.getText().trim();
        try {
            byte sk = Byte.parseByte(skStr);
            if (sk >= 0 && sk <= 8) {
                // đổi event
                ConstEvent.EVENT = sk;

                // lấy tên sự kiện SAU khi gán EVENT
                String nameSk = ConstEvent.gI().getNameEv();

                // log mỗi dòng một lệnh (đỡ dính vào 1 dòng)
                Logger.error("Sự Kiện đã đổi thành: " + sk + "\n");
                Logger.error("Sự kiện: " + nameSk + "\n");

                // thông báo ingame
//                Service.getInstance().sendThongBaoAllPlayer(
//                        "|7|Sự kiện " + nameSk + " đang được diễn ra"
//                        + "\n|5|Thông tin chi tiết Sự kiện vui lòng xem tại NPC " + nameSk + " tại Làng Aru");

                JOptionPane.showMessageDialog(null, "Đã đổi sang Sự kiện " + nameSk);
            } else {
                JOptionPane.showMessageDialog(null, "ID sự kiện không hợp lệ! Chỉ từ 1 đến 8.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "ID sự kiện không hợp lệ!");
        }
    }
}



    private void createFormkhuyenmainap() {
    JPanel panel = new JPanel();
    JTextField kmField = new JTextField(5);
    
    panel.add(new JLabel("Giá trị khuyến mãi nạp (x lần):"));
    panel.add(kmField);

    int result = JOptionPane.showConfirmDialog(null, panel, "Thiết lập khuyến mãi nạp", JOptionPane.OK_CANCEL_OPTION);
    if (result == JOptionPane.OK_OPTION) {
        String kmStr = kmField.getText().trim();
        try {
            byte kmValue = Byte.parseByte(kmStr);
            if (kmValue < 0) {
                JOptionPane.showMessageDialog(null, "Giá trị khuyến mãi không hợp lệ (phải >= 0)");
                return;
            }

            Manager.KHUYEN_MAI_NAP = kmValue;
            Logger.error("\nGiá trị Khuyến mãi hiện tại là: x" + kmValue);
            JOptionPane.showMessageDialog(null, "Đã cập nhật khuyến mãi nạp thành: x" + kmValue);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Giá trị không hợp lệ! Vui lòng nhập số nguyên.");
        }
    }
}

    private void sendNextTask() {
    JPanel panel = new JPanel();
    JTextField nameField = new JTextField(10);
    JTextField taskIdField = new JTextField(5);
    panel.add(new JLabel("Tên nhân vật:"));
    panel.add(nameField);
    panel.add(new JLabel("ID nhiệm vụ (1-100):"));
    panel.add(taskIdField);

    int result = JOptionPane.showConfirmDialog(null, panel, "Next Nhiệm Vụ", JOptionPane.OK_CANCEL_OPTION);
    if (result == JOptionPane.OK_OPTION) {
        String name = nameField.getText().trim();
        String taskStr = taskIdField.getText().trim();

        if (name.isEmpty() || taskStr.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Không được để trống");
            return;
        }

        try {
            int taskId = Integer.parseInt(taskStr);
            if (taskId < 1 || taskId > 100) {
                JOptionPane.showMessageDialog(null, "ID nhiệm vụ 1-100");
                return;
            }

            Player player = Client.gI().getPlayer(name);
            if (player == null) {
                JOptionPane.showMessageDialog(null, "Người chơi không online");
                return;
            }

            // Lặp gửi nhiệm vụ đến khi đạt task yêu cầu
            while (player.playerTask.taskMain.id < taskId) {
                TaskService.gI().sendNextTaskMain(player);
            }

            JOptionPane.showMessageDialog(null, "Đã cập nhật nhiệm vụ đến ID: " + player.playerTask.taskMain.id + " cho " + player.name);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "ID nhiệm vụ không hợp lệ");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Đã xảy ra lỗi: " + e.getMessage());
        }
    }
}
private void createFormSenditem1() {
    JTextField playerNameField = new JTextField();
    JTextField itemIdField = new JTextField();
    JTextField optionIdField = new JTextField();
    JTextField optionValueField = new JTextField();
    JTextField itemQuantityField = new JTextField();

    Object[] message = {
        "Tên người chơi:", playerNameField,
        "ID Item (nhiều ID cách nhau bằng '-'): ", itemIdField,
        "ID Option (cách nhau bằng '-'): ", optionIdField,
        "Giá trị Option (cách nhau bằng '-'): ", optionValueField,
        "Số lượng:", itemQuantityField
    };

    int option = JOptionPane.showConfirmDialog(null, message, "Buff vật phẩm", JOptionPane.OK_CANCEL_OPTION);
    if (option == JOptionPane.OK_OPTION) {
        try {
            String playerName = playerNameField.getText();
            int slItemBuff = Integer.parseInt(itemQuantityField.getText());

            Player pBuffItem = Client.gI().getPlayer(playerName);
            if (pBuffItem != null) {
                String txtBuff = "Buff to player: " + pBuffItem.name + "\b";

                String[] itemIdList = itemIdField.getText().split("-");

                String[] idOptions = optionIdField.getText().split("-");
                String[] valOptions = optionValueField.getText().split("-");

                if (idOptions.length != valOptions.length) {
                    JOptionPane.showMessageDialog(null, "Số lượng ID Option và Giá trị không khớp!");
                    return;
                }

                for (String itemIdStr : itemIdList) {
                    int idItemBuff = Integer.parseInt(itemIdStr.trim());

                    // Buff tiền đặc biệt
                    if (idItemBuff == -1) {
                        pBuffItem.inventory.gold = Math.min(pBuffItem.inventory.gold + (long) slItemBuff, Inventory.LIMIT_GOLD);
                        txtBuff += slItemBuff + " vàng\b";
                        Service.getInstance().sendMoney(pBuffItem);
                    } else if (idItemBuff == -2) {
                        pBuffItem.inventory.gem = Math.min(pBuffItem.inventory.gem + slItemBuff, 2_000_000_000);
                        txtBuff += slItemBuff + " ngọc\b";
                        Service.getInstance().sendMoney(pBuffItem);
                    } else if (idItemBuff == -3) {
                        pBuffItem.inventory.ruby = Math.min(pBuffItem.inventory.ruby + slItemBuff, 2_000_000_000);
                        txtBuff += slItemBuff + " ngọc khóa\b";
                        Service.getInstance().sendMoney(pBuffItem);
                    } else {
                        // Tạo và gán option cho item thường
                        Item itemBuff = ItemService.gI().createNewItem((short) idItemBuff);
                        for (int i = 0; i < idOptions.length; i++) {
                            int optId = Integer.parseInt(idOptions[i].trim());
                            int optVal = Integer.parseInt(valOptions[i].trim());
                            itemBuff.itemOptions.add(new Item.ItemOption(optId, optVal));
                        }
                        itemBuff.quantity = slItemBuff;

                        txtBuff += "x" + slItemBuff + " " + itemBuff.template.name + " (ID: " + idItemBuff + ")\b";

                        InventoryServiceNew.gI().addItemBag(pBuffItem, itemBuff);
                    }
                }

                InventoryServiceNew.gI().sendItemBags(pBuffItem);
                NpcService.gI().createTutorial(pBuffItem, 27723, txtBuff);
            } else {
                JOptionPane.showMessageDialog(null, "Người chơi không online");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Dữ liệu nhập không hợp lệ!");
        }
    }
}

private void createFormSendItemSKH() {
    JTextField playerNameField = new JTextField();
    JTextField itemIdField = new JTextField();
    JTextField optionSKHField = new JTextField();
    JTextField optionBuffField = new JTextField();
    JTextField optionBuffValueField = new JTextField();
    JTextField itemQuantityField = new JTextField();

    Object[] message = {
        "--- SEND ITEM SKH ---",
        "Tên người chơi:", playerNameField,
        "ID Item:", itemIdField,
        "ID Option SKH:", optionSKHField,
        "ID Option phụ:", optionBuffField,
        "Giá trị Option phụ:", optionBuffValueField,
        "Số lượng item:", itemQuantityField
    };

    int option = JOptionPane.showConfirmDialog(null, message, "SEND ITEM SKH", JOptionPane.OK_CANCEL_OPTION);
    if (option == JOptionPane.OK_OPTION) {
        try {
            String playerName = playerNameField.getText().trim();
            int itemId = Integer.parseInt(itemIdField.getText().trim());
            int optionSKH = Integer.parseInt(optionSKHField.getText().trim());
            int optionBuff = Integer.parseInt(optionBuffField.getText().trim());
            int valueBuff = Integer.parseInt(optionBuffValueField.getText().trim());
            int quantity = Integer.parseInt(itemQuantityField.getText().trim());

            Player player = Client.gI().getPlayer(playerName);
            if (player == null) {
                JOptionPane.showMessageDialog(null, "Người chơi không online!");
                return;
            }

            // Tạo item
            Item item = ItemService.gI().createNewItem((short) itemId);
            item.itemOptions.add(new Item.ItemOption(optionSKH, 0)); // Option SKH
            item.itemOptions.add(new Item.ItemOption(30, 0)); // Option mặc định SKH
            item.itemOptions.add(new Item.ItemOption(optionBuff, valueBuff)); // Option phụ

            // Thêm option phụ ẩn tùy vào loại SKH
            switch (optionSKH) {
                case 127 : item.itemOptions.add(new Item.ItemOption(139, 0));
                case 128 : item.itemOptions.add(new Item.ItemOption(140, 0));
                case 129 : item.itemOptions.add(new Item.ItemOption(141, 0));
                case 130 : item.itemOptions.add(new Item.ItemOption(142, 0));
                case 131 : item.itemOptions.add(new Item.ItemOption(143, 0));
                case 132 : item.itemOptions.add(new Item.ItemOption(144, 0));
                case 133 : item.itemOptions.add(new Item.ItemOption(136, 0));
                case 134 : item.itemOptions.add(new Item.ItemOption(137, 0));
                case 135 : item.itemOptions.add(new Item.ItemOption(138, 0));
            }

            item.quantity = quantity;

            // Gửi item
            InventoryServiceNew.gI().addItemBag(player, item);
            InventoryServiceNew.gI().sendItemBags(player);

            String messageLog = "Buff to player: " + player.name + "\n"
                    + "x" + quantity + " " + item.template.name + "\n"
                    + "Option SKH: " + optionSKH + ", Option phụ: " + optionBuff + " +" + valueBuff;

            NpcService.gI().createTutorial(player, 24, messageLog);
            JOptionPane.showMessageDialog(null, "Gửi item thành công cho " + playerName);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Dữ liệu nhập không hợp lệ: " + e.getMessage());
        }
    }
}
    public static void startAntiDDoS() {
        try {
            Runtime rt = Runtime.getRuntime();
            String command = "cmd /c start \"\" \"E:\\Downloads\\v12\\NRO 2017\\chongddos\\run_chongddosvv.bat\"";

            rt.exec(command);
            Logger.log("Đã bật chống DDoS");
        } catch (IOException ex) {
           Logger.logException(obito.class, ex, "Không thể bật chống DDoS");
        }
    }

    private void scheduleMaintenance() {
        String minutesStr = minutesField.getText();
        try {
            int minutes = Integer.parseInt(minutesStr);
            if (minutes <= 0) {
                messageLabel.setText("Số phút phải lớn hơn 0");
                return;
            }
            // Lưu giá trị vào tệp
            try {
                File file = new File("maintenanceTime.txt");
                FileWriter fw = new FileWriter(file);
                fw.write(String.valueOf(minutes));
                fw.close();
            } catch (Exception e) {

            }

            long delay = minutes * 60L * 1000L;
            remainingSeconds = minutes * 60;
            countdownLabel.setText("Thời gian còn lại: " + formatTime(remainingSeconds));
            countdownTimer = new Timer(1000, e -> {
                remainingSeconds--;
                countdownLabel.setText("Thời gian còn lại: " + formatTime(remainingSeconds));
                if (remainingSeconds == 0) {
                    countdownTimer.stop();
                    Maintenance.gI().start(15);
                    messageLabel.setText("");
                    countdownLabel.setText("");
                }
            });
            countdownTimer.start();

            messageLabel.setText("Đã hẹn bảo trì sau " + minutes + " phút");
        } catch (NumberFormatException e) {
            String error = e.getMessage();
            if (error.equals("For input string: \"\"")) {
                JOptionPane.showMessageDialog(null, "Không được để trống");
            } else {
                JOptionPane.showMessageDialog(null, "Bạn nhập sai phút");
            }

        }
    }

    private String formatTime(int seconds) {
        int minutes = seconds / 60;
        int secs = seconds % 60;
        return String.format("%02d:%02d", minutes, secs);
    }

    private void confirmExit() {
        int dialogButton = JOptionPane.YES_NO_OPTION;
        int dialogResult = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn thoát chương trình?", "Thoát", dialogButton);
        if (dialogResult == 0) {
            System.exit(0);
        }
    }

    @Override
    public void setDefaultCloseOperation(int operation) {
        if (operation == JFrame.EXIT_ON_CLOSE) {
            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    confirmExit();
                }
            });
        } else {
            super.setDefaultCloseOperation(operation);
        }
    }

    private void scheduleMaintenance(JComboBox<Integer> hoursComboBox, JComboBox<Integer> minutesComboBox) {
        int hours = hoursComboBox.getItemAt(hoursComboBox.getSelectedIndex());
        int minutes = minutesComboBox.getItemAt(minutesComboBox.getSelectedIndex());
        if (minutes == -1 || hours == -1) {
            JOptionPane.showMessageDialog(this, "Thời gian sai");
            return;
        }
        // Ghi giá trị vào tệp tin
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("maintenanceConfig.txt"))) {
            writer.write(hours + "\n");
            writer.write(minutes + "\n");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        AtomicBoolean timeReached = new AtomicBoolean(false); // Sử dụng AtomicBoolean để đảm bảo tính nhất quán trong thread
        info.setText("Time Bảo trì TỰ ĐỘNG " + "vào lúc " + hours + ":" + minutes);
        new Thread(() -> {
            while (!timeReached.get()) { // Kiểm tra điều kiện dừng
                try {
                    LocalTime currentTime = LocalTime.now();
                    int hourss = hoursComboBox.getItemAt(hoursComboBox.getSelectedIndex());
                    int minutess = minutesComboBox.getItemAt(minutesComboBox.getSelectedIndex());
                    int hour_now = currentTime.getHour();
                    int minute_now = currentTime.getMinute();

                    if (hourss == hour_now && minutess == minute_now) {
                        performMaintenance();
                        timeReached.set(true); // Gán giá trị true để dừng vòng lặp
                    }
                    Thread.sleep(10000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private long calculateDelay(int hours, int minutes) {
        long currentMillis = System.currentTimeMillis();
        long scheduledMillis = currentMillis + (hours * 60 * 60 * 1000) + (minutes * 60 * 1000);
        return scheduledMillis - currentMillis;
    }

    private void performMaintenance() {
        Maintenance.gI().start(15);

    }

    public static void runBatchFile(String batchFilePath) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/c", "start", batchFilePath);
        Process process = processBuilder.start();
        try {
            process.waitFor();
        } catch (Exception e) {
        }
    }
    private void createFormBanIP() {
    JPanel panel = new JPanel();
    JTextField ipField = new JTextField(15);
    panel.add(new JLabel("Nhập IP cần ban:"));
    panel.add(ipField);

    int result = JOptionPane.showConfirmDialog(null, panel, "Ban IP", JOptionPane.OK_CANCEL_OPTION);
    if (result == JOptionPane.OK_OPTION) {
        String ip = ipField.getText().trim();
        if (ip.isEmpty()) {
            JOptionPane.showMessageDialog(null, "IP không được để trống");
            return;
        }

        try {
            File file = new File("banip.txt");
            if (!file.exists()) file.createNewFile();

            BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
            writer.write(ip);
            writer.newLine();
            writer.close();

            // 🔥 Tìm và kick người chơi đang dùng IP này
            int count = 0;
            for (Player p : Client.gI().getPlayers()) {
                if (p != null && p.getSession() != null && ip.equals(p.getSession().ipAddress)) {
                    // ✅ Gửi thông báo trước khi kick
                    Service.gI().sendThongBaoOK(p.getSession(), "Bạn đã bị sút khỏi máy chủ");
                    p.getSession().disconnect();
                    count++;
                }
            }

            JOptionPane.showMessageDialog(null, "Đã ban IP: " + ip + (count > 0 ? "\n" + count + " người chơi đã bị kick." : "\nKhông có ai đang dùng IP này."));

        } catch (IOException e) {
            Logger.logException(obito.class, e, "Lỗi khi ghi file banip.txt");
            JOptionPane.showMessageDialog(null, "Lỗi khi ghi file: " + e.getMessage());
        }
    }
}

private void createFormUnbanIP() {
    JPanel panel = new JPanel();
    JTextField ipField = new JTextField(15);
    panel.add(new JLabel("Nhập IP cần gỡ ban:"));
    panel.add(ipField);

    int result = JOptionPane.showConfirmDialog(null, panel, "Gỡ ban IP", JOptionPane.OK_CANCEL_OPTION);
    if (result == JOptionPane.OK_OPTION) {
        String ipToRemove = ipField.getText().trim();
        if (ipToRemove.isEmpty()) {
            JOptionPane.showMessageDialog(null, "IP không được để trống");
            return;
        }

        File banFile = new File("banip.txt");
        if (!banFile.exists()) {
            JOptionPane.showMessageDialog(null, "Không tìm thấy file banip.txt");
            return;
        }

        try {
            List<String> lines = new ArrayList<>();
            boolean found = false;

            try (BufferedReader reader = new BufferedReader(new FileReader(banFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().equals(ipToRemove)) {
                        found = true;
                        continue; // bỏ dòng chứa IP cần xóa
                    }
                    lines.add(line);
                }
            }

            if (!found) {
                JOptionPane.showMessageDialog(null, "IP không tồn tại trong danh sách ban.");
                return;
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(banFile))) {
                for (String line : lines) {
                    writer.write(line);
                    writer.newLine();
                }
            }

            FirewallManager.removeIpFromBanFile(ipToRemove); // Gỡ khỏi RAM để login ngay
            JOptionPane.showMessageDialog(null, "✅ Đã gỡ ban IP: " + ipToRemove);

        } catch (IOException e) {
            Logger.logException(obito.class, e, "Lỗi khi gỡ ban IP");
            JOptionPane.showMessageDialog(null, "Lỗi khi gỡ ban IP: " + e.getMessage());

        }
        
    }
}
    private void optimizeRAM() {
    long before = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
    System.gc();
    long after = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
    long freed = before - after;

    Logger.log("✅ Đã tối ưu RAM. Bộ nhớ giải phóng: " + (freed / (1024 * 1024)) + " MB");

    JOptionPane.showMessageDialog(null, "✅ Tối ưu RAM thành công\nĐã giải phóng: " + (freed / (1024 * 1024)) + " MB");
    }
    

    }
