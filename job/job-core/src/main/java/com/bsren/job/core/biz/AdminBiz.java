package com.bsren.job.core.biz;

import com.bsren.job.core.biz.model.CallbackParam;
import com.bsren.job.core.biz.model.RegistryParam;
import com.bsren.job.core.biz.model.ReturnT;

import java.util.List;

public interface AdminBiz {

    ReturnT<String> callback(List<CallbackParam> callbackParams);

    ReturnT<String> registry(RegistryParam registryParam);

    ReturnT<String> registryRemove(RegistryParam registryParam);

}
