package com.girlkun.server;

import com.girlkun.MaQuaTang.MaQuaTangManager;
import com.girlkun.database.GirlkunDB;
import com.girlkun.server.ServerManager;
import com.girlkun.server.obito;
import com.girlkun.server.Maintenance;
import com.girlkun.services.*;
import com.girlkun.models.player.Player;
import com.girlkun.models.item.Item;
import com.girlkun.utils.Logger;
import com.girlkun.consts.ConstEvent;
import com.girlkun.server.security.FirewallManager;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

public class ToolServer implements Runnable {

    private static ToolServer instance;
    private boolean running = false;
    private int port = 14446; // mặc định port

    public static ToolServer gI() {
        if (instance == null) {
            instance = new ToolServer();
        }
        return instance;
    }

    public void open() {
        if (!running) {
            running = true;
            new Thread(this, "ToolServer").start();
            Logger.log("ToolServer started at port " + port);
        }
    }

    public void open(int p) {
        this.port = p;
        open();
    }

    public void close() {
        running = false;
    }

    @Override
    public void run() {
        try (ServerSocket ss = new ServerSocket(port)) {
            while (running) {
                Socket s = ss.accept();
                new Thread(() -> handle(s)).start();
            }
        } catch (Exception e) {
            Logger.logException(ToolServer.class, e, "ToolServer.run");
        }
    }

