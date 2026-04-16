//package com.girlkun.MaQuaTang;
//
//import com.girlkun.database.GirlkunDB;
//import com.girlkun.models.item.Item.ItemOption;
//import com.girlkun.models.player.Player;
//import com.girlkun.services.NpcService;
//import com.girlkun.utils.Logger;
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.TimeZone;
//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;
//import org.json.simple.JSONValue;
//import java.text.SimpleDateFormat;
//
//public class MaQuaTangManager {
//
//    public String name;
//    public final ArrayList<MaQuaTang> listGiftCode = new ArrayList<>();
//
//    private static MaQuaTangManager instance;
//
//    public static MaQuaTangManager gI() {
//        if (instance == null) {
//            instance = new MaQuaTangManager();
//        }
//        return instance;
//    }
//
//    public void init() {
//        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
//
//        try (Connection con = GirlkunDB.getConnection()) {
//            listGiftCode.clear();
//
//            PreparedStatement ps = con.prepareStatement("SELECT `code`, `limit`, `create_date`, `expired`, `listItem`, `itemoption`, `listuser` FROM giftcode");
//            ResultSet rs = ps.executeQuery();
//
//            while (rs.next()) {
//                MaQuaTang giftcode = new MaQuaTang();
//                giftcode.code = rs.getString("code");
//                giftcode.countLeft = rs.getInt("limit");
//                giftcode.create_date = rs.getTimestamp("create_date", cal);
//                giftcode.dateexpired = rs.getTimestamp("expired", cal);
//
//                // Đọc danh sách vật phẩm (listItem)
//                String detailStr = rs.getString("listItem");
//                if (detailStr != null && !detailStr.isEmpty()) {
//                    JSONArray detailArr = (JSONArray) JSONValue.parse(detailStr);
//                    if (detailArr != null) {
//                        for (Object o : detailArr) {
//                            JSONObject jsonObj = (JSONObject) o;
//                            int itemId = Integer.parseInt(jsonObj.get("id").toString());
//                            int quantity = Integer.parseInt(jsonObj.get("quantity").toString());
//                            giftcode.listItem.put(itemId, quantity);
//                        }
//                    }
//                }
//
//                // Đọc tùy chọn chung từ cột itemoption
//                String optionStr = rs.getString("itemoption");
//                if (optionStr != null && !optionStr.isEmpty()) {
//                    JSONArray optionArr = (JSONArray) JSONValue.parse(optionStr);
//                    if (optionArr != null) {
//                        for (Object o : optionArr) {
//                            JSONObject jsonOpt = (JSONObject) o;
//                            giftcode.option.add(new ItemOption(
//                                    Integer.parseInt(jsonOpt.get("id").toString()),
//                                    Integer.parseInt(jsonOpt.get("param").toString())
//                            ));
//                        }
//                    }
//                }
//
//                String usedUsersStr = rs.getString("listuser");
//                if (usedUsersStr != null && !usedUsersStr.isEmpty()) {
//                    JSONArray usedUsersArr = (JSONArray) JSONValue.parse(usedUsersStr);
//                    if (usedUsersArr != null) {
//                        for (Object o : usedUsersArr) {
//                            giftcode.listIdPlayer.add(Integer.parseInt(o.toString()));
//                        }
//                    }
//                }
//                listGiftCode.add(giftcode);
//            }
//            rs.close();
//            ps.close();
//        } catch (Exception e) {
//            Logger.logException(MaQuaTangManager.class, e, "Lỗi khi khởi tạo MaQuaTangManager");
//        }
//    }
//
//    public MaQuaTang findGiftCode(String code) {
//        for (MaQuaTang giftCode : listGiftCode) {
//            if (giftCode.code.equals(code)) {
//                return giftCode;
//            }
//        }
//        return null;
//    }
//
//    public void updateGiftCodeInDB(MaQuaTang giftcode) {
//        JSONArray jsonArray = new JSONArray();
//        for (Integer id : giftcode.listIdPlayer) {
//            jsonArray.add(id);
//        }
//        String listUserJson = jsonArray.toJSONString();
//
//        try (Connection con = GirlkunDB.getConnection()) {
//            String sql = "UPDATE giftcode SET `limit` = ?, `listuser` = ? WHERE `code` = ?";
//            PreparedStatement ps = con.prepareStatement(sql);
//            ps.setInt(1, giftcode.countLeft);
//            ps.setString(2, listUserJson);
//            ps.setString(3, giftcode.code);
//            ps.executeUpdate();
//        } catch (Exception e) {
//            Logger.logException(MaQuaTangManager.class, e, "Lỗi khi cập nhật giftcode vào DB");
//        }
//    }
//
//    public void checkInfomationGiftCode(Player p) {
//        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
//        StringBuilder sb = new StringBuilder();
//
//        for (MaQuaTang giftCode : listGiftCode) {
//            String ngayTao = giftCode.create_date != null ? sdf.format(giftCode.create_date) : "N/A";
//            String ngayHetHan = giftCode.dateexpired != null ? sdf.format(giftCode.dateexpired) : "Vĩnh viễn";
//
//            sb.append("Code: ").append(giftCode.code)
//                    .append(", SL: ").append(giftCode.countLeft)
//                    .append(", Tạo: ").append(ngayTao)
//                    .append(", Hết hạn: ").append(ngayHetHan).append("\n");
//        }
//        NpcService.gI().createTutorial(p, 30274, sb.toString());
//    }
//}
package com.girlkun.MaQuaTang;

import com.girlkun.database.GirlkunDB;
import com.girlkun.models.item.Item.ItemOption;
import com.girlkun.models.player.Player;
import com.girlkun.services.NpcService;
import com.girlkun.utils.Logger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import java.text.SimpleDateFormat;

