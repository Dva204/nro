package com.girlkun.services;

import com.girlkun.database.GirlkunDB; // Import thư viện kết nối database của bạn
import com.girlkun.models.item.Item;
import com.girlkun.models.player.Player;
import com.girlkun.utils.Logger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import org.json.simple.JSONArray; // Thư viện JSON để lưu option

public class ThuDoService {

    private static ThuDoService i;

    public static ThuDoService gI() {
        if (i == null) {
            i = new ThuDoService();
        }
        return i;
    }

    /**
     * Thêm một vật phẩm vào rương đồ của người chơi (lưu vào database)
     * @param player Người chơi nhận vật phẩm
     * @param item   Vật phẩm cần thêm
     */
    public void addItemToThuDo(Player player, Item item) {
        if (player == null || item == null) {
            return;
        }

        // --- BẠN CẦN VIẾT LOGIC LƯU VẬT PHẨM VÀO DATABASE TẠI ĐÂY ---
        // Ví dụ: Lưu vào một bảng tên là `ruong_do` có các cột:
        // `player_id`, `item_id`, `quantity`, `item_options`

        try (Connection con = GirlkunDB.getConnection()) {
    // Chuyển danh sách option của vật phẩm thành chuỗi JSON
    JSONArray options = new JSONArray();
    for (Item.ItemOption io : item.itemOptions) {
        // 1. Tạo một mảng JSON con cho mỗi cặp option
        JSONArray optionData = new JSONArray();
        
        // 2. Thêm ID và param vào mảng con
        optionData.add(io.optionTemplate.id);
        optionData.add(io.param);
        
        // 3. Thêm mảng con đó vào mảng chính
        options.add(optionData);
    }
    String jsonOptions = options.toJSONString();

    // Câu lệnh SQL để chèn vật phẩm vào bảng
    String sql = "INSERT INTO ruong_do (player_id, item_id, quantity, item_options) VALUES (?, ?, ?, ?)";
    
    PreparedStatement ps = con.prepareStatement(sql);
    ps.setLong(1, player.id);
    ps.setInt(2, item.template.id);
    ps.setInt(3, item.quantity);
    ps.setString(4, jsonOptions);
    
    ps.executeUpdate();
    
    Logger.log("Đã gửi vật phẩm [" + item.template.name + " x" + item.quantity + "] vào rương đồ của " + player.name);

} catch (Exception e) {
    Logger.logException(ThuDoService.class, e, "Lỗi khi thêm vật phẩm vào rương đồ cho " + player.name);
}
        // -------------------------------------------------------------
    }
}