    private void handle(Socket s) {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream())); PrintWriter out = new PrintWriter(new OutputStreamWriter(s.getOutputStream(), "UTF-8"), true)) {
            String line = in.readLine();
            if (line == null) {
                out.println("ERR null");
                return;
            }
            Logger.log("ToolServer recv: " + line);

            String cmd = line.split(" ", 2)[0].trim().toUpperCase();
            String payload = line.length() > cmd.length() ? line.substring(cmd.length()).trim() : "";

            switch (cmd) {
                case "KICK":
                    out.println(handleKick(payload));
                    break;
                case "MAINT":
                    out.println(handleMaint(payload));
                    break;
                case "SETEXP":
                    out.println(handleSetExp(payload));
                    break;
                case "NAP":
                    out.println(handleNap(payload));
                    break;
                case "NEXTTASK":
                    out.println(handleNextTask(payload));
                    break;
                case "SENDITEM":
                    out.println(handleSendItem(payload));
                    break;
                case "SENDITEM_ADV":
                    out.println(handleSendItemAdv(payload));
                    break;
                case "EVENT":
                    out.println(handleEvent(payload));
                    break;
                case "PROMO":
                    out.println(handlePromo(payload));
                    break;
                case "BANIP":
                    out.println(handleBanIp(payload));
                    break;
                case "UNBANIP":
                    out.println(handleUnbanIp(payload));
                    break;
                case "OPTIMIZE":
                    out.println(handleOptimize());
                    break;
                case "STATS":
                    out.println(handleStats());
                    break;
                case "CREATEGIFTCODE":
                    out.println(handleCreateGiftCode(payload));
                    break;
                case "LISTGIFTCODE":
                    out.println(handleListGiftCode());
                    break;
                case "DELETEGIFTCODE":
                    out.println(handleDeleteGiftCode(payload));
                    break;
                case "UPDATEGIFTCODE":
                    out.println(handleUpdateGiftCode(payload));
                    break;
                case "RELOAD_GIFTCODE":
                    out.println(handleReloadGiftCode());
                    break;

                default:
                    out.println("UNKNOWN_CMD");
            }

        } catch (Exception e) {
            Logger.logException(ToolServer.class, e, "handle");
        } finally {
            try {
                s.close();
            } catch (Exception ignored) {
            }
        }
    }

    // ---------- Handlers ----------
    private String handleKick(String payload) {
        try {
            String name = payload.trim();
            if (name.isEmpty()) {
                return "FAIL Missing name";
            }
            Player p = Client.gI().getPlayer(name);
            if (p != null && p.getSession() != null) {
                Service.gI().sendThongBaoOK(p.getSession(), "Bạn đã bị sút khỏi máy chủ");
                p.getSession().disconnect();
                return " Kick " + name;
            } else {
                return "FAIL Player not online";
            }
        } catch (Exception e) {
            Logger.logException(ToolServer.class, e, "handleKick");
            return "ERR " + e.getMessage();
        }
    }

    private String handleMaint(String payload) {
        try {
            int seconds = 15;
            try {
                seconds = Integer.parseInt(payload.trim());
            } catch (Exception ignored) {
            }
            Maintenance.gI().start(seconds);
            return "Save Time Bảo Trì " + seconds + "s";
        } catch (Exception e) {
            Logger.logException(ToolServer.class, e, "handleMaint");
            return "ERR " + e.getMessage();
        }
    }

    private String handleSetExp(String payload) {
        try {
            int rate = Integer.parseInt(payload.trim());
            Manager.RATE_EXP_SERVER = (byte) rate;
            Logger.error("Exp hiện tại là: " + rate);
            return "Set TNSM Toàn Server " + rate;
        } catch (Exception e) {
            Logger.logException(ToolServer.class, e, "handleSetExp");
            return "ERR " + e.getMessage();
        }
    }

    private String handleNap(String payload) {
        try {
            String[] p = payload.split(" ");
            if (p.length < 2) {
                return "FAIL usage: NAP <username> <vnd>";
            }

            String username = p[0].trim();
            int vndNap = Integer.parseInt(p[1].trim());
            if (vndNap <= 0) {
                return "FAIL vnd must be > 0";
            }

            int km = (Manager.KHUYEN_MAI_NAP > 1)
                    ? Manager.KHUYEN_MAI_NAP : 1;
            int tongCong = vndNap * km;

            Player pl = Client.gI().getPlayer(username);
            if (pl != null && pl.getSession() != null) {
                pl.getSession().vnd += tongCong;
                pl.getSession().tongnap += tongCong;

                try (Connection con = GirlkunDB.getConnection(); PreparedStatement ps = con.prepareStatement(
                        "UPDATE account SET vnd = vnd + ?, tongnap = tongnap + ? WHERE id = ?")) {

                    ps.setInt(1, tongCong);
                    ps.setInt(2, tongCong);
                    ps.setInt(3, pl.getSession().userId);
                    ps.executeUpdate();
                } catch (Exception e) {
                    Logger.logException(ToolServer.class, e, "handleNap DB for online player");
                    return "ERR DB " + e.getMessage();
                }

                Service.getInstance().sendMoney(pl);
                return " Nạp " + vndNap + " VND cho " + username + " (x" + km + ")";
            }

            try (Connection con = GirlkunDB.getConnection()) {
                int uid = -1;
                try (PreparedStatement ps = con.prepareStatement("SELECT id FROM account WHERE username=? LIMIT 1")) {
                    ps.setString(1, username);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        uid = rs.getInt("id");
                    }
                    rs.close();
                }
                if (uid <= 0) {
                    return "FAIL user not found";
                }
                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE account SET vnd = vnd + ?, tongnap = tongnap + ? WHERE id = ?")) {
                    ps.setInt(1, tongCong);
                    ps.setInt(2, tongCong);
                    ps.setInt(3, uid);
                    ps.executeUpdate();
                }
            } catch (Exception e) {
                Logger.logException(ToolServer.class, e, "handleNap DB for offline player");
                return "ERR DB " + e.getMessage();
            }

            return " Nạp" + vndNap + " VND cho " + username + " (x" + km + ")";
        } catch (NumberFormatException e) {
            return "FAIL vnd not a number";
        } catch (Exception e) {
            Logger.logException(ToolServer.class, e, "handleNap");
            return "ERR " + e.getMessage();
        }
    }

    private String handleUpdateGiftCode(String payload) {
        try (Connection conn = GirlkunDB.getConnection()) {
            String[] parts = payload.split("\\|", -1);
            if (parts.length < 8) {
                return "ERR Invalid payload, expected 8 parts";
            }

            int id = Integer.parseInt(parts[0]);
            String code = parts[1];
            int limit = Integer.parseInt(parts[2]);
            String listuser = parts[3];
            String listItem = parts[4]; // Đổi tên biến cho rõ
            int bagCount = Integer.parseInt(parts[5]);
            String itemoption = parts[6]; // Lấy option chung
            String expired = parts[7];

            // Cập nhật câu lệnh SQL
            String sql = "UPDATE giftcode SET code=?, `limit`=?, listuser=?, listItem=?, bagCount=?, itemoption=?, expired=? WHERE id=?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, code);
                ps.setInt(2, limit);
                ps.setString(3, listuser);
                ps.setString(4, listItem);
                ps.setInt(5, bagCount);
                ps.setString(6, itemoption);
                ps.setTimestamp(7, Timestamp.valueOf(expired));
                ps.setInt(8, id);

                int updated = ps.executeUpdate();
                return updated > 0 ? "Update Code Thành Công" : "ERR Not found";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "ERR " + e.getMessage();
        }
    }

    private String handleNextTask(String payload) {
        try {
            String[] p = payload.split(" ");
            if (p.length < 2) {
                return "FAIL NEXTTASK <name> <taskId>";
            }
            String name = p[0];
            int taskId = Integer.parseInt(p[1]);
            Player player = Client.gI().getPlayer(name);
            if (player == null) {
                return "FAIL player not online";
            }
            while (player.playerTask.taskMain.id < taskId) {
                TaskService.gI().sendNextTaskMain(player);
            }
            return "Next Nhiệm Vụ " + player.playerTask.taskMain.id;
        } catch (Exception e) {
            Logger.logException(ToolServer.class, e, "handleNextTask");
            return "ERR " + e.getMessage();
        }
    }

    private String handleSendItem(String payload) {
        try {
            String[] p = payload.split("\\|", -1);
            if (p.length < 5) {
                return "FAIL SENDITEM name|itemIds|optIds|optVals|qty";
            }

            String playerName = p[0].trim();
            String itemIdList = p[1].trim();
            String optionIdList = p[2].trim();
            String optionValList = p[3].trim();
            int slItemBuff = Integer.parseInt(p[4].trim());

            Player pBuffItem = Client.gI().getPlayer(playerName);
            if (pBuffItem == null) {
                return "FAIL player not online";
            }

            StringBuilder txtBuff = new StringBuilder("Admin Gửi Đồ");
            String[] itemIds = itemIdList.split("-");
            String[] idOptions = optionIdList.isEmpty() ? new String[0] : optionIdList.split("-");
            String[] valOptions = optionValList.isEmpty()
                    ? new String[0] : optionValList.split("-");

            if (idOptions.length != valOptions.length) {
                return "FAIL số lượng ID Option và Giá trị không khớp!";
            }

            for (String itemIdStr : itemIds) {
                int idItemBuff = Integer.parseInt(itemIdStr.trim());
                if (idItemBuff == -1) {
                    pBuffItem.inventory.gold = Math.min(pBuffItem.inventory.gold + (long) slItemBuff, InventoryServiceNew.LIMIT_GOLD);
                    Service.getInstance().sendMoney(pBuffItem);
                } else if (idItemBuff == -2) {
                    pBuffItem.inventory.gem = Math.min(pBuffItem.inventory.gem + slItemBuff, 2_000_000_000);
                    Service.getInstance().sendMoney(pBuffItem);
                } else if (idItemBuff == -3) {
                    pBuffItem.inventory.ruby = Math.min(pBuffItem.inventory.ruby + slItemBuff, 2_000_000_000);
                    Service.getInstance().sendMoney(pBuffItem);
                } else {
                    Item itemBuff = ItemService.gI().createNewItem((short) idItemBuff);
                    for (int i = 0; i < idOptions.length; i++) {
                        int optId = Integer.parseInt(idOptions[i].trim());
                        int optVal = Integer.parseInt(valOptions[i].trim());
                        itemBuff.itemOptions.add(new Item.ItemOption(optId, optVal));
                    }
                    itemBuff.quantity = slItemBuff;
                    InventoryServiceNew.gI().addItemBag(pBuffItem, itemBuff);
                }
            }

            InventoryServiceNew.gI().sendItemBags(pBuffItem);
            NpcService.gI().createTutorial(pBuffItem, 27723, txtBuff.toString());
            return "Gửi Vật Phẩm Thành Công";

        } catch (Exception e) {
            Logger.logException(ToolServer.class, e, "handleSendItem");
            return "ERR " + e.getMessage();
        }
    }

    private String handleEvent(String payload) {
        try {
            int ev = Integer.parseInt(payload.trim());
            if (ev < 0 || ev > 8) {
                return "FAIL eventId 0-8";
            }
            ConstEvent.EVENT = (byte) ev;
            String nameSk = ConstEvent.gI().getNameEv();
            if(ev == 0){
                return "";
            }
            Service.getInstance().sendThongBaoAllPlayer("|7|Sự kiện " + nameSk + " đang diễn ra\n|5|Xem chi tiết tại NPC " + nameSk);
            Logger.error("Sự kiện đã đổi: " + nameSk);
            return "Đã Thay Đổi Sự Kiện " + ev + " (" + nameSk + ")";
        } catch (Exception e) {
            Logger.logException(ToolServer.class, e, "handleEvent");
            return "ERR " + e.getMessage();
        }
    }

    private String handlePromo(String payload) {
        try {
            int x = Integer.parseInt(payload.trim());
            Manager.KHUYEN_MAI_NAP = (byte) x;
            Logger.error("Promo set x" + x);
            return "Khuyến Mại x" + x;
        } catch (Exception e) {
            Logger.logException(ToolServer.class, e, "handlePromo");
            return "ERR " + e.getMessage();
        }
    }

    private String handleBanIp(String payload) {
        try {
            String ip = payload.trim();
            if (ip.isEmpty()) {
                return "FAIL Missing ip";
            }
            File f = new File("banip.txt");
            try (FileWriter fw = new FileWriter(f, true); BufferedWriter bw = new BufferedWriter(fw)) {
                bw.write(ip);
                bw.newLine();
            }
            int kicked = 0;
            for (Player p : Client.gI().getPlayers()) {
                if (p != null && p.getSession() != null && ip.equals(p.getSession().ipAddress)) {
                    Service.gI().sendThongBaoOK(p.getSession(), "Bạn đã bị sút khỏi máy chủ");
                    p.getSession().disconnect();
                    kicked++;
                }
            }
            try {
                FirewallManager.loadBannedIps();
            } catch (Throwable ignored) {
            }
            return " BanIP " + ip + " kicked=" + kicked;
        } catch (Exception e) {
            Logger.logException(ToolServer.class, e, "handleBanIp");
            return "ERR " + e.getMessage();
        }
    }

    private String handleUnbanIp(String payload) {
        try {
            String ip = payload.trim();
            if (ip.isEmpty()) {
                return "FAIL Missing ip";
            }
            File banFile = new File("banip.txt");
            if (!banFile.exists()) {
                return "FAIL banip.txt not found";
            }
            List<String> keep = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader(banFile))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (!line.trim().equals(ip)) {
                        keep.add(line);
                    }
                }
            }
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(banFile, false))) {
                for (String l : keep) {
                    bw.write(l);
                    bw.newLine();
                }
            }
            try {
                FirewallManager.removeIpFromBanFile(ip);
            } catch (Throwable ignored) {
            }
            return " Unban " + ip;
        } catch (Exception e) {
            Logger.logException(ToolServer.class, e, "handleUnbanIp");
            return "ERR " + e.getMessage();
        }
    }

    private String handleOptimize() {
        try {
            long before = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            System.gc();
            long after = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            long freed = before - after;
            return "Tối Ưu Ram " + (freed / (1024 * 1024)) + " MB";
        } catch (Exception e) {
            Logger.logException(ToolServer.class, e, "handleOptimize");
            return "ERR " + e.getMessage();
        }
    }

    private String handleStats() {
        try {
            int threads = Thread.activeCount();
            int online = Client.gI().getPlayers().size();
            return " Threads:" + threads + " Online:" + online;
        } catch (Exception e) {
            Logger.logException(ToolServer.class, e, "handleStats");
            return "ERR " + e.getMessage();
        }
    }

    private String handleCreateGiftCode(String payload) {
        try {
            // Format: code|limit|p2|p3|p4|listItem|bagCount|itemoption|expired
            String[] p = payload.split("\\|", 9);
            if (p.length < 9) {
                return "FAIL CREATEGIFTCODE: Thiếu tham số. Format: code|limit|_|_|_|listItem|bagCount|itemoption|expired";
            }

            String code = p[0].trim();
            int limit = Integer.parseInt(p[1].trim());
            String listItem = p[5].trim();
            int bagCount = Integer.parseInt(p[6].trim());
            String itemoption = p[7].trim(); // global itemoption (fallback cho dữ liệu cũ)
            String expired = p[8].trim();

            // Validate listItem: phải là JSON array hợp lệ
            // Hỗ trợ 2 format:
            // Cũ: [{"id":2066,"quantity":1}]
            // Mới: [{"id":2066,"quantity":1,"options":[{"id":30,"param":0}]}]
            Object parsedList = JSONValue.parse(listItem);
            if (!(parsedList instanceof JSONArray)) {
                return "FAIL CREATEGIFTCODE: listItem không hợp lệ. Phải là JSON array.\n"
                        + "Ví dụ mới (per-item options):\n"
                        + "[{\"id\":2066,\"quantity\":1,\"options\":[{\"id\":30,\"param\":0}]},"
                        + "{\"id\":2067,\"quantity\":1,\"options\":[]},"
                        + "{\"id\":2065,\"quantity\":1,\"options\":[{\"id\":73,\"param\":0}]}]";
            }

            // Validate itemoption: phải là JSON array hợp lệ
            Object parsedOpt = JSONValue.parse(itemoption);
            if (!(parsedOpt instanceof JSONArray)) {
                return "FAIL CREATEGIFTCODE: itemoption không hợp lệ. Phải là JSON array.\n"
                        + "Ví dụ: [{\"id\":30,\"param\":0}] hoặc [] nếu không có option chung";
            }

            try (Connection con = GirlkunDB.getConnection()) {

                // Kiểm tra code trùng
                try (PreparedStatement psCheck = con.prepareStatement("SELECT COUNT(*) FROM giftcode WHERE code = ?")) {
                    psCheck.setString(1, code);
                    ResultSet rsCheck = psCheck.executeQuery();
                    if (rsCheck.next() && rsCheck.getInt(1) > 0) {
                        return "FAIL CREATEGIFTCODE: Code \"" + code + "\" đã tồn tại!";
                    }
                }

                int nextId = 1;
                try (PreparedStatement psMax = con.prepareStatement("SELECT MAX(id) as maxId FROM giftcode"); ResultSet rs = psMax.executeQuery()) {
                    if (rs.next()) {
                        nextId = rs.getInt("maxId") + 1;
                    }
                }

                String sql = "INSERT INTO giftcode(id, code, type, `delete`, `limit`, listuser, listItem, bagCount, itemoption, create_date, expired) "
                        + "VALUES (?, ?, 1, 1, ?, ?, ?, ?, ?, NOW(), ?)";

                try (PreparedStatement ps2 = con.prepareStatement(sql)) {
                    ps2.setInt(1, nextId);
                    ps2.setString(2, code);
                    ps2.setInt(3, limit);
                    ps2.setString(4, "[]");          // listuser mặc định rỗng
                    ps2.setString(5, listItem);
                    ps2.setInt(6, bagCount);
                    ps2.setString(7, itemoption);
                    ps2.setTimestamp(8, Timestamp.valueOf(expired)); // format: yyyy-MM-dd HH:mm:ss
                    ps2.executeUpdate();
                }

                // Reload memory ngay sau khi tạo
                MaQuaTangManager.gI().init();

                // Tóm tắt items được tạo
                JSONArray itemArr = (JSONArray) parsedList;
                StringBuilder itemSummary = new StringBuilder();
                for (Object o : itemArr) {
                    JSONObject jo = (JSONObject) o;
                    itemSummary.append("id=").append(jo.get("id"))
                            .append(" x").append(jo.get("quantity"));
                    if (jo.containsKey("options")) {
                        itemSummary.append(" opts=").append(jo.get("options"));
                    }
                    itemSummary.append(" | ");
                }

                return "Tạo Code Thành Công: " + code
                        + " | Limit: " + limit
                        + " | Items: " + itemSummary
                        + " | Hết hạn: " + expired;
            }

        } catch (NumberFormatException e) {
            return "FAIL CREATEGIFTCODE: Sai định dạng số - " + e.getMessage();
        } catch (Exception e) {
            Logger.logException(ToolServer.class, e, "handleCreateGiftCode");
            return "ERR " + e.getMessage();
        }
    }

    private String handleSendItemAdv(String payload) {
        try {
            String[] p = payload.split("\\|", -1);
            if (p.length < 3) {
                return "FAIL SENDITEM_ADV use: name|itemIds|quantity  OR  name|itemIds|optIds|optVals|quantity";
            }

            String playerName = p[0].trim();
            String itemIdList = p[1].trim();
            String optIds = "";
            String optVals = "";
            String qtyStr = "";

            if (p.length == 3) {
                qtyStr = p[2].trim();
            } else {
                optIds = p.length > 2 ? p[2].trim() : "";
                optVals = p.length > 3 ? p[3].trim() : "";
                qtyStr = p.length > 4 ? p[4].trim() : "";
            }

            if (playerName.isEmpty()) {
                return "FAIL SENDITEM_ADV missing playerName";
            }
            if (itemIdList.isEmpty()) {
                return "FAIL SENDITEM_ADV missing itemIds";
            }

            int quantity = 1;
            try {
                quantity = Integer.parseInt(qtyStr.isEmpty() ? "1" : qtyStr);
            } catch (NumberFormatException nfe) {
                return "FAIL SENDITEM_ADV invalid quantity: " + qtyStr;
            }

            Player target = Client.gI().getPlayer(playerName);
            if (target == null) {
                return "FAIL player not online";
            }

            String[] ids = itemIdList.split("[,\\-]");
            StringBuilder sb = new StringBuilder();
            boolean anyOk = false;

            for (String idRaw : ids) {
                String idTrim = idRaw.trim();
                if (idTrim.isEmpty()) {
                    continue;
                }
                String singlePayload = playerName + "|" + idTrim + "|" + optIds + "|" + optVals + "|" + quantity;
                String res = handleSendItem(singlePayload);
                sb.append(res).append(" ; ");
                if (res != null && (res.startsWith("OK") || res.startsWith("Gửi") || res.startsWith("SUCCESS"))) {
                    anyOk = true;
                }
            }

            String combined = sb.length() > 0 ? sb.toString().trim() : "FAIL SENDITEM_ADV no valid itemId";
            return anyOk ? "Gửi Vật Phẩm: " + combined : "FAIL SENDITEM_ADV: " + combined;

        } catch (Exception e) {
            Logger.logException(ToolServer.class, e, "handleSendItemAdv");
            return "FAIL SENDITEM_ADV Exception: " + e.getMessage();
        }
    }

    private String handleListGiftCode() {
        try (Connection con = GirlkunDB.getConnection(); PreparedStatement ps = con.prepareStatement(
                "SELECT id, code, `limit`, listuser, listItem, bagCount, itemoption, create_date, expired "
                + "FROM giftcode ORDER BY id DESC LIMIT 200")) {

            ResultSet rs = ps.executeQuery();
            StringBuilder sb = new StringBuilder();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            while (rs.next()) {
                // Đếm số người đã dùng từ listuser JSON
                int usedCount = 0;
                String listUserStr = rs.getString("listuser");
                if (listUserStr != null && !listUserStr.isEmpty()) {
                    JSONArray arr = (JSONArray) JSONValue.parse(listUserStr);
                    if (arr != null) {
                        usedCount = arr.size();
                    }
                }

                // Null-safe cho create_date
                String createStr = "N/A";
                java.sql.Timestamp createTs = rs.getTimestamp("create_date");
                if (createTs != null) {
                    createStr = sdf.format(createTs);
                }

                // Null-safe cho expired
                String expiredStr = "Vĩnh viễn";
                java.sql.Timestamp expiredTs = rs.getTimestamp("expired");
                if (expiredTs != null) {
                    expiredStr = sdf.format(expiredTs);
                }

                sb.append(rs.getInt("id"))
                        .append(":").append(rs.getString("code"))
                        .append(",limit=").append(rs.getInt("limit"))
                        .append(",used=").append(usedCount)
                        .append(",listItem=").append(rs.getString("listItem"))
                        .append(",bagCount=").append(rs.getInt("bagCount"))
                        .append(",itemoption=").append(rs.getString("itemoption"))
                        .append(",create_date=").append(createStr)
                        .append(",expired=").append(expiredStr)
                        .append(";");
            }

            return sb.length() == 0 ? "OK Không có giftcode nào." : "OK " + sb;

        } catch (Exception e) {
            Logger.logException(ToolServer.class, e, "handleListGiftCode");
            return "ERR " + e.getMessage();
        }
    }

    private String handleDeleteGiftCode(String payload) {
        try {
            int id = Integer.parseInt(payload.trim());

            try (Connection con = GirlkunDB.getConnection(); PreparedStatement ps = con.prepareStatement("DELETE FROM giftcode WHERE id = ?")) {
                ps.setInt(1, id);
                int deleted = ps.executeUpdate();
                if (deleted == 0) {
                    return "FAIL: Không tìm thấy giftcode với id = " + id;
                }
                // Reload memory sau khi xóa
                MaQuaTangManager.gI().init();
                return "Xóa thành công giftcode id = " + id;
            }

        } catch (NumberFormatException e) {
            return "FAIL: id phải là số nguyên - " + e.getMessage();
        } catch (Exception e) {
            Logger.logException(ToolServer.class, e, "handleDeleteGiftCode");
            return "ERR " + e.getMessage();
        }
    }

    private String handleReloadGiftCode() {
        try {
            int before = MaQuaTangManager.gI().listGiftCode.size();
            MaQuaTangManager.gI().init();
            int after = MaQuaTangManager.gI().listGiftCode.size();
            return "Reload GiftCode thành công. Tổng: " + after + " code(s) (trước: " + before + ")";
        } catch (Exception e) {
            Logger.logException(ToolServer.class, e, "handleReloadGiftCode");
            return "ERR: " + e.getMessage();
        }
    }
