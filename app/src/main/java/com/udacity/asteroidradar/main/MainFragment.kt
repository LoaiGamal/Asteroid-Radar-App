package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.api.Network.asteroids
import com.udacity.asteroidradar.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding
    private lateinit var adapter: AsteroidsAdapter

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        adapter = AsteroidsAdapter(AsteroidsAdapter.AsteroidListener {
            viewModel.onClick(it)
        })
        viewModel.asteroids.observe(viewLifecycleOwner, Observer{
            if (it != null) {
                adapter.submitList(it)
            }
        })
        binding.asteroidRecycler.adapter = adapter


        viewModel.navigateToDetailFragment.observe(viewLifecycleOwner, Observer {
            if (it != null){
                findNavController().navigate(MainFragmentDirections.actionShowDetail(it))
                viewModel.doneNavigating()
            }
        })

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.show_today_asteroids -> viewModel.getTodayAsteroidsFromDatabase()
            R.id.show_week_asteroids -> viewModel.getWeekAsteroidsFromDatabase()
            R.id.show_all_asteroids -> viewModel.getAllAstroidsFromDatabase()
        }
        return true
    }
}
