# OpsCloud发布

#### OC3.0版本发布

* **工单、web堡垒机、跳板机迁移至oc3**
* **登录账户/密码不变**
* **新应用上线工单目前还在老oc中申请**
* **任何意见、建议、bug都可以反馈给我baiyi@gegejia.com**

####  平台特性

* 全接口API，可调用查询运维元数据 [https://oc3.ops.yangege.cn/oc3/doc.html\#/home](https://oc3.ops.yangege.cn/oc3/doc.html#/home)
* 工单优化、支持组织架构上级审批
* 增强的web-xterm

#### 跳板机使用说明

* [https://oc3.ops.yangege.cn/index.html\#/workbench/jump](https://oc3.ops.yangege.cn/index.html#/workbench/jump)
* 登录方式：

```bash
# -C 压缩传输
# -o StrictHostKeyChecking=no 公钥免检
ssh {USERNAME}@oc3.ops.yangege.cn
```

#### web-xterm使用说明

* [https://oc3.ops.yangege.cn/index.html\#/workbench/xterm](https://oc3.ops.yangege.cn/index.html#/workbench/xterm)
* 支持高权限批量登录系统（工单中申请）
* 增强的批量命令，开启批量命令后任意窗口拥有输入焦点即可输入
* 会话复制，当前终端快速复制N个窗口（可改变高/低权限登录）
* 个人文档，用于保存常用命令
* 全站web-xterm，服务器管理、playbook等界面都能快速登录系统排除故障

![](https://opscloud-store.oss-cn-hangzhou.aliyuncs.com/github/gif/oc-webxterm-2.gif)

### API使用说明

* 接口文档 [https://oc3.ops.yangege.cn/oc3/doc.html\#/home](https://oc3.ops.yangege.cn/oc3/doc.html#/home)
* 个人详情中申请API-Token
* API权限与本人账户权限相同，特殊权限找baiyi@gegejia.com申请
* 示例

{% code title="Oc3Test.java" %}
```java
package com.baiyi.opscloud.oc3test;

import com.alibaba.fastjson.JSON;
import com.baiyi.opscloud.BaseUnit;
import com.baiyi.opscloud.common.util.JSONMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Joiner;
import lombok.Data;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * @Author baiyi
 * @Date 2020/5/19 3:30 下午
 * @Version 1.0
 */
public class Oc3Test extends BaseUnit {

    private static final String TOKEN_KEY = "x-token";

    // API-TOKEN 在个人详情中申请
    private static final String X_TOKEN = "AAAAAAAAAAA";

    private static final String OC3_URL = "https://oc3.ops.yangege.cn";

    private static final String queryServer = "/oc3/server/page/fuzzy/query";

    private HttpPost getHttpPostClient(String url) {
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(20000)
                .setConnectTimeout(20000)
                .setConnectionRequestTimeout(20000)
                .build();
        HttpPost httpPost = new HttpPost(Joiner.on("").join(OC3_URL, url));
        httpPost.setConfig(requestConfig);
        httpPost.setHeader(TOKEN_KEY, X_TOKEN);
        httpPost.setHeader("Content-Type", "application/json;charset=utf-8");
        return httpPost;
    }

    @Data
    public class PageQueryServerParam {
        private Integer serverGroupId; // 服务器组id
        private Integer envType; // 环境
        private Integer tagId;
        private Integer extend = 0; // 不显示详情
        private Integer page = 1;
        private Integer length = 100;
    }

    @Test
    void testQueryServer() {
        queryServer(null, null, null);
    }

    private void queryServer(Integer serverGroupId, Integer envType, 
    Integer tagId) {
        HttpPost httpPost = getHttpPostClient(queryServer);
        PageQueryServerParam pageQueryServerParam = new PageQueryServerParam();
        pageQueryServerParam.setServerGroupId(serverGroupId);
        pageQueryServerParam.setEnvType(envType);
        pageQueryServerParam.setTagId(tagId);

        HttpClient httpClient = HttpClients.createDefault();
        try {
            httpPost.setEntity(
            new StringEntity(JSON.toJSONString(pageQueryServerParam), "utf-8"));
            HttpResponse response 
            = httpClient.execute(httpPost, new HttpClientContext());

            HttpEntity entity = response.getEntity();
            byte[] data = EntityUtils.toByteArray(entity);
            JsonNode jsonNode = readTree(data);
            // server list
            System.err.println(jsonNode.get("body").get("data"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static JsonNode readTree(byte[] data) {
        try {
            JSONMapper mapper = new JSONMapper();
            return mapper.readTree(new String(data));
        } catch (Exception e) {
            return null;
        }
    }

}

```
{% endcode %}