//    private String handleCreateGiftCode(String payload) {
//        try {
//            String[] p = payload.split("\\|", 9);
//            if (p.length < 9) {
//                return "FAIL CREATEGIFTCODE: Thiếu tham số";
//            }
//
//            String code = p[0].trim();
//            int limit = Integer.parseInt(p[1].trim());
//            String listItem = p[5].trim();
//            int bagCount = Integer.parseInt(p[6].trim());
//            String itemoption = p[7].trim(); // Lấy option chung từ payload
//            String expired = p[8].trim();
//
//            try (Connection con = GirlkunDB.getConnection()) {
//                int nextId = 1;
//                try (PreparedStatement psMax = con.prepareStatement("SELECT MAX(id) as maxId FROM giftcode"); ResultSet rs = psMax.executeQuery()) {
//                    if (rs.next()) {
//                        nextId = rs.getInt("maxId") + 1;
//                    }
//                }
//
//                // Cập nhật lại câu lệnh SQL cho đúng cột
//                String sql = "INSERT INTO giftcode(id, code, type, `delete`, `limit`, listuser, listItem, bagCount, itemoption, expired) "
//                        + "VALUES (?, ?, 1, 1, ?, ?, ?, ?, ?, ?)";
//
//                try (PreparedStatement ps = con.prepareStatement(sql)) {
//                    ps.setInt(1, nextId);
//                    ps.setString(2, code);
//                    ps.setInt(3, limit);
//                    ps.setString(4, "[]"); // listuser mặc định
//                    ps.setString(5, listItem);
//                    ps.setInt(6, bagCount);
//                    ps.setString(7, itemoption);
//                    ps.setTimestamp(8, Timestamp.valueOf(expired));
//
//                    ps.executeUpdate();
//                }
//                return "Tạo Code Thành Công " + code;
//            }
//
//        } catch (Exception e) {
//            Logger.logException(ToolServer.class, e, "handleCreateGiftCode");
//            return "ERR " + e.getMessage();
//        }
//    }
//
//    private String handleSendItemAdv(String payload) {
//        try {
//            String[] p = payload.split("\\|", -1);
//            if (p.length < 3) {
//                return "FAIL SENDITEM_ADV use: name|itemIds|quantity  OR  name|itemIds|optIds|optVals|quantity";
//            }
//
//            String playerName = p[0].trim();
//            String itemIdList = p[1].trim();
//            String optIds = "";
//            String optVals = "";
//            String qtyStr = "";
//
//            if (p.length == 3) {
//                qtyStr = p[2].trim();
//            } else {
//                optIds = p.length > 2
//                        ? p[2].trim() : "";
//                optVals = p.length > 3 ? p[3].trim() : "";
//                qtyStr = p.length > 4
//                        ? p[4].trim() : "";
//            }
//
//            if (playerName.isEmpty()) {
//                return "FAIL SENDITEM_ADV missing playerName";
//            }
//            if (itemIdList.isEmpty()) {
//                return "FAIL SENDITEM_ADV missing itemIds";
//            }
//
//            int quantity = 1;
//            try {
//                quantity = Integer.parseInt(qtyStr.isEmpty() ? "1" : qtyStr);
//            } catch (NumberFormatException nfe) {
//                return "FAIL SENDITEM_ADV invalid quantity: " + qtyStr;
//            }
//
//            Player target = Client.gI().getPlayer(playerName);
//            if (target == null) {
//                return "FAIL player not online";
//            }
//
//            String[] ids = itemIdList.split("[,\\-]");
//            StringBuilder sb = new StringBuilder();
//            boolean anyOk = false;
//
//            for (String idRaw : ids) {
//                String idTrim = idRaw.trim();
//                if (idTrim.isEmpty()) {
//                    continue;
//                }
//                String singlePayload = playerName + "|"
//                        + idTrim + "|" + optIds + "|" + optVals + "|" + quantity;
//                String res = handleSendItem(singlePayload);
//                sb.append(res).append(" ; ");
//                if (res != null && (res.startsWith("OK") || res.startsWith("Gửi") || res.startsWith("SUCCESS"))) {
//                    anyOk = true;
//                }
//            }
//
//            String combined = sb.length() > 0
//                    ? sb.toString().trim() : "FAIL SENDITEM_ADV no valid itemId";
//            if (anyOk) {
//                return "Gửi Vật Phẩm: " + combined;
//            } else {
//                return "FAIL SENDITEM_ADV: " + combined;
//            }
//
//        } catch (Exception e) {
//            Logger.logException(ToolServer.class, e, "handleSendItemAdv");
//            return "FAIL SENDITEM_ADV Exception: " + e.getMessage();
//        }
//    }
//
//    private String handleListGiftCode() {
//        try (Connection con = GirlkunDB.getConnection(); // Cập nhật câu lệnh SELECT
//                 PreparedStatement ps = con.prepareStatement(
//                        "SELECT id, code, `limit`, listuser, listItem, bagCount, itemoption, expired "
//                        + "FROM giftcode ORDER BY id DESC LIMIT 200")) {
//
//            ResultSet rs = ps.executeQuery();
//            StringBuilder sb = new StringBuilder();
//
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//            while (rs.next()) {
//                sb.append(rs.getInt("id"))
//                        .append(":")
//                        .append(rs.getString("code"))
//                        .append(",limit=")
//                        .append(rs.getInt("limit"))
//                        .append(",listuser=")
//                        .append(rs.getString("listuser"))
//                        .append(",listItem=") // Giữ key là 'listItem' để admin panel đọc được
//                        .append(rs.getString("listItem"))
//                        .append(",bagCount=")
//                        .append(rs.getInt("bagCount"))
//                        .append(",itemoption=") // Thêm key 'itemoption'
//                        .append(rs.getString("itemoption"))
//                        .append(",expired=")
//                        .append(sdf.format(rs.getTimestamp("expired")))
//                        .append(";");
//            }
//
//            return "OK " + sb;
//        } catch (Exception e) {
//            Logger.logException(ToolServer.class, e, "handleListGiftCode");
//            return "ERR " + e.getMessage();
//        }
//    }
//
//    private String handleDeleteGiftCode(String payload) {
//        try {
//            int id = Integer.parseInt(payload.trim());
//            try (Connection con = GirlkunDB.getConnection(); PreparedStatement ps = con.prepareStatement(
//                    "DELETE FROM giftcode WHERE id=?")) {
//                ps.setInt(1, id);
//                int deleted = ps.executeUpdate();
//                return "Delete Code " + deleted + " code(s)";
//            }
//        } catch (Exception e) {
//            Logger.logException(ToolServer.class, e, "handleDeleteGiftCode");
//            return "ERR " + e.getMessage();
//        }
//    }
//
//    // MỚI: Hàm xử lý lệnh reload giftcode
//    private String handleReloadGiftCode() {
//        try {
//            MaQuaTangManager.gI().init(); // Gọi lại hàm init để tải lại dữ liệu từ DB
//            return "Reload GiftCode.";
//        } catch (Exception e) {
//            Logger.logException(ToolServer.class, e, "handleReloadGiftCode");
//            return "ERR: " + e.getMessage();
//        }
//    }

}
