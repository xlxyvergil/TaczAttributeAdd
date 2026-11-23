package com.xlxyvergil.taa.compat.kubejs;

import com.xlxyvergil.taa.TaczAttributeAdd;
import com.xlxyvergil.taa.compat.kubejs.events.TAAPropertyDisplayEvents;
import com.xlxyvergil.taa.compat.kubejs.events.TAAContextEvents;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * TAA 的 KubeJS 插件入口，注册属性面板显示与属性后处理两个事件组
 * <p>
 * KubeJS 通过各 mod jar 里的 kubejs.plugins.txt 发现插件：本类就登记在
 * src/main/resources/kubejs.plugins.txt 中，改名或移动包之后必须同步改那个文件，
 * 否则下面的回调一个都不会被调用。
 * <p>
 * 这里的插件 API 是「继承 KubeJSPlugin 父类、覆写各回调」的形式，事件组由 GROUP 自己 register()。
 */
public class TAAKubeJSPlugin extends KubeJSPlugin {
    /** 日志器名带 -kubejs 后缀，方便在统一日志里区分插件本身的输出。 */
    private static final Logger LOGGER = LogManager.getLogger(TaczAttributeAdd.MODID + "-kubejs");
    
    /**
     * 插件初始化回调，KubeJS 启动阶段调用一次
     * 目前只打一条日志，真正的注册工作在 registerEvents 里
     */
    @Override
    public void init() {
        LOGGER.info("TAA KubeJS plugin initialized");
    }
    
    /**
     * 往脚本环境里加全局绑定；本模组不需要
     * 属性类型在脚本里以字符串传递（面板行的 key），不必再把 Java 类暴露成全局变量。
     */
    @Override
    public void registerBindings(BindingsEvent event) {
        // 不需要注册额外的绑定，属性类型直接使用字符串
    }
    
    /**
     * 注册两个自定义事件组，脚本里才写得出 onEvent 监听
     * 少注册一个，对应的事件在被触发时也找不到任何监听器。
     */
    @Override
    public void registerEvents() {
        // 注册自定义事件组
        TAAPropertyDisplayEvents.GROUP.register();
        TAAContextEvents.GROUP.register();
        LOGGER.info("TAA events registered");
    }
}