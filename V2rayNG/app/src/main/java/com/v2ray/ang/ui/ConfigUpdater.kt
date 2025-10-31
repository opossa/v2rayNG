import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONObject

class ConfigUpdater(private val context: Context) {

    private val configUrl = "https://raw.githubusercontent.com/opossa/App-update-/refs/heads/main/ss%20txt" 
    private val localVersionKey = "config_version"
    private val sharedPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    fun checkForUpdates() {
        Thread {
            try {
                
                val jsonText = downloadUrl(configUrl)
                val jsonObject = JSONObject(jsonText)
                val remoteVersion = jsonObject.getInt("version")
                val remoteConfig = jsonObject.getString("config")

              
                val localVersion = sharedPref.getInt(localVersionKey, 0)

                if (remoteVersion > localVersion) {
                  
                    saveConfig(remoteConfig, remoteVersion)
                    println("✅ Config updated to version $remoteVersion")
                } else {
                    println("☑️ Already latest config (v$localVersion)")
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    private fun downloadUrl(link: String): String {
        val url = URL(link)
        val connection = url.openConnection() as HttpURLConnection
        connection.connectTimeout = 5000
        connection.readTimeout = 5000
        connection.requestMethod = "GET"

        val reader = BufferedReader(InputStreamReader(connection.inputStream))
        val result = reader.readText()
        reader.close()
        connection.disconnect()

        return result
    }

    private fun saveConfig(configData: String, version: Int) {
        
        sharedPref.edit()
            .putString("config_data", configData)
            .putInt(localVersionKey, version)
            .apply()
    }

    fun getCurrentConfig(): String? {
        return sharedPref.getString("config_data", null)
    }
}
