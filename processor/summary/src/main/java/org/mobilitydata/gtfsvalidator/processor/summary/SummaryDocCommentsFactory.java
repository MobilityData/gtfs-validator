package org.mobilitydata.gtfsvalidator.processor.summary;

import static javax.lang.model.util.ElementFilter.fieldsIn;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import org.mobilitydata.gtfsvalidator.processor.notices.CommentCleaner;

/**
 * Provides methods for constructing {@link SummaryMetadata} from summary class variables' {@link
 * VariableElement}.
 */
public class SummaryDocCommentsFactory {

  private final CommentCleaner cleaner = new CommentCleaner();

  private final Elements elements;
  private final Types types;

  public SummaryDocCommentsFactory(Elements elements, Types types) {
    this.elements = elements;
    this.types = types;
  }

  /**
   * Holds the resolved type information: a simplified type string and a flag indicating whether the
   * type is considered primitive or external (i.e. not defined within our own code).
   */
  public record ResolvedTypeInfo(
      String typeString, boolean isPrimitiveOrExternal, DeclaredType type) {}

  /**
   * Recursively resolves the provided type mirror to a simplified type string and a flag. Examples:
   * - java.lang.String -> "String", true - double -> "double", true -
   * java.util.List<java.lang.String> -> "List[String]", true - java.util.List<JsonReportCounts> ->
   * "List[JsonReportCounts]", false
   */
  private ResolvedTypeInfo resolveTypeInfo(TypeMirror typeMirror) {
    // Handle true primitives (int, boolean, etc.)
    if (typeMirror.getKind().isPrimitive()) {
      return new ResolvedTypeInfo(typeMirror.toString(), true, null);
    }

    // Handle arrays by recursing on the component type.
    if (typeMirror.getKind() == TypeKind.ARRAY) {
      ArrayType arrayType = (ArrayType) typeMirror;
      ResolvedTypeInfo componentInfo = resolveTypeInfo(arrayType.getComponentType());
      return new ResolvedTypeInfo(
          componentInfo.typeString + "[]", componentInfo.isPrimitiveOrExternal, componentInfo.type);
    }

    // Handle declared types (classes, interfaces, etc.)
    if (typeMirror instanceof DeclaredType) {
      DeclaredType declaredType = (DeclaredType) typeMirror;
      // Check if this type is a List.
      String erased = types.erasure(declaredType).toString();
      if (erased.equals("java.util.List")) {
        List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
        if (!typeArguments.isEmpty()) {
          // For a List, resolve the type argument and wrap it.
          ResolvedTypeInfo argInfo = resolveTypeInfo(typeArguments.get(0));
          return new ResolvedTypeInfo(
              argInfo.typeString + "[]", argInfo.isPrimitiveOrExternal, argInfo.type);
        } else {
          return new ResolvedTypeInfo("List", true, null);
        }
      } else {
        // For other declared types, get the simple name.
        TypeElement typeElement = (TypeElement) declaredType.asElement();
        String qualifiedName = typeElement.getQualifiedName().toString();
        String simpleName = typeElement.getSimpleName().toString();
        // Consider the type external if it is not defined in our own package.
        boolean isExternal = !qualifiedName.startsWith("org.mobilitydata.gtfsvalidator");
        return new ResolvedTypeInfo(simpleName, isExternal, declaredType);
      }
    }

    // Fallback for other cases.
    return new ResolvedTypeInfo(typeMirror.toString(), true, null);
  }

  /**
   * Returns documentation metadata for the provided field element. If the field has a {@link
   * SerializedName} annotation, the value of that annotation is used as the field name. Otherwise,
   * the field's name is used. If the field has no documentation, an empty {@link Optional} is
   * returned.
   */
  public Optional<SummaryMetadata> create(VariableElement element) {
    String fieldName = element.getSimpleName().toString();
    SerializedName serializedName = element.getAnnotation(SerializedName.class);
    if (serializedName != null) {
      fieldName = serializedName.value();
    }

    String fieldComment = elements.getDocComment(element);
    if (fieldComment != null) {
      fieldComment = String.join("\n", cleaner.cleanComment(fieldComment));
    } else {
      return Optional.empty();
    }

    // Get the type of the field
    TypeMirror fieldType = element.asType();
    ResolvedTypeInfo resolvedTypeInfo = resolveTypeInfo(fieldType);
    // List to hold nested metadata (if applicable)
    List<SummaryMetadata> nestedMetadata = null;

    if (!resolvedTypeInfo.isPrimitiveOrExternal) {
      DeclaredType declaredType = resolvedTypeInfo.type;
      if (declaredType != null) {
        // Get the corresponding TypeElement.
        TypeElement typeElement = (TypeElement) types.asElement(declaredType);
        // Retrieve all enclosed fields of the type.
        List<VariableElement> nestedFields = fieldsIn(typeElement.getEnclosedElements());
        if (!nestedFields.isEmpty()) {
          nestedMetadata = new ArrayList<>();
          // Process each nested field recursively.
          for (VariableElement nestedField : nestedFields) {
            Optional<SummaryMetadata> nestedSummary = create(nestedField);
            nestedSummary.ifPresent(nestedMetadata::add);
          }
        }
      }
    }
    return Optional.of(
        new SummaryMetadata(fieldName, fieldComment, resolvedTypeInfo.typeString, nestedMetadata));
  }

  public record SummaryMetadata(
      String name, String description, String type, @Nullable List<SummaryMetadata> fields) {}
}
