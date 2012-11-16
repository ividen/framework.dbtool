package ru.kwanza.dbtool.orm.impl.mapping;

/**
 * @author Kiryl Karatsetski
 */
public class FieldImplTest extends EntityFieldTest {

    @Override
    protected EntityField getEntityField() throws Exception {
        return FieldImpl.create(getField());
    }
}