public class MaQuaTangManager {

    public String name;
    public final ArrayList<MaQuaTang> listGiftCode = new ArrayList<>();

    private static MaQuaTangManager instance;

    public static MaQuaTangManager gI() {
        if (instance == null) {
            instance = new MaQuaTangManager();
        }
        return instance;
    }

    public void init() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));

        try (Connection con = GirlkunDB.getConnection()) {
            listGiftCode.clear();

            PreparedStatement ps = con.prepareStatement("SELECT `code`, `limit`, `create_date`, `expired`, `listItem`, `itemoption`, `listUser` FROM giftcode WHERE `Delete` = 0");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                MaQuaTang giftcode = new MaQuaTang();
                giftcode.code = rs.getString("code");
                giftcode.countLeft = rs.getInt("limit");
                giftcode.create_date = rs.getTimestamp("create_date", cal);
                giftcode.dateexpired = rs.getTimestamp("expired", cal);

                // Read global itemoption (fallback if item has no own options)
                ArrayList<ItemOption> globalOptions = new ArrayList<>();
                String optionStr = rs.getString("itemoption");
                if (optionStr != null && !optionStr.isEmpty()) {
                    JSONArray optionArr = (JSONArray) JSONValue.parse(optionStr);
                    if (optionArr != null) {
                        for (Object o : optionArr) {
                            JSONObject jsonOpt = (JSONObject) o;
                            globalOptions.add(new ItemOption(
                                    Integer.parseInt(jsonOpt.get("id").toString()),
                                    Integer.parseInt(jsonOpt.get("param").toString())
                            ));
                        }
                    }
                }

                // Read listItem — supports both old format and new per-item options format
                String detailStr = rs.getString("listItem");
                if (detailStr != null && !detailStr.isEmpty()) {
                    JSONArray detailArr = (JSONArray) JSONValue.parse(detailStr);
                    if (detailArr != null) {
                        for (Object o : detailArr) {
                            JSONObject jsonObj = (JSONObject) o;
                            int itemId = Integer.parseInt(jsonObj.get("id").toString());
                            int quantity = Integer.parseInt(jsonObj.get("quantity").toString());

                            MaQuaTang.GiftItem giftItem = new MaQuaTang.GiftItem(itemId, quantity);

                            // Check if this item has its own "options" array in JSON
                            if (jsonObj.containsKey("options")) {
                                JSONArray itemOptionArr = (JSONArray) jsonObj.get("options");
                                if (itemOptionArr != null && !itemOptionArr.isEmpty()) {
                                    for (Object oo : itemOptionArr) {
                                        JSONObject jsonOpt = (JSONObject) oo;
                                        giftItem.options.add(new ItemOption(
                                                Integer.parseInt(jsonOpt.get("id").toString()),
                                                Integer.parseInt(jsonOpt.get("param").toString())
                                        ));
                                    }
                                }
                                // If "options" key exists but is empty → item has NO option (intentional)
                            } else {
                                // Old format: no per-item options → fall back to global itemoption
                                giftItem.options.addAll(globalOptions);
                            }

                            giftcode.listItem.add(giftItem);
                        }
                    }
                }

                // Read used players list
                String usedUsersStr = rs.getString("listuser");
                if (usedUsersStr != null && !usedUsersStr.isEmpty()) {
                    JSONArray usedUsersArr = (JSONArray) JSONValue.parse(usedUsersStr);
                    if (usedUsersArr != null) {
                        for (Object o : usedUsersArr) {
                            giftcode.listIdPlayer.add(Integer.parseInt(o.toString()));
                        }
                    }
                }

                listGiftCode.add(giftcode);
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            Logger.logException(MaQuaTangManager.class, e, "Lỗi khi khởi tạo MaQuaTangManager");
        }
    }

    public MaQuaTang findGiftCode(String code) {
        for (MaQuaTang giftCode : listGiftCode) {
            if (giftCode.code.equals(code)) {
                return giftCode;
            }
        }
        return null;
    }

    public void updateGiftCodeInDB(MaQuaTang giftcode) {
        JSONArray jsonArray = new JSONArray();
        for (Integer id : giftcode.listIdPlayer) {
            jsonArray.add(id);
        }
        String listUserJson = jsonArray.toJSONString();

        try (Connection con = GirlkunDB.getConnection()) {
            String sql = "UPDATE giftcode SET `limit` = ?, `listuser` = ? WHERE `code` = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, giftcode.countLeft);
            ps.setString(2, listUserJson);
            ps.setString(3, giftcode.code);
            ps.executeUpdate();
        } catch (Exception e) {
            Logger.logException(MaQuaTangManager.class, e, "Lỗi khi cập nhật giftcode vào DB");
        }
    }

    public void checkInfomationGiftCode(Player p) {
        this.init();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        StringBuilder sb = new StringBuilder();

        for (MaQuaTang giftCode : listGiftCode) {
            if (giftCode.countLeft <= 0 || giftCode.isExpired()) {
                continue; // lọc hết hạn
            }
            String ngayTao = giftCode.create_date != null ? sdf.format(giftCode.create_date) : "N/A";
            String ngayHetHan = giftCode.dateexpired != null ? sdf.format(giftCode.dateexpired) : "Vĩnh viễn";

            sb.append("Code: ").append(giftCode.code)
                    .append(", SL: ").append(giftCode.countLeft)
                    .append(", Tạo: ").append(ngayTao)
                    .append(", Hết hạn: ").append(ngayHetHan).append("\n");
        }

        // Fix lỗi sb rỗng
        if (sb.length() == 0) {
            sb.append("Hiện không có Giftcode nào còn hiệu lực.");
        }

        NpcService.gI().createTutorial(p, 30274, sb.toString());
    }
}
