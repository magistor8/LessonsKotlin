package com.magistor8.lesson1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton

class MainActivity : AppCompatActivity() {

    private lateinit var data:Data

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        data = Data(1, "Первый объект")
        setListener()
    }

    private fun setListener() {
        //Копируем дата класс
        val data2:Data = data.copy()
        val tv:TextView = textV()
        //меняем аргументы
        data2.arg1 = 2
        data2.arg2 = "Второй объект"
        findViewById<AppCompatButton>(R.id.data_class).setOnClickListener {
            //Выводим данные Дата класса
            tv.text = "1 аргумент класса Data: ${data.arg1}, второй аргумент: ${data.arg2}"
        }
        findViewById<AppCompatButton>(R.id.objectCopy).setOnClickListener {
            //Выводим данные Дата класса
            tv.text = "Аргументы 1 экземпляра: ${data.arg1}, ${data.arg2}, аргументы 2 экземпляра: ${data2.arg1}, ${data2.arg2}"
        }
        findViewById<AppCompatButton>(R.id.cycle).setOnClickListener {
            var s:String = ""
            val data3 = data.copy()
            val data4 = data.copy()

            //меняем аргументы
            data3.arg1 = 3
            data3.arg2 = "Третий объект"
            data4.arg1 = 4
            data4.arg2 = "Четвертый объект"

            val list = listOf(data, data2, data3, data4)

            list.forEach {
                s = s.plus("1 аргумент класса Data: ${it.arg1}, второй аргумент: ${it.arg2}; ")
                tv.text = s
            }

        }
    }

    private fun textV() : TextView {
        return findViewById(R.id.tv) as TextView
    }
}


