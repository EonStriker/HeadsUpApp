package com.example.basicscodelab.util

import android.content.Context
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.PrintWriter
import java.net.URL
import javax.net.ssl.HttpsURLConnection

suspend fun sendJsonToPi(json: JSONObject, context: Context) {
    val url = URL("https://headsup.local:8443/upload")
    try {
        val connection = url.openConnection() as HttpsURLConnection
        connection.requestMethod = "POST"
        connection.setRequestProperty("Content-Type", "application/json")
        connection.doOutput = true

        PrintWriter(connection.outputStream).use { it.write(json.toString()) }

        val responseCode = connection.responseCode
        withContext(Dispatchers.Main) {
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                Toast.makeText(context, "Config sent to Pi!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Send failed: $responseCode", Toast.LENGTH_LONG).show()
            }
        }

        connection.disconnect()
    } catch (e: Exception) {
        Log.e("WiFiSend", "Error: ${e.message}")
        withContext(Dispatchers.Main) {
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
