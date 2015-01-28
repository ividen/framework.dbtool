package ru.kwanza.dbtool.core.lock;

/*
 * #%L
 * dbtool-core
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

import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import java.sql.SQLException;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Ivan Baluk
 */
@ContextConfiguration(locations = "classpath:base-oracle-config.xml")
public class TestOracleLock extends TestMySQLLock {

    @Override
    protected AppLock createLockForDeadLockTest(String name) throws SQLException {
        return new DefaultAppLock(dbTool,name,new ReentrantLock(),true);
    }




}
