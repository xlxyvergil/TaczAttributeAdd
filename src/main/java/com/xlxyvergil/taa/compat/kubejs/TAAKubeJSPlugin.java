package com.xlxyvergil.taa.compat.kubejs;

import com.xlxyvergil.taa.TaczAttributeAdd;
import com.xlxyvergil.taa.compat.kubejs.events.TAAPropertyDisplayEvents;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * TAA KubeJS插件
 * 提供枪械属性面板显示值修改的事件支持
 */
public class TAAKubeJSPlugin extends KubeJSPlugin {
    private static final Logger LOGGER = LogManager.getLogger(TaczAttributeAdd.MODID + "-kubejs");
    
    @Override
    public void init() {
        LOGGER.info("TAA KubeJS plugin initialized");
    }
    
    @Override
    public void registerBindings(BindingsEvent event) {
        // 不需要注册额外的绑定，属性类型直接使用字符串
    }
    
    @Override
    public void registerEvents() {
        // 注册自定义事件组
        TAAPropertyDisplayEvents.GROUP.register();
        LOGGER.info("TAA property display events registered");
    }
}
