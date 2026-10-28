/* Console student management system */
import java.io.File

data class Student(
    val id: Int,
    val name: String,
    val age: Int,
    val course: String
)

data class StudentMarks(
    val id: Int,
    val subjectMarks: MutableMap< String, Int>,

    )

fun main(){
    val students = loadStudents()
    val allMarks = mutableListOf<StudentMarks>()

    while (true) {
        println("\n======== STUDENT MANAGEMENT SYSTEM ========")
        println("1. Add Student")
        println("2. View Students")
        println("3. Search Student")
        println("4. Remove Student")
        println("5. Update student")
        println("6. School marks system menu:")
        println("7. Exit")
        print(" Choose an option: ")

        val choice = readlnOrNull()?.toIntOrNull()

        when (choice) {
            1 -> addStudent(students,allMarks)
            2 -> viewStudents(students)
            3 -> searchStudent(students,allMarks)
            4 -> removeStudent(students)
            5 -> updateStudent(students, allMarks)
            6 -> {
                while (true){
                    println("============ SCHOOL MARKS SYSTEM ==============")
                    println("1. View student Report")
                    println("2. Class performance report")
                    println("3. Update students marks")
                    println("4. Exit")

                    when(readlnOrNull()?.toIntOrNull()) {
                        1 -> searchStudent(students,allMarks)
                        2 -> classPerformanceReport(students,allMarks)
                        3 -> updateStudent(students,allMarks)
                        4 -> {
                            println("Exiting School Mark System.")
                            break
                        }

                        else -> println("Invalid option. Choose 1 to 4.")
                    }
                }
            }
            7 -> {
                saveStudents(students)
                println("Thank you for using the system. Goodbye!")
                break
            }
            else -> println("Invalid option .Choose 1 to 5")
        }
    }
}

fun addStudent(
    students:MutableList<Student>,
    allMarks :MutableList<StudentMarks>){


    print("Enter student ID: ")
    val id = readln()?.toIntOrNull()

    if (id == null || id<= 0){
        println("Invalid student ID.")
        return
    }
    if(students.any{ it.id == id}){
        println("Student with this ID already exists.")
        return
    }

    print("Enter student name: ")
    val name = readlnOrNull()?.trim().orEmpty()

    if (name.isBlank()){
        println("Students name cannot be empty.")
        return
    }

    print("Enter student age: ")
    val age = readlnOrNull()?.toIntOrNull()

    if(age == null || age !in 1..120){
        println("Enter a valid age between 1 and 120. ")
        return
    }

    println("Enter course: ")
    val course = readlnOrNull()?.trim().orEmpty()

    if(course.isBlank()){
        println("Course cannot be empty.")
        return
    }

    print("Enter three subjects: ")


    println("Subject 1")
    val subject1 = readlnOrNull()?.trim().orEmpty()
    println("Subject 2")
    val subject2 = readlnOrNull()?.trim().orEmpty()
    println("Subject 3")
    val subject3 = readlnOrNull()?.trim().orEmpty()

    println("Would like to enter marks? (press 1 for yes and 2 for no)   ")
    print("For later changes use the update menu item")
    val choice = readlnOrNull()?.toIntOrNull()
    var sub1 :Int = 0
    var sub2 : Int = 0
    var sub3 : Int = 0
    if (choice == 1){

        sub1 = readMark(subject1)
        sub2 = readMark(subject2)
        sub3 = readMark(subject3)
    }



    val student = Student(id, name, age, course)
    val marks = StudentMarks(
        id,
        subjectMarks= mutableMapOf(
            subject1 to sub1,
            subject2 to sub2,
            subject3 to sub3
        )
    )
    students.add(student)
    allMarks.add(marks)
    println("Student saved successful.")
    saveStudents(students)

}

fun viewStudents (students: List<Student>){
    if (students.isEmpty()){
        println("No student has been registered.")
        return
    }

    println("\n============= REGISTERED STUDENTS =============")

    for (student in students){
        println("ID: ${student.id}")
        println("Name: ${student.name}")
        println("Age: ${student.age}")
        println("Course: ${student.course}")
        println("-------------------------------------------------")
    }
}

