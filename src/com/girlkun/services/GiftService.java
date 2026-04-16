//package com.girlkun.services;
//
//import com.girlkun.MaQuaTang.MaQuaTang;
//import com.girlkun.MaQuaTang.MaQuaTangManager;
//import com.girlkun.models.item.Item;
//import com.girlkun.models.item.Item.ItemOption;
//import com.girlkun.models.player.Player;
//import java.util.ArrayList;
//import java.util.List;
//
//public class GiftService {
//
//    private static GiftService i;
//
//    private GiftService() {
//    }
//
//    public static GiftService gI() {
//        if (i == null) {
//            i = new GiftService();
//        }
//        return i;
//    }
//
//    public void giftCode(Player player, String code) {
//        MaQuaTang giftcode = MaQuaTangManager.gI().findGiftCode(code);
//
//        // --- CÁC BƯỚC KIỂM TRA GIFTCODE BAN ĐẦU (GIỮ NGUYÊN) ---
//        if (giftcode == null) {
//            Service.getInstance().sendThongBao(player, "Giftcode không tồn tại!");
//            return;
//        }
//        if (giftcode.isUsedGiftCode((int) player.id)) {
//            Service.getInstance().sendThongBao(player, "Bạn đã sử dụng code này rồi!");
//            return;
//        }
//        if (giftcode.countLeft <= 0) {
//            Service.getInstance().sendThongBao(player, "Giftcode đã hết lượt sử dụng!");
//            return;
//        }
//        if (giftcode.isExpired()) {
//            Service.getInstance().sendThongBao(player, "Giftcode đã hết hạn sử dụng!");
//            return;
//        }
//
//        // --- LOGIC MỚI ĐÃ ĐƯỢC TỐI ƯU HÓA ---
//
//        // Bước 1: Trừ lượt và lưu thông tin người dùng ngay lập tức
//        giftcode.countLeft -= 1;
//        giftcode.addPlayerUsed((int) player.id);
//        MaQuaTangManager.gI().updateGiftCodeInDB(giftcode);
//
//        // Bước 2: Chuẩn bị các danh sách để xử lý
//        List<Item> itemsToAddToBag = new ArrayList<>(); // Danh sách vật phẩm thường
//        List<Item> overflowItems = new ArrayList<>();   // Danh sách vật phẩm bị đầy túi
//        List<String> rewardNames = new ArrayList<>();   // Danh sách TÊN của TẤT CẢ phần quà
//
//        // Bước 3: Phân loại quà (Tài nguyên hay Vật phẩm thường)
//        for (Integer idItem : giftcode.listItem.keySet()) {
//            int quantity = giftcode.listItem.get(idItem);
//
//            switch (idItem) {
//                case -1: // Vàng
//                    player.inventory.gold = Math.min(player.inventory.gold + quantity, InventoryServiceNew.LIMIT_GOLD);
//                    rewardNames.add(String.format("%,d Vàng", quantity));
//                    break;
//                case -2: // Ngọc
//                    player.inventory.gem = Math.min(player.inventory.gem + quantity, 2_000_000_000);
//                    rewardNames.add(String.format("%,d Ngọc", quantity));
//                    break;
//                case -3: // Ruby
//                    player.inventory.ruby = Math.min(player.inventory.ruby + quantity, 2_000_000_000);
//                    rewardNames.add(String.format("%,d Hồng ngọc", quantity));
//                    break;
//                default:
//                    // Đây là vật phẩm thường, tạo và thêm vào danh sách chờ
//                    Item newItem = ItemService.gI().createNewItem((short) (int) idItem);
//                    if (newItem != null) {
//                        newItem.quantity = quantity;
//                        if (giftcode.option != null && !giftcode.option.isEmpty()) {
//                            for (ItemOption opt : giftcode.option) {
//                                newItem.itemOptions.add(new ItemOption(opt.optionTemplate.id, opt.param));
//                            }
//                        }
//                        itemsToAddToBag.add(newItem);
//                    }
//                    break;
//            }
//        }
//
//        // Bước 4: Xử lý các vật phẩm thường
//        for (Item item : itemsToAddToBag) {
//            if (InventoryServiceNew.gI().addItemBag(player, item)) {
//                // Thêm thành công vào túi đồ
//                rewardNames.add(item.template.name + " x" + item.quantity);
//            } else {
//                // Túi đồ bị đầy, đưa vào danh sách chờ gửi thư
//                overflowItems.add(item);
//            }
//        }
//
//        // Bước 5: Gửi các vật phẩm bị đầy vào hòm đồ/thư
//        if (!overflowItems.isEmpty()) {
//            for (Item item : overflowItems) {
//                // Thay thế bằng hàm gửi đồ vào hòm thư/NPC của bạn
//                ThuDoService.gI().addItemToThuDo(player, item);
//            }
//        }
//        
//        // Bước 6: Gửi thông báo cuối cùng cho người chơi
//        StringBuilder finalMessage = new StringBuilder("|1|Nhận quà thành công!");
//        if (!rewardNames.isEmpty()) {
//            finalMessage.append("\n\n|7|Bạn nhận được:\n");
//            for (String reward : rewardNames) {
//                finalMessage.append("|6|- ").append(reward).append("\n");
//            }
//        }
//        if (!overflowItems.isEmpty()) {
//            finalMessage.append("\n|2|Một số vật phẩm không vừa hành trang đã được gửi vào Rương đồ của bạn!");
//        }
//
//        Service.getInstance().sendThongBao(player, finalMessage.toString());
//        Service.getInstance().sendMoney(player); // Cập nhật lại tiền
//        InventoryServiceNew.gI().sendItemBags(player); // Cập nhật lại hành trang
//    }
//}


