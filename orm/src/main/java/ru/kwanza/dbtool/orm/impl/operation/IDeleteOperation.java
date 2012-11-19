package ru.kwanza.dbtool.orm.impl.operation;

import ru.kwanza.dbtool.core.UpdateException;

import java.util.Collection;

/**
 * @author Kiryl Karatsetski
 */
public interface IDeleteOperation {

    void executeDelete(Object object) throws UpdateException;

    void executeDelete(Collection objects) throws UpdateException;

    void executeDeleteByKey(Object key) throws UpdateException;

    void executeDeleteByKeys(Collection keys) throws UpdateException;
}
