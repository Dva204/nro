package com.girlkun.models.boss.list_boss.FideBack;

import com.girlkun.models.boss.Boss;
import com.girlkun.models.boss.BossID;
import com.girlkun.models.boss.BossManager;
import com.girlkun.models.boss.BossStatus;
import com.girlkun.models.boss.BossesData;
import com.girlkun.models.map.ItemMap;
import com.girlkun.models.player.Player;
import com.girlkun.server.Manager;
import com.girlkun.services.EffectSkillService;
import com.girlkun.services.PlayerService;
import com.girlkun.services.Service;
import com.girlkun.services.TaskService;
import com.girlkun.utils.Util;
import java.util.Random;

public class FideRobot extends Boss {

    public FideRobot() throws Exception {
        super(BossID.FIDE_ROBOT, BossesData.FIDE_ROBOT);
    }


    @Override
public void reward(Player plKill) {
    

    // Rơi ngẫu nhiên một item từ 925 đến 931
    int itemId = Util.nextInt(925, 931); // 932 vì nextInt là exclusive
    Service.gI().dropItemMap(this.zone, new ItemMap(this.zone, itemId, 1, this.location.x, this.location.y, plKill.id));

    // Boss chat khi rơi item
    this.chat("Haha, " + plKill.name + " nhận được vật phẩm bí ẩn từ ta rồi đó!");

    byte randomDo = (byte) new Random().nextInt(Manager.itemDC13.length);
        byte randomNR = (byte) new Random().nextInt(Manager.itemIds_NR_SB.length);
        int[] itemDos = new int[]{555, 556, 557, 558, 559, 560, 561, 562, 563, 564, 565, 566, 567};
        int randomc13 = new Random().nextInt(itemDos.length);
        if (Util.isTrue(BossManager.ratioReward, 100)) {
            if (Util.isTrue(60, 100)) {
                Service.gI().dropItemMap(this.zone, new ItemMap(zone, 1861, 5, this.location.x, this.location.y, plKill.id));
                return;
            }
            Service.gI().dropItemMap(this.zone, Util.ratiDTL(zone, Manager.itemDC13[randomDo], 1, this.location.x, this.location.y, plKill.id));
        } else if (Util.isTrue(10, 100)) {
            Service.gI().dropItemMap(this.zone, Util.ratiDTL(zone, itemDos[randomc13], 1, this.location.x, this.location.y, plKill.id));
            return;
        } else {
            Service.gI().dropItemMap(this.zone, new ItemMap(zone, Manager.itemIds_NR_SB[randomNR], 1, this.location.x, this.location.y, plKill.id));
        }
    
    
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
