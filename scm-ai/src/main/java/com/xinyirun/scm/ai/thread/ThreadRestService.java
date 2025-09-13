/**
 * Thread业务服务层，提供会话的创建、查询、更新、关闭等核心功能
 */
package com.xinyirun.scm.ai.thread;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.ai.constant.TypeConsts;
import com.xinyirun.scm.ai.constant.BytedeskConsts;
import com.xinyirun.scm.ai.rbac.user.UserEntity;
import com.xinyirun.scm.ai.rbac.user.UserProtobuf;
import com.xinyirun.scm.ai.thread.entity.ThreadEntity;
import com.xinyirun.scm.ai.thread.request.ThreadRequest;
import com.xinyirun.scm.ai.thread.response.ThreadResponse;
import com.xinyirun.scm.ai.thread.enums.ThreadProcessStatusEnum;
import com.xinyirun.scm.ai.thread.enums.ThreadTypeEnum;
import com.xinyirun.scm.ai.thread.event.ThreadCloseEvent;
import com.xinyirun.scm.ai.thread.event.ThreadRemoveTopicEvent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ThreadRestService extends ServiceImpl<ThreadMapper, ThreadEntity> {

    @Autowired
    private ThreadMapper threadMapper;

    public Page<ThreadEntity> query(ThreadRequest request) {
        QueryWrapper<ThreadEntity> queryWrapper = new QueryWrapper<>();
        if (StringUtils.hasText(request.getUserUid())) {
            queryWrapper.eq("user_uid", request.getUserUid());
        }
        if (StringUtils.hasText(request.getTopic())) {
            queryWrapper.eq("topic", request.getTopic());
        }
        if (StringUtils.hasText(request.getStatus())) {
            queryWrapper.eq("status", request.getStatus());
        }
        queryWrapper.eq("deleted", false);
        queryWrapper.orderByDesc("updated_at");
        
        Page<ThreadEntity> page = new Page<>(request.getPageNumber(), request.getPageSize());
        return this.page(page, queryWrapper);
    }

    public ThreadResponse queryByUid(String uid) {
        ThreadEntity thread = getByUid(uid);
        if (thread != null) {
            return convertToResponse(thread);
        }
        return null;
    }

    public ThreadEntity getByUid(@NonNull String uid) {
        QueryWrapper<ThreadEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid).eq("deleted", false);
        return this.getOne(queryWrapper);
    }

    public Optional<ThreadEntity> findByUid(@NonNull String uid) {
        ThreadEntity thread = getByUid(uid);
        return Optional.ofNullable(thread);
    }

    public Boolean existsByUid(@NonNull String uid) {
        QueryWrapper<ThreadEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", uid).eq("deleted", false);
        return this.count(queryWrapper) > 0;
    }

    @Cacheable(value = "thread", key = "#topic + '-' + #owner.uid", unless = "#result == null")
    public Optional<ThreadEntity> findFirstByTopicAndOwner(@NonNull String topic, UserEntity owner) {
        QueryWrapper<ThreadEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("topic", topic)
                .eq("owner_uid", owner.getUid())
                .eq("deleted", false)
                .orderByDesc("updated_at")
                .last("LIMIT 1");
        ThreadEntity thread = this.getOne(queryWrapper);
        return Optional.ofNullable(thread);
    }

    @Cacheable(value = "threads", key = "#topic", unless = "#result == null")
    public List<ThreadEntity> findListByTopic(@NonNull String topic) {
        QueryWrapper<ThreadEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("topic", topic)
                .eq("deleted", false)
                .orderByDesc("created_at");
        return this.list(queryWrapper);
    }

    @Cacheable(value = "thread", key = "#topic", unless = "#result == null")
    public Optional<ThreadEntity> findFirstByTopic(@NonNull String topic) {
        QueryWrapper<ThreadEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("topic", topic)
                .eq("deleted", false)
                .orderByDesc("created_at")
                .last("LIMIT 1");
        ThreadEntity thread = this.getOne(queryWrapper);
        return Optional.ofNullable(thread);
    }

    @Cacheable(value = "thread", key = "#topic", unless = "#result == null")
    public Optional<ThreadEntity> findFirstByTopicNotClosed(String topic) {
        QueryWrapper<ThreadEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("topic", topic)
                .ne("status", ThreadProcessStatusEnum.CLOSED.name())
                .eq("deleted", false)
                .orderByDesc("created_at")
                .last("LIMIT 1");
        ThreadEntity thread = this.getOne(queryWrapper);
        return Optional.ofNullable(thread);
    }

    public List<ThreadEntity> findByTopicStartsWithAndStatus(String topicPrefix, String status) {
        QueryWrapper<ThreadEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.likeRight("topic", topicPrefix)
                .eq("status", status)
                .eq("deleted", false);
        return this.list(queryWrapper);
    }

    @Transactional
    public ThreadResponse create(ThreadRequest request) {
        // 检查是否已存在相同topic的会话
        if (StringUtils.hasText(request.getTopic()) && StringUtils.hasText(request.getOwnerUid())) {
            UserEntity owner = new UserEntity();
            owner.setUid(request.getOwnerUid());
            Optional<ThreadEntity> threadOptional = findFirstByTopicAndOwner(request.getTopic(), owner);
            if (threadOptional.isPresent()) {
                return convertToResponse(threadOptional.get());
            }
        }
        
        ThreadEntity thread = new ThreadEntity();
        thread.setUid(generateUid());
        thread.setTopic(request.getTopic());
        thread.setType(request.getType() != null ? request.getType() : ThreadTypeEnum.TEXT.name());
        thread.setStatus(ThreadProcessStatusEnum.CHATTING.name());
        
        String user = request.getUser() != null ? request.getUser() : BytedeskConsts.EMPTY_JSON_STRING;
        thread.setUser(user);
        
        thread.setChannel(request.getChannel());
        thread.setOwnerUid(request.getOwnerUid());
        
        boolean saved = this.save(thread);
        if (!saved) {
            throw new RuntimeException("thread save failed");
        }
        
        return convertToResponse(thread);
    }

    public ThreadResponse update(ThreadRequest threadRequest) {
        if (!StringUtils.hasText(threadRequest.getUid())) {
            throw new RuntimeException("thread uid is required");
        }
        
        ThreadEntity thread = getByUid(threadRequest.getUid());
        if (thread == null) {
            throw new RuntimeException("update thread " + threadRequest.getUid() + " not found");
        }
        
        if (threadRequest.getTop() != null) {
            thread.setTop(threadRequest.getTop());
        }
        if (threadRequest.getUnread() != null) {
            thread.setUnread(threadRequest.getUnread());
        }
        if (threadRequest.getMute() != null) {
            thread.setMute(threadRequest.getMute());
        }
        if (threadRequest.getHide() != null) {
            thread.setHide(threadRequest.getHide());
        }
        if (threadRequest.getStar() != null) {
            thread.setStar(threadRequest.getStar());
        }
        if (threadRequest.getFold() != null) {
            thread.setFold(threadRequest.getFold());
        }
        if (StringUtils.hasText(threadRequest.getContent())) {
            thread.setContent(threadRequest.getContent());
        }
        
        boolean updated = this.updateById(thread);
        if (!updated) {
            throw new RuntimeException("thread save failed");
        }
        return convertToResponse(thread);
    }

    public ThreadResponse updateTop(ThreadRequest threadRequest) {
        if (!StringUtils.hasText(threadRequest.getUid())) {
            throw new RuntimeException("thread uid is required");
        }
        
        ThreadEntity thread = getByUid(threadRequest.getUid());
        if (thread == null) {
            throw new RuntimeException("update thread " + threadRequest.getUid() + " not found");
        }
        
        thread.setTop(threadRequest.getTop());
        
        boolean updated = this.updateById(thread);
        if (!updated) {
            throw new RuntimeException("thread save failed");
        }
        return convertToResponse(thread);
    }

    public ThreadResponse updateStar(ThreadRequest threadRequest) {
        if (!StringUtils.hasText(threadRequest.getUid())) {
            throw new RuntimeException("thread uid is required");
        }
        
        ThreadEntity thread = getByUid(threadRequest.getUid());
        if (thread == null) {
            throw new RuntimeException("update thread " + threadRequest.getUid() + " not found");
        }
        
        thread.setStar(threadRequest.getStar());
        
        boolean updated = this.updateById(thread);
        if (!updated) {
            throw new RuntimeException("thread save failed");
        }
        return convertToResponse(thread);
    }

    public ThreadResponse updateMute(ThreadRequest threadRequest) {
        if (!StringUtils.hasText(threadRequest.getUid())) {
            throw new RuntimeException("thread uid is required");
        }
        
        ThreadEntity thread = getByUid(threadRequest.getUid());
        if (thread == null) {
            throw new RuntimeException("update thread " + threadRequest.getUid() + " not found");
        }
        
        thread.setMute(threadRequest.getMute());
        
        boolean updated = this.updateById(thread);
        if (!updated) {
            throw new RuntimeException("thread save failed");
        }
        return convertToResponse(thread);
    }

    public ThreadResponse updateHide(ThreadRequest threadRequest) {
        if (!StringUtils.hasText(threadRequest.getUid())) {
            throw new RuntimeException("thread uid is required");
        }
        
        ThreadEntity thread = getByUid(threadRequest.getUid());
        if (thread == null) {
            throw new RuntimeException("update thread " + threadRequest.getUid() + " not found");
        }
        
        thread.setHide(threadRequest.getHide());
        
        boolean updated = this.updateById(thread);
        if (!updated) {
            throw new RuntimeException("thread save failed");
        }
        return convertToResponse(thread);
    }

    public ThreadResponse updateFold(ThreadRequest threadRequest) {
        if (!StringUtils.hasText(threadRequest.getUid())) {
            throw new RuntimeException("thread uid is required");
        }
        
        ThreadEntity thread = getByUid(threadRequest.getUid());
        if (thread == null) {
            throw new RuntimeException("update thread " + threadRequest.getUid() + " not found");
        }
        
        thread.setFold(threadRequest.getFold());
        
        boolean updated = this.updateById(thread);
        if (!updated) {
            throw new RuntimeException("thread save failed");
        }
        return convertToResponse(thread);
    }

    public ThreadResponse updateUser(ThreadRequest threadRequest) {
        if (!StringUtils.hasText(threadRequest.getUid())) {
            throw new RuntimeException("thread uid is required");
        }
        
        ThreadEntity thread = getByUid(threadRequest.getUid());
        if (thread == null) {
            throw new RuntimeException("update thread " + threadRequest.getUid() + " not found");
        }
        
        thread.setUser(threadRequest.getUser());
        
        boolean updated = this.updateById(thread);
        if (!updated) {
            throw new RuntimeException("thread save failed");
        }
        return convertToResponse(thread);
    }

    public ThreadResponse updateUnread(ThreadRequest threadRequest) {
        if (!StringUtils.hasText(threadRequest.getUid())) {
            throw new RuntimeException("thread uid is required");
        }
        
        ThreadEntity thread = getByUid(threadRequest.getUid());
        if (thread == null) {
            throw new RuntimeException("update thread " + threadRequest.getUid() + " not found");
        }
        
        thread.setUnread(threadRequest.getUnread());
        
        boolean updated = this.updateById(thread);
        if (!updated) {
            throw new RuntimeException("thread save failed");
        }
        return convertToResponse(thread);
    }

    public ThreadResponse updateState(ThreadRequest threadRequest) {
        if (!StringUtils.hasText(threadRequest.getUid())) {
            throw new RuntimeException("thread uid is required");
        }
        
        ThreadEntity thread = getByUid(threadRequest.getUid());
        if (thread == null) {
            throw new RuntimeException("update thread " + threadRequest.getUid() + " not found");
        }
        
        thread.setStatus(threadRequest.getStatus());
        
        boolean updated = this.updateById(thread);
        if (!updated) {
            throw new RuntimeException("thread save failed");
        }
        return convertToResponse(thread);
    }

    public ThreadResponse updateNote(ThreadRequest threadRequest) {
        if (!StringUtils.hasText(threadRequest.getUid())) {
            throw new RuntimeException("thread uid is required");
        }
        
        ThreadEntity thread = getByUid(threadRequest.getUid());
        if (thread == null) {
            throw new RuntimeException("update thread " + threadRequest.getUid() + " not found");
        }
        
        thread.setNote(threadRequest.getNote());
        
        boolean updated = this.updateById(thread);
        if (!updated) {
            throw new RuntimeException("thread save failed");
        }
        return convertToResponse(thread);
    }

    public ThreadResponse closeByUid(ThreadRequest request) {
        Optional<ThreadEntity> threadOptional = findByUid(request.getUid());
        if (!threadOptional.isPresent()) {
            throw new RuntimeException("close thread " + request.getUid() + " not found");
        }
        
        ThreadEntity thread = threadOptional.get();
        if (ThreadProcessStatusEnum.CLOSED.name().equals(thread.getStatus())) {
            throw new RuntimeException("thread " + thread.getUid() + " is already closed");
        }
        thread.setAutoClose(request.getAutoClose());
        thread.setStatus(ThreadProcessStatusEnum.CLOSED.name());
        
        String content = Boolean.TRUE.equals(request.getAutoClose())
                ? "系统自动关闭"
                : "客服关闭";
        thread.setContent(content);
        
        boolean updated = this.updateById(thread);
        if (!updated) {
            throw new RuntimeException("thread save failed");
        }
        
        return convertToResponse(thread);
    }

    public ThreadResponse closeByTopic(ThreadRequest request) {
        List<ThreadEntity> threads = findListByTopic(request.getTopic());
        for (ThreadEntity thread : threads) {
            if (!thread.isClosed()) {
                thread.setAutoClose(request.getAutoClose());
                thread.setStatus(ThreadProcessStatusEnum.CLOSED.name());
                
                String content = Boolean.TRUE.equals(request.getAutoClose())
                        ? "系统自动关闭"
                        : "客服关闭";
                thread.setContent(content);
                
                boolean updated = this.updateById(thread);
                if (!updated) {
                    throw new RuntimeException("thread save failed");
                }
                
                return convertToResponse(thread);
            } else {
                return convertToResponse(thread);
            }
        }
        return null;
    }

    public int countByThreadTopicAndState(String topic, String state) {
        QueryWrapper<ThreadEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("topic", topic)
                .eq("status", state)
                .eq("deleted", false);
        return Math.toIntExact(this.count(queryWrapper));
    }

    public int countByThreadTopicAndStateNot(String topic, String state) {
        QueryWrapper<ThreadEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("topic", topic)
                .ne("status", state)
                .eq("deleted", false);
        return Math.toIntExact(this.count(queryWrapper));
    }

    @CacheEvict(value = "thread", key = "#topic")
    public void deleteByTopic(String topic) {
        List<ThreadEntity> threads = findListByTopic(topic);
        threads.forEach(thread -> {
            thread.setDeleted(true);
            this.updateById(thread);
        });
    }

    @CacheEvict(value = "thread", key = "#uid")
    public void deleteByUid(String uid) {
        ThreadEntity thread = getByUid(uid);
        if (thread != null) {
            thread.setDeleted(true);
            this.updateById(thread);
        }
    }

    public ThreadResponse convertToResponse(ThreadEntity thread) {
        if (thread == null) {
            return null;
        }
        
        ThreadResponse response = new ThreadResponse();
        response.setUid(thread.getUid());
        response.setType(thread.getType());
        response.setTopic(thread.getTopic());
        response.setStatus(thread.getStatus());
        response.setContent(thread.getContent());
        response.setTop(thread.getTop());
        response.setUnread(thread.getUnread());
        response.setMute(thread.getMute());
        response.setHide(thread.getHide());
        response.setStar(thread.getStar());
        response.setFold(thread.getFold());
        response.setNote(thread.getNote());
        // 将String类型的user转换为UserProtobuf
        if (StringUtils.hasText(thread.getUser())) {
            try {
                UserProtobuf userProtobuf = JSON.parseObject(thread.getUser(), UserProtobuf.class);
                response.setUser(userProtobuf);
            } catch (Exception e) {
                response.setUser(null);
            }
        } else {
            response.setUser(null);
        }
        
        response.setAgent(thread.getAgent());
        response.setRobot(thread.getRobot());
        response.setWorkgroup(thread.getWorkgroup());
        response.setChannel(thread.getChannel());
        
        // 将ZonedDateTime转换为LocalDateTime
        response.setCreatedAt(thread.getCreatedAt() != null ? thread.getCreatedAt().toLocalDateTime() : null);
        response.setUpdatedAt(thread.getUpdatedAt() != null ? thread.getUpdatedAt().toLocalDateTime() : null);
        
        return response;
    }

    private String generateUid() {
        return "thread_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);
    }
}