package ru.kwanza.dbtool.orm.springintegration;

/*
 * #%L
 * dbtool-orm
 * %%
 * Copyright (C) 2015 Kwanza
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.beans.factory.xml.XmlReaderContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;
import ru.kwanza.dbtool.orm.impl.EntityManagerImpl;
import ru.kwanza.dbtool.orm.impl.mapping.EntityMappingRegistry;
import ru.kwanza.dbtool.orm.impl.mapping.SpringEntityMappingRegistryImpl;

/**
 * @author Kiryl Karatsetski
 */
public class EntityMappingRegistryParser implements BeanDefinitionParser {

    private static final String BASE_PACKAGE_ATTRIBUTE = "scan-package";

    private static final String DBTOOL_PROPERTY = "dbTool";
    private static final String VERSION_GENERATOR_PROPERTY = "versionGenerator";
    private static final String MAPPING_REGISTRY_PROPERTY = "mappingRegistry";

    private static final String ENTITY_MAPPING_REGISTRY = "dbtool.IEntityMappingRegistry";

    private static final String ENTITY_MANAGER = "dbtool.IEntityManager";
    private static final String DBTOOL = "dbtool.DBTool";
    private static final String VERSION_GENERATOR = "dbtool.VersionGenerator";

    private static final String INIT_METHOD = "init";

    public BeanDefinition parse(Element element, ParserContext parserContext) {
        final XmlReaderContext readerContext = parserContext.getReaderContext();

        final BeanDefinitionRegistry beanDefinitionRegistry = readerContext.getRegistry();

        if (!beanDefinitionRegistry.containsBeanDefinition(ENTITY_MAPPING_REGISTRY)) {
            final BeanDefinitionBuilder beanDefinitionBuilder = createBeanDefinitionBuilder(EntityMappingRegistry.class);
            beanDefinitionRegistry.registerBeanDefinition(ENTITY_MAPPING_REGISTRY, beanDefinitionBuilder.getBeanDefinition());
        }

        if (!beanDefinitionRegistry.containsBeanDefinition(ENTITY_MANAGER)) {
            final BeanDefinitionBuilder beanDefinitionBuilder = createBeanDefinitionBuilder(EntityManagerImpl.class);
            beanDefinitionBuilder.addPropertyReference(DBTOOL_PROPERTY, DBTOOL);
            beanDefinitionBuilder.addPropertyReference(VERSION_GENERATOR_PROPERTY, VERSION_GENERATOR);
            beanDefinitionBuilder.addPropertyReference(MAPPING_REGISTRY_PROPERTY, ENTITY_MAPPING_REGISTRY);
            beanDefinitionBuilder.setInitMethodName(INIT_METHOD);

            beanDefinitionBuilder.setDependencyCheck(AbstractBeanDefinition.DEPENDENCY_CHECK_ALL);

            beanDefinitionRegistry.registerBeanDefinition(ENTITY_MANAGER, beanDefinitionBuilder.getBeanDefinition());
        }

        final BeanDefinitionBuilder beanDefinitionBuilder = createBeanDefinitionBuilder(SpringEntityMappingRegistryImpl.class);
        beanDefinitionBuilder.addConstructorArgReference(ENTITY_MAPPING_REGISTRY);
        beanDefinitionBuilder.addConstructorArgValue(getBasePackages(element));
        final BeanDefinition beanDefinition = beanDefinitionBuilder.getBeanDefinition();
        String beanId = element.getAttribute("id");
        if (beanId == null || beanId.isEmpty()) {
            beanId = BeanDefinitionReaderUtils.generateBeanName(beanDefinition, beanDefinitionRegistry, false);
        }
        beanDefinitionRegistry.registerBeanDefinition(beanId, beanDefinition);

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
