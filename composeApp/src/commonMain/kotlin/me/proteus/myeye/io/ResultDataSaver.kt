package me.proteus.myeye.io

import androidx.compose.runtime.Composable
import me.proteus.myeye.TestResult

interface DBConnector {
    @Composable
    fun create()
    fun executeGet(query: String): List<TestResult>
    fun executeSet(query: TestResult)
    fun executeSQL(query: String)
    fun close()
}

internal expect class ResultDataSaver {

    companion object {

        @Composable
        fun getConnection(): DBConnector

        fun createTable(db: DBConnector)

        fun select(db: DBConnector, resultId: Int?): List<TestResult>

        fun getLastID(db: DBConnector): Int

        fun insert(db: DBConnector, result: TestResult)

        fun delete(db: DBConnector, id: Int)

    }
}