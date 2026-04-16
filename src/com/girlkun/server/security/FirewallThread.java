package com.girlkun.server.security;

import com.girlkun.server.Client;
import com.girlkun.server.io.MySession;
import com.girlkun.utils.Logger;

import java.util.HashMap;
import java.util.Map;

public class FirewallThread extends Thread {

    private final Map<String, Integer> connectionCounter = new HashMap<>();

    @Override
    public void run() {
        while (true) {
            try {
                connectionCounter.clear();

                // ✅ Đếm số lượng kết nối của mỗi IP
                for (MySession session : Client.gI().getSessions()) {
                    String ip = session.ipAddress;
                    connectionCounter.put(ip, connectionCounter.getOrDefault(ip, 0) + 1);
                }

                // ✅ Kiểm tra các IP có dấu hiệu tấn công
                for (Map.Entry<String, Integer> entry : connectionCounter.entrySet()) {
                    String ip = entry.getKey();
                    int count = entry.getValue();

                    // ⚠️ Chỉ xử lý nếu bật firewall và IP không được phép
                    if (count > 30 && FirewallManager.isDdosProtectionEnabled() && !FirewallManager.isIpAllowed(ip)) {
                        FirewallManager.attackIps.merge(ip, 1, Integer::sum);

                        Logger.log(Logger.RED + "⚠️ Phát hiện IP tấn công: " + ip + " - Số kết nối: " + count + Logger.RESET);

                        if (FirewallManager.uiInstance != null) {
                            FirewallManager.uiInstance.log("⛔ Chặn IP: " + ip + " - " + count + " kết nối");
                        }

                        if (!FirewallManager.getBlockedIps().contains(ip)) {
                            FirewallManager.blockIp(ip);
                            FirewallManager.appendIpToBanFile(ip); // ghi luôn vào file, restart không mất
                            Client.gI().kickSessionByIp(ip);
                        }
                    }
                }

                Thread.sleep(3000); // 🔁 Kiểm tra mỗi 3 giây

            } catch (Exception e) {
                Logger.logException(FirewallThread.class, e, "Lỗi trong FirewallThread");
            }
        }
    }
}
