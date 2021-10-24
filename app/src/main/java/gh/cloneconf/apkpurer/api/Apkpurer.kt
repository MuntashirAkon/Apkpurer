package gh.cloneconf.apkpurer.api

import gh.cloneconf.apkpurer.Singleton.okhttp
import gh.cloneconf.apkpurer.model.*
import okhttp3.*
import org.json.JSONArray
import org.jsoup.Jsoup
import org.jsoup.nodes.Document


object Apkpurer {

    fun suggestions(q: String): ArrayList<String> {
        okhttp.newCall(
            Request.Builder()
                .url("https://apkpure.com/api/v1/search_suggestion?key=$q")
                .header(
                    "User-Agent",
                    "Mozilla/5.0 (X11; Linux x86_64; rv:78.0) Gecko/20100101 Firefox/78.0"
                )
                .build()
        ).execute().apply {
            JSONArray(body()!!.string()).apply {
                close()

                return ArrayList<String>().apply {
                    for (i in 0 until length()) {
                        add(getJSONObject(i).getString("key"))
                    }
                }
            }
        }
    }


    fun search(q: String, page: Int): Search {
        var url = "https://apkpure.com/search-page?q=$q&t=app&begin="
        url += if (page > 1) (page - 1) * 15 else 0


        okhttp.newCall(
            Request.Builder()
                .url(url)
                .build()
        ).execute().apply {
            Jsoup.parse(body()!!.string()).apply {
                close()
                return Search(
                    apps = ArrayList<App>().apply {
                        select(".search-dl").forEach { dl ->
                            add(
                                App(
                                    name = dl.selectFirst(".search-title")!!.text(),
                                    logo = dl.selectFirst("img")!!.attr("src"),
                                    id = dl.selectFirst(".search-title a")!!.attr("href").split("/")
                                        .last(),
                                    score = try {
                                        dl.selectFirst(".star")!!.text()
                                    } catch (e: Exception) {
                                        null
                                    },
                                    dev = dl.select("p a")[1].text()
                                    //description = dl.selectFirst(".description .content")!!.html()
                                )
                            )
                        }
                    },
                    more = select(".search-dl").size > 10
                )

            }
        }

    }


    fun getApp(id: String): AppPage {
        okhttp.newCall(
            Request.Builder()
                .url("https://apkpure.com/store/apps/details?id=$id")
                .build()
        ).execute().apply {
            Jsoup.parse(body()!!.string()).apply {
                close()
                return AppPage(
                    name = select(".title-like h1").text(),
                    logo = select(".icon img").attr("src"),
                    id = "df",
                    description = select(".description .content").html(),
                    images = ArrayList<Image>().apply {
                        select("a.mpopup img").forEach {
                            add(
                                Image(
                                    thumb = it.attr("src"),
                                    original = it.attr("srcset").split(" ").first()
                                )
                            )
                        }
                    },
                    download = try {
                        selectFirst(".ny-down a.da")!!.attr("href")
                    } catch (e: Exception) {
                        null
                    },
                    score = try {
                        select(".rating .average").text()
                    } catch (e: Exception) {
                        ""
                    },
                    dev = select(".details-author > p > a").text(),
                    size = selectFirst(".fsize")!!.text().replace(Regex("""[\(\)]"""), "")
                )
            }
        }
    }



}


