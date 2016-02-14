package ru.application.task2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/*
        Задача 2
        Игровое приложение (одна активность)
        на экран выводятся четыре таракана. По нажатию кноки на экране они начинают забег.
        в каждый момент времени он перемещяются каждый по своей по дорожке на случайную длину вперёд
        (не более 2% от общей длины дорожки). По мере достижения финиша на конце дорожки
        показывается место таракана в забеге. Когда добегает последний таракан - забег заканчивается.
*/

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new MySurfaceView(this, getResources()));
    }
}
