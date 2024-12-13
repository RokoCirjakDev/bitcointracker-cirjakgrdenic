package com.cirjakgrdenic.bitcointracker

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.LinearLayout
import android.view.Gravity
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

data class PriceResponse(val bitcoin: CurrencyInfo)
data class CurrencyInfo(val eur: Double)

interface CoinGeckoApi {
    @GET("simple/price")
    fun getBitcoinPrice(
        @Query("ids") ids: String = "bitcoin",
        @Query("vs_currencies") vsCurrencies: String = "eur"
    ): Call<PriceResponse>
}

class MainActivity : AppCompatActivity() {

    private lateinit var textView: TextView
    private lateinit var refreshButton: Button
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.coingecko.com/api/v3/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val api = retrofit.create(CoinGeckoApi::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        textView = TextView(this).apply {
            text = "Ucitavanje Cijene..."
            textSize = 24f
            gravity = Gravity.CENTER
        }

        refreshButton = Button(this).apply {
            text = "Refreshaj Cijenu"
            textSize = 24f
            setOnClickListener { fetchBitcoinPrice() }
        }

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            addView(textView)
            addView(refreshButton)
        }

        setContentView(layout)

        fetchBitcoinPrice()
    }

    private fun fetchBitcoinPrice() {
        api.getBitcoinPrice().enqueue(object : Callback<PriceResponse> {
            override fun onResponse(call: Call<PriceResponse>, response: Response<PriceResponse>) {
                if (response.isSuccessful) {
                    val price = response.body()?.bitcoin?.eur
                    textView.text = "Bitcoin Cijena: €${price ?: "Error"} EUR"
                } else {
                    textView.text = "Pogreska"
                }
            }

            override fun onFailure(call: Call<PriceResponse>, t: Throwable) {
                textView.text = "Pogreska: ${t.message}"
            }
        })
    }
}
