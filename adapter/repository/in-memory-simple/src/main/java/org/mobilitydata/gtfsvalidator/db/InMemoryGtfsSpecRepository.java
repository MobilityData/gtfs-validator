package org.mobilitydata.gtfsvalidator.db;

import com.google.common.io.Resources;
import com.google.protobuf.TextFormat;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsSpecRepository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class InMemoryGtfsSpecRepository implements GtfsSpecRepository {

    private final GtfsSpecProto.CsvSpecProtos inMemoryGTFSSpec;

    public InMemoryGtfsSpecRepository(final String specResourceName) throws IOException {
        //noinspection UnstableApiUsage
        inMemoryGTFSSpec = TextFormat.parse(Resources.toString(Resources.getResource("gtfs_spec.asciipb"), StandardCharsets.UTF_8),
                GtfsSpecProto.CsvSpecProtos.class);
    }

    @Override
    public List<String> getRequiredFilenameList() {
        return inMemoryGTFSSpec.getCsvspecList().stream()
                .filter(GtfsSpecProto.CsvSpecProto::getRequired).map(GtfsSpecProto.CsvSpecProto::getFilename)
                .collect(Collectors.toList());
    }
}
