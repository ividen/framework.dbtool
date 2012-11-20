package ru.kwanza.dbtool.orm.springintegration;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * @author Kiryl Karatsetski
 */
public class DBToolOrmNamespaceHandler extends NamespaceHandlerSupport {

    public void init() {
        registerBeanDefinitionParser("entityMapping", new EntityMappingRegistryParser());
    }
}
