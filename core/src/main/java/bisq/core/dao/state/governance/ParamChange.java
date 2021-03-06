/*
 * This file is part of Bisq.
 *
 * Bisq is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * Bisq is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Bisq. If not, see <http://www.gnu.org/licenses/>.
 */

package bisq.core.dao.state.governance;

import bisq.common.proto.persistable.PersistablePayload;

import io.bisq.generated.protobuffer.PB;

import lombok.Value;

import javax.annotation.concurrent.Immutable;

/**
 * Holds the data for a parameter change. Gets persisted with the DaoState.
 */
@Immutable
@Value
public class ParamChange implements PersistablePayload {
    // We use the enum name instead of the enum to be more flexible with changes at updates
    private final String paramName;
    private final long value;
    private final int activationHeight;

    public ParamChange(String paramName, long value, int activationHeight) {
        this.paramName = paramName;
        this.value = value;
        this.activationHeight = activationHeight;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    // PROTO BUFFER
    ///////////////////////////////////////////////////////////////////////////////////////////


    @Override
    public PB.ParamChange toProtoMessage() {
        return PB.ParamChange.newBuilder()
                .setParamName(paramName)
                .setParamValue(value)
                .setActivationHeight(activationHeight)
                .build();
    }

    public static ParamChange fromProto(PB.ParamChange proto) {
        return new ParamChange(proto.getParamName(),
                proto.getParamValue(),
                proto.getActivationHeight());
    }
}
