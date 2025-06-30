package org.nkk.media;

import lombok.extern.slf4j.Slf4j;
import org.nkk.media.utils.SsrcUtil;
import org.springframework.boot.CommandLineRunner;

@Slf4j
public class SsrLineRunner implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        SsrcUtil.initSsrc();
        log.info("「初始化Ssrc完成」");
    }
}
