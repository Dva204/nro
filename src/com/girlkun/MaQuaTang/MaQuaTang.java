//package com.girlkun.MaQuaTang;
//
//import com.girlkun.models.item.Item.ItemOption;
//import java.sql.Timestamp;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//
//public class MaQuaTang {
//
//    public String code;
//    public int countLeft;
//    public HashMap<Integer, Integer> listItem = new HashMap<>();
//    public ArrayList<Integer> listIdPlayer = new ArrayList<>();
//    public ArrayList<ItemOption> option = new ArrayList<>();
//    public Timestamp create_date;
//    public Timestamp dateexpired;
//
//    public boolean isUsedGiftCode(int idPlayer) {
//        return listIdPlayer.contains(idPlayer);
//    }
//
//    public void addPlayerUsed(int idPlayer) {
//        listIdPlayer.add(idPlayer);
//    }
//
//    public boolean isExpired() {
//        if (this.dateexpired == null) {
//            return false;
//        }
//        return new Date().getTime() > this.dateexpired.getTime();
//    }
//}

package com.girlkun.MaQuaTang;

import com.girlkun.models.item.Item.ItemOption;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

public class MaQuaTang {

    // Inner class to hold each item entry with its own options
    public static class GiftItem {
        public int itemId;
        public int quantity;
        public ArrayList<ItemOption> options = new ArrayList<>();

        public GiftItem(int itemId, int quantity) {
            this.itemId = itemId;
            this.quantity = quantity;
        }
    }

    public String code;
    public int countLeft;
    public ArrayList<GiftItem> listItem = new ArrayList<>();  // Changed from HashMap to ArrayList<GiftItem>
    public ArrayList<Integer> listIdPlayer = new ArrayList<>();
    public Timestamp create_date;
    public Timestamp dateexpired;

    public boolean isUsedGiftCode(int idPlayer) {
        return listIdPlayer.contains(idPlayer);
    }

    public void addPlayerUsed(int idPlayer) {
        listIdPlayer.add(idPlayer);
    }

    public boolean isExpired() {
        if (this.dateexpired == null) {
            return false;
        }
        return new Date().getTime() > this.dateexpired.getTime();
    }
}