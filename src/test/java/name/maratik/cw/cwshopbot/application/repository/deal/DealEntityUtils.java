//    cwshopbot
//    Copyright (C) 2019  Marat Bukharov.
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU Affero General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU Affero General Public License for more details.
//
//    You should have received a copy of the GNU Affero General Public License
//    along with this program.  If not, see <http://www.gnu.org/licenses/>.
package name.maratik.cw.cwshopbot.application.repository.deal;

import name.maratik.cw.cwshopbot.application.config.ClockHolder;
import name.maratik.cw.cwshopbot.entity.AccountEntity;
import name.maratik.cw.cwshopbot.entity.DealEntity;
import name.maratik.cw.cwshopbot.model.Castle;

import java.time.LocalDateTime;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public class DealEntityUtils {
    public static DealEntity createDealEntity(long sellerAccountId, long buyerAccountId, ClockHolder clockHolder) {
        return DealEntity.builder()
            .sellerAccountId(sellerAccountId)
            .buyerAccountId(buyerAccountId)
            .item("testItem")
            .qty(100)
            .price(200)
            .creationTime(LocalDateTime.now(clockHolder))
            .build();
    }

    public static AccountEntity createSellerAccount(ClockHolder clockHolder) {
        return AccountEntity.builder()
            .externalId("testSellerExternalId")
            .name("testSellerName")
            .castle(Castle.DAWN_CASTLE)
            .creationTime(LocalDateTime.now(clockHolder))
            .build();
    }

    public static AccountEntity createBuyerAccount(ClockHolder clockHolder) {
        return AccountEntity.builder()
            .externalId("testBuyerExternalId")
            .name("testBuyerName")
            .castle(Castle.DAWN_CASTLE)
            .creationTime(LocalDateTime.now(clockHolder))
            .build();
    }
}
