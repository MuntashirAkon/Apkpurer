package gh.cloneconf.apkpurer

import gh.cloneconf.apkpurer.model.App
import gh.cloneconf.apkpurer.model.AppPage
import gh.cloneconf.apkpurer.model.Search
import okhttp3.*
import org.json.JSONArray
import org.jsoup.Jsoup
import org.jsoup.nodes.Document


object Apkpurer {

    var client = OkHttpClient().newBuilder()
        .build()


    const val BASE = "apkpure.com/"


    private var call : Call? = null

    fun getString(url : String): String {
        val request = Request.Builder()
            .url(url)
            .header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:78.0) Gecko/20100101 Firefox/78.0")
            .build()

        call = client.newCall(request)
        return call!!.execute().body!!.string()
    }


    fun getDoc(url : String): Document {
        return Jsoup.parse(getString(url))
    }



    fun getSuggestions(q : String): ArrayList<String> {
        return ArrayList<String>().apply {
            JSONArray(getString("https://apkpure.com/api/v1/search_suggestion?key=$q")).also { json ->

                for (i in 0 until json.length()){
                    add(json.getJSONObject(i).getString("key"))
                }

            }
        }
    }

    fun getResults(q: String, page : Int): Search {
        var url = "https://apkpure.com/search-page?q=$q&t=app&begin="
        url += if (page > 1)
            (page-1) * 15
        else
            0

        val doc = getDoc(url)

        return Search(
            apps = ArrayList<App>().apply {
                doc.select(".search-dl").forEach { dl ->
                    add(
                        App(
                            name = dl.selectFirst(".search-title")!!.text(),
                            logo = dl.selectFirst("img")!!.attr("src"),
                            id = dl.selectFirst(".search-title a")!!.attr("href").split("/").last(),
                            score = try {
                                dl.selectFirst(".star")!!.text()
                            }catch (e:Exception){null},
                            dev = dl.select("p a")[1].text()
                            //description = dl.selectFirst(".description .content")!!.html()
                        )
                    )
                }
            },
            more = doc.select(".search-dl").size > 10
        )
        }



    fun getApp(id : String): AppPage {
        val url = "https://apkpure.com/store/apps/details?id=$id"
        val doc = getDoc(url)


        return AppPage(
            name = doc.select(".title-like h1").text(),
            logo = doc.select(".icon img").attr("src"),
            id= "df",
            description =  doc.select(".description .content").html(),
            images = ArrayList<String>().apply {
                doc.select("a.mpopup img").forEach {
                    add(it.attr("src"))
                }
            },
            download = doc.selectFirst(".ny-down a.da")!!.attr("href"),
            score = "hj",
            dev = doc.select(".publisher a").text(),
            size = doc.selectFirst(".fsize")!!.text()
        )
    }
}


