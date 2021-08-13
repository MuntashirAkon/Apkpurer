package gh.cloneconf.apkpurer.ui

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import gh.cloneconf.apkpurer.Apkpurer
import gh.cloneconf.apkpurer.MainActivity
import gh.cloneconf.apkpurer.R
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchFragment : Fragment(R.layout.fragment_search), TextWatcher,
    TextView.OnEditorActionListener, AdapterView.OnItemClickListener {


    val adapter by lazy {
        ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        searchEd.addTextChangedListener(this)
        searchEd.setOnEditorActionListener(this)
        suggestionsLv.adapter = adapter
        suggestionsLv.setOnItemClickListener(this)

    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    var job : Job? = null

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        job?.cancel()

        val q = searchEd.text.toString()

        if (q.isEmpty()) {
            statusIv.setImageResource(R.drawable.ic_baseline_tag_faces_24)
            adapter.clear()
            return
        }

        statusIv.setImageResource(R.drawable.ic_baseline_hourglass_bottom_24)

        job = lifecycleScope.launch(Dispatchers.IO) {
            try {
                val suggestions = Apkpurer.getSuggestions(searchEd.text.toString())
                withContext(Dispatchers.Main) {

                    if (suggestions.isEmpty()) {
                        statusIv.setImageResource(R.drawable.ic_baseline_not_interested_24)
                        return@withContext
                    }

                    statusIv.setImageResource(R.drawable.ic_baseline_check_24)
                    adapter.clear()
                    adapter.addAll(suggestions)
                }
            }catch (e:Exception){
                withContext(Dispatchers.Main){
                    statusIv.setImageResource(R.drawable.ic_baseline_wifi_off_24)
                }
            }
        }
    }

    override fun afterTextChanged(p0: Editable?) {

    }

    override fun onEditorAction(p0: TextView?, p1: Int, p2: KeyEvent?): Boolean {
        showResults(searchEd.text.toString())
        return true
    }


    fun showResults(q:String){
        val fragment = ResultsFragment()
        fragment.arguments = Bundle().apply {
            putString("q", q)
        }

        (requireActivity() as MainActivity).goTo(fragment)
    }


    /**
     * Show the keyboard and focus to the input when the fragment is visible.
     */
    override fun onResume() {
        super.onResume()

        if(searchEd.requestFocus()) {
            (requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                .showSoftInput(searchEd, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    /**
     * Clear data and hide keyboard when user go off.
     */
    override fun onPause() {
        super.onPause()
        searchEd.text.clear()
        adapter.clear()

        (requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .hideSoftInputFromWindow(searchEd.windowToken, 0)

    }

    override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        showResults(adapter.getItem(p2)!!)
    }
}

