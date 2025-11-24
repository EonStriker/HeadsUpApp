package com.example.basicscodelab.util

import android.content.Context
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
//import org.json.JSONArray
import org.json.JSONObject
import java.io.PrintWriter
import java.net.HttpURLConnection
import java.net.URL
//import javax.net.ssl.HttpURLConnection

suspend fun sendJsonToPi(json: JSONObject, context: Context) {
    val url = URL("http://headsup.local:4000/upload")
    try {
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.setRequestProperty("Content-Type", "application/json")
        connection.doOutput = true

        PrintWriter(connection.outputStream).use { it.write(json.toString()) }

        val responseCode = connection.responseCode
        withContext(Dispatchers.Main) {
            if (responseCode == HttpURLConnection.HTTP_OK) {
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

// attempt at second portion GET
suspend fun fetchDataTypesFromPi(
    url: String = "http://headsup.local:4000/data-types"
): List<String> = withContext(Dispatchers.IO) {
    val connection = (URL(url).openConnection() as HttpURLConnection).apply {
        requestMethod = "GET"
        // Prefer text, but accept anything
        setRequestProperty("Accept", "text/plain, */*;q=0.8")
        doInput = true
    }
    try {
        val body = connection.inputStream.bufferedReader().use { it.readText() }
        parseCsvTypes(body)
    } finally {
        connection.disconnect()
    }
}

// new attempt at parsing
private fun parseCsvTypes(raw: String): List<String> {
    val tokens = raw.replace("\r\n", "\n")
        .replace('\r', '\n')
        .split(',', '\n')
        .map { it.trim().trim('"', '\'') }
        .filter { it.isNotEmpty() }

    return LinkedHashSet(tokens).toList()
}
