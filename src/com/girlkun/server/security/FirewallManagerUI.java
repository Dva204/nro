package com.girlkun.server.security;

import com.girlkun.server.Client;

import javax.swing.*;
import java.awt.event.*;
import java.util.Map;

public class FirewallManagerUI extends JFrame {

    private JCheckBox chkEnableFirewall, chkBlockForeign;
    private JTextArea areaAttackIps;
    private JTextField txtWhitelistIp;
    private JButton btnAddWhitelist;

    private DefaultListModel<String> logModel = new DefaultListModel<>();
    private JList<String> logList = new JList<>(logModel);

    public FirewallManagerUI() {
        FirewallManager.uiInstance = this;

        setTitle("Firewall Manager");
        setSize(500, 500);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Bật chống DDoS
        chkEnableFirewall = new JCheckBox("Bật chống DDoS");
        chkEnableFirewall.setBounds(20, 20, 200, 30);
        add(chkEnableFirewall);
        chkEnableFirewall.addActionListener(e -> {
            boolean enabled = chkEnableFirewall.isSelected();
            FirewallManager.setFirewallEnabled(enabled);
            JOptionPane.showMessageDialog(this, enabled ? "✅ Đã bật chống DDoS!" : "❌ Đã tắt chống DDoS!");
        });

        // Chặn IP ngoại quốc
        chkBlockForeign = new JCheckBox("Chặn IP ngoại quốc");
        chkBlockForeign.setBounds(20, 60, 200, 30);
        add(chkBlockForeign);
        chkBlockForeign.addActionListener(e -> {
            boolean enabled = chkBlockForeign.isSelected();
            FirewallManager.setBlockForeignEnabled(enabled);
            JOptionPane.showMessageDialog(this, enabled ? "✅ Đã bật chặn IP ngoại quốc!" : "❌ Đã tắt chặn IP ngoại quốc!");
        });

        // Label và ô nhập IP
        JLabel label = new JLabel("Whitelisted / IP xử lý:");
        label.setBounds(20, 100, 200, 25);
        add(label);

        txtWhitelistIp = new JTextField();
        txtWhitelistIp.setBounds(180, 100, 150, 25);
        add(txtWhitelistIp);

        // Thêm whitelist
        btnAddWhitelist = new JButton("Thêm WL");
        btnAddWhitelist.setBounds(340, 100, 100, 25);
        add(btnAddWhitelist);
        btnAddWhitelist.addActionListener(e -> {
            String ip = txtWhitelistIp.getText().trim();
            if (!ip.isEmpty()) {
                FirewallManager.whiteListIps.add(ip);
                log("✅ Whitelist: " + ip);
                JOptionPane.showMessageDialog(this, "✅ Đã thêm " + ip + " vào whitelist!");
            }
        });

        // Tạo nút Ban IP
JButton btnBanIp = new JButton("Ban IP");
btnBanIp.setBounds(20, 130, 100, 25);
add(btnBanIp);

// Xử lý sự kiện Ban IP
btnBanIp.addActionListener(e -> {
    String ip = JOptionPane.showInputDialog(this, "Nhập IP cần chặn:");
    if (ip != null && !ip.trim().isEmpty()) {
        ip = ip.trim();
        FirewallManager.blockIp(ip);
        FirewallManager.appendIpToBanFile(ip);
        com.girlkun.server.Client.gI().kickSessionByIp(ip);
        log("❌ Đã chặn IP: " + ip);
        JOptionPane.showMessageDialog(this, "❌ Đã chặn IP " + ip);
    }
});

// Tạo nút Gỡ chặn IP
JButton btnUnbanIp = new JButton("Gỡ chặn");
btnUnbanIp.setBounds(140, 130, 100, 25);
add(btnUnbanIp);

// Xử lý sự kiện Gỡ IP
btnUnbanIp.addActionListener(e -> {
    String ip = JOptionPane.showInputDialog(this, "Nhập IP cần gỡ chặn:");
    if (ip != null && !ip.trim().isEmpty()) {
        ip = ip.trim();
        FirewallManager.removeIpFromBanFile(ip); // Gỡ trong file & RAM
        log("✅ Đã gỡ IP khỏi ban: " + ip);
        JOptionPane.showMessageDialog(this, "✅ Đã gỡ chặn IP " + ip);
    }
});


        // Danh sách IP tấn công
        areaAttackIps = new JTextArea();
        areaAttackIps.setEditable(false);
        JScrollPane scroll = new JScrollPane(areaAttackIps);
        scroll.setBounds(20, 180, 440, 120);
        add(scroll);

        // Log real-time
        JScrollPane logScroll = new JScrollPane(logList);
        logScroll.setBounds(20, 310, 440, 140);
        add(logScroll);

        // Tự động cập nhật danh sách IP tấn công
        Timer timer = new Timer(3000, e -> refreshAttackIps());
        timer.start();

        setVisible(true);
    }

    private void refreshAttackIps() {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, Integer> entry : FirewallManager.attackIps.entrySet()) {
            builder.append(entry.getKey()).append(" - ").append(entry.getValue()).append(" lần\n");
        }
        areaAttackIps.setText(builder.toString());
    }

    public void log(String message) {
        SwingUtilities.invokeLater(() -> {
            logModel.addElement(message);
            if (logModel.getSize() > 100) {
                logModel.remove(0);
            }
        });
    }

    public static void main(String[] args) {
        new FirewallManagerUI();
    }
}
