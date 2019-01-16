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
package name.maratik.cw.cwshopbot.model.cwasset;

import name.maratik.cw.cwshopbot.util.EnumWithCode;
import name.maratik.spring.telegram.util.LocalizableValue;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Optional;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@RequiredArgsConstructor
public enum ItemLocation implements EnumWithCode, LocalizableValue {
    STOCK("stock", "ItemLocation.RESOURCES"),
    CRAFTING("crafting", "ItemLocation.CRAFTING"),
    MISC("misc", "ItemLocation.MISC"),
    EQUIPMENT("equipment", "ItemLocation.EQUIPMENT");

    @Getter(onMethod_ = {@JsonValue})
    private final String code;
    @Getter
    private final String translationTag;
    private static final Map<String, ItemLocation> cache = Util.createCache(values());

    @JsonCreator
    public static Optional<ItemLocation> findByCode(String code) {
        return Optional.ofNullable(cache.get(code));
    }
}
