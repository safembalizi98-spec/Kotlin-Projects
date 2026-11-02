import java.sql.Connection
import java.sql.DriverManager

class SchoolDatabase {

    private val connection: Connection

    init {
        connection = DriverManager.getConnection(
            "jdbc:sqlite:assessment.db"
        )

        connection.createStatement().use{ statement ->
            statement.execute("PRAGMA foreign_keys = ON")
        }

        createTable()
    }


    private fun createTable(){
        val tables = listOf(
            """
                CREATE TABLE IF NOT EXISTS classes(
                    class_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    class_name TEXT NOT NULL UNIQUE
                )
            """,
            """
                CREATE TABLE IF NOT EXISTS students(
                    admission_number INTEGER PRIMARY KEY,
                    name TEXT NOT NULL,
                    class_id INTEGER NOT NULL,
                    FOREIGN KEY (class_id)
                        REFERENCES classes(class_id)
                )
            """,
            """
                CREATE TABLE IF NOT EXISTS subjects(
                    subject_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    subject_name TEXT NOT NULL
                )
            """,
            """
                CREATE TABLE IF NOT EXISTS terms(
                    term_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    term_name TEXT NOT NULL,
                    year INTEGER NOT NULL,
                    UNIQUE ( term_name, year )
                )
            """,
            """
                CREATE TABLE IF NOT EXISTS assessments (
                    assessment_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    class_id INTEGER NOT NULL,
                    subject_id INTEGER NOT NULL,
                    term_id INTEGER NOT NULL,
                    assessment_type TEXT NOT NULL,
                    max_mark INTEGER NOT NULL CHECK(max_mark > 0),
                    assessment_date TEXT NOT NULL,
                    
                    FOREIGN KEY (class_id)
                        REFERENCES classes(class_id),
                    FOREIGN KEY (subject_id)
                        REFERENCES subjects (subject_id),
                    FOREIGN KEY (term_id)
                        REFERENCES terms(term_id)
                )
            """,
            """
                CREATE TABLE IF NOT EXISTS marks (
                    assessment_id INTEGER NOT NULL,
                    admission_number INTEGER NOT NULL,
                    mark REAL NOT NULL CHECK (mark >= 0),
                    
                    PRIMARY KEY(assessment_id, admission_number),
                    
                    FOREIGN KEY (assessment_id)
                        REFERENCES assessments(assessment_id),
                    FOREIGN KEY(admission_number)
                        REFERENCES students (admission_number)
                )
            """
        )

        connection.createStatement().use { statement ->
            for(table in tables){
                statement.execute(table.trimIndent())
            }
        }
    }


    fun addClass( className: String): Int{

        val sql ="""
            INSERT INTO classes(class_name)
            VALUES (?)
        """.trimIndent()

        connection.prepareStatement(
            sql,
            java.sql.Statement.RETURN_GENERATED_KEYS
        ).use{statement ->
            statement.setString(1,className)
            statement.executeUpdate()

            statement.generatedKeys.use { result ->
                if(result.next()){
                    return result.getInt(1)
                }
            }
        }
        error("Failed to insert class")
    }
    fun addSubject( subjectName: String): Int {

        val sql = """
            INSERT INTO subjects (subject_name)
            VALUES (?)
        """.trimIndent()

        connection.prepareStatement(
            sql,
            java.sql.Statement.RETURN_GENERATED_KEYS
        ).use{statement ->
            statement.setString(1, subjectName)
            statement.executeUpdate()

            statement.generatedKeys.use { results ->
                if(results.next()){
                    return results.getInt(1)
                }
              }
            }
        error("Failed to insert subject")
        }

    fun addStudent(
        admissionNumber: Int,
        name: String,
        classId: Int
    ){
        val sql = """
            INSERT INTO students(admission_number, name, class_id)
            VALUES (?, ?, ?)
        """.trimIndent()

        connection.prepareStatement(sql).use{ statement ->
            statement.setInt(1, admissionNumber)
            statement.setString(2, name)
            statement.setInt(3, classId)
            statement.executeUpdate()
        }
    }

    fun addTerm(termName: String, year: Int): Int {
        require(termName.isNotBlank())
        require(year in 2000..2100)

        val sql = """
            INSERT INTO terms(term_name,year)
            VALUES (?,?)
        """.trimIndent()

        connection.prepareStatement(
            sql,
            java.sql.Statement.RETURN_GENERATED_KEYS
        ).use { statement ->
            statement.setString(1, termName)
            statement.setInt(2, year)
            statement.executeUpdate()

            statement.generatedKeys.use { results ->
                if(results.next()){
                   return  results.getInt(1)
                }
            }
        }
        error("Failed to insert term")

    }

