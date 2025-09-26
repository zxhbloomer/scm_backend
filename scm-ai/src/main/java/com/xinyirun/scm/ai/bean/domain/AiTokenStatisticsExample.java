package com.xinyirun.scm.ai.bean.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AiTokenStatisticsExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public AiTokenStatisticsExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andIdIsNull() {
            addCriterion("id is null");
            return (Criteria) this;
        }

        public Criteria andIdIsNotNull() {
            addCriterion("id is not null");
            return (Criteria) this;
        }

        public Criteria andIdEqualTo(String value) {
            addCriterion("id =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(String value) {
            addCriterion("id <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(String value) {
            addCriterion("id >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(String value) {
            addCriterion("id >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(String value) {
            addCriterion("id <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(String value) {
            addCriterion("id <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLike(String value) {
            addCriterion("id like", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotLike(String value) {
            addCriterion("id not like", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<String> values) {
            addCriterion("id in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<String> values) {
            addCriterion("id not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(String value1, String value2) {
            addCriterion("id between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(String value1, String value2) {
            addCriterion("id not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andUserIdIsNull() {
            addCriterion("user_id is null");
            return (Criteria) this;
        }

        public Criteria andUserIdIsNotNull() {
            addCriterion("user_id is not null");
            return (Criteria) this;
        }

        public Criteria andUserIdEqualTo(String value) {
            addCriterion("user_id =", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdNotEqualTo(String value) {
            addCriterion("user_id <>", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdLike(String value) {
            addCriterion("user_id like", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdNotLike(String value) {
            addCriterion("user_id not like", value, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdIn(List<String> values) {
            addCriterion("user_id in", values, "userId");
            return (Criteria) this;
        }

        public Criteria andUserIdNotIn(List<String> values) {
            addCriterion("user_id not in", values, "userId");
            return (Criteria) this;
        }

        public Criteria andTenantIsNull() {
            addCriterion("tenant is null");
            return (Criteria) this;
        }

        public Criteria andTenantIsNotNull() {
            addCriterion("tenant is not null");
            return (Criteria) this;
        }

        public Criteria andTenantEqualTo(String value) {
            addCriterion("tenant =", value, "tenant");
            return (Criteria) this;
        }

        public Criteria andTenantNotEqualTo(String value) {
            addCriterion("tenant <>", value, "tenant");
            return (Criteria) this;
        }

        public Criteria andTenantLike(String value) {
            addCriterion("tenant like", value, "tenant");
            return (Criteria) this;
        }

        public Criteria andTenantNotLike(String value) {
            addCriterion("tenant not like", value, "tenant");
            return (Criteria) this;
        }

        public Criteria andTenantIn(List<String> values) {
            addCriterion("tenant in", values, "tenant");
            return (Criteria) this;
        }

        public Criteria andTenantNotIn(List<String> values) {
            addCriterion("tenant not in", values, "tenant");
            return (Criteria) this;
        }

        public Criteria andStatTypeIsNull() {
            addCriterion("stat_type is null");
            return (Criteria) this;
        }

        public Criteria andStatTypeIsNotNull() {
            addCriterion("stat_type is not null");
            return (Criteria) this;
        }

        public Criteria andStatTypeEqualTo(String value) {
            addCriterion("stat_type =", value, "statType");
            return (Criteria) this;
        }

        public Criteria andStatTypeNotEqualTo(String value) {
            addCriterion("stat_type <>", value, "statType");
            return (Criteria) this;
        }

        public Criteria andStatTypeLike(String value) {
            addCriterion("stat_type like", value, "statType");
            return (Criteria) this;
        }

        public Criteria andStatTypeNotLike(String value) {
            addCriterion("stat_type not like", value, "statType");
            return (Criteria) this;
        }

        public Criteria andStatTypeIn(List<String> values) {
            addCriterion("stat_type in", values, "statType");
            return (Criteria) this;
        }

        public Criteria andStatTypeNotIn(List<String> values) {
            addCriterion("stat_type not in", values, "statType");
            return (Criteria) this;
        }

        public Criteria andStatDateIsNull() {
            addCriterion("stat_date is null");
            return (Criteria) this;
        }

        public Criteria andStatDateIsNotNull() {
            addCriterion("stat_date is not null");
            return (Criteria) this;
        }

        public Criteria andStatDateEqualTo(LocalDate value) {
            addCriterion("stat_date =", value, "statDate");
            return (Criteria) this;
        }

        public Criteria andStatDateNotEqualTo(LocalDate value) {
            addCriterion("stat_date <>", value, "statDate");
            return (Criteria) this;
        }

        public Criteria andStatDateGreaterThan(LocalDate value) {
            addCriterion("stat_date >", value, "statDate");
            return (Criteria) this;
        }

        public Criteria andStatDateGreaterThanOrEqualTo(LocalDate value) {
            addCriterion("stat_date >=", value, "statDate");
            return (Criteria) this;
        }

        public Criteria andStatDateLessThan(LocalDate value) {
            addCriterion("stat_date <", value, "statDate");
            return (Criteria) this;
        }

        public Criteria andStatDateLessThanOrEqualTo(LocalDate value) {
            addCriterion("stat_date <=", value, "statDate");
            return (Criteria) this;
        }

        public Criteria andStatDateIn(List<LocalDate> values) {
            addCriterion("stat_date in", values, "statDate");
            return (Criteria) this;
        }

        public Criteria andStatDateNotIn(List<LocalDate> values) {
            addCriterion("stat_date not in", values, "statDate");
            return (Criteria) this;
        }

        public Criteria andStatDateBetween(LocalDate value1, LocalDate value2) {
            addCriterion("stat_date between", value1, value2, "statDate");
            return (Criteria) this;
        }

        public Criteria andStatDateNotBetween(LocalDate value1, LocalDate value2) {
            addCriterion("stat_date not between", value1, value2, "statDate");
            return (Criteria) this;
        }

        public Criteria andModelNameIsNull() {
            addCriterion("model_name is null");
            return (Criteria) this;
        }

        public Criteria andModelNameIsNotNull() {
            addCriterion("model_name is not null");
            return (Criteria) this;
        }

        public Criteria andModelNameEqualTo(String value) {
            addCriterion("model_name =", value, "modelName");
            return (Criteria) this;
        }

        public Criteria andModelNameNotEqualTo(String value) {
            addCriterion("model_name <>", value, "modelName");
            return (Criteria) this;
        }

        public Criteria andModelNameLike(String value) {
            addCriterion("model_name like", value, "modelName");
            return (Criteria) this;
        }

        public Criteria andModelNameNotLike(String value) {
            addCriterion("model_name not like", value, "modelName");
            return (Criteria) this;
        }

        public Criteria andModelNameIn(List<String> values) {
            addCriterion("model_name in", values, "modelName");
            return (Criteria) this;
        }

        public Criteria andModelNameNotIn(List<String> values) {
            addCriterion("model_name not in", values, "modelName");
            return (Criteria) this;
        }

        public Criteria andRequestCountIsNull() {
            addCriterion("request_count is null");
            return (Criteria) this;
        }

        public Criteria andRequestCountIsNotNull() {
            addCriterion("request_count is not null");
            return (Criteria) this;
        }

        public Criteria andRequestCountEqualTo(Long value) {
            addCriterion("request_count =", value, "requestCount");
            return (Criteria) this;
        }

        public Criteria andRequestCountNotEqualTo(Long value) {
            addCriterion("request_count <>", value, "requestCount");
            return (Criteria) this;
        }

        public Criteria andRequestCountGreaterThan(Long value) {
            addCriterion("request_count >", value, "requestCount");
            return (Criteria) this;
        }

        public Criteria andRequestCountGreaterThanOrEqualTo(Long value) {
            addCriterion("request_count >=", value, "requestCount");
            return (Criteria) this;
        }

        public Criteria andRequestCountLessThan(Long value) {
            addCriterion("request_count <", value, "requestCount");
            return (Criteria) this;
        }

        public Criteria andRequestCountLessThanOrEqualTo(Long value) {
            addCriterion("request_count <=", value, "requestCount");
            return (Criteria) this;
        }

        public Criteria andRequestCountIn(List<Long> values) {
            addCriterion("request_count in", values, "requestCount");
            return (Criteria) this;
        }

        public Criteria andRequestCountNotIn(List<Long> values) {
            addCriterion("request_count not in", values, "requestCount");
            return (Criteria) this;
        }

        public Criteria andRequestCountBetween(Long value1, Long value2) {
            addCriterion("request_count between", value1, value2, "requestCount");
            return (Criteria) this;
        }

        public Criteria andRequestCountNotBetween(Long value1, Long value2) {
            addCriterion("request_count not between", value1, value2, "requestCount");
            return (Criteria) this;
        }

        public Criteria andTotalTokensIsNull() {
            addCriterion("total_tokens is null");
            return (Criteria) this;
        }

        public Criteria andTotalTokensIsNotNull() {
            addCriterion("total_tokens is not null");
            return (Criteria) this;
        }

        public Criteria andTotalTokensEqualTo(Long value) {
            addCriterion("total_tokens =", value, "totalTokens");
            return (Criteria) this;
        }

        public Criteria andTotalTokensNotEqualTo(Long value) {
            addCriterion("total_tokens <>", value, "totalTokens");
            return (Criteria) this;
        }

        public Criteria andTotalTokensGreaterThan(Long value) {
            addCriterion("total_tokens >", value, "totalTokens");
            return (Criteria) this;
        }

        public Criteria andTotalTokensGreaterThanOrEqualTo(Long value) {
            addCriterion("total_tokens >=", value, "totalTokens");
            return (Criteria) this;
        }

        public Criteria andTotalTokensLessThan(Long value) {
            addCriterion("total_tokens <", value, "totalTokens");
            return (Criteria) this;
        }

        public Criteria andTotalTokensLessThanOrEqualTo(Long value) {
            addCriterion("total_tokens <=", value, "totalTokens");
            return (Criteria) this;
        }

        public Criteria andTotalTokensIn(List<Long> values) {
            addCriterion("total_tokens in", values, "totalTokens");
            return (Criteria) this;
        }

        public Criteria andTotalTokensNotIn(List<Long> values) {
            addCriterion("total_tokens not in", values, "totalTokens");
            return (Criteria) this;
        }

        public Criteria andTotalTokensBetween(Long value1, Long value2) {
            addCriterion("total_tokens between", value1, value2, "totalTokens");
            return (Criteria) this;
        }

        public Criteria andTotalTokensNotBetween(Long value1, Long value2) {
            addCriterion("total_tokens not between", value1, value2, "totalTokens");
            return (Criteria) this;
        }

        public Criteria andTotalCostIsNull() {
            addCriterion("total_cost is null");
            return (Criteria) this;
        }

        public Criteria andTotalCostIsNotNull() {
            addCriterion("total_cost is not null");
            return (Criteria) this;
        }

        public Criteria andTotalCostEqualTo(BigDecimal value) {
            addCriterion("total_cost =", value, "totalCost");
            return (Criteria) this;
        }

        public Criteria andTotalCostNotEqualTo(BigDecimal value) {
            addCriterion("total_cost <>", value, "totalCost");
            return (Criteria) this;
        }

        public Criteria andTotalCostGreaterThan(BigDecimal value) {
            addCriterion("total_cost >", value, "totalCost");
            return (Criteria) this;
        }

        public Criteria andTotalCostGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("total_cost >=", value, "totalCost");
            return (Criteria) this;
        }

        public Criteria andTotalCostLessThan(BigDecimal value) {
            addCriterion("total_cost <", value, "totalCost");
            return (Criteria) this;
        }

        public Criteria andTotalCostLessThanOrEqualTo(BigDecimal value) {
            addCriterion("total_cost <=", value, "totalCost");
            return (Criteria) this;
        }

        public Criteria andTotalCostIn(List<BigDecimal> values) {
            addCriterion("total_cost in", values, "totalCost");
            return (Criteria) this;
        }

        public Criteria andTotalCostNotIn(List<BigDecimal> values) {
            addCriterion("total_cost not in", values, "totalCost");
            return (Criteria) this;
        }

        public Criteria andTotalCostBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("total_cost between", value1, value2, "totalCost");
            return (Criteria) this;
        }

        public Criteria andTotalCostNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("total_cost not between", value1, value2, "totalCost");
            return (Criteria) this;
        }

        public Criteria andCreatedAtIsNull() {
            addCriterion("created_at is null");
            return (Criteria) this;
        }

        public Criteria andCreatedAtIsNotNull() {
            addCriterion("created_at is not null");
            return (Criteria) this;
        }

        public Criteria andCreatedAtEqualTo(LocalDateTime value) {
            addCriterion("created_at =", value, "createdAt");
            return (Criteria) this;
        }

        public Criteria andCreatedAtNotEqualTo(LocalDateTime value) {
            addCriterion("created_at <>", value, "createdAt");
            return (Criteria) this;
        }

        public Criteria andCreatedAtGreaterThan(LocalDateTime value) {
            addCriterion("created_at >", value, "createdAt");
            return (Criteria) this;
        }

        public Criteria andCreatedAtGreaterThanOrEqualTo(LocalDateTime value) {
            addCriterion("created_at >=", value, "createdAt");
            return (Criteria) this;
        }

        public Criteria andCreatedAtLessThan(LocalDateTime value) {
            addCriterion("created_at <", value, "createdAt");
            return (Criteria) this;
        }

        public Criteria andCreatedAtLessThanOrEqualTo(LocalDateTime value) {
            addCriterion("created_at <=", value, "createdAt");
            return (Criteria) this;
        }

        public Criteria andCreatedAtIn(List<LocalDateTime> values) {
            addCriterion("created_at in", values, "createdAt");
            return (Criteria) this;
        }

        public Criteria andCreatedAtNotIn(List<LocalDateTime> values) {
            addCriterion("created_at not in", values, "createdAt");
            return (Criteria) this;
        }

        public Criteria andCreatedAtBetween(LocalDateTime value1, LocalDateTime value2) {
            addCriterion("created_at between", value1, value2, "createdAt");
            return (Criteria) this;
        }

        public Criteria andCreatedAtNotBetween(LocalDateTime value1, LocalDateTime value2) {
            addCriterion("created_at not between", value1, value2, "createdAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtIsNull() {
            addCriterion("updated_at is null");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtIsNotNull() {
            addCriterion("updated_at is not null");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtEqualTo(LocalDateTime value) {
            addCriterion("updated_at =", value, "updatedAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtNotEqualTo(LocalDateTime value) {
            addCriterion("updated_at <>", value, "updatedAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtGreaterThan(LocalDateTime value) {
            addCriterion("updated_at >", value, "updatedAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtGreaterThanOrEqualTo(LocalDateTime value) {
            addCriterion("updated_at >=", value, "updatedAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtLessThan(LocalDateTime value) {
            addCriterion("updated_at <", value, "updatedAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtLessThanOrEqualTo(LocalDateTime value) {
            addCriterion("updated_at <=", value, "updatedAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtIn(List<LocalDateTime> values) {
            addCriterion("updated_at in", values, "updatedAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtNotIn(List<LocalDateTime> values) {
            addCriterion("updated_at not in", values, "updatedAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtBetween(LocalDateTime value1, LocalDateTime value2) {
            addCriterion("updated_at between", value1, value2, "updatedAt");
            return (Criteria) this;
        }

        public Criteria andUpdatedAtNotBetween(LocalDateTime value1, LocalDateTime value2) {
            addCriterion("updated_at not between", value1, value2, "updatedAt");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}