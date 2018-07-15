package nl.gjalsem.basickotlinexample.model

import android.util.Log
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONException
import org.json.JSONObject

const val URL = "https://guidebook.com/service/v2/upcomingGuides/"

class RpcClient(val requestQueue: RequestQueue) {
    val tag = javaClass.simpleName
    val requestTag = javaClass.name

    fun fetch(callback: (MainState) -> Unit) {
        requestQueue.add(JsonObjectRequest(URL, null,
                Response.Listener { response ->
                    try {
                        callback(MainState(getInfoItems(response), LoadingState.DONE))
                    } catch (e: JSONException) {
                        Log.w(tag, e)
                        callback(MainState(listOf(), LoadingState.ERROR))
                    }
                },
                Response.ErrorListener { error ->
                    Log.w(tag, error)
                    callback(MainState(listOf(), LoadingState.ERROR))
                }).setTag(requestTag))
    }

    fun cancel() {
        requestQueue.cancelAll(requestTag)
    }

    private fun getInfoItems(jsonObject: JSONObject): List<InfoItem> {
        Log.d(tag, "Retrieved JSON: ${jsonObject.toString(2)}")

        val result = mutableListOf<InfoItem>()

        val data = jsonObject.getJSONArray("data")
        for (i in 0 until data.length()) {
            val jsonItem = data.getJSONObject(i)
            val jsonVenue = jsonItem.getJSONObject("venue")
            result.add(InfoItem(
                    name = jsonItem.getString("name"),
                    city = jsonVenue.optString("city"),
                    state = jsonVenue.optString("state"),
                    endDate = jsonItem.getString("endDate")))
        }

        return result
    }
}
