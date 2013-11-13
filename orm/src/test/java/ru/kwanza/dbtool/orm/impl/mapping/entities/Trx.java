package ru.kwanza.dbtool.orm.impl.mapping.entities;

import ru.kwanza.dbtool.orm.annotations.Field;

import java.util.Date;

/**
 * @author Kiryl Karatsetski
 */
public abstract class Trx extends AbstractEntity {

    @Field( "parent_trx_id")
    private Long parentTrxId;

    @Field( "started_at")
    private Date startedAt;

    @Field( "finished_at")
    private Date finishedAt;

    @Field( "result_code")
    private Integer resultCode;

    @Field( "extended_code")
    private Integer extendedCode;

    protected Trx() {
    }

    protected Trx(Long id, String pcid) {
        super(id, pcid);
    }

    public Long getParentTrxId() {
        return parentTrxId;
    }

    public void setParentTrxId(Long parentTrxId) {
        this.parentTrxId = parentTrxId;
    }

    public Date getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Date startedAt) {
        this.startedAt = startedAt;
    }

    public Date getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(Date finishedAt) {
        this.finishedAt = finishedAt;
    }

    public Integer getResultCode() {
        return resultCode;
    }

    public void setResultCode(Integer resultCode) {
        this.resultCode = resultCode;
    }

    public Integer getExtendedCode() {
        return extendedCode;
    }

    public void setExtendedCode(Integer extendedCode) {
        this.extendedCode = extendedCode;
    }
}
