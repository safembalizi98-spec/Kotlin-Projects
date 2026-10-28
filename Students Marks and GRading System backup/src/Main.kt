import java.io.File

data class SchoolStudent(
    val admissionNumber: Int,
    val name: String
)

data class StudentMarks(
    var admissionNumber: Int,
    val mathematics: Int,
    val english: Int,
    val science: Int,
    val kiswahili: Int
)


fun main(args: Array<String>) {
    val students =loadStudents()
    val allMarks = loadMarks()

    while(true){
        println("\n============= SCHOOL MARKS SYSTEM ==============")
        println("1. Registered student and enter marks")
        println("2. View individual student report")
        println("3. Class Performance Report")
        println("4. View Students reports")
        println("5. Exit")
        print("Choose an option: ")

        when(readlnOrNull()?.toIntOrNull()){
            1 -> {
                registerStudent(students, allMarks)
                saveStudents(students, allMarks)

            }
            2 -> searchStudentReport(students, allMarks)
            3 -> classPerformanceReport(students, allMarks)
            4 -> viewStudentsReport(students, allMarks)
            5 -> {
                println("Exiting school marks System")
                break
            }
            else -> println("Invalid option. Choose 1 to 4.")
        }
    }
}

fun saveStudents(
    students: List<SchoolStudent>,
    allMarks: List<StudentMarks>
){
    val schoolStudentFile = File("students.txt")

    val schoolStudentLine = students.map { student ->

        "${student.admissionNumber}\t|${student.name}"
    }

    schoolStudentFile.writeText(schoolStudentLine.joinToString("\n"))

    val studentMarksFile = File("marks.txt")

    val studentMarksLine = allMarks.map { student ->
        "${student.admissionNumber}\t|${student.mathematics}\t|${student.english}\t|${student.science}\t|${student.kiswahili}"
    }

    studentMarksFile.writeText(studentMarksLine.joinToString("\n"))

    println("Student records saved successfully.")
}
fun loadStudents():MutableList<SchoolStudent>{
    val file= File("students.txt")
    val students = mutableListOf<SchoolStudent>()

    if(!file.exists()){
        return students
    }

    for (line in file.readLines()){
        val parts = line.split("\t|")

        if (parts.size != 2){
            continue
        }

        val admissionNumber = parts[0].toInt()
        val name = parts[1]

        if (admissionNumber != null &&
            admissionNumber > 0 &&
            name.isNotBlank() &&
            students.none {it.admissionNumber == admissionNumber}){

            students.add(SchoolStudent(admissionNumber,name))
        }
    }
    return students
}

fun loadMarks():MutableList<StudentMarks>{
    val file= File("marks.txt")
    val marks = mutableListOf<StudentMarks>()

    if(!file.exists()){
        return marks
    }

    for(line in file.readLines()){
        val parts = line.split("\t|")

        if(parts.size != 5){
            continue
        }

        val admissionNumber = parts[0].toInt()
        val mathematics = parts[1].toInt()
        val english = parts[2].toInt()
        val science = parts[3].toInt()
        val kiswahili = parts[4].toInt()

        if(admissionNumber != null &&
            admissionNumber > 0 &&
            mathematics != null &&
            english != null &&
            science != null &&
            kiswahili != null &&
            marks.none {it.admissionNumber == admissionNumber}){

            marks.add(StudentMarks(admissionNumber,mathematics,english,science,kiswahili))
        }
    }

    return marks
}

fun calculateTotals(marks: StudentMarks): Int {
    return marks.mathematics+
            marks.english+
            marks.science+
            marks.kiswahili
}

fun calculateAverage(marks: StudentMarks): Double {
    return calculateTotals(marks)/4.0
}

fun calculateGrade(average: Double): String{
    return when{
        average >= 80 -> "A"
        average >= 70 -> "B"
        average >= 60 -> "C"
        average >= 50 -> "D"
        else -> "E"
    }
}

fun calculateResult(average: Double): String{
    return if(average >= 50) "PASS" else "FAIL"
}

fun readMark(subject: String): Int{
    while (true){
        print("Enter $subject marks (1 - 100): ")
        val mark = readlnOrNull()?.toIntOrNull()

        if(mark != null && mark in 1..100){
            return mark
        }
        println("Invalid marks entry. Enter mark between 1 - 100")
    }
}

