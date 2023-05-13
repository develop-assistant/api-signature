# API验签组件

### 概述

此组件支持query、json表单签名<br>
请求头需添加以下参数<br>

| 参数名称  | 必填 | 说明                          | 示例                                 |
| --------- | ---- | ----------------------------- | ------------------------------------ |
| x-ca-key     | 是   | 应用Id (服务端生成)                       | 1683980765855                        |
| x-ca-nonce     | 是   | 每次请求随机生成 | 8091a230-8c4f-4d6f-b4be-6c585af6c8ad |
| x-ca-timestamp | 是   | 请求时间戳（毫秒）            | 1683980765855              |
| x-ca-signature-method      | 是   | 签名方法                          | MD5     |
| x-ca-signature      | 是   | 签名                          | 5b2T5oiR6YGH5LiK5L2g     |

### 依赖

```xml
<dependency>
    <groupId>cn.idea360</groupId>
    <artifactId>api-signature</artifactId>
    <version>0.0.1</version>
</dependency>
```

### 使用示例

**签名配置**

示例中配置URI `/sign` 需要验签, 签名中心分发的密钥信息为:

```text
appId: xxx
appSecret: 123
签名类型: MD5
```

```java
package cn.idea360.signature;

import cn.idea360.signature.configration.SignatureConfigration;
import cn.idea360.signature.filter.SignatureFilter;
import cn.idea360.signature.properties.Secret;
import cn.idea360.signature.properties.SignatureProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

/**
 * @author cuishiying
 */
@Configuration
public class SignatureAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean({SignatureProperties.class})
    public SignatureProperties signatureProperties() {
        SignatureProperties signatureProperties = new SignatureProperties();
        signatureProperties.setIncludedUris(Collections.singletonList("/sign"));
        signatureProperties.setSecrets(Collections.singletonList(Secret.builder()
                .appId("xxx")
                .appSecret("123")
                .build()));
        return signatureProperties;
    }

    @Bean
    @ConditionalOnMissingBean({SignatureConfigration.class})
    public SignatureConfigration signatureConfigration(SignatureProperties signatureProperties) {
        SignatureConfigration signatureConfigration = new SignatureConfigration();
        signatureConfigration.setSignatureProperties(signatureProperties);
        return signatureConfigration;
    }

    @Bean
    @ConditionalOnMissingBean({SignatureFilter.class})
    public FilterRegistrationBean<SignatureFilter> filterRegistration(SignatureConfigration signatureConfigration) {
        FilterRegistrationBean<SignatureFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new SignatureFilter(signatureConfigration));
        registration.addUrlPatterns("/*");
        registration.setName("SignatureFilter");
        registration.setOrder(1);
        return registration;
    }
}
```

**接口请求示例**

![](https://raw.githubusercontent.com/qidian360/oss/master/images/api-sign-demo.png)

### 签名规则

```text
 String sign = base64(md5AsHex(query+body+nonce+timestamp+appSecret));
```
### TODO

- [ ] 更多签名算法
- [ ] 更多HTTP请求类型
- [ ] 使用示例说明
- [ ] 基础包中移除spring依赖
