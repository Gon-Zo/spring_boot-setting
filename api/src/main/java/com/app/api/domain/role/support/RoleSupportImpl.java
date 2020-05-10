package com.app.api.domain.role.support;

import com.app.api.domain.role.Role;
import com.app.api.web.dto.PageableDto;
import com.app.api.web.dto.RoleResponseDto;
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
import javax.transaction.Transactional;

import java.util.List;

import static com.app.api.domain.role.QRole.role;
import static com.app.api.utils.ApiDomainUtils.isNotEmpty;
import static com.app.api.utils.ApiDomainUtils.getOrder;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static com.app.api.utils.ApiDomainUtils.isNotEmpty;

@Repository
public class RoleSupportImpl extends QuerydslRepositorySupport implements RoleSupport {

    private final EntityManager entityManager;
    private final JPAQueryFactory jpaQueryFactory;

    public RoleSupportImpl(EntityManager entityManager, JPAQueryFactory jpaQueryFactory) {
        super(Role.class);
        this.entityManager = entityManager;
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public List<Role> findByTitles(List<String> titles) {
        return jpaQueryFactory.selectFrom(role)
                .where(role.title.in(titles)).fetch();
    }

    @Override
    public Page<Role> findByPages(PageableDto dto) {

        JPAQuery<Role> query = jpaQueryFactory.selectFrom(role);

        if (isNotEmpty(dto.getSort())) {
            query.orderBy(getOrder(dto.getSort()).toArray(new OrderSpecifier[0]));
        }

        Pageable pageable = PageRequest.of(dto.getPage(), dto.getSize());

        query.limit(pageable.getPageSize()).offset(pageable.getOffset());

        QueryResults<Role> fetchResults = query.fetchResults();

        List<Role> result = fetchResults.getResults();

        long total = fetchResults.getTotal();

        return new PageImpl<>(result, pageable, total);
    }

    @Override
    @Transactional
    public void update( long seq , RoleResponseDto dto) {

        UpdateClause<JPAUpdateClause> updateCase = update(role);

        if(isNotEmpty(dto.getTitle())){
            updateCase.set(role.title , dto.getTitle());
        }

        if (isNotEmpty(dto.getMenus())) {
            updateCase.set(role.menus, dto.getMenus());
        }

        if(isNotEmpty(dto.getAuthUrl())){
            updateCase.set(role.authUrl , dto.getAuthUrl());
        }

        updateCase.where(role.seq.eq(seq));

        updateCase.execute();

    }

}