fun displayStudentReport(
    student : Student,
    marks: StudentMarks
){
    val total = classTotal(marks)
    val average = calculateAverage(marks)
    val grade = calculateGrade (average)
    val result =calculateResult(average)

    println("\n============ STUDENT REPORT ==============")
    println(" Admission No: ${student.id}")
    println(" Name:         ${student.name}")
    println("-------------------------------------------")
    for ((subject,marks) in marks.subjectMarks){
        println("$subject : $marks")
    }

    println("---------------------------------------------")
    println("Total:          $total / 300")
    println("Average Grade:  ${"%.2f".format(average)}%")
    println("Grade:          $grade")
    println("Result:         $result")
}

fun searchStudent(
    students: List<Student>,
    allMarks :List<StudentMarks>){
    print("Enter student ID  to search: ")
    val id = readlnOrNull()?.toIntOrNull()

    if (id == null || id <= 0){
        println("Please enter a valid numeric ID.")
        return
    }

    val student = students.find{
        it.id == id
    }

    if (student != null){
        println("\n Student found: ")
        println("ID: ${student.id}")
        println("Name: ${student.name}")
        println("Age: ${student.age}")
        println("Course: ${student.course}")

        val marks = allMarks.find{
            it.id == student.id
        }

        if (marks != null ){
            println("============== Marks =============")
            for((subject, marks) in marks.subjectMarks){
                println("$subject : $marks")
            }
        }else{
            println("No Marks Found")
        }


    }else {
        println("No student found with  ID $id")
    }
}

fun removeStudent (students: MutableList<Student>){
    print("Enter student ID to remove: ")
    val id = readlnOrNull()?.toIntOrNull()

    if (id == null || id <= 0){
        println("Please enter a valid numeric ID.")
        return
    }

    val student = students.find({it.id == id})

    if(id != null){
        students.remove(student)
        saveStudents(students)
        println("Student ${student?.name} removed successfully!")
    }else{
        println("No student found with this ID: $id")
    }
}

fun updateStudent(students: MutableList<Student>, allMarks :MutableList<StudentMarks>){
    print("Enter student ID  to update: ")
    val id = readlnOrNull()?.toIntOrNull()

    if (id == null || id <= 0){
        println("Please enter a valid numeric ID.")
        return
    }
    val index = students.indexOfFirst{it.id == id}

    if (index == -1){
        println("No student found with this ID: $id")
        return
    }

    val student = students[index]

    println("\n Current student details: ")
    println("Name: ${student.name}")
    println("Age: ${student.age}")
    println("Course: ${student.course}")

    val mappingStudentToMarks = allMarks.findLast {
        it.id == student.id
    }

    println("------------------------------------------")
    println("============= Subject marks =============")
    if (mappingStudentToMarks != null){
        for( (subject, marks) in mappingStudentToMarks.subjectMarks){
            println("$subject : $marks")
        }
    } else{
        println(" No marks entered yet...")
    }


    print("Update the name? (Press enter to keep current name)")
    val nameInput = readlnOrNull()?.trim().orEmpty()

    print("Enter new age  press enter to keep the cuurent age: ")
    val ageInput = readlnOrNull()?.trim().orEmpty()
    val newAge = if(ageInput.isBlank()){
        student.age
    } else{
        ageInput.toInt()
    }

    if( newAge !in 1..120){
        println("Invalid age update cancelled.")
        return
    }

    print("Update the course:(Press enter to keep the current) ")
    val courseInput = readlnOrNull()?.trim().orEmpty()

    println("Update subject  and marks? (Press enter to skip this) ")
    if (mappingStudentToMarks!!.subjectMarks.isNotEmpty()){
        var no = 1
        var newSubject = ""
        var newMarks: Int ?
        var value: Int ? = null
        for ((subject,marks) in mappingStudentToMarks.subjectMarks){
            if (subject != ""){
                print("Change subject name $subject (to ignore press enter)")

                newSubject = readlnOrNull()?.trim().orEmpty()
                if(newSubject != subject || newSubject == ""){
                    value = mappingStudentToMarks.subjectMarks.remove(subject)
                }

                println("Update marks $marks ( to ignore press enter)")
                newMarks = readlnOrNull()?.trim().orEmpty().toIntOrNull()
                if (newMarks== null || newMarks <= 0){
                    newMarks = marks
                }
            }else{
                print("enter subject $no name")
                newSubject = readlnOrNull()?.trim().orEmpty()
                no++

                println("Update marks $marks ( to ignore press enter)")
                newMarks = readlnOrNull()?.trim().orEmpty().toIntOrNull()
                if (newMarks== null || newMarks <= 0){
                    newMarks = marks
                }
            }



            if (newSubject != "" && newMarks != marks){
                mappingStudentToMarks.subjectMarks[newSubject] = newMarks
            }
            else if (newSubject == "" && newMarks != marks){
                mappingStudentToMarks.subjectMarks[subject] = newMarks
            }else{
                mappingStudentToMarks.subjectMarks[newSubject] = value as Int
            }

        }
    }else{
        println("Enter new subject details:(To exit press 1")
        val subjectDetails = mutableMapOf<String,Int>()
        var wantToEnterMarks = 0

        while(wantToEnterMarks == 0){
            print("Enter Subject name:")
            var subject = readlnOrNull()?.trim().orEmpty()

            if (subject != null || subject != ""){
                print("Enter $subject marks:")
                val mark= readlnOrNull()?.toIntOrNull()
                subjectDetails[subject]= mark as Int
            }else if (subject == "1"){
                break
            }
        }

        for((subject, mark) in mappingStudentToMarks.subjectMarks){


            subject
            mappingStudentToMarks.subjectMarks[subject] = mark
        }
    }


    val updatedStudent = student.copy(
        name = if(nameInput == "") student.name else nameInput,
        age = newAge,
        course = if(courseInput == "") student.course else courseInput
    )

    students[index] = updatedStudent

    println("Student updated successfully!")


}

