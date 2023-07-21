package site.btyhub.batchrun.aop;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanExpressionContext;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import site.btyhub.batchrun.annotation.BatchParam;
import site.btyhub.batchrun.annotation.BatchRun;
import site.btyhub.batchrun.core.PartitionArray;
import site.btyhub.batchrun.core.PartitionList;
import site.btyhub.batchrun.exception.BatchRunException;

/**
 * @author: baotingyu
 * @date: 2023/7/19
 **/
public class BatchRunInterceptor implements MethodInterceptor {
    private static final Logger log = LoggerFactory.getLogger(BatchRunInterceptor.class);

    private final ConfigurableBeanFactory beanFactory;

    private final Map<Method,Integer> validMultiParamMethod2BatchParamIdx = new HashMap<>();

    public BatchRunInterceptor(BeanFactory beanFactory) {
        if (beanFactory instanceof ConfigurableBeanFactory) {
            this.beanFactory = (ConfigurableBeanFactory) beanFactory;
            return;
        }
        throw new UnsupportedOperationException("batchRun needs BeanFactory");

    }


    @SuppressWarnings("all")
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {

        Method method = invocation.getMethod();

        BatchRun batchRun = method.getAnnotation(BatchRun.class);

        if (batchRun == null) {
            return invocation.proceed();
        }

        int batchSize = resolveBatchSize(batchRun);


        Parameter[] parameters = method.getParameters();

        if (Objects.isNull(parameters) || parameters.length == 0) {
            return invocation.proceed();
        }

        Class<?> returnType = method.getReturnType();
        if (!isArrayOrList(returnType)) {
            throw new BatchRunException("=== batch returnType should be an array or a List ===");
        }

        Object[] args = invocation.getArguments();


        List<Object> ret = new ArrayList<>();
        Object param = null;
        if (args.length == 1) {
            param = args[0];
        } else {
            if(validMultiParamMethod2BatchParamIdx.containsKey(method)){
                param = args[validMultiParamMethod2BatchParamIdx.get(method)];
            } else {
                int annotatedParamCount = 0;

                for (int i = 0; i < parameters.length; i++) {
                    Object a = args[i];
                    Parameter p = parameters[i];

                    BatchParam annotation = p.getAnnotation(BatchParam.class);
                    if (Objects.nonNull(annotation)) {
                        annotatedParamCount++;
                        param = a;
                        validMultiParamMethod2BatchParamIdx.put(method,i);
                    }
                }

                if (annotatedParamCount == 0) {
                    throw new BatchRunException(
                            "=== one method param should be annotated by @BatchParam when method has many params ===");
                }
                if (annotatedParamCount == 2) {
                    throw new BatchRunException(
                            "=== method params should be annotated by only and only one @BatchParam ===");
                }

            }


        }


        if (Objects.isNull(param)) {
            // let annotated method handles null input, batchrun don't do anything
            return invocation.proceed();
        }
        if (!isArrayOrList(param)) {
            throw new BatchRunException("=== batch param should be an array or a List ===");
        }

        if (isArray(param)) {
            PartitionArray partitionArray = new PartitionArray((Object[]) param, batchSize);
            for (Object[] p : partitionArray) {
                log.debug("invoke for input: {}", p);
                args[validMultiParamMethod2BatchParamIdx.getOrDefault(method,0)] = p;
                Object invoke = method.invoke(invocation.getThis(), args);
                log.debug("invoke results: {}", invoke);
                addResult(ret, invoke, returnType);
            }
        }
        if (isList(param)) {
            PartitionList partitionList = new PartitionList((List) param, batchSize);
            for (List list : partitionList) {
                log.debug("invoke for input: {}", list);
                args[validMultiParamMethod2BatchParamIdx.getOrDefault(method,0)] = list;
                Object invoke = method.invoke(invocation.getThis(), args);
                log.debug("invoke results: {}", invoke);
                addResult(ret, invoke, returnType);
            }
        }
        if (isArray(ret)) {
            return ret.toArray();
        }
        if (isList(ret)) {
            return ret;
        }
        throw new BatchRunException();
    }

    private int resolveBatchSize(BatchRun batchRun) {

        String batchSizeStr = batchRun.batchSize().trim();

        if (batchSizeStr.length() == 0) {
            throw new BatchRunException("no batchSize");
        }

        try {
            Integer.parseInt(batchSizeStr);
        } catch (NumberFormatException e) {
            // ${}
            Integer ret = resolveProperties(batchSizeStr);


            if (Objects.nonNull(ret)) {
                return ret;
            }
            // #{}
            ret = resolveSpringEL(batchSizeStr);

            if (Objects.isNull(ret)) {
                throw new BatchRunException("no batchSize");
            }
            return ret;

        }
        return Integer.parseInt(batchSizeStr);
    }


    private Integer resolveSpringEL(String batchSizeStr) {
        if (Objects.isNull(this.beanFactory.getBeanExpressionResolver())) {
            return null;
        }

        Object evaluate = this.beanFactory.getBeanExpressionResolver().evaluate(batchSizeStr, new BeanExpressionContext(this.beanFactory, null));

        if (evaluate instanceof Integer) {
            return (Integer) evaluate;
        }
        if (evaluate instanceof String) {
            try {
                Integer.parseInt((String) evaluate);
            } catch (NumberFormatException e) {
                return null;
            }
            return Integer.parseInt((String) evaluate);
        }
        return null;
    }

    private Integer resolveProperties(String batchSizeStr) {

        String s = this.beanFactory.resolveEmbeddedValue(batchSizeStr);

        if (Objects.nonNull(s)) {
            try {
                Integer.parseInt(s);
            } catch (NumberFormatException e) {
                return null;
            }
            return Integer.parseInt(s);
        }
        return null;
    }


    private void addResult(List<Object> result, Object invoke, Class<?> retType) {
        if (isArray(retType)) {
            Object[] res = (Object[]) invoke;
            result.addAll(Arrays.asList(res));
        }
        if (isList(retType)) {
            List res = (List) invoke;
            result.addAll(res);
        }
    }


    private boolean isArrayOrList(Object param) {
        return isArray(param) || isList(param);
    }

    private boolean isArray(Object param) {
        if (param instanceof Class) {
            return ((Class<?>) param).isArray();
        }
        return param.getClass().isArray();
    }

    private boolean isList(Object param) {
        if (param instanceof Class) {
            return ((Class<?>) param).isAssignableFrom(List.class);
        }
        return param instanceof List;
    }
}
