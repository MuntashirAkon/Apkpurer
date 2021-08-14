package gh.cloneconf.apkpurer.ui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import gh.cloneconf.apkpurer.api.Apkpurer
import gh.cloneconf.apkpurer.MainActivity
import gh.cloneconf.apkpurer.R
import gh.cloneconf.apkpurer.model.App
import kotlinx.android.synthetic.main.fragment_app.*
import kotlinx.android.synthetic.main.item_image.view.*
import kotlinx.android.synthetic.main.item_result.logoIv
import kotlinx.android.synthetic.main.item_result.titleTv
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AppFragment : Fragment(R.layout.fragment_app) {


    val settings by lazy {
        (requireActivity() as MainActivity).settings
    }

    val app by lazy {
        Gson().fromJson(
            requireArguments().getString("app")!!,
            App::class.java
        )
    }
    inner class ImagesAdapter : RecyclerView.Adapter<ImagesAdapter.ViewHolder>(){

        val images = ArrayList<String>()

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(layoutInflater.inflate(R.layout.item_image, parent, false))
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            val bm = Bitmap.createBitmap(200, 360, Bitmap.Config.ARGB_8888)


            val canvas = Canvas(bm)
            canvas.drawColor(Color.argb(100, 221,221,221))

            Picasso.get()
                .load(images[position])
                .placeholder(BitmapDrawable(requireContext().resources, bm))
                .into(holder.itemView.imageIv)

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
            settings(true)
        }


        titleTv.text = app.name

        download()

    }



    private fun download(){

        if (settings.liteMode) {
            logoIv.visibility = View.GONE
        }else {
            val bm = Bitmap.createBitmap(170, 170, Bitmap.Config.ARGB_8888)

            val canvas = Canvas(bm)
            canvas.drawColor(Color.argb(100, 221, 221, 221))

            Picasso.get()
                .load(app.logo)
                .placeholder(BitmapDrawable(requireContext().resources, bm))
                .into(logoIv)


            imagesRv.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            imagesRv.adapter = adapter
        }

        devTv.text = app.dev

        job = lifecycleScope.launch(Dispatchers.IO) {
            val app =
                Apkpurer.getApp(app.id)

            withContext(Dispatchers.Main){

                cProgress.visibility = View.GONE
                downloadBtn.visibility = View.VISIBLE



                descTv.text = Html.fromHtml(app.description)


                adapter.images.addAll(app.images)
                adapter.notifyDataSetChanged()

                downloadBtn.text = app.size
            }



            if (app.download != null) {

                val doc = Apkpurer.getDoc("https://apkpure.com" + app.download)

                withContext(Dispatchers.Main) {
                    downloadBtn.isEnabled = true


                    downloadBtn.setOnClickListener {
                        val url = doc.select("#iframe_download").attr("src")
                        val i = Intent(Intent.ACTION_VIEW)
                        i.data = Uri.parse(url)
                        startActivity(i)
                    }
                }
            }else{

                withContext(Dispatchers.Main){
                    downloadBtn.text = "ERROR!"
                }

            }
        }
    }
}