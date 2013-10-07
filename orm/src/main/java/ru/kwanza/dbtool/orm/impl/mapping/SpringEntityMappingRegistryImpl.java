package ru.kwanza.dbtool.orm.impl.mapping;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.SystemPropertyUtils;
import ru.kwanza.dbtool.orm.annotations.Entity;
import ru.kwanza.dbtool.orm.springintegration.DBToolOrmNamespaceHandler;

import java.io.IOException;
import java.util.Collection;

/**
 * @author Kiryl Karatsetski
 */
public class SpringEntityMappingRegistryImpl implements IEntityMappingRegistry {

    private final Log log = LogFactory.getLog(DBToolOrmNamespaceHandler.class);

    private static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";

    private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
    private MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resourcePatternResolver);

    private IEntityMappingRegistry delegate;

    public SpringEntityMappingRegistryImpl(IEntityMappingRegistry delegate, String[] basePackages) {
        this.delegate = delegate;
        for (String basePackage : basePackages) {
            scanPackage(basePackage);
        }
    }

    private void scanPackage(String basePackage) {
        try {
            final String packageSearchPath = createPackageSearchPath(basePackage);
            final Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);
            for (Resource resource : resources) {
                if (log.isTraceEnabled()) {
                    log.trace("Scanning " + resource);
                }
                if (resource.isReadable()) {
                    try {
                        final MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                        final ClassMetadata classMetadata = metadataReader.getClassMetadata();
                        final String className = classMetadata.getClassName();
                        if (!classMetadata.isAbstract() && !Enum.class.getName().equals(classMetadata.getSuperClassName())) {
                            log.trace(className);
                            final ClassLoader classLoader = resourcePatternResolver.getClassLoader();
                            Class<?> entityClass = classLoader.loadClass(className);
                            if (entityClass.isAnnotationPresent(Entity.class)) {
                                delegate.registerEntityClass(entityClass);
                            }
                        }
                    } catch (Throwable e) {
                        throw new RuntimeException("Error while registering entity mapping: " + resource, e);
                    }
                } else {
                    if (log.isTraceEnabled()) {
                        log.trace("Ignored because not readable: " + resource);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("I/O failure during classpath scanning", e);
        }
    }

    private String createPackageSearchPath(String basePackage) {
        return ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + resolveBasePackage(basePackage) + "/" + DEFAULT_RESOURCE_PATTERN;
    }

    protected String resolveBasePackage(String basePackage) {
        return ClassUtils.convertClassNameToResourcePath(SystemPropertyUtils.resolvePlaceholders(basePackage));
    }

    public void registerEntityClass(Class entityClass) {
        delegate.registerEntityClass(entityClass);
    }

    public boolean isRegisteredEntityClass(Class entityClass) {
        return delegate.isRegisteredEntityClass(entityClass);
    }

    public boolean isRegisteredEntityName(String entityName) {
        return delegate.isRegisteredEntityName(entityName);
    }

    public String getTableName(Class entityClass) {
        return delegate.getTableName(entityClass);
    }

    public String getTableName(String entityName) {
        return delegate.getTableName(entityName);
    }

    public String getEntityName(Class entityClass) {
        return delegate.getEntityName(entityClass);
    }

    public Class getEntityClass(String entityName) {
        return delegate.getEntityClass(entityName);
    }

    public Collection<String> getColumnNames(Class entityClass) {
        return delegate.getColumnNames(entityClass);
    }

    public Collection<String> getColumnNames(String entityName) {
        return delegate.getColumnNames(entityName);
    }

    public Collection<FieldMapping> getFieldMappings(Class entityClass) {
        return delegate.getFieldMappings(entityClass);
    }

    public Collection<FieldMapping> getFieldMappings(String entityName) {
        return delegate.getFieldMappings(entityName);
    }

    public Collection<FieldMapping> getIdFields(Class entityClass) {
        return delegate.getIdFields(entityClass);
    }

    public Collection<FieldMapping> getIdFields(String entityName) {
        return delegate.getIdFields(entityName);
    }

    public FieldMapping getVersionField(Class entityClass) {
        return delegate.getVersionField(entityClass);
    }

    public FieldMapping getVersionField(String entityName) {
        return delegate.getVersionField(entityName);
    }

    public Collection<RelationMapping> getRelationMappings(Class entityClass) {
        return delegate.getRelationMappings(entityClass);
    }

    public Collection<RelationMapping> getRelationMappings(String entityName) {
        return delegate.getRelationMappings(entityName);
    }

    public FieldMapping getFieldMapping(Class entityClass, String propertyName) {
        return delegate.getFieldMapping(entityClass, propertyName);
    }

    public FieldMapping getFieldMapping(String entityName, String propertyName) {
        return delegate.getFieldMapping(entityName, propertyName);
    }

    public RelationMapping getRelationMapping(Class entityClass, String propertyName) {
        return delegate.getRelationMapping(entityClass, propertyName);
    }

    public RelationMapping getRelationMapping(String entityName, String propertyName) {
        return delegate.getRelationMapping(entityName, propertyName);
    }
}
