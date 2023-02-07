package org.mobilitydata.gtfsvalidator.noticeschemagenerator;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.javadoc.Javadoc;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
import org.mobilitydata.gtfsvalidator.notice.Notice;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.noticeschemagenerator.model.FieldSchema;
import org.mobilitydata.gtfsvalidator.noticeschemagenerator.model.NoticeSchema;

class NoticeSchemaGenerator {
  private final ClassOrInterfaceDeclaration classDeclaration;

  NoticeSchemaGenerator(ClassOrInterfaceDeclaration classDeclaration) {
    this.classDeclaration = classDeclaration;
  }

  NoticeSchema createSchema() {
    String code = Notice.getCode(classDeclaration.getNameAsString());
    SeverityLevel severityLevel = getSeverityLevel().orElse(SeverityLevel.INFO);
    NoticeSchema schema = new NoticeSchema(code, severityLevel);
    schema.setDescription(getDescription());
    classDeclaration.accept(new FieldVisitor(), schema);
    return schema;
  }

  String getDescription() {
    Optional<Javadoc> javadoc = classDeclaration.getJavadoc();
    if (javadoc.isEmpty()) {
      return "";
    }
    return extractNoticeDescriptionFromJavadoc(javadoc.get().toText());
  }

  static String extractNoticeDescriptionFromJavadoc(String content) {
    return Arrays.stream(content.split("\n"))
        .filter(s -> !s.isBlank())
        .filter(s -> !s.contains("<p>Severity"))
        .collect(Collectors.joining(" "));
  }

  Optional<SeverityLevel> getSeverityLevel() {
    Optional<Javadoc> javadoc = classDeclaration.getJavadoc();
    if (javadoc.isEmpty()) {
      return Optional.empty();
    }
    return extractSeverityFromJavadoc(javadoc.get().toText());
  }

  static Optional<SeverityLevel> extractSeverityFromJavadoc(String content) {
    return Arrays.stream(content.split("\n"))
        .filter(s -> !s.isBlank())
        .filter(s -> s.contains("<p>Severity"))
        .map(
            s -> {
              for (SeverityLevel sl : SeverityLevel.values()) {
                if (s.contains(sl.name())) {
                  return sl;
                }
              }
              return null;
            })
        .findFirst();
  }

  private static class FieldVisitor extends VoidVisitorAdapter<NoticeSchema> {

    @Override
    public void visit(FieldDeclaration n, NoticeSchema notice) {
      super.visit(n, notice);

      for (VariableDeclarator var : n.getVariables()) {
        FieldSchema.Builder builder = FieldSchema.builder();
        builder.setFieldName(var.getNameAsString());
        builder.setType(var.getType().asString());
        if (n.getComment().isPresent()) {
          builder.setDescription(n.getComment().get().getContent().strip());
        } else if (n.getJavadoc().isPresent()) {
          builder.setDescription(n.getJavadoc().get().toText().strip());
        }

        notice.addField(builder.build());
      }
    }
  }
}
