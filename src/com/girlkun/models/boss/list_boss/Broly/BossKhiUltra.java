package com.girlkun.models.boss.list_boss.Broly;

import com.girlkun.consts.ConstPlayer;
import com.girlkun.models.boss.list_boss.android.*;
import com.girlkun.models.boss.Boss;
import com.girlkun.models.boss.BossID;
import com.girlkun.models.boss.BossManager;
import com.girlkun.models.boss.BossStatus;
import com.girlkun.models.boss.BossesData;
import com.girlkun.models.item.Item;
import com.girlkun.models.map.ItemMap;
import com.girlkun.models.player.Player;
import com.girlkun.models.skill.Skill;
import com.girlkun.server.Client;
import com.girlkun.server.ServerNotify;
import com.girlkun.services.EffectSkillService;
import com.girlkun.services.InventoryServiceNew;
import com.girlkun.services.ItemService;
import com.girlkun.services.PlayerService;
import com.girlkun.services.Service;
import com.girlkun.services.SkillService;
import com.girlkun.services.TaskService;
import com.girlkun.utils.SkillUtil;
import com.girlkun.utils.Util;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class BossKhiUltra extends Boss {

    private long lastTimeHapThu;
    // Cooldown hấp thụ: random 7~15 giây
    private int timeHapThu = Util.nextInt(7000, 15000);

    public BossKhiUltra() throws Exception {
        super(BossID.BOSS_KHI_ULTRA, BossesData.BOSS_KHI_ULTRA);
    }

    public List<Player> PlayerPlAtt = new ArrayList<>();

    public void addPlayerPlAtt(Player pl) {
        if (!this.PlayerPlAtt.contains(pl) && pl.isPl()) {
            PlayerPlAtt.add(pl);
        }
    }

    // Trả về giờ hiện tại
    public int gethour() {
        return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    }

    // Kiểm tra có trong giờ sự kiện không (19h - 22h)
    private boolean isEventTime() {
        int h = gethour();
        return h >= 19 && h <= 22;
    }

    public String df(double sfm) {
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(sfm);
    }

    public void reward(Player plKill) {
        if (!isEventTime()) {
            this.PlayerPlAtt.clear();
            return;
        }

        // Thưởng cho người kết liễu boss
        if (plKill != null) {
            plKill.inventory.ruby += 20000;
            Service.gI().sendMoney(plKill);

            if (plKill.DucNTdamethanmeo > 500_000_000) {
                plKill.DucNTdamethanmeo = 500_000_000;
            }

            Item KHXD = new Item();
            KHXD.template = ItemService.gI().getTemplate(2002);
            KHXD.quantity = 1;
            InventoryServiceNew.gI().addItemBag(plKill, KHXD);
            InventoryServiceNew.gI().sendItemBags(plKill);
            Service.gI().sendThongBaoOK(plKill,
                    "\nBạn nhận được 20K Hồng ngọc và 1 Hộp Trang Bị Kích Hoạt Xayda do kết liễu Boss.");
            plKill.DucNTdamethanmeo = 0;
        }

        // Thưởng cho tất cả người tham chiến theo % dame gây ra
        // Dùng Iterator để tránh ConcurrentModificationException
        Iterator<Player> iterator = this.PlayerPlAtt.iterator();
        while (iterator.hasNext()) {
            Player pl = iterator.next();
            iterator.remove();

            if (pl == null || pl.zone == null || pl.zone.map.mapId != 13) {
                continue;
            }
            if (pl.DucNTdamethanmeo <= 0) {
                continue;
            }

            if (pl.DucNTdamethanmeo > 500_000_000) {
                pl.DucNTdamethanmeo = 500_000_000;
            }

            // % HP boss mà player đã gây ra (tính trên 500tr là max)
            double ptnhanqua = pl.DucNTdamethanmeo * 100.0 / 500_000_000;
            ptnhanqua = Math.max(0, Math.min(ptnhanqua, 3)); // Giới hạn 0% ~ 3%

            if (ptnhanqua >= 3) {
                // Đạt ngưỡng cao: thưởng Thỏi Vàng
                Item tv = new Item();
                tv.template = ItemService.gI().getTemplate(457);
                tv.quantity = (int) (ptnhanqua * 10); // tối đa 30 thỏi
                InventoryServiceNew.gI().addItemBag(pl, tv);
                InventoryServiceNew.gI().sendItemBags(pl);
                Service.gI().sendThongBaoOK(pl,
                        "Bạn nhận được " + tv.quantity + " Thỏi Vàng thưởng tham gia sự kiện!");
            } else {
                // Dưới ngưỡng: thưởng Hồng Ngọc
                pl.inventory.ruby += 20000;
                Service.gI().sendMoney(pl);
                Service.gI().sendThongBaoOK(pl,
                        "Bạn đã gây " + this.df(ptnhanqua) + "% HP boss\n"
                        + "Vì dưới 3% bạn nhận được: 20K Hồng ngọc.");
            }

            pl.DucNTdamethanmeo = 0;
        }

        // Reset dame của tất cả player còn lại (không tham chiến hoặc không ở map)
        Client.gI().getPlayers().forEach(player -> {
            if (player != null && player.DucNTdamethanmeo > 0) {
                player.DucNTdamethanmeo = 0;
            }
        });

        this.PlayerPlAtt.clear();
    }

    @Override
    public double injured(Player plAtt, double damage, boolean piercing, boolean isMobAttack) {
        // Ngoài giờ sự kiện: không nhận sát thương
        if (!isEventTime()) {
            return 0;
        }

        if (this.isDie()) {
            return 0;
        }

        // Một số kỹ năng bị hấp thụ (boss hồi máu thay vì nhận dame)
        if (plAtt != null
                && plAtt.playerSkill != null
                && plAtt.playerSkill.skillSelect != null
                && plAtt.playerSkill.skillSelect.template != null) {

            switch (plAtt.playerSkill.skillSelect.template.id) {
                case Skill.KAMEJOKO:
                case Skill.MASENKO:
                case Skill.ANTOMIC:
                    int hpHoi = (int) ((long) damage * 80 / 100);
                    PlayerService.gI().hoiPhuc(this, hpHoi, 0);
                    if (Util.isTrue(1, 5)) {
                        this.chat("Hấp thụ.. các ngươi nghĩ sao vậy?");
                    }
                    return 0;
            }
        }

        // Lọc dame quá nhỏ
        if (damage < 5000) {
            return 0;
        }

        // Giới hạn dame tối đa 1 tỷ / hit
        if (damage > 1_000_000_000) {
            damage = 1_000_000_000;
        }

        // Tính dame thực tế sau giảm trừ (chia 5 rồi tính def)
        damage = this.nPoint.subDameInjureWithDeff(damage / 5);

        // Xử lý khiên
        if (!piercing && effectSkill.isShielding) {
            if (damage > nPoint.hpMax) {
                EffectSkillService.gI().breakShield(this);
            }
            damage = 1;
        }

        // Tích lũy dame cho player
        if (plAtt != null && plAtt.isPl()) {
            this.addPlayerPlAtt(plAtt);
            plAtt.DucNTdamethanmeo += damage;
        }

        // Trừ HP boss
        this.nPoint.subHP(damage);

        // Boss chết
        if (isDie()) {
            this.setDie(this);
            reward(plAtt);
            if (plAtt != null) {
                ServerNotify.gI().notify(plAtt.name + " vừa tiêu diệt được " + this.name
                        + " nhận được 20K Hồng ngọc");
            }
            this.changeStatus(BossStatus.DIE);
        }

        return damage;
    }

    @Override
    public void active() {
        if (this.typePk == ConstPlayer.NON_PK) {
            this.changeToTypePK();
        }
        this.attack();
        this.hapThu();
    }

    @Override
    public void joinMap() {
        super.joinMap();
    }

    @Override
    public void attack() {
        if (!isEventTime()) return;

        if (Util.canDoWithTime(this.lastTimeAttack, 100)
                && this.typePk == ConstPlayer.PK_ALL) {
            this.lastTimeAttack = System.currentTimeMillis();
            try {
                Player pl = getPlayerAttack();
                if (pl == null || pl.isDie()) {
                    return;
                }
                this.playerSkill.skillSelect = this.playerSkill.skills
                        .get(Util.nextInt(0, this.playerSkill.skills.size() - 1));

                if (Util.getDistance(this, pl) <= this.getRangeCanAttackWithSkillSelect()) {
                    if (Util.isTrue(5, 20)) {
                        if (SkillUtil.isUseSkillChuong(this)) {
                            this.moveTo(
                                    pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(20, 200)),
                                    Util.nextInt(10) % 2 == 0 ? pl.location.y : pl.location.y - Util.nextInt(0, 70));
                        } else {
                            this.moveTo(
                                    pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(10, 40)),
                                    Util.nextInt(10) % 2 == 0 ? pl.location.y : pl.location.y - Util.nextInt(0, 50));
                        }
                    }
                    SkillService.gI().useSkillboss(this, pl, null);
                    checkPlayerDie(pl);
                } else {
                    if (Util.isTrue(1, 2)) {
                        this.moveToPlayer(pl);
                    }
                }
            } catch (Exception ex) {
                // ignored
            }
        }
    }

    private void hapThu() {
        // Kiểm tra cooldown hấp thụ
        if (!Util.canDoWithTime(this.lastTimeHapThu, this.timeHapThu)) {
            return;
        }

        // Xác suất 5% mỗi lần active() gọi
        if (!Util.isTrue(5, 100)) {
            return;
        }

        Player pl = this.zone.getRandomPlayerInMap();
        if (pl == null || pl.isDie()) {
            return;
        }

        // Boss hút HP + tăng chỉ số từ player
        this.nPoint.dameg += (pl.nPoint.dame * 5 / 100);
        this.nPoint.hpg   += (pl.nPoint.hp  * 10 / 100);
        this.nPoint.critg++;
        this.nPoint.calPoint();

        // Hồi HP boss bằng 80% HP hiện tại của player
        int hpHoi = (int) ((long) pl.nPoint.hp * 80 / 100);
        PlayerService.gI().hoiPhuc(this, hpHoi, 0);

        // Giết player bị nuốt
        pl.injured(null, pl.nPoint.hpMax, true, false);

        // Thông báo
        Service.gI().sendThongBao(pl, "Bạn vừa bị " + this.name + " nuốt chửng!");
        this.chat(2, "Ui cha cha, kinh dị quá. " + pl.name + " vừa bị tên " + this.name + " nuốt chửng kìa!!!");
        this.chat("Haha, ngọt lắm đấy " + pl.name + "..");

        // Reset cooldown và random lại thời gian hấp thụ tiếp theo (7~15 giây)
        this.lastTimeHapThu = System.currentTimeMillis();
        this.timeHapThu = Util.nextInt(7000, 15000);
    }
}