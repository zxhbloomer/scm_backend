package com.xinyirun.scm.ai.bean.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AiTokenUsageExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public AiTokenUsageExample() {
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

        public Criteria andConversationIdIsNull() {
            addCriterion("conversation_id is null");
            return (Criteria) this;
        }

        public Criteria andConversationIdIsNotNull() {
            addCriterion("conversation_id is not null");
            return (Criteria) this;
        }

        public Criteria andConversationIdEqualTo(String value) {
            addCriterion("conversation_id =", value, "conversationId");
            return (Criteria) this;
        }

        public Criteria andConversationIdNotEqualTo(String value) {
            addCriterion("conversation_id <>", value, "conversationId");
            return (Criteria) this;
        }

        public Criteria andConversationIdLike(String value) {
            addCriterion("conversation_id like", value, "conversationId");
            return (Criteria) this;
        }

        public Criteria andConversationIdNotLike(String value) {
            addCriterion("conversation_id not like", value, "conversationId");
            return (Criteria) this;
        }

        public Criteria andConversationIdIn(List<String> values) {
            addCriterion("conversation_id in", values, "conversationId");
            return (Criteria) this;
        }

        public Criteria andConversationIdNotIn(List<String> values) {
            addCriterion("conversation_id not in", values, "conversationId");
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

        public Criteria andPromptTokensIsNull() {
            addCriterion("prompt_tokens is null");
            return (Criteria) this;
        }

        public Criteria andPromptTokensIsNotNull() {
            addCriterion("prompt_tokens is not null");
            return (Criteria) this;
        }

        public Criteria andPromptTokensEqualTo(Long value) {
            addCriterion("prompt_tokens =", value, "promptTokens");
            return (Criteria) this;
        }

        public Criteria andPromptTokensNotEqualTo(Long value) {
            addCriterion("prompt_tokens <>", value, "promptTokens");
            return (Criteria) this;
        }

        public Criteria andPromptTokensGreaterThan(Long value) {
            addCriterion("prompt_tokens >", value, "promptTokens");
            return (Criteria) this;
        }

        public Criteria andPromptTokensGreaterThanOrEqualTo(Long value) {
            addCriterion("prompt_tokens >=", value, "promptTokens");
            return (Criteria) this;
        }

        public Criteria andPromptTokensLessThan(Long value) {
            addCriterion("prompt_tokens <", value, "promptTokens");
            return (Criteria) this;
        }

        public Criteria andPromptTokensLessThanOrEqualTo(Long value) {
            addCriterion("prompt_tokens <=", value, "promptTokens");
            return (Criteria) this;
        }

        public Criteria andPromptTokensIn(List<Long> values) {
            addCriterion("prompt_tokens in", values, "promptTokens");
            return (Criteria) this;
        }

        public Criteria andPromptTokensNotIn(List<Long> values) {
            addCriterion("prompt_tokens not in", values, "promptTokens");
            return (Criteria) this;
        }

        public Criteria andPromptTokensBetween(Long value1, Long value2) {
            addCriterion("prompt_tokens between", value1, value2, "promptTokens");
            return (Criteria) this;
        }

        public Criteria andPromptTokensNotBetween(Long value1, Long value2) {
            addCriterion("prompt_tokens not between", value1, value2, "promptTokens");
            return (Criteria) this;
        }

        public Criteria andCompletionTokensIsNull() {
            addCriterion("completion_tokens is null");
            return (Criteria) this;
        }

        public Criteria andCompletionTokensIsNotNull() {
            addCriterion("completion_tokens is not null");
            return (Criteria) this;
        }

        public Criteria andCompletionTokensEqualTo(Long value) {
            addCriterion("completion_tokens =", value, "completionTokens");
            return (Criteria) this;
        }

        public Criteria andCompletionTokensNotEqualTo(Long value) {
            addCriterion("completion_tokens <>", value, "completionTokens");
            return (Criteria) this;
        }

        public Criteria andCompletionTokensGreaterThan(Long value) {
            addCriterion("completion_tokens >", value, "completionTokens");
            return (Criteria) this;
        }

        public Criteria andCompletionTokensGreaterThanOrEqualTo(Long value) {
            addCriterion("completion_tokens >=", value, "completionTokens");
            return (Criteria) this;
        }

        public Criteria andCompletionTokensLessThan(Long value) {
            addCriterion("completion_tokens <", value, "completionTokens");
            return (Criteria) this;
        }

        public Criteria andCompletionTokensLessThanOrEqualTo(Long value) {
            addCriterion("completion_tokens <=", value, "completionTokens");
            return (Criteria) this;
        }

        public Criteria andCompletionTokensIn(List<Long> values) {
            addCriterion("completion_tokens in", values, "completionTokens");
            return (Criteria) this;
        }

        public Criteria andCompletionTokensNotIn(List<Long> values) {
            addCriterion("completion_tokens not in", values, "completionTokens");
            return (Criteria) this;
        }

        public Criteria andCompletionTokensBetween(Long value1, Long value2) {
            addCriterion("completion_tokens between", value1, value2, "completionTokens");
            return (Criteria) this;
        }

        public Criteria andCompletionTokensNotBetween(Long value1, Long value2) {
            addCriterion("completion_tokens not between", value1, value2, "completionTokens");
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

        public Criteria andUsageTimeIsNull() {
            addCriterion("usage_time is null");
            return (Criteria) this;
        }

        public Criteria andUsageTimeIsNotNull() {
            addCriterion("usage_time is not null");
            return (Criteria) this;
        }

        public Criteria andUsageTimeEqualTo(LocalDateTime value) {
            addCriterion("usage_time =", value, "usageTime");
            return (Criteria) this;
        }

        public Criteria andUsageTimeNotEqualTo(LocalDateTime value) {
            addCriterion("usage_time <>", value, "usageTime");
            return (Criteria) this;
        }

        public Criteria andUsageTimeGreaterThan(LocalDateTime value) {
            addCriterion("usage_time >", value, "usageTime");
            return (Criteria) this;
        }

        public Criteria andUsageTimeGreaterThanOrEqualTo(LocalDateTime value) {
            addCriterion("usage_time >=", value, "usageTime");
            return (Criteria) this;
        }

        public Criteria andUsageTimeLessThan(LocalDateTime value) {
            addCriterion("usage_time <", value, "usageTime");
            return (Criteria) this;
        }

        public Criteria andUsageTimeLessThanOrEqualTo(LocalDateTime value) {
            addCriterion("usage_time <=", value, "usageTime");
            return (Criteria) this;
        }

        public Criteria andUsageTimeIn(List<LocalDateTime> values) {
            addCriterion("usage_time in", values, "usageTime");
            return (Criteria) this;
        }

        public Criteria andUsageTimeNotIn(List<LocalDateTime> values) {
            addCriterion("usage_time not in", values, "usageTime");
            return (Criteria) this;
        }

        public Criteria andUsageTimeBetween(LocalDateTime value1, LocalDateTime value2) {
            addCriterion("usage_time between", value1, value2, "usageTime");
            return (Criteria) this;
        }

        public Criteria andUsageTimeNotBetween(LocalDateTime value1, LocalDateTime value2) {
            addCriterion("usage_time not between", value1, value2, "usageTime");
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