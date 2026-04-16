package com.girlkun.models.player;

import com.girlkun.card.Card;
import com.girlkun.card.OptionCard;
import com.girlkun.consts.ConstNpc;
import com.girlkun.consts.ConstPlayer;
import com.girlkun.consts.ConstRatio;
import com.girlkun.consts.ConstTask;
import com.girlkun.models.intrinsic.Intrinsic;
import com.girlkun.models.item.Item;
import com.girlkun.models.npc.Npc;
import com.girlkun.models.player.Pet;
import com.girlkun.models.player.Player;
import com.girlkun.models.player.RewardBlackBall;
import com.girlkun.models.skill.Skill;
import com.girlkun.server.Manager;
import com.girlkun.services.EffectSkillService;
import com.girlkun.services.InventoryServiceNew;
import com.girlkun.services.ItemService;
import com.girlkun.services.MapService;
import com.girlkun.services.PlayerService;
import com.girlkun.services.Service;
import com.girlkun.services.TaskService;
import com.girlkun.utils.Logger;
import com.girlkun.utils.SkillUtil;
import com.girlkun.utils.Util;
import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.List;

public class NPoint {

    public static final byte MAX_LIMIT = 9;

    private Player player;

    public NPoint(Player player) {
        this.player = player;
        this.tlHp = new ArrayList<>();
        this.tlMp = new ArrayList<>();
        this.tlDef = new ArrayList<>();
        this.tlDame = new ArrayList<>();
        this.tlDameAttMob = new ArrayList<>();
        this.tlSDDep = new ArrayList<>();
        this.tlTNSM = new ArrayList<>();
        this.tlDameCrit = new ArrayList<>();
    }

    public boolean isCrit;
    public boolean isCrit100;

    private Intrinsic intrinsic;
    private int percentDameIntrinsic;
    public int dameAfter;
    public boolean isFrost;
    /*-----------------------Chỉ số cơ bản------------------------------------*/
    public byte numAttack;
    public short stamina, maxStamina;
    public boolean isDrabura;
    public byte limitPower;
    public long power;
    public long tiemNang;
    public long chuyensinh;
    public boolean isYacon;
    public long hp, hpMax, hpg;
    public long mp, mpMax, mpg;
    public long dame, dameg;
    public int def, defg;
    public int crit, critg;
    public byte speed = 8;
    public boolean isThieuDot;
    public boolean teleport;
    public boolean isSexy;
    public boolean CoCaiTrangHoaDa;
    public boolean IsBiHoaDa;
    public boolean isMabu;
    public boolean khangTDHS;
    public boolean khangTM;
    public boolean Iskimco50;
    public boolean Iskimco80;
    public boolean isXinBaTo;
    public short tlHpGiamDiaNguc;
    public boolean isSoiDaiCa;
    public boolean isSieuThan;
    public boolean isBuiBui;
    public boolean isThoDaiCa;
    /**
     * Chỉ số cộng thêm
     */
    public int hpAdd, mpAdd, dameAdd, defAdd, critAdd, hpHoiAdd, mpHoiAdd;

    /**
     * //+#% sức đánh chí mạng
     */
    public List<Integer> tlDameCrit;

    /**
     * Tỉ lệ hp, mp cộng thêm
     */
    public List<Integer> tlHp, tlMp;

    /**
     * Tỉ lệ giáp cộng thêm
     */
    public List<Integer> tlDef;

    /**
     * Tỉ lệ sức đánh/ sức đánh khi đánh quái
     */
    public List<Integer> tlDame, tlDameAttMob;

    /**
     * Lượng hp, mp hồi mỗi 30s, mp hồi cho người khác
     */
    public int hpHoi, mpHoi, mpHoiCute;

    /**
     * Tỉ lệ hp, mp hồi cộng thêm
     */
    public short tlHpHoi, tlMpHoi;

    /**
     * Tỉ lệ hp, mp hồi bản thân và đồng đội cộng thêm
     */
    public short tlHpHoiBanThanVaDongDoi, tlMpHoiBanThanVaDongDoi;

    /**
     * Tỉ lệ hút hp, mp khi đánh, hp khi đánh quái
     */
    public short tlHutHp, tlHutMp, tlHutHpMob;

    /**
     * Tỉ lệ hút hp, mp xung quanh mỗi 5s
     */
    public short tlHutHpMpXQ;

    /**
     * Tỉ lệ phản sát thương
     */
    public short tlPST;

    /**
     * Tỉ lệ tiềm năng sức mạnh
     */
    public List<Integer> tlTNSM;

    /**
     * Tỉ lệ vàng cộng thêm
     */
    public short tlGold;

    /**
     * Tỉ lệ né đòn
     */
    public short tlNeDon, tlchinhxac;

    /**
     * Tỉ lệ sức đánh đẹp cộng thêm cho bản thân và người xung quanh
     */
    public List<Integer> tlSDDep;
    public short tlDameSexy = 25;
    public short tlDameHorny = 25;

    /**
     * Tỉ lệ giảm sức đánh
     */
    public short tlSubSD;

    public int voHieuChuong;

    /*------------------------Effect skin-------------------------------------*/
    public Item trainArmor;
    public boolean wornTrainArmor;
    public boolean wearingTrainArmor;

    public boolean wearingVoHinh;
    public boolean isKhongLanh;

    public short tlHpGiamODo;
    public short test;

    public short multicationChuong;
    public long lastTimeMultiChuong;

    /*-------------------------------------------------------------------------*/
    /**
     * Tính toán mọi chỉ số sau khi có thay đổi
     */
    public void calPoint() {
        if (this.player.pet != null) {
            this.player.pet.nPoint.setPointWhenWearClothes();
        }
        this.setPointWhenWearClothes();
    }

