package com.example.multitoolsdocumentscanner.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.documentfile.provider.DocumentFile
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chauthai.swipereveallayout.SwipeRevealLayout
import com.chauthai.swipereveallayout.ViewBinderHelper
import com.example.multitoolsdocumentscanner.R
import com.example.multitoolsdocumentscanner.utils.StringUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class RecyclerAdapterDocs(
    private val context: Context,
    private val listener: IDocClickListener
) :
    RecyclerView.Adapter<RecyclerAdapterDocs.ViewHolder>() {

    private val viewBinderHelper = ViewBinderHelper()
    private val docs = ArrayList<DocumentFile>()

    init {

        viewBinderHelper.setOpenOnlyOne(true)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val swipeRevealLayout: SwipeRevealLayout = itemView.findViewById(R.id.swipe_reveal_layout)
        val frontLayout: FrameLayout = itemView.findViewById(R.id.front_layout)

        val textViewName: TextView = itemView.findViewById(R.id.text_view_name)
        val textViewDate: TextView = itemView.findViewById(R.id.text_view_date)
        val textViewPagesCount: TextView = itemView.findViewById(R.id.text_view_page_count)
        val imageViewThumbnail: ImageView = itemView.findViewById(R.id.image_view_thumbnail)

        val imageViewEdit: ImageView = itemView.findViewById(R.id.image_view_edit)
        val imageViewDelete: ImageView = itemView.findViewById(R.id.image_view_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.recycler_item_doc,
            parent, false
        )
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        CoroutineScope(Dispatchers.Main).launch {

            val doc = docs[position]

            viewBinderHelper.bind(holder.swipeRevealLayout, doc.uri.path)

            val dateString = StringUtils.timestampToDate(doc.lastModified())

            holder.textViewDate.text =
                if (StringUtils.isDateToday(dateString)) "Today" else dateString

            holder.textViewName.text = doc.name
            holder.textViewPagesCount.text = "1 Page"

            Glide.with(context)
                .load(doc.uri)
                .into(holder.imageViewThumbnail)

            holder.frontLayout.setOnClickListener {

                listener.onDocClick(docs[position])
            }

            holder.imageViewEdit.setOnClickListener {

                listener.onDocRenameClick(docs[position])
            }

            holder.imageViewDelete.setOnClickListener {

                listener.onDocDeleteClick(docs[position])
            }
        }
    }

    override fun getItemCount(): Int {

        return docs.size
    }

    @SuppressLint("SimpleDateFormat")
    fun updateData(newList: List<DocumentFile>) {

        docs.clear()
        notifyDataSetChanged()
        docs.addAll(newList)

        docs.sortByDescending { doc ->

            Log.d("d_srt", "updateData: " + doc.lastModified())
            doc.lastModified()
        }

//        notifyItemRangeRemoved(0, previousContentSize);

        notifyItemRangeInserted(0, docs.size)
    }
}

interface IDocClickListener {

    fun onDocClick(doc: DocumentFile)
    fun onDocRenameClick(doc: DocumentFile)
    fun onDocDeleteClick(doc: DocumentFile)
}