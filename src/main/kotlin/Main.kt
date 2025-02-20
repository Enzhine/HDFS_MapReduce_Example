package ru.enzhine

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.Path
import org.apache.hadoop.io.LongWritable
import org.apache.hadoop.io.Text
import org.apache.hadoop.mapreduce.Job
import org.apache.hadoop.mapreduce.Mapper
import org.apache.hadoop.mapreduce.Reducer
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat
import org.tartarus.snowball.ext.russianStemmer
import java.util.StringTokenizer
import kotlin.system.exitProcess

class WordsMapper : Mapper<LongWritable, Text, Text, LongWritable>() {
    companion object {
        const val LINE_SETTER = "<|startoftext|>"
    }

    override fun map(key: LongWritable?, value: Text?, context: Context?) {
        val rs = russianStemmer()

        val text = value?.toString() ?: return
        val ctx = context ?: return
        if (text.isEmpty()) {
            return
        }
        val dict = HashMap<String, Long>()

        val line = StringTokenizer(text.substring(LINE_SETTER.length), ".,!?:- ")
        while (line.hasMoreTokens()) {
            val baseWord = filterChars(line.nextToken()).lowercase()
            if (baseWord.isEmpty()) {
                continue
            }

            rs.current = baseWord
            val word = if (rs.stem()) {
                rs.current
            } else {
                baseWord
            }

            val prevCount = dict.getOrDefault(word, 0)
            dict[word] = prevCount + 1
        }

        dict.forEach { (key, value) -> ctx.write(Text(key), LongWritable(value)) }
    }

    private fun filterChars(str: String): String =
        str.filter { it.isLetter() }

}

class WordsReducer : Reducer<Text, LongWritable, Text, LongWritable>() {
    override fun reduce(key: Text?, values: MutableIterable<LongWritable>?, context: Context?) {
        val word = key ?: return
        val counted = values?.map { it.get() } ?: return
        val ctx = context ?: return

        val total = counted.reduce { acc, new -> acc + new }
        ctx.write(word, LongWritable(total))
    }
}

class WordsDriver {
    companion object {
        @JvmStatic
        fun main(vararg args: String) {
            val fileIn = args.getOrNull(0) ?: throw RuntimeException("FileIn not provided")
            val fileOut = args.getOrNull(1) ?: throw RuntimeException("FileOut not provided")

            val cfg = Configuration()
            val job = Job.getInstance(cfg, "WordsCounter MapReduce").apply {
                setJarByClass(WordsDriver::class.java)

                mapperClass = WordsMapper::class.java
                reducerClass = WordsReducer::class.java

                outputKeyClass = Text::class.java
                outputValueClass = LongWritable::class.java
            }.also {
                FileInputFormat.addInputPath(it, Path(fileIn))
                FileOutputFormat.setOutputPath(it, Path(fileOut))
            }

            exitProcess(if (job.waitForCompletion(true)) 0 else 1)
        }
    }
}