    fun createAssessment(
        classId: Int,
        subjectId:Int,
        termId: Int,
        assessmentType: String,
        maxMark: Int,
        assessmentDate: String
    ): Int {
        require(assessmentType.isNotBlank())
        require(maxMark > 0)
        require(assessmentDate.matches (
            Regex("""\d{4}-\d{2}-\d{2}""")
        ))
        val sql = """
            INSERT INTO assessments (
                class_id,
                subject_id,
                term_id,
                assessment_type,
                max_mark,
                assessment_date
            )
            VALUES (?,?,?,?,?,?)
        """
        connection.prepareStatement(
            sql,
            java.sql.Statement.RETURN_GENERATED_KEYS
            ).use { statement ->
            statement.setInt(1, classId)
            statement.setInt(2, subjectId)
            statement.setInt(3, termId)
            statement.setString(4, assessmentType)
            statement.setInt(5, maxMark)
            statement.setString(6, assessmentDate)

            statement.executeUpdate()

            statement.generatedKeys.use { results ->
                if(results.next()){
                    return results.getInt(1)
                }
            }
        }

        error("Failed to insert assessment")
    }

    fun recordMark (
        assessmentId: Int,
        admissionNumber: Int,
        mark: Double
    ){
        val assessmentSql = """
            SELECT class_id, max_mark
            FROM assessments
            WHERE assessment_id = ?
        """.trimIndent()

        val assessment = connection.prepareStatement(assessmentSql)
            .use {statement ->
                statement.setInt(1, assessmentId)

                statement.executeQuery().use{ results ->
                    if(results.next()){
                        results.getInt("class_id") to
                            results.getInt("max_mark")
                    } else{
                        null
                    }
                }
            }?: error ("Assessment not found.")

        require (mark.isFinite() && mark >= 0 && mark <= assessment.second){
            "Mark must be between 0 and ${assessment.second}."
        }

        val studentSql = """
            SELECT class_id
            FROM students
            WHERE admission_number = ?
        """.trimIndent()

        val studentClassId = connection.prepareStatement(
            studentSql
        ).use {statement ->
            statement.setInt(1, admissionNumber)

            statement.executeQuery().use { results ->
                if(results.next()){
                    results.getInt("class_id")
                }else null
            }
        }?: error ("student not found.")

        require(studentClassId == assessment.first) {
            "This student does not belong to the assessment class."
        }
        val sql = """
            INSERT INTO marks(
                assessment_id,
                admission_number,
                mark
        )
        VALUES (?, ?, ?)
        ON CONFLICT (assessment_id,admission_number)
        DO UPDATE SET mark = excluded.mark
        """.trimIndent()

        connection.prepareStatement(sql).use{ statement ->
            statement.setInt(1, assessmentId)
            statement.setInt(2, admissionNumber)
            statement.setDouble(3, mark)
            statement.executeUpdate()
        }

        println("Mark saved successfully")
    }

    fun displayStudents() {
        val sql ="""
            SELECT admission_number,name
            FROM students
        """

        connection.prepareStatement(sql).use{ statement ->
            statement.executeQuery().use { results ->
                while (results.next()){

                    val admissionNumber =
                        results.getInt("admission_number")

                    val name =
                        results.getString("name")

                    println("$admissionNumber - $name")
                }
            }
        }
    }

    fun displaySubjects() {
        val sql = """
            SELECT (subject_name)
            FROM subjects
        """

        connection.prepareStatement(sql).use {statement ->
            statement.executeQuery().use { results ->
                while(results.next()){
                    val subjectName = results.getString("subject_name")

                    println("$subjectName")
                }
            }
        }
    }

    fun displayAssessmentReport(assessmentId: Int) {
        val sql ="""
            SELECT s.admission_number, s.name, m.mark, a.max_mark
            FROM assessments a 
            JOIN students s
                ON s.class_id = a.class_id
            LEFT JOIN marks m
                ON m.assessment_id = a.assessment_id
                AND m.admission_number = s.admission_number
            WHERE a.assessment_id = ?
            ORDER BY s.name
            
        """.trimIndent()

        connection.prepareStatement(sql).use { statement ->
            statement.setInt(1, assessmentId)

            statement.executeQuery().use { result ->
                var recordedCount = 0
                var totalPercentage = 0.0

                println("\n========== ASSESSMENT REPORT ==========")

                while (result.next()) {
                    val id = result.getInt("admission_number")
                    val name = result.getString("name")
                    val mark = result.getObject("mark") as? Number
                    val maxMark = result.getInt("max_mark")

                    if (mark == null) {
                        println("$id | $name | Not entered")
                    } else {
                        val score = mark.toDouble()
                        val percentage = score / maxMark * 100

                        totalPercentage += percentage
                        recordedCount++

                        println(
                            "$id | $name | $score/$maxMark | " +
                                    "${"%.2f".format(percentage)}%"
                        )
                    }
                }

                if (recordedCount > 0) {
                    println("---------------------------------------")
                    println(
                        "Class average: " +
                                "${"%.2f".format(totalPercentage / recordedCount)}%"
                    )
                } else {
                    println("No marks have been recorded.")
                }

                println("=======================================")
            }
        }
    }


    fun close(){
        connection.close()
    }


}