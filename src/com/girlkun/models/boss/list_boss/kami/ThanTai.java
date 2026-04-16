/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.girlkun.models.boss.list_boss.kami;

import com.girlkun.consts.ConstPlayer;
import com.girlkun.models.boss.Boss;
import com.girlkun.models.boss.BossID;
import com.girlkun.models.boss.BossManager;
import com.girlkun.models.boss.BossStatus;
import com.girlkun.models.boss.BossesData;
import com.girlkun.models.boss.list_boss.kami.kamiSooMe;
import com.girlkun.models.map.ItemMap;
import com.girlkun.models.player.Player;
import com.girlkun.server.Manager;
import com.girlkun.services.EffectSkillService;
import com.girlkun.services.PlayerService;
import com.girlkun.services.Service;
import com.girlkun.utils.Util;
import java.util.Random;
import com.girlkun.models.item.Item.ItemOption;
import static com.girlkun.utils.Util.isTrue;

public class ThanTai extends Boss {

    public ThanTai() throws Exception {
        super(BossID.KAMILOC, BossesData.KAMILOC);
    }

    @Override
    public void reward(Player plKill) {
        int[] itemIds = {1825, 1826, 1827};
        int[] optionIds = {77, 103, 50, 14, 98, 99, 5}; // đầy đủ option, có 2 option 98

        // Chọn ngẫu nhiên 1 item trong danh sách
        int itemId = itemIds[Util.nextInt(0, itemIds.length - 1)];

        // Tạo item rơi
        ItemMap itemMap = new ItemMap(
                this.zone, itemId, 1,
                this.location.x,
                this.zone.map.yPhysicInTop(this.location.x, this.location.y - 24),
                plKill.id
        );

        // Gán đầy đủ các option với value ngẫu nhiên 10-25
        for (int optionId : optionIds) {
            int optionValue = Util.nextInt(8, 15);
            itemMap.options.add(new ItemOption(optionId, optionValue));
        }
        //itemMap.options.add(new ItemOption(30, 0));
        if (isTrue(75, 100)) {
            itemMap.options.add(new ItemOption(93, Util.nextInt(5, 8)));
        }
        // Drop ra map
        Service.gI().dropItemMap(this.zone, itemMap);
    }

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

}
