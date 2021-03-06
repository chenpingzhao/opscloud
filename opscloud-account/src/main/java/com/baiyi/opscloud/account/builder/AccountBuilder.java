package com.baiyi.opscloud.account.builder;

import com.baiyi.opscloud.account.base.AccountType;
import com.baiyi.opscloud.account.bo.AccountBO;
import com.baiyi.opscloud.common.util.BeanCopierUtils;
import com.baiyi.opscloud.domain.generator.opscloud.OcAccount;
import com.baiyi.opscloud.zabbix.entry.ZabbixUser;


/**
 * @Author baiyi
 * @Date 2019/11/27 4:30 PM
 * @Version 1.0
 */
public class AccountBuilder {

    public static OcAccount build(ZabbixUser user) {
        AccountBO bo = AccountBO.builder()
                .accountId(user.getUserid())
                .username(user.getAlias())
                .displayName(user.getName())
                .isActive(true)
                .accountType(AccountType.ZABBIX.getType())
                .build();
        return convert(bo);
    }

    private static OcAccount convert(AccountBO bo) {
        return BeanCopierUtils.copyProperties(bo, OcAccount.class);
    }

}
