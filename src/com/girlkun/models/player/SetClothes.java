package com.girlkun.models.player;

import com.girlkun.models.item.Item;

public class SetClothes {

    private Player player;
    private boolean huydietClothers;
    public boolean thanlinhClothers;
    public int setGod;

    public SetClothes(Player player) {
        this.player = player;
    }

    public byte songoku;
    public byte kaioken;
    public byte kirin;
    public byte songoku1;
    public byte kaioken1;
    public byte kirin1;

    public byte ocTieu;
    public byte pikkoroDaimao;
    public byte picolo;
    public byte ocTieu1;
    public byte pikkoroDaimao1;
    public byte picolo1;

    public byte kakarot;
    public byte cadic;
    public byte nappa;
    public byte kakarot1;
    public byte cadic1;
    public byte nappa1;

    public byte worldcup;
    public byte setDHD;
    public byte SetTinhAn;
    public byte SetNguyetAn;
    public byte SetNhatAn;
    public byte SetThienSu;
    public boolean godClothes;
    public int ctHaiTac = -1;

    public void setup() {
        setDefault();
        setupSKT();
        this.godClothes = true;
        for (int i = 0; i < 5; i++) {
            Item item = this.player.inventory.itemsBody.get(i);
            if (item.isNotNullItem()) {
                if (item.template.id > 567 || item.template.id < 555) {
                    this.godClothes = true;
                    break;
                }
            } else {
                this.godClothes = false;
                break;
            }
        }
        Item ct = this.player.inventory.itemsBody.get(5);
        if (ct.isNotNullItem()) {
            switch (ct.template.id) {
                case 618:
                case 619:
                case 620:
                case 621:
                case 622:
                case 623:
                case 624:
                case 626:
                case 627:
                    this.ctHaiTac = ct.template.id;
                    break;

            }
        }

        this.player.setClothes.SetThienSu = 0;
        for (int i = 0; i < 5; i++) {
            Item item = this.player.inventory.itemsBody.get(i);
            if (item.isNotNullItem()) {
                if (item.template.id >= 1048 && item.template.id <= 1062) {

                    player.setClothes.SetThienSu++;
                }
            }
        }

        this.player.setClothes.SetTinhAn = 0;
        for (int i = 0; i < 5; i++) {
            Item item = this.player.inventory.itemsBody.get(i);
            if (item.isNotNullItem()) {
                int chiso = 0;
                Item.ItemOption optionLevel = null;
                for (Item.ItemOption io : item.itemOptions) {
                    if (io.optionTemplate.id == 34) {
                        chiso = io.param;
                        optionLevel = io;
                        break;
                    }
                }
                if (optionLevel != null) {
                    player.setClothes.SetTinhAn++;
                }

            }
        }
        this.player.setClothes.SetNguyetAn = 0;
        for (int i = 0; i < 5; i++) {
            Item item = this.player.inventory.itemsBody.get(i);
            if (item.isNotNullItem()) {
                int chiso = 0;
                Item.ItemOption optionLevel = null;
                for (Item.ItemOption io : item.itemOptions) {
                    if (io.optionTemplate.id == 35) {
                        chiso = io.param;
                        optionLevel = io;
                        break;
                    }
                }
                if (optionLevel != null) {
                    player.setClothes.SetNguyetAn++;
                }
            }
        }
        this.player.setClothes.SetNhatAn = 0;
        for (int i = 0; i < 5; i++) {
            Item item = this.player.inventory.itemsBody.get(i);
            if (item.isNotNullItem()) {
                int chiso = 0;
                Item.ItemOption optionLevel = null;
                for (Item.ItemOption io : item.itemOptions) {
                    if (io.optionTemplate.id == 36) {
                        chiso = io.param;
                        optionLevel = io;
                        break;
                    }
                }
                if (optionLevel != null) {
                    player.setClothes.SetNhatAn++;
                }
            }
        }
    }

