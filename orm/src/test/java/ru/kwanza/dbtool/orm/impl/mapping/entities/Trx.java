package ru.kwanza.dbtool.orm.impl.mapping.entities;

import ru.kwanza.dbtool.orm.annotations.Field;
import ru.kwanza.dbtool.orm.annotations.VersionField;

import java.util.Date;

/**
 * @author Kiryl Karatsetski
 */
public abstract class Trx extends AbstractEntity {

    @Field(columnName = "parent_trx_id")
    private Long parentTrxId;

    @Field(columnName = "started_at")
    private Date startedAt;

    @Field(columnName = "finished_at")
    private Date finishedAt;

    @Field(columnName = "result_code")
    private Integer resultCode;

    @Field(columnName = "extended_code")
    private Integer extendedCode;

    @VersionField(columnName = "version")
    private Long version;
}
