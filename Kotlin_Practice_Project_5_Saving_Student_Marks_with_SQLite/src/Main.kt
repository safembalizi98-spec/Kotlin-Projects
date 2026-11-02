


fun main(args: Array<String>) {
   // val students =loadStudents()
    //val allMarks = loadMarks()

    val database = DatabaseHelper()

    try{
        while(true){
            println("\n============= SCHOOL DATABASE SYSTEM ==============")
            println("1. Add student")
            println("2. Save student marks")
            println("3. View student Marks")
            println("4. Exit")
            print("Choose an option: ")

            when(readlnOrNull()?.toIntOrNull()){
                1 -> {
                    print("Admission number: ")
                    val id = readlnOrNull()?.toIntOrNull()

                    print("Student name: ")
                    val name = readlnOrNull()?.trim().orEmpty()

                    if(id != null && id > 0 && name.isNotBlank()){
                        try{
                            database.addStudent(id, name)
                            println("Student saved.")
                        } catch (e: java.sql.SQLException) {
                            println("Couid not save student: $ {e.message}")
                        }
                    } else {
                        println("Enter a valid ID and name.")
                    }
                }

                2 -> {
                    print("Admission number: ")
                    val id = readlnOrNull()?.toIntOrNull()

                    if (id == null){
                        println("Invalid admission number.")
                        continue
                    }

                    val math = readMark("Mathematics")
                    val english = readMark("English")
                    val science = readMark("Science")
                    val kiswahili = readMark("Kiswahili")

                    try {
                        database.saveMarks(
                            id,
                            math,
                             english,
                             science,
                             kiswahili
                            )
                        println("Marks saved.")
                    } catch (e: IllegalArgumentException) {
                        println("Invalid marks")
                    } catch (e: java.sql.SQLException) {
                        println("Could not save marks: ${e.message}")
                    }
                }
                3 -> database.displayStudent()
                4 -> {
                    println("Goodbye!")
                    break
                }

                else -> println("Choose an option from 1 to 4.")
            }

        }
    } finally {
        database.close()
    }
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

