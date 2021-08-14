package gh.cloneconf.apkpurer.ui

import android.app.DownloadManager
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import gh.cloneconf.apkpurer.Apkpurer
import gh.cloneconf.apkpurer.MainActivity
import gh.cloneconf.apkpurer.R
import kotlinx.android.synthetic.main.fragment_app.*
import kotlinx.android.synthetic.main.item_image.view.*
import kotlinx.android.synthetic.main.item_result.logoIv
import kotlinx.android.synthetic.main.item_result.titleTv
import kotlinx.coroutines.*

class AppFragment : Fragment(R.layout.fragment_app) {


    val id by lazy {
        requireArguments().getString("id")!!
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
            back(true)
        }

        imagesRv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        imagesRv.adapter = adapter



        job = lifecycleScope.launch(Dispatchers.IO) {
            val app =
                Apkpurer.getApp(id)

            withContext(Dispatchers.Main){

                cProgress.visibility = View.GONE


                titleTv.text = app.name


                val bm = Bitmap.createBitmap(170, 170, Bitmap.Config.ARGB_8888)

                Picasso.get()
                    .load(app.logo)
                    .placeholder(BitmapDrawable(requireContext().resources, bm))
                    .into(logoIv)

                descTv.text = Html.fromHtml(app.description)

                devTv.text = app.dev

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