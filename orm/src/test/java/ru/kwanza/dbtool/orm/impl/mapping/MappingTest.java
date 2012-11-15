package ru.kwanza.dbtool.orm.impl.mapping;

import ru.kwanza.dbtool.orm.entity.TestEntity1;
import ru.kwanza.dbtool.orm.impl.fetcher.*;

/**
 * @author Kiryl Karatsetski
 */
public class MappingTest {

    public static void main(String[] args) {
        EntityMappingRegistryImpl mappingRegistry = new EntityMappingRegistryImpl();
        mappingRegistry.registerEntityClass(TestEntity1.class);

        mappingRegistry.registerEntityClass(TestEntity.class);
        mappingRegistry.registerEntityClass(TestEntityA.class);
        mappingRegistry.registerEntityClass(TestEntityB.class);
        mappingRegistry.registerEntityClass(TestEntityC.class);
        mappingRegistry.registerEntityClass(TestEntityD.class);
        mappingRegistry.registerEntityClass(TestEntityF.class);
        mappingRegistry.registerEntityClass(TestEntityG.class);

        System.out.println();
    }
}
