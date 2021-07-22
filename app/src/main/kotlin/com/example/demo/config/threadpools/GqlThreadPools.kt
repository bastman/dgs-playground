package com.example.demo.config.threadpools

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


object GqlThreadPools {
    // https://engineering.zalando.com/posts/2019/04/how-to-set-an-ideal-thread-pool-size.html
    // (e.g.: waiting: 100ms / busy 10ms)
    // cores * (1 + waiting/busy) = 2*(1+10) = 22

    // https://www.reddit.com/r/Kotlin/comments/dl2o5t/why_kotlin_coroutines_dispatchersio_set_to_64/
    // https://docs.spring.io/spring-framework/docs/4.2.x/spring-framework-reference/html/scheduling.html
    // @Async -> ForkJoinPool.commonPool

    val IO:ExecutorService by lazy { Executors.newFixedThreadPool(64) }
}
