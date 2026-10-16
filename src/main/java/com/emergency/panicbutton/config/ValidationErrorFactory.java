package com.emergency.panicbutton.config;

import com.emergency.panicbutton.common.ValidationErrorItem;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Produces FastAPI/pydantic-style validation error items. The running service
 * emits {@code {"loc": [...], "msg": "...", "type": "..."}} (no {@code input}
 * or {@code ctx}), so those fields are intentionally left {@code null}.
 */
final class ValidationErrorFactory {

    private static final Pattern SIZE_BETWEEN =
            Pattern.compile("between\\s+(\\d+)\\s+and\\s+(\\d+)");
    private static final Pattern GREATER_EQUAL =
            Pattern.compile("greater than or equal to\\s+(-?\\d+(?:\\.\\d+)?)");
    private static final Pattern LESS_EQUAL =
            Pattern.compile("less than or equal to\\s+(-?\\d+(?:\\.\\d+)?)");

    private ValidationErrorFactory() {
    }

    /**
     * Maps each declared field of the target bean to its source-declaration
     * index, so validation details can be reported in pydantic field order.
     */
    static Map<String, Integer> declarationOrder(Class<?> targetClass) {
        Map<String, Integer> order = new HashMap<>();
        if (targetClass == null) {
            return order;
        }
        int index = 0;
        for (Field field : targetClass.getDeclaredFields()) {
            if (!field.isSynthetic()) {
                order.putIfAbsent(field.getName(), index++);
            }
        }
        return order;
    }

    static ValidationErrorItem forField(
            Class<?> targetClass, String fieldName, String code,
            String defaultMessage, Object rejectedValue) {
        String name = jsonName(targetClass, fieldName);
        String type;
        String msg;

        if (code == null) {
            type = "value_error";
            msg = defaultMessage;
        } else {
            switch (code) {
                case "NotNull", "NotBlank", "NotEmpty" -> {
                    type = "missing";
                    msg = "Field required";
                }
                case "Size" -> {
                    SizeBounds bounds = parseSize(defaultMessage);
                    boolean tooShort = rejectedLength(rejectedValue) < bounds.min();
                    type = tooShort ? "string_too_short" : "string_too_long";
                    msg = tooShort
                            ? "String should have at least " + bounds.min() + " characters"
                            : "String should have at most " + bounds.max() + " characters";
                }
                case "Email" -> {
                    type = "value_error";
                    msg = "value is not a valid email address: "
                            + "An email address must have an @-sign.";
                }
                case "Min", "DecimalMin" -> {
                    type = "greater_than_equal";
                    msg = "Input should be greater than or equal to "
                            + extract(GREATER_EQUAL, defaultMessage);
                }
                case "Max", "DecimalMax" -> {
                    type = "less_than_equal";
                    msg = "Input should be less than or equal to "
                            + extract(LESS_EQUAL, defaultMessage);
                }
                default -> {
                    type = "value_error";
                    msg = defaultMessage;
                }
            }
        }

        return new ValidationErrorItem(List.of("body", name), msg, type, null, null);
    }

    static ValidationErrorItem forQuery(String paramName, String message) {
        return new ValidationErrorItem(
                List.of("query", paramName), message, "value_error", null, null);
    }

    static ValidationErrorItem unreadable() {
        return new ValidationErrorItem(
                List.of("body"),
                "Request body is missing or malformed",
                "value_error",
                null,
                null);
    }

    private static String jsonName(Class<?> targetClass, String fieldName) {
        if (targetClass == null) {
            return fieldName;
        }
        Class<?> current = targetClass;
        while (current != null) {
            try {
                Field field = current.getDeclaredField(fieldName);
                JsonProperty jsonProperty = field.getAnnotation(JsonProperty.class);
                if (jsonProperty != null && !jsonProperty.value().isEmpty()) {
                    return jsonProperty.value();
                }
                return fieldName;
            } catch (NoSuchFieldException e) {
                current = current.getSuperclass();
            }
        }
        return fieldName;
    }

    private static int rejectedLength(Object value) {
        if (value == null) {
            return 0;
        }
        if (value instanceof CharSequence cs) {
            return cs.length();
        }
        if (value instanceof Collection<?> c) {
            return c.size();
        }
        if (value instanceof Map<?, ?> m) {
            return m.size();
        }
        if (value.getClass().isArray()) {
            return java.lang.reflect.Array.getLength(value);
        }
        return String.valueOf(value).length();
    }

    private static SizeBounds parseSize(String message) {
        Matcher matcher = SIZE_BETWEEN.matcher(message == null ? "" : message);
        if (matcher.find()) {
            return new SizeBounds(
                    Integer.parseInt(matcher.group(1)),
                    Integer.parseInt(matcher.group(2)));
        }
        return new SizeBounds(0, Integer.MAX_VALUE);
    }

    private static String extract(Pattern pattern, String message) {
        Matcher matcher = pattern.matcher(message == null ? "" : message);
        return matcher.find() ? matcher.group(1) : message;
    }

    private record SizeBounds(int min, int max) {
    }
}
