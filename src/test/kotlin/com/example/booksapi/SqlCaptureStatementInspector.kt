package com.example.booksapi

import org.hibernate.resource.jdbc.spi.StatementInspector

class SqlCaptureStatementInspector : StatementInspector {
    override fun inspect(sql: String): String {
        capturedStatements.get()?.add(sql)
        return sql
    }

    companion object {
        private val capturedStatements = ThreadLocal<MutableList<String>?>()

        fun <T> measure(block: () -> T): CapturedExecution<T> {
            check(capturedStatements.get() == null) { "SQL capture is already active on this thread" }
            capturedStatements.set(mutableListOf())
            return try {
                val result = block()
                CapturedExecution(result, capturedStatements.get().orEmpty().toList())
            } finally {
                capturedStatements.remove()
            }
        }
    }

    data class CapturedExecution<T>(val result: T, val statements: List<String>)
}
