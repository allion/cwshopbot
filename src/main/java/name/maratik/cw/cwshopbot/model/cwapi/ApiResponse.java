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

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * @author <a href="mailto:maratik@yandex-team.ru">Marat Bukharov</a>
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "action"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = CreateAuthCodeResponse.class, name = "createAuthCode"),
    @JsonSubTypes.Type(value = GrantTokenResponse.class, name = "grantToken"),
    @JsonSubTypes.Type(value = AuthAdditionalOperationResponse.class, name = "authAdditionalOperation"),
    @JsonSubTypes.Type(value = GrantAdditionalOperationResponse.class, name = "grantAdditionalOperation"),
    @JsonSubTypes.Type(value = GetInfoResponse.class, name = "getInfo"),
    @JsonSubTypes.Type(value = RequestProfileResponse.class, name = "requestProfile"),
    @JsonSubTypes.Type(value = RequestStockResponse.class, name = "requestStock")
})
public interface ApiResponse<T extends ResponsePayload> extends ApiQueryWithPayload<T> {
    String getUuid();
    ChatWarsApiResult getResult();
}
