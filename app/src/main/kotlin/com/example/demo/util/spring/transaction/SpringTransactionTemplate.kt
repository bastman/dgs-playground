package com.example.demo.util.spring.transaction

import org.jetbrains.exposed.spring.SpringTransactionManager
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.TransactionDefinition.TIMEOUT_DEFAULT
import org.springframework.transaction.TransactionStatus
import org.springframework.transaction.support.TransactionTemplate

class SpringTransactionTemplate(
    private val delegate: TransactionTemplate,
) {
    companion object

    /**
     * execute transactional code:
     *  -> on error -> rollback -> throw
     *  -> on success -> commit
     */
    fun <T> execute(block: (status: TransactionStatus) -> T): T = delegate.execute(block) as T

}


class SpringTransactionTemplateBuilder(
    private val transactionManager: PlatformTransactionManager,
    var timeout: Int = TIMEOUT_DEFAULT,
    var propagation: Int = TransactionDefinition.PROPAGATION_REQUIRED,
    var isReadOnly: Boolean = false,
    var isolationLevel: Int = TransactionDefinition.ISOLATION_DEFAULT,
) {
    companion object

    fun readOnly(value: Boolean): SpringTransactionTemplateBuilder {
        isReadOnly = value
        return this
    }

    fun propagation(value: Int): SpringTransactionTemplateBuilder {
        propagation = value
        return this
    }

    fun propagationRequired(): SpringTransactionTemplateBuilder =
        propagation(TransactionDefinition.PROPAGATION_REQUIRED)

    fun propagationRequiresNew(): SpringTransactionTemplateBuilder =
        propagation(TransactionDefinition.PROPAGATION_REQUIRES_NEW)

    fun propagationMandatory(): SpringTransactionTemplateBuilder =
        propagation(TransactionDefinition.PROPAGATION_MANDATORY)

    fun propagationNested(): SpringTransactionTemplateBuilder = propagation(TransactionDefinition.PROPAGATION_NESTED)
    fun isolationLevel(value: Int): SpringTransactionTemplateBuilder {
        isolationLevel = value
        return this
    }

    fun isolationLevelDefault(): SpringTransactionTemplateBuilder =
        isolationLevel(TransactionDefinition.ISOLATION_DEFAULT)

    fun isolationLevelRepeatableRead(): SpringTransactionTemplateBuilder =
        isolationLevel(TransactionDefinition.ISOLATION_REPEATABLE_READ)

    fun isolationLevelSerializable(): SpringTransactionTemplateBuilder =
        isolationLevel(TransactionDefinition.ISOLATION_SERIALIZABLE)

    fun build(): SpringTransactionTemplate {
        val delegate = TransactionTemplate(transactionManager)
        delegate.timeout = timeout
        delegate.propagationBehavior = propagation
        delegate.isReadOnly = isReadOnly
        delegate.isolationLevel = isolationLevel
        return SpringTransactionTemplate(delegate)
    }
}

operator fun SpringTransactionTemplate.Companion.invoke(
    transactionManager: PlatformTransactionManager, init: SpringTransactionTemplateBuilder.() -> Unit = {},
): SpringTransactionTemplate = springTransactionTemplate(transactionManager, init)

fun springTransactionTemplate(
    transactionManager: PlatformTransactionManager, init: SpringTransactionTemplateBuilder.() -> Unit = {},
): SpringTransactionTemplate = SpringTransactionTemplateBuilder(transactionManager)
    .apply(init)
    .build()


fun <T> inNewTransaction(stm: SpringTransactionManager, block: (status: TransactionStatus) -> T) =
    springTransactionTemplate(stm) {
        propagationRequiresNew()
    }.execute(block)

fun <T> inTransaction(stm: SpringTransactionManager, block: (status: TransactionStatus) -> T) =
    springTransactionTemplate(stm) {
        propagationRequired()
    }.execute(block)
