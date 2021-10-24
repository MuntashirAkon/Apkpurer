package gh.cloneconf.apkpurer.ui

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import gh.cloneconf.apkpurer.MainActivity
import gh.cloneconf.apkpurer.R
import gh.cloneconf.apkpurer.Singleton.gson
import gh.cloneconf.apkpurer.Singleton.okhttp
import gh.cloneconf.apkpurer.api.Apkpurer
import gh.cloneconf.apkpurer.databinding.FragmentAppBinding
import gh.cloneconf.apkpurer.databinding.ItemImageBinding
import gh.cloneconf.apkpurer.model.App
import gh.cloneconf.apkpurer.model.Image
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Request
import org.jsoup.Jsoup


class AppFragment : Fragment(R.layout.fragment_app) {


    private val activity by lazy { requireActivity() as MainActivity }
    private val settings by lazy { activity.settings }
    private lateinit var binds : FragmentAppBinding
    private val app by lazy { gson.fromJson(requireArguments().getString("app")!!, App::class.java) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binds = FragmentAppBinding.inflate(inflater)
        return binds.root
    }

    inner class ImagesAdapter : RecyclerView.Adapter<ImagesAdapter.ViewHolder>(){

        val images = ArrayList<Image>()

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val binds = ItemImageBinding.bind(itemView)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(layoutInflater.inflate(R.layout.item_image, parent, false))
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            val bm = Bitmap.createBitmap(200, 360, Bitmap.Config.ARGB_8888)


            val canvas = Canvas(bm)
            canvas.drawColor(Color.argb(100, 221,221,221))


            Glide.with(this@AppFragment)
                .load(images[position].thumb)
                .placeholder(BitmapDrawable(requireContext().resources, bm))
                .into(holder.binds.imageIv)



            holder.binds.imageIv.setOnClickListener {
                requireActivity().supportFragmentManager.beginTransaction()
                    .addToBackStack(null)
                    .add(R.id.fContainer, GalleryFragment.newInstance(images, position))
                    .commit()
            }


        }

        override fun getItemCount(): Int {
            return images.size
        }

    }

    override fun onPause() {
        super.onPause()

        job?.cancel()
    }

    val adapter by lazy {
        ImagesAdapter()
    }

    var job : Job? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireContext() as MainActivity).apply {
            title = app.name
            back(true)
        }

        binds.titleTv.text = app.name

        download()

    }



    @SuppressLint("NotifyDataSetChanged")
    private fun download(){

        if (settings.liteMode) {
            binds.logoIv.visibility = View.GONE
        }else {
            val bm = Bitmap.createBitmap(170, 170, Bitmap.Config.ARGB_8888)

            val canvas = Canvas(bm)
            canvas.drawColor(Color.argb(100, 221, 221, 221))


            Glide.with(this)
                .load(app.logo)
                .placeholder(BitmapDrawable(requireContext().resources, bm))
                .into(binds.logoIv)




            binds.imagesRv.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            binds.imagesRv.adapter = adapter
        }

        binds.devTv.text = app.dev

        job = lifecycleScope.launch(Dispatchers.IO) {
            val app =
                Apkpurer.getApp(app.id)

            withContext(Dispatchers.Main){

                binds.cProgress.visibility = View.GONE
                binds.downloadBtn.visibility = View.VISIBLE


                binds.descTv.text = HtmlCompat.fromHtml(app.description, HtmlCompat.FROM_HTML_MODE_COMPACT)


                adapter.images.addAll(app.images)
                adapter.notifyDataSetChanged()

                binds.downloadBtn.text = app.size
            }



            if (app.download != null) {

                okhttp.newCall(Request.Builder()
                    .url("https://apkpure.com${app.download}")
                    .build()).execute().apply {
                    Jsoup.parse(body()!!.string()).apply {
                        close()
                        withContext(Dispatchers.Main) {
                            binds.downloadBtn.isEnabled = true

                            binds.downloadBtn.setOnClickListener {
                                val url = select("#iframe_download").attr("src")
                                try {
                                    val i = Intent(Intent.ACTION_VIEW)
                                    i.data = Uri.parse(url)
                                    startActivity(i)
                                }catch (e:Exception){
                                    Toast.makeText(requireContext(), "No Browser founded.. (Copied !)", Toast.LENGTH_SHORT).show()

                                    (requireContext() as MainActivity).clipboardManager.setPrimaryClip(
                                        ClipData.newPlainText("url", url))
                                }
                            }
                        }
                    }
                }

            }else{

                withContext(Dispatchers.Main){
                    binds.downloadBtn.text = "ERROR!"
                }

            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.app_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.open) {
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse("https://apkpure.com/store/apps/details?id=${app.id}")
            startActivity(i)
            true
        } else false
    }

}