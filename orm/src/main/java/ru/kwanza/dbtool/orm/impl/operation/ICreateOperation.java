package ru.kwanza.dbtool.orm.impl.operation;

import ru.kwanza.dbtool.core.UpdateException;

import java.util.Collection;

/**
 * @author Kiryl Karatsetski
 */
public interface ICreateOperation {

    void executeCreate(Object object) throws UpdateException;

    void executeCreate(Collection objects) throws UpdateException;
}
