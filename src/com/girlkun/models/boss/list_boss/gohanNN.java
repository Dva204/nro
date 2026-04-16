/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.girlkun.models.boss.list_boss;

import com.girlkun.models.boss.Boss;
import com.girlkun.models.boss.BossID;
import com.girlkun.models.boss.BossManager;
import com.girlkun.models.boss.BossStatus;
import com.girlkun.models.boss.BossesData;
import com.girlkun.models.map.ItemMap;
import com.girlkun.models.player.Player;
import com.girlkun.server.Manager;
import com.girlkun.services.EffectSkillService;
import com.girlkun.services.Service;
import com.girlkun.utils.Util;
import java.util.Random;

/**
 * @@Stole By obito ZALO 0866468126
 */
public class gohanNN extends Boss {

    public gohanNN() throws Exception {
        super(BossID.GOHAN_NN, BossesData.GOHAN_NHAT_NGUYET);
    }

    @Override
    public void reward(Player plKill) {

        byte randomDo = (byte) new Random().nextInt(Manager.itemDC13.length);
        byte randomNR = (byte) new Random().nextInt(Manager.itemIds_NR_SB.length);
        int[] itemDos = new int[]{555, 556, 557, 558, 559, 560, 561, 562, 563, 564, 565, 566, 567};
        int randomc13 = new Random().nextInt(itemDos.length);
        if (Util.isTrue(BossManager.ratioReward, 100)) {
            if (Util.isTrue(95, 100)) {
                Service.gI().dropItemMap(this.zone, new ItemMap(zone, 1861, 5, this.location.x, this.location.y, plKill.id));
                return;
            }
            Service.gI().dropItemMap(this.zone, Util.ratiDTL(zone, Manager.itemDC13[randomDo], 1, this.location.x, this.location.y, plKill.id));
        } else if (Util.isTrue(10,100)) {
            Service.gI().dropItemMap(this.zone, Util.ratiDTL(zone, itemDos[randomc13], 1, this.location.x, this.location.y, plKill.id));
            return;
        } else {
            Service.gI().dropItemMap(this.zone, new ItemMap(zone, Manager.itemIds_NR_SB[randomNR], 1, this.location.x, this.location.y, plKill.id));
        }
        
        Service.gI().dropItemMap(this.zone, Util.ratiItem(zone, 16, Util.nextInt(1, 2), this.location.x, this.location.y, plKill.id));
        return;

    }

    @Override
    public double injured(Player plAtt, double damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            return Util.nextInt(700, 1000);
        } else {
            return 0;
        }
    }

    @Override
    public void active() {
        super.active(); //To change body of generated methods, choose Tools | Templates.
        //    if (Util.canDoWithTime(st, 900000)) {
        //       this.changeStatus(BossStatus.LEAVE_MAP);
        //   }
    }

    @Override
    public void joinMap() {
        super.joinMap(); //To change body of generated methods, choose Tools | Templates.
        st = System.currentTimeMillis();
    }
    private long st;

}
