
fun main() {

    // 1. Create the database object
    val database = SchoolDatabase()

    try {

        // 2. Create a class
        val classId = database.addClass("Grade 5 Blue")

        // 3. Register a subject
        val subjectId = database.addSubject("Mathematics")

        // 4. Register a learner
        database.addStudent(
            101,
            "Safe",
            classId
        )

        // 5. Create an academic term
        val termId = database.addTerm(
            "Term 1",
            2026
        )

        // 6. Create an assessment
        val assessmentId = database.createAssessment(
            classId,
            subjectId,
            termId,
            "Midterm",
            100,
            "2026-11-02"
        )

        // 7. Record the learner's marks
        database.recordMark(
            assessmentId,
            101,
            80.0
        )

        // 8. Display registered learners
        println("\nREGISTERED LEARNERS")
        database.displayStudents()

        // 9. Display subjects
        println("\nREGISTERED SUBJECTS")
        database.displaySubjects()

        // 10. Display assessment report
        database.displayAssessmentReport(assessmentId)

    } finally {

        // 11. Close the database connection
        database.close()
    }
}
