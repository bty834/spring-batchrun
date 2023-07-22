package site.btyhub.batchrun.aop;

import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.core.Ordered;

/**
 *
 * @author: baotingyu
 * @date: 2023/7/19
 **/
public class BatchRunAdvisor extends AbstractPointcutAdvisor implements BeanFactoryAware {



    private final BatchRunPointCut batchRunPointCut = new BatchRunPointCut();
    private BatchRunInterceptor batchRunInterceptor ;

    @Override
    public Pointcut getPointcut() {
        return batchRunPointCut;
    }

    @Override
    public Advice getAdvice() {
        return batchRunInterceptor;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.batchRunInterceptor = new BatchRunInterceptor(beanFactory);
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 100;
    }
}
