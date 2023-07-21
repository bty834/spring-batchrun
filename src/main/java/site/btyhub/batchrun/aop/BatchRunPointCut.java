package site.btyhub.batchrun.aop;

import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.annotation.AnnotationMethodMatcher;
import site.btyhub.batchrun.annotation.BatchRun;

/**
 *
 * @author: baotingyu
 * @date: 2023/7/19
 **/
public class BatchRunPointCut implements Pointcut {

    private static final AnnotationMethodMatcher batchRunAnnoMethodMatcher = new AnnotationMethodMatcher(BatchRun.class);

    @Override
    public ClassFilter getClassFilter() {
        return ClassFilter.TRUE;
    }

    @Override
    public MethodMatcher getMethodMatcher() {
        return batchRunAnnoMethodMatcher;
    }
}
