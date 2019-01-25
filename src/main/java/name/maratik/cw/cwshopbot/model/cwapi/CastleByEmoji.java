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
package name.maratik.cw.cwshopbot.model.cwapi;

import name.maratik.cw.cwshopbot.model.Castle;
import name.maratik.cw.cwshopbot.util.EnumWithCode;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import static com.google.common.collect.Maps.toImmutableEnumMap;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@RequiredArgsConstructor
public enum CastleByEmoji implements EnumWithCode {
    MOONLIGHT(Castle.MOONLIGHT),
    WOLFPACK(Castle.WOLFPACK),
    POTATO(Castle.POTATO),
    SHARKTEETH(Castle.SHARKTEETH),
    HIGHNEST(Castle.HIGHNEST),
    DEERHORN(Castle.DEERHORN),
    DRAGONSCALE(Castle.DRAGONSCALE),
    TORTUGA(Castle.TORTUGA),
    NIGHT_CASTLE(Castle.NIGHT_CASTLE),
    AMBER(Castle.AMBER),
    STRONGHOLD(Castle.STRONGHOLD),
    CLIFF(Castle.CLIFF),
    DAWN_CASTLE(Castle.DAWN_CASTLE),
    FARM(Castle.FARM),
    URN(Castle.URN);

    @Getter
    private final Castle castle;
    private static final Map<String, CastleByEmoji> cache = Util.createCache(values());
    private static final Map<Castle, CastleByEmoji> castleCache = Arrays.stream(values())
        .collect(toImmutableEnumMap(
            CastleByEmoji::getCastle,
            t -> t
        ));

    @JsonValue
    @Override
    public String getCode() {
        return castle.getEmoji();
    }

    public static Optional<CastleByEmoji> findByCode(String code) {
        return Optional.ofNullable(cache.get(code));
    }

    public static CastleByEmoji findByCastle(Castle castle) {
        return castleCache.get(castle);
    }
}
