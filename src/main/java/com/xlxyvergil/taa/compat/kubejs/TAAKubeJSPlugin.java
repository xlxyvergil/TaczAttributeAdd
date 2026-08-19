package com.xlxyvergil.taa.compat.kubejs;

import com.xlxyvergil.taa.TaczAttributeAdd;
import com.xlxyvergil.taa.compat.kubejs.events.TAAPropertyDisplayEvents;
import com.xlxyvergil.taa.compat.kubejs.events.TAAContextEvents;
import dev.latvian.mods.kubejs.event.EventGroupRegistry;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * TAA KubeJS插件
 * 提供枪械属性面板显示值修改和属性后处理的事件支持
 */
public class TAAKubeJSPlugin implements KubeJSPlugin {
    private static final Logger LOGGER = LogManager.getLogger(TaczAttributeAdd.MODID + "-kubejs");
    
    @Override
    public void init() {
        LOGGER.info("TAA KubeJS plugin initialized");
    }
    
    @Override
    public void registerBindings(BindingRegistry event) {
        // 不需要注册额外的绑定，属性类型直接使用字符串
    }
    
    @Override
    public void registerEvents(EventGroupRegistry registry) {
        // 注册自定义事件组
        registry.register(TAAPropertyDisplayEvents.GROUP);
        registry.register(TAAContextEvents.GROUP);
        LOGGER.info("TAA events registered");
    }
}
