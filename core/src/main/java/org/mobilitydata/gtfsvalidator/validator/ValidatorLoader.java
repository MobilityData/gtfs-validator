package org.mobilitydata.gtfsvalidator.validator;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.reflect.ClassPath;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.annotation.Inject;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsEntity;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTableContainer;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * A {@code ValidatorLoader} object locates all validators registered with {@code @GtfsValidator} annotation and
 * provides convenient methods to invoke them on a single entity of file.
 */
public class ValidatorLoader {
    private final ListMultimap<Class<? extends GtfsEntity>, SingleEntityValidator<?>> singleEntityValidators = ArrayListMultimap.create();
    private final ListMultimap<Class<? extends GtfsTableContainer>, Class<? extends FileValidator>> singleFileValidators = ArrayListMultimap.create();
    private final List<Class<? extends FileValidator>> multiFileValidators = new ArrayList<>();

    public ValidatorLoader() {
        List<Class<? extends SingleEntityValidator>> singleEntityValidatorClasses = new ArrayList<>();
        List<Class<? extends FileValidator>> fileValidatorClasses = new ArrayList<>();
        ClassPath classPath;
        try {
            classPath = ClassPath.from(ClassLoader.getSystemClassLoader());
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        for (ClassPath.ClassInfo classInfo : classPath
                .getTopLevelClassesRecursive("org.mobilitydata.gtfsvalidator.validator")) {
            Class<?> clazz = classInfo.load();
            if (clazz.isAnnotationPresent(GtfsValidator.class)) {
                if (SingleEntityValidator.class.isAssignableFrom(clazz)) {
                    singleEntityValidatorClasses.add((Class<? extends SingleEntityValidator>) clazz);
                } else if (FileValidator.class.isAssignableFrom(clazz)) {
                    fileValidatorClasses.add((Class<? extends FileValidator>) clazz);
                }
            }
        }

        for (Class<? extends SingleEntityValidator> validatorClass : singleEntityValidatorClasses) {
            for (Method method : validatorClass.getMethods()) {
                // A child class of SingleEntityValidator has two `validate' methods:
                // 1) the inherited void validate(GtfsEntity entity, NoticeContainer noticeContainer);
                // 2) the type-specific void validate(Gtfs<name> entity, NoticeContainer noticeContainer).
                // We need to skip the first one and use the second one.
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (method.getName().equals("validate") && method.getParameterCount() == 2
                        && GtfsEntity.class.isAssignableFrom(parameterTypes[0])
                        && !parameterTypes[0].isAssignableFrom(GtfsEntity.class)
                        && parameterTypes[1].isAssignableFrom(NoticeContainer.class)) {
                    try {
                        singleEntityValidators.put(
                                (Class<? extends GtfsEntity>) parameterTypes[0],
                                ((Class<? extends SingleEntityValidator>) validatorClass).getConstructor().newInstance());
                    } catch (ReflectiveOperationException exception) {
                        System.err.println("Cannot instantiate validator: " + exception);
                    }
                    break;
                }
            }
        }

        for (Class<? extends FileValidator> validatorClass : fileValidatorClasses) {
            List<Field> tableInjectableFields = new ArrayList<>();
            for (Field field : validatorClass.getDeclaredFields()) {
                if (isTableInjectableField(field)) {
                    tableInjectableFields.add(field);
                }
            }
            if (tableInjectableFields.size() == 1) {
                singleFileValidators.put(
                        (Class<? extends GtfsTableContainer>) tableInjectableFields.get(0).getType(),
                        validatorClass);
            } else {
                multiFileValidators.add(validatorClass);
            }
        }

    }

    private static boolean isTableInjectableField(Field field) {
        return field.isAnnotationPresent(Inject.class) && GtfsTableContainer.class.isAssignableFrom(field.getType());
    }

    public <T extends GtfsEntity>
    List<SingleEntityValidator<T>> getSingleEntityValidators(Class<T> clazz) {
        return (List<SingleEntityValidator<T>>) (List<?>) singleEntityValidators.get(clazz);
    }

    public <T extends GtfsEntity> void invokeSingleEntityValidators(T entity, NoticeContainer noticeContainer) {
        for (SingleEntityValidator validator : getSingleEntityValidators(entity.getClass())) {
            validator.validate(entity, noticeContainer);
        }
    }

    public <T extends GtfsEntity> void invokeSingleFileValidators(GtfsTableContainer<T> table, NoticeContainer noticeContainer) {
        for (Class<? extends FileValidator> validatorClass : singleFileValidators.get(table.getClass())) {
            FileValidator validator;
            try {
                validator = createValidator(validatorClass, table);
            } catch (ReflectiveOperationException exception) {
                System.err.println("Cannot instantiate validator: " + exception);
                continue;
            }
            validator.validate(noticeContainer);
        }
    }

    private FileValidator createValidator(Class<? extends FileValidator> clazz, GtfsTableContainer table)
            throws ReflectiveOperationException {
        FileValidator validator = clazz.getConstructor().newInstance();
        for (Field field : clazz.getDeclaredFields()) {
            if (!isTableInjectableField(field)) {
                continue;
            }
            if (!field.getType().isAssignableFrom(table.getClass())) {
                throw new InstantiationException("Cannot inject a field of type " + field.getType().getSimpleName());
            }
            field.set(validator, table);
        }
        return validator;
    }

    private FileValidator createValidator(Class<? extends FileValidator> clazz, GtfsFeedContainer feed)
            throws ReflectiveOperationException {
        FileValidator validator = clazz.getConstructor().newInstance();
        for (Field field : clazz.getDeclaredFields()) {
            if (!isTableInjectableField(field)) {
                continue;
            }
            GtfsTableContainer table = feed.getTable((Class<? extends GtfsTableContainer>) field.getType());
            if (table == null) {
                throw new InstantiationException("Cannot find " + field.getType().getSimpleName() + " in feed container");
            }
            field.set(validator, table);
        }
        return validator;
    }

    public List<FileValidator> createMultiFileValidators(GtfsFeedContainer feed) {
        ArrayList<FileValidator> validators = new ArrayList<>();
        validators.ensureCapacity(multiFileValidators.size());
        for (Class<? extends FileValidator> validatorClass : multiFileValidators) {
            try {
                validators.add(createValidator(validatorClass, feed));
            } catch (ReflectiveOperationException exception) {
                System.err.println("Cannot instantiate validator: " + exception);
            }
        }
        return validators;
    }

    public String listValidators() {
        StringBuilder builder = new StringBuilder();
        if (!singleEntityValidators.isEmpty()) {
            builder.append("Single-entity validators\n");
            for (Map.Entry<Class<? extends GtfsEntity>, Collection<SingleEntityValidator<?>>> entry : singleEntityValidators.asMap().entrySet()) {
                builder.append("\t").append(entry.getKey().getSimpleName()).append(": ");
                for (SingleEntityValidator validator : entry.getValue()) {
                    builder.append(validator.getClass().getSimpleName()).append(" ");
                }
                builder.append("\n");
            }
        }
        if (!singleFileValidators.isEmpty()) {
            builder.append("Single-file validators\n");
            for (Map.Entry<Class<? extends GtfsTableContainer>, Collection<Class<? extends FileValidator>>> entry :
                    singleFileValidators.asMap().entrySet()) {
                builder.append("\t").append(entry.getKey().getSimpleName()).append(": ");
                for (Class<? extends FileValidator> validatorClass : entry.getValue()) {
                    builder.append(validatorClass.getSimpleName()).append(" ");
                }
                builder.append("\n");
            }
        }
        if (!multiFileValidators.isEmpty()) {
            builder.append("Multi-file validators\n").append("\t");
            for (Class<? extends FileValidator> validatorClass : multiFileValidators) {
                builder.append(validatorClass.getSimpleName()).append(" ");
            }
            builder.append("\n");
        }
        return builder.toString();
    }
}
