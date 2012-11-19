package ru.kwanza.dbtool.orm.impl.operation;

import ru.kwanza.dbtool.core.UpdateException;

import java.util.Collection;

/**
 * @author Kiryl Karatsetski
 */
public interface IUpdateOperation {

    void executeUpdate(Object object) throws UpdateException;

    void executeUpdate(Collection objects) throws UpdateException;
}
