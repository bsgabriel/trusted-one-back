package com.bsg.trustedone.helper;

import jakarta.persistence.GeneratedValue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;

public class DummyObjects {

    public static <E> E newInstance(Class<E> claz) {
        E instance;

        try {
            instance = claz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException e) {
            var s = String.format("Failed to instantiate class '%s': class may be abstract or an interface", claz.getName());
            throw new RuntimeException(s, e);
        } catch (IllegalAccessException e) {
            var s = String.format("Access denied to constructor of class '%s': check access permissions", claz.getName());
            throw new RuntimeException(s, e);
        } catch (InvocationTargetException e) {
            var s = String.format("Error during constructor execution for class '%s'", claz.getName());
            throw new RuntimeException(s, e);
        } catch (NoSuchMethodException e) {
            var s = String.format("Default constructor not found for class '%s': ensure a no-args constructor exists", claz.getName());
            throw new RuntimeException(s, e);
        }

        try {
            populate(instance);
        } catch (IllegalAccessException e) {
            var s = String.format("Error populating fields for instance '%s': access denied to one or more fields. Check if fields are accessible or call setAccessible()", instance.getClass().getName());
            throw new RuntimeException(s, e);
        }
        return instance;
    }

    private static <E> void populate(E instance) throws IllegalAccessException {
        var declaredFields = getAllDeclaredFields(instance.getClass(), new ArrayList<>())
                .stream()
                .filter(f -> !Modifier.isFinal(f.getModifiers()))
                .iterator();

        while (declaredFields.hasNext()) {
            Field field = declaredFields.next();
            field.setAccessible(true);

            if (String.class.isAssignableFrom(field.getType())) {
                int max = field.isAnnotationPresent(Size.class) ? field.getAnnotation(Size.class).max() : 10;
                field.set(instance, RandomUtils.nextString(Math.min(max, 500)));
                continue;
            }

            if (Long.TYPE.isAssignableFrom(field.getType()) || Long.class.isAssignableFrom(field.getType())) {
                if (!field.isAnnotationPresent(GeneratedValue.class)) {
                    field.set(instance, RandomUtils.nextLong(1, 32767));
                    continue;
                }
            }

            if (Integer.TYPE.isAssignableFrom(field.getType()) || Integer.class.isAssignableFrom(field.getType())) {
                var min = (int) (field.isAnnotationPresent(Min.class) ? field.getAnnotation(Min.class).value() : 1L);
                var max = (int) (field.isAnnotationPresent(Max.class) ? field.getAnnotation(Max.class).value() : 1000L);
                field.set(instance, RandomUtils.nextInt(min, max));
                continue;
            }

            if (Double.TYPE.isAssignableFrom(field.getType()) || Double.class.isAssignableFrom(field.getType())) {
                var min = (int) (field.isAnnotationPresent(Min.class) ? field.getAnnotation(Min.class).value() : 1L);
                var max = (int) (field.isAnnotationPresent(Max.class) ? field.getAnnotation(Max.class).value() : 1000L);
                field.set(instance, RandomUtils.nextDouble(min, max));
                continue;
            }

            if (Boolean.TYPE.isAssignableFrom(field.getType()) || Boolean.class.isAssignableFrom(field.getType())) {
                field.set(instance, RandomUtils.nextBoolean());
                continue;
            }

            if (LocalDateTime.class.isAssignableFrom(field.getType())) {
                field.set(instance, LocalDateTime.now().withNano(0));
                continue;
            }

            if (LocalDate.class.isAssignableFrom(field.getType())) {
                field.set(instance, LocalDate.now());
                continue;
            }

            if (Date.class.isAssignableFrom(field.getType())) {
                field.set(instance, new Date());
                continue;
            }

            if (Enum.class.isAssignableFrom(field.getType())) {
                Class enumClass = field.getType();
                Object[] enumConstants = enumClass.getEnumConstants();
                Enum e = Enum.valueOf(enumClass, enumConstants[RandomUtils.nextInt(0, enumConstants.length - 1)].toString());
                field.set(instance, e);
                continue;
            }

            if (BigDecimal.class.isAssignableFrom(field.getType())) {
                field.set(instance, RandomUtils.nextBigDecimal(1L, 1000L, 0));
                continue;
            }

            if (BigInteger.class.isAssignableFrom(field.getType())) {
                var min = (int) (field.isAnnotationPresent(Min.class) ? field.getAnnotation(Min.class).value() : 1L);
                var max = (int) (field.isAnnotationPresent(Max.class) ? field.getAnnotation(Max.class).value() : 1000L);
                field.set(instance, RandomUtils.nextBigInteger(min, max));
                continue;
            }

            if (UUID.class.isAssignableFrom(field.getType())) {
                field.set(instance, RandomUtils.nextUUID());
            }

        }
    }

    private static List<Field> getAllDeclaredFields(Class<?> type, List<Field> fields) {
        fields.addAll(Stream.of(type.getDeclaredFields()).collect(Collectors.toSet()));

        if (nonNull(type.getSuperclass()) && Object.class != type.getSuperclass()) {
            getAllDeclaredFields(type.getSuperclass(), fields);
        }

        return fields;
    }

}
