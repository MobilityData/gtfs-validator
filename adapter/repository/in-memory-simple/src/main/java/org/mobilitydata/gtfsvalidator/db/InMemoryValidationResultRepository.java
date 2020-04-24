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

package org.mobilitydata.gtfsvalidator.db;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.mobilitydata.gtfsvalidator.adapter.protos.GtfsValidationOutputProto;
import org.mobilitydata.gtfsvalidator.domain.entity.NoticeExporter;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.ErrorNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.InfoNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.WarningNotice;
import org.mobilitydata.gtfsvalidator.exporter.JsonNoticeExporter;
import org.mobilitydata.gtfsvalidator.exporter.ProtobufNoticeExporter;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Holds information about the validation process. Stores notices according to their types. Provides methods to add
 * said notices to the repository and get said notices.
 * This is created  when creating a new default configuration, all fields being set to their default value.
 */
public class InMemoryValidationResultRepository implements ValidationResultRepository {
    private final List<InfoNotice> infoNoticeList = new ArrayList<>();
    private final List<WarningNotice> warningNoticeList = new ArrayList<>();
    private final List<ErrorNotice> errorNoticeList = new ArrayList<>();

    /**
     * Adds an info notice to the repository and returns the notice
     *
     * @param newInfo a info notice
     * @return the info notice that was added to the repository
     */
    @Override
    public InfoNotice addNotice(InfoNotice newInfo) {
        infoNoticeList.add(newInfo);
        return newInfo;
    }

    /**
     * Adds an warning notice to the repository and returns the notice
     *
     * @param newWarning a warning notice
     * @return the info notice that was added to the repository
     */
    @Override
    public WarningNotice addNotice(WarningNotice newWarning) {
        warningNoticeList.add(newWarning);
        return newWarning;
    }

    /**
     * Adds an error notice to the repository and returns the notice. Useful for automatic type inference
     *
     * @param newError notice
     * @return the notice that was added to the repository
     */
    @Override
    public ErrorNotice addNotice(ErrorNotice newError) {
        errorNoticeList.add(newError);
        return newError;
    }

    /**
     * Visit a generic notice to add it to the repository and returns the notice. Useful for automatic type inference
     *
     * @param newNotice notice
     * @return the notice that was added to the repository
     */
    @Override
    public Notice addNotice(Notice newNotice) {
        return newNotice.visit(this);
    }

    /**
     * Returns a collection of all notices contained in the validation repository
     *
     * @return all notices contained in the validation repository as a collection
     */
    @Override
    public Collection<Notice> getAll() {
        return Stream.concat(
                Stream.concat(
                        infoNoticeList.stream(),
                        warningNoticeList.stream()),
                errorNoticeList.stream())
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public NoticeExporter getExporter(boolean outputAsProto, String outputPath) throws IOException {
        if (outputAsProto) {
            return new ProtobufNoticeExporter(GtfsValidationOutputProto.GtfsProblem.newBuilder(),
                    new ProtobufNoticeExporter.ProtobufOutputStreamGenerator(outputPath));
        } else {
            return new JsonNoticeExporter(new ObjectMapper().getFactory().createGenerator(
                    Files.newOutputStream(Paths.get(
                            outputPath + File.separator + "results" +
                                    JsonNoticeExporter.FILE_EXTENSION
                            )
                    )));
        }
    }
}