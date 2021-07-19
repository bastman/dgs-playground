package com.example.demo.config.exposed

import com.zaxxer.hikari.HikariDataSource
import mu.KLogging
import org.jetbrains.exposed.spring.SpringTransactionManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.annotation.EnableTransactionManagement

@Configuration
@EnableTransactionManagement
class Exposed {
    companion object : KLogging()

    /*
    @Bean // PersistenceExceptionTranslationPostProcessor with proxyTargetClass=false, see https://github.com/spring-projects/spring-boot/issues/1844
    fun persistenceExceptionTranslationPostProcessor() = PersistenceExceptionTranslationPostProcessor()
    */

    @Bean
    fun transactionManager(dataSource: HikariDataSource): SpringTransactionManager = dataSource
        .also(::logDataSource)
        .toSpringTransactionManager()

    private fun HikariDataSource.toSpringTransactionManager(): SpringTransactionManager =
        SpringTransactionManager(this)

    private fun logDataSource(dataSource: HikariDataSource): Unit =
        logger.info { "=== USE SQL datasource: user=${dataSource.username} url=${dataSource.jdbcUrl}" }


}
