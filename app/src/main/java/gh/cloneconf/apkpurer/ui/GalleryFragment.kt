package gh.cloneconf.apkpurer.ui

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import gh.cloneconf.apkpurer.R
import gh.cloneconf.apkpurer.databinding.FragmentGalleryBinding
import gh.cloneconf.apkpurer.databinding.ItemZoomImageBinding
import gh.cloneconf.apkpurer.model.Image
import kotlin.properties.Delegates


class GalleryFragment : Fragment() {


    companion object {
        fun newInstance(images : ArrayList<Image>, pos : Int) = GalleryFragment().apply {
            arguments = Bundle().apply {
                putSerializable("images", images)
                putInt("pos", pos)
            }
        }
    }


    private lateinit var images : ArrayList<Image>
    private var pos by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        images = (requireArguments().getSerializable("images") as ArrayList<Image>)
        pos = (requireArguments().getInt("pos", 0))
    }

    private lateinit var binds : FragmentGalleryBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binds = FragmentGalleryBinding.inflate(inflater)
        return binds.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binds.apply {
            viewPager2.adapter = Adapter()
            viewPager2.setCurrentItem(pos, false)

        }
    }


    inner class Adapter : RecyclerView.Adapter<Adapter.ViewHolder>(){
        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val binds = ItemZoomImageBinding.bind(itemView)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)=
            ViewHolder(layoutInflater.inflate(R.layout.item_zoom_image, parent, false))

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val image = images[position]


            holder.binds.apply {

                Glide.with(requireContext())
                    .load(image.thumb)
                    .into(thumbIv)


                Glide.with(requireContext())
                    .load(image.original)
                    .apply(
                        RequestOptions()
                            .dontAnimate().skipMemoryCache(true)
                    )
                    .listener(object : RequestListener<Drawable?> {
                        override fun onLoadFailed(
                            @Nullable e: GlideException?,
                            model: Any,
                            target: Target<Drawable?>,
                            isFirstResource: Boolean
                        ): Boolean {
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any,
                            target: Target<Drawable?>,
                            dataSource: DataSource,
                            isFirstResource: Boolean
                        ): Boolean {
                            cProgress.hide()
                            return false
                        }
                    })
                    .into(zoomView)
            }


        }

        override fun getItemCount() = images.size
    }
}