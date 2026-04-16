package com.girlkun.models.boss.list_boss.BLACK;

import com.girlkun.models.boss.*;
import com.girlkun.models.item.Item;
import com.girlkun.models.map.ItemMap;
import com.girlkun.models.player.Player;
import com.girlkun.server.Manager;
import com.girlkun.services.EffectSkillService;
import com.girlkun.services.Service;
import com.girlkun.utils.Util;
import java.util.Arrays;
import java.util.List;

import java.util.Random;

public class Black extends Boss {

    public Black() throws Exception {
        super(BossID.BLACK, BossesData.BLACK_GOKU);
    }

    @Override
    public void reward(Player plKill) {
        if (Util.isTrue(BossManager.ratioReward, 100)) {
            Service.gI().dropItemMap(this.zone, Util.khongthegiaodich(zone, 992, 1, this.location.x, this.location.y, plKill.id));
        }
        byte randomNR = (byte) new Random().nextInt(Manager.itemIds_NR_SB.length);
        int[] item_drop = {14, 15, 16, 17};
        ItemMap item = new ItemMap(zone, 1000, 1,this.location.x, this.location.y, plKill.id);
        item.options.add(new Item.ItemOption(50, 50));
        item.options.add(new Item.ItemOption(77, 50));
        item.options.add(new Item.ItemOption(103, 50));
        if (Util.isTrue(40, 100)) {
            Service.gI().dropItemMap(this.zone, new ItemMap(zone, item_drop[0], 1, this.location.x, this.location.y, plKill.id));
        } else {
            Service.gI().dropItemMap(this.zone, new ItemMap(zone, Manager.itemIds_NR_SB[1], 1, this.location.x, this.location.y, plKill.id));
        }
        //Service.gI().dropItemMap(this.zone, item);
//        ItemMap item = Service.g
//        if(Util.isTrue(10, 100)){
//            Service.gI().dropItemMap(zone, item);
//        }
    }

    @Override
    public void active() {
        super.active(); //To change body of generated methods, choose Tools | Templates.
//        if (Util.canDoWithTime(st, 1800000)) {
//            this.changeStatus(BossStatus.LEAVE_MAP);
//        }
    }

    @Override
    public double injured(Player plAtt, double damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            if (!piercing && Util.isTrue(this.nPoint.tlNeDon, 1000)) {
                this.chat("Xí hụt");
                return 0;
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

    @Override
    public void joinMap() {
        super.joinMap(); //To change body of generated methods, choose Tools | Templates.
        // st = System.current+TimeMillis();
    }

    private long st;

//    @Override
//    public void moveTo(int x, int y) {
//        if(this.currentLevel == 1){
//            return;
//        }
//        super.moveTo(x, y);
//    }
//
//    @Override
//    public void reward(Player plKill) {
//        if(this.currentLevel == 1){
//            return;
//        }
//        super.reward(plKill);
//    }
//    
//    @Override
//    protected void notifyJoinMap() {
//        if(this.currentLevel == 1){
//            return;
//        }
//        super.notifyJoinMap();
//    }
}
