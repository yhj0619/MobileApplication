package ddwu.mobile.winter_study.test02

class User constructor( val name : String, var count : Int){
    init{
        println("$name $count")
    }

    fun myName() {
        print ("$name")
    }
}

fun main() {
    val user = User("cooling", 123);

    user.myName()
}

