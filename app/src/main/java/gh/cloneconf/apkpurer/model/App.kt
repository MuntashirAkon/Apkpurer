package gh.cloneconf.apkpurer.model


open class App (
    val id : String,
    val name : String,
    val logo : String,
    val score : String?,
    val dev : String
)

class AppPage(
    id: String,
    name: String,
    logo: String,
    val description : String,
    val images : List<Image>,
    val download : String?,
    val size : String,
    score: String?,
    dev: String
) : App(id, name, logo, score, dev)