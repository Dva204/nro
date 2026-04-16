/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.girlkun.models.boss.list_boss.NgucTu;

import com.girlkun.models.boss.Boss;
import com.girlkun.models.boss.BossID;
import com.girlkun.models.boss.BossManager;
import com.girlkun.models.boss.BossStatus;
import com.girlkun.models.boss.BossesData;
import com.girlkun.models.map.ItemMap;
import com.girlkun.models.player.Player;
import com.girlkun.models.skill.Skill;
import com.girlkun.server.Manager;
import com.girlkun.services.EffectSkillService;
import com.girlkun.services.PetService;
import com.girlkun.services.PlayerService;
import com.girlkun.services.Service;
import com.girlkun.utils.Util;
import java.util.Random;

/**
 *
 * @Stole By obito ZALO 0866468126
 */
public class Cumber extends Boss {

    public Cumber() throws Exception {
        super(BossID.CUMBER, BossesData.CUMBER);
    }

    @Override
    public void reward(Player plKill) {
        // Rơi item chính: 1105 và 1998
        Service.gI().dropItemMap(this.zone, new ItemMap(this.zone, 1105, 1, this.location.x, this.location.y, plKill.id));//ộp đồ skh thiên sứ
        Service.gI().dropItemMap(this.zone, new ItemMap(this.zone, 1998, 1, this.location.x + 30, this.location.y, plKill.id));

        // Boss chat
        this.chat("Haha, " + plKill.name + " nhận được vật phẩm bí ẩn từ ta rồi đó!");

        // Rơi item phụ theo kiểu rải đều
        int[] itemPhu = new int[]{457, 674, 1862, 1395}; // Thêm các item khác tại đây
        int a = 0;
        for (int itemId : itemPhu) {
            for (int i = 0; i < 50; i++) { // Mỗi loại rơi 5 cái
                ItemMap it = new ItemMap(this.zone, itemId, 1, this.location.x + a,
                        this.zone.map.yPhysicInTop(this.location.x + a, this.location.y - 24), -1);
                Service.getInstance().dropItemMap(this.zone, it);
                a += 30;
            }
        }
    }

    @Override
    public double injured(Player plAtt, double damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            if (!piercing && Util.isTrue(this.nPoint.tlNeDon, 1)) {
                this.chat("Xí hụt");
                return 0;
            }

            //   if(Util.isTrue(20, 100)){
            //       int hpHoi = (int) ((long) damage);
            //       PlayerService.gI().hoiPhuc(this, hpHoi, 0);
            //       if (Util.isTrue(5, 5)) {
            //           this.chat("Nảy Nảy ... Ngươi không hạ gục được ta đâu! ");
            //           }
            //   }            
            damage = this.nPoint.subDameInjureWithDeff(damage);
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

    // Thêm vào cả 2 file: Cumber.java và SongokuTaAc.java
    @Override
    public void joinMap() {
        super.joinMap();
        st = System.currentTimeMillis();
    }
    private long st;

    @Override
    public void active() {
        super.active();
        if (Util.canDoWithTime(st, 1800000)) {
            this.changeStatus(BossStatus.LEAVE_MAP);
        }
    }

    @Override
    public void leaveMap() {
        super.leaveMap();
        super.dispose();
        BossManager.gI().removeBoss(this);
    }

}
