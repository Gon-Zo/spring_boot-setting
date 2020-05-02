package com.app.api.domain.product.support;

import com.app.api.domain.product.Product;
import com.app.api.utils.ApiDomainUtils;
import com.app.api.web.dto.PageableDto;
import com.app.api.web.dto.ProductResponseDto;
import com.querydsl.core.QueryResults;
import com.querydsl.core.dml.UpdateClause;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAUpdateClause;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

import static com.app.api.domain.product.QProduct.product;
import static org.apache.commons.lang3.ObjectUtils.compare;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;
import static com.app.api.utils.ApiDomainUtils.isNotEmpty;


@Repository
public class ProductSupportImpl extends QuerydslRepositorySupport  implements ProductSupport {

    private final JPAQueryFactory jpaQueryFactory;

    private final EntityManager  entityManager;

    public ProductSupportImpl(JPAQueryFactory jpaQueryFactory, EntityManager entityManager) {
        super(Product.class);
        this.jpaQueryFactory = jpaQueryFactory;
        this.entityManager = entityManager;
    }

    @Override
    public void update(long seq, ProductResponseDto dto) {
        UpdateClause<JPAUpdateClause> update = update(product);

        if (isNotEmpty(dto.getTitle())) {
            update.set(product.title, dto.getTitle());
        }

        if (isNotEmpty(dto.getCnt())) {
            update.set(product.cnt, dto.getCnt());
        }

        if (isNotEmpty(dto.getPrice())) {
            update.set(product.price, dto.getPrice());
        }

        if (isNotEmpty(dto.getInfo())) {
            update.set(product.info, dto.getInfo());
        }

        if (isNotEmpty(dto.getIsSold())) {
            update.set(product.isSold, dto.getIsSold());
        }

        update.where(product.seq.eq(seq)).execute();
    }

    @Override
    public Page<Product> findByProducts(PageableDto dto) {

        JPAQuery<Product> query = jpaQueryFactory.selectFrom(product);

        if (isNotEmpty(dto.getSort())) {
            query.orderBy(ApiDomainUtils.getOrder(dto.getSort()).toArray(new OrderSpecifier[0]));
        }

        QueryResults<Product> results = query.fetchResults();

        long total = results.getTotal();

        List<Product> result = results.getResults();

        Pageable pageable = PageRequest.of(dto.getPage(), dto.getSize());

        return new PageImpl<>(result, pageable, total);

    }


}
