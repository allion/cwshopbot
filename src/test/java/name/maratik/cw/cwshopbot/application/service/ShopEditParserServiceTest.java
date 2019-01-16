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
package name.maratik.cw.cwshopbot.application.service;

import name.maratik.cw.cwshopbot.mock.MockMessage;
import name.maratik.cw.cwshopbot.mock.MockMessageEntity;
import name.maratik.cw.cwshopbot.mock.MockedTest;
import name.maratik.cw.cwshopbot.model.ShopPublishStatus;
import name.maratik.cw.cwshopbot.model.cwasset.Assets;
import name.maratik.cw.cwshopbot.model.parser.ParsedShopEdit;

import com.google.common.collect.ImmutableList;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Message;

import static name.maratik.cw.cwshopbot.utils.SamePropertyValuesAsExcept.samePropertyValuesAsExcept;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public class ShopEditParserServiceTest extends MockedTest {
    @Autowired
    private CWParser<ParsedShopEdit> shopEditParser;

    @Autowired
    private Assets assets;

    @Test
    public void shouldParserShopEditMessage() {
        Message mockedMessage = new MockMessage("" +
            "Customize your Drunken Master Shack #56\n" +
            "/s_56_help - usage\n" +
            '\n' +
            "Offers 5/5\n" +
            "Steel mold, 15\uD83D\uDCA7 1\uD83D\uDCB0 /s_56_d_1\n" +
            "Silver mold, 15\uD83D\uDCA7 1\uD83D\uDCB0 /s_56_d_2\n" +
            "Coke, 10\uD83D\uDCA7 1\uD83D\uDCB0 /s_56_d_3\n" +
            "Rope, 10\uD83D\uDCA7 1\uD83D\uDCB0 /s_56_d_4\n" +
            "Steel arrows pack, 120\uD83D\uDCA7 1\uD83D\uDCB0 /s_56_d_5\n" +
            '\n' +
            "\uD83D\uDD14 Link: /ws_lBR02",
            ImmutableList.of(
                new MockMessageEntity("bold", 15, 24),
                new MockMessageEntity("bot_command", 40, 10),
                new MockMessageEntity("bold", 67, 3),
                new MockMessageEntity("bot_command", 92, 9),
                new MockMessageEntity("bot_command", 124, 9),
                new MockMessageEntity("bot_command", 149, 9),
                new MockMessageEntity("bot_command", 174, 9),
                new MockMessageEntity("bot_command", 213, 9),
                new MockMessageEntity("bot_command", 233, 9)
            )
        );
        ParsedShopEdit parsedShopEdit = shopEditParser.parse(mockedMessage)
            .orElseThrow(AssertionError::new);

        ParsedShopEdit expected = ParsedShopEdit.builder()
            .shopName("Drunken Master Shack")
            .shopNumber(56)
            .maxOffersCount(5)
            .offersCount(5)
            .shopCommand("/ws_lBR02")
            .shopPublishStatus(ShopPublishStatus.PUBLISH)
            .shopLine(ParsedShopEdit.ShopLine.builder()
                .item(assets.getCraftableItems().get("27"))
                .mana(15)
                .price(1)
                .build()
            ).shopLine(ParsedShopEdit.ShopLine.builder()
                .item(assets.getCraftableItems().get("28"))
                .mana(15)
                .price(1)
                .build()
            ).shopLine(ParsedShopEdit.ShopLine.builder()
                .item(assets.getCraftableItems().get("23"))
                .mana(10)
                .price(1)
                .build()
            ).shopLine(ParsedShopEdit.ShopLine.builder()
                .item(assets.getCraftableItems().get("31"))
                .mana(10)
                .price(1)
                .build()
            ).shopLine(ParsedShopEdit.ShopLine.builder()
                .item(assets.getCraftableItems().get("511"))
                .mana(120)
                .price(1)
                .build()
            ).build();

        assertThat(parsedShopEdit, samePropertyValuesAsExcept(expected, "shopLines"));
        assertThat(parsedShopEdit.getShopLines(), contains(expected.getShopLines().stream()
            .map(Matchers::samePropertyValuesAs)
            .collect(toImmutableList())
        ));
    }

    @Test
    public void shouldParserShopEditMessageWithHunterGloves() {
        Message mockedMessage = new MockMessage("" +
            "Customize your Drunken Master Shack #56\n" +
            "/s_56_help - usage\n" +
            '\n' +
            "Offers 4/5\n" +
            "String, 10\uD83D\uDCA7 1\uD83D\uDCB0 /s_56_d_1\n" +
            "Hunter Gloves, 150\uD83D\uDCA7 30\uD83D\uDCB0 /s_56_d_2\n" +
            "Silver mold, 15\uD83D\uDCA7 1\uD83D\uDCB0 /s_56_d_3\n" +
            "Hunter Boots, 150\uD83D\uDCA7 30\uD83D\uDCB0 /s_56_d_4\n" +
            '\n' +
            "\uD83D\uDD14 Link: /ws_lBR02",
            ImmutableList.of(
                new MockMessageEntity("bold", 15, 24),
                new MockMessageEntity("bot_command", 40, 10),
                new MockMessageEntity("bold", 67, 3),
                new MockMessageEntity("bot_command", 88, 9),
                new MockMessageEntity("bot_command", 124, 9),
                new MockMessageEntity("bot_command", 156, 9),
                new MockMessageEntity("bot_command", 191, 9),
                new MockMessageEntity("bot_command", 211, 9)
            )
        );
        ParsedShopEdit parsedShopEdit = shopEditParser.parse(mockedMessage)
            .orElseThrow(AssertionError::new);

        ParsedShopEdit expected = ParsedShopEdit.builder()
            .shopName("Drunken Master Shack")
            .shopNumber(56)
            .maxOffersCount(5)
            .offersCount(4)
            .shopCommand("/ws_lBR02")
            .shopPublishStatus(ShopPublishStatus.PUBLISH)
            .shopLine(ParsedShopEdit.ShopLine.builder()
                .item(assets.getCraftableItems().get("22"))
                .mana(10)
                .price(1)
                .build()
            ).shopLine(ParsedShopEdit.ShopLine.builder()
                .item(assets.getCraftableItems().get("a35"))
                .mana(150)
                .price(30)
                .build()
            ).shopLine(ParsedShopEdit.ShopLine.builder()
                .item(assets.getCraftableItems().get("28"))
                .mana(15)
                .price(1)
                .build()
            ).shopLine(ParsedShopEdit.ShopLine.builder()
                .item(assets.getCraftableItems().get("a34"))
                .mana(150)
                .price(30)
                .build()
            ).build();

        assertThat(parsedShopEdit, samePropertyValuesAsExcept(expected, "shopLines"));
        assertThat(parsedShopEdit.getShopLines(), contains(expected.getShopLines().stream()
            .map(Matchers::samePropertyValuesAs)
            .collect(toImmutableList())
        ));
    }
}
