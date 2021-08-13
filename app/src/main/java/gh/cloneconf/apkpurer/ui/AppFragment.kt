package gh.cloneconf.apkpurer.ui

import android.app.DownloadManager
import android.content.Intent
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import gh.cloneconf.apkpurer.Apkpurer
import gh.cloneconf.apkpurer.R
import kotlinx.android.synthetic.main.fragment_app.*
import kotlinx.android.synthetic.main.item_image.view.*
import kotlinx.android.synthetic.main.item_result.logoIv
import kotlinx.android.synthetic.main.item_result.titleTv
import kotlinx.coroutines.*

class AppFragment : Fragment(R.layout.fragment_app) {


    inner class ImagesAdapter : RecyclerView.Adapter<ImagesAdapter.ViewHolder>(){

        val images = ArrayList<String>()

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(layoutInflater.inflate(R.layout.item_image, parent, false))
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            Picasso.get()
                .load(images[position])
                .placeholder(requireContext().getDrawable(R.mipmap.ic_launcher)!!)
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

        imagesRv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        imagesRv.adapter = adapter



        job= lifecycleScope.launch(Dispatchers.IO) {
            val app =
                Apkpurer.getApp(requireArguments().getString("id")!!)

            withContext(Dispatchers.Main){
                titleTv.text = app.name

                Picasso.get()
                    .load(app.logo)
                    .into(logoIv)

                descTv.text = Html.fromHtml(app.description)
                
                adapter.images.addAll(app.images)
                adapter.notifyDataSetChanged()

                downloadBtn.text = app.size
            }



            val doc = Apkpurer.getDoc("https://apkpure.com"+app.download)

            withContext(Dispatchers.Main){
                downloadBtn.isEnabled = true

                downloadBtn.setOnClickListener {
                    val url = doc.select("#iframe_download").attr("src")
                    val i = Intent(Intent.ACTION_VIEW)
                    i.data = Uri.parse(url)
                    startActivity(i)
                }
            }
        }
    }
}