fun saveStudents(students:List<Student>){
    val file =File("students.txt")

    val lines = students.map{ student ->
        "${student.id} | ${student.name} | ${student.age} | ${student.course}"
    }

    file.writeText(lines.joinToString("\n"))

    println("Students record saved successfully.")
}

fun loadStudents():MutableList<Student>{
    val file =File("students.txt")
    val students = mutableListOf<Student>()

    if(!file.exists()){
        return students
    }

    for (line in  file.readLines()){
        val parts = line.split (" | ")

        if(parts.size !=4){
            continue
        }

        val id = parts[0].toIntOrNull()
        val name = parts[1]
        val age = parts[2].toIntOrNull()
        val course = parts[3]

        if (id != null && id > 0 && name.isNotBlank() && age != null
            && age in 1..120 && course.isNotBlank()
            && students.none{ it.id == id }){
            students.add(Student(id, name, age, course))
        }
    }

    return students
}

fun classTotal (marks: StudentMarks): Int {

    var sum: Int = 0
    for (mark in marks.subjectMarks.values){
        sum = mark
    }
    return sum
}

fun calculateAverage (marks: StudentMarks): Double {
    return classTotal(marks) /3.0
}

fun calculateGrade (average: Double): String{
    return when{
        average >= 80 -> "A"
        average >= 70 -> "B"
        average >= 60 -> "C"
        average >= 50 -> "D"
        else -> "E"
    }
}

fun calculateResult(average: Double): String {
    return if(average >= 50) "PASS" else "FAIL"
}

fun readMark(subject: String): Int{
    while(true){
        print("Enter marks for $subject between 1 and 100: ")

        val mark = readlnOrNull()?.toIntOrNull()

        if(mark != null && mark in 1..100){
            return mark
        }
        println("Invalid mark.Please enter mark between 1 and 100.")
    }
}

fun classPerformanceReport(
    students: List<Student>,
    allMarks: List<StudentMarks>
) {
    if (students.isEmpty()) {
        println("No student has been registered.")
        return
    }

    val averages = students.mapNotNull { student ->
        val marks = allMarks.find{
            it.id == student.id
        }
        if (marks != null) {
            student to calculateAverage(marks)
        }
        else null
    }

    if (averages.isEmpty()){
        println("No marks records available.")
        return
    }

    val classAverage = averages.map {it.second}.average()
    val topStudent = averages.maxByOrNull { it.second }

    val passed = averages.count{it.second >= 50}
    val failed = averages.count{it.second < 50}

    println("\n ========= CLASS PERFORMANCE REPORT ==========")
    println("Registered students: ${students.size}")
    println("Students with marks: ${averages.size}")
    println("Class average: ${"%.2f".format(classAverage)}%")
    println("Students passed: $passed")
    println("Students Failed: $failed")

    if (topStudent != null) {
        println("Top performer: ${topStudent.first.name}")
        println("Top average: ${"%.2f".format(topStudent.second)}")
    }

    println("===================================================")
}

