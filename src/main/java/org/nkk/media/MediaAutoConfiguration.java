package org.nkk.media;

import lombok.extern.slf4j.Slf4j;
import org.nkk.media.api.ZlmRestTemplate;
import org.nkk.media.hook.controller.ZlmHookController;
import org.nkk.media.hook.service.ZlmHookService;
import org.nkk.media.hook.service.impl.DefaultZlmHookServiceImpl;
import org.nkk.media.utils.SsrcUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Optional;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * zlm流媒体自动配置
 *
 * @author nkk
 * @date 2024/07/19
 */
@Slf4j
@Import({
        ZlmHookController.class,
        ZlmRestTemplate.class,
        SsrLineRunner.class
})
public class MediaAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnWebApplication
    public ZlmHookService zlmHookService() {
        return new DefaultZlmHookServiceImpl();
    }

    @Bean("zlmPoolExecutor")
    public ThreadPoolTaskExecutor asyncServiceExecutor() {

        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        // 设置核心线程数
        threadPoolTaskExecutor.setCorePoolSize(10);
        // 设置最大线程数
        threadPoolTaskExecutor.setMaxPoolSize(200);
        // 配置队列大小
        threadPoolTaskExecutor.setQueueCapacity(1024);
        // 设置线程活跃时间（秒）
        threadPoolTaskExecutor.setKeepAliveSeconds(60);
        // 设置默认线程名称
        threadPoolTaskExecutor.setThreadNamePrefix("zlmHookTask");
        // 设置拒绝策略
        // CallerRunsPolicy:不在新线程中执行任务，而是由调用者所在的线程来执行
        threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 执行初始化
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }

}
