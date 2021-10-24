package gh.cloneconf.apkpurer.ui

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import androidx.fragment.app.Fragment
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import gh.cloneconf.apkpurer.api.Apkpurer
import gh.cloneconf.apkpurer.MainActivity
import gh.cloneconf.apkpurer.R
import gh.cloneconf.apkpurer.databinding.FragmentSearchBinding
import gh.cloneconf.apkpurer.databinding.ItemSuggestionBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchFragment : Fragment(R.layout.fragment_search), TextWatcher,
    TextView.OnEditorActionListener {


    private val adapter by lazy { ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1) }

    private lateinit var binds : FragmentSearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binds = FragmentSearchBinding.inflate(inflater)
        return binds.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        (requireContext() as MainActivity).apply {
            title = getString(R.string.app_name)
            back(false)
        }


        binds.searchEd.addTextChangedListener(this)
        binds.searchEd.setOnEditorActionListener(this)

        binds.apply {
            suggestionsLv.adapter = adapter
            suggestionsLv.setOnItemClickListener { parent, view, position, id ->
                showResults(adapter.getItem(position)!!)
            }
        }

    }



    private var job : Job? = null


    /**
     * On user types.
     */
    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
    override fun afterTextChanged(p0: Editable?) {}
    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        job?.cancel()

        val q = binds.searchEd.text.toString()

        if (q.isEmpty()) {
            binds.statusIv.setImageResource(R.drawable.ic_baseline_tag_faces_24)
            adapter.clear()
            adapter.notifyDataSetChanged()
            return
        }

        binds.statusIv.setImageResource(R.drawable.ic_baseline_hourglass_bottom_24)

        job = lifecycleScope.launch(Dispatchers.IO) {
            try {
                val suggestions = Apkpurer.suggestions(binds.searchEd.text.toString())
                withContext(Dispatchers.Main) {

                    if (suggestions.isEmpty()) {
                        binds.statusIv.setImageResource(R.drawable.ic_baseline_not_interested_24)
                        return@withContext
                    }

                    binds.statusIv.setImageResource(R.drawable.ic_baseline_check_24)
                    adapter.apply {
                        clear()
                    }.addAll(suggestions)
                    adapter.notifyDataSetChanged()
                }
            }catch (e:Exception){
                withContext(Dispatchers.Main){
                    binds.statusIv.setImageResource(R.drawable.ic_baseline_wifi_off_24)
                }
            }
        }
    }


    /**
     * On enter.
     */
    override fun onEditorAction(p0: TextView?, p1: Int, p2: KeyEvent?): Boolean {
        showResults(binds.searchEd.text.toString())
        return true
    }



    inner class Adapter: RecyclerView.Adapter<Adapter.ViewHolder>(){
        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val binds = ItemSuggestionBinding.bind(itemView)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(layoutInflater.inflate(R.layout.item_suggestion, parent, false))

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val suggestion = adapter.getItem(position)!!

            holder.binds.apply {
                nameTv.text = suggestion

                root.setOnClickListener {
                    showResults(suggestion)
                }
            }
        }

        override fun getItemCount() = adapter.count
    }


    /**
     * Send to results fragment.
     */
    private fun showResults(q:String){
        val fragment = ResultsFragment()
        fragment.arguments = Bundle().apply {
            putString("q", q)
        }
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fContainer, fragment)
            .addToBackStack(null)
            .commit()
    }




    /**
     * Show the keyboard and focus to the input when the fragment is visible.
     */
    override fun onResume() {
        super.onResume()

        if(binds.searchEd.requestFocus()) {
            (requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                .showSoftInput(binds.searchEd, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    /**
     * Clear data and hide keyboard when user go off.
     */
    override fun onPause() {
        super.onPause()
        binds.searchEd.text.clear()
        adapter.clear()
        adapter.notifyDataSetChanged()

        (requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(binds.searchEd.windowToken, 0)

    }



    /**
     * Menu
     */

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.settings -> {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fContainer, SettingsFragment())
                    .addToBackStack(null)
                    .commit()
                true
            }
            else -> false
        }
    }


}

