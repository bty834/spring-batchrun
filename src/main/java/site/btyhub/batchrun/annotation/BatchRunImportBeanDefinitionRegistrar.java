package site.btyhub.batchrun.annotation;

import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.Ordered;
import org.springframework.core.type.AnnotationMetadata;

import site.btyhub.batchrun.aop.BatchRunAdvisor;

/**
 * @author: baotingyu
 * @date: 2023/7/19
 **/
public class BatchRunImportBeanDefinitionRegistrar
        implements ImportBeanDefinitionRegistrar, ApplicationContextAware, SmartInitializingSingleton {

    private ApplicationContext ac;

    public static final int DEFAULT_AOP_ORDER = Ordered.LOWEST_PRECEDENCE - 100;
    private int order = DEFAULT_AOP_ORDER;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        RootBeanDefinition rootBeanDefinition = new RootBeanDefinition();
        rootBeanDefinition.setBeanClass(BatchRunAdvisor.class);

        Map<String, Object> annotationAttributes =
                importingClassMetadata.getAnnotationAttributes(EnableBatchRun.class.getName(), true);

        if (annotationAttributes != null && annotationAttributes.containsKey("aopOrder")) {
            this.order = (int) annotationAttributes.get("aopOrder");
        }

        registry.registerBeanDefinition("batchRunAdvisor", rootBeanDefinition);
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ac = applicationContext;
    }


    @Override
    public void afterSingletonsInstantiated() {
        BatchRunAdvisor advisor = this.ac.getBean(BatchRunAdvisor.class);
        advisor.setOrder(order);
    }

}
