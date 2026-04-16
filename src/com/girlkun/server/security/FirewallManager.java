package com.girlkun.server.security;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class FirewallManager {

    // Tập hợp IP đang bị chặn trong RAM
    private static final Set<String> blockedIps = ConcurrentHashMap.newKeySet();

    // Danh sách IP được phép (vượt qua block)
    public static final Set<String> whiteListIps = ConcurrentHashMap.newKeySet();

    // Danh sách IP tấn công và số lượng kết nối
    public static final Map<String, Integer> attackIps = new ConcurrentHashMap<>();

    // Giao diện quản lý firewall (log UI)
    public static FirewallManagerUI uiInstance;

    // Trạng thái bật chống DDoS và chặn IP ngoại quốc
    private static volatile boolean firewallEnabled = false;
    private static volatile boolean blockForeignEnabled = false;

    // Trạng thái bật chống DDoS
    public static boolean isDdosProtectionEnabled() {
        return firewallEnabled;
    }

    public static boolean isBlockForeignEnabled() {
        return blockForeignEnabled;
    }

    public static void setFirewallEnabled(boolean enabled) {
        firewallEnabled = enabled;
    }

    public static void setBlockForeignEnabled(boolean enabled) {
        blockForeignEnabled = enabled;
    }

    /**
     * ✅ Chặn IP vào RAM (không ghi file ở đây)
     */
    public static void blockIp(String ip) {
        blockedIps.add(ip);
    }

    /**
     * ✅ Gỡ IP khỏi RAM và danh sách tấn công
     */
    public static void unblockIp(String ip) {
        blockedIps.remove(ip);
        attackIps.remove(ip);
    }

    /**
     * ✅ Kiểm tra IP có được phép truy cập không
     */
    public static boolean isIpAllowed(String ip) {
        if (!firewallEnabled) return true;                    // Không bật chống DDoS
        if (whiteListIps.contains(ip)) return true;           // IP trong whitelist
        if (isVietnamIp(ip)) return true;                     // IP VN thì cho phép
        if (blockForeignEnabled && !isVietnamIp(ip)) return false; // Bật chặn IP ngoại
        return !blockedIps.contains(ip);                      // Nếu chưa bị chặn thì cho phép
    }

    /**
     * ✅ Kiểm tra IP có phải của Việt Nam không (dựa vào đầu IP)
     */
    public static boolean isVietnamIp(String ip) {
        return ip.startsWith("1.") || ip.startsWith("14.") || ip.startsWith("27.") ||
               ip.startsWith("42.") || ip.startsWith("101.") || ip.startsWith("113.") ||
               ip.startsWith("115.") || ip.startsWith("116.") || ip.startsWith("117.") ||
               ip.startsWith("118.") || ip.startsWith("119.") || ip.startsWith("120.") ||
               ip.startsWith("125.") || ip.startsWith("171.") || ip.startsWith("172.") ||
               ip.startsWith("203.") || ip.startsWith("222.");
    }

    /**
     * ✅ Trả về danh sách IP bị chặn (chỉ đọc)
     */
    public static Set<String> getBlockedIps() {
        return Collections.unmodifiableSet(blockedIps);
    }

    /**
     * ✅ Ghi IP mới vào file banip.txt (nếu chưa tồn tại)
     */
    public static void appendIpToBanFile(String ip) {
    try {
        File file = new File("banip.txt");
        if (!file.exists()) file.createNewFile();

        // Tránh ghi trùng IP
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line.trim());
            }
        }

        if (!lines.contains(ip)) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
                writer.write(ip);
                writer.newLine();
            }
        }

        // ✅ Block ngay trong RAM để hiệu lực tức thì
        blockIp(ip);

        if (uiInstance != null) {
            uiInstance.log("⛔ Đã chặn IP: " + ip);
        }

    } catch (IOException e) {
        e.printStackTrace();
    }
}

    
    public static void loadBannedIps() {
    File file = new File("banip.txt");
    if (!file.exists()) return;
    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
        String line;
        while ((line = reader.readLine()) != null) {
            if (!line.trim().isEmpty()) {
                blockIp(line.trim());
            }
        }
        if (uiInstance != null) {
            uiInstance.log("✅ Đã load danh sách banip.txt vào RAM");
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
}


    /**
     * ✅ Gỡ IP khỏi banip.txt và unblock trong RAM
     */
    public static void removeIpFromBanFile(String ipToRemove) {
    File file = new File("banip.txt");
    if (!file.exists()) return;

    try {
        List<String> updatedLines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().equals(ipToRemove.trim())) {
                    updatedLines.add(line);
                }
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String line : updatedLines) {
                writer.write(line);
                writer.newLine();
            }
        }

        // ✅ Xóa khỏi RAM
        unblockIp(ipToRemove);

        // ✅ Log nếu có UI
        if (uiInstance != null) {
            uiInstance.log("✅ Đã gỡ IP khỏi ban: " + ipToRemove);
        }

    } catch (IOException e) {
        e.printStackTrace();

        }
    }
}
