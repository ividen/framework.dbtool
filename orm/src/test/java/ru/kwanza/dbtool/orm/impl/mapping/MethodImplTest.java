package ru.kwanza.dbtool.orm.impl.mapping;

/**
 * @author Kiryl Karatsetski
 */
public class MethodImplTest extends EntityFieldTest {

    @Override
    protected EntityField getEntityField() throws Exception {
        return MethodImpl.create(getGetter(), getSetter());
    }
}
