package ru.skillbranch.skillarticles.ui.dialogs

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_dialog_chose_category.*
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.data.local.entities.CategoryData

class CategoryAdapter(
    private val listener: (String, Boolean) -> Unit
) : ListAdapter<CategoryDataItem, CategoryAdapter.CategoryViewHolder>(CategoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_dialog_chose_category, parent, false),
            listener
        )
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CategoryViewHolder(
        override val containerView: View,
        val listener: (String, Boolean) -> Unit
    ) : RecyclerView.ViewHolder(containerView), LayoutContainer {


        fun bind(item: CategoryDataItem) {
            ch_select.setOnCheckedChangeListener(null)
            ch_select.isChecked = item.isChecked
            tv_category.text = item.title
            tv_count.text = "${item.articlesCount}"

            Glide.with(containerView.context)
                .load(item.icon)
                .apply(RequestOptions.centerCropTransform())
                .override(iv_icon.width)
                .into(iv_icon)

            ch_select.setOnCheckedChangeListener { _, checked ->
                listener(
                    item.categoryId,
                    checked
                )
            }
            itemView.setOnClickListener { ch_select.toggle() }
        }

    }

}

class CategoryDiffCallback : DiffUtil.ItemCallback<CategoryDataItem>() {
    override fun areItemsTheSame(
        oldItem: CategoryDataItem,
        newItem: CategoryDataItem
    ): Boolean =
        oldItem.categoryId == newItem.categoryId

    override fun areContentsTheSame(
        oldItem: CategoryDataItem,
        newItem: CategoryDataItem
    ): Boolean =
        oldItem == newItem

}

data class CategoryDataItem(
    val categoryId: String,
    val icon: String,
    val title: String,
    val articlesCount: Int = 0,
    val isChecked: Boolean = false
)

fun CategoryData.toItem(checked: Boolean = false) =
    CategoryDataItem(categoryId, icon, title, articlesCount, checked)