package org.mobilitydata.gtfsvalidator.noticeschemagenerator;

import com.beust.jcommander.JCommander;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.utils.Log;
import com.github.javaparser.utils.SourceRoot;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.mobilitydata.gtfsvalidator.noticeschemagenerator.model.NoticeSchema;

public class Main {
  private static final ImmutableList<Path> NOTICE_SRC_DIRS =
      ImmutableList.of(Path.of("core/src/main/java"), Path.of("main/src/main/java"));

  public static void main(String[] argv) throws IOException {
    Log.setAdapter(new Log.StandardOutStandardErrorAdapter());

    Arguments args = new Arguments();
    JCommander.newBuilder().addObject(args).build().parse(argv);

    Path projectBaseDir = getProjectBaseDir();
    NoticeNodesCollector collector = new NoticeNodesCollector();
    List<ClassOrInterfaceDeclaration> noticeTypes = new ArrayList<>();

    for (Path noticeSrcDir : NOTICE_SRC_DIRS) {
      SourceRoot sourceRoot = new SourceRoot(projectBaseDir.resolve(noticeSrcDir));
      List<ParseResult<CompilationUnit>> parseResults = sourceRoot.tryToParse();
      for (ParseResult<CompilationUnit> pr : parseResults) {
        Optional<CompilationUnit> cu = pr.getResult();
        if (pr.isSuccessful() && cu.isPresent()) {
          cu.get().accept(collector, noticeTypes);
        } else {
          System.err.println("problem=" + cu);
        }
      }
    }

    if (!args.notices.isEmpty()) {
      noticeTypes =
          noticeTypes.stream()
              .filter(n -> args.notices.contains(n.getNameAsString()))
              .collect(Collectors.toList());
    }

    Map<String, NoticeSchema> schemasByCode = new HashMap<>();
    for (ClassOrInterfaceDeclaration noticeType : noticeTypes) {
      NoticeSchemaGenerator generator = new NoticeSchemaGenerator(noticeType);
      NoticeSchema schema = generator.createSchema();
      schemasByCode.put(schema.code(), schema);
    }

    GsonBuilder gsonBuilder = new GsonBuilder().setPrettyPrinting();
    Gson gson = gsonBuilder.create();

    System.out.println(gson.toJson(schemasByCode));
  }

  static Path getProjectBaseDir() {
    Path path = Path.of(System.getProperty("user.dir"));
    if (path.endsWith("notice-schema-generator")) {
      return path.getParent();
    }
    throw new IllegalStateException("Couldn't parse project path: " + path);
  }
}
