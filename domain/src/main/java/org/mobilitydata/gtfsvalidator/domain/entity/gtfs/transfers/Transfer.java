/*
 * Copyright (c) 2020. MobilityData IO.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mobilitydata.gtfsvalidator.domain.entity.gtfs.transfers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Transfer {

    @NotNull
    private final String fromStopId;

    @NotNull
    private final String toStopId;

    @NotNull
    private final TransferType transferType;

    @Nullable
    private final Integer minTransferTime;

    private Transfer(@NotNull final String fromStopId,
                     @NotNull final String toStopId,
                     @NotNull final TransferType transferType,
                     @Nullable final Integer minTransferTime) {
        this.fromStopId = fromStopId;
        this.toStopId = toStopId;
        this.transferType = transferType;
        this.minTransferTime = minTransferTime;
    }

    @NotNull
    public String getFromStopId() {
        return fromStopId;
    }

    @NotNull
    public String getToStopId() {
        return toStopId;
    }

    @NotNull
    public TransferType getTransferType() {
        return transferType;
    }

    @Nullable
    public Integer getMinTransferTime() {
        return minTransferTime;
    }

    public static class TransferBuilder {
        private String fromStopId;
        private String toStopId;
        private TransferType transferType;
        private Integer minTransferTime;

        public TransferBuilder fromStopId(@NotNull final String fromStopId) {
            this.fromStopId = fromStopId;
            return this;
        }

        public TransferBuilder toStopId(@NotNull final String toStopId) {
            this.toStopId = toStopId;
            return this;
        }

        public TransferBuilder transferType(@NotNull final Integer transferType) {
            this.transferType = TransferType.fromInt(transferType);
            return this;
        }

        public TransferBuilder minTransferTime(@Nullable final Integer minTransferTime) {
            this.minTransferTime = minTransferTime;
            return this;
        }

        public Transfer build() {
            if (fromStopId == null) {
                throw new IllegalArgumentException("field from_stop_id can not be null in transfers.txt");
            }
            if (toStopId == null) {
                throw new IllegalArgumentException("field to_stop_id can not be null in transfers.txt");
            }
            if (transferType == null) {
                throw new IllegalArgumentException("unexpected value encountered for field transfer_type" +
                        " transfers.txt");
            }
            if (minTransferTime != null && minTransferTime < 0) {
                throw new IllegalArgumentException("invalid value encountered for field min_transfer_time" +
                        " transfers.txt");
            }
            return new Transfer(fromStopId, toStopId, transferType, minTransferTime);
        }
    }
}
