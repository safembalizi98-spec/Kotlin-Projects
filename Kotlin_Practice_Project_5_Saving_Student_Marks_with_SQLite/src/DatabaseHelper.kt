
import java.sql.Connection
import java.sql.DriverManager

class DatabaseHelper {

    private val connection: Connection

    init {
        connection = DriverManager.getConnection(
            "jdbc:sqlite:school.db"
        )
        connection.createStatement().use {
            it.execute("PRAGMA foreign_keys = ON")
        }
        createTables()
    }

    private fun createTables() {
        val studentTable = """
            CREATE TABLE IF NOT EXISTS students (
                admission_number INTEGER PRIMARY KEY,
                name TEXT NOT NULL
            )
        """.trimIndent()

        val marksTable = """
            CREATE TABLE IF NOT EXISTS marks (
                admission_number INTEGER PRIMARY KEY,
                mathematics INTEGER NOT NULL,
                english INTEGER NOT NULL,
                science INTEGER NOT NULL,
                kiswahili INTEGER NOT NULL,
                FOREIGN KEY (admission_number)
                    REFERENCES students (admission_number)
                    ON DELETE CASCADE
            )
        """.trimIndent()

        connection.createStatement().use { statement ->
            statement.execute(studentTable)
            statement.execute(marksTable)
        }
    }

    fun addStudent(admissionNumber: Int, name: String){
        val sql = """
            INSERT INTO students (admission_number,name)
            VALUES (?, ?)
        """.trimIndent()

        connection.prepareStatement(sql).use { statement ->
            statement.setInt(1, admissionNumber)
            statement.setString(2, name)
            statement.executeUpdate()
        }
    }

    fun saveMarks(
        admissionNumber: Int,
        mathematics: Int,
        english: Int,
        science: Int,
        kiswahili: Int
    ){
        require(mathematics in 1 .. 100)
        require(english in 1 .. 100)
        require(science in 1 .. 100)
        require(kiswahili in 1 .. 100)

        val sql ="""
            INSERT INTO marks (
            admission_number,
            mathematics,
            english,
            science,
            kiswahili
            )
            VALUES(?, ?, ?, ?, ?)
            ON CONFLICT (admission_number) DO UPDATE SET
                mathematics = excluded.mathematics,
                english = excluded.english,
                science = excluded.science,
                kiswahili = excluded.kiswahili
        """.trimIndent()

        connection.prepareStatement(sql).use{ statement ->
            statement.setInt(1, admissionNumber)
            statement.setInt(2, mathematics)
            statement.setInt(3, english)
            statement.setInt(4, science)
            statement.setInt(5, kiswahili)
            statement.executeUpdate()
        }
    }

    fun displayStudent(){
        val sql ="""
            SELECT 
                s.admission_number,
                s.name,
                m.mathematics,
                m.english,
                m.science,
                m.kiswahili
            FROM students s
            LEFT JOIN marks m
                ON s.admission_number = m.admission_number
            ORDER BY s.admission_number
        """.trimIndent()

        connection.createStatement().use {statement ->
            statement.executeQuery(sql).use{result ->
                while(result.next()){
                    val id = result.getInt("admission_number")
                    val name = result.getString("name")
                    val mathematics = result.getObject("mathematics")
                    val english = result.getObject("english")
                    val science = result.getObject("science")
                    val kiswahili = result.getObject("kiswahili")

                    println(
                        "ID: $id | Name: $name | " +
                        "Math: ${mathematics ?: "Not entered"} | " +
                        "English: ${english ?: "Not entered"} | " +
                        "Science: ${science ?: "Not entered"} | " +
                        "Kiswahili: ${kiswahili ?: "Not entered"}"

                    )
                }
            }
        }
    }
    fun close() {
        connection.close()
    }
}
