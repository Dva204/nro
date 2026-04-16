package com.girlkun.card;

import com.girlkun.models.player.Player;
import com.girlkun.network.io.Message;
import java.util.ArrayList;
import java.util.List;
import com.girlkun.services.Service;
import com.girlkun.utils.Logger;

/**
 *
 * @author Dev obito ZALO 0866468126
 */
public class RadarService {

    public List<RadarCard> RADAR_TEMPLATE = new ArrayList<>();

    private static RadarService instance;

    public static RadarService gI() {
        if (instance == null) {
            instance = new RadarService();
        }
        return instance;
    }

    public void sendRadar(Player pl, List<Card> cards) {
        try {
            Message m = new Message(127);
            m.writer().writeByte(0);
            m.writer().writeShort(RadarService.gI().RADAR_TEMPLATE.size());
            for (RadarCard radar : RadarService.gI().RADAR_TEMPLATE) {
                Card card = cards.stream().filter(c -> c.Id == radar.Id).findFirst().orElse(null);
                if (card == null) {
                    card = new Card(radar.Max, radar.Options);
                }
                m.writer().writeShort(radar.Id);
                m.writer().writeShort(radar.IconId);
                m.writer().writeByte(radar.Rank);
                m.writer().writeByte(card.Amount);  //amount
                m.writer().writeByte(card.MaxAmount);  //max_amount
                m.writer().writeByte(radar.Type);  //type 0: monster, 1: charpart
                switch (radar.Type) {
                    case 0:
                        m.writer().writeShort(radar.Template); //Monster
                        break;
                    case 1:
                        m.writer().writeShort(radar.Head); //Head
                        m.writer().writeShort(radar.Body); //Body
                        m.writer().writeShort(radar.Leg); //Leg
                        m.writer().writeShort(radar.Bag); //bag
                        break;
                }
                m.writer().writeUTF(radar.Name);  //name
                m.writer().writeUTF(radar.Info);  //info
                m.writer().writeByte(card.Level);  //LEvel
                m.writer().writeByte(card.Used);  //use
                m.writer().writeByte(radar.Options.size());  //option radar
                for (OptionCard option : radar.Options) {
                    m.writer().writeByte(option.id);  //id
                    m.writer().writeShort(option.param);  //param
                    m.writer().writeByte(option.active);  //ActiveCard
                }
            }
            m.writer().flush();
            pl.sendMessage(m);
            m.cleanup();
        } catch (Exception e) {
        }
    }

    public void Radar1(Player pl, short id, int use) {
        try {
            // 1. Tìm thẻ người chơi muốn bật/tắt
            Card cardToUpdate = pl.Cards.stream().filter(c -> c.Id == id).findFirst().orElse(null);
            if (cardToUpdate == null) {
                return; // Người chơi không sở hữu thẻ này
            }

            // 2. Xử lý logic bật/tắt
            if (use == 1) { // --- TRƯỜNG HỢP BẬT THẺ ---
                // Tắt tất cả các thẻ khác đang được sử dụng
                Card cardUnused = pl.Cards.stream().filter(c -> c.Used == 1).findFirst().orElse(null);
                if (cardUnused != null) {
                    cardUnused.Used = 0;
                    // Gửi tin nhắn cho client biết thẻ cũ đã bị tắt
                    Message mUnuse = new Message(127);
                    mUnuse.writer().writeByte(1);
                    mUnuse.writer().writeShort(cardUnused.Id);
                    mUnuse.writer().writeByte(0); // 0 = tắt
                    pl.sendMessage(mUnuse);
                    mUnuse.cleanup();
                }
                // Bật thẻ mới
                cardToUpdate.Used = 1;
            } else { // --- TRƯỜNG HỢP TẮT THẺ ---
                cardToUpdate.Used = 0;
            }

            // 3. Gửi tin nhắn xác nhận cho client
            Message mUse = new Message(127);
            mUse.writer().writeByte(1);
            mUse.writer().writeShort(id);
            mUse.writer().writeByte(use); // Gửi lại trạng thái mới (0 hoặc 1)
            pl.sendMessage(mUse);
            mUse.cleanup();

            // 4. TÍNH TOÁN LẠI CHỈ SỐ VÀ CẬP NHẬT NGOẠI HÌNH (RẤT QUAN TRỌNG)
            pl.nPoint.calPoint(); // Tính lại toàn bộ chỉ số (từ NPoint.java)
            Service.gI().player(pl); // Cập nhật lại ngoại hình (để hiển thị aura)
            Service.gI().Send_Info_NV(pl); // Gửi thông tin HP, MP... mới cho client

        } catch (Exception e) {
            Logger.error("Lỗi tại Radar1: " + e.getMessage());
        }
    }

    public void RadarSetLevel(Player pl, int id, int level) {
        try {
            Message message = new Message(127);
            message.writer().writeByte(2);
            message.writer().writeShort(id);
            message.writer().writeByte(level);
            message.writer().flush();
            pl.sendMessage(message);
            message.cleanup();
        } catch (Exception e) {
        }
    }

    public void RadarSetAmount(Player pl, int id, int amount, int max_amount) {
        try {
            Message message = new Message(127);
            message.writer().writeByte(3);
            message.writer().writeShort(id);
            message.writer().writeByte(amount);
            message.writer().writeByte(max_amount);
            message.writer().flush();
            pl.sendMessage(message);
            message.cleanup();
        } catch (Exception e) {
        }
    }
}