    private void setupSKT() {
        for (int i = 0; i < 5; i++) {
            Item item = this.player.inventory.itemsBody.get(i);
            if (item.isNotNullItem()) {
                boolean isActSet = false;
                for (Item.ItemOption io : item.itemOptions) {
                    switch (io.optionTemplate.id) {
                        case 129:
                        case 141:
                            isActSet = true;
                            songoku++;
                            break;
                        case 250:
                        case 262:
                            isActSet = true;
                            songoku1++;
                            break;    
                        case 128:
                        case 139:
                            isActSet = true;
                            kaioken++;
                            break;
                        case 249:
                        case 260:
                            isActSet = true;
                            kaioken1++;
                            break;    
                        case 127:
                        case 140:
                            isActSet = true;
                            kirin++;
                            break;
                        case 248:
                        case 261:
                            isActSet = true;
                            kirin1++;
                            break;
                        case 131:
                        case 143:
                            isActSet = true;
                            ocTieu++;
                            break;
                        case 252:
                        case 264:
                            isActSet = true;
                            ocTieu1++;
                            break;
                        case 132:
                        case 144:
                            isActSet = true;
                            pikkoroDaimao++;
                            break;
                        case 253:
                        case 265:
                            isActSet = true;
                            pikkoroDaimao1++;
                            break;
                        case 130:
                        case 142:
                            isActSet = true;
                            picolo++;
                            break;
                        case 251:
                        case 263:
                            isActSet = true;
                            picolo1++;
                            break;
                        case 135:
                        case 138:
                            isActSet = true;
                            nappa++;
                            break;
                        case 256:
                        case 259:
                            isActSet = true;
                            nappa1++;
                            break;
                        case 133:
                        case 136:
                            isActSet = true;
                            kakarot++;
                            break;
                        case 254:
                        case 257:
                            isActSet = true;
                            kakarot1++;
                            break;
                        case 134:
                        case 137:
                            isActSet = true;
                            cadic++;
                            break;
                        case 255:
                        case 258:
                            isActSet = true;
                            cadic1++;
                            break;
                        case 21:
                            if (io.param == 80) {
                                setDHD++;
                            }
                            break;
                        case 34:
                            isActSet = true;
                            SetTinhAn++;
                            break;
                        case 35:
                            isActSet = true;
                            SetNguyetAn++;
                            break;
                        case 36:
                            isActSet = true;
                            SetNhatAn++;
                            break;
                    }

                    if (isActSet) {
                        break;
                    }
                }
            } else {
                break;
            }
        }
    }

    //checksetthanlinh
    public int setGod() {
        int count = 0;
        for (Item item : this.player.inventory.itemsBody) {
            if (item != null && item.isNotNullItem()
                    && item.template.id >= 555
                    && item.template.id <= 567) {
                count++;
            }
        }
        return count;
    }

    // check set huy diet
    public boolean setGod14() {

        if (this.player == null
                || this.player.inventory == null
                || this.player.inventory.itemsBody == null) {
            return false;
        }

        int count = 0;

        for (Item item : this.player.inventory.itemsBody) {

            if (item != null && item.isNotNullItem()) {

                if (item.template.id >= 650 && item.template.id <= 663) {
                    count++;
                }
            } else {
                this.huydietClothers = false;
                return false;
            }
        }

        this.huydietClothers = (count >= 6);
        return this.huydietClothers;
    }

    private void setDefault() {
        this.songoku1 = 0;
        this.songoku = 0;
        this.kaioken1 = 0;
        this.kaioken = 0;
        this.kirin1 = 0;
        this.kirin = 0;
        this.ocTieu1 = 0;
        this.ocTieu = 0;
        this.pikkoroDaimao1 = 0;
        this.pikkoroDaimao = 0;
        this.picolo1 = 0;
        this.picolo = 0;
        this.kakarot1 = 0;
        this.kakarot = 0;
        this.cadic1 = 0;
        this.cadic = 0;
        this.nappa1 = 0;
        this.nappa = 0;
        this.setDHD = 0;
        this.worldcup = 0;
        this.SetTinhAn = 0;
        this.SetNhatAn = 0;
        this.SetNguyetAn = 0;
        this.godClothes = false;
        this.ctHaiTac = -1;
    }

    public void dispose() {
        this.player = null;
    }

    //public boolean setGod() {
    //   throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
}
//}
