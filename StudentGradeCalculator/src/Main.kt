fun main (){
    println("================================================")
    println("           STUDENT GRADE CALCULATOR")
    println("================================================")

    val name = validNameEntry()

    val mathematics = readMark("Mathematics")
    val english = readMark("English")
    val science = readMark("Science")
    val kiswahili = readMark("Kiswahili")
    println("marks entered successfully for  ${name}")


    val total = mathematics + english + science + kiswahili
    val average = total/ 4.0

    val grade = calculateGrade(average)
    val result = if (average > 50) "PASS" else "FAIL"

    println()
    println("============= STUDENT REPORT ============= ")
    println("Student: $name")
    println("------------------------------------------ ")
    println("Mathematics: $mathematics")
    println("English: $english")
    println("Science: $science")
    println("Kiswahili: $kiswahili")
    println("------------------------------------------ ")
    println("Totals: $total / 400")
    println("Average: ${"%.2f".format (average)}%")
    println("Grade: $grade")
    println("Result: $result")
    println("============================================ ")

}
fun validNameEntry(): String{
    var name: String? = null
    while (true){
        print("Enter student name: ")
        name = readlnOrNull()
        if(!name.isNullOrEmpty()){
            return name
        }
        println("Please enter a valid student name")

    }
}

fun readMark (subject: String):Int{
    while (true) {
        print("Enter $subject marks (0 - 100): ")
        val input = readlnOrNull()
        val mark = input?.toIntOrNull()

        if (mark != null && mark in 1..100) {
            return mark
        }

        println("Invalid input.Enter a whole number from 0 to 100")
    }
}

fun calculateGrade( average: Double): Char{
    return when{
        average >= 80 -> 'A'
        average >= 70 -> 'B'
        average >= 60 -> 'C'
        average >= 50 -> 'D'
        else -> 'F'
    }
}