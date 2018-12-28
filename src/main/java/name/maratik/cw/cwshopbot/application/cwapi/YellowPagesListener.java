//    cwshopbot
//    Copyright (C) 2018  Marat Bukharov.
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
package name.maratik.cw.cwshopbot.application.cwapi;

import name.maratik.cw.cwshopbot.application.service.YellowPagesService;
import name.maratik.cw.cwshopbot.model.cwapi.YellowPages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@Component
public class YellowPagesListener {
    private static final Logger logger = LogManager.getLogger(YellowPagesListener.class);
    private final YellowPagesService yellowPagesService;

    public YellowPagesListener(YellowPagesService yellowPagesService) {
        this.yellowPagesService = yellowPagesService;
    }

    @RabbitListener(queues = "${spring.rabbitmq.username}_yellow_pages")
    public void processYellowPagesAnnounce(List<YellowPages> data) {
        logger.debug("Received next: {}", data);
        yellowPagesService.storeYellowPages(data);
    }
}
