package site.btyhub.batchrun.core;

import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;

/**
 *
 * @author: baotingyu
 * @date: 2023/7/19
 **/
public class BatchRunAdvisor extends AbstractPointcutAdvisor {

    private final static BatchRunPointCut batchRunPointCut = new BatchRunPointCut();
    private final static BatchRunInterceptor batchRunInterceptor = new BatchRunInterceptor();

    @Override
    public Pointcut getPointcut() {
        return batchRunPointCut;
    }

    @Override
    public Advice getAdvice() {
        return batchRunInterceptor;
    }
}
