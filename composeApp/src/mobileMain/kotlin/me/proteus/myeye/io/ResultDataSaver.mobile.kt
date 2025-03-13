package me.proteus.myeye.io

import androidx.compose.runtime.Composable
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.sqlite.execSQL
import me.proteus.myeye.TestResult

class MobileDBConnector : DBConnector {
    private var driver: SQLiteConnection? = null

    private fun <T> assertDriver(function: (T) -> Unit, arg: T) {
        if (driver != null) function(arg)
        else println("Error: Database method invocation without driver initialization")
    }


    @Composable
    override fun create() {
        val path = getPath("results.sql")
        val mobileDriver = BundledSQLiteDriver().open(path.toString())
        driver = mobileDriver
//        createTable(driver)
    }
    override fun executeSet(query: TestResult) {
        val insertionQuery = "INSERT OR IGNORE INTO Results" +
                "(TIMESTAMP, TEST, DISTANCE, RESULT)" +
                "VALUES (?, ?, ?, ?)"
        assertDriver(arg = Unit, function = {
            driver!!.prepare(insertionQuery).use { stmt ->
                stmt.bindLong(1, query.timestamp)
                stmt.bindText(2, query.testID)
                stmt.bindFloat(3, query.distance)
                stmt.bindBlob(4, query.result)
                stmt.step()
            }
        })

    }

    override fun executeSQL(query: String) {
        assertDriver(arg = Unit, function = {
            driver!!.execSQL(query)
        })
    }

    override fun executeGet(query: String): List<TestResult> {
        val response = mutableListOf<TestResult>()
        assertDriver(arg = Unit, function = {
            driver!!.prepare(query).use { stmt ->
                while (stmt.step()) {
                    val id = stmt.getInt(0)
                    val timestamp = stmt.getLong(1)
                    val testId = stmt.getText(2)
                    val distance = stmt.getFloat(3)
                    val result = stmt.getBlob(4)

                    response.add(TestResult(id, testId, timestamp, distance, result))
                }
            }
        })

        return response.toList()

    }

    override fun close() {
        assertDriver(arg = Unit, function = { driver!!.close() })
    }
}

internal actual class ResultDataSaver {

    actual companion object {

        @Composable
        actual fun getConnection(): DBConnector {
            val connector = MobileDBConnector()
            connector.create()
            return connector
        }

        actual fun createTable(db: DBConnector) {
            val schema = "CREATE TABLE IF NOT EXISTS RESULTS (" +
                    "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "TIMESTAMP INTEGER NOT NULL, " +
                    "TEST TEXT NOT NULL, " +
                    "DISTANCE REAL NOT NULL, " +
                    "RESULT BLOB NOT NULL)"
            db.executeSQL(schema)
        }

        actual fun select(db: DBConnector, resultId: Int?): List<TestResult> {

            var query = "SELECT * FROM Results"
            if (resultId != null) query += " WHERE ID=$resultId"

            val allData = db.executeGet(query)

            println("Znaleziono ${allData.size} wiersze")
//            db.close()
            return allData
        }

        actual fun getLastID(db: DBConnector): Int {
            val list = db.executeGet("SELECT ID FROM Results ORDER BY TIMESTAMP DESC LIMIT 1")
            if (list.isEmpty()) return -1
            else return list[0].resultID
        }

        actual fun insert(db: DBConnector, result: TestResult) {
            db.executeSet(result)
            println("Dodano wiersz")
//            db.close()
        }

        actual fun delete(db: DBConnector, id: Int) {
            db.executeSQL("DELETE FROM Results WHERE id = $id")
//            db.close()
        }

    }
}