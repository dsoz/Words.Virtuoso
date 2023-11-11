package wordsvirtuoso

import java.io.File

private var allWordList = listOf<String>()
private var candidateWordsList = listOf<String>()

fun main(args: Array<String>) {
    if (checkArgs(args)){
        readFile(args)
        game()
    }
}

fun checkArgs(args: Array<String>): Boolean{
    if (args.size != 2) {
        println("Error: Wrong number of arguments.")
        return false
    }
    val allWordFile = File(args[0])
    val candidateWordsFile = File(args[1])

    if (!allWordFile.exists()) {
        println("Error: The words file ${args[0]} doesn't exist.")
        return false
    }
    if (!candidateWordsFile.exists()) {
        println("Error: The candidate words file ${args[1]} doesn't exist.")
        return false
    }
    return true
}

fun checkWord(word: String): Boolean{
    if (word.length != 5)
        println("The input isn't a 5-letter word.")
    else if ("[a-zA-Z]".toRegex().findAll(word).count() != 5)
        println("One or more letters of the input aren't valid.")
    else if (word.toSet().size != 5)
        println("The input has duplicate letters.")
    else if (!allWordList.contains(word))
        println("The input word isn't included in my words list.")
    else {
       // println("The input is a valid string.")
        return true
    }
    return false
}

fun readFile(args: Array<String>){
    val allWordFile = File(args[0])
    val candidateWordsFile = File(args[1])

    allWordList = allWordFile.readLines().map { it.lowercase() }
    candidateWordsList = candidateWordsFile.readLines().map { it.lowercase() }

    val vocabularyList = listOf(allWordList, candidateWordsList)
    for (index in vocabularyList.indices){
        var cnt = 0
        for (word in vocabularyList[index]){
            if (word.length != 5 || "[a-zA-Z]".toRegex().findAll(word).count() != 5 || word.toSet().size != 5)
                cnt++
        }
        if (cnt > 0) {
            println("Error: $cnt invalid words were found in the ${args[index]} file.")
            kotlin.system.exitProcess(1)
        }
    }
    val notIncludedWords = candidateWordsList.filter { !allWordList.contains(it) }.size
    if (notIncludedWords > 0) {
        println("Error: $notIncludedWords candidate words are not included in the ${args[0]} file.")
        kotlin.system.exitProcess(1)
    }
    println("Words Virtuoso")
}

fun game(){
    val secretWord = candidateWordsList.random()
    val guessesList = mutableListOf<String>()
    val abcSet = mutableSetOf<Char>()
    var guessWord = ""
    val start = System.currentTimeMillis()
    var tryCnt = 1

    while (true){
        println("\nInput a 5-letter word:")
        guessWord = readln().lowercase()

        if (guessWord == "exit") {
            println("The game is over.")
            kotlin.system.exitProcess(1)
        }
        if (checkWord(guessWord)){
            val clueString = StringBuilder()

            for (index in guessWord.indices){
                val ch = guessWord[index].uppercaseChar()

                if (guessWord[index] == secretWord[index]) {
                    clueString.append("\u001B[48:5:10m$ch\u001B[0m")
                }
                else if (secretWord.contains(guessWord[index])) {
                    clueString.append("\u001B[48:5:11m$ch\u001B[0m")

                }
                else if (!secretWord.contains(guessWord[index])) {
                    clueString.append("\u001B[48:5:7m$ch\u001B[0m")
                    abcSet.add(ch)
                }
            }
            guessesList.add(clueString.toString())
            println()
            for (word in guessesList){
                for (char in word){
                    print(char)
                }
                print("\n")
            }
            if (abcSet.isNotEmpty() && guessWord != secretWord){
               val abc = abcSet.sorted().joinToString("")
               println("\u001B[48:5:14m$abc\u001B[0m")
            }
        }
        if (guessWord == secretWord){
            val end = System.currentTimeMillis()
            val duration = (end - start) / 1000

            if (tryCnt > 1)
                println("\nCorrect!\nThe solution was found after $tryCnt tries in $duration seconds.")
            else
                println("\nCorrect!\nAmazing luck! The solution was found at once.")
            kotlin.system.exitProcess(1)
        }
        tryCnt++
    }
}