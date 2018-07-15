package nl.gjalsem.basickotlinexample.model

import android.util.Log
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONException
import org.json.JSONObject

const val TAG = "RpcClient"
const val URL = "https://guidebook.com/service/v2/upcomingGuides/"

class RpcClient(private val requestQueue: RequestQueue) {
    fun fetch(callback: (MainState) -> Unit) {
        requestQueue.add(JsonObjectRequest(URL, null,
                Response.Listener { response ->
                    try {
                        callback(MainState(getInfoItems(response), LoadingState.DONE))
                    } catch (e: JSONException) {
                        Log.w(TAG, e)
                        callback(MainState(listOf(), LoadingState.ERROR))
                    }
                },
                Response.ErrorListener { error ->
                    Log.w(TAG, error)
                    callback(MainState(listOf(), LoadingState.ERROR))
                }).setTag(TAG))
    }

    fun cancel() {
        requestQueue.cancelAll(TAG)
    }

    private fun getInfoItems(jsonObject: JSONObject): List<InfoItem> {
        Log.d(TAG, "Retrieved JSON: ${jsonObject.toString(2)}")

        val result = mutableListOf<InfoItem>()

        val data = jsonObject.getJSONArray("data")
        for (i in 0 until data.length()) {
            val jsonItem = data.getJSONObject(i)
            val jsonVenue = jsonItem.getJSONObject("venue")
            result.add(InfoItem(
                    name = jsonItem.getString("name"),
                    city = jsonVenue.optString("city"),
                    state = jsonVenue.optString("state"),
                    endDate = jsonItem.getString("endDate"),
                    iconUrl = jsonItem.getString("icon")))
        }

        return result
    }
}
