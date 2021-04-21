package org.nutz.dao.util.lambda;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2021/4/21
 */
public class LambdaQuery {

    /**
     * 字段映射
     */
    private static final Map<String, String> COLUMN_CACHE_MAP = new ConcurrentHashMap<>();


    /**
     * @param <T>    类型，被调用的 Function 对象的目标类型
     * @param lambda 需要解析的 lambda 对象
     * @return 返回解析后的字段名称
     */
    public static <T> String resolve(PFun<T, ?> lambda) {
        Class<?> clazz = lambda.getClass();
        String className = clazz.getName();
        return Optional.ofNullable(COLUMN_CACHE_MAP.get(className)).orElseGet(() -> getPropertyName(className, lambda));
    }

    /**
     * @param <T>    类型，被调用的 Function 对象的目标类型
     * @param lambda 需要解析的 lambda 对象
     * @return 返回解析后的字段名称
     */
    public static <T> String[] resolves(PFun<T, ?>... lambda) {
        return Arrays.stream(lambda).map(LambdaQuery::resolve).collect(Collectors.toList()).toArray(new String[0]);
    }

    /**
     * 获取字段名称
     *
     * @param className
     * @param lambda
     * @param <T>
     * @return
     */
    private static <T> String getPropertyName(String className, PFun<T, ?> lambda) {
        if (!lambda.getClass().isSynthetic()) {
            throw new RuntimeException("该方法仅能传入 lambda 表达式产生的合成类");
        }
        try {
            Method writeReplace = lambda.getClass().getDeclaredMethod("writeReplace");
            writeReplace.setAccessible(true);
            SerializedLambda serializedLambda = (SerializedLambda) writeReplace.invoke(lambda);
            return COLUMN_CACHE_MAP.computeIfAbsent(className, s -> methodNameToPropertyName(serializedLambda.getImplMethodName()));
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException("不可能发生的异常！！！");
        }
    }

    /**
     * 方法名转属性名
     *
     * @return
     */
    @SuppressWarnings("all")
    public static String methodNameToPropertyName(String methodName) {
        if (methodName.startsWith("is")) {
            methodName = methodName.substring(2);
        } else if (methodName.startsWith("get") || methodName.startsWith("set")) {
            methodName = methodName.substring(3);
        } else {
            throw new RuntimeException("方法名'" + methodName + "'不是以 'is','get','set' 开始的！");
        }
        if (methodName.length() == 1 || (methodName.length() > 1 && !Character.isUpperCase(methodName.charAt(1)))) {
            methodName = methodName.substring(0, 1).toLowerCase(Locale.ENGLISH) + methodName.substring(1);
        }
        return methodName;
    }
}
