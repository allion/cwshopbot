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
package name.maratik.cw.cwshopbot.application.repository.yellow.page.offer;

import name.maratik.cw.cwshopbot.application.repository.yellow.page.YellowPageRepository;
import name.maratik.cw.cwshopbot.entity.YellowPageEntity;
import name.maratik.cw.cwshopbot.entity.YellowPageOfferEntity;
import name.maratik.cw.cwshopbot.mock.MockedTest;

import com.google.common.collect.ImmutableList;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

import static name.maratik.cw.cwshopbot.application.repository.yellow.page.YellowPageEntityUtils.createYellowPageEntity;
import static name.maratik.cw.cwshopbot.application.repository.yellow.page.YellowPageEntityUtils.createYellowPageOfferEntity;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public class YellowPageOfferRepositoryTest extends MockedTest {
    @Autowired
    private YellowPageOfferRepository yellowPageOfferRepository;

    @Autowired
    private YellowPageRepository yellowPageRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Test
    public void shouldSaveAndGetYellowPageOffer() {
        transactionTemplate.execute(status -> {
            YellowPageEntity yellowPageEntity = createYellowPageEntity().build();
            yellowPageRepository.save(yellowPageEntity);

            YellowPageOfferEntity yellowPageOfferEntity = createYellowPageOfferEntity().build();
            yellowPageOfferRepository.save(yellowPageOfferEntity);

            List<YellowPageOfferEntity.Content> fetchedYellowPageOfferEntities = yellowPageOfferRepository.findByYellowPage(yellowPageEntity.getLink())
                .collect(toImmutableList());

            assertThat(fetchedYellowPageOfferEntities, contains(offerMatches(yellowPageOfferEntity)));

            return null;
        });
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldBatchedSaveAndGetYellowPageOffer() {
        transactionTemplate.execute(status -> {
            YellowPageEntity yellowPageEntity = createYellowPageEntity().build();
            yellowPageRepository.save(yellowPageEntity);

            YellowPageOfferEntity yellowPageOfferEntity = createYellowPageOfferEntity().build();
            YellowPageOfferEntity anotherYellowPageOfferEntity = yellowPageOfferEntity.toBuilder()
                .item(yellowPageOfferEntity.getItem() + '1')
                .build();
            yellowPageOfferRepository.saveAll(ImmutableList.of(yellowPageOfferEntity, anotherYellowPageOfferEntity));

            List<YellowPageOfferEntity.Content> fetchedYellowPageOfferContents = yellowPageOfferRepository.findByYellowPage(yellowPageEntity.getLink())
                .collect(toImmutableList());

            assertThat(fetchedYellowPageOfferContents, containsInAnyOrder(
                offerMatches(yellowPageOfferEntity),
                offerMatches(anotherYellowPageOfferEntity)
            ));

            return null;
        });
    }

    @Test
    public void shouldUpdateYellowPageOffer() {
        transactionTemplate.execute(status -> {
            YellowPageEntity yellowPageEntity = createYellowPageEntity().build();
            yellowPageRepository.save(yellowPageEntity);

            YellowPageOfferEntity yellowPageOfferEntity = createYellowPageOfferEntity()
                .active(true)
                .build();
            yellowPageOfferRepository.save(yellowPageOfferEntity);
            YellowPageOfferEntity.Content yellowPageOfferContent = yellowPageOfferRepository.findByYellowPage(yellowPageEntity.getLink())
                .findAny()
                .orElseThrow(() -> new AssertionError("Could not fetch yellow_page_offer"));
            assertTrue(yellowPageOfferContent.isActive());

            yellowPageOfferEntity = yellowPageOfferEntity.toBuilder()
                .active(false)
                .build();
            yellowPageOfferRepository.save(yellowPageOfferEntity);
            yellowPageOfferContent = yellowPageOfferRepository.findByYellowPage(yellowPageEntity.getLink())
                .findAny()
                .orElseThrow(() -> new AssertionError("Could not fetch yellow_page_offer"));
            assertFalse(yellowPageOfferContent.isActive());

            return null;
        });
    }

    @Test
    public void shouldInactiveYellowPageOffer() {
        transactionTemplate.execute(status -> {
            YellowPageEntity yellowPageEntity = createYellowPageEntity().build();
            yellowPageRepository.save(yellowPageEntity);

            YellowPageOfferEntity yellowPageOfferEntity = createYellowPageOfferEntity()
                .active(true)
                .build();
            yellowPageOfferRepository.save(yellowPageOfferEntity);
            YellowPageOfferEntity.Content yellowPageOfferContent = yellowPageOfferRepository.findByYellowPage(yellowPageEntity.getLink())
                .findAny()
                .orElseThrow(() -> new AssertionError("Could not fetch yellow_page_offer"));
            assertTrue(yellowPageOfferContent.isActive());

            yellowPageOfferRepository.setAllInactive();
            yellowPageOfferContent = yellowPageOfferRepository.findByYellowPage(yellowPageEntity.getLink())
                .findAny()
                .orElseThrow(() -> new AssertionError("Could not fetch yellow_page_offer"));
            assertFalse(yellowPageOfferContent.isActive());

            return null;
        });
    }

    private static Matcher<YellowPageOfferEntity.Content> offerMatches(YellowPageOfferEntity yellowPageOfferEntity) {
        return allOf(
            hasProperty("item", equalTo(yellowPageOfferEntity.getItem())),
            hasProperty("price", equalTo(yellowPageOfferEntity.getPrice())),
            hasProperty("mana", equalTo(yellowPageOfferEntity.getMana())),
            hasProperty("active", equalTo(yellowPageOfferEntity.isActive()))
        );
    }
}