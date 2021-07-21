package com.example.demo.config.threadpools

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


object GqlThreadPools {
    val IO:ExecutorService by lazy { Executors.newFixedThreadPool(64) }
}
