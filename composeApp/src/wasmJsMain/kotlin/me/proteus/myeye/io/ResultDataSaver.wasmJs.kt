package me.proteus.myeye.io

import androidx.compose.runtime.Composable
import kotlinx.browser.localStorage
import kotlinx.serialization.json.Json
import me.proteus.myeye.TestResult

class LSDBConnector : DBConnector {

    @Composable
    override fun create() {
        return
    }

    override fun executeGet(query: String): List<TestResult> {
        val all = mutableListOf<TestResult>()

        var i = query.toIntOrNull() ?: 1
        var item = localStorage.getItem("result-$i")
        while (item != null || (i == query.toIntOrNull())) {
            all.add(Json.decodeFromString(item!!))
            item = localStorage.getItem("result-${++i}")
        }
//        println("Size: ${all.size}")
        if (query == "last") {
            return if (all.isEmpty()) listOf() else listOf(all.last())
        }
        return all
    }

    override fun executeSet(query: TestResult) {
        val id = query.resultID
        localStorage.setItem("result-$id", Json.encodeToString(query))
    }

    override fun executeSQL(query: String) {
        if (query.startsWith("delete ")) {
            val id = query.split(' ')[1]
            localStorage.removeItem("result-$id")
        }
    }

    override fun close() {
        return
    }

}

internal actual class ResultDataSaver {
    actual companion object {
        @Composable
        actual fun getConnection(): DBConnector {
            val connector = LSDBConnector()
            connector.create()
            return connector
        }

        actual fun createTable(db: DBConnector) {
            return
        }

        actual fun select(db: DBConnector, resultId: Int?): List<TestResult> {
            return db.executeGet(resultId.toString())
        }

        actual fun getLastID(db: DBConnector): Int {
            val nullableRes = db.executeGet("last")
            if (nullableRes.isEmpty()) return -1
            else return nullableRes[0].resultID
        }

        actual fun insert(db: DBConnector, result: TestResult) {
            db.executeSet(result)
        }

        actual fun delete(db: DBConnector, id: Int) {
            db.executeSQL("delete $id")
        }

    }

}