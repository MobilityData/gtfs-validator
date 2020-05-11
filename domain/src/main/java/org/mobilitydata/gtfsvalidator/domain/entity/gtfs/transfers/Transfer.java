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
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.EntityBuildResult;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.GtfsEntity;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingRequiredValueNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.UnexpectedEnumValueNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.warning.SuspiciousIntegerValueNotice;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for all entities defined in transfers.txt. Can not be directly instantiated: user must use the
 * {@link TransferBuilder} to create this.
 */
public class Transfer extends GtfsEntity {
    @NotNull
    private final String fromStopId;
    @NotNull
    private final String toStopId;
    @NotNull
    private final TransferType transferType;
    @Nullable
    private final Integer minTransferTime;

    /**
     * @param fromStopId      identifies a stop or station where a connection between routes begins
     * @param toStopId        identifies a stop or station where a connection between routes ends
     * @param transferType    indicates the type of connection for the specified
     * @param minTransferTime amount of time, in seconds, that must be available to permit a transfer between routes at
     *                        the specified stops
     */
    private Transfer(@NotNull final String fromStopId,
                     @NotNull final String toStopId,
                     @NotNull final TransferType transferType,
                     @Nullable final Integer minTransferTime) {
        this.fromStopId = fromStopId;
        this.toStopId = toStopId;
        this.transferType = transferType;
        this.minTransferTime = minTransferTime;
    }

    @NotNull public String getFromStopId() {
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

    @SuppressWarnings("unused")
    @Nullable
    public Integer getMinTransferTime() {
        return minTransferTime;
    }

    /**
     * Builder class to create {@link Transfer} objects. Allows an unordered definition of the different attributes of
     * {@link Transfer}.
     */
    public static class TransferBuilder {
        private String fromStopId;
        private String toStopId;
        private TransferType transferType;
        private Integer originalTransferTypeInteger;
        private Integer minTransferTime;
        private final List<Notice> noticeCollection = new ArrayList<>();

        /**
         * Sets field fromStopId value and returns this
         *
         * @param fromStopId identifies a stop or station where a connection between routes begins
         * @return builder for future object creation
         */
        public TransferBuilder fromStopId(@NotNull final String fromStopId) {
            this.fromStopId = fromStopId;
            return this;
        }

        /**
         * Sets field toStopId value and returns this
         *
         * @param toStopId a stop or station where a connection between routes ends
         * @return builder for future object creation
         */
        public TransferBuilder toStopId(@NotNull final String toStopId) {
            this.toStopId = toStopId;
            return this;
        }

        /**
         * Sets field transferType value and returns this
         *
         * @param transferType indicates the type of connection for the specified
         * @return builder for future object creation
         */
        public TransferBuilder transferType(@NotNull final Integer transferType) {
            this.transferType = TransferType.fromInt(transferType);
            this.originalTransferTypeInteger = transferType;
            return this;
        }

        /**
         * Sets field minTransferTime value and returns this
         *
         * @param minTransferTime amount of time, in seconds, that must be available to permit a transfer between
         *                        routes at the specified stops
         * @return builder for future object creation
         */
        public TransferBuilder minTransferTime(@Nullable final Integer minTransferTime) {
            this.minTransferTime = minTransferTime;
            return this;
        }

        /**
         * Returns an entity representing a row from transfers.txt if the requirements from the official GTFS
         * specification are met. Otherwise, method returns an entity representing a list of notices.
         *
         * @return entity representing a row from transfers.txt if the requirements from the official GTFS specification
         * are met. Otherwise, method returns an entity representing a list of notices.
         */
        public EntityBuildResult<?> build() {
            noticeCollection.clear();

            if (fromStopId == null || toStopId == null || transferType == null ||
                    (minTransferTime != null && minTransferTime < 0)) {

                if (fromStopId == null) {
                    noticeCollection.add(new MissingRequiredValueNotice("transfers.txt",
                            "from_stop_id", fromStopId + ";" + toStopId));
                }
                if (toStopId == null) {
                    noticeCollection.add(new MissingRequiredValueNotice("transfers.txt",
                            "to_stop_id", fromStopId + ";" + toStopId));
                }
                if (!TransferType.isEnumValueValid(originalTransferTypeInteger)) {
                    noticeCollection.add(new UnexpectedEnumValueNotice("transfers.txt",
                            "transfer_type", fromStopId + ";" + toStopId,
                            originalTransferTypeInteger));
                }
                if (minTransferTime != null && minTransferTime < 1) {
                    // here minTransferTime threshold should be user configurable
                    noticeCollection.add(new SuspiciousIntegerValueNotice("transfers.txt",
                            "min_transfer_time", fromStopId + ";" + toStopId, 0,
                            Integer.MAX_VALUE, minTransferTime));
                }
                return new EntityBuildResult<>(noticeCollection);
            } else {
                return new EntityBuildResult<>(new Transfer(fromStopId, toStopId, transferType, minTransferTime));
            }
        }
    }
}
