package site.btyhub.batchrun.annotation;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import site.btyhub.batchrun.aop.BatchRunAdvisor;

/**
 * @author: baotingyu
 * @date: 2023/7/19
 **/
public class BatchRunImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {


    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        RootBeanDefinition rootBeanDefinition = new RootBeanDefinition();
        rootBeanDefinition.setBeanClass(BatchRunAdvisor.class);

        registry.registerBeanDefinition("batchRunAdvisor", rootBeanDefinition);
    }


}
