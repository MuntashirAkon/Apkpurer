package gh.cloneconf.apkpurer.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import gh.cloneconf.apkpurer.Apkpurer
import gh.cloneconf.apkpurer.MainActivity
import gh.cloneconf.apkpurer.R
import gh.cloneconf.apkpurer.model.App
import kotlinx.android.synthetic.main.fragment_results.*
import kotlinx.android.synthetic.main.item_result.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class ResultsFragment : Fragment(R.layout.fragment_results) {

    val q by lazy {
        requireArguments().getString("q")!!
    }


    inner class ResultAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        val results = ArrayList<Any>()

        fun add(item : Any){
            results.add(item)
            notifyDataSetChanged()
        }

        inner class ResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
            val titleTv = itemView.titleTv
            val logoIv = itemView.logoIv
            val devTv = itemView.devTv
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
                    val result = item as App

                    holder.itemView.setOnClickListener{
                        showApp(result.id)
                    }

                    holder.titleTv.text = result.name

                    holder.devTv.text = result.dev
                    Picasso.get()
                        .load(result.logo)
                        .placeholder(requireContext().getDrawable(R.mipmap.ic_launcher)!!)
                        .into(holder.logoIv)

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

    fun showApp(id : String){

        val fragment = AppFragment()
        fragment.arguments = Bundle().apply {
            putString("id", id)
        }

        (requireActivity() as MainActivity).goTo(fragment)
    }

    val adapter by lazy {
        ResultAdapter()
    }





    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        resultsRv.layoutManager = LinearLayoutManager(requireContext())
        resultsRv.adapter = adapter


        if (page == 1) {
            download()
        }


        resultsRv.viewTreeObserver.addOnScrollChangedListener {
            if (more && !busy)
                try {
                    if (!resultsRv.canScrollVertically(1)) {
                        download()
                        (adapter.results.last() as View)
                    }
                }catch (e:Exception){}
        }


    }

    override fun onPause() {
        super.onPause()
        lifecycleScope.cancel()
        busy = false
    }

    var page = 1
    var more = false
    var busy = false


    fun download(){
        busy = true
        if (page == 1)
            adapter.add(1)
        lifecycleScope.launch(Dispatchers.IO) {
            val results =Apkpurer.getResults(q, page)

            more = results.more

            println(results.apps.size)
            println(results.more)

            withContext(Dispatchers.Main){

                if (adapter.results.isNotEmpty())
                    adapter.results.removeLast()

                adapter.results.addAll(results.apps)


                if (more) adapter.add(1)

                adapter.notifyDataSetChanged()

            }

            page++
            busy = false
        }
    }
}