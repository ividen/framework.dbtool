package ru.kwanza.dbtool.orm;

import java.util.Collection;

/**
 * @author Alexander Guzanov
 */
public interface IFetcher {

    /**
     * Fetch relations for items. <br>
     * Example:  <br>
     * <code>
     *     IEntityManager em = ....   <br>
     *     IQuery&lt;PaymentTrx&gt; query = em.queryFor(PaymentTrx).setMaxSize(10000).orderBy(OrderBy.ASC("createAt").create(); <br>
     *     Collection&lt;PaymentTrx&gt; items = query.selectList() <br>
     *     IFetcher em.getFetcher();  <br>
     *     fetcher.fetch(PaymentTrx.class, items, "terminal{agent, subAgent{agent}}, agent, subAgent"   <br>
     *
     * </code>
     *
     *
     * @param entityClass - class of objects in items
     * @param items - fetch relation for this items
     * @param relationPath - relationPath of relations, example: "terminal{agent, subAgent{agent}}"
     *
     */
    public <T> void fetch(Class<T> entityClass, Collection<T> items, String relationPath);

}
