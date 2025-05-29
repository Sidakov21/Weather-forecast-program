package com.example.weatherforecastprogram

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private val apiKey = "5ed1de6f894605957a3cac828450165e" // <-- Ключ от OpenWeatherMap

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val cityInput = findViewById<EditText>(R.id.cityInput)
        val searchButton = findViewById<Button>(R.id.searchButton)
        val tempText = findViewById<TextView>(R.id.tempText)
        val descText = findViewById<TextView>(R.id.descText)
        val humidityText = findViewById<TextView>(R.id.humidityText)
        val weatherIcon = findViewById<ImageView>(R.id.weatherIcon)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(WeatherApi::class.java)

        searchButton.setOnClickListener {
            val city = cityInput.text.toString()
            if (city.isNotEmpty()) {
                lifecycleScope.launch {
                    try {
                        val response = api.getWeather(city, apiKey)
                        if (response.isSuccessful) {
                            val data = response.body()
                            data?.let {
                                tempText.text = "Температура: ${it.main.temp}°C"
                                descText.text = "Описание: ${it.weather[0].description}"
                                humidityText.text = "Влажность: ${it.main.humidity}%"

                                val iconCode = it.weather[0].icon
                                val iconUrl = "https://openweathermap.org/img/wn/$iconCode@2x.png"
                                Picasso.get().load(iconUrl).into(weatherIcon)
                            }
                        } else {
                            Toast.makeText(this@MainActivity, "Город не найден", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this@MainActivity, "Ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Введите город", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
