package gh.cloneconf.apkpurer.ui

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import gh.cloneconf.apkpurer.api.Apkpurer
import gh.cloneconf.apkpurer.MainActivity
import gh.cloneconf.apkpurer.R
import gh.cloneconf.apkpurer.databinding.FragmentResultsBinding
import gh.cloneconf.apkpurer.databinding.ItemResultBinding
import gh.cloneconf.apkpurer.model.App
import kotlinx.coroutines.*
import java.lang.Exception

class ResultsFragment : Fragment(R.layout.fragment_results) {

    val jobs = ArrayList<Job>()

    val q by lazy {
        requireArguments().getString("q")!!
    }


    val settings by lazy {
        (requireActivity() as MainActivity).settings
    }

    private lateinit var binds : FragmentResultsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binds = FragmentResultsBinding.inflate(inflater)
        return binds.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ui update.
        (requireContext() as MainActivity).apply {
            title = q
            back(true)
        }


        binds.resultsRv.layoutManager = LinearLayoutManager(requireContext())
        binds.resultsRv.adapter = adapter


        if (page == 1) {
            download()
        }

        binds.resultsRv.viewTreeObserver.addOnScrollChangedListener {
            if (more && !busy)
                try {
                    if (!binds.resultsRv.canScrollVertically(1)) {
                        download()
                    }
                }catch (e:Exception){}
        }


    }




    /**
     * Results Adapter.
     */
    inner class ResultAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        val results = ArrayList<Any>()

        fun add(item : Any){
            results.add(item)
            notifyDataSetChanged()
        }

        inner class ResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
            val binds = ItemResultBinding.bind(itemView)
        }

        inner class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){}

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return when(viewType){
                100 -> ResultViewHolder(layoutInflater.inflate(R.layout.item_result, parent, false))
                else -> LoadingViewHolder(layoutInflater.inflate(R.layout.item_loading, parent, false))
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

            val item = results[position]
            when (holder){
                is ResultViewHolder -> {

                    jobs.add(lifecycleScope.launch {
                        val result = item as App

                        holder.itemView.setOnClickListener {
                            showApp(result)
                        }

                        holder.binds.titleTv.text = result.name

                        holder.binds.devTv.text = result.dev



                        val bm = Bitmap.createBitmap(170, 170, Bitmap.Config.ARGB_8888)


                        val canvas = Canvas(bm)
                        canvas.drawColor(Color.argb(100, 221, 221, 221))


                        if (settings.liteMode) {
                            holder.binds.logoIv.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.mipmap.ic_launcher))
                        }else{
                            val bm = Bitmap.createBitmap(170, 170, Bitmap.Config.ARGB_8888)


                            val canvas = Canvas(bm)
                            canvas.drawColor(Color.argb(100, 221, 221, 221))

                            Glide.with(this@ResultsFragment)
                                .load(result.logo)
                                .placeholder(BitmapDrawable(requireContext().resources, bm))
                                .into(holder.binds.logoIv)

                        }


                    })

                }
            }
        }

        override fun getItemCount(): Int {
            return results.size
        }

        override fun getItemViewType(position: Int): Int {
            return if (results[position] is App) 100
            else 0
        }

    }




    /**
     * Send to app fragment.
     */

    fun showApp(app : App){

        val fragment = AppFragment()
        fragment.arguments = Bundle().apply {
            putString("app", Gson().toJson(app))
        }

        requireActivity().supportFragmentManager
            .beginTransaction()
            .replace(R.id.fContainer, fragment)
            .addToBackStack(null)
            .commit()
    }

    val adapter by lazy {
        ResultAdapter()
    }


    /**
     * Cancel all jobs when it's inactive.
     */
    override fun onPause() {
        super.onPause()
        jobs.forEach {
            it.cancel()
        }
    }

    var page = 1
    var more = false
    var busy = false


    private fun download(){
        busy = true
        if (page == 1)
            adapter.add(1)
        jobs.add(lifecycleScope.launch(Dispatchers.IO) {
            val results = Apkpurer.search(q, page)

            more = results.more

            withContext(Dispatchers.Main){

                if (adapter.results.isNotEmpty())
                    adapter.results.removeLast()

                adapter.results.addAll(results.apps)


                if (more) adapter.add(1)

                adapter.notifyDataSetChanged()

            }

            page++
            busy = false
        })
    }
}