fun registerStudent(
    students: MutableList<SchoolStudent>,
    allMarks: MutableList<StudentMarks>
){
    println("Enter admission number:")
    val admissionNumber = readlnOrNull()?.toIntOrNull()

    if (admissionNumber == null || admissionNumber <= 0){
        println("Invalid admission number")
        return
    }

    print("Enter student name: ")
    val name = readlnOrNull()?.trim().orEmpty()

    if(name.isBlank()){
        println("Name cannot be blank.")
        return
    }

    val student = SchoolStudent(admissionNumber,name)

    val mathematics = readMark("Mathematics")
    val english = readMark("English")
    val science = readMark("Science")
    val kiswahili = readMark("Kiswahili")

    val marks = StudentMarks (
        admissionNumber,
        mathematics,
        english,
        science,
        kiswahili
    )

    students.add(student)
    allMarks.add(marks)

    println("Student and marks are registered successfully.")
}

fun displayStudentReport(
    student: SchoolStudent,
    marks: StudentMarks
){
   val total = calculateTotals(marks)
   val average = calculateAverage(marks)
   val grade = calculateGrade(average)
   val result = calculateResult(average)

    println("\n================ STUDENT REPORT ================")
    println("Admission No: ${student.admissionNumber}")
    println("Name: ${student.name}")
    println("--------------------------------------------------")
    println("Mathematics: ${marks.mathematics}")
    println("English:     ${marks.english}")
    println("Science:     ${marks.science}")
    println("Kiswahili:     ${marks.kiswahili}")
    println("---------------------------------------------------")
    println("total:       $total/400")
    println("Average:     ${"%.2f".format(average)}%")
    println("Grade:       $grade")
    println("Result:      $result")
    println("====================================================")
}

fun searchStudentReport(
    students: List<SchoolStudent>,
    allMarks: List<StudentMarks>
){
    println("Enter admission number:")
    val admissionNumber = readlnOrNull()?.toIntOrNull()

    if (admissionNumber == null || admissionNumber <= 0){
        println("Invalid admission number")
        return
    }

    val student = students.find{
        it.admissionNumber == admissionNumber
    }
    val marks = allMarks.find{
        it.admissionNumber == admissionNumber
    }
    if (student != null && marks != null){
        displayStudentReport(student, marks)
    } else{
        println("Student and marks record not found.")
    }
}

fun viewStudentsReport(
    students: List<SchoolStudent>,
    allmarks: List< StudentMarks>
){
    val studentList = students.map{ student ->
        val marks = allmarks.find{
            it.admissionNumber == student.admissionNumber
        }
        student to marks
    }
    println(" Admission No | Name          | Mathematics | English | Science | Kiswahili ")
    studentList.forEach {
        println("${it.first.admissionNumber} | ${it.first.name} | ${it.second?.mathematics} | ${it.second?.english} | ${it.second?.science} | ${it.second?.kiswahili}")
    }
}

fun classPerformanceReport(
    students: List<SchoolStudent>,
    marks: List<StudentMarks>
){
    if(students.isEmpty()){
        println("No student has been registered.")
    }

    val averages = students.mapNotNull{ student ->
        val mark = marks.find {
            it.admissionNumber == student.admissionNumber
        }

        if(mark != null){
            student to calculateAverage(mark)
        }else {
            null
        }
    }

    if (averages.isEmpty()){
        println("No mark recor avilable.")
        return
    }

    val classAverage = averages.map {it.second}.average()
    val topStudent = averages.maxByOrNull {it.second}

    val passed = averages.count{it.second >= 50}
    val failed = averages.count{it.second < 50}
    println("\n========== CLASS PERFORMANCE REPORT ==========")
    println("Registered students: ${students.size}")
    println("Students with marks: ${averages.size}")
    println("Class average: ${"%.2f".format(classAverage)}%")
    println("Students passed: $passed")
    println("Students failed: $failed")

    if (topStudent != null){
        println("Top performer: ${topStudent.first.name}")
        println("Top average: ${"%.2f".format(topStudent.second)}%")
    }
    println("=================================================")
}
