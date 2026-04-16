package com.girlkun.models.boss.list_boss.Picolo;

import com.girlkun.models.boss.*;
import com.girlkun.models.item.Item;
import com.girlkun.models.map.ItemMap;
import com.girlkun.models.player.Player;
import com.girlkun.server.Manager;
import com.girlkun.services.EffectSkillService;
import com.girlkun.services.PlayerService;
import com.girlkun.services.Service;
import com.girlkun.services.TaskService;
import com.girlkun.utils.Util;
import java.util.Arrays;
import java.util.List;

import java.util.Random;

public class babymonkey extends Boss {

    public babymonkey() throws Exception {
        super(BossID.BABY, BossesData.BABY1,BossesData.BABY2);
    }

    @Override
    public void moveTo(int x, int y) {
        if (this.currentLevel == 1) {
            return;
        }
        super.moveTo(x, y);
    }

    @Override
public void reward(Player plKill) {
    super.reward(plKill);
    if (this.currentLevel == 1) {
        return;
    }

    // Rơi ngẫu nhiên một item từ 925 đến 931
    int itemId = Util.nextInt(925, 932); // 932 vì nextInt là exclusive
    Service.gI().dropItemMap(this.zone, new ItemMap(this.zone, itemId, 1, this.location.x, this.location.y, plKill.id));

    // Tỉ lệ rơi item 1980 (ví dụ: 5%)
//    if (Util.isTrue(5, 100)) {
//        Service.gI().dropItemMap(this.zone, new ItemMap(this.zone, 1980, 1, this.location.x, this.location.y, plKill.id));
//        this.chat("Ôi không! Vật phẩm hiếm đã bị rơi ra!");
//    }

    // Boss chat khi rơi item thường
    this.chat("Haha, " + plKill.name + " nhận được vật phẩm bí ẩn !");

    // Kiểm tra nhiệm vụ
    TaskService.gI().checkDoneTaskKillBoss(plKill, this);
}



    @Override
    protected void notifyJoinMap() {
        if (this.currentLevel == 1) {
            return;
        }
        super.notifyJoinMap();
    }

   // @Override
   // public void active() {
   //     super.active();
   // }
    
    @Override
    public void active() {
        super.active(); //To change body of generated methods, choose Tools | Templates.
        if (Util.canDoWithTime(st, 1800000)) {
            this.changeStatus(BossStatus.LEAVE_MAP);
        }
    }

    @Override
    public void joinMap() {
        super.joinMap(); //To change body of generated methods, choose Tools | Templates.
        st = System.currentTimeMillis();
    }
    private long st;
    @Override
    public double injured(Player plAtt, double damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            if (!piercing && Util.isTrue(this.nPoint.tlNeDon, 1)) {
                this.chat("Xí hụt");
                return 0;
            }

            if(Util.isTrue(1, 100)){
                int hpHoi = (int) ((long) damage);
                PlayerService.gI().hoiPhuc(this, hpHoi, 0);
                if (Util.isTrue(5, 5)) {
                    this.chat("Nảy Nảy ... Ngươi không hạ gục được ta đâu! ");
                    }

            }            
            damage = this.nPoint.subDameInjureWithDeff(damage / 5);
            if (!piercing && effectSkill.isShielding) {
                if (damage > nPoint.hpMax) {
                    EffectSkillService.gI().breakShield(this);
                }
                damage = 1;
            }
            this.nPoint.subHP(damage);
            if (isDie()) {
                this.setDie(plAtt);
                die(plAtt);
            }
            return damage;
        } else {
            return 0;
        }
    }
}
