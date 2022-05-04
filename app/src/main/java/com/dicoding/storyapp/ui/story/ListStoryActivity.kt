package com.dicoding.storyapp.ui.story

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.storyapp.R
import com.dicoding.storyapp.databinding.ActivityListStoryBinding
import com.dicoding.storyapp.helper.SettingsPreferences
import com.dicoding.storyapp.helper.ViewModelFactory
import com.dicoding.storyapp.helper.wrapEspressoIdlingResource
import com.dicoding.storyapp.ui.add.AddStoryActivity
import com.dicoding.storyapp.ui.login.MainActivity
import com.dicoding.storyapp.ui.map.MapStoryActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class ListStoryActivity : AppCompatActivity() {
    private lateinit var binding : ActivityListStoryBinding
    private lateinit var listStoryViewModel: ListStoryViewModel
    private var userToken: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvStory.setHasFixedSize(true)
        binding.rvStory.layoutManager = LinearLayoutManager(this)

        val pref = SettingsPreferences.getInstance(dataStore)
        wrapEspressoIdlingResource{
            lifecycleScope.launch{
                userToken = pref.getSessionInfo().first()
            }
        }

        listStoryViewModel = ViewModelProvider(this, ViewModelFactory.getInstance(application, pref)).get(
            ListStoryViewModel::class.java
        )

        listStoryViewModel.getSessionToken().observe(this){ sessionData ->
            if(sessionData.trim() != ""){
                userToken = sessionData
            }
        }

        showRecyclerList()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.options_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.maps -> {
                val mapsIntent = Intent(this@ListStoryActivity, MapStoryActivity::class.java)
                startActivity(mapsIntent)
            }
            R.id.add -> {
                val addStoryIntent = Intent(this@ListStoryActivity, AddStoryActivity::class.java)
                startActivity(addStoryIntent)
            }
            R.id.localization -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            }
            R.id.logout -> {
                listStoryViewModel.deleteSessionInfo()
                val logoutIntent = Intent(this@ListStoryActivity, MainActivity::class.java)
                startActivity(logoutIntent)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showRecyclerList(){
        val listStoryAdapter = ListStoryAdapter()
        binding.rvStory.adapter = listStoryAdapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                listStoryAdapter.retry()
            }
        )
        listStoryViewModel.listStory.observe(this) {
            listStoryAdapter.submitData(lifecycle, it)
        }
    }

}