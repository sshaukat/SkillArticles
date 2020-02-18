package ru.skillbranch.skillarticles.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
//import android.widget.Toolbar
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_root.*
import kotlinx.android.synthetic.main.layout_bottombar.*
import kotlinx.android.synthetic.main.layout_submenu.*
import kotlinx.android.synthetic.main.search_view_layout.*
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.extensions.dpToIntPx
import ru.skillbranch.skillarticles.ui.base.BaseActivity
import ru.skillbranch.skillarticles.ui.custom.IArticleView
import ru.skillbranch.skillarticles.viewmodels.*
import ru.skillbranch.skillarticles.viewmodels.base.Notify
import ru.skillbranch.skillarticles.viewmodels.base.ViewModelFactory

class RootActivity : BaseActivity<ArticleViewModel>(),IArticleView {
    //      constructor()
    override val layout: Int = R.layout.activity_root
    override lateinit var viewModel: ArticleViewModel

    private var isSearching: Boolean=false
    private var searchQuery: String?= null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_root)


        //btn_like.setOnClickListener {
        //    Snackbar.make(coordinator_container, "test", Snackbar.LENGTH_LONG)
        //        .setAnchorView(bottombar)
        //        .show()
        //}

        //switch_mode.setOnClickListener{
        //    delegate.localNightMode=if (switch_mode.isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        //}

        val vmFactory=
            ViewModelFactory("0")
        viewModel=ViewModelProviders.of(this,vmFactory).get(ArticleViewModel::class.java)
        viewModel.observeState(this) {
            renderUi(it)
            //if (it.isSearch) {
            //    isSearching=true
            //    searchQuery=it.searchQuery
            //}
        }

        viewModel.observeNotifications(this){
            renderNotification(it)
        }

    }





    override fun setupViews() {
        setupToolbar()
        setupBottomBar()
        setupSubMenu()
    }

    override fun renderSearchResult(searchResult: List<Pair<Int, Int>>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun renderSearchPosition(searchPosition: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun clearSearchResult() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showSearchBar() {
        bottombar.setSearchState(true)
    }

    override fun hideSearchBar() {
        bottombar.setSearchState(false)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search,menu)
        val menuItem=menu?.findItem(R.id.action_search)
        val searchView=(menuItem?.actionView as? SearchView)
        searchView?.queryHint=getString(R.string.article_search_placeholder)


        if (isSearching){
            menuItem?.expandActionView()
            searchView?.setQuery(searchQuery,false)
            //searchView?.clearFocus()
        }

        //return super.onCreateOptionsMenu(menu)

        menuItem?.setOnActionExpandListener(object: MenuItem.OnActionExpandListener{
            override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                Log.d("menuitem","collapse")
                viewModel.handleSearchMode(false)
                return true
            }

            override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
                Log.d("menuitem","expande")
                viewModel.handleSearchMode(true)
                return true
            }
        })
        searchView?.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.handleSearch(query)
                //searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.handleSearch(newText)
                //searchView.clearFocus()
                return true
            }

        })


        return super.onCreateOptionsMenu(menu)
    }

    private fun renderNotification(notify: Notify){
        val snackbar=Snackbar.make(coordinator_container,notify.message,Snackbar.LENGTH_LONG)
            snackbar.setAnchorView(bottombar)
            snackbar.setActionTextColor(getColor(R.color.color_accent_dark))

        when (notify){
            is  Notify.TextMessage -> {}
            is Notify.ActionMessage -> {
                snackbar.setAction(notify.actionLabel){notify.actionHandler?.invoke()}
            }
            is Notify.ErrorMessage -> {
                with(snackbar){
                    setBackgroundTint(getColor(R.color.design_default_color_error))
                    setTextColor(getColor(android.R.color.white))
                    setActionTextColor(getColor(android.R.color.white))
                    setAction(notify.errLabel){
                        notify.errHandler?.invoke()
                    }
                }
            }
        }

        snackbar.show()

    }

    private fun setupSubMenu() {
        btn_text_up.setOnClickListener{ viewModel.handleUpText() }
        btn_text_down.setOnClickListener{ viewModel.handleDownText() }
        switch_mode.setOnClickListener{ viewModel.handleNightMode() }
        }


    private fun setupBottomBar() {
        btn_like.setOnClickListener{ viewModel.handleLike()}
        btn_bookmark.setOnClickListener{viewModel.handleBookmark()}
        btn_share.setOnClickListener{viewModel.handleShare()}
        btn_settings.setOnClickListener{viewModel.handleToggleMenu()}

        btn_result_up.setOnClickListener{
            if (search_view.hasFocus()) search_view.clearFocus()
            viewModel.handleUpResult()
        }

        btn_result_down.setOnClickListener{
            if (search_view.hasFocus()) search_view.clearFocus()
            viewModel.handleDownResult()
        }

        btn_search_close.setOnClickListener{
            viewModel.handleSearchMode(false)
            invalidateOptionsMenu()
        }
    }

    private fun renderUi(data:ArticleState){
        bottombar.setSearchState(data.isSearch)

        btn_settings.isChecked=data.isShowMenu
        if (data.isShowMenu) submenu.open() else submenu.close()

        btn_like.isChecked=data.isLike
        btn_bookmark.isChecked=data.isBookmark

        switch_mode.isChecked=data.isDarkMode

        delegate.localNightMode=
            if (data.isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else  AppCompatDelegate.MODE_NIGHT_NO

        if (data.isBigText) {
            tv_text_content.textSize=18f
            btn_text_up.isChecked = true
            btn_text_down.isChecked = false
        } else {
            tv_text_content.textSize=14f
            btn_text_up.isChecked = false
            btn_text_down.isChecked = true
        }

        tv_text_content.text= if (data.isLoadingContent) "loading" else data.content.first() as String

        toolbar.title= data.title?: "loading"
        toolbar.subtitle=data.category?:"loading"
        if (data.categoryIcon!=null) toolbar.logo=getDrawable(data.categoryIcon as Int)

    }


    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val logo:ImageView?= if(toolbar.childCount>2) toolbar.getChildAt(2) as ImageView else null
        logo?.scaleType=ImageView.ScaleType.CENTER_CROP
        val lp=logo?.layoutParams as  Toolbar.LayoutParams
        lp?.let {
            it.width=this.dpToIntPx(40)
            it.height=this.dpToIntPx(40)
            it.marginEnd=this.dpToIntPx(16)
            logo.layoutParams=it
        }
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}