package com.girlkun.services;

import com.girlkun.MaQuaTang.MaQuaTang;
import com.girlkun.MaQuaTang.MaQuaTangManager;
import com.girlkun.models.item.Item;
import com.girlkun.models.item.Item.ItemOption;
import com.girlkun.models.player.Player;
import java.util.ArrayList;
import java.util.List;

public class GiftService {

    private static GiftService i;

    private GiftService() {
    }

    public static GiftService gI() {
        if (i == null) {
            i = new GiftService();
        }
        return i;
    }

    public void giftCode(Player player, String code) {
        MaQuaTang giftcode = MaQuaTangManager.gI().findGiftCode(code);

        // --- VALIDATION ---
        if (giftcode == null) {
            Service.getInstance().sendThongBao(player, "Giftcode không tồn tại!");
            return;
        }
        if (giftcode.isUsedGiftCode((int) player.id)) {
            Service.getInstance().sendThongBao(player, "Bạn đã sử dụng code này rồi!");
            return;
        }
        if (giftcode.countLeft <= 0) {
            Service.getInstance().sendThongBao(player, "Giftcode đã hết lượt sử dụng!");
            return;
        }
        if (giftcode.isExpired()) {
            Service.getInstance().sendThongBao(player, "Giftcode đã hết hạn sử dụng!");
            return;
        }

        // Bước 1: Trừ lượt và lưu thông tin người dùng ngay lập tức
        giftcode.countLeft -= 1;
        giftcode.addPlayerUsed((int) player.id);
        MaQuaTangManager.gI().updateGiftCodeInDB(giftcode);

        // Bước 2: Chuẩn bị các danh sách
        List<Item> itemsToAddToBag = new ArrayList<>();
        List<Item> overflowItems = new ArrayList<>();
        List<String> rewardNames = new ArrayList<>();

        // Bước 3: Phân loại quà
        for (MaQuaTang.GiftItem giftItem : giftcode.listItem) {
            int idItem = giftItem.itemId;
            int quantity = giftItem.quantity;

            switch (idItem) {
                case -1: // Vàng
                    player.inventory.gold = Math.min(player.inventory.gold + quantity, InventoryServiceNew.LIMIT_GOLD);
                    rewardNames.add(String.format("%,d Vàng", quantity));
                    break;
                case -2: // Ngọc
                    player.inventory.gem = Math.min(player.inventory.gem + quantity, 2_000_000_000);
                    rewardNames.add(String.format("%,d Ngọc", quantity));
                    break;
                case -3: // Ruby
                    player.inventory.ruby = Math.min(player.inventory.ruby + quantity, 2_000_000_000);
                    rewardNames.add(String.format("%,d Hồng ngọc", quantity));
                    break;
                default:
                    Item newItem = ItemService.gI().createNewItem((short) idItem);
                    if (newItem != null) {
                        newItem.quantity = quantity;
                        // Apply THIS item's own options (each item has its own list now)
                        if (giftItem.options != null && !giftItem.options.isEmpty()) {
                            for (ItemOption opt : giftItem.options) {
                                newItem.itemOptions.add(new ItemOption(opt.optionTemplate.id, opt.param));
                            }
                        }
                        itemsToAddToBag.add(newItem);
                    }
                    break;
            }
        }

        // Bước 4: Xử lý các vật phẩm thường
        for (Item item : itemsToAddToBag) {
            if (InventoryServiceNew.gI().addItemBag(player, item)) {
                rewardNames.add(item.template.name + " x" + item.quantity);
            } else {
                overflowItems.add(item);
            }
        }

        // Bước 5: Gửi các vật phẩm bị đầy vào hòm thư
        if (!overflowItems.isEmpty()) {
            for (Item item : overflowItems) {
                ThuDoService.gI().addItemToThuDo(player, item);
            }
        }

        // Bước 6: Thông báo kết quả
        StringBuilder finalMessage = new StringBuilder("|1|Nhận quà thành công!");
        if (!rewardNames.isEmpty()) {
            finalMessage.append("\n\n|7|Bạn nhận được:\n");
            for (String reward : rewardNames) {
                finalMessage.append("|6|- ").append(reward).append("\n");
            }
        }
        if (!overflowItems.isEmpty()) {
            finalMessage.append("\n|2|Một số vật phẩm không vừa hành trang đã được gửi vào Rương đồ của bạn!");
        }

        Service.getInstance().sendThongBao(player, finalMessage.toString());
        Service.getInstance().sendMoney(player);
        InventoryServiceNew.gI().sendItemBags(player);
    }
}