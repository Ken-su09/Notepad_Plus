package com.suonk.notepad_plus.ui.note.list

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.suonk.mynotepad.utils.viewBinding
import com.suonk.notepad_plus.R
import com.suonk.notepad_plus.databinding.FragmentNotesListBinding
import com.suonk.notepad_plus.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotesListFragment : Fragment(R.layout.fragment_notes_list) {

    private val viewModel by viewModels<NotesListViewModel>()
    private val binding by viewBinding(FragmentNotesListBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupBottomNavigationView()
    }

    //region ========================================================= TOOLBAR / BOTTOM NAV =========================================================

    private fun setupToolbar() {
        binding.toolbar.inflateMenu(R.menu.list_toolbar_menu)

        binding.toolbar.menu?.let { menu ->
            val searchItem = menu.findItem(R.id.action_search)
            val searchView = searchItem?.actionView as SearchView
            searchView.imeOptions = EditorInfo.IME_ACTION_DONE;
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(search: String?): Boolean {
                    search?.let {
//                        viewModel.onSearchQueryChanged()
//                        viewModel.onSearchQueryDone(search)
                    }
                    return false
                }

                override fun onQueryTextChange(search: String?): Boolean {
                    search?.let {
//                        viewModel.onSearchQueryChanged(search)
                    }
                    return true
                }
            })
        }
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                else -> {
                    it.isChecked = true
                }
            }
            true
        }
    }

    private fun setupBottomNavigationView() {
        binding.bottomNavigation.menu.findItem(R.id.nav_notes).isChecked = true
        binding.bottomNavigation.setOnItemSelectedListener { menuItem ->
            val fragmentToCommit = when (menuItem.itemId) {
                R.id.nav_garbage -> {
//                    DeletedNotesListFragment()
                    NotesListFragment()
                }
                else -> {
                    NotesListFragment()
                }
            }

            replaceFragment(R.id.fragment_container_view, fragmentToCommit, "")

            true
        }
    }

    //endregion

    private fun replaceFragment(fragmentContainer: Int, fragment: Fragment, tag: String) {
        if (requireActivity() is MainActivity) {
            requireActivity().supportFragmentManager.beginTransaction().replace(fragmentContainer, fragment, tag).addToBackStack(null)
                .commit()
        }
    }
}