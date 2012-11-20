package ru.kwanza.dbtool.orm.springintegration;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.beans.factory.xml.XmlReaderContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;
import ru.kwanza.dbtool.orm.impl.mapping.EntityMappingRegistryImpl;
import ru.kwanza.dbtool.orm.impl.mapping.SpringEntityMappingRegistryImpl;

/**
 * @author Kiryl Karatsetski
 */
public class EntityMappingRegistryParser implements BeanDefinitionParser {

    private static final String BASE_PACKAGE_ATTRIBUTE = "scan-package";

    private static final String ENTITY_MAPPING_REGISTRY = "dbtool.IEntityMappingRegistry";

    public BeanDefinition parse(Element element, ParserContext parserContext) {
        final XmlReaderContext readerContext = parserContext.getReaderContext();

        final BeanDefinitionRegistry beanDefinitionRegistry = readerContext.getRegistry();

        if (!beanDefinitionRegistry.containsBeanDefinition(ENTITY_MAPPING_REGISTRY)) {
            final BeanDefinitionBuilder beanDefinitionBuilder = createBeanDefinitionBuilder(EntityMappingRegistryImpl.class);
            final BeanDefinition beanDefinition = beanDefinitionBuilder.getBeanDefinition();
            beanDefinitionRegistry.registerBeanDefinition(ENTITY_MAPPING_REGISTRY, beanDefinition);
        }

        final BeanDefinitionBuilder beanDefinitionBuilder = createBeanDefinitionBuilder(SpringEntityMappingRegistryImpl.class);
        beanDefinitionBuilder.addConstructorArgReference(ENTITY_MAPPING_REGISTRY);
        beanDefinitionBuilder.addConstructorArgValue(getBasePackages(element));
        final BeanDefinition beanDefinition = beanDefinitionBuilder.getBeanDefinition();
        final String beanName = BeanDefinitionReaderUtils.generateBeanName(beanDefinition, beanDefinitionRegistry, true);
        beanDefinitionRegistry.registerBeanDefinition(beanName, beanDefinition);

        return beanDefinition;
    }

    private BeanDefinitionBuilder createBeanDefinitionBuilder(Class beanClass) {
        return BeanDefinitionBuilder.rootBeanDefinition(beanClass);
    }

    private String[] getBasePackages(Element element) {
        final String basePackage = element.getAttribute(BASE_PACKAGE_ATTRIBUTE);
        return StringUtils.tokenizeToStringArray(basePackage, ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
    }
}
