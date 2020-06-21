package com.baiyi.opscloud.aliyun.ecs.impl;

import com.aliyuncs.ecs.model.v20140526.*;
import com.baiyi.opscloud.aliyun.core.AliyunCore;
import com.baiyi.opscloud.aliyun.ecs.AliyunECS;
import com.baiyi.opscloud.aliyun.ecs.base.ECSDisk;
import com.baiyi.opscloud.aliyun.ecs.handler.AliyunECSHandler;
import com.baiyi.opscloud.common.util.BeanCopierUtils;
import com.baiyi.opscloud.domain.BusinessWrapper;
import com.google.common.collect.Lists;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author baiyi
 * @Date 2020/1/10 6:49 下午
 * @Version 1.0
 */
@EnableCaching
@Component("AliyunECS")
public class AliyunECSImpl implements AliyunECS {

    @Resource
    private AliyunCore aliyunCore;

    @Resource
    private AliyunECSHandler aliyunECSHandler;

    @Override
    public List<DescribeInstancesResponse.Instance> getInstanceList() {
        List<String> regionIds = aliyunCore.getRegionIds();
        List<DescribeInstancesResponse.Instance> instanceList = Lists.newArrayList();
        regionIds.forEach(e -> instanceList.addAll(aliyunECSHandler.getInstanceList(e)));
        return instanceList;
    }

    @Override
    public DescribeInstancesResponse.Instance getInstance(String regionId, String instanceId) {
        return aliyunECSHandler.getInstance(regionId, instanceId);
    }

    @Cacheable(cacheNames = "aliyunECSDisk")
    @Override
    public List<ECSDisk> getDiskList(String regionId, String instanceId) {
        DescribeDisksRequest request = new DescribeDisksRequest();
        request.setInstanceId(instanceId);
        DescribeDisksResponse response = aliyunECSHandler.getDisksResponse(regionId, request);
        if (response == null || response.getRequestId().isEmpty()) return Lists.newArrayList();
        return response.getDisks().stream().map(e -> BeanCopierUtils.copyProperties(e, ECSDisk.class)).collect(Collectors.toList());
    }

    @Cacheable(cacheNames = "aliyunECSRenew")
    @Override
    public String getRenewAttribute(String regionId, String instanceId) {
        DescribeInstanceAutoRenewAttributeRequest describe = new DescribeInstanceAutoRenewAttributeRequest();
        describe.setSysRegionId(regionId);
        describe.setInstanceId(instanceId);
        try {
            List<DescribeInstanceAutoRenewAttributeResponse.InstanceRenewAttribute> list = aliyunECSHandler.getInstanceRenewAttribute(regionId, describe);
            return list.get(0).getRenewalStatus();
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public BusinessWrapper<Boolean> power(String regionId, String instanceId, Boolean action) {
        if (action) {
            return aliyunECSHandler.start(regionId, instanceId);
        } else {
            return aliyunECSHandler.stop(regionId, instanceId);
        }
    }


}