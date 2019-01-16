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
package name.maratik.cw.cwshopbot.application.repository.yellow.page.specialization;

import name.maratik.cw.cwshopbot.entity.YellowPageSpecializationEntity;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.Repository;

import java.util.stream.Stream;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
public interface YellowPageSpecializationRepository extends Repository<YellowPageSpecializationEntity, Void>, CustomizedYellowPageSpecializationRepository {
    @Query("" +
        "SELECT specialization, value" +
        "  FROM yellow_page_specialization" +
        " WHERE yellow_page = :yellowPage")
    Stream<YellowPageSpecializationEntity.Content> findByYellowPage(String yellowPage);

    @Query("" +
        "UPDATE yellow_page_specialization" +
        "   SET value = 0")
    @Modifying
    void zeroAllValues();
}