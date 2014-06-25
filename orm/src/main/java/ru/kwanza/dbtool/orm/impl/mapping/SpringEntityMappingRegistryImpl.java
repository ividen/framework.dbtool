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
import ru.kwanza.dbtool.orm.annotations.AbstractEntity;
import ru.kwanza.dbtool.orm.annotations.Entity;
import ru.kwanza.dbtool.orm.api.internal.IEntityMappingRegistry;
import ru.kwanza.dbtool.orm.api.internal.IEntityType;
import ru.kwanza.dbtool.orm.springintegration.DBToolOrmNamespaceHandler;

import java.io.IOException;

/**
 * @author Kiryl Karatsetski
 */
public class SpringEntityMappingRegistryImpl implements IEntityMappingRegistry {

    private final Log log = LogFactory.getLog(DBToolOrmNamespaceHandler.class);

    private static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";

    private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
    private MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resourcePatternResolver);

    private EntityMappingRegistry delegate;

    public SpringEntityMappingRegistryImpl(EntityMappingRegistry delegate, String[] basePackages) {
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
                            if (entityClass.isAnnotationPresent(Entity.class) || entityClass.isAnnotationPresent(AbstractEntity.class)) {
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

    public IEntityType registerEntityClass(Class entityClass) {
        return delegate.registerEntityClass(entityClass);
    }

    public boolean isRegisteredEntityClass(Class entityClass) {
        return delegate.isRegisteredEntityClass(entityClass);
    }

    public boolean isRegisteredEntityName(String entityName) {
        return delegate.isRegisteredEntityName(entityName);
    }

    public IEntityType getEntityType(String name) {
        return delegate.getEntityType(name);
    }

    public IEntityType getEntityType(Class entityClass) {
        return delegate.getEntityType(entityClass);
    }
}