    private void setPointWhenWearClothes() {
        resetPoint();
        if (this.player.rewardBlackBall.timeOutOfDateReward[0] > System.currentTimeMillis()) {
            dame += RewardBlackBall.R1S_1;
        }
        if (this.player.rewardBlackBall.timeOutOfDateReward[1] > System.currentTimeMillis()) {
            hpMax += RewardBlackBall.R2S_1;
        }
        if (this.player.rewardBlackBall.timeOutOfDateReward[2] > System.currentTimeMillis()) {
            tlHutHp += (RewardBlackBall.R3S_1);
        }
        if (this.player.rewardBlackBall.timeOutOfDateReward[3] > System.currentTimeMillis()) {
            tlPST += RewardBlackBall.R4S_1;
        }
        if (this.player.rewardBlackBall.timeOutOfDateReward[4] > System.currentTimeMillis()) {
            tlDameCrit.add(RewardBlackBall.R5S_1);
            //tlNeDon += RewardBlackBall.R6S_2;
        }
        if (this.player.rewardBlackBall.timeOutOfDateReward[5] > System.currentTimeMillis()) {
            mpMax += RewardBlackBall.R6S_1;
            //tlHutHp += RewardBlackBall.R7S_2;
        }
        if (this.player.rewardBlackBall.timeOutOfDateReward[6] > System.currentTimeMillis()) {
            tlNeDon += RewardBlackBall.R7S_1;
        }

        this.player.setClothes.worldcup = 0;
        for (Item item : this.player.inventory.itemsBody) {
            if (item.isNotNullItem()) {
                switch (item.template.id) {
                    case 966:
                    case 982:
                    case 983:
                    case 883:
                    case 904:
                        player.setClothes.worldcup++;
                }
                if (item.template.id >= 592 && item.template.id <= 594) {
                    teleport = true;
                }
                for (Item.ItemOption io : item.itemOptions) {
                    switch (io.optionTemplate.id) {
                        case 0: //Tấn công +#
                            this.dameAdd += io.param;
                            break;
                        case 2: //HP, KI+#000
                            this.hpAdd += io.param * 1000;
                            this.mpAdd += io.param * 1000;
                            break;
                        case 3:// fake
                            this.voHieuChuong += io.param;
                            break;
                        case 5: //+#% sức đánh chí mạng
                            this.tlDameCrit.add(io.param);
                            break;
                        case 78: //+#% sức đánh hủy diệt
                            this.tlDameCrit.add(io.param);
                            break;
                        case 6: //HP+#
                            this.hpAdd += io.param;
                            break;
                        case 7: //KI+#
                            this.mpAdd += io.param;
                            break;
                        case 8: //Hút #% HP, KI xung quanh mỗi 5 giây
                            this.tlHutHpMpXQ += io.param;
                            break;
                        case 14: //Chí mạng+#%
                            this.critAdd += io.param;
                            break;
                        case 19: //Tấn công+#% khi đánh quái
                            this.tlDameAttMob.add(io.param);
                            break;
                        case 22: //HP+#K
                            this.hpAdd += io.param * 1000;
                            break;
                        case 23: //MP+#K
                            this.mpAdd += io.param * 1000;
                            break;
                        case 24:
                            this.isBuiBui = true;
                            break;
                        case 25:
                            this.isYacon = true;
                            break;
                        case 26: // +# HP/30s
                            this.isDrabura = true;
                            break;
                        case 29: // +# KI/30s
                            this.isMabu = true;
                            break;
                        case 43: //HP+#K
                            this.hpAdd += io.param * 100;
                            break;
                        case 44: //MP+#K
                            this.mpAdd += io.param * 100;
                            break;

                        case 213:// hóa đá
                            this.Iskimco50 = true;
                            break;
                        case 214:// hóa đá
                            this.Iskimco80 = true;
                            break;
                        case 27: //+# HP/30s
                            this.hpHoiAdd += io.param;
                            break;
                        case 28: //+# KI/30s
                            this.mpHoiAdd += io.param;
                            break;
                        case 33: //dịch chuyển tức thời
                            this.teleport = true;
                            break;
                        case 41:
                            this.tlDame.add(io.param);
                            this.tlHp.add(io.param);
                            this.tlMp.add(io.param);
                            break;
                        case 47: //Giáp+#
                            this.defAdd += io.param;
                            break;
                        case 48: //HP/KI+#
                            this.hpAdd += io.param;
                            this.mpAdd += io.param;
                            break;
                        case 49: //Tấn công+#%
                        case 50: // Sức đánh +#%

                            this.tlDame.add(io.param);
                            break;
                        case 77: //HP+#%
                            this.tlHp.add(io.param);
                            break;
                        case 80: //HP+#%/30s
                            this.tlHpHoi += io.param;
                            break;
                        case 81: //MP+#%/30s
                            this.tlMpHoi += io.param;
                            break;
                        case 88: //Cộng #% exp khi đánh quái
                            this.tlTNSM.add(io.param);
                            break;
                        case 94: //Giáp #%
                            this.tlDef.add(io.param);
                            break;
                        case 95: //Biến #% tấn công thành HP
                            this.tlHutHp += io.param;
                            break;
                        case 96: //Biến #% tấn công thành MP
                            this.tlHutMp += io.param;
                            break;
                        case 97: //Phản #% sát thương
                            this.tlPST += io.param;
                            break;
                        case 100: //+#% vàng từ quái
                            this.tlGold += io.param;
                            break;
                        case 101: //+#% TN,SM
                            this.tlTNSM.add(io.param);
                            break;
                        case 103: //KI +#%
                            this.tlMp.add(io.param);
                            break;
                        case 104: //Biến #% tấn công quái thành HP
                            this.tlHutHpMob += io.param;
                            break;
                        case 105: //Vô hình khi không đánh quái và boss
                            this.wearingVoHinh = true;
                            break;
                        case 106: //Không ảnh hưởng bởi cái lạnh
                            this.isKhongLanh = true;
                            break;
                        case 108: //#% Né đòn
                            this.tlNeDon += io.param;// đối nghịch
                            break;
                        case 109: //Hôi, giảm #% HP
                            this.tlHpGiamODo += io.param;
                            break;
                        case 111:
                            this.isXinBaTo = true;
                            break;
                        case 116: //Kháng thái dương hạ san
                            this.khangTDHS = true;
                            break;
                        case 118: //Đẹp +#% SĐ cho mình và người xung quanh
                            this.tlSDDep.add(io.param);
                            break;
                        case 117:
                            this.isSexy = true;

                            this.tlSDDep.add(io.param);

                            //this.tlDameHorny += io.param;
                            break;
                        case 147: //+#% sức đánh
                            this.tlDame.add(io.param);
                            break;
                        case 160: // Sư phụ mặc, đệ tử được +% exp khi đánh quái
                            if (!this.player.isPet) { // Chỉ áp dụng khi là sư phụ
                                Pet pet = this.player.pet;
                                if (pet != null && pet.nPoint != null) {
                                    pet.nPoint.tlTNSM.add(io.param);
                                }
                            }
                            break;
                        case 75: //Giảm 50% sức đánh, HP, KI và +#% SM, TN, vàng từ quái
                            this.tlSubSD -= 50;
                            this.tlTNSM.add(io.param);
                            this.tlGold += io.param;
                            break;
                        case 159: // x# chưởng mỗi phút
                            this.multicationChuong += io.param;
                            break;
                        case 162: //Cute hồi #% KI/s bản thân và xung quanh
                            this.mpHoiCute += io.param;
                            break;
                        case 167: // Cute hồi #% KI/s bản thân và xung quanh
                            this.isFrost = true;
                            break;
                        case 173: //Phục hồi #% HP và KI cho đồng đội
                            this.tlHpHoiBanThanVaDongDoi += io.param;
                            this.tlMpHoiBanThanVaDongDoi += io.param;
                            break;
                        case 211: //test
                            this.test += io.param;
                            break;
                        case 215: // test
                            this.isSieuThan = true;
                            break;
                        case 198: //SD,HP,KI
                            this.tlDame.add(io.param);
                            this.tlHp.add(io.param);
                            this.tlMp.add(io.param);
                            break;
                        case 199: //SD,HP,KI
                            this.tlDame.add(io.param);
                            this.tlHp.add(io.param);
                            this.tlMp.add(io.param);
                            break;
                        case 200: //SD,HP,KI
                            this.tlDame.add(io.param);
                            this.tlHp.add(io.param);
                            this.tlMp.add(io.param);
                            break;
                        case 201: //SD,HP,KI
                            this.tlDame.add(io.param);
                            this.tlHp.add(io.param);
                            this.tlMp.add(io.param);
                            break;
                        case 205: //SD,HP,KI
                            this.tlDame.add(io.param);
                            this.tlHp.add(io.param);
                            this.tlMp.add(io.param);
                            break;
                        case 218: //SD,HP,KI
                            this.tlDame.add(io.param);
                            this.tlHp.add(io.param);
                            this.tlMp.add(io.param);
                            break;
                        case 219: //HP PS
                            this.tlHp.add(io.param);
                            break;
                        case 220: //KI PS
                            this.tlMp.add(io.param);
                            break;
                        case 221: //SD PS
                            this.tlDame.add(io.param);
                            break;
                        case 222: //SDCM PS
                            this.tlDameCrit.add(io.param);
                            break;
                        case 224: // Tỉ lệ chí mạng
                            this.critAdd += io.param;
                            break;
                        case 225:// Tỉ lệ giáp PS
                            this.tlDef.add(io.param);
                            break;
                        case 226: // Tỉ lệ né đòn PS
                            this.tlNeDon += io.param;
                            break;
                        case 239:
                            this.isSoiDaiCa = true;
                            break;

                    }
                }
            }
        }

        // [CỘNG CHỈ SỐ TỪ RADAR CARD]
        try {
            if (this.player.Cards != null && !this.player.Cards.isEmpty()) {
                for (com.girlkun.card.Card card : this.player.Cards) {
                    // Chỉ cộng chỉ số nếu thẻ đang được "sử dụng" (Used == 1)
                    if (card.Used == 1 && card.Options != null) {
                        for (com.girlkun.card.OptionCard option : card.Options) {
                            // Chúng ta lặp qua các option của thẻ và cộng chỉ số tương tự như trang bị
                            // Lưu ý: ở đây là option.id, khác với io.optionTemplate.id của trang bị
                            switch (option.id) {
                                case 0: //Tấn công +#
                                    this.dameAdd += option.param;
                                    break;
                                case 2: //HP, KI+#000
                                    this.hpAdd += option.param * 1000;
                                    this.mpAdd += option.param * 1000;
                                    break;
                                case 5: //+#% sức đánh chí mạng
                                    this.tlDameCrit.add(option.param);
                                    break;
                                case 6: //HP+#
                                    this.hpAdd += option.param;
                                    break;
                                case 7: //KI+#
                                    this.mpAdd += option.param;
                                    break;
                                case 14: //Chí mạng+#%
                                    this.critAdd += option.param;
                                    break;
                                case 19: //Tấn công+#% khi đánh quái
                                    this.tlDameAttMob.add(option.param);
                                    break;
                                case 22: //HP+#K
                                    this.hpAdd += option.param * 1000;
                                    break;
                                case 23: //MP+#K
                                    this.mpAdd += option.param * 1000;
                                    break;
                                case 47: //Giáp+#
                                    this.defAdd += option.param;
                                    break;
                                case 48: //HP/KI+#
                                    this.hpAdd += option.param;
                                    this.mpAdd += option.param;
                                    break;
                                case 49: //Tấn công+#%
                                case 50: // Sức đánh +#%
                                    this.tlDame.add(option.param);
                                    break;
                                case 77: //HP+#%
                                    this.tlHp.add(option.param);
                                    break;
                                case 80: //HP+#%/30s
                                    this.tlHpHoi += option.param;
                                    break;
                                case 81: //MP+#%/30s
                                    this.tlMpHoi += option.param;
                                    break;
                                case 94: //Giáp #%
                                    this.tlDef.add(option.param);
                                    break;
                                case 95: //Biến #% tấn công thành HP
                                    this.tlHutHp += option.param;
                                    break;
                                case 96: //Biến #% tấn công thành MP
                                    this.tlHutMp += option.param;
                                    break;
                                case 97: //Phản #% sát thương
                                    this.tlPST += option.param;
                                    break;
                                case 101: //+#% TN,SM
                                    this.tlTNSM.add(option.param);
                                    break;
                                case 103: //KI +#%
                                    this.tlMp.add(option.param);
                                    break;
                                case 104: //Biến #% tấn công quái thành HP
                                    this.tlHutHpMob += option.param;
                                    break;
                                case 108: //#% Né đòn
                                    this.tlNeDon += option.param;
                                    break;
                                // ---- Các chỉ số Phù (nếu thẻ có) ----
                                case 219: //HP PS
                                    this.tlHp.add(option.param);
                                    break;
                                case 220: //KI PS
                                    this.tlMp.add(option.param);
                                    break;
                                case 221: //SD PS
                                    this.tlDame.add(option.param);
                                    break;
                                case 222: //SDCM PS
                                    this.tlDameCrit.add(option.param);
                                    break;
                                case 224: // Tỉ lệ chí mạng
                                    this.critAdd += option.param;
                                    break;
                                case 225:// Tỉ lệ giáp PS
                                    this.tlDef.add(option.param);
                                    break;
                                case 226: // Tỉ lệ né đòn PS
                                    this.tlNeDon += option.param;
                                    break;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            Logger.error("Lỗi cộng chỉ số Radar Card cho: " + this.player.name + "\n");
        }
        // [KẾT THÚC CỘNG CHỈ SỐ RADAR CARD]

        boolean has1876 = false;
        boolean has1983 = false;

        for (Item it : this.player.inventory.itemsBody) {
            if (it != null && it.template != null) {
                if (it.template.id == 1876) {
                    has1876 = true;
                }
                if (it.template.id == 1983) {
                    has1983 = true;
                }
            }
        }

        if (has1876 && has1983) {
            this.tlDame.add(15); // +15% dame
        }

        boolean has1951 = false;
        boolean has1874 = false;

        for (Item it : this.player.inventory.itemsBody) {
            if (it != null && it.template != null) {
                if (it.template.id == 1874) {
                    has1874 = true;
                }
                if (it.template.id == 1951) {
                    has1951 = true;
                }
            }
        }

        if (has1874 && has1951) {
            this.tlHp.add(15); // +15% hp
        }

        boolean has1885 = false;
        boolean has1892 = false;

        for (Item it : this.player.inventory.itemsBody) {
            if (it != null && it.template != null) {
                if (it.template.id == 1885) {
                    has1885 = true;
                }
                if (it.template.id == 1892) {
                    has1892 = true;
                }
            }
        }

        if (has1885 && has1892) {
            this.tlMp.add(15); // +15% ki
        }
        boolean has1931 = false;
        boolean has1884 = false;

        for (Item it : this.player.inventory.itemsBody) {
            if (it != null && it.template != null) {
                if (it.template.id == 1884) {
                    has1884 = true;
                }
                if (it.template.id == 1931) {
                    has1931 = true;
                }
            }
        }

        if (has1931 && has1884) {
            this.tlMp.add(15); // +15% ki
        }
        boolean has1593 = false;
        boolean has1959 = false;

        for (Item it : this.player.inventory.itemsBody) {
            if (it != null && it.template != null) {
                if (it.template.id == 1593) {
                    has1593 = true;
                }
                if (it.template.id == 1959) {
                    has1959 = true;
                }
            }
        }

        if (has1959 && has1593) {
            this.tlHp.add(15); // +15% hp
        }

        boolean has2067 = false;
        boolean has1763 = false;
        boolean has1895 = false;
        boolean has1762 = false;

        for (Item it : this.player.inventory.itemsBody) {
            if (it != null && it.template != null) {
                switch (it.template.id) {
                    case 2067:
                        has2067 = true;
                        break;
                    case 1763:
                        has1763 = true;
                        break;
                    case 1895:
                        has1895 = true;
                        break;
                    case 1762:
                        has1762 = true;
                        break;
                }
            }
        }

// 2067 + 1763 = +15% sức đánh
        if (has2067 && has1763) {
            this.tlDame.add(12); // +15% dame
        }

// 2067 + 1895 và đang hợp thể = +15% sát thương chí mạng
        if (has2067 && has1895 && this.player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
            this.tlDameCrit.add(15); // +15% dame chí mạng
        }

// 1895 + 1762 = +15% sức đánh
        if (has1895 && has1762) {
            this.tlDame.add(12); // +15% dame
        }

        boolean has1877 = false;
        boolean has1960 = false;

        for (Item it : this.player.inventory.itemsBody) {
            if (it != null && it.template != null) {
                if (it.template.id == 1877) {
                    has1877 = true;
                }
                if (it.template.id == 1960) {
                    has1960 = true;
                }
            }
        }

        if (has1877 && has1960) {
            this.tlDame.add(15); // +15% dame
        }

        boolean has2065 = false;
        boolean has1265 = false;

        for (Item it : this.player.inventory.itemsBody) {
            if (it != null && it.template != null) {
                if (it.template.id == 1265) {
                    has1265 = true;
                }
                if (it.template.id == 2065) {
                    has2065 = true;
                }
            }
        }

        if (has1265 && has2065) {
            this.tlDameCrit.add(50); // +15% dame
        }

        if (this.player.isPl() && this.player.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
            for (Item item : this.player.inventory.itemsBag) {
                if (item.isNotNullItem() && item.template.id == 921) {
                    for (Item.ItemOption io : item.itemOptions) {
                        switch (io.optionTemplate.id) {
                            case 14: //Chí mạng+#%
                                this.critAdd += io.param;
                                break;
                            case 50: //Sức đánh+#%
                                this.tlDame.add(io.param);
                                break;
                            case 41:
                                this.tlDame.add(io.param);
                                this.tlHp.add(io.param);
                                this.tlMp.add(io.param);
                                
                                break;
                            case 77: //HP+#%
                                this.tlHp.add(io.param);
                                break;
                            case 80: //HP+#%/30s
                                this.tlHpHoi += io.param;
                                break;
                            case 81: //MP+#%/30s
                                this.tlMpHoi += io.param;
                                break;
                            case 94: //Giáp #%
                                this.tlDef.add(io.param);
                                break;
                            case 103: //KI +#%
                                this.tlMp.add(io.param);
                                break;
                            case 108: //#% Né đòn
                                this.tlNeDon += io.param;

                        }
                    }
                    break;
                }
            }
        } else if (this.player.isPl() && this.player.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
            for (Item item : this.player.inventory.itemsBag) {
                if (item.isNotNullItem() && item.template.id == 1155) {
                    for (Item.ItemOption io : item.itemOptions) {
                        switch (io.optionTemplate.id) {
                            case 14: //Chí mạng+#%
                                this.critAdd += io.param;
                                break;
                            case 41:
                                this.tlDame.add(io.param);
                                this.tlHp.add(io.param);
                                this.tlMp.add(io.param);
                               
                                break;
                            case 50: //Sức đánh+#%
                                this.tlDame.add(io.param);
                                break;
                            case 77: //HP+#%
                                this.tlHp.add(io.param);
                                break;
                            case 80: //HP+#%/30s
                                this.tlHpHoi += io.param;
                                break;
                            case 81: //MP+#%/30s
                                this.tlMpHoi += io.param;
                                break;
                            case 94: //Giáp #%
                                this.tlDef.add(io.param);
                                break;
                            case 103: //KI +#%
                                this.tlMp.add(io.param);
                                break;
                            case 108: //#% Né đòn
                                this.tlNeDon += io.param;
                        }
                    }
                    break;
                }
            }
        } else if (this.player.isPl() && this.player.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4) {
            for (Item item : this.player.inventory.itemsBag) {
                if (item.isNotNullItem() && item.template.id == 1156) {
                    for (Item.ItemOption io : item.itemOptions) {
                        switch (io.optionTemplate.id) {
                            case 14: //Chí mạng+#%
                                this.critAdd += io.param;
                                break;
                            case 41:
                                this.tlDame.add(io.param);
                                this.tlHp.add(io.param);
                                this.tlMp.add(io.param);
                                break;
                            case 50: //Sức đánh+#%
                                this.tlDame.add(io.param);
                                break;
                            case 77: //HP+#%
                                this.tlHp.add(io.param);
                                break;
                            case 80: //HP+#%/30s
                                this.tlHpHoi += io.param;
                                break;
                            case 81: //MP+#%/30s
                                this.tlMpHoi += io.param;
                                break;
                            case 94: //Giáp #%
                                this.tlDef.add(io.param);
                                break;
                            case 103: //KI +#%
                                this.tlMp.add(io.param);
                                break;
                            case 108: //#% Né đòn
                                this.tlNeDon += io.param;
                        }
                    }
                    break;
                }
            }
        }

        setDameTrainArmor();
        setBasePoint();
    }

    private void setDameTrainArmor() {
        if (!this.player.isPet && !this.player.isBoss && !this.player.isPet3 && !this.player.isBoss) {
            if (this.player.inventory.itemsBody.size() < 7) {
                return;
            }
            try {
                Item gtl = this.player.inventory.itemsBody.get(6);
                if (gtl.isNotNullItem()) {
                    this.wearingTrainArmor = true;
                    this.wornTrainArmor = true;
                    this.player.inventory.trainArmor = gtl;
                    this.tlSubSD += ItemService.gI().getPercentTrainArmor(gtl);
                } else {
                    if (this.wornTrainArmor) {
                        this.wearingTrainArmor = false;
                        for (Item.ItemOption io : this.player.inventory.trainArmor.itemOptions) {
                            if (io.optionTemplate.id == 9 && io.param > 0) {
                                this.tlDame.add(ItemService.gI().getPercentTrainArmor(this.player.inventory.trainArmor));
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Logger.error("Lỗi get giáp tập luyện " + this.player.name + "\n");
            }
        }
    }

    public void setBasePoint() {
        setHpMax();
        setHp();
        setMpMax();
        setMp();
        setDame();
        setDef();
        setCrit();
        setHpHoi();
        setMpHoi();
        setNeDon();
        settlGold();
        setTLHPGiam();
    }

    private void setTLHPGiam() {
        if (this.player.zone != null && (this.player.isPl() || this.player.isPet) && !this.player.isDie()
                && MapService.gI().isMapDiaNguc(this.player.zone.map.mapId)) {
            this.isThieuDot = true;
            this.tlHpGiamDiaNguc = 20;
        } else {
            this.tlHpGiamDiaNguc = 0;
            this.isThieuDot = false;
        }
    }

    private void setNeDon() {

    }

    private void setHpHoi() {
        this.hpHoi = Util.TamkjllGH(this.hpMax / 100);
        this.hpHoi += this.hpHoiAdd;
        this.hpHoi += ((long) this.hpMax * this.tlHpHoi / 100);
        this.hpHoi += ((long) this.hpMax * this.tlHpHoiBanThanVaDongDoi / 100);
    }

    private void setMpHoi() {
        this.mpHoi = Util.TamkjllGH(this.mpMax / 100);
        this.mpHoi += this.mpHoiAdd;
        this.mpHoi += ((long) this.mpMax * this.tlMpHoi / 100);
        this.mpHoi += ((long) this.mpMax * this.tlMpHoiBanThanVaDongDoi / 100);
    }

    private void setHpMax() {
        this.hpMax = this.hpg;
        this.hpMax += this.hpAdd;
        if (this.player.effectSkill.isHoaLanh && !this.isKhongLanh) {
            this.hpMax /= 2;
        }
//        if (this.player.isPet && ((Pet) this.player).typePet == 1
//                && (((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA || ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2)) {
//            this.hpMax += ((long) this.hpMax * 20 / 100);
//        }
        //đồ
        int totalPercent = 0;
        for (int tl : tlHp) {
            totalPercent += tl;
        }
        this.hpMax += this.hpMax * totalPercent / 100;
        //set nappa
        if (this.player.setClothes.nappa == 5) {
            this.hpMax *= 2.0;
        }
        if (this.player.setClothes.nappa1 == 5) {
            this.hpMax *= 1.5;
        }
        //set worldcup
        if (this.player.setClothes.worldcup == 2) {
            this.hpMax += ((long) this.hpMax * 10 / 100);
        }
        if (this.player.setClothes.SetTinhAn == 5) {
            this.hpMax += ((long) this.hpMax * 30 / 100);
        }

        //ngọc rồng đen 2 sao
        if (this.player.rewardBlackBall.timeOutOfDateReward[1] > System.currentTimeMillis()) {
            this.hpMax += ((long) this.hpMax * RewardBlackBall.R1S_1 / 100);
        }
        //biến broly
        if (this.player.effectSkill.isBroly) {
            if (!this.player.isPet || (this.player.isPet
                    && ((Pet) this.player).status != Pet.FUSION)) {
                int percent = SkillUtil.getPercentHpBroly(player.effectSkill.levelBroly);
                this.hpMax += ((long) this.hpMax * percent / 100);
            }
        }

        if (this.player.effectSkill.isMonkey) {
            if (!this.player.isPet && !this.player.isPet3 || (this.player.isPet
                    && ((Pet) this.player).status != Pet.FUSION)) {
                int percent = SkillUtil.getPercentHpMonkey(player.effectSkill.levelMonkey);
                this.hpMax += ((long) this.hpMax * percent / 100);
            }
        }
        //super bư
        if (this.player.isPet && ((Pet) this.player).typePet == 1// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA) {
            this.hpMax += ((long) this.hpMax * 5 / 100);//chi so hp
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 1
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
            this.hpMax += ((long) this.hpMax * 5 / 100);
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 1
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
            this.hpMax += ((long) this.hpMax * 5 / 100);
        }

        if (this.player.isPet && ((Pet) this.player).typePet == 1
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4) {
            this.hpMax += ((long) this.hpMax * 10 / 100);
        }
        //pet Berus
        if (this.player.isPet && ((Pet) this.player).typePet == 2// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA) {
            this.hpMax += ((long) this.hpMax * 10 / 100);//chi so hp
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 2
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
            this.hpMax += ((long) this.hpMax * 10 / 100);
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 2
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
            this.hpMax += ((long) this.hpMax * 10 / 100);
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 2
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4) {
            this.hpMax += ((long) this.hpMax * 10 / 100);
        }
        //Pet UUb
        if (this.player.isPet && ((Pet) this.player).typePet == 3// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA) {
            this.hpMax += ((long) this.hpMax * 15 / 100);//chi so hp
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 3
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
            this.hpMax += ((long) this.hpMax * 15 / 100);
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 3
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
            this.hpMax += ((long) this.hpMax * 15 / 100);
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 3
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4) {
            this.hpMax += ((long) this.hpMax * 15 / 100);
        }
        //Bư Music
        if (this.player.isPet && ((Pet) this.player).typePet == 4// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA) {
            this.hpMax += ((long) this.hpMax * 20 / 100);//chi so hp
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 4
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
            this.hpMax += ((long) this.hpMax * 20 / 100);
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 4
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
            this.hpMax += ((long) this.hpMax * 20 / 100);
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 4
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4) {
            this.hpMax += ((long) this.hpMax * 20 / 100);
        }
        //Wukong
        if (this.player.isPet && ((Pet) this.player).typePet == 5// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA) {
            this.hpMax += ((long) this.hpMax * 25 / 100);//chi so hp
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 5
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
            this.hpMax += ((long) this.hpMax * 25 / 100);
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 5
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
            this.hpMax += ((long) this.hpMax * 25 / 100);
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 5
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4) {
            this.hpMax += ((long) this.hpMax * 25 / 100);
        }
        //goku bnvc
        if (this.player.isPet && ((Pet) this.player).typePet == 22// chi so lam sao bac tu cho dj
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA) {
            this.hpMax += ((long) this.hpMax * 30 / 100);//chi so hp
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 22
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
            this.hpMax += ((long) this.hpMax * 30 / 100);
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 22
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
            this.hpMax += ((long) this.hpMax * 30 / 100);
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 22
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4) {
            this.hpMax += ((long) this.hpMax * 30 / 100);
        }
        //phù
        if (this.player.zone != null && MapService.gI().isMapBlackBallWar(this.player.zone.map.mapId)) {
            this.hpMax *= this.player.effectSkin.xHPKI;
        }
        //+hp đệ
        if (this.player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
            this.hpMax += this.player.pet.nPoint.hpMax;
        }
//        //btc2
        if (this.player.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
            this.hpMax *= 1.05;
        }
        //btc3
        if (this.player.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
            this.hpMax *= 1.10;
        }
        //btc4
        if (this.player.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4) {
            this.hpMax *= 1.15;
        }
        //huýt sáo
        if (!this.player.isPet && !this.player.isPet3
                || (this.player.isPet
                && ((Pet) this.player).status != Pet.FUSION)) {
            if (this.player.effectSkill.tiLeHPHuytSao != 0) {
                this.hpMax += ((long) this.hpMax * this.player.effectSkill.tiLeHPHuytSao * 1 / 100L);
                //this.hpMax *=3;
            }
        }
        //bổ huyết
        if (this.player.itemTime != null && this.player.itemTime.isUseBoHuyet) {
            this.hpMax *= 2;
        }// item sieu cawsp
        if (this.player.itemTime != null && this.player.itemTime.isUseBoHuyet2) {
            this.hpMax += this.hpMax * 1.2;
        }
        if (this.player.zone != null && MapService.gI().isMapCold(this.player.zone.map)
                && !this.isKhongLanh) {
            this.hpMax /= 2;
        }
        if (this.player.itemTime.isdkhi) {
            this.hpMax += ((long) this.hpMax * 10 / 100);
            this.tlDameCrit.add(5);

        }
        if (this.Iskimco50) {
            this.hpMax = (this.hpMax * 50 / 100);
        }
        if (this.Iskimco80) {
            this.hpMax = (this.hpMax * 80 / 100);
        }
    }

    // (hp sư phụ + hp đệ tử ) + 15%
    // (hp sư phụ + 15% +hp đệ tử)
    private void setHp() {
        if (this.hp > this.hpMax) {
            this.hp = this.hpMax;
        }
    }

    private void setMpMax() {
        this.mpMax = this.mpg;
        this.mpMax += this.mpAdd;
        //đồ
        for (Integer tl : new ArrayList<>(this.tlMp)) {
            this.mpMax += ((long) this.mpMax * tl / 100);
        }

        if (this.player.effectSkill.isHoaLanh && !this.isKhongLanh) {
            this.mpMax /= 2;
        }
        if (this.player.setClothes.picolo == 5) {
            this.mpMax *= 2;
        }
        if (this.player.setClothes.picolo1 == 5) {
            this.mpMax *= 1.5;
        }
        //set nhật ấn
        if (this.player.setClothes.SetNhatAn == 5) {
            this.mpMax += ((long) this.mpMax * 30 / 100);
        }

        //ngọc rồng đen 3 sao
        if (this.player.rewardBlackBall.timeOutOfDateReward[5] > System.currentTimeMillis()) {
            this.mpMax += ((long) this.mpMax * RewardBlackBall.R6S_1 / 100);
        }
        //set worldcup
        if (this.player.setClothes.worldcup == 2) {
            this.mpMax += ((long) this.mpMax * 10 / 100);
        }
        //pet mabư
        if (this.player.isPet && ((Pet) this.player).typePet == 1
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA) {
            this.mpMax += ((long) this.mpMax * 5 / 100);
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 1
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
            this.mpMax += ((long) this.mpMax * 5 / 100);
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 1
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
            this.mpMax += ((long) this.mpMax * 5 / 100);
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 1
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4) {
            this.mpMax += ((long) this.mpMax * 5 / 100);
        }
        //pet berus
        if (this.player.isPet && ((Pet) this.player).typePet == 2
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA) {
            this.mpMax += ((long) this.mpMax * 10 / 100);
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 2
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
            this.mpMax += ((long) this.mpMax * 10 / 100);
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 2
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
            this.mpMax += ((long) this.mpMax * 10 / 100);
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 2
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4) {
            this.mpMax += ((long) this.mpMax * 10 / 100);
        }
        //pet Fide
        if (this.player.isPet && ((Pet) this.player).typePet == 3
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA) {
            this.mpMax += ((long) this.mpMax * 15 / 100);
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 3
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
            this.mpMax += ((long) this.mpMax * 15 / 100);
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 3
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
            this.mpMax += ((long) this.mpMax * 15 / 100);
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 3
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4) {
            this.mpMax += ((long) this.mpMax * 15 / 100);
        }
        //Music
        if (this.player.isPet && ((Pet) this.player).typePet == 4
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA) {
            this.mpMax += ((long) this.mpMax * 20 / 100);
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 4
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
            this.mpMax += ((long) this.mpMax * 20 / 100);
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 4
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
            this.mpMax += ((long) this.mpMax * 20 / 100);
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 4
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4) {
            this.mpMax += ((long) this.mpMax * 20 / 100);
        }
        //wukong
        if (this.player.isPet && ((Pet) this.player).typePet == 5
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA) {
            this.mpMax += ((long) this.mpMax * 25 / 100);
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 5
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
            this.mpMax += ((long) this.mpMax * 25 / 100);
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 5
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
            this.mpMax += ((long) this.mpMax * 25 / 100);
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 5
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4) {
            this.mpMax += ((long) this.mpMax * 25 / 100);
        }
        //goku
        if (this.player.isPet && ((Pet) this.player).typePet == 22
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA) {
            this.mpMax += ((long) this.mpMax * 30 / 100);
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 22
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
            this.mpMax += ((long) this.mpMax * 30 / 100);
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 22
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
            this.mpMax += ((long) this.mpMax * 30 / 100);
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 22
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4) {
            this.mpMax += ((long) this.mpMax * 30 / 100);
        }
        //hợp thể
        if (this.player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
            this.mpMax += this.player.pet.nPoint.mpMax;
        }
//        //BTc2
        if (this.player.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
            this.mpMax *= 1.05;
        }
        //btc3
        if (this.player.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
            this.mpMax *= 1.10;
        }
        //btc4
        if (this.player.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4) {
            this.mpMax *= 1.15;
        }
        //bổ khí
        if (this.player.itemTime != null && this.player.itemTime.isUseBoKhi) {
            this.mpMax *= 2;
        }
        if (this.player.itemTime != null && this.player.itemTime.isUseBoKhi2) {
            this.mpMax += this.mpMax * 1.2;
        }
        if (this.player.itemTime.isdkhi) {
            this.mpMax += ((long) this.dame * 10 / 100);
        }
        if (this.player.effectSkill.isBroly) {//biến broly
            if (!this.player.isPet || (this.player.isPet
                    && ((Pet) this.player).status != Pet.FUSION)) {
                int percent = SkillUtil.getPercentKiBroly(player.effectSkill.levelBroly);
                this.mpMax += ((long) this.mp * percent / 100);
            }
        }
        if (this.player.zone != null && MapService.gI().isMapCold(this.player.zone.map)
                && !this.isKhongLanh) {
            this.mpMax /= 2;
        }
        if (this.Iskimco50) {
            this.mpMax = (this.mpMax * 50 / 100);
        }
        if (this.Iskimco80) {
            this.mpMax = (this.mpMax * 80 / 100);
        }
        //phù
        if (this.player.zone != null && MapService.gI().isMapBlackBallWar(this.player.zone.map.mapId)) {
            this.mpMax *= this.player.effectSkin.xHPKI;
        }
//        //xiên cá
        if (this.player.effectFlagBag.useXienCa) {
            this.mpMax += ((long) this.mpMax * 15 / 100);
        }
    }

    private void setMp() {
        if (this.mp > this.mpMax) {
            this.mp = this.mpMax;
        }
    }

    private void setDame() {
        this.dame = this.dameg;
        this.dame += this.dameAdd;
        //đồ
        for (Integer tl : this.tlDame) {
            this.dame += ((long) this.dame * tl / 100);
        }
        for (Integer tl : this.tlSDDep) {
            this.dame += ((long) this.dame * tl / 100);
        }
        //set nguyệt ấn
        if (this.player.setClothes.SetNguyetAn == 5) {
            this.dame += ((long) this.dame * 20 / 100);
        }
        //pet mabư
        if (this.player.isPet && ((Pet) this.player).typePet == 1
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA) {
            this.dame += ((long) this.dame * 5 / 100);
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 1
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
            this.dame += ((long) this.dame * 5 / 100);
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 1
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
            this.dame += ((long) this.dame * 5 / 100);
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 1
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4) {
            this.dame += ((long) this.dame * 5 / 100);
        }
        //pet Xên
        if (this.player.isPet && ((Pet) this.player).typePet == 2
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA) {
            this.dame += ((long) this.dame * 10 / 100);
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 2
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
            this.dame += ((long) this.dame * 10 / 100);
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 2
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
            this.dame += ((long) this.dame * 10 / 100);
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 2
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4) {
            this.dame += ((long) this.dame * 10 / 100);
        }
        //pet Fide
        if (this.player.isPet && ((Pet) this.player).typePet == 3
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA) {
            this.dame += ((long) this.dame * 15 / 100);
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 3
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
            this.dame += ((long) this.dame * 15 / 100);
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 3
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
            this.dame += ((long) this.dame * 15 / 100);
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 3
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4) {
            this.dame += ((long) this.dame * 15 / 100);
        }
        //Music
        if (this.player.isPet && ((Pet) this.player).typePet == 4
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA) {
            this.dame += ((long) this.dame * 20 / 100);
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 4
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
            this.dame += ((long) this.dame * 20 / 100);
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 4
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
            this.dame += ((long) this.dame * 20 / 100);
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 4
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4) {
            this.dame += ((long) this.dame * 20 / 100);
        }
        //wukong
        if (this.player.isPet && ((Pet) this.player).typePet == 5
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA) {
            this.dame += ((long) this.dame * 25 / 100);
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 5
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
            this.dame += ((long) this.dame * 25 / 100);
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 5
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
            this.dame += ((long) this.dame * 25 / 100);
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 5
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4) {
            this.dame += ((long) this.dame * 25 / 100);
        }
        //goku
        if (this.player.isPet && ((Pet) this.player).typePet == 22
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA) {
            this.dame += ((long) this.dame * 30 / 100);
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 22
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
            this.dame += ((long) this.dame * 30 / 100);
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 22
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
            this.dame += ((long) this.dame * 30 / 100);
        }
        if (this.player.isPet && ((Pet) this.player).typePet == 22
                && ((Pet) this.player).master.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4) {
            this.dame += ((long) this.dame * 30 / 100);
        }
        //thức ăn
        if (!this.player.isPet && !this.player.isPet3 && this.player.itemTime.isEatMeal
                || this.player.isPet && ((Pet) this.player).master.itemTime.isEatMeal) {
            this.dame += ((long) this.dame * 10 / 100);
        }
        //hợp thể
        if (this.player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
            this.dame += this.player.pet.nPoint.dame;
        }
//        //btc2
        if (this.player.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
            this.dame *= 1.05;
        }
        //btc3
        if (this.player.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
            this.dame *= 1.10;
        }
        //btc4
        if (this.player.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4) {
            this.dame *= 1.15;
        }
        //cuồng nộ
        if (this.player.itemTime != null && this.player.itemTime.isUseCuongNo) {
            this.dame *= 2;
        }
        if (this.player.itemTime != null && this.player.itemTime.isUseCuongNo2) {
            this.dame += this.dame * 1.2;
        }
        //giảm dame
        this.dame -= ((long) this.dame * tlSubSD / 100);
        //map cold
        if (this.player.zone != null && MapService.gI().isMapCold(this.player.zone.map)
                && !this.isKhongLanh) {
            this.dame /= 2;
        }
        //ngọc rồng đen 1 sao
        if (this.player.rewardBlackBall.timeOutOfDateReward[0] > System.currentTimeMillis()) {
            this.dame += ((long) this.dame * RewardBlackBall.R1S_1 / 100);
        }
        //set worldcup
        if (this.player.setClothes.worldcup == 2) {
            this.dame += ((long) this.dame * 10 / 100);
            this.tlDameCrit.add(20);
        }
        if (this.Iskimco50) {
            this.dame = (this.dame * 50 / 100);
        }
        if (this.Iskimco80) {
            this.dame = (this.dame * 80 / 100);
        }

//        //phóng heo
        if (this.player.effectFlagBag.usePhongHeo) {
            this.dame += ((long) this.dame * 15 / 100);
        }
        //khỉ
        if (this.player.effectSkill.isBroly) { //biến broly
            if (!this.player.isPet || (this.player.isPet
                    && ((Pet) this.player).status != Pet.FUSION)) {
                int percent = SkillUtil.getPercentDameBroly(player.effectSkill.levelBroly);
                this.dame += ((long) this.dame * percent / 100);
            }
        }
        if (this.player.effectSkill.isMonkey) {
            if (!this.player.isPet && !this.player.isPet3 || (this.player.isPet
                    && ((Pet) this.player).status != Pet.FUSION)) {
                int percent = SkillUtil.getPercentDameMonkey(player.effectSkill.levelMonkey);
                this.dame += ((long) this.dame * percent / 100);
            }
        }
        if (this.player.itemTime.isdkhi) {
            this.dame += ((long) this.dame * 10 / 100);
        }
        if (this.player.itemTime.isdcarot) {
            this.dame += ((long) this.dame * 5 / 100);
        }

    }

    private void setDef() {
        this.def = this.defg * 4;
        this.def += this.defAdd;
        //đồ
        for (Integer tl : this.tlDef) {
            this.def += ((long) this.def * tl / 100);
        }
        //ngọc rồng đen 2 sao
        //if (this.player.rewardBlackBall.timeOutOfDateReward[1] > System.currentTimeMillis()) {
        //    this.def += ((long) this.def * RewardBlackBall.R2S_2 / 100);
        //}
    }

    public void setCrit() {
        this.crit = this.critg;
        this.crit += this.critAdd;
        // ngọc rồng đen 3 sao
        if (this.player.rewardBlackBall.timeOutOfDateReward[4] > System.currentTimeMillis()) {
            this.tlDameCrit.add(RewardBlackBall.R5S_1);
        }
        // da do
//        if (!this.player.isPet && this.player.itemTime.isEatDaDo
//                || this.player.isPet && ((Pet) this.player).master.itemTime.isEatDaDo) {
//            this.crit += 10;
//        }
//        if (this.player.itemTime != null && this.player.itemTime.istrbcrit) {
//            this.crit += 10;
//        }
        // biến khỉ
        if (this.player.effectSkill.isMonkey) {
            this.crit = 110;
        }
//        if (!this.player.isPet && this.player.itemTime.isEatDuoiKhi && !this.player.effectSkill.isMonkey) {
//            this.crit = 110;
//        }
        // cuong chien
        if (this.player.effectSkill.isCuongChien) {
            this.crit += 10;
        }
//        if (this.player.effectSkill.isHoaCarot || this.player.effectSkill.isHoaSocola
//                || this.player.effectSkill.isHoaKeo || this.player.effectSkill.isHoaBiNgo) {
//            this.crit = 1;
//        }
        if (this.player.effectSkill.isHorny) {
            this.crit += 10;
        }
        //
//        if (!this.player.effectSkill.isMonkey && !this.player.effectSkill.isCuongChien && this.crit > 33
//                && !this.player.itemTime.isEatDuoiKhi) {
//            this.crit = 50;
//        }
//        if (this.player.fusion.typeFusion == ConstPlayer.LUONG_LONG_NHAT_THE && this.player.nPoint.isSSJ4
//                && this.player.pet != null && this.player.pet.nPoint.isSSJ4) {
//            this.crit = 100;
//        }

    }

    private void resetPoint() {
        this.voHieuChuong = 0;
        this.hpAdd = 0;
        this.mpAdd = 0;
        this.dameAdd = 0;
        this.isDrabura = false;
        this.defAdd = 0;
        this.critAdd = 0;
        this.tlHp.clear();
        this.tlMp.clear();
        this.tlDef.clear();
        this.tlDame.clear();
        this.tlDameCrit.clear();
        this.tlDameAttMob.clear();
        this.tlHpHoiBanThanVaDongDoi = 0;
        this.tlMpHoiBanThanVaDongDoi = 0;
        this.hpHoi = 0;
        this.mpHoi = 0;
        this.mpHoiCute = 0;
        this.tlHpHoi = 0;
        this.tlMpHoi = 0;
        this.tlHutHp = 0;
        this.tlHutMp = 0;
        this.tlHutHpMob = 0;
        this.tlHutHpMpXQ = 0;
        this.tlPST = 0;
        this.tlTNSM.clear();
        this.tlDameAttMob.clear();
        this.tlHpGiamDiaNguc = 0;
        this.tlGold = 0;
        this.tlNeDon = 0;
        this.tlSDDep.clear();
        this.tlSubSD = 0;
        this.tlHpGiamODo = 0;
        this.test = 0;
        this.tlGold = 0;
        this.tlDameSexy = 0;
        this.IsBiHoaDa = false;
        this.CoCaiTrangHoaDa = false;
        this.Iskimco50 = false;
        this.Iskimco80 = false;
        this.teleport = false;
        this.wearingVoHinh = false;
        this.isKhongLanh = false;
        this.isSoiDaiCa = false;
        this.khangTDHS = false;
        this.isFrost = false;
        this.khangTM = false;
        this.isBuiBui = false;
        this.isMabu = false;
        this.isSexy = false;
        this.isYacon = false;
        this.isThieuDot = false;
        this.isXinBaTo = false;
        this.isSieuThan = false;
    }

    public void addHp(long hp) {
        this.hp += hp;
        if (this.hp > this.hpMax) {
            this.hp = this.hpMax;
        }
    }

    public void addMp(long mp) {
        this.mp += mp;
        if (this.mp > this.mpMax) {
            this.mp = this.mpMax;
        }
    }

    public void setHp(long hp) {
        if (hp > this.hpMax) {
            this.hp = this.hpMax;
        } else {
            this.hp = (long) hp;
        }
    }

    public void setMp(long mp) {
        if (mp > this.mpMax) {
            this.mp = this.mpMax;
        } else {
            this.mp = mp;
        }
    }

    public long getHP() {
        return this.hp <= this.hpMax ? this.hp : this.hpMax;
    }

    public void settlGold() {
        if (intrinsic != null && intrinsic.id == 23) {
            this.tlGold += intrinsic.param1;
        }
    }

    private void setIsCrit() {
        if (intrinsic != null && intrinsic.id == 25
                && this.getCurrPercentHP() <= intrinsic.param1) {
            isCrit = true;
        } else if (isCrit100) {
            isCrit100 = false;
            isCrit = true;
        } else {
            isCrit = Util.isTrue(this.crit, ConstRatio.PER100);
        }
    }

    public double getDameAttack(boolean isAttackMob) {
        setIsCrit();
        double dameAttack = this.dame;
        intrinsic = this.player.playerIntrinsic.intrinsic;
        percentDameIntrinsic = 0;
        long percentDameSkill = 0;
        long percentXDame = 0;
        Skill skillSelect = player.playerSkill.skillSelect;
        switch (skillSelect.template.id) {
            case Skill.DRAGON:
                if (intrinsic.id == 1) {
                    percentDameIntrinsic = intrinsic.param1;
                }
                percentDameSkill = skillSelect.damage;
                break;
            case Skill.KAMEJOKO:
                if (intrinsic.id == 2) {
                    percentDameIntrinsic = intrinsic.param1;
                }
                percentDameSkill = skillSelect.damage;
                if (this.player.setClothes.songoku == 5) {
                    percentXDame = 100;
                }
                if (this.player.setClothes.songoku1 == 5) {
                    percentXDame = 50;
                }
                break;
            case Skill.GALICK:
                if (intrinsic.id == 16) {
                    percentDameIntrinsic = intrinsic.param1;
                }
                percentDameSkill = skillSelect.damage;
                if (this.player.setClothes.kakarot == 5) {
                    percentXDame = 100;
                }
                if (this.player.setClothes.kakarot1 == 5) {
                    percentXDame = 50;
                }
                break;
            case Skill.ANTOMIC:
                if (intrinsic.id == 17) {
                    percentDameIntrinsic = intrinsic.param1;
                }
                percentDameSkill = skillSelect.damage;
                break;
            case Skill.DEMON:
                if (intrinsic.id == 8) {
                    percentDameIntrinsic = intrinsic.param1;
                }
                percentDameSkill = skillSelect.damage;
                break;
            case Skill.MASENKO:
                if (intrinsic.id == 9) {
                    percentDameIntrinsic = intrinsic.param1;
                }
                percentDameSkill = skillSelect.damage;
                break;
            case Skill.KAIOKEN:
                if (intrinsic.id == 26) {
                    percentDameIntrinsic = intrinsic.param1;
                }
                percentDameSkill = skillSelect.damage;
                if (this.player.setClothes.kaioken == 5) {
                    percentXDame = 100;
                }
                if (this.player.setClothes.kaioken1 == 5) {
                    percentXDame = 50;
                }
                break;
            case Skill.SUPER_KAME:
                percentDameSkill = skillSelect.damage;
                break;
            case Skill.LIEN_HOAN_CHUONG:
                percentDameSkill = skillSelect.damage;
                break;
            case Skill.LIEN_HOAN:
                if (intrinsic.id == 13) {
                    percentDameIntrinsic = intrinsic.param1;
                }
                percentDameSkill = skillSelect.damage;
                if (this.player.setClothes.ocTieu == 5) {
                    percentXDame = 100;
                }
                if (this.player.setClothes.ocTieu1 == 5) {
                    percentXDame = 50;
                }
                break;
            case Skill.TU_SAT:
                percentDameSkill = skillSelect.damage;
                // Nếu đang hợp thể + có cả item 1593 và 1874 thì +100% dame
                if (this.player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
                    boolean has1593 = false;
                    boolean has1874 = false;
                    for (Item it : this.player.inventory.itemsBody) {
                        if (it != null && it.template != null) {
                            if (it.template.id == 1593) {
                                has1593 = true;
                            }
                            if (it.template.id == 1874) {
                                has1874 = true;
                            }
                        }
                    }
                    if (has1593 || has1874) {
                        percentXDame = 100; // +100% dame skill TU_SAT
                    }
                }
                break;
            case Skill.DICH_CHUYEN_TUC_THOI:
                dameAttack *= 2;
                dameAttack = Util.Tamkjllnext(dameAttack - (dameAttack * 5 / 100),
                        dameAttack + (dameAttack * 5 / 100));
                return dameAttack;
            case Skill.MAKANKOSAPPO:
                percentDameSkill = skillSelect.damage;
                long dameSkill = this.mpMax * percentDameSkill / 100;

                // Nếu có cả item 1884 và 1885 thì +100% dame
                boolean has1884 = false;
                boolean has1885 = false;
                for (Item it : this.player.inventory.itemsBody) {
                    if (it != null && it.template != null) {
                        if (it.template.id == 1884) {
                            has1884 = true;
                        }
                        if (it.template.id == 1885) {
                            has1885 = true;
                        }
                    }
                }
                if (has1884 || has1885) {
                    dameSkill *= 2; // +100% dame MAKANKOSAPPO
                }

                return dameSkill;

            case Skill.QUA_CAU_KENH_KHI:
                long dame = this.dame * 40;
                if (this.player.setClothes.kirin == 5) {
                    dame *= 2;
                }
                if (this.player.setClothes.kirin1 == 5) {
                    dame *= 1.5;
                }
                dame = dame + (Util.nextInt(-5, 5) * dame / 100);
                return dame;
        }
        if (intrinsic.id == 18 && this.player.effectSkill.isMonkey) {
            percentDameIntrinsic = intrinsic.param1;
        }
        if (percentDameSkill != 0) {
            dameAttack = dameAttack * percentDameSkill / 100;
        }
        dameAttack += (dameAttack * percentDameIntrinsic / 100);
        dameAttack += (dameAttack * dameAfter / 100);

        if (isAttackMob) {
            for (Integer tl : this.tlDameAttMob) {
                dameAttack += (dameAttack * tl / 100);
            }
        }
        dameAfter = 0;
        if (this.player.isPet && ((Pet) this.player).master.charms.tdDeTu > System.currentTimeMillis()) {
            dameAttack *= 2;
        }
        if (isCrit) {
            dameAttack *= 2;
            for (Integer tl : this.tlDameCrit) {
                dameAttack += (dameAttack * tl / 100);
            }
        }
        dameAttack += dameAttack * percentXDame / 100;
        dameAttack = Util.Tamkjllnext(dameAttack - (dameAttack * 5 / 100), dameAttack + (dameAttack * 5 / 100));
        if (player.isPl()) {
            if (player.inventory.haveOption(player.inventory.itemsBody, 5, 159)) {
                if (Util.canDoWithTime(player.lastTimeUseOption, 60000) && (player.playerSkill.skillSelect.skillId == Skill.KAMEJOKO || player.playerSkill.skillSelect.skillId == Skill.ANTOMIC || player.playerSkill.skillSelect.skillId == Skill.MASENKO)) {
                    dameAttack *= player.inventory.getParam(player.inventory.itemsBody.get(5), 159);
                    player.lastTimeUseOption = System.currentTimeMillis();
                }
            }
        }
        //check activation set
        return dameAttack;
    }

    public long calPercent(long param, long percent) {
        return param * percent / 100;
    }

    public long getDameAttackSkillNotFocus() {
        setIsCrit();
        long dameAttack = this.dame;
        intrinsic = this.player.playerIntrinsic.intrinsic;
        percentDameIntrinsic = 0;
        int percentDameSkill = 0;
        int percentXDame = 0;
        Skill skillSelect = player.playerSkill.skillSelect;
        switch (skillSelect.template.id) {

        }
        if (intrinsic.id == 18 && this.player.effectSkill.isMonkey) {
            percentDameIntrinsic = intrinsic.param1;
        }
        if (percentDameSkill != 0) {
            dameAttack = calPercent(dameAttack, percentDameSkill);
        }
        dameAttack += calPercent(dameAttack, percentDameIntrinsic);
        dameAttack += calPercent(dameAttack, dameAfter);
        dameAfter = 0;
        if (this.player.isPet && ((Pet) this.player).master.charms.tdDeTu > System.currentTimeMillis()) {
            dameAttack *= 2;
        }
        if (isCrit) {
            dameAttack *= 2;
            for (Integer tl : this.tlDameCrit) {
                dameAttack += calPercent(dameAttack, tl);
            }
        }
        dameAttack += calPercent(dameAttack, percentXDame);
        dameAttack = Util.Tamkjllnext((long) (dameAttack - calPercent(dameAttack, 5)), (long) (dameAttack + calPercent(dameAttack, 5)));
        return (long) dameAttack;
    }

    public int getCurrPercentHP() {
        if (this.hpMax == 0) {
            return 100;
        }
        return (int) ((long) this.hp * 100 / this.hpMax);
    }

    public int getCurrPercentMP() {
        return (int) ((long) this.mp * 100 / this.mpMax);
    }

    public void setFullHpMp() {
        this.hp = this.hpMax;
        this.mp = this.mpMax;

    }

    public void subHP(double sub) {
        this.hp -= sub;
        if (this.hp < 0) {
            this.hp = 0;
        }
    }

    public void subMP(int sub) {
        this.mp -= sub;
        if (this.mp < 0) {
            this.mp = 0;
        }
    }

    public long calSucManhTiemNang(long tiemNang) {
        // ❗ Chặn từ đầu nếu là sư phụ và đã quá 220 tỷ
//        if (!this.player.isPet && this.power >= 220_100_000_000L) {
//            return 0;
//        }

        if (power < getPowerLimit()) {
            for (Integer tl : this.tlTNSM) {
                tiemNang += ((long) tiemNang * tl / 100);
            }
            if (this.player.cFlag != 0) {
                if (this.player.cFlag == 8) {
                    tiemNang += ((long) tiemNang * 10 / 100);
                } else {
                    tiemNang += ((long) tiemNang * 5 / 100);
                }
            }
            long tn = tiemNang;
            if (this.player.charms.tdTriTue > System.currentTimeMillis()) {
                tiemNang += tn;
            }
            if (this.player.charms.tdTriTue3 > System.currentTimeMillis()) {
                tiemNang += tn * 2;
            }
            if (this.player.charms.tdTriTue4 > System.currentTimeMillis()) {
                tiemNang += tn * 3;
            }
            if (this.intrinsic != null && this.intrinsic.id == 24) {
                tiemNang += ((long) tiemNang * this.intrinsic.param1 / 100);
            }
            if (this.power >= 80000000000L) {
                tiemNang -= ((long) tiemNang * 80 / 100);
            }
            if (this.player.isPet) {
                if (((Pet) this.player).master.charms.tdDeTu > System.currentTimeMillis()) {
                    tiemNang += tn * 2;
                }
            }
            tiemNang *= Manager.RATE_EXP_SERVER;
            tiemNang = calSubTNSM(tiemNang);
            if (tiemNang <= 0) {
                tiemNang = 1;
            }
        } else {
            tiemNang = 10;
        }
        return tiemNang;
    }

    public long calSubTNSM(long tiemNang) {
        long pwr = this.power;
        if (this.player.isPet) {
            pwr = ((Pet) this.player).nPoint.power; // lấy sức mạnh chính của đệ tử
        }

        if (pwr >= getPowerLimit()) {
            tiemNang = 0;
        } else if (pwr >= 300_100_000_000L) {
            tiemNang /= 5000;
        } else if (pwr >= 280_100_000_000L) {
            tiemNang -= (tiemNang * 99 / 100);
        } else if (pwr >= 220_100_000_000L) {
            tiemNang -= (tiemNang * 98 / 100);
        }

        return tiemNang;
    }

    public short getTileHutHp(boolean isMob) {
        if (isMob) {
            return (short) (this.tlHutHp + this.tlHutHpMob);
        } else {
            return this.tlHutHp;
        }
    }

    public short getTiLeHutMp() {
        return this.tlHutMp;
    }

    public short getTlGold() {
        return this.tlGold;
    }

    public double subDameInjureWithDeff(double dame) {
        int def = this.def;
        dame -= def;
        if (this.player.itemTime.isUseGiapXen) {
            dame /= 2;
        }
        if (this.player.itemTime.isUseGiapXen2) {
            dame -= dame * 60;
        }
        if (dame < 0) {
            dame = 1;
        }
        return dame;
    }

    /*------------------------------------------------------------------------*/
    public boolean canOpenPower() {
        return this.power >= getPowerLimit();
    }

    public long getPowerLimit() {
        switch (limitPower) {
            case 0:
                return 18000000000L;
            case 1:
                return 30000000000L;
            case 2:
                return 50000000000L;
            case 3:
                return 80100000000L;
            case 4:
                return 120100000000L;
            case 5:
                return 170100000000L;
            case 6:
                return 220100000000L;
            case 7:
                return 280100000000L;
            case 8:
                return 310100000000L;
            case 9:
                return 350100000000L;
            default:
                return 0;
        }
    }

    public long getPowerNextLimit() {
        switch (limitPower + 1) {
            case 0:
                return 18000000000L;
            case 1:
                return 30000000000L;
            case 2:
                return 50000000000L;
            case 3:
                return 80100000000L;
            case 4:
                return 120100000000L;
            case 5:
                return 170100000000L;
            case 6:
                return 220100000000L;
            case 7:
                return 280100000000L;
            case 8:
                return 310100000000L;
            case 9:
                return 350100000000L;
            default:
                return 0;
        }
    }

    public int getHpMpLimit() {
        if (limitPower == 0) {
            return 220000;
        }
        if (limitPower == 1) {
            return 240000;
        }
        if (limitPower == 2) {
            return 300000;
        }
        if (limitPower == 3) {
            return 350000;
        }
        if (limitPower == 4) {
            return 400000;
        }
        if (limitPower == 5) {
            return 450000;
        }
        if (limitPower == 6) {
            return 500000;
        }
        if (limitPower == 7) {
            return 550000;
        }
        if (limitPower == 8) {
            return 560000;
        }
        if (limitPower == 9) {
            return 700000;
        }
        return 0;
    }

    public int getDameLimit() {
        if (limitPower == 0) {
            return 11000;
        }
        if (limitPower == 1) {
            return 12000;
        }
        if (limitPower == 2) {
            return 15000;
        }
        if (limitPower == 3) {
            return 18000;
        }
        if (limitPower == 4) {
            return 20000;
        }
        if (limitPower == 5) {
            return 22000;
        }
        if (limitPower == 6) {
            return 25000;
        }
        if (limitPower == 7) {
            return 30000;
        }
        if (limitPower == 8) {
            return 31000;
        }
        if (limitPower == 9) {
            return 35000;
        }
        return 0;
    }

    public short getDefLimit() {
        if (limitPower == 0) {
            return 550;
        }
        if (limitPower == 1) {
            return 600;
        }
        if (limitPower == 2) {
            return 700;
        }
        if (limitPower == 3) {
            return 800;
        }
        if (limitPower == 4) {
            return 1000;
        }
        if (limitPower == 5) {
            return 1200;
        }
        if (limitPower == 6) {
            return 1400;
        }
        if (limitPower == 7) {
            return 1600;
        }
        if (limitPower == 8) {
            return 1700;
        }
        if (limitPower == 9) {
            return 1800;
        }
        return 0;
    }

    public byte getCritLimit() {
        if (limitPower == 0) {
            return 5;
        }
        if (limitPower == 1) {
            return 6;
        }
        if (limitPower == 2) {
            return 7;
        }
        if (limitPower == 3) {
            return 8;
        }
        if (limitPower == 4) {
            return 9;
        }
        if (limitPower == 5) {
            return 10;
        }
        if (limitPower == 6) {
            return 10;
        }
        if (limitPower == 7) {
            return 10;
        }
        if (limitPower == 8) {
            return 10;
        }
        if (limitPower == 9) {
            return 10;
        }
        return 0;
    }

    public int getexp() {
        int[] expTable = {5000, 10000, 20000, 40000, 80000, 120000, 240000, 500000};
        if (player.typetrain >= 0 && player.typetrain < expTable.length) {
            return expTable[player.typetrain];
        } else {
            return 0;
        }
    }

    public String getNameNPC(Player player, Npc npc, byte type) {
        if (type == 2) {
            switch (npc.tempId) {
                case ConstNpc.THAN_MEO_KARIN:
                case ConstNpc.THUONG_DE:
                case ConstNpc.THAN_VU_TRU:
                case ConstNpc.TO_SU_KAIO:
                case ConstNpc.BILL:
                    return "ta";
            }
        } else if (type == 1) {
            switch (npc.tempId) {
                case ConstNpc.THAN_MEO_KARIN:
                    return "Yajirô";
                case ConstNpc.THUONG_DE:
                    return "Mr.PôPô";
                case ConstNpc.THAN_VU_TRU:
                    return "Khỉ Bubbles";
                case ConstNpc.TO_SU_KAIO:
                    return "ta";
                case ConstNpc.BILL:
                    return "Whis";
            }
        }
        return "Vũ Trụ Ngọc Rồng";
    }

    public int getExpbyNPC(Player player, Npc npc, byte type) {
        if (type == 2) {
            switch (npc.tempId) {
                case ConstNpc.THAN_MEO_KARIN:
                    return 5000;
                case ConstNpc.THUONG_DE:
                    return 20000;
                case ConstNpc.THAN_VU_TRU:
                    return 80000;
                case ConstNpc.TO_SU_KAIO:
                    return 120000;
                case ConstNpc.BILL:
                    return 500000;
            }
        } else if (type == 1) {
            switch (npc.tempId) {
                case ConstNpc.THAN_MEO_KARIN:
                    return 5000;
                case ConstNpc.THUONG_DE:
                    return 10000;
                case ConstNpc.THAN_VU_TRU:
                    return 40000;
                case ConstNpc.TO_SU_KAIO:
                    return 120000;
                case ConstNpc.BILL:
                    return 240000;
            }
        }
        return 0;
    }

    //**************************************************************************
    //POWER - TIEM NANG
    public void powerUp(long power) {
        this.power += power;
        TaskService.gI().checkDoneTaskPower(player, this.power);
    }

    public void tiemNangUp(long tiemNang) {
        this.tiemNang += tiemNang;
    }

    public void increasePoint(byte type, short point) {
        if (point <= 0 || point > 100) {
            return;
        }
        long tiemNangUse = 0;
        if (type == 0) {
            int pointHp = point * 20;
            tiemNangUse = point * (2 * (this.hpg + 1000) + pointHp - 20) / 2;
            if ((this.hpg + pointHp) <= getHpMpLimit()) {
                if (doUseTiemNang(tiemNangUse)) {
                    hpg += pointHp;
                }
            } else {
                Service.gI().sendThongBaoOK(player, "Vui lòng mở giới hạn sức mạnh");
                return;
            }
        }
        if (type == 1) {
            int pointMp = point * 20;
            tiemNangUse = point * (2 * (this.mpg + 1000) + pointMp - 20) / 2;
            if ((this.mpg + pointMp) <= getHpMpLimit()) {
                if (doUseTiemNang(tiemNangUse)) {
                    mpg += pointMp;
                }
            } else {
                Service.gI().sendThongBaoOK(player, "Vui lòng mở giới hạn sức mạnh");
                return;
            }
        }
        if (type == 2) {
            tiemNangUse = point * (2 * this.dameg + point - 1) / 2 * 100;
            if ((this.dameg + point) <= getDameLimit()) {
                if (doUseTiemNang(tiemNangUse)) {
                    dameg += point;
                }
            } else {
                Service.gI().sendThongBaoOK(player, "Vui lòng mở giới hạn sức mạnh");
                return;
            }
        }
        if (type == 3) {
            tiemNangUse = 2 * (this.defg + 5) / 2 * 100000;
            if ((this.defg + point) <= getDefLimit()) {
                if (doUseTiemNang(tiemNangUse)) {
                    defg += point;
                }
            } else {
                Service.gI().sendThongBaoOK(player, "Vui lòng mở giới hạn sức mạnh");
                return;
            }
        }
        if (type == 4) {
            tiemNangUse = 50000000L;
            for (int i = 0; i < this.critg; i++) {
                tiemNangUse *= 5L;
            }
            if ((this.critg + point) <= getCritLimit()) {
                if (doUseTiemNang(tiemNangUse)) {
                    critg += point;
                }
            } else {
                Service.gI().sendThongBaoOK(player, "Vui lòng mở giới hạn sức mạnh");
                return;
            }
        }
        Service.gI().point(player);
    }

    private boolean doUseTiemNang(long tiemNang) {
        if (this.tiemNang < tiemNang) {
            Service.gI().sendThongBaoOK(player, "Bạn không đủ tiềm năng");
            return false;
        }
        if (this.tiemNang >= tiemNang && this.tiemNang - tiemNang >= 0) {
            this.tiemNang -= tiemNang;
            TaskService.gI().checkDoneTaskUseTiemNang(player);
            return true;
        }
        return false;
    }

    //--------------------------------------------------------------------------
    private long lastTimeHoiPhuc;
    private long lastTimeHoiStamina;

    public void update() {
        if (player != null && player.effectSkill != null) {
            if (player.effectSkill.isCharging && player.effectSkill.countCharging < 10) {
                int tiLeHoiPhuc = SkillUtil.getPercentCharge(player.playerSkill.skillSelect.point);
                if (player.effectSkill.isCharging && !player.isDie() && !player.effectSkill.isHaveEffectSkill()
                        && (hp < hpMax || mp < mpMax)) {
                    PlayerService.gI().hoiPhuc(player, hpMax / 100 * tiLeHoiPhuc,
                            mpMax / 100 * tiLeHoiPhuc);
                    if (player.effectSkill.countCharging % 3 == 0) {
                        Service.gI().chat(player, "Phục hồi năng lượng " + getCurrPercentHP() + "%");
                    }
                } else {
                    EffectSkillService.gI().stopCharge(player);
                }
                if (++player.effectSkill.countCharging >= 10) {
                    EffectSkillService.gI().stopCharge(player);
                }
            }
            if (Util.canDoWithTime(lastTimeHoiPhuc, 30000)) {
                PlayerService.gI().hoiPhuc(this.player, hpHoi, mpHoi);
                this.lastTimeHoiPhuc = System.currentTimeMillis();
            }
            if (Util.canDoWithTime(lastTimeHoiStamina, 60000) && this.stamina < this.maxStamina) {
                this.stamina++;
                this.lastTimeHoiStamina = System.currentTimeMillis();
                if (!this.player.isBoss && !this.player.isPet) {
                    PlayerService.gI().sendCurrentStamina(this.player);
                }
            }
        }
        //hồi phục 30s
        //hồi phục thể lực
    }

    public void dispose() {
        this.intrinsic = null;
        this.player = null;
        this.tlHp = null;
        this.tlMp = null;
        this.tlDef = null;
        this.tlDame = null;
        this.tlDameAttMob = null;
        this.tlSDDep = null;
        this.tlTNSM = null;
        this.tlDameCrit = null;
        // this.tlSpeed = null;
    